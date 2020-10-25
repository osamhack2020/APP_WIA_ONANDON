package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

// 일반 게시물의 정보를 저장할 객체
public class PostDTO {
    public String name="";
    public String content=""; // 게시물이 업로드 된 게시판의 Id
    public String explain=""; // 게시물 내용
    public String title=""; // 게시물 제목
    public String uid=""; // 게시물 업로드 한 사용자의 고유 Uid
    public long timestamp=0; // 게시물 업로드 시기
    public int favoriteCount = 0; // 게시물 좋아요 수
    public int commentCount = 0; // 게시물 댓글 수
    public int isPhoto=0; // 게시물의 사진 업로드 유무
    public int annonymous=0; //익명 유무
    public String imageUri=""; // 게시물에 업로드 된 사진 링크
    public Map<String, Boolean> scrap = new HashMap<>();
    public Map<String, Boolean> favorites = new HashMap<>(); // 좋아요를 누른 사용자 Uid가 저장 될 HashMap
    public Map<String, String> kind = new HashMap<>(); // 해시 태그
}
