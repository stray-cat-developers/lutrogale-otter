<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu">
            <li class="header">Management<li>
                <a id="members" href="#" onclick="OsoriRoute.go('view.management.members');">
                    <i class="fa fa-users"></i> <span>사용자 정보 관리</span>
                </a>
            </li>
            <li class="header">Projects</li>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
<script>
    if(window.location.pathname == OsoriRoute.getUri('view.management.members',{}))
        $('#members').parents('li').addClass('active');

    AJAX.getData(OsoriRoute.getUri("project.findAll")).done(function(data){
        if(data.code == "0000"){
            $.each(data.result, function(i,v){
                $('.sidebar-menu')
                        .append(
                                '<li class="treeview">' +
                                    '<a href="#"> <i class="fa fa-building"></i> <span>'+ v.name +'</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i> </span> </a>' +
                                    '<ul class="treeview-menu">' +
                                        '<li><a href="'+ OsoriRoute.getUri('view.project.configuration.navigation', {id: v.id}) + '"><i class="fa fa-circle-o"></i>메뉴 네비게이션 설정</a></li>' +
                                        '<li><a href="'+ OsoriRoute.getUri('view.project.configuration.authority', {id: v.id}) + '" ><i class="fa fa-circle-o"></i>권한 그룹 설정</a></li>' +
                                        '<li><a href="'+ OsoriRoute.getUri('view.project.configuration.members', {id: v.id}) +'"><i class="fa fa-circle-o"></i>사용자 권한 관리</a></li>' +
                                    '</ul>' +
                                '</li>');
            });
        }

        $('.treeview-menu > li a').each(function(i,v){
            var href = $(this).attr('href');

            if(URI(window.location.pathname).equals(href)){
                $(this).parents('li').addClass('active');
            }
        });
    });
</script>
