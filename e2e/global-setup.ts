import { chromium, FullConfig } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

const AUTH_FILE = path.join(__dirname, '.auth/admin.json');

async function globalSetup(config: FullConfig) {
  const baseURL = config.projects[0].use.baseURL!;

  const browser = await chromium.launch();
  const context = await browser.newContext({ baseURL });

  // HTML 폼(jQuery AJAX)을 거치지 않고 로그인 API를 직접 호출
  // context.request는 브라우저 컨텍스트의 쿠키 jar를 공유하므로
  // SESSION 쿠키가 자동으로 저장된다
  const response = await context.request.post('/v1/check-login', {
    data: { email: 'admin@osori.com', password: 'admin' },
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok()) {
    const body = await response.text();
    throw new Error(`Login failed: HTTP ${response.status()} - ${body}`);
  }

  fs.mkdirSync(path.dirname(AUTH_FILE), { recursive: true });
  await context.storageState({ path: AUTH_FILE });
  await browser.close();
}

export default globalSetup;
