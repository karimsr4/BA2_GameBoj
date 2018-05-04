package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.memory.Ram;

import static ch.epfl.gameboj.Preconditions.*;

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
    public static final int BG_EDGE = 256;
    private final int MODE2_CYCLES = 20;
    private final int MODE3_CYCLES = 43;
    private final int MODE0_CYCLES = 51;
    private final int LINE_CYCLES = 114;

    private final Cpu cpu;
    private final Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    private final RegisterFile<Reg> lcdcRegs = new RegisterFile<>(Reg.values());
    private long nextNonIdleCycle = Long.MAX_VALUE;
    private LcdImage.Builder nextImageBuilder = new LcdImage.Builder(LCD_WIDTH,
            LCD_HEIGHT);
    private LcdImage currentImage = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT)
            .build();
    private int winY;

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
        return currentImage;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Clocked#cycle(long)
     */
    @Override
    public void cycle(long cycle) {

        if (nextNonIdleCycle == Long.MAX_VALUE && screenIsOn()) {

            nextNonIdleCycle = cycle + MODE2_CYCLES;
            changeMode(2);

        }
        if (cycle == nextNonIdleCycle) {
            reallyCycle();
        }

    }

    private void reallyCycle() {

        switch (currentMode()) {
        case 0: {

            if (get(Reg.LY) == LCD_HEIGHT - 1) {

                changeMode(1);
                nextNonIdleCycle += LINE_CYCLES;
                cpu.requestInterrupt(Interrupt.VBLANK);
                setLyLyc(Reg.LY, get(Reg.LY) + 1);
                currentImage = nextImageBuilder.build();
                winY = 0;

            } else {

                setLyLyc(Reg.LY, get(Reg.LY) + 1);
                changeMode(2);
                nextNonIdleCycle += MODE2_CYCLES;

            }
        }
            break;
        case 1: {

            if (get(Reg.LY) == 153) {
                changeMode(2);
                nextNonIdleCycle += MODE2_CYCLES;
                nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
                setLyLyc(Reg.LY, 0);
                winY = 0;

            } else {
                setLyLyc(Reg.LY, get(Reg.LY) + 1);
                nextNonIdleCycle += LINE_CYCLES;

            }
        }
            break;
        case 2: {

            changeMode(3);
            nextNonIdleCycle += MODE3_CYCLES;

            nextImageBuilder.setLine(get(Reg.LY),
                    computeLine((get(Reg.LY) + get(Reg.SCY)) % BG_EDGE)
                            .mapColors(get(Reg.BGP))
                            .extractWrapped(get(Reg.SCX), LCD_WIDTH));

        }
            break;
        case 3: {

            changeMode(0);
            nextNonIdleCycle += MODE0_CYCLES;

        }

        }
    }

    private void changeMode(int mode) {
        int mask = Bits.mask(0) | Bits.mask(1);
        int statNewValue = (mode & mask) | (get(Reg.STAT) & (~mask));
        set(Reg.STAT, statNewValue);
        if (Bits.test(get(Reg.STAT), mode + 3) && mode != 3)
            cpu.requestInterrupt(Interrupt.LCD_STAT);

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        checkBits16(address);
        if (address >= AddressMap.VIDEO_RAM_START
                && address < AddressMap.VIDEO_RAM_END)
            return videoRam.read(address - AddressMap.VIDEO_RAM_START);
        if (address >= AddressMap.REGS_LCDC_START
                && address < AddressMap.REGS_LCDC_END)
            return get(Reg.values()[address - AddressMap.REGS_LCDC_START]);

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
        if (address >= AddressMap.VIDEO_RAM_START
                && address < AddressMap.VIDEO_RAM_END) {
            videoRam.write(address - AddressMap.VIDEO_RAM_START, data);
        } else if (address >= AddressMap.REGS_LCDC_START
                && address < AddressMap.REGS_LCDC_END) {
            if (address == AddressMap.REGS_LCDC_START
                    && !(Bits.test(data, 7))) {

                setLyLyc(Reg.LY, 0);
                changeMode(0);
                nextNonIdleCycle = Long.MAX_VALUE;

            } else if (address == AddressMap.REG_STAT) {
                set(Reg.STAT, Bits.clip(3, get(Reg.STAT))
                        | (Bits.extract(get(Reg.STAT), 3, 5) << 3));
                return;
            } else if (address == AddressMap.REG_LYC) {
                setLyLyc(Reg.LYC, data);
                return;
            } else if (address == AddressMap.REG_LY) {
                return;
            }
            set(Reg.values()[address - AddressMap.REGS_LCDC_START], data);

        }

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

    private int getTileImageByte(int index, int tileIndex) {

        int result;
        if (Bits.test(get(Reg.LCDC), 4)) {

            int address = AddressMap.TILE_SOURCE[1] + tileIndex * 16 + index;
            result = Bits.reverse8(
                    videoRam.read(address - AddressMap.VIDEO_RAM_START));

        } else {

            int shift = tileIndex < 0x80 ? 0x80 : -0x80;
            int address = AddressMap.TILE_SOURCE[0] + (tileIndex + shift) * 16
                    + index;
            return Bits.reverse8(
                    videoRam.read(address - AddressMap.VIDEO_RAM_START));

        }

        return result;

    }

    private LcdImageLine computeLine(int index) {
        LcdImageLine result = new LcdImageLine.Builder(256).build();
        if (backGroundActivated()) {
            result = reallyComputeLine(index, Bits.test(get(Reg.LCDC), 3));
        }

        if (windowActivated()
                && index >= (get(Reg.WY) + get(Reg.SCY)) % BG_EDGE) {
            result = result.join(
                    reallyComputeLine(winY, Bits.test(get(Reg.LCDC), 6))
                            .shift((get(Reg.WX) - 7 + get(Reg.SCX))),
                    (get(Reg.WX) - 7) + get(Reg.SCX));
            winY++;
        }
        return result;

    }

    private LcdImageLine reallyComputeLine(int index, boolean area) {
        int firstByte = (index % Byte.SIZE) * 2;
        int firstTile = (index / Byte.SIZE) * 32;
        LcdImageLine.Builder builder = new LcdImageLine.Builder(256);
        for (int i = 0; i < 32; i++) {
            int tileIndex = TileIndex(firstTile + i, area);
            builder.setByte(i, getTileImageByte(firstByte + 1, tileIndex),
                    getTileImageByte(firstByte, tileIndex));
        }
        return builder.build();

    }

    private int get(Reg a) {
        return lcdcRegs.get(a);
    }

    private void set(Reg a, int data) {
        lcdcRegs.set(a, data);
    }

    private boolean screenIsOn() {
        return Bits.test(get(Reg.LCDC), 7);
    }

    private int currentMode() {
        return Bits.clip(2, get(Reg.STAT));
    }

    private boolean backGroundActivated() {
        return Bits.test(get(Reg.LCDC), 0);
    }

    private boolean windowActivated() {
        return Bits.test(get(Reg.LCDC), 5) && get(Reg.WX) >= 7
                && get(Reg.WX) < 167;
    }

}
