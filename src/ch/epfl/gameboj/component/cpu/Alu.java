package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

public final class Alu {

	public enum RotDir {
		LEFT, RIGHT
	}

	public enum Flag implements Bit {
		UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, C, H, N, Z
	}

	private Alu() {

	}

	public static int maskZNHC(boolean z, boolean n, boolean h, boolean c) {
		return (z ? Flag.Z.mask() : 0) | (n ? Flag.N.mask() : 0) | (h ? Flag.H.mask() : 0) | (c ? Flag.C.mask() : 0);

	}

	public static int unpackValue(int valueFlags) {
		Preconditions.checkArgument(Bits.clip(4, valueFlags) == 0 && Bits.extract(valueFlags, 24, 8) == 0);
		return Bits.extract(valueFlags, 8, 16);

	}

	public static int unpackFlags(int valueFlags) {
		Preconditions.checkArgument(Bits.clip(4, valueFlags) == 0 && Bits.extract(valueFlags, 24, 8) == 0);
		return (Bits.extract(valueFlags, 4, 4)) << 4;

	}

	public static int add(int l, int r, boolean c0) {

		int result = Preconditions.checkBits8(l) + Preconditions.checkBits8(r) + Bits.set(0, 0, c0);
		boolean c = (result) > 0xFF;
		boolean h = (Bits.clip(4, r) + Bits.clip(4, l) + Bits.set(0, 0, c0)) > 0xF;

		int additionResult = Bits.extract(result, 0, 8);
		boolean z = additionResult == 0;

		return packValueZNHC(additionResult, z, false, h, c);

	}

	public static int add(int l, int r) {
		return add(l, r, false);

	}

	public static int add16L(int l, int r) {

		int result = Preconditions.checkBits16(l) + Preconditions.checkBits16(r);
		boolean c = (Bits.clip(8, l) + Bits.clip(8, r)) > 0xFF;
		boolean h = (Bits.clip(4, l) + Bits.clip(4, r)) > 0xF;
		result = Bits.clip(16, result);

		return packValueZNHC(result, false, false, h, c);

	}

	public static int add16H(int l, int r) {

		int result = Preconditions.checkBits16(l) + Preconditions.checkBits16(r);
		boolean cin = (Bits.clip(8, l) + Bits.clip(8, r)) > 0xFF;
		boolean c = (Bits.extract(l, 8, 8) + Bits.extract(r, 8, 8)+Bits.set(0,0,cin)) > 0xFF;
		boolean h = (Bits.extract(l, 8, 4) + Bits.extract(r, 8, 4)+Bits.set(0,0,cin)) > 0xF;

		result = Bits.clip(16, result);

		return packValueZNHC(result, false, false, h, c);

	}

	public static int sub(int l, int r, boolean b0) {

		int result = Bits.clip(8, Preconditions.checkBits8(l) - (Preconditions.checkBits8(r) + Bits.set(0, 0, b0)));
		boolean c = l < (r + Bits.set(0, 0, b0));
		boolean z = (result == 0);
		boolean h = (Bits.clip(4, l) < (Bits.clip(4, r) + Bits.set(0, 0, b0)));

		return packValueZNHC(result, z, true, h, c);

	}

	public static int sub(int l, int r) {
		return sub(l, r, false);
	}

	public static int bcdAdjust(int v, boolean n, boolean h, boolean c) {
		boolean fixL = (h) || ((!(n)) && (Bits.clip(4, v) > 9));
		boolean fixH = c || ((!n) && (v > 0x99));
		int fix = (0x60) * Bits.set(0, 0, fixH) + (0x06) * Bits.set(0, 0, fixL);
		int Va = n ? v - fix : v + fix;
		return packValueZNHC(Va, Va == 0, n, false, fixH);

	}

	public static int and(int l, int r) {

		int result = Preconditions.checkBits8(l) & Preconditions.checkBits8(r);
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, true, false);

	}

	public static int or(int l, int r) {

		int result = Preconditions.checkBits8(l) | Preconditions.checkBits8(r);
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, false, false);

	}

	public static int xor(int l, int r) {

		int result = Preconditions.checkBits8(l) ^ Preconditions.checkBits8(r);
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, false, false);

	}

	public static int shiftLeft(int v) {
		
		int result = Bits.clip(8, Preconditions.checkBits8(v) << 1);
		return packValueZNHC(result, result == 0, false, false, Bits.test(v, 7));

	}

	public static int shiftRightA(int v) {
		
		int result = ((Preconditions.checkBits8(v) << 24) >> 1) >>> 24;
		return packValueZNHC(result, result == 0, false, false, Bits.test(v, 0));

	}

	public static int shiftRightL(int v) {
		Preconditions.checkBits8(v);
		return packValueZNHC(v >>> 1, v >>> 1 == 0, false, false, Bits.test(v, 0));

	}

	public static int rotate(RotDir d, int v) {
		Preconditions.checkBits8(v);
		int result = 0;
		switch (d) {
		case LEFT:
			result = Bits.rotate(8, v, 1);
			return packValueZNHC(result, result == 0, false, false, Bits.test(v, 7));
		default:
			result = Bits.rotate(8, v, -1);
			return packValueZNHC(result, result == 0, false, false, Bits.test(v, 0));
		}

	}

	public static int rotate(RotDir d, int v, boolean c) {
		
		v = Bits.set(Preconditions.checkBits8(v), 8, c);
		int result = 0;

		switch (d) {
		case LEFT:
			v = Bits.rotate(9, v, 1);
			result = Bits.clip(8, v);
			return packValueZNHC(result, result == 0, false, false, Bits.test(v, 8));
		default:
			v = Bits.rotate(9, v, -1);
			result = Bits.clip(8, v);
			return packValueZNHC(result, result == 0, false, false, Bits.test(v, 8));
		}

	}

	public static int swap(int v) {
		
		return Bits.extract(Preconditions.checkBits8(v), 4, 0) | Bits.clip(4, v) << 4;

	}

	public static int testBit(int v, int bitIndex) {
		Preconditions.checkBits8(v);
		if ((bitIndex < 0) || (bitIndex > 7)) {
			throw new IndexOutOfBoundsException();
		} else {
			return packValueZNHC(0, Bits.test(v, bitIndex), false, true, false);
		}

	}

	private static int packValueZNHC(int v, boolean z, boolean n, boolean h, boolean c) {
		return (v << 8) | maskZNHC(z, n, h, c);
	}

}
