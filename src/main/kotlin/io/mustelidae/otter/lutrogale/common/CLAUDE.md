# common 패키지 개발 지침

`io.mustelidae.otter.lutrogale.common` 패키지는 전 레이어에서 공유하는 기반 타입이다.

## 파일 구성

| 파일 | 역할 |
|---|---|
| `ErrorCode.kt` | 에러 코드 enum. 모든 에러의 식별자 |
| `ErrorSource.kt` | 에러 정보 인터페이스 |
| `DefaultError.kt` | `ErrorSource` 기본 구현체 |
| `Reply.kt` | 단건 HTTP 응답 래퍼 (`Reply<T>`, `.toReply()`) |
| `Replies.kt` | 컬렉션 HTTP 응답 래퍼 (`Replies<T>`, `.toReplies()`) |
| `Audit.kt` | 엔티티 감사 컬럼 베이스 클래스 |
| `Constant.kt` | 프로젝트 공통 상수 |

---

## ErrorCode 명명 규칙

### Prefix H — 호출 주체(클라이언트)의 잘못된 요청

| 코드 | 의미 | 사용 상황 |
|---|---|---|
| `HD__` | Data 오류 | 데이터가 없거나, 데이터 자체가 잘못된 경우 |
| `HA__` | Auth/권한 오류 | 호출 주체에게 접근 권한이 없는 경우 |
| `HI__` | Input 오류 | 입력값(파라미터, 헤더 등)이 잘못된 경우 |

### Prefix P — 내부 정책 위배

| 코드 | 의미 | 사용 상황 |
|---|---|---|
| `PD__` | Develop 실수 | 논리적으로 발생해서는 안 되는 오류. 버그 의심 시 |
| `PL__` | Policy 위반 | 정의된 비즈니스 정책과 맞지 않는 경우 |

### Prefix S — 시스템 간 통신·인프라 문제

| 코드 | 의미 | 사용 상황 |
|---|---|---|
| `SA__` | Async 오류 | 비동기·논블로킹 코드에서 발생한 오류 |
| `SI__` | Illegal State | 시스템 상태 자체가 비정상인 경우 |
| `SD__` | DB/Storage 오류 | 스토리지 접근, 쿼리 오류 |
| `ST__` | 타 시스템 통신 오류 | 외부 API 호출 관련 문제 |

새 `ErrorCode`를 추가할 때는 위 규칙에 따라 기존 항목 그룹 안에 순서대로 추가한다.

---

## 에러 표현 방법

### 기본 — `DefaultError`

에러 코드와 메시지를 명시할 때 `DefaultError`를 사용한다.

```kotlin
// 단순 메시지
DefaultError(ErrorCode.HD00, "User not found")

// 원인 맥락 포함
DefaultError(ErrorCode.HD00, "User not found", mapOf("email" to email))
```

### 커스텀 포맷이 필요할 때 — `ErrorSource` 직접 구현

별도 필드나 포맷이 필요한 경우에만 `ErrorSource`를 직접 구현한 클래스를 새로 만든다.

```kotlin
class DetailedError(
    override val code: String,
    override val message: String,
    override var causeBy: Map<String, Any?>? = null,
    override var refCode: String? = null,
    val extraField: String,
) : ErrorSource
```

---

## 예외 throw — `CustomException` 계층

예외는 `config/CustomExceptions.kt`에 정의된 계층을 통해 throw 한다.
에러 코드 Prefix와 Exception 계층은 아래와 같이 대응한다.

```
CustomException
├── HumanException (H__)        — 클라이언트 요청 오류
│   ├── DataNotFindException        HD00
│   ├── PreconditionFailException   HD02
│   ├── DataPermissionException     HA01
│   ├── InvalidArgumentException    HI01
│   └── MissingRequestXHeaderException  HI02
├── SystemException (P__/S__)   — 내부 로직·인프라 오류
│   └── DevelopMistakeException     PD01
├── PolicyException (PL__)      — 정책 위반
├── UnAuthorizedException (HA__)— 권한 없음
│   └── PermissionException         HA00 / HA01
├── CommunicationException (ST__/CT__) — 외부 통신 오류
│   ├── ClientException             C000
│   ├── ConnectionTimeoutException  CT01
│   └── ReadTimeoutException        CT02
└── AsyncException (SA__)       — 비동기 오류  SA00
```

### 새 에러 케이스 추가 절차

1. `ErrorCode.kt`에 규칙에 맞는 코드 추가
2. `CustomExceptions.kt`에서 적합한 부모 클래스를 상속해 Exception 클래스 추가
3. 생성자에서 `DefaultError` 또는 커스텀 `ErrorSource` 구현체를 전달

```kotlin
// 예시: 새 정책 위반 예외
class DuplicateRequestException(id: Long) : PolicyException(
    DefaultError(ErrorCode.PL01, "Duplicate request", mapOf("id" to id))
)
```

---

## 응답 래퍼 사용법

Controller 반환 타입은 항상 `Reply<T>` 또는 `Replies<T>`를 사용한다.

```kotlin
// 단건 반환
fun get(): Reply<MyDto> = myDto.toReply()

// 컬렉션 반환
fun list(): Replies<MyDto> = myDtoList.toReplies()
```
