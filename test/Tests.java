import java.util.List;

import ch.epfl.gameboj.bits.BitVector;

public class Tests {

    public static void main(String[] args) {
       
        BitVector v1 = new BitVector(32, false);
       BitVector v2=v1.not();
       System.out.println(v1);
       System.out.println(v2);
       BitVector v3=v2.shift(-4);
       System.out.println(v3);
       BitVector v4=v2.or(v3);
       System.out.println(v4);
       System.out.println(Math.floorMod(-3, 32));
//        BitVector v1 = new BitVector(32, true);
//        BitVector v2 = v1.extractZeroExtended(-17, 32).not();
//        BitVector v3 = v2.extractWrapped(11, 64);
//        for (BitVector v: List.of(v1, v2, v3))
//          System.out.println(v);
    }
    
}
