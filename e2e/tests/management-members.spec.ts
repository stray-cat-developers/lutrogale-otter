import { test, expect } from '../fixtures/auth';

test.describe('management/members 페이지', () => {
  test.beforeEach(async ({ adminPage }) => {
    await adminPage.goto('/view/management/members');
    await adminPage.waitForSelector('#tb-users tbody tr');
  });

  test('페이지가 정상 렌더링된다', async ({ adminPage }) => {
    await expect(adminPage.locator('#tb-users')).toBeVisible();
    await expect(adminPage.locator('h1')).toContainText('사용자 정보 관리');
  });

  test('상태 변경 버튼이 btn-warning 클래스를 가진다', async ({ adminPage }) => {
    const statusBtns = adminPage.locator('.btn-group.pull-right .btn');
    await expect(statusBtns.first()).toHaveClass(/btn-warning/);
    await expect(statusBtns.first()).not.toHaveClass(/bg-orange/);
  });

  test('modal 컨테이너에 hidden 클래스가 적용된다', async ({ adminPage }) => {
    for (const id of [
      '#modal-user-status-content',
      '#modal-modify-multi-user-content',
      '#modal-modify-single-user-content',
      '#modal-group-detail-content',
    ]) {
      const el = adminPage.locator(id);
      await expect(el).toHaveClass(/hidden/);
      await expect(el).not.toHaveAttribute('style');
    }
  });

  test('사용자 체크 후 정보 수정 모달이 열린다 (getCheckedUsers 검증)', async ({ adminPage }) => {
    // 첫 번째 사용자 체크박스 선택
    const checkbox = adminPage.locator('#tb-users tbody input[name="cbx_user"]').first();
    await checkbox.check();

    // 수정 버튼 클릭
    await adminPage.click('button[onclick="modifyUserInfo();"]');
    await adminPage.waitForSelector('#modal-modify-user.in');

    // 모달에 선택된 사용자가 표시되어야 함
    await adminPage.waitForSelector('#modal-modify-user table tbody tr');
    await expect(adminPage.locator('#modal-modify-user .modal-body table tbody tr')).toHaveCount(1);
  });

  test('상태 변경 모달을 두 번 열어도 정상 표시된다 (clone 패턴 검증)', async ({ adminPage }) => {
    const checkbox = adminPage.locator('#tb-users tbody input[name="cbx_user"]').first();
    const dropdownToggle = adminPage.locator('.btn-group.pull-right .dropdown-toggle');
    await checkbox.check();

    // 첫 번째 열기 — dropdown을 먼저 열어야 링크가 보임
    await dropdownToggle.click();
    await adminPage.click('text=허용');
    await adminPage.waitForSelector('#modal-user-status.in');
    await adminPage.waitForSelector('#modal-user-status table tbody tr');
    await expect(adminPage.locator('#modal-user-status .modal-body')).not.toBeEmpty();

    await adminPage.click('#modal-user-status button.close');
    await adminPage.waitForSelector('#modal-user-status', { state: 'hidden' });

    // 두 번째 열기
    await dropdownToggle.click();
    await adminPage.click('text=허용');
    await adminPage.waitForSelector('#modal-user-status.in');
    await adminPage.waitForSelector('#modal-user-status table tbody tr');
    await expect(adminPage.locator('#modal-user-status .modal-body')).not.toBeEmpty();
  });
});
