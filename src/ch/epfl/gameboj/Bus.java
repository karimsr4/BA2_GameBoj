package ch.epfl.gameboj;

import java.util.ArrayList;
import static java.util.Objects.*;
import static ch.epfl.gameboj.Preconditions.*;
import ch.epfl.gameboj.component.Component;

/**
 * Classe qui simule le bus
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class Bus {

    private final ArrayList<Component> componentArray = new ArrayList<Component>();

    /**
     * attache le composant donné au bus
     * 
     * @param component
     *            composant à attacher au bus
     * @throws NullPointerException
     *             si le composant donné vaut null
     */
    public void attach(Component component) {
        requireNonNull(component);
        componentArray.add(component);
    }

    /**
     * retourne la valeur stockée à l'adresse donnée si au moins un des
     * composants attaché au bus possède une valeur à cette adresse
     * 
     * @param address
     *            adresse en mémoire
     * @return la valeur retourne la valeur stockée à l'adresse donnée si au
     *         moins un des composants attaché au bus possède une valeur à cette
     *         adresse
     * @return 0xFF si aucun composant attaché au bus ne possède une valeur à
     *         cette adresse
     * @throws IllegalArgumentException
     *             si l'adresse donnée n'est pas une valeur 16 bits
     */
    public int read(int address) {

        checkBits16(address);

        for (Component e : componentArray) {
            if (e.read(address) != Component.NO_DATA)
                return e.read(address);
        }

        return 0xFF;

    }

    /**
     * stocke la valeur donnée à l'adresse donnée dans tous les composants
     * connectés au bus 
     * 
     * @param address
     *            adresse ou stocker la valeur
     * @param data
     *            valeur à stocker
     * @throws IllegalArgumentException
     *             si l'adresse n'est pas une valeur 16 bits ou si la valeur
     *             n'est pas une valeur 8 bits
     */
    public void write(int address, int data) {

        checkBits8(data);
        checkBits16(address);

        for (Component e : componentArray) {
            e.write(address, data);
        }

    }

}
