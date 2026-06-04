<ul class="nav nav-wizard">
    <li class="">
        <a href="#" onclick="OsoriRoute.go('view.newProject');" data-toggle="tab">Step1. 프로젝트 기본 정보 입력</a>
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
    let url = window.location.pathname;
    let project_id = extractByWord('project');

    let arr_li = $('.nav-wizard').children('li');

    const steps = [
        { route: 'view.newProject.navi',      idx: 1 },
        { route: 'view.newProject.authGroup', idx: 2 },
        { route: 'view.newProject.complete',  idx: 3 }
    ];

    const activeStep = steps.find(function(s) {
        return url === OsoriRoute.getUri(s.route, {id: project_id});
    });
    const activeIdx = activeStep ? activeStep.idx : 0;

    $(arr_li[activeIdx]).addClass('active');

    if (activeStep) {
        $(arr_li[activeIdx]).children('a').prop('data-toggle', 'tab').click(function() {
            OsoriRoute.go(activeStep.route, {id: project_id});
        });
    }

</script>
<hr class="separator-line">

