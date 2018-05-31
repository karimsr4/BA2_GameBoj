package ch.epfl.gameboj.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Opcode;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;

public final class Disassembler {

    private final static int PREFIXED_INSTRUC_FLAG = 0xCB;
    private static final Map<Integer, Opcode> prefixedOpcodeTable = getOpcodeTable(Kind.PREFIXED);
    private static final Map<Integer, Opcode> directOpcodeTable = getOpcodeTable(Kind.DIRECT);
    private Disassembler() {
    };

    private static Map<Integer, Opcode> getOpcodeTable(Kind kind) {
        
        Map<Integer, Opcode> map = new HashMap<>();
        for (Opcode o : Opcode.values())
            if(o.kind==kind)
                map.put(o.encoding, o);

        return Collections.unmodifiableMap(map);
    }
    public static void main(String[] args) {
        try {
            disassemble(new File("tetris.gb"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void disassemble(File source) throws FileNotFoundException {
        int e;
        int address=0;
        Cpu cpu=new Cpu();
        try (InputStream input = new FileInputStream(source);
                Writer writer = new FileWriter(new File("assembly.txt"));) {
            while ((e = input.read()) != -1) {
                address++;
                
                System.out.println(e);
                if (e == PREFIXED_INSTRUC_FLAG) {
                    e = input.read();
                    writer.write(prefixedOpcodeTable.get(e).getString());

                } else {
                    Opcode o = directOpcodeTable.get(e);
          
                    if (o.totalBytes == 1) {
                        writer.write(o.getString());
                    } else if (o.totalBytes == 2) {
                        writer.write(o.getString(input.read()));
                    } else {
                        int firstRead=input.read();
                        int secondRead=input.read();
                        writer.write(o.getString(firstRead, secondRead));
                    }
                    System.out.println(o);
                }
                writer.write("\r\n");

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
