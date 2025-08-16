# ⚡ reactive-webflux-api

---
## 📖 프로젝트 개요

**reactive-webflux-api** 는 spring 기반의 reactive api ( WebFlux )를 이해하기 위한 예제 프로젝트로서 h2 데이터베이스 기반 실 데이터를 활용하여 Service 로직에 대한 행위 테스트를 진행합니다.

---

## 🛠 주요 로직

 
1. **RestController** 형태의 요청-응답 처리
2. 결과를 **Mono, Flux** 로 전달
3. **H2 DB** 데이터베이스에서 사용자 데이터 조회 및 체크
4. 데이터베이스 기반 **사용자 데이터**를 활용한 서비스 테스트
5. **Spring Security + JWT** 기반 인증·인가
   

---

## 🏗 아키텍처 및 기술 스택

### 백엔드

* **언어 & 프레임워크:** Java 23, Spring Boot 3
* **API 문서화:** SpringDoc OpenAPI
* **인증/인가:** Spring Security, JWT

---

## 🚀 설치 및 실행

1. 저장소 클론

   ```bash
   git clone https://github.com/eschoeDeveloper/reactive-webflux-api.git
   cd reactive-webflux-api
   ```

2. API 문서 확인

   ```text
   http://localhost:8090/swagger-ui.html
   ```

---

## 📂 프로젝트 구조

```
├── data
│   ├── reactive-webflux-db # 예제 사용자 데이터
├── src/main/java/io/github/eschoe/reactivewebfluxapi
│   ├── config       # Config 클래스
│   ├── controller   # API 엔드포인트
│   ├── service      # 비즈니스 로직
│   ├── repository   # H2 DB 데이터 연동
│   ├── exception    # 예외 처리
│   ├── security     # 인증/인가 설정
│   └── dto          # 요청/응답용 객체
├── src/main/resources
│   ├── application.yaml # 애플리케이션 설정 파일
```

---

## 🤝 연락처

* **GitHub:** [github.com/eschoeDeveloper/reactive-webflux-api](https://https://github.com/eschoeDeveloper/reactive-webflux-api)
* **Email:** [develop.eschoe@gmail.com](mailto:develop.eschoe@gmail.com)

---

## 📜 라이선스

Apache License 2.0 © 2025 ChoeEuiSeung
