(function ($) {
    require(
        ["./assets/lib/kendoUI/js/kendo.grid.js"],
        function () {
            var Fn = {
                grid: {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "messageTemplate/list",
                                dataType: "json"
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
                        template: kendo.template($('#addMessageTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'messageTemplate/addMT'
                    }, {
                        template: kendo.template($('#modifyMessageTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'messageTemplate/modMT'
                    }, {
                        template: kendo.template($('#deleteMessageTemplate').html())
                    },{
                        template: kendo.template($('#exportTemplate').html()),
                        name: '<i class="icon-download" /> 导出',
                        url: BASEPATH + 'messageTemplate/exportMessageTemplate'
                    },{
                        template: kendo.template($('#importMessageTemplate').html()),
                        name: '<i class="icon-upload" /> 导入',
                        url: BASEPATH + 'messageTemplate/toImportMessageTemplate'
                    }],
                    columns: [{
                        field: "messageTemplateId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: messageTemplateId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
                        width: 30
                    }, {
                        field: "messageTemplateCode",
                        title: "消息模版代码"
                    }, {
                        field: "name",
                        title: "名称"
                    }, {
                        field: "template",
                        title: "模版内容"
                    }],
                    excelExport: function(e) {
                        e.workbook.fileName = "Grid.xlsx";
                      }
                },
                messageTemplatDelete: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要删除的记录！");
                        return false;
                    } else if (confirm("确定要删除选定的信息模版吗？")) {
                        $.post(
                            BASEPATH + "messageTemplate/deleteMessageTemplate",
                            {
                                ids: ids.join(',')
                            },
                            function (data) {
                                if (data && data == "success") {
                                    alert("删除信息模版成功");
                                    $(document).trigger('page.fresh.messageTemplateGrid');
                                } else {
                                    alert("删除信息模版失败");
                                }
                            });
                    }
                },
                modify: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要修改的记录！");
                        return false;
                    }
                    if (ids.length != 1) {
                        alert("只能选一个！");
                        return false;
                    }
                    var src = $("#modBtn").data().src;
                    $("#modBtn").data("src", BASEPATH + "messageTemplate/modMT?messageTemplateId=" + ids[0]);
                },
                import : function(){
					var _this = $(this);
	                _this.data("src", $.utils.formatUrl(_this.data().src));
				},
				exportMessage: function(){
					var src = BASEPATH + "messageTemplate/exportMessageTemplate";
					window.location = src;
				},					
				bind: function (grid) {
	                var $this = this;
	                $(document).on('page.fresh.messageTemplateGrid', function () {
	                        $this.init();
	                    });
	            },
                init: function () {
                    var _this = this, grid = $('#messageTemplateDataTable').kendoGrid(this.grid).data("kendoGrid");
                    $("#modBtn").on('click', this.modify);
                    $("#deleteBtn").on('click',this.messageTemplatDelete);
                    $("#importBtn").on('click', this.import);
                    $("#exportBtn").on('click', this.exportMessage);
                    _this.bind(grid); 
                }
            };
            $(Fn.init.bind(Fn))
        })
})(jQuery);