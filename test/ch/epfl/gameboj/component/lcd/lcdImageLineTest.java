package ch.epfl.gameboj.component.lcd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.bits.BitVector;

class lcdImageLineTest {

    @Test
    void constructorDoesNotWorkWithDifferentSizestest() {
        BitVector msb = new BitVector.Builder(64).setByte(1, 0b11011001)
                .setByte(4, 0b11111111).build();
        // System.out.println(msb);
        BitVector lsb = new BitVector.Builder(32).build();
        BitVector opacity = new BitVector.Builder(32).build();
        assertThrows(IllegalArgumentException.class,
                () -> new LcdImageLine(msb, lsb, opacity));

    }

    @Test
    void equalstest() {
        BitVector msb = new BitVector.Builder(64).setByte(1, 0b11011001)
                .setByte(4, 0b11111111).build();
        BitVector lsb = new BitVector.Builder(64).build();
        BitVector opacity = new BitVector.Builder(64).setByte(2, 0b11110101)
                .build();
        LcdImageLine l1 = new LcdImageLine(msb, lsb, opacity);
        LcdImageLine l2 = new LcdImageLine(
                new BitVector.Builder(64).setByte(4, 0b11111111)
                        .setByte(1, 0b11011001).build(),
                new BitVector(64, false),
                new BitVector.Builder(64).setByte(2, 0b11110101).build());

        assertEquals(true, l1.equals(l2));
    }

    @Test
    void hashCodeTest() {

        BitVector msb = new BitVector.Builder(64).setByte(1, 0b11011001)
                .setByte(4, 0b11111111).build();
        BitVector lsb = new BitVector.Builder(64).build();
        BitVector opacity = new BitVector.Builder(64).setByte(2, 0b11110101)
                .build();
        LcdImageLine l1 = new LcdImageLine(msb, lsb, opacity);
        LcdImageLine l2 = new LcdImageLine(
                new BitVector.Builder(64).setByte(4, 0b11111111)
                        .setByte(1, 0b11011001).build(),
                new BitVector(64, false),
                new BitVector.Builder(64).setByte(2, 0b11110101).build());

        // System.out.println(l1.hashCode());
        // System.out.println(l2.hashCode());
        assertEquals(l1.hashCode(), l2.hashCode());

    }

    @Test
    void belowTest() {
        BitVector msb1 = new BitVector.Builder(64).setByte(1, 0b11011001)
                .setByte(4, 0b11111111).build();
        BitVector lsb1 = new BitVector.Builder(64).build();
        BitVector opacity1 = new BitVector.Builder(64).setByte(2, 0b11110101)
                .build();
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity1);

        BitVector msb2 = new BitVector.Builder(64).setByte(1, 0b11111111)
                .setByte(4, 0b11111111).setByte(7, 0b10011100).build();
        BitVector lsb2 = new BitVector.Builder(64).setByte(2, 0b11001100)
                .build();
        BitVector opacity2 = new BitVector(64, true);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity2);
        BitVector opacity3 = new BitVector(64, false);
        LcdImageLine l3 = new LcdImageLine(msb2, lsb2, opacity3);

//        System.out.println(l1.getMsb());
//        System.out.println(l3.getMsb());
//        System.out.println(l3.getOpacity());
//        System.out.println(l1.below(l3).getMsb());
        assertEquals(l2, l1.below(l2));
        // assertEquals(l1.getMsb(), l1.below(l3).getMsb());
        assertEquals(l1, l1.below(l3));
        

    }
    
    
    
    @Test 
    void joinTest() {
        
        
        BitVector msb1 = new BitVector.Builder(64).setByte(1, 0b11011001)
                .setByte(4, 0b11111111).build();
        BitVector lsb1 = new BitVector.Builder(64).build();
        BitVector opacity1 = new BitVector.Builder(64).setByte(2, 0b11110101)
                .build();
        System.out.println(msb1);
        System.out.println(lsb1);
        System.out.println(opacity1);
        LcdImageLine l1 = new LcdImageLine(msb1, lsb1, opacity1);

        BitVector msb2 = new BitVector.Builder(64).setByte(1, 0b11111111)
                .setByte(4, 0b11111111).setByte(7, 0b10011100).build();
        BitVector lsb2 = new BitVector.Builder(64).setByte(2, 0b11001100)
                .build();
        BitVector opacity2 = new BitVector(64, true);
        System.out.println();
        System.out.println(msb2);
        System.out.println(lsb2);
        System.out.println(opacity2);
        LcdImageLine l2 = new LcdImageLine(msb2, lsb2, opacity2);
        
        BitVector msb3 = new BitVector.Builder(64).setByte(1, 0b11111001)
                .setByte(4, 0b11111111).setByte(7, 0b10011100).build();
        BitVector lsb3 = new BitVector.Builder(64).setByte(2, 0b11001100)
                .build();
        BitVector opacity3 = new BitVector.Builder(64).build().not().shift(12);
        
        LcdImageLine l3 = new LcdImageLine(msb3, lsb3, opacity3);
        
        assertEquals(l3, l1.join(l2, 12));
        
    }
    
    
    @Test
    void mapColorTest(){
        
        BitVector v0=new BitVector(32, false);
        LcdImageLine l0=new LcdImageLine(v0, v0, v0);
        LcdImageLine l=l0.mapColors(0b00110110);
        System.out.println(l.getLsb());
        
        
    }

}
