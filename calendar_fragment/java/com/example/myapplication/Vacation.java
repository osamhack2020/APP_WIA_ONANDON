package com.example.myapplication;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;

public class Vacation {

    private String type;
    private String name;
    private int period;
    private HashSet<CalendarDay> dates = new HashSet<>();

    public HashSet<CalendarDay> getDates() {
        return dates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Vacation(String type, String name, int period) {
        this.type = type;
        this.name = name;
        this.period = period;
    }
}
