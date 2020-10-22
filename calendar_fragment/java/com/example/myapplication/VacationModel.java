package com.example.myapplication;


import java.util.List;

public class VacationModel {

    String name;
    String type;
    int period;
    int color;
    List<CalendarDayModel> dates;

    public VacationModel(String name, String type, int period, int color, List<CalendarDayModel> dates) {
        this.name = name;
        this.type = type;
        this.period = period;
        this.color = color;
        this.dates = dates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<CalendarDayModel> getDates() {
        return dates;
    }

    public void setDates(List<CalendarDayModel> dates) {
        this.dates = dates;
    }


}

class CalendarDayModel {

    int day;
    int month;
    int year;

    public CalendarDayModel(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}