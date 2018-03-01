package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.component.cpu.Alu.RotDir;

class AluTest {

    @Test
    void maskZNHCworksPerfectly() {
        assertEquals(0x70, Alu.maskZNHC(false, true, true, true));
    }

    @Test
    void maskZNHCworksForAllPositive() {
        assertEquals(0b11110000, Alu.maskZNHC(true, true, true, true));
    }

    @Test
    void maskZNHCworksForAllNegative() {
        assertEquals(0, Alu.maskZNHC(false, false, false, false));
    }


    @Test
    void unpackValueWorksOnKnownValues1() {
        assertEquals(0xFF, Alu.unpackValue(0xFF70));
    }

    @Test
    void unpackValueWorksForZeroValues() {
        assertEquals(0, Alu.unpackValue(0));
    }

    @Test
    void unpackValueWorksFor16BitValues() {
        assertEquals(0xFF70, Alu.unpackValue(0xFF7070));
    }

    @Test
    void unpackValueWorksForMaxValue() {
        assertEquals(0xFFFF, Alu.unpackValue(0xFFFF00));
    }

    @Test
    void unpackFlagsWorksOnKnownValues() {
        assertEquals(0x70, Alu.unpackFlags(0xFF70));
    }

    @Test
    void unpackFlagsWorksOnZeroValues() {
        assertEquals(0, Alu.unpackFlags(0));
    }

    @Test
    void UnpackFlagsWorksOnMaxValue() {
        assertEquals(0xF0, Alu.unpackFlags(0xF0));
    }

    @Test
    void add1WorksOnKnownValues() {
        assertEquals(0x00, Alu.unpackValue(Alu.add(0x80, 0x7F, true)));
        assertEquals(0xB0, Alu.unpackFlags(Alu.add(0x80, 0x7F, true)));
    }

    @Test
    void add2WorksOnKnownValues() {
        assertEquals(0x25, Alu.unpackValue(Alu.add(0x10, 0x15)));
        assertEquals(0x00, Alu.unpackFlags(Alu.add(0x10, 0x15)));

        assertEquals(0x10, Alu.unpackValue(Alu.add(0x08, 0x08)));
        assertEquals(0x20, Alu.unpackFlags(Alu.add(0x08, 0x08)));
    }

    @Test
    void add16LWorksOnKnownValues() {
        assertEquals(0x1200, Alu.unpackValue(Alu.add16L(0x11FF, 0x0001)));
        assertEquals(0x30, Alu.unpackFlags(Alu.add16L(0x11FF, 0x0001)));
    }

    @Test
    void add16HWorksOnKnownValues() {
        assertEquals(0x1200, Alu.unpackValue(Alu.add16H(0x11FF, 0x0001)));
        assertEquals(0x00, Alu.unpackFlags(Alu.add16H(0x11FF, 0x0001)));
    }

    @Test
    void sub1WorksOnKnownValues() {
        assertEquals(0x00, Alu.unpackValue(Alu.sub(0x10, 0x10)));
        assertEquals(0xC0, Alu.unpackFlags(Alu.sub(0x10, 0x10)));

        assertEquals(0x90, Alu.unpackValue(Alu.sub(0x10, 0x80)));
        assertEquals(0x50, Alu.unpackFlags(Alu.sub(0x10, 0x80)));
    }

    @Test
    void sub2WorksOnKnownValues() {
        assertEquals(0xFF70, Alu.sub(0x01, 0x01, true));

    }

    @Test
    void andWorksOnKnownValues() {
        assertEquals(0x03, Alu.unpackValue(Alu.and(0x53, 0xA7)));
        assertEquals(0x20, Alu.unpackFlags(Alu.and(0x53, 0xA7)));
    }

    @Test
    void orWorksOnKnownValues() {
        assertEquals(0xF7, Alu.unpackValue(Alu.or(0x53, 0xA7)));
        assertEquals(0x00, Alu.unpackFlags(Alu.or(0x53, 0xA7)));
    }

    @Test
    void xorWorksOnKnownValues() {
        assertEquals(0xF4, Alu.unpackValue(Alu.xor(0x53, 0xA7)));
        assertEquals(0x00, Alu.unpackFlags(Alu.xor(0x53, 0xA7)));
    }

    @Test
    void shiftLeftWorksOnKnownValues() {
        assertEquals(0x00, Alu.unpackValue(Alu.shiftLeft(0x80)));
        assertEquals(0x90, Alu.unpackFlags(Alu.shiftLeft(0x80)));
    }

    @Test
    void shiftRightAWorksOnKnownValues() {
        assertEquals(0xC0, Alu.unpackValue(Alu.shiftRightA(0x80)));

    }

    @Test
    void shiftRightLWorksOnKnownValues() {
        assertEquals(0x40, Alu.unpackValue(Alu.shiftRightL(0x80)));
        assertEquals(0x00, Alu.unpackFlags(Alu.shiftRightL(0x80)));
    }

    @Test
    void rotate1WorksOnKnownValues() {
        assertEquals(0x0110, Alu.rotate(RotDir.LEFT, 0x80));
        assertEquals(0x0110, Alu.rotate(RotDir.LEFT, 0x80));
        
        
    }

    @Test
    void rotate2WorksOnKnownValues() {

        assertEquals(0x01,
                Alu.unpackValue(Alu.rotate(RotDir.LEFT, 0x00, true)));
        assertEquals(0x00,
                Alu.unpackFlags(Alu.rotate(RotDir.LEFT, 0x00, true)));

        assertEquals(0x00,
                Alu.unpackValue(Alu.rotate(RotDir.LEFT, 0x80, false)));
        assertEquals(0x90,
                Alu.unpackFlags(Alu.rotate(RotDir.LEFT, 0x80, false)));
        
        
        assertEquals(0b1000000000000000, Alu.rotate(RotDir.RIGHT, 0b0,true));
    }

    @Test
    void BCDadjustWorksonKnownValues() {

        assertEquals(0x7300, Alu.bcdAdjust(0x6D, false, false, false));
        assertEquals(0x0940, Alu.bcdAdjust(0x0F, true, true, false));
    }
    
    
    @Test
    void swapWorkOnKnownValues() {
        
        assertEquals(0x7F00, Alu.swap( 0xF7));
        
    }

  
}
