package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.RandomGenerator;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MyCpuTestStep5_7303 {

    private long currentCpuCycle = 0;
    private Cpu cpu;
    private Bus bus;

    //No need to do -1 since Ram will go from index 0 to \/ (the address stated below) -1
    private static final int TEST_RAM_SIZE = AddressMap.HIGH_RAM_START;

    private void resetComponents() {
        cpu = new Cpu();
        Ram ram = new Ram(TEST_RAM_SIZE);
        bus = new Bus();
        currentCpuCycle = 0;

        RamController rc = new RamController(ram, 0);
        cpu.attachTo(bus);
        rc.attachTo(bus);
    }

    private void extraCycleCpu(long extraCycles) {
        long finalCycles = extraCycles + currentCpuCycle;
        for (long c = currentCpuCycle; c < finalCycles; ++c)
            cpu.cycle(c);

        currentCpuCycle = finalCycles;
    }

    private int sumCycles(Opcode ...opcodes) {
        int sumCycles = 0;
        for (Opcode opcode: opcodes) {
            sumCycles += opcode.cycles;
        }
        return sumCycles;
    }

    private int sumAdditionalCycles(Opcode ...opcodes) {
        int sumAdditionalCycles = 0;
        for (Opcode opcode: opcodes) {
            sumAdditionalCycles += opcode.additionalCycles;
        }
        return sumAdditionalCycles;
    }

    private int sumTotalBytes(Opcode ...opcodes) {
        int sumTotalBytes = 0;
        for (Opcode opcode: opcodes) {
            sumTotalBytes += opcode.totalBytes;
        }
        return sumTotalBytes;
    }

    private Opcode[] getWholeFamily(Opcode.Family family) {
        ArrayList<Opcode> opcodes = new ArrayList<>();

        for (Opcode opcode : Opcode.values()) {
            if (opcode.family == family) {
                opcodes.add(opcode);
            }
        }

        return opcodes.toArray(new Opcode[]{});
    }

    private int calculateExpectedSP(int expectedStackSize) {
        return Bits.clip(16, 0xFFFF - 2*expectedStackSize);
    }

    private void testCpuRegisters(int PC, int SP, int A, int F, int B, int C, int D, int E, int H, int L) {
        assertArrayEquals(new int[] {PC, SP, A, F, B, C, D, E, H, L}, cpu._testGetPcSpAFBCDEHL());
    }

    @BeforeEach
    void setUp() {
        resetComponents();
    }

    @Test
    void practiceTestLD_R8_N8() {
        Opcode test = Opcode.LD_A_N8;

        bus.write(0, test.encoding);
        bus.write(1, 0x34);

        extraCycleCpu(sumCycles(test));

        assertArrayEquals(new int[] {sumTotalBytes(test), 0, 0x34, 0, 0, 0, 0, 0, 0, 0}, cpu._testGetPcSpAFBCDEHL());
    }

    @Test
    void practiceTestLD_R8_N8v2() {
        Opcode[] opcodes = getWholeFamily(Opcode.Family.LD_R8_N8);

        final Opcode[] TO_ARRAY_PARAMETER = new Opcode[]{};

        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {

            resetComponents();
            int a = 0, b = 0, c = 0, d = 0, e = 0, h = 0, l = 0;
            int currentAddress = 0;

            ArrayList<Opcode> excecutedOpcodeArrayList = new ArrayList<>();

            for (int j=0; j < RandomGenerator.randomInt(7, 1); j++) {

                Opcode opcode = opcodes[RandomGenerator.randomInt(6, 0)];

                bus.write(currentAddress++, opcode.encoding);

                int assignedByte = RandomGenerator.randomBit(8);
                bus.write(currentAddress++, assignedByte);

                switch (Bits.extract(opcode.encoding, 3, 3)) {
                    case 0b000 :
                        b = assignedByte;
                        break;
                    case 0b001 :
                        c = assignedByte;
                        break;
                    case 0b010 :
                        d = assignedByte;
                        break;
                    case 0b011 :
                        e = assignedByte;
                        break;
                    case 0b100 :
                        h = assignedByte;
                        break;
                    case 0b101 :
                        l = assignedByte;
                        break;
                    case 0b111 :
                        a = assignedByte;
                        break;
                    default :
                        throw new IllegalArgumentException();
                }

                excecutedOpcodeArrayList.add(opcode);
            }

            Opcode[] executedOpcodesArray = excecutedOpcodeArrayList.toArray(TO_ARRAY_PARAMETER);
            extraCycleCpu(sumCycles(executedOpcodesArray));
            assertArrayEquals(new int[] {sumTotalBytes(executedOpcodesArray), 0, a, 0, b, c, d, e, h, l}, cpu._testGetPcSpAFBCDEHL());
//            System.out.println(Arrays.toString(cpu._testGetPcSpAFBCDEHL()));
        }
    }

    @Test
    void testJP_HL() {
        bus.write(0, Opcode.LD_HL_N16.encoding);

        int hlAddressMSBs = 0x11;
        int hlAddressLSBs = 0x22;

        bus.write(1, hlAddressLSBs);
        bus.write(2, hlAddressMSBs);

        extraCycleCpu(sumCycles(Opcode.LD_HL_N16));
        testCpuRegisters(sumTotalBytes(Opcode.LD_HL_N16), 0, 0, 0,0,0,0,0, hlAddressMSBs, hlAddressLSBs);

        bus.write(3, Opcode.JP_HL.encoding);
        extraCycleCpu(sumCycles(Opcode.JP_HL));
        testCpuRegisters(Bits.make16(hlAddressMSBs, hlAddressLSBs), 0, 0, 0,0,0,0,0, hlAddressMSBs, hlAddressLSBs);
    }

    @Test
    void testJP_N16() {
        Opcode testedOP = Opcode.JP_N16;
        bus.write(0, testedOP.encoding);

        int addressLSBs = 0x24;
        int addressMSBs = 0x55;

        bus.write(1, addressLSBs);
        bus.write(2, addressMSBs);

        extraCycleCpu(sumCycles(testedOP));
        testCpuRegisters(Bits.make16(addressMSBs, addressLSBs), 0, 0, 0,0,0,0,0, 0, 0);
    }

    @Test
    void testJP_NZ_N16() {
        Opcode testedOP = Opcode.JP_NZ_N16;

        int flagZTrue = Alu.maskZNHC(false, false, false, false);

        cpu.writeRegF(flagZTrue);

        bus.write(0, testedOP.encoding);

        int addressLSBs = 0x25;
        int addressMSBs = 0x66;
        int address = Bits.make16(addressMSBs, addressLSBs);

        bus.write(1, addressLSBs);
        bus.write(2, addressMSBs);

        extraCycleCpu(sumCycles(testedOP) + sumAdditionalCycles(testedOP));
        testCpuRegisters(address, 0, 0, flagZTrue,0,0,0,0, 0, 0);

        int flagZfalse = Alu.maskZNHC(true, false, false, false);

        cpu.writeRegF(flagZfalse);
        bus.write(address, testedOP.encoding);

        int wrongAddressLSBs = 0x08;
        int wrongAddressMSBs = 0x06;
//        int wrongAddress = Bits.make16(wrongAddressMSBs, wrongAddressLSBs);

        bus.write(address + 1, wrongAddressLSBs);
        bus.write(address + 2, wrongAddressMSBs);

        extraCycleCpu(sumCycles(testedOP));
        testCpuRegisters(address + sumTotalBytes(testedOP), 0, 0, flagZfalse,0,0,0,0, 0, 0);
    }

    @Test
    void testJP_CC_N16() {
        Opcode[] opcodes = new Opcode[]{Opcode.JP_NZ_N16, Opcode.JP_Z_N16, Opcode.JP_NC_N16, Opcode.JP_C_N16};

        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            for (Opcode testedOp : opcodes) {
                resetComponents();

                int targetAddress = RandomGenerator.randomInt(AddressMap.HIGH_RAM_END, 0);
                int targetAddressLSBs = Bits.clip(8, targetAddress);
                int targetAddressMSBs = Bits.extract(targetAddress, 8, 8);

                boolean shouldBeExecuted = RandomGenerator.randomBoolean();
                boolean concernedFlagIsZ = testedOp == Opcode.CALL_NZ_N16 || testedOp == Opcode.JP_Z_N16;
                boolean concernedFlagInverted = testedOp == Opcode.CALL_NZ_N16 || testedOp == Opcode.JP_NC_N16;

                int flagZorC = Alu.maskZNHC(
                        concernedFlagIsZ && shouldBeExecuted ^ concernedFlagInverted,
                        false,
                        false,
                        !(concernedFlagIsZ) && shouldBeExecuted ^ concernedFlagInverted
                );

                cpu.writeRegF(flagZorC);

                bus.write(0, testedOp.encoding);
                bus.write(1, targetAddressLSBs);
                bus.write(2, targetAddressMSBs);

//                System.out.println(testedOP.name());
//                System.out.println(shouldBeExecuted);
//                System.out.println(concernedFlagIsZ +"\t"+ concernedFlagInverted);
//                System.out.println("Z: " + (concernedFlagIsZ && shouldBeExecuted ^ concernedFlagInverted));
//                System.out.println("C: " + (!(concernedFlagIsZ) && shouldBeExecuted ^ concernedFlagInverted));
//                System.out.println();

                extraCycleCpu(sumCycles(testedOp) + (shouldBeExecuted ? sumAdditionalCycles(testedOp) : 0));
                testCpuRegisters(shouldBeExecuted ? targetAddress : sumTotalBytes(testedOp), 0, 0, flagZorC, 0, 0, 0, 0, 0, 0);
            }
        }
    }

    @Test
    void testJR_E8() {
        for (int i=0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            resetComponents();
            int startAddress = RandomGenerator.randomInt(AddressMap.HIGH_RAM_END - 1, 0);
            extraCycleCpu(startAddress);

            Opcode testedOP = Opcode.JR_E8;
            bus.write(startAddress, testedOP.encoding);

            int relativeJumpDist = RandomGenerator.randomInt(0xFF, 0);
            bus.write(startAddress + 1, relativeJumpDist);

            extraCycleCpu(sumCycles(testedOP));

            int expectedPC = Bits.clip(16, Bits.signExtend8(relativeJumpDist) + startAddress + sumTotalBytes(testedOP));
            testCpuRegisters(expectedPC, 0, 0, 0,0,0,0,0, 0, 0);
        }
    }

    @Test
    void testJR_CC_E8() {
        Opcode[] opcodes = new Opcode[]{Opcode.JR_NZ_E8, Opcode.JR_Z_E8, Opcode.JR_NC_E8, Opcode.JR_C_E8};

        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            for (Opcode testedOP : opcodes) {
                resetComponents();

                boolean shouldBeExecuted = RandomGenerator.randomBoolean();
                boolean concernedFlagIsZ = testedOP == Opcode.JR_NZ_E8 || testedOP == Opcode.JR_Z_E8;
                boolean concernedFlagInverted = testedOP == Opcode.JR_NZ_E8 || testedOP == Opcode.JR_NC_E8;

                int flagZorC = Alu.maskZNHC(
                        concernedFlagIsZ && shouldBeExecuted ^ concernedFlagInverted,
                        false,
                        false,
                        !(concernedFlagIsZ) && shouldBeExecuted ^ concernedFlagInverted
                );

                cpu.writeRegF(flagZorC);

                int startAddress = RandomGenerator.randomInt(AddressMap.HIGH_RAM_END - 1, 0);
                extraCycleCpu(startAddress);

                bus.write(startAddress, testedOP.encoding);

                int relativeJumpDist = RandomGenerator.randomInt(0xFF, 0);
                bus.write(startAddress + 1, relativeJumpDist);

                extraCycleCpu(sumCycles(testedOP) + (shouldBeExecuted ? sumAdditionalCycles(testedOP) : 0));

                int executedExpectedPC = Bits.clip(16, Bits.signExtend8(relativeJumpDist) + startAddress + sumTotalBytes(testedOP));
                int notExecutedExpectedPC = startAddress + sumCycles(testedOP);
                testCpuRegisters(shouldBeExecuted ? executedExpectedPC : notExecutedExpectedPC, 0, 0, flagZorC,0,0,0,0, 0, 0);
            }
        }
    }

    @Test
    void testCALL_N16() {
        //ALWAYS FUCKING ADD THIS WHEN U WORK WITH STACK BITCH
        bus.write(0, Opcode.LD_SP_N16.encoding);
        bus.write(1, 0xFF);
        bus.write(2, 0xFF);

        extraCycleCpu(sumCycles(Opcode.LD_SP_N16));

        Opcode testedOP = Opcode.CALL_N16;
        bus.write(3, testedOP.encoding);

        int addressLSBs = 0x56;
        int addressMSBs = 0x78;

        bus.write(4, addressLSBs);
        bus.write(5, addressMSBs);

        extraCycleCpu(sumCycles(testedOP));
        testCpuRegisters(Bits.make16(addressMSBs, addressLSBs), calculateExpectedSP(1), 0, 0,0,0,0,0, 0, 0);
    }

    @Test
    void testCALL_CC_N16() {
        Opcode[] opcodes = new Opcode[]{Opcode.CALL_NZ_N16, Opcode.CALL_Z_N16, Opcode.CALL_NC_N16, Opcode.CALL_C_N16};

        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            for (Opcode testedOp : opcodes) {
                resetComponents();
                //ALWAYS FUCKING ADD THIS WHEN U WORK WITH STACK BITCH
                bus.write(0, Opcode.LD_SP_N16.encoding);
                bus.write(1, 0xFF);
                bus.write(2, 0xFF);

                extraCycleCpu(sumCycles(Opcode.LD_SP_N16));

                int targetAddress = RandomGenerator.randomInt(AddressMap.HIGH_RAM_END, 0);
                int targetAddressLSBs = Bits.clip(8, targetAddress);
                int targetAddressMSBs = Bits.extract(targetAddress, 8, 8);

                boolean shouldBeExecuted = RandomGenerator.randomBoolean();
                boolean concernedFlagIsZ = testedOp == Opcode.CALL_NZ_N16 || testedOp == Opcode.CALL_Z_N16;
                boolean concernedFlagInverted = testedOp == Opcode.CALL_NZ_N16 || testedOp == Opcode.CALL_NC_N16;

                int flagZorC = Alu.maskZNHC(
                        concernedFlagIsZ && shouldBeExecuted ^ concernedFlagInverted,
                        false,
                        false,
                        !(concernedFlagIsZ) && shouldBeExecuted ^ concernedFlagInverted
                );

                cpu.writeRegF(flagZorC);

                bus.write(3, testedOp.encoding);
                bus.write(4, targetAddressLSBs);
                bus.write(5, targetAddressMSBs);

                extraCycleCpu(sumCycles(testedOp) + (shouldBeExecuted ? sumAdditionalCycles(testedOp) : 0));
                testCpuRegisters(shouldBeExecuted ? targetAddress : sumTotalBytes(testedOp, Opcode.LD_SP_N16), calculateExpectedSP(1), 0, flagZorC, 0, 0, 0, 0, 0, 0);
            }
        }
    }

    @Test
    void testRST_U3() {
        Opcode[] rstFamily = getWholeFamily(Opcode.Family.RST_U3);
        for (Opcode testedOp: rstFamily) {
            resetComponents();

            //ALWAYS FUCKING ADD THIS WHEN U WORK WITH STACK BITCH
            bus.write(0, Opcode.LD_SP_N16.encoding);
            bus.write(1, 0xFF);
            bus.write(2, 0xFF);

            extraCycleCpu(sumCycles(Opcode.LD_SP_N16));
            testCpuRegisters(sumTotalBytes(Opcode.LD_SP_N16), calculateExpectedSP(0), 0,0,0,0,0,0,0,0);

            int targetAddress = 8 * Bits.extract(testedOp.encoding, 3, 3);

            bus.write(3, testedOp.encoding);

            extraCycleCpu(sumCycles(testedOp));
            testCpuRegisters(targetAddress, calculateExpectedSP(1), 0,0,0,0,0,0,0,0);
        }
    }

    @Test
    void testCALLthenRET() {
        //ALWAYS FUCKING ADD THIS WHEN U WORK WITH STACK BITCH
        bus.write(0, Opcode.LD_SP_N16.encoding);
        bus.write(1, 0xFF);
        bus.write(2, 0xFF);

        int startAddress = 0x2345;
        extraCycleCpu(startAddress);

        Opcode testedOP = Opcode.CALL_N16;
        bus.write(startAddress, testedOP.encoding);

        int addressLSBs = 0x56;
        int addressMSBs = 0x78;
        int address = Bits.make16(addressMSBs, addressLSBs);

        bus.write(startAddress + 1, addressLSBs);
        bus.write(startAddress + 2, addressMSBs);

        extraCycleCpu(sumCycles(testedOP));
        testCpuRegisters(address, calculateExpectedSP(1), 0, 0,0,0,0,0, 0, 0);

        int bottomOfStack = Bits.make16(bus.read(0xFFFE), bus.read(0xFFFD));
        int returnAddress = startAddress + 3;
        assertEquals(returnAddress, bottomOfStack);

        Opcode testedOP2 = Opcode.LD_A_N8;
        Opcode testedOP3 = Opcode.RET;
        Opcode[] ugh = {testedOP2, testedOP3};

        bus.write(address, testedOP2.encoding);
        bus.write(address + 1, 0x09);
        bus.write(address + 2, testedOP3.encoding);

        extraCycleCpu(sumCycles(ugh));
        testCpuRegisters(returnAddress, calculateExpectedSP(0), 0x09, 0, 0,0,0,0,0,0);
    }

    @Test
    void testRET_CC() {

    }

    @Test
    void testEDI() {

    }

    @Test
    void testRETI() {

    }

    @Test
    void testHALT() {

    }

    @Test
    void testStackUsage() {
        Opcode[] opcodes = {Opcode.LD_SP_N16, Opcode. LD_BC_N16, Opcode.LD_DE_N16, Opcode.PUSH_BC, Opcode.PUSH_DE, Opcode.POP_BC, Opcode.POP_DE};

        bus.write(0, opcodes[0].encoding);
        bus.write(1, 0xFF);
        bus.write(2, 0xFF);
        bus.write(3, opcodes[1].encoding);
        bus.write(4, 0x34);
        bus.write(5, 0x12);
        bus.write(6, opcodes[2].encoding);
        bus.write(7, 0x78);
        bus.write(8, 0x56);
        bus.write(9, opcodes[3].encoding);
        bus.write(10, opcodes[4].encoding);
        bus.write(11, opcodes[5].encoding);
        bus.write(12, opcodes[6].encoding);

        extraCycleCpu(sumCycles(opcodes));
        testCpuRegisters(sumTotalBytes(opcodes), calculateExpectedSP(0), 0,0,0x56,0x78,0x12,0x34,0,0);
    }
}
