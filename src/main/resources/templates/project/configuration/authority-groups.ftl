<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
    <@layout.baseHeader "권한 그룹 설정"/>
    <body class="hold-transition skin-blue sidebar-mini">
        <@layout.baseWrapper>
        <section class="content-header">
            <h1>권한 그룹 설정</h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-lg-12 col-xs-12">
                    <div class="box box-solid">
                        <div class="box-header with-border" data-toggle="collapse" href="#collapse-create-group">
                            <h3 class="box-title">권한 그룹 생성</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="collapse" id="collapse-create-group">
                            <div class="box-body">
                                <form role="form">
                                    <div class="form-group">
                                        <label for="group-name">권한 그룹 명</label>
                                        <input id="group-name" type="text" class="form-control" placeholder="권한들을 대표하는 이름을 입력하세요. ex) 일반 관리자, 고객센터 기본 권한 그룹 등등.. ">
                                    </div>
                                    <hr class="separator-line">
                                    <div class="col-lg-4 col-xs-12">
                                        <div class="box box-solid">
                                            <div class="box-header">
                                                <h3 class="box-title">
                                                    네비게이션 트리에서 선택
                                                    <i class="fa fa-question-circle text-muted" 
                                                       data-toggle="tooltip" 
                                                       data-placement="top" 
                                                       title="키보드: 화살표로 이동, 'v'키로 체크박스 토글"></i>
                                                </h3>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body tree-container">
                                                <div id="menuNaviTree"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-lg-8 col-xs-12">
                                        <div class="box box-solid">
                                            <div class="box-header">
                                                <h3 class="box-title">선택된 API 리스트</h3>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body">
                                                <table id="table-selected" class="table table-bordered table-striped"></table>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <div class="box-footer">
                                <button id="btn_create" class="btn btn-primary">생성</button>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-12 col-xs-12">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">등록된 권한 그룹</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <form role="form">
                                <div class="col-lg-4 col-xs-12">
                                    <div class="box box-solid">
                                        <table id="table-groups" class="table table-bordered table-striped"></table>
                                    </div>
                                </div>
                                <div class="col-lg-8 col-xs-12">
                                    <div class="box box-solid">
                                        <div class="box-body">
                                            <table id="table-group-api" class="table table-bordered table-striped"></table>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <!-- /.box-body -->
                    </div>
                </div>
            </div>
        </section>
        
        <div id="modal-content" class="hidden">
            <form role="form">
                <div class="col-lg-5 col-xs-12">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h4 class="box-title">
                                네비게이션 트리에서 선택
                                <i class="fa fa-question-circle text-muted" 
                                   data-toggle="tooltip" 
                                   data-placement="top" 
                                   title="키보드: 화살표로 이동, 'v'키로 체크박스 토글"></i>
                            </h4>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body tree-container">
                            <div id="modal-menu-tree"></div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-7 col-xs-12">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h4 class="box-title">선택된 API 리스트</h4>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="modal-selected" class="table table-bordered table-striped"></table>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        </@layout.baseWrapper>
        <@layout.plainModal "" "modal-fullsize" "modify-modal" ""/>
        <!-- Jstree https://github.com/orangehill/jstree-bootstrap-theme -->
        <script src="/static/plugins/jstree/jstree.min.js"></script>
        <!-- Datatables -->
        <script src="/static/plugins/datatables/jquery.dataTables.js"></script>
        <script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
        <script>
            SS.setItem("project_id", extractByWord('project'));

            const opt = {
                'table_selected': {
                    'scrollY': '',
                    'scrollCollapse': false,
                    'columns': [
                        {title: 'API ID', data: 'id'},
                        {title: '타입', data: 'type'},
                        {title: '메뉴명', data: 'name'},
                        {title: 'URL 경로', data: 'fullUrl'}
                    ]
                },
                'table_groups': {
                    'columns': [
                        {title: '권한그룹명', data: 'name'}
                    ],
                    'columnDefs': [{
                        'targets': 1,
                        'data': null,
                        'defaultContent': '<button id="btn_modify" type="button" class="btn btn-block btn-warning btn-xs">수정</button>'
                    },
                    {
                        'targets': 2,
                        'data': null,
                        'defaultContent': '<button id="btn_delete" type="button" class="btn btn-block btn-purple btn-xs">삭제</button>'
                    }]
                },
                'menu_tree': {
                    'plugins': ['sort', 'types', 'checkbox'],
                    'checkbox' : {
                        'three_state' : false,
                        'whole_node' : false,
                        'tie_selection' : false
                    }
                }
            };

            $(document).ready(function() {
                // 툴팁 초기화
                $('[data-toggle="tooltip"]').tooltip();
                
                $('#table-selected').DataTable(OPTION.data_table(opt.table_selected));
                $('#table-group-api').DataTable(OPTION.data_table(opt.table_selected));
                $('#table-groups').DataTable(OPTION.data_table(opt.table_groups)).on('click', 'button', function(){
                    let table_groups = $('#table-groups').DataTable();
                    let auth_obj = table_groups.row($(this).parents('tr')).data();

                    let btn_type = $(this).prop('id');
                    if(btn_type === 'btn_modify'){
                        openModifyModal(auth_obj);
                    }else{
                        let data = table_groups.row($(this).parents('tr')).data();

                        if(!confirm('['+data.name+'] 권한그룹을 삭제하시겠습니까?'))
                            return false;

                        AJAX.deleteData(
                            OsoriRoute.getUri('authority.expire', {id:SS.project_id, authId:data.authId})
                        ).done(function(){
                            $('#table-group-api').DataTable().clear().draw();
                            getAuthGroup();
                        });
                    }
                }).on('click', 'tr', function(){
                    let table_groups = $('#table-groups').DataTable();
                    let table_group_api = $('#table-group-api').DataTable();
                    let data = table_groups.row($(this)).data();

                    table_groups.$('tr.active').removeClass('active');
                    $(this).addClass('active');

                    AJAX.getData(OsoriRoute.getUri('authority.findBundlesNavigations', {id:data.projectId, authId:data.authId}))
                        .done(function(data){
                            table_group_api.clear().rows.add(data.content).draw();
                        });
                });

                $.when(
                    AJAX.getData(OsoriRoute.getUri('project.findOne', {id:SS.project_id})),
                    AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:SS.project_id})),
                    getAuthGroup()
                ).done(function(first, second){
                    let project_obj = first[0];
                    let navigation_list = second[0].content;

                    $('#project_name').text(project_obj.name);
                    $('#project_desc').html(project_obj.description);
                    $('#project_apiKey').text(project_obj.apiKey);

                    const $menuNaviTree = $('#menuNaviTree');
                    $menuNaviTree.jstree(OPTION.jstree(opt.menu_tree, navigation_list)).on('check_node.jstree uncheck_node.jstree', function (event, data) {
                        let data_table = $('#table-selected').DataTable();
                        let node = data.node.a_attr;

                        if(data.node.state.checked){
                            node.DT_RowId = node.id;
                            data_table.row.add(node);
                        }else{
                            let target_row = $('#table-selected > tbody tr[id="'+node.id+'"]');
                            data_table.row($(target_row[0])).remove();
                        }

                        data_table.draw();

                    }).on('loaded.jstree', function () {
                        $(this).jstree("open_all");
                    }).on('select_node.jstree', function(e, data) {
                        // 노드 선택 시 포커스를 jstree 컨테이너에 설정하여 키보드 이벤트를 받을 수 있도록 함
                        $(this).focus();
                    });

                    // menuNaviTree에 키보드 이벤트 리스너 추가
                    $menuNaviTree.on('keydown', function(e) {
                        // 'v' 키 (keyCode: 86) 또는 'V' 키를 눌렀을 때
                        if (e.keyCode === 86 || e.key === 'v' || e.key === 'V') {
                            e.preventDefault();
                            
                            let tree = $(this).jstree(true);
                            let target_node_id = null;
                            
                            // 현재 hovered/focused 노드를 찾기
                            let hovered_node = $(this).find('.jstree-hovered').first();
                            if (hovered_node.length > 0) {
                                target_node_id = hovered_node.closest('.jstree-node').attr('id');
                            } else {
                                // hovered 노드가 없으면 선택된 노드 사용
                                let selected_nodes = tree.get_selected();
                                if (selected_nodes.length > 0) {
                                    target_node_id = selected_nodes[0];
                                }
                            }
                            
                            if (target_node_id) {
                                // 현재 타겟 노드의 체크박스 상태를 토글
                                if (tree.is_checked(target_node_id)) {
                                    tree.uncheck_node(target_node_id);
                                } else {
                                    tree.check_node(target_node_id);
                                }
                            }
                        }
                    }).attr('tabindex', '0'); // tabindex를 설정하여 키보드 포커스를 받을 수 있도록 함

                });
            });

            $('#btn_create').click(function(){
                let data_table = $('#table-selected').DataTable();
                let group_name = $('#group-name').val();

                if(group_name === ""){
                    alert('그룹명을 입력해주세요.');
                    return false;
                }

                if(data_table.data().length < 1){
                    alert('최소 한개이상의 메뉴를 선택해주세요.');
                    return false;
                }

                let param = {
                    groupName : group_name,
                    naviId : _.pluck(data_table.data(), 'id')
                };

                AJAX.postData(
                    OsoriRoute.getUri('authority.create', {id:SS.project_id}),
                    param
                ).done(function(){
                    const $menuTree = $('#menuNaviTree');
                    $menuTree.jstree(true).uncheck_all();
                    $('#table-selected').DataTable().clear().draw();
                    $('#table-groups').DataTable().clear().draw();
                    $('#group-name').val('');

                    getAuthGroup();
                });

            });

            function openModifyModal(auth_obj){
                $.when(
                    AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:auth_obj.projectId})),
                    AJAX.getData(OsoriRoute.getUri('authority.findBundlesBranches', {id:auth_obj.projectId, authId:auth_obj.authId}))
                ).done(function(first, second){
                    let all_branch = first[0].content;
                    let bundleBranches = second[0].content;

                    _.map(all_branch, function(v){
                        if(!_.isUndefined(_.findWhere(bundleBranches, {id:v.id})))
                            return _.extend(v, {'state':{'checked':true}});
                    });

                    _.map(bundleBranches, function(v){
                       return _.extend(v.a_attr, {'DT_RowId': v.a_attr.id});
                    });

                    $('#modify-modal .modal-title').text(auth_obj.name);
                    $('#modify-modal .modal-body').empty().append($('#modal-content form').clone());

                    setTimeout(function(){
                        $('#modify-modal #modal-selected').DataTable(OPTION.data_table(opt.table_selected, _.pluck(bundleBranches, 'a_attr')));
                    }, 300);

                    $('#modify-modal #modal-menu-tree')
                    .jstree('destroy')
                    .jstree(OPTION.jstree(opt.menu_tree, all_branch))
                    .on('check_node.jstree uncheck_node.jstree', function (event, data) {
                        let data_table = $('#modify-modal #modal-selected').DataTable();
                        let node = data.node.a_attr;

                        if(data.node.state.checked){
                            AJAX.putData(
                                OsoriRoute.getUri('authority.modifyInfo', {id:Number(auth_obj.projectId), authId:Number(auth_obj.authId)}),
                                {'naviId': node.id},
                                {async: false}
                            ).done(function(){
                                node.DT_RowId = node.id;
                                data_table.row.add(node);
                            });

                        }else{
                            const param = {
                                'id': auth_obj.projectId,
                                'authId': auth_obj.authId,
                                'menuNaviIdGroup': [node.id]
                            };

                            AJAX.deleteData(
                                OsoriRoute.getUri('authority.expireNavigations', param),
                                {},
                                {async: false}
                            ).done(function(){
                                let target_row = $('#modify-modal #modal-selected > tbody tr[id="'+node.id+'"]');
                                data_table.row($(target_row[0])).remove();
                            });

                        }

                        data_table.draw();
                    }).on('loaded.jstree', function () {
                        $(this).jstree("open_all");
                    }).on('select_node.jstree', function(e, data) {
                        $(this).focus();
                    });

                    $('#modify-modal #modal-menu-tree').on('keydown', function(e) {
                        if (e.keyCode === 86 || e.key === 'v' || e.key === 'V') {
                            e.preventDefault();

                            let tree = $(this).jstree(true);
                            let target_node_id = null;

                            let hovered_node = $(this).find('.jstree-hovered').first();
                            if (hovered_node.length > 0) {
                                target_node_id = hovered_node.closest('.jstree-node').attr('id');
                            } else {
                                let selected_nodes = tree.get_selected();
                                if (selected_nodes.length > 0) {
                                    target_node_id = selected_nodes[0];
                                }
                            }

                            if (target_node_id) {
                                if (tree.is_checked(target_node_id)) {
                                    tree.uncheck_node(target_node_id);
                                } else {
                                    tree.check_node(target_node_id);
                                }
                            }
                        }
                    }).attr('tabindex', '0');

                    let modal_tree = $('#modify-modal #modal-menu-tree').jstree(true);
                    $.each(_.pluck(bundleBranches, 'id'), function(i,v){
                        modal_tree.check_node(v);
                    });

                    // 모달 내 툴팁 초기화
                    setTimeout(function() {
                        $('[data-toggle="tooltip"]').tooltip();
                    }, 100);

                    $('#modify-modal').modal();

                });
            }

            function getAuthGroup(){
                return AJAX.getData(
                    OsoriRoute.getUri('authority.findAll', {id:SS.project_id})
                ).done(function(data){
                    let group_table = $('#table-groups').DataTable();
                    group_table.clear().rows.add(data.content).draw();
                });
            }

        </script>

    </body>
</html>
