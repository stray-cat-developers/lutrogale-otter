<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
	<@layout.baseHeader "New Project"/>
	<body class="hold-transition skin-blue sidebar-mini">
		<@layout.baseWrapper>
			<#include "content-header.ftl">
			<section class="content">
				<div class="row">
                    <div class="col-md-12">
                        <#include "navi-wizard.ftl">
                    </div>
					<div class="col-lg-8 col-xs-12">
                        <div class="box box-solid">
                            <div class="box-header with-border">
                                <h3 class="box-title">권한 그룹 생성</h3>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body">
                                <form role="form">
                                    <div class="form-group">
                                        <label for="group-name">권한 그룹 명</label>
                                        <input id="group-name" type="text" class="form-control" placeholder="권한들을 대표하는 이름을 입력하세요. ex) 일반 관리자, 고객센터 기본 권한 그룹 등등.. ">
                                    </div>
                                    <hr style="border: none; border-bottom: 1px solid dimgrey;">
                                    <div class="col-lg-4 col-xs-12">
                                        <div class="box box-solid">
                                            <div class="box-header">
                                                <h3 class="box-title">네비게이션 트리에서 선택</h3>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body">
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
                            <!-- /.box-body -->
                            <div class="box-footer">
                                <button id="btn_create" class="btn btn-primary">생성</button>
                            </div>
                        </div>
					</div>
                    <div class="col-lg-4 col-xs-12">
                        <div class="box box-solid">
                            <div class="box-header with-border">
                                <h3 class="box-title">등록된 권한 그룹</h3>
                            </div>
                            <div class="box-body">
                                <table id="table-groups" class="table table-bordered table-striped"></table>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12">
                        <button onclick="OsoriRoute.go('view.newProject.complete',{id:SS.project_id})" class="btn btn-info pull-right">다음</button>
                    </div>
				</div>

			</section>
		</@layout.baseWrapper>
        <!-- Jstree https://github.com/orangehill/jstree-bootstrap-theme -->
        <script src="/static/plugins/jstree/jstree.min.js"></script>
        <!-- Datatables -->
        <script src="/static/plugins/datatables/jquery.dataTables.js"></script>
        <script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>


        <script>
            $(document).ready(function() {
                SS.setItem("project_id", extractByWord('project'));

                let opt = {
                    'table_selected': {
                        'columns': [
                            {title: 'API ID', data: 'id'},
                            {title: '타입', data: 'type'},
                            {title: '메뉴명', data: 'name'},
                            {title: 'URL 경로', data: 'uriBlock'}
                        ]
                    },
                    'table_groups': {
                        'columns': [
                            {title: '권한그룹명', data: 'name'}
                        ],
                        'columnDefs': [{
                            'targets': 1,
                            'data': null,
                            'defaultContent': "<button type='button' class='btn btn-block btn-danger btn-xs'>삭제</button>"
                        }]
                    }
                };

                $('#table-selected').DataTable(OPTION.data_table(opt.table_selected));
                $('#table-groups').DataTable(OPTION.data_table(opt.table_groups)).on('click', 'button', function(){
                    let table = $('#table-groups').DataTable();
                    let data = table.row($(this).parents('tr')).data();

                    if(!confirm('['+data.name+'] 권한그룹을 삭제하시겠습니까?'))
                        return false;

                    AJAX.deleteData(
                        OsoriRoute.getUri('authority.expire', {id:SS.project_id, authId:data.authId})
                    ).done(function(){
                        getAuthGroup();
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

                    let tree_opt = {
                        'plugins': ['sort', 'types', 'checkbox'],
                        'checkbox' : {
                            three_state : false,
                            whole_node : false,
                            tie_selection : false
                        }
                    };

                    $('#menuNaviTree').jstree(OPTION.jstree(tree_opt, navigation_list)).on('check_node.jstree uncheck_node.jstree', function (event, data) {
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
                    });

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
                    $('#menuNaviTree').jstree(true).uncheck_all();
                    $('#table-selected').DataTable().clear().draw();
                    $('#table-groups').DataTable().clear().draw();
                    $('#group-name').val('');

                    getAuthGroup();
                });

            });

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
