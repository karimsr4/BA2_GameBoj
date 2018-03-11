package ch.epfl.gameboj.component.cpu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

public class MiniCpuTest2 {
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
    void LD_R16SP_N16WorksB() {
        Cpu c = new Cpu();
        Ram r = new Ram(0xFFFF - 1 );
        Bus bus = connect(c, r);
        
     
        bus.write(0, Opcode.LD_SP_N16.encoding);
        bus.write(1, 42);
        bus.write(2, 0);
        bus.write(3, Opcode.LD_DE_N16.encoding);
        bus.write(4, 37);
        bus.write(5, 13);
        
        cycleCpu(c,6);
        
        assertArrayEquals(new int[] {6,42,0,0,0,0,13,37,0,0},c._testGetPcSpAFBCDEHL());
    }
    
    @Test
    void LD_R8_HLRWorksB() {
       Cpu c= new Cpu();
       Ram r= new Ram(0xFFFF-1);
       Bus bus= connect(c,r);

       
       bus.write(0, Opcode.LD_H_N8.encoding);
       bus.write(1, 0);
       bus.write(2,Opcode.LD_L_N8.encoding);
       bus.write(3, 20);
       bus.write(4, Opcode.LD_D_HLR.encoding);
       bus.write(20,2);
       cycleCpu(c,6);
    
       assertArrayEquals(new int[] {5,0,0,0,0,0,2,0,0,20},c._testGetPcSpAFBCDEHL());

   }
   
   @Test
   void LD_A_HLRUWorksB() {
      Cpu c= new Cpu();
      Ram r= new Ram(0xFFFF-1);
      Bus bus= connect(c,r);
      
      bus.write(0, Opcode.LD_H_N8.encoding);
      bus.write(1,0);
      bus.write(2,Opcode.LD_L_N8.encoding);
      bus.write(3,11);
      bus.write(4, Opcode.LD_D_HLR.encoding);
      bus.write(11, 3);
      cycleCpu(c,6);
      
      assertArrayEquals(new int [] {5,0,0,0,0,0,3,0,0,11},c._testGetPcSpAFBCDEHL());
  }
   
  
  
   
   @Test
   void LD_A_N8RWokrsB() {
       Cpu c= new Cpu();
       Ram r= new Ram(0xFFFF-1);
       Bus bus= connect(c,r);

       bus.write(11+0xFF00,42);
       bus.write(0, Opcode.LD_A_N8R.encoding);
       bus.write(1, 11);
       cycleCpu(c,2);
       
       assertArrayEquals(new int[] {2,0,42,0,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
       
   }
   
   @Test
   void LD_A_CRWorksB() {
       Cpu c= new Cpu();
       Ram r= new Ram(0xFFFF-1);
       Bus bus= connect(c,r);
       
       
       bus.write(11+0xFF00,42);
       bus.write(0, Opcode.LD_C_N8.encoding);
       bus.write(1, 11);
       bus.write(2, Opcode.LD_A_CR.encoding);
       cycleCpu(c,4);
       

       
       assertArrayEquals(new int[] {3,0,42,0,0,11,0,0,0,0},c._testGetPcSpAFBCDEHL());
   }
   

   @Test
   void LD_A_N16RWorksB() {
       Cpu c=new Cpu();
       Ram r= new Ram(0xFFFF-1);
       Bus bus=connect(c,r);
       
       bus.write(0b1111111111110111,33);
       bus.write(0, Opcode.LD_A_N16R.encoding);
       bus.write(1, 0b11110111);
       bus.write(2,0b11111111);
       cycleCpu(c,2);
       
       assertArrayEquals(new int[] {3,0,33,0,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
       
   }
   
   @Test
   void LD_A_BCRWorksB() {
       Cpu c= new Cpu();
       Ram r= new Ram(0xFFFF-1);
       Bus bus=connect(c,r);
        
       bus.write(0,Opcode.LD_B_N8.encoding);
       bus.write(1, 0b00000001);
       bus.write(2, Opcode.LD_C_N8.encoding);
       bus.write(3, 0b00000001);
       bus.write(4, Opcode.LD_A_BCR.encoding);
       bus.write(0b0000000100000001, 33);
       
       cycleCpu(c,6);
       
       assertArrayEquals(new int[] {5,0,33,0,0b1,0b1,0,0,0,0},c._testGetPcSpAFBCDEHL());
       
       
   }
   

   
   @Test
   void LD_A_DERWorksB() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF - 1 );
       Bus bus = connect(c, r);
       
       bus.write(0,Opcode.LD_D_N8.encoding);
       bus.write(1, 0b00000001);
       bus.write(2, Opcode.LD_E_N8.encoding);
       bus.write(3, 0b00000001);
       bus.write(4, Opcode.LD_A_DER.encoding);
       bus.write(0b0000000100000001, 33);
       
       cycleCpu(c,6);
       
       assertArrayEquals(new int[] {5,0,33,0,0,0,0b1,0b1,0,0},c._testGetPcSpAFBCDEHL());
       
   }
   
   @Test
   void LD_R8_N8WorksB() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF - 1 );
       Bus bus = connect(c, r);
       
       
       bus.write(0,Opcode.LD_A_N8.encoding);
       bus.write(1,1);
       bus.write(2,Opcode.LD_B_N8.encoding);
       bus.write(3,2);
       bus.write(4,Opcode.LD_C_N8.encoding);
       bus.write(5,3);
       bus.write(6,Opcode.LD_D_N8.encoding);
       bus.write(7,4);
       bus.write(8,Opcode.LD_E_N8.encoding);
       bus.write(9,5);
       bus.write(10,Opcode.LD_H_N8.encoding);
       bus.write(11,6);
       bus.write(12,Opcode.LD_L_N8.encoding);
       bus.write(13,7);
       
       cycleCpu(c,14);
       
       assertArrayEquals(new int[] {14,0,1,0,2,3,4,5,6,7},c._testGetPcSpAFBCDEHL());
               
   }
   
   @Test
   void LD_POP_R16Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF - 1 );
       Bus bus = connect(c, r);
       
       bus.write(0, Opcode.LD_SP_N16.encoding);
       bus.write(1, 0b100);
       bus.write(2, 0b0);
       bus.write(3, Opcode.POP_BC.encoding);
       bus.write(4, 42);
       cycleCpu(c,5);
       
       
       assertArrayEquals(new int[] {4,0b110,0,0,0,42,0,0,0,0},c._testGetPcSpAFBCDEHL());
       
   }
   
   @Test
   void LD_N8R_AWorksE() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xffff - 1);
       Bus b = connect(c, r);
       b.write(0,  Opcode.LD_A_N8R.encoding);
       b.write(1,  15);
       b.write(2,  Opcode.LD_N8R_A.encoding);
       b.write(3,  0x0f -1);
       b.write(0xff0f, 5);
       cycleCpu(c, 4);
       assertArrayEquals(new int [] {4, 0, 5, 0, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
       assertEquals(5, b.read(0xff0f), 0.1);
   }
   
   @Test
   void LD_HL_R8WorksE() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF - 1 );
       Bus b = connect(c, r);
       b.write(0,  Opcode.LD_A_N8.encoding);
       b.write(1, 45);
       b.write(2, Opcode.LD_H_N8.encoding);
       b.write(3, 0);
       b.write(4, Opcode.LD_L_N8.encoding);
       b.write(5, 28);
       b.write(6, Opcode.LD_HLR_A.encoding);
       cycleCpu(c, 9);
       assertArrayEquals(new int [] {8, 0, 45, 0, 0, 0, 0, 0, 0, 28}, c._testGetPcSpAFBCDEHL());
       assertEquals(45, b.read(28), 0.1);
   }
   @Test
   void LD_HLRU_AWorksE() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xffff -1);
       Bus b = connect(c, r);
       b.write(0,  Opcode.LD_A_N8.encoding);
       b.write(1,  55);
       b.write(2,  Opcode.LD_L_N8.encoding);
       b.write(3, 120);
       b.write(4, Opcode.LD_HLRI_A.encoding); // incremente
       cycleCpu(c, 6);
       assertArrayEquals(new int [] {5, 0, 55, 0, 0, 0, 0, 0, 0, 121}, c._testGetPcSpAFBCDEHL());
       assertEquals(55, b.read(120), 0.1);
   }
   
   @Test
   void LD_HLRU_AWorks2E() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xffff -1);
       Bus b = connect(c, r);
       b.write(0,  Opcode.LD_A_N8.encoding);
       b.write(1,  55);
       b.write(2,  Opcode.LD_L_N8.encoding);
       b.write(3, 120);
       b.write(4, Opcode.LD_HLRD_A.encoding); // decremente
       cycleCpu(c, 6);
       assertArrayEquals(new int [] {5, 0, 55, 0, 0, 0, 0, 0, 0, 119}, c._testGetPcSpAFBCDEHL());
       assertEquals(55, b.read(120), 0.1);
   }
   @Test 
   void LD_DER_AWorksE() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xffff - 1);
       Bus b = connect(c, r);
       b.write(0,  Opcode.LD_A_N8.encoding);
       b.write(1,  29);
       b.write(2, Opcode.LD_E_N8.encoding);
       b.write(3,  43);
       b.write(43,  33);
       b.write(4,  Opcode.LD_DER_A.encoding);
       cycleCpu(c, 7);
       assertArrayEquals(new int [] {6, 0, 29, 0, 0, 0, 0, 43, 0, 0}, c._testGetPcSpAFBCDEHL());
       assertEquals(29, b.read(43), 0.1);
   }

   @Test
   void LD_BCR_AWorksE() {
   Cpu c = new Cpu();
   Ram r = new Ram(0xffff - 1);
   Bus b = connect(c, r);
   b.write(0,  Opcode.LD_A_N8.encoding);
   b.write(1,  29);
   b.write(2, Opcode.LD_C_N8.encoding);
   b.write(3,  43);
   b.write(43,  33);
   b.write(4,  Opcode.LD_BCR_A.encoding);
   cycleCpu(c, 7);
   assertArrayEquals(new int [] {6, 0, 29, 0, 0, 43, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
   assertEquals(29, b.read(43), 0.1);
    }
   
   @Test
   public void nopDoesNothing2() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_B_B.encoding);
       b.write(1, Opcode.LD_C_C.encoding);
       b.write(2, Opcode.LD_D_D.encoding);
       b.write(3, Opcode.LD_E_E.encoding);
       b.write(4, Opcode.LD_H_H.encoding);
       b.write(5, Opcode.LD_L_L.encoding);
       b.write(6, Opcode.LD_A_A.encoding);
       cycleCpu(c, 7 * Opcode.LD_A_A.cycles);
       assertArrayEquals(new int[] {7,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldR8HlrWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_B_HLR.encoding);
       cycleCpu(c, Opcode.LD_B_HLR.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0X46,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldAHlIRWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_HLRI.encoding);
       cycleCpu(c, Opcode.LD_A_HLRI.cycles);
       assertArrayEquals(new int[] {1,0,0x2A,0,0,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldAHlDRWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_HLRD.encoding);
       cycleCpu(c, Opcode.LD_A_HLRD.cycles);
       assertArrayEquals(new int[] {1,0,0x3A,0,0,0,0,0,0xFF,0xFF}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldAN8RWorksOutsideRange() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_N8R.encoding);
       cycleCpu(c, Opcode.LD_A_N8R.cycles);
       assertArrayEquals(new int[] {2,0,255,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   } 
   
   @Test
   public void ldAN8RWorksInRange() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFF01);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_N8R.encoding);
       cycleCpu(c, Opcode.LD_A_N8R.cycles);
       assertArrayEquals(new int[] {2,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldACRWorksOutsideRange() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_CR.encoding);
       cycleCpu(c, Opcode.LD_A_CR.cycles);
       assertArrayEquals(new int[] {1,0,255,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldACRWorksInRange() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFF01);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_CR.encoding);
       cycleCpu(c, Opcode.LD_A_CR.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   } 
   
   @Test
   public void ldAN16RWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_N16R.encoding);
       cycleCpu(c, Opcode.LD_A_N16R.cycles);
       assertArrayEquals(new int[] {3,0,0xFA,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldABcRWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_BCR.encoding);
       cycleCpu(c, Opcode.LD_A_BCR.cycles);
       assertArrayEquals(new int[] {1,0,0xA,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldADeRWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_DER.encoding);
       cycleCpu(c, Opcode.LD_A_DER.cycles);
       assertArrayEquals(new int[] {1,0,0x1A,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldR8N8Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_B_N8.encoding);
       b.write(1, 0x42);
       cycleCpu(c, Opcode.LD_B_N8.cycles);
       assertArrayEquals(new int[] {2,0,0,0,0x42,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldR16N16Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_SP_N16.encoding);
       b.write(1, 0x42);
       b.write(2, 0x78);
       cycleCpu(c, Opcode.LD_SP_N16.cycles);
       assertArrayEquals(new int[] {3,0x7842,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void popR16Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.POP_BC.encoding);
       cycleCpu(c, Opcode.POP_BC.cycles);
       assertArrayEquals(new int[] {1,2,0,0,0,0xC1,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldR8R8Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_B_C.encoding);
       cycleCpu(c, Opcode.LD_B_C.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void ldSpHlWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_SP_HL.encoding);
       cycleCpu(c, Opcode.LD_SP_HL.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void multipleInstructions() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xF000);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_HL_N16.encoding);
       b.write(1, 0x34);
       b.write(2, 0x12);
       b.write(3, Opcode.LD_C_HLR.encoding);
       b.write(0x1234, 0x42);
       cycleCpu(c, Opcode.LD_HL_N16.cycles + Opcode.LD_C_HLR.cycles);
       assertArrayEquals(new int[] {4,0,0,0,0,0x42,0,0,0x12,0x34}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void multipleInstructions2() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xF000);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_L_N8.encoding);
       b.write(1, 0x36);
       b.write(2, Opcode.LD_C_L.encoding);      
       b.write(3, Opcode.LD_C_C.encoding);        
       cycleCpu(c, Opcode.LD_L_N8.cycles + Opcode.LD_C_L.cycles+ Opcode.LD_C_C.cycles);
       assertArrayEquals(new int[] {4,0,0,0,0,0x36,0,0,0,0x36}, c._testGetPcSpAFBCDEHL());
   }
   
   @Test
   public void multipleInstructions3() {
       Cpu c = new Cpu();
       Ram r = new Ram(0x2500);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_HL_N16.encoding);
       b.write(1, 0x01);
       b.write(2, 0x23);
       b.write(3, Opcode.LD_C_N8.encoding);
       b.write(4, 0x25);
       b.write(5, Opcode.LD_HLR_C.encoding);
       cycleCpu(c, 6);
       assertArrayEquals(new int[] {6,0,0,0,0,0x25,0,0,0x23,0x1}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0x2301), 0x25);
   }
   
   ////////////////////////// MES TESTS //////////////////////////
   
   @Test
   public void ldHLRR8Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_HLR_B.encoding);
       cycleCpu(c, Opcode.LD_HLR_B.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0), 0);
   }
   @Test
   public void ldHlIRAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_HLRI_A.encoding);
       cycleCpu(c, Opcode.LD_HLRI_A.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,1}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0), 0);
   }
   
   @Test
   public void ldHlDRAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_HLRD_A.encoding);
       cycleCpu(c, Opcode.LD_HLRD_A.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0xFF,0xFF}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0), 0);
   }
   
  @Test
   public void ldN8RAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFF0);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_N8R_A.encoding);
       cycleCpu(c, Opcode.LD_N8R_A.cycles);
       assertArrayEquals(new int[] {2,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(AddressMap.REGS_START + b.read(1)), 0);
   }
   
   @Test
   public void ldCRAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFF0);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_CR_A.encoding);
       cycleCpu(c, Opcode.LD_CR_A.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(AddressMap.REGS_START), 0);
   }
   
   @Test
   public void ldN16RAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_N16R_A.encoding);
       b.write(1, Opcode.CP_A_HLR.encoding);
       b.write(2, Opcode.XOR_A_HLR.encoding);
       cycleCpu(c, Opcode.LD_N16R_A.cycles);
       int value = Bits.make16(b.read(2), b.read(1));
       assertArrayEquals(new int[] {3,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(value), 0);
   }
   
   @Test
   public void ldBCRAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_BCR_A.encoding);
       cycleCpu(c, Opcode.LD_BCR_A.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0), 0);
   }
   
   @Test
   public void ldDERAWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_DER_A.encoding);
       cycleCpu(c, Opcode.LD_DER_A.cycles);
       assertArrayEquals(new int[] {1,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0), 0);
   }
   
   @Test
   public void ldHLRn8Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_HLR_N8.encoding);
       b.write(1, Opcode.LD_DER_A.encoding);
       cycleCpu(c, Opcode.LD_HLR_N8.cycles);
       assertArrayEquals(new int[] {2,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0), 0x12);
       //assertEquals(b.read(0), b.read(1));
   }
   
   @Test
   public void ldN16R_SPWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_N16R_SP.encoding);
       cycleCpu(c, Opcode.LD_N16R_SP.cycles);
       int value = Bits.make16(b.read(2), b.read(1));
       assertArrayEquals(new int[] {3,0,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(value), 0);
   }
   
   @Test
   public void ldPUSH_BCWorks() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c, r);
       b.write(0, Opcode.PUSH_BC.encoding);
       cycleCpu(c, Opcode.PUSH_BC.cycles);
       assertArrayEquals(new int[] {1,0xFFFE,0,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       assertEquals(b.read(0xFFFE), 0);
   }
   
//Bizarre comme test ... 

   @Test

   void loadFromAdress() {

       Cpu c = new Cpu();

       Ram r = new Ram(0xFFFF);

       Bus b = connect(c, r);

       b.write(1, 0xFA);

       b.write(2, 0x30);

       b.write(3, 0x15);

       b.write(0x1530, 0x30);

      

       

       c.cycle(0);

       assertArrayEquals(new int[] {1,0, 0x0,0x00,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

       c.cycle(1);

       c.cycle(2);

       c.cycle(3);

       c.cycle(4);

       assertArrayEquals(new int[] {4, 0, 0x30,0x0,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

       c.cycle(5);

       assertArrayEquals(new int[] {5, 0, 0x30,0x0,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

       c.cycle(6);

       assertArrayEquals(new int[] {6, 0, 0x30,0x0,0x0, 0x0,0x0,0,0,0}, c._testGetPcSpAFBCDEHL());

   }
   
   @Test
   void LD_R8_HLRworks() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       
       
       Opcode opcode0 = Opcode.LD_B_N8;
       Opcode opcode1 = Opcode.LD_HL_N16;
       Opcode opcode2 = Opcode.LD_L_HLR;
       
       b.write(0, opcode0.encoding);
       b.write(1, 0x10);
       b.write(2, opcode1.encoding);
       b.write(3, 0x06);
       b.write(4, 0x00);
       b.write(5, opcode2.encoding);
       b.write(0x06, 0x07);
       cycleCpu(c, opcode0.cycles + opcode1.cycles + opcode2.cycles);
       assertArrayEquals(new int[] {6,0,0,0,0x10,0,0,0, 0, 0x07}, c._testGetPcSpAFBCDEHL());
       
   }
   @Test
   void LD_R8_N8_Workplease() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       
       Opcode opcode = Opcode.LD_B_N8;
       b.write(0,  opcode.encoding);
       b.write(1, 0x62);
       cycleCpu(c, opcode.cycles);
       assertArrayEquals(new int[] {2,0,0,0,0x62,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       
   }
   @Test
   void LD__A_N8R() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       
       Opcode opcode = Opcode.LD_A_N8R;
       
       b.write(0,  opcode.encoding);
       b.write(1, 32);
       b.write(0xFF20,0x20);
       cycleCpu(c, opcode.cycles);
       assertArrayEquals(new int[] {opcode.totalBytes,0,0x20,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
}
   @Test
   void LD_A_CR() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       
       
       Opcode opcode1 = Opcode.LD_A_CR;
       Opcode opcode = Opcode.LD_C_N8;
       b.write(0,  opcode.encoding);
       b.write(1, 0x20);
       b.write(2, opcode1.encoding);
       b.write(0xFF00+32, 32);
       cycleCpu(c, opcode1.cycles+ opcode.cycles);
       assertArrayEquals(new int[] {opcode1.totalBytes+opcode.totalBytes,0,0x20,0,0,0x20,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   
   }
   @Test
   void LD_HLRUworksOnKnownValue() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       
       Opcode opcode0 = Opcode.LD_HL_N16;
       Opcode opcode1 = Opcode.LD_A_HLRI;
      
       b.write(0, opcode0.encoding);
       b.write(1, 0x06);
       b.write(2, 0x00);
       b.write(0x6, 0x32);
       b.write(3,opcode1.encoding);
       cycleCpu(c, opcode1.cycles+ opcode0.cycles);
       assertArrayEquals(new int[] {opcode1.totalBytes+opcode0.totalBytes,0,0x32,0,0,0,0,0,0,0x7}, c._testGetPcSpAFBCDEHL());
   }
   @Test
   void LD_A_N16R() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       Opcode opcode = Opcode.LD_A_N16R;
       b.write(1, 16);
       b.write(2, 0);
       
       b.write(0, opcode.encoding);
       b.write(16, 16);
       
       cycleCpu(c, opcode.cycles);
       assertArrayEquals(new int[] {opcode.totalBytes,0,16,0,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
   }
  
   @Test
   void LD_A_BCR() {
       Cpu c = new Cpu();
       Ram r = new Ram(0xFFFF);
       Bus b = connect(c,r);
       
       Opcode opcode0 = Opcode.LD_BC_N16;
       Opcode opcode1 = Opcode.LD_A_BCR;
       
       b.write(0, opcode0.encoding);
       b.write(1, 0x12);
       b.write(2, 0);
       b.write(0x12, 0x2);
       b.write(3, opcode1.encoding);
       
       cycleCpu(c, opcode0.cycles + opcode1.cycles);
       assertArrayEquals(new int[] {opcode0.totalBytes + opcode1.totalBytes,0,0x2,0,0,0x12,0,0,0,0}, c._testGetPcSpAFBCDEHL());
       
   }
   
   @Test
   void LD_R8_HLR_Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_HLR.encoding);
       cycleCpu(c, Opcode.LD_A_HLR.cycles);
       assertArrayEquals(new int[] { 1, 0, 126, 0, 0, 0, 0, 0, 0, 0 },
               c._testGetPcSpAFBCDEHL());
   }

   @Test
   void LD_R8_N8_Works_and_LD_A_B() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_N8.encoding);
       b.write(1, 0x11);
       b.write(2, Opcode.LD_B_A.encoding);
       cycleCpu(c, Opcode.LD_A_N8.cycles + Opcode.LD_B_A.cycles);
       assertArrayEquals(new int[] { 3, 0, 0x11, 0, 0x11, 0, 0, 0, 0, 0 },
               c._testGetPcSpAFBCDEHL());
   }

   @Test
   void LD_A_HLRU_Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_A_HLRI.encoding);
       cycleCpu(c, Opcode.LD_A_HLRI.cycles);// increment HL
       assertArrayEquals(new int[] { 1, 0, 42, 0, 0, 0, 0, 0, 0, 1 },
               c._testGetPcSpAFBCDEHL());
   }

   @Test
   void POP_16_Works() {
       Cpu c = new Cpu();
       Ram r = new Ram(10);
       Bus b = connect(c, r);
       b.write(0, Opcode.LD_B_A.encoding);
       b.write(1, Opcode.LD_A_B.encoding);
       b.write(2, Opcode.POP_BC.encoding);
       cycleCpu(c, Opcode.LD_A_B.cycles + Opcode.LD_SP_N16.cycles
               + Opcode.LD_B_A.cycles);
       assertArrayEquals(new int[] { 3, 2, 0, 0, 0x78, 0x47, 0, 0, 0, 0 },
               c._testGetPcSpAFBCDEHL());
   }
    
   
}
