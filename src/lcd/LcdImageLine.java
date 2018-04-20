package lcd;

import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.BitVector;
import static ch.epfl.gameboj.Preconditions.*;

/**
 * @author Ahmed
 *
 */
public final class LcdImageLine {
    private BitVector msb;
    private BitVector lsb;
    private BitVector opacity;

    /**
     * @param msb
     * @param lsb
     * @param opacity
     */
    public LcdImageLine(BitVector msb, BitVector lsb, BitVector opacity) {
        checkArgument(
                checkSize(msb, lsb) && checkSize(msb,opacity));
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

    }

    /**
     * @param other
     * @param opacity
     * @return
     */
    public LcdImageLine below(LcdImageLine other, BitVector opacity) {
        return 

    }

    /**
     * @param other
     * @param pixel
     * @return
     */
    public LcdImageLine join(LcdImageLine other, int pixel) {

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
    private boolean checkSize (BitVector first, BitVector second) {
        return first.size()==second.size();
    }

}
