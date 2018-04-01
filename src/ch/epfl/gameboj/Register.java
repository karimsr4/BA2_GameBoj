package ch.epfl.gameboj;

/**
 * Interface qui represente l'abstraction du registre
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public interface Register {
    
    /**
     * @return 
     */
    int ordinal();
    
    /**
     * @return
     */
    default int index() {
        return this.ordinal();
        
    }

}
