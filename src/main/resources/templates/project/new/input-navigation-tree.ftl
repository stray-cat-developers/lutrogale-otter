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
                    <#include "project-info-box.ftl" />
                    <!-- general form elements -->
                    <!-- /.box -->
                    <div class="col-md-5">
                        <div class="box box-solid">
                            <div class="box-header with-border">
                                <h3 class="box-title">네비게이션 생성</h3>
                            </div>
                            <!-- /.box-header -->
                            <!-- form start -->
                            <form class="form-horizontal">
                                <div class="box-body">
                                    <div id="menuNaviTree">
                                        <ul>
                                            <li>Root
                                                <ul>
                                                    <li id="child_node">Child</li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </div>
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
                                        <input type="radio" name="navTypeGroup1" value="category" checked>카테고리
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="navTypeGroup1" value="menu">메뉴
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="navTypeGroup1" value="function">기능
                                    </label>
                                </div>
                                <!-- text input -->
                                <div class="form-group">
                                    <label>전체 경로</label>
                                    <input id="full_url" type="text" class="form-control" disabled>
                                </div>
                                <div class="form-group">
                                    <label>네비게이션 명</label>
                                    <input id="info_name" type="text" class="form-control" placeholder="메뉴 수정">
                                </div>
                                <div class="form-group">
                                    <label>URL Path Block</label>
                                    <i class="fa fa-question-circle"></i>
                                    <input id="info_url_path" type="text" class="form-control" placeholder="Enter ...">
                                </div>
                                <div class="form-group">
                                    <label>Http Method Type 선택</label>
                                    <i class="fa fa-question-circle"></i>
                                    <select id="info_method" class="form-control">
                                        <option>GET</option>
                                        <option>POST</option>
                                        <option>PUT</option>
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
                    <div class="col-md-12">
                        <button onclick="javascript:OsoriRoute.go('view.newProject.authGroup', {id:SS.project_id});" class="btn btn-info pull-right">다음</button>
                    </div>
                </div>
            </div>

            <div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false" >
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h4 class="modal-title">네비게이션 정보 입력</h4>
                        </div>
                        <div class="modal-body">
                            <div class="box box-solid">
                                <!-- /.box-header -->
                                <div class="box-body">
                                    <div id="nav_radio_group" class="form-group">
                                        <label>네비게이션 타입</label>
                                        <label class="radio-inline">
                                            <input type="radio" name="navTypeGroup2" value="category" checked>카테고리
                                        </label>
                                        <label class="radio-inline">
                                            <input type="radio" name="navTypeGroup2" value="menu">메뉴
                                        </label>
                                        <label class="radio-inline">
                                            <input type="radio" name="navTypeGroup2" value="function">기능
                                        </label>
                                    </div>
                                    <!-- text input -->
                                    <div class="form-group">
                                        <label>네비게이션 명</label>
                                        <input id="nav_name" type="text" class="form-control" placeholder="메뉴 수정">
                                    </div>
                                    <div class="form-group">
                                        <label>URL Path Block</label>
                                        <i class="fa fa-question-circle"></i>
                                        <input id="nav_url_path" type="text" class="form-control" placeholder="Enter ...">
                                    </div>
                                    <div class="form-group">
                                        <label>Http Method Type 선택</label>
                                        <i class="fa fa-question-circle"></i>
                                        <select id="nav_method" class="form-control">
                                            <option>GET</option>
                                            <option>POST</option>
                                            <option>PUT</option>
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
    <#include "script/input-navigation-tree-script.ftl">
    </body>
</html>
