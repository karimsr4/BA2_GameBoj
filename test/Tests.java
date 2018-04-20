import java.util.List;

import ch.epfl.gameboj.bits.BitVector;

public class Tests {

    public static void main(String[] args) {
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractZeroExtended(-17, 32).not();
        BitVector v3 = v2.extractWrapped(11, 64);
        for (BitVector v: List.of(v1, v2, v3))
          System.out.println(v);
    }
    
}
