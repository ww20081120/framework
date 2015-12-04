(function ($) {
    require(["../assets/lib/kendoUI/js/kendo.validator.js",
        "../assets/lib/kendoUI/js/kendo.multiselect.js"], function () {

        var Fn = {
            selectInputTypeOption: {
                dataSource: {
                    transport: {
                        read: {
                            url: BASEPATH + "attr/queryInputType",
                            dataType: "json"
                        }
                    }
                }
            },
            selectDataTypeOption: {
                dataSource: {
                    transport: {
                        read: {
                            url: BASEPATH + "attr/queryDataType",
                            dataType: "json"
                        }
                    }
                }
            },
            init: function () {
            	var $this = this;
            	$("#inputType").kendoMultiSelect({
                    placeholder: "请选择输入类型",
                    dataTextField: "dictDataName",
                    dataValueField: "dictDataValue",
                    autoBind: false,
                    maxSelectedItems : 1,
                    dataSource: $this.selectInputTypeOption.dataSource
                }).data("kendoMultiSelect").value($("#oldInputType").val());
            	$("#dataType").kendoMultiSelect({
                    placeholder: "请选择数据类型",
                    dataTextField: "dictDataName",
                    dataValueField: "dictDataValue",
                    autoBind: false,
                    maxSelectedItems : 1,
                    dataSource: $this.selectDataTypeOption.dataSource
                }).data("kendoMultiSelect").value($("#oldDataType").val());
            	$("#modifyForm").kendoValidator();
                $('#submitBtn').on('click', function () {
                        var form = $("#modifyForm"), validator = form.kendoValidator().data("kendoValidator");
                        if (validator.validate()) {
                            form.submit();
                        }
                    }
                );

            }
        };
        $(Fn.init.bind(Fn));
    });
})(jQuery);