(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js",
	          "../assets/lib/kendoUI/js/kendo.multiselect.js"], function() {
		var Fn = {
	            selectDirectoryCode: {
	                dataSource: {
	                    transport: {
	                        read: {
	                            url: BASEPATH + "config/queryDirectoryCode",
	                            dataType: "json"
	                        }
	                    }
	                }
	            },
	            selectModuleCode: {
	                dataSource: {
	                    transport: {
	                        read: {
	                            url: BASEPATH + "config/queryModuleCode",
	                            dataType: "json"
	                        }
	                    }
	                }
	            },
			submit : function() {
				var form = $("#modifyForm"), validator = form.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				var $this = this;
            	$("#directoryCode").kendoMultiSelect({
                    placeholder: "请选择目录代码",
                    dataTextField: "directoryName",
                    dataValueField: "directoryCode",
                    autoBind: false,
                    maxSelectedItems : 1,
                    dataSource: $this.selectDirectoryCode.dataSource
                }).data("kendoMultiSelect").value($("#oldDirectoryCode").val());
            	$("#moduleCode").kendoMultiSelect({
                    placeholder: "请选择业务模块代码",
                    dataTextField: "moduleName",
                    dataValueField: "moduleCode",
                    autoBind: false,
                    maxSelectedItems : 1,
                    dataSource: $this.selectModuleCode.dataSource
                }).data("kendoMultiSelect").value($("#oldModuleCode").val());
				$("#modifyForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn));
	});
})(jQuery);