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
							url : BASEPATH + "module",
							dataType : "json"
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
				$("#addForm").kendoValidator({
					rules: {
						only: function (input) {
							var _hasAttribute = function (_input, _name) {
								if (_input.length)  {
									return _input[0].attributes[_name] != null;
								}
								return false;
							};
							if(!_hasAttribute(input, "only")){
								return true;
							}
							var result = false, _data = {};
							_data[input.attr("name")] = input.val();
							_data['orgId'] = input.attr(kendo.attr("only-id"));
							result =  $.ajax({
								url: input.attr(kendo.attr("only-url")),
								data: _data,
								async: false
							}).responseText;
							return result === "true";
						}
					}
				});
				$('#submitBtn').on('click', this.submit);
				$("#ownerArea").kendoDropDownList(this.moduleDataOption);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);