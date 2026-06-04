import { test as base, Page } from '@playwright/test';

export const PROJECT_ID = 1;

export const test = base.extend<{ adminPage: Page }>({
  adminPage: async ({ page }, use) => {
    await use(page);
  },
});

export { expect } from '@playwright/test';
