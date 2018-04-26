package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

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
        return o instanceof LcdImage &&  lines.equals((LcdImage) o );
    }

    @Override
    public int hashCode() {
        return lines.hashCode();

    }

    public int get(int x, int y) {
        LcdImageLine line= lines.get(y);
        int msb = line.getMsb().testBit(x)? 1: 0 ;
        int lsb = line.getLsb().testBit(x)? 1: 0 ;
        
        return (msb << 1) | lsb ;

        
    }

    public final static class Builder {
        
        private final LcdImageLine[] lines;
        private final int height, width;
        private boolean isBuilded;
        
        
        Builder(int width, int height){
            lines =new LcdImageLine[height];
            for(int i=0;i<height; i++) {
                lines[i]=new LcdImageLine.Builder(width).build();
            }
            
            this.width=width;
            this.height=height;
            isBuilded = false;
        }
        
        
        
        public Builder setLine(int index , LcdImageLine newLine) {
            if (isBuilded)
                throw new IllegalStateException();
            
            Objects.checkIndex(index, height);
            Preconditions.checkArgument(newLine.size()==width);
            lines[index]=newLine;
            return this;
            
        }

        
        public LcdImage build() {
            if (isBuilded)
                throw new IllegalStateException();
            
            return new LcdImage(width, height, Arrays.asList(lines));
        }
    }

}
