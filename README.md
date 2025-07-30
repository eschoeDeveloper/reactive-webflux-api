# ⚡ reactive-mock-api

---
## 📖 프로젝트 개요

**reactive-mock-api** 는 spring 기반의 reactive api ( WebFlux )를 이해하기 위한 예제 프로젝트로서 실 데이터 대신
예제 데이터를 활용하여 response를 하며, 요청은 swagger로 할 수 있도록, api docs를 구성하였습니다.
또한, Mockito를 활용하여 실제 Service 로직에 대한 행위 테스트를 진행합니다.

---

## 🛠 주요 로직

 
1. **RestController** 형태의 요청-응답 처리
2. 결과를 **Mono, Flux** 로 전달
3. **Mockito**를 활용한 서비스 테스트
4. **Spring Security + JWT** 기반 인증·인가
   

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
   git clone https://github.com/eschoeDeveloper/reactive-mockito-api.git
   cd reactive-mockito-api
   ```

2. API 문서 확인

   ```text
   http://localhost:8090/swagger-ui.html
   ```

---

## 📂 프로젝트 구조

```
├── src/main/java/io/github/eschoe/reactivemockapi
│   ├── config       # Config 클래스
│   ├── controller   # API 엔드포인트
│   ├── service      # 비즈니스 로직
│   ├── repository   # Mockito 데이터 연동
│   ├── exception    # 예외 처리
│   ├── security     # 인증/인가 설정
│   └── dto          # 요청/응답용 객체
├── src/main/resources
│   ├── application.yaml # 애플리케이션 설정 파일
```

---

## 🤝 연락처

* **GitHub:** [github.com/eschoeDeveloper/reactive-mock-api](https://https://github.com/eschoeDeveloper/reactive-mockito-api)
* **Email:** [develop.eschoe@gmail.com](mailto:develop.eschoe@gmail.com)

---

## 📜 라이선스

Apache License 2.0 © 2025 ChoeEuiSeung
