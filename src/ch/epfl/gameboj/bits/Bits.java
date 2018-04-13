package ch.epfl.gameboj.bits;

import static java.util.Objects.*;

import static ch.epfl.gameboj.Preconditions.*;

/**
 * Classe contenant des méthodes pour manipuler des bits
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Bits {

    private Bits() {
    }

    /**
     * retourne un entier dont seul le bit d'index donné vaut 1
     * 
     * @param index
     *            entier representant l'index
     * @return entier dont seul le bit d'index donné vaut 1
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     */
    public static int mask(int index) {
        checkIndex(index, Integer.SIZE);
        return 1 << index;

    }

    /**
     * retourne vrai ssi le bit d'index donné de bits vaut 1
     * 
     * @param bits
     *            entier à tester
     * @param index
     *            entier representant l'index
     * @return vrai ssi le bit d'index donné de bits vaut 1
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     */
    public static boolean test(int bits, int index) {
        checkIndex(index, Integer.SIZE);
        bits = bits & mask(index);
        return (bits == mask(index));

    }

    /**
     * retourne vrai ssi le bit d'index donné de bits vaut 1
     * 
     * @param bits
     *            entier à tester
     * @param bit
     *            entier dont l'index sera utilisé
     * @return vrai ssi le bit d'index donné de bits vaut 1
     * 
     */
    public static boolean test(int bits, Bit bit) {
        return test(bits, bit.index());
    }

    /**
     * retourne une valeur dont tous les bits sont égaux à ceux de bits, sauf
     * celui d'index donné, qui est égal à newValue
     * 
     * @param bits
     *            entier à tester
     * @param index
     *            entier representant l'index
     * @param newValue
     *            booleen representant la nouvelle valeur
     * @return une valeur dont tous les bits sont égaux à ceux de bits, sauf
     *         celui d'index donné, qui est égal à newValue
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     */
    public static int set(int bits, int index, boolean newValue) {

        checkIndex(index, Integer.SIZE);

        return (newValue) ? bits | mask(index) : bits & ~mask(index);
    }

    /**
     * retourne une valeur dont les size bits de poids faible sont égaux à ceux
     * de bits, les autres valant 0
     * 
     * @param size
     *            entier :taille de bits
     * @param bits
     *            entier donné
     * @return une valeur dont les size bits de poids faible sont égaux à ceux
     *         de bits, les autres valant 0
     * @throws IllegalArgumentException
     *             si le size est invalide
     * 
     */
    public static int clip(int size, int bits) {

        checkArgument(size >= 0 && size <= 32);
        return size == 32 ? bits : bits & ((1 << size) - 1);

    }

    /**
     * retourne une valeur dont les size bits de poids faible sont égaux à ceux
     * de bits allant de l'index start (inclus) à l'index start + size (exclus)
     * 
     * @param bits
     *            entier donné
     * @param start
     *            entier :index de début
     * @param size
     *            entier: taille
     * @return une valeur dont les size bits de poids faible sont égaux à ceux
     *         de bits allant de l'index start (inclus) à l'index start + size
     *         (exclus)
     * @throws IndexOutOfBoundsException
     *             si start et size ne délimitent pas une plage de bits valide
     * 
     * 
     */
    public static int extract(int bits, int start, int size) {
        checkFromIndexSize(start, size, 32);
        bits = bits >> start;
        return clip(size, bits);

    }

    /**
     * retourne une valeur dont les size bits de poids faible sont ceux de la
     * valeur donnée mais auxquels une rotation a été appliquée
     * 
     * @param size
     *            nombre de bits qui vont subir unr rotation
     * 
     * @param bits
     *            entier donné
     * @param distance
     *            donne la direction ( vers la gauche si positif, vers la droite
     *            si négatif) et la distance de la rotation
     * @throws IllegalArgumentException
     *             si size n'est pas compris entre 0(exclus) et 32 (inclus) ou
     *             si la valeur donnée n'est pas une valeur de size bits
     * @return une valeur dont les size bits de poids faible ont dubi une
     *         rotation
     */
    public static int rotate(int size, int bits, int distance) {
        checkArgument(size > 0 && size <= 32);
        checkArgument(bits < Math.pow(2, size));

        int a = Math.floorMod(distance, size);

        a = (clip(size, bits << a)) | (bits >>> (size - a));
        return a;
    }

    /**
     * copie le bit d'index 7 dans les bits d'index 8 à 31 et retourne cet
     * entier
     * 
     * @param b
     *            entier a traiter
     * @return entier dont les bits d'indexes 8 à 31 ont la même valeur que le
     *         bit d'index 7 de l'entier donné
     * @throws IllegalArgumentException
     *             si la valeur donnée n'est pas une valeur 8 bits
     */
    public static int signExtend8(int b) {
        checkBits8(b);
        b = (byte) b;
        return (int) b;

    }

    /**
     * retourne une valeur égale à celle donnée, à l'exception des 8 bits de
     * poids faible qui ont été renversés
     * 
     * @param b
     *            valeur donnée
     * @return une valeur égale à celle donnée, à l'exception des 8 bits de
     *         poids faible qui ont été renversés
     * @throws IllegalArgument
     *             Exception si la valeur donnée n'est pas une valeur 8 bits
     */
    public static int reverse8(int b) {
        checkBits8(b);
        int[] allInverses = new int[] { 0x00, 0x80, 0x40, 0xC0, 0x20, 0xA0,
                0x60, 0xE0, 0x10, 0x90, 0x50, 0xD0, 0x30, 0xB0, 0x70, 0xF0,
                0x08, 0x88, 0x48, 0xC8, 0x28, 0xA8, 0x68, 0xE8, 0x18, 0x98,
                0x58, 0xD8, 0x38, 0xB8, 0x78, 0xF8, 0x04, 0x84, 0x44, 0xC4,
                0x24, 0xA4, 0x64, 0xE4, 0x14, 0x94, 0x54, 0xD4, 0x34, 0xB4,
                0x74, 0xF4, 0x0C, 0x8C, 0x4C, 0xCC, 0x2C, 0xAC, 0x6C, 0xEC,
                0x1C, 0x9C, 0x5C, 0xDC, 0x3C, 0xBC, 0x7C, 0xFC, 0x02, 0x82,
                0x42, 0xC2, 0x22, 0xA2, 0x62, 0xE2, 0x12, 0x92, 0x52, 0xD2,
                0x32, 0xB2, 0x72, 0xF2, 0x0A, 0x8A, 0x4A, 0xCA, 0x2A, 0xAA,
                0x6A, 0xEA, 0x1A, 0x9A, 0x5A, 0xDA, 0x3A, 0xBA, 0x7A, 0xFA,
                0x06, 0x86, 0x46, 0xC6, 0x26, 0xA6, 0x66, 0xE6, 0x16, 0x96,
                0x56, 0xD6, 0x36, 0xB6, 0x76, 0xF6, 0x0E, 0x8E, 0x4E, 0xCE,
                0x2E, 0xAE, 0x6E, 0xEE, 0x1E, 0x9E, 0x5E, 0xDE, 0x3E, 0xBE,
                0x7E, 0xFE, 0x01, 0x81, 0x41, 0xC1, 0x21, 0xA1, 0x61, 0xE1,
                0x11, 0x91, 0x51, 0xD1, 0x31, 0xB1, 0x71, 0xF1, 0x09, 0x89,
                0x49, 0xC9, 0x29, 0xA9, 0x69, 0xE9, 0x19, 0x99, 0x59, 0xD9,
                0x39, 0xB9, 0x79, 0xF9, 0x05, 0x85, 0x45, 0xC5, 0x25, 0xA5,
                0x65, 0xE5, 0x15, 0x95, 0x55, 0xD5, 0x35, 0xB5, 0x75, 0xF5,
                0x0D, 0x8D, 0x4D, 0xCD, 0x2D, 0xAD, 0x6D, 0xED, 0x1D, 0x9D,
                0x5D, 0xDD, 0x3D, 0xBD, 0x7D, 0xFD, 0x03, 0x83, 0x43, 0xC3,
                0x23, 0xA3, 0x63, 0xE3, 0x13, 0x93, 0x53, 0xD3, 0x33, 0xB3,
                0x73, 0xF3, 0x0B, 0x8B, 0x4B, 0xCB, 0x2B, 0xAB, 0x6B, 0xEB,
                0x1B, 0x9B, 0x5B, 0xDB, 0x3B, 0xBB, 0x7B, 0xFB, 0x07, 0x87,
                0x47, 0xC7, 0x27, 0xA7, 0x67, 0xE7, 0x17, 0x97, 0x57, 0xD7,
                0x37, 0xB7, 0x77, 0xF7, 0x0F, 0x8F, 0x4F, 0xCF, 0x2F, 0xAF,
                0x6F, 0xEF, 0x1F, 0x9F, 0x5F, 0xDF, 0x3F, 0xBF, 0x7F, 0xFF, };
        return allInverses[b];
    }

    /**
     * retourne une valeur égale à celle donnée, exceptés les 8 bits de poids
     * faible ont été inversés bit à bit
     * 
     * @param b
     * @throw IllegalArgumentException si la valeur n'est pas une valeur 8 bits
     * @return une valeur égale à celle donnée, si ce n'est que les 8 bits de
     *         poids faible ont été inversés bit à bit
     */
    public static int complement8(int b) {
        checkBits8(b);
        return (b ^ DATA_MAX_VALUE);
    }

    /**
     * retourne une valeur 16 bits a partir de valeur 8 bits données en les
     * concaténant
     * 
     * @param highB
     *            8 bits de poids fort
     * @param lowB
     *            8 bits de poids faible
     * @return une valeur 16 bits a partir de valeur 8 bits données en les
     *         concaténant
     * @throw IllegalArgumentException si les valeur données ne sont pas des
     *        valeurs 8 bits
     */
    public static int make16(int highB, int lowB) {
        checkBits8(highB);
        checkBits8(lowB);
        return (highB * 256) | lowB;
    }

}
