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
                <div class="col-xs-12">
					<#include "project-info-box.ftl" />

                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">메뉴 정보</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <form role="form">
                                <div class="col-lg-4 col-xs-12">
                                    <div class="box box-solid">
                                        <div class="box-header">
                                            <h3 class="box-title">네비게이션 트리</h3>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body">
                                            <div id="tree-navigation"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-lg-8 col-xs-12">
                                    <div class="box box-solid">
                                        <div class="box-header">
                                            <h3 class="box-title">API 리스트</h3>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body">
                                            <table id="table-total-api" class="table table-bordered table-striped"></table>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <!-- /.box-body -->
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <div class="box box-solid">
                        <div class="box-header">
                            <h3 class="box-title">권한그룹 정보</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <form role="form">
                                <div class="col-lg-4 col-xs-12">
                                    <div class="box box-solid">
                                        <div class="box-body">
                                            <table id="table-groups" class="table table-bordered table-striped"></table>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-lg-8 col-xs-12">
                                    <div class="box box-solid">
                                        <div class="box-header">
                                            <h3 class="box-title">API 리스트</h3>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body">
                                            <table id="table-group-api" class="table table-bordered table-striped"></table>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->
                    <div class="col-xs-12">
                        <button onclick="OsoriRoute.go('dashBoard')" class="btn btn-info pull-right">완료</button>
                    </div>
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

                $.when(
                    AJAX.getData(OsoriRoute.getUri('project.findOne', {id:SS.project_id})),
                    AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:SS.project_id})),
                    AJAX.getData(OsoriRoute.getUri('authority.findAll', {id:SS.project_id}))
                ).done(function(first, n, g){
                    var project_obj = first[0];
                    var navigation_list = n[0].result;
                    var group_list = g[0].result;

                    var opt = {
                        'tree_navigation': {
                            'plugins': ['sort', 'types']
                        },
                        'table_total_api': {
                            'columns': [
                                {title: 'API ID', data: 'id'},
                                {title: '타입', data: 'type'},
                                {title: '메뉴명', data: 'name'},
                                {title: 'URL 경로', data: 'fullUrl'}
                            ],
                            'data': _.pluck(navigation_list, 'a_attr')
                        },
                        'table_groups': {
                            'data': group_list,
                            'columns': [
                                {title: '권한그룹명', data: 'name'}
                            ]
                        },
                        'table_group_api': {
                            'columns': [
                                {title: 'API ID', data: 'id'},
                                {title: '타입', data: 'type'},
                                {title: '메뉴명', data: 'name'},
                                {title: 'URL 경로', data: 'fullUrl'}
                            ]
                        }
                    }

                    $('#tree-navigation').jstree(
                        OPTION.jstree(opt.tree_navigation, navigation_list)
                    ).on('loaded.jstree', function () {
                        $(this).jstree("open_all");
                    });

                    $('#table-total-api').DataTable(OPTION.data_table(opt.table_total_api));
                    $('#table-group-api').DataTable(OPTION.data_table(opt.table_group_api));
                    $('#table-groups').DataTable(OPTION.data_table(opt.table_groups)).on('click', 'tr', function(){
                        var table_groups = $('#table-groups').DataTable();
                        var table_group_api = $('#table-group-api').DataTable();
                        var data = table_groups.row($(this)).data();

                        table_groups.$('tr.active').removeClass('active');
                        $(this).addClass('active');

                        AJAX.getData(OsoriRoute.getUri('authority.findBundlesNavigations', {id:data.projectId, authId:data.authId})).done(function(obj){
                            table_group_api.clear().rows.add(obj.result).draw();
                        });
                    });

                    $('#project_name').text(project_obj.name);
                    $('#project_desc').html(project_obj.description);
                    $('#project_apiKey').text(project_obj.apiKey);

                });
            });

        </script>
	</body>
</html>
