package com.example.myapplication.model;

// 동아리 페이지에 업로드 된 질문 글 정보를 저장할 객체
public class QuestionDTO {
    public String uid=""; // 질문 글을 올린 사용자의 Uid
    public String explain=""; // 질문 글 내용
    public String answer=""; // 질문 글 답변 내용
    public int isAnswer=0; // 답변 유무
    public long timestamp = 0; // 질문 글 업로드 된 시기
}
