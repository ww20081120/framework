(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.grid.js"], function () {
        var Fn = {
            options: function (fn) {
                return {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "dict/queryDictDataPager",
                                dataType: "json",
                                data: fn
                            }
                        },
                        serverPaging: true,
                        pageSize: 5,
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
                        template: kendo.template($('#addDictDataTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'dict/toAddDictData'
                    }, {
                        template: kendo.template($('#modifyDictDataTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'dict/toModifyDictData'
                    }, {
                        template: kendo.template($('#deleteDictDataTemplate').html())
                    }, {
                        template: kendo.template($('#importDictDataTemplate').html()),
                        name: '<i class="icon-upload" /> 导入',
                        url: BASEPATH + 'dict/toImportDictData'
                    },  {
                        template: kendo.template($('#exportDictDataTemplate').html())
                    }],
                    columns: [{
                        field: "dictDataId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: dictDataId #" data-toggle="checkcolumn" class="dictData-checkboxcolumn" data-target="\\#dictData-check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".dictData-checkboxcolumn" id="dictData-check-all" />',
                        width: 30
                    }, {
                        field: "dictCode",
                        title: "字典代码"
                    }, {
                        field: "dictDataName",
                        title: "字典数据名称"
                    }, {
                        field: "dictDataValue",
                        title: "字典数据值"
                    }, {
                        field: "isFixed",
                        title: "是否固定"
                    }, {
                        field: "isCancel",
                        title: "是否可以删除"
                    }]
                };
            },
            addDictData: function () {
                var _this = $(this),_dict = Fn.getDict(), params = {};
                params['dictCode'] = _dict.dictCode;
                _this.data("src", $.utils.formatUrl(_this.data().src,params));
            },
			modifyDictData: function (e) {
                var dictDataIds = [];
                $('.dictData-checkboxcolumn:checked').each(function () {
                	dictDataIds.push(this.value);
                });

                if (dictDataIds.length === 0) {
                    e.stopPropagation();
                    alert("请选择要修改的记录！");

                    return false;
                } else if (dictDataIds.length != 1) {
                    alert("只能选择一条记录进行修改");
                    e.stopPropagation();
                    return false;
                }
                var _this = $(this), params = {};
                params['dictDataId'] = dictDataIds[0];
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
			deleteDictData : function() {
				var dictDataIds = [];
				$('.dictData-checkboxcolumn:checked').each(
						function() {
							dictDataIds.push(this.value);
						});
				if (dictDataIds.length === 0) {
					alert("请选择要删除的字典！");
				} 
				else{
					if (confirm("确定要删除选定的字典吗？")) {
						$.post(
								BASEPATH + "dict/deleteDictData",
								{
									dictDataIds : dictDataIds.join(',')
								},
								function(data) {
									if (data && data.errcode) {
										alert(data.errmsg || "删除失败");
									} else {
										alert("删除字典成功");
										$(document).trigger('page.fresh.dictDataGrid');
									}
								});
				}}
			},
			importDictData : function(){
				
			},
			exportDictData : function(){
				var src = BASEPATH + "dict/exportDictData";
				window.location = src;
			},
			bind: function (grid) {
                var $this = this;
                $(document).on('page.fresh.dictDataGrid', function () {
                        $this.init();
                        $.utils.closePopup();
                    });
                $("#addDictDataBtn").unbind().on('click', $this.addDictData);
                $("#modifyDictDataBtn").unbind().on('click', $this.modifyDictData);
                $("#deleteDictDataBtn").unbind().on('click', $this.deleteDictData);
                $("#importDictDataBtn").unbind().on('click', $this.importDictData);
                $("#exportDictDataBtn").unbind().on('click', $this.exportDictData);
            },
            getDict: function () {
                var _dictGrid = $('#dictTable').data("kendoGrid"), _select;
                var dictCode = '';
                if (_dictGrid && (_select = _dictGrid.select()) && _select.length > 0) {
                	dictCode = _select.find('input.dictionary-checkbox-column').val();
                }
                return {dictCode: dictCode};
            },
            assemble: function (options) {
            	var params = $.extend(true, {
                    dictCode: Fn.getDict().dictCode,
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
            init: function () {
                var _this = this, _grid = $('#dictDataTable').kendoGrid(_this.options(_this.assemble)).data("kendoGrid");
				_this.bind(_grid);
            }
        };
        $.fn.extend({
        	dictDataGrid: function () {
                $.extend(this, Fn);
                return this;
            }
        });
    });
})(jQuery);