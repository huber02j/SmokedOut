package com.example.smokedout;
import java.util.Comparator;

public class MilestoneSorter implements Comparator<Milestone>  {
    public int compare(Milestone m1, Milestone m2) {
        return m2.getDate().compareTo(m1.getDate());
    }
}

