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

import com.sun.media.sound.RealTimeSequencerProvider;

public final class LcdController implements Component, Clocked {
    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    public static final int BG_EDGE = 256;
    private final Cpu cpu;
    private final Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    private final RegisterFile<Reg> lcdcRegs = new RegisterFile<>(Reg.values());
    private long nextNonIdleCycle = Long.MAX_VALUE;
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

            nextNonIdleCycle = cycle + 20;
            changeMode(2);

        }
        if (cycle == nextNonIdleCycle) {
            reallyCycle();
        }

    }

    private void reallyCycle() {

        switch (Bits.clip(2, get(Reg.STAT))) {
        case 0: {
            
            if (get(Reg.LY) == LCD_HEIGHT - 1) {

                changeMode(1);
                nextNonIdleCycle += 114;

                cpu.requestInterrupt(Interrupt.VBLANK);
                setLyLyc(Reg.LY, get(Reg.LY) + 1);

                currentImage = currentImageBuilder.build();

            } else {

                setLyLyc(Reg.LY, get(Reg.LY) + 1);
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
            currentImageBuilder.setLine(get(Reg.LY) ,computeLine( get(Reg.LY) ) );
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

    private int TileIndex(int tile) {
        int start = AddressMap.BG_DISPLAY_DATA[Bits.test(get(Reg.LCDC), 3) ? 1
                : 0];
        return videoRam.read(start + tile - AddressMap.VIDEO_RAM_START);
    }

    private int getTileImageByte(int index, int tile) {
        int tileIndex = TileIndex(tile);
        int result;
        if (Bits.test(get(Reg.STAT), 4)) {
            int address = AddressMap.TILE_SOURCE[1] + tileIndex * 16 + index;
            result= Bits.reverse8(
                    videoRam.read(address - AddressMap.VIDEO_RAM_START));
            
        } else {

            int shift = tileIndex <= 0x7F ? 0x7F : -0x80;
            
            int address = AddressMap.TILE_SOURCE[0] + (tileIndex + shift) * 16
                    + index;
           
            result= Bits.reverse8(
                    videoRam.read(address - AddressMap.VIDEO_RAM_START));
          
        }

        return result;

    }

    private LcdImageLine computeLine(int index) {
        int firstByte = (index % 8) * 2;
        int firstTile = (index / 8) * 32;
        LcdImageLine.Builder builder = new LcdImageLine.Builder(256);

        for (int i = 0; i < 32; i++) {
            builder.setByte(i, getTileImageByte(firstByte + 1, firstTile + i),
                    getTileImageByte(firstByte, firstTile + i));
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

}
