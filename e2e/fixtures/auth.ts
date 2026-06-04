import { test as base, Page, BrowserContext } from '@playwright/test';

export const ADMIN_EMAIL = 'admin@osori.com';
export const ADMIN_PASSWORD = 'admin';
export const PROJECT_ID = 1;

async function loginAsAdmin(page: Page): Promise<void> {
  await page.goto('/login.html');
  await page.fill('#email', ADMIN_EMAIL);
  await page.fill('#password', ADMIN_PASSWORD);

  await Promise.all([
    page.waitForURL('**/dashboard'),
    page.click('#submit'),
  ]);
}

export const test = base.extend<{
  adminPage: Page;
  adminContext: BrowserContext;
}>({
  adminContext: async ({ browser }, use) => {
    const context = await browser.newContext();
    const page = await context.newPage();
    await loginAsAdmin(page);
    await page.close();
    await use(context);
    await context.close();
  },

  adminPage: async ({ adminContext }, use) => {
    const page = await adminContext.newPage();
    await use(page);
    await page.close();
  },
});

export { expect } from '@playwright/test';
