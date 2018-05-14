package ch.epfl.gameboj.gui;

public final class TurboCounter {

    private int counter=1;
    
    public void increment() {
        this.counter++;
    }
    
    public int getCounter() {
        return this.counter;
    }
    
    
    public void reset () {
        this.counter=1;
        }
    
}
