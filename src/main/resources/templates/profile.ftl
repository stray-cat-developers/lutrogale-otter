<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html>
<@layout.baseHeader "Profile"/>
<body class="hold-transition skin-blue sidebar-mini">
<@layout.baseWrapper>
	<section class="content-header">
		<h1>
			<i class="fa fa-wrench"></i>
			관리자 프로파일
			<small>오소리 관리자의 정보를 변경합니다.</small>
		</h1>
	</section>
	<section class="content">
		<div class="row">
			<div class="col-md-12">
				<!-- general form elements -->
				<div class="box box-primary">
					<div class="box-header with-border">
						<h3 class="box-title">관리 정보</h3>
					</div>
					<!-- /.box-header -->
					<!-- form start -->
					<div class="box-body">
                        <div class="form-group">
                            <label>Admin Email</label>
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-envelope-o"></i>
                                </div>
                                <input id="admin_email" type="text" class="form-control" value="" disabled>
                            </div>
                            <!-- /.input group -->
                        </div>
                        <div class="form-group">
                            <label>이름</label>
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-user"></i>
                                </div>
                                <input id="admin_name" type="text" class="form-control" value="" disabled>
                            </div>
                            <!-- /.input group -->
                        </div>
                        <div class="form-group">
                            <label>설명</label>
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-file-text-o"></i>
                                </div>
                                <input id="admin_description" type="text" class="form-control" value="">
                            </div>
                            <!-- /.input group -->
                        </div>
                        <div id="change_password" class="form-group">
                            <label>비밀번호 변경</label>
                            <div class="row">
                                <div class="col-lg-6">
                                    <div class="input-group">
										<span class="input-group-addon">
										  비밀번호 입력
										</span>
										<input id="password" type="password" class="form-control">
									</div>
								</div>
								<div class="col-lg-6">
									<div class="input-group">
										<span class="input-group-addon">
										  비밀번호 재입력
										</span>
                                        <input id="password_again"type="password" class="form-control">
                                    </div>
                                    <!-- /input-group -->
                                </div>
                            </div>
                            <span style="display: none" class="help-block">비밀번호가 일치하지 않습니다.</span>
						</div>
                        <div class="form-group">
                            <label>관리자 이미지 변경</label>
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-image"></i>
                                </div>
                                <input id="image_url" type="text" class="form-control" value="OsoriLoginUser.img" placeholder="이미지 url을 입력해주세요.">
                            </div>
                            <!-- /.input group -->
                        </div>
					</div>
					<!-- /.box-body -->
					<div class="box-footer">
						<button id="btn_submit" type="submit" class="btn btn-primary">변경</button>
                        <button id="btn_expire" type="submit" class="btn btn-danger pull-right">만료</button>
					</div>

				</div>
				<!-- /.box -->
			</div>
		</div>
	</section>
</@layout.baseWrapper>

<script>
    $(document).ready(function() {
        AJAX.getData(OsoriRoute.getUri('admin.findOne')).done(function(data){
            console.log(data.code);
            if(data.code == "0000"){
                $('#admin_name').val(data.result.name);
				$('#admin_email').val(data.result.email);
				$('#admin_description').val(data.result.description);
                $('#image_url').val(data.result.img);
            }
        }).fail(function(xhr, status){
			alert('로그인 한 관리자의 정보를 불러올 수 없습니다.');
            console.log(xhr);
            console.log(status);
        });
    });

    $('#btn_submit').click(function(){
		var pw = $('#password').val();
		var pwAgain = $('#password_again').val();

        $('.help-block').hide();
        $('#change_password').removeClass('has-error').addClass('has-feedback');

		if(!_.isEmpty(pw) || !_.isEmpty(pwAgain)){
			if(!_.isEqual(pw, pwAgain)){
                $('#change_password').removeClass('has-feedback').addClass('has-error');
                $('.help-block').show();
				return;
			}
		}

        var param = {
            url: OsoriRoute.getUri('admin.modifyInfo'),
            data: {
                pw: pw,
                description: $('#admin_description').val(),
				imageUrl: $('#image_url').val()
            }
        };

        AJAX.putData(param.url, param.data).done(function(data){
            if(data.code == "0000")
                alert("수정 완료");
            else
                alert("수정 중 오류가 발생했습니다");
        }).fail(function(xhr, status){
            console.log(xhr);
            console.log(status);
        });
    });
</script>
</body>
</html>
