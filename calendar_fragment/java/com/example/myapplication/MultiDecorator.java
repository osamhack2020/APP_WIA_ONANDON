package com.example.myapplication;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;

public class MultiDecorator implements DayViewDecorator {

    private CalendarDay day;
    private ArrayList<GeneralEvent> eventList;

    public MultiDecorator(CalendarDay day, ArrayList<GeneralEvent> eventList) {
        this.day = day;
        this.eventList = eventList;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day == day;
    }

    @Override
    public void decorate(DayViewFacade view) {
        for(int i=0; i<eventList.size(); i++) {
            view.addSpan(new CustomSpan(10, eventList.get(i).getDecorator().getColor(), eventList.size(), i+1));
        }
    }

}
