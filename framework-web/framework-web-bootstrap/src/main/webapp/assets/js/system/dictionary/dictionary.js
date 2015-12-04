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
									url : BASEPATH + "dict/queryDictPager",
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
	                        template: kendo.template($('#addDictTemplate').html()),
	                        name: '<i class="icon-plus" /> 新增',
	                        url: BASEPATH + 'dict/toAddDict'
	                    }, {
	                        template: kendo.template($('#modifyDictTemplate').html()),
	                    }, {
	                        template: kendo.template($('#afterModifyDictTemplate').html()),
	                        url: BASEPATH + 'dict/toModifyDict'
	                    },{
	                        template: kendo.template($('#deleteDictTemplate').html())
	                    },  {
	                        template: kendo.template($('#importDictTemplate').html()),
	                        name: '<i class="icon-upload" /> 导入',
	                        url: BASEPATH + 'dict/toImportDict'
	                    },  {
	                        template: kendo.template($('#exportDictTemplate').html())
	                    }],
						columns : [
								{
									field : "dictCode",
									sortable : false,
									template : '<input type="checkbox" value="#: dictCode #" data-toggle="checkcolumn" class="dictionary-checkbox-column" data-target="\\#dictionary-check-all">',
									headerTemplate : '<input type="checkbox" data-toggle="checkall" data-target=".dictionary-checkbox-column" id="dictionary-check-all" />',
									width : 30
								}, 
								{
									field : "dictCode",
									title : "字典代码"
								},{
									field : "dictName",
									title : "字典名称"
								}, {
									field : "remark",
									title : "备注"
								} ]
					};
					},
					addDict: function () {
		                var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
		            },
					modifyDict: function (e) {
		                var dictCodes = [];
		                $('.dictionary-checkbox-column:checked').each(function () {
		                	dictCodes.push(this.value);
		                });

		                if (dictCodes.length === 0) {
		                    e.stopPropagation();
		                    alert("请选择要修改的记录！");

		                    return false;
		                } else if (dictCodes.length != 1) {
		                    alert("只能选择一条记录进行修改");
		                    e.stopPropagation();
		                    return false;
		                }
		                $.post(
								BASEPATH + "dict/checkHasChild",
								{
									dictCodes : dictCodes[0]
								},
								function(data) {
									if (data){
										alert("要删除的字典中有字典数据,请确认");
										return false;
									}
									else {
										$("#afterModifyDictBtn").click();
									}
								});
		            },
		            afterModifyDict:function(){
		            	var _this = $(this), params = {};
		            	var dictCodes = [];
		            	$('.dictionary-checkbox-column:checked').each(function () {
		                	dictCodes.push(this.value);
		                });
		                params['dictCode'] = dictCodes[0];
		                _this.data("src", $.utils.formatUrl(_this.data().src, params));
		            },
					deleteDict : function() {
						var dictCodes = [];
						$('.dictionary-checkbox-column:checked').each(
								function() {
									dictCodes.push(this.value);
								});
						if (dictCodes.length === 0) {
							alert("请选择要删除的字典！");
						} 
						else{
							if (confirm("确定要删除选定的字典吗？")) {
									$.post(
											BASEPATH + "dict/checkHasChild",
											{
												dictCodes : dictCodes.join(',')
											},
											function(data) {
												if (data){
													alert("要删除的字典中有字典数据,请确认");
													return false;
												}
												else {
													$.post(
															BASEPATH + "dict/deleteDict",
															{
																dictCodes : dictCodes.join(',')
															},
															function(data) {
																if (data && data.errcode) {
																	alert(data.errmsg || "删除失败");
																} else {
																	alert("删除字典成功");
																	$(document).trigger('page.fresh.dictGrid');
																}
															});
												}
											});
						}}
					},
					importDict : function(){
						var _this = $(this);
		                _this.data("src", $.utils.formatUrl(_this.data().src));
					},
					exportDict : function(){
						var src = BASEPATH + "dict/exportDict";
						window.location = src;
					},
					bind: function (grid) {
		                var $this = this;
		                $(document).on('page.fresh.dictGrid', function () {
		                        $this.init();
		                        $.utils.closePopup();
		                    });
		                $("#addDictBtn").unbind().on('click', $this.addDict);
		                $("#modifyDictBtn").unbind().on('click', $this.modifyDict);
		                $("#afterModifyDictBtn").unbind().on('click', $this.afterModifyDict);
		                $("#deleteDictBtn").unbind().on('click', $this.deleteDict);
		                $("#importDictBtn").unbind().on('click', $this.importDict);
		                $("#exportDictBtn").unbind().on('click', $this.exportDict);
		            },
					init : function() {
						var $this = this,
	                    _grid = $('#dictTable').kendoGrid(this.options()).data("kendoGrid");
						_grid.dataSource.bind("change", function () {
		                    if(this.total() == 0){
		                        $('#dictDataTable').dictDataGrid().load({dictCode: ''});
		                    }
		                });
		                _grid.bind("change", function () {
		                    var _dictCode = this.select().find('input.dictionary-checkbox-column').val();
		                    $('#dictDataTable').dictDataGrid().load({dictCode: _dictCode});
		                }).bind("dataBound", function () {
		                    _grid.select(_grid.tbody.find("tr:eq(0)"));
		                });
		                $this.bind(_grid);
					}
				};
				$(Fn.init.bind(Fn));
			});
})(jQuery);