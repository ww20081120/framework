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

				// var aa = document.getElementById("moduleCodeSel");
				// var opts = document.getElementById("moduleCode");
				// //
				// for (var i = 0; i < opts.options.length; i++) {
				// if (aa == opts.options[i].value) {
				// opts.options[i].selected = 'selected';
				// alert(opts.options[i].value);
				// break;
				//					}
				//				}
				$("#addForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);