var OsoriRoute = (function () {
    var map = new Map();

    //View Page
    map.set("dashBoard",    "/dashboard");
    map.set("logout",       "/logout");
    map.set("view.profile", "/view/profile");

    map.set("view.newProject",              "/view/new-project");
    map.set("view.newProject.navi",         "/view/new-project/{id}/navi");
    map.set("view.newProject.authGroup",    "/view/new-project/{id}/auth-groups");
    map.set("view.newProject.complete",     "/view/new-project/{id}/complete");

    map.set("view.management.newMember",                "/view/management/new-member");
    map.set("view.management.newMember.authorityGrant", "/view/management/new-member/{userId}/authority-grant");
    map.set("view.management.newMember.personalGrant",  "/view/management/new-member/{userId}/personal-grant");
    map.set("view.management.newMember.complete",       "/view/management/new-member/{userId}/complete");

    map.set("view.management.members",      "/view/management/members");

    map.set("view.project.configuration.navigation",    "/view/project/{id}/configuration/navigation");
    map.set("view.project.configuration.authority",     "/view/project/{id}/configuration/authority");
    map.set("view.project.configuration.members",       "/view/project/{id}/configuration/members");

    //Api
    map.set("admin.findOne",    "/v1/maintenance/management/admin");
    map.set("admin.modifyInfo", "/v1/maintenance/management/admin");

    map.set("user.create",                  "/v1/maintenance/management/user");
    map.set("user.modifyInfo",              "/v1/maintenance/management/user/{userId}");
    map.set("user.findOne",                 "/v1/maintenance/management/user/{userId}");
    map.set("user.findUsersProjects",       "/v1/maintenance/management/user/{userId}/projects");
    map.set("user.findUsersGrants",         "/v1/maintenance/management/user/{userId}/grants");
    map.set("user.findGrantsForUser",       "/v1/maintenance/management/user/{userId}/grant/project/{projectId}");
    map.set("user.assignAuthorityGrant",    "/v1/maintenance/management/user/{userId}/grant/project/{projectId}/authority-bundle/{authIdGroup}");
    map.set("user.withdrawAuthorityGrant",  "/v1/maintenance/management/user/{userId}/grant/project/{projectId}/authority-bundle/{authIdGroup}");
    map.set("user.assignPersonalGrant",     "/v1/maintenance/management/user/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIdGroup}");
    map.set("user.withdrawPersonalGrant",   "/v1/maintenance/management/user/{userId}/grant/project/{projectId}/authority-personal/{menuNaviIdGroup}");

    map.set("users.findAll",        "/v1/maintenance/management/users");
    map.set("users.modifyInfo",     "/v1/maintenance/management/users/{userIdGroup}");
    map.set("users.expireStatus",   "/v1/maintenance/management/users/{userIdGroup}");

    map.set("authority.findAll",                "/v1/maintenance/project/{id}/authority-bundles");
    map.set("authority.create",                 "/v1/maintenance/project/{id}/authority-bundle");
    map.set("authority.expire",                 "/v1/maintenance/project/{id}/authority-bundle/{authId}");
    map.set("authority.modifyInfo",             "/v1/maintenance/project/{id}/authority-bundle/{authId}");
    map.set("authority.findBundlesBranches",    "/v1/maintenance/project/{id}/authority-bundle/{authId}/branches");
    map.set("authority.findBundlesNavigations", "/v1/maintenance/project/{id}/authority-bundle/{authId}/navigations");
    map.set("authority.expireNavigations",      "/v1/maintenance/project/{id}/authority-bundle/{authId}/navigations/{menuNaviIdGroup}");

    map.set("menuTree.getAllBranch",    "/v1/maintenance/project/{id}/menu-tree/branches");
    map.set("menuTree.createBranch",    "/v1/maintenance/project/{id}/menu-tree/branch");
    map.set("menuTree.findBranch",      "/v1/maintenance/project/{id}/menu-tree/branch/{nodeId}");
    map.set("menuTree.moveBranch",      "/v1/maintenance/project/{id}/menu-tree/branch/{nodeId}");
    map.set("menuTree.deleteBranch",    "/v1/maintenance/project/{id}/menu-tree/branch/{nodeId}");

    map.set("navigation.modifyInfo",    "/project/{id}/navigation/{nodeId}");

    map.set("project.findAll",          "/v1/maintenance/projects");
    map.set("project.create",           "/v1/maintenance/project");
    map.set("project.findOne",          "/v1/maintenance/project/{id}");
    map.set("project.findUsersProject", "/v1/maintenance/project/{id}/users");
    map.set("project.findNavigationsProject", "/v1/maintenance/project/{id}/navigations");


    var findBy = function (name) {
        var routeUri = URI(map.get(name));

        if(_.isEmpty(routeUri)){
            alert("찾는 Route uri가 없습니다.");
            return;
        }
        return routeUri;
    };

    var getUri = function (name, pathVariable) {
        if(_.isEmpty(pathVariable))
            return findBy(name).path();
        else
            return URI.expand(findBy(name).path(), pathVariable).path();
    };

    var go = function (name, pathVariable, queryVariable) {
        if(_.isEmpty(pathVariable))
            pathVariable = [];

        if(_.isEmpty(queryVariable))
            location.href= getUri(name, pathVariable);
        else
            location.href= URI.expand(findBy(name).path(), pathVariable).setSearch(queryVariable);
    };

    var getQuery = function(){
        return URI(window.location.search).search(true);
    }

    return {
        getQuery : getQuery,
        findBy   : findBy,
        getUri   : getUri,
        go       : go
    };

}());
