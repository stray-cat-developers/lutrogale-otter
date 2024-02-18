<#import "mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
	<@layout.baseHeader "Dashboard">
		<link rel="stylesheet" href="/static/plugins/datatables/extensions/Select/select.dataTables.min.css">
	</@layout.baseHeader>

	<body class="hold-transition skin-blue sidebar-mini">
		<@layout.baseWrapper>
			<section class="content-header">
				<h1>Dashboard</h1>
                <button type="button" class="btn btn-primary pull-right" onclick="OsoriRoute.go('view.management.newMember',{});">신규 사용자 생성</button>
                <button type="button" class="btn btn-primary pull-right" style="margin-right: 5px;" onclick="OsoriRoute.go('view.newProject',{});">신규 프로젝트 생성</button>
			</section>
			<section class="content">
                <div class="row">
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-aqua"><i class="ion ion-ios-people-outline"></i></span>

                            <div class="info-box-content">
                                <span class="info-box-text">전체 사용자</span>
                                <span id="total_count" class="info-box-number"></span>
                            </div>
                            <!-- /.info-box-content -->
                        </div>
                        <!-- /.info-box -->
                    </div>
                    <div class="col-md-3 col-sm-6 col-xs-12">
                        <div class="info-box">
                            <span class="info-box-icon bg-yellow"><i class="ion ion-person-stalker"></i></span>

                            <div class="info-box-content">
                                <span class="info-box-text">권한 세팅 대기 사용자</span>
                                <span id="wait_count" class="info-box-number"></span>
                            </div>
                            <!-- /.info-box-content -->
                        </div>
                        <!-- /.info-box -->
                    </div>
                </div>
                <div class="row">
					<div class="col-xs-12">
                        <div class="box box-solid">
                            <div class="box-header">
                                <h3 class="box-title">프로젝트 리스트</h3>
                            </div>
                            <!-- /.box-header -->
                            <div class="box-body">
                                <table id="dataTables-projects" class="table table-striped"></table>
                            </div>
                            <!-- /.box-body -->
                        </div>
					</div>
                </div>
			</section>
		</@layout.baseWrapper>
        <script src="/static/plugins/datatables/jquery.dataTables.js"></script>
        <script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
        <script>
            var opt = {
                "ajax": {
                    "url": OsoriRoute.getUri("project.findAll"),
                    "dataSrc": function ( json ) {
                        return json.content;
                    }
                },
                "columns": [
                    {"title": "ID", "data": "id"},
                    {"title": "프로젝트명", "data": "name"},
                    {"title": "설명", "data": "description"},
                    {"title": "API KEY", "data": "apiKey"},
                    {"title": "생성일", "data": "created"}
                ]
            };

            $(document).ready(function() {

                $.when(
                    AJAX.getData(OsoriRoute.getUri('users.findAll')),
                    AJAX.getData(OsoriRoute.getUri('users.findAll'), {status: 'WAIT'})
                ).done(function(all_user, wait_user){
                    all_user = all_user[0].content;
                    wait_user = wait_user[0].content;

                    $('#total_count').text(_.isUndefined(all_user)?0:all_user.length);
                    $('#wait_count').text(_.isUndefined(wait_user)?0:wait_user.length);
                });

            	$('#dataTables-projects').DataTable(OPTION.data_table(opt));
            });
        </script>
	</body>

</html>
