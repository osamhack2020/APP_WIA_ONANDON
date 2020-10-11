package com.example.myapplication.model;


public class PushDTO {
    public String to="";
    public Notification notification = new Notification();

    public class Notification{
        public String body="";
        public String title="";
    }
}
