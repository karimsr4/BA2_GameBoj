package ch.epfl.gameboj.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        try (InputStream input = new FileInputStream(source);
                FileWriter writer = new FileWriter(new File("assembly.txt"));) {

            while ((e = input.read()) != -1) {
                if (e == PREFIXED_INSTRUC_FLAG) {
                    e = input.read();
                    writer.write(prefixedOpcodeTable.get(e).getString());

                } else {
                    Opcode o = directOpcodeTable.get(e);
                    if (o.totalBytes == 1) {
                        writer.write(o.toString());
                    } else if (o.totalBytes == 2) {
                        writer.write(o.getString(input.read()));
                    } else {
                        writer.write(o.getString(input.read(), input.read()));
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
