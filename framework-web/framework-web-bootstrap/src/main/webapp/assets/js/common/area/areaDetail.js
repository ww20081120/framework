(function($) {
	require([ "./assets/lib/kendoUI/js/kendo.grid.js" ], function() {
		var Fn = {
			grid : {
				dataSource : {
					transport : {
						read : {
							url : BASEPATH + "area/getAreaById",
							dataType : "json",
							type : "POST"
						}
					},
					schema : {
						data : function(response) {
							if (response) {
								for (var i = 0; i < response.length; i++) {
									if (response[i].areaType == "O") {
										response[i].areaType = "社区";
									} else if (response[i].areaType == "G") {
										response[i].areaType = "小区";
									} else if (response[i].areaType == "C") {
										response[i].areaType = "市";
									} else if (response[i].areaType == "D") {
										response[i].areaType = "区县";
									} else if (response[i].areaType == "P") {
										response[i].areaType = "省、直辖市";
									} else {
										response[i].areaType = "未知区域类型";
									}

								}
							}
							return response;
						}
					}
				},
				scrollable : false,
				columns : [ {
					field : "areaId",
					title : "区域标识"
				}, {
					field : "areaName",
					title : "区域名称"
				}, {
					field : "areaType",
					title : "区域类型"
				}, {
					field : "areaCode",
					title : "区域编码"
				}, {
					field : "remark",
					title : "备注"
				} ]
			},
			getarea : function() {
				var _tree = $("#treeview").data("kendoTreeView"), _treeSelect;
				var areaId = '', areaName = '';
				if (_tree && (_treeSelect = _tree.select())
						&& _treeSelect.length > 0) {
					var _nodeData = _tree.dataItem(_treeSelect);
					areaId = _nodeData.areaId;
					areaName = _nodeData.text;
				}
				return {
					areaId : areaId,
					areaName : areaName
				};
			},
			getGridOptions : function() {
				var _this = this;
				$.extend(_this.grid.dataSource.transport.read, {
					data : {
						areaId : _this.getarea().areaId
					}
				});
				return _this.grid;
			},
			init : function() {
				var $this = this, _grid = $('#dutyDataTable').kendoGrid(
						this.getGridOptions()).data("kendoGrid");
			}
		};
		$(Fn.init.bind(Fn));
	})
})(jQuery);