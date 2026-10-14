# 아키텍처

## 기본 구조 구성

```
src/main/java/io/keede/pcm
├── core/                                   핵심 비즈니스 영역
├   └── domains/                            각 도메인 집합 영역
├       └── Domain Name/                    도메인의 이름으로 정의
├           └── entity/                     엔티티, Repository 정의
├           └── service/                    비즈니스 로직 정의
├               └── XXQueryServicejava      읽기전용 기능 정의  
├               └── XXCommandService.java   CUD 기능 정의   
├       └── common/                         도메인 공통 (BaseTimeEntity, 날짜/시간 Converter 등)
├   └── config/                             설정 영역   
├       └── security/                       Spring Security, JWT 설정 및 토큰 발급
├   └── exception/                          예외처리 설정
├   └── external/                           외부 API, SDK
├   └── web/                                통신과 관련된 영역
├       └── api/                            API - 도메인별로 집합
├       └── auth/                           인증 사용자 주입 (@LoginUserId)
├       └── dto/                            요청/응답 DTO - 도메인별로 집합
```

```
src/test/java/io/keede/pcm
├── core/                    핵심 비즈니스 영역
├   └── domains/             각 도메인 집합 영역
├       └── Domain Name/     도메인의 이름으로 정의
├           └── fixture/     
├           └── service/     비즈니스 로직 정의
├   └── web/
├       └── api/             API 통합 테스트 (PRD 필수 시나리오)
```

