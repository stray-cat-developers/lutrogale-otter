import { test, expect, PROJECT_ID } from '../fixtures/auth';

test.describe('authority-groups 페이지', () => {
  test.beforeEach(async ({ adminPage }) => {
    await adminPage.goto(`/view/project/${PROJECT_ID}/configuration/authority`);
    // 권한 그룹 목록이 로드될 때까지 대기
    await adminPage.waitForSelector('#table-groups tbody tr');
  });

  test('페이지가 정상 렌더링된다', async ({ adminPage }) => {
    await expect(adminPage.locator('#table-groups')).toBeVisible();
    await expect(adminPage.locator('#menuNaviTree')).toBeVisible();
  });

  test('수정 버튼이 btn-warning 클래스를 가진다', async ({ adminPage }) => {
    const modifyBtn = adminPage.locator('#table-groups tbody #btn_modify').first();
    await expect(modifyBtn).toHaveClass(/btn-warning/);
    await expect(modifyBtn).not.toHaveClass(/bg-yellow/);
  });

  test('삭제 버튼이 btn-purple 클래스를 가진다', async ({ adminPage }) => {
    const deleteBtn = adminPage.locator('#table-groups tbody #btn_delete').first();
    await expect(deleteBtn).toHaveClass(/btn-purple/);
    await expect(deleteBtn).not.toHaveClass(/bg-purple/);
  });

  test('수정 모달을 두 번 열어도 jstree와 DataTable이 정상 표시된다', async ({ adminPage }) => {
    const modifyBtn = adminPage.locator('#table-groups tbody #btn_modify').first();

    // 첫 번째 열기
    await modifyBtn.click();
    await adminPage.waitForSelector('#modify-modal.in');
    await adminPage.waitForSelector('#modify-modal #modal-menu-tree .jstree-anchor');

    const modalBody = adminPage.locator('#modify-modal .modal-body');
    await expect(modalBody).not.toBeEmpty();
    await expect(adminPage.locator('#modify-modal #modal-menu-tree')).toBeVisible();

    // 닫기
    await adminPage.click('#modify-modal button.close');
    await adminPage.waitForSelector('#modify-modal', { state: 'hidden' });

    // 두 번째 열기 — clone 패턴 수정 검증
    await modifyBtn.click();
    await adminPage.waitForSelector('#modify-modal.in');
    await adminPage.waitForSelector('#modify-modal #modal-menu-tree .jstree-anchor');

    await expect(modalBody).not.toBeEmpty();
    await expect(adminPage.locator('#modify-modal #modal-menu-tree')).toBeVisible();
  });

  test('modal-content 원본 컨테이너가 비워지지 않는다 (clone 패턴 검증)', async ({ adminPage }) => {
    const modifyBtn = adminPage.locator('#table-groups tbody #btn_modify').first();

    await modifyBtn.click();
    await adminPage.waitForSelector('#modify-modal.in');

    // 원본 템플릿 컨테이너는 항상 form을 보유해야 한다
    const templateForm = adminPage.locator('#modal-content form');
    await expect(templateForm).toHaveCount(1);

    await adminPage.click('#modify-modal button.close');
    await adminPage.waitForSelector('#modify-modal', { state: 'hidden' });

    // 두 번째 열기 후에도 원본은 그대로 있어야 한다
    await modifyBtn.click();
    await adminPage.waitForSelector('#modify-modal.in');
    await expect(templateForm).toHaveCount(1);
  });

  test('트리 컨테이너에 separator-line hr이 표시된다', async ({ adminPage }) => {
    const hr = adminPage.locator('hr.separator-line').first();
    await expect(hr).toBeVisible();
    await expect(hr).not.toHaveAttribute('style');
  });
});
