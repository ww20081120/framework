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
				var form = $("#modForm"), validator = form.kendoValidator()
						.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				$("#modForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
				$("#eventType").kendoDropDownList(this.moduleDataOption);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);