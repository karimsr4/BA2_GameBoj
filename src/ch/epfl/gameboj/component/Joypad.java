package ch.epfl.gameboj.component;

import static ch.epfl.gameboj.Preconditions.checkBits16;
import static ch.epfl.gameboj.Preconditions.checkBits8;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.bits.Bit;
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
    
    private final static int LINE_0_BIT=4;
    private final static int LINE_1_BIT=5;

    private final Cpu cpu;
    private int[] pressedKeysMatrix = new int[2];
    private boolean firstLineIsActive;
    private boolean secondLineIsActive;
    
    

    /**
     * Enumération représentant les touches
     * 
     * @author Karim HADIDANE (271018)
     * @author Ahmed JELLOULI (274056)
     */
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

        Objects.requireNonNull(key);
        int line = key.ordinal() >>> 2;
        int column = Bits.clip(2, key.ordinal()) ;
        boolean b = Bits.test(computeP1(), column);
        pressedKeysMatrix[line] = Bits.set(pressedKeysMatrix[line], column,
                true);
        if (Bits.test(computeP1(), column) && !(b)) {
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

        Objects.requireNonNull(key);
        int line = key.ordinal() >>> 2;
        int column = Bits.clip(2, key.ordinal()) ;
        pressedKeysMatrix[line] = Bits.set(pressedKeysMatrix[line], column,
                false);
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
            return Bits.complement8(computeP1());

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
            int previousp1 = computeP1();
            firstLineIsActive = !Bits.test(data, LINE_0_BIT);
            secondLineIsActive =!Bits.test(data, LINE_1_BIT);
            if ( (Bits.complement8(previousp1)
                    & computeP1() ) > 0) {
                cpu.requestInterrupt(Interrupt.JOYPAD);
            }

        }
    }

    private int computeP1() {
        int result = 0;

        result = Bits.set(result, LINE_0_BIT, firstLineIsActive)
                | Bits.set(result,LINE_1_BIT, secondLineIsActive);
        if (firstLineIsActive) {
            result = result | pressedKeysMatrix[0];
        }
        if (secondLineIsActive) {
            result = result | pressedKeysMatrix[1];
        }
        return result;

    }

}
