<#macro baseHeader title>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Osori Authority | ${title!""}</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="/static/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="/static/bootstrap/css/bootstrap-switch.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="/static/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins
		 folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="/static/dist/css/skins/_all-skins.min.css">
	<#include "../layout/base-css.ftl"/>

	<#nested />

    <!-- jQuery 2.2.3 -->
    <script src="//code.jquery.com/jquery-2.2.3.min.js"></script>
    <!-- jQuery UI 1.11.4 -->
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
    <script>
        $.widget.bridge('uibutton', $.ui.button);
    </script>
    <!-- Bootstrap 3.3.6 -->
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
    <script src="/static/bootstrap/js/bootstrap-switch.min.js"></script>

	<script src="/static/dist/js/underscore.js"></script>
	<!-- https://medialize.github.io/URI.js -->
	<script src="/static/plugins/uri/URI.js"></script>
	<!-- layout  -->
    <script src="/static/dist/js/layout.js"></script>
	<script src="/static/dist/js/route.js"></script>
    <script src="/static/dist/js/osori.js"></script>
</head>
</#macro>

<#macro baseWrapper>
	<div class="wrapper">
		<header class="main-header">
			<!-- Logo -->
			<a href="/dashboard" class="logo">
				<!-- mini logo for sidebar mini 50x50 pixels -->
				<span class="logo-mini"><b>A</b>uth</span>
				<!-- logo for regular state and mobile devices -->
				<span class="logo-lg"><b>Osori</b>Authority</span>
			</a>
			<!-- Header Navbar: style can be found in header.less -->
			<#include "../layout/gnb-top.ftl"/>
		</header>
		<!-- Left side column. contains the logo and sidebar -->
		<#include "../layout/gnb-left.ftl"/>

		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<#nested/>
		</div>
		<!-- /.content-wrapper -->
		<#include "../layout/footer.ftl"/>
		<!-- Control Sidebar -->
		<#include "../layout/control-side.ftl"/>
		<!-- /.control-sidebar -->
        <!-- ./wrapper -->
		<#include "../layout/base-script.ftl"/>
	</div>
</#macro>

<#macro plainModal type size id needFooter>
<div id="${id}" class="modal ${type} fade" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog ${size}">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">x</span></button>
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">

			</div>
			<div class="modal-footer">
				<#if needFooter != "">
				<button id="${id}-close" type="button" class="btn btn-default pull-left" data-dismiss="modal">취소</button>
				<button id="${id}-submit" type="button" class="btn btn-primary">적용</button>
				</#if>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
</#macro>

<#macro alertModal type id>
	<div class="alert-modal">
		<div id="${id!"alert-modal"}" class="modal ${type!"modal-dialog"} fade" tabindex="-1" role="dialog" aria-labelledby="${id!"alert-modal"}" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Alert</h4>
					</div>
					<div class="modal-body">
						<p>

						</p>
					</div>
                    <div class="modal-footer">
						<button id="${id!"alert-modal"}_close" type="button" class="btn btn-outline pull-left" data-dismiss="modal">Close</button>
						<button id="${id!"alert-modal"}_submit" type="button" class="btn btn-outline">Confirm</button>
					</div>
				</div>
                <!-- /.modal-content -->
			</div>
            <!-- /.modal-dialog -->
		</div>
        <!-- /.modal -->
	</div>
</#macro>

<#macro authorityBundleDetailModal>
	<div id="authority-bundle-detail-modal" class="modal fade" role="dialog" aria-labelledby="authority-bundle-detail" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">권한 그룹</h4>
				</div>
				<div class="modal-body">
                    <div class="row">
                        <div class="col-lg-5">
                            <div class="box box-solid">
								<div class="box-header with-border"><h4>메뉴구조</h4></div>
								<div class="box-body">
                                    <div id="menuNaviTree"></div>
								</div>
							</div>
						</div>
                        <div class="col-lg-7">
                            <div class="box box-solid">
								<div class="box-header with-border"><h4>API 리스트</h4></div>
								<div class="box-body">
                                    <table id="apiList" class="table table-bordered table-striped" cellspacing="0" style="width:100%;"></table>
								</div>
							</div>
						</div>
                    </div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
</#macro>
