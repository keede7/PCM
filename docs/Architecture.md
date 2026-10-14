# 아키텍처

## 기본 구조 구성

```
src/main/java/io/keede/pcm
├── core/                                   핵심 비즈니스 영역
├   └── domains/                            각 도메인 집합 영역
├       └── Domain Name/                    도메인의 이름으로 정의
├           └── entity/                     엔티티 정의
├           └── service/                    비즈니스 로직 정의
├               └── XXQueryServicejava      읽기전용 기능 정의  
├               └── XXCommandService.java   CUD 기능 정의   
├   └── config/                             설정 영역   
├   └── exception/                          예외처리 설정
├   └── external/                           외부 API, SDK
├   └── web/                                통신과 관련된 영역
├       └── api/                            API - 도메인별로 집합
├       └── dto/             
```

```
src/test/java/io/keede/pcm
├── core/                    핵심 비즈니스 영역
├   └── domains/             각 도메인 집합 영역
├       └── Domain Name/     도메인의 이름으로 정의
├           └── fixture/     
├           └── service/     비즈니스 로직 정의
```

## 규칙

---
- CRITICAL: 읽기 쉬운 코드로 작성한다.
- CRITICAL: Entity 영역과 API 영역은 온전히 분리되어있어야 한다.
- CRITICAL: 연관관계 매핑시 물리적 FK는 설정하지 않는다.
- 테스트에 사용할 `fixture` 디렉토리의 경우 해당 도메인의 단위 테스트에 사용할 모든 `Mocking` 데이터의 구조를 정의한다.
  - `builder` 패턴으로 꺼내 쓸 수 있는 구조를 형성한다.  
