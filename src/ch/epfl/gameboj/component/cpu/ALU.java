package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bits;

public final class ALU {

	private ALU() {

	}

	public static int maskZNHC(boolean z, boolean n, boolean h, boolean c) {
		return Bits.set(0, 4, c) | Bits.set(0, 5, h) | Bits.set(0, 6, n) | Bits.set(0, 7, z);

	}

	public static int unpackValue(int valueFlags) {
		return Bits.extract(valueFlags, 8, 8);

	}

	public static int unpackFlags(int valueFlags) {
		return Bits.extract(valueFlags, 4, 4);

	}

	public static int add(int l, int r, boolean c0) {
		// A verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);

		int result = l + r + Bits.set(0, 0, c0);
		boolean c = (result) > 0xFF;
		boolean h = ((l << 4) + (r << 4)) + (Bits.set(0, 0, c0) << 4) > 0xFF;

		int additionResult = Bits.extract(result, 0, 8);
		boolean z = additionResult == 0;
		
		return packValueZNHC(additionResult, z, false, h, c);

		

	}

	public static int add(int l, int r) {
		return add(l, r, false);

	}

	public static int add16L(int l, int r) {
		// a verifier
		Preconditions.checkBits16(l);
		Preconditions.checkBits16(r);

		int result = l + r;
		boolean c = (((l << 8) >>> 8) + ((r << 8) >>> 8)) > 0xFF;
		boolean h = (((l << 12) >>> 12) + ((r << 12) >>> 12)) > 0xF;
		
		return packValueZNHC(result, false, false, h, c);

		

	}

	public static int add16H(int l, int r) {
		return 0;

	}

	public static int sub(int l, int r, boolean b0) {
		return 0;

	}

	public static int sub(int l, int r) {
		return 0;

	}

	public static int bcdAdjust(int v, boolean n, boolean h, boolean c) {
		return 0;

	}

	public static int and(int l, int r) {
		// a verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l & r;
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, true, false);
		

	}

	public static int or(int l, int r) {
		// a verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l | r;
		boolean z = (result == 0);
		return (result << 8) | maskZNHC(z, false, false, false);

	}

	public static int xor(int l, int r) {
		// a verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l ^ r;
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, false, false);
		

	}

	public static int shiftLeft(int v) {
		return 0;

	}

	public static int shiftRightA(int v) {
		return 0;

	}

	public static int shiftRightL(int v) {
		return 0;

	}

	public static int rotate(RotDir d, int v) {

	}

	public static int rotate(RotDir d, int v, boolean c) {

	}

	public static int swap(int v) {
		return 0;

	}

	public static int testBit(int v, int bitIndex) {
		return 0;

	}

	
	private static int packValueZNHC(int v , boolean z, boolean n, boolean h, boolean c) {
		return (v<<8)| maskZNHC(z, n, h, c);
	}
	
}
