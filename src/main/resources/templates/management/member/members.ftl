<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
	<@layout.baseHeader "Users">
        <link rel="stylesheet" href="/static/plugins/datatables/extensions/Select/select.dataTables.min.css">
	</@layout.baseHeader>

	<body class="hold-transition skin-blue sidebar-mini">
		<@layout.baseWrapper>
        <section class="content-header">
            <h1>사용자 정보 관리<small>오소리 권한 시스템을 이용하는 사용자를 관리합니다.</small></h1>
        </section>
		<section class="content">
            <div class="row">
                <div class="col-lg-12">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">사용자 정보</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="tb-users" class="table table-bordered table-striped"></table>
                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer">
                            <div class="btn-group">
                                <button type="button" class="btn btn-primary" onclick="modifyUserInfo();">사용자정보 수정</button>
                                <button type="button" class="btn btn-primary" onclick="OsoriRoute.go('view.management.newMember');">사용자 생성</button>
                                <button type="button" class="btn btn-success" onclick="openBulkRegisterModal();">대량 등록</button>
                            </div>

                            <div class="btn-group pull-right">
                                <button type="button" class="btn btn-warning">사용자 상태 변경</button>
                                <button type="button" class="btn bg-orange dropdown-toggle" data-toggle="dropdown">
                                    <span class="caret"></span>
                                    <span class="sr-only">Toggle Dropdown</span>
                                </button>
                                <ul class="dropdown-menu" role="menu">
                                    <li><a href="#" onclick="changeUserStatus('ALLOW');">허용</a></li>
                                    <li><a href="#" onclick="changeUserStatus('REJECT');">불가</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#" onclick="changeUserStatus('EXPIRE');">만료</a></li>
                                </ul>
                            </div>
						</div>
                    </div>
				</div>
            </div>
            <div class="row">
                <div class="col-lg-4">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">프로젝트 필터</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="tb-project" class="table table-bordered table-striped"></table>
                        </div>
                        <!-- /.box-body -->
                    </div>
                </div>
                <div class="col-lg-8">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">허가된 권한 그룹</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="tb-group" class="table table-bordered table-striped"></table>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">허가된 개인 별 네비게이션</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="tb-personal" class="table table-bordered table-striped"></table>
                        </div>
                        <!-- /.box-body -->
                    </div>
                </div>
            </div>
		</section>

        <div id="modal-user-status-content" class="hidden">
            <form role="form">
                <table class="table table-bordered table-striped"></table>
            </form>
        </div>

        <div id="modal-modify-multi-user-content" class="hidden">
            <form role="form-horizontal">
                <div class="row">
                    <div class="col-md-9">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">선택된 사용자</h3>
                            </div>
                            <div class="box-body">
                                <table class="table table-bordered table-striped"></table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">일괄수정</h3>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body">
                                <!-- text input -->
                                <div class="form-group">
                                    <label for="user_department">부서</label>
                                    <input id="user_department" type="text" class="form-control">
                                </div>
                            </div>
                            <!-- /.box-body -->
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div id="modal-modify-single-user-content" class="hidden">
            <form role="form-horizontal">
                <div class="row">
                    <div class="col-md-9">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">선택된 사용자</h3>
                            </div>
                            <div class="box-body">
                                <table class="table table-bordered table-striped"></table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title">수정</h3>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body">
                                <!-- text input -->
                                <div class="form-group">
                                    <label for="user_name">이름</label>
                                    <input id="user_name" type="text" class="form-control">
                                </div>
                                <div class="form-group">
                                    <label for="user_department">부서</label>
                                    <input id="user_department" type="text" class="form-control">
                                </div>
                                <div class="form-group">
                                    <label for="user_privacy">개인정보열람 가능</label>
                                    <select id="user_privacy" class="form-control">
                                        <option value="true">가능</option>
                                        <option value="false">불가</option>
                                    </select>
                                </div>
                            </div>
                            <!-- /.box-body -->
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div id="modal-group-detail-content" class="hidden">
            <form role="form-horizontal">
                <div class="row">
                    <div class="col-md-12">
                        <table id="modal-selected" class="table table-bordered table-striped"></table>
                    </div>
                </div>
                </br>
                <div class="row">
                    <div class="col-lg-4 col-xs-12">
                        <div class="box">
                            <div class="box-header">
                                <h4 class="box-title">메뉴구조</h4>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body">
                                <div id="modal-menu-tree"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-8 col-xs-12">
                        <div class="box">
                            <div class="box-header">
                                <h4 class="box-title">그룹 API 리스트</h4>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body">
                                <table id="modal-api-list" class="table table-bordered table-striped"></table>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <@layout.plainModal "" "modal-fullsize" "modal-group-detail" ""/>
        <div id="modal-bulk-register" class="modal fade" role="dialog" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">x</span></button>
                        <h4 class="modal-title">사용자 대량 등록</h4>
                    </div>
                    <div class="modal-body">
                        <div id="bulk-form-step">
                            <div class="form-group">
                                <label>이메일 목록 <small class="text-muted">(쉼표 또는 공백으로 구분, 최대 10개)</small></label>
                                <textarea id="bulk-emails" class="form-control" rows="4"
                                    placeholder="예) user1@example.com, user2@example.com user3@example.com"></textarea>
                            </div>
                            <div class="form-group">
                                <label>프로젝트 <small class="text-muted">(선택)</small></label>
                                <select id="bulk-project" class="form-control">
                                    <option value="">-- 선택 안 함 --</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>권한 그룹 <small class="text-muted">(선택, 프로젝트 선택 시 활성화)</small></label>
                                <select id="bulk-authority" class="form-control" disabled>
                                    <option value="">-- 선택 안 함 --</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>초기 상태</label>
                                <div>
                                    <label class="radio-inline">
                                        <input type="radio" name="bulk-status" value="ALLOW" checked> 허용 (ALLOW)
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="bulk-status" value="WAIT"> 대기 (WAIT)
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div id="bulk-progress-step" style="display:none">
                            <div class="progress">
                                <div id="bulk-progress-bar" class="progress-bar progress-bar-striped active"
                                     role="progressbar" style="width:0%">0 / 0</div>
                            </div>
                            <table class="table table-condensed" style="margin-top:10px">
                                <thead><tr><th>이메일</th><th>결과</th></tr></thead>
                                <tbody id="bulk-result-list"></tbody>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button id="bulk-register-close" type="button" class="btn btn-default pull-left" data-dismiss="modal">취소</button>
                        <button id="bulk-register-submit" type="button" class="btn btn-success">등록</button>
                    </div>
                </div>
            </div>
        </div>

        <@layout.plainModal "" "modal-dialog" "modal-user-status" "true"/>
        <@layout.plainModal "" "modal-fullsize" "modal-modify-user" "true"/>

        </@layout.baseWrapper>
        <script src="/static/plugins/jstree/jstree.min.js"></script>
        <!-- Datatables -->
        <script src="/static/plugins/datatables/jquery.dataTables.js"></script>
        <script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
		<script>
            let projectId = extractByWord('project');
            let title = {'ALLOW':'허용', 'REJECT':'불가', 'EXPIRE':'만료'};
            let opt = {
                'tb_users': {
                    'info': true,
                    'paging': true,
                    'searching': true,
                    'columns': [
                        {title: ''},
                        {title: 'email', data: 'email'},
                        {title: '이름', data: 'name'},
                        {title: '부서', data: 'department'},
                        {title: '개인정보열람', data: 'accessPrivacyInformation'},
                        {title: '등록일', data: 'regDate'},
                        {title: '상태', data: 'status'}
                    ],
                    'columnDefs': [
                        {
                            'targets': 0,
                            'data': null,
                            'defaultContent': '<input name="cbx_user" type="checkbox" class="flat-red">'
                        }
                    ]
                },
                'tb_project': {
                    'columns': [
                        {title: '프로젝트ID', data: 'id'},
                        {title: '프로젝트명', data: 'name'},
                    ],
                    'columnDefs': [
                        {
                            "targets": [ 0 ],
                            "visible": false
                        }
                    ]
                },
                'tb_group': {
                    'sDom': '<"top">t<"bottom"ilp<"clear">>',
                    'searching': true,
                    'columns': [
                        {title: '프로젝트ID', data: 'projectId'},
                        {title: '프로젝트명', data: 'projectName'},
                        {title: '권한명', data: 'name'},
                        {title: '적용일', data: 'regDate'}
                    ],
                    'columnDefs': [
                        {
                            'targets': [0],
                            'visible': false
                        },
                        {
                            'targets': 4,
                            'data': null,
                            'defaultContent': '<button type="button" class="btn btn-block btn-info btn-xs">상세</button>'
                        }
                    ]
                },
                'tb_personal': {
                    'sDom': '<"top">t<"bottom"ilp<"clear">>',
                    'searching': true,
                    'columns': [
                        {title: '프로젝트ID', data: 'projectId'},
                        {title: '프로젝트명', data: 'projectName'},
                        {title: '타입', data: 'type'},
                        {title: '메뉴명', data: 'name'},
                        {title: '적용일', data: 'regDate'}
                    ],
                    'columnDefs': [
                        {
                            'targets': [0],
                            'visible': false
                        }
                    ]
                },
                'tb_modal_user_status': {
                    'columns': [
                        {title: 'email', data: 'email'},
                        {title: '이름', data: 'name'},
                        {title: '부서', data: 'department'},
                        {title: '개인정보열람', data: 'accessPrivacyInformation'},
                        {title: '등록일', data: 'regDate'},
                    ]
                },
                'table_api_list': {
                    'columns': [
                        {title: 'API ID', data: 'id'},
                        {title: '타입', data: 'type'},
                        {title: '메뉴명', data: 'name'},
                        {title: 'URL 경로', data: 'fullUrl'}
                    ]
                },
                'tb_selected_group':{
                    'info': false,
                    'ordering': false,
                    'columns': [
                        {title: '프로젝트ID', data: 'projectId'},
                        {title: '프로젝트명', data: 'projectName'},
                        {title: '권한명', data: 'name'},
                        {title: '적용일', data: 'regDate'}
                    ]
                },
                'menu_tree': {
                    'plugins': ['sort', 'types']
                }
            };

            $(document).ready(function() {
                let tb_project = $('#tb-project').DataTable(OPTION.data_table(opt.tb_project))
                    .on('click', 'tr', function(){
                        $('#tb-project').find('tr.active').removeClass('active');
                        $(this).addClass('active');

                        let project_obj = tb_project.row(this).data();

                        tb_group.columns(0).search(project_obj.id).draw();
                        tb_personal.columns(0).search(project_obj.id).draw();
                    });
                let tb_group = $('#tb-group').DataTable(OPTION.data_table(opt.tb_group));
                let tb_personal = $('#tb-personal').DataTable(OPTION.data_table(opt.tb_personal));

                AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data){
                    let tb_users = $('#tb-users').DataTable(OPTION.data_table(opt.tb_users, data.content))
                    .on('click', 'tr', function(){
                        $('#tb-users').find('tr.active').removeClass('active');
                        $(this).addClass('active');

                        let user_obj = tb_users.row(this).data();

                        AJAX.getData(OsoriRoute.getUri('user.findOne', {userId : user_obj.id}))
                        .done(function(data){
                            tb_project.clear().rows.add(data.projects).draw();
                            tb_group.clear().rows.add(data.authorityDefinitions).draw();
                            tb_personal.clear().rows.add(data.menuNavigations).draw();

                        }).done(function(){
                           $('#tb-group :button').click(function(){
                               let group_obj = tb_group.row($(this).parents('tr')).data();

                               $.when(
                                   AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:group_obj.projectId})),
                                   AJAX.getData(OsoriRoute.getUri('authority.findBundlesBranches', {id:group_obj.projectId, authId:group_obj.id}))
                               ).done(function(first, second){
                                   let all_branch = first[0].content;
                                   let bundleBranch = second[0].content;

                                   $('#modal-group-detail .modal-title').text('권한그룹 상세');
                                   $('#modal-group-detail .modal-body').empty().append($('#modal-group-detail-content form').clone());

                                   setTimeout(function(){
                                       $('#modal-group-detail #modal-selected').DataTable(OPTION.data_table(opt.tb_selected_group, [group_obj]));
                                       $('#modal-group-detail #modal-api-list').DataTable(OPTION.data_table(opt.table_api_list, _.pluck(bundleBranch, 'a_attr')));
                                   }, 300);

                                   $('#modal-group-detail #modal-menu-tree')
                                   .jstree('destroy')
                                   .jstree(OPTION.jstree(opt.menu_tree, all_branch))
                                   .on('loaded.jstree', function () {
                                       $(this).jstree("open_all");
                                   });

                               });

                               $('#modal-group-detail').modal();
                           }) ;

                        });

                    });

                });
            });

            function getCheckedUsers() {
                let tb_users = $('#tb-users').DataTable();
                return _.map($('#tb-users input[type="checkbox"]:checked'), function(v) {
                    return tb_users.row($(v).parents('tr')).data();
                });
            }

            function modifyUserInfo(){
                let select_count = $('#tb-users input[type="checkbox"]:checked').length;

                if (select_count < 1){
                    alert("사용자가 선택되어 있지 않습니다.");
                    return false;
                }

                let checked_users = getCheckedUsers();

                let user_type = (select_count > 1)?'multi':'single';

                $('#modal-modify-user').find('.modal-title').text('사용자정보 수정');
                $('#modal-modify-user .modal-body').empty().append($('#modal-modify-'+user_type+'-user-content form').clone());
                setTimeout(function() {
                    $('#modal-modify-user table').DataTable(OPTION.data_table(opt.tb_modal_user_status, checked_users));
                }, 300);

                $('#modal-modify-user').modal();

                if(select_count === 1){
                    $('#modal-modify-user #user_name').val(checked_users[0].name);
                    $('#modal-modify-user #user_department').val(checked_users[0].department);
                    $('#modal-modify-user #user_privacy').val(checked_users[0].accessPrivacyInformation+"");
                }

            }

            $('#modal-modify-user-submit').click(function(){
                let tb_selected = $('#modal-modify-user table').DataTable();
                let data_obj = tb_selected.rows().data();

                let after_func = function(){
                    let tb_users = $('#tb-users').DataTable();
                    AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data){
                        tb_users.clear().rows.add(data.content).draw();
                    });

                    $('#modal-modify-user').modal('hide');
                };

                if (data_obj.length > 1){
                    AJAX.putData(
                        OsoriRoute.getUri('users.modifyInfo',{'userIdGroup': _.pluck(data_obj, 'id').join()}),
                        {department: $('#modal-modify-user #user_department').val()}
                    ).done(after_func);
                }else{
                    let param = {
                        name: $('#modal-modify-user #user_name').val(),
                        department: $('#modal-modify-user #user_department').val(),
                        isPrivacy: $('#modal-modify-user #user_privacy').val()
                    };
                    AJAX.putData(
                        OsoriRoute.getUri('user.modifyInfo',{'userId': data_obj[0].id}),
                        param
                    ).done(after_func);
                }

            });

            function changeUserStatus(type){
                let select_count = $('#tb-users input[type="checkbox"]:checked').length;

                if (select_count < 1){
                    alert("사용자가 선택되어 있지 않습니다.");
                    return false;
                }

                let checked_users = getCheckedUsers();

                $('#modal-user-status-submit').prop('value', type);
                $('#modal-user-status').find('.modal-title').text('상태변경 ['+title[type]+']');
                $('#modal-user-status .modal-body').empty().append($('#modal-user-status-content form').clone());
                setTimeout(function() {
                    $('#modal-user-status table').DataTable(OPTION.data_table(opt.tb_modal_user_status, checked_users));
                }, 300);

                $('#modal-user-status').modal();

            }

            $('#modal-user-status-submit').click(function() {
                let status_type = $(this).val();

                if (!confirm('선택된 사용자들을 ' + title[status_type] + '처리 하시겠습니까?'))
                    return false;

                let checked_users = getCheckedUsers();
                let target_id = _.pluck(checked_users, 'id');
                let tb_users = $('#tb-users').DataTable();

                let after_func = function(){
                    $('#modal-user-status').modal('hide');

                    AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data){
                        tb_users.clear().rows.add(data.content).draw();
                    });
                }

                if(status_type === 'EXPIRE'){
                    AJAX.deleteData(
                        OsoriRoute.getUri('users.expireStatus',{userIdGroup: target_id.join()})
                    ).done(after_func);
                }else{
                    AJAX.putData(
                        OsoriRoute.getUri('users.modifyInfo',{userIdGroup: target_id.join()}),
                        {'status': status_type}
                    )
                    .done(after_func)
                    .fail(function(res){
                        alert(res.responseJSON.message);
                    });
                }

            });

            function openBulkRegisterModal() {
                $('#bulk-form-step').show();
                $('#bulk-progress-step').hide();
                $('#bulk-emails').val('');
                $('#bulk-project').val('');
                $('#bulk-authority').val('').prop('disabled', true).html('<option value="">-- 선택 안 함 --</option>');
                $('input[name="bulk-status"][value="ALLOW"]').prop('checked', true);
                $('#bulk-register-submit').show().prop('disabled', false);
                $('#bulk-register-close').text('취소');
                $('#bulk-result-list').empty();

                AJAX.getData('/v1/maintenance/projects').done(function(data) {
                    let $select = $('#bulk-project').html('<option value="">-- 선택 안 함 --</option>');
                    $.each(data.content, function(i, p) {
                        $select.append('<option value="' + p.id + '">' + p.name + '</option>');
                    });
                });

                $('#modal-bulk-register').modal('show');
            }

            $('#bulk-project').change(function() {
                let pid = $(this).val();
                let $authSelect = $('#bulk-authority');
                $authSelect.prop('disabled', true).html('<option value="">-- 선택 안 함 --</option>');
                $('input[name="bulk-status"]').prop('disabled', false);

                if (!pid) return;

                AJAX.getData('/v1/maintenance/project/' + pid + '/authority-bundles').done(function(data) {
                    $.each(data.content, function(i, a) {
                        $authSelect.append('<option value="' + a.authId + '">' + a.name + '</option>');
                    });
                    $authSelect.prop('disabled', false);
                });
            });

            $('input[name="bulk-status"]').change(function() {
                if ($(this).val() === 'WAIT') {
                    $('#bulk-authority').val('').prop('disabled', true);
                } else {
                    let pid = $('#bulk-project').val();
                    if (pid) {
                        $('#bulk-project').trigger('change');
                    }
                }
            });

            $('#bulk-register-submit').click(function() {
                let rawText = $('#bulk-emails').val().trim();
                if (!rawText) { alert('이메일을 입력해주세요.'); return; }

                let emails = rawText.split(/[\s,]+/).filter(function(e) { return e.length > 0; });
                if (emails.length === 0) { alert('유효한 이메일이 없습니다.'); return; }
                if (emails.length > 10) { alert('이메일은 최대 10개까지 입력 가능합니다.'); return; }

                let projectId = $('#bulk-project').val() || null;
                let authorityDefinitionId = $('#bulk-authority').val() || null;
                let initialStatus = $('input[name="bulk-status"]:checked').val();

                let payload = { emails: emails, projectId: projectId, authorityDefinitionId: authorityDefinitionId, initialStatus: initialStatus };
                if (projectId) payload.projectId = parseInt(projectId);
                if (authorityDefinitionId) payload.authorityDefinitionId = parseInt(authorityDefinitionId);

                $('#bulk-form-step').hide();
                $('#bulk-progress-step').show();
                $('#bulk-register-submit').hide();
                $('#bulk-register-close').text('닫기');

                let total = emails.length;
                $('#bulk-progress-bar').css('width', '0%').text('0 / ' + total);

                $.ajax({
                    url: '/v1/maintenance/management/user/batch',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(payload),
                    success: function(res) {
                        let results = res.content;
                        let shown = 0;
                        let intervalId = setInterval(function() {
                            if (shown >= results.length) {
                                clearInterval(intervalId);
                                $('#bulk-progress-bar').removeClass('active');
                                let tb_users = $('#tb-users').DataTable();
                                AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data) {
                                    tb_users.clear().rows.add(data.content).draw();
                                });
                                return;
                            }
                            let r = results[shown];
                            let isSuccess = r.outcome === 'SUCCESS';
                            let $label = isSuccess
                                ? $('<span class="label label-success">').text('등록 완료')
                                : $('<span class="label label-default">').text('이미 존재');
                            let $row = $('<tr>').append($('<td>').text(r.email)).append($('<td>').append($label));
                            $('#bulk-result-list').append($row);
                            shown++;
                            let pct = Math.round(shown / total * 100);
                            $('#bulk-progress-bar').css('width', pct + '%').text(shown + ' / ' + total);
                        }, 100);
                    },
                    error: function(res) {
                        $('#bulk-form-step').show();
                        $('#bulk-progress-step').hide();
                        $('#bulk-register-submit').show();
                        $('#bulk-register-close').text('취소');
                        let msg = (res.responseJSON && res.responseJSON.message) ? res.responseJSON.message : '등록 중 오류가 발생했습니다.';
                        alert(msg);
                    }
                });
            });
		</script>
	</body>
</html>
