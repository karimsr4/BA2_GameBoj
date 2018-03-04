package ch.epfl.gameboj;

import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

/**
 * Classe simulant un banc de registre de 8 bits
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class RegisterFile <E extends Register> {
    
    
    private int taille;
    private Byte[] allRegs;
    
    /**
     * constructeur qui construit un banc de registres 8 bits 
     * @param allRegs
     */
    public RegisterFile(E[] allRegs) {
       this.allRegs=new Byte[allRegs.length];
        
    }

    
    /**
     * retourne la valeur 8 bits contenue dans le registre donné, 
     * sous la forme d'un entier compris entre 0 (inclus) et FF16 (inclus)
     * @param reg registre donné
     * @return la valeur 8 bits contenue dans reg
     */
    public int get(E reg) {
        
        return Preconditions.checkBits8(allRegs[reg.index()]);
        
    }
    
    
    
    public void set(E reg, int newValue) {
        Preconditions.checkBits8(newValue);
        this.allRegs[reg.index()]=(byte) newValue;
        
        
    }
    
    
    public boolean testBit(E reg, Bit b) {
        return Bits.test(this.allRegs[reg.index()], b);
        
    }
    
    
    public void setBit(E reg, Bit bit, boolean newValue) {
        
    }
    
    
}
