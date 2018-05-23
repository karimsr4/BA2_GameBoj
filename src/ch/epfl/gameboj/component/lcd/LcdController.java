package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.checkBits16;
import static ch.epfl.gameboj.Preconditions.checkBits8;

import java.util.Arrays;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.memory.Ram;

/**
 * Classe qui simule le controlleur d'écran
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class LcdController implements Component, Clocked {
    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    public static final int IMAGE_EDGE = 256;

    private static final int TILE_EDGE = 8;
    private static final int TILE_IMAGE_BYTES = 16;
    private static final int IMAGE_LINE_TILES = 32;
    private static final int SCREEN_LINE_TILES = 20;


    private final Cpu cpu;
    private final Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    private final RegisterFile<Reg> lcdcRegs = new RegisterFile<>(Reg.values());
    private final Ram oam = new Ram(AddressMap.OAM_RAM_SIZE);
    private Bus bus;

    private long nextNonIdleCycle = Long.MAX_VALUE;

    private LcdImage.Builder nextImageBuilder = new LcdImage.Builder(LCD_WIDTH,
            LCD_HEIGHT);
    private LcdImage nextImage = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT)
            .build();
    private int winY;

    private boolean isQuickCopying;
    private int copyStartAddress;
    private int copiedBytes;

    private enum LCDCBits implements Bit {
        BG, OBJ, OBJ_SIZE, BG_AREA, TILE_SOURCE, WIN, WIN_AREA, LCD_STATUS
    }
    
    private enum STATBits implements Bit {
        MODE0, MODE1, LYC_EQ_LY, INT_MODE0, INT_MODE1, INT_MODE2, INT_LYC, UNUSED
    }
    private enum Mode {
        MODE_0(0, 51), MODE_1(1, 114), MODE_2(2, 20), MODE_3(3, 43);
        private int mode;
        private int lineCycles;

        private Mode(int mode, int lineCycles) {
            this.mode = mode;
            this.lineCycles = lineCycles;
        }
    }

    private enum Reg implements Register {
        LCDC, STAT, SCY, SCX, LY, LYC, DMA, BGP, OPB0, OPB1, WY, WX;
    }

    private enum Position {
        BACKGOUND, FOREGOUND
    }

    private enum TileSource {
        SOURCE_0(AddressMap.TILE_SOURCE[0]), SOURCE_1(
                AddressMap.TILE_SOURCE[1]);
        private int start;

        private TileSource(int start) {
            this.start = start;
        }

    }

    private enum TileDataArea {
        AREA_0 (AddressMap.BG_DISPLAY_DATA[0]), AREA_1(AddressMap.BG_DISPLAY_DATA[1]);
        private final int start ;
        private TileDataArea(int start) {
            this.start=start;
        }
    }

    /**
     * construit un controlleur d'écran LCD
     * 
     * @param cpu
     *            le processeur du Game Boy auquel il appartient
     */
    public LcdController(Cpu cpu) {
        for (Reg o : Reg.values())
            set(o, 0);
        this.cpu = cpu;
    }

    /**
     * retourne l'image actuellement affichée à l'écran
     * 
     * @return l'image actuellement affichée à l'écran
     */
    public LcdImage currentImage() {
        return nextImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        checkBits16(address);
        if (addressBelongsToVideoRam(address))

            return videoRam.read(address - AddressMap.VIDEO_RAM_START);

        if (addressBelongsToRegisters(address))

            return get(Reg.values()[address - AddressMap.REGS_LCDC_START]);

        if (addressBelongstoObjectsRam(address))

            return oam.read(address - AddressMap.OAM_START);

        return Component.NO_DATA;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#write(int, int)
     */
    @Override
    public void write(int address, int data) {
        checkBits8(data);
        checkBits16(address);
        if (addressBelongsToVideoRam(address)) {

            videoRam.write(address - AddressMap.VIDEO_RAM_START, data);

        } else if (addressBelongstoObjectsRam(address)) {

            oam.write(address - AddressMap.OAM_START, data);

        } else if (addressBelongsToRegisters(address)) {

            Reg reg = Reg.values()[address - AddressMap.REGS_LCDC_START];

            switch (reg) {
            case LCDC: {
                set(Reg.LCDC, data);
                if (!screenIsOn()) {
                    setLyLyc(Reg.LY, 0);
                    changeMode(Mode.MODE_0);
                    nextNonIdleCycle = Long.MAX_VALUE;
                }

            }
                break;
            case STAT: {
                int mask = 0b111;
                set(Reg.STAT, (get(Reg.STAT) & mask)
                        | (data & ~mask) );
                
                if (modeInterruptActive(currentMode())
                        || (lyEqualsLycInterruptActive()
                                && get(Reg.LY) == get(Reg.LYC)))
                    cpu.requestInterrupt(Interrupt.LCD_STAT);
            }
                break;
            case LYC: {
                setLyLyc(reg, data);
            }
                break;
            case LY:
                break;
            case DMA: {
                set(reg, data);
                isQuickCopying = true;
                copyStartAddress = Bits.make16(data, 0);
            }
                break;
            default: {
                set(reg, data);
            }
            break;
            }
        }

    }

    /* (non-Javadoc)
     * @see ch.epfl.gameboj.component.Component#attachTo(ch.epfl.gameboj.Bus)
     */
    @Override
    public void attachTo(Bus bus) {
        this.bus = bus;
        bus.attach(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Clocked#cycle(long)
     */

    @Override
    public void cycle(long cycle) {
        if (isQuickCopying) 
             quickCopy();

        if (nextNonIdleCycle == Long.MAX_VALUE && screenIsOn()) {

            nextNonIdleCycle = cycle;

            changeMode(Mode.MODE_2);
        }

        else if (cycle == nextNonIdleCycle) {
            reallyCycle();
        }

    }

    private void reallyCycle() {
        switch (currentMode()) {
        case MODE_0: {
            setLyLyc(Reg.LY, get(Reg.LY) + 1);

            if (get(Reg.LY) == LCD_HEIGHT) {

                cpu.requestInterrupt(Interrupt.VBLANK);
                changeMode(Mode.MODE_1);
                nextImage = nextImageBuilder.build();

            } else {
                changeMode(Mode.MODE_2);

            }
        }
            break;
        case MODE_1: {

            setLyLyc(Reg.LY, get(Reg.LY) + 1);
            if (get(Reg.LY) == LCD_HEIGHT+10) {

                setLyLyc(Reg.LY, 0);
                changeMode(Mode.MODE_2);
                nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
                winY = 0;

            } else {
                nextNonIdleCycle += Mode.MODE_1.lineCycles;
            }
        }
            break;
        case MODE_2: {

            changeMode(Mode.MODE_3);
            int ly = get(Reg.LY);
            nextImageBuilder.setLine(ly, computeLine(ly));

        }
            break;
        case MODE_3: {
            changeMode(Mode.MODE_0);

        }
            break;

        }
    }

    private LcdImageLine computeLine(int index) {

        LcdImageLine line = new LcdImageLine.Builder(IMAGE_EDGE).build();
        if (backGroundActivated()) {
            int realIndex = (index + get(Reg.SCY)) % IMAGE_EDGE;
            int scx = get(Reg.SCX);
            TileDataArea tileArea = Bits.test(get(Reg.LCDC), LCDCBits.BG_AREA)
                    ? TileDataArea.AREA_1
                    : TileDataArea.AREA_0;
            line = reallyComputeLine(scx, realIndex, tileArea)
                    .shift(-(scx % TILE_EDGE));

        }
        if (windowActivated() && index >= get(Reg.WY)) {
            TileDataArea tileArea = Bits.test(get(Reg.LCDC), LCDCBits.WIN_AREA)
                    ? TileDataArea.AREA_1
                    : TileDataArea.AREA_0;
            LcdImageLine window = reallyComputeLine(0, winY, tileArea);
            int shift = getRealWX();
            line = line.join(window.shift(shift), Integer.max(0, shift));
            winY++;
        }
        line = line.extractWrapped(0, LCD_WIDTH).mapColors(get(Reg.BGP));

        if (spritesActivated()) {
            int[] lineSprites = spritesIntersectingLine(index);
            LcdImageLine backgroundSprites = computeSpriteLine(lineSprites,
                    index, Position.BACKGOUND);
            BitVector opacity = line.getOpacity().not()
                    .and(backgroundSprites.getOpacity()).not();
            LcdImageLine foregroundSprites = computeSpriteLine(lineSprites,
                    index, Position.FOREGOUND);
            return backgroundSprites.below(line, opacity)
                    .below(foregroundSprites);
        }

        return line;

    }


    
    /**
     * calcule une ligne d'image LCD d'index donné dont la première tuile 
     * contient le pixel d'index donné
     * @param startPixel pixel donné 
     * @param index index de la ligne
     * @param tileArea plage d'indexs des tuiles
     * @return
     */
    private LcdImageLine reallyComputeLine(int startPixel, int index,
            TileDataArea tileArea) {
        int firstByte = (index % TILE_EDGE) * 2;
        int start = startPixel / TILE_EDGE;
        int firstTile = (index / TILE_EDGE) * (IMAGE_LINE_TILES);
        LcdImageLine.Builder builder = new LcdImageLine.Builder(IMAGE_EDGE);
        int tileIndex;
        for (int i = 0; i <=  SCREEN_LINE_TILES; ++i) {
            tileIndex = tileIndex(firstTile + ((i+start) % IMAGE_LINE_TILES), tileArea);
            builder.setByte(i ,
                    getTileImageByte(firstByte + 1, tileIndex, tileSource(),
                            false),
                    getTileImageByte(firstByte, tileIndex, tileSource(),
                            false));
        }
        return builder.build();

    }

    private LcdImageLine computeSpriteLine(int[] sprites, int lineIndex,
            Position spritePosition) {

        int sprite;
        LcdImageLine result = new LcdImageLine.Builder(LCD_WIDTH).build();
        for (int i = 0; i < sprites.length; i++) {
            sprite = sprites[i];
            if (spritePosition(sprite) == spritePosition) {
                result = spriteLine(sprite, lineIndex).below(result);
            }
        }
        return result;

    }

    private int[] spritesIntersectingLine(int lineIndex) {
        int spriteHeight = spriteHeight();
        int[] intersectingSprites = new int[10];
        int spriteIndex = 0;
        int nbSprites = 0;
        int realY = 0;
        while (nbSprites < 10 && spriteIndex < 40) {
            realY = getSpriteRealY(spriteIndex);
            if (realY <= lineIndex && realY + spriteHeight > lineIndex) {
                intersectingSprites[nbSprites] = Bits
                        .make16(getSpriteX(spriteIndex), spriteIndex);
                nbSprites++;
            }
            spriteIndex++;
        }
        Arrays.sort(intersectingSprites, 0, nbSprites);
        int[] result = new int[nbSprites];
        for (int j = 0; j < nbSprites; j++) {
            result[j] = Bits.clip(Byte.SIZE, intersectingSprites[j]);
        }
        return result;
    }

    
    private int getRealWX() {
        return get(Reg.WX) - 7;
    }
    
    private LcdImageLine spriteLine(int sprite, int line) {
        LcdImageLine.Builder result = new LcdImageLine.Builder(LCD_WIDTH);
        int tile = oam.read(sprite * 4 + 2);
        int spriteAttributes = oam.read(sprite * 4 + 3);
        boolean verticalFlip = Bits.test(spriteAttributes, 6);
        boolean horizontalFlip = Bits.test(spriteAttributes, 5);
        int spritePalette = Bits.test(spriteAttributes, 4) ? get(Reg.OPB1)
                : get(Reg.OPB0);
        int realY=getSpriteRealY(sprite);

        line = verticalFlip
                ? spriteHeight()
                        - (line - realY + 1)
                : line - realY;

        result.setByte(0,
                getTileImageByte(line * 2 + 1, tile, TileSource.SOURCE_1,
                        horizontalFlip),
                getTileImageByte(line * 2, tile, TileSource.SOURCE_1,
                        horizontalFlip));

        return result.build().mapColors(spritePalette)
                .shift(getSpriteRealX(sprite));
    }

    private int tileIndex(int tile, TileDataArea tileArea) {
        return videoRam.read(tileArea.start + tile - AddressMap.VIDEO_RAM_START);
    }

    private int getTileImageByte(int byteIndex, int tileIndex,
            TileSource source, boolean reverse) {

        int address = source.start;
        switch (source) {

        case SOURCE_1:
            address += tileIndex * TILE_IMAGE_BYTES + byteIndex;
            break;
        case SOURCE_0: {

            int shift = tileIndex < 0x80 ? 0x80 : -0x80;
            address += (tileIndex + shift) * TILE_IMAGE_BYTES + byteIndex;
        }
        }

        return reverse ? videoRam.read(address - AddressMap.VIDEO_RAM_START)
                : Bits.reverse8(
                        videoRam.read(address - AddressMap.VIDEO_RAM_START));

    }

    private void changeMode(Mode nextMode) {

        int mask = 0b11;
        int statNewValue = (nextMode.mode & mask) | (get(Reg.STAT) & (~mask));

        set(Reg.STAT, statNewValue);
        nextNonIdleCycle += nextMode.lineCycles;

        if (modeInterruptActive(nextMode)) {
            cpu.requestInterrupt(Interrupt.LCD_STAT);

        }

    }

    private void setLyLyc(Reg a, int data) {

        set(a, data);
        boolean equal = get(Reg.LY) == get(Reg.LYC);
        set(Reg.STAT,
                Bits.set(get(Reg.STAT), STATBits.LYC_EQ_LY.index(), equal));
        if (lyEqualsLycInterruptActive() && equal)
            cpu.requestInterrupt(Interrupt.LCD_STAT);

    }

    private void quickCopy() {
            oam.write(copiedBytes, bus.read(copyStartAddress + copiedBytes));
            copiedBytes++;
        if (copiedBytes == AddressMap.OAM_RAM_SIZE) {
            isQuickCopying = false;
            copiedBytes = 0;
        }

    }

    private int getSpriteRealX(int spriteIndex) {
        return getSpriteX(spriteIndex)-8;
    }
    
    private int getSpriteX(int spriteIndex) {
        return oam.read(spriteIndex * 4 + 1);
    }

    private int getSpriteRealY(int spriteIndex) {
        return oam.read(spriteIndex * 4) - TILE_EDGE * 2;
    }
    // méthodes pour faciliter l'accés au banc de registres
    private void set(Reg a, int data) {
        lcdcRegs.set(a, data);
    }

    private int get(Reg a) {
        return lcdcRegs.get(a);
    }

    private TileSource tileSource() {
        return Bits.test(get(Reg.LCDC), LCDCBits.TILE_SOURCE)
                ? TileSource.SOURCE_1
                : TileSource.SOURCE_0;
    }

    private Position spritePosition(int spriteIndex) {
        return Bits.test(oam.read(spriteIndex * 4 + 3), 7) ? Position.BACKGOUND
                : Position.FOREGOUND;
    }

    private Mode currentMode() {
        return Mode.values()[Bits.clip(2, get(Reg.STAT))];
    }

    // Méthodes de tests sur les registres
    private boolean spritesActivated() {
        return Bits.test(get(Reg.LCDC), LCDCBits.OBJ);

    }

    private boolean windowActivated() {
        return Bits.test(get(Reg.LCDC), LCDCBits.WIN)
                && getRealWX() < LCD_WIDTH;
    }

    private boolean backGroundActivated() {
        return Bits.test(get(Reg.LCDC), LCDCBits.BG);
    }

    private int spriteHeight() {
        return Bits.test(get(Reg.LCDC), LCDCBits.OBJ_SIZE) ? 2 * TILE_EDGE
                : TILE_EDGE;

    }

    private boolean modeInterruptActive(Mode mode) {
        return mode != Mode.MODE_3 && Bits.test(get(Reg.STAT), mode.mode + 3);

    }

    private boolean lyEqualsLycInterruptActive() {
        return Bits.test(get(Reg.STAT), STATBits.INT_LYC);
    }

    private boolean screenIsOn() {
        return Bits.test(get(Reg.LCDC), LCDCBits.LCD_STATUS);
    }

    // Méthodes de tests d'adresses
    private boolean addressBelongsToVideoRam(int address) {
        return address >= AddressMap.VIDEO_RAM_START
                && address < AddressMap.VIDEO_RAM_END;
    }

    private boolean addressBelongsToRegisters(int address) {
        return address >= AddressMap.REGS_LCDC_START
                && address < AddressMap.REGS_LCDC_END;
    }

    private boolean addressBelongstoObjectsRam(int address) {
        return address >= AddressMap.OAM_START && address < AddressMap.OAM_END;
    }

}
