/*
* Kendo UI Complete v2014.1.318 (http://kendoui.com)
* Copyright 2014 Telerik AD. All rights reserved.
*
* Kendo UI Complete commercial licenses may be obtained at
* http://www.telerik.com/purchase/license-agreement/kendo-ui-complete
* If you do not own a commercial license, this file shall be governed by the trial license terms.
*/
(function(f, define){
    define([ "./kendo.mobile.application" ], f);
})(function(){

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Widget = ui.Widget,
        EXPANED_PANE_SHIM = "<div class='km-expanded-pane-shim' />",
        View = ui.View;

    var SplitView = View.extend({
        init: function(element, options) {
            var that = this, pane;

            Widget.fn.init.call(that, element, options);
            element = that.element;

            $.extend(that, options);

            that._id();
            that._layout();
            that._overlay();
            that._style();
            kendo.mobile.init(element.children(kendo.roleSelector("modalview")));

            that.panes = [];
            that._paramsHistory = [];

            that.element.children(kendo.roleSelector("pane")).each(function() {
                pane = kendo.initWidget(this, {}, ui.roles);
                that.panes.push(pane);
            });

            that.expandedPaneShim = $(EXPANED_PANE_SHIM).appendTo(that.element);

            that._shimUserEvents = new kendo.UserEvents(that.expandedPaneShim, {
                tap: function() {
                    that.collapsePanes();
                }
            });
        },

        options: {
            name: "SplitView",
            style: "horizontal"
        },

        expandPanes: function() {
            this.element.addClass("km-expanded-splitview");
        },

        collapsePanes: function() {
            this.element.removeClass("km-expanded-splitview");
        },

        // Implement view interface
        _layout: function() {
            var that = this,
                element = that.element;

            element.data("kendoView", that).addClass("km-view km-splitview");

            that.transition = kendo.attrValue(element, "transition");
            $.extend(that, { header: [], footer: [], content: element });
        },

        _style: function () {
            var style = this.options.style,
                element = this.element,
                styles;

            if (style) {
                styles = style.split(" ");
                $.each(styles, function () {
                    element.addClass("km-split-" + this);
                });
            }
        },

        showStart: function() {
            var that = this;
            that.element.css("display", "");

            if (!that.inited) {
                that.inited = true;
                $.each(that.panes, function() {
                    if (this.options.initial) {
                        this.navigateToInitial();
                    } else {
                        this.navigate("");
                    }
                });
                that.trigger("init", {view: that});
            }

            that.trigger("show", {view: that});
        }
    });

    ui.plugin(SplitView);
})(window.kendo.jQuery);

return window.kendo;

}, typeof define == 'function' && define.amd ? define : function(_, f){ f(); });