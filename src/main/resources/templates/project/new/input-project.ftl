<#import "../../mecro/base-layout.ftl" as layout>
<!DOCTYPE html>
<html>
    <@layout.baseHeader "New Project"/>
    <body class="hold-transition skin-blue sidebar-mini">
        <@layout.baseWrapper>
            <#include "content-header.ftl">
            <section class="content">
                <div class="row">
                    <div class="col-md-12">
                        <#include "navi-wizard.ftl">
                    </div>
                    <div class="col-md-12">
                        <!-- general form elements -->
                        <div class="box box-solid">
                            <div class="box-header with-border">
                                <h3 class="box-title">프로젝트 기본 정보</h3>
                            </div>
                            <!-- /.box-header -->
                            <!-- form start -->
                                <div class="box-body">
                                    <div class="form-group">
                                        <label for="inputProjectName">프로젝트 명</label>
                                        <i class="fa fa-question-circle"></i>
                                        <input type="text" class="form-control" id="inputProjectName" placeholder="Enter project name">
                                        <span style="display: none" class="help-block">프로젝트명을 입력해주세요.</span>
                                    </div>
                                    <div class="form-group">
                                        <label for="inputProjectDescription">설명</label>
                                        <div id="inputProjectDescription" data-placeholder="Place some text here"  style="font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;">
                                        </div>
                                    </div>
                                </div>
                                <!-- /.box-body -->
                                <div class="box-footer">
                                        <button id="btn_submit" type="submit" class="btn btn-primary pull-right">생성</button>
                                </div>

                        </div>
                        <!-- /.box -->
                    </div>
                </div>
            </section>
        </@layout.baseWrapper>

        <script>
            $.fn.wysihtml5.defaultOptions.locale = 'ko-KR';
            $("#inputProjectDescription").wysihtml5();

            $('#btn_submit').click(function(){
                if($('#inputProjectName').val() == ""){
                    $('.form-group:first').removeClass('has-feedback').addClass('has-error');
                    $('.help-block').show();

                    return false;
                }

                var param = {
                    url: OsoriRoute.getUri("project.create"),
                    data: {
                        name: $('#inputProjectName').val(),
                        description: $('#inputProjectDescription').html()
                    }
                };

                AJAX.postData(param.url, param.data).done(function(data){
                    OsoriRoute.go("view.newProject.navi", {id: data.result});
                }).fail(function(xhr, status){
                    console.log(xhr);
                    console.log(status);
                    alert("등록중 오류가 발생했습니다");
                });
            });

        </script>
    </body>
</html>
