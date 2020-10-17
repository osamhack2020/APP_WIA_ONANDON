package com.example.myapplication;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;
import java.util.Random;

public class Vacation {

    private String type;
    private String name;
    private int period;
    private HashSet<CalendarDay> dates = new HashSet<>();
    private EventDecorator decorator;

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

    public EventDecorator getDecorator() {
        return decorator;
    }

    public Vacation(String type, String name, int period) {
        this.type = type;
        this.name = name;
        this.period = period;

        //int color = Color.argb(255, rnd.nextInt(100) + 156, rnd.nextInt(100) + 156, rnd.nextInt(100) + 156);
        int color = ((int)(Math.random()*16777215)) | (0xFF << 24);
        decorator = new EventDecorator(color, dates);
    }
}
