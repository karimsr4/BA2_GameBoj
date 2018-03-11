package ch.epfl.gameboj;

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
