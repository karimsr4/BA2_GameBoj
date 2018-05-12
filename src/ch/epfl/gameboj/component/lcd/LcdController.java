package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.checkBits16;
import static ch.epfl.gameboj.Preconditions.checkBits8;

import java.util.Arrays;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.memory.Ram;

/**
 * classe qui représente le controlleur d'écran LCD
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class LcdController implements Component, Clocked {
    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    public static final int IMAGE_EDGE = 256;

    private static final int TILE_EDGE = 8;
    private static final int TILE_IMAGE_BYTES = 16;

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
    private int nbreOfCopiedBytes;

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
     * @see ch.epfl.gameboj.component.Clocked#cycle(long)
     */

    @Override
    public void cycle(long cycle) {
        quickCopy();
        

        if (nextNonIdleCycle == Long.MAX_VALUE && screenIsOn()) {

            nextNonIdleCycle = cycle;
            if (get(Reg.LY)==1)
                System.out.print("Cycle = "+cycle + " since frame :" + (nextNonIdleCycle-cycle) + "mode 0 --> 2");
            changeMode(Mode.MODE_2);
        }


        if(cycle==nextNonIdleCycle) {
            reallyCycle();
        }

    
      

    

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
                if (!Bits.test(data, 7)) {
                    setLyLyc(Reg.LY, 0);
                    changeMode(Mode.MODE_0);
                    
                    nextNonIdleCycle = Long.MAX_VALUE;
                }
                set(reg, data);
            }
                break;
            case STAT: {
                set(Reg.STAT, Bits.clip(3, get(Reg.STAT))
                        | (Bits.extract(get(Reg.STAT), 3, 5) << 3));
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

            }
            break;
            default: {
                set(reg, data);
            }
            }
        }

    }

    private void quickCopy() {
        if (isQuickCopying) {
            int address = Bits.make16(get(Reg.DMA), 0);
            oam.write(nbreOfCopiedBytes, bus.read(address + nbreOfCopiedBytes));
            nbreOfCopiedBytes++;

        }
        if (nbreOfCopiedBytes == AddressMap.OAM_RAM_SIZE) {
            isQuickCopying = false;
            nbreOfCopiedBytes = 0;
        }

    }

    @Override
    public void attachTo(Bus bus) {
        this.bus = bus;
        bus.attach(this);

    }

    private void changeMode(Mode nextMode) {
        int mask = Bits.mask(0) | Bits.mask(1);
        int statNewValue = (nextMode.mode & mask) | (get(Reg.STAT) & (~mask));
        set(Reg.STAT, statNewValue);
        nextNonIdleCycle += nextMode.lineCycles;
        if (Bits.test(get(Reg.STAT), nextMode.mode + 3)
                && nextMode != Mode.MODE_3) {
            cpu.requestInterrupt(Interrupt.LCD_STAT);
            System.out.print("LCDSTAT REQUESTED");}

    }

    private LcdImageLine computeLine(int index) {
        int realIndex = (index + get(Reg.SCY)) % IMAGE_EDGE;
        LcdImageLine bgWindowLine = new LcdImageLine.Builder(256).build();
        if (backGroundActivated()) {
            bgWindowLine = reallyComputeLine(realIndex,
                    Bits.test(get(Reg.LCDC), 3));
        }

        if (windowActivated() && index >= get(Reg.WY)) {
            bgWindowLine = bgWindowLine.join(
                    reallyComputeLine(winY, Bits.test(get(Reg.LCDC), 6))
                            .shift(get(Reg.WX) - 7 + get(Reg.SCX)),
                    (get(Reg.WX) - 7) + get(Reg.SCX));
            winY++;
        }
        bgWindowLine = bgWindowLine.extractWrapped(get(Reg.SCX), LCD_WIDTH)
                .mapColors(get(Reg.BGP));
        if (spriteActivated()) {
            LcdImageLine backgroundSprites = computeSpriteLine(index, true);
            BitVector opacity = bgWindowLine.getOpacity().not()
                    .and(backgroundSprites.getOpacity()).not();
            LcdImageLine foregroundSprites = computeSpriteLine(index, false);
            return backgroundSprites.below(bgWindowLine, opacity)
                    .below(foregroundSprites);
        }

        return bgWindowLine;

    }

    private boolean spriteActivated() {
        return Bits.test(get(Reg.LCDC), 1);

    }

    private LcdImageLine computeSpriteLine(int lineIndex, boolean bgSprites) {
        int[] sprites = spritesIntersectingLine(lineIndex);

        int sprite;
        LcdImageLine result = new LcdImageLine.Builder(LCD_WIDTH).build();

        int x;

        for (int i = 0; i < sprites.length; i++) {
            sprite = sprites[i];

            x = getSpriteX(sprite);

            if (spriteInBackGround(sprite) == bgSprites) {
                result = spriteLine(sprite, lineIndex)
                        .mapColors(getSpritePalette(sprite)).shift(x - 8)
                        .below(result);

            }
        }
        return result;

    }

    private int getSpriteX(int index) {
        return oam.read(index * 4 + 1);
    }

    private boolean spriteInBackGround(int spriteIndex) {
        return Bits.test(oam.read(spriteIndex * 4 + 3), 7);
    }

    private void setLyLyc(Reg a, int data) {
        set(a, data);
        boolean equal = get(Reg.LY) == get(Reg.LYC);
        set(Reg.STAT, Bits.set(get(Reg.STAT), 2, equal));
        if (Bits.test(get(Reg.STAT), 6) && equal)
            cpu.requestInterrupt(Interrupt.LCD_STAT);

    }

    private int TileIndex(int tile, boolean area) {
        int start = AddressMap.BG_DISPLAY_DATA[area ? 1 : 0];

        return videoRam.read(start + tile - AddressMap.VIDEO_RAM_START);
    }

    private LcdImageLine spriteLine(int index, int line) {
        LcdImageLine.Builder result = new LcdImageLine.Builder(LCD_WIDTH);
        int tile = oam.read(index * 4 + 2);
        line = isFlippedVertically(index)
                ? spriteHeight()
                        - (line - oam.read(index * 4) + TILE_EDGE * 2 + 1)
                : line - oam.read(index * 4) + TILE_EDGE * 2;

        boolean horizontalFlip = isFlippedHorizontally(index);
        result.setByte(0,
                getTileImageByte(line * 2 + 1, tile, true, horizontalFlip),
                getTileImageByte(line * 2, tile, true, horizontalFlip));
        return result.build();
    }

    private boolean isFlippedVertically(int index) {
        return Bits.test(oam.read(index * 4 + 3), 6);
    }

    private boolean isFlippedHorizontally(int index) {
        return Bits.test(oam.read(index * 4 + 3), 5);
    }

    private int getTileImageByte(int byteIndex, int tileIndex, boolean area,
            boolean reverse) {

        int address;
        if (area) {

            address = AddressMap.TILE_SOURCE[1] + tileIndex * TILE_IMAGE_BYTES
                    + byteIndex;

        } else {

            int shift = tileIndex < 0x80 ? 0x80 : -0x80;
            address = AddressMap.TILE_SOURCE[0]
                    + (tileIndex + shift) * TILE_IMAGE_BYTES + byteIndex;

        }

        return reverse ? videoRam.read(address - AddressMap.VIDEO_RAM_START)
                : Bits.reverse8(
                        videoRam.read(address - AddressMap.VIDEO_RAM_START));

    }

    private boolean theTileSourceEffect() {
        return Bits.test(get(Reg.LCDC), 4);
    }

    private int getSpritePalette(int index) {
        return Bits.test(oam.read(index * 4 + 3), 4) ? get(Reg.OPB1)
                : get(Reg.OPB0);
    }

    private LcdImageLine reallyComputeLine(int index, boolean area) {
        int firstByte = (index % TILE_EDGE) * 2;
        int firstTile = (index / TILE_EDGE) * (IMAGE_EDGE / TILE_EDGE);
        LcdImageLine.Builder builder = new LcdImageLine.Builder(IMAGE_EDGE);
        for (int i = 0; i < 32; i++) {
            int tileIndex = TileIndex(firstTile + i, area);
            builder.setByte(i,
                    getTileImageByte(firstByte + 1, tileIndex,
                            theTileSourceEffect(), false),
                    getTileImageByte(firstByte, tileIndex,
                            theTileSourceEffect(), false));
        }
        return builder.build();

    }

    private void reallyCycle() {

        switch (currentMode()) {
        case MODE_0: {
            setLyLyc(Reg.LY, get(Reg.LY) + 1);

            if (get(Reg.LY) == LCD_HEIGHT) {

                changeMode(Mode.MODE_1);
                cpu.requestInterrupt(Interrupt.VBLANK);
                nextImage = nextImageBuilder.build();

            } else {

                changeMode(Mode.MODE_2);

            }
        }
            break;
        case MODE_1: {

            if (get(Reg.LY) == 153) {
                changeMode(Mode.MODE_2);
                nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
                setLyLyc(Reg.LY, 0);
                winY = 0;

            } else {
                setLyLyc(Reg.LY, get(Reg.LY) + 1);
                nextNonIdleCycle += Mode.MODE_1.lineCycles;

            }
        }
            break;
        case MODE_2: {

            changeMode(Mode.MODE_3);

            nextImageBuilder.setLine(get(Reg.LY), computeLine(get(Reg.LY)));

        }
            break;
        case MODE_3: {

            changeMode(Mode.MODE_0);

        }

        }
    }

    private void set(Reg a, int data) {
        lcdcRegs.set(a, data);
    }

    private Mode currentMode() {
        return Mode.values()[Bits.clip(2, get(Reg.STAT))];
    }

    private int get(Reg a) {
        return lcdcRegs.get(a);
    }

    private boolean windowActivated() {
        return Bits.test(get(Reg.LCDC), 5) && get(Reg.WX) >= 7
                && get(Reg.WX) < 167;
    }

    private boolean backGroundActivated() {
        return Bits.test(get(Reg.LCDC), 0);
    }

    private boolean screenIsOn() {
        return Bits.test(get(Reg.LCDC), 7);
    }

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

    private int[] spritesIntersectingLine(int lineIndex) {
        int spriteHeight = spriteHeight();
        int[] intersectingSprites = new int[10];
        int spriteIndex = 0;
        int nbSprites = 0;
        int realY = 0;
        while (nbSprites < 10 && spriteIndex < 40) {
            realY = oam.read(spriteIndex * 4) - 16;
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

    private int spriteHeight() {
        return Bits.test(get(Reg.LCDC), 2) ? 2 * TILE_EDGE : TILE_EDGE;

    }

}
