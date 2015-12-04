(function ($) {
    var _path = './assets/lib/kendoUI/js/',
        p_treeView = _path + 'kendo.treeview.js',
        p_dragAndDrop = _path + 'kendo.draganddrop.js',
        p_grid = _path + 'kendo.grid.js',
        Fn = {
            processData: function (data, fields, rootLevel) {
                var idField = fields.idField,
                    foreignKey = fields.parentField,
                    textField = fields.textField,
                    permissionField = fields.permissionField;
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

                    item.enabled = item[permissionField];
                    delete item[permissionField];
                }
                return hash[rootLevel];
            },
            tree: {
                behind: function (tree) {
                    var _select = tree.select(), _parent, params = {};
                    if (_select.length > 0 && (_parent = tree.parent(_select)).length > 0) {
                        var _nodeData = tree.dataItem(_parent);
                        params['parentOrgId'] = _nodeData.orgId;
                        params['parentOrgName'] = _nodeData.text;
                    }
                    this.data("src", $.utils.formatUrl(this.data().src, params));
                },
                sub: function (tree) {
                    var _select = tree.select(), params = {};
                    if (_select.length > 0) {
                        var _nodeData = tree.dataItem(_select);
                        params['parentOrgId'] = _nodeData.orgId;
                        params['parentOrgName'] = _nodeData.text;
                    }
                    this.data("src", $.utils.formatUrl(this.data().src, params));
                },
                modify: function (tree, e) {
                    var _select = tree.select(), params = {};
                    if (_select.length <= 0) {
                        alert("请选择要修改的组织！");
                        e.stopPropagation();
                        return false;
                    }
                    params['orgId'] = tree.dataItem(_select).orgId;
                    var _parent = tree.parent(_select);
                    if (_parent.length > 0) {
                        params['parentOrgName'] = tree.dataItem(_parent).text;
                    }
                    this.data("src", $.utils.formatUrl(this.data().src, params));
                },
                remove: function (tree) {
                    var _src = this.data().src, _select = tree.select();
                    if (_select.length <= 0) {
                        alert("请选择要删除的组织！");
                    } else if (confirm("确定要删除选定的组织吗？")) {
                        $.post(_src, {id: tree.dataItem(_select).orgId}, function (data) {
                            if (data && data.errcode) {
                                alert(data.errmsg || "删除失败");
                            } else {
                                alert("删除组织成功")
                                $(document).trigger('page.fresh.orgTree');
                            }
                        });
                    }
                }
            },
            getDataSource: function (params) {
                var opts = {
                    url: BASEPATH + "org/list",
                    async: false
                };
                $.extend(opts, params || {});

                return Fn.processData(eval($.ajax(opts).responseText), {
                    idField: "orgId",
                    parentField: "parentOrgId",
                    textField: "orgName",
                    permissionField: "permission"
                }, null);
            },
            defaultSelect: function (tree) {
                var expandParent = function (_tree, _current) {
                    var _parent = _tree.parent(_current);
                    if (_parent.length > 0) {
                        _tree.expand(_parent);
                        expandParent(_tree, _parent);
                    }
                };

                tree.element.find(".k-item").each(function () {
                    var _this = $(this);
                    if (!_this.find(".k-in").hasClass('k-state-disabled')) {
                        tree.select(_this);
                        expandParent(tree, _this);
                        return false;
                    }
                });
            }
        };
    require([p_treeView, p_dragAndDrop, p_grid], function () {
        var _tree = $("#treeview").kendoTreeView({
            template: kendo.template($("#treeview-template").html()),
            dataSource: Fn.getDataSource()
        }).data("kendoTreeView");
        _tree.element.on("dblclick.kendoTreeView", ".k-in", $.proxy(_tree._toggleButtonClick, _tree))
            .on("click.kendoTreeView", ".k-plus-disabled,.k-minus-disabled", $.proxy(_tree._toggleButtonClick, _tree));
        _tree.bind("select", function (e) {
            var _node = _tree.dataItem(e.node) || _tree.dataItem(e);
            $('#dutyDataTable').dutyGrid().load({orgId: _node ? _node.orgId : ''});
        });
        Fn.defaultSelect(_tree);
        _tree.trigger("select", _tree.element.find(".k-item:first"));

        $(".col-md-3 button").on("click", function (e) {
            e.preventDefault();
            var _this = $(this), _id = _this.attr("id");
            Fn.tree[_id].call(_this, _tree, e);
        });

        $(document).on('page.fresh.orgTree', function () {
            _tree.setDataSource(Fn.getDataSource());
            Fn.defaultSelect(_tree);
            _tree.trigger("select", _tree.element.find(".k-item:first"));

            $.utils.closePopup();
        });
    });
})(jQuery);