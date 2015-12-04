(function($) {
	require(
			[ "./assets/lib/kendoUI/js/kendo.grid.js" ],
			function() {
				var Fn = {
					options : function(){
					  return{
						dataSource : {
							transport : {
								read : {
									url : BASEPATH + "attr/queryAttrPager",
									dataType : "json"
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
	                        template: kendo.template($('#addAttrTemplate').html()),
	                        name: '<i class="icon-plus" /> 新增',
	                        url: BASEPATH + 'attr/toAddAttr'
	                    }, {
	                        template: kendo.template($('#modifyAttrTemplate').html()),
	                    }, {
	                        template: kendo.template($('#afterModifyAttrTemplate').html()),
	                        url: BASEPATH + 'attr/toModifyAttr'
	                    },{
	                        template: kendo.template($('#deleteAttrTemplate').html())
	                    },  {
	                        template: kendo.template($('#importAttrTemplate').html()),
	                        name: '<i class="icon-upload" /> 导入',
	                        url: BASEPATH + 'attr/toImportAttr'
	                    },  {
	                        template: kendo.template($('#exportAttrTemplate').html())
	                    }],
						columns : [
								{
									field : "attrId",
									sortable : false,
									template : '<input type="checkbox" value="#: attrId #" data-toggle="checkcolumn" class="attr-checkbox-column" data-target="\\#attr-check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".attr-checkbox-column" id="attr-check-all" />',
									width : 30
								}, 
								{
									field : "attrName",
									title : "属性名称"
								},{
									field : "attrType",
									title : "属性类型"
								}, {
									field : "parentAttrId",
									title : "父属性标识"
								} , {
									field : "attrCode",
									title : "属性代码"
								}, {
									field : "visible",
									title : "是否可见"
								}, {
									field : "instantiatable",
									title : "是否可实例化"
								}, {
									field : "defaultValue",
									title : "缺省值"
								}, {
									field : "inputType",
									title : "输入方式"
								} , {
									field : "dataType",
									title : "数据类型"
								} , {
									field : "valueScript",
									title : "取值校验规则"
								}]
					};
					},
					addAttr: function () {
		                var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
		            },
					modifyAttr: function (e) {
		                var attrIds = [];
		                $('.attr-checkbox-column:checked').each(function () {
		                	attrIds.push(this.value);
		                });

		                if (attrIds.length === 0) {
		                    e.stopPropagation();
		                    alert("请选择要修改的记录！");

		                    return false;
		                } else if (attrIds.length != 1) {
		                    alert("只能选择一条记录进行修改");
		                    e.stopPropagation();
		                    return false;
		                }
		                $.post(
								BASEPATH + "attr/checkHasChild",
								{attrIds : attrIds[0]},
								function(data) {
									if (data){
										alert("要修改的属性有子属性,请确认");
										return false;
									}
									else {
										$.post(
											BASEPATH + "attr/checkHasValue",
											{attrIds : attrIds[0]},
											function(data) {
												if (data){
													alert("要修改的属性有属性值,请确认");
													return false;
												}
												else {
													$("#afterModifyAttrBtn").click();
												}
											}
						                );
									}
								}
		                );
		            },
		            afterModifyAttr:function(){
		            	var _this = $(this), params = {};
		            	var attrIds = [];
		            	$('.attr-checkbox-column:checked').each(function () {
		            		attrIds.push(this.value);
		                });
		                params['attrId'] = attrIds[0];
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					deleteAttr : function() {
						var attrIds = [];
						$('.attr-checkbox-column:checked').each(
								function() {
									attrIds.push(this.value);
								});
						if (attrIds.length === 0) {
							alert("请选择要删除的属性！");
						} 
						else{
								$.post(
									BASEPATH + "attr/checkHasChild",
									{attrIds : attrIds.join(',')},
									function(data) {
										if (data){
											alert("要修改的属性有子属性,请确认");
											return false;
										}
										else {
											$.post(
												BASEPATH + "attr/checkHasValue",
												{attrIds : attrIds.join(',')},
												function(data) {
													if (data){
														alert("要修改的属性有属性值,请确认");
														return false;
													}
													else {
														if (confirm("确定要删除选定的属性吗？")) {
														$.post(
																BASEPATH + "attr/deleteAttr",
																{
																	attrIds : attrIds.join(',')
																},
																function(data) {
																	if (data && data.errcode) {
																		alert(data.errmsg || "删除失败");
																	} else {
																		alert("删除属性成功");
																		$(document).trigger('page.fresh.attrGrid');
																	}
																});
														}
													}
												}
							                );
										}
									}
			                );
						}
					},
					importAttr : function(){
						var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
					},
					exportAttr : function(){
						var src = BASEPATH + "attr/exportAttr";
						window.location = src;
					},
					bind: function (grid) {
		                var $this = this;
		                $(document).on('page.fresh.attrGrid', function () {
		                        $this.init();
		                        $.utils.closePopup();
		                    });
		                $("#addAttrBtn").unbind().on('click', $this.addAttr);
		                $("#modifyAttrBtn").unbind().on('click', $this.modifyAttr);
		                $("#afterModifyAttrBtn").unbind().on('click', $this.afterModifyAttr);
		                $("#deleteAttrBtn").unbind().on('click', $this.deleteAttr);
		                $("#importAttrBtn").unbind().on('click', $this.importAttr);
		                $("#exportAttrBtn").unbind().on('click', $this.exportAttr);
		            },
					init : function() {
						var $this = this,
	                    _grid = $('#attrTable').kendoGrid(this.options()).data("kendoGrid");
						_grid.dataSource.bind("change", function () {
		                    if(this.total() == 0){
		                        $('#attrValueTable').dictDataGrid().load({attrId: ''});
		                    }
		                });
		                _grid.bind("change", function () {
		                    var _attrId = this.select().find('input.attr-checkbox-column').val();
		                    $('#attrValueTable').attrValueGrid().load({attrId : _attrId});
		                }).bind("dataBound", function () {
		                    _grid.select(_grid.tbody.find("tr:eq(0)"));
		                });
		                $this.bind(_grid);
					}
				};
				$(Fn.init.bind(Fn));
			});
})(jQuery);