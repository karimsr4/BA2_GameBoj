package ch.epfl.gameboj.component.cartridge;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Classe qui simule un contrÃ´leur de banque mÃ©moire de type 0
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class MBC0 implements Component {

    private final int ROM_SIZE = 0x8000;
    private Rom rom;

    /**
     * construit un contrÃ´leur de banque mÃ©moire de type 0
     * 
     * @param rom
     *            la memoire morte
     * @throws NullPointerException
     *             si l'argument rom est null
     * @throws IllegalArgumentException
     *             si la taille de la rom n'est pas egale 32768
     */
    public MBC0(Rom rom) {
        Objects.requireNonNull(rom);
        Preconditions.checkArgument(rom.size() == ROM_SIZE);
        this.rom = rom;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address >= ROM_SIZE) {
            return NO_DATA;
        }
        return rom.read(address);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#write(int, int)
     */
    @Override
    public void write(int address, int data) {
    }

}
