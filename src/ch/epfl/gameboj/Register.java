package ch.epfl.gameboj;

/**
 * Interface qui représente un registre
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public interface Register {
    
    /**
     * une méthode  fournie par une énumération de registres
     * @return la position du registre dans l'énumération
     * @see Enum#ordinal()
     */
    int ordinal();
    
    /**
     * retourne la meme valeur que la methode ordinal
     * @return la meme valeur que la methode ordinal
     */
    default int index() {
        return ordinal(); 
    }

}
