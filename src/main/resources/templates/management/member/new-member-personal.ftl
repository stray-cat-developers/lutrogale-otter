<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html>
<@layout.baseHeader "New Project">

</@layout.baseHeader>
<body class="hold-transition skin-blue sidebar-mini">
<@layout.baseWrapper>
<section class="content-header">
    <h1>사용자 생성</h1>
</section>

<section class="content">
    <div class="create-user-wrap">
        <!-- Nav tabs -->
        <ul class="nav nav-pills nav-justified">
            <li class="done">1. 신규 사용자 생성</li>
            <li class="done">2. 권한 그룹 설정</li>
            <li class="active">3. 개인별 권한 설정</li>
            <li>4. 완료</li>
        </ul>
        <!-- Tab panes -->
        <div class="box box-primary box-body">
            <div class="box box-solid">
                <div class="box-header with-border"><h4>사용자 정보</h4></div>
                <div class="box-body form-horizontal">
                    <div class="form-group">
                        <strong class="col-sm-2">사용자 이메일</strong>
                        <div id="user-email" class="col-sm-10"></div>
                    </div>
                    <div class="form-group">
                        <strong class="col-sm-2">사용자 이름</strong>
                        <div id="user-name" class="col-sm-10"></div>
                    </div>
                    <div class="form-group">
                        <strong class="col-sm-2">소속 부서</strong>
                        <div id="user-dept" class="col-sm-10"></div>
                    </div>
                    <div class="form-group">
                        <strong class="col-sm-2">개인 정보 노출 여부</strong>
                        <div id="user-privacy" class="col-sm-10"></div>
                    </div>
                </div>
            </div>

            <div id="content"></div>

            <div class="btn-area text-right">
                <button class="btn btn-default" type="button">이전</button>
                <button class="btn btn-primary" type="button" onclick="nextStep();">다음</button>
            </div>
        </div>
    </div>
</section>
</@layout.baseWrapper>
<script src="/static/plugins/datatables/jquery.dataTables.js"></script>
<script src="/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script>
    var opt = {
        'tb_api': {
            'scrollY': '150px',
            'scrollCollapse': true,
            'columns': [
                null,
                {title: '타입', data: 'type'},
                {title: '메뉴명', data: 'name'},
                {title: 'URL 경로', data: 'fullUrl'}
            ],
            'columnDefs': [
                {
                    'targets': 0,
                    'data': null,
                    'defaultContent': '<input name="cbx_auth" type="checkbox" class="flat-red">'
                }
            ]
        }
    };

    var user_id = extractByWord('new-member');
    var project_id = OsoriRoute.getQuery().projectId.split(',');

    $(document).ready(function(){
        AJAX.getData(OsoriRoute.getUri('user.findOne', {userId: user_id}))
        .done(function(user){
            var user = user.result;

            $('#user-email').text(user.email);
            $('#user-name').text(user.name);
            $('#user-dept').text(user.department);
            $('#user-privacy').text(user.accessPrivacyInformation);
        });

        var row;
        $.each(project_id, function(index, project_id) {
            row = $('<div/>', {class: 'row'});

            $.when(
                AJAX.getData(OsoriRoute.getUri('project.findOne', {id: project_id}), {}, {async:false}),
                AJAX.getData(OsoriRoute.getUri('project.findNavigationsProject', {id: project_id}), {}, {async:false})
            ).done(function(project, api_list) {
                var project = project[0].result;
                var api_list = _.map(api_list[0].result, function(v){
                                    return _.extend(v, {projectId: project.id})
                                });

                $('<div/>', {class: 'col-md-12'}).append(
                    $('<div/>', {class: 'box box-solid'}).append(
                        $('<div/>', {class: 'box-header with-border'}).append(
                            $('<h4/>', {text: project.name})
                        ),
                        $('<div/>', {class: 'box-body'}).append(
                            $('<table/>', {class: 'table table-bordered table-striped'})
                        )
                    )
                ).appendTo($(row));

                $(row).find('.table').DataTable(OPTION.data_table(opt.tb_api, api_list));
            });

            $(row).appendTo('#content');
        });

        setTimeout(function(){
            var table = $('.table').DataTable();
            $('#content input:checkbox').change(function(){
                var data = table.row($(this).parents('tr')).data();
                if($(this).is(":checked"))
                    assignPersonalGrant(data.projectId, user_id, data.id, this);
                else
                    withdrawPersonalGrant(data.projectId, user_id, data.id, this);
            });
        }, 1000);

    });

    function assignPersonalGrant(project_id, user_id, navi_id, chx_onoff){
        AJAX.postData(
            OsoriRoute.getUri('user.assignPersonalGrant', {projectId: project_id, userId: user_id, menuNaviIdGroup: navi_id})
        )
        .fail(function(){
            $(chx_onoff).prop('checked', false);
        });
    }

    function withdrawPersonalGrant(project_id, user_id, navi_id, chx_onoff){
        AJAX.deleteData(
            OsoriRoute.getUri('user.withdrawPersonalGrant', {projectId: project_id, userId: user_id, menuNaviIdGroup: navi_id})
        )
        .fail(function(){
            $(chx_onoff).prop('checked', true);
        });
    }

    function nextStep(){
        OsoriRoute.go(
            'view.management.newMember.complete',
            {userId: user_id}
        );
    }

</script>
</body>
</html>
