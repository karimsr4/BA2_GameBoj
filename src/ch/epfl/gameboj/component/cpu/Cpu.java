package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;

public class Cpu implements Component, Clocked {

    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }

    private int PC;
    private int SP;
    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.DIRECT);
    private Bus bus;

    private enum Reg16 implements Register {
        
        AF(Reg.A,Reg.F),
        BC(Reg.B,Reg.C),
        DE(Reg.D,Reg.E),
        HL(Reg.H,Reg.L);
        
        
        private Reg first;
        private Reg second;
        private Reg16(Reg first, Reg second) {
            this.first=first;
            this.second=second;
        }
    }

    private RegisterFile<Reg> regs8bits = new RegisterFile<Reg>(Reg.values());

    public Cpu() {
        regs8bits.set(Reg.A, 0);
        regs8bits.set(Reg.F, 0);
        regs8bits.set(Reg.B, 0);
        regs8bits.set(Reg.C, 0);
        regs8bits.set(Reg.D, 0);
        regs8bits.set(Reg.E, 0);
        regs8bits.set(Reg.H, 0);
        regs8bits.set(Reg.L, 0);
        PC = 0;
        SP = 0;

    }

    private static Opcode[] buildOpcodeTable(Kind kind) {
        Opcode[] opcodes = new Opcode[256];
        for (Opcode o : Opcode.values()) {
            if (o.kind == kind) {
                opcodes[o.encoding] = o;
            }
        }

        return opcodes;
    }

    public int[] _testGetPcSpAFBCDEHL() {

        return new int[] { PC, SP, regs8bits.get(Reg.A), regs8bits.get(Reg.F),
                regs8bits.get(Reg.B), regs8bits.get(Reg.C),
                regs8bits.get(Reg.D), regs8bits.get(Reg.E),
                regs8bits.get(Reg.H), regs8bits.get(Reg.L) };

    }

    @Override
    public void cycle(long cycle) {
        // TODO Auto-generated method stub

    }

    @Override
    public int read(int address) {
        return NO_DATA;
    }

    @Override
    public void write(int address, int data) {

    }
    @Override
    public void attachTo(Bus bus) {
        this.bus=bus;
        bus.attach(this);
    }
    
    
    private int read8(int address) {
        
    }
    
    
    private int read8AtHl() {
        
    }
    
    
    private int read8AfterOpcode() {
        
    }

    
    private int read16(int address) {
        
    }
    
    
    private int read16AfterOpcode() {
        
    }
    
    
    private void write8(int address, int v) {
        
    }
    
    
    private void write16(int address, int v) {
        
    }
    
    
    private void write8AtHl(int v) {
        
    }
    
    
    private void push16(int v) {
        
    }
}
