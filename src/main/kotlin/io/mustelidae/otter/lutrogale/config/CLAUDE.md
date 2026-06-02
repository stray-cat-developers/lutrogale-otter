# config 패키지 개발 지침

`io.mustelidae.otter.lutrogale.config` 패키지는 전역 예외 처리, 필터, 인터셉터, Spring 설정을 담는다.

---

## CustomExceptions.kt — 예외 계층 구조

`CustomException`을 루트로 하는 계층 구조다. 어떤 예외를 선택할지는 `ErrorCode` prefix로 결정한다.

```
CustomException
├── HumanException        — H__ 코드: 클라이언트 요청 오류
│   ├── DataNotFindException        (HD00)  데이터가 존재하지 않는 경우
│   ├── PreconditionFailException   (HD02)  사전 조건 불충족
│   ├── DataPermissionException     (HA01)  데이터 접근 권한 없음
│   ├── InvalidArgumentException    (HI01)  잘못된 인자
│   └── MissingRequestXHeaderException (HI02) 필수 헤더 누락
├── SystemException       — P__/S__ 코드: 내부 로직·인프라 오류
│   └── DevelopMistakeException     (PD01)  논리적으로 발생해서는 안 되는 버그
├── PolicyException       — PL__ 코드: 비즈니스 정책 위반
├── UnAuthorizedException — HA__ 코드: 인증/권한 없음
│   └── PermissionException         (HA00/HA01)
├── CommunicationException — C__/CT__ 코드: 외부 시스템 통신 오류
│   ├── ClientException             (C000)
│   ├── ConnectionTimeoutException  (CT01)
│   └── ReadTimeoutException        (CT02)
└── AsyncException        — SA__ 코드: 비동기 처리 오류  (SA00)
```

---

## ExceptionConfiguration.kt — 핸들러 등록 규칙

### ErrorCode prefix → HTTP ResponseStatus 매핑

| ErrorCode prefix | 설명 | ResponseStatus |
|---|---|---|
| `H__` 공통 기본 | 클라이언트 요청 오류 | `BAD_REQUEST` (400) |
| `HD__` | 데이터 없음 | `NOT_FOUND` (404) |
| `HA__` | 인증/권한 없음 | `UNAUTHORIZED` (401) |
| `HI__` | 입력값 오류 | `BAD_REQUEST` (400) |
| `PD__` | 개발 실수 (버그) | `INTERNAL_SERVER_ERROR` (500) |
| `PL__` | 정책 위반 | `BAD_REQUEST` (400) |
| `S__` | 시스템 오류 | `INTERNAL_SERVER_ERROR` (500) |
| `C__` / `CT__` | 외부 통신 오류 | `INTERNAL_SERVER_ERROR` (500) |
| `SA__` | 비동기 오류 | `INTERNAL_SERVER_ERROR` (500) |

**핵심 규칙**: `S`로 시작하는 코드(시스템 오류)는 반드시 `INTERNAL_SERVER_ERROR`.
`H`로 시작하는 코드(휴먼 오류)는 위 표에서 세부 prefix에 맞는 Status를 사용.

### 새 Exception Handler 추가 절차

1. **`ErrorCode.kt`** — 규칙에 맞는 새 코드 추가 (common/CLAUDE.md 참고)
2. **`CustomExceptions.kt`** — 상위 Exception 클래스를 상속해 새 예외 정의
3. **`ExceptionConfiguration.kt`** — `@ExceptionHandler`로 핸들러 등록

```kotlin
// Step 2 — CustomExceptions.kt에 예외 추가
class SomeSystemException(message: String) : SystemException(
    DefaultError(ErrorCode.SI01, message)
)

class SomePolicyException(id: Long) : PolicyException(
    DefaultError(ErrorCode.PL01, "Duplicate request", mapOf("id" to id))
)
```

```kotlin
// Step 3 — ExceptionConfiguration.kt에 핸들러 등록
// S__ → INTERNAL_SERVER_ERROR
@ExceptionHandler(value = [SomeSystemException::class])
@ResponseStatus(INTERNAL_SERVER_ERROR)
@ResponseBody
fun handleSomeSystemException(e: SomeSystemException, request: HttpServletRequest): GlobalErrorFormat {
    return errorForm(request, e, e.error)
}

// H__ (HD__) → NOT_FOUND
@ExceptionHandler(value = [SomePolicyException::class])
@ResponseStatus(BAD_REQUEST)
@ResponseBody
fun handleSomePolicyException(e: SomePolicyException, request: HttpServletRequest): GlobalErrorFormat {
    return errorForm(request, e, e.error)
}
```

### 주의: 예외 계층 우선 순위

Spring MVC는 **가장 구체적인 타입의 핸들러**를 먼저 선택한다.

- `DataNotFindException`(`NOT_FOUND`)은 `HumanException`(`BAD_REQUEST`) 핸들러보다 우선한다.
- 새 Exception이 기존 부모 핸들러와 다른 ResponseStatus를 가져야 한다면, 반드시 **별도 핸들러**를 추가한다.
- 부모 핸들러와 동일한 ResponseStatus라면 별도 핸들러 없이 부모 핸들러에 맡겨도 된다.

### GlobalErrorFormat 응답 구조

```json
{
  "timestamp": "2026-05-29T10:00:00.000+00:00",
  "status": 400,
  "code": "HI01",
  "message": "Invalid argument: email format",
  "type": "InvalidArgumentException",
  "causeBy": { "field": "email" },
  "refCode": null
}
```

`errorForm()` 헬퍼 메서드를 통해 자동으로 구성된다. 핸들러에서 직접 빌드하지 않는다.
