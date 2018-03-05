package ch.epfl.gameboj;

public interface Register {
    
    int ordinal();
    
    default int index() {
        return this.ordinal();
        
    }

}
