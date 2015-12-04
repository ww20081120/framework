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
							url : BASEPATH + "/modadmin",
							dataType : "json"
						}
					}
				}
			},
			submit : function() {
				var form = $("#modForm"), validator = form.kendoValidator()
						.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				$("#modForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
				//$("#moduleCode").kendoDropDownList(this.moduleDataOption);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);