package ch.epfl.gameboj;

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

    /**
     * Construit une nouvelle Gameboy en créant ses composants : une ram et deux
     * ram controllers et un bus et en attachant ces composants au bus
     * 
     * @param cartridge
     *            jeu a jouer
     */
    public GameBoy(Object cartridge) {
        ram = new Ram(AddressMap.WORK_RAM_SIZE);
        ramController = new RamController(ram, AddressMap.WORK_RAM_START,
                AddressMap.WORK_RAM_END);
        echoRamController = new RamController(ram, AddressMap.ECHO_RAM_START,
                AddressMap.ECHO_RAM_END);
        bus = new Bus();
        bus.attach(ramController);
        bus.attach(echoRamController);

    }

    /**
     * retourne le bus de la gameboy
     * 
     * @return bus de la gameboy reliant ses composants
     */
    public Bus bus() {
        return bus;

    }

}
