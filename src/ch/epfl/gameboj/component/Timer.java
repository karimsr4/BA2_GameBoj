package ch.epfl.gameboj.component;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;

public final class Timer implements Clocked, Component {

    private Cpu cpu;
    private int FIMA;
    private int TIMA;
    private int TMA;
    private int TAC;

    public Timer(Cpu cpu) {
        Objects.requireNonNull(cpu);
        this.cpu = cpu;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
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
            return cpu.read(address);
        }

    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
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

        }

    }

    @Override
    public void cycle(long cycle) {
        boolean previousState = state();
        FIMA = Bits.clip(16, FIMA + 4);
        incIfChange(previousState);

    }

    private boolean state() {
        return Bits.test(TAC, 3) && Bits.test(FIMA, bitIndex());

    }

    private void incIfChange(boolean previousState) {
        if ((previousState) && (!state())) {
            if (TIMA == 0xFF) {
                cpu.requestInterrupt(Interrupt.TIMER);
                TIMA = TMA;
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