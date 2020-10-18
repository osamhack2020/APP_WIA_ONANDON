package com.example.myapplication;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final int color;
    private HashSet<CalendarDay> dates;
    private HashMap<CalendarDay, ArrayList<GeneralEvent>> multipleEventInfo;

    public EventDecorator(int color, HashSet<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
        this.multipleEventInfo = new HashMap<CalendarDay, ArrayList<GeneralEvent>>();
    }

    public void setMultipleEventInfo(HashMap<CalendarDay, ArrayList<GeneralEvent>> info) {
        this.multipleEventInfo = info;
    }

    public HashMap<CalendarDay, ArrayList<GeneralEvent>> getMultipleEventInfo() {
        return multipleEventInfo;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        //return dates.contains(day);
        return dates.contains(day) && !multipleEventInfo.containsKey(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.addSpan(new DotSpan(5, color));
        view.addSpan(new CustomSpan(10, color, 1, 1));
    }

}
