(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js",
			"../assets/lib/kendoUI/js/kendo.dropdownlist.js" ], function() {
		var Fn = {
			submit : function() {
				var form = $("#modForm"), validator = form.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				$("#modForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);