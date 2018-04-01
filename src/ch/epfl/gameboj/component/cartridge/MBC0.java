package ch.epfl.gameboj.component.cartridge;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Classe qui simule un contrôleur de banque mémoire de type 0
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class MBC0 implements Component {

    private Rom rom;

    /**
     * construit un contrôleur de banque mémoire de type 0 
     * @param rom la memoire morte
     */
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
        return rom.read(address); 
    }

    @Override
    public void write(int address, int data) {
    }

}
