(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js", 
	          "../assets/lib/kendoUI/js/kendo.multiselect.js"], function() {
		var Fn = {
			submit : function() {
				var form = $("#modifyForm"), validator = form.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			selectParentOption: {
                dataSource: {
                    transport: {
                        read: {
                            url: BASEPATH + "attr/queryAllAttr",
                            dataType: "json"
                        }
                    }
                }
            },
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
			init : function() {
				var $this = this;
				$("#parentAttrId").kendoMultiSelect({
                    placeholder: "请选择父属性",
                    dataTextField: "attrName",
                    dataValueField: "attrId",
                    autoBind: false,
                    maxSelectedItems : 1,
                    dataSource: $this.selectParentOption.dataSource
                }).data("kendoMultiSelect").value($("#oldParentAttrId").val());
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
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn));
	});
})(jQuery);