<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
	<@layout.baseHeader "New Project">
		<style>
			.tree-container {
				height: 60vh;
				overflow-y: auto;
			}
			.form-separator {
				border: none;
				border-bottom: 1px solid #ddd;
				margin: 20px 0;
			}
		</style>
	</@layout.baseHeader>
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
                                    <hr class="form-separator">
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
                                                <div id="menuNaviTree" tabindex="0"></div>
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
            // 전역 변수
            let projectId;

            // 페이지 옵션 설정
            const pageOptions = {
                selectedTable: {
                    columns: [
                        {title: 'API ID', data: 'id'},
                        {title: '타입', data: 'type'},
                        {title: '메뉴명', data: 'name'},
                        {title: 'URL 경로', data: 'uriBlock'}
                    ]
                },
                groupsTable: {
                    columns: [
                        {title: '권한그룹명', data: 'name'}
                    ],
                    columnDefs: [{
                        targets: 1,
                        data: null,
                        defaultContent: '<button type="button" class="btn btn-danger btn-xs delete-group-btn"><i class="fa fa-trash"></i> 삭제</button>'
                    }]
                },
                navigationTree: {
                    plugins: ['sort', 'types', 'checkbox'],
                    checkbox: {
                        three_state: false,
                        whole_node: false,
                        tie_selection: false
                    }
                }
            };

            $(document).ready(function() {
                initializePage();
                initializeTooltips();
            });

            // 페이지 초기화
            function initializePage() {
                projectId = extractByWord('project');
                SS.setItem("project_id", projectId);
                
                initializeTables();
                loadPageData();
                bindEvents();
            }

            // 테이블 초기화
            function initializeTables() {

                $('#table-selected').DataTable(OPTION.data_table(pageOptions.selectedTable));
                $('#table-groups').DataTable(OPTION.data_table(pageOptions.groupsTable))
                    .on('click', '.delete-group-btn', handleDeleteGroup);
            }

            // 그룹 삭제 처리
            function handleDeleteGroup() {
                const table = $('#table-groups').DataTable();
                const data = table.row($(this).parents('tr')).data();

                if (!confirm('[' + data.name + '] 권한그룹을 삭제하시겠습니까?')) {
                    return;
                }

                AJAX.deleteData(
                    OsoriRoute.getUri('authority.expire', {id: projectId, authId: data.authId})
                ).done(function() {
                    loadAuthorityGroups();
                });
            }

            // 페이지 데이터 로드
            function loadPageData() {
                $.when(
                    AJAX.getData(OsoriRoute.getUri('project.findOne', {id: projectId})),
                    AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id: projectId})),
                    loadAuthorityGroups()
                ).done(function(projectResult, navigationResult) {
                    const projectData = projectResult[0];
                    const navigationData = navigationResult[0].content;

                    updateProjectInfo(projectData);
                    initializeNavigationTree(navigationData);
                });
            }

            // 프로젝트 정보 업데이트
            function updateProjectInfo(projectData) {
                $('#project_name').text(projectData.name);
                $('#project_desc').html(projectData.description);
                $('#project_apiKey').text(projectData.apiKey);
            }

            // 네비게이션 트리 초기화
            function initializeNavigationTree(navigationData) {
                $('#menuNaviTree')
                    .jstree(OPTION.jstree(pageOptions.navigationTree, navigationData))
                    .on('check_node.jstree uncheck_node.jstree', handleTreeNodeCheck)
                    .on('loaded.jstree', function() {
                        $(this).jstree("open_all");
                    })
                    .on('select_node.jstree', function() {
                        $(this).focus();
                    });

                addKeyboardSupport('#menuNaviTree');
            }

            // 트리 노드 체크 처리
            function handleTreeNodeCheck(event, data) {
                const dataTable = $('#table-selected').DataTable();
                const node = data.node.a_attr;

                if (data.node.state.checked) {
                    node.DT_RowId = node.id;
                    dataTable.row.add(node);
                } else {
                    const targetRow = $('#table-selected > tbody tr[id="' + node.id + '"]');
                    dataTable.row($(targetRow[0])).remove();
                }

                dataTable.draw();
            }

            // 키보드 접근성 지원
            function addKeyboardSupport(treeSelector) {
                $(treeSelector).on('keydown', function(e) {
                    if (e.keyCode === 86 || e.key === 'v' || e.key === 'V') {
                        e.preventDefault();
                        
                        const tree = $(this).jstree(true);
                        let targetNodeId = null;
                        
                        const hoveredNode = $(this).find('.jstree-hovered').first();
                        if (hoveredNode.length > 0) {
                            targetNodeId = hoveredNode.closest('.jstree-node').attr('id');
                        } else {
                            const selectedNodes = tree.get_selected();
                            if (selectedNodes.length > 0) {
                                targetNodeId = selectedNodes[0];
                            }
                        }
                        
                        if (targetNodeId) {
                            if (tree.is_checked(targetNodeId)) {
                                tree.uncheck_node(targetNodeId);
                            } else {
                                tree.check_node(targetNodeId);
                            }
                        }
                    }
                });
            }

            // 권한 그룹 목록 로드
            function loadAuthorityGroups() {
                return AJAX.getData(OsoriRoute.getUri('authority.findAll', {id: projectId}))
                    .done(function(data) {
                        const groupTable = $('#table-groups').DataTable();
                        groupTable.clear().rows.add(data.content).draw();
                    });
            }

            // 이벤트 바인딩
            function bindEvents() {
                $('#btn_create').off('click').on('click', handleCreateAuthority);
            }

            // 권한 그룹 생성 처리
            function handleCreateAuthority() {
                const dataTable = $('#table-selected').DataTable();
                const groupName = $('#group-name').val().trim();

                // 유효성 검사
                if (!validateForm(groupName, dataTable)) {
                    return;
                }

                const requestData = {
                    groupName: groupName,
                    naviId: _.pluck(dataTable.data(), 'id')
                };

                AJAX.postData(
                    OsoriRoute.getUri('authority.create', {id: projectId}),
                    requestData
                ).done(function() {
                    resetForm();
                    loadAuthorityGroups();
                    alert('권한 그룹이 생성되었습니다.');
                });
            }

            // 폼 유효성 검사
            function validateForm(groupName, dataTable) {
                if (groupName === '') {
                    alert('그룹명을 입력해주세요.');
                    $('#group-name').focus();
                    return false;
                }

                if (dataTable.data().length < 1) {
                    alert('최소 한개이상의 메뉴를 선택해주세요.');
                    return false;
                }

                return true;
            }

            // 폼 초기화
            function resetForm() {
                $('#menuNaviTree').jstree(true).uncheck_all();
                $('#table-selected').DataTable().clear().draw();
                $('#group-name').val('');
            }

            // 툴팁 초기화
            function initializeTooltips() {
                $('[data-toggle="tooltip"]').tooltip();
            }

            // 레거시 함수 - 기존 호환성 유지
            function getAuthGroup() {
                return loadAuthorityGroups();
            }

        </script>
	</body>
</html>
