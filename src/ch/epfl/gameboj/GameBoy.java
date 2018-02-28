package ch.epfl.gameboj;

import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

public class GameBoy {
    private Ram ram;
    private RamController ramController;
    private RamController echoRamController;
    private Bus bus;

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

    public Bus bus() {
        return bus;

    }

}
