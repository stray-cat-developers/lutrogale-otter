import { chromium, FullConfig } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

const AUTH_FILE = path.join(__dirname, '.auth/admin.json');

async function globalSetup(config: FullConfig) {
  const baseURL = config.projects[0].use.baseURL!;

  const browser = await chromium.launch();
  const context = await browser.newContext({ baseURL });
  const page = await context.newPage();

  await page.goto('/login.html');
  await page.locator('#email').fill('admin@osori.com');
  await page.locator('#password').fill('admin');

  await Promise.all([
    page.waitForURL('**/dashboard'),
    page.locator('#submit').click(),
  ]);

  fs.mkdirSync(path.dirname(AUTH_FILE), { recursive: true });
  await context.storageState({ path: AUTH_FILE });
  await browser.close();
}

export default globalSetup;
