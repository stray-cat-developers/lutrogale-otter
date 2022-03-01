<#if format??>
{"timestamp":${timestamp?c},"code":"${code}","message":"${message}","result":null}
<#else>
	<#import "mecro/base-layout.ftl" as layout>
	<!DOCTYPE html>
	<html>
		<@layout.baseHeader "New Project"/>
	<body class="hold-transition skin-blue sidebar-mini">
		<@layout.baseWrapper>
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
			${code} Error Page
            </h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="error-page">
                <h2 class="headline text-red">${code}</h2>

                <div class="error-content">
                    <h3><i class="fa fa-warning text-red"></i> Oops! ${message}.</h3>
                    <p>
						<#if loggingMsg??>
						${loggingMsg}
						<#else>
                            프로젝트 github에 이슈로 남겨주세요 :") </br>
                            github link: <a href="https://github.com/woowabros/osori/issues">github osori</a>
						</#if>
                    </p>
                </div>
            </div>
            <!-- /.error-page -->
        </section>
		</@layout.baseWrapper>
	</body>
	</html>
</#if>

