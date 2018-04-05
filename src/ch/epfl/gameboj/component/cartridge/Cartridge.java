package ch.epfl.gameboj.component.cartridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Classe qui simule la cartouche
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class Cartridge implements Component {
    private Component controller;

    private Cartridge(Component controller) {
        this.controller = controller;
    }

    /**
     * retourne une cartouche dont la mémoire morte contient les octets du fichier donné 
     * @param romFile fichier contenant les octets
     * @return une cartouche dont la mémoire morte contient les octets du fichier donné 
     * @throws IOException en cas d'erreur d'entrée-sortie
     */
    public static Cartridge ofFile(File romFile) throws IOException {
       
        try ( FileInputStream input = new FileInputStream(romFile)) {
        
            byte[] dataInFile = new byte[32768];
            input.read(dataInFile);
            /*int e=0;
            int i=0;
            while((e=input.read())!=-1)
            {
              dataInFile[i]=(byte)e;
              i++;
            }*/
             
            if (dataInFile[0x147] != 0)
                throw new IllegalArgumentException();
            return new Cartridge(new MBC0(new Rom(dataInFile)));
        } catch (FileNotFoundException e) {
            throw new IOException();
        }
      

        
    }

    @Override
    public int read(int address) {
      
        return controller.read(address);
       
    }

    @Override
    public void write(int address, int data) {
        controller.write(Preconditions.checkBits16(address),
                Preconditions.checkBits8(data));
    }

}
