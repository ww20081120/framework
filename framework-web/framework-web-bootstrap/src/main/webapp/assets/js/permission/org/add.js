(function ($) {
    require(["../assets/lib/kendoUI/js/kendo.validator.js",
        "../assets/lib/kendoUI/js/kendo.dropdownlist.js"], function () {
        var Fn = {
            moduleDataOption: {
                dataTextField: "moduleName",
                dataValueField: "moduleCode",
                dataSource: {
                    transport: {
                        read: {
                            url: BASEPATH + "module",
                            dataType: "json"
                        }
                    }
                }
            },
            submit: function () {
                var form = $("#addForm"), validator = form.data("kendoValidator");
                if (validator.validate()) {
                    form.submit();
                }
            },
            init: function () {
                $("#addForm").kendoValidator();
                $('#submitBtn').on('click', this.submit);
                $("#ownerArea").kendoDropDownList(this.moduleDataOption);
            }
        };
        $(Fn.init.bind(Fn))
    })
})(jQuery);