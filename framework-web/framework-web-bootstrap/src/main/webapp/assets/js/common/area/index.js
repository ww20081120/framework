(function($) {
	var _path = './assets/lib/kendoUI/js/',
	     p_treeView = _path+ 'kendo.treeview.js',
	     p_grid = _path + 'kendo.grid.js', 
	     Fn = {
		processData : function(data, fields, rootLevel) {
			var idField = fields.idField, foreignKey = fields.parentField, textField = fields.textField;
			var hash = {};
			for (var i = 0; i < data.length; i++) {
				var item = data[i];
				var id = item[idField];
				var parentId = item[foreignKey];
				hash[id] = hash[id] || [];
				hash[parentId] = hash[parentId] || [];

				item.items = hash[id];
				hash[parentId].push(item);

				if (item[textField]) {
					item.text = item[textField];
					delete item[textField];
				}
				item.expanded="true";//全部展开
			}
			return hash[rootLevel];
		},
		subUrl : function(url) {
			var _index = -1;
			if ((_index = url.indexOf('?')) > -1) {
				url = url.substring(0, _index);
			}
			return url;
		},
		tree : {
			behind : function(tree) {
				var _src = this.data().src;
				_src = Fn.subUrl(_src);
				this.data("src", _src);
			},
			sub : function(tree) {
				var _src = this.data().src, _select = tree.select();
				_src = Fn.subUrl(_src);
				if (_select.length > 0) {
					var _nodeData = tree.dataItem(_select);
					_src += '?parentAreaId=' + _nodeData.areaId
							+ '&parentAreaName=' + _nodeData.text
				}
				this.data("src", _src);
			},
			remove : function(tree) {
				var _src = this.data().src, _select = tree.select();
				if (_select.length <= 0) {
					alert("请选择要删除的区域！");
				} else if (_select.children() && _select.children().length > 1) {
					alert("该区域下有子区域，不能删除！");
				} else if (confirm("确定要删除选定的区域吗？")) {
					$.post(_src, {
						id : tree.dataItem(_select).areaId
					}, function(data) {
						if (data && data.errcode) {
							alert(data.errmsg || "删除失败");
						} else {
							alert("删除区域成功")
							$(document).trigger('page.fresh.areaTree');
						}
					});
				}
			},
			modify : function(tree) {
				var _src = this.data().src, _select = tree.select();
				_src = Fn.subUrl(_src);
				if (_select.length > 0) {
					_src += '?areaId=' + tree.dataItem(_select).areaId;

					var _parent = tree.parent(_select);
					if (_parent.length > 0) {
						_src += '&parentAreaName='
								+ tree.dataItem(_parent).text
					}
				}
				this.data("src", _src);
			},
			exportArea : function(tree) {
				var _src = this.data().src;
				_src = Fn.subUrl(_src);
				
				window.location =_src;
				
			},
			importArea : function(tree) {
				var _src = this.data().src;
				_src = Fn.subUrl(_src);
				//this.data("src", _src);
				$.post(_src,function(data) {
					if (data && data.errcode) {
						alert(data.errmsg || "导入成功");
					} else {
						alert("导入区域成功")
						$(document).trigger('page.fresh.areaTree');
					}
				});
			}
		},
		getDataSource : function(params) {
			var opts = {
				url : BASEPATH + "area/list",
				async : false
			};
			$.extend(opts, params || {});

			return Fn.processData(eval($.ajax(opts).responseText), {
				idField : "areaId",
				parentField : "parentAreaId",
				textField : "areaName"
			}, null);
		},
		defaultSelect : function(tree) {
			tree.select(".k-item:first");
		}
	};
	require(
			[ p_treeView, p_grid ],
			function() {
				var _tree = $("#treeview").kendoTreeView({
					template : kendo.template($("#treeview-template").html()),
					dataSource : Fn.getDataSource()
				}).data("kendoTreeView");
				
				Fn.defaultSelect(_tree);

				$(".col-md-4 button").on("click", function(e) {
					e.preventDefault();
					var _this = $(this), _id = _this.attr("id");
					Fn.tree[_id].call(_this, _tree);
				});

				$(document).on(
						'page.fresh.areaTree',
						function() {
							_tree.setDataSource(Fn.getDataSource());
							Fn.defaultSelect(_tree);
							_tree.trigger("select", _tree.element
									.find(".k-item:first"));
						});

				_tree
						.bind(
								"select",
								function(e) {
									var _areaId = (_tree.dataItem(e.node) || _tree
											.dataItem(e)).areaId, _grid = $(
											'#dutyDataTable').data("kendoGrid"), _opts = _grid.options;

									$.extend(_opts.dataSource.transport.read, {
										data : {
											areaId : _areaId
										}
									});

									_grid.dataSource.read();
								});
			});

})(jQuery);