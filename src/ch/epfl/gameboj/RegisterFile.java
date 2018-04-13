package ch.epfl.gameboj;

import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import static ch.epfl.gameboj.Preconditions.checkBits8;

/**
 * Classe qui simule un banc de registre de 8 bits
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class RegisterFile<E extends Register> {

    private final byte[] registerArray;

    /**
     * constructeur qui construit un banc de registres 8 bits
     * 
     * @param allRegs
     *            tableau des registres
     */
    public RegisterFile(E[] allRegs) {
        this.registerArray = new byte[allRegs.length];

    }

    /**
     * retourne la valeur 8 bits contenue dans le registre donné, sous la forme
     * d'un entier compris entre 0 (inclus) et 0xFF (inclus)
     * 
     * @param reg
     *            registre donné
     * @return la valeur 8 bits contenue dans reg
     */
    public int get(E reg) {

        return Byte.toUnsignedInt(registerArray[reg.index()]);

    }

    /**
     * modifie le contenu du registre donné pour qu'il soit égal à la valeur 8
     * bits donnée
     * 
     * @param reg
     *            registre donné
     * @param newValue
     *            valeur 8 bits
     * @throws IllegalArgumentException
     *             si newValue n'est pas une valeur 8 bits
     */
    public void set(E reg, int newValue) {
        this.registerArray[reg.index()] = (byte) (checkBits8(newValue));

    }

    /**
     * retourne vrai si et seulement si le bit donné du registre donné vaut 1
     * 
     * @param reg
     *            registre donné
     * @param b
     *            bit donné
     * @return vrai si et seulement si le bit donné du registre donné vaut 1
     */
    public boolean testBit(E reg, Bit b) {
        return Bits.test(registerArray[reg.index()], b);

    }

    /**
     * modifie la valeur stockée dans le registre donné pour que le bit donné
     * ait la nouvelle valeur donnée.
     * 
     * @param reg
     *            registre donné
     * @param bit
     *            bit donné
     * @param newValue
     *            nouvelle valeur
     */
    public void setBit(E reg, Bit bit, boolean newValue) {

        set(reg, Bits.set(get(reg), bit.index(), newValue));

    }

}
