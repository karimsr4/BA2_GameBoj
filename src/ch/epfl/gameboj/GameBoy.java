package ch.epfl.gameboj;

import java.util.Objects;

import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

/**
 * Classe qui simule le Gameboy
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class GameBoy {
    private Ram ram;
    private RamController ramController;
    private RamController echoRamController;
    private Bus bus;
    private Cpu cpu;
    private long cycles;
    private BootRomController bootRomController;
    private Timer timer;

    /**
     * Construit une nouvelle Gameboy en créant ses composants et en les
     * attachant au bus
     * 
     * @param cartridge
     *            jeu a jouer
     * @throws NullPointerException
     *             si la cartouche est null
     */
    public GameBoy(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        ram = new Ram(AddressMap.WORK_RAM_SIZE);
        ramController = new RamController(ram, AddressMap.WORK_RAM_START);
        echoRamController = new RamController(ram, AddressMap.ECHO_RAM_START,
                AddressMap.ECHO_RAM_END);
        cpu = new Cpu();
        bus = new Bus();
        bootRomController = new BootRomController(cartridge);
        timer = new Timer(cpu);

        bus.attach(ramController);
        bus.attach(echoRamController);
        cpu.attachTo(bus);
        bus.attach(bootRomController);
        bus.attach(timer);
    }

    /**
     * retourne le processeur du Game Boy
     * 
     * @return le processeur
     */
    public Cpu cpu() {
        return this.cpu;
    }

    /**
     * retourne le bus de la gameboy
     * 
     * @return bus de la gameboy reliant ses composants
     */
    public Bus bus() {
        return bus;

    }

    /**
     * simule le fonctionnement du GameBoy jusqu'au cycle donné moins 1
     * 
     * @param cycle
     *            le cycle donné
     * @throws IllegalArgumentException
     *             si un nombre (strictement) supérieur de cycles a déjà été
     *             simulé
     */
    public void runUntil(long cycle) {
        Preconditions.checkArgument(cycles <= cycle);
        while (cycles < cycle) {
            timer.cycle(cycles);
            cpu.cycle(cycles);
            cycles++;
        }
    }

    /**
     * retourne le nombre de cycles déjà simulés
     * 
     * @return le nombre de cycles déjà simulés
     */
    public long cycles() {
        return cycles;
    }

    /**
     * retourne le minuteur du GameBoy
     * 
     * @return le minuteur du GameBoy
     */
    public Timer timer() {
        return this.timer;
    }

}
