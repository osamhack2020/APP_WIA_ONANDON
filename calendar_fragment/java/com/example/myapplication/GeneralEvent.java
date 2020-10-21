package com.example.myapplication;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.HashSet;

public class GeneralEvent {

    private String name;
    private HashSet<CalendarDay> dates;
    private EventDecorator decorator;
    private int color;

    public HashSet<CalendarDay> getDates() {
        return dates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventDecorator getDecorator() {
        return decorator;
    }

    public GeneralEvent(String name, Collection<CalendarDay> dates) {
        this.name = name;
        this.dates = new HashSet<>(dates);

        color = ((int)(Math.random()*16777215)) | (0xFF << 24);
        decorator = new EventDecorator(color, this.dates);
    }
}
