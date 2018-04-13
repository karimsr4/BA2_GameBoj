package ch.epfl.gameboj.bits;

/**
 * Interface représentant un ensemble de bits
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public interface Bit {

    /**
     * une méthode  fournie par l’énumération qui représente les bits
     * @return la position du bit dans l'énumération
     * @see Enum#ordinal()
     */
    int ordinal();

    /**
     * retourne la meme valeur que la methode ordinal
     * @return la meme valeur que la methode ordinal
     * @see Bit#ordinal()
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
