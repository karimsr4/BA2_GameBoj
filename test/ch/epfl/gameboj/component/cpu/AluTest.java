package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AluTest {

	@Test
	void maskZNHCworksPerfectly() {
		assertEquals(0x70, Alu.maskZNHC(false, true, true, true));
	}

}
