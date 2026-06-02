# Web Domain Layer — 개발 규칙

이 디렉터리는 관리자 웹 UI가 호출하는 API의 비즈니스 도메인 계층이다.
도메인은 특정 비즈니스, 카테고리, 기능의 집합을 표현한다.
HTTP 엔드포인트가 필요한 경우 도메인 루트 하위에 `api/` 패키지를 만들고 Controller를 추가한다.

## 패키지 구조 규칙

```
domain/
├── {feature}/                         # 도메인 루트
│   ├── {Entity}.kt                    # JPA 엔티티
│   ├── *Interaction.kt                # Use Case / Service Layer
│   ├── *Finder.kt                     # 읽기 전용 쿼리 서비스
│   ├── api/                           # HTTP 엔드포인트 (필요한 경우만)
│   │   ├── *Controller.kt
│   │   └── *Resources.kt
│   └── repository/
│       └── *Repository.kt
```

---

## Rule 1 — 모든 Controller에 @LoginCheck를 클래스 레벨에 선언한다

`web/domain` 하위의 모든 `*Controller` 클래스에는 예외 없이 `@LoginCheck`를 클래스 레벨에 선언한다.

```kotlin
@Tag(name = "프로젝트")
@LoginCheck                          // 반드시 클래스 레벨에 선언
@RestController
@RequestMapping("/v1/maintenance/...")
class FooController(...) { ... }
```

- `@LoginCheck`가 없는 Controller는 규칙 위반이다.
- 특정 메서드만 인증을 해제해야 한다면 클래스 선언은 유지하고 해당 메서드에 `@LoginCheck(enable = false)`를 추가한다.
- 이 규칙은 `api/domain`의 외부 서비스용 Controller와 구분되는 핵심 차이점이다. 외부 API는 `@LoginCheck(false)`를 사용하지만, 이곳의 관리자 API는 항상 세션 인증이 필요하다.

---

## Rule 2 — Interaction이 Workflow를 소유한다

`*Interaction`은 한 Use Case의 실행 흐름을 책임진다.

- 트랜잭션 경계는 Interaction 메서드 단위로 설정한다.
- 실제 비즈니스 규칙은 **Entity 메서드**에 위임한다. Interaction은 흐름만 조율한다.
- 읽기 쿼리는 `*Finder`에 위임한다. Interaction 내에서 직접 조회 쿼리를 작성하지 않는다.

```kotlin
// 올바른 예 — Interaction은 흐름을 조율하고 Entity가 규칙을 실행한다
@Service
@Transactional
class UserGrantInteraction(
    private val userFinder: UserFinder,
    private val authorityDefinitionFinder: AuthorityDefinitionFinder,
) {
    fun addByAuthorityGrant(userId: Long, projectId: Long, definitionIds: List<Long>) {
        val user = userFinder.findByStatusAllow(userId)       // Finder 위임
        val definitions = authorityDefinitionFinder.findByLive(definitionIds)
        definitions.forEach { user.addBy(it) }                // Entity 위임
        userRepository.saveAll(...)
    }
}
```

---

## Rule 3 — Finder는 읽기 전용이다

`*Finder` 클래스는 반드시 `@Transactional(readOnly = true)`를 선언하고 조회만 담당한다.

- 저장, 수정, 삭제 로직은 Finder에 두지 않는다.
- 존재하지 않는 데이터를 조회하면 적절한 예외(`HumanException`, `InvalidArgumentException` 등)를 던진다.
- `findBy*` 메서드가 nullable 반환(`?`)인 경우와 non-null 반환인 경우를 명확히 구분한다.

---

## Rule 4 — Entity는 자신의 비즈니스 규칙을 소유한다

연관관계 편의 메서드는 `addBy(entity)` / `setBy(entity)` 형식으로 Entity에 선언한다.
상태 변경 로직(soft delete의 `expire()`, 상태 전환 등)도 Entity 메서드로 정의한다.

```kotlin
// Entity가 규칙을 소유하는 예
class Project(...) : Audit() {
    fun expire() { status = false }          // 상태 변경은 Entity 책임

    fun addBy(menuNavigation: MenuNavigation) {
        menuNavigations.add(menuNavigation)
        if (this != menuNavigation.project) menuNavigation.setBy(this)
    }
}
```

- 새 Entity는 `Audit`을 상속하고 `@Audited(targetAuditMode = NOT_AUDITED)`를 적용한다.
- Soft delete 패턴: `status = true/false` 필드 + `@SQLRestriction("status = true")` 사용.

---

## Rule 5 — 도메인 간 의존은 같은 계층 안에서만 허용한다

```
web/domain/grant  → web/domain/user     (허용)
web/domain/grant  → web/domain/navigation (허용)
web/domain/*      → api/domain/*        (금지)
```

- `web/domain` 내 도메인끼리는 상호 참조할 수 있다.
- `api/domain`을 역방향으로 참조하지 않는다.

---

## 신규 도메인 추가 체크리스트

```
1. domain/{feature}/ 디렉터리 생성
2. {Entity}.kt 작성 — Audit 상속, @Audited, @SQLRestriction
3. {Feature}Interaction.kt 작성 — @Service, @Transactional, 흐름 조율만
4. {Feature}Finder.kt 작성 — @Service, @Transactional(readOnly = true), 조회만
5. repository/{Feature}Repository.kt 작성
6. (HTTP API 필요 시) api/ 서브패키지 생성
   - {Domain}Resources.kt — Request/Reply DTO
   - {Domain}Controller.kt — @LoginCheck 클래스 레벨 필수
```
