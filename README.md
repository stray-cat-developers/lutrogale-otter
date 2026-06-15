# Lutrogale-otter
### Permission check system

[![Build Status](https://github.com/stray-cat-developers/lutrogale-otter/actions/workflows/gradle.yml/badge.svg)](https://github.com/stray-cat-developers/lutrogale-otter)

## New Features!
### 1.1.0

- **관리자 계정 관리**: SUPER 관리자가 관리자 계정을 목록 조회, 생성, 만료, 비밀번호 변경할 수 있습니다.
- **사용자 일괄 등록**: SUPER 관리자가 CSV 형식으로 다수의 사용자를 한 번에 등록할 수 있습니다.
- **권한 감사 로그**: 권한 체크 결과를 Grafana/Loki 파이프라인으로 전송하여 감사 로그를 기록합니다.
- **마이그레이션 스펙 URL 자동 동기화**: 등록된 OpenAPI/GraphQL 스펙 URL을 주기적으로 자동 동기화하여 MenuNavigation 트리를 최신 상태로 유지합니다.
  - 대시보드에서 프로젝트별 동기화 상태를 확인하고 수동 동기화를 실행할 수 있습니다.
- **이메일 기반 사용자 만료 API**: 이메일 주소로 특정 사용자를 만료(EXPIRE) 상태로 일괄 처리하는 API를 추가했습니다.
- **보안 강화**: 관리자 비밀번호 해싱 알고리즘을 SHA-256에서 BCrypt로 교체했습니다.
- **라이브러리 업그레이드**: Spring Boot, Kotlin, QueryDSL 등 주요 라이브러리를 최신 안정 버전으로 업그레이드했습니다.

### 1.0.3

- 권한 체크 결과를 Redis에 5분간 캐싱하여 동일 요청에 대한 DB 조회를 줄입니다.
  - 메뉴ID 기반(`/authorization-check/id`), URL 기반(`/authorization-check/uri`), GraphQL 기반(`/authorization-check/graphql`) 모두 적용
  - Redis 장애 시 DB에서 직접 조회하는 폴백 처리 포함
- Playwright 기반 E2E 테스트 인프라를 추가했습니다.
  - 권한 그룹(authority-groups), 프로젝트 멤버(project-members), 회원 관리(management-members) 시나리오 포함
  - `e2e-test.sh` 스크립트로 Docker MySQL 환경과 함께 E2E 테스트를 한 번에 실행할 수 있습니다.

### 1.0.2
- Envers 적용으로 권한 관리의 변경사항을 저장합니다.
- 메뉴트리가 많아지는 경우 스크롤이 생기지 않던 문제를 해결했습니다.
- 마이그레이션 API 오류 시 오류 내용이 제대로 나오지 않는 문제를 해결했습니다.
- auditor id 뒤에 .이 들어간 부분을 제거했습니다.

### 1.0.1
- GraphQL URL을 통해 권한 전체를 마이그레이션 할 수 있습니다.
- Open API (2.0, 3.x) URL을 통해 권한 전체를 마이그레이션 할 수 있습니다.
  - Format은 JSON, YAML을 지원합니다.
- GraphQL Operation을 통해 권한을 확인할 수 있습니다.

### 1.0.0
- 사용자, 프로젝트, 권한, 권한 그룹, 권한 그룹에 권한을 추가할 수 있습니다.
- 사용자는 프로젝트에 참여할 수 있습니다.
- 사용자는 프로젝트에 참여할 때 권한 그룹을 선택할 수 있습니다.
- 특정 타켓만 가진 권한 그룹을 만들 수 있습니다.
- 커스터마이징 된 개별 권한을 만들 수 있습니다.
- url을 통해 권한을 확인할 수 있습니다.
- 권한 id를 통해 권한을 확인할 수 있습니다.

# Installation
### Quick start
바로 시작을 하기 위해서는 Java 21, Docker가 설치되어 있어야 합니다.
Docker 기반 Maria DB를 스토리지로 사용 중 입니다.

```sh
git clone https://github.com/stray-cat-developers/lutrogale-otter.git
./quick-start.sh
```
Homepage is http://localhost:4210/

# How to use
- 어플리케이션을 시작합니다.
- http://localhost:4210/ 에 접근합니다.
  - 관리자 계정은 ID: admin@osori.com, PW: admin 입니다.
  - 로그인을 하면 테스트를 위해 "Otter Project" 프로젝트와 "lutrogale@otter.com" 사용자 계정이 등록되어 있습니다.
- 로그인을 하게 되면 대시보드로 이동이 됩니다. 

# Testing

## Unit / Integration Tests

H2 인메모리 DB를 사용합니다. Docker 없이 실행 가능합니다.

```sh
./gradlew test
```

## E2E Tests (Playwright)

실제 브라우저에서 UI 동작을 검증합니다. Node.js 18+ 가 필요합니다.

**최초 1회 설정**

```sh
cd e2e
npm install
npx playwright install chromium
```

**테스트 실행**

```sh
cd e2e
npm test
```

첫 실행 시 Spring Boot 서버(port 4211, H2 embedded)를 자동으로 시작합니다. 이미 4211 포트에 서버가 떠 있으면 재사용합니다.

**옵션**

```sh
npm run test:ui      # 시각적 UI 모드 (브라우저로 실행 흐름 확인)
npm run test:debug   # 디버그 모드 (한 단계씩 실행)
npm run report       # 마지막 테스트 HTML 리포트 열기
npm run codegen      # 브라우저 조작을 코드로 자동 생성
```

> CI 환경에서는 `CI=true npm test` 로 실행하면 서버를 항상 새로 시작하고 실패 시 1회 재시도합니다.

---

# Local DB Intellij 설정
- Database 탭에서 + 버튼을 클릭합니다.
- Data Source > Mysql DB를 선택합니다.
- Url에 jdbc:mysql://localhost:3306/local?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true 를 입력합니다.
- User에 root, Password에 root를 입력합니다.
- Test Connection 버튼을 클릭합니다.
- OK 버튼을 클릭합니다.
- 이제 Database 탭에서 local DB를 확인할 수 있습니다.
- 비밀번호 저장은 '계속'으로 해둡니다.

