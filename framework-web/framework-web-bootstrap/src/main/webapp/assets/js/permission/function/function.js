(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.grid.js"], function () {
        var Fn = {
            options: function (fn) {
                return {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "function/query",
                                dataType: "json",
                                data: fn
                            }
                        },
                        serverPaging: true,
                        serverFilter: true,
                        pageSize: 5,
                        schema: {
                            data: function (response) {
                                if (response.data) {
                                    for (var i = 0; i < response.data.length; i++) {
                                        if (response.data[i]['createTime']) {
                                            response.data[i]['createTime'] = new Date(response.data[i]['createTime']);
                                        }
                                    }
                                }
                                return response.data;
                            },
                            total: function (response) {
                                return response.totalCount;
                            }
                        }
                    },
                    sortable: true,
                    selectable: 'multiple',
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
                        template: kendo.template($('#dialogTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'function/add'
                    }, {
                        template: kendo.template($('#modifyTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'function/toModify'
                    }, {
                        template: kendo.template($('#deleteTemplate').html()),
                        name: '<i class="icon-trash"></i> 删除'
                    }, {
                        template: kendo.template($('#queryFunctionTemplate').html())
                    }, {
                        template: kendo.template($('#importTemplate').html()),
                        name: '<i class="icon-edit" /> 导入',
                        url: BASEPATH + 'function/toImport'
                    }, {
                        template: kendo.template($('#exportTemplate').html()),
                        name: '<i class="icon-edit" /> 导出'
                    }],
                    columns: [{
                        field: "functionId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: functionId #" data-toggle="checkcolumn" class="function-checkbox-column" data-target="\\#function-check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".function-checkbox-column" id="function-check-all" />',
                        width: 30
                    }, {
                        field: "functionName",
                        title: "功能名称"
                    }, {
                        field: "directoryName",
                        title: "归属目录"
                    }, {
                        field: "createTime",
                        title: "创建时间",
                        format: "{0: yyyy-MM-dd HH:mm:ss}"
                    }, {
                        field: "remark",
                        title: "备注"
                    }]
                };
            },
            add: function () {
                var _this = $(this), _directory = Fn.getDirectory(), params = {};
                params['directoryCode'] = _directory.directoryCode;
                params['directoryName'] = _directory.directoryName;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            modify: function (e) {
                var ids = [];
                $('.function-checkbox-column:checked').each(function () {
                    ids.push(this.value);
                });

                if (ids.length === 0) {
                    e.stopPropagation();
                    alert("请选择要修改的记录！");

                    return false;
                } else if (ids.length != 1) {
                    alert("只能选择一条记录进行修改");
                    e.stopPropagation();
                    return false;
                }
                var _this = $(this), params = {};
                params['functionId'] = ids[0];
                params['directoryName'] = Fn.getDirectory().directoryName;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            delete: function () {
                var ids = [];
                $('.function-checkbox-column:checked').each(function () {
                    ids.push(this.value);
                });
                if (ids.length === 0) {
                    alert("请选择要删除的记录！");
                } else if (confirm("确定要删除选定的功能模块吗？")) {
                    $.post(BASEPATH + "function/delete", {ids: ids.join(',')}, function (data) {
                        if (data && data.errcode) {
                            alert(data.errmsg || "删除失败");
                        } else {
                            alert("删除功能模块成功")
                            $(document).trigger('page.fresh.functionGrid');
                        }
                    });
                }
            },
            import: function () {
                Fn.add.call(this);
            },
            export: function (e) {
                var id = $(e.currentTarget).attr("id"), params = {};
                params['directoryCode'] = Fn.getDirectory().directoryCode;
                if (id == "condition") {
                    var inputValue = $('#queryInput').val() || '';
                    if (!inputValue) {
                        alert("请输入查询条件！");
                        return;
                    }
                    params['params'] = inputValue;
                }
                window.location.href = $.utils.formatUrl(BASEPATH + "function/export", params);
            },
            getDirectory: function () {
                var _tree = $("#treeview").data("kendoTreeView"), _treeSelect;
                var directoryCode = '', directoryName = '';
                if (_tree && (_treeSelect = _tree.select()) && _treeSelect.length > 0) {
                    var _nodeData = _tree.dataItem(_treeSelect);
                    directoryCode = _nodeData.directoryCode;
                    directoryName = _nodeData.directoryName;
                }
                return {directoryCode: directoryCode, directoryName: directoryName};
            },
            bind: function (grid) {
                var $this = this;
                $(document).on('click', '#deleteBtn', $this.delete)
                    .on('click', '#searchBtn', function () {
                        $this.load();
                    }).on('page.fresh.functionGrid', function () {
                        $this.load();
                        $.utils.closePopup();
                    });
                $("#addBtn").on('click', $this.add);
                $("#modifyBtn").on('click', $this.modify);
                $("#importBtn").on('click', $this.import);

                $("#exportBtn").find("ul>li:not(.divider)").on('click', $this.export);
            },
            assemble: function (options) {
                var $this = this;
                return $.extend(true, {
                    directoryCode: Fn.getDirectory().directoryCode,
                    params: $('#queryInput').val() || '',
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
                    _grid = $('#functionDataTable').kendoGrid(this.options(this.assemble)).data("kendoGrid");

                _grid.dataSource.bind("change", function () {
                    if (this.total() == 0) {
                        $('#urlDataTable').urlGrid().load({functionId: ''});
                    }
                });
                _grid.bind("change", function () {
                    var _functionId = this.select().find('input[data-toggle="checkcolumn"]').val();
                    $('#urlDataTable').urlGrid().load({functionId: _functionId});
                }).bind("dataBound", function () {
                    _grid.select(_grid.tbody.find(">tr:not(.k-grouping-row)").eq(0));
                });

                $this.bind(_grid);
            }
        };

        $.fn.extend({
            functionGrid: function () {
                $.extend(this, Fn);
                return this;
            }
        });
    })
})(jQuery);