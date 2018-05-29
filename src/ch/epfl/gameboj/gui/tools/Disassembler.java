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

public final class Disassembler {

    private final static int PREFIXED_INSTRUC_FLAG = 0xCB;
    private static final Map<Integer, Opcode> opcodeTable = getOpcodeTable();

    private Disassembler() {
    };

    private static Map<Integer, Opcode> getOpcodeTable() {
        Map<Integer, Opcode> map = new HashMap<>();
        for (Opcode o : Opcode.values())
            map.put(o.encoding, o);

        return Collections.unmodifiableMap(map);
    }

    public static void disassemble(File source) throws FileNotFoundException {

        int e;
        try (InputStream input = new FileInputStream(source);
                FileWriter writer = new FileWriter(new File("assembly.txt"));) {

            while ((e = input.read()) != -1) {
                if (e == PREFIXED_INSTRUC_FLAG) {
                    e = input.read();
                    writer.write(opcodeTable.get(e).getString());

                } else {
                    Opcode o = opcodeTable.get(e);
                    if (o.totalBytes == 1) {
                        writer.write(o.toString());
                    } else if (o.totalBytes == 2) {
                        writer.write(o.getString(input.read()));
                    } else {
                        writer.write(o.getString(input.read(), input.read()));
                    }
                }
                writer.write("\r\n");

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
