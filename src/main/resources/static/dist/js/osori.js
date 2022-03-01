var AJAX = (function() {

    var ajax_func = function(url, method, data, opt){
        var default_opt = {
            url: url,
            method: method,
            data: data,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            async: true
        };

        $.extend(default_opt, opt);

        return $.ajax(default_opt).fail(function(xhr, status){
            console.log(xhr);
            console.log(status);
        });

    }

    var getData = function(url, data, opt){
        return ajax_func(url, 'GET', data, opt);
    }

    var postData = function(url, data, opt){
        return ajax_func(url, 'POST', JSON.stringify(data), opt);
    }

    var putData = function(url, data, opt){
        return ajax_func(url, 'PUT', JSON.stringify(data), opt);
    }

    var deleteData = function(url, data, opt){
        return ajax_func(url, 'DELETE', data, opt);
    }

    return {
        getData    : getData,
        postData   : postData,
        putData    : putData,
        deleteData : deleteData
    };

}());

var OPTION = (function() {
    var DEFAULT = {
        'jstree': {
            'plugins': ['contextmenu', 'dnd', 'changed', 'crrm', 'sort', 'types'],
            'core': {
                'check_callback': true,
                'data': [],
                'themes': {
                    'name': 'proton',
                    'responsive': true
                }
            },
            'types': {
                'category': {
                    'icon': 'glyphicon glyphicon-th-large'
                },
                'menu': {
                    'icon': 'glyphicon glyphicon-th-list'
                },
                'function': {
                    'icon': 'glyphicon glyphicon-cog'
                }
            },
            'contextmenu': {
                'items': {
                    'create': {
                        'separator_before': false,
                        'separator_after': true,
                        '_disabled': false,
                        'label': '메뉴생성',
                        'action': function (data) {
                            var inst = $.jstree.reference(data.reference),
                                obj = inst.get_node(data.reference);

                            inst.create_node(obj, {}, 'last', function (new_node) {
                                setTimeout(function () {
                                    inst.edit(new_node);
                                }, 0);
                            });
                        }
                    },
                    'remove': {
                        'separator_before': false,
                        'icon': false,
                        'separator_after': false,
                        '_disabled': false,
                        'label': '제거',
                        'action': function (data) {
                            var inst = $.jstree.reference(data.reference),
                                obj = inst.get_node(data.reference);
                            if (confirm('[' + obj.text + ']을 삭제하시겠습니까?')) {
                                AJAX.deleteData(
                                    OsoriRoute.getUri('menuTree.deleteBranch', {id: SS.project_id, nodeId: obj.a_attr.id})
                                ).done(function(result){
                                    if (inst.is_selected(obj)) {
                                        inst.delete_node(inst.get_selected());
                                    }else {
                                        inst.delete_node(obj);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        },
        'data_table': {
            'data': [],
            'columns': [],
            'responsive': true,
            'destroy': true,
            'select': true,
            'scrollY': '400px',
            'scrollCollapse': true,
            'paging': false,
            'searching': false,
            'ordering': true,
            'autoWidth': false,
            'columnDefs': [],
            'language': {
                'url': '//cdn.datatables.net/plug-ins/1.10.12/i18n/Korean.json'
            }
        }
    };

    var jstree = function(opt, data){
        var obj = _.clone(DEFAULT.jstree);
        obj.core.data = data;
        $.extend(obj, opt);

        return obj;
    };

    var data_table = function(opt, data){
        var obj = _.clone(DEFAULT.data_table);
        obj.data = data;
        $.extend(obj, opt);

        return obj;
    };

    return {
        jstree     : jstree,
        data_table : data_table
    };

}());

//Html5 SessionStorage 단축 변수
var SS = (function(){
    return window.sessionStorage;
}());

var extractByWord = function(word){
    var reg_exp = new RegExp(word+'/[0-9]*');
    var result = reg_exp.exec(window.location.pathname);
    if(_.isEmpty(result))
        return null;
    else
        return result[0].split('/')[1];
};
