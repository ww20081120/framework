(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.grid.js"], function () {
        var Fn = {
            options: function (fn) {
                return {
                    dataSource: {
                        transport: {
                            read: {
                                url: BASEPATH + "attr/queryAttrValuePager",
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
                        template: kendo.template($('#addAttrValueTemplate').html()),
                        name: '<i class="icon-plus" /> 新增',
                        url: BASEPATH + 'attr/toAddAttrValue'
                    }, {
                        template: kendo.template($('#modifyAttrValueTemplate').html()),
                        name: '<i class="icon-edit" /> 修改',
                        url: BASEPATH + 'attr/toModifyAttrValue'
                    }, {
                        template: kendo.template($('#deleteAttrValueTemplate').html())
                    }, {
                        template: kendo.template($('#importAttrValueTemplate').html()),
                        name: '<i class="icon-upload" /> 导入',
                        url: BASEPATH + 'attr/toImportAttrValue'
                    },  {
                        template: kendo.template($('#exportAttrValueTemplate').html())
                    }],
                    columns: [{
                        field: "attrValueId",
                        sortable: false,
                        template: '<input type="checkbox" value="#: attrValueId #" data-toggle="checkcolumn" class="attr-value-checkboxcolumn" data-target="\\#attr-value-check-all">',
                        headerTemplate: '<input type="checkbox" data-toggle="checkall" data-target=".attr-value-checkboxcolumn" id="attr-value-check-all" />',
                        width: 30
                    }, {
                        field: "valueMark",
                        title: "取值说明"
                    }, {
                        field: "value",
                        title: "取值"
                    }]
                };
            },
            addAttrValue: function () {
                var _this = $(this),_dict = Fn.getAttrId(), params = {};
                params['attrId'] = _dict.attrId;
                _this.data("src", $.utils.formatUrl(_this.data().src,params));
            },
			modifyAttrValue: function (e) {
                var attrValueIds = [];
                $('.attr-value-checkboxcolumn:checked').each(function () {
                	attrValueIds.push(this.value);
                });

                if (attrValueIds.length === 0) {
                    e.stopPropagation();
                    alert("请选择要修改的记录！");

                    return false;
                } else if (attrValueIds.length != 1) {
                    alert("只能选择一条记录进行修改");
                    e.stopPropagation();
                    return false;
                }
                var _this = $(this), params = {};
                params['attrValueId'] = attrValueIds[0];
                _this.data("src", $.utils.formatUrl(_this.data().src, params));
            },
			deleteAttrValue : function() {
				var attrValueIds = [];
				$('.attr-value-checkboxcolumn:checked').each(
						function() {
							attrValueIds.push(this.value);
						});
				if (attrValueIds.length === 0) {
					alert("请选择要删除的属性值！");
				} 
				else{
					if (confirm("确定要删除选定的属性值吗？")) {
						$.post(
								BASEPATH + "attr/deleteAttrValue",
								{
									attrValueIds : attrValueIds.join(',')
								},
								function(data) {
									if (data && data.errcode) {
										alert(data.errmsg || "删除失败");
									} else {
										alert("删除属性值成功");
										$(document).trigger('page.fresh.attrValueGrid');
									}
								});
				}}
			},
			importAttrValue : function(){
				var _this = $(this);
                _this.data("src", $.utils.formatUrl(_this.data().src));
			},
			exportAttrValue : function(){
				var src = BASEPATH + "attr/exportAttrValue";
				window.location = src;
			},
			bind: function (grid) {
                var $this = this;
                $(document).on('page.fresh.attrValueGrid', function () {
                        $this.init();
                        $.utils.closePopup();
                    });
                $("#addAttrValueBtn").unbind().on('click', $this.addAttrValue);
                $("#modifyAttrValueBtn").unbind().on('click', $this.modifyAttrValue);
                $("#deleteAttrValueBtn").unbind().on('click', $this.deleteAttrValue);
                $("#importAttrValueBtn").unbind().on('click', $this.importAttrValue);
                $("#exportAttrValueBtn").unbind().on('click', $this.exportAttrValue);
            },
            getAttrId: function () {
                var _dictGrid = $('#attrTable').data("kendoGrid"), _select;
                var attrId = '';
                if (_dictGrid && (_select = _dictGrid.select()) && _select.length > 0) {
                	attrId = _select.find('input.attr-checkbox-column').val();
                }
                return {attrId: attrId};
            },
            assemble: function (options) {
            	var params = $.extend(true, {
            		attrId: Fn.getAttrId().attrId,
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
                var _this = this, _grid = $('#attrValueTable').kendoGrid(_this.options(_this.assemble)).data("kendoGrid");
				_this.bind(_grid);
            }
        };
        $.fn.extend({
        	attrValueGrid: function () {
                $.extend(this, Fn);
                return this;
            }
        });
    });
})(jQuery);