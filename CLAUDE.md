# Lutrogale-otter — Permission Check System

권한 관리 서버. 외부 서비스가 API Key로 사용자의 접근 권한을 조회하는 중앙 허브다.

## Project Purpose

클라이언트 서비스들이 자체 권한 로직 없이, 이 서버 하나에 `POST /v1/verification/authorization-check/*`를 호출해 권한 여부를 응답받는다.
권한은 관리자 웹 UI(`http://localhost:4210/`)에서 Project/User/AuthorityDefinition 단위로 관리한다.

## Technology Stack

| Category | Stack |
|---|---|
| Language | Kotlin 1.9.22, JVM 21 |
| Framework | Spring Boot 3.2.2 |
| Persistence | Spring Data JPA + QueryDSL 5.1 + Hibernate Envers |
| DB | MySQL (local Docker), H2 (test) |
| Session | Spring Session JDBC |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Gradle Kotlin DSL |
| Testing | Kotest 5, MockK, MockMvc |
| Container | Docker Compose, Jib |

## Package Structure

```
io.mustelidae.otter.lutrogale
├── api/                          # 외부 클라이언트용 REST API (권한 조회·로그인·마이그레이션)
│   ├── domain/
│   │   ├── authorization/        # 권한 체크 핵심 — AccessChecker, ClientCertificationInteraction
│   │   ├── login/                # 관리자 로그인 API
│   │   └── migration/            # OpenAPI/GraphQL 스펙으로 권한 일괄 import
│   └── permission/               # RoleHeader (x-system-id, x-admin-id 등), Permission 인터페이스
│
├── web/                          # 관리자 웹 UI 도메인 (Freemarker 템플릿 기반)
│   ├── common/annotation/        # @LoginCheck — 세션 로그인 필터 제어
│   └── domain/
│       ├── project/              # Project 엔티티 — API Key 보유, 권한 트리 루트
│       ├── user/                 # User 엔티티 — 권한 체크 대상자 (status: ALLOW/WAIT/REJECT/EXPIRE)
│       ├── authority/            # AuthorityDefinition — 권한 그룹 (MenuNavigation 묶음)
│       ├── navigation/           # MenuNavigation — URL+HTTP Method 트리 노드
│       ├── grant/                # UserAuthorityGrant, UserPersonalGrant — User↔권한 연결
│       ├── admin/                # 관리자 계정
│       ├── session/              # Spring Session 래퍼
│       └── home/                 # 대시보드
│
├── config/                       # Spring 설정, 필터, 인터셉터, 예외 처리
├── common/                       # Reply/Replies (응답 래퍼), Constant, ErrorCode
└── utils/                        # Jackson, RestClient, Crypto, JPASpecificationDSL 헬퍼
```

## Domain Model

```
Project (apiKey)
  └── MenuNavigation (uriBlock, methodType, treeId) — 트리 구조, 재귀 parent
  └── AuthorityDefinition (권한 그룹)
        └── AuthorityNavigationUnit ──→ MenuNavigation
        └── UserAuthorityGrant ──→ User

User (email, status)
  ├── userAuthorityGrants ──→ AuthorityDefinition  (그룹 권한)
  └── userPersonalGrants  ──→ MenuNavigation       (개별 권한)
```

## Naming Conventions

| Suffix | Role |
|---|---|
| `*Controller` | REST 엔드포인트. `@RequestMapping` 정의. |
| `*Resources` | Request/Reply DTO. Controller 파일 내 중첩 object로 선언. |
| `*Interaction` | Service layer. 트랜잭션 단위 비즈니스 로직. |
| `*Finder` | 읽기 전용 쿼리 서비스. `@Transactional(readOnly = true)`. |
| Entity (plain name) | JPA 엔티티. `Audit` 상속. 연관관계 편의 메서드(`addBy`, `setBy`) 포함. |
| `*Repository` | Spring Data JPA 또는 QueryDSL 레포지토리. |

## Key Flows

### 권한 체크 (외부 서비스 → 이 서버)

```
POST /v1/verification/authorization-check/uri
  Header: x-system-id = {projectApiKey}
  Body: { email, uris: [{uri, methodType}] }

ClientCertificationInteraction
  → ProjectFinder.findByLiveProjectOfApiKey(apiKey)
  → UserFinder.findBy(email)  — 없으면 WAIT 상태로 자동 생성 후 denied 반환
  → User의 그룹/개별 권한 MenuNavigation 수집
  → UriBaseAccessChecker.validate() — PathPatternParser로 패턴 매칭
```

ID 기반(`/id`), GraphQL Operation 기반(`/graphql`) 체크도 동일한 흐름.

### 권한 마이그레이션

```
POST /v1/migration/open-api  — OpenAPI JSON/YAML URL → MenuNavigation 트리 일괄 생성
POST /v1/migration/graphql   — GraphQL SDL URL → MenuNavigation 생성
```

## Testing

테스트는 두 계층으로 구성된다.

| Class | 역할 |
|---|---|
| `FlowTestSupport` | `@SpringBootTest` + `@ActiveProfiles("embedded")`. MockMvc 제공. 통합 테스트 베이스. |
| `*Flow` | 특정 컨트롤러의 HTTP 호출을 캡슐화한 헬퍼. Test 클래스에서 재사용. |
| `*Test` | 실제 테스트 시나리오. `FlowTestSupport` 상속 + `*Flow` 조합. |

H2 인메모리 DB를 사용한다 (`embedded` 프로파일). 실제 MySQL 없이 동작한다.

테스트 실행:
```sh
./gradlew test
```

## Local Dev

```sh
# Docker DB + 앱 동시 시작
./quick-start.sh

# 앱만 시작 (Docker DB가 이미 떠 있을 때)
./gradlew bootRun

# 관리자 UI
open http://localhost:4210/
# 계정: admin@osori.com / admin
```

DB 연결: `jdbc:mysql://localhost:3306/local`, user: `root`, password: `root`

## Claude Code Instructions

### CLI Tools

`ls`, `cat`, `grep` 대신 모던 CLI를 사용할 것:

```sh
rg 'pattern'               # grep 대신
fd 'pattern'               # find 대신
bat src/main/.../Foo.kt    # cat 대신
eza --tree src/main        # ls -R 대신
jq '.dependencies' < f.json
sd 'old' 'new' file.kt     # sed 대신
```

### Code Style

- 새 엔티티는 `Audit` 상속, `@Audited(targetAuditMode = NOT_AUDITED)` 적용
- Controller는 `*Resources` inner object로 Request/Reply DTO를 정의
- 비즈니스 로직은 `*Interaction`에. `*Finder`는 읽기 전용.
- 연관관계 편의 메서드는 `addBy(entity)` / `setBy(entity)` 형식
- `status = true/false` soft delete 패턴 사용. `@SQLRestriction("status = true")` 적용.

### Testing Rules

- 모든 새 기능/버그픽스에 TDD 적용
- 통합 테스트는 `FlowTestSupport` 상속 + `*Flow` 헬퍼 조합
- H2 테스트 DB 사용. 실제 MySQL 연결 없이 동작해야 함
- 목(mock) DB 사용 금지. 실제 H2에 저장/조회하는 테스트 작성

## 개발 워크플로우

코드 구현이 완료되면 반드시 다음 단계를 수행할 것:

1. 구현 완료 후 `code-reviewer` 서브에이전트를 호출하여 변경된 파일을 리뷰
2. 리뷰 결과에서 Critical/High 이슈가 있으면 즉시 수정 후 재리뷰
3. 이슈가 없거나 Low 이슈만 남으면 최종 완료 보고
4. gradle task 중 formatKotlin을 수행하여 Lint 처리