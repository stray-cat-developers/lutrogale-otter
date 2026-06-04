import { test, expect, PROJECT_ID } from '../fixtures/auth';

test.describe('project/configuration/members 페이지', () => {
  test.beforeEach(async ({ adminPage }) => {
    await adminPage.goto(`/view/project/${PROJECT_ID}/configuration/members`);
    await adminPage.waitForSelector('#tb-users tbody tr');
  });

  test('페이지가 정상 렌더링된다', async ({ adminPage }) => {
    await expect(adminPage.locator('#tb-users')).toBeVisible();
    await expect(adminPage.locator('#tb-authorized-group')).toBeVisible();
    await expect(adminPage.locator('#tb-authorized-personal')).toBeVisible();
  });

  test('modal 컨테이너에 style 속성 없이 hidden 클래스가 적용된다', async ({ adminPage }) => {
    for (const id of [
      '#modal-modify-user-content',
      '#modal-modify-group-content',
      '#modal-modify-personal-content',
      '#modal-group-detail-content',
    ]) {
      const el = adminPage.locator(id);
      await expect(el).toHaveClass(/hidden/);
      await expect(el).not.toHaveAttribute('style');
    }
  });

  test('권한그룹 상세 모달을 두 번 열어도 정상 표시된다', async ({ adminPage }) => {
    // 사용자 행 클릭 → 권한 그룹 로드
    await adminPage.click('#tb-users tbody tr:first-child');
    await adminPage.waitForSelector('#tb-authorized-group tbody tr');

    const detailBtn = adminPage.locator('#tb-authorized-group tbody button').first();

    // 첫 번째 열기
    await detailBtn.click();
    await adminPage.waitForSelector('#modal-group-detail.in');
    await adminPage.waitForSelector('#modal-group-detail #modal-menu-tree .jstree-anchor');

    await expect(adminPage.locator('#modal-group-detail .modal-body')).not.toBeEmpty();

    await adminPage.click('#modal-group-detail button.close');
    await adminPage.waitForSelector('#modal-group-detail', { state: 'hidden' });

    // 두 번째 열기 — clone 패턴 수정 검증
    await detailBtn.click();
    await adminPage.waitForSelector('#modal-group-detail.in');
    await adminPage.waitForSelector('#modal-group-detail #modal-menu-tree .jstree-anchor');

    await expect(adminPage.locator('#modal-group-detail .modal-body')).not.toBeEmpty();
  });
});
