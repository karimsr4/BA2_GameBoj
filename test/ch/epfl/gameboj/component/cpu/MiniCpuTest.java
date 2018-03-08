package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;


public class MiniCpuTest {
    private Bus connect(Cpu cpu, Ram ram) {
        RamController rc = new RamController(ram, 0);
        Bus b = new Bus();
        cpu.attachTo(b);
        rc.attachTo(b);
        return b;
    }

    private void cycleCpu(Cpu cpu, long cycles) {
        for (long c = 0; c < cycles; ++c)
            cpu.cycle(c);
    }

    @Test
    void nopDoesNothing() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        b.write(0, Opcode.NOP.encoding);
        cycleCpu(c, Opcode.NOP.cycles);
        assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
    }
    @Test 
    void LD_R8_HLR_WorksOnKnowValues(){
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        b.write(0, Opcode.LD_A_HLR.encoding);
        b.write(1, 0x11);
        cycleCpu(c, Opcode.LD_A_HLR.cycles);
        assertArrayEquals(new int[] { 1,0,126,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        
    }
    
    
    @Test 
    void LD_A_HLRU_WorksOnKnowValues(){
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        //b.write(1, 0x11);
        b.write(0, Opcode.LD_A_HLRI.encoding);
        cycleCpu(c, Opcode.LD_A_HLRI.cycles);
        assertArrayEquals(new int[] { 1,0,42,0,0,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());
        
    }
}
