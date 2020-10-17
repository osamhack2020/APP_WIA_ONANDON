
# WIA API 개발 문서
WIA는 군장병들의 병영생활을 향상시킬 수 있는 종합 SNS 플랫폼입니다. 이 문서는 개발에 사용된 코드와 데이터 모델에 대한 자세한 설명과 함께, 시스템의 전체적인 구조를 다루고 있습니다. 
WIA가 제공하는 서비스에 대한 구체적인 설명은 What_is_WIA.pdf에서 확인하실 수 있으며, 이 문서를 일기 전에 먼저 정독하실 것을 추천드립니다.  

### 데이터 모델
이 항목은 WIA가 firestore에 데이터를 저장하고 읽어오면서 사용하는 객체 데이터 모델에 대해 다루고 있습니다.

WIA는 다음과 같은 총 10개의 객체 데이터 모델을 사용하고 있습니다.


객체 파일 | 설명 
------------ | ------------- 
UserDTO.java  | 사용자 정보가 담기는 객체
MyToken.java  | 사용자 토큰이 저장되는 객체
PushDTO.java  | 푸시 알림 정보가 담기는 객체
BoardDTO.java  | 사용자가 게시판을 추가할 때, 게시판 정보가 담기는 객체
ClubDTO.java  | 동아리 페이지를 생성할 때, 페이지 정보가 담기는 객체
Question.java  | 동아리 페이지의 질문 글 정보가 담기는 객체
MyGoalContentDTO.java  | '나의 도전 이야기'게시판의 게시물 정보가 담기는 객체
PostDTO.java  | 일반 게시판의 게시물 정보가 담기는 객체
CommentDTO.java  | 댓글 정보가 담기는 객체
TagDTO.java  | 각 글의 해시태그 정보가 담기는 객체

.


#### UserDTO.java

```java
public class UserDTO {
    public String uid=""; // 사용자 고유 Uid
    public String name=""; // 사용자 이름
    public String army=""; // 사용자의 소속 군
    public String budae=""; // 사용자의 자대
    public String rank=""; // 사용자의 계급
    public String speciality=""; // 사용자의 특기
}
```
UserDTO 클래스에는 사용자의 정보가 저장됩니다. uid 변수에는 firebase에서 랜덤으로 생성한 사용자의
고유 Id가 저장되며, name 변수에는 사용자의 이름이, army 변수에는 사용자의 소속 군이 저장됩니다.
이외에도 사용자의 자대, 계급, 특기 등이 저장됩니다.

#### MyToken.java
```java
public class MyToken {
    public String pushtoken=""; // 사용자 토큰
}
```
MyToken 객체는 오직 하나의 변수로만 이루어져 있습니다. pushtoken 변수에는 사용자의 토큰이
저장되며, 이 토큰은 사용자가 좋아요, 댓글 알림 같은 푸시 알림을 받을 때 활용됩니다.

#### PushDTO.java
```java
// 푸시 알림을 구성하는 객체
public class PushDTO {
    public String to="";
    public Notification notification = new Notification();

    public class Notification{
        public String body=""; // 푸시 알림 내용
        public String title=""; // 푸시 알림 제목
    }
}
```
pushDTO 객체는 사용자가 타사용자에게 푸시알림을 보낼 때 사용됩니다. to 변수에는 푸시알림을 받을 사용자의 토큰이 저장되며,
body 변수와 title 변수에는 각각 푸시 알림 메세지의 제목과 내용이 저장됩니다. 사용자는 WIA 앱이 백그라운드 상태일 때만
푸시 알림을 받을 수 있습니다.

#### BoardDTO.java

```java
public class BoardDTO {
    public String manager="";
    public String name="";
    public String explain="";
    public long timestamp=0;
} 
```
위 BoardDTO.java 코드에서 name 변수에는 게시판의 이름이, explain 변수에는 게시판에 대한 설명이 저장됩니다. timestamp 변수에는 게시판을 생성한 시간이 저장되며
manager 변수에는 게시판의 관리자 Uid가 저장됩니다. 처음 게시판을 생성하는 경우, 게시판을 생성한 사용자의 Uid가 자동으로 관리자로 등록됩니다. 따라서, 이와 같은 경우
manager 변수에 게시판을 만든 사용자의 Uid가 저장됩니다.

#### ClubDTO.java

```java
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
```
ClubDTO 객체에는 동아리 게시판에서 열람할 수 있는 각 부대별 동아리 페이지들의 정보가 저장됩니다.
kind 변수에는 동아리의 성향과 분야를 나타내는 해시태그 정보가 저장되며, questionCount 변수에는
동아리 페이지에 게시된 질문 글의 수가 저장됩니다. number 변수에는 동아리 연락처가, represent에는
동아리 대표자의 Uid가 저장되며, 동아리에 대한 설명은 explain 변수에, 동아리 이름은 name 변수에 저장됩니다.

이렇게 저장된 동아리 정보들은 동아리 페이지의 '동아리 설명'란에 기재되어, 사용자들로 하여금 부대 내 동아리를
쉽게 접할 수 있도록 합니다.

#### Question.java

```java
public class QuestionDTO {
    public String uid=""; // 질문 글을 올린 사용자의 Uid
    public String explain=""; // 질문 글 내용
    public String answer=""; // 질문 글 답변 내용
    public int isAnswer=0; // 답변 유무
    public long timestamp = 0; // 질문 글 업로드 된 시기
}
```
QuestionDTO 객체에는 동아리 페이지에 게시되는 질문 글 정보가 저장됩니다. 동아리 페이지에 질문 글이 올라오면
관리자가 질문에 답변을 달 수 있는 데, 답변이 달리는 경우 isAnswer 변수에 1이 저장되어, 답변이 달렸음을 표시합니다.
반대로 isAnswer 변수에 0이 저장되어 있으면, 답변이 달리지 않았다는 의미입니다. 달린 답변의 내용은 answer 변수에 저장됩니다.

#### MyGoalContentDTO.java
```java
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
```
MyGoalContentDTO 객체에는 나의 도전 게시판에 게시되는 게시물의 정보가 저장됩니다.
favorites 변수에는 게시물에 좋아요를 누른 사용자들의 Uid가 저장되며, 또한, 이렇게 눌린 좋아요의 수는
favoriteCount 변수에 저장됩니다. 도전 게시판에 게시판을 작성하는 경우, 자신의 도전 목표일을 기재하도록 되어 있는 데,
이렇게 기재된 날짜는 year, month, day 변수에 저장됩니다. 이후, 이 세 변수는 안드로이드 앱 내부에서 Calendar 변수에 저장되어
현재 날짜 부터 목표일 까지의 D-day를 계산하는데 활용됩니다.


### firebase를 활용한 DB와 서버 구축


