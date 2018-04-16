package ch.epfl.gameboj.bits;

import static ch.epfl.gameboj.Preconditions.*;

import java.util.Arrays;

import static java.util.Objects.*;

import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * @author Ahmed
 *
 */
public final class BitVector {

    private final int[] vector;
    private static final int CELL_SIZE=32;

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

        checkArgument((size >= 0) && (size % CELL_SIZE == 0));
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
        BinaryOperator<Integer> and = (x, y) -> x & y;
        return new BitVector(function(vector, and));

    }


    @Override
    public boolean equals(Object obj) {

        return obj instanceof BitVector
                && Arrays.equals(vector, ((BitVector) obj).vector);

    }

    /**
     * @param start
     * @param end
     * @return
     */


    // public BitVector not() {
    // int[] copy = new int[vector.length]; ////////!!!!
    // for(int i=0; i< copy.length;i++)
    // copy[i]=~vector[i];
    // //copy[i]= vector[i]^ 0xFFFFFFFF;
    // return new BitVector(copy);
    // }

    public BitVector extractWrapped(int start, int size) {

        return extract(start, size, ExtractionMethod.WRAPPED);


    }

    /**
     * @param start
     * @param end
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
        return Bits.test(vector[index / CELL_SIZE], index % CELL_SIZE);
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


    private BitVector extract(int start, int size, ExtractionMethod method) {

        int[] extracted = new int[size / CELL_SIZE];
        int div = Math.floorDiv(start, CELL_SIZE);
        int reste = Math.floorMod(start, CELL_SIZE);

        if (reste == 0) {
            for (int i = 0; i < size; i++) {
                extracted[i] = elementExtracting(i, method);
            }
        } else {

        }

        return new BitVector(extracted);

    }


    private int elementExtracting(int index, ExtractionMethod method) {
        if (method == ExtractionMethod.WRAPPED) {
            return vector[Math.floorMod(index, vector.length)];
        } else {
            if (index >= vector.length || index < 0)
                return 0;
            return vector[index];
        }
    }

    
    public final static class Builder{
        
        Builder(int size){
            checkArgument(size>0 && size%32==0);
        }
        
        
        public Builder setBytes(int index, int valeur) {
            return null;
            
        }
        
        
        public BitVector build() {
            return null;
            
        }
        
    }
    
}
