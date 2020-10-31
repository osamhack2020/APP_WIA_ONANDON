package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

// 나의 도전 이야기의 게시물 정보를 저장할 객체
public class MyGoalContentDTO {
    public String content=""; // 게시판 명칭 ("MyGoal"이 저장됨)
    public String explain=""; // 게시물 내용
    public String title=""; // 게시물 제목
    public String uid=""; // 게시물을 업로드 한 사용자의 Uid
    public long timestamp=0; // 게시물 업로드 시기
    public int year=0; // 목표한 날짜의 연도
    public int month=0; // 목표한 날짜의 달
    public int day=0; // 목표한 날짜의 일
    public int favoriteCount = 0; // 좋아요 수
    public int commentCount = 0; // 댓글 수
    public int isPhoto=0; // 사진 업로드 유무
    public String imageUri=""; // 게시물에 업로드 된 사진 링크
    public Map<String, Boolean> favorites = new HashMap<>(); // 좋아요를 누른 사용자 Uid가 저장 될 HashMap
    public Map<String, String> kind = new HashMap<>(); // 해시 태그
}
