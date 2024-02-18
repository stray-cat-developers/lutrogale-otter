<script>
    $(document).ready(function() {
        SS.setItem("project_id", extractByWord('project'));

        $.when(
            AJAX.getData(OsoriRoute.getUri('project.findOne', {id:SS.project_id})),
            AJAX.getData(OsoriRoute.getUri('menuTree.getAllBranch', {id:SS.project_id}))
        ).done(function(first, second){
            let project_obj = first[0];
            let navigation_list = second[0].content;

            $('#project_name').text(project_obj.name);
            $('#project_desc').html(project_obj.description);
            $('#project_apiKey').text(project_obj.apiKey);

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
                let param = {
                    parentTreeId : data.parent
                };

                AJAX.putData(
                    OsoriRoute.getUri('menuTree.moveBranch', {id:SS.project_id, nodeId:data.node.a_attr.id}),
                    param
                ).done(function(){
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
        let tree = $('#menuNaviTree').jstree(true);

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

        let param = {
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
            $('#menuNaviTree').jstree("deselect_all");
        })
    });

    $('#btn_modal_submit').click(function(){
        var nav_type     = $('#nav_radio_group :radio:checked').val();
        var nav_name     = $('#nav_name').val();
        var nav_url_path = $('#nav_url_path').val();
        var nav_method   = $('#nav_method').val();

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
