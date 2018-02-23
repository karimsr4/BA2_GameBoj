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
     * 
     * @param ram
     * @param startAddress
     * @param endAddress
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
     * @param ram
     * @param startAddress
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
