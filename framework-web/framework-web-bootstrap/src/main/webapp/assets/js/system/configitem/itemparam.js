(function($) {
	require(
			[ "./assets/lib/kendoUI/js/kendo.grid.js"],
			function() {
				var Fn = {
					options : function(fn){
					  return{
						dataSource : {
							transport : {
								read : {
									url : BASEPATH + "config/queryItemParamPager",
									dataType : "json",
									data : fn
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
	                        template: kendo.template($('#addParamTemplate').html()),
	                        name: '<i class="icon-plus" /> 新增',
	                        url: BASEPATH + 'config/toAddConfigItemParam'
	                    }, {
	                        template: kendo.template($('#modifyParamTemplate').html()),
	                    }, {
	                        template: kendo.template($('#afterModifyParamTemplate').html()),
	                        url: BASEPATH + 'config/toModifyConfigItemParam'
	                    },{
	                        template: kendo.template($('#deleteParamTemplate').html())
	                    },  {
	                        template: kendo.template($('#importParamTemplate').html()),
	                        name: '<i class="icon-upload" /> 导入',
	                        url: BASEPATH + 'config/toImportConfigItemParam'
	                    },  {
	                        template: kendo.template($('#exportParamTemplate').html())
	                    }],
						columns : [
								{
									field : "configItemId",
									sortable : false,
									template : '<input type="checkbox" value="#: configItemId #" data-toggle="checkcolumn" class="item-checkbox-column" data-target="\\#item-check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".item-checkbox-column" id="item-check-all" />',
									width : 30
								}, 
								{
									field : "paramCode",
									title : "参数编码"
								},{
									field : "paramName",
									title : "参数名称"
								}, {
									field : "paramValue",
									title : "参数取值"
								} , {
									field : "defaultParamValue",
									title : "缺省值"
								}, {
									field : "dataType",
									title : "数据类型"
								}, {
									field : "inputType",
									title : "输入方式"
								}, {
									field : "valueScript",
									title : "取值校验规则"
								} , {
									field : "updateTime",
									title : "更新时间",
									format: "{0: yyyy-MM-dd HH:mm:ss}"
								} , {
									field : "remark",
									title : "备注"
								}]
					};
					},
					addParam: function () {
		                var _this = $(this),params = {};
		                var configItemId = Fn.getConfigItemId().configItemId;
		                params['configItemId'] = configItemId;
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					modifyParam: function (e) {
		                var configItemIds = [];
		                var paramCodes = [];
		                $('.item-checkbox-column:checked').each(function () {
		                	configItemIds.push(this.value);
		                	paramCodes.push(this.parentNode.nextSibling.textContent);
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
								BASEPATH + "config/checkHasParamValue",
								{configItemIds : configItemIds[0], paramCodes : paramCodes[0]},
								function(data) {
									if (data){
										alert("要修改的配置项参数下有参数值,请确认");
										return false;
									}
									else {
										
										$("#afterModifyParamBtn").click();
									}
								}
		                );
		            },
		            afterModifyParam:function(){
		            	var _this = $(this), params = {};
		            	var configItemIds = [];
		            	var paramCodes = [];
		            	$('.item-checkbox-column:checked').each(function () {
		            		configItemIds.push(this.value);
		            		paramCodes.push(this.parentNode.nextSibling.textContent);
		                });
		                params['configItemId'] = configItemIds[0];
		                params['paramCode'] = paramCodes[0];
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					deleteParam : function() {
						var configItemIds = [];
						var paramCodes = [];
						$('.item-checkbox-column:checked').each(function() {
									configItemIds.push(this.value);
									paramCodes.push(this.parentNode.nextSibling.textContent);
								});
						if (configItemIds.length === 0) {
							alert("请选择要删除的配置项参数！");
						} 
						else{
								$.post(
									BASEPATH + "config/checkHasParamValue",
									{configItemIds : configItemIds.join(','), paramCodes : paramCodes.join(',')},
									function(data) {
										if (data){
											alert("要删除的配置项参数有参数值,请确认");
											return false;
										}
										else {
											if (confirm("确定要删除选定的配置项参数吗？")) {
												$.post(
														BASEPATH + "config/deleteConfigItemParam",
														{
															configItemIds : configItemIds.join(','),
															paramCodes : paramCodes.join(',')
														},
														function(data) {
															if (data && data.errcode) {
																alert(data.errmsg || "删除失败");
															} else {
																alert("删除配置项参数成功");
																$(document).trigger('page.fresh.paramGrid');
															}
														});
											}
										}
									}
			                );
						}
					},
					importParam : function(){
						var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
					},
					exportParam : function(){
						var src = BASEPATH + "config/exportConfigItemParam";
						window.location = src;
					},
					bind: function (grid) {
		                var $this = this;
		                $(document).on('page.fresh.paramGrid', function () {
		                        $this.init();
		                        $.utils.closePopup();
		                    });
		                $("#addParamBtn").unbind().on('click', $this.addParam);
		                $("#modifyParamBtn").unbind().on('click', $this.modifyParam);
		                $("#afterModifyParamBtn").unbind().on('click', $this.afterModifyParam);
		                $("#deleteParamBtn").unbind().on('click', $this.deleteParam);
		                $("#importParamBtn").unbind().on('click', $this.importParam);
		                $("#exportParamBtn").unbind().on('click', $this.exportParam);
		            },
		            getConfigItemId: function () {
		                var _itemGrid = $('#itemTable').data("kendoGrid"), _select;
		                var configItemId = '';
		                if (_itemGrid && (_select = _itemGrid.select()) && _select.length > 0) {
		                	configItemId = _select.find('input.config-checkbox-column').val();
		                }
		                return {configItemId: configItemId};
		            },
		            assemble: function (options) {
		            	var params = $.extend(true, {
		            		configItemId: Fn.getConfigItemId().configItemId,
		                    page: 1
		                },options);
		                return params;
		            },
		            load: function (options) {
		                var grid = this.data("kendoGrid");
		                if (grid) {
		                    grid.dataSource.read(this.assemble(options));
		                    return;
		                }
		                this.init();
		            },
					init : function() {
						var $this = this,
	                    _grid = $('#paramTable').kendoGrid($this.options($this.assemble)).data("kendoGrid");
						_grid.dataSource.bind("change", function () {
		                    if(this.total() == 0){
		                        $('#paramValueTable').paramValueGrid().load({configItemId: '',paramCode: ''});
		                    }
		                });
		                _grid.bind("change", function () {
		                    var configItemId = this.select().find('input.item-checkbox-column').val();
		                    var paramCode = this.select().find('td:eq(1)').text();
		                    $('#paramValueTable').paramValueGrid().load({configItemId : configItemId, paramCode : paramCode});
		                }).bind("dataBound", function () {
		                    _grid.select(_grid.tbody.find("tr:eq(0)"));
		                });
		                $this.bind(_grid);
					}
				};
				$.fn.extend({
		        	paramGrid: function () {
		                $.extend(this, Fn);
		                return this;
		            }
		        });
			});
})(jQuery);