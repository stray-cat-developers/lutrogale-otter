#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

GREEN='\033[0;32m'; RED='\033[0;31m'; YELLOW='\033[1;33m'; NC='\033[0m'

cleanup() {
    echo -e "\n${YELLOW}Stopping Docker containers...${NC}"
    docker-compose down
}
trap cleanup EXIT

# 1. MySQL 기동
echo -e "${GREEN}[1/3] Starting MySQL via Docker Compose...${NC}"
docker-compose up -d

# 2. MySQL ready 대기
echo -e "${GREEN}[2/3] Waiting for MySQL...${NC}"
RETRIES=30
until docker-compose exec -T mysql mysqladmin ping -h localhost -ulocal -plocal --silent 2>/dev/null; do
    RETRIES=$((RETRIES - 1))
    [ $RETRIES -le 0 ] && echo -e "${RED}MySQL did not become ready. Aborting.${NC}" && exit 1
    echo "  not ready, retrying... ($RETRIES attempts left)"
    sleep 3
done
echo "  MySQL ready."

# 3. npm 의존성 확인
if [ ! -d "e2e/node_modules" ]; then
    echo -e "${GREEN}Installing npm dependencies...${NC}"
    cd e2e && npm install && cd ..
fi

# 4. E2E 테스트 실행 (Spring Boot는 Playwright webServer가 관리)
echo -e "${GREEN}[3/3] Running E2E tests...${NC}"
cd e2e
EXIT_CODE=0
npm test || EXIT_CODE=$?

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}E2E tests passed.${NC}"
else
    echo -e "${RED}E2E tests failed (exit code: $EXIT_CODE).${NC}"
fi
exit $EXIT_CODE
