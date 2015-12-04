(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.grid.js"], function () {
        var Fn = {
            options: function (fn) {
                return {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "url/query",
                                dataType: "json",
                                data: fn
                            }
                        },
                        serverPaging: true,
                        pageSize: 5,
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
                        template: kendo.template($('#addUrlTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'url/toAdd'
                    }, {
                        template: kendo.template($('#modifyUrlTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'url/toModify'
                    }, {
                        template: kendo.template($('#deleteUrlTemplate').html())
                    }],
                    columns: [{
                        field: "resourceId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: resourceId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
                        width: 30
                    }, {
                        field: "resourceName",
                        title: "URL资源名称"
                    }, {
                        field: "url",
                        title: "地址",
                        width: '40%'
                    }, {
                        field: "method",
                        title: "方法"
                    }, {
                        field: "eventId",
                        title: "事件"
                    }]
                };
            },
            add: function () {
                var _this = $(this), params = {};
                params['functionId'] = Fn.getFunction().functionId;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            modify: function (e) {
                var ids = [];
                $('input[data-toggle="checkcolumn"]:checked').each(function () {
                    ids.push(this.value);
                });
                if (ids.length === 0) {
                    alert("请选择要修改的记录！");
                    e.stopPropagation();
                    return false;
                } else if (ids.length != 1) {
                    alert("只能选一个！");
                    e.stopPropagation();
                    return false;
                }
                var _this = $(this), params = {};
                params['resourceId'] = ids[0];
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            delete: function () {
                var ids = [];
                $('input[data-toggle="checkcolumn"]:checked').each(function () {
                    ids.push(this.value);
                });
                if (ids.length === 0) {
                    alert("请选择要删除的记录！");
                } else if (confirm("确定要删除选定的URL资源吗？")) {
                    $.post(BASEPATH + "url/delete", {
                        ids: ids.join(',')
                    }, function (data) {
                        if (data && data.errcode) {
                            alert(data.errmsg || "删除失败");
                        } else {
                            alert("删除URL资源成功")
                            $(document).trigger('page.fresh.urlGrid');
                        }
                    });
                }
            },
            getFunction: function () {
                var _functionGrid = $('#functionDataTable').data("kendoGrid"), _select;
                var functionId = '';
                if (_functionGrid && (_select = _functionGrid.select()) && _select.length > 0) {
                    functionId = _select.find('input[data-toggle="checkcolumn"]').val();
                }
                return {functionId: functionId};
            },
            assemble: function (options) {
                return $.extend(true, {
                    functionId: Fn.getFunction().functionId,
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
                var _this = this, grid = $('#urlDataTable').kendoGrid(_this.options(_this.assemble)).data("kendoGrid");
                $(document).on('page.fresh.urlGrid', function () {
                    _this.load();
                    $.utils.closePopup();
                }).on('click', '#deleteUrlBtn', _this.delete);
                $("#modifyUrlBtn").on('click', _this.modify);
                $("#addUrlBtn").on('click', _this.add);
            }
        };
        $.fn.extend({
            urlGrid: function () {
                $.extend(this, Fn);
                return this;
            }
        });
    })
})(jQuery);