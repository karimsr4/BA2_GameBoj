package ch.epfl.gameboj;

import java.util.Objects;

import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

/**
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public class GameBoy {
    private Ram ram;
    private RamController ramController;
    private RamController echoRamController;
    private Bus bus;
    private Cpu cpu;
    private long cycles;
    private BootRomController bootRomController;
    private Timer timer;

    /**
     * Construit une nouvelle Gameboy en créant ses composants : une ram et deux
     * ram controllers et un bus et en attachant ces composants au bus
     * 
     * @param cartridge
     *            jeu a jouer
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
        bus.attach(cpu);
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
     * @param cycle
     */
    public void runUntil(long cycle) {
        if (cycles > cycle) {
            throw new IllegalArgumentException();

        } else {
            while (cycles < cycle) {
                timer.cycle(cycle);
                cpu.cycle(cycles);
                cycles++;
            }
        }
    }

    /**
     * @return
     */
    public long cycles() {
        return cycles;
    }

    /**
     * @return
     */
    public Timer timer() {
        return this.timer;
    }

}
