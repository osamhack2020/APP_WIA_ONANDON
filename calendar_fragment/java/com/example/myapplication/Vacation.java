package com.example.myapplication;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;

public class Vacation extends GeneralEvent {

    // GeneralEvent에 기간과 종류를 추가한 휴가 클래스

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

    public Vacation(String type, String name, int period, int color) {
        super(name, new HashSet<CalendarDay>(), color);
        this.type = type;
        this.period = period;
    }
}
