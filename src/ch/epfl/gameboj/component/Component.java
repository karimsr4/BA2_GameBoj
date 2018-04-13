package ch.epfl.gameboj.component;

import ch.epfl.gameboj.Bus;

/**
 * Interface qui represente l'abstraction d'un composant
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public interface Component {

    /**
     * attribut utilisé pour signaler le fait qu'il n'y a aucune donnée à lire à l'adresse reçue
     */
    int NO_DATA = 256;

    /**
     * retourne l'octet stocké a l'adresse donnee par le composant, ou NO_DATA
     * si le composant ne possède aucune valeur à cette adresse
     * 
     * @param address
     *            adresse du composant
     * @return l'octet stocke a l'adresse donnée par le composant, ou NO_DATA si
     *         le composant ne possède aucune valeur a cette adresse
     * @throws IllegalArgumentException
     *             si l'adresse n'est pas une valeur de 16 bits
     */
    int read(int address);

    /**
     * stocke la valeur donnee a l'adresse donnée dans le composant ne fait rien
     * si le composant ne permet pas de stocker de valeur à cette adresse
     * 
     * @param address
     *            adresse ou stocker la valeur
     * @param data
     *            valeur à stocker
     * @throws IllegalArgumentException
     *             si l'adresse n'est pas une valeur de 16 bits ou si la donnee
     *             n'est pas une valeur 8 bits
     */
    void write(int address, int data);

    
    
    /**
     * attache le composant au bus donné
     * 
     * @param bus
     *            bus auquel le composant sera attaché
     */
    default void attachTo(Bus bus) {
        bus.attach(this);
    }

}
