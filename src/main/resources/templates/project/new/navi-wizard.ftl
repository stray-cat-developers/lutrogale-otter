<ul class="nav nav-wizard">
    <li class="">
        <a href="#" onclick="javascript:OsoriRoute.go('view.newProject');" data-toggle="tab">Step1. 프로젝트 기본 정보 입력</a>
    </li>
    <li class="">
        <a href="#">Step2. 메뉴 네비게이션 등록</a>
    </li>
    <li class="">
        <a href="#">Step3. 권한 그룹 생성</a>
    </li>
    <li class="">
        <a href="#">Step4. 완료</a>
    </li>
</ul>

<script>
    var url = window.location.pathname;
    var project_id = extractByWord('project');

    var arr_li = $('.nav-wizard').children('li');

    switch(url){
        case OsoriRoute.getUri('view.newProject.complete',{id:project_id}):
            $(arr_li[3]).addClass('active'); break;
        case OsoriRoute.getUri('view.newProject.authGroup',{id:project_id}):
            $(arr_li[2]).addClass('active'); break;
        case OsoriRoute.getUri('view.newProject.navi',{id:project_id}):
            $(arr_li[1]).addClass('active'); break;
        default:
            $(arr_li[0]).addClass('active');
    }

    switch(url){
        case OsoriRoute.getUri('view.newProject.complete',{id:project_id}):
            $(arr_li[3]).children('a').prop('data-toggle', 'tab');
            $(arr_li[3]).children('a').click(function(){
                OsoriRoute.go('view.newProject.complete',{id:project_id});
            });
        case OsoriRoute.getUri('view.newProject.authGroup',{id:project_id}):
            $(arr_li[2]).children('a').prop('data-toggle', 'tab');
            $(arr_li[2]).children('a').click(function(){
                OsoriRoute.go('view.newProject.authGroup',{id:project_id});
            });
        case OsoriRoute.getUri('view.newProject.navi',{id:project_id}):
            $(arr_li[1]).children('a').prop('data-toggle', 'tab');
            $(arr_li[1]).children('a').click(function(){
                OsoriRoute.go('view.newProject.navi',{id:project_id});
            });
    }

</script>
<hr style="border: none; border-bottom: 1px solid dimgrey;">

