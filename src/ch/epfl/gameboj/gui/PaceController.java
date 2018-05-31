package ch.epfl.gameboj.gui;

/**
 * Classe qui représente le controlleur de vitesse de la simulation
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class PaceController {
    private double accelerationRatio = 1;
    private long time;
    private long actualCycles;

    /**
     * méthode setter poour l'attribut time
     * 
     * @param timeNow
     */
    public void setTime(long timeNow) {
        time = timeNow;
    }

    /**
     * calcule le temps passé depuis le dernier appel
     * 
     * @param timeNow
     *            temps au moment de l'appel
     * @return le temps passé depuis le dernier appel
     */
    public long computeElapsedTime(long timeNow) {
        return timeNow - time;
    }

    /**
     * initialise le controlleur avec une accélération initiale normale
     */
    public PaceController() {
        this.accelerationRatio = 1;
    }

    /**
     * retourne l'accélration de la simulation
     * 
     * @return l'accélration de la simulation
     */
    public double getAccelerationRatio() {
        return accelerationRatio;
    }

    /**
     * methode setter pour accelerationRation
     * 
     * @param d
     *            nouvelle accéleration
     */
    public void setAccelerationRatio(double d) {
        this.accelerationRatio = d;
    }

    /**
     * ajoute cycles au cycles déja simulés
     * 
     * @param cycles
     *            cycles à ajouter
     */
    public void addCycles(long cycles) {
        actualCycles += cycles;
    }

    /**
     * retourne les cycles simulés
     * 
     * @return les cycles simulés
     */
    public long getCycles() {
        return actualCycles;
    }

}
