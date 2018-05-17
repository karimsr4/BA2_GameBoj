package ch.epfl.gameboj.component.lcd;

import java.util.ArrayList;
import java.util.List;


public class Main {
    
    public static void main(String[] args) {
        List<Integer> l=new ArrayList<>();
        l.add(1);
        l.add(2);
        System.out.println(l);
        
        List <Integer> l1=new ArrayList<>();
        l1.add(1);
        l1.add(2);
        System.out.println(l1);
        System.out.println(l1==l);
        System.out.println(l.equals(l1));
        System.out.println(l.hashCode());
        System.out.println(l1.hashCode());
        
        
    }

}
