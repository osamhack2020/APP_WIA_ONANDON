package com.example.myapplication.model;

public class AlarmDTO {
    public String doUid=""; // 알림을 일으킨 사용자 고유 Id
    public String documentUid=""; // 알림이 일어난 게시물의 게시판 고유 Id
    public String postUid=""; // 알림이 일어난 게시물의 고유 Id
    public String manager=""; // 게시판의 관리자 Id
    public String name=""; // 게시판 이름
    public int annonymous=0; // 익명 유무
    public int key=0; // 0 : 좋아요 알림, 1 : 댓글 알림
    public long timestamp=0; // 알림이 일어난 시간
}
