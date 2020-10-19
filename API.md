
# WIA API 개발 문서
WIA는 군장병들의 병영생활을 향상시킬 수 있는 종합 SNS 플랫폼입니다. 이 문서는 개발에 사용된 코드와 데이터 모델에 대한 자세한 설명과 함께, 시스템의 전체적인 구조를 다루고 있습니다. 
WIA가 제공하는 서비스에 대한 구체적인 설명은 What_is_WIA.pdf에서 확인하실 수 있으며, 이 문서를 일기 전에 먼저 정독하실 것을 추천드립니다.  

### 1. 개발 환경 및 사용 라이브러리
WIA는 안드로이드 스튜디오를 기반으로 개발되었으며, firebase를 활용하여 서버 및 DB를 구축하였습니다.

#### 사용언어
**Frontend**
* XML

**Backend**
* 자바
* firebase
---
#### 개발 환경

```gradle
android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"

   // ...
}
```

---
#### 사용 라이브러리

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

##### 상세설명
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

##### 그외 라이브러리
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
### 2. 데이터 모델
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

.

---
### 3. firebase를 활용한 DB와 서버 구축
이 항목은 WIA에서 활용하고 있는 DB의 구조와, 자주 사용된 firebase 함수에 대해 다루고 있습니다.

#### DB 구조

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
 
* 푸시 투큰 정보 DB 'PushTokens'

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
 
 * 부대 게시판 DB '*(부대이름)* 게시판'
 
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

* 해시태그 DB '*(게시판 고유 Id)*_tag'

collection | document | field
------------ | ------------- | -------------
|**0iqfcMLngZPEN9FqWxMtlqcTr5Q2_tag**| tag | TagDTO.java|

해시태그 DB는 게시판의 게시물들에 달린 모든 해시태그를 저장하고 있는 DB이며, 사용자가 해당 게시판에서 해시태그를 활용하여 특정 게시판을 검색할 때,
활용됩니다. 위 표는 위 항목에서 예시로 들었던 고민게시판의 해시태그 DB이며, *(고민게시판의 고유 Id)_tag*로 collection의 이름이 지정됩니다. 이 DB는 'tag'라는 이름의
document 하나로 이루어져 있으며, tagDTO 객체에 모든 해시태그 정보가 저장됩니다. 게시판이 새로 생성되면, 해당 게시판의 해시태그 DB도 함께 생성됩니다.

* 동아리 DB '*(부대이름)* 동아리'

collection | document | field
------------ | ------------- | -------------
|교육사동아리| cvUZhpXRLwKT8bRHO2CX | ClubDTO.java|
|└| hC17nTAGgtAxKWIIbcvo | ClubDTO.java|

