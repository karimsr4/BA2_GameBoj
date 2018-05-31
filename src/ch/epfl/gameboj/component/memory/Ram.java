package ch.epfl.gameboj.component.memory;

import static ch.epfl.gameboj.Preconditions.checkArgument;
import static ch.epfl.gameboj.Preconditions.checkBits8;
import static java.util.Objects.checkIndex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Classe qui simule la mémoire vive RAM
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Ram {

    private final byte[] data;

    /**
     * construit une nouvelle memoire vive
     * 
     * @param size
     *            Taille de la memoire vive
     * @throws IllegalArgumentException
     *             si la taille donnéee est négative
     * 
     */
    public Ram(int size) {
        checkArgument(size >= 0);
        data = new byte[size];

    }

    private Ram(byte[] data) {
        this.data = data;
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

        return Byte.toUnsignedInt(data[checkIndex(index, data.length)]);

    }

    /**
     * écrit la valeur donnée à l'index donné
     * 
     * @param index
     *            position en memoire
     * @param value
     *            valeur a enregistrer en memoire
     * @throws IndexOutOfBoundsException
     *             si l'index est invalide
     * @throws IllegalArgumentException
     *             si la valeur donnee n'est pas une valeur 8 bits
     * 
     */
    public void write(int index, int value) {

        data[checkIndex(index, data.length)] = (byte) (checkBits8(value));
    }

    /**
     * retourne la taille, en octets, de la memoire
     * 
     * @return la taille de la memoire vive
     */
    public int size() {

        return data.length;
    }

    public void createSaveFile(String fileName) {

        try (OutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File(fileName)))) {
            stream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Ram getRamFromFile(File file) throws IOException {
        
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        byte[] data = stream.readAllBytes() ;
        stream.close();
        return new Ram(data);

    }

}
