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
    private int[] pressedKeysMatrix = new int[2];
    private boolean firstLineIsActive;
    private boolean secondLineIsActive;

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
        
        int ligne = key.ordinal() /4;
        int colonne = key.ordinal() % 4;
        boolean b = Bits.test(read(AddressMap.REG_P1), colonne);
        pressedKeysMatrix[ligne] = Bits.set(pressedKeysMatrix[ligne], colonne,
                true);
        if (Bits.test(read(AddressMap.REG_P1), 4 + ligne) && !(b)) {
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
        }

    

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        checkBits16(address);

        if (address == AddressMap.REG_P1) {
            int result = 0;

            result = Bits.set(result, 4, firstLineIsActive)
                    | Bits.set(result, 5, secondLineIsActive);
            if (firstLineIsActive) {
                result = result | pressedKeysMatrix[0];
            }
            if (secondLineIsActive) {
                result = result | pressedKeysMatrix[1];
            }
            return Bits.complement8(result);

        }

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
            int previousp1=read(AddressMap.REG_P1);
           firstLineIsActive=Bits.test(Bits.complement8(data) , 4);
           secondLineIsActive=Bits.test(Bits.complement8(data), 5);
           if(Bits.clip(4, (Bits.complement8(previousp1) & read(AddressMap.REG_P1)))>0) {
               cpu.requestInterrupt(Interrupt.JOYPAD);
           }

        } 
        
        

    }

    }


