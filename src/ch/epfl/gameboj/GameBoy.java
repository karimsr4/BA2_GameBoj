package ch.epfl.gameboj;

import static java.util.Objects.*;
import static ch.epfl.gameboj.Preconditions.*;
import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.lcd.LcdController;
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
    private final Ram ram;
    private final RamController ramController;
    private final RamController echoRamController;
    private final Bus bus;
    private final Cpu cpu;
    private long cycles;
    private final BootRomController bootRomController;
    private final Timer timer;
    private final LcdController lcdController;

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
        requireNonNull(cartridge);
        ram = new Ram(AddressMap.WORK_RAM_SIZE);
        ramController = new RamController(ram, AddressMap.WORK_RAM_START);
        echoRamController = new RamController(ram, AddressMap.ECHO_RAM_START,
                AddressMap.ECHO_RAM_END);
        cpu = new Cpu();
        bus = new Bus();
        bootRomController = new BootRomController(cartridge);
        timer = new Timer(cpu);
        lcdController=new LcdController(cpu);

        bus.attach(ramController);
        bus.attach(echoRamController);
        cpu.attachTo(bus);
        bus.attach(bootRomController);
        bus.attach(timer);
        lcdController.attachTo(bus);
    }

    /**
     * retourne le processeur du Game Boy
     * 
     * @return le processeur
     */
    public Cpu cpu() {
        return cpu;
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
        checkArgument(cycles <= cycle);
        while (cycles < cycle) {
            timer.cycle(cycles);
            lcdController.cycle(cycles);
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
        return timer;
    }
    
    
    /**
     * retourne le controlleur LCD du gameboy
     * 
     * @return le controlleur LCD du gameboy
     */
    public LcdController lcdController() {
        return lcdController;
    }

}
