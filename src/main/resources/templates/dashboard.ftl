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

        <!-- 자동 Sync 추가 모달 -->
        <div class="modal fade" id="syncAddModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                        <h4 class="modal-title">자동 Sync 추가</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Spec 타입</label>
                            <select id="sync-add-spec-type" class="form-control">
                                <option value="OPENAPI_JSON">OpenAPI (JSON)</option>
                                <option value="OPENAPI_YAML">OpenAPI (YAML)</option>
                                <option value="GRAPHQL">GraphQL</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Spec URL</label>
                            <input type="text" id="sync-add-url" class="form-control" placeholder="https://example.com/openapi.json">
                        </div>
                        <div class="form-group">
                            <label>테스트 결과</label>
                            <pre id="sync-add-preview-result" class="pre-scrollable" style="min-height:80px; background:#f4f4f4; padding:8px; border-radius:4px; font-size:12px; white-space:pre-wrap;"></pre>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
                        <button type="button" class="btn btn-info" id="sync-add-test-btn">테스트</button>
                        <button type="button" class="btn btn-primary" id="sync-add-save-btn">저장하기</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 자동 Sync 조회/수정 모달 -->
        <div class="modal fade" id="syncEditModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                        <h4 class="modal-title">자동 Sync 설정</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Spec 타입</label>
                            <select id="sync-edit-spec-type" class="form-control">
                                <option value="OPENAPI_JSON">OpenAPI (JSON)</option>
                                <option value="OPENAPI_YAML">OpenAPI (YAML)</option>
                                <option value="GRAPHQL">GraphQL</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Spec URL</label>
                            <input type="text" id="sync-edit-url" class="form-control" placeholder="https://example.com/openapi.json">
                        </div>
                        <div class="form-group">
                            <label>테스트 결과</label>
                            <pre id="sync-edit-preview-result" class="pre-scrollable" style="min-height:80px; background:#f4f4f4; padding:8px; border-radius:4px; font-size:12px; white-space:pre-wrap;"></pre>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-danger pull-left" id="sync-edit-delete-btn">Sync 삭제</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
                        <button type="button" class="btn btn-info" id="sync-edit-test-btn">테스트</button>
                        <button type="button" class="btn btn-primary" id="sync-edit-save-btn">저장하기</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="/static/plugins/datatables/jquery.dataTables.js"></script>
        <script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
        <script>
            var currentSyncProjectId = null;

            function buildPreviewPayload(specType, url) {
                if (specType === 'GRAPHQL') {
                    return {
                        endpoint: OsoriRoute.getUri('migration.graphql.preview'),
                        data: { url: url, httpOperation: 'ONLY_POST' }
                    };
                } else {
                    return {
                        endpoint: OsoriRoute.getUri('migration.openapi.preview'),
                        data: {
                            url: url,
                            version: '',
                            format: specType === 'OPENAPI_YAML' ? 'YAML' : 'JSON',
                            migrationType: 'FLAT'
                        }
                    };
                }
            }

            function runPreview(specType, url, resultElementId) {
                var payload = buildPreviewPayload(specType, url);
                var $result = $('#' + resultElementId);
                $result.text('테스트 중...');
                AJAX.postData(payload.endpoint, payload.data)
                    .done(function(res) {
                        $result.text(res.content || '(응답 없음)');
                    })
                    .fail(function(xhr) {
                        $result.text('오류: ' + (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : xhr.status));
                    });
            }

            // 자동 Sync 추가 모달
            $(document).on('click', '.btn-sync-add', function() {
                currentSyncProjectId = $(this).data('project-id');
                $('#sync-add-spec-type').val('OPENAPI_JSON');
                $('#sync-add-url').val('');
                $('#sync-add-preview-result').text('');
                $('#syncAddModal').modal('show');
            });

            $('#sync-add-test-btn').on('click', function() {
                var specType = $('#sync-add-spec-type').val();
                var url = $('#sync-add-url').val().trim();
                if (!url) { alert('URL을 입력하세요.'); return; }
                runPreview(specType, url, 'sync-add-preview-result');
            });

            $('#sync-add-save-btn').on('click', function() {
                var specType = $('#sync-add-spec-type').val();
                var url = $('#sync-add-url').val().trim();
                if (!url) { alert('URL을 입력하세요.'); return; }
                var apiUrl = OsoriRoute.getUri('project.sync', { id: currentSyncProjectId });
                AJAX.postData(apiUrl, { specType: specType, url: url })
                    .done(function() {
                        $('#syncAddModal').modal('hide');
                        $('#dataTables-projects').DataTable().ajax.reload();
                    })
                    .fail(function(xhr) {
                        alert('저장 실패: ' + (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : xhr.status));
                    });
            });

            // 자동 Sync 조회/수정 모달
            $(document).on('click', '.btn-sync-edit', function() {
                currentSyncProjectId = $(this).data('project-id');
                var specType = $(this).data('spec-type');
                var migrationUrl = $(this).data('migration-url');
                $('#sync-edit-spec-type').val(specType);
                $('#sync-edit-url').val(migrationUrl);
                $('#sync-edit-preview-result').text('');
                $('#syncEditModal').modal('show');
            });

            $('#sync-edit-test-btn').on('click', function() {
                var specType = $('#sync-edit-spec-type').val();
                var url = $('#sync-edit-url').val().trim();
                if (!url) { alert('URL을 입력하세요.'); return; }
                runPreview(specType, url, 'sync-edit-preview-result');
            });

            $('#sync-edit-save-btn').on('click', function() {
                var specType = $('#sync-edit-spec-type').val();
                var url = $('#sync-edit-url').val().trim();
                if (!url) { alert('URL을 입력하세요.'); return; }
                var apiUrl = OsoriRoute.getUri('project.sync', { id: currentSyncProjectId });
                AJAX.putData(apiUrl, { specType: specType, url: url })
                    .done(function() {
                        $('#syncEditModal').modal('hide');
                        $('#dataTables-projects').DataTable().ajax.reload();
                    })
                    .fail(function(xhr) {
                        alert('저장 실패: ' + (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : xhr.status));
                    });
            });

            $('#sync-edit-delete-btn').on('click', function() {
                if (!confirm('자동 Sync 설정을 삭제하시겠습니까?')) return;
                var apiUrl = OsoriRoute.getUri('project.sync', { id: currentSyncProjectId });
                AJAX.deleteData(apiUrl)
                    .done(function() {
                        $('#syncEditModal').modal('hide');
                        $('#dataTables-projects').DataTable().ajax.reload();
                    })
                    .fail(function(xhr) {
                        alert('삭제 실패: ' + (xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : xhr.status));
                    });
            });

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
                    {"title": "생성일", "data": "created"},
                    {
                        "title": "자동 Sync",
                        "data": null,
                        "orderable": false,
                        "render": function(data, type, row) {
                            if (row.syncEnabled) {
                                return '<button class="btn btn-xs btn-success btn-sync-edit"' +
                                    ' data-project-id="' + row.id + '"' +
                                    ' data-spec-type="' + row.specType + '"' +
                                    ' data-migration-url="' + _.escape(row.migrationUrl) + '">' +
                                    '자동 Sync</button>';
                            } else {
                                return '<button class="btn btn-xs btn-primary btn-sync-add"' +
                                    ' data-project-id="' + row.id + '">' +
                                    '자동Sync 추가</button>';
                            }
                        }
                    }
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
