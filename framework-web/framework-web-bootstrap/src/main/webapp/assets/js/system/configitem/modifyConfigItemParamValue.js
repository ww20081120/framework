(function ($) {
    require(["../assets/lib/kendoUI/js/kendo.validator.js"], function () {

        var Fn = {
            init: function () {
            	$("#modifyForm").kendoValidator();
                $('#submitBtn').on('click', function () {
                        var form = $("#modifyForm"), validator = form.kendoValidator().data("kendoValidator");
                        if (validator.validate()) {
                            form.submit();
                        }
                    }
                );
            }
        };
        $(Fn.init.bind(Fn));
    });
})(jQuery);