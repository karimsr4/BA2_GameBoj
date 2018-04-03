package ch.epfl.gameboj.component.memory;

import java.util.Arrays;
import java.util.Objects;

/**
 * Classe qui simule la memoire morte ROM
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Rom {

    private byte[] data;

    /**
     * construit une memoire morte ROM
     * 
     * @param data
     *            tableau de byte qui represente le contenu initial de la
     *            memoire morte
     */
    public Rom(byte[] data) {
//        if (data == null) {
//            throw new NullPointerException();
//        } else {
//            
//        }
        
        this.data = Arrays.copyOf(Objects.requireNonNull(data), data.length);
    }

    /**
     * retourne la taille, en octets, de la memoire
     * 
     * @return la taille, en octets, de la memoire
     */
    public int size() {
        return data.length;
    }

    /**
     * retourne l'octet se trouvant à l'index donne
     * 
     * @param index
     *            position en memoire
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     * @return l'octet se trouvant a l'index donne en parametre
     */
    public int read(int index) {
//        if ((index >= 0) && (index < data.length)) {
//            Byte.toUnsignedInt(data[index])
//        } else {
//            throw new IndexOutOfBoundsException();
//        }
        
        return Byte.toUnsignedInt(data[Objects.checkIndex(index, data.length)]);
        
        
    }

}
