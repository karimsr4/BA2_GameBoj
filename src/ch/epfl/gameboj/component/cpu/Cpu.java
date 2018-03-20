package ch.epfl.gameboj.component.cpu;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Alu.RotDir;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;

/**
 * classe simulant le processeur du GameBoy
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 *
 */
public final class Cpu implements Component, Clocked {

    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }

    private enum Reg16 implements Register {

        AF(Reg.A, Reg.F), BC(Reg.B, Reg.C), DE(Reg.D, Reg.E), HL(Reg.H, Reg.L);

        private Reg first;
        private Reg second;

        private Reg16(Reg first, Reg second) {
            this.first = first;
            this.second = second;
        }
    }

    private int PC;
    private int SP;
    private boolean IME;
    private int IE;
    private int IF;
    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.DIRECT);
    private static final Opcode[] PREFIXED_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.PREFIXED);
    private Bus bus;
    private static RegisterFile<Reg> regs8bits = new RegisterFile<Reg>(
            Reg.values());
    private long nextNonIdleCycle;

    public enum Interrupt implements Bit {
        VBLANK, LCD_STAT, TIMER, SERIAL, JOYPAD
    }

    /**
     * Constructeur publique du CPU
     * 
     */
    public Cpu() {
        for (Reg o : Reg.values()) {
            regs8bits.set(o, 0);
        }

        PC = 0;
        SP = 0;
        IME=false;
        IE=0;
        IF=0;

    }

    /**
     * methode utilisée pour faciliter les tests
     * 
     * @return un tableau representant les valeurs stockées dans les registres
     *         du CPU
     */
    public int[] _testGetPcSpAFBCDEHL() {

        return new int[] { PC, SP, regs8bits.get(Reg.A), regs8bits.get(Reg.F),
                regs8bits.get(Reg.B), regs8bits.get(Reg.C),
                regs8bits.get(Reg.D), regs8bits.get(Reg.E),
                regs8bits.get(Reg.H), regs8bits.get(Reg.L) };

    }

    @Override
    public void cycle(long cycle) {
        if (cycle == nextNonIdleCycle)
            reallyCycle();
        
        
        
   /*     int encoding;
        Opcode opcode;
        if (cycle == nextNonIdleCycle) {
            encoding = read8(PC);
            if (encoding == 0xCB) {
                encoding = read8AfterOpcode();
                opcode = PREFIXED_OPCODE_TABLE[encoding];
            } else {
                opcode = DIRECT_OPCODE_TABLE[encoding];
            }

            dispatch(opcode);
           
        }*/

    }

    private void reallyCycle() {
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

        this.bus = bus;
        bus.attach(this);
    }

    // ajouté a l'etape 5
    public void requestInterrupt(Interrupt i) {
        int bit = i.index();
        IF=Bits.set(IF, bit, true);
        

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

    // Acces au bus
    private int read8(int address) {

        return bus.read(address);

    }

    private int read8AtHl() {
        return read8(reg16(Reg16.HL));

    }

    private int read8AfterOpcode() {
        return read8(PC + 1);

    }

    private int read16(int address) {

        return Bits.make16(read8(address + 1), read8(address));

    }

    private int read16AfterOpcode() {
        return read16(PC + 1);

    }

    private void write8(int address, int v) {
        bus.write(address, v);

    }

    private void write16(int address, int v) {
        write8(address, Bits.clip(8, Preconditions.checkBits16(v)));
        write8(address + 1, Bits.extract(v, 8, 8));
    }

    private void write8AtHl(int v) {
        write8(reg16(Reg16.HL), v);

    }

    private void push16(int v) {
        SP = Bits.clip(16, SP - 2);
        write16(SP, Preconditions.checkBits16(v));

    }

    private int pop16() {
        int v = read16(SP);
        SP = Bits.clip(16, SP + 2);
        return v;
    }

    // gestion des paires de registres
    private int reg16(Reg16 r) {
        return Bits.make16(regs8bits.get(r.first), regs8bits.get(r.second));
    }

    private void setReg16(Reg16 r, int newV) {
        Preconditions.checkBits16(newV);
        switch (r) {
        case AF:
            regs8bits.set(Reg.A, Bits.extract(newV, 8, 8));
            regs8bits.set(Reg.F, Bits.clip(8, newV) & 0xF0);
            break;
        default:
            regs8bits.set(r.first, Bits.extract(newV, 8, 8));
            regs8bits.set(r.second, Bits.clip(8, newV));
        }

    }

    private void setReg16SP(Reg16 r, int newV) {
        switch (r) {
        case AF:
            SP = Preconditions.checkBits16(newV);
            break;
        default:
            setReg16(r, newV);

        }

    }

    // extraction de paramètres
    private Reg extractReg(Opcode opcode, int startBit) {

        int r = Bits.extract(opcode.encoding, Objects.checkIndex(startBit, 5),
                3);
        switch (r) {
        case 0b000:
            return Reg.B;
        case 0b001:
            return Reg.C;
        case 0b010:
            return Reg.D;
        case 0b011:
            return Reg.E;
        case 0b100:
            return Reg.H;
        case 0b101:
            return Reg.L;
        case 0b111:
            return Reg.A;
        default:
            throw new Error("impossible");
        }

    }

    private Reg16 extractReg16(Opcode opcode) {
        int r = Bits.extract(opcode.encoding, 4, 2);
        switch (r) {
        case 0b00:
            return Reg16.BC;
        case 0b01:
            return Reg16.DE;
        case 0b10:
            return Reg16.HL;
        default:
            return Reg16.AF;

        }

    }

    private int extractHlIncrement(Opcode opcode) {
        return (Bits.test(opcode.encoding, 4)) ? -1 : +1;

    }

    // dispatch method
    /**
     * @param opcode
     */
    private void dispatch(Opcode opcode) {
        System.out.println(opcode.family);
        int nextPC=PC+opcode.totalBytes;
        boolean needAdditionnalCycles=false;
        switch (opcode.family) {
        case NOP: {
        }
            break;
        case LD_R8_HLR: {
            regs8bits.set(extractReg(opcode, 3), read8AtHl());
        }
            break;
        case LD_A_HLRU: {
            regs8bits.set(Reg.A, read8AtHl());
            setReg16(Reg16.HL, Bits.clip(16,
                    reg16(Reg16.HL) + extractHlIncrement(opcode)));
        }
            break;
        case LD_A_N8R: {
            regs8bits.set(Reg.A,
                    read8(AddressMap.REGS_START + read8AfterOpcode()));
        }
            break;
        case LD_A_CR: {
            regs8bits.set(Reg.A,
                    read8(AddressMap.REGS_START + regs8bits.get(Reg.C)));
        }
            break;
        case LD_A_N16R: {
            regs8bits.set(Reg.A, read8(read16AfterOpcode()));
        }
            break;
        case LD_A_BCR: {
            regs8bits.set(Reg.A, read8(reg16(Reg16.BC)));

        }
            break;
        case LD_A_DER: {
            regs8bits.set(Reg.A, read8(reg16(Reg16.DE)));

        }
            break;
        case LD_R8_N8: {
            regs8bits.set(extractReg(opcode, 3), read8AfterOpcode());
        }
            break;
        case LD_R16SP_N16: {
            setReg16SP(extractReg16(opcode), read16AfterOpcode());
        }
            break;
        case POP_R16: {
            setReg16(extractReg16(opcode), pop16());
        }
            break;
        case LD_HLR_R8: {
            write8AtHl(regs8bits.get(extractReg(opcode, 0)));

        }
            break;
        case LD_HLRU_A: {
            write8AtHl(regs8bits.get(Reg.A));
            setReg16(Reg16.HL, Bits.clip(16,
                    reg16(Reg16.HL) + extractHlIncrement(opcode)));
        }
            break;
        case LD_N8R_A: {
            write8(AddressMap.REGS_START + read8AfterOpcode(),
                    regs8bits.get(Reg.A));
        }
            break;
        case LD_CR_A: {
            write8(AddressMap.REGS_START + regs8bits.get(Reg.C),
                    regs8bits.get(Reg.A));
        }
            break;
        case LD_N16R_A: {
            write8(read16AfterOpcode(), regs8bits.get(Reg.A));
        }
            break;
        case LD_BCR_A: {
            write8(reg16(Reg16.BC), regs8bits.get(Reg.A));
        }
            break;
        case LD_DER_A: {
            write8(reg16(Reg16.DE), regs8bits.get(Reg.A));
        }
            break;
        case LD_HLR_N8: {
            write8AtHl(read8AfterOpcode());
        }
            break;
        case LD_N16R_SP: {
            write16(read16AfterOpcode(), SP);
        }
            break;
        case LD_R8_R8: {

            regs8bits.set(extractReg(opcode, 3),
                    regs8bits.get(extractReg(opcode, 0)));
        }
            break;
        case LD_SP_HL: {

            setReg16SP(Reg16.AF, reg16(Reg16.HL));
        }
            break;
        case PUSH_R16: {
            push16(reg16(extractReg16(opcode)));
        }
            break;
        // Add
        case ADD_A_R8: {
            int result = Alu.add(regs8bits.get(Reg.A),
                    regs8bits.get(extractReg(opcode, 0)), withCarry(opcode));
            setRegFlags(Reg.A, result);
        }
            break;
        case ADD_A_N8: {
            int result = Alu.add(regs8bits.get(Reg.A), read8AfterOpcode(),
                    withCarry(opcode));
            setRegFlags(Reg.A, result);
        }
            break;
        case ADD_A_HLR: {
            int result = Alu.add(regs8bits.get(Reg.A), read8AtHl(),
                    withCarry(opcode));
            setRegFlags(Reg.A, result);
        }
            break;
        case INC_R8: {
            int result = Alu.add(regs8bits.get(extractReg(opcode, 3)), 1);
            setRegFromAlu(extractReg(opcode, 3), result);
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.CPU);
        }
            break;
        case INC_HLR: {
            int result = Alu.add(read8AtHl(), 1);
            write8AtHl(Alu.unpackValue(result));
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.CPU);
        }
            break;
        case INC_R16SP: {
            int result = Alu.add16H(extractReg16SPValue(opcode), 1);
            setReg16SP(extractReg16(opcode), Alu.unpackValue(result));
        }
            break;
        case ADD_HL_R16SP: {

            int result = Alu.add16H(reg16(Reg16.HL),
                    extractReg16SPValue(opcode));
            setReg16(Reg16.HL, Alu.unpackValue(result));
            combineAluFlags(result, FlagSrc.CPU, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.ALU);

        }
            break;
        case LD_HLSP_S8: {

            int result = Alu.add16L(SP,
                    Bits.clip(16, Bits.signExtend8(read8AfterOpcode())));
            if (Bits.test(opcode.encoding, 4)) {
                setReg16(Reg16.HL, Alu.unpackValue(result));
            } else {
                SP = Alu.unpackValue(result);
            }

            combineAluFlags(result, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.ALU);

        }
            break;

        // Subtract
        case SUB_A_R8: {
            int result = Alu.sub(regs8bits.get(Reg.A),
                    regs8bits.get(extractReg(opcode, 0)), withCarry(opcode));
            setRegFlags(Reg.A, result);
        }
            break;
        case SUB_A_N8: {
            int result = Alu.sub(regs8bits.get(Reg.A), read8AfterOpcode(),
                    withCarry(opcode));
            setRegFlags(Reg.A, result);
        }
            break;
        case SUB_A_HLR: {
            int result = Alu.sub(regs8bits.get(Reg.A), read8AtHl(),
                    withCarry(opcode));
            setRegFlags(Reg.A, result);
        }
            break;
        case DEC_R8: {
            Reg register = extractReg(opcode, 3);
            int result = Alu.sub(regs8bits.get(register), 1);
            setRegFromAlu(register, result);
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.V1, FlagSrc.ALU,
                    FlagSrc.CPU);

        }
            break;
        case DEC_HLR: {
            int result = Alu.sub(read8AtHl(), 1);
            write8AtHl(Alu.unpackValue(result));
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.V1, FlagSrc.ALU,
                    FlagSrc.CPU);
        }
            break;
        case CP_A_R8: {
            int result = Alu.sub(regs8bits.get(Reg.A),
                    regs8bits.get(extractReg(opcode, 0)));
            setFlags(result);
        }
            break;
        case CP_A_N8: {

            int result = Alu.sub(regs8bits.get(Reg.A), read8AfterOpcode());
            setFlags(result);

        }
            break;
        case CP_A_HLR: {
            int result = Alu.sub(regs8bits.get(Reg.A), read8AtHl());
            setFlags(result);
        }
            break;
        case DEC_R16SP: {
            int result = Bits.clip(16, extractReg16SPValue(opcode) - 1);
            setReg16SP(extractReg16(opcode), result);
        }
            break;

        // And, or, xor, complement
        case AND_A_N8: {
            int result = Alu.and(regs8bits.get(Reg.A), read8AfterOpcode());
            setRegFlags(Reg.A, result);

        }
            break;
        case AND_A_R8: {
            int result = Alu.and(regs8bits.get(Reg.A),
                    regs8bits.get(extractReg(opcode, 0)));
            setRegFlags(Reg.A, result);
        }
            break;
        case AND_A_HLR: {
            int result = Alu.and(regs8bits.get(Reg.A), read8AtHl());
            setRegFlags(Reg.A, result);
        }
            break;
        case OR_A_R8: {
            int result = Alu.or(regs8bits.get(Reg.A),
                    regs8bits.get(extractReg(opcode, 0)));
            setRegFlags(Reg.A, result);
        }
            break;
        case OR_A_N8: {
            int result = Alu.or(regs8bits.get(Reg.A), read8AfterOpcode());
            setRegFlags(Reg.A, result);
        }
            break;
        case OR_A_HLR: {
            int result = Alu.or(regs8bits.get(Reg.A), read8AtHl());
            setRegFlags(Reg.A, result);
        }
            break;
        case XOR_A_R8: {
            int result = Alu.xor(regs8bits.get(Reg.A),
                    regs8bits.get(extractReg(opcode, 0)));
            setRegFlags(Reg.A, result);
        }
            break;
        case XOR_A_N8: {
            int result = Alu.xor(regs8bits.get(Reg.A), read8AfterOpcode());
            setRegFlags(Reg.A, result);
        }
            break;
        case XOR_A_HLR: {
            int result = Alu.xor(regs8bits.get(Reg.A), read8AtHl());
            setRegFlags(Reg.A, result);
        }
            break;
        case CPL: {
            int result = Bits.complement8(regs8bits.get(Reg.A));
            regs8bits.set(Reg.A, result);
            combineAluFlags(0, FlagSrc.CPU, FlagSrc.V1, FlagSrc.V1,
                    FlagSrc.CPU);
        }
            break;

        // Rotate, shift
        case ROTCA: {

            int result = Alu.rotate(rotationDir(opcode), regs8bits.get(Reg.A));
            setRegFromAlu(Reg.A, result);
            combineAluFlags(result, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.ALU);

        }
            break;
        case ROTA: {
            int result = Alu.rotate(rotationDir(opcode), regs8bits.get(Reg.A),
                    Bits.test(regs8bits.get(Reg.F), 4));
            setRegFromAlu(Reg.A, result);
            combineAluFlags(result, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU,
                    FlagSrc.ALU);

        }
            break;
        case ROTC_R8: {
            Reg register = extractReg(opcode, 0);
            int result = Alu.rotate(rotationDir(opcode),
                    regs8bits.get(register));
            setRegFlags(register, result);
        }
            break;
        case ROT_R8: {
            Reg register = extractReg(opcode, 0);
            int result = Alu.rotate(rotationDir(opcode),
                    regs8bits.get(register),
                    Bits.test(regs8bits.get(Reg.F), 4));
            setRegFlags(register, result);
        }
            break;
        case ROTC_HLR: {
            int result = Alu.rotate(rotationDir(opcode), read8AtHl());
            write8AtHlAndSetFlags(result);
        }
            break;
        case ROT_HLR: {
            int result = Alu.rotate(rotationDir(opcode), read8AtHl(),
                    Bits.test(regs8bits.get(Reg.F), 4));
            write8AtHlAndSetFlags(result);
        }
            break;
        case SWAP_R8: {
            Reg register = extractReg(opcode, 0);
            int result = Alu.swap(regs8bits.get(register));
            setRegFlags(register, result);
        }
            break;
        case SWAP_HLR: {
            int result = Alu.swap(read8AtHl());
            write8AtHlAndSetFlags(result);
        }
            break;
        case SLA_R8: {
            Reg register = extractReg(opcode, 0);
            int result = Alu.shiftLeft(regs8bits.get(register));
            setRegFlags(register, result);
        }
            break;
        case SRA_R8: {
            Reg register = extractReg(opcode, 0);
            int result = Alu.shiftRightA(regs8bits.get(register));
            setRegFlags(register, result);
        }
            break;
        case SRL_R8: {
            Reg register = extractReg(opcode, 0);
            int result = Alu.shiftRightL(regs8bits.get(register));
            setRegFlags(register, result);
        }
            break;
        case SLA_HLR: {
            int result = Alu.shiftLeft(read8AtHl());
            write8AtHlAndSetFlags(result);
        }
            break;
        case SRA_HLR: {
            int result = Alu.shiftRightA(read8AtHl());
            write8AtHlAndSetFlags(result);
        }
            break;
        case SRL_HLR: {
            int result = Alu.shiftRightL(read8AtHl());
            write8AtHlAndSetFlags(result);
        }
            break;

        // Bit test and set
        case BIT_U3_R8: {
            int result = Alu.testBit(regs8bits.get(extractReg(opcode, 0)),
                    bitIndex(opcode));
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.V0, FlagSrc.V1,
                    FlagSrc.CPU);
        }
            break;
        case BIT_U3_HLR: {
            int result = Alu.testBit(read8AtHl(), bitIndex(opcode));
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.V0, FlagSrc.V1,
                    FlagSrc.CPU);

        }
            break;
        case CHG_U3_R8: {
            Reg reg = extractReg(opcode, 0);
            System.out.println(regs8bits.get(reg));
            regs8bits.set(reg, Bits.set(regs8bits.get(reg), bitIndex(opcode),
                    bitValue(opcode)));

        }
            break;
        case CHG_U3_HLR: {
            write8AtHl(
                    Bits.set(read8AtHl(), bitIndex(opcode), bitValue(opcode)));
        }
            break;

        // Misc. ALU
        case DAA: {
            int Fvalue = regs8bits.get(Reg.F);
            int result = Alu.bcdAdjust(regs8bits.get(Reg.A),
                    Bits.test(Fvalue, 6), Bits.test(Fvalue, 5),
                    Bits.test(Fvalue, 4));
            setRegFromAlu(Reg.A, result);
            combineAluFlags(result, FlagSrc.ALU, FlagSrc.CPU, FlagSrc.V0,
                    FlagSrc.ALU);

        }
            break;
        case SCCF: {
            int result = Bits.set(regs8bits.get(Reg.F), 4,
                    !(withCarry(opcode)));
            combineAluFlags(result, FlagSrc.CPU, FlagSrc.V0, FlagSrc.V0,
                    FlagSrc.ALU);
            break;
        }
        // Jumps
        case JP_HL: {
            PC = reg16(Reg16.HL);
        }
            break;
        case JP_N16: {
            PC = read16AfterOpcode();
        }
            break;
        case JP_CC_N16: {
            if (testCondition(opcode)) {
                PC = read16AfterOpcode();
                needAdditionnalCycles=true;
            }
               
        }
            break;
        case JR_E8: {
            PC = nextPC+ Bits.signExtend8(read8AfterOpcode());
        }
            break;
        case JR_CC_E8: {
            if (testCondition(opcode)) {
                PC = nextPC + Bits.signExtend8(read8AfterOpcode());
                needAdditionnalCycles=true;
            }
                
        }
            break;

        // Calls and returns
        case CALL_N16: {
            push16(nextPC);
            PC=read16AfterOpcode();
        }
            break;
        case CALL_CC_N16: {
            if (testCondition(opcode)) {
                needAdditionnalCycles=true;
                push16(nextPC);
                PC=read16AfterOpcode();
            }
               
                
        }
            break;
        case RST_U3: {
            push16(nextPC);
            PC=8 * bitIndex(opcode);
        }
            break;
        case RET: {
            PC=pop16();
        }
            break;
        case RET_CC: {
            if (testCondition(opcode)) {
                needAdditionnalCycles=true;
                PC=pop16();
            }
                
                
        }
            break;

        // Interrupts
        case EDI: {
            IME=Bits.test(opcode.encoding, 3);
        }
            break;
        case RETI: {
            IME=true;
            PC=pop16();
            
        }
            break;

        // Misc control
        case HALT: {
            nextNonIdleCycle=Long.MAX_VALUE;
            
        }
            break;
        case STOP:
            throw new Error("STOP is not implemented");

        }
        
        nextNonIdleCycle += opcode.cycles;
        if (needAdditionnalCycles) {
            nextNonIdleCycle+=opcode.additionalCycles;
        }
           
        PC += opcode.totalBytes;
        
    }

    private static RotDir rotationDir(Opcode opcode) {
        return (Bits.test(opcode.encoding, 3)) ? RotDir.RIGHT : RotDir.LEFT;

    }

    private void setRegFromAlu(Reg r, int vf) {
        regs8bits.set(r, Alu.unpackValue(vf));
    }

    private void setFlags(int valueFlags) {
        regs8bits.set(Reg.F, Alu.unpackFlags(valueFlags));
    }

    private void setRegFlags(Reg r, int vf) {
        setRegFromAlu(r, vf);
        setFlags(vf);
    }

    private void write8AtHlAndSetFlags(int vf) {
        write8AtHl(Alu.unpackValue(vf));
        setFlags(vf);
    }

    private enum FlagSrc implements Bit {
        V0, V1, ALU, CPU

    }

    private static void combineAluFlags(int vf, FlagSrc z, FlagSrc n, FlagSrc h,
            FlagSrc c) {
        int V1_mask = getFlagSrcMask(FlagSrc.V1, z, n, h, c);
        int ALU_mask = getFlagSrcMask(FlagSrc.ALU, z, n, h, c);
        int CPU_mask = getFlagSrcMask(FlagSrc.CPU, z, n, h, c);

        int result = (V1_mask) | (ALU_mask & vf)
                | (CPU_mask & regs8bits.get(Reg.F));
        System.out.println(result);

        regs8bits.set(Reg.F, result);

    }

    private static int getFlagSrcMask(FlagSrc Test, FlagSrc z, FlagSrc n,
            FlagSrc h, FlagSrc c) {

        return Alu.maskZNHC(z == Test, n == Test, h == Test, c == Test);
    }

    private int bitIndex(Opcode o) {

        return Bits.extract(o.encoding, 3, 3);
    }

    private boolean bitValue(Opcode o) {
        return Bits.test(o.encoding, 6);
    }

    private boolean withCarry(Opcode o) {

        return Bits.test(o.encoding, 3) && Bits.test(regs8bits.get(Reg.F), 4);
    }

    private int extractReg16SPValue(Opcode opcode) {
        int r = Bits.extract(opcode.encoding, 4, 2);
        switch (r) {
        case 0b00:
            return reg16(Reg16.BC);
        case 0b01:
            return reg16(Reg16.DE);
        case 0b10:
            return reg16(Reg16.HL);
        default:
            return SP;

        }

    }

    private boolean testCondition(Opcode o) {
        int condition = Bits.extract(o.encoding, 3, 2);
        switch (condition) {
        case 0b00:
            return Bits.test(regs8bits.get(Reg.F), 7) == false;
        case 0b01:
            return Bits.test(regs8bits.get(Reg.F), 7) == true;
        case 0b10:
            return Bits.test(regs8bits.get(Reg.F), 4) == false;
        default:
            return Bits.test(regs8bits.get(Reg.F), 4) == true;

        }
    }

}
