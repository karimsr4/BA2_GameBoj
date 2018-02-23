package ch.epfl.gameboj;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.gameboj.component.Component;

/**
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class Bus {

    private ArrayList<Component> componentArray = new ArrayList<Component>();

    /**
     * @param component
     */
    public void attach(Component component) {
        Objects.requireNonNull(component);
        componentArray.add(component);
    }

    /**
     * @param address
     * @return
     */
    public int read(int address) {

        Preconditions.checkBits16(address);

        for (Component e : componentArray) {
            if (e.read(address) != Component.NO_DATA)
                return e.read(address);
        }

        return 0xFF;

    }

    /**
     * @param address
     * @param data
     */
    public void write(int address, int data) {

        Preconditions.checkBits8(data);
        Preconditions.checkBits16(address);

        for (Component e : componentArray) {
            e.write(address, data);
        }

    }

}
