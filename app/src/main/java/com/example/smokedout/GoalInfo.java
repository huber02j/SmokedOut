package com.example.smokedout;

import java.util.Map;

public class GoalInfo {
    public String name;
    public String period;
    public Integer number;
    public Boolean orMore;
    public String days;
    public Map<String, Boolean> checks;
//    public String test;

    public GoalInfo(String name, String period, Integer number, Boolean orMore, String days, Map<String, Boolean> checks) {
        this.name = name;
        this.period = period;
        this.number = number;
        this.orMore = orMore;
        this.days = days;
        this.checks = checks;
    }
}
