package ch.epfl.gameboj.component;

import static ch.epfl.gameboj.Preconditions.*;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;

public final class Joypad implements Component {

    private final Cpu cpu;
    private int regP1;
    private int line0_pressedKeys;
    private int line1_pressedKeys;
    public enum Key{
        RIGHT, LEFT, UP, DOWN, A, B, SELECT , START
    }
    
    public Joypad(Cpu cpu) {
        this.cpu=cpu;
    }
    
    
    public void keyPressed(Key key) {
        
    }
    
    public void keyReleased(Key key) {
        
    }
    
    @Override
    public int read(int address) {
        checkBits16(address);
        if (address==AddressMap.REG_P1)
            return Bits.complement8(regP1);
        return NO_DATA;
    }

    @Override
    public void write(int address, int data) {
        checkBits16(address);
        checkBits8(data);
        if (address==AddressMap.REG_P1){
            regP1=Bits.complement8(((data>>>4)<<4) & Bits.clip(4, regP1));
        }
        
    }

}
