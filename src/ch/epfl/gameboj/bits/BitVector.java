package ch.epfl.gameboj.bits;

import static ch.epfl.gameboj.Preconditions.checkArgument;
import static java.lang.Math.*;
import static java.util.Objects.checkIndex;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntBinaryOperator;

/**
 * @author Ahmed
 *
 */
public final class BitVector {

    private final int[] vector;
    private static final int CELL_SIZE = 32;

    /**
     * @param size
     * @param initialValue
     */
    public BitVector(int size, boolean initialValue) {
        this(initialisedVector(size, initialValue));
    }

    /**
     * @param size
     */
    public BitVector(int size) {
        this(size, false);
    }

    private BitVector(int[] vector) {
        this.vector = vector;
    }

    private static int[] initialisedVector(int size, boolean initialValue) {

        checkArgument((size > 0) && (size % CELL_SIZE == 0));
        int[] tab = new int[size / CELL_SIZE];
        int fillingValue = initialValue ? Integer.MAX_VALUE : 0;
        Arrays.fill(tab, fillingValue);
        return tab;
    }

    /**
     * @param that
     * @return
     */
    public BitVector and(BitVector that) {
        checkArgument(verifySize(that));
        return new BitVector(function(vector, (x, y) -> x & y));

    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof BitVector
                && Arrays.equals(vector, ((BitVector) obj).vector);

    }


    /**
     * @param start
     * @param size
     * @return
     */
    public BitVector extractWrapped(int start, int size) {
        return extract(start, size, ExtractionMethod.WRAPPED);

    }

    /**
     * @param start
     * @param size
     * @return
     */
    public BitVector extractZeroExtended(int start, int size) {
        return extract(start, size, ExtractionMethod.ZERO);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return Arrays.hashCode(vector);
    }

    /**
     * @return
     */
    public BitVector not() {
        return new BitVector(function(vector, (x, y) -> ~x));
    }

    /**
     * @param that
     * @return
     */
    public BitVector or(BitVector that) {
        checkArgument(verifySize(that));
        return new BitVector(function(vector, (x, y) -> x | y));

    }

    /**
     * @param distance
     * @return
     */
    public BitVector shift(int distance) {
        return extractZeroExtended(-distance, size());

    }

    /**
     * @return
     */
    public int size() {
        return vector.length * 32;
    }

    /**
     * @param index
     * @return
     */
    public boolean testBit(int index) {
        Objects.checkIndex(index, size());
        return Bits.test(vector[index / CELL_SIZE], index % CELL_SIZE);
    }

    public String toString() {
        String st = "";
        for (int i = 0; i < vector.length; i++) {
            st = Integer.toBinaryString(vector[i]) + st;
        }
        return st;
    }

    private int[] function(int[] other, IntBinaryOperator a) {
        int[] result = new int[vector.length];
        for (int i = 0; i < vector.length; ++i)
            result[i] = a.applyAsInt(vector[i], other[i]);
        return result;
    }

    private boolean verifySize(BitVector that) {
        return size() == that.size();
    }

    private enum ExtractionMethod {
        ZERO, WRAPPED;
    }

    private BitVector extract(int start, int size, ExtractionMethod method) {
        checkArgument(size > 0 && size % 32 == 0);
        int[] extracted = new int[size / CELL_SIZE];
        int shift = floorMod(start, CELL_SIZE);
        if (start % 32 == 0) {
            for (int i = 0; i < extracted.length; ++i) {
                extracted[i] = elementExtracting(start + i * CELL_SIZE, method);
            }
        } else {
            for (int i = 0; i < extracted.length; ++i) {
                extracted[i] = elementExtracting(start + i * CELL_SIZE,
                        method) >>> shift
                        | elementExtracting(start + (i + 1) * CELL_SIZE,
                                method) << (CELL_SIZE - shift);
            }

        }

        return new BitVector(extracted);

    }

    private int elementExtracting(int index, ExtractionMethod method) {
        switch (method) {
        case WRAPPED:
            return vector[floorMod(index, vector.length)];
        default:
            return (index >= size() || index < 0) ? 0
                    : vector[floorDiv(index, CELL_SIZE)];
        }
    }

    public final static class Builder {
        private int[] vector;
        private int size;

        public Builder(int size) {
            checkArgument(size > 0 && size % 32 == 0);
            vector=new int[size/32];
            this.size=size;

        }

        public Builder setBytes(int index, int valeur) {
            checkIndex(index, size/8 );
          int cas=index /4;
          int mod=index%4;
          int pow=Math.round((float)Math.pow(8, mod));
          if (pow!=1)
              valeur=valeur<<pow;
          
                 
          
          return this;

        }

        public BitVector build() {
            return new BitVector(vector);

        }

    }

}
