package ch.epfl.gameboj.bits;

import static ch.epfl.gameboj.Preconditions.*;

import java.util.Arrays;

import static java.util.Objects.*;
<<<<<<< HEAD
=======

>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * @author Ahmed
 *
 */
public final class BitVector {

    private final int[] vector;

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

        checkArgument((size >= 0) && (size % 32 == 0));
        int[] tab = new int[size / 32];
        int fillingValue = initialValue ? 0xFFFFFFFF : 0;
        Arrays.fill(tab, fillingValue);
        return tab;
    }

    /**
     * @param that
     * @return
     */
    public BitVector and(BitVector that) {
        checkArgument(verifySize(that));
        BinaryOperator<Integer> and = (x, y) -> x & y;
        return new BitVector(function(vector, and));

    }

<<<<<<< HEAD
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

=======
>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
    @Override
    public boolean equals(Object obj) {

        return obj instanceof BitVector
                && Arrays.equals(vector, ((BitVector) obj).vector);
<<<<<<< HEAD

=======
>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
    }

    /**
     * @param start
     * @param end
     * @return
     */
<<<<<<< HEAD
    public BitVector extractWrapped(int start, int end) {
=======

    // public BitVector not() {
    // int[] copy = new int[vector.length]; ////////!!!!
    // for(int i=0; i< copy.length;i++)
    // copy[i]=~vector[i];
    // //copy[i]= vector[i]^ 0xFFFFFFFF;
    // return new BitVector(copy);
    // }

    public BitVector extractWrapped(int start, int size) {
>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
        return null;

    }

    /**
     * @param start
     * @param end
     */
    public BitVector extractZeroExtended(int start, int size) {
        return null;

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
        BinaryOperator<Integer> not = (x, y) -> ~x;
        return new BitVector(function(vector, not));
    }

    /**
     * @param that
     * @return
     */
    public BitVector or(BitVector that) {
        checkArgument(verifySize(that));
        BinaryOperator<Integer> or = (x, y) -> x | y;
        return new BitVector(function(vector, or));

    }

    /**
     * @param distance
     * @return
     */
    public BitVector shift(int distance) {
        return extractZeroExtended(-distance, size());

    }

    /**
     *   @return
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
        return Bits.test(vector[index / 32], index % 32);
    }

    private int[] function(int[] other, BinaryOperator<Integer> a) {
        int[] result = new int[vector.length];
        for (int i = 0; i < vector.length; ++i)
            result[i] = a.apply(vector[i], other[i]);
        return result;
    }

    private boolean verifySize(BitVector that) {
        return size() == that.size();
    }

    private enum ExtractionMethod {
        ZERO, WRAPPED;
    }

<<<<<<< HEAD
    private BitVector extract(int start, int size, boolean byWinding) {
=======
    private BitVector extract(int start, int size, ExtractionMethod method) {
>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
        int[] extracted = new int[size / 32];
        int div = Math.floorDiv(start, 32);
        int reste = Math.floorMod(start, 32);
        if (reste == 0) {
            for (int i = 0; i < size; i++) {
<<<<<<< HEAD
                extracted[i] = elementExtracting(i, byWinding);
=======
                extracted[i] = elementExtracting(i, method);
>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
            }
        } else {

        }

        return new BitVector(extracted);

    }

<<<<<<< HEAD
    private int elementExtracting(int index, boolean byWinding) {
        if (byWinding) {
=======
    private int elementExtracting(int index, ExtractionMethod method) {
        if (method == ExtractionMethod.WRAPPED) {
>>>>>>> e206596d6191d8012db244979008c2103f7b3e78
            return Math.floorMod(index, vector.length);
        } else {
            if (index >= vector.length || index < 0)
                return 0;
            return vector[index];
        }
    }

}
