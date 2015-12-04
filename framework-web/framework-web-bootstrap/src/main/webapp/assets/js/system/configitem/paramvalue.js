(function($) {
	require(
			[ "./assets/lib/kendoUI/js/kendo.grid.js" ],
			function() {
				var Fn = {
					options : function(fn){
					  return{
						dataSource : {
							transport : {
								read : {
									url : BASEPATH + "config/queryParamValuePager",
									dataType : "json",
									data : fn
								}
							},
							serverPaging : true,
							pageSize : 5,
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
	                        template: kendo.template($('#addParamValueTemplate').html()),
	                        name: '<i class="icon-plus" /> 新增',
	                        url: BASEPATH + 'config/toAddConfigItemParamValue'
	                    }, {
	                        template: kendo.template($('#modifyParamValueTemplate').html()),
	                        name: '<i class="icon-edit" /> 修改',
	                        url: BASEPATH + 'config/toModifyConfigItemParamValue'
	                    },{
	                        template: kendo.template($('#deleteParamValueTemplate').html())
	                    },  {
	                        template: kendo.template($('#importParamValueTemplate').html()),
	                        name: '<i class="icon-upload" /> 导入',
	                        url: BASEPATH + 'config/toImportConfigItemParamValue'
	                    },  {
	                        template: kendo.template($('#exportParamValueTemplate').html())
	                    }],
						columns : [
								{
									field : "paramValueId",
									sortable : false,
									template : '<input type="checkbox" value="#: paramValueId #" data-toggle="checkcolumn" class="paramvalue-checkbox-column" data-target="\\#paramvalue-check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".paramvalue-checkbox-column" id="paramvalue-check-all" />',
									width : 30
								}, 
								{
									field : "valueMark",
									title : "取值说明"
								}, {
									field : "value",
									title : "取值"
								} , {
									field : "remark",
									title : "备注"
								}]
					};
					},
					addParamValue: function () {
		                var _this = $(this), params = {};
		                params['configItemId'] = Fn.getConfigItemIdAndParamCode().configItemId;
		                params['paramCode'] = Fn.getConfigItemIdAndParamCode().paramCode;
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					modifyParamValue: function (e) {
		                var paramValueIds = [];
		                $('.paramvalue-checkbox-column:checked').each(function () {
		                	paramValueIds.push(this.value);
		                });

		                if (paramValueIds.length === 0) {
		                    e.stopPropagation();
		                    alert("请选择要修改的记录！");

		                    return false;
		                } else if (paramValueIds.length != 1) {
		                    alert("只能选择一条记录进行修改");
		                    e.stopPropagation();
		                    return false;
		                }
		                var _this = $(this), params = {};
		                params['paramValueId'] = paramValueIds[0];
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					deleteParamValue : function() {
						var paramValueIds = [];
						$('.paramvalue-checkbox-column:checked').each(
								function() {
									paramValueIds.push(this.value);
								});
						if (paramValueIds.length === 0) {
							alert("请选择要删除的参数值！");
						} 
						else{
							if (confirm("确定要删除选定的参数值吗？")) {
								$.post(
										BASEPATH + "config/deleteConfigItemParamValue",
										{
											paramValueIds : paramValueIds.join(',')
										},
										function(data) {
											if (data && data.errcode) {
												alert(data.errmsg || "删除失败");
											} else {
												alert("删除参数值成功");
												$(document).trigger('page.fresh.paramValueGrid');
											}
										});
							}
						}
					},
					importParamValue : function(){
						var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
					},
					exportParamValue : function(){
						var src = BASEPATH + "config/exportConfigItemParamValue";
						window.location = src;
					},
					bind: function (grid) {
		                var $this = this;
		                $(document).on('page.fresh.paramValueGrid', function () {
		                        $this.init();
		                        $.utils.closePopup();
		                    });
		                $("#addParamValueBtn").unbind().on('click', $this.addParamValue);
		                $("#modifyParamValueBtn").unbind().on('click', $this.modifyParamValue);
		                $("#deleteParamValueBtn").unbind().on('click', $this.deleteParamValue);
		                $("#importParamValueBtn").unbind().on('click', $this.importParamValue);
		                $("#exportParamValueBtn").unbind().on('click', $this.exportParamValue);
		            },
		            getConfigItemIdAndParamCode: function () {
		                var paramGrid = $('#paramTable').data("kendoGrid"), _select;
		                var configItemId = '';
		                var paramCode = '';
		                if (paramGrid && (_select = paramGrid.select()) && _select.length > 0) {
		                	configItemId = _select.find('input.item-checkbox-column').val();
		                	paramCode = paramGrid.select().find('td:eq(1)').text();
		                }
		                return {configItemId : configItemId, paramCode : paramCode};
		            },
		            assemble: function (options) {
		            	var params = $.extend(true, {
		            		configItemId: Fn.getConfigItemIdAndParamCode().configItemId,
		            		paramCode : Fn.getConfigItemIdAndParamCode().paramCode,
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
	                    _grid = $('#paramValueTable').kendoGrid($this.options($this.assemble)).data("kendoGrid");
		                $this.bind(_grid);
					}
				};
				$.fn.extend({
		        	paramValueGrid: function () {
		                $.extend(this, Fn);
		                return this;
		            }
		        });
			});
})(jQuery);