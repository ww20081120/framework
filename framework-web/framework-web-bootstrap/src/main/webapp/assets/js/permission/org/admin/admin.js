(function ($) {
    require(
        ["./assets/lib/kendoUI/js/kendo.grid.js"],
        function () {
            var Fn = {
                options: function (fn) {
                    return {
                        dataSource: {
                            transport: {
                                read: {
                                    url: BASEPATH + "admin/query",
                                    dataType: "json",
                                    data: fn
                                }
                            },
                            serverPaging: true,
                            pageSize: 10,
                            schema: {
                                data: function (response) {
                                    if (response.data) {
                                        for (var i = 0; i < response.data.length; i++) {
                                            if (response.data[i]['createTime']) {
                                                response.data[i]['createTime'] = new Date(response.data[i]['createTime']);
                                            }
                                            var gender = response.data[i]['gener'];
                                            response.data[i]['gener'] = gender == 'M' ? '男' : gender == 'F' ? '女' : '保密';
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
                            template: kendo.template($('#addAdminTemplate').html()),
                            name: '<i class="icon-plus" /> 新增',
                            url: BASEPATH + 'admin/toAdd'
                        }, {
                            template: kendo.template($('#modifyAdminTemplate').html()),
                            name: '<i class="icon-edit" /> 修改',
                            url: BASEPATH + 'admin/toModAdmin'
                        }, {
                            template: kendo.template($('#deleteAdminTemplate').html())
                        }, {
                            template: kendo.template($('#modifyPwdTemplate').html()),
                            url: BASEPATH + 'admin/toModPassword'
                        }, {
                            template: kendo.template($('#queryAdminTemplate').html())
                        }],
                        columns: [{
                            field: "adminId",
                            sortable: false,
                            template: '<input type="checkbox" value="#: adminId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
                            headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
                            width: 30
                        }, {
                            field: "adminName",
                            title: "管理员名称"
                        }, {
                            field: "gener",
                            title: "性别"
                        }, {
                            field: "email",
                            title: "电子邮件"
                        }, {
                            field: "phone",
                            title: "电话"
                        }, {
                            field: "createTime",
                            title: "创建时间",
                            format: "{0: yyyy-MM-dd HH:mm:ss}"
                        }]
                    };
                },
                add: function () {
                    var _this = $(this), params = {};
                    params['dutyId'] = Fn.getDuty().dutyId;
                    _this.data("src", $.utils.formatUrl(_this.data().src, params));
                },
                modify: function (e) {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要修改的记录！");
                        return false;
                    }
                    if (ids.length != 1) {
                        e.stopPropagation();
                        alert("只能选择一条记录进行修改！");
                        return false;
                    }
                    var _this = $(this), params = {};
                    params['adminId'] = ids[0];
                    _this.data("src", $.utils.formatUrl(_this.data().src, params));
                },
                modPwdAdmin: function (e) {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        e.stopPropagation();
                        alert("请选择要重置密码的管理员！");
                        return false;
                    }
                    if (ids.length != 1) {
                        e.stopPropagation();
                        alert("只能选择一条记录进行修改！");
                        return false;
                    }
                    var _this = $(this), params = {};
                    params['adminId'] = ids[0];
                    _this.data("src", $.utils.formatUrl(_this.data().src, params));
                },
                adminDelete: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要删除的记录！");
                    } else if (confirm("确定要删除选定的管理员吗？")) {
                        $.post(BASEPATH + "admin/deladmin", {
                            ids: ids.join(',')
                        }, function (data) {
                            if (data && data.errcode) {
                                alert(data.errmsg || "删除失败");
                            } else {
                                alert("删除管理员信息成功")
                                $(document).trigger('page.fresh.adminGrid');
                            }
                        });
                    }
                },
                getDuty: function () {
                    var _dutyGrid = $('#dutyDataTable').data("kendoGrid"), _select;
                    var dutyId = '', dutyName = '';
                    if (_dutyGrid && (_select = _dutyGrid.select()) && _select.length > 0) {
                        dutyId = _select.find('input[data-toggle="checkcolumn"]').val();
                        dutyName = '';
                    }
                    return {dutyId: dutyId, dutyName: dutyName};
                },
                assemble: function (options) {
                    var $this = this;
                    return $.extend(true, {
                        dutyId: Fn.getDuty().dutyId,
                        Str: $('#queryInput').val(),
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
                    var _this = this, grid = $('#adminDataTable').kendoGrid(this.options(this.assemble)).data("kendoGrid");

                    $(document).on('page.fresh.adminGrid', function () {
                        _this.load();
                        $.utils.closePopup();
                    }).on('click', '#searchBtn', function () {
                        _this.load();
                    }).on('click', '#deleteAdminBtn', this.adminDelete);
                    $("#addAdminBtn").on('click', this.add);
                    $("#modBtn").on('click', this.modify);
                    $("#modPwdBtn").on('click', this.modPwdAdmin);
                }
            };
            $.fn.extend({
                adminGrid: function () {
                    $.extend(this, Fn);
                    return this;
                }
            });
        })
})(jQuery);