package ch.epfl.gameboj;

import java.io.File;

public interface Saveable {
    
    
    void save(String pathName);
    
    void load (String pathName);

}
