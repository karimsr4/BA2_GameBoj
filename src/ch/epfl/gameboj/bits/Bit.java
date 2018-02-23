package ch.epfl.gameboj.bits;

import java.util.Objects;

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
     * @return
     */
    default int index(){
       return this.ordinal();
    }
    
    /**
     * @return
     */
    default int mask(){
       return Bits.mask(index());
        
    }

}
