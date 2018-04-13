package ch.epfl.gameboj.bits;

import static ch.epfl.gameboj.Preconditions.*;

import java.util.Arrays;

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
        checkArgument((size > 0) && (size % 32 == 0));
        vector = new int[size / 32];
        int fillingValue = initialValue ? 0xFFFFFFFF : 0;
        Arrays.fill(vector, fillingValue);
    }
    /**
     * @param size
     */
    public BitVector(int size) {
        this(size,false);
    }
    

}
