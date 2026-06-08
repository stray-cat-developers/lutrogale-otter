# Redis Key 생성 규칙

`io.mustelidae.otter.lutrogale.config.redis` 패키지는 Redis 연결 설정과 Key 생성 규칙을 담는다.

---

## RedisKey 인터페이스

Redis Key는 반드시 `RedisKey` 인터페이스를 구현한 전용 클래스를 통해 생성한다.
**직접 문자열로 Key를 조합하는 것을 금지한다.** 이를 통해 Key 중복을 방지하고 Key를 코드에서 탐색하기 쉽게 유지한다.

```kotlin
interface RedisKey {
    fun getKey(): String

    companion object {
        const val PREFIX = "lutrogale"
    }
}
```

---

## Key 생성 클래스 작성 규칙

### 1. 클래스 위치

Key 생성 클래스는 **해당 Key를 사용하는 도메인 패키지 내부**에 배치한다.

```text
api/domain/authorization/
  ├── ClientCertificationInteraction.kt
  └── IdBaseAuthorizedKey.kt          ← Key 클래스는 사용하는 도메인 옆에
```

### 2. 클래스 명명

`{UseCase}Key` 형식으로 명명한다.

| 사용 케이스 | 클래스 이름 |
| --- | --- |
| ID 기반 권한 체크 캐시 | `IdBaseAuthorizedKey` |
| URI 기반 권한 체크 캐시 | `UriBaseAuthorizedKey` |
| GraphQL 권한 체크 캐시 | `GraphqlBaseAuthorizedKey` |

### 3. Key 구조

`{PREFIX}:{api-path}` 형태로 조합한다. PREFIX는 `RedisKey.PREFIX` 상수(`lutrogale`)를 사용한다.

```text
lutrogale:{segment1}:{segment2}:...
```

### 4. Key 구현 예시

```kotlin
// IdBaseAuthorizedKey.kt
class IdBaseAuthorizedKey(
    private val projectId: Long,
    private val userId: Long,
) : RedisKey {
    override fun getKey(): String =
        "${RedisKey.PREFIX}:v1:verification:authorization-check:id:$projectId:$userId"
}
```

```kotlin
// UriBaseAuthorizedKey.kt
class UriBaseAuthorizedKey(
    private val projectId: Long,
    private val email: String,
) : RedisKey {
    override fun getKey(): String =
        "${RedisKey.PREFIX}:v1:verification:authorization-check:uri:$projectId:$email"
}
```

### 5. Key 사용 방법

Key 클래스를 생성하고 `.getKey()`로 완성된 Key 문자열을 가져온다.
직접 문자열을 조합하지 않는다.

```kotlin
// 올바른 사용
val key = IdBaseAuthorizedKey(projectId, userId).getKey()
stringRedisTemplate.opsForValue().get(key)

// 금지 — 문자열 직접 조합
val key = "lutrogale:v1:verification:authorization-check:id:$projectId:$userId"
```

### 6. Key 클래스에 TTL 상수 포함

TTL이 필요한 경우 Key 클래스 내에 `companion object`로 함께 선언한다.

```kotlin
class IdBaseAuthorizedKey(
    private val projectId: Long,
    private val userId: Long,
) : RedisKey {
    override fun getKey(): String =
        "${RedisKey.PREFIX}:v1:verification:authorization-check:id:$projectId:$userId"

    companion object {
        val TTL: Duration = Duration.ofMinutes(10)
    }
}
```

---

## 기존 Key 목록

새 Key를 추가하기 전에 아래 목록에서 중복 여부를 확인한다.

- **`IdBaseAuthorizedKey`** — `api/domain/authorization/`
  - Key: `lutrogale:authz:id:{apiKey}:{email}:{sortedIds}`
  - TTL: 5분

- **`UriBaseAuthorizedKey`** — `api/domain/authorization/`
  - Key: `lutrogale:authz:uri:{apiKey}:{email}:{METHOD}:{uri}|{METHOD}:{uri}|...` (각 URI-method 쌍을 `METHOD:uri` 형태로 변환 후 정렬, `|`로 구분)
  - TTL: 5분
  - GraphQL 엔드포인트도 내부적으로 URI로 변환(`/{operation}`)되어 이 키를 사용함

> **주의**: 새 Key 클래스를 추가하면 이 목록에 반드시 등록한다.
