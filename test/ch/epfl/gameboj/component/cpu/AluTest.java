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
    void unpackValueWorksOnKnownValues() {
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
        assertEquals(0xF0, Alu.unpackFlags(0xFF));
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
        assertEquals(0x01, Alu.unpackValue(Alu.rotate(RotDir.LEFT, 0x80)));
        assertEquals(0x10, Alu.unpackFlags(Alu.rotate(RotDir.LEFT, 0x80)));
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
    }

    @Test
    void BCDadjustWorksonKnownValues() {

        assertEquals(0x7300, Alu.bcdAdjust(0x6D, false, false, false));
        assertEquals(0x0940, Alu.bcdAdjust(0x0F, true, true, false));
    }

    @Test
    void maskZNHCWorks() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int z = RandomGenerator.randomIntBetweenOneAndZero();
            int n = RandomGenerator.randomIntBetweenOneAndZero();
            int h = RandomGenerator.randomIntBetweenOneAndZero();
            int c = RandomGenerator.randomIntBetweenOneAndZero();
            assertEquals(
                    Alu.maskZNHC(zeroAndOneToBoolean(z), zeroAndOneToBoolean(n),
                            zeroAndOneToBoolean(h), zeroAndOneToBoolean(c)),
                    c << 4 | h << 5 | n << 6 | z << 7);
        }
    }

    @Test
    void unpackValueWorksOn8And16Bits() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit16 = RandomGenerator.randomBit(16);
            int bit8 = RandomGenerator.randomBit(8);
            assertEquals(
                    Alu.unpackValue(
                            bit16 << 8 | (RandomGenerator.randomBit(4)) << 4),
                    bit16);
            assertEquals(
                    Alu.unpackValue(
                            bit8 << 8 | (RandomGenerator.randomBit(4)) << 4),
                    bit8);
        }

    }

    @Test
    void unpackValueThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = RandomGenerator.randomBit(25) | 1 << 24;
            int outOfBoundBit2 = RandomGenerator.randomBit(32) | 1 << 24;
            int uncorrectFormat = RandomGenerator.randomBit(23) | 1111;
            int negatifBit = 1 << 31 | RandomGenerator.randomBit(32);
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackValue(negatifBit);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackValue(outOfBoundBit);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackValue(outOfBoundBit2);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackValue(uncorrectFormat);
            });
        }
    }

    @Test
    void unpackFlagsWorksOn8And16Bits() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit16 = RandomGenerator.randomBit(16) & ~0b1111;
            int bit8 = RandomGenerator.randomBit(8) & ~0b1111;
            int bit24 = RandomGenerator.randomBit(24) & ~0b1111;
            assertEquals(Alu.unpackFlags(bit16), ((bit16 >>> 4) & 0b1111));
            assertEquals(Alu.unpackFlags(bit8), ((bit8 >>> 4) & 0b1111));
            assertEquals(Alu.unpackFlags(bit24), ((bit24 >>> 4) & 0b1111));
        }

    }

    @Test
    void unpackFlagsThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = RandomGenerator.randomBit(25) | 1 << 24;
            int outOfBoundBit2 = RandomGenerator.randomBit(32) | 1 << 24;
            int uncorrectFormat = RandomGenerator.randomBit(23) | 1;
            int negatifBit = 1 << 31 | RandomGenerator.randomBit(32);
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackFlags(negatifBit);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackFlags(outOfBoundBit);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackFlags(outOfBoundBit2);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.unpackFlags(uncorrectFormat);
            });
        }
    }

    @Test
    void add8ThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = RandomGenerator.randomBit(8) + 1 | 1 << 9;
            int outOfBoundBit2 = RandomGenerator.randomBit(9) | 1 << 9;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.add(outOfBoundBit, outOfBoundBit2, zeroAndOneToBoolean(
                        RandomGenerator.randomIntBetweenOneAndZero()));
            });
        }
    }

    @Test
    void addWorksOn8Bit() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(8);
            int bit2 = RandomGenerator.randomBit(8);
            int carryIn = RandomGenerator.randomIntBetweenOneAndZero();
            boolean c = bit1 + bit2 + carryIn > 0xFF;
            boolean h = (bit1 & 0xF) + (bit2 & 0xF) + carryIn > 0xF;
            boolean z = ((bit1 + bit2 + carryIn) & 0xFF) == 0;
            assertEquals(
                    ((bit1 + bit2 + carryIn) << 8
                            | (booleanToZeroAndOne(z) << 7) | (0 << 6)
                            | (booleanToZeroAndOne(h) << 5)
                            | (booleanToZeroAndOne(c) << 4))
                            & 0b1111111111110000,
                    Alu.add(bit1, bit2, zeroAndOneToBoolean(carryIn)));
        }
    }

    @Test
    void add16LThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = RandomGenerator.randomBit(16) + 1 | 1 << 16;
            int outOfBoundBit2 = RandomGenerator.randomBit(17) | 1 << 17;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.add16L(outOfBoundBit, outOfBoundBit2);
            });
        }
    }

    @Test
    void add16LWorksOn8Bits() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(16);
            int bit2 = RandomGenerator.randomBit(16);
            boolean c = (bit1 & 0xFF) + (bit2 & 0xFF) > 0xFF;
            boolean h = (bit1 & 0xF) + (bit2 & 0xF) > 0xF;
            assertEquals(
                    ((bit1 + bit2) << 8 | (0 << 7) | (0 << 6)
                            | (booleanToZeroAndOne(h) << 5)
                            | (booleanToZeroAndOne(c) << 4))
                            & 0b111111111111111111110000,
                    Alu.add16L(bit1, bit2));
        }

    }

    @Test
    void add16HThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = RandomGenerator.randomBit(16) + 1 | 1 << 16;
            int outOfBoundBit2 = RandomGenerator.randomBit(17) | 1 << 17;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.add16H(outOfBoundBit, outOfBoundBit2);
            });
        }
    }

    @Test 
    void add16HWorksOn8Bits () {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(16);
            int bit2 = RandomGenerator.randomBit(16);
            int carryIn = booleanToZeroAndOne((bit1 & 0xFF) + (bit2 & 0xFF) > 0xFF);
            boolean c = (((bit1 & 0xFF00) + (bit2 & 0xFF00)) >>> 8)  + carryIn > 0xFF;
            boolean h = (((bit1 & 0xF00) + (bit2 & 0xF00))  >>> 8) + carryIn > 0xF;
            assertEquals (((bit1 + bit2)<<8 | (0 << 7) | (0 << 6) | (booleanToZeroAndOne(h) << 5) | (booleanToZeroAndOne(c) << 4)) & 0b111111111111111111110000, Alu.add16H(bit1, bit2));
        }
        
    }

    @Test
    void subThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = (RandomGenerator.randomBit(9) + 1) | 1 << 8;
            int outOfBoundBit2 = RandomGenerator.randomBit(32) | 1 << 9;
            boolean borrow = RandomGenerator.randomBoolean();
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.sub(outOfBoundBit, outOfBoundBit2, borrow);
            });
        }
    }

    @Test
    void subWorksOn8bits() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(8);
            System.out.println(Integer.toHexString(bit1));
            int bit2 = RandomGenerator.randomBit(8);
            System.out.println(Integer.toHexString(bit2));
            int borrow = RandomGenerator.randomIntBetweenOneAndZero();
            System.out.println(borrow);
            boolean z = bit1 == bit2 + borrow;
            boolean c = bit1 < bit2 + borrow;
            boolean h = (bit1 & 0x0F) < ((bit2 & 0x0F) + borrow);
            assertEquals(
                    (((bit1 - bit2 - borrow) << 8
                            | (booleanToZeroAndOne(z) << 7) | (1 << 6)
                            | (booleanToZeroAndOne(h) << 5)
                            | (booleanToZeroAndOne(c) << 4)) & 0xFFF0),
                    Alu.sub(bit1, bit2, zeroAndOneToBoolean(borrow)));
        }
    }

    @Test
    void andThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = (RandomGenerator.randomBit(8) + 1) | 1 << 8;
            int outOfBoundBit2 = RandomGenerator.randomBit(32) | 1 << 9;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.and(outOfBoundBit, outOfBoundBit2);
            });
        }
    }

    @Test
    void andWorks() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(8);
            int bit2 = RandomGenerator.randomBit(8);
            boolean z = (bit1 & bit2) == 0;
            assertEquals((((bit1 & bit2) << 8 | (booleanToZeroAndOne(z) << 7)
                    | (0 << 6) | (1 << 5) | (0 << 4))
                    & 0b111111111111111111110000), Alu.and(bit1, bit2));
        }
    }

    @Test
    void orThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = (RandomGenerator.randomBit(8) + 1) | 1 << 8;
            int outOfBoundBit2 = RandomGenerator.randomBit(32) | 1 << 9;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.or(outOfBoundBit, outOfBoundBit2);
            });
        }
    }

    @Test
    void orWorks() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(8);
            int bit2 = RandomGenerator.randomBit(8);
            boolean z = (bit1 | bit2) == 0;
            assertEquals((((bit1 | bit2) << 8 | (booleanToZeroAndOne(z) << 7)
                    | (0 << 6) | (0 << 5) | (0 << 4))
                    & 0b111111111111111111110000), Alu.or(bit1, bit2));
        }
    }

    @Test
    void xorThrowsException() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = (RandomGenerator.randomBit(8) + 1) | 1 << 8;
            int outOfBoundBit2 = RandomGenerator.randomBit(32) | 1 << 9;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.xor(outOfBoundBit, outOfBoundBit2);
            });
        }
    }

    @Test
    void xorWorks() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(8);
            int bit2 = RandomGenerator.randomBit(8);
            boolean z = (bit1 ^ bit2) == 0;
            assertEquals((((bit1 ^ bit2) << 8 | (booleanToZeroAndOne(z) << 7)
                    | (0 << 6) | (0 << 5) | (0 << 4))
                    & 0b111111111111111111110000), Alu.xor(bit1, bit2));
        }
    }

    @Test
    void shiftLeft() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int outOfBoundBit = (RandomGenerator.randomBit(i % 32) + 1)
                    | 1 << 8;
            assertThrows(IllegalArgumentException.class, () -> {
                Alu.shiftLeft(outOfBoundBit);
            });
        }
    }

    @Test
    void shiftLeftWorks() {
        for (int i = 0; i < RandomGenerator.RANDOM_ITERATIONS; i++) {
            int bit1 = RandomGenerator.randomBit(8);
            int value = (bit1 << 1) & 0xFF;
            boolean c = (bit1 << 1) > 0xFF;
            boolean z = value == 0;
            assertEquals((((value) << 8 | (booleanToZeroAndOne(z) << 7)
                    | (0 << 6) | (0 << 5) | (booleanToZeroAndOne(c) << 4))
                    & 0b111111111111111111110000), Alu.shiftLeft(bit1));
        }
    }

    private boolean zeroAndOneToBoolean(int i) {
        switch (i) {
        case 0:
            return false;
        case 1:
            return true;
        default:
            throw new IllegalArgumentException();
        }
    }

    private int booleanToZeroAndOne(boolean i) {
        if (i)
            return 1;
        else
            return 0;
    }

}
