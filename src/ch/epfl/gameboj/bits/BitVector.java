package ch.epfl.gameboj.bits;

import static ch.epfl.gameboj.Preconditions.*;

import java.util.Arrays;
import java.util.Objects;

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

    private static int[] initialisedVector(int size, boolean initialValue) {

        checkArgument((size >= 0) && (size % 32 == 0));
        int[] tab = new int[size / 32];
        int fillingValue = initialValue ? 0xFFFFFFFF : 0;
        Arrays.fill(tab, fillingValue);
        return tab;
    }

    private BitVector(int[] vector) {
        this.vector = vector;
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
        return Bits.test(vector[index/32], index % 32);     
    }
    
    /**
     * @return
     */
    public BitVector not() {
        int[] copy = new int[vector.length]; ////////!!!!
        for(int i=0; i< copy.length;i++)
            copy[i]= vector[i]^ 0xFFFFFFFF;
        return new BitVector(copy);
    }
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    /**
     * @param that
     * @return
     */
    public BitVector and (BitVector that) {
        return that;
        
    }
    /**
     * @param that
     * @return
     */
    public BitVector or (BitVector that) {
        return null;
        
    }
    /**
     * @param distance
     * @return
     */
    public BitVector shift(int distance) {
        return null;
        
    }
    /**
     * @param start
     * @param end
     * @return
     */
    public BitVector extractWrapped(int start,int end) {
        return null;
        
    }
    /**
     * @param start
     * @param end
     */
    public BitVector extractZeroExtended(int start, int end) {
        return null;
        
    }
    
    
    
    
    

}
