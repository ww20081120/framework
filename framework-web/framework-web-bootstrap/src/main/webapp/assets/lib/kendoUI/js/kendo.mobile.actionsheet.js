/*
* Kendo UI Complete v2014.1.318 (http://kendoui.com)
* Copyright 2014 Telerik AD. All rights reserved.
*
* Kendo UI Complete commercial licenses may be obtained at
* http://www.telerik.com/purchase/license-agreement/kendo-ui-complete
* If you do not own a commercial license, this file shall be governed by the trial license terms.
*/
(function(f, define){
    define([ "./kendo.mobile.popover", "./kendo.mobile.shim" ], f);
})(function(){

(function($, undefined) {
    var kendo = window.kendo,
        support = kendo.support,
        ui = kendo.mobile.ui,
        Shim = ui.Shim,
        Popup = ui.Popup,
        Widget = ui.Widget,
        OPEN = "open",
        CLOSE = "close",
        COMMAND = "command",
        BUTTONS = "li>a",
        CONTEXT_DATA = "actionsheetContext",
        WRAP = '<div class="km-actionsheet-wrapper" />',
        cancelTemplate = kendo.template('<li class="km-actionsheet-cancel"><a href="\\#">#:cancel#</a></li>');

    var ActionSheet = Widget.extend({
        init: function(element, options) {
            var that = this,
                ShimClass,
                tablet,
                type,
                os = support.mobileOS;

            Widget.fn.init.call(that, element, options);

            options = that.options;
            type = options.type;

            if (type === "auto") {
                tablet = os && os.tablet;
            } else {
                tablet = type === "tablet";
            }

            ShimClass = tablet ? Popup : Shim;

            element = that.element;

            if (options.cancelTemplate) {
                cancelTemplate = kendo.template(options.cancelTemplate);
            }

            element
                .addClass("km-actionsheet")
                .append(cancelTemplate({cancel: that.options.cancel}))
                .wrap(WRAP)
                .on("up", BUTTONS, "_click")
                .on("click", BUTTONS, kendo.preventDefault);

            that.wrapper = element.parent().addClass(type ? " km-actionsheet-" + type : "");
            that.shim = new ShimClass(that.wrapper, $.extend({modal: os.ios && os.majorVersion < 7, className: "km-actionsheet-root"}, that.options.popup) );

            kendo.notify(that, ui);

            if (tablet) {
                kendo.onResize($.proxy(this, "_resize"));
            }
        },

        events: [
            OPEN,
            CLOSE,
            COMMAND
        ],

        options: {
            name: "ActionSheet",
            cancel: "Cancel",
            type: "auto",
            popup: { height: "auto" }
        },

        open: function(target, context) {
            var that = this;
            that.target = $(target);
            that.context = context;
            that.shim.show(target);
        },

        close: function() {
            this.context = this.target = null;
            this.shim.hide();
        },

        openFor: function(target) {
            var that = this,
                context = target.data(CONTEXT_DATA);

            that.open(target, context);
            that.trigger(OPEN, { target: target, context: context });
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.shim.destroy();
        },

        _click: function(e) {
            if (e.isDefaultPrevented()) {
                return;
            }

            var currentTarget = $(e.currentTarget);
            var action = currentTarget.data("action");

            if (action) {
                kendo.getter(action)(window)({
                    target: this.target,
                    context: this.context
                });
            }

            this.trigger(COMMAND, { target: this.target, context: this.context, currentTarget: currentTarget });

            e.preventDefault();
            this.close();
            this.trigger(CLOSE);
        },

        _resize: function() {
            this.shim.hide();
        }
    });

    ui.plugin(ActionSheet);
})(window.kendo.jQuery);

return window.kendo;

}, typeof define == 'function' && define.amd ? define : function(_, f){ f(); });