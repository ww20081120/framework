(function ($) {
    require(
        ["./assets/lib/kendoUI/js/kendo.grid.js"],
        function () {
            var Fn = {
                grid: {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "announcement/list",
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
                        template: kendo.template($('#addAnnouncement').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'announcement/toAnnouncementManager'
                    }, {
                        template: kendo.template($('#modifyAnnouncement').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'announcement/toAnnouncementManager'
                    }, {
                        template: kendo.template($('#deleteAnnouncement').html())
                    },{
                        template: kendo.template($('#exportAnnouncement').html()),
                        name: '<i class="icon-download" /> 导出',
                        url: BASEPATH + 'announcement/exportAnnouncement'
                    },{
                        template: kendo.template($('#importAnnouncement').html()),
                        name: '<i class="icon-upload" /> 导入',
                        url: BASEPATH + 'announcement/toImportAnnouncement'
                    },{
                        template: kendo.template($('#auditAnnouncement').html()),
                        name: '<i class="icon-edit" /> 审核',
                        url: BASEPATH + 'announcement/toAnnouncementAudit'
                    }],
                    columns: [{
                        field: "announcementId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: announcementId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
                        width: 30
                    }, {
                        field: "title",
                        title: "标题"
                    }, {
                        field: "content",
                        title: "公告内容"
                    }, {
                        field: "state",
                        title: "状态"
                    }, {
                        field: "comments",
                        title: "备注"
                    }],
                    excelExport: function(e) {
                        e.workbook.fileName = "Grid.xlsx";
                      }
                },
                announcementDelete: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要删除的记录！");
                        return false;
                    } else if (confirm("确定要删除选定的任务信息吗？")) {
                        $.post(
                            BASEPATH + "announcement/deleteAnnouncement",
                            {
                                ids: ids.join(',')
                            },
                            function (data) {
                                if (data && data == "success") {
                                    alert("删除公告成功");
                                    $(document).trigger('page.fresh.announcementGrid');
                                } else {
                                    alert("删除公告失败");
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
                    $("#modBtn").data("src", BASEPATH + "announcement/toAnnouncementManager?announcementId=" + ids[0]);
                    
                },
                audit: function () {
                    var ids = [];
                    $('input[data-toggle="checkcolumn"]:checked').each(function () {
                        ids.push(this.value);
                    });
                    if (ids.length === 0) {
                        alert("请选择要审批的记录！");
                        return false;
                    }
                    var src = $("#auditBtn").data().src;
                    ids = ids.join(',');
                    $("#auditBtn").data("src", BASEPATH + "announcement/toAnnouncementAudit?ids=" + ids);
                    
                },
                import : function(){
					var _this = $(this);
	                _this.data("src", $.utils.formatUrl(_this.data().src));
				},
				exportDetail:function(){
                	var src = BASEPATH + "announcement/exportAnnouncement";
					window.location = src;
                },
                bind: function (grid) {
	                var $this = this;
	                $(document).on('page.fresh.announcementGrid', function () {
	                        $this.init();
	                    });
	            },
                init: function () {
                    var _this = this, grid = $('#announcementDataTable').kendoGrid(this.grid).data("kendoGrid");
                    
                    $("#modBtn").on('click', this.modify);
                    $("#deleteBtn").on('click',this.announcementDelete);
                    $("#importBtn").on('click', this.import);
                    $("#exportBtn").on('click', this.exportDetail);
                    $("#auditBtn").on('click', this.audit);
                    _this.bind(grid);
                }
            };
            $(Fn.init.bind(Fn))
        })
})(jQuery);