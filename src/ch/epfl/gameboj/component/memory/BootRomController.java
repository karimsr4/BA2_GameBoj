package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.Cartridge;

/**
 * Classe qui simule le contrôleur de la mémoire morte de démarrage
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class BootRomController implements Component {
    private Cartridge cartridge;
    private boolean enabled = true;

    /**
     * construit un controleur de la memoire morte de demarrage
     * @param cartridge la cartouche de jeu
     * @throws NullPointerException si cartridge est null
     */
    public BootRomController(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        this.cartridge = cartridge;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if ((enabled) && (address <= 0xFF)) {
            return BootRom.DATA[address];
        } else {
            return cartridge.read(address);
        }

    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        if(address==AddressMap.REG_BOOT_ROM_DISABLE)
            enabled=false;
        else
            cartridge.write(address, data);

    }

}
