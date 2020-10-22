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

    public GeneralEvent(String name, Collection<CalendarDay> dates, int color) {
        this.name = name;
        this.dates = new HashSet<>(dates);

        // 일정의 날짜를 넘겨주어 EventDecorator 생성
        // 색깔이 지정되지 않았으면 임의로 지정
        if(color != 0)
            this.color = ((int)(Math.random()*16777215)) | (0xFF << 24);
        else
            this.color = color;
        decorator = new EventDecorator(color, this.dates);
    }
}
