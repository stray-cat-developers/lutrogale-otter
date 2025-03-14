<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
    <@layout.baseHeader "메뉴 네비게이션 설정"/>
    <body class="hold-transition skin-blue sidebar-mini">
    <@layout.baseWrapper>
    <section class="content-header">
        <h1>메뉴 네비게이션 설정</h1>
    </section>
    <section class="content">
        <div class="row">
            <div class="col-md-12">
                <!-- general form elements -->
                <!-- /.box -->
                <div class="col-md-5">
                    <div class="box  box-solid">
                        <div class="box-header with-border">
                            <h3 class="box-title">네비게이션 생성</h3>
                        </div>
                        <!-- /.box-header -->
                        <!-- form start -->
                        <form class="form-horizontal">
                            <div class="box-body" style="height: 50vh; overflow-y: auto;">
                                <div id="menuNaviTree"></div>
                            </div>
                            <!-- /.box-body -->
                        </form>
                    </div>
                </div>
                <div class="col-md-7">
                    <div class="box box-solid">
                        <div class="box-header with-border">
                            <h3 class="box-title">네비게이션 정보 입력</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <div id="info_radio_group" class="form-group">
                                <label>네비게이션 타입</label>
                                <label class="radio-inline">
                                    <input type="radio" name="navTypeGroup1" value="CATEGORY" checked>카테고리
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="navTypeGroup1" value="MENU">메뉴
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="navTypeGroup1" value="FUNCTION">기능
                                </label>
                            </div>
                            <!-- text input -->
                            <div class="form-group">
                                <label for="full_url">전체 경로</label>
                                <input id="full_url" type="text" class="form-control" disabled>
                            </div>
                            <div class="form-group">
                                <label for="info_name">네비게이션 명</label>
                                <input id="info_name" type="text" class="form-control" placeholder="메뉴 수정">
                            </div>
                            <div class="form-group">
                                <label for="info_url_path">URL Path Block</label>
                                <i class="fa fa-question-circle"></i>
                                <input id="info_url_path" type="text" class="form-control" placeholder="Enter ...">
                            </div>
                            <div class="form-group">
                                <label for="info_method">Http Method Type 선택</label>
                                <i class="fa fa-question-circle"></i>
                                <select id="info_method" class="form-control">
                                    <option>GET</option>
                                    <option>POST</option>
                                    <option>PUT</option>
                                    <option>PATCH</option>
                                    <option>DELETE</option>
                                </select>
                            </div>
                            <!-- /.box-body -->
                            <div class="box-footer">
                                <button id="btn_info_modify" type="submit" class="btn btn-info">수정</button>
                            </div>
                            <!-- /.box-footer -->
                            <div id="popover_result" style="display: none;" class="alert alert-info alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                                <h4><i class="icon fa fa-check"></i>수정완료!</h4>
                            </div>

                        </div>
                        <!-- /.box-body -->
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div class="box box-solid">
                    <div class="box-header with-border">
                        <h3 class="box-title">메뉴 API 목록</h3>
                    </div>
                    <div class="box-body">
                        <table id="table-api" class="table table-bordered table-striped"></table>
                    </div>
                </div>
            </div>
        </div>

        <div id="modal" class="modal fade" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">네비게이션 정보 입력</h4>
                    </div>
                    <div class="modal-body">
                        <div class="box-info">
                            <!-- /.box-header -->
                            <div class="box-body">
                                <div id="nav_radio_group" class="form-group">
                                    <label>네비게이션 타입</label>
                                    <label class="radio-inline">
                                        <input type="radio" name="navTypeGroup2" value="CATEGORY" checked>카테고리
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="navTypeGroup2" value="MENU">메뉴
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="navTypeGroup2" value="FUNCTION">기능
                                    </label>
                                </div>
                                <!-- text input -->
                                <div class="form-group">
                                    <label for="nav_name">네비게이션 명</label>
                                    <input id="nav_name" type="text" class="form-control" placeholder="메뉴 수정">
                                </div>
                                <div class="form-group">
                                    <label for="nav_url_path">URL Path Block</label>
                                    <i class="fa fa-question-circle"></i>
                                    <input id="nav_url_path" type="text" class="form-control" placeholder="Enter ...">
                                </div>
                                <div class="form-group">
                                    <label for="nav_method">Http Method Type 선택</label>
                                    <i class="fa fa-question-circle"></i>
                                    <select id="nav_method" class="form-control">
                                        <option>GET</option>
                                        <option>POST</option>
                                        <option>PUT</option>
                                        <option>PATCH</option>
                                        <option>DELETE</option>
                                    </select>
                                </div>
                            </div>
                            <!-- /.box-body -->
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button id="btn_modal_close" type="button" class="btn btn-default pull-left" data-dismiss="modal">닫기</button>
                        <button id="btn_modal_submit" type="button" class="btn btn-primary">확인</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
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

            AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:SS.project_id}))
            .done(function(data){
                let navigation_list = data.content;
                let table_api_opt = {
                    'searching': true,
                    'data': _.pluck(navigation_list, 'a_attr'),
                    'columns': [
                        {title: 'API ID', data: 'id'},
                        {title: '타입', data: 'type'},
                        {title: '메뉴명', data: 'name'},
                        {title: 'URL 경로', data: 'fullUrl'},
                    ]
                };

                $('#table-api').DataTable(OPTION.data_table(table_api_opt));

                $('#menuNaviTree').jstree(OPTION.jstree({}, navigation_list)).on('create_node.jstree', function(e, data){
                    SS.setItem('node_id', data.node.id);
                    SS.setItem('node_parent', data.node.parent);

                    $('#modal').modal('toggle');

                }).on('select_node.jstree', function(e, obj){

                    AJAX.getData(
                        OsoriRoute.getUri('menuTree.findBranch', {id:SS.project_id, nodeId:obj.node.a_attr.id})
                    ).done(function(data){
                        clearNavInfoArea();

                        let navi_obj = data;
                        $('#info_radio_group :input:radio[value='+navi_obj.type+']').prop('checked', true);
                        $('#full_url').val(navi_obj.a_attr.fullUrl);
                        $('#info_name').val(navi_obj.a_attr.name);
                        $('#info_url_path').val(navi_obj.a_attr.uriBlock);
                        $('#info_method').val(navi_obj.a_attr.methodType).attr('selected', 'selected');

                        SS.setItem('selected_node_id', navi_obj.a_attr.id);
                        SS.setItem('selected_tree_id', navi_obj.id);
                    });

                }).on('move_node.jstree', function(e, data){
                    var param = {
                        parentTreeId : data.parent
                    };

                    AJAX.putData(
                        OsoriRoute.getUri('menuTree.moveBranch', {id:SS.project_id, nodeId:data.node.a_attr.id}),
                        param
                    ).done(function(){
                        refreshApiList();

                        $('#popover_result').fadeTo(800, 500).slideUp(500, function(){
                            $("#success-alert").slideUp(500);
                        });
                    });

                }).on("loaded.jstree", function () {
                    $(this).jstree("open_all");
                });

            });

        });

        $('#btn_modal_close').click(function(){
            var tree = $('#menuNaviTree').jstree(true);

            tree.delete_node([SS.node_id]);
            clearModalData();
        });

        $('#btn_info_modify').click(function(){
            var info_type     = $('#info_radio_group :radio:checked').val();
            var info_name     = $('#info_name').val();
            var info_url_path = $('#info_url_path').val();
            var info_method   = $('#info_method').val();

            if(!SS.hasOwnProperty('selected_node_id')){
                alert('대상을 선택해주세요.');
                return false;
            }

            var param = {
                id : SS.selected_node_id,
                projectId : SS.project_id,
                type : info_type,
                name : info_name,
                uriBlock :info_url_path,
                methodType : info_method
            };

            AJAX.putData(
                OsoriRoute.getUri('navigation.modifyInfo', {id:SS.project_id,nodeId:SS.selected_node_id}),
                param
            ).done(function(){
                let tree = $('#menuNaviTree').jstree(true);
                let this_node = tree.get_node(SS.selected_tree_id);

                this_node.a_attr.uriBlock = param.uriBlock;
                tree.set_type(this_node, info_type);
                tree.rename_node(this_node, info_name);

                $('#popover_result').fadeTo(800, 500).slideUp(500, function(){
                    $("#success-alert").slideUp(500);
                });

                clearNavInfoArea();
                refreshApiList();
                $('#menuNaviTree').jstree("deselect_all");
            })
        });

        $('#btn_modal_submit').click(function(){
            let nav_type     = $('#nav_radio_group :radio:checked').val();
            let nav_name     = $('#nav_name').val();
            let nav_url_path = $('#nav_url_path').val();
            let nav_method   = $('#nav_method').val();

            if(nav_type === ""){
                alert("네비게이션 타입을 선택해주세요.");
                return false;
            }

            if(nav_name === "") {
                alert("네비게이션명을 입력해주세요.");
                return false;
            }

            var param = {
                projectId : SS.project_id,
                treeId : SS.node_id,
                parentTreeId : SS.node_parent,
                type : nav_type,
                name : nav_name,
                uriBlock :nav_url_path,
                methodType : nav_method
            };

            AJAX.postData(
                OsoriRoute.getUri('menuTree.createBranch', {id:SS.project_id}),
                param
            ).done(function(data){
                let tree = $('#menuNaviTree').jstree(true);
                let this_node = tree.get_node(SS.node_id);

                this_node.a_attr.uriBlock = param.uriBlock;
                this_node.a_attr.id = data.content;

                tree.set_type(this_node, nav_type);
                tree.rename_node(this_node, nav_name);

                $('#modal').modal('toggle');

                clearModalData();
                refreshApiList();
            });

        });

        function getFullUrl(node){
            var id_list = _.flatten([node.id, node.parents]);

            return _.chain(id_list)
                    .without(id_list, '#')
                    .map(function(treeId){
                        var tree = $('#menuNaviTree').jstree(true);
                        var this_node = tree.get_node(treeId);
                        return this_node.a_attr.uriBlock;
                    })
                    .reverse()
                    .value()
                    .join('');
        }

        function refreshApiList(){
            AJAX.getData(
                OsoriRoute.getUri('menuTree.getAllBranch', {id:SS.project_id})
            ).done(function(data){
                let data_table = $('#table-api').DataTable();
                data_table.clear().rows.add(_.pluck(data.content, 'a_attr')).draw();
            });
        }

        function clearNavInfoArea(){
            SS.removeItem('selected_node_id');
            SS.removeItem('selected_tree_id');

            $('#info_radio_group').find(':radio:first').prop('checked', true);
            $('#full_url').val('');
            $('#info_name').val('');
            $('#info_url_path').val('');
            $('#info_method').find('option:first').attr('selected', 'selected');
        }

        function clearModalData(){
            SS.removeItem('node_id');
            SS.removeItem('node_parent');

            $('#nav_radio_group').find(':radio:first').prop('checked', true);
            $('#nav_name').val('');
            $('#nav_url_path').val('');
            $('#nav_method').find('option:first').attr('selected', 'selected');

            $('#menuNaviTree').jstree("deselect_all");
        }

    </script>

    </body>
</html>
