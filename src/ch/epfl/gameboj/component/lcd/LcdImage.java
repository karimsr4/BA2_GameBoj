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

    private final List<LcdImageLine> lignes;
    private final int width;
    private final int height;
    
    public LcdImage(int width, int height, List<LcdImageLine> lignes) {
        List<LcdImageLine> copy=new LinkedList(lignes);
        
        for(LcdImageLine e :lignes) {
            if (e.size()==width)
                copy.remove(e);
        }
        checkArgument(copy.isEmpty() && lignes.size()==height);
        
        this.width=width;
        this.height=height;
        this.lignes=Collections.unmodifiableList(new ArrayList<LcdImageLine> (lignes));
        
        
    }
    
    @Override
    public boolean equals(Object o) {
        return false;
        
    }
    
    
    @Override
    public int hashCode() {
        return 0;
        
    }
    
    
    public final static class Builder{
        
    }
    
    
}
