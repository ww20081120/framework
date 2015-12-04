(function ($) {
    require(["../assets/lib/kendoUI/js/kendo.validator.js",
        "../assets/lib/kendoUI/js/kendo.multiselect.js",
        "../assets/lib/kendoUI/js/kendo.tabstrip.js"], function () {

        var Fn = {
            selectDataOption: {
                dataTextField: "roleName",
                dataValueField: "roleId",
                dataSource: {
                    transport: {
                        read: {
                            url: BASEPATH + "role/list",
                            dataType: "json"
                        }
                    }
                }
            },
            init: function () {
                var $this = this, _roleList = $("input[name=selectedRoles]").val();
                $("#addForm").kendoValidator();

                var _tabStrip = $("#tabstrip").kendoTabStrip({
                    animation: {
                        open: {
                            effects: "fadeIn"
                        }
                    }
                }).data("kendoTabStrip");
                $.ajax({
                    url: BASEPATH + "module",
                    dataType: "json",
                    async: false,
                    success: function (data) {
                        $.each(eval(data), function (i, json) {
                            var _id = 'roleList_' + json.moduleCode;
                            _tabStrip.append({
                                text: json.moduleName,
                                content: kendo.template($("#selectTemplate").html())({
                                    id: _id,
                                    name: _id
                                })
                            });
                        });
                    }
                });
                _tabStrip.select('li:first');
                var _selects = _tabStrip.element.find("select");
                _selects.each(function () {
                    var _$this = $(this), _moduleCode = _$this.attr("id").split("_")[1];
                    $.extend($this.selectDataOption.dataSource.transport.read, {data: {moduleCode: _moduleCode}});
                    _$this.kendoMultiSelect($this.selectDataOption).data("kendoMultiSelect").value(_roleList.split(','));
                });

                $('#submitBtn').on('click', function () {
                        var form = $("#addForm"), validator = form.kendoValidator().data("kendoValidator");
                        if (validator.validate()) {
                            var items = [];
                            _selects.each(function () {
                                var _multiSelect = $(this).data("kendoMultiSelect");
                                $.merge(items, _multiSelect.value());
                            });
                            items = $.unique(items);

                            if (items.length <= 0) {
                                alert("请选择至少一种角色");
                                return false;
                            }
                            var _roleInput = $('<input/>').attr("type", "hidden").attr("name", "roleList").val(items.join(','));
                            form.append(_roleInput);
                            form.submit();
                        }
                    }
                );

            }
        };
        $(Fn.init.bind(Fn));
    })
})(jQuery);