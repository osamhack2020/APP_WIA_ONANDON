package com.example.myapplication;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class Vacation extends GeneralEvent {

    private String type;
    private int period;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Vacation(String type, String name, int period) {
        super(name, new HashSet<CalendarDay>());
        this.type = type;
        this.period = period;
    }
}
