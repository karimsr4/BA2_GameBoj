package ch.epfl.gameboj.component;

import java.util.Objects;

import ch.epfl.gameboj.component.cpu.Cpu;

public final class Timer implements Clocked, Component {

    
    private Cpu cpu;
    
    
    public Timer(Cpu cpu) {
        Objects.requireNonNull(cpu)
    }
    
    
    
    
    
    @Override
    public int read(int address) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void write(int address, int data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void cycle(long cycle) {
        // TODO Auto-generated method stub
        
    }

}
