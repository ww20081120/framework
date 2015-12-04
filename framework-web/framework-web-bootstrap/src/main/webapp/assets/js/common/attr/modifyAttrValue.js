(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js" ], function() {
		var Fn = {
			submit : function() {
				var form = $("#modifyForm"), validator = form.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				$("#modifyForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn));
	});
})(jQuery);