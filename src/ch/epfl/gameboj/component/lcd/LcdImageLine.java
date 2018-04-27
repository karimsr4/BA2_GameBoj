package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.*;
import static java.util.Objects.checkIndex;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;

import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;

/**
 * @author Ahmed
 *
 */
public final class LcdImageLine {
    private final BitVector msb;
    private final BitVector lsb;
    private final BitVector opacity;
    
    private final int IDENTITY_MAP=0b11100100;

    /**
     * @param msb
     * @param lsb
     * @param opacity
     */
    public LcdImageLine(BitVector msb, BitVector lsb, BitVector opacity) {
        checkArgument(
                (msb.size() == lsb.size()) && (msb.size() == opacity.size()));
        this.msb = msb;
        this.lsb = lsb;
        this.opacity = opacity;

    }

    /**
     * @return
     */
    public int size() {
        return msb.size();
    }

    /**
     * @return
     */
    public BitVector getMsb() {
        return msb;
    }

    /**
     * @return
     */
    public BitVector getLsb() {
        return lsb;
    }

    /**
     * @return
     */
    public BitVector getOpacity() {
        return opacity;
    }

    /**
     * @param distance
     * @return
     */
    public LcdImageLine shift(int distance) {
        return new LcdImageLine(msb.shift(distance), lsb.shift(distance),
                opacity.shift(distance));
    }

    /**
     * @param pixel
     * @param length
     * @return
     */
    public LcdImageLine extractWrapped(int pixel, int length) {
        return new LcdImageLine(msb.extractWrapped(pixel, length),
                lsb.extractWrapped(pixel, length),
                opacity.extractWrapped(pixel, length));
    }

    /**
     * @param j
     * @return
     */

    public LcdImageLine mapColors(int map) {
        if(map==IDENTITY_MAP) {
            return this;
        }
        BitVector couleur_00 = msb.not().and(lsb.not());
        BitVector couleur_01 = msb.not().and(lsb);
        BitVector couleur_10 = msb.and(lsb.not());
        BitVector couleur_11 = msb.and(lsb);

        BitVector newMsb = new BitVector(size(), false);
        BitVector newLsb = new BitVector(size(), false);

        BitVector[] maskArray = new BitVector[] { couleur_00, couleur_01,
                couleur_10, couleur_11 };
        for (int i = 0; i < 8; i++) {
            if (Bits.test(map, i)) {
                if (i % 2 == 0) {
                    newLsb = newLsb.or(maskArray[i / 2]);
                } else {
                    newMsb = newMsb.or(maskArray[i / 2]);
                }

            }

        }

        return new LcdImageLine(newMsb, newLsb, opacity);

    }

    /**
     * @param other
     * @return
     */
    public LcdImageLine below(LcdImageLine other) {
        return below(other, other.opacity);
    }

    /**
     * @param other
     * @param opacity
     * @return
     */
    public LcdImageLine below(LcdImageLine other, BitVector opacity) {
        checkArgument(checkSize(other));
        checkArgument(opacity.size() == size());
        BinaryOperator<BitVector> below = (x, y) -> (opacity.and(x))
                .or(opacity.not().and(y));
        return new LcdImageLine(below.apply(other.msb, msb),
                below.apply(other.lsb, lsb), this.opacity.or(opacity));

    }

    /**
     * @param other
     * @param pixel
     * @return
     */
    public LcdImageLine join(LcdImageLine other, int pixel) {
        checkArgument(checkSize(other));
        checkArgument((pixel >= 0) && (pixel < size()));
        BitVector mask = new BitVector(size(), true).shift(pixel);
        BinaryOperator<BitVector> join = (x, y) -> (x.and(mask.not()))
                .or(y.and(mask));

        return new LcdImageLine(join.apply(msb, other.msb),
                join.apply(lsb, other.lsb), join.apply(opacity, other.opacity));

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        return ((other instanceof LcdImageLine)
                && msb.equals(((LcdImageLine) other).msb)
                && lsb.equals(((LcdImageLine) other).lsb)
                && opacity.equals(((LcdImageLine) other).opacity));

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return Objects.hash(msb, lsb, opacity);

    }

    private boolean checkSize(LcdImageLine that) {
        return size() == that.size();
    }

    /**
     * @author ADMIN
     *
     */
    public final static class Builder {

        private final BitVector.Builder b1;
        private final BitVector.Builder b2;
        private boolean isBuilded;
        private final int size;

        Builder(int size) {
            checkArgument(size > 0 && size % 32 == 0);
            b1 = new BitVector.Builder(size);
            b2 = new BitVector.Builder(size);
            this.size = size;
            isBuilded = false;

        }

        public Builder setByte(int index, int msbByte, int lsbByte) {
            checkBits8(msbByte);
            checkBits8(lsbByte);
            if (isBuilded)
                throw new IllegalStateException();

            checkIndex(index, size / 8);
            b1.setByte(index, msbByte);
            b2.setByte(index, lsbByte);

            return this;
        }

        public LcdImageLine build() {
            if (isBuilded) {
                throw new IllegalStateException();
            }
            isBuilded = true;
            BitVector msb = b1.build();
            BitVector lsb = b2.build();

            return new LcdImageLine(msb, lsb, msb.or(lsb));
        }

    }

}
