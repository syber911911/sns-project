# 멋쟁이 사자처럼 백엔트 스쿨 5기 - LEEHEEJUN
# 📮멋사SNS📮
Spring Boot Project - SNS 플랫폼 api

## 📒프로젝트 개요
Spring Boot 를 기반으로한 SNS 플랫폼 api 개발 프로젝트

## 🗓️ 프로젝트 기간
2023.08.03 ~ 2023.08.08

## 🧑🏻‍💻 개발환경
- `java version 17`
- `Spring Boot 3.1.1`
- `Spring Security`
- `SQLite`
- `IntelliJ`
- `MacOS`

## ⚙️ 구현 기능
### DB ERD
<img src="https://github.com/likelion-backend-5th/Project_1_LeeHeeJun/assets/64578367/2231d3b3-ea24-4bf5-8681-648076c2b3d6" width="500" height="500">

## DAY 1
### 회원 가입, 로그인, 사용자 프로필 이미지 업로드, 인증 및 JWT 발급
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 회원 가입 | POST | /auth/register
 로그인 | GET | /auth/login
프로필 이미지 업로드 | PUT | /auth/profile/image

#### 회원 가입, 로그인
- 회원 가입
    - 사용자는 회원가입 시 ID, PW, 확인용 PW 를 필수로 입력해 회원가입 진행 (ID, PW 에 대한 validation 진행)
    - 사용자는 회원가입 시 필수 데이터를 제외하고 실명, 이메일, 전화번호, 주소와 같은 부수적인 정보를 함께 입력해 회원가입 가능
    - 회원 가입 시 사용자의 데이터를 받는 Dto 를 UserDetails 로 변환해 create 진행
    - 회원 가입 시 입력한 비밀번호는 encoding 을 진행 후 저장
    - 회원 가입 시 인증 절차 불필요 (ignoring 진행)
    - 회원 가입 완료 시 사용자에게 회원 가입 완료 메세지와 HttpStatusCode(200, OK) 반환
- 로그인 및 JWT 발급
    - 사용자가 입력한 ID, PW 를 기반으로 로그인 진행
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

## 추가 정보
[Application.yaml & Postman Collection](https://www.notion.so/Spring-13646b407a5140c6b05c86400d94b63a)
해당 노션 페이지에 접속 후 Market Project2 토글을 열면 yaml 파일과 Postman Collection 이 존재합니다



 

