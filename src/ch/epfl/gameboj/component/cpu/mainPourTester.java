package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.bits.Bits;

public class mainPourTester {

    public static void main(String[] args) {
       System.out.println(0xC+Bits.signExtend8(0xFA));

    }

}
