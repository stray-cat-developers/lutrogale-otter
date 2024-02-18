<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
<@layout.baseHeader "Project Users">
<link rel="stylesheet" href="/static/plugins/datatables/extensions/Select/select.dataTables.min.css">
</@layout.baseHeader>

<body class="hold-transition skin-blue sidebar-mini">
<@layout.baseWrapper>
<section class="content-header">
    <h1>사용자 권한 관리<small>프로젝트에 접근 가능한 사용자들을 관리합니다.</small></h1>
</section>
<section class="content">
    <div class="row">
        <div class="col-lg-12">
            <div class="box box-solid">
                <div class="box-header">
                    <button id="btn-modify-user" type="button" class="btn btn-primary btn-sm pull-right">프로젝트 할당</button>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <table id="tb-users" class="table table-bordered table-striped"></table>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-6">
            <div class="box box-solid">
                <div class="box-header">
                    <h3 class="box-title">허가된 권한 그룹</h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <table id="tb-authorized-group" class="table table-bordered table-striped"></table>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
        <div class="col-lg-6">
            <div class="box box-solid">
                <div class="box-header">
                    <h3 class="box-title">허가된 개인 별 네비게이션</h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <table id="tb-authorized-personal" class="table table-bordered table-striped"></table>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
    </div>
</section>

<div id="modal-modify-user-content" style="display: none;">
    <form role="form">
        <div class="col-md-6">
            <div class="box box-solid">
                <div class="box-header with-border">
                    <h4>사용자리스트</h4>
                </div>
                <div class="box-body">
                    <table id="tb-user" class="table table-bordered table-striped"></table>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="box box-solid">
                <div class="box-header with-border">
                    <h4>권한 그룹 리스트</h4>
                </div>
                <div class="box-body">
                    <table id="tb-group" class="table table-bordered table-striped"></table>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="modal-modify-group-content" style="display: none;">
    <form role="form">
        <div class="col-lg-4 col-xs-12">
            <div class="box box-solid">
                <div class="box-header">
                    <h4 class="box-title">권한 그룹</h4>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <table id="tb-group-list" class="table table-bordered table-striped"></table>
                </div>
            </div>
        </div>
        <div class="col-lg-8 col-xs-12">
            <div class="box box-solid">
                <div class="box-header">
                    <h4 class="box-title">API 리스트</h4>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <table id="tb-group-api-list" class="table table-bordered table-striped"></table>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="modal-modify-personal-content" style="display: none;">
    <form role="form">
        <div class="col-lg-12 col-xs-12">
            <div class="box box-solid">
                <div class="box-body">
                    <table id="tb-api-list" class="table table-bordered table-striped"></table>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="modal-group-detail-content" style="display: none;">
    <form role="form">
        <div class="col-lg-5 col-xs-12">
            <div class="box box-solid">
                <div class="box-header">
                    <h4 class="box-title">네비게이션 트리</h4>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <div id="modal-menu-tree"></div>
                </div>
            </div>
        </div>
        <div class="col-lg-7 col-xs-12">
            <div class="box box-solid">
                <div class="box-header">
                    <h4 class="box-title">API 리스트</h4>
                </div>
                <!-- /.box-header -->
                <div class="box-body">
                    <table id="modal-selected" class="table table-bordered table-striped"></table>
                </div>
            </div>
        </div>
    </form>
</div>

<!-- /.modal -->
<@layout.plainModal "" "modal-lg" "modal-modify-user" ""/>
<@layout.plainModal "" "modal-fullsize" "modal-modify-group" ""/>
<@layout.plainModal "" "modal-fullsize" "modal-modify-personal" ""/>
<@layout.plainModal "" "modal-fullsize" "modal-group-detail" ""/>

</@layout.baseWrapper>
<script src="/static/plugins/jstree/jstree.min.js"></script>
<script src="/static/plugins/datatables/jquery.dataTables.js"></script>
<script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script>
    var opt = {
        'tb_modal_users': {
            'columns': [
                {title: 'email', data: 'email'},
                {title: '이름', data: 'name'},
                {title: '부서', data: 'department'}
            ]
        },
        'tb_modal_group': {
            'columns': [
                {title: '권한명', data: 'name'}
            ],
            'columnDefs': [
                {
                    'targets': 1,
                    'data': null,
                    'defaultContent': '<input name="is-avail" type="checkbox" data-size="mini">'
                }
            ]
        },
        'tb_users': {
            'columns': [
                {title: 'email', data: 'email'},
                {title: '이름', data: 'name'},
                {title: '부서', data: 'department'},
                {title: '개인정보 열람', data: 'accessPrivacyInformation'},
                {title: '등록일', data: 'regDate'},
                {title: '상태', data: 'status'}
            ],
            'columnDefs': [
                {
                    'targets': 6,
                    'data': null,
                    'defaultContent': '<button id="btn_group" type="button" class="btn btn-block btn-info btn-xs">권한그룹수정</button>'
                },
                {
                    'targets': 7,
                    'data': null,
                    'defaultContent': '<button id="btn_personal" type="button" class="btn btn-block btn-info btn-xs">개인권한수정</button>'
                }
            ]
        },
        'tb_authorized_group': {
            'columns': [
                {title: '프로젝트명', data: 'projectName'},
                {title: '권한명', data: 'name'},
                {title: '적용일', data: 'regDate'}
            ],
            'columnDefs': [
                {
                    'targets': 3,
                    'data': null,
                    'defaultContent': '<button type="button" class="btn btn-block btn-info btn-xs" data-target="#authority-bundle-detail-modal" data-toggle="modal">상세</button>'
                }
            ]
        },
        'tb_authorized_personal': {
            'columns': [
                {title: '프로젝트명', data: 'projectName'},
                {title: '타입', data: 'type'},
                {title: '메뉴명', data: 'name'},
                {title: '적용일', data: 'regDate'}
            ]
        },
        'tb_authority_group': {
            'columns': [
                {title: 'ID', data: 'authId'},
                {title: '권한명', data: 'name'}
            ],
            'columnDefs': [
                {
                    'targets': 2,
                    'data': null,
                    'defaultContent': '<input name="is-avail" type="checkbox" data-size="mini">'
                }
            ]
        },
        'tb_api_list': {
            'columns': [
                {title: 'API ID', data: 'id'},
                {title: '타입', data: 'type'},
                {title: '메뉴명', data: 'name'},
                {title: 'URL 경로', data: 'fullUrl'},
            ]
        },
        'tb_personal_api_list': {
            'columns': [
                {title: 'API ID', data: 'id'},
                {title: '타입', data: 'type'},
                {title: '메뉴명', data: 'name'},
                {title: 'URL 경로', data: 'fullUrl'}
            ],
            'columnDefs': [
                {
                    'targets': 4,
                    'data': null,
                    'defaultContent': '<input name="is-avail" type="checkbox" data-size="mini">'
                }
            ]
        },
        'menu_tree': {
            'plugins': ['sort', 'types']
        }
    };

    var tb_users = $('#tb-users').DataTable(OPTION.data_table(opt.tb_users));
    var tb_authorized_group = $('#tb-authorized-group').DataTable(OPTION.data_table(opt.tb_authorized_group));
    var tb_authorized_personal = $('#tb-authorized-personal').DataTable(OPTION.data_table(opt.tb_authorized_personal));

    var project_id = extractByWord('project');

    $(document).ready(function() {

        AJAX.getData(
            OsoriRoute.getUri('project.findUsersProject', {id: project_id})
        ).done(function(data){
            _.map(data.content, function(v){
                return _.extend(v, {DT_RowId: v.id});
            });

            tb_users.rows.add(data.content).draw();

            tb_users
            .on('click', 'tr', function(){
                $('#tb-users tr.active').removeClass('active');
                $(this).addClass('active');

                var user = tb_users.row($(this)).data();

                AJAX.getData(
                    OsoriRoute.getUri('user.findGrantsForUser', {userId : user.id, projectId: project_id})
                ).done(function(data){
                    tb_authorized_group.clear().rows.add(data.authorityDefinitions).draw();
                    tb_authorized_personal.clear().rows.add(data.menuNavigations).draw();

                    tb_authorized_group.on('click', 'button', function(){
                        let auth_obj = tb_authorized_group.row($(this).parents('tr')).data();
                        openGroupDetailModal(auth_obj.projectId, auth_obj.id, auth_obj.name);
                    });
                });
            }).on('click', 'button', function(){
                let user_data = tb_users.row($(this).parents('tr')).data();

                if($(this).prop('id') === 'btn_group')
                    modifyGroup(user_data);
                else
                    modifyPersonal(user_data);
            });
        });
    });

    $('#btn-modify-user').click(function() {
        tb_authorized_group.clear().draw();
        tb_authorized_personal.clear().draw();

        $('#modal-modify-user .modal-title').text('프로젝트 할당');
        $('#modal-modify-user .modal-body').empty().append($('#modal-modify-user-content form').clone());

        let tb_group = $('#modal-modify-user #tb-group')
                        .DataTable(OPTION.data_table(opt.tb_modal_group));

        let tb_user = $('#modal-modify-user #tb-user')
                        .DataTable(OPTION.data_table(opt.tb_modal_users))
                        .on('click', 'tr', function(){
                            $('#modal-modify-user #tb-user tr.active').removeClass('active');
                            $(this).addClass('active');

                            let user = $('#modal-modify-user #tb-user').DataTable().row($(this)).data();

                            $.when(
                                AJAX.getData(OsoriRoute.getUri('authority.findAll', {id: project_id})),
                                AJAX.getData(OsoriRoute.getUri('user.findGrantsForUser', {userId: user.id, projectId: project_id}))
                            ).done(function(first, second){
                                let all_group = first[0].content;
                                let own_group = second[0].authorityDefinitions;

                                tb_group.clear().rows.add(all_group).draw();

                                _.each($('#modal-modify-user #tb-group input[name="is-avail"]'), function (v) {
                                    let auth_obj = tb_group.row($(v).parents('tr')).data();

                                    if (_.indexOf(_.pluck(own_group, 'id'), auth_obj.authId) !== -1)
                                        $(v).bootstrapSwitch('state', true);
                                    else
                                        $(v).bootstrapSwitch('state', false);
                                });

                                $('#modal-modify-user #tb-group input[name="is-avail"]').on('switchChange.bootstrapSwitch', function () {
                                    let auth_obj = tb_group.row($(this).parents('tr')).data();

                                    let after_func = function(){
                                        AJAX.getData(
                                            OsoriRoute.getUri('project.findUsersProject', {id: project_id})
                                        ).done(function(data){
                                            $('#tb-users').DataTable().clear().rows.add(data.content).draw();
                                        });
                                    };

                                    if ($(this).bootstrapSwitch('state')) {
                                        assignAuthorityGrant(project_id, user.id, auth_obj.authId, this)
                                        .done(after_func);
                                    }else {
                                        withdrawAuthorityGrant(project_id, user.id, auth_obj.authId, this)
                                        .done(after_func);
                                    }
                                });

                            });

                        });

        $.when(
            AJAX.getData(OsoriRoute.getUri('project.findUsersProject', {id: project_id})),
            AJAX.getData(OsoriRoute.getUri('users.findAll'), {status: 'ALLOW'})
        ).done(function(first, second){
            let user_projects = first[0].content;
            let all_user = second[0].content;

            let filtered_user = _.filter(all_user, function(v){
                return _.indexOf(_.pluck(user_projects, 'id'), v.id) === -1;
            });

            setTimeout(function() {
                tb_user.clear().rows.add(filtered_user).draw();
            }, 300);
        });

        $('#modal-modify-user').modal();
    });

    function modifyGroup(user_data){
        $.when(
            AJAX.getData(OsoriRoute.getUri("authority.findAll", {id: project_id})),
            AJAX.getData(OsoriRoute.getUri('user.findGrantsForUser', {userId: user_data.id, projectId: project_id}))
        ).done(function (first, second) {
            let all_auth = first[0].content;
            let user_auth = second[0].authorityDefinitions;

            $('#modal-modify-group .modal-title').text('사용자 권한 그룹 수정');
            $('#modal-modify-group .modal-body').empty().append($('#modal-modify-group-content form').clone());

            $('#modal-modify-group #tb-group-list').on('click', 'tr', function () {

                let tb_group_list = $('#modal-modify-group #tb-group-list').DataTable();
                let auth_obj = tb_group_list.row($(this)).data();

                $('#modal-modify-group #tb-group-list tr.active').removeClass('active');
                $(this).addClass('active');

                AJAX.getData(OsoriRoute.getUri('authority.findBundlesNavigations', {
                    id: auth_obj.projectId,
                    authId: auth_obj.authId
                })).done(function (data) {
                    $('#modal-modify-group #tb-group-api-list').DataTable(OPTION.data_table(opt.tb_api_list, data.content));
                });
            });

            setTimeout(function() {
                $('#modal-modify-group #tb-group-list').DataTable(OPTION.data_table(opt.tb_authority_group, all_auth));
            },300);

            setTimeout(function(){
                _.each($('#modal-modify-group #tb-group-list input[name="is-avail"]'), function (v) {
                    let tb_group_list = $('#modal-modify-group #tb-group-list').DataTable();
                    let row_data = tb_group_list.row($(v).parents('tr')).data();
                    if (_.indexOf(_.pluck(user_auth, 'id'), row_data.authId) !== -1)
                        $(v).bootstrapSwitch('state', true);
                    else
                        $(v).bootstrapSwitch('state', false);
                });

                $('#modal-modify-group #tb-group-list input[name="is-avail"]').on('switchChange.bootstrapSwitch', function () {
                    let tb_group_list = $('#modal-modify-group #tb-group-list').DataTable();
                    let auth_obj = tb_group_list.row($(this).parents('tr')).data();

                    let after_func = function(){

                        AJAX.getData(
                            OsoriRoute.getUri('user.findGrantsForUser', {projectId: project_id, userId: user_data.id})
                        ).done(function(data){
                            tb_authorized_group.clear().rows.add(data.authorityDefinitions).draw();

                            AJAX.getData(
                                OsoriRoute.getUri('project.findUsersProject', {id: project_id})
                            ).done(function(data) {
                                tb_users.clear().rows.add(data.content).draw();
                            });
                        });
                    };

                    if ($(this).bootstrapSwitch('state')) {
                        assignAuthorityGrant(project_id, user_data.id, auth_obj.authId, this)
                        .done(after_func);
                    }else {
                        withdrawAuthorityGrant(project_id, user_data.id, auth_obj.authId, this)
                        .done(after_func);
                    }
                });
            }, 500);

            $('#modal-modify-group').modal();

        });
    }

    function assignAuthorityGrant(project_id, user_id, auth_id, btn_onoff){
        return AJAX.postData(
            OsoriRoute.getUri('user.assignAuthorityGrant', {projectId: project_id, userId: user_id, authIdGroup: auth_id})
        )
        .fail(function(){
            $(btn_onoff).bootstrapSwitch('state', false);
        });
    }

    function withdrawAuthorityGrant(project_id, user_id, auth_id, btn_onoff){
        return AJAX.deleteData(
            OsoriRoute.getUri('user.withdrawAuthorityGrant', {projectId: project_id, userId: user_id, authIdGroup: auth_id})
        )
        .fail(function(){
            $(btn_onoff).bootstrapSwitch('state', true);
        });
    }

    function assignPersonalGrant(project_id, user_id, navi_id, btn_onoff){
        AJAX.postData(
            OsoriRoute.getUri('user.assignPersonalGrant', {projectId: project_id, userId: user_id, menuNaviIdGroup: navi_id})
        )
        .done(function() {
            AJAX.getData(
                OsoriRoute.getUri('user.findGrantsForUser', {projectId: project_id, userId: user_id})
            ).done(function(data){
                tb_authorized_personal.clear().rows.add(data.menuNavigations).draw();
            });

        })
        .fail(function(){
            $(btn_onoff).bootstrapSwitch('state', false);
        });
    }

    function withdrawPersonalGrant(project_id, user_id, navi_id, btn_onoff){
        AJAX.deleteData(
            OsoriRoute.getUri('user.withdrawPersonalGrant', {projectId: project_id, userId: user_id, menuNaviIdGroup: navi_id})
        )
        .done(function() {
            AJAX.getData(
                OsoriRoute.getUri('user.findGrantsForUser', {projectId: project_id, userId: user_id})
            ).done(function(data){
                tb_authorized_personal.clear().rows.add(data.menuNavigations).draw();
            });
        })
        .fail(function(){
            $(btn_onoff).bootstrapSwitch('state', true);
        });
    }

    function openGroupDetailModal(project_id, auth_id, name){
        $.when(
            AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:project_id})),
            AJAX.getData(OsoriRoute.getUri('authority.findBundlesBranches', {id:project_id, authId:auth_id}))
        ).done(function(first, second){
            let all_branch = first[0].content;
            let bundleBranches = second[0].content;

            setTimeout(function(){
                $('#modal-selected').DataTable(OPTION.data_table(opt.tb_api_list, _.pluck(bundleBranches, 'a_attr')));
            }, 300);

            $('#modal-menu-tree')
            .jstree('destroy')
            .jstree(OPTION.jstree(opt.menu_tree, all_branch))
            .on('loaded.jstree', function () {
                $(this).jstree("open_all");
            });

            $('#modal-group-detail .modal-title').text(name);
            $('#modal-group-detail .modal-body').append($('#modal-group-detail-content form'));

            $('#modal-group-detail').modal();

        });
    }

    function modifyPersonal(user_data){
        $.when(
            AJAX.getData(OsoriRoute.getUri('project.findNavigationsProject', {id:project_id})),
            AJAX.getData(OsoriRoute.getUri('user.findGrantsForUser', {userId : user_data.id, projectId: project_id}))
        ).done(function(first, second){
            let api_list = first[0].content;
            let user_api = second[0].menuNavigations;

            $('#modal-modify-personal .modal-title').text('개인권한 수정');
            $('#modal-modify-personal .modal-body').empty().append($('#modal-modify-personal-content form').clone());

            $('#modal-modify-personal').modal();

            setTimeout(function () {
                $('#modal-modify-personal #tb-api-list').DataTable(OPTION.data_table(opt.tb_personal_api_list, api_list));
            },200);

            setTimeout(function () {
                _.each($('#modal-modify-personal #tb-api-list input[name="is-avail"]'), function (v) {
                    let tb_api_list = $('#modal-modify-personal #tb-api-list').DataTable();
                    let row_data = tb_api_list.row($(v).parents('tr')).data();

                    if (_.indexOf(_.pluck(user_api, 'id'), row_data.id) !== -1)
                        $(v).bootstrapSwitch('state', true);
                    else
                        $(v).bootstrapSwitch('state', false);
                });

                $('#modal-modify-personal #tb-api-list input[name="is-avail"]').on('switchChange.bootstrapSwitch', function () {
                    let tb_api_list = $('#modal-modify-personal #tb-api-list').DataTable();
                    let navi_obj = tb_api_list.row($(this).parents('tr')).data();

                    if ($(this).bootstrapSwitch('state'))
                        assignPersonalGrant(project_id, user_data.id, navi_obj.id, this);
                    else
                        withdrawPersonalGrant(project_id, user_data.id, navi_obj.id, this);

                });
            }, 500);

        });

    }

</script>
</body>
</html>
