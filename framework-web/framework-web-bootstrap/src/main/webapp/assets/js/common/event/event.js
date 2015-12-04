(function($) {
	require(
			[ "./assets/lib/kendoUI/js/kendo.grid.js" ],
			function() {
				var Fn = {
					grid : {
						dataSource : {
							transport : {
								read : {
									url : BASEPATH + "event/list",
									dataType : "json"
								}
							},
							serverPaging : true,
							pageSize : 10,
							sortable : true,
							pageable : true,
							schema : {
								data : function(response) {
									return response.data;
								},
								total : function(response) {
									return response.totalCount;
								}
							}
						},
						sortable : true,
						pageable : {
							refresh : true,
							pageSizes : true,
							buttonCount : 5,
							messages : {
								display : "显示{0}-{1}条，共{2}条",
								empty : "没有数据",
								page : "页",
								of : "/ {0}",
								itemsPerPage : "条/页",
								first : "第一页",
								previous : "前一页",
								next : "下一页",
								last : "最后一页",
								refresh : "刷新"
							}
						},
						toolbar : [
								{
									template : kendo.template($(
											'#addEventTemplate').html()),
									name : '<i class="icon-plus" /> 新增',
									url : BASEPATH + 'event/toAdd'
								},
								{
									template : kendo.template($(
											'#modifyEventTemplate').html()),
									name : '<i class="icon-edit" /> 修改',
									url : BASEPATH + 'event/toModify'
								},
								{
									template : kendo.template($(
											'#deleteEventTemplate').html())
								},
								{
									template : kendo.template($(
											'#importTemplate').html()),
									name : '<i class="icon-plus" /> 导入',
									url : BASEPATH + 'event/toImport'
								},
								{
									template : kendo.template($(
											'#exportTemplate').html())
								} ],
						columns : [
								{
									field : "eventId",
									sortable : false,
									template : '<input type="checkbox" value="#: eventId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
									width : 30
								}, {
									field : "eventName",
									title : "事件名称"
								}, {
									field : "eventTypeName",
									title : "事件类型"
								}, {
									field : "paramsName",
									title : "参数名"
								}, {
									field : "remark",
									title : "备注"
								} ]

					},
					eventDelete : function() {
						var ids = [];
						$('input[data-toggle="checkcolumn"]:checked').each(
								function() {
									ids.push(this.value);
								});
						if (ids.length === 0) {
							alert("请选择要删除的记录！");
						} else if (confirm("确定要删除选定的事件信息吗？")) {
							$.post(BASEPATH + "event/delete", {
								ids : ids.join(',')
							}, function(data) {
								if (data && data.errcode) {
									alert(data.errmsg || "删除失败");
								} else {
									alert("删除事件信息成功")
								}
							});
						}
					},
					modify : function() {
						var ids = [];
						$('input[data-toggle="checkcolumn"]:checked').each(
								function() {
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
						$("#modBtn").data("src",
								BASEPATH + "event/toModify?eventId=" + ids[0]);
					},
					excel : function() {
						window.location.href = $.utils.formatUrl(BASEPATH
								+ "event/export");
					},

					init : function() {
						var _this = this, grid = $('#eventDataTable')
								.kendoGrid(this.grid).data("kendoGrid");

						$("#modBtn").on('click', this.modify);
						$("#deleteEventBtn").on('click', this.eventDelete);
						$("#Btn").on('click', this.excel);
					}
				};
				$(Fn.init.bind(Fn))
			})
})(jQuery);