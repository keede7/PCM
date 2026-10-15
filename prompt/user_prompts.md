# User Prompts

## 2026-10-14 #1

### 지시사항

> 해당 프로젝트를 분석하고 요구사항에 맞춰 개발을 진행

### 결과

- `CALUDE.md`, `docs/PRD.md`, `docs/Architecture.md`, `docs/TODO.md` 분석 후 TDD로 개발 진행
  1. 테스트 선 작성 (fixture, 서비스 단위 테스트, PRD 필수 시나리오 통합 테스트) → 컴파일 실패(RED) 확인
  2. 구현 → `./gradlew test` 전체 통과(GREEN, 27개)
- 구현 내용
  - 의존성 추가: `spring-boot-starter-webmvc`, `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`, 테스트용 `webmvc-test`, `security-test`
  - 사용자 API: `POST /users/signup`, `POST /users/login`(`{ "access_token": "..." }`), `GET /users/me`
  - TODO API: `POST /todos`, `GET /todos`, `GET /todos/{id}`, `PUT /todos/{id}`, `DELETE /todos/{id}` (본인 TODO만 접근)
  - JWT(HS256, 만료 1시간) 기반 Stateless 인증, 401 응답 JSON 통일, 전역 예외 처리(`ErrorCode`)
  - `Todo`는 `userId` 값으로만 사용자를 참조 (물리적 FK 없음)
  - 테스트 프로필(`application-test.yml`)로 테스트 DB를 `build/pcm-test.db`로 분리
- 문서 업데이트: `docs/TODO.md`(구현 여부, 작업 이력), `docs/Architecture.md`(repository/security/auth 패키지), `CALUDE.md`(기술 스택)
- 기타: `.gitignore`의 `./*.db` 패턴이 동작하지 않아 `*.db`로 수정

## 2026-10-14 #2

### 지시사항

> 변경된 규칙에 준수하여 아키텍처를 변경하고 선택사항들 또한 개발을 진행할것

### 결과

- 변경된 규칙 반영 (리팩터링)
  - Repository를 각 도메인의 `entity/` 패키지로 이동 (`domains/{user,todo}/entity/*Repository`)
  - `Todo` → `User` 연관관계를 `@ManyToOne` + `@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))`로 변경 (기존 `userId` 값 참조 제거)
  - Service Layer에서 DTO로 변환하여 반환하도록 변경 → Controller는 Entity를 참조하지 않음
    - 로그인 토큰 발급도 `UserQueryService.login()`으로 이동
  - 지시사항/결과는 `user_prompts.md`에만 기록하도록 `docs/TODO.md`의 작업 이력 섹션 제거
- 선택 사항 구현 (TDD: 테스트 선 작성 → RED 확인 → 구현 → GREEN)
  - `GET /todos/search?keyword=` — 본인 TODO 중 제목에 키워드 포함 검색, 키워드 누락/공백 시 400
  - `PUT /users/me` — 이름 변경, 비밀번호는 입력 시에만 변경
  - `DELETE /users/me` — 회원 탈퇴. 물리적 FK가 없어 DB cascade가 없으므로 사용자의 TODO를 먼저 일괄 삭제 후 사용자 삭제
- 테스트: `./gradlew test` 전체 41개 통과
  - FK 미생성 검증 테스트를 시도했으나, SQLite 방언은 `NO_CONSTRAINT` 유무와 관계없이 FK를 생성하지 않아 검증 의미가 없어 제외
- `docs/TODO.md` 선택 사항 구현 여부 표기 업데이트

## 2026-10-14 #3

### 지시사항

> 수정해야 할 작업을 완료하고 커밋해라

### 결과

- 신규 규칙(`docs/Convention.md`) 반영: 인자가 2개 이상인 메서드/생성자 선언을 파라미터별 줄바꿈 + `final` 형태로 변경
  - 대상: Service, Controller, Entity, Repository, Security/예외/인증 지원 클래스, 테스트 헬퍼 메서드
  - record는 컴포넌트에 `final`을 붙일 수 없어 줄바꿈 형식만 적용 (`ErrorResponse`, `JwtProperties`, `UserResponse`)
- `./gradlew test` 전체 41개 통과
- 변경 사항을 Prefix 형식(`feat:`, `docs:`, `chore:`)으로 나누어 커밋

## 2026-10-14 #4

### 지시사항

> 최종 검토 이후 작업을 마무리하며 필요한 추가 작업이 있으면 작업 후 커밋한다

### 결과

- 신규 규칙 반영
  - `docs/Convention.md`: 일시는 `yyyy-MM-dd HH:mm:ss`, 날짜는 `yyyy-MM-dd` 형식으로 저장 및 응답
    - 응답: `JacksonConfig`에서 `LocalDateTime`/`LocalDate` 형식을 전역 지정
    - 저장: `LocalDateTimeConverter`/`LocalDateConverter`(autoApply)로 문자열 저장 (기존에는 epoch millis 정수로 저장되고 있었음)
    - `BaseTimeEntity`는 초 단위로 절삭하여 기록 (저장 값과 응답 값 일치)
  - `CLAUDE.md`: `README.md` 작성 (기술 스택, 실행 방법, 테스트 실행 방법, API 목록, 에러 응답)
- 검토 중 발견하여 수정
  - TODO 수정 응답의 `updatedAt`이 갱신 전 값으로 내려가던 문제 → 수정 후 flush 하여 반영
  - `spring.jpa.open-in-view` 비활성화 (Service Layer에서 DTO 변환하므로 불필요, 기동 경고 제거)
- 테스트: 일시 응답/저장 형식 검증 테스트 추가, `./gradlew test` 전체 43개 통과
- 참고: 검증용 서버 실행 중 사용자가 띄워둔 로컬 서버(8080)로 요청이 전달되어 `pcm.db`에 테스트 데이터가 생성되고, 해당 서버 프로세스가 종료됨. 생성된 테스트 데이터(사용자 1건, TODO 1건)는 삭제함
- 참고: 기존 `pcm.db`의 일시 컬럼은 정수(epoch millis)로 저장되어 있어 신규 형식으로 변환 필요

## 2026-10-14 #5

### 지시사항

> 다시 검토하고 마무리를 커밋을 진행한다.

### 결과

- 변경된 규칙 반영 (`docs/Convention.md`)
  - 일시 저장 형식을 `yyyy-MM-ddTHH:mm:ss`(ISO-8601)로 변경, API 응답은 `yyyy-MM-dd HH:mm:ss` 유지
- 재검토 후 수정 (TDD: 테스트 선 작성 → RED 확인 → 구현 → GREEN)
  - 에러 응답 형식 통일: 존재하지 않는 경로(404 `RESOURCE_NOT_FOUND`), 미지원 메서드(405 `METHOD_NOT_ALLOWED`), 예기치 못한 오류(500 `INTERNAL_SERVER_ERROR`)도 `{code, message}` 형식으로 응답. 그 외 Spring MVC 예외(415 등)는 원래 상태 유지
  - 동시 회원가입으로 이메일 유니크 제약 위반 시 500 대신 409 `DUPLICATE_EMAIL` 응답
  - JWT 서명 키가 32바이트 미만/미지정이면 로그인 시점 500 대신 기동 시점에 실패
  - TODO 검색 키워드의 앞뒤 공백 제거
- 문서: `README.md`(저장 형식, 에러 코드, JWT 키 조건), `docs/Architecture.md`(테스트 `config/` 디렉토리) 갱신
- 테스트: `./gradlew test` 전체 49개 통과
- 참고: 기존 로컬 `pcm.db`의 일시 데이터는 신규 저장 형식으로 변환 필요

## 2026-10-15 #6

### 지시사항

> 변경 사항 및 검증해야 할 작업들을 확인 후 커밋한다.

### 결과

- 변경 사항 확인
  - `docs/PRD.md`: 회원탈퇴 시 토큰 만료 요구사항 추가
  - `docs/Convention.md`: 인자 1개 이상인 모든 메서드 시그니처를 동일한 줄바꿈 + `final` 형태로 작성
  - `UserQueryService`: 비밀번호 검증 로직 `verifyPassword()` 분리 (사용자 변경, 유지)
- 회원 탈퇴 시 토큰 만료 구현 (TDD)
  - 1차: 사용자 ID별 만료 시각을 저장하는 방식으로 구현 → 테스트 중 SQLite가 삭제된 사용자 ID를 재사용하여, 같은 초에 가입한 신규 사용자의 토큰까지 거부되는 문제 발견
  - 최종: 사용자별 고유 토큰 키(UUID)를 토큰 claim(`token_key`)에 담고, 요청마다 `TokenOwnerValidator`가 ID + 토큰 키가 일치하는 사용자 존재 여부를 검증
    - 탈퇴 시 사용자(토큰 키)가 삭제되어 해당 사용자의 모든 토큰이 401 처리됨
    - ID가 재사용되어도 토큰 키가 달라 기존 토큰은 계속 거부됨
- Convention 반영: 인자 1개인 메서드/생성자 시그니처까지 줄바꿈 + `final` 형태로 변경 (메인/테스트 전체), 단일 컴포넌트 record도 줄바꿈 적용
- 검증
  - `./gradlew test` 전체 53개 통과 (3회 반복 실행 안정)
  - 별도 포트/임시 DB로 실행 검증: 탈퇴 후 기존 토큰 2개 모두 401, 동일 ID로 재가입한 사용자의 새 토큰 200, 기존 토큰 401
- `README.md` 토큰 만료 및 에러 응답 설명 갱신
- 참고: `users` 테이블에 `token_key` 컬럼이 추가되어 기존 로컬 `pcm.db`는 재생성 필요
