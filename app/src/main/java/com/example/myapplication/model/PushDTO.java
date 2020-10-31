package com.example.myapplication.model;

// 푸시 알림을 구성하는 객체
public class PushDTO {
    public String to="";
    public Notification notification = new Notification();

    public class Notification{
        public String body=""; // 푸시 알림 내용
        public String title=""; // 푸시 알림 제목
    }
}
