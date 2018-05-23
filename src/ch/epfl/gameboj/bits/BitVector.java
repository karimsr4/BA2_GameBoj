package ch.epfl.gameboj.bits;

import static ch.epfl.gameboj.Preconditions.*;
import static java.lang.Math.*;
import static java.util.Objects.checkIndex;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntBinaryOperator;

/**
 * Classe représentant un vecteur de bits
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class BitVector {

    private final int[] vector;
    private static final int CELL_SIZE = 32;

    /**
     * construit un nouveau vecteur de bits de taille donnée avec la valeur
     * initiale donnée ( 1 si vrai 0 si faux)
     * 
     * @param size
     *            la taille du vecteur
     * @param initialValue
     *            la valeur initiale des bits
     * @throws IllegalArgumentException
     *             si la taille est négative ou nulle ou si elle n'est pas un
     *             multiple de 32
     */
    public BitVector(int size, boolean initialValue) {
        this(initialisedVector(size, initialValue));
    }

    /**
     * construit un nouveau vecteur de bits de taille donnée en initialisant
     * tous les bits avec 0
     * 
     * @param size
     *            la taille du vecteur
     * @throws IllegalArgumentException
     *             si la taille est négative ou nulle ou si elle n'est pas un
     *             multiple de 32
     */
    public BitVector(int size) {
        this(size, false);
    }

    /**
     * retourne un nouveau vecteur de bits obtenu par la conjonction du vecteur
     * de bits et de l'argument donné
     * 
     * @param that
     *            le vecteur de bits donné
     * @return le vecteur de bits résultant de la conjonction du vecteur de bits
     *         et l'argument
     * @throws IllegalArgumentException
     *             si le vecteur de bits passé en argument n'est pas de même
     *             taille que le vecteur de bits qui appelle la méthode
     */
    public BitVector and(BitVector that) {
        checkArgument(verifySize(that));
        return new BitVector(function(that.vector, (x, y) -> x & y));

    }

    /**
     * extraire un vecteur de taille donnée de l'extension par enroulement du
     * vecteur
     * 
     * @param start
     *            entier répresentant l'index de début de l'extraction
     * @param size
     *            taille du vecteur de bits extrait
     * @return le vecteur de bits extrait
     * @throws IllegalArgumentException
     *             si la taille est négative ,nulle ou n'est pas multiple de 32
     */
    public BitVector extractWrapped(int start, int size) {
        return extract(start, size, ExtractionMethod.WRAPPED);

    }

    /**
     * extraire un vecteur de taille donnée de l'extension par 0 du vecteur
     * 
     * @param start
     *            entier répresentant l'index de début de l'extraction
     * @param size
     *            taille du vecteur de bits extrait
     * @return le vecteur de bits extrait
     * @throws IllegalArgumentException
     *             si la taille est négative ,nulle ou n'est pas multiple de 32
     */
    public BitVector extractZeroExtended(int start, int size) {
        return extract(start, size, ExtractionMethod.ZERO);

    }

    /**
     * calculer le complément du vecteur de bits
     * 
     * @return le complément du vecteur de bits
     */
    public BitVector not() {
        return new BitVector(function(vector, (x, y) -> ~x));
    }

    /**
     * retourne un nouveau vecteur de bits obtenu par la disjonction du vecteur
     * de bits et de l'argument donné
     * 
     * @param that
     *            le vecteur de bits donné
     * @return le vecteur de bits résultant de la disjonction du vecteur de bits
     *         et l'argument
     * @throws IllegalArgumentException
     *             si le vecteur de bits passé en argument n'est pas de même
     *             taille que le vecteur de bits qui appelle la méthode
     */
    public BitVector or(BitVector that) {
        checkArgument(verifySize(that));
        return new BitVector(function(that.vector, (x, y) -> x | y));

    }

    /**
     * décale le vecteur d'une distance quelconque
     * 
     * @param distance
     *            entier representant la distance de décalage
     * @return un nouveau vecteur de bits résultant du décalage
     */
    public BitVector shift(int distance) {
        return extractZeroExtended(-distance, size());

    }

    /**
     * retourne la taille du vecteur de bits
     * 
     * @return la taille du vecteur de bits
     */
    public int size() {
        return vector.length * CELL_SIZE;
    }

    /**
     * déterminer si le bit d'index donné est vrai ou faux
     * 
     * @param index
     *            entier representant l'index
     * @return vrai ssi le bit d'index donné de bits vaut 1
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     */
    public boolean testBit(int index) {
        Objects.checkIndex(index, size());
        return Bits.test(vector[index / CELL_SIZE], index % CELL_SIZE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            st.append(testBit(i) ? 1 : 0);
        }
        return st.reverse().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return Arrays.hashCode(vector);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        return obj instanceof BitVector
                && Arrays.equals(vector, ((BitVector) obj).vector);

    }

    private int[] function(int[] other, IntBinaryOperator a) {
        int[] result = new int[vector.length];
        for (int i = 0; i < vector.length; ++i)
            result[i] = a.applyAsInt(vector[i], other[i]);
        return result;
    }

    private boolean verifySize(BitVector that) {
        return size() == that.size();
    }

    private enum ExtractionMethod {
        ZERO, WRAPPED;
    }

    private BitVector extract(int start, int size, ExtractionMethod method) {
        checkArgument(size > 0 && size % CELL_SIZE == 0);
        int[] extracted = new int[size / CELL_SIZE];
        int shift = floorMod(start, CELL_SIZE);
        if (start % CELL_SIZE == 0) {
            for (int i = 0; i < extracted.length; ++i) {
                extracted[i] = elementExtracting(start + i * CELL_SIZE, method);
            }
        } else {
            for (int i = 0; i < extracted.length; ++i) {
                extracted[i] = elementExtracting(start + i * CELL_SIZE,
                        method) >>> shift
                        | elementExtracting(start + (i + 1) * CELL_SIZE,
                                method) << (CELL_SIZE - shift);
            }

        }

        return new BitVector(extracted);

    }

    private int elementExtracting(int index, ExtractionMethod method) {
        switch (method) {
        case WRAPPED: {
            int cell = floorMod(floorDiv(index, CELL_SIZE), vector.length);
            return vector[cell];
        }
        case ZERO:
            return (index >= size() || index < 0) ? 0
                    : vector[index / CELL_SIZE];
        default:
            throw new Error();
        }

    }

    private BitVector(int[] vector) {
        this.vector = vector;
    }

    private static int[] initialisedVector(int size, boolean initialValue) {

        checkArgument((size > 0) && (size % CELL_SIZE == 0));
        int[] vector = new int[size / CELL_SIZE];
        int fillingValue = initialValue ? 0xFFFFFFFF : 0;
        Arrays.fill(vector, fillingValue);
        return vector;
    }

    /**
     * Classe réprésentant un bâtisseur de vecteur de bits
     * 
     * @author Karim HADIDANE (271018)
     * @author Ahmed JELLOULI (274056)
     */
    public final static class Builder {
        private static final int BYTES_PER_CELL = 4;
        private final int[] vector;
        private final int size;
        private boolean isBuilded;

        /**
         * construit un bâtisseur de vecteur de bits avec la taille donnée
         * 
         * @param size
         *            taille du vecteur à construire
         * @throws IllegalArgumentException
         *             si la taille est négative, nulle ou n'est pas multiple de
         *             32
         */
        public Builder(int size) {
            checkArgument(size > 0 && size % CELL_SIZE == 0);
            vector = new int[size / CELL_SIZE];
            this.size = size;
            isBuilded = false;

        }

        /**
         * définit la valeur d'un octet désigné par son index
         * 
         * @param index
         *            entier répresentant l'index de l'octet
         * @param valeur
         *            entier de 8 bits représentant la nouvelle valeur de
         *            l'octet d'index donné
         * @return ce bâtisseur
         * @throws IllegalStateException
         *             si la méthode est appelée après que le vecteur de bits
         *             est construit
         * @throws IndexOutOfBoundsException
         *             si l'index n'est pas valide
         * @throws IllegalArgumentException
         *             si la valeur n'est pas 8 bits
         */
        public Builder setByte(int index, int valeur) {
            if (isBuilded)
                throw new IllegalStateException();
            checkIndex(index, size / Byte.SIZE);
            checkBits8(valeur);
            int cell = index / BYTES_PER_CELL;
            int byteIndex = index % BYTES_PER_CELL;
            int result = (vector[cell] & ~byteMask(byteIndex))
                    | (valeur << (byteIndex * Byte.SIZE) & byteMask(byteIndex));
            vector[cell] = result;
            return this;

        }

        /**
         * construit le vecteur de bits
         * 
         * @return le vecteur de bits
         * @throws IllegalStateException
         *             si la méthode est appelée après que le vecteur de bits
         *             est construit
         */
        public BitVector build() {
            if (isBuilded) {
                throw new IllegalStateException();
            }
            isBuilded = true;
            return new BitVector(vector);

        }

    }

    private static int byteMask(int byteIndex) {
        return (0xFF) << (byteIndex * Byte.SIZE);
    }

}
