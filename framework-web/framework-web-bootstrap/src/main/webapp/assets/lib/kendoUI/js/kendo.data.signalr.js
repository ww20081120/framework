/*
* Kendo UI Complete v2014.1.318 (http://kendoui.com)
* Copyright 2014 Telerik AD. All rights reserved.
*
* Kendo UI Complete commercial licenses may be obtained at
* http://www.telerik.com/purchase/license-agreement/kendo-ui-complete
* If you do not own a commercial license, this file shall be governed by the trial license terms.
*/
(function(f, define){
    define([ "./kendo.data" ], f);
})(function(){

(function() {
    kendo.data.transports.signalr = kendo.data.RemoteTransport.extend({
        init: function (options) {
            var signalr = options && options.signalr ? options.signalr : {};

            var promise = signalr.promise;

            if (!promise) {
                throw new Error('The "promise" option must be set.');
            }

            if (typeof promise.done != "function" || typeof promise.fail != "function") {
                throw new Error('The "promise" option must be a Promise.');
            }

            this.promise = promise;

            var hub = signalr.hub;

            if (!hub) {
                throw new Error('The "hub" option must be set.');
            }

            if (typeof hub.on != "function" || typeof hub.invoke != "function") {
                throw new Error('The "hub" option is not a valid SignalR hub proxy.');
            }

            this.hub = hub;

            kendo.data.RemoteTransport.fn.init.call(this, options);
        },

        push: function(callbacks) {
            var client = this.options.signalr.client || {};

            if (client.create) {
                this.hub.on(client.create, callbacks.pushCreate);
            }

            if (client.update) {
                this.hub.on(client.update, callbacks.pushUpdate);
            }

            if (client.destroy) {
                this.hub.on(client.destroy, callbacks.pushDestroy);
            }
        },

        _crud: function(options, type) {
            var hub = this.hub;

            var server = this.options.signalr.server;

            if (!server || !server[type]) {
                throw new Error(kendo.format('The "server.{0}" option must be set.', type));
            }

            var args = [server[type]];

            var data = this.parameterMap(options.data, type);

            if (!$.isEmptyObject(data)) {
                args.push(data);
            }

            this.promise.done(function() {
                hub.invoke.apply(hub, args)
                          .done(options.success)
                          .fail(options.error);
            });
        },

        read: function(options) {
            this._crud(options, "read");
        },

        create: function(options) {
            this._crud(options, "create");
        },

        update: function(options) {
            this._crud(options, "update");
        },

        destroy: function(options) {
            this._crud(options, "destroy");
        }
    });
})();

return window.kendo;

}, typeof define == 'function' && define.amd ? define : function(_, f){ f(); });