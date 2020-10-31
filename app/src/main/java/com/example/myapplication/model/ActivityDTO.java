package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class ActivityDTO {
    public String explain=""; // 게시물 내용
    public String title=""; // 게시물 제목
    public long timestamp=0; // 게시물 업로드 시기
    public String imageUri=""; // 게시물에 업로드 된 사진 링크
    public String link="";
    public String participation="";
    public String name="";
    public int year=0; // 목표한 날짜의 연도
    public int month=0; // 목표한 날짜의 달
    public int day=0; // 목표한 날짜의 일
    public Map<String, Boolean> scrap = new HashMap<>();
    public Map<String, String> kind = new HashMap<>(); // 해시 태그
}
