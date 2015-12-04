(function ($) {
    require(["./assets/lib/kendoUI/js/kendo.validator.js"], function () {
        var Fn = {
            submit: function () {
                var form = $("#addForm"), validator = form.kendoValidator().data("kendoValidator");
                if (validator.validate()) {
                    form.submit();
                }
            },
            init: function () {
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
                            result = $.ajax({
                                url: input.attr(kendo.attr("only-url")),
                                data: _data,
                                async: false
                            }).responseText;
                            return result === "true";
                        }
                    }
                });
                $('#submitBtn').on('click', this.submit);
            }
        };
        $(Fn.init.bind(Fn))
    })
})(jQuery);