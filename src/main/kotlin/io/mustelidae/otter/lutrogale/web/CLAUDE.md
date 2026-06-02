# web 패키지 개발 지침

이 패키지는 관리자 웹 UI용 REST API를 담당한다.

## 인증

`web` 하위 패키지의 모든 `*Controller` 클래스에는 반드시 `@LoginCheck` 어노테이션을 클래스 레벨에 선언한다.

```kotlin
@LoginCheck
@RestController
@RequestMapping("/v1/maintenance/...")
class FooController(...) { ... }
```

- 예외 없음. 로그인 체크가 불필요한 엔드포인트라도 클래스에 선언한 뒤 메서드에 `@LoginCheck(enable = false)`를 붙여 개별 해제한다.

## REST API 표준

### HTTP 상태 코드

| Method | 정상 응답 코드 | 선언 방법 |
|---|---|---|
| `GET` | 200 OK | 기본값, 별도 선언 불필요 |
| `POST` | 201 Created | `@ResponseStatus(HttpStatus.CREATED)` 필수 |
| `PUT` / `PATCH` | 200 OK | 기본값, 별도 선언 불필요 |
| `DELETE` | 204 No Content | `@ResponseStatus(HttpStatus.NO_CONTENT)` 필수 |

`@PostMapping` 핸들러에 `@ResponseStatus(HttpStatus.CREATED)` 가 없으면 규칙 위반이다.

```kotlin
// 올바른 예
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
fun create(@RequestBody request: FooResources.Request): Reply<Long> { ... }

// 잘못된 예 — ResponseStatus 누락
@PostMapping
fun create(@RequestBody request: FooResources.Request): Reply<Long> { ... }
```

### URL 설계

- 컬렉션 자원: 복수형 명사 (`/projects`, `/users`)
- 단일 자원: 복수형 명사 + ID (`/project/{id}` 또는 `/projects/{id}`)
- 중첩 자원: 부모 ID를 경로에 포함 (`/project/{projectId}/navigations`)
- 동사 사용 금지. 행위는 HTTP 메서드로 표현한다.

### 응답 래퍼

모든 응답은 `Reply<T>` 또는 `Replies<T>`로 감싸서 반환한다.

```kotlin
// 단건
fun findOne(): Reply<FooResources.Reply> = fooFinder.findBy(id).toReply()

// 목록
fun findAll(): Replies<FooResources.Reply> = fooFinder.findAll().map { ... }.toReplies()

// 생성 — 생성된 리소스 ID 반환
fun create(...): Reply<Long> = id.toReply()

// 수정/삭제 — 본문 없음
fun modify(...): Reply<Unit> = Unit.toReply()
```
