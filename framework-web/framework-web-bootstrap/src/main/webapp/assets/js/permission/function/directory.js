(function ($) {
    var _path = './assets/lib/kendoUI/js/',
        p_treeView = _path + 'kendo.treeview.js',
        p_grid = _path + 'kendo.grid.js',
        Fn = {
            behind: function (tree) {
                var _select = tree.select(), _parent, params = {};
                if (_select.length > 0 && (_parent = tree.parent(_select)).length > 0) {
                    var _nodeData = tree.dataItem(_parent);
                    params['parentDirectoryCode'] = _nodeData.directoryCode;
                    params['parentDirectoryName'] = _nodeData.directoryName;
                }
                this.data("src", $.utils.formatUrl(this.data().src, params));
            },
            sub: function (tree) {
                var _select = tree.select(), params = {};
                if (_select.length > 0) {
                    var _nodeData = tree.dataItem(_select);
                    params['parentDirectoryCode'] = _nodeData.directoryCode;
                    params['parentDirectoryName'] = _nodeData.directoryName;
                }
                this.data("src", $.utils.formatUrl(this.data().src, params));
            },
            modify: function (tree) {
                var _select = tree.select(), params = {};
                if (_select.length > 0) {
                    params['directoryCode'] = tree.dataItem(_select).directoryCode;
                    var _parent = tree.parent(_select);
                    if (_parent.length > 0) {
                        params['parentDirectoryName'] = tree.dataItem(_parent).directoryName;
                    }
                }
                this.data("src", $.utils.formatUrl(this.data().src, params));
            },
            remove: function (tree) {
                var _src = this.data().src, _select = tree.select();
                if (_select.length <= 0) {
                    alert("请选择要删除的目录！");
                } else if (confirm("确定要删除选定的目录吗？")) {
                    $.post(_src, {
                        directoryCode: tree.dataItem(_select).directoryCode
                    }, function (data) {
                        if (data && data.errcode) {
                            alert(data.errmsg || "删除失败");
                        } else {
                            alert("删除目录成功")
                            $(document).trigger('page.fresh.directoryTree');
                        }
                    });
                }
            },
            defaultSelect: function (tree) {
                tree.select(tree.element.find(".k-item:first"));
            }
        };
    require([p_treeView, p_grid], function () {
        var _tree = $("#treeview").kendoTreeView({
            dataTextField: "directoryName",
            dataSource: new kendo.data.HierarchicalDataSource({
                transport: {
                    read: {
                        url: function (options) {
                            return BASEPATH + "directory/list?parentDirectoryCode=" + (options.directoryCode || 'DIR_URL');
                        },
                        dataType: "json"
                    }
                },
                schema: {
                    model: {
                        id: "directoryCode",
                        hasChildren: function () {
                            return this.childNum > 0;
                        }
                    }
                }
            })
        }).data("kendoTreeView");

        _tree.bind("dataBound", function () {
            Fn.defaultSelect(_tree);
            _tree.trigger("select", _tree.element.find(".k-item:first"));
        }).bind("select", function (e) {
            var _node = _tree.dataItem(e.node) || _tree.dataItem(e);
            $('#functionDataTable').functionGrid().load({directoryCode: _node ? _node.directoryCode : ''});
        });

        $(".col-md-3 button").on("click", function (e) {
            e.preventDefault();
            var _this = $(this), _id = _this.attr("id");
            Fn[_id].call(_this, _tree);
        });

        $(document).on('page.fresh.directoryTree', function () {
            _tree.dataSource.read();
            Fn.defaultSelect(_tree);
            _tree.trigger("select", _tree.element.find(".k-item:first"));
            $.utils.closePopup();
        });

    });

})(jQuery);