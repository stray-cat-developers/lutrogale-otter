<nav class="navbar navbar-static-top">
    <!-- Sidebar toggle button-->
    <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
        <span class="sr-only">Toggle navigation</span>
    </a>

    <div class="navbar-custom-menu">
        <ul id="admin-profile" style="display: none" class="nav navbar-nav">
            <!-- Notifications: style can be found in dropdown.less -->
            <li class="dropdown notifications-menu">
            <!-- Tasks: style can be found in dropdown.less -->
            <li class="dropdown user user-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                    <img src="" class="user-image" alt="User Image">
                    <span class="hidden-xs">dfdfdf</span>
                </a>
                <ul class="dropdown-menu">
                    <!-- User image -->
                    <li class="user-header">
                        <img src="" class="img-circle" alt="User Image">

                        <p>
                            <small></small>
                        </p>
                    </li>
                    <!-- Menu Footer-->
                    <li class="user-footer">
                        <div class="pull-left">
                            <a href="#" onclick="OsoriRoute.go('view.profile')" class="btn btn-default btn-flat">Profile</a>
                        </div>
                        <div class="pull-right">
                            <a href="#" onclick="OsoriRoute.go('logout');" class="btn btn-default btn-flat">Sign out</a>
                        </div>
                    </li>
                </ul>
            </li>

        </ul>
    </div>

    <script>
        AJAX.getData(OsoriRoute.getUri('admin.findOne')).done(function(data){
            $('#admin-profile').show();
            $('.user-menu img').attr('src',data.img);
            $('.user-menu span').text(data.email);
            $('.user-header p').html(data.name + '<small>' + data.description + '</small>');
        }).fail(function(){
            $('#admin-profile').hide();
        });
    </script>
</nav>
