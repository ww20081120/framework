(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js",
			"../assets/lib/kendoUI/js/kendo.dropdownlist.js" ], function() {
		var Fn = {
			moduleDataOption : {
				dataTextField : "moduleName",
				dataValueField : "moduleCode",
				dataSource : {
					transport : {
						read : {
							url : BASEPATH + "/saveAdmin",
							dataType : "json"
						}
					}
				}
			},
			submit : function() {
				var form = $("#addForm"), validator = form.kendoValidator()
						.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				var $this = this;
                $("#addForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
				//$("#moduleCode").kendoDropDownList(this.moduleDataOption);
				
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);