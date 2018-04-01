package ch.epfl.gameboj.component.memory;

/**
 * Classe qui simule la mémoire vive RAM
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
        if (size < 0) {
            throw new IllegalArgumentException();
        } else {
            data = new byte[size];

        }
    }

    /**
     * retourne l'octet se trouvant à l'index donne
     * 
     * @param index
     *            position en memoire
     * @return retourne l'octet se trouvant à l'index donne en parametre
     */
    public int read(int index) {
        if ((index >= 0) && (index < data.length)) {
            return Byte.toUnsignedInt(data[index]);
        } else {
            throw new IndexOutOfBoundsException();
        }
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
        if ((index < 0) || (index >= data.length)) {
            throw new IndexOutOfBoundsException();
        } else if ((value < 0) || (value > 0x00FF)) {
            throw new IllegalArgumentException();
        } else {
            data[index] = (byte) value;
        }
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
