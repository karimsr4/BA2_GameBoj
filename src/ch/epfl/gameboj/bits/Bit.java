package ch.epfl.gameboj.bits;

/**
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public interface Bit {

    /**
     * @return
     */
    int ordinal();

    /**
     * retourne la meme valeur que la methode ordinal
     * @return la meme valeur que la methode ordinal
     */
    default int index() {
        return this.ordinal();
    }

    /**
     * retourne le masque correspondant au bit
     * 
     * @return le masque correspondant au bit
     */
    default int mask() {
        return Bits.mask(index());

    }

}
