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
    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.DIRECT);
    private static final Opcode[] PREFIXED_OPCODE_TABLE = buildOpcodeTable(
            Opcode.Kind.PREFIXED);
    private Bus bus;
    private RegisterFile<Reg> regs8bits = new RegisterFile<Reg>(Reg.values());
    private long nextNonIdleCycle;

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
        int encoding;
        Opcode opcode;
        if (cycle == nextNonIdleCycle) {
            encoding = read8(PC);
            opcode = DIRECT_OPCODE_TABLE[encoding];
            dispatch(opcode);
            nextNonIdleCycle += opcode.cycles;
            PC += opcode.totalBytes;
        }

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
    private void dispatch(Opcode opcode) {
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
            regs8bits.set(Reg.A, read16(reg16(Reg16.DE)));
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
                    regs8bits.get(extractReg(opcode, 0)));
            regs8bits.set(Reg.A, Alu.unpackValue(result));
            regs8bits.set(Reg.F, result);
        }
            break;
        case ADD_A_N8: {
            int result = Alu.add(regs8bits.get(Reg.A), read8AfterOpcode());
            regs8bits.set(Reg.A, Alu.unpackValue(result));
            regs8bits.set(Reg.F, result);
        }
            break;
        case ADD_A_HLR: {
            int result = Alu.add(regs8bits.get(Reg.A), read8AtHl());
            regs8bits.set(Reg.A, Alu.unpackValue(result));
            regs8bits.set(Reg.F, result);
        }
            break;
        case INC_R8: {
        }
            break;
        case INC_HLR: {
        }
            break;
        case INC_R16SP: {
        }
            break;
        case ADD_HL_R16SP: {
        }
            break;
        case LD_HLSP_S8: {
        }
            break;

        // Subtract
        case SUB_A_R8: {
        }
            break;
        case SUB_A_N8: {
        }
            break;
        case SUB_A_HLR: {
        }
            break;
        case DEC_R8: {
        }
            break;
        case DEC_HLR: {
        }
            break;
        case CP_A_R8: {
        }
            break;
        case CP_A_N8: {
        }
            break;
        case CP_A_HLR: {
        }
            break;
        case DEC_R16SP: {
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
            regs8bits.set(Reg.A, Alu.unpackValue(result));
            regs8bits.set(Reg.F, Alu.unpackFlags(result));
        }
            break;
        case XOR_A_N8: {
            int result = Alu.xor(regs8bits.get(Reg.A), read8AfterOpcode());
            regs8bits.set(Reg.A, Alu.unpackValue(result));
            regs8bits.set(Reg.F, Alu.unpackFlags(result));
        }
            break;
        case XOR_A_HLR: {
            int result = Alu.xor(regs8bits.get(Reg.A), read8AtHl());
            regs8bits.set(Reg.A, Alu.unpackValue(result));
            regs8bits.set(Reg.F, Alu.unpackFlags(result));
        }
            break;
        case CPL: {
         
        }
            break;

        // Rotate, shift
        case ROTCA: {
            // averifier
             int result=Alu.rotate(rotationDir(opcode), regs8bits.get(Reg.A));
             setRegFlags(Reg.A, result);
        }
            break;
        case ROTA: {
            int result=Alu.rotate(rotationDir(opcode), regs8bits.get(Reg.A), Bits.test(regs8bits.get(Reg.F), 4));
            setRegFlags(Reg.A, result);
            
        }
            break;
        case ROTC_R8: {
            Reg register = extractReg(opcode, 0);
            int result=Alu.rotate(rotationDir(opcode), regs8bits.get(register));
            setRegFlags(register, result);
        }
            break;
        case ROT_R8: {
            Reg register = extractReg(opcode, 0);
            int result=Alu.rotate(rotationDir(opcode), regs8bits.get(register), Bits.test(regs8bits.get(Reg.F), 4));
            setRegFlags(register, result);
        }
            break;
        case ROTC_HLR: {
            int result=Alu.rotate(rotationDir(opcode), read8AtHl());
            write8AtHlAndSetFlags(result);
        }
            break;
        case ROT_HLR: {
            int result=Alu.rotate(rotationDir(opcode), read8AtHl(),Bits.test(regs8bits.get(Reg.F), 4));
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
        }
            break;
        case BIT_U3_HLR: {
        }
            break;
        case CHG_U3_R8: {
        }
            break;
        case CHG_U3_HLR: {
        }
            break;

        // Misc. ALU
        case DAA: {
        }
            break;
        case SCCF: {
        }
            break;
        }

    }

    
      private static RotDir rotationDir(Opcode opcode) {
         return (Bits.test(opcode.encoding, 3)) ? RotDir.RIGHT   : RotDir.LEFT;
         
          }
      
      
      private boolean combineFanionCBit3(Opcode opcode) {
        return Bits.test(opcode.encoding, 3) && Bits.test(regs8bits.get(Reg.F), 4);
          
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
        write8(reg16(Reg16.HL), Alu.unpackValue(vf));
        setFlags(vf);
    }

    private enum FlagSrc implements Bit {
        V0, V1, ALU, CPU
        
    }
  /*  private void combineAluFlags(int vf, FlagSrc z, FlagSrc n, FlagSrc h, FlagSrc c) {
        FlagSrc[] tab  = new FlagSrc[] {z,n,h,c};
        for(int i=0;i<4;i++) {
            switch (tab[i]) {
                case V0 :
                    regs8bits.setBit(Reg.F, , false);
                case V1 :
                    regs8bits.setBit(Reg.F, , true);
                case ALU :
                    regs8bits.setBit(Reg.F, , true);
            }*/
            
            
     //   }
        
        
        
    }


