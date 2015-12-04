(function ($) {
    require(
        ["./assets/lib/kendoUI/js/kendo.grid.js"],
        function () {
            var Fn = {
                grid: {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "task/list",
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
                        template: kendo.template($('#addTask').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'task/toTaskManager'
                    }, {
                        template: kendo.template($('#modifyTask').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'task/toTaskManager'
                    }, {
                        template: kendo.template($('#deleteTask').html())
                    },{
                        template: kendo.template($('#exportTask').html()),
                        name: '<i class="icon-download" /> 导出',
                        url: BASEPATH + 'task/exportTask'
                    },{
                        template: kendo.template($('#importTask').html()),
                        name: '<i class="icon-upload" /> 导入',
                        url: BASEPATH + 'task/toImportTask'
                    },{
                        template: kendo.template($('#playTask').html()),
                        name: '<i class="icon-play" /> 运行',
                        url: BASEPATH + 'task/playTask'
                    },{
                        template: kendo.template($('#pauseTask').html()),
                        name: '<i class="icon-pause" /> 暂停',
                        url: BASEPATH + 'task/pauseTask'
                    }],
                    columns: [{
                        field: "taskId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: taskId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
                        width: 30
                    }, {
                        field: "taskName",
                        title: "任务名称"
                    }, {
                        field: "className",
                        title: "执行类名"
                    }, {
                        field: "method",
                        title: "方法名"
                    }, {
                        field: "moduleCode",
                        title: "业务模块代码"
                    }, {
                        field: "priority",
                        title: "优先级"
                    }, {
                        field: "isConcurrent",
                        title: "是否并发"
                    }, {
                        field: "taskState",
                        title: "任务状态"
                    }],
                    excelExport: function(e) {
                        e.workbook.fileName = "Grid.xlsx";
                      }
                },
                taskDelete: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要删除的记录！");
                        return false;
                    } else if (confirm("确定要删除选定的任务信息吗？")) {
                        $.post(
                            BASEPATH + "task/deleteTask",
                            {
                                ids: ids.join(',')
                            },
                            function (data) {
                                if (data && data == "success") {
                                    alert("删除任务信息成功");
                                    $(document).trigger('page.fresh.taskGrid');
                                } else {
                                    alert("删除任务信息失败");
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
                    $("#modBtn").data("src", BASEPATH + "task/toTaskManager?taskId=" + ids[0]);
                    
                },
                import : function(){
					var _this = $(this);
	                _this.data("src", $.utils.formatUrl(_this.data().src));
				},
                exportTaskDetail:function(){
                	var src = BASEPATH + "task/exportTask";
					window.location = src;
                },
                playTask: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要运行的任务！");
                        return false;
                    }
                    ids=ids.join(',');
                    var src = $("#playTaskBtn").data().src;
                    $("#playTaskBtn").data("src", BASEPATH + "task/playTask?ids=" + ids);                 
                },
                bind: function (grid) {
	                var $this = this;
	                $(document).on('page.fresh.taskGrid', function () {
	                        $this.init();
	                    });
	            },
	            pauseTask: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要运行的任务！");
                        return false;
                    }
                    ids=ids.join(',');
                    var src = $("#pauseTaskBtn").data().src;
                    $("#pauseTaskBtn").data("src", BASEPATH + "task/pauseTask?ids=" + ids);                 
                },
                bind: function (grid) {
	                var $this = this;
	                $(document).on('page.fresh.taskGrid', function () {
	                        $this.init();
	                    });
	            },
                init: function () {
                    var _this = this, grid = $('#taskDataTable').kendoGrid(this.grid).data("kendoGrid");
                    
                    $("#modBtn").on('click', this.modify);
                    $("#deleteBtn").on('click',this.taskDelete);
                    $("#importBtn").on('click', this.import);
                    $("#exportBtn").on('click', this.exportTaskDetail);
                    $("#playTaskBtn").on('click', this.playTask);//
                    $("#pauseTaskBtn").on('click', this.pauseTask);
                    _this.bind(grid);
                }
            };
            $(Fn.init.bind(Fn))
        })
})(jQuery);