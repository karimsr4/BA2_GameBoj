package ch.epfl.gameboj.component.memory;

import static ch.epfl.gameboj.Preconditions.checkArgument;
import static ch.epfl.gameboj.Preconditions.checkBits8;
import static java.util.Objects.checkIndex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Classe qui simule la mémoire vive RAM
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Ram  {

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

//    @Override
//    public void save(String pathName) {
//        try {
//            BufferedOutputStream output = new BufferedOutputStream(
//                    new FileOutputStream(pathName, true));
//            output.write(data);
//            output.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void load(String pathName) {
//        byte[] result=new byte[size()];
//        try {
//            BufferedInputStream input = new BufferedInputStream(
//                    new FileInputStream(pathName));
//            result=input.readAllBytes();
//            input.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        
//        for(int i=0; i<size(); i++) {
//            write(i, result[i]);
//        }
//
//    }

}
