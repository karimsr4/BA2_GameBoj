package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;

import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;


public class MiniCpuTest1 {
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
    public void testDAA() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        b.write(0, Opcode.LD_A_N8.encoding);
        b.write(1, 0b00110101);
        b.write(2, Opcode.ADD_A_N8.encoding);
        b.write(3, 0b00111000);
        b.write(4,Opcode.DAA.encoding);
        cycleCpu( c, Opcode.LD_A_N8.cycles+Opcode.ADD_A_N8.cycles+Opcode.DAA.cycles);
        assertArrayEquals(new int[] {5,0,0b01110011,0b0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
    }
        
   
        
    

 /*   @Test
    void nopDoesNothing() {
        Cpu c = new Cpu();
        Ram r = new Ramb10);
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
      /*  //b.write(1, 0x11);
        b.write(0, Opcode.LD_A_HLRI.encoding);
        cycleCpu(c, Opcode.LD_A_HLRI.cycles);
        assertArrayEquals(new int[] { 1,0,42,0,0,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());

        c.setReg16(Reg16.HL, 1);
        b.write(1, 1);
        b.write(0, Opcode.LD_B_HLR.encoding);
        b.write(1, 0xAF);
        cycleCpu(c, Opcode.LD_B_HLR.cycles);
        assertArrayEquals(new int[] { Opcode.LD_B_HLR.totalBytes,0,0,0,0xAF,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());

        
    }
    @Test
    void LD_A_HLRDworks() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        c.setReg16(Reg16.HL, 1);
        b.write(0, Opcode.LD_A_HLRD.encoding);
        b.write(1, 0xAF);
        cycleCpu(c, Opcode.LD_A_HLRD.cycles);
        assertArrayEquals(new int[] { Opcode.LD_A_HLRD.totalBytes,0,0xAF,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        
    }
    @Test
    void LD_A_HLRIworks() {
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        c.setReg16(Reg16.HL,1);
        b.write(0, Opcode.LD_A_HLRI.encoding);
        b.write(1, 0);
        cycleCpu(c, Opcode.LD_A_HLRI.cycles);
        assertArrayEquals(new int[] { Opcode.LD_A_HLRI.totalBytes,0,0,0,0,0,0,0,0,2}, c._testGetPcSpAFBCDEHL());
       
    }
    @Test
    void LD_A_N8Rworks() {
        Cpu c = new Cpu();
        Ram r = new Ram(0xFFFF);
        Bus b = connect(c, r);
        
        b.write(0, Opcode.LD_A_N8R.encoding);
        b.write(1, 10);
        b.write(10+AddressMap.REGS_START, 10);
        
        
        cycleCpu(c, Opcode.LD_A_N8R.cycles);
        assertArrayEquals(new int[] { Opcode.LD_A_N8R.totalBytes,0,10,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        
        
    }
    @Test
    void LD_A_CRworks() {
        Cpu c = new Cpu();
        Ram r = new Ram(0xFFFF);
        Bus b = connect(c, r);
        Opcode o= Opcode.LD_A_N16R;        
        b.write(0, o.encoding);
        b.write(1, 0xFF);
        b.write(0xFF, 10);
        cycleCpu(c, o.cycles);
        assertArrayEquals(new int[] { o.totalBytes,0,10,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        
        
    }

    
    
 */   
    
    
    @Test 
    void SCF_WorksOnKnowValues(){
        Cpu c = new Cpu();
        Ram r = new Ram(10);
        Bus b = connect(c, r);
        
        b.write(0, Opcode.ADD_A_N8.encoding);
        b.write(1, 0);
    

        b.write(2, Opcode.SCF.encoding);
        cycleCpu(c, 3);
        assertArrayEquals(new int[] { 3,0,0,144,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
        
    }
    
    
}
