package ch.epfl.gameboj.component;

import static java.util.Objects.*;

import ch.epfl.gameboj.AddressMap;
import static ch.epfl.gameboj.Preconditions.*;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;

/**
 * Classe qui simule le minuteur
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Timer implements Clocked, Component {

    private Cpu cpu;
    private int FIMA;
    private int TIMA;
    private int TMA;
    private int TAC;

    /**
     * construit un timer pour le cpu donné
     * 
     * @param cpu
     *            processeur
     * @throws NullPointerException
     *             si le cpu est null
     */
    public Timer(Cpu cpu) {
        requireNonNull(cpu);
        this.cpu = cpu;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        checkBits16(address);
        switch (address) {
        case AddressMap.REG_DIV:
            return Bits.extract(FIMA, 8, 8);
        case AddressMap.REG_TIMA:
            return TIMA;
        case AddressMap.REG_TMA:
            return TMA;
        case AddressMap.REG_TAC:
            return TAC;
        default:
            return NO_DATA;
        }

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
        boolean previousState = state();
        switch (address) {
        case AddressMap.REG_DIV: {
            FIMA = 0;
            incIfChange(previousState);
        }
            break;

        case AddressMap.REG_TIMA: {
            TIMA = data;
        }
            break;

        case AddressMap.REG_TMA: {
            TMA = data;
        }
            break;
        case AddressMap.REG_TAC: {
            TAC = data;
            incIfChange(previousState);
        }
            break;

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Clocked#cycle(long)
     */
    @Override
    public void cycle(long cycle) {
        boolean previousState = state();
        FIMA = Bits.clip(16, FIMA + 4);
        incIfChange(previousState);

    }

    private boolean state() {
        return Bits.test(TAC, 2) && Bits.test(FIMA, bitIndex());

    }

    private void incIfChange(boolean previousState) {
        if ((previousState) && (!state())) {
            if (TIMA == 0xFF) {
                cpu.requestInterrupt(Interrupt.TIMER);
                TIMA = TMA;
            } else {
                TIMA++;
            }

        }

    }

    private int bitIndex() {
        switch (Bits.clip(2, TAC)) {
        case 0b00:
            return 9;
        case 0b01:
            return 3;
        case 0b10:
            return 5;
        case 0b11:
            return 7;
        default:
            return 0;
        }
    }

}
