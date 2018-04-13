package ch.epfl.gameboj.component;

/**
 * Interface qui représente un element controlé par une horloge
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public interface Clocked {

    /**
     * demande au composant d'évoluer en exécutant toutes les opérations qu'il
     * doit exécuter durant le cycle d'index donné en argument.
     * 
     * @param cycle
     *            cycle donné
     */
    void cycle(long cycle);

}
