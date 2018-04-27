package ch.epfl.gameboj.component.lcd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.bits.BitVector;

class lcdImageLineTest {
    
    BitVector v0=new BitVector(32, false);
    BitVector v1=new BitVector(32, true);

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
        BitVector v1=new BitVector(32, true);
        LcdImageLine l0=new LcdImageLine(v0, v0, v0);
        LcdImageLine l=l0.mapColors(0b00110110);
        assertEquals(v1, l.getMsb());
        assertEquals(v0, l.getLsb());
        assertEquals(v0, l.getOpacity());
        assertEquals(new LcdImageLine(v1, v0, v0), l);
        
        
    }

    
    
    @Test
    void setByteDoesNotWorkAfterBuild() {
        LcdImageLine.Builder b=new LcdImageLine.Builder(64);
        LcdImageLine l=b.build();
        
        assertThrows(IllegalStateException.class, ()-> b.setByte(0, 0b00000000, 0b00000000));
        
    }
    
    
    
    @Test
    void extractWrappedDoesNotWorkWithWrongLength() {
        LcdImageLine l0=new LcdImageLine(v0, v1, v1);
        assertThrows(IllegalArgumentException.class, () -> l0.extractWrapped(-7, 31));
    }
    
    
    
    @Test
    void extractWrappedDoesIt() {
        LcdImageLine l0=new LcdImageLine(v1, v1, v1);
        BitVector.Builder b=new BitVector.Builder(64).setByte(2, 0b11110011).setByte(4, 0b11111111).setByte(7, 0b11111111);
        
//        BitVector.Builder b2=new BitVector.Builder(64).setByte(3, 0b11111111);
//        BitVector v1=new BitVector(64, true);
//        LcdImageLine l1=new LcdImageLine(b.build(), b2.build(), v1);
        BitVector a = b.build();
        System.out.println(a.toString());
        a=a.extractWrapped(-7, 32);
        System.out.println(a);
        assertEquals("01111001100000000000000001111111", a.toString());
    }
}
