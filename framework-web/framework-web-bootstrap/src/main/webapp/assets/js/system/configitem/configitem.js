(function($) {
	require(
			[ "./assets/lib/kendoUI/js/kendo.grid.js"],
			function() {
				var Fn = {
					options : function(){
					  return{
						dataSource : {
							transport : {
								read : {
									url : BASEPATH + "config/queryConfigItemPager",
									dataType : "json"
								}
							},
							serverPaging : true,
							pageSize : 5,
							schema : {
								data : function(response) {
									if (response.data) {
	                                    for (var i = 0; i < response.data.length; i++) {
	                                        if (response.data[i]['updateTime']) {
	                                            response.data[i]['updateTime'] = new Date(response.data[i]['updateTime']);
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
						selectable : 'multiple',
						pageable : {
							refresh : true,
							pageSizes : true,
							buttonCount : 5,
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
	                        template: kendo.template($('#addItemTemplate').html()),
	                        name: '<i class="icon-plus" /> 新增',
	                        url: BASEPATH + 'config/toAddConfigItem'
	                    }, {
	                        template: kendo.template($('#modifyItemTemplate').html()),
	                    }, {
	                        template: kendo.template($('#afterModifyItemTemplate').html()),
	                        url: BASEPATH + 'config/toModifyConfigItem'
	                    },{
	                        template: kendo.template($('#deleteItemTemplate').html())
	                    },  {
	                        template: kendo.template($('#importItemTemplate').html()),
	                        name: '<i class="icon-upload" /> 导入',
	                        url: BASEPATH + 'config/toImportConfigItem'
	                    },  {
	                        template: kendo.template($('#exportItemTemplate').html())
	                    }],
						columns : [
								{
									field : "configItemId",
									sortable : false,
									template : '<input type="checkbox" value="#: configItemId #" data-toggle="checkcolumn" class="config-checkbox-column" data-target="\\#config-check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".config-checkbox-column" id="config-check-all" />',
									width : 30
								}, 
								{
									field : "directoryCode",
									title : "目录代码"
								},{
									field : "moduleCode",
									title : "业务模块代码"
								}, {
									field : "configItemCode",
									title : "配置项代码"
								} , {
									field : "configItemName",
									title : "配置项名称"
								}, {
									field : "isVisiable",
									title : "是否可见"
								}, {
									field : "updateTime",
									title : "更新时间",
									format: "{0: yyyy-MM-dd HH:mm:ss}"
								}, {
									field : "remark",
									title : "备注"
								}]
					};
					},
					addConfig: function () {
		                var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
		            },
					modifyConfig: function (e) {
		                var configItemIds = [];
		                $('.config-checkbox-column:checked').each(function () {
		                	configItemIds.push(this.value);
		                });

		                if (configItemIds.length === 0) {
		                    e.stopPropagation();
		                    alert("请选择要修改的记录！");

		                    return false;
		                } else if (configItemIds.length != 1) {
		                    alert("只能选择一条记录进行修改");
		                    e.stopPropagation();
		                    return false;
		                }
		                $.post(
								BASEPATH + "config/checkHasParam",
								{configItemIds : configItemIds[0]},
								function(data) {
									if (data){
										alert("要修改的配置项下有参数,请确认");
										return false;
									}
									else {
										
										$("#afterModifyItemBtn").click();
									}
								}
		                );
		            },
		            afterModifyConfig:function(){
		            	var _this = $(this), params = {};
		            	var configItemIds = [];
		            	$('.config-checkbox-column:checked').each(function () {
		            		configItemIds.push(this.value);
		                });
		                params['configItemId'] = configItemIds[0];
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					deleteConfig : function() {
						var configItemIds = [];
						$('.config-checkbox-column:checked').each(function() {
							configItemIds.push(this.value);
						});
						if (configItemIds.length === 0) {
							alert("请选择要删除的配置项！");
							return false;
						} 
						else{
								$.post(
									BASEPATH + "config/checkHasParam",
									{configItemIds : configItemIds.join(",")},
									function(data) {
										if (data){
											alert("要删除的配置项下有参数,请确认");
											return false;
										}
										else {
											if (confirm("确定要删除选定的配置项吗？")) {
												$.post(
														BASEPATH + "config/deleteConfigItem",
														{
															configItemIds : configItemIds.join(',')
														},
														function(data) {
															if (data && data.errcode) {
																alert(data.errmsg || "删除失败");
															} else {
																alert("删除配置项成功");
																$(document).trigger('page.fresh.itemGrid');
															}
														});
											}
										}
									}
			                );
						}
					},
					importConfig : function(){
						var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
					},
					exportConfig : function(){
						var src = BASEPATH + "config/exporConfigItem";
						window.location = src;
					},
					bind: function (grid) {
		                var $this = this;
		                $(document).on('page.fresh.itemGrid', function () {
		                        $this.init();
		                        $.utils.closePopup();
		                    });
		                $("#addItemBtn").unbind().on('click', $this.addConfig);
		                $("#modifyItemBtn").unbind().on('click', $this.modifyConfig);
		                $("#afterModifyItemBtn").unbind().on('click', $this.afterModifyConfig);
		                $("#deleteItemBtn").unbind().on('click', $this.deleteConfig);
		                $("#importItemBtn").unbind().on('click', $this.importConfig);
		                $("#exportItemBtn").unbind().on('click', $this.exportConfig);
		            },
					init : function() {
						var $this = this,
	                    _grid = $('#itemTable').kendoGrid(this.options()).data("kendoGrid");
						_grid.dataSource.bind("change", function () {
		                    if(this.total() == 0){
		                        $('#paramTable').paramGrid().load({configItemId: ''});
		                    }
		                });
		                _grid.bind("change", function () {
		                    var configItemId = this.select().find('input.config-checkbox-column').val();
		                    $('#paramTable').paramGrid().load({configItemId : configItemId});
		                }).bind("dataBound", function () {
		                    _grid.select(_grid.tbody.find("tr:eq(0)"));
		                });
		                $this.bind(_grid);
					}
				};
				$(Fn.init.bind(Fn));
			});
})(jQuery);