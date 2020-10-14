package com.example.myapplication;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final int color;
    public HashSet<CalendarDay> dates;

    public EventDecorator(int color, HashSet<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(10, color));
    }
}
