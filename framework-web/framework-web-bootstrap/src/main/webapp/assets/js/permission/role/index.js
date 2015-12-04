(function($) {
	require(
			[ "./assets/lib/kendoUI/js/kendo.grid.js" ],
			function() {
				var Fn = {
					grid : {
						dataSource : {
							transport : {
								read : {
									url : BASEPATH + "role/query",
									dataType : "json"
								}
							},
							serverPaging : true,
							pageSize : 10,
							schema : {
								data : function(response) {
									if (response.data) {
										for (var i = 0; i < response.data.length; i++) {
											if (response.data[i]['createTime']) {
												response.data[i]['createTime'] = new Date(
														response.data[i]['createTime']);
											}
										}
									}
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
							buttonCount : 5
						},
						toolbar : [
								{
									template : '<button class="btn btn-default" data-title="新增角色" data-src="'
											+ BASEPATH
											+ 'role/add" data-toggle="modal" data-target="\\#optionDialog"><i class="icon-plus" /> 新增</button>'
								},
								{
									template : '<button class="btn btn-default" id="deleteBtn"><i class="icon-trash"></i> 删除角色</button>'
								} ],
						columns : [
								{
									field : "roleId",
									sortable : false,
									template : '<input type="checkbox" value="#: roleId #" data-toggle="checkcolumn" class="checkboxcolumn" data-target="\\#check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".checkboxcolumn" id="check-all" />',
									width : 30
								}, {
									field : "roleName",
									title : "角色名称"
								}, {
									field : "moduleName",
									title : "模块代码"
								}, {
									field : "createTime",
									title : "创建时间",
									format : "{0: yyyy-MM-dd HH:mm:ss}"
								},
								{
									title : "操作",
									template : '<button class="btn btn-default" data-title="修改角色" data-src="'
											+ BASEPATH
											+ 'role/modify/#: roleId #" data-toggle="modal" data-target="\\#optionDialog"><i class="icon-edit" /> 修改</button>'
								} ]
					},
					deleteRole : function() {
						var ids = [];
						$('input[data-toggle="checkcolumn"]:checked').each(
								function() {
									ids.push(this.value);
								});
						if (ids.length === 0) {
							alert("请选择要删除的记录！");
						} else if (confirm("确定要删除选定的角色吗？")) {
							$
									.post(
											BASEPATH + "role/delete",
											{
												ids : ids.join(',')
											},
											function(data) {
												if (data && data.errcode) {
													alert(data.errmsg || "删除失败");
												} else {
													alert("删除角色信息成功")
													$(document).trigger('page.fresh');
												}
											});
						}
					},
					init : function() {
						var grid = $('#dataTable').kendoGrid(this.grid).data(
								"kendoGrid");
						$(document).on('page.fresh', function() {
							grid.dataSource.read();
							window.setTimeout(function(){
								$('#optionDialog').data('bs.modal').hide();
							}, 1000);
						}).on('click', '#deleteBtn', this.deleteRole);
					}
				};
				$(Fn.init.bind(Fn))
			})
})(jQuery);