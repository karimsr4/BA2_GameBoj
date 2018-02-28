package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;

/**
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class RamController implements Component {

    private Ram ram;
    private int startAddress;
    private int endAddress;

    /**
     * construit un contrôleur pour la mémoire vive donnée
     * 
     * @param ram
     *            mémoire vive pour laquelle le controlleur sera construit
     * @param startAddress
     *            adresse de départ en mémoire
     * @param endAddress
     *            dernière adresse (non accessible) délimitant la zone en mémoie
     * @throws NullPointerException
     *             si la mémoire vive donnée est nulle
     * @throws IllegalArgumentException
     *             si l'une des valeurs n'est pas une valeur 16 bits ou si
     *             l'intervalle qu'elles délimitent a une taille négative ou a
     *             une taille supérieure a celle de la mémoire
     */
    public RamController(Ram ram, int startAddress, int endAddress) {
        Objects.requireNonNull(ram);
        Preconditions.checkBits16(startAddress);
        Preconditions.checkBits16(endAddress);
        Preconditions.checkArgument((endAddress - startAddress >= 0)
                && (endAddress - startAddress <= ram.size()));
        this.ram = ram;
        this.startAddress = startAddress;
        this.endAddress = endAddress;

    }

    /**
     * construit un controlleur pour la totalité de la mémoire vive fournie
     * 
     * @param ram
     *            mémoire vive pour laquelle le controlleur sera construit
     * @param startAddress
     *            adresse de départ en mémoire
     */
    public RamController(Ram ram, int startAddress) {
        this(ram, startAddress, startAddress + ram.size());
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if ((address >= startAddress) && (address < endAddress)) {
            return ram.read(address - startAddress);
        }
        return Component.NO_DATA;

    }

    @Override
    public void write(int address, int data) {

        Preconditions.checkBits8(data);
        Preconditions.checkBits16(address);
        if ((address >= startAddress) && (address < endAddress)) {
            this.ram.write(address - startAddress, data);
        }

    }

}
