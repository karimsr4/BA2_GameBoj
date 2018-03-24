package ch.epfl.gameboj.component;

import java.util.Objects;

import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;

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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void write(int address, int data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cycle(long cycle) {
        // TODO Auto-generated method stub

    }

    private boolean state() {
        return Bits.test(TAC, 3) && Bits.test(FIMA, bitIndex());

    }

    private void incIfChange(boolean previousState) {
        if ((previousState) && (!state()))
            TIMA++;

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
