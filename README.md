# Lutrogale-otter
### Permission check system

[![Build Status](https://github.com/stray-cat-developers/lutrogale-otter/actions/workflows/gradle.yml/badge.svg)](https://github.com/stray-cat-developers/lutrogale-otter)

## New Features!
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



