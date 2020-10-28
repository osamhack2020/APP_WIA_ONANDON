
# WIA API 개발 문서
WIA는 군장병들의 병영생활을 향상시킬 수 있는 종합 SNS 플랫폼입니다. 이 문서는 개발에 사용된 코드와 데이터 모델에 대한 자세한 설명과 함께, 시스템의 전체적인 구조를 다루고 있습니다. 
WIA가 제공하는 서비스에 대한 구체적인 설명은 What_is_WIA.pdf에서 확인하실 수 있으며, 이 문서를 일기 전에 먼저 정독하실 것을 추천드립니다.  

## 1. 개발 환경 및 사용 라이브러리
WIA는 안드로이드 스튜디오를 기반으로 개발되었으며, firebase를 활용하여 서버 및 DB를 구축하였습니다.

   ### 1) 사용언어

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">
    
**Frontend**
* XML

**Backend**
* 자바
* firebase firestore
* firebase storage
* firebase message

---

</div>
</details>

### 2) 개발 환경

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```gradle
android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"

   // ...
}
```

---

</div>
</details>

### 3) 사용 라이브러리

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```gradle
dependencies {
    // ...
    
    // firebase 라이브러리
    implementation 'com.google.firebase:firebase-analytics:17.5.0'
    implementation 'com.google.firebase:firebase-core:17.5.0'
    implementation 'com.google.firebase:firebase-auth:19.4.0'
    implementation 'com.google.firebase:firebase-firestore:21.7.0'
    implementation 'com.google.firebase:firebase-storage:19.2.0'
    implementation 'com.google.firebase:firebase-messaging:20.3.0'
    
    // ...
}
```
위 코드는 Wia 개발에 활용된 firebase 라이브러리를 모아놓은 코드입니다.

#### 상세설명

```gradle
implementation 'com.google.firebase:firebase-firestore:21.7.0'
```
* firebase에서 제공하는 DB인 firestore 관련 라이브러리 입니다.
* 리얼타임 데이터베이스를 제공합니다.


```gradle
implementation 'com.google.firebase:firebase-auth:19.4.0'
```
* 로그인, 로그아웃을 포함한 사용자 권한 기능을 제공하는 라이브러리 입니다.


```gradle
implementation 'com.google.firebase:firebase-storage:19.2.0'
```
* firebase에서 제공하는 저장소와 관련된 코드입니다.
* 업로드 되는 사진 데이터들이 이 저장소에 저장됩니다.


```gradle
implementation 'com.google.firebase:firebase-messaging:20.3.0'
```
* 푸시 알림 관련 라이브러리입니다.


```gradle
implementation 'com.google.firebase:firebase-analytics:17.5.0'
```
* 사용자 데이터 분석에 관한 라이브러리 입니다.
* firebase는 위 라이브러리를 활용하여 사용자 데이터를 분석한 뒤, 분석 결과를 firebase 계정을 통해 보여줍니다.

---

</div>
</details>
    
#### 그외 라이브러리

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">
   
```gradle
implementation 'com.squareup.okhttp3:okhttp:3.10.0'
implementation 'com.google.code.gson:gson:2.8.6'
```
* HTTP 통신을 위해 선언된 라이브러리 입니다.
* 특정 기기에 푸시 알림을 보낼 때 사용됩니다.

```gradle
implementation 'com.github.bumptech.glide:glide:4.9.0'
```
* glide 함수를 위한 라이브러리입니다.
* 서버에서 사진 데이터를 가져와 사용자에게 보여주는 기능을 위해 사용됩니다.

```gradle
implementation 'androidx.recyclerview:recyclerview:1.1.0'
```
* 리사이클러뷰를 위한 라이브러리 입니다.

---

</div>
</details>


## 2. 데이터 모델
이 항목은 WIA가 firestore에 데이터를 저장하고 읽어오면서 사용하는 객체 데이터 모델에 대해 다루고 있습니다.

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

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
DietDTO.java | 각 부대의 식단표 정보가 담기는 객체
AlarmDTO.java | 알림 정보가 담기는 객체
MyPostDTO.java | 내가 쓴 게시물, 스크랩, 내가 댓글 쓴 게시물 정보가 담기는 객체

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
BoardDTO 객체에는 사용자가 게시판을 생성하는 경우, 생성된 게시판의 정보가 저장됩니다. 위 BoardDTO.java 코드에서 name 변수에는 게시판의 이름이, explain 변수에는 게시판에 대한 설명이 저장됩니다. timestamp 변수에는 게시판을 생성한 시간이 저장되며 manager 변수에는 게시판의 관리자 Uid가 저장됩니다. 처음 게시판을 생성하는 경우, 게시판을 생성한 사용자의 Uid가 자동으로 관리자로 등록됩니다. 따라서, 이와 같은 경우 manager 변수에 게시판을 만든 사용자의 Uid가 저장됩니다.

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
favorites 변수에는 게시물에 좋아요를 누른 사용자들의 Uid가 저장되며, 이렇게 눌린 좋아요의 수는
favoriteCount 변수에 저장됩니다. 

도전 게시판에 게시물을 작성하는 경우, 자신의 도전 목표일을 기재하도록 되어 있는 데,
이렇게 기재된 날짜는 year, month, day 변수에 저장됩니다. 이후, 이 세 변수는 안드로이드 앱 내부에서 Calendar 변수에 저장되어
현재 날짜 부터 목표일 까지의 D-day를 계산하는데 활용됩니다.

#### PostDTO.java
```java
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
    public Map<String, Boolean> favorites = new HashMap<>(); // 좋아요를 누른 사용자 Uid가 저장 될 HashMap
    public Map<String, String> kind = new HashMap<>(); // 해시 태그
}
```
PostDTO 객체에는 사용자들이 생성한 일반 게시판에 게시되는 게시물의 정보가 저장됩니다. MyGoalContentDTO.java에서
날짜 변수들이 삭제되는 대신, annonymous 변수가 추가되었습니다. annonymous 변수는 사용자가 게시물을 익명으로
업로드 하였는지, 혹은 실명으로 업로드 하였는지에 대한 정보를 담고 있습니다. annonymous 변수에 1이 저장되어 있다는 것은
해당 게시물이 익명으로 업로드 되었다는 것을 의미합니다.

#### CommentDTO.java
```java
// 댓글 정보를 저장하는 객체
public class CommentDTO {
    public String uid=""; // 댓글을 업로드 한 사용자 Uid
    public String comment=""; // 댓글 내용
    public long timeStamp = 0; // 댓글 올린 시기
}
```
CommentDTO 객체에는 게시물에 달린 댓글 정보가 저장됩니다. comment 변수에는 댓글 내용이, uid 변수에는 댓글을 단 사용자의 Id가 저장됩니다.

#### TagDTO.java
```java
public class TagDTO {
    public ArrayList<String> tag = new ArrayList<>();
}
```
TagDTO 객체에는 게시물에 달린 모든 해시태그 정보가 저장됩니다. WIA는 게시물에 달린 해시태그를 활용하여,
원하는 게시물을 검색할 수 있는 기능을 제공하고 있는 데, tag 변수에 저장된 해시 태그 배열은 이러한 검색 과정에서 활용됩니다.

#### DietDTO.java
```java
public class DietDTO {
    public long postDay=0; // 식단표에 해당하는 날짜
    public ArrayList<String> breakfast = new ArrayList<>(); // 조식 메뉴
    public ArrayList<String> lunch = new ArrayList<>(); // 중식 메뉴
    public ArrayList<String> dinner = new ArrayList<>(); // 석식 메뉴
}
```

DietDTO 객체에는 각 부대의 식단 정보가 저장됩니다. postDay 변수에는 식단표에 해당하는 날짜가 저장되며,
나머지 3개의 ArrayList에는 조식, 중식, 석식 메뉴가 String 배열로 저장됩니다.

#### AlarmDTO.java
```java
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
```

AlarmDTO.java 객체에는 사용자에게 뜨는 알림 메세지의 정보가 저장됩니다. doUid변수에는
알림을 일으킨 사용자의 고유 Id(댓글을 남긴 사람, 좋아요를 누른 사람의 고유 ID)가 저장되며, key 변수에는
알림 메세지의 종류가 저장됩니다. 0이 저장되면 '좋아요 알림', 1이 저장되면 '댓글 알림'이라는 의미입니다.

documentUid, postUid, manager, name 변수에는 알림이 일어난 게시물의 정보가 저장됩니다. 뜬 알림을 누르면
알림이 발생한 게시물로 이동하게 되는데, 이때 필요한 정보들이 저장됩니다.

#### MyPostDTO.java
```java
public class MyPostDTO {
    public String name=""; // 게시판 이름
    public String documentUid=""; // 게시판 고유 Id
    public String postUid=""; // 게시물 고유 Id
    public long timestamp=0; // 저장된 시간
}
```

MyPostDTO.java 객체에는 '내가 쓴 게시물', '내가 댓글 단 게시물', '스크랩한 게시물' 정보가 저장됩니다.
name 변수에는 게시판 이름 정보가 저장됩니다. 예를 들어, A라는 글을 스크랩 했다면, name 변수에는 A 게시물이
업로드된 게시판의 이름이 저장됩니다. 

timestamp는 객체가 저장된 시간이 저장됩니다. MyPostDTO 객체는 이벤트가
발생하는 즉시 저장되므로, timestamp변수에는 댓글을 단 시간 혹은 게시물을 스크랩한 시간 등, 이벤트가 발생한
시간이 저장됩니다.

---

</div>
</details>

## 3. firebase를 활용한 DB와 서버 구축

이 항목은 WIA에서 활용하고 있는 DB의 구조와, 자주 사용된 firebase 함수에 대해 다루고 있습니다.

### 1) DB 구조

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

firebase는 기본적으로 NoSQL 구조의 데이터베이스를 지원하며, WIA 또한 같은 형식의 데이터베이스로 서비스를 제공합니다.

#### 사용자 정보 DB 'UserInfo'

collection | document | field
------------ | ------------- | -------------
 UserInfo | 0iqfcMLngZPEN9FqWxMtlqcTr5Q2  | UserDTO.java
 └| AVKFXfvtFJOWbHzcKDFfhjEAjVF3  | UserDTO.java
 └| B756uS3DFOTeQlUg245r6ziClrm1 | UserDTO.java
 └| BXhI5OaMsrNkH2RdmLvB43ntUOZ2 | UserDTO.java
 
 사용자 정보는 'UserInfo' collection에 저장됩니다. UserInfo는 사용자 계정의 고유 Id로 이름이 지정된
 하위 document들로 구성되어 있으며, 각 document는 사용자 정보를 가지고 있는 UserDTO 객체와 연결되어 있습니다.
 이때, 사용자 계정의 고유 Id란, 사용자가 계정을 만들 때 firebase에서 랜덤으로 지정해 준 Id를 말합니다.
 
 ---
 
#### 푸시 투큰 정보 DB 'PushTokens'

collection | document | field
------------ | ------------- | -------------
 PushTokens | 0iqfcMLngZPEN9FqWxMtlqcTr5Q2  | MyToken.java
 └| AVKFXfvtFJOWbHzcKDFfhjEAjVF3  | MyToken.java
 └| B756uS3DFOTeQlUg245r6ziClrm1 | MyToken.java
 └| BXhI5OaMsrNkH2RdmLvB43ntUOZ2 | MyToken.java
 
 사용자들의 토큰 정보는 'PushTokens' collection에 저장됩니다. PushTokens는 UserInfo와 마찬가지로, 사용자들의
 고유 Id로 지정된 하위 document들로 구성되어 있으며, 각 docuemt는 사용자들의 토큰 정보를 가지고 있는 MyToken.java와
 연결되어 있습니다.
 
 **예시)** 사용자의 고유 Id가 A라면, PushTokens 이름의 컬렉션에서 A 이름의 document를 불러와 MyToken.java 객체를
 추출하여 사용자 토큰 정보를 얻어 올 수 있습니다.
 
  ---
 
 #### 부대 게시판 DB '*(부대이름)* 게시판'
 
 collection | document | field
------------ | ------------- | -------------
|교육사게시판 | **0iqfcMLngZPEN9FqWxMtlqcTr5Q2** | BoardDTO.java|
|└| AVKFXfvtFJOWbHzcKDFfhjEAjVF3  | BoardDTO.java |
|└| B756uS3DFOTeQlUg245r6ziClrm1 | BoardDTO.java |
|└| BXhI5OaMsrNkH2RdmLvB43ntUOZ2 | BoardDTO.java |

WIA는 각 부대별로 커뮤니티를 제공하기 때문에, 부대마다 사용하고 있는 게시판들의 이름이 다를 수 있습니다.
'*(부대이름)* 게시판' collection은 *(부대이름)* 에 게설된 게시판 정보를 담고 있습니다. 위 표에서는 공군 '교육사'를 예시로,
교육사 커뮤니티의 게시판 정보를 담고 있는 DB의 일부를 보여주고 있습니다.

표에서도 볼 수 있듯이, 교육사게시판 collection은 각 게시판의 고유 Id로 지정된 하위 document들로 이루어져 있으며,
각 document는 BoardDTO 객체와 연결되어 있습니다. document를 이루고 있는 고유 Id 하나는 교육사 커뮤니티에 개설된
게시판 하나를 의미합니다.

**예시)** 교육사 커뮤니티에 '고민 게시판'이 게설되었다면, 고민 게시판은 firebase로부터 고유 Id를 부여받습니다. 위 표에서 bold체로 적혀 있는
Id를 예시로 들자면, 고민 게시판의 고유 Id '**0iqfcMLngZPEN9FqWxMtlqcTr5Q2**'로 지정된 document가 고민 게시판의 정보를 담고 있습니다.

게시판이 게설되면 각 게시판의 고유 Id로 지정된 collection이 만들어지며, 이 collection에는 각 게시판에 업로드 된 게시물 데이터가 저장됩니다.
아래의 표에서 그 예시를 볼 수 있습니다.

collection | document | field
------------ | ------------- | -------------
|**0iqfcMLngZPEN9FqWxMtlqcTr5Q2** | **6PFPTRB2OCKlGfALE58A**  | PostDTO.java|
|└| DARToyKvC54PfU6onL8U   | PostDTO.java |
|└| LaWhw7YaYG8ycpysdKO9  | PostDTO.java |
|└| M1W2iWJPJ8zt9qVaFmIL  | PostDTO.java |

위 표는 위에서 예시로 들었던 고민 게시판의 DB입니다. 고민 게시판의 고유 Id '**0iqfcMLngZPEN9FqWxMtlqcTr5Q2**'로 지정된
collection은 각 게시물의 고유 Id로 지정된 하위 document로 이루어져 있으며, 각 document는 게시물 정보를 담고 있는 PostDTO객체와 연결되어 있습니다.
document를 이루고 있는 Id 하나는 고민 게시판에 업로드 된 게시물 하나를 의미합니다.

게시판에 게시물이 업로드 되면, 게시물의 고유 Id로 지정된 collection이 만들어지며, 이 collection에는 게시물에 달린 댓글 데이터가 저장됩니다.
아래의 표에서 그 예시를 볼 수 있습니다.

collection | document | field
------------ | ------------- | -------------
|**6PFPTRB2OCKlGfALE58A** | i7Bz9XtaiTDSx4oRfgIL | CommentDTO.java|
|└| k30wDOzIYUJ7Y4w6NvNn | CommentDTO.java |
|└| CVug2OwhROhyskEN86Ca | CommentDTO.java |
|└| KTSXBggtMBOXRymkwQfW  | CommentDTO.java |

위 표는 고민 게시판에 업로드 되었던 게시물 중 '**6PFPTRB2OCKlGfALE58A**' 아이디의 게시물 DB 입니다. 게시물의 고유 Id '**6PFPTRB2OCKlGfALE58A**'
로 지정된 collection은 각 댓글의 고유 Id로 지정된 하위 document로 이루어져 있으며, 각 document는 댓글 정보를 담고 있는 commentDTO객체와 연결되어 있습니다.

 ---

#### 해시태그 DB '*(게시판 고유 Id)*_tag'

collection | document | field
------------ | ------------- | -------------
|**0iqfcMLngZPEN9FqWxMtlqcTr5Q2_tag**| tag | TagDTO.java|

해시태그 DB는 게시판의 게시물들에 달린 모든 해시태그를 저장하고 있는 DB이며, 사용자가 해당 게시판에서 해시태그를 활용하여 특정 게시물을 검색할 때,
활용됩니다. 위 표는 위 항목에서 예시로 들었던 고민게시판의 해시태그 DB이며, *(고민게시판의 고유 Id)_tag*로 collection의 이름이 지정됩니다. 이 DB는 'tag'라는 이름의
document 하나로 이루어져 있으며, tagDTO 객체에 모든 해시태그 정보가 저장됩니다. 게시판이 새로 생성되면, 해당 게시판의 해시태그 DB도 함께 생성됩니다.

 ---

#### 동아리 DB '*(부대이름)* 동아리'

collection | document | field
------------ | ------------- | -------------
|교육사동아리| **cvUZhpXRLwKT8bRHO2CX** | ClubDTO.java|
|└| hC17nTAGgtAxKWIIbcvo | ClubDTO.java|

동아리 DB는 각 부대에 게설된 동아리 정보를 저장합니다. '*(부대이름)* 동아리'라는 이름으로 지정된 collection은 각 동아리의 고유 Id로 지정된 하위 document들로 구성되어 있으며, 각 document들은 동아리 정보를 담고 있는 ClubDTO.java 객체로 이루어져 있습니다. 부대 내 동아리가 게설되면, 게설된 동아리는 firebase로 부터 고유 Id를 부여받으며, 이 Id로 지정된 하위 document가 '*(부대이름)* 동아리' collection에 추가됩니다.

**예시)** 위 표를 예시로 들자면, 교육사에 게설된 동아리 정보를 담고 있는 '교육사동아리' collection은 총 2개의 하위 document로 이루어져 있으며 이는 교육사에 총 2개의 동아리가 개설되었음을 의미합니다.

 ---

#### 동아리의 질문글 DB '*(동아리의 고유 Id)*_question'

collection | document | field
------------ | ------------- | -------------
|**cvUZhpXRLwKT8bRHO2CX_question**| BZyYwYOgA99c9Ks5ZyTe  | QuestionDTO.java|
|└| S3fTOgita8aPTNUGfntO  | QuestionDTO.java|
|└| 6DAqzDdlhkQeWtTxsbor  | QuestionDTO.java|

동아리 질문 글 DB는 각 동아리 페이지에 업로드 된 질문글 데이터를 저장하고 있는 DB입니다. '*(동아리의 고유 Id)* _question'으로 컬렉션이 지정되며, 각 컬렉션은 질문글의
고유 Id로 지정된 하위 document들로 구성되어 있습니다. 각 document는 질문 글 정보를 저장하고 있는 QuestionDTO 객체와 연결되어 있습니다.

**예시)** 교육사 동아리에 게설된 축구동아리가 **cvUZhpXRLwKT8bRHO2CX**를 고유 Id로 지정받았다고 가정해 봅시다. 축구 동아리가 게설되는 순간 **cvUZhpXRLwKT8bRHO2CX_question**으로 지정된 collection이 선언되며, 이 colleciton에는 질문글 데이터가 저장됩니다. 위 표에서 축구동아리에 업로드 된 질문글의 개수가 3개임을 알 수 있습니다.

---

#### 동아리 게시판 DB '*(부대이름)* 동아리게시판'

collection | document | field
------------ | ------------- | -------------
|교육사동아리게시판| waymMyEanIx8oSeQUuzK   | postDTO.java|
|└| yWO8cjGlfSI4CYKFuqAC  | postDTO.java|
|└| ngqQJwDvgV2EifQWHnps  | postDTO.java|

각 동아리 페이지에는 동아리의 활동 내용과 홍보글을 올릴 수 있는 동아리 전용 게시판이 있습니다. '*(부대이름)* 동아리게시판' 으로
이름이 지정된 DB는 각 동아리 페이지의 전용 페이지에 업로드 되는 게시물의 데이터를 저장합니다. 이 DB는 각 게시물의 고유 ID로 지정된
하위 document로 구성되어 있으며, 각 document는 게시물 정보를 가지고 있는 객체 데이터 postDTO와 연결되어 있습니다. 

---

#### 알림 DB '*(사용자의 고유 ID)* _Alarm'

collection | document | field
------------ | ------------- | -------------
|**gah0PGV0jmgq536SboVip6vURu92_Alarm**| 0qHjLRZgaKTU3qOizAeB   | AlarmDTO.java|
|└| 6TenHBbCbFSPqjwE4MNr  | AlarmDTO.java|
|└| PX9PJoPvGtCGMUemF4vw  | AlarmDTO.java|

사용자에게 댓글 알림, 좋아요 알림이 발생하면 알림 객체 정보가 서버에 저장되는데, 이때 '*(사용자의 고유 ID)* _Alarm'로 지정된
collection에 알림 객체 정보가 저장됩니다. collection은 알림의 고유 ID로 지정된 하위 document로 구성되어 있으며,
이 하위 document는 AlarmDTO.java 객체와 연결되어 있습니다.

---

#### 내가 쓴 글 DB '*(사용자의 고유 ID)* _MyPost'

collection | document | field
------------ | ------------- | -------------
|**gah0PGV0jmgq536SboVip6vURu92_Scrap**| qHum1IKZcNNJPGkeOe0K | MyPostDTO.java |
|└| yUTEwRXfNYJG8xhpUl61  | MyPostDTO.java |
|└| ZC5UTNefue86NfWqI5tl  | MyPostDTO.java |

WIA는 사용자가 게시물을 업로드 하면, 사용자가 업로드 한 게시물만 따로 모아서 제공합니다. 이때, 사용자가 업로드한 게시물 정보가
'*(사용자의 고유 ID)* _MyPost'로 지정된 collection에 저장됩니다. collection은 각 게시물의 고유 Id로 지정된 하위 document로 구성되며,
각 document는 MyPostDTO 객체와 연결됩니다.

이 외에도, 같은 구조로 다음과 같은 DB가 있습니다.

**1) 스크랩 DB '*(사용자의 고유 ID)* _Scrap'**   
사용자가 스크랩한 게시물 정보가 저장됩니다.

**2) 내가 댓글 쓴 게시물 DB '*(사용자의 고유 ID)* _MyMessage'**  
사용자가 댓글을 단 게시물 정보가 저장됩니다.

---

</div>
</details>

### 2) 함수

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">
WIA는 firebase에서 제공하는 여러 함수를 활용하여 DB와 서버 기능을 제공하고 있습니다. 이 항목은 WIA의 개발에 자주 사용되었던 firebase 함수를 다루고 있습니다.


#### 1. 데이터 가져오기
<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```java
 FirebaseFirestore firestore = FirebaseFirestore.getInstance();

 firestore.collection(/* collection 이름 */).document(/* document 이름 */).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                    
                        //...
                        
                    }
                });
```

해당 collection의 하위 document에 저장되어 있는 데이터를 가져오는 함수입니다. 가져오기가 성공하면 documentSnaphot 변수에서 데이터를 추출해 낼 수 있습니다.

```java
FirebaseFirestore firestore = FirebaseFirestore.getInstance();

final DocumentReference docRef = firestore.collection(/* collection 이름 */).document(/* document 이름 */);
firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        
                        //...
                        
                        return null;
                    }
                });               
```

데이터를 가져오는 함수이지만, 첫 번째 함수와는 달리 데이터에 대한 사용자들의 중복 접근을 방지하는 함수입니다. Firebase가 제공하는 NoSQL 데이터베이스는 중복 입력에 대한 보호막이 존재하지 않아, 댓글 갯수 카운트 혹은 좋아요 수 카운트 기능을 구현할 시, 갯수가 중복으로 카운트 되는 경우가 발생합니다. 이러한 상황을 방지하기 위해 runTransaction() 함수를 사용합니다.
DocumentSnapshot 객체 변수 snapshot에서 데이터를 추출할 수 있습니다.

```java
FirebaseFirestore firestore = FirebaseFirestore.getInstance();

firestore.collection(/* collection 이름 */).document(/* document 이름 */)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            // ...

                            notifyDataSetChanged();
                        }
                    });
```
앞의 두 함수는 pull driven 형식의 서비스를 제공했던 반면, 바로 위의 함수 'addSnapshotListener()' 함수는 push driven 형식의 서비스를 제공합니다.
서버에서 데이터가 변형되거나 업데이트 될 때마다 실시간으로 동기화하여, 데이터의 변화를 사용자에게 보여줍니다. QuerySnapshot 객체 변수인 value에서 
데이터를 추출할 수 있습니다.

---

</div>
</details>

#### 2. 데이터 쓰기 

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">
   
```java
FirebaseFirestore firestore = FirebaseFirestore.getInstance();

firestore.collection(/* collection 이름 */).document(/* document 이름 */).set(/* 데이터 */)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 데이터 저장에 성공했을 때
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 저장에 실패했을 때
                    }
                });
                    
```
서버에 데이터를 입력하는 함수이며, set()부분에 입력할 데이터 변수가 삽입됩니다.

```java
FirebaseFirestore firestore = FirebaseFirestore.getInstance();

firestore.collection(/* collection 이름 */).document(/* document 이름 */).update("/* field 이름 */", /* 입력할 데이터 */)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 데이터 저장에 성공했을 때
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 저장에 실패했을 때
                    }
                });                 
```

서버에 저장되어 있는 데이터 모델의 필드 값 중 일부만 수정하고 싶은 경우, 위 코드와 같이 update 함수를 사용하여 수정할 수 있습니다.

---
 
</div>
</details> 

#### 3. 계정 관련 기능

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```java
 FirebaseAuth auth = FirebaseAuth.getInstance();

 auth.createUserWithEmailAndPassword(/* 사용자 이메일 */, /* 사용자 비밀번호 */)
                .addOnCompleteListener(MakeAccount.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 성공 했을 때
                        }
                        else{
                            // 실패 했을 때
                        }
                    }
                });
```

위 함수는 사용자의 계정을 생성해 주는 함수입니다. 함수의 인수에 사용자의 이메일과 비밀번호를 매개변수로 넣어주면 함수가 firebase와 통신하며
사용자의 계정을 생성합니다.

```java
 FirebaseAuth auth = FirebaseAuth.getInstance();
 
 auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   // 성공했을 때...
                } else {
                    // 실패했을 떄...
                }

                // ...
            }
        });
```

위 함수는 로그인 기능을 구현하는 역할을 합니다. signInWithEmailAndPassword() 함수 내 매개변수로 사용자의 이메일 아이디와 비밀번호를 입력하면
firebase가 서버에 저장된 계정 데이터와 대조하여 사용자를 로그인 시키거나, 접근 제한을 시킵니다.

---
   
</div>
</details> 

#### 4. Storage

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```java
FirebaseStorage storage = FirebaseStorage.getInstance();

final StorageReference storageRef =
                            storage.getReferenceFromUrl(/*저장소 주소*/).child(/*폴더 이름*/).child(/*파일 이름*/);
                    UploadTask uploadTask = storageRef.putFile(/*다운로드 주소*/);

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return storageRef.getDownloadUrl(); // 이미지의 다운로드 주소 추출
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri uri = task.getResult();
                            
                            // 저장을 성공한 후...
                        }
                    });
```

위 함수는 storage에 사진 파일을 저장하는 역할을 합니다. StorageReference 객체에 저장소 참조를 선언한후, 저장소 주소와, 이미지 파일 이름, 저장할 폴더 이름 등을
데이터로 넣어줍니다. 이후 putFile() 함수를 사용하여 이미지 데이터를 저장소에 저장합니다. 이후, 저장한 이미지의 다운로드 주소를 추출할려면 위 코드에서 볼 수 있는 것처럼
getDownloadUrl() 함수를 사용하여 추출합니다.


```java
FirebaseStorage storage = FirebaseStorage.getInstance();

StorageReference httpsReference = storage.getReferenceFromUrl(/*이미지의 다운로드 주소*/);
httpsReference.delete();
```

위 코드는 저장소에 저장된 이미지 파일을 삭제하는 코드입니다. StorageReference에 이미지를 다운로드 받을 수 있는 주소 데이터를 넣어 storage 참조를 선언한후,
delete() 함수를 사용하여 이미지 파일을 삭제합니다.

---
   
</div>
</details>


#### 5. 참고 문헌

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

이 항목은 WIA를 개발하면서 참고하였던 firebase 개발 문서 링크를 다루고 있으며, 아래 링크들을 통해 firebase 함수의 활용을 더욱 심화적으로
알 수 있습니다.

[Cloud Firestore로 데이터 가져오기](https://firebase.google.com/docs/firestore/query-data/get-data?hl=ko#%EC%9E%90%EB%B0%94)  
[Cloud Firestore에 데이터 쓰기](https://firebase.google.com/docs/firestore/manage-data/add-data?hl=ko) 
[Cloud Firestore에 데이터 추가](https://firebase.google.com/docs/firestore/manage-data/add-data?hl=ko)  
[Firestore로 실시간 업데이트 가져오기](https://firebase.google.com/docs/firestore/query-data/listen?hl=ko)  
[트랜잭션 및 일괄 쓰기](https://firebase.google.com/docs/firestore/manage-data/transactions?hl=ko#%EC%9E%90%EB%B0%94_4)  
[Cloud Firestore에서 단순 쿼리 및 복합 쿼리 실행](https://firebase.google.com/docs/firestore/query-data/queries?hl=ko)  
[Cloud Firestore의 색인 유형](https://firebase.google.com/docs/firestore/query-data/index-overview?hl=ko)  
[Cloud Firestore에서 데이터 삭제](https://firebase.google.com/docs/firestore/manage-data/delete-data?hl=ko)

[Android에서 Firebase 인증 시작하기](https://firebase.google.com/docs/auth/android/start)  
[Firebase에서 사용자 관리하기](https://firebase.google.com/docs/auth/android/manage-users)  
[Android에서 비밀번호 기반 계정으로 Firebase에 인증](https://firebase.google.com/docs/auth/android/password-auth?hl=ko)

[Android에서 Cloud Storage 시작하기](https://firebase.google.com/docs/storage/android/start)  
[Android에서 스토리지 참조 만들기](https://firebase.google.com/docs/storage/android/create-reference)  
[Android에서 파일 업로드](https://firebase.google.com/docs/storage/android/upload-files)  
[Android에서 파일 다운로드](https://firebase.google.com/docs/storage/android/download-files)  
[Android에서 파일 삭제](https://firebase.google.com/docs/storage/android/delete-files)

---

</div>
</details>

</div>
</details>

## 4. 서비스 구조 및 코드 설명

이 항목에서는 WIA의 레이아웃 구조와, 각 자바 파일의 역할, 코드에 대한 자세한 설명 등을 다루고 있습니다. 

### 1) 로그인 및 회원 가입 기능

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

</div>
</details>

### 2) MainActivity.java

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">
   
![MainActivity](https://raw.githubusercontent.com/osamhack2020/APP_WIA_ONANDON/master/API_image/MainActivity.jpg)

MainActivity는 앱의 가장 큰 클이자, 기본적인 구성을 담당하며, bottomnavigation view로 이벤트를 받아서, 화면 대부분을 차지하고 있는 'main_content' framelayout에
프래그먼트를 교체해 주는 작업을 해줍니다. 위 사진에서 볼 수 있듯이, 하단에는 4개의 bottomnavigation 버튼이 있으며, 버튼을 누를 때마다 위의 framelayout에 적절한
프래그먼트를 교체해 줍니다. 첫 번째 버튼은 HomeFragment, 두 번째 버튼은 PlanFragment, 세 번째 버튼은 DashboardFragment, 네 번째 버튼은 AlarmFragment로 교체해 줍니다.

```java
class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();

            // BottomNavigationView의 하단 버튼을 누를 때 마다 화면 이동을 지정
            switch(id){
                case R.id.navigation_home :
                    HomeFragment homeFragment = new HomeFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, homeFragment)
                            .commit();
                    return true;
                case R.id.navigation_plan :
                    PlanFragment planFragment = new PlanFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, planFragment)
                            .commit();
                    return true;
                case R.id.navigation_dashboard :
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, dashboardFragment)
                            .commit();
                    return true;
                case R.id.navigation_notifications :
                    NotificationFragment notificationFragment = new NotificationFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_content, notificationFragment)
                            .commit();
                    return true;
            }
            return false;
        }
    }
```

위 코드는 bottomnavigation 리스너 코드이며, 버튼을 누를 때 마다 해당 fragment로 교체해 줍니다. switch 문으로 버튼의 케이스를 나누어
클릭 이벤트를 처리하고 있습니다.

MainActivity는 이것 이외에도, 사요자에게 내부 저장소에 접근할 수 있는 권한 허용을 요청하는 역할과, 사용자의 푸쉬 토큰을 서버에 저장하는 역할을 합니다.

```java
// 사용자에게 권한 허가를 받는 함수
    public void checkPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        else{
            for(String permission : permission_list) {
                int chk = checkCallingOrSelfPermission(permission);
                if (chk == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(permission_list, 0);
                }
            }
        }
    }
```

안드로이드 버전이 마쉬멜로우 이상이면 내부저장소에 접근할 수 있는 권한을 사용자로부터 허가 받습니다. 이미 권한이 허용된 상태이면,
권한을 묻지 않고, 함수를 종료시킵니다.

```java
// 푸시알림을 위해 사용자의 토큰을 서버에 저장
    public void registerPushToken(){
        String pushToken = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MyToken myToken = new MyToken();
        myToken.pushtoken=pushToken;
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid).set(myToken);
    }
```

사용자의 푸쉬 토큰을 발급받아, 'pushtokens'로 지정된 collection에 토큰 정보를 저장합니다. 저장된 푸쉬 토큰은 서버로 부터 푸쉬 알림을 받을 때 사용됩니다.

</div>
</details>

### 3) HomeFragment.java

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">
   
HomeFragment는 앱을 처음 작동시켰을 때 나오는 메인 화면을 담당하며, 사용자가 즐겨찾기로 등록한 게시판 목록과, WIA가 제공하는 여러 서비스 들을 시작적으로
제시합니다. 

![HomeFragment](https://raw.githubusercontent.com/osamhack2020/APP_WIA_ONANDON/master/API_image/HomeFragment.jpg)

위 사진은 HomeFragment의 구성을 간략히 그림으로 정리한 것입니다.

```java
FragmentManager manager = getChildFragmentManager();
                PagerAdapter pagerAdapter = new PagerAdapter(manager, frag_list.size());
                pager.setAdapter(pagerAdapter);
                pager.setCurrentItem(0);

                // viewpager 양쪽 미리보기 설정
                int dpValue = 30;
                float d = getResources().getDisplayMetrics().density;
                int margin = (int) (dpValue * d);

                pager.setClipToPadding(false);
                pager.setPadding(margin, 0, margin, 0);
                pager.setPageMargin(margin/2);
```

HomeFragment의 상단에는 주요게시판을 사용자에게 노출시키는 viewpager가 위치하고 있으며, 위 코드는 viewpager를 구성하는 코드입니다.
양쪽 미리보기를 설정하여 사용자로 하여금 화면에 가려진 부분에도 뷰가 있다는 것을 직관적으로 알려주고 있습니다.

viewpager 아래에는 BudaePost.java와 TotalBudaePost.java, 총 2개의 FrameLayout이 위치하고 있으며, 이 레이아웃은
사용자가 즐겨찾기로 등록한 게시판의 목록을 recyclerview로 보여줍니다. BudaePost.java는 부대게시판으로, 사용자와 같은 부대에 소속된
사용자들 간의 커뮤티니를 제공하며, TotalBudaePost.java가 제공하는 전체게시판은 모든 군인 장병들이 공유하는 커뮤니티를 제공합니다.

Framelayout 내에 위치하고 있는 각 게시판 객체는 가장 최근에 업로드 된 게시물의 내용을 미리보기로 보여주며, 글이 새로 올라올 때마다
new 아이콘을 노출시키면서 사용자에게 알려줍니다.

```java
firestore.collection("Activity").orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("new", Context.MODE_PRIVATE);
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.size() == 0){
                
                    // 게시물이 없으면 미리보기에 '게시물이 없습니다'로 표시합니다.
                    activityPreview.setText("게시물이 없습니다.");
                    activityNewPost.setVisibility(View.GONE);
                }
                for(QueryDocumentSnapshot doc : value) {
                    ActivityDTO activityDTO = doc.toObject(ActivityDTO.class);
                    activityPreview.setText(activityDTO.explain);

                    // 사용자의 폰에는 게시판에 마지막으로 접근한 시간 정보가 SharedPreferences로 저장되어 있습니다.
                    // 이 시간 정보가 새로 올라온 게시물의 업로드 시간 보다 작으면 new 표시를 노출시킵니다.
                    if(activityDTO.timestamp > sharedPreferences.getLong("Activity", 0)){
                        activityNewPost.setVisibility(View.VISIBLE);
                    }else{
                        activityNewPost.setVisibility(View.GONE);
                    }

                    sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                            if(s.equals("Activity")){
                                activityNewPost.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
```

위 코드는 게시판 한개의 미리보기를 담당하는 코드이며, 타 게시판 또한 같은 구조로 코드가 구성되어 있습니다.
firestore에서 가장 최근 게시물 한개에 접근하여, 게시물 정보를 activityDTO 객체 변수로 받습니다.
이 객체 변수에 저된 게시물의 내용을 미리보기 TextView 'activityPreview'에 집어넣습니다.

사용자의 핸드폰에는 게시판에 대한 마지막 접근 시간 정보를 SharedPreference 객체 내에 저장하고 있으며, 이 시간 정보와
가장 최근에 올라온 게시물의 업로드 시간 정보를 비교하여 new 표시의 노출 여부를 결정합니다.

---
  
</div>
</details>

### 3) BudaePost.java

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

BudaePost 프래그먼트는 사용자가 즐겨찾기로 등록한 게시판 목록을 recyclerview로 표시하며, TotalBudaePost 또한 같은 구조를 취하고 있습니다.

![BudaePost](https://raw.githubusercontent.com/osamhack2020/APP_WIA_ONANDON/master/API_image/BudaePost.jpg)

recylerview의 각 아이템을 클릭하면 해당 게시판을 담당하는 PostListFrame.java로 이동하게 됩니다. 게시판에 따라 ClubActivity.java, ActivityFrame.java로 이동하기도 하지만
기본적인 구조는 모두 같습니다.

```java
RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.budae_post_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());
```
리사이클러 뷰를 선언하는 코드입니다. DetailRecyclerViewAdapter를 어뎁더로 받고 있습니다.

```java
DetailRecyclerViewAdapter() {
            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            firestore.collection(collection).orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            contentDTOs.clear();
                            contentUidList.clear();

                            if (value == null) return;
                            for (QueryDocumentSnapshot doc : value) {
                                BoardDTO item = doc.toObject(BoardDTO.class);
                                if(item.clip.containsKey(user.getUid())){
                                    contentDTOs.add(item);
                                    contentUidList.add(doc.getId());
                                }
                            }

                            // 서버에 저장된 게시판 정보가 바뀔 때 마다 리스트뷰를 새롭게 그린다.
                            notifyDataSetChanged();
                        }
                    });
        }
```
위 코드는 리사이클러 뷰의 어뎁터 'DetailRecyclerViewAdapter'의 생산자 코드입니다. BudaePost.java는 생산자 내부에 화면에 표시할 게시판 목록 정보를
서버로 부터 받아오는 코드를 위치시키고 있습니다. addsnapshotListener를 사용하여 수신대기를 통한 실시간 업데이트 기능을 구현하고 있으며 서버로 부터 받아온
게시판 정보를 BoardDTO 객체인 item 변수로 받은 후, item 변수를 'contentDTOs' arraylist에 업로든 순으로 집어 넣고 있습니다.

리사이클러뷰는 contentDTOs를 활용하여 화면에 뷰를 표시하게 됩니다. 'contentUidList' arraylist에는 서버에서 접근한 하위 document의 고유 Id를 저장하고 있습니다.

```java
public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budae_post_item, parent, false);
            return new CustomViewHolder(view);
        }
```

리사이클러뷰에 각 뷰의 UI를 담당할 레이아웃 파일 정보를 넣어줍니다. 위 코드에서는 R.layout.budae_post_item 레이아웃 파이을 활용하여 하나의 뷰를
표시하고 있습니다.

```java
@Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final BudaePostItemBinding binding = ((CustomViewHolder) holder).getBinding();

            // ...

            binding.budaeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                
                    // 해당 게시판으로 이동
                    Intent intent = new Intent(getContext(), PostListFrame.class);
                    intent.putExtra("name", contentDTOs.get(position).name);
                    intent.putExtra("explain", contentDTOs.get(position).explain);
                    intent.putExtra("documentUid", contentUidList.get(position));
                    intent.putExtra("manager", contentDTOs.get(position).manager);
                    startActivity(intent);
                    
                    // ...
                    
                    }
                }
            });
        }
```

onBindViewHolder는 서버로 부터 받아온 정보를 레이아웃의 각 변수에 바인딩 시켜 리사이클러뷰 내에서 직접적으로 뷰를 생성하는 함수입니다.
각 뷰는 클릭 이벤트 리스너가 설정되어 있어, 뷰를 누를 경우 해당 게시판 화면을 담당하고 있는 PostListFrame.class로 이동되도록 구성되어 있습니다.

 이름 | 정보 | 설명
------------ | ------------- | -------------
|"name" | contentDTOs.get(position).name | BoardDTO.java|
|"explain"| contentDTOs.get(position).explain  | BoardDTO.java |
|"documentUid"| contentUidList.get(position) | BoardDTO.java |
|"manager"| contentDTOs.get(position).manager | BoardDTO.java |

