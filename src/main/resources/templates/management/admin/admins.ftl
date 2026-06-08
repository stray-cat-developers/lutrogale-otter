<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
    <@layout.baseHeader "Admins">
    </@layout.baseHeader>

    <body class="hold-transition skin-blue sidebar-mini">
        <@layout.baseWrapper>
        <section class="content-header">
            <h1>어드민 관리<small>시스템 관리자 계정을 관리합니다.</small></h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-lg-12">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">어드민 목록</h3>
                            <div class="box-tools pull-right">
                                <button id="btn-add-admin" type="button" class="btn btn-primary btn-sm" style="display:none;">
                                    <i class="fa fa-plus"></i> 어드민 추가
                                </button>
                            </div>
                        </div>
                        <div class="box-body">
                            <table class="table table-bordered table-striped">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>이메일</th>
                                        <th>이름</th>
                                        <th>역할</th>
                                        <th>상위 어드민</th>
                                        <th>설명</th>
                                        <th>생성일</th>
                                        <th>액션</th>
                                    </tr>
                                </thead>
                                <tbody id="tb-admins-body"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <@layout.plainModal "" "modal-dialog" "modal-add-admin" "true"/>
        <@layout.plainModal "" "modal-dialog" "modal-change-pw" "true"/>
        </@layout.baseWrapper>

        <script>
            var currentAdminId = null;
            var currentAdminRole = null;
            var allAdmins = [];

            $(document).ready(function() {
                AJAX.getData(OsoriRoute.getUri('admin.findOne')).done(function(data) {
                    currentAdminId = data.id;
                    currentAdminRole = data.role;
                    if (currentAdminRole === 'SUPER') {
                        $('#btn-add-admin').show();
                    }
                }).always(function() {
                    loadAdmins();
                });

                $('#btn-add-admin').click(function() {
                    openAddAdminModal();
                });

                $('#modal-add-admin-submit').click(function() {
                    submitAddAdmin();
                });

                $('#modal-change-pw-submit').click(function() {
                    submitChangePw();
                });
            });

            function loadAdmins() {
                AJAX.getData(OsoriRoute.getUri('admins.findAll')).done(function(data) {
                    allAdmins = data.content;
                    renderAdminTable(allAdmins);
                });
            }

            function renderAdminTable(admins) {
                var tbody = $('#tb-admins-body');
                tbody.empty();
                $.each(admins, function(i, admin) {
                    tbody.append(buildRow(admin, false));
                    if (admin.children && admin.children.length > 0) {
                        $.each(admin.children, function(j, child) {
                            tbody.append(buildRow(child, true));
                        });
                    }
                });
            }

            function buildRow(admin, isChild) {
                var prefix = isChild ? '&nbsp;&nbsp;&nbsp;&nbsp;└&nbsp;' : '';
                var roleBadge = admin.role === 'SUPER'
                    ? '<span class="label label-danger">SUPER</span>'
                    : '<span class="label label-default">REGULAR</span>';
                var parentName = isChild ? (admin.parentAdminId || '-') : '-';
                var createdAt = admin.createdAt ? admin.createdAt.substring(0, 10) : '-';
                var actions = buildActions(admin);

                return '<tr>' +
                    '<td>' + admin.id + '</td>' +
                    '<td>' + prefix + admin.email + '</td>' +
                    '<td>' + admin.name + '</td>' +
                    '<td>' + roleBadge + '</td>' +
                    '<td>' + parentName + '</td>' +
                    '<td>' + (admin.description || '') + '</td>' +
                    '<td>' + createdAt + '</td>' +
                    '<td>' + actions + '</td>' +
                    '</tr>';
            }

            function buildActions(admin) {
                var html = '';
                if (currentAdminRole === 'SUPER' || admin.id === currentAdminId) {
                    html += '<button class="btn btn-warning btn-xs" onclick="openChangePwModal(' + admin.id + ')">PW 변경</button> ';
                }
                if (currentAdminRole === 'SUPER' && admin.id !== currentAdminId) {
                    html += '<button class="btn btn-danger btn-xs" onclick="expireAdmin(' + admin.id + ', \'' + admin.name + '\')">만료</button>';
                }
                return html;
            }

            function openAddAdminModal() {
                var parentOptions = '<option value="">없음</option>';
                $.each(allAdmins, function(i, a) {
                    parentOptions += '<option value="' + a.id + '">' + a.name + ' (' + a.email + ')</option>';
                });

                var body =
                    '<form role="form">' +
                    '<div class="form-group"><label>이메일</label><input id="add-email" type="email" class="form-control" placeholder="이메일 입력"></div>' +
                    '<div class="form-group"><label>이름</label><input id="add-name" type="text" class="form-control" placeholder="이름 입력"></div>' +
                    '<div class="form-group"><label>비밀번호</label><input id="add-pw" type="password" class="form-control" placeholder="비밀번호 입력"></div>' +
                    '<div class="form-group"><label>역할</label><select id="add-role" class="form-control"><option value="REGULAR">REGULAR</option><option value="SUPER">SUPER</option></select></div>' +
                    '<div class="form-group"><label>설명</label><input id="add-description" type="text" class="form-control" placeholder="설명 (선택)"></div>' +
                    '<div class="form-group"><label>상위 어드민</label><select id="add-parent" class="form-control">' + parentOptions + '</select></div>' +
                    '</form>';

                $('#modal-add-admin .modal-title').text('어드민 추가');
                $('#modal-add-admin .modal-body').html(body);
                $('#modal-add-admin').modal('show');
            }

            function submitAddAdmin() {
                var parentVal = $('#modal-add-admin #add-parent').val();
                var payload = {
                    email: $('#modal-add-admin #add-email').val(),
                    name: $('#modal-add-admin #add-name').val(),
                    pw: $('#modal-add-admin #add-pw').val(),
                    role: $('#modal-add-admin #add-role').val(),
                    description: $('#modal-add-admin #add-description').val() || null,
                    parentAdminId: parentVal ? parseInt(parentVal) : null
                };
                AJAX.postData(OsoriRoute.getUri('admins.create'), payload).done(function() {
                    $('#modal-add-admin').modal('hide');
                    loadAdmins();
                }).fail(function(res) {
                    alert(res.responseJSON ? res.responseJSON.message : '오류가 발생했습니다.');
                });
            }

            function expireAdmin(adminId, adminName) {
                if (!confirm('[' + adminName + '] 어드민을 만료 처리하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) return;
                AJAX.postData(OsoriRoute.getUri('admin.expire', {adminId: adminId}), {}).done(function() {
                    loadAdmins();
                }).fail(function(res) {
                    alert(res.responseJSON ? res.responseJSON.message : '오류가 발생했습니다.');
                });
            }

            function openChangePwModal(adminId) {
                var body =
                    '<form role="form">' +
                    '<input id="change-pw-target-id" type="hidden" value="' + adminId + '">' +
                    '<div class="form-group"><label>새 비밀번호</label><input id="change-pw-new" type="password" class="form-control" placeholder="새 비밀번호 입력"></div>' +
                    '</form>';

                $('#modal-change-pw .modal-title').text('비밀번호 변경');
                $('#modal-change-pw .modal-body').html(body);
                $('#modal-change-pw').modal('show');
            }

            function submitChangePw() {
                var adminId = parseInt($('#modal-change-pw #change-pw-target-id').val());
                var payload = { pw: $('#modal-change-pw #change-pw-new').val() };
                AJAX.putData(OsoriRoute.getUri('admin.changePassword', {adminId: adminId}), payload).done(function() {
                    $('#modal-change-pw').modal('hide');
                    alert('비밀번호가 변경되었습니다.');
                }).fail(function(res) {
                    alert(res.responseJSON ? res.responseJSON.message : '오류가 발생했습니다.');
                });
            }
        </script>
    </body>
</html>
