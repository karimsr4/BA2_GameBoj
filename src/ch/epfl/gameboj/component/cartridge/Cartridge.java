package ch.epfl.gameboj.component.cartridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class Cartridge implements Component {
    private Component controller;

    private Cartridge(Component controller) {
        this.controller = controller;
    }

    public static Cartridge ofFile(File romFile) throws IOException {
       
        try ( FileInputStream input = new FileInputStream(romFile)) {
        
            byte[] dataInFile = new byte[32768];
            // input.read(dataInFile);
            int e=0;
            int i=0;
            while((e=input.read())!=-1)
            {
              dataInFile[i]=(byte)e;
              i++;
            }
             //System.out.println(Arrays.toString(dataInFile));
            if (dataInFile[0x147] != 0)
                throw new IllegalArgumentException();
            return new Cartridge(new MBC0(new Rom(dataInFile)));
        } catch (FileNotFoundException e) {
            throw new IOException();
        }
      

        
    }

    @Override
    public int read(int address) {
       // System.out.println(Integer.toHexString(address));
        return controller.read(address);
       
    }

    @Override
    public void write(int address, int data) {
        controller.write(Preconditions.checkBits16(address),
                Preconditions.checkBits8(data));
    }

}
