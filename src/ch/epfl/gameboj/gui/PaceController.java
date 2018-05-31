package ch.epfl.gameboj.gui;

public final class PaceController {
    private double accelerationRatio = 1;
    private long elapsedTime;
    private long actualCycles;
    

    
    public void setElapsedTime(long timeNow)
    {
        elapsedTime=timeNow;
    }
    
    
    
    public long computeTimeDifference(long timeNow)
    {
        return timeNow-elapsedTime;
    }
    
    
 

    public PaceController() {
        this.accelerationRatio=1;
    }
    


    public double getRatio() {
        return accelerationRatio;
    }

   


    public void setAccelerationRatio(double d) {
        this.accelerationRatio = d;        
    }
    

    
    
    public void addCycles(long cycles) {
        actualCycles+=cycles;
    }
    
    
    public long getCycles() {
        return actualCycles;
    }

}
