<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html>
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
                            </div>

                            <div class="btn-group pull-right">
                                <button type="button" class="btn bg-orange">사용자 상태 변경</button>
                                <button type="button" class="btn bg-orange dropdown-toggle" data-toggle="dropdown">
                                    <span class="caret"></span>
                                    <span class="sr-only">Toggle Dropdown</span>
                                </button>
                                <ul class="dropdown-menu" role="menu">
                                    <li><a href="#" onclick="changeUserStatus('allow');">허용</a></li>
                                    <li><a href="#" onclick="changeUserStatus('reject');">불가</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#" onclick="changeUserStatus('expire');">만료</a></li>
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

        <div id="modal-user-status-content" style="display: none;">
            <form role="form">
                <table class="table table-bordered table-striped"></table>
            </form>
        </div>

        <div id="modal-modify-multi-user-content" style="display: none;">
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
                                    <label>부서</label>
                                    <input id="user_department" type="text" class="form-control">
                                </div>
                            </div>
                            <!-- /.box-body -->
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div id="modal-modify-single-user-content" style="display: none;">
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
                                    <label>이름</label>
                                    <input id="user_name" type="text" class="form-control">
                                </div>
                                <div class="form-group">
                                    <label>부서</label>
                                    <input id="user_department" type="text" class="form-control">
                                </div>
                                <div class="form-group">
                                    <label>개인정보열람 가능</label>
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

        <div id="modal-group-detail-content" style="display: none;">
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
        <@layout.plainModal "" "modal-dialog" "modal-user-status" "true"/>
        <@layout.plainModal "" "modal-fullsize" "modal-modify-user" "true"/>

        </@layout.baseWrapper>
        <!-- Jstree https://github.com/orangehill/jstree-bootstrap-theme -->
        <script src="//cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
        <!-- Datatables -->
        <script src="/static/plugins/datatables/jquery.dataTables.js"></script>
        <script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
		<script>
            var projectId = extractByWord('project');
            var title = {'allow':'허용', 'reject':'불가', 'expire':'만료'};
            var opt = {
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
                var tb_project = $('#tb-project').DataTable(OPTION.data_table(opt.tb_project))
                    .on('click', 'tr', function(){
                        $('#tb-project').find('tr.active').removeClass('active');
                        $(this).addClass('active');

                        var project_obj = tb_project.row(this).data();

                        tb_group.columns(0).search(project_obj.id).draw();
                        tb_personal.columns(0).search(project_obj.id).draw();
                    });
                var tb_group = $('#tb-group').DataTable(OPTION.data_table(opt.tb_group));
                var tb_personal = $('#tb-personal').DataTable(OPTION.data_table(opt.tb_personal));

                AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data){
                    var tb_users = $('#tb-users').DataTable(OPTION.data_table(opt.tb_users, data.result))
                    .on('click', 'tr', function(){
                        $('#tb-users').find('tr.active').removeClass('active');
                        $(this).addClass('active');

                        var user_obj = tb_users.row(this).data();

                        AJAX.getData(OsoriRoute.getUri('user.findOne', {userId : user_obj.id}))
                        .done(function(data){
                            tb_project.clear().rows.add(data.result.projects).draw();
                            tb_group.clear().rows.add(data.result.authorityDefinitions).draw();
                            tb_personal.clear().rows.add(data.result.menuNavigations).draw();

                        }).done(function(){
                           $('#tb-group :button').click(function(){
                               var group_obj = tb_group.row($(this).parents('tr')).data();

                               $.when(
                                   AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:group_obj.projectId})),
                                   AJAX.getData(OsoriRoute.getUri('authority.findBundlesBranches', {id:group_obj.projectId, authId:group_obj.id}))
                               ).done(function(branch, bundleBranches){
                                   var all_branch = branch[0].result;
                                   var bundleBranches = bundleBranches[0].result;

                                   $('#modal-group-detail .modal-title').text('권한그룹 상세');
                                   $('#modal-group-detail .modal-body').empty().append($('#modal-group-detail-content form').clone());

                                   setTimeout(function(){
                                       $('#modal-group-detail #modal-selected').DataTable(OPTION.data_table(opt.tb_selected_group, [group_obj]));
                                       $('#modal-group-detail #modal-api-list').DataTable(OPTION.data_table(opt.table_api_list, _.pluck(bundleBranches, 'a_attr')));
                                   }, 300);

                                   $('#modal-group-detail #modal-menu-tree')
                                   .jstree('destroy')
                                   .jstree(OPTION.jstree(opt.menu_tree, all_branch))
                                   .on('loaded.jstree', function (event, data) {
                                       $(this).jstree("open_all");
                                   });

                               });

                               $('#modal-group-detail').modal();
                           }) ;

                        });

                    });

                });
            });

            function modifyUserInfo(){
                var select_count = $('#tb-users input[type="checkbox"]:checked').length;

                if (select_count < 1){
                    alert("사용자가 선택되어 있지 않습니다.");
                    return false;
                }

                var tb_users = $('#tb-users').DataTable();
                var checked_users = _.map($('#tb-users input[type="checkbox"]:checked'), function(v){
                    return tb_users.row($(v).parents('tr')).data();
                });

                var user_type = (select_count > 1)?'multi':'single';

                $('#modal-modify-user').find('.modal-title').text('사용자정보 수정');
                $('#modal-modify-user .modal-body').empty().append($('#modal-modify-'+user_type+'-user-content form').clone());
                setTimeout(function() {
                    $('#modal-modify-user table').DataTable(OPTION.data_table(opt.tb_modal_user_status, checked_users));
                }, 300);

                $('#modal-modify-user').modal();

                if(select_count == 1){
                    $('#modal-modify-user #user_name').val(checked_users[0].name);
                    $('#modal-modify-user #user_department').val(checked_users[0].department),
                    $('#modal-modify-user #user_privacy').val(checked_users[0].accessPrivacyInformation+"");
                }

            }

            $('#modal-modify-user-submit').click(function(){
                var tb_selected = $('#modal-modify-user table').DataTable();
                var data_obj = tb_selected.rows().data();

                var after_func = function(){
                    var tb_users = $('#tb-users').DataTable();
                    AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data){
                        tb_users.clear().rows.add(data.result).draw();
                    });

                    $('#modal-modify-user').modal('hide');
                };

                if (data_obj.length > 1){
                    AJAX.putData(
                        OsoriRoute.getUri('users.modifyInfo',{'userIdGroup': _.pluck(data_obj, 'id').join()}),
                        {department: $('#modal-modify-user #user_department').val()}
                    ).done(after_func);
                }else{
                    var param = {
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
                var select_count = $('#tb-users input[type="checkbox"]:checked').length;

                if (select_count < 1){
                    alert("사용자가 선택되어 있지 않습니다.");
                    return false;
                }

                var tb_users = $('#tb-users').DataTable();
                var checked_users = _.map($('#tb-users input[type="checkbox"]:checked'), function(v){
                    return tb_users.row($(v).parents('tr')).data();
                });

                $('#modal-user-status-submit').prop('value', type);
                $('#modal-user-status').find('.modal-title').text('상태변경 ['+title[type]+']');
                $('#modal-user-status .modal-body').append($('#modal-user-status-content form'));
                setTimeout(function() {
                    $('#modal-user-status table').DataTable(OPTION.data_table(opt.tb_modal_user_status, checked_users));
                }, 300);

                $('#modal-user-status').modal();

            }

            $('#modal-user-status-submit').click(function() {
                var status_type = $(this).val();

                if (!confirm('선택된 사용자들을 ' + title[status_type] + '처리 하시겠습니까?'))
                    return false;

                var tb_users = $('#tb-users').DataTable();
                var checked_users = _.map($('#tb-users input[type="checkbox"]:checked'), function(v){
                    return tb_users.row($(v).parents('tr')).data();
                });
                var target_id = _.pluck(checked_users, 'id');

                var after_func = function(){
                    $('#modal-user-status').modal('hide');

                    AJAX.getData(OsoriRoute.getUri("users.findAll")).done(function(data){
                        tb_users.clear().rows.add(data.result).draw();
                    });
                }

                if(status_type == 'expire'){
                    AJAX.deleteData(
                        OsoriRoute.getUri('users.expireStatus',{userIdGroup: target_id.join()})
                    ).done(after_func);
                }else{
                    AJAX.putData(
                        OsoriRoute.getUri('users.modifyInfo',{userIdGroup: target_id.join()}),
                        {'status': status_type}
                    )
                    .done(after_func)
                    .fail(function(res, text){
                        alert(res.responseJSON.message);
                    });
                }

            });
		</script>
	</body>
</html>
