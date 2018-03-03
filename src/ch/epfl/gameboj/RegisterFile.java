package ch.epfl.gameboj;

import ch.epfl.gameboj.bits.Bit;

/**
 * Classe simulant un banc de registre de 8 bits
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class RegisterFile <E extends Register> {
    
    
    /**
     * constructeur qui construit un banc de registres 8 bits 
     * @param allRegs
     */
    public RegisterFile(E[] allRegs) {
        
    }

    
    public int get(E reg) {
        return 0;
        
    }
    
    
    
    public void set(E reg, int newValue) {
        Preconditions.checkBits8(newValue);
    }
    
    
    public boolean testBit(E reg, Bit b) {
        return false;
        
    }
    
    
    public void setBit(E reg, Bit bit, boolean newValue) {
        
    }
    
    
}
