package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;


public class Main {
    
    public static void main(String[] args) {
        BitVector b=new BitVector(32,true);
        System.out.println(b.shift(-3));
    }

}
