# CLAUDE.md
# 프로젝트: PCM

JWT 인증을 활용하여 TODO API Server를 개발한다.

## 기술 스택

- Java 21, Spring Boot 4.1.1, Gradle
- Spring Web MVC, Spring Validation
- Spring Data JPA (Hibernate), SQLite (`hibernate-community-dialects`)
- Spring Security + OAuth2 Resource Server (Nimbus JOSE JWT, HS256)
- 테스트: JUnit 5, Mockito, AssertJ, MockMvc, Spring Security Test

## 아키텍처 규칙

- `./docs/` 내 파일들을 준수한다.
- `./docs/Architecture.md` 를 준수한다. 

## 개발 프로세스

- CRITICAL: 새 기능 구현 시 테스트 먼저 작성 후 구현하며, `./docs/TODO.md` 를 참고하여야 한다. (TDD)
- CRITICAL: TDD 작업 이후 `./gradlew test` 를 통과할 때 까지 작업을 반복한다.
- CRITICAL: 작업 완료 후 `./docs/TODO.md` 에 작성하여 업데이트 한다.
- CRITICAL: 받은 지시사항과 그에 따른 최종결과를 ./prompt/user_prompts.md 에 작성한다.
- 커밋 메시지는 Prefix 형태로 선 작성하며 이후 내용을 한글로 작성한다. (`feat:`, `fix:`, `docs:`, `refactor:`, `chore:`)
- 추가 및 수정된 주요 기술스택을 해당 파일에 기재한다. 


## 그 외 준수사항

- CRITICAL: 모든 지시사항과 그에 따른 결과를 ./prompt/user_prompts.md 에 작성하며 그 외 파일에는 작성하지 않는다..