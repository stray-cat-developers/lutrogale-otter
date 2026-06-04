import { defineConfig, devices } from '@playwright/test';

const BASE_URL = 'http://localhost:4211';

export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: [['html', { open: 'never' }], ['line']],
  globalSetup: './global-setup',

  use: {
    baseURL: BASE_URL,
    storageState: '.auth/admin.json',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],

  webServer: {
    command: 'cd .. && ./gradlew bootRun --args="--spring.profiles.active=embedded"',
    url: `${BASE_URL}/login.html`,
    timeout: 180_000,
    reuseExistingServer: !process.env.CI,
    stdout: 'ignore',
    stderr: 'pipe',
  },
});
