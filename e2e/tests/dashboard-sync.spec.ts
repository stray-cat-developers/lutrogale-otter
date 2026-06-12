import { Page } from '@playwright/test';
import { test, expect } from '../fixtures/auth';

// DataTable이 페이지네이션으로 특정 행을 숨길 수 있으므로 모든 행을 표시
async function showAllRows(page: Page): Promise<void> {
  await page.evaluate(() => {
    (window as any).$('#dataTables-projects').DataTable().page.len(-1).draw();
  });
}

// DataTable reload를 기다리는 waitForResponse + DataTable 전체 표시 헬퍼
async function waitForTableReloadAndShowAll(page: Page): Promise<void> {
  await page.waitForResponse(
    (r) => r.url().includes('/v1/maintenance/projects') && r.status() === 200,
  );
  await showAllRows(page);
}

test.describe('대시보드 자동 Sync 기능', () => {
  let testProjectId: number;

  test.beforeEach(async ({ adminPage }) => {
    const res = await adminPage.request.post('/v1/maintenance/project', {
      data: { name: 'E2E Sync Test', description: 'e2e test', listStructure: 'FLAT' },
      headers: { 'Content-Type': 'application/json' },
    });
    expect(res.ok()).toBeTruthy();
    const body = await res.json();
    testProjectId = body.content;

    await adminPage.goto('/dashboard');
    // DataTable AJAX 초기 로드 대기
    await adminPage.waitForResponse(
      (r) => r.url().includes('/v1/maintenance/projects') && r.status() === 200,
    );
    await adminPage.waitForSelector('#dataTables-projects tbody tr');
    await showAllRows(adminPage);
    // 생성한 프로젝트의 버튼이 DOM에 나타날 때까지 대기
    await adminPage.waitForSelector(`[data-project-id="${testProjectId}"]`);
  });

  test('Sync 미설정 프로젝트에 "자동Sync 추가" 버튼이 표시된다', async ({ adminPage }) => {
    await expect(
      adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`),
    ).toBeVisible();
  });

  test('"자동Sync 추가" 클릭 시 specType 드롭다운, URL 입력, 테스트·저장 버튼이 있는 모달이 열린다', async ({
    adminPage,
  }) => {
    await adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncAddModal.in');

    await expect(adminPage.locator('#sync-add-spec-type')).toBeVisible();
    await expect(adminPage.locator('#sync-add-url')).toBeVisible();
    await expect(adminPage.locator('#sync-add-test-btn')).toBeVisible();
    await expect(adminPage.locator('#sync-add-save-btn')).toBeVisible();
    // 수정 모달의 "Sync 삭제" 버튼은 등록 모달에 없어야 한다
    await expect(adminPage.locator('#sync-add-preview-result')).toBeAttached();
  });

  test('등록 모달은 닫기 버튼으로 닫힌다', async ({ adminPage }) => {
    await adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncAddModal.in');

    await adminPage.click('#syncAddModal button.close');
    await adminPage.waitForSelector('#syncAddModal', { state: 'hidden' });
    await expect(adminPage.locator('#syncAddModal')).not.toBeVisible();
  });

  test('등록 모달 specType 기본값은 OPENAPI_JSON이다', async ({ adminPage }) => {
    await adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncAddModal.in');

    await expect(adminPage.locator('#sync-add-spec-type')).toHaveValue('OPENAPI_JSON');
  });

  test('Sync 등록 후 해당 프로젝트의 버튼이 "자동 Sync"로 바뀐다', async ({ adminPage }) => {
    await adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncAddModal.in');

    await adminPage.selectOption('#sync-add-spec-type', 'OPENAPI_JSON');
    await adminPage.fill('#sync-add-url', 'http://example.com/openapi.json');

    await Promise.all([
      waitForTableReloadAndShowAll(adminPage),
      adminPage.click('#sync-add-save-btn'),
    ]);
    await adminPage.waitForSelector('#syncAddModal', { state: 'hidden' });

    await expect(
      adminPage.locator(`.btn-sync-edit[data-project-id="${testProjectId}"]`),
    ).toBeVisible();
    await expect(
      adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`),
    ).not.toBeAttached();
  });

  test('"자동 Sync" 클릭 시 기존 specType·URL이 채워진 수정 모달이 열린다', async ({
    adminPage,
  }) => {
    await adminPage.request.post(`/v1/maintenance/project/${testProjectId}/sync`, {
      data: { specType: 'OPENAPI_YAML', url: 'http://example.com/api.yaml' },
      headers: { 'Content-Type': 'application/json' },
    });

    await adminPage.reload();
    await adminPage.waitForResponse(
      (r) => r.url().includes('/v1/maintenance/projects') && r.status() === 200,
    );
    await adminPage.waitForSelector('#dataTables-projects tbody tr');
    await showAllRows(adminPage);
    await adminPage.waitForSelector(`[data-project-id="${testProjectId}"]`);

    await adminPage.locator(`.btn-sync-edit[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncEditModal.in');

    await expect(adminPage.locator('#sync-edit-spec-type')).toHaveValue('OPENAPI_YAML');
    await expect(adminPage.locator('#sync-edit-url')).toHaveValue('http://example.com/api.yaml');
    await expect(adminPage.locator('#sync-edit-test-btn')).toBeVisible();
    await expect(adminPage.locator('#sync-edit-save-btn')).toBeVisible();
    await expect(adminPage.locator('#sync-edit-delete-btn')).toBeVisible();
  });

  test('수정 모달에서 Sync 업데이트 후 변경된 URL이 다시 열면 반영된다', async ({
    adminPage,
  }) => {
    await adminPage.request.post(`/v1/maintenance/project/${testProjectId}/sync`, {
      data: { specType: 'OPENAPI_JSON', url: 'http://example.com/v1.json' },
      headers: { 'Content-Type': 'application/json' },
    });

    await adminPage.reload();
    await adminPage.waitForResponse(
      (r) => r.url().includes('/v1/maintenance/projects') && r.status() === 200,
    );
    await adminPage.waitForSelector('#dataTables-projects tbody tr');
    await showAllRows(adminPage);
    await adminPage.waitForSelector(`[data-project-id="${testProjectId}"]`);

    // 수정 모달 열기
    await adminPage.locator(`.btn-sync-edit[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncEditModal.in');

    await adminPage.fill('#sync-edit-url', 'http://example.com/v2.json');

    await Promise.all([
      waitForTableReloadAndShowAll(adminPage),
      adminPage.click('#sync-edit-save-btn'),
    ]);
    await adminPage.waitForSelector('#syncEditModal', { state: 'hidden' });

    // 다시 열어서 변경된 URL 확인
    await adminPage.locator(`.btn-sync-edit[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncEditModal.in');
    await expect(adminPage.locator('#sync-edit-url')).toHaveValue('http://example.com/v2.json');
  });

  test('수정 모달에서 Sync 삭제 후 "자동Sync 추가" 버튼으로 되돌아온다', async ({
    adminPage,
  }) => {
    await adminPage.request.post(`/v1/maintenance/project/${testProjectId}/sync`, {
      data: { specType: 'GRAPHQL', url: 'http://example.com/graphql' },
      headers: { 'Content-Type': 'application/json' },
    });

    await adminPage.reload();
    await adminPage.waitForResponse(
      (r) => r.url().includes('/v1/maintenance/projects') && r.status() === 200,
    );
    await adminPage.waitForSelector('#dataTables-projects tbody tr');
    await showAllRows(adminPage);
    await adminPage.waitForSelector(`[data-project-id="${testProjectId}"]`);

    await adminPage.locator(`.btn-sync-edit[data-project-id="${testProjectId}"]`).click();
    await adminPage.waitForSelector('#syncEditModal.in');

    // confirm 다이얼로그 자동 수락
    adminPage.once('dialog', (dialog) => dialog.accept());

    await Promise.all([
      waitForTableReloadAndShowAll(adminPage),
      adminPage.click('#sync-edit-delete-btn'),
    ]);
    await adminPage.waitForSelector('#syncEditModal', { state: 'hidden' });

    await expect(
      adminPage.locator(`.btn-sync-add[data-project-id="${testProjectId}"]`),
    ).toBeVisible();
    await expect(
      adminPage.locator(`.btn-sync-edit[data-project-id="${testProjectId}"]`),
    ).not.toBeAttached();
  });
});
