package ch.epfl.gameboj.component;

import static ch.epfl.gameboj.Preconditions.*;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;

/**
 * classe qui représente le clavier du Gameboy
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class Joypad implements Component {

    private final Cpu cpu;
    private int regP1;
    private int line0_pressedKeys;
    private int line1_pressedKeys;
    private int[] pressedKeysMatrix = new int[] { line0_pressedKeys,
            line1_pressedKeys };

    public enum Key {
        RIGHT, LEFT, UP, DOWN, A, B, SELECT, START
    }

    /**
     * construit un clavier de Gameboy
     * 
     * @param cpu
     *            processeur du gameboy
     */
    public Joypad(Cpu cpu) {
        this.cpu = cpu;
    }

    /**
     * méthode qui simule la pression d'une touche de clavier
     * 
     * @param key
     *            la touche pressée
     */
    public void keyPressed(Key key) {
        int ligne = key.ordinal() / 4;
        int colonne = key.ordinal() % 4;
        if (Bits.test(regP1, 4 + ligne)) {
            pressedKeysMatrix[ligne] = Bits.set(pressedKeysMatrix[ligne],
                    colonne, true);
            regP1 = Bits.set(regP1, colonne, true);
            cpu.requestInterrupt(Interrupt.JOYPAD);
        }

    }

    /**
     * méthode qui simule le relâchement d'une touche de clavier
     * 
     * @param key
     *            la touche relâchée
     */
    public void keyReleased(Key key) {
        int ligne = key.ordinal() / 4;
        int colonne = key.ordinal() % 4;
        pressedKeysMatrix[ligne] = Bits.set(pressedKeysMatrix[ligne], colonne,
                false);
        if (!(Bits.test(regP1, (ligne + 1) % 2)
                && Bits.test(pressedKeysMatrix[(ligne + 1) % 2], colonne))) {
            regP1 = Bits.set(regP1, colonne, false);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        checkBits16(address);
        if (address == AddressMap.REG_P1)
            return Bits.complement8(regP1);
        return NO_DATA;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#write(int, int)
     */
    @Override
    public void write(int address, int data) {
        checkBits16(address);
        checkBits8(data);
        if (address == AddressMap.REG_P1) {
            regP1 = Bits.complement8(((data >>> 4) << 4) & Bits.clip(4, regP1));
        }

    }

}
