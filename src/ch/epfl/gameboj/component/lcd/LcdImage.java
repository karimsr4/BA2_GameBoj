package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ADMIN
 *
 */
public final class LcdImage {

    private final List<LcdImageLine> lines;
    private final int width;
    private final int height;

    public LcdImage(int width, int height, List<LcdImageLine> lines) {

        checkArgument(lines.size() == height);
        for (LcdImageLine e : lines)
            checkArgument(e.size() == width);

        this.width = width;
        this.height = height;
        this.lines = Collections
                .unmodifiableList(new ArrayList<LcdImageLine>(lines));

    }

    @Override
    public boolean equals(Object o) {
        return false;

    }

    @Override
    public int hashCode() {
        return 0;

    }

    public int get(int x, int y) {
        LcdImageLine line= lines.get(y);
        int msb = line.getMsb().testBit(x)? 1: 0 ;
        int lsb = line.getLsb().testBit(x)? 1: 0 ;
        
        return (msb << 1) | lsb ;

        
    }

    public final static class Builder {

    }

}
