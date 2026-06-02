# API Layer — 개발 규칙

이 디렉터리는 외부 클라이언트(다른 서비스)가 호출하는 REST API 계층이다.
관리자 웹 UI는 `web/` 패키지가 담당한다.

## 패키지 구성

```
api/
├── domain/
│   ├── {feature}/
│   │   ├── *Interaction.kt       # 비즈니스 로직 (Service)
│   │   └── api/
│   │       ├── *Controller.kt    # REST 엔드포인트
│   │       └── *Resources.kt     # Request / Reply DTO
└── permission/
    └── RoleHeader.kt             # 헤더 상수 (x-system-id 등)
```

---

## Rule 1 — 응답은 반드시 Reply / Replies로 래핑한다

`@RestController` 메서드의 반환 타입 규칙:

| 반환 데이터 | 타입 | 생성 방법 |
|---|---|---|
| 단일 객체 | `Reply<T>` | `value.toReply()` |
| 컬렉션 | `Replies<T>` | `list.toReplies()` |
| 없음 (생성/삭제) | `Reply<Long>` (생성된 ID) 또는 `Unit` | — |

```kotlin
// 단일 반환
fun getItem(): Reply<ItemResources.Reply.Item> =
    interaction.find(id).toReply()

// 리스트 반환
fun listItems(): Replies<ItemResources.Reply.Item> =
    interaction.findAll().map { ... }.toReplies()
```

`Reply`와 `Replies`는 `io.mustelidae.otter.lutrogale.common` 패키지에 있다.
절대로 bare 타입(`String`, `List<T>`, custom DTO)을 직접 반환하지 않는다.

---

## Rule 2 — API Key 기반 기본 인증

외부 서비스가 호출하는 모든 엔드포인트는 `x-system-id` 헤더로 API Key를 받아야 한다.

```kotlin
@PostMapping("/some-endpoint")
fun someEndpoint(
    @RequestHeader(RoleHeader.XSystem.KEY) apiKey: String,  // 필수
    @RequestBody request: SomeResources.Request.Something,
): Reply<SomeResources.Reply.Result> { ... }
```

- 헤더 상수: `RoleHeader.XSystem.KEY = "x-system-id"`
- API Key는 `ProjectFinder.findByLiveProjectOfApiKey(apiKey)`로 Project를 조회하는 데 사용한다
- `@LoginCheck(false)` 어노테이션을 Controller 클래스에 붙여 세션 로그인 필터를 우회한다
- 관리자 세션 인증이 필요한 컨트롤러는 `@LoginCheck` 어노테이션을 생략한다

---

## Rule 3 — DTO 파일 명칭은 *Resources.kt

Controller 한 개당 대응하는 `*Resources.kt` 파일이 같은 `api/` 패키지에 있어야 한다.

```
authorization/api/
├── AuthorizationController.kt
└── AccessResources.kt          ← Controller와 1:1 대응
```

- 파일명: `{Domain}Resources.kt`
- 최상위 클래스: `class {Domain}Resources` (data class 아님)
- Swagger 스키마 이름 규칙: `@Schema(name = "Lutrogale.{Domain}.{Type}")`

---

## Rule 4 — Request / Reply는 Resources 내부 클래스로 선언

모든 입력/출력 DTO는 `*Resources` 클래스 안의 `Request` / `Reply` inner class로 정의한다.

```kotlin
class FooResources {

    class Request {
        @Schema(name = "Lutrogale.Foo.Request.Create")
        class Create(
            val name: String,
            val value: Int,
        )

        @Schema(name = "Lutrogale.Foo.Request.Update")
        class Update(
            val value: Int,
        )
    }

    class Reply {
        @Schema(name = "Lutrogale.Foo.Reply.Item")
        class Item(
            val id: Long,
            val name: String,
            val value: Int,
        )
    }
}
```

Controller에서 참조할 때: `FooResources.Request.Create`, `FooResources.Reply.Item`

---

## Rule 5 — 공유 타입은 Resources 최상위에 선언

Request와 Reply에서 동일한 구조를 쓰는 클래스는 `*Resources` 클래스 직속에 선언한다.
중복 선언 금지.

```kotlin
class AccessResources {

    // Request와 Reply 양쪽에서 공유
    @Schema(name = "Lutrogale.Access.AccessUri")
    class AccessUri(
        val uri: String,
        val methodType: RequestMethod,
    )

    class Request {
        class UriBase(
            val email: String,
            val uris: List<AccessUri>,  // 공유 타입 참조
        )
    }

    class Reply {
        class AccessState(
            val target: String,
            val hasPermission: Boolean,
        )
    }
}
```

공유 범위가 단일 Request 또는 Reply 안에서만 국한된 경우에는 해당 클래스 내부에 선언해도 된다.

---

## 구조 예시 — 신규 API 추가 체크리스트

```
1. domain/{feature}/api/ 디렉터리 생성
2. {Domain}Resources.kt 생성
   - Request inner class 정의
   - Reply inner class 정의
   - 공유 타입 있으면 최상위에 선언
3. {Domain}Controller.kt 생성
   - @Tag, @LoginCheck(false) 선언
   - 모든 메서드에 @RequestHeader(RoleHeader.XSystem.KEY) apiKey 파라미터
   - 반환 타입은 Reply<T> 또는 Replies<T>
4. {Domain}Interaction.kt 생성 (api/ 상위 패키지)
```
