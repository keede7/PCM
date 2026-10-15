# PCM

JWT 인증 기반 TODO API Server

## 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| Language / Build | Java 21, Gradle |
| Framework | Spring Boot 4.1.1 (Spring Web MVC, Validation) |
| 인증 | Spring Security, OAuth2 Resource Server (Nimbus JOSE JWT, HS256), BCrypt |
| 데이터 | Spring Data JPA (Hibernate 7), SQLite (`hibernate-community-dialects`) |
| 테스트 | JUnit 5, Mockito, AssertJ, MockMvc, Spring Security Test |

## 실행 방법

Java 21이 필요합니다.

```bash
./gradlew bootRun
```

- 기본 포트: `8080`
- DB: 프로젝트 루트의 `pcm.db` (SQLite, 실행 시 테이블 자동 생성)
- JWT 서명 키는 환경변수 `JWT_SECRET`(32바이트 이상)으로 지정합니다. 미지정 시 로컬 개발용 기본값이 사용되며, 32바이트 미만이면 기동 시 실패합니다.

```bash
JWT_SECRET=your-32-bytes-or-longer-secret-key-value ./gradlew bootRun
```

## 테스트 실행 방법

```bash
./gradlew test
```

- 테스트는 `test` 프로필로 실행되며 별도 DB(`build/pcm-test.db`)를 사용합니다.
- PRD 필수 시나리오 테스트
  1. 회원가입 → 로그인 → `/users/me` 조회: `UserApiTest`
  2. TODO 생성 → 목록 조회 → 수정 → 삭제: `TodoApiTest`
  3. 토큰 없이 요청 시 401: `UserApiTest`, `TodoApiTest`
  4. 존재하지 않는 TODO ID 요청 시 404: `TodoApiTest`

## API 목록

인증이 필요한 API는 `Authorization: Bearer <access_token>` 헤더를 사용합니다.

- 토큰 만료: 발급 후 1시간
- 회원 탈퇴 시 해당 사용자에게 발급된 모든 토큰이 즉시 만료됩니다. (토큰에 사용자별 토큰 키를 담아 요청마다 검증)

### 사용자

| Method | Path | 설명 | 인증 | 성공 응답 |
|--------|------|------|------|-----------|
| POST | `/users/signup` | 회원가입 | – | 201 |
| POST | `/users/login` | 로그인, `{ "access_token": "..." }` 반환 | – | 200 |
| GET | `/users/me` | 내 정보 조회 | 필요 | 200 |
| PUT | `/users/me` | 회원정보 수정 (이름, 비밀번호는 입력 시에만 변경) | 필요 | 200 |
| DELETE | `/users/me` | 회원 탈퇴 (본인 TODO 함께 삭제, 발급된 모든 토큰 만료) | 필요 | 204 |

### TODO

로그인한 사용자 본인의 TODO만 다룹니다.

| Method | Path | 설명 | 인증 | 성공 응답 |
|--------|------|------|------|-----------|
| POST | `/todos` | 생성 | 필요 | 201 |
| GET | `/todos` | 내 목록 조회 | 필요 | 200 |
| GET | `/todos/search?keyword=` | 제목으로 검색 | 필요 | 200 |
| GET | `/todos/{id}` | 단건 조회 | 필요 | 200 |
| PUT | `/todos/{id}` | 수정 | 필요 | 200 |
| DELETE | `/todos/{id}` | 삭제 | 필요 | 204 |

### 요청 예시

```bash
# 회원가입
curl -X POST localhost:8080/users/signup -H 'Content-Type: application/json' \
  -d '{"email": "user@pcm.io", "password": "password123", "name": "keede"}'

# 로그인
curl -X POST localhost:8080/users/login -H 'Content-Type: application/json' \
  -d '{"email": "user@pcm.io", "password": "password123"}'

# TODO 생성
curl -X POST localhost:8080/todos -H "Authorization: Bearer <access_token>" \
  -H 'Content-Type: application/json' -d '{"title": "장보기", "content": "우유, 계란"}'

# TODO 수정
curl -X PUT localhost:8080/todos/1 -H "Authorization: Bearer <access_token>" \
  -H 'Content-Type: application/json' -d '{"title": "장보기", "content": "우유", "completed": true}'
```

### 응답 형식

- 일시: `yyyy-MM-dd HH:mm:ss` (예: `2026-10-14 19:57:50`), 날짜: `yyyy-MM-dd`
- DB 저장 시 일시는 `yyyy-MM-ddTHH:mm:ss` (ISO-8601) 형식의 문자열로 저장됩니다.

```json
{
  "id": 1,
  "title": "장보기",
  "content": "우유, 계란",
  "completed": false,
  "createdAt": "2026-10-14 19:57:50",
  "updatedAt": "2026-10-14 19:57:50"
}
```

### 에러 응답

모든 에러는 `{ "code": "...", "message": "..." }` 형식으로 응답합니다.

| Status | code | 상황 |
|--------|------|------|
| 400 | `INVALID_INPUT` | 요청 값 검증 실패 |
| 401 | `UNAUTHORIZED` | 토큰 없음, 만료, 위조, 탈퇴한 사용자의 토큰 |
| 401 | `INVALID_CREDENTIALS` | 이메일 또는 비밀번호 불일치 |
| 404 | `USER_NOT_FOUND` | 사용자 없음 |
| 404 | `TODO_NOT_FOUND` | 존재하지 않거나 본인 소유가 아닌 TODO |
| 404 | `RESOURCE_NOT_FOUND` | 존재하지 않는 경로 |
| 405 | `METHOD_NOT_ALLOWED` | 지원하지 않는 HTTP 메서드 |
| 409 | `DUPLICATE_EMAIL` | 이미 가입된 이메일 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |

그 외 HTTP 상태를 가진 요청 오류(예: 415)는 `code`에 상태명(`UNSUPPORTED_MEDIA_TYPE` 등)을 담아 응답합니다.
