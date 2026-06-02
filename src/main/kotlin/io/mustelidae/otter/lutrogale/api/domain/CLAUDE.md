# API Domain Layer — 개발 규칙

이 디렉터리는 외부 클라이언트가 호출하는 API의 비즈니스 도메인 계층이다.
각 도메인은 특정 비즈니스 기능의 집합을 표현하며, 필요 시 하위 `api/` 패키지를 통해 HTTP 엔드포인트를 노출한다.

## 패키지 구조 규칙

```
domain/
├── {feature}/                        # 도메인 루트
│   ├── *Interaction.kt               # Use Case / Service Layer
│   ├── *Interface.kt                 # 도메인 내 전략 인터페이스 (선택)
│   ├── *ConcreteImpl.kt              # 인터페이스 구현체 (선택)
│   └── api/                          # HTTP 진입점 (도메인 API가 필요한 경우만)
│       ├── *Controller.kt
│       └── *Resources.kt
```

- 도메인에 HTTP API가 필요한 경우에만 `api/` 하위 패키지를 생성한다.
- 내부 서비스 간 공유 로직은 `api/` 바깥 도메인 루트에 둔다.

---

## Rule 1 — Interaction이 Workflow를 소유한다

`*Interaction`은 한 Use Case의 실행 흐름(workflow)을 책임진다.

- 트랜잭션 경계는 Interaction 메서드 단위로 설정한다.
- 로직의 실제 처리는 **Entity 메서드**와 **도메인 객체 간 상호작용**으로 표현한다.
- Interaction이 직접 비즈니스 규칙을 구현하지 않는다. 규칙은 Entity 또는 도메인 객체에 위임한다.
- 읽기 전용 쿼리는 `*Finder`에 위임하고, Interaction은 조율만 담당한다.

```kotlin
// 올바른 예 — Interaction은 흐름을 조율한다
@Service
@Transactional
class ClientCertificationInteraction(
    private val userFinder: UserFinder,
    private val projectFinder: ProjectFinder,
    private val accessCheckerHandler: AccessCheckerHandler,
) {
    fun check(grant: AccessGrant): List<AccessResources.Reply.AccessState> {
        val project = projectFinder.findByLiveProjectOfApiKey(grant.apiKey)
        val user = userFinder.findBy(grant.email)!!
        val navigations = collectNavigations(user, project)   // Entity 협력
        val checker = accessCheckerHandler.handle(grant.authenticationCheckType)
        return checker.validate(navigations, grant)           // 전략 위임
    }
}
```

---

## Rule 2 — 전략 패턴은 인터페이스 + Handler로 표현한다

동일한 Use Case에 복수의 처리 방법이 존재하면 인터페이스로 전략을 정의하고 `*Handler`가 선택한다.

```
AccessChecker (interface)
├── UriBaseAccessChecker   — PathPatternParser로 URI 패턴 매칭
└── IdBaseAccessChecker    — MenuNavigation ID 직접 비교

AccessCheckerHandler       — AuthenticationCheckType을 보고 구현체 반환
```

- 인터페이스 구현체는 `@Service`를 붙이지 않고 Handler가 직접 생성하거나 주입받는다.
- Handler는 `@Service`로 등록하고 Interaction이 주입받아 사용한다.

---

## Rule 3 — 도메인 간 의존 방향

```
api/domain/* → web/domain/*   (허용: web 도메인의 Entity, Finder, Interaction 사용)
web/domain/* → api/domain/*   (금지)
```

- `api/domain`의 Interaction은 `web/domain`의 Finder, Interaction, Repository를 자유롭게 사용한다.
- `web/domain`에서 `api/domain`을 역방향으로 참조하지 않는다.

---

## Rule 4 — AccessGrant는 도메인 입력 객체다

`AccessGrant`는 Controller에서 받은 요청을 Interaction에 넘기는 **도메인 입력 DTO**다.

- `companion object`의 팩터리 메서드(`ofIdBase`, `ofUrlBase`, `ofOperationBase`)로만 생성한다.
- Controller에서 직접 생성하지 말고 `AccessGrant.of*(...)` 팩터리를 사용한다.
- `authenticationCheckType` 필드가 `AccessCheckerHandler`의 전략 분기 키가 된다.

---

## Rule 5 — 외부 HTTP 통신은 client/ 패키지로 격리한다

외부 스펙 URL을 호출하는 등 외부 시스템과의 통신은 `client/` 서브패키지로 분리한다.

```
migration/
└── client/
    ├── HttpSpecClient.kt           # 인터페이스
    ├── StableHttpSpecClient.kt     # 실제 구현
    └── DummyHttpSpecClient.kt      # 테스트/프리뷰용 구현
```

- Interaction은 인터페이스(`HttpSpecClient`)에만 의존한다.
- 구현체 교체는 Spring Bean 설정(`MigrationClientConfiguration`)으로 처리한다.

---

## Rule 6 — 변환 로직은 도메인 객체에 캡슐화한다

외부 스펙(OpenAPI, GraphQL SDL)을 내부 모델(`MenuNavigation`)로 변환하는 로직은
전용 클래스(`PathToMenu` 인터페이스 구현체)로 캡슐화한다.

- Interaction은 변환 방법을 알지 못하고, 변환 결과만 사용한다.
- 변환 전략이 여러 개이면(TREE, FLAT) 인터페이스를 두고 Interaction에서 `when`으로 선택한다.

---

## 신규 도메인 추가 체크리스트

```
1. domain/{feature}/ 디렉터리 생성
2. {Feature}Interaction.kt 작성
   - @Service, @Transactional
   - 메서드당 하나의 Use Case
   - 비즈니스 규칙은 Entity/도메인 객체에 위임
3. (전략 분기 필요 시) 인터페이스 + *Handler 작성
4. (HTTP API 필요 시) api/ 서브패키지 생성
   - {Domain}Resources.kt — Request/Reply DTO
   - {Domain}Controller.kt — @LoginCheck(false), RoleHeader 헤더 처리
5. (외부 통신 필요 시) client/ 서브패키지로 격리
```
