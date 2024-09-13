## 📒프로젝트 개요
Spring Boot 를 기반으로한 SNS 플랫폼 api 개발 프로젝트

## 🧑🏻‍💻 개발환경
- `java version 17`
- `Spring Boot 3.1.1`
- `Spring Security`
- `SQLite`
- `IntelliJ`
- `MacOS`

## ⚙️ 구현 기능
### DB ERD
<img src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/b8948053-f239-4dd4-989a-bf93e063575c" width="900" height="500">

## 회원 기능
### 회원 가입, 로그인, 사용자 프로필 이미지 업로드, 인증 및 JWT 발급
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 회원 가입 | POST | /auth/register
 로그인 | GET | /auth/login
프로필 이미지 업로드 | PUT | /auth/profile/image

#### 회원 가입, 로그인
- 회원 가입
    - 사용자는 회원가입 시 ID, PW, 확인용 PW 를 필수로 입력해 회원가입 진행 (ID, PW 에 대한 validation 진행)
      - ID (아이디는 소문자로만 시작 가능합니다 | 사용 가능 문자 : 영소문자, 숫자 | 글자수 제한 : 최소 6자에서 15자)
      - password (비밀번호는 영문 숫자 특수문자를 모두 포함해야 합니다 | 사용 가능 문자 : 영대소문자, 숫자, 특수문자 (`~₩!@#$%^&*) | 글자수 제한 : 최소 8자에서 20자)
      - realName (2 글자 이상의 이름)
      - email (~~~~@~~~.~~~ 형식)
      - phone (01[0 | 1 | 6 ~ 9]-[3자리 혹은 4자리]-[4자리], "-"은 생략 가능)
    - 사용자는 회원가입 시 필수 데이터를 제외하고 실명, 이메일, 전화번호, 주소와 같은 부수적인 정보를 함께 입력해 회원가입 가능
    - 회원 가입 시 사용자의 데이터를 받는 Dto 를 UserDetails 로 변환해 create 진행
    - 회원 가입 시 입력한 비밀번호는 encoding 을 진행 후 저장
    - 회원 가입 시 인증 절차 불필요 (ignoring 진행)
    - 회원 가입 완료 시 사용자에게 회원 가입 완료 메세지와 HttpStatusCode(200, OK) 반환
- 로그인 및 JWT 발급
    - 사용자가 입력한 ID, PW 를 기반으로 로그인 진행
      - ID, PW 필수 기입 사항
    - ID 를 통해 DB 에서 사용자 정보 조회 후 비밀번호 검증
    - 검증이 완료된 사용자에게 ID 정보와 생성 시간, 만료 시간을 담은 Jwt 발급
    - 로그인 시 인증 절차 인증 절차 불필요 (ignoring 진행)
    - 로그인 완료 시 사용자에게 Jwt 와 HttpStatusCode(200, OK) 반환
- 사용자 프로필 이미지 업로드
  - 로그인을 완료한 사용자가 프로필 이미 업로드 요청
  - 사용자가 요청을 보낼 때 포함한 Jwt token 검증
  - 검증이 완료된 Jwt 에서 사용자 정보를 추출해 ContextHolder 에 저장
  - ContextHolder 에 저장된 사용자 정보를 바탕으로 요청 처리
  - 요청 Body 에 포함된 이미지를 images/profile/ID/ 에 생성시간_ID.확장자 형태로 저장
  - 인증 객체에 포함된 사용자 정보를 바탕으로 DB 조회
  - 조회된 사용자 데이터에 profile image path 를 셋팅 후 update
  - profile image 요청을 위한 path 는 localhost:8080/profile/ID/filename
  
#### 인증
- 서버에서 발급한 JWT 의 유효성을 jwtTokenFilter 를 통해 검증
- 유효한 Jwt 에서 ID 를 추출해 SecurityContext 에 저장 후 ContextHolder 에 담는다 (추후 인증된 사용자의 정보가 필요한 경우 활용)
- 기본적으로 ContextHolder 는 Thread 내에서 전역적으로 사용이 가능
- JWT 유효성 검증하는 과정에서 발생하는 예외를 커스텀해서 처리하니 예외가 발생하면 그대로 요청이 중단됨
- 회원 가입, 로그인과 같은 인증이 불필요한 요청에 대해서 permitAll 처리를 해주었지만 permitAll 한 요청은 인증이 되지 않아도 resource 를 제공받을 수 있다는 의미지 Filter 를 무시하는 것이 아니기 때문에 로그인과 회원 가입 시에도 Token 이 존재하지 않으면 요청이 중단됨.
- 인증이 불필요한 요청에 대해 ignore 처리를 해주었지만 Filter 를 Component 어노테이션을 활용해 등록하게 되면서 ApplicationFilterChain 과 SecurityFilterChain 양쪽에 등록이 되면서 SecurityFilterChain 에서는 무시를 하지만 ApplicationFilterChain 에서 걸리게 됨.
- ApplicationFilterChain 에 등록된 Filter 를 사용하지 않게 설정하는 것으로 해결

#### 추가적으로 진행할 사항
- 현재 user 에 대한 정보를 담는 UserDetails 와 User 관련 service 를 처리하는 UserDetailsManager 를 구현해 사용
- form-login 에 경우 사용자가 입력한 ID 와 PW 를 통해 UsernamePasswordAuthenticationFilter 에서 인증용 UsernamePasswordAuthenticationToken 을 생성해
- AuthenticationManager 의 authenticate 메서드에 인증용 토큰을 담아 providerManager 호출
- 인증용 토큰을 넘겨 받은 providerManager 는 인증요청을 처리할 수 있는 provider(DaoAuthenticationProvider) 를 찾아 다시 인증용 토큰을 전달
- 토큰을 받은 provider 는 UserDetailsService 의 loadUserByUsername 메서드를 활용해 토큰에 담긴 ID 에 해당하는 UserDetails 객체를 반환받아
- 인증 절차를 진행하 인증에 성공하면 인증 정보를 ContextHolder 에 담아 successHandler 를 실행, 실패 시 Exception 발생
- UserDetails 와 Manager 는 전적으로 form 로그인 방식에서 인증 과정을 처리하는데 필요하기 때문에 구현한 것이다.
- 하지만 현재는 Jwt 를 활용해 로그인과 인증을 진행하고 JwtFilter 에서 인증 결과를 바탕으로 예외를 발생하거나 인증 정보를 ContextHolder 에 저장하기 때문에
- UserDetails 와 Manager 의 기능을 Jwt 인증과정에서 사용하지 않기 때문에 굳이 해당 형태로 구현할 이유가 없다고 판단된다.
- UserDetails 는 Dto 로 바꾸고 Manager 는 loadUserByUsername 이 필요하기 때문에 Manager 의 구현체가 아닌 일반적인 UserService 로 변환해도 괜찮겠다는 생각

## 피드 기능
### 피드 생성, Target User 피드 전체 조회, 피드 단일 조회, 피드 수정, 피드 삭제
 기능 | HTTP METHOD | URL
 --- |-------------| ---
 피드 생성 | POST        | /article
 전체 조회 | GET         | /article?targetUser={targetUser}
단일 조회 | GET         | /article/{articleId}
피드 수정 | PUT         | /article/{articleId}
피드 삭제 | DELETE      | /article/{articleId}

#### 피드 생성
 - 피드 생성 기능
 - 인증이 필요한 요청
 - 인증이 완료된 사용자의 인증 객체에서 사용자 ID 추출 후 피드 생성
 - 피드를 등록하는 경우 제목과 내용은 필수 사항
 - 피드 이미지 등록은 선택 사항 (등록하는 경우 여러 장의 이미지 등록 가능)
   - 피드 이미지가 등록되지 않은 피드는 draft 컬럼을 활용해 이미지가 등록된 피드인지 구분
   - 이미지가 등록되지 않은 피드는 추후 조회 당시 기본 이미지가 반환

#### 피드 전체 조회
 - targetUser 의 피드를 전체 조회하는 기능
 - 인증이 불필요한 요청
 - requestParameter 를 통해 받은 targetUser 의 ID 를 기반으로 전체 피드 조회
 - targetUser 가 등록한 피드 중 삭제한 기록이 있는 피드는 제외
 - 작성자, 제목, 대표 이미지 (등록된 첫 이미지 없는 경우 기본 이미지) 반환

#### 피드 단일 조회
 - 피드 ID 를 기반으로 단일 피드를 조회하는 기능
 - 인증이 필요한 요청
 - 피드 ID 를 기반으로 피드를 조회, 단 삭제한 기록이 있는 피드인 경우 피드가 없다는 예외 메세지 반환
 - 작성자, 제목, 모든 이미지 (없는 경우 기본 이미지) 반환

#### 피드 수정
 - 피드 ID 를 기반으로 단일 피드 수정 기능
 - 인증이 필요한 요청
 - 피드 ID 를 기반으로 피드 조회, 단 삭제한 기록이 있는 피드인 경우 피드가 없다는 예외 메세지 반환
 - 조회된 피드를 작성한 사용자의 요청인지 확인
 - 조회된 피드를 사용자의 수청 요청을 보내면서 requestBody 에 추가한 정보를 바탕으로 수정 진행
   - 수정이 가능한 사항은 제목, 내용, 이미지 추가, 이미지 삭제
   - 사용자는 수정할 항목을 선택적으로 requestBody 에 추가, 이로인해 requestBody 가 전부 비어있는 상태로 요청이 올 수도 있음을 고려해
   - requestBody 가 전부 빈 수정 요청은 수정할 사항이 없다는 예외 메세지 반환
   - requestBody 의 제목, 내용, 추가할 이미지, 삭제할 이미지 항목에 데이터가 담겨있는지 한 요소씩 확인
   - 데이터가 담겨있음을 확인한 요소는 수정 진행
   - 이미지 삭제의 경우 사용자가 선택적으로 이미지를 삭제한다고 가정
     - 사용자가 피드를 조회했을 당시 반환된 이미지 resource 요청 url 을 포함한 경우 해당 이미지는 삭제할 이미지라고 가정
     - resource 요청 url 에서 파일 이름을 추출 후 실제 디렉토리 path 와 조합해 해당 이미지 삭제
     - 피드의 모든 이미지가 삭제된 경우 draft 컬럼 값을 변경해 등록된 이미지가 없음을 표시

#### 피드 삭제
 - 피드 ID 를 기반으로 단일 피드 삭제 기능
 - 인증이 필요한 요청
 - 피드 ID 를 기반으로 피드 조회, 단 삭제한 기록이 있는 피드인 경우 피드가 없다는 예외 메세지 반환
 - 조회된 피드를 작성한 사용자의 요청인지 확인
 - 해당 피드에 등록된 모든 이미지 조회
   - 조회된 모든 이미지를 삭제하고 디렉토리 삭제
 - 해당 피드를 실제로 삭제하지 않고 삭제된 시간만을 표시

## POSTMAN
### 회원 프로필 이미지 업로드
 - 프로필 이미지 업로드 요청 시에 requestBody(form-data) 가 빈 상태로 요청을 보내는 것을 주의해주세요
 - 해당 요청은 헤더의 content type 이 지정한 MediaType.MULTIPART_FORM_DATA_VALUE 과 일치할 때 동작합니다.
 - requestBody(form-data) 가 빈 상태로 요청을 보낼 시 헤더에 content type 이 포함되지 않아 예외를 발생시킬 수 있습니다.

*** 의도되지 않은 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/f0acbe43-114f-4134-a658-fa8c947384bf">

*** 예외는 발생시키지만 service 에서 판단 후 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/f4c95bbd-a09f-4bf5-9324-db88e1df1098">

### 피드 등록
 - 피드 등록 요청 시에 requestBody(form-data) 가 빈 상태로 요청을 보내는 것을 주의해주세요
 - 해당 요청은 헤더의 content type 이 지정한 MediaType.MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE 과 일치할 때 동작합니다.
 - requestBody(form-data) 가 빈 상태로 요청을 보낼 시 헤더에 content type 이 포함되지 않아 예외를 발생시킬 수 있습니다.

*** 의도되지 않은 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/cc8450ad-9fed-4a9d-acb2-fb8daba54f1a">

*** Validation 에서 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/a0793744-4266-42fa-8359-7999758a263d">

*** 정상 처리가 가능한 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/dd841cee-60a1-4388-8f58-defbe7ef2c1d">
<img width="1486" alt="스크린샷 2023-08-08 오후 11 13 28" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/a1369833-dfb1-489b-b46a-0e60d597b3e2">

### targetUser 의 피드 전체 조회
 - requestParameter 가 빈 상태로 요청을 보내는 것을 주의해주세요

*** 의도되지 않은 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/50345341-8c17-4bc9-be0c-e34558261ee5">

*** 예외는 발생시키지만 service 에서 판단 후 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/818ed60f-6634-449c-b47e-3f4bd18e878b">

### 단일 피드 수정
 - requestBody(form-data) 가 빈 상태로 요청을 보내는 것을 주의해주세요
 - 이유는 위와 동일합니다
 - 단일 피드 수정의 경우 이미지를 추가하고 싶다면 addImages Key 항목에 추가할 이미지를 첨부해 요청을 보내세요
 - 단일 피드 수정의 경우 이미지를 삭제하고 싶다면 deleteImages Key 항목에 삭제할 이미지의 접근 경로를 첨부해 요청을 보내세요(피드 조회 시 반환되는 imageUrl)
   - 여러 이미지를 동시에 삭제하고 싶다면 deleteImages Key 항목을 추가해 요청을 보내세요

*** 의도되지 않은 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/28d494a2-02d7-43ec-b04c-a303080768d8">

*** 예외는 발생시키지만 service 에서 판단 후 예외를 발생시키는 요청 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/b39e5ec2-1334-4eb3-b635-aa68c4e013f3">

*** 여러 장의 사진을 삭제하고 싶은 경우 ***
*** 삭제하고자 하는 사진은 read 요청의 결과로 반환되는 url 을 입력해주세요 ***
<img width="1486" alt="image" src="https://github.com/likelion-backend-5th/Project_2_LEEHEEJUN/assets/64578367/f0282013-f134-4524-be48-816bdae9b4e2">

## 추가 정보
[Postman Collection](https://www.notion.so/Spring-13646b407a5140c6b05c86400d94b63a)
해당 노션 페이지에 접속 후 SNS Project 토글을 열면 Postman Collection 이 존재합니다



 

