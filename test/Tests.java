import java.util.List;

import ch.epfl.gameboj.bits.BitVector;

public class Tests {

    public static void main(String[] args) {
//       
//        BitVector v1 = new BitVector(32, false);
//       BitVector v2=v1.not();
//       System.out.println(v1);
//       System.out.println(v2);
//       BitVector v3=v2.shift(4);
//      System.out.println(v3);
//       BitVector v4=v2.or(v3);
//       System.out.println(v4);
//       System.out.println(Math.floorMod(-3, 32));
//        BitVector v1 = new BitVector(32, true);
//        BitVector v2 = v1.extractZeroExtended(-17, 32).not();
//        BitVector v3 = v2.extractWrapped(11, 64);
//        for (BitVector v: List.of(v1, v2, v3))
//          System.out.println(v);
        
//        BitVector v = new BitVector.Builder(32)
//                .setByte(0, 0b1111_0000)
//                .setByte(1, 0b1010_1010)
//                .setByte(3, 0b1100_1100)
//                .build();
//              System.out.println(v);
//              
//              System.out.println(Integer.toBinaryString((1<<8)-1));
        BitVector v1 = new BitVector(32, true);
        BitVector v2 = v1.extractZeroExtended(-17, 32).not();
        BitVector v3 = v2.extractWrapped(11, 64);
        BitVector v4 = v3.extractWrapped(33, 96);
        BitVector v5 = v4.extractWrapped(32, 64);
        BitVector v6 = v5.extractWrapped(-32, 32);
        for (BitVector v: List.of(v1, v2, v3,v4,v5,v6))
        {
                    System.out.println(v);
        }
        BitVector v = new BitVector.Builder(32).setByte(0, 0b1111_0000).setByte(1, 0b1010_1010)
                                  .setByte(3, 0b1100_1100)
                                  .build();
                                System.out.println(v);

        
    }
    
}
