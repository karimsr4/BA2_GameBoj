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
	void unpackValueWorksOnKnownValues() {
		assertEquals(0xFF,Alu.unpackValue(0xFF70));
	}

	
	@Test
	void unpackFlagsWorksOnKnownValues() {
		assertEquals(0x70,Alu.unpackFlags(0xFF70));
	}
	
	
	@Test
	void add1WorksOnKnownValues() {
		assertEquals(0x00,Alu.unpackValue(Alu.add(0x80, 0x7F, true)));
		assertEquals(0xB0,Alu.unpackFlags(Alu.add(0x80, 0x7F, true)));
	}
	
	@Test
	void add2WorksOnKnownValues() {
		assertEquals(0x25,Alu.unpackValue(Alu.add(0x10, 0x15)));
		assertEquals(0x00,Alu.unpackFlags(Alu.add(0x10, 0x15)));
		
		assertEquals(0x10,Alu.unpackValue(Alu.add(0x08, 0x08)));
		assertEquals(0x20,Alu.unpackFlags(Alu.add(0x08, 0x08)));
	}
	
	
	@Test
	void add16LWorksOnKnownValues() {
		assertEquals(0x1200,Alu.unpackValue(Alu.add16L(0x11FF, 0x0001)));
		assertEquals(0x30,Alu.unpackFlags(Alu.add16L(0x11FF, 0x0001)));
	}
	
	
	@Test
	void add16HWorksOnKnownValues() {
		assertEquals(0x1200,Alu.unpackValue(Alu.add16H(0x11FF, 0x0001)));
		assertEquals(0x00,Alu.unpackFlags(Alu.add16H(0x11FF, 0x0001)));
	}
	
	
	@Test
	void sub1WorksOnKnownValues() {
		assertEquals(0x00,Alu.unpackValue(Alu.sub(0x10, 0x10)));
		assertEquals(0xC0,Alu.unpackFlags(Alu.sub(0x10, 0x10)));
		
		assertEquals(0x90,Alu.unpackValue(Alu.sub(0x10, 0x80)));
		assertEquals(0x50,Alu.unpackFlags(Alu.sub(0x10, 0x80)));
	}
	
	
	@Test
	void sub2WorksOnKnownValues() {
		assertEquals(0xFF,Alu.unpackValue(Alu.sub(0x01, 0x01, true)));
		assertEquals(0x70,Alu.unpackFlags(Alu.sub(0x01, 0x01, true)));
	}
	
	
	@Test
	void andWorksOnKnownValues() {
		assertEquals(0x03,Alu.unpackValue(Alu.and(0x53, 0xA7)));
		assertEquals(0x20,Alu.unpackFlags(Alu.and(0x53, 0xA7)));
	}
	
	
	@Test
	void orWorksOnKnownValues() {
		assertEquals(0xF7,Alu.unpackValue(Alu.or(0x53, 0xA7)));
		assertEquals(0x00,Alu.unpackFlags(Alu.or(0x53, 0xA7)));
	}
	
	
	@Test
	void xorWorksOnKnownValues() {
		assertEquals(0xF4,Alu.unpackValue(Alu.xor(0x53, 0xA7)));
		assertEquals(0x00,Alu.unpackFlags(Alu.xor(0x53, 0xA7)));
	}
	
	
	@Test
	void shiftLeftWorksOnKnownValues() {
		assertEquals(0x00,Alu.unpackValue(Alu.shiftLeft(0x80)));
		assertEquals(0x90,Alu.unpackFlags(Alu.shiftLeft(0x80)));
	}
	
	
	
	@Test
	void shiftRightAWorksOnKnownValues() {
		assertEquals(0xC0,Alu.unpackValue(Alu.shiftRightA(0x80)));
		assertEquals(0x00,Alu.unpackFlags(Alu.shiftRightA(0x80)));
	}
	
	
	
	@Test
	void shiftRightLWorksOnKnownValues() {
		assertEquals(0x40,Alu.unpackValue(Alu.shiftRightL(0x80)));
		assertEquals(0x00,Alu.unpackFlags(Alu.shiftRightL(0x80)));
	}
	
	
	@Test
	void rotate1WorksOnKnownValues() {
		assertEquals(0x01,Alu.unpackValue(Alu.rotate(RotDir.LEFT, 0x80)));
		assertEquals(0x10,Alu.unpackFlags(Alu.rotate(RotDir.LEFT, 0x80)));
	}
	
	@Test
	void rotate2WorksOnKnownValues() {
		
		assertEquals(0x01,Alu.unpackValue(Alu.rotate(RotDir.LEFT, 0x00,true)));
		assertEquals(0x00,Alu.unpackFlags(Alu.rotate(RotDir.LEFT, 0x00,true)));
		
		assertEquals(0x00,Alu.unpackValue(Alu.rotate(RotDir.LEFT, 0x80,false)));
		assertEquals(0x90,Alu.unpackFlags(Alu.rotate(RotDir.LEFT, 0x80,false)));
	}
	
	
	
	
	
	
}