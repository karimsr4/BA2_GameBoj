package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;

public class Cpu implements Component, Clocked  {
    
    
    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
      }
    
    
    private enum Reg16 implements Register {
        AF, BC, DE , HL
      }
    
    
    public int[] _testGetPcSpAFBCDEHL() {
        return null;
        
    }


    @Override
    public void cycle(long cycle) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public int read(int address) {
        return NO_DATA;
    }


    @Override
    public void write(int address, int data) {
        
    }

}
