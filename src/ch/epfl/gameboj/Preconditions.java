package ch.epfl.gameboj;

/**
 * Interface de vérification des préconditions
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public interface Preconditions {

    

    /**
     * verifie la valeur de l'argument , ne fait rien si celui-ci est vrai
     * 
     * @param b
     *            argument booleen à tester
     * 
     * @throws IllegalArgumentException
     *             si l'argument est faux
     */
    static void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();

    }

    /**
     * retourne son argument si celui-ci est compris entre 0 et 0xFF (inclus)
     * 
     * @param v
     *            entier à tester
     * @throws IllegalArgumentException
     *             si l'argument n'est pas une valeur de 8 bits
     * @return l'argument v si celui-ci est une valeur de 8 bits
     */
    static int checkBits8(int v) {
        checkArgument((v >= 0) && (v <= 0xFF));
        return v;
    }

    /**
     * retourne son argument si celui-ci est compris entre 0 et 0xFFFF(inclus)
     * 
     * @param v
     *            entier à tester
     * @throws IllegalArgumentException
     *             si l'argument n'est pas une valeur de 16 bits
     * @return l'argument v si celui-ci est une valeur de 16 bits
     */
    static int checkBits16(int v) {
        checkArgument((v >= 0) && (v <= 0xFFFF));
        return v;

    }
}
