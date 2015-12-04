(function($) {
	require([ "../assets/lib/kendoUI/js/kendo.validator.js",
			"../assets/lib/kendoUI/js/kendo.multiselect.js" ], function() {
		var Fn = {
			selectDataOption: {
				maxSelectedItems: 1,
				dataTextField: "functionName",
				dataValueField: "functionId",
				headerTemplate: '<div class="dropdown-header k-widget k-header">' +
									'<span>目录名</span> - <span>功能名</span>' +
								'</div>',
				itemTemplate: '<span class="k-state-default">#: data.directoryName #</span> - <span class="k-state-default">#: data.functionName #</span>',
				tagTemplate: '<span class="k-state-default">#: data.directoryName #</span> - <span class="k-state-default">#: data.functionName #</span>',
				dataSource: {
					transport: {
						read: {
							url: BASEPATH + "menu/listFunction",
							dataType: "json"
						}
					}
				}
			},
			submit : function() {
				var form = $("#addForm"), validator = form.kendoValidator().data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				$('#functionId').kendoMultiSelect(this.selectDataOption);
				$("#addForm").kendoValidator();
				$('#submitBtn').on('click', this.submit);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);