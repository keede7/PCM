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
