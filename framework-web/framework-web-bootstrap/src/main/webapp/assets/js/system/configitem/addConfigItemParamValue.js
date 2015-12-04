(function ($) {
    require(["../assets/lib/kendoUI/js/kendo.validator.js"], function () {

        var Fn = {
            init: function () {
            	$("#addForm").kendoValidator();
                $('#submitBtn').on('click', function () {
                        var form = $("#addForm"), validator = form.kendoValidator().data("kendoValidator");
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