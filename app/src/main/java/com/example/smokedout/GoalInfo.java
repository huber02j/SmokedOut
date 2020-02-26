package com.example.smokedout;

public class GoalInfo {
    public String name;
    public String period;
    public Integer number;
    public Boolean orMore;
    public String days;
    public String motivation;

    public GoalInfo(String name, String period, Integer number, Boolean orMore, String days, String motivation) {
        this.name = name;
        this.period = period;
        this.number = number;
        this.orMore = orMore;
        this.days = days;
        this.motivation = motivation;
    }
}
