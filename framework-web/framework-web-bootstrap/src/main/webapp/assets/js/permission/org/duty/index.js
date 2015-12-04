(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.grid.js"], function () {
        var Fn = {
            options: function (fn) {
                return {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "duty/list",
                                dataType: "json",
                                type: "POST",
                                data: fn
                            }
                        },
                        serverPaging: true,
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
                        template: kendo.template($('#addDutyTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'duty/toAdd'
                    }, {
                        template: kendo.template($('#modifyDutyTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'duty/toModify'
                    }, {
                        template: kendo.template($('#deleteDutyTemplate').html())
                    }],
                    columns: [{
                        field: "dutyId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: dutyId #" data-toggle="checkcolumn" class="duty-checkbox-column" data-target="\\#duty-check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".duty-checkbox-column" id="duty-check-all" />',
                        width: 30
                    }, {
                        field: "dutyName",
                        title: "岗位名称"
                    }, {
                        field: "orgName",
                        title: "归属组织"
                    }, {
                        field: "operatorId",
                        title: "操作者"
                    }, {
                        field: "createTime",
                        title: "创建时间",
                        format: "{0: yyyy-MM-dd HH:mm:ss}"
                        //}, {
                        //    command: [{
                        //        template: kendo.template($('#modifyDutyTemplate').html()),
                        //        name: '<i class="icon-edit" /> 修改',
                        //        url: BASEPATH + 'duty/toModify'
                        //    }]
                    }]
                };
            },
            add: function () {
                var _this = $(this), _org = Fn.getOrg(), params = {};
                params['orgId'] = _org.orgId;
                params['orgName'] = _org.orgName;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            modify: function (e) {
                var ids = [];
                $('.duty-checkbox-column:checked').each(function () {
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
                params['dutyId'] = ids[0];
                params['orgName'] = Fn.getOrg().orgName;
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
            delete: function () {
                var ids = [];
                $('.duty-checkbox-column:checked').each(function () {
                    ids.push(this.value);
                });
                if (ids.length === 0) {
                    alert("请选择要删除的记录！");
                } else if (confirm("确定要删除选定的岗位吗？")) {
                    $.post(BASEPATH + "duty/remove", {ids: ids.join(',')}, function (data) {
                        if (data && data.errcode) {
                            alert(data.errmsg || "删除失败");
                        } else {
                            alert("删除岗位信息成功")
                            $(document).trigger('page.fresh.dutyGrid');
                        }
                    });
                }
            },
            getOrg: function () {
                var _tree = $("#treeview").data("kendoTreeView"), _treeSelect;
                var orgId = '', orgName = '';
                if (_tree && (_treeSelect = _tree.select()) && _treeSelect.length > 0) {
                    var _nodeData = _tree.dataItem(_treeSelect);
                    orgId = _nodeData.orgId;
                    orgName = _nodeData.text;
                }
                return {orgId: orgId, orgName: orgName};
            },
            assemble: function (options) {
                var $this = this;
                return $.extend(true, {
                    orgId: Fn.getOrg().orgId,
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
                    _grid = $('#dutyDataTable').kendoGrid(this.options(this.assemble)).data("kendoGrid");

                _grid.dataSource.bind("change", function () {
                    if(this.total() == 0){
                        $('#adminDataTable').adminGrid().load({dutyId: ''});
                    }
                });
                _grid.bind("dataBound", function () {
                    _grid.select(_grid.tbody.find(">tr:not(.k-grouping-row)").eq(0));
                }).bind("change", function () {
                    var _dutyId = this.select().find('input[data-toggle="checkcolumn"]').val();
                    $('#adminDataTable').adminGrid().load({dutyId: _dutyId});
                });

                $(document).on('page.fresh.dutyGrid', function () {
                    $this.load();
                    $.utils.closePopup();
                }).on('click', '#deleteDutyBtn', $this.delete);
                $('#addDutyBtn').on('click', this.add);
                $('#modifyDutyBtn').on('click', this.modify);
            }
        };
        $.fn.extend({
            dutyGrid: function () {
                $.extend(this, Fn);
                return this;
            }
        });
    })
})(jQuery);