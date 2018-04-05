package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

/**
 * Classe qui simule la mémoire vive RAM
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Ram {

    private byte[] data;

    /**
     * construit une nouvelle memoire vive
     * 
     * @param size
     *            Taille de la memoire vive
     * @throws new
     *             IllegalArgumentException si la taille donnéee est négative
     * 
     */
    public Ram(int size) {
        Preconditions.checkArgument(size >= 0);
        data = new byte[size];

    }

    /**
     * retourne l'octet se trouvant à l'index donne
     * 
     * @param index
     *            position en memoire
     * @return retourne l'octet se trouvant à l'index donne en parametre
     * @throws IndexOutOfBoundsException
     *             si l'index n'est pas valide
     */
    public int read(int index) {


        return Byte.toUnsignedInt(data[Objects.checkIndex(index, data.length)]);

    }

    /**
     * modifie le contenu de la memoire à l'index donne en la valeur donnee 
     * 
     * @param index
     *            position en memoire à modifier
     * @param value
     *            valeur a enregistrer en memoire
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     * @throws IllegalArgumentException
     *             si la valeur donnee n'est pas une valeur 8 bits
     * 
     */
    public void write(int index, int value) {

        data[Objects.checkIndex(index,data.length)] = (byte) (Preconditions.checkBits8(value));
    }

    /**
     * retourne la taille, en octets, de la memoire
     * 
     * @return la taille de la memoire vive
     */
    public int size() {

        return data.length;
    }

}
