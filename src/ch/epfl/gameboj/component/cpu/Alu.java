package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

public final class Alu {

	public enum RotDir {
		LEFT, RIGHT
	}

	public enum Flag implements Bit {
		UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, Z, N, H, C
	}

	private Alu() {

	}

	/**
	 * @param z
	 * @param n
	 * @param h
	 * @param c
	 * @return
	 */
	public static int maskZNHC(boolean z, boolean n, boolean h, boolean c) {
		return Bits.set(0, 4, c) | Bits.set(0, 5, h) | Bits.set(0, 6, n) | Bits.set(0, 7, z);

	}

	/**
	 * @param valueFlags
	 * @return
	 */
	public static int unpackValue(int valueFlags) {
		// value 16 bits?
		return Bits.extract(valueFlags, 8, 8);

	}

	/**
	 * @param valueFlags
	 * @return
	 */
	public static int unpackFlags(int valueFlags) {
		// 8 bits?
		return Bits.extract(valueFlags, 4, 4);

	}

	/**
	 * @param l
	 * @param r
	 * @param c0
	 * @return
	 */
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

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int add(int l, int r) {
		return add(l, r, false);

	}

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int add16L(int l, int r) {
		// a verifier
		Preconditions.checkBits16(l);
		Preconditions.checkBits16(r);

		int result = l + r;
<<<<<<< HEAD
		boolean c = (((l << 8) >>> 8) + ((r << 8) >>> 8)) > 0xFF;// Bits.clip(8, l)+Bits.clip(8, r)?
		boolean h = (((l << 12) >>> 12) + ((r << 12) >>> 12)) > 0xF;// Bits.clip(4,l)+Bits.clip(4,r)?
		// result=Bits.clip(result,16); ?
=======
		boolean c = (((l << 8) >>> 8) + ((r << 8) >>> 8)) > 0xFF;//Bits.clip(8, l)+Bits.clip(8, r)?
		boolean h = (((l << 12) >>> 12) + ((r << 12) >>> 12)) > 0xF;//Bits.clip(4,l)+Bits.clip(4,r)?
		//result=Bits.clip(result,16); ?
		
		return packValueZNHC(Bits.extract(result, 0, 16), false, false, h, c);
>>>>>>> b007de66e6ecd3bad14e3e12f56fc9a78d208cd3

		return packValueZNHC(result, false, false, h, c);

	}

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int add16H(int l, int r) {
		Preconditions.checkBits16(l);
		Preconditions.checkBits16(r);
		int result = l + r;
		boolean c = (Bits.extract(l, 8, 4) + Bits.extract(l, 8, 4)) > 0xFF;
		boolean h = (Bits.extract(l, 8, 4) + Bits.extract(l, 8, 4)) > 0xF;
		result = Bits.clip(16, result);

		/**
		 * int result1=add(Bits.clip(8,l),Bits.clip(8,r)); int
		 * result2=add(Bits.extract(8,l),Bits.extract(8,r),Bits.test(result1,4); int
		 * result = Bits.add16....
		 */

		return packValueZNHC(result, false, false, h, c);

	}

	/**
	 * @param l
	 * @param r
	 * @param b0
	 * @return
	 */
	public static int sub(int l, int r, boolean b0) {
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l - (r + Bits.set(0, 0, b0));
		boolean c = l < r;
		boolean z = (l == r);
		boolean h = (Bits.clip(4, l) < Bits.clip(4, r));

		return packValueZNHC(result, z, true, h, c);

	}

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int sub(int l, int r) {
		return sub(l, r, false);
	}

	/**
	 * @param v
	 * @param n
	 * @param h
	 * @param c
	 * @return
	 */
	public static int bcdAdjust(int v, boolean n, boolean h, boolean c) {
		return 0;

	}

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int and(int l, int r) {
		// a verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l & r;
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, true, false);

	}

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int or(int l, int r) {
		// a verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l | r;
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, false, false);

	}

	/**
	 * @param l
	 * @param r
	 * @return
	 */
	public static int xor(int l, int r) {
		// a verifier
		Preconditions.checkBits8(l);
		Preconditions.checkBits8(r);
		int result = l ^ r;
		boolean z = (result == 0);
		return packValueZNHC(result, z, false, false, false);

	}

	/**
	 * @param v
	 * @return
	 */
	public static int shiftLeft(int v) {
<<<<<<< HEAD
		Preconditions.checkBits8(v);
		return packValueZNHC(v << 1, v == 0, false, false, Bits.test(v, 7));
=======
		int result=Bits.extract(v<<1, 0, 8);
		return packValueZNHC(result, (result==0	), false, false, Bits.test(v, 7));
>>>>>>> b007de66e6ecd3bad14e3e12f56fc9a78d208cd3

	}

	/**
	 * @param v
	 * @return
	 */
	public static int shiftRightA(int v) {
<<<<<<< HEAD
		Preconditions.checkBits8(v);
		return packValueZNHC(v >> 1, v == 0, false, false, Bits.test(v, 0));
=======
		int result=Bits.extract(v>>1, 0, 8);
		return packValueZNHC(result, (result==0	), false, false, Bits.test(v, 0));
>>>>>>> b007de66e6ecd3bad14e3e12f56fc9a78d208cd3

	}

	/**
	 * @param v
	 * @return
	 */
	public static int shiftRightL(int v) {
<<<<<<< HEAD
		Preconditions.checkBits8(v);
		return packValueZNHC(v >>> 1, v == 0, false, false, Bits.test(v, 0));
=======
		int result=Bits.extract(v>>>1, 0, 8);
		return packValueZNHC(result, (result==0	), false, false, Bits.test(v, 0));
>>>>>>> b007de66e6ecd3bad14e3e12f56fc9a78d208cd3

	}

	/**
	 * @param d
	 * @param v
	 * @return
	 */
	public static int rotate(RotDir d, int v) {
		Preconditions.checkBits8(v);
		int result = 0;
		switch (d) {
		case LEFT:
			result = Bits.rotate(8, v, 1);
			return packValueZNHC(result, result == 0, false, false, Bits.test(v, 1));
		default:
			result = Bits.rotate(8, v, -1);
			return packValueZNHC(result, result == 0, false, false, Bits.test(v, 7));
		}

	}

	/**
	 * @param d
	 * @param v
	 * @param c
	 * @return
	 */
	public static int rotate(RotDir d, int v, boolean c) {
		return 0;
	}

	/**
	 * @param v
	 * @return
	 */
	public static int swap(int v) {
<<<<<<< HEAD
		Preconditions.checkBits8(v);

		return Bits.make16(Bits.clip(4, v), Bits.extract(v, 4, 4));
=======
		//a verifier
		Preconditions.checkBits8(v);
		return or(Bits.extract(v,4,4), Bits.extract(v<<4,0 , 8)); 
>>>>>>> b007de66e6ecd3bad14e3e12f56fc9a78d208cd3

	}

	/**
	 * @param v
	 * @param bitIndex
	 * @return
	 */
	public static int testBit(int v, int bitIndex) {
		Preconditions.checkBits8(v);
		if ((bitIndex < 0) || (bitIndex > 7)) {
			throw new IndexOutOfBoundsException();
		}
		return packValueZNHC(0, Bits.test(v, bitIndex), false, true, Bits.test(v, 7));

	}

<<<<<<< HEAD
	private static int packValueZNHC(int v, boolean z, boolean n, boolean h, boolean c) {
		return (v << 8) | maskZNHC(z, n, h, c);// Bits.add16?
=======
	
	/**
	 * @param v
	 * @param z
	 * @param n
	 * @param h
	 * @param c
	 * @return
	 */
	private static int packValueZNHC(int v , boolean z, boolean n, boolean h, boolean c) {
		return (v<<8)| maskZNHC(z, n, h, c);
>>>>>>> b007de66e6ecd3bad14e3e12f56fc9a78d208cd3
	}

}
