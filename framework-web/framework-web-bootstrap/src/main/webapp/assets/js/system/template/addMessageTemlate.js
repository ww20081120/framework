(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js",
			"../assets/lib/kendoUI/js/kendo.dropdownlist.js" ], function() {
		var Fn = {
			submit : function() {
				var form = $("#addForm"), validator = form.kendoValidator().data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				$("#addForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);