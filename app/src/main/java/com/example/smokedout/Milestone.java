package com.example.smokedout;

import java.util.Date;

public class Milestone implements Comparable<Milestone>{
    public String name;
    public String desc;
    public Long date;

    public Milestone(String name, String desc, Long date) {
        this.name = name;
        this.desc = desc;
        this.date = date;
    }

    public Long getDate() {
        return date;
    }

    @Override
    public int compareTo(Milestone m) {
        if (this.getDate() == null || m.getDate() == null) {
            return 0;
        }
        return getDate().compareTo(m.getDate());
    }
}