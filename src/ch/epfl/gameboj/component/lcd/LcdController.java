package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.lcd.LcdImage.Builder;
import ch.epfl.gameboj.component.memory.Ram;

import static ch.epfl.gameboj.Preconditions.*;

public final class LcdController implements Component, Clocked {
    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    public static final int BG_EDGE = 256;
    private final Cpu cpu;
    private final Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    private final RegisterFile<Reg> lcdcRegs = new RegisterFile<>(Reg.values());
    private long nextNonIdleCycle= Long.MAX_VALUE;
    private LcdImage.Builder currentImageBuilder = new LcdImage.Builder(BG_EDGE,
            BG_EDGE);
    private LcdImage currentImage;

    private enum Reg implements Register {
        LCDC, STAT, SCY, SCX, LY, LYC, DMA, BGP, OPB0, OPB1, WY, WX;
    }

    public LcdController(Cpu cpu) {
        for (Reg o : Reg.values())
            set(o, 0);

        this.cpu = cpu;
    }

    public LcdImage currentImage() {
        return currentImage;
    }

    @Override
    public void cycle(long cycle) {
        
        
        if (nextNonIdleCycle == Long.MAX_VALUE && screenIsOn()) {
            nextNonIdleCycle = cycle;
        }
        if (cycle == nextNonIdleCycle  ) {
            reallyCycle();
        }
        

    }

    private void reallyCycle() {

        switch (Bits.clip(2, get(Reg.STAT))) {
        case 0: {
            if (get(Reg.LY) == LCD_HEIGHT - 1) {
                System.out.println(nextNonIdleCycle);
                
                changeMode(1);
                nextNonIdleCycle += 114;
                
                cpu.requestInterrupt(Interrupt.VBLANK);
                setLyLyc(Reg.LY, get(Reg.LY) + 1);

                currentImage = currentImageBuilder.build();
                
            } else {
                
                setLyLyc(Reg.LY, get(Reg.LY)+1);
                changeMode(2);
                nextNonIdleCycle += 20;

            }
        }
            break;
        case 1: {

            if (get(Reg.LY) == 153) {
                changeMode(2);

                currentImageBuilder = new LcdImage.Builder(BG_EDGE, BG_EDGE);
                setLyLyc(Reg.LY, 0);

                nextNonIdleCycle += 20;
            } else {
                setLyLyc(Reg.LY, get(Reg.LY) + 1);
                nextNonIdleCycle += 114;

            }
        }
            break;
        case 2: {

            changeMode(3);
            nextNonIdleCycle += 43;

        }
            break;
        case 3: {

            changeMode(0);
            nextNonIdleCycle += 51;
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
                // set(Reg.STAT, (get(Reg.STAT) >>> 2) << 2);
                changeMode(0);
                nextNonIdleCycle = Long.MAX_VALUE;

            } else if (address == AddressMap.REGS_LCDC_START + 1) {
                set(Reg.STAT, Bits.clip(3, get(Reg.STAT))
                        | (Bits.extract(get(Reg.STAT), 3, 5) << 3));
                return;
            } else if (address == AddressMap.REGS_LCDC_START + 5) {
                setLyLyc(Reg.LYC, data);
                return;
            } else if (address == AddressMap.REGS_LCDC_START + 4) {
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

    private int get(Reg a) {
        return lcdcRegs.get(a);
    }

    private void set(Reg a, int data) {
        lcdcRegs.set(a, data);
    }

    private boolean screenIsOn() {
        return Bits.test(get(Reg.LCDC), 7);
    }

}
