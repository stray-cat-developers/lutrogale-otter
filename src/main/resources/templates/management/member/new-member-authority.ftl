<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
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
            <li class="active">2. 권한 그룹 설정</li>
            <li>3. 개인별 권한 설정</li>
            <li>4. 완료</li>
        </ul>
        <!-- Tab panes -->
        <div class="box box-primary box-body">
            <@layout.userInfoBox />

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
    let opt = {
        'tb_project': {
            'scrollY': '150px',
            'scrollCollapse': true,
            'columns': [
                {title: '권한 그룹명', data: 'name'},
                {title: ''}
            ],
            'columnDefs': [
                {
                    'targets': 1,
                    'data': null,
                    'defaultContent': '<input name="cbx_auth" type="checkbox" class="flat-red">'
                }
            ]
        }
    };

    let user_id = extractByWord('new-member');
    let project_id = OsoriRoute.getQuery().projectId.split(',');

    $(document).ready(function(){

        AJAX.getData(OsoriRoute.getUri('user.findOne', {userId: user_id}))
        .done(function(data){
            $('#user-email').text(data.email);
            $('#user-name').text(data.name);
            $('#user-dept').text(data.department);
            $('#user-privacy').text(data.accessPrivacyInformation);
        });

        const COLUMNS_PER_ROW = 3;
        let row;

        $.each(project_id, function(index, pid) {
            if (index % COLUMNS_PER_ROW === 0)
                row = $('<div/>', {class: 'row'});

            $.when(
                AJAX.getData(OsoriRoute.getUri('project.findOne', {id: pid}), {}, {async:false}),
                AJAX.getData(OsoriRoute.getUri('authority.findAll', {id: pid}), {}, {async:false})
            ).done(function(first, second) {
                let project = first[0];
                let authority = second[0].content;

                $('<div/>', {class: 'col-md-4'}).append(
                    $('<div/>', {class: 'box box-solid'}).append(
                        $('<div/>', {class: 'box-header with-border'}).append(
                            $('<h4/>', {text: project.name})
                        ),
                        $('<div/>', {class: 'box-body'}).append(
                            $('<table/>', {id: 'table_'+index, class: 'table table-bordered table-striped'})
                        )
                    )
                ).appendTo($(row));

                $(row).find('#table_'+index).DataTable(OPTION.data_table(opt.tb_project, authority));
            });

            if ((index + 1) % COLUMNS_PER_ROW === 0 || index === project_id.length - 1)
                $(row).appendTo('#content');
        });

        setTimeout(function(){
            let table = $('.table').DataTable();
            $('#content input:checkbox').change(function(){
                let data = table.row($(this).parents('tr')).data();

                if($(this).is(":checked")){
                    assignAuthorityGrant(data.projectId, user_id, data.authId, this);
                }else{
                    withdrawAuthorityGrant(data.projectId, user_id, data.authId, this);
                }

            });
        }, 1000);

    });

    function assignAuthorityGrant(project_id, user_id, auth_id, chx_on_off){
        AJAX.postData(
            OsoriRoute.getUri('user.assignAuthorityGrant', {projectId: project_id, userId: user_id, authIdGroup: auth_id}),
            {'async': false}
        )
        .fail(function(){
            $(chx_on_off).prop('checked', false);
        });
    }

    function withdrawAuthorityGrant(project_id, user_id, auth_id, chx_on_off){
        AJAX.deleteData(
            OsoriRoute.getUri('user.withdrawAuthorityGrant', {projectId: project_id, userId: user_id, authIdGroup: auth_id}),
            {'async': false}
        )
        .fail(function(){
            $(chx_on_off).prop('checked', true);
        });
    }

    function nextStep(){
        AJAX.getData(OsoriRoute.getUri('user.findUsersProjects', {userId: user_id})).done(function(data){
            if(data.content.length === 0){
                alert('최소 한개이상의 권한 그룹을 선택해야합니다.');
                return false;
            }else{
                OsoriRoute.go(
                    'view.management.newMember.personalGrant',
                    {userId: user_id}, {projectId: new URI(window.location).query(true).projectId}
                );
            }

        });
    }

</script>
</body>
</html>
