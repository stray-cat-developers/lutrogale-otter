import { test, expect } from '../fixtures/auth';

const uniqueEmail = (label: string) => `bulk-e2e-${label}-${Date.now()}@test.com`;

test.describe('사용자 대량 등록 UX', () => {
  test.beforeEach(async ({ adminPage }) => {
    await adminPage.goto('/view/management/members');
    await adminPage.waitForSelector('#tb-users tbody tr');
  });

  test('대량 등록 버튼이 표시되고 클릭하면 모달이 열린다', async ({ adminPage }) => {
    const btn = adminPage.locator('button.btn-success', { hasText: '대량 등록' });
    await expect(btn).toBeVisible();

    await btn.click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    await expect(adminPage.locator('#bulk-emails')).toBeVisible();
    await expect(adminPage.locator('#bulk-project')).toBeVisible();
    await expect(adminPage.locator('#bulk-authority')).toBeVisible();
    await expect(adminPage.locator('input[name="bulk-status"][value="ALLOW"]')).toBeChecked();
  });

  test('취소 버튼을 누르면 모달이 닫힌다', async ({ adminPage }) => {
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    await adminPage.click('#bulk-register-close');
    await adminPage.waitForSelector('#modal-bulk-register', { state: 'hidden' });
  });

  test('WAIT 상태를 선택하면 권한 그룹 셀렉트가 비활성화된다', async ({ adminPage }) => {
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    // 프로젝트 선택 후 권한 그룹 활성화 확인
    await adminPage.selectOption('#bulk-project', { index: 1 });
    await adminPage.waitForSelector('#bulk-authority:not([disabled])');
    await expect(adminPage.locator('#bulk-authority')).toBeEnabled();

    // WAIT 선택 → 권한 그룹 비활성화
    await adminPage.check('input[name="bulk-status"][value="WAIT"]');
    await expect(adminPage.locator('#bulk-authority')).toBeDisabled();

    // ALLOW 재선택 → 권한 그룹 재활성화 및 옵션 재로드
    await adminPage.check('input[name="bulk-status"][value="ALLOW"]');
    await adminPage.waitForSelector('#bulk-authority:not([disabled])');
    await expect(adminPage.locator('#bulk-authority')).toBeEnabled();
  });

  test('프로젝트를 선택하면 권한 그룹 목록이 로드된다', async ({ adminPage }) => {
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    await adminPage.selectOption('#bulk-project', { index: 1 });
    await adminPage.waitForFunction(() => (document.querySelector('#bulk-authority') as HTMLSelectElement)?.options.length > 1);

    const optionCount = await adminPage.locator('#bulk-authority option').count();
    expect(optionCount).toBeGreaterThan(1);
  });

  test('이메일을 입력하지 않고 등록하면 경고 알림이 뜬다', async ({ adminPage }) => {
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    const alertMessage = await new Promise<string>(resolve => {
      adminPage.once('dialog', dialog => {
        resolve(dialog.message());
        dialog.accept();
      });
      adminPage.click('#bulk-register-submit');
    });

    expect(alertMessage).toContain('이메일');
  });

  test('이메일 11개 입력 시 최대 개수 초과 경고 알림이 뜬다', async ({ adminPage }) => {
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    const tooManyEmails = Array.from({ length: 11 }, (_, i) => `over${i}@test.com`).join(', ');
    await adminPage.fill('#bulk-emails', tooManyEmails);

    const alertMessage = await new Promise<string>(resolve => {
      adminPage.once('dialog', dialog => {
        resolve(dialog.message());
        dialog.accept();
      });
      adminPage.click('#bulk-register-submit');
    });

    expect(alertMessage).toContain('10개');
  });

  test('신규 이메일 등록 시 진행률 바가 완료되고 SUCCESS 결과가 표시된다', async ({ adminPage }) => {
    const email = uniqueEmail('success');

    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    await adminPage.fill('#bulk-emails', email);
    await adminPage.click('#bulk-register-submit');

    // 진행 단계로 전환
    await adminPage.waitForSelector('#bulk-progress-step', { state: 'visible' });
    await adminPage.waitForSelector('#bulk-form-step', { state: 'hidden' });

    // 진행률 바 완료 대기 (progress-bar active 클래스 제거)
    await adminPage.waitForFunction(() => {
      const bar = document.querySelector('#bulk-progress-bar');
      return bar && !bar.classList.contains('active');
    }, { timeout: 10_000 });

    // 결과 목록에 SUCCESS 레이블 표시
    const resultRow = adminPage.locator('#bulk-result-list tr').first();
    await expect(resultRow).toContainText(email);
    await expect(resultRow.locator('.label-success')).toContainText('등록 완료');
  });

  test('이미 존재하는 이메일을 등록하면 SKIPPED 결과가 표시된다', async ({ adminPage }) => {
    const email = uniqueEmail('skip');

    // 1차 등록
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');
    await adminPage.fill('#bulk-emails', email);
    await adminPage.click('#bulk-register-submit');
    await adminPage.waitForFunction(() => {
      const bar = document.querySelector('#bulk-progress-bar');
      return bar && !bar.classList.contains('active');
    }, { timeout: 10_000 });
    await adminPage.click('#bulk-register-close');
    await adminPage.waitForSelector('#modal-bulk-register', { state: 'hidden' });

    // 2차 등록 — 같은 이메일
    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');
    await adminPage.fill('#bulk-emails', email);
    await adminPage.click('#bulk-register-submit');
    await adminPage.waitForFunction(() => {
      const bar = document.querySelector('#bulk-progress-bar');
      return bar && !bar.classList.contains('active');
    }, { timeout: 10_000 });

    const resultRow = adminPage.locator('#bulk-result-list tr').first();
    await expect(resultRow).toContainText(email);
    await expect(resultRow.locator('.label-default')).toContainText('이미 존재');
  });

  test('쉼표와 공백으로 구분된 이메일이 모두 등록된다', async ({ adminPage }) => {
    const ts = Date.now();
    const emails = [
      `bulk-sep1-${ts}@test.com`,
      `bulk-sep2-${ts}@test.com`,
      `bulk-sep3-${ts}@test.com`,
    ];

    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    await adminPage.fill('#bulk-emails', `${emails[0]}, ${emails[1]} ${emails[2]}`);
    await adminPage.click('#bulk-register-submit');
    await adminPage.waitForFunction(() => {
      const bar = document.querySelector('#bulk-progress-bar');
      return bar && !bar.classList.contains('active');
    }, { timeout: 10_000 });

    const rows = adminPage.locator('#bulk-result-list tr');
    await expect(rows).toHaveCount(3);
    await expect(rows.locator('.label-success')).toHaveCount(3);

    const barText = await adminPage.locator('#bulk-progress-bar').textContent();
    expect(barText).toContain('3 / 3');
  });

  test('프로젝트·권한 그룹 선택 후 등록하면 전체 워크플로가 완료된다', async ({ adminPage }) => {
    const email = uniqueEmail('full');

    await adminPage.locator('button.btn-success', { hasText: '대량 등록' }).click();
    await adminPage.waitForSelector('#modal-bulk-register.in');

    await adminPage.fill('#bulk-emails', email);

    // 프로젝트 선택
    await adminPage.selectOption('#bulk-project', { index: 1 });
    await adminPage.waitForFunction(() => (document.querySelector('#bulk-authority') as HTMLSelectElement)?.options.length > 1);

    // 첫 번째 권한 그룹 선택
    await adminPage.selectOption('#bulk-authority', { index: 1 });

    // ALLOW 상태 확인
    await expect(adminPage.locator('input[name="bulk-status"][value="ALLOW"]')).toBeChecked();

    await adminPage.click('#bulk-register-submit');
    await adminPage.waitForFunction(() => {
      const bar = document.querySelector('#bulk-progress-bar');
      return bar && !bar.classList.contains('active');
    }, { timeout: 10_000 });

    const resultRow = adminPage.locator('#bulk-result-list tr').first();
    await expect(resultRow.locator('.label-success')).toContainText('등록 완료');
  });
});
