package ch.epfl.gameboj.component.cartridge;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class MBC0 implements Component {

    private Rom rom;

    public MBC0 (Rom rom) {
    Objects.requireNonNull(rom);
        if (rom.size() != 32768)
            throw new IllegalArgumentException();
        this.rom = rom;

    }

    @Override
    public int read(int address) {
        if (address>=0x8000) {
            return NO_DATA;
        }
        return rom.read(Preconditions.checkBits16(address)); 
    }

    @Override
    public void write(int address, int data) {
    }

}
