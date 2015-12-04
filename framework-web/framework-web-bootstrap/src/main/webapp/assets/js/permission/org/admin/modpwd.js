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
				var form = $("#modpwdForm"), validator = form.kendoValidator()
						.data("kendoValidator");
				if (validator.validate()) {
					form.submit();
				}
			},
			init : function() {
				 $("#modpwdForm").kendoValidator({
	                    rules: {
	                        compare: function(input){
	                        	var _hasAttribute = function (_input, _name) {
	                                if (_input.length)  {
	                                    return _input[0].attributes[_name] != null;
	                                }
	                                return false;
	                            };
	                            if(!_hasAttribute(input, "compare")){
	                                return true;
	                            }
	                            var result = false, _current = input.val(),
	                            _compare = $("#" + input.attr(kendo.attr("compare-id"))).val();
	                            return _current === _compare;
	                        }
	                    }
	                
	                });
				$('#submitBtn').on('click', this.submit);
				//$("#moduleCode").kendoDropDownList(this.moduleDataOption);
			}
		};
		$(Fn.init.bind(Fn))
	})
})(jQuery);