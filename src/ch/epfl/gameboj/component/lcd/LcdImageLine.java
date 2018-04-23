package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.checkArgument;

import java.util.function.BinaryOperator;

import ch.epfl.gameboj.bits.BitVector;

/**
 * @author Ahmed
 *
 */
public final class LcdImageLine {
    private final BitVector msb;
    private final BitVector lsb;
    private final BitVector opacity;

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
     * @param map
     * @return
     */
    public LcdImageLine mapColors(Byte map) {

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
                below.apply(other.lsb, lsb), this.opacity.or(other.opacity));

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

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

    }

    private boolean checkSize(LcdImageLine that) {
        return size() == that.size();
    }

}
