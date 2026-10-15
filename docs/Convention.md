## Convention

## 규칙

---
- CRITICAL: 읽기 쉬운 코드로 작성한다.
- CRITICAL: Entity 영역과 API 영역은 온전히 분리되어있어야 한다.
- CRITICAL: 연관관계 매핑시 `@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)` 를 활용한다.
- CRITICAL: API에 반환할 데이터는 Service Layer에서 DTO로 변환해야 한다.
- 테스트에 사용할 `fixture` 디렉토리의 경우 해당 도메인의 단위 테스트에 사용할 모든 `Mocking` 데이터의 구조를 정의한다.
    - `builder` 패턴으로 꺼내 쓸 수 있는 구조를 형성한다.
- 메서드 작성시 인자가 2개 이상일 경우 다음과 같은 형태로 작성하며, 그 외 메서드 시그니처들 또한 동일한 형태로 한다.
```java
    public JwtTokenProvider(
            final JwtEncoder jwtEncoder, 
            final JwtProperties jwtProperties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }
```
- 시간이 포함된 데이터는 `yyyy-MM-ddTHH:mm:ss` 형태로 저장되어 DB에서 용이하게 다룰수 있도록 하며 API형태로 전달할 떄는 `yyyy-MM-dd HH:mm:ss`로 응답되게 한다. 
  - 날짜의 경우 그대로 정의한다.(예시. LocalDate -> `yyyy-MM-dd`) 
---