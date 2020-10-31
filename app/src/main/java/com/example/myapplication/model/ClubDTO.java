package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

// 동아리 페이지에 대한 정보를 저장하는 객체
public class ClubDTO {
    public String name=""; // 동아리 이름
    public String explain=""; // 동아리 설명
    public String period=""; // 주기적으로 만나는 시간
    public String represent=""; // 동아리 대표자
    public String number=""; // 동아리 연락처
    public String manager=""; // 동아리 페이지 관리자 Uid
    public String imageUri=""; // 동아리 설명에 업로드 된 사진 링크
    public Map<String, String> kind = new HashMap<>(); // 해시 태그
    public int questionCount=0; // 동아리 페이지에 게시된 질문 수
    public long timestamp=0; // 페이지 생성 시기
    public int isPhoto=0; // 사진 업로드 유무
}
