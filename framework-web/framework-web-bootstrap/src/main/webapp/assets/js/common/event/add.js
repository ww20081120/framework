(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js",
			"../assets/lib/kendoUI/js/kendo.dropdownlist.js" ], function() {
		var Fn = {
			moduleDataOption : {
				dataTextField : "value",
				dataValueField : "key",
				dataSource : EVENT_TYPE_GROUP		
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
				$("#eventType").kendoDropDownList(this.moduleDataOption);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);