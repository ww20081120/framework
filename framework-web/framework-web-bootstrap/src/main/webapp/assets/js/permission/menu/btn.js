(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.grid.js"], function () {
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
            options: function (fn) {
                return {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "menu/listButton",
                                dataType: "json",
                                type: "POST",
                                data: fn
                            }
                        },
                        serverPaging: true,
                        pageSize: 10,
                        schema: {
                            data: function (response) {
                                return response.data;
                            },
                            total: function (response) {
                                return response.totalCount;
                            }
                        }
                    },
                    sortable: true,
                    //selectable: 'multiple',
                    pageable: {
                        refresh: true,
                        pageSizes: true,
                        buttonCount: 5,
                        messages: {
                            display: "显示{0}-{1}条，共{2}条",
                            empty: "没有数据",
                            page: "页",
                            of: "/ {0}",
                            itemsPerPage: "条/页",
                            first: "第一页",
                            previous: "前一页",
                            next: "下一页",
                            last: "最后一页",
                            refresh: "刷新"
                        }
                    },
                    toolbar: [{
                        template: kendo.template($('#addBtnTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'menu/toAddBtn'
                    }, {
                        template: kendo.template($('#modifyBtnTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'menu/toModifyBtn'
                    }, {
                        template: kendo.template($('#deleteBtnTemplate').html())
                    }],
                    columns: [{
                        field: "resourceId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: resourceId #" data-toggle="checkcolumn" class="checkbox-column" data-target="\\#check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".checkbox-column" id="check-all" />',
                        width: 30
                    }, {
                        field: "menuName",
                        title: "菜单按钮名称"
                    }, {
                        field: "parentMenuName",
                        title: "归属菜单"
                    }, {
                        field: "moduleName",
                        title: "归属模块"
                    }, {
                        field: "functionName",
                        title: "关联功能"
                    }, {
                        field: "seq",
                        title: "顺序"
                    }]
                };
            },
            add: function () {
                var _this = $(this), _menu = Fn.getMenu(), params = Fn.tab.getActive();
                params['parentResourceId'] = _menu.resourceId;
                params['parentMenuName'] = _menu.menuName;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            modify: function (e) {
                var ids = [];
                $('.checkbox-column:checked').each(function () {
                    ids.push(this.value);
                });

                if (ids.length === 0) {
                    e.stopPropagation();
                    alert("请选择要修改的记录！");
                    return false;
                } else if (ids.length != 1) {
                    e.stopPropagation();
                    alert("只能选择一条记录进行修改！");
                    return false;
                }
                var _this = $(this), params = {};
                params['resourceId'] = ids[0];
                params['menuName'] = Fn.getMenu().menuName;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            delete: function () {
                var ids = [];
                $('.checkbox-column:checked').each(function () {
                    ids.push(this.value);
                });
                if (ids.length === 0) {
                    alert("请选择要删除的记录！");
                } else if (confirm("确定要删除选定的菜单按钮吗？")) {
                    $.post(BASEPATH + "menu/remove", {resourceId: ids.join(',')}, function (data) {
                        if (data && data.errcode) {
                            alert(data.errmsg || "删除失败");
                        } else {
                            alert("删除菜单按钮信息成功")
                            $(document).trigger('page.fresh.menuBtnGrid');
                        }
                    });
                }
            },
            getMenu: function () {
                var _tree = $("#treeview").data("kendoTreeView"), _treeSelect;
                var resourceId = '', menuName = '';
                if (_tree && (_treeSelect = _tree.select()) && _treeSelect.length > 0) {
                    var _nodeData = _tree.dataItem(_treeSelect);
                    resourceId = _nodeData.resourceId;
                    menuName = _nodeData.menuName;
                }
                return {resourceId: resourceId, menuName: menuName};
            },
            assemble: function (options) {
                var $this = this;
                return $.extend(true, {
                    moduleCode: Fn.tab.getActive().moduleCode,
                    menuResourceId: Fn.getMenu().resourceId,
                    page: 1
                }, options);
            },
            load: function (options) {
                var grid = this.data("kendoGrid");
                if (grid) {
                    grid.dataSource.read(this.assemble(options));
                    return;
                }
                this.init();
            },
            init: function () {
                var $this = this,
                    _grid = $('#menuBtnDataTable').kendoGrid(this.options(this.assemble)).data("kendoGrid");
                $(document).on('page.fresh.menuBtnGrid', function () {
                    $this.load();
                    $.utils.closePopup();
                }).on('click', '#deleteBtn', $this.delete);
                $('#addBtn').on('click', $this.add);
                $('#modifyBtn').on('click', $this.modify);
            }
        };
        $.fn.extend({
            menuBtnGrid: function () {
                $.extend(this, Fn);
                return this;
            }
        });
    })
})(jQuery);