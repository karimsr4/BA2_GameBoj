package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.*;
import static java.util.Objects.checkIndex;

import java.util.Objects;
import java.util.function.BinaryOperator;

import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;

/**
 * Classe représentant une ligne d'image Game Boy
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class LcdImageLine {
    private final static int IDENTITY_MAP = 0b11100100;

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
     * obtenir la longueur, en pixels, de la ligne
     * 
     * @return la longueur de la ligne en pixels
     */
    public int size() {
        return msb.size();
    }

    /**
     * obtenir le vecteur de bits de poids fort
     * 
     * @return le vecteur de bits de poids fort
     */
    public BitVector getMsb() {
        return msb;
    }

    /**
     * obtenir le vecteur de bits de poids faible
     * 
     * @return le vecteur de bits de poids faible
     */
    public BitVector getLsb() {
        return lsb;
    }

    /**
     * obtenir le vecteur de bits de l'opacité
     * 
     * @return le vecteur de bits de l'opacité
     */
    public BitVector getOpacity() {
        return opacity;
    }

    /**
     * décaler la ligne d'un nombre de pixels donné, en préservant sa longueur
     * 
     * @param distance
     *            la distance de décalage
     * @return une nouvelle ligne résultat du décalage
     */
    public LcdImageLine shift(int distance) {
        return new LcdImageLine(msb.shift(distance), lsb.shift(distance),
                opacity.shift(distance));
    }

    /**
     * extraire de l'extension infinie par enroulement, à partir d'un pixel
     * donné, une ligne de longueur donnée
     * 
     * @param pixel
     * @param length
     * @return l'extension infinie par enroulement de la ligne courante
     */
    public LcdImageLine extractWrapped(int pixel, int length) {
        return new LcdImageLine(msb.extractWrapped(pixel, length),
                lsb.extractWrapped(pixel, length),
                opacity.extractWrapped(pixel, length));
    }

    /**
     * transformer les couleurs de la ligne en fonction d'une palette donnée
     * 
     * @param map
     *            la palette sous la forme d'un octet
     * @return ligne avec les couleurs transformées
     * @throws IllegalArgumentException
     *             si la palette n'est pas un entier de 8 bits
     */

    public LcdImageLine mapColors(int map) {
        checkBits8(map);
        if (map == IDENTITY_MAP) {
            return this;
        }

        BitVector newMsb = new BitVector(size(), false);
        BitVector newLsb = new BitVector(size(), false);

        for (int i = 0; i < 4; ++i) {
            BitVector mask = (i % 2 == 0 ? lsb.not() : lsb)
                    .and(i < 2 ? msb.not() : msb);
            if (Bits.test(map, i * 2))
                newLsb = newLsb.or(mask);
            if (Bits.test(map, i * 2 + 1))
                newMsb = newMsb.or(mask);
        }

        return new LcdImageLine(newMsb, newLsb, opacity);

    }

    /**
     * composer la ligne avec une seconde de même longueur placée au dessus
     * d'elle en utilisant l'opacité de la ligne supérieure pour effectuer la
     * composition
     * 
     * @param other
     *            la seconde ligne
     * @return la ligne résultante de la composition des deux lignes
     * @throws IllegalArgumentException
     *             si les deux lignes n'ont pas la même taille
     */
    public LcdImageLine below(LcdImageLine other) {
        return below(other, other.opacity);
    }

    /**
     * composer la ligne avec une seconde de même longueur en utilisant le
     * vecteur d'opacité passé en argument
     * 
     * @param other
     *            la seconde ligne
     * @param opacity
     *            le vecteur d'opacité
     * @return la ligne résultante de la composition des deux lignes
     * @throws IllegalArgumentException
     *             si les deux lignes n'ont pas la même taille ou si le vecteur
     *             d'opacité et la ligne n'ont pas la même taille
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
     * joint la ligne avec une autre de même longueur, à partir d'un pixel
     * d'index donné
     * 
     * @param other
     *            la seconde ligne
     * @param pixel
     *            l'index du pixel
     * @return la ligne résultante de la composition des deux lignes
     * @throws IllegalArgumentException
     *             si les deux lignes n'ont pas la même taille
     * @throws IndexOutOfBoundsException
     *             si l'index du pixel n'est pas valide
     *
     */
    public LcdImageLine join(LcdImageLine other, int pixel) {
        checkArgument(checkSize(other));
        checkIndex(pixel, size());
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
     * Classe représentant un bâtisseur de ligne d'image Game Boy
     * 
     * @author Karim HADIDANE (271018)
     * @author Ahmed JELLOULI (274056)
     */
    public final static class Builder {

        private final BitVector.Builder msbBuilder;
        private final BitVector.Builder lsbBuilder;
        private boolean isBuilded;

        /**
         * construit un bâtisseur de ligne d'image avec la taille donnée
         * 
         * @param size
         *            taille de la ligne à construire
         * @throws IllegalArgumentException
         *             si la taille est négative, nulle ou n'est pas un multiple
         *             de 32
         */
        public Builder(int size) {
            msbBuilder = new BitVector.Builder(size);
            lsbBuilder = new BitVector.Builder(size);
            isBuilded = false;

        }

        /**
         * définit la valeur des octets de poids fort et de poids faible de la
         * ligne, à un index donné
         * 
         * @param index
         *            l'index de l'octet a définir
         * @param msbByte
         *            la valeur de l'octet du vecteur de bits de poids fort
         * @param lsbByte
         *            la valeur de l'octet du vecteur de bits de poids faible
         * @return ce bâtisseur de ligne d'image
         * @throws IllegalArgumentException
         *             si les valeurs ne sont pas des entiers 8 bits ou si
         *             l'index n'est pas valide
         * @throws IllegalStateException
         *             si la méthode est appelée après que la ligne
         *             est construite
         */
        public Builder setByte(int index, int msbByte, int lsbByte) {
            if (isBuilded)
                throw new IllegalStateException();

            msbBuilder.setByte(index, msbByte);
            lsbBuilder.setByte(index, lsbByte);
            return this;
        }

        /**
         * construit la ligne d'image
         * 
         * @return la ligne d'image
         * @throws IllegalStateException
         *             si la méthode est appelée après que la ligne
         *             est construite
         */
        public LcdImageLine build() {

            if (isBuilded) {
                throw new IllegalStateException();
            }
            isBuilded = true;

            BitVector msb = msbBuilder.build();
            BitVector lsb = lsbBuilder.build();

            return new LcdImageLine(msb, lsb, msb.or(lsb));
        }

    }

}
