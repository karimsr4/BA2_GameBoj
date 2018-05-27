package ch.epfl.gameboj.gui;

public final class TurboCounter {
    private double ratio = 1;

    public TurboCounter(long start) {
        this.start = start;
    }

    public double getRatio() {
        return ratio;
    }

   
    public long getStart() {
        return start;
    }

    public void setStart(long cycles) {
        this.start = cycles;
    }

    private long start;

    public void setRatio(double d) {
        this.ratio = d;        
    }

}
