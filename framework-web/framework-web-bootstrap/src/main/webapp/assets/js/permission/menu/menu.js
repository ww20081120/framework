(function ($) {
    var _path = './assets/lib/kendoUI/js/',
        p_treeView = _path + 'kendo.treeview.js',
        p_dragAndDrop = _path + 'kendo.draganddrop.js',
        p_grid = _path + 'kendo.grid.js';

    var Fn = {
        tab: {
            init: function (tree) {
                $(".nav-tabs > li").click(function () {
                    $(".nav-tabs > li").removeClass("active");
                    $(this).addClass("active");
                    tree.dataSource.read();
                });
            },
            getActive: function () {
                var data = $(".nav-tabs > li.active").data();
                return {moduleCode: data.code, moduleName: data.name};
            }
        },
        action: {
            behind: function (tree) {
                var _select = tree.select(), _parent, params = Fn.tab.getActive();

                if (_select.length > 0 && (_parent = tree.parent(_select)).length > 0) {
                    var _nodeData = tree.dataItem(_parent);
                    params['parentResourceId'] = _nodeData.resourceId;
                    params['parentMenuName'] = _nodeData.menuName;
                }
                this.data("src", $.utils.formatUrl(this.data().src, params));
            },
            sub: function (tree) {
                var _select = tree.select(), params = Fn.tab.getActive();
                if (_select.length > 0) {
                    var _nodeData = tree.dataItem(_select);
                    params['parentResourceId'] = _nodeData.resourceId;
                    params['parentMenuName'] = _nodeData.menuName;
                }
                this.data("src", $.utils.formatUrl(this.data().src, params));
            },
            modify: function (tree, e) {
                var _select = tree.select(), params = {};
                if (_select.length <= 0) {
                    alert("请选择要修改的菜单！");
                    e.stopPropagation();
                    return false;
                }
                params['resourceId'] = tree.dataItem(_select).resourceId;
                var _parent = tree.parent(_select);
                if (_parent.length > 0) {
                    params['parentMenuName'] = tree.dataItem(_parent).menuName;
                }
                this.data("src", $.utils.formatUrl(this.data().src, params));
            },
            remove: function (tree) {
                var _src = this.data().src, _select = tree.select();
                if (_select.length <= 0) {
                    alert("请选择要删除的菜单！");
                } else if (confirm("确定要删除选定的菜单吗？")) {
                    $.post(_src, {
                        resourceId: tree.dataItem(_select).resourceId
                    }, function (data) {
                        if (data && data.errcode) {
                            alert(data.errmsg || "删除失败");
                        } else {
                            alert("删除菜单成功")
                            $(document).trigger('page.fresh.menuTree');
                        }
                    });
                }
            }
        },
        treeOptions: function (fn) {
            var options = {
                dataSource: {
                    transport: {
                        read: {
                            url: BASEPATH + "menu/list",
                            dataType: "json"
                        }
                    },
                    schema: {
                        model: {
                            children: "childrenMenu"
                        }
                    }
                },
                dataTextField: "menuName",
                dragAndDrop: true
            };
            if ($.isFunction(fn)) {
                options = $.extend(true, options, {
                    dataSource: {
                        transport: {
                            read: {
                                data: fn
                            }
                        }
                    }
                });
            }
            return options;
        },
        putMenuData: function (datas, data, pid, seq) {
            var menu = {
                "id": data.resourceId,
                "pid": pid,
                "seq": seq
            };
            datas.push(menu);
            if (data.childrenMenu) {
                menu.isLeaf = 'N';
                data.childrenMenu.forEach(function (d, i) {
                    Fn.putMenuData(datas, d, data.resourceId, i + 1);
                })
            } else {
                menu.isLeaf = 'Y';
            }
        },
        initTree: function (tree) {
            tree.bind("dragend", function () {
                var datas = [];
                tree.dataSource.data().forEach(function (d, i) {
                    Fn.putMenuData(datas, d, null, i + 1);
                });
                $.post(BASEPATH + 'menu/modify/seq', {
                    menus: JSON.stringify(datas)
                }, function (d) {
                    if (d && d.errcode) {
                        alert(d.errmsg);
                    }
                });
            }).bind("select", function (e) {
                var _node = tree.dataItem(e.node) || tree.dataItem(e);
                $('#menuBtnDataTable').menuBtnGrid().load({menuResourceId: _node ? _node.resourceId : ''});
            }).bind("dataBound", function () {
                var _firstNode = tree.element.find(".k-item:first");
                tree.select(_firstNode);
                tree.trigger("select", _firstNode);
            });


        }
    };
    require([p_treeView, p_dragAndDrop, p_grid], function () {
        var _tree = $("#treeview").kendoTreeView(Fn.treeOptions(Fn.tab.getActive)).data("kendoTreeView");

        Fn.initTree(_tree);

        Fn.tab.init(_tree);

        $(document).on('page.fresh.menuTree', function () {
            _tree.dataSource.read();
            $.utils.closePopup();
        });

        $(".col-md-3 button").on("click", function (e) {
            e.preventDefault();
            var _this = $(this), _id = _this.attr("id");
            Fn.action[_id].call(_this, _tree, e);
        });
    });
})(jQuery);


