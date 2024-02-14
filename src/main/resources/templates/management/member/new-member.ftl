<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html lang="ko">
<@layout.baseHeader "New Project">
    <link rel="stylesheet" href="/static/plugins/select2/select2.css">
    <link rel="stylesheet" href="/static/dist/css/AdminLTE.min.css">
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
            <li class="active">1. 신규 사용자 생성</li>
            <li>2. 권한 그룹 설정</li>
            <li>3. 개인별 권한 설정</li>
            <li>4. 완료</li>
        </ul>
        <!-- Tab panes -->
        <div class="tab-content box box-primary box-body">
            <div role="tabpanel" class="tab-pane active form-horizontal" id="tab1">
                <div class="form-group">
                    <label class="col-sm-2 control-label">사용자 이메일</label>
                    <div class="col-sm-10">
                        <label for="user-email"></label>
                        <input class="form-control" id="user-email" type="email" placeholder="email 주소를 입력해주세요">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">사용자 이름</label>
                    <div class="col-sm-10">
                        <label for="user-name"></label>
                        <input class="form-control" id="user-name" type="text" placeholder="이름을 입력해주세요">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">소속 부서</label>
                    <div class="col-sm-10">
                        <label for="user-dept"></label>
                        <input class="form-control" id="user-dept" type="text" placeholder="부서를 입력해주세요">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">개인 정보 노출 여부</label>
                    <div class="col-sm-10">
                        <label for="user-privacy"></label>
                        <select id="user-privacy" class="form-control">
                            <option value="true">yes</option>
                            <option value="false">no</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">프로젝트 목록</label>
                    <div class="col-sm-10">
                        <select class="form-control select2" multiple="multiple" data-placeholder="프로젝트를 선택해주세요"></select>
                    </div>
                </div>
            </div>

            <div class="btn-area text-right">
                <button class="btn btn-primary" type="button" onclick="nextStep();">다음</button>
            </div>
        </div>
    </div>
</section>
</@layout.baseWrapper>
<script src="/static/plugins/select2/select2.full.min.js"></script>
<script>
    $(document).ready(function(){
        AJAX.getData(OsoriRoute.getUri('project.findAll')).done(function(data){
            $(".select2").select2({
                data: _.map(data.result, function(v){ return {id: v.id, text: v.name};})
            });
        });

    });

    function nextStep(){
        let email = $('#user-email').val();
        let name = $('#user-name').val();
        let dept = $('#user-dept').val();
        let privacy = $('#user-privacy').val();

        let param = {
            email: email,
            name: name,
            department: dept,
            isPrivacy: privacy
        };

        AJAX.postData(OsoriRoute.getUri('user.create'), param).done(function(data){
            OsoriRoute.go(
                'view.management.newMember.authorityGrant',
                {userId: data.content}, {projectId: $('.select2').val().join()}
            );
        });
    }
</script>
</body>
</html>
