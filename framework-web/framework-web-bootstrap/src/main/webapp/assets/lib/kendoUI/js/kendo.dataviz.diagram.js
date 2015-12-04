/*
* Kendo UI Complete v2014.1.318 (http://kendoui.com)
* Copyright 2014 Telerik AD. All rights reserved.
*
* Kendo UI Complete commercial licenses may be obtained at
* http://www.telerik.com/purchase/license-agreement/kendo-ui-complete
* If you do not own a commercial license, this file shall be governed by the trial license terms.
*/
(function(f, define){
    define([ "./kendo.data", "./kendo.draganddrop", "./kendo.userevents", "./kendo.mobile.scroller", "./kendo.core", "./kendo.dataviz.core" ], f);
})(function(){

(function ($, undefined) {
    var extend = $.extend,
        math = Math;

    function mwDelta(e) {
        var origEvent = e.originalEvent,
            delta = 0;

        if (origEvent.wheelDelta) {
            delta = -origEvent.wheelDelta / 40;
            delta = delta > 0 ? math.ceil(delta) : math.floor(delta);
        } else if (origEvent.detail) {
            delta = origEvent.detail;
        }

        return delta;
    }

    extend($.fn, {
        mousewheel: function (handler, options) {
            var o = extend({
                ns: ""
            }, options || {});

            var that = this,
                mousewheel_NS = "DOMMouseScroll" + o.ns + " mousewheel" + o.ns;

            that.on(mousewheel_NS, function (e) {
                e.data = { delta: mwDelta(e) };

                handler(e);
            });

            return that;
        }
    });
})(window.kendo.jQuery);

(function ($, undefined) {
    var kendo = window.kendo,
        diagram = kendo.dataviz.diagram = {},
        Class = kendo.Class,
        deepExtend = kendo.deepExtend,
        isArray = $.isArray,
        EPSILON = 1e-06;

    /*-------------------Diverse utilities----------------------------*/
    var Utils = {
    };

    deepExtend(Utils, {
        isNearZero: function (num) {
            return Math.abs(num) < EPSILON;
        },
        isDefined: function (obj) {
            return typeof obj !== 'undefined';
        },

        isUndefined: function (obj) {
            return (typeof obj === 'undefined') || obj === null;
        },
        /**
         * Returns whether the given object is an object or a value.
         */
        isObject: function (obj) {
            return obj === Object(obj);
        },
        /**
         * Returns whether the object has a property with the given name.
         */
        has: function (obj, key) {
            return Object.hasOwnProperty.call(obj, key);
        },
        /**
         * Returns whether the given object is a string.
         */
        isString: function (obj) {
            return Object.prototype.toString.call(obj) == '[object String]';
        },
        isBoolean: function (obj) {
            return Object.prototype.toString.call(obj) == '[object Boolean]';
        },
        isType: function (obj, type) {
            return Object.prototype.toString.call(obj) == '[object ' + type + ']';
        },
        /**
         * Returns whether the given object is a number.
         */
        isNumber: function (obj) {
            return !isNaN(parseFloat(obj)) && isFinite(obj);
        },
        /**
         * Return whether the given object (array or dictionary).
         */
        isEmpty: function (obj) {
            if (obj === null) {
                return true;
            }
            if (isArray(obj) || Utils.isString(obj)) {
                return obj.length === 0;
            }
            for (var key in obj) {
                if (Utils.has(obj, key)) {
                    return false;
                }
            }
            return true;
        },
        simpleExtend: function(destination, source) {
            if(!Utils.isObject(source)) {
                return;
            }

            for(var name in source) {
                destination[name] = source[name];
            }
        },
        /**
         * Returns an array of the specified size and with each entry set to the given value.
         * @param size
         * @param value
         * @returns {Array}
         */
        initArray: function createIdArray(size, value) {
            var array = [];
            for (var i = 0; i < size; ++i) {
                array[i] = value;
            }
            return array;
        },
        serializePoints: function (points) {
            var res = [];
            for (var i = 0; i < points.length; i++) {
                var p = points[i];
                res.push(p.x + ";" + p.y);
            }
            return res.join(";");
        },
        deserializePoints: function (s) {
            var v = s.split(";"), points = [];
            if (v.length % 2 !== 0) {
                throw "Not an array of points.";
            }
            for (var i = 0; i < v.length; i += 2) {
                points.push(new diagram.Point(
                    parseInt(v[i], 10),
                    parseInt(v[i + 1], 10)
                ));
            }
            return points;
        },
        /**
         * Returns an integer within the given bounds.
         * @param lower The inclusive lower bound.
         * @param upper The exclusive upper bound.
         * @returns {number}
         */
        randomInteger: function (lower, upper) {
            return parseInt(Math.floor(Math.random() * upper) + lower, 10);
        } ,
        /*
         Depth-first traversal of the given node.
         */
        DFT: function (el, func) {
            func(el);
            if (el.childNodes) {
                for (var i = 0; i < el.childNodes.length; i++) {
                    var item = el.childNodes[i];
                    this.DFT(item, func);
                }
            }
        },
        /*
         Returns the angle in degrees for the given matrix
         */
        getMatrixAngle: function (m) {
            if (m === null || m.d === 0) {
                return 0;
            }
            return Math.atan2(m.b, m.d) * 180 / Math.PI;
        },

        /*
         Returns the scaling factors for the given matrix.
         */
        getMatrixScaling: function (m) {
            var sX = Math.sqrt(m.a * m.a + m.c * m.c);
            var sY = Math.sqrt(m.b * m.b + m.d * m.d);
            return [sX, sY];
        }

    });

    /**
     * The Range defines an array of equally separated numbers.
     * @param start The start-value of the Range.
     * @param stop The end-value of the Range.
     * @param step The separation between the values (default:1).
     * @returns {Array}
     */
    function Range(start, stop, step) {
        if (typeof start == 'undefined' || typeof stop == 'undefined') {
            return [];
        }
        if (step && Utils.sign(stop - start) != Utils.sign(step)) {
            throw "The sign of the increment should allow to reach the stop-value.";
        }
        step = step || 1;
        start = start || 0;
        stop = stop || start;
        if ((stop - start) / step === Infinity) {
            throw "Infinite range defined.";
        }
        var range = [], i = -1, j;

        function rangeIntegerScale(x) {
            var k = 1;
            while (x * k % 1) {
                k *= 10;
            }
            return k;
        }

        var k = rangeIntegerScale(Math.abs(step));
        start *= k;
        stop *= k;
        step *= k;
        if (start > stop && step > 0) {
            step = -step;
        }
        if (step < 0) {
            while ((j = start + step * ++i) >= stop) {
                range.push(j / k);
            }
        }
        else {
            while ((j = start + step * ++i) <= stop) {
                range.push(j / k);
            }
        }
        return range;
    }

    /*-------------------Diverse math functions----------------------------*/

    function findRadian(start, end) {
        if (start == end) {
            return 0;
        }
        var sngXComp = end.x - start.x,
            sngYComp = start.y - end.y,
            atan = Math.atan(sngXComp / sngYComp);
        if (sngYComp >= 0) {
            return sngXComp < 0 ? atan + (2 * Math.PI) : atan;
        }
        return atan + Math.PI;
    }

    Utils.sign = function(number) {
        return number ? number < 0 ? -1 : 1 : 0;
    };

    Utils.findAngle = function(center, end) {
        return findRadian(center, end) * 180 / Math.PI;
    };

    /*-------------------Array Helpers ----------------------------*/

    Utils.forEach = function(arr, iterator, thisRef) {
        for (var i = 0; i < arr.length; i++) {
            iterator.call(thisRef, arr[i], i, arr);
        }
    };

    Utils.any = function(arr, predicate) {
        for (var i = 0; i < arr.length; ++i) {
            if (predicate(arr[i])) {
                return arr[i];
            }
        }
        return null;
    };

    Utils.remove = function (arr, what) {
        var ax;
        while ((ax = Utils.indexOf(arr, what)) !== -1) {
            arr.splice(ax, 1);
        }
        return arr;
    };

    Utils.contains = function (arr, obj) {
        return Utils.indexOf(arr, obj) !== -1;
    };

    Utils.indexOf = function(arr, what) {
        return $.inArray(what, arr);
    };

    Utils.fold = function (list, iterator, acc, context) {
        var initial = arguments.length > 2;

        for (var i = 0; i < list.length; i++) {
            var value = list[i];
            if (!initial) {
                acc = value;
                initial = true;
            }
            else {
                acc = iterator.call(context, acc, value, i, list);
            }
        }

        if (!initial) {
            throw 'Reduce of empty array with no initial value';
        }

        return acc;
    };

    Utils.find = function (arr, iterator, context) {
        var result;
        Utils.any(arr, function (value, index, list) {
            if (iterator.call(context, value, index, list)) {
                result = value;
                return true;
            }
            return false;
        });
        return result;
    };

    Utils.first = function (arr, constraint, context) {
        if (arr.length === 0) {
            return null;
        }
        if (Utils.isUndefined(constraint)) {
            return arr[0];
        }

        return Utils.find(arr, constraint, context);
    };

    /**
     * Inserts the given element at the specified position and returns the result.
     */
    Utils.insert = function (arr, element, position) {
        arr.splice(position, 0, element);
        return arr;
    };

    Utils.all = function (arr, iterator, context) {
        var result = true;
        var value;

        for (var i = 0; i < arr.length; i++) {
            value = arr[i];
            result = result && iterator.call(context, value, i, arr);

            if (!result) {
                break;
            }
        }

        return result;
    };

    Utils.clear = function (arr) {
        arr.splice(0, arr.length);
    };

    /**
     * Sort the arrays on the basis of the first one (considered as keys and the other array as values).
     * @param a
     * @param b
     * @param sortfunc (optiona) sorting function for the values in the first array
     */
    Utils.bisort = function (a, b, sortfunc) {
        if (Utils.isUndefined(a)) {
            throw "First array is not specified.";
        }
        if (Utils.isUndefined(b)) {
            throw "Second array is not specified.";
        }
        if (a.length != b.length) {
            throw "The two arrays should have equal length";
        }

        var all = [], i;

        for (i = 0; i < a.length; i++) {
            all.push({ 'x': a[i], 'y': b[i] });
        }
        if (Utils.isUndefined(sortfunc)) {
            all.sort(function (m, n) {
                return m.x - n.x;
            });
        }
        else {
            all.sort(function (m, n) {
                return sortfunc(m.x, n.x);
            });
        }

        Utils.clear(a);
        Utils.clear(b);

        for (i = 0; i < all.length; i++) {
            a.push(all[i].x);
            b.push(all[i].y);
        }
    };

    Utils.addRange = function (arr, range) {
        arr.push.apply(arr, range);
    };

    var Easing = {
        easeInOut: function (pos) {
            return ((-Math.cos(pos * Math.PI) / 2) + 0.5);
        }
    };

    /**
     * An animation ticker driving an adapter which sets a particular
     * property in function of the tick.
     * @type {*}
     */
    var Ticker = kendo.Class.extend({
        init: function () {
            this.adapters = [];
            this.target = 0;
            this.tick = 0;
            this.interval = 20;
            this.duration = 800;
            this.lastTime = null;
            this.handlers = [];
            var _this = this;
            this.transition = Easing.easeInOut;
            this.timerDelegate = function () {
                _this.onTimerEvent();
            };
        },
        addAdapter: function (a) {
            this.adapters.push(a);
        },
        onComplete: function (handler) {
            this.handlers.push(handler);
        },
        removeHandler: function (handler) {
            this.handlers = $.grep(this.handlers, function (h) {
                return h !== handler;
            });
        },
        trigger: function () {
            var _this = this;
            if (this.handlers) {
                Utils.forEach(this.handlers, function (h) {
                    return h.call(_this.caller !== null ? _this.caller : _this);
                });
            }
        },
        onStep: function () {
        },
        seekTo: function (to) {
            this.seekFromTo(this.tick, to);
        },
        seekFromTo: function (from, to) {
            this.target = Math.max(0, Math.min(1, to));
            this.tick = Math.max(0, Math.min(1, from));
            this.lastTime = new Date().getTime();
            if (!this.intervalId) {
                this.intervalId = window.setInterval(this.timerDelegate, this.interval);
            }
        },
        stop: function () {
            if (this.intervalId) {
                window.clearInterval(this.intervalId);
                this.intervalId = null;

                //this.trigger.call(this);
                this.trigger();
                // this.next();
            }
        },
        play: function (origin) {
            if (this.adapters.length === 0) {
                return;
            }
            if (origin !== null) {
                this.caller = origin;
            }
            this.initState();
            this.seekFromTo(0, 1);
        },
        reverse: function () {
            this.seekFromTo(1, 0);
        },
        initState: function () {
            if (this.adapters.length === 0) {
                return;
            }
            for (var i = 0; i < this.adapters.length; i++) {
                this.adapters[i].initState();
            }
        },
        propagate: function () {
            var value = this.transition(this.tick);

            for (var i = 0; i < this.adapters.length; i++) {
                this.adapters[i].update(value);
            }
        },
        onTimerEvent: function () {
            var now = new Date().getTime();
            var timePassed = now - this.lastTime;
            this.lastTime = now;
            var movement = (timePassed / this.duration) * (this.tick < this.target ? 1 : -1);
            if (Math.abs(movement) >= Math.abs(this.tick - this.target)) {
                this.tick = this.target;
            } else {
                this.tick += movement;
            }

            try {
                this.propagate();
            } finally {
                this.onStep.call(this);
                if (this.target == this.tick) {
                    this.stop();
                }
            }
        }
    });

    kendo.deepExtend(diagram, {
        init: function (element) {
            kendo.init(element, diagram.ui);
        },

        Utils: Utils,
        Range: Range,
        Ticker: Ticker
    });
})(window.kendo.jQuery);

(function ($, undefined) {
    // Imports ================================================================
    var kendo = window.kendo,
        diagram = kendo.dataviz.diagram,
        Class = kendo.Class,
        deepExtend = kendo.deepExtend,
        dataviz = kendo.dataviz,
        Utils = diagram.Utils,
        Point = dataviz.Point2D,
        isFunction = kendo.isFunction,
        contains = Utils.contains,
        map = $.map;

    // Constants ==============================================================
    var HITTESTAREA = 3,
        EPSILON = 1e-06;

    deepExtend(Point.fn, {
        plus: function (p) {
            return new Point(this.x + p.x, this.y + p.y);
        },
        minus: function (p) {
            return new Point(this.x - p.x, this.y - p.y);
        },
        offset: function (value) {
            return new Point(this.x - value, this.y - value);
        },
        times: function (s) {
            return new Point(this.x * s, this.y * s);
        },
        normalize: function () {
            if (this.length() === 0) {
                return new Point();
            }
            return this.times(1 / this.length());
        },
        length: function () {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        },
        toString: function () {
            return "(" + this.x + "," + this.y + ")";
        },
        lengthSquared: function () {
            return (this.x * this.x + this.y * this.y);
        },
        middleOf: function MiddleOf(p, q) {
            return new Point(q.x - p.x, q.y - p.y).times(0.5).plus(p);
        },
        toPolar: function (useDegrees) {
            var factor = 1;
            if (useDegrees) {
                factor = 180 / Math.PI;
            }
            var a = Math.atan2(Math.abs(this.y), Math.abs(this.x));
            var halfpi = Math.PI / 2;
            var len = this.length();
            if (this.x === 0) {
                // note that the angle goes down and not the usual mathematical convention

                if (this.y === 0) {
                    return new Polar(0, 0);
                }
                if (this.y > 0) {
                    return new Polar(len, factor * halfpi);
                }
                if (this.y < 0) {
                    return new Polar(len, factor * 3 * halfpi);
                }
            }
            else if (this.x > 0) {
                if (this.y === 0) {
                    return new Polar(len, 0);
                }
                if (this.y > 0) {
                    return new Polar(len, factor * a);
                }
                if (this.y < 0) {
                    return new Polar(len, factor * (4 * halfpi - a));
                }
            }
            else {
                if (this.y === 0) {
                    return new Polar(len, 2 * halfpi);
                }
                if (this.y > 0) {
                    return new Polar(len, factor * (2 * halfpi - a));
                }
                if (this.y < 0) {
                    return new Polar(len, factor * (2 * halfpi + a));
                }
            }
        },
        isOnLine: function (from, to) {
            if (from.x > to.x) { // from must be the leftmost point
                var temp = to;
                to = from;
                from = temp;
            }
            var r1 = new Rect(from.x, from.y).inflate(HITTESTAREA, HITTESTAREA),
                r2 = new Rect(to.x, to.y).inflate(HITTESTAREA, HITTESTAREA), o1, u1;
            if (r1.union(r2).contains(this)) {
                if (from.x === to.x || from.y === to.y) {
                    return true;
                }
                else if (from.y < to.y) {
                    o1 = r1.x + (((r2.x - r1.x) * (this.y - (r1.y + r1.height))) / ((r2.y + r2.height) - (r1.y + r1.height)));
                    u1 = (r1.x + r1.width) + ((((r2.x + r2.width) - (r1.x + r1.width)) * (this.y - r1.y)) / (r2.y - r1.y));
                }
                else {
                    o1 = r1.x + (((r2.x - r1.x) * (this.y - r1.y)) / (r2.y - r1.y));
                    u1 = (r1.x + r1.width) + ((((r2.x + r2.width) - (r1.x + r1.width)) * (this.y - (r1.y + r1.height))) / ((r2.y + r2.height) - (r1.y + r1.height)));
                }
                return (this.x > o1 && this.x < u1);
            }
            return false;
        }
    });

    deepExtend(Point, {
        parse: function (str) {
            var tempStr = str.slice(1, str.length - 1),
                xy = tempStr.split(","),
                x = parseInt(xy[0], 10),
                y = parseInt(xy[1], 10);
            if (!isNaN(x) && !isNaN(y)) {
                return new Point(x, y);
            }
        }
    });

    /**
     * Structure combining a Point with two additional points representing the handles or tangents attached to the first point.
     * If the additional points are null or equal to the first point the path will be sharp.
     * Left and right correspond to the direction of the underlying path.
     */
    var PathDefiner = Class.extend(
        {
            init: function (p, left, right) {
                this.point = p;
                this.left = left;
                this.right = right;
            }
        }
    );

    /**
     * Defines a rectangular region.
     */
    var Rect = Class.extend({
        init: function (x, y, width, height) {
            this.x = x || 0;
            this.y = y || 0;
            this.width = width || 0;
            this.height = height || 0;
        },
        contains: function (point) {
            return ((point.x >= this.x) && (point.x <= (this.x + this.width)) && (point.y >= this.y) && (point.y <= (this.y + this.height)));
        },
        inflate: function (dx, dy) {
            if (dy === undefined) {
                dy = dx;
            }

            this.x -= dx;
            this.y -= dy;
            this.width += 2 * dx + 1;
            this.height += 2 * dy + 1;
            return this;
        },
        offset: function (dx, dy) {
            var x = dx, y = dy;
            if (dx instanceof Point) {
                x = dx.x;
                y = dx.y;
            }
            this.x += x;
            this.y += y;
            return this;
        },
        union: function (r) {
            var x1 = Math.min(this.x, r.x);
            var y1 = Math.min(this.y, r.y);
            var x2 = Math.max((this.x + this.width), (r.x + r.width));
            var y2 = Math.max((this.y + this.height), (r.y + r.height));
            return new Rect(x1, y1, x2 - x1, y2 - y1);
        },
        center: function () {
            return new Point(this.x + this.width / 2, this.y + this.height / 2);
        },
        top: function () {
            return new Point(this.x + this.width / 2, this.y);
        },
        right: function () {
            return new Point(this.x + this.width, this.y + this.height / 2);
        },
        bottom: function () {
            return new Point(this.x + this.width / 2, this.y + this.height);
        },
        left: function () {
            return new Point(this.x, this.y + this.height / 2);
        },
        topLeft: function () {
            return new Point(this.x, this.y);
        },
        topRight: function () {
            return new Point(this.x + this.width, this.y);
        },
        bottomLeft: function () {
            return new Point(this.x, this.y + this.height);
        },
        bottomRight: function () {
            return new Point(this.x + this.width, this.y + this.height);
        },
        clone: function () {
            return new Rect(this.x, this.y, this.width, this.height);
        },
        isEmpty: function () {
            return !this.width && !this.height;
        },
        equals: function (rect) {
            return this.x === rect.x && this.y === rect.y && this.width === rect.width && this.height === rect.height;
        },
        rotatedBounds: function (angle) {
            var rect = this.clone(),
                points = this.rotatedPoints(angle),
                tl = points[0],
                tr = points[1],
                br = points[2],
                bl = points[3];

            rect.x = Math.min(br.x, tl.x, tr.x, bl.x);
            rect.y = Math.min(br.y, tl.y, tr.y, bl.y);
            rect.width = Math.max(br.x, tl.x, tr.x, bl.x) - rect.x;
            rect.height = Math.max(br.y, tl.y, tr.y, bl.y) - rect.y;

            return rect;
        },
        rotatedPoints: function (angle) {
            var rect = this,
                c = rect.center(),
                br = rect.bottomRight().rotate(c, 360 - angle),
                tl = rect.topLeft().rotate(c, 360 - angle),
                tr = rect.topRight().rotate(c, 360 - angle),
                bl = rect.bottomLeft().rotate(c, 360 - angle);

            return [tl, tr, br, bl];
        },
        toString: function (delimiter) {
            delimiter = delimiter || " ";

            return this.x + delimiter + this.y + delimiter + this.width + delimiter + this.height;
        },
        scale: function (scaleX, scaleY, staicPoint, adornerCenter, angle) {
            var tl = this.topLeft();
            var thisCenter = this.center();
            tl.rotate(thisCenter, 360 - angle).rotate(adornerCenter, angle);

            var delta = staicPoint.minus(tl);
            var scaled = new Point(delta.x * scaleX, delta.y * scaleY);
            var position = delta.minus(scaled);
            tl = tl.plus(position);
            tl.rotate(adornerCenter, 360 - angle).rotate(thisCenter, angle);

            this.x = tl.x;
            this.y = tl.y;

            this.width *= scaleX;
            this.height *= scaleY;
        }
    });

    var Size = Class.extend({
        init: function (width, height) {
            this.width = width;
            this.height = height;
        }
    });

    Size.prototype.Empty = new Size(0, 0);

    Rect.toRect = function (rect) {
        if (!(rect instanceof Rect)) {
            rect = new Rect(rect.x, rect.y, rect.width, rect.height);
        }

        return rect;
    };

    Rect.empty = function () {
        return new Rect(0, 0, 0, 0);
    };

    Rect.fromPoints = function (p, q) {
        if (isNaN(p.x) || isNaN(p.y) || isNaN(q.x) || isNaN(q.y)) {
            throw "Some values are NaN.";
        }
        return new Rect(Math.min(p.x, q.x), Math.min(p.y, q.y), Math.abs(p.x - q.x), Math.abs(p.y - q.y));
    };

    function isNearZero(num) {
        return Math.abs(num) < EPSILON;
    }

    function intersectLine(start1, end1, start2, end2, isSegment) {
        var tangensdiff = ((end1.x - start1.x) * (end2.y - start2.y)) - ((end1.y - start1.y) * (end2.x - start2.x));
        if (isNearZero(tangensdiff)) {
            //parallel lines
            return;
        }

        var num1 = ((start1.y - start2.y) * (end2.x - start2.x)) - ((start1.x - start2.x) * (end2.y - start2.y));
        var num2 = ((start1.y - start2.y) * (end1.x - start1.x)) - ((start1.x - start2.x) * (end1.y - start1.y));
        var r = num1 / tangensdiff;
        var s = num2 / tangensdiff;

        if (isSegment && (r < 0 || r > 1 || s < 0 || s > 1)) {
            //r < 0 => line 1 is below line 2
            //r > 1 => line 1 is above line 2
            //s < 0 => line 2 is below line 1
            //s > 1 => line 2 is above line 1
            return;
        }

        return new Point(start1.x + (r * (end1.x - start1.x)), start1.y + (r * (end1.y - start1.y)));
    }

    var Intersect = {
        lines: function (start1, end1, start2, end2) {
            return intersectLine(start1, end1, start2, end2);
        },
        segments: function (start1, end1, start2, end2) {
            return intersectLine(start1, end1, start2, end2, true);
        },
        rectWithLine: function (rect, start, end) {
            return  Intersect.segments(start, end, rect.topLeft(), rect.topRight()) ||
                Intersect.segments(start, end, rect.topRight(), rect.bottomRight()) ||
                Intersect.segments(start, end, rect.bottomLeft(), rect.bottomRight()) ||
                Intersect.segments(start, end, rect.topLeft(), rect.bottomLeft());
        },
        rects: function (rect1, rect2, angle) {
            var tl = rect2.topLeft(),
                tr = rect2.topRight(),
                bl = rect2.bottomLeft(),
                br = rect2.bottomRight();
            var center = rect2.center();
            if (angle) {
                tl = tl.rotate(center, angle);
                tr = tr.rotate(center, angle);
                bl = bl.rotate(center, angle);
                br = br.rotate(center, angle);
            }

            var intersect = rect1.contains(tl) ||
                rect1.contains(tr) ||
                rect1.contains(bl) ||
                rect1.contains(br) ||
                Intersect.rectWithLine(rect1, tl, tr) ||
                Intersect.rectWithLine(rect1, tl, bl) ||
                Intersect.rectWithLine(rect1, tr, br) ||
                Intersect.rectWithLine(rect1, bl, br);

            if (!intersect) {//last possible case is rect1 to be completely within rect2
                tl = rect1.topLeft();
                tr = rect1.topRight();
                bl = rect1.bottomLeft();
                br = rect1.bottomRight();

                if (angle) {
                    var reverseAngle = 360 - angle;
                    tl = tl.rotate(center, reverseAngle);
                    tr = tr.rotate(center, reverseAngle);
                    bl = bl.rotate(center, reverseAngle);
                    br = br.rotate(center, reverseAngle);
                }

                intersect = rect2.contains(tl) ||
                    rect2.contains(tr) ||
                    rect2.contains(bl) ||
                    rect2.contains(br);
            }

            return intersect;
        }
    };

    /**
     * Aligns two rectangles, where one is the container and the other is content.
     */
    var RectAlign = Class.extend({
        init: function (container) {
            this.container = Rect.toRect(container);
        },

        align: function (content, alignment) {
            var alignValues = alignment.toLowerCase().split(" ");

            for (var i = 0; i < alignValues.length; i++) {
                content = this._singleAlign(content, alignValues[i]);
            }

            return content;
        },
        _singleAlign: function (content, alignment) {
            if (isFunction(this[alignment])) {
                return this[alignment](content);
            }
            else {
                return content;
            }
        },

        left: function (content) {
            return this._align(content, this._left);
        },
        center: function (content) {
            return this._align(content, this._center);
        },
        right: function (content) {
            return this._align(content, this._right);
        },
        stretch: function (content) {
            return this._align(content, this._stretch);
        },
        top: function (content) {
            return this._align(content, this._top);
        },
        middle: function (content) {
            return this._align(content, this._middle);
        },
        bottom: function (content) {
            return this._align(content, this._bottom);
        },

        _left: function (container, content) {
            content.x = container.x;
        },
        _center: function (container, content) {
            content.x = ((container.width - content.width) / 2) || 0;
        },
        _right: function (container, content) {
            content.x = container.width - content.width;
        },
        _top: function (container, content) {
            content.y = container.y;
        },
        _middle: function (container, content) {
            content.y = ((container.height - content.height) / 2) || 0;
        },
        _bottom: function (container, content) {
            content.y = container.height - content.height;
        },
        _stretch: function (container, content) {
            content.x = 0;
            content.y = 0;
            content.height = container.height;
            content.width = container.width;
        },
        _align: function (content, alignCalc) {
            content = Rect.toRect(content);
            alignCalc(this.container, content);

            return content;
        }
    });

    var Polar = Class.extend({
        init: function (r, a) {
            this.r = r;
            this.angle = a;
        }
    });

    /**
     * SVG transformation matrix.
     */
    var Matrix = Class.extend({
        init: function (a, b, c, d, e, f) {
            this.a = a || 0;
            this.b = b || 0;
            this.c = c || 0;
            this.d = d || 0;
            this.e = e || 0;
            this.f = f || 0;
        },
        plus: function (m) {
            this.a += m.a;
            this.b += m.b;
            this.c += m.c;
            this.d += m.d;
            this.e += m.e;
            this.f += m.f;
        },
        minus: function (m) {
            this.a -= m.a;
            this.b -= m.b;
            this.c -= m.c;
            this.d -= m.d;
            this.e -= m.e;
            this.f -= m.f;
        },
        times: function (m) {
            return new Matrix(
                this.a * m.a + this.c * m.b,
                this.b * m.a + this.d * m.b,
                this.a * m.c + this.c * m.d,
                this.b * m.c + this.d * m.d,
                this.a * m.e + this.c * m.f + this.e,
                this.b * m.e + this.d * m.f + this.f
            );
        },
        apply: function (p) {
            return new Point(this.a * p.x + this.c * p.y + this.e, this.b * p.x + this.d * p.y + this.f);
        },
        applyRect: function (r) {
            return Rect.fromPoints(this.apply(r.topLeft()), this.apply(r.bottomRight()));
        },
        toString: function () {
            return "matrix(" + this.a + " " + this.b + " " + this.c + " " + this.d + " " + this.e + " " + this.f + ")";
        }
    });

    deepExtend(Matrix, {
        fromSVGMatrix: function (vm) {
            var m = new Matrix();
            m.a = vm.a;
            m.b = vm.b;
            m.c = vm.c;
            m.d = vm.d;
            m.e = vm.e;
            m.f = vm.f;
            return m;
        },
        fromMatrixVector: function (v) {
            var m = new Matrix();
            m.a = v.a;
            m.b = v.b;
            m.c = v.c;
            m.d = v.d;
            m.e = v.e;
            m.f = v.f;
            return m;
        },
        fromList: function (v) {
            if (v.length !== 6) {
                throw "The given list should consist of six elements.";
            }
            var m = new Matrix();
            m.a = v[0];
            m.b = v[1];
            m.c = v[2];
            m.d = v[3];
            m.e = v[4];
            m.f = v[5];
            return m;
        },
        translation: function (x, y) {
            var m = new Matrix();
            m.a = 1;
            m.b = 0;
            m.c = 0;
            m.d = 1;
            m.e = x;
            m.f = y;
            return m;
        },
        unit: function () {
            return new Matrix(1, 0, 0, 1, 0, 0);
        },
        rotation: function (angle, x, y) {
            var m = new Matrix();
            m.a = Math.cos(angle * Math.PI / 180);
            m.b = Math.sin(angle * Math.PI / 180);
            m.c = -m.b;
            m.d = m.a;
            m.e = (x - x * m.a + y * m.b) || 0;
            m.f = (y - y * m.a - x * m.b) || 0;
            return m;
        },
        scaling: function (scaleX, scaleY) {
            var m = new Matrix();
            m.a = scaleX;
            m.b = 0;
            m.c = 0;
            m.d = scaleY;
            m.e = 0;
            m.f = 0;
            return m;
        },
        parse: function (v) {
            var parts, nums;
            if (v) {
                v = v.trim();
                // of the form "matrix(...)"
                if (v.slice(0, 6).toLowerCase() === "matrix") {
                    nums = v.slice(7, v.length - 1).trim();
                    parts = nums.split(",");
                    if (parts.length === 6) {
                        return Matrix.fromList(map(parts, function (p) {
                            return parseFloat(p);
                        }));
                    }
                    parts = nums.split(" ");
                    if (parts.length === 6) {
                        return Matrix.fromList(map(parts, function (p) {
                            return parseFloat(p);
                        }));
                    }
                }
                // of the form "(...)"
                if (v.slice(0, 1) === "(" && v.slice(v.length - 1) === ")") {
                    v = v.substr(1, v.length - 1);
                }
                if (v.indexOf(",") > 0) {
                    parts = v.split(",");
                    if (parts.length === 6) {
                        return Matrix.fromList(map(parts, function (p) {
                            return parseFloat(p);
                        }));
                    }
                }
                if (v.indexOf(" ") > 0) {
                    parts = v.split(" ");
                    if (parts.length === 6) {
                        return Matrix.fromList(map(parts, function (p) {
                            return parseFloat(p);
                        }));
                    }
                }
            }
            return parts;
        }
    });

    /**
     * SVG transformation represented as a vector.
     */
    var MatrixVector = Class.extend({
        init: function (a, b, c, d, e, f) {
            this.a = a || 0;
            this.b = b || 0;
            this.c = c || 0;
            this.d = d || 0;
            this.e = e || 0;
            this.f = f || 0;
        },
        fromMatrix: function FromMatrix(m) {
            var v = new MatrixVector();
            v.a = m.a;
            v.b = m.b;
            v.c = m.c;
            v.d = m.d;
            v.e = m.e;
            v.f = m.f;
            return v;
        }
    });

    /**
     * Returns a value with Gaussian (normal) distribution.
     * @param mean The mean value of the distribution.
     * @param deviation The deviation (spreading at half-height) of the distribution.
     * @returns {number}
     */
    function normalVariable(mean, deviation) {
        var x, y, r;
        do {
            x = Math.random() * 2 - 1;
            y = Math.random() * 2 - 1;
            r = x * x + y * y;
        }
        while (!r || r > 1);
        return mean + deviation * x * Math.sqrt(-2 * Math.log(r) / r);
    }

    /**
     * Returns a random identifier which can be used as an ID of objects, eventually augmented with a prefix.
     * @returns {string}
     */
    function randomId(length) {
        if (Utils.isUndefined(length)) {
            length = 10;
        }
        // old version return Math.floor((1 + Math.random()) * 0x1000000).toString(16).substring(1);
        var result = '';
        var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        for (var i = length; i > 0; --i) {
            result += chars[Math.round(Math.random() * (chars.length - 1))];
        }
        return result;
    }

    var Geometry = {

        /**
         * Returns the squared distance to the line defined by the two given Points.
         * @param p An arbitrary Point.
         * @param a An endpoint of the line or segment.
         * @param b The complementary endpoint of the line or segment.
         */
        _distanceToLineSquared: function (p, a, b) {
            function d2(pt1, pt2) {
                return (pt1.x - pt2.x) * (pt1.x - pt2.x) + (pt1.y - pt2.y) * (pt1.y - pt2.y);
            }

            if (a === b) { // returns the distance of p to a
                return d2(p, a);
            }

            var vx = b.x - a.x,
                vy = b.y - a.y,
                dot = (p.x - a.x) * vx + (p.y - a.y) * vy;
            if (dot < 0) {
                return d2(a, p); // sits on side of a
            }

            dot = (b.x - p.x) * vx + (b.y - p.y) * vy;
            if (dot < 0) {
                return d2(b, p); // sits on side of b
            }
            // regular case, use crossproduct to get the sine out
            dot = (b.x - p.x) * vy - (b.y - p.y) * vx;
            return dot * dot / (vx * vx + vy * vy);
        },

        /**
         * Returns the distance to the line defined by the two given Points.
         * @param p An arbitrary Point.
         * @param a An endpoint of the line or segment.
         * @param b The complementary endpoint of the line or segment.
         */
        distanceToLine: function (p, a, b) {
            return Math.sqrt(this._distanceToLineSquared(p, a, b));
        },

        /**
         * Returns the distance of the given points to the polyline defined by the points.
         * @param p An arbitrary point.
         * @param points The points defining the polyline.
         * @returns {Number}
         */
        distanceToPolyline: function (p, points) {
            var minimum = Number.MAX_VALUE;
            if (Utils.isUndefined(points) || points.length === 0) {
                return Number.MAX_VALUE;
            }
            for (var s = 0; s < points.length - 1; s++) {
                var p1 = points[s];
                var p2 = points[s + 1];

                var d = this._distanceToLineSquared(p, p1, p2);
                if (d < minimum) {
                    minimum = d;
                }
            }
            return Math.sqrt(minimum);
        }
    };

    /*---------------The HashTable structure--------------------------------*/

    /**
     * Represents a collection of key-value pairs that are organized based on the hash code of the key.
     * _buckets[hashId] = {key: key, value:...}
     * Important: do not use the standard Array access method, use the get/set methods instead.
     * See http://en.wikipedia.org/wiki/Hash_table
     */
    var HashTable = kendo.Class.extend({
        init: function () {
            this._buckets = [];
            this.length = 0;
        },

        /**
         * Adds the literal object with the given key (of the form {key: key,....}).
         */
        add: function (key, value) {

            var obj = this._createGetBucket(key);
            if (Utils.isDefined(value)) {
                obj.value = value;
            }
            return obj;
        },

        /**
         * Gets the literal object with the given key.
         */
        get: function (key) {
            if (this._bucketExists(key)) {
                return this._createGetBucket(key);
            }
            return null;
        },

        /**
         * Set the key-value pair.
         * @param key The key of the entry.
         * @param value The value to set. If the key already exists the value will be overwritten.
         */
        set: function (key, value) {
            this.add(key, value);
        },

        /**
         * Determines whether the HashTable contains a specific key.
         */
        containsKey: function (key) {
            return this._bucketExists(key);
        },

        /**
         * Removes the element with the specified key from the hashtable.
         * Returns the removed bucket.
         */
        remove: function (key) {
            if (this._bucketExists(key)) {
                var hashId = this._hash(key);
                delete this._buckets[hashId];
                this.length--;
                return key;
            }
        },

        /**
         * Foreach with an iterator working on the key-value pairs.
         * @param func
         */
        forEach: function (func) {
            var hashes = this._hashes();
            for (var i = 0, len = hashes.length; i < len; i++) {
                var hash = hashes[i];
                var bucket = this._buckets[hash];
                if (Utils.isUndefined(bucket)) {
                    continue;
                }
                func(bucket);
            }
        },

        /**
         * Returns a (shallow) clone of the current HashTable.
         * @returns {HashTable}
         */
        clone: function () {
            var ht = new HashTable();
            var hashes = this._hashes();
            for (var i = 0, len = hashes.length; i < len; i++) {
                var hash = hashes[i];
                var bucket = this._buckets[hash];
                if (Utils.isUndefined(bucket)) {
                    continue;
                }
                ht.add(bucket.key, bucket.value);
            }
            return ht;
        },

        /**
         * Returns the hashes of the buckets.
         * @returns {Array}
         * @private
         */
        _hashes: function () {
            var hashes = [];
            for (var hash in this._buckets) {
                if (this._buckets.hasOwnProperty(hash)) {
                    hashes.push(hash);
                }
            }
            return hashes;
        },

        _bucketExists: function (key) {
            var hashId = this._hash(key);
            return Utils.isDefined(this._buckets[hashId]);
        },

        /**
         * Returns-adds the createGetBucket with the given key. If not present it will
         * be created and returned.
         * A createGetBucket is a literal object of the form {key: key, ...}.
         */
        _createGetBucket: function (key) {
            var hashId = this._hash(key);
            var bucket = this._buckets[hashId];
            if (Utils.isUndefined(bucket)) {
                bucket = { key: key };
                this._buckets[hashId] = bucket;
                this.length++;
            }
            return bucket;
        },

        /**
         * Hashing of the given key.
         */
        _hash: function (key) {
            if (Utils.isNumber(key)) {
                return key;
            }
            if (Utils.isString(key)) {
                return this._hashString(key);
            }
            if (Utils.isObject(key)) {
                return this._objectHashId(key);
            }
            throw "Unsupported key type.";
        },

        /**
         * Hashing of a string.
         */
        _hashString: function (s) {
            // see for example http://stackoverflow.com/questions/7616461/generate-a-hash-from-string-in-javascript-jquery
            var result = 0;
            if (s.length === 0) {
                return result;
            }
            for (var i = 0; i < s.length; i++) {
                var ch = s.charCodeAt(i);
                result = ((result * 32) - result) + ch;
            }
            return result;
        },

        /**
         * Returns the unique identifier for an object. This is automatically assigned and add on the object.
         */
        _objectHashId: function (key) {
            var id = key._hashId;
            if (Utils.isUndefined(id)) {
                id = randomId();
                key._hashId = id;
            }
            return id;
        }
    });

    /*---------------The Dictionary structure--------------------------------*/

    /**
     * Represents a collection of key-value pairs.
     * Important: do not use the standard Array access method, use the get/Set methods instead.
     */
    var Dictionary = kendo.Observable.extend({
        /**
         * Initializes a new instance of the Dictionary class.
         * @param dictionary Loads the content of the given dictionary into this new one.
         */
        init: function (dictionary) {
            var that = this;
            kendo.Observable.fn.init.call(that);
            this._hashTable = new HashTable();
            this.length = 0;
            if (Utils.isDefined(dictionary)) {
                if ($.isArray(dictionary)) {
                    for (var i = 0; i < dictionary.length; i++) {
                        this.add(dictionary[i]);
                    }
                } else {
                    dictionary.forEach(function (k, v) {
                        this.add(k, v);
                    }, this);
                }
            }
        },

        /**
         * Adds a key-value to the dictionary.
         * If the key already exists this will assign the given value to the existing entry.
         */
        add: function (key, value) {
            var entry = this._hashTable.get(key);
            if (!entry) {
                entry = this._hashTable.add(key);
                this.length++;
                this.trigger('changed');
            }
            entry.value = value;
        },

        /**
         * Set the key-value pair.
         * @param key The key of the entry.
         * @param value The value to set. If the key already exists the value will be overwritten.
         */
        set: function (key, value) {
            this.add(key, value);
        },

        /**
         * Gets the value associated with the given key in the dictionary.
         */
        get: function (key) {
            var entry = this._hashTable.get(key);
            if (entry) {
                return entry.value;
            }
            throw new Error("Cannot find key " + key);
        },

        /**
         * Returns whether the dictionary contains the given key.
         */
        containsKey: function (key) {
            return this._hashTable.containsKey(key);
        },

        /**
         * Removes the element with the specified key from the dictionary.
         */
        remove: function (key) {
            if (this.containsKey(key)) {
                this.trigger("changed");
                this.length--;
                return this._hashTable.remove(key);
            }
        },

        /**
         * The functional gets the key and value as parameters.
         */
        forEach: function (func, thisRef) {
            this._hashTable.forEach(function (entry) {
                func.call(thisRef, entry.key, entry.value);
            });
        },

        /**
         * Same as forEach except that only the value is passed to the functional.
         */
        forEachValue: function (func, thisRef) {
            this._hashTable.forEach(function (entry) {
                func.call(thisRef, entry.value);
            });
        },

        /**
         * Calls a defined callback function for each key in the dictionary.
         */
        forEachKey: function (func, thisRef) {
            this._hashTable.forEach(function (entry) {
                func.call(thisRef, entry.key);
            });
        },

        /**
         * Gets an array with all keys in the dictionary.
         */
        keys: function () {
            var keys = [];
            this.forEachKey(function (key) {
                keys.push(key);
            });
            return keys;
        }
    });

    /*---------------Queue structure--------------------------------*/

    var Queue = kendo.Class.extend({

        init: function () {
            this._tail = null;
            this._head = null;
            this.length = 0;
        },

        /**
         * Enqueues an object to the end of the queue.
         */
        enqueue: function (value) {
            var entry = { value: value, next: null };
            if (!this._head) {
                this._head = entry;
                this._tail = this._head;
            }
            else {
                this._tail.next = entry;
                this._tail = this._tail.next;
            }
            this.length++;
        },

        /**
         * Removes and returns the object at top of the queue.
         */
        dequeue: function () {
            if (this.length < 1) {
                throw new Error("The queue is empty.");
            }
            var value = this._head.value;
            this._head = this._head.next;
            this.length--;
            return value;
        },

        contains: function (item) {
            var current = this._head;
            while (current) {
                if (current.value === item) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }
    });


    /**
     * While other data structures can have multiple times the same item a Set owns only
     * once a particular item.
     * @type {*}
     */
    var Set = kendo.Observable.extend({
        init: function (resource) {
            var that = this;
            kendo.Observable.fn.init.call(that);
            this._hashTable = new HashTable();
            this.length = 0;
            if (Utils.isDefined(resource)) {
                if (resource instanceof HashTable) {
                    resource.forEach(function (d) {
                        this.add(d);
                    });
                }
                else if (resource instanceof Dictionary) {
                    resource.forEach(function (k, v) {
                        this.add({key: k, value: v});
                    }, this);
                }
            }
        },

        contains: function (item) {
            return this._hashTable.containsKey(item);
        },

        add: function (item) {
            var entry = this._hashTable.get(item);
            if (!entry) {
                this._hashTable.add(item, item);
                this.length++;
                this.trigger('changed');
            }
        },

        get: function (item) {
            if (this.contains(item)) {
                return this._hashTable.get(item).value;
            }
            else {
                return null;
            }
        },

        /**
         * Returns the hash of the item.
         * @param item
         * @returns {*}
         */
        hash: function (item) {
            return this._hashTable._hash(item);
        },

        /**
         * Removes the given item from the set. No exception is thrown if the item is not in the Set.
         * @param item
         */
        remove: function (item) {
            if (this.contains(item)) {
                this._hashTable.remove(item);
                this.length--;
                this.trigger('changed');
            }
        },
        /**
         * Foreach with an iterator working on the key-value pairs.
         * @param func
         */
        forEach: function (func, context) {
            this._hashTable.forEach(function (kv) {
                func(kv.value);
            }, context);
        },
        toArray: function () {
            var r = [];
            this.forEach(function (d) {
                r.push(d);
            });
            return r;
        }
    });

    /*----------------Node-------------------------------*/

    /**
     * Defines the node (vertex) of a Graph.
     */
    var Node = kendo.Class.extend({

        init: function (id, shape) {

            /**
             * Holds all the links incident with the current node.
             * Do not use this property to manage the incoming links, use the appropriate add/remove methods instead.
             */
            this.links = [];

            /**
             * Holds the links from the current one to another Node .
             * Do not use this property to manage the incoming links, use the appropriate add/remove methods instead.
             */
            this.outgoing = [];

            /**
             * Holds the links from another Node to the current one.
             * Do not use this property to manage the incoming links, use the appropriate add/remove methods instead.
             */
            this.incoming = [];

            /**
             * Holds the weight of this Node.
             */
            this.weight = 1;

            if (Utils.isDefined(id)) {
                this.id = id;
            }
            else {
                this.id = randomId();
            }
            if (Utils.isDefined(shape)) {
                this.associatedShape = shape;
                // transfer the shape's bounds to the runtime props
                var b = shape.bounds();
                this.width = b.width;
                this.height = b.height;
                this.x = b.x;
                this.y = b.y;
            }
            else {
                this.associatedShape = null;
            }
            /**
             * The payload of the node.
             * @type {null}
             */
            this.data = null;
            this.type = "Node";
            this.shortForm = "Node '" + this.id + "'";
            /**
             * Whether this is an injected node during the analysis or layout process.
             * @type {boolean}
             */
            this.isVirtual = false;
        },

        /**
         * Returns whether this node has no links attached.
         */
        isIsolated: function () {
            return Utils.isEmpty(this.links);
        },

        /**
         * Gets or sets the bounding rectangle of this node.
         * This should be considered as runtime data, the property is not hotlinked to a SVG item.
         */
        bounds: function (r) {
            if (!Utils.isDefined(r)) {
                return new diagram.Rect(this.x, this.y, this.width, this.height);
            }

            this.x = r.x;
            this.y = r.y;
            this.width = r.width;
            this.height = r.height;
        },

        /**
         * Returns whether there is at least one link with the given (complementary) node. This can be either an
         * incoming or outgoing link.
         */
        isLinkedTo: function (node) {
            var that = this;
            return Utils.any(that.links, function (link) {
                return link.getComplement(that) === node;
            });
        },

        /**
         * Gets the children of this node, defined as the adjacent nodes with a link from this node to the adjacent one.
         * @returns {Array}
         */
        getChildren: function () {
            if (this.outgoing.length === 0) {
                return [];
            }
            var children = [];
            for (var i = 0, len = this.outgoing.length; i < len; i++) {
                var link = this.outgoing[i];
                children.push(link.getComplement(this));
            }
            return children;
        },

        /**
         * Gets the parents of this node, defined as the adjacent nodes with a link from the adjacent node to this one.
         * @returns {Array}
         */
        getParents: function () {
            if (this.incoming.length === 0) {
                return [];
            }
            var parents = [];
            for (var i = 0, len = this.incoming.length; i < len; i++) {
                var link = this.incoming[i];
                parents.push(link.getComplement(this));
            }
            return parents;
        },

        /**
         * Returns a clone of the Node. Note that the identifier is not cloned since it's a different Node instance.
         * @returns {Node}
         */
        clone: function () {
            var copy = new Node();
            if (Utils.isDefined(this.weight)) {
                copy.weight = this.weight;
            }
            if (Utils.isDefined(this.balance)) {
                copy.balance = this.balance;
            }
            if (Utils.isDefined(this.owner)) {
                copy.owner = this.owner;
            }
            copy.associatedShape = this.associatedShape;
            copy.x = this.x;
            copy.y = this.y;
            copy.width = this.width;
            copy.height = this.height;
            return copy;
        },

        /**
         * Returns whether there is a link from the current node to the given node.
         */
        adjacentTo: function (node) {
            return this.isLinkedTo(node) !== null;
        },

        /**
         * Removes the given link from the link collection this node owns.
         * @param link
         */
        removeLink: function (link) {
            if (link.source === this) {
                Utils.remove(this.links, link);
                Utils.remove(this.outgoing, link);
                link.source = null;
            }

            if (link.target === this) {
                Utils.remove(this.links, link);
                Utils.remove(this.incoming, link);
                link.target = null;
            }
        },

        /**
         * Returns whether there is a (outgoing) link from the current node to the given one.
         */
        hasLinkTo: function (node) {
            return Utils.any(this.outgoing, function (link) {
                return link.target === node;
            });
        },

        /**
         * Returns the degree of this node, i.e. the sum of incoming and outgoing links.
         */
        degree: function () {
            return this.links.length;
        },

        /**
         * Returns whether this node is either the source or the target of the given link.
         */
        incidentWith: function (link) {
            return contains(this.links, link);
        },

        /**
         * Returns the links between this node and the given one.
         */
        getLinksWith: function (node) {
            return Utils.all(this.links, function (link) {
                return link.getComplement(this) === node;
            }, this);
        },

        /**
         * Returns the nodes (either parent or child) which are linked to the current one.
         */
        getNeighbors: function () {
            var neighbors = [];
            Utils.forEach(this.incoming, function (e) {
                neighbors.push(e.getComplement(this));
            }, this);
            Utils.forEach(this.outgoing, function (e) {
                neighbors.push(e.getComplement(this));
            }, this);
            return neighbors;
        }
    });

    /**
     * Defines a directed link (edge, connection) of a Graph.
     */
    var Link = kendo.Class.extend({

        init: function (source, target, id, connection) {
            if (Utils.isUndefined(source)) {
                throw "The source of the new link is not set.";
            }
            if (Utils.isUndefined(target)) {
                throw "The target of the new link is not set.";
            }
            var sourceFound, targetFound;
            if (Utils.isString(source)) {
                sourceFound = new Node(source);
            }
            else {
                sourceFound = source;
            }
            if (Utils.isString(target)) {
                targetFound = new Node(target);
            }
            else {
                targetFound = target;
            }

            this.source = sourceFound;
            this.target = targetFound;
            this.source.links.push(this);
            this.target.links.push(this);
            this.source.outgoing.push(this);
            this.target.incoming.push(this);
            if (Utils.isDefined(id)) {
                this.id = id;
            }
            else {
                this.id = randomId();
            }
            if (Utils.isDefined(connection)) {
                this.associatedConnection = connection;
            }
            else {
                this.associatedConnection = null;
            }
            this.type = "Link";
            this.shortForm = "Link '" + this.source.id + "->" + this.target.id + "'";
        },

        /**
         * Returns the complementary node of the given one, if any.
         */
        getComplement: function (node) {
            if (this.source !== node && this.target !== node) {
                throw "The given node is not incident with this link.";
            }
            return this.source === node ? this.target : this.source;
        },

        /**
         * Returns the overlap of the current link with the given one, if any.
         */
        getCommonNode: function (link) {
            if (this.source === link.source || this.source === link.target) {
                return this.source;
            }
            if (this.target === link.source || this.target === link.target) {
                return this.target;
            }
            return null;
        },

        /**
         * Returns whether the current link is bridging the given nodes.
         */
        isBridging: function (v1, v2) {
            return this.source === v1 && this.target === v2 || this.source === v2 && this.target === v1;
        },

        /**
         * Returns the source and target of this link as a tuple.
         */
        getNodes: function () {
            return [this.source, this.target];
        },

        /**
         * Returns whether the given node is either the source or the target of the current link.
         */
        incidentWith: function (node) {
            return this.source === node || this.target === node;
        },

        /**
         * Returns whether the given link is a continuation of the current one. This can be both
         * via an incoming or outgoing link.
         */
        adjacentTo: function (link) {
            return contains(this.source.links, link) || contains(this.target.links, link);
        },

        /**
         * Changes the source-node of this link.
         */
        changeSource: function (node) {
            Utils.remove(this.source.links, this);
            Utils.remove(this.source.outgoing, this);

            node.links.push(this);
            node.outgoing.push(this);

            this.source = node;
        },

        /**
         * Changes the target-node of this link.
         * @param node
         */
        changeTarget: function (node) {
            Utils.remove(this.target.links, this);
            Utils.remove(this.target.incoming, this);

            node.links.push(this);
            node.incoming.push(this);

            this.target = node;
        },

        /**
         * Changes both the source and the target nodes of this link.
         */
        changesNodes: function (v, w) {
            if (this.source === v) {
                this.changeSource(w);
            }
            else if (this.target === v) {
                this.changeTarget(w);
            }
        },

        /**
         * Reverses the direction of this link.
         */
        reverse: function () {
            var oldSource = this.source;
            var oldTarget = this.target;

            this.source = oldTarget;
            Utils.remove(oldSource.outgoing, this);
            this.source.outgoing.push(this);

            this.target = oldSource;
            Utils.remove(oldTarget.incoming, this);
            this.target.incoming.push(this);
            return this;
        },

        /**
         * Ensures that the given target defines the endpoint of this link.
         */
        directTo: function (target) {
            if (this.source !== target && this.target !== target) {
                throw "The given node is not incident with this link.";
            }
            if (this.target !== target) {
                this.reverse();
            }
        },

        /**
         * Returns a reversed clone of this link.
         */
        createReverseEdge: function () {
            var r = this.clone();
            r.reverse();
            r.reversed = true;
            return r;
        },

        /**
         * Returns a clone of this link.
         */
        clone: function () {
            var clone = new Link(this.source, this.target);
            return clone;
        }
    });

    /*--------------Graph structure---------------------------------*/
    /**
     * Defines a directed graph structure.
     * Note that the incidence structure resides in the nodes through the incoming and outgoing links collection, rahter than
     * inside the Graph.
     */
    var Graph = kendo.Class.extend({
        init: function (idOrDiagram) {
            /**
             * The links or edge collection of this Graph.
             * @type {Array}
             */
            this.links = [];
            /**
             * The node or vertex collection of this Graph.
             * @type {Array}
             */
            this.nodes = [];
            /**
             * The optional reference to the Diagram on which this Graph is based.
             * @type {null}
             */
            this.diagram = null;

            /**
             * The root of this Graph. If not set explicitly the first Node with zero incoming links will be taken.
             * @type {null}
             * @private
             */
            this._root = null;
            if (Utils.isDefined(idOrDiagram)) {
                if (Utils.isString(idOrDiagram)) {
                    this.id = idOrDiagram;
                }
                else {
                    this.diagram = idOrDiagram;
                    this.id = idOrDiagram.id;
                }
            }
            else {
                this.id = randomId();
            }

            /**
             * The bounds of this graph if the nodes have spatial extension defined.
             * @type {Rect}
             */
            this.bounds = new Rect();
            // keeps track whether the children & parents have been created
            this._hasCachedRelationships = false;
            this.type = "Graph";
        },
        /**
         * Caches the relational information of parents and children in the 'parents' and 'children'
         * properties.
         * @param forceRebuild If set to true the relational info will be rebuild even if already present.
         */
        cacheRelationships: function (forceRebuild) {
            if (Utils.isUndefined(forceRebuild)) {
                forceRebuild = false;
            }
            if (this._hasCachedRelationships && !forceRebuild) {
                return;
            }
            for (var i = 0, len = this.nodes.length; i < len; i++) {
                var node = this.nodes[i];
                node.children = this.getChildren(node);
                node.parents = this.getParents(node);
            }
            this._hasCachedRelationships = true;
        },

        /**
         * Assigns tree-levels to the nodes assuming this is a tree graph.
         * If not connected or not a tree the process will succeed but
         * will have little meaning.
         * @param startNode The node from where the level numbering starts, usually the root of the tree.
         * @param visited The collection of visited nodes.
         * @param offset The offset or starting counter of the level info.
         */
        assignLevels: function (startNode, offset, visited) {
            if (!startNode) {
                throw "Start node not specified.";
            }
            if (Utils.isUndefined(offset)) {
                offset = 0;
            }
            // if not done before, cache the parents and children
            this.cacheRelationships();
            if (Utils.isUndefined(visited)) {
                visited = new Dictionary();
                Utils.forEach(this.nodes, function (n) {
                    visited.add(n, false);
                });
            }
            visited.set(startNode, true);
            startNode.level = offset;
            var children = startNode.children;
            for (var i = 0, len = children.length; i < len; i++) {
                var child = children[i];
                if (!child || visited.get(child)) {
                    continue;
                }
                this.assignLevels(child, offset + 1, visited);
            }
        },

        /**
         * Gets or set the root of this graph.
         * If not set explicitly the first Node with zero incoming links will be taken.
         * @param value
         * @returns {*}
         */
        root: function (value) {
            if (Utils.isUndefined(value)) {
                if (!this._root) {
                    // TODO: better to use the longest path for the most probable root?
                    var found = Utils.first(this.nodes, function (n) {
                        return n.incoming.length === 0;
                    });
                    if (found) {
                        return found;
                    }
                    return Utils.first(this.nodes);
                }
                else {
                    return this._root;
                }
            }
            else {
                this._root = value;
            }
        },

        /**
         * Returns the connected components of this graph.
         * Note that the returned graphs are made up of the nodes and links of this graph, i.e. a pointer to the items of this graph.
         * If you alter the items of the components you'll alter the original graph and vice versa.
         * @returns {Array}
         */
        getConnectedComponents: function () {
            this.componentIndex = 0;
            this.setItemIndices();
            var componentId = Utils.initArray(this.nodes.length, -1);

            for (var v = 0; v < this.nodes.length; v++) {
                if (componentId[v] === -1) {
                    this._collectConnectedNodes(componentId, v);
                    this.componentIndex++;
                }
            }

            var components = [], i;
            for (i = 0; i < this.componentIndex; ++i) {
                components[i] = new Graph();
            }
            for (i = 0; i < componentId.length; ++i) {
                var graph = components[componentId[i]];
                graph.addNodeAndOutgoings(this.nodes[i]);
            }
            // sorting the components in decreasing order of node count
            components.sort(function (a, b) {
                return b.nodes.length - a.nodes.length;
            });
            return components;
        },

        _collectConnectedNodes: function (setIds, nodeIndex) {
            setIds[nodeIndex] = this.componentIndex; // part of the current component
            var node = this.nodes[nodeIndex];
            Utils.forEach(node.links,
                function (link) {
                    var next = link.getComplement(node);
                    var nextId = next.index;
                    if (setIds[nextId] === -1) {
                        this._collectConnectedNodes(setIds, nextId);
                    }
                }, this);
        },

        /**
         * Calculates the bounds of this Graph if the Nodes have spatial dimensions defined.
         * @returns {Rect}
         */
        calcBounds: function () {
            if (this.isEmpty()) {
                this.bounds = new Rect();
                return this.bounds;
            }
            var b = null;
            for (var i = 0, len = this.nodes.length; i < len; i++) {
                var node = this.nodes[i];
                if (!b) {
                    b = node.bounds();
                }
                else {
                    b = b.union(node.bounds());
                }
            }
            this.bounds = b;
            return this.bounds;
        },

        /**
         * Creates a spanning tree for the current graph.
         * Important: this will not return a spanning forest if the graph is disconnected.
         * Prim's algorithm  finds a minimum-cost spanning tree of an edge-weighted, connected, undirected graph;
         * see http://en.wikipedia.org/wiki/Prim%27s_algorithm .
         * @param root The root of the spanning tree.
         * @returns {Graph}
         */
        getSpanningTree: function (root) {
            var tree = new Graph();
            var map = new Dictionary(), source, target;
            tree.root = root.clone();
            tree.root.level = 0;
            tree.root.id = root.id;
            map.add(root, tree.root);
            root.level = 0;

            var visited = [];
            var remaining = [];
            tree.nodes.push(tree.root);
            visited.push(root);
            remaining.push(root);

            var levelCount = 1;
            while (remaining.length > 0) {
                var next = remaining.pop();
                for (var ni = 0; ni < next.links.length; ni++) {
                    var link = next.links[ni];
                    var cn = link.getComplement(next);
                    if (contains(visited, cn)) {
                        continue;
                    }

                    cn.level = next.level + 1;
                    if (levelCount < cn.level + 1) {
                        levelCount = cn.level + 1;
                    }
                    if (!contains(remaining, cn)) {
                        remaining.push(cn);
                    }
                    if (!contains(visited, cn)) {
                        visited.push(cn);
                    }
                    if (map.containsKey(next)) {
                        source = map.get(next);
                    }
                    else {
                        source = next.clone();
                        source.level = next.level;
                        source.id = next.id;
                        map.add(next, source);
                    }
                    if (map.containsKey(cn)) {
                        target = map.get(cn);
                    }
                    else {
                        target = cn.clone();
                        target.level = cn.level;
                        target.id = cn.id;
                        map.add(cn, target);
                    }
                    var newLink = new Link(source, target);
                    tree.addLink(newLink);
                }

            }

            var treeLevels = [];
            for (var i = 0; i < levelCount; i++) {
                treeLevels.push([]);
            }

            Utils.forEach(tree.nodes, function (node) {
                treeLevels[node.level].push(node);
            });

            tree.treeLevels = treeLevels;
            tree.cacheRelationships();
            return tree;
        },

        /**
         * Returns a random node in this graph.
         * @param excludedNodes The collection of nodes which should not be considered.
         * @param incidenceLessThan The maximum degree or incidence the random node should have.
         * @returns {*}
         */
        takeRandomNode: function (excludedNodes, incidenceLessThan) {
            if (Utils.isUndefined(excludedNodes)) {
                excludedNodes = [];
            }
            if (Utils.isUndefined(incidenceLessThan)) {
                incidenceLessThan = 4;
            }
            if (this.nodes.length === 0) {
                return null;
            }
            if (this.nodes.length === 1) {
                return contains(excludedNodes, this.nodes[0]) ? null : this.nodes[0];
            }
            var pool = $.grep(this.nodes, function (node) {
                return !contains(excludedNodes, node) && node.degree() <= incidenceLessThan;
            });
            if (Utils.isEmpty(pool)) {
                return null;
            }
            return pool[Utils.randomInteger(0, pool.length)];
        },

        /**
         * Returns whether this is an empty graph.
         */
        isEmpty: function () {
            return Utils.isEmpty(this.nodes);
        },

        /**
         * Checks whether the endpoints of the links are all in the nodes collection.
         */
        isHealthy: function () {
            return Utils.all(this.links, function (link) {
                return contains(this.nodes, link.source) && contains(this.nodes, link.target);
            }, this);
        },

        /**
         * Gets the parents of this node, defined as the adjacent nodes with a link from the adjacent node to this one.
         * @returns {Array}
         */
        getParents: function (n) {
            if (!this.hasNode(n)) {
                throw "The given node is not part of this graph.";
            }
            return n.getParents();
        },

        /**
         * Gets the children of this node, defined as the adjacent nodes with a link from this node to the adjacent one.
         * @returns {Array}
         */
        getChildren: function (n) {
            if (!this.hasNode(n)) {
                throw "The given node is not part of this graph.";
            }
            return n.getChildren();
        },

        /**
         * Adds a new link to the graph between the given nodes.
         */
        addLink: function (sourceOrLink, target, owner) {

            if (Utils.isUndefined(sourceOrLink)) {
                throw "The source of the link is not defined.";
            }
            if (Utils.isUndefined(target)) {
                // can only be undefined if the first one is a Link
                if (Utils.isDefined(sourceOrLink.type) && sourceOrLink.type === "Link") {
                    this.addExistingLink(sourceOrLink);
                    return;
                }
                else {
                    throw "The target of the link is not defined.";
                }
            }

            var foundSource = this.getNode(sourceOrLink);
            if (Utils.isUndefined(foundSource)) {
                foundSource = this.addNode(sourceOrLink);
            }
            var foundTarget = this.getNode(target);
            if (Utils.isUndefined(foundTarget)) {
                foundTarget = this.addNode(target);
            }

            var newLink = new Link(foundSource, foundTarget);

            if (Utils.isDefined(owner)) {
                newLink.owner = owner;
            }

            /*newLink.source.outgoing.push(newLink);
             newLink.source.links.push(newLink);
             newLink.target.incoming.push(newLink);
             newLink.target.links.push(newLink);*/

            this.links.push(newLink);

            return newLink;
        },

        /**
         * Removes all the links in this graph.
         */
        removeAllLinks: function () {
            while (this.links.length > 0) {
                var link = this.links[0];
                this.removeLink(link);
            }
        },

        /**
         * Adds the given link to the current graph.
         */
        addExistingLink: function (link) {

            if (this.hasLink(link)) {
                return;
            }
            this.links.push(link);
            if (this.hasNode(link.source.id)) {
                // priority to the existing node with the id even if other props are different
                var s = this.getNode(link.source.id);
                link.changeSource(s);
            }
            else {
                this.addNode(link.source);
            }

            if (this.hasNode(link.target.id)) {
                var t = this.getNode(link.target.id);
                link.changeTarget(t);
            }
            else {
                this.addNode(link.target);
            }

            /*  if (!link.source.outgoing.contains(link)) {
             link.source.outgoing.push(link);
             }
             if (!link.source.links.contains(link)) {
             link.source.links.push(link);
             }
             if (!link.target.incoming.contains(link)) {
             link.target.incoming.push(link);
             }
             if (!link.target.links.contains(link)) {
             link.target.links.push(link);
             }*/
        },

        /**
         * Returns whether the given identifier or Link is part of this graph.
         * @param linkOrId An identifier or a Link object.
         * @returns {*}
         */
        hasLink: function (linkOrId) {
            if (Utils.isString(linkOrId)) {
                return Utils.any(this.links, function (link) {
                    return link.id === linkOrId;
                });
            }
            if (linkOrId.type === "Link") {
                return contains(this.links, linkOrId);
            }
            throw "The given object is neither an identifier nor a Link.";
        },
        /**
         * Gets the node with the specified Id or null if not part of this graph.
         */
        getNode: function (nodeOrId) {
            if (Utils.isUndefined(nodeOrId)) {
                throw "No identifier or Node specified.";
            }
            if (Utils.isString(nodeOrId)) {
                return Utils.find(this.nodes, function (n) {
                    return n.id == nodeOrId;
                });
            }
            else {
                if (this.hasNode(nodeOrId)) {
                    return nodeOrId;
                }
                else {
                    return null;
                }
            }
        },

        /**
         * Returns whether the given node or node Id is part of this graph.
         */
        hasNode: function (nodeOrId) {
            if (Utils.isString(nodeOrId)) {
                return Utils.any(this.nodes, function (n) {
                    return n.id === nodeOrId;
                });
            }
            if (Utils.isObject(nodeOrId)) {
                return Utils.any(this.nodes, function (n) {
                    return n === nodeOrId;
                });
            }
            throw "The identifier should be a Node or the Id (string) of a node.";
        },

        /**
         * Removes the given node from this graph.
         * The node can be specified as an object or as an identifier (string).
         */
        removeNode: function (nodeOrId) {
            var n = nodeOrId;
            if (Utils.isString(nodeOrId)) {
                n = this.getNode(nodeOrId);
            }

            if (Utils.isDefined(n)) {
                var links = n.links;
                n.links = [];
                for (var i = 0, len = links.length; i < len; i++) {
                    var link = links[i];
                    this.removeLink(link);
                }
                Utils.remove(this.nodes, n);
            }
            else {
                throw "The identifier should be a Node or the Id (string) of a node.";
            }
        },

        /**
         * Returns whether the given nodes are connected with a least one link independently of the direction.
         */
        areConnected: function (n1, n2) {
            return Utils.any(this.links, function (link) {
                return link.source == n1 && link.target == n2 || link.source == n2 && link.target == n1;
            });
        },

        /**
         * Removes the given link from this graph.
         */
        removeLink: function (link) {
            /*    if (!this.links.contains(link)) {
             throw "The given link is not part of the Graph.";
             }
             */
            Utils.remove(this.links, link);

            Utils.remove(link.source.outgoing, link);
            Utils.remove(link.source.links, link);
            Utils.remove(link.target.incoming, link);
            Utils.remove(link.target.links, link);
        },

        /**
         * Adds a new node to this graph, if not already present.
         * The node can be an existing Node or the identifier of a new node.
         * No error is thrown if the node is already there and the existing one is returned.
         */
        addNode: function (nodeOrId, layoutRect, owner) {

            var newNode = null;

            if (!Utils.isDefined(nodeOrId)) {
                throw "No Node or identifier for a new Node is given.";
            }

            if (Utils.isString(nodeOrId)) {
                if (this.hasNode(nodeOrId)) {
                    return this.getNode(nodeOrId);
                }
                newNode = new Node(nodeOrId);
            }
            else {
                if (this.hasNode(nodeOrId)) {
                    return this.getNode(nodeOrId);
                }
                // todo: ensure that the param is a Node?
                newNode = nodeOrId;
            }

            if (Utils.isDefined(layoutRect)) {
                newNode.bounds(layoutRect);
            }

            if (Utils.isDefined(owner)) {
                newNode.owner = owner;
            }
            this.nodes.push(newNode);
            return newNode;
        },

        /**
         * Adds the given Node and its outgoing links.
         */
        addNodeAndOutgoings: function (node) {

            if (!contains(this.nodes, node)) {
                this.nodes.push(node);
            }

            var newLinks = node.outgoing;
            node.outgoing = [];
            Utils.forEach(newLinks, function (link) {
                this.addExistingLink(link);
            }, this);
        },

        /**
         * Sets the 'index' property on the links and nodes of this graph.
         */
        setItemIndices: function () {
            var i;
            for (i = 0; i < this.nodes.length; ++i) {
                this.nodes[i].index = i;
            }

            for (i = 0; i < this.links.length; ++i) {
                this.links[i].index = i;
            }
        },

        /**
         * Returns a clone of this graph.
         */
        clone: function (saveMapping) {
            var copy = new Graph();
            var save = Utils.isDefined(saveMapping) && saveMapping === true;
            if (save) {
                copy.nodeMap = new Dictionary();
                copy.linkMap = new Dictionary();
            }
            // we need a map even if the saveMapping is not set
            var map = new Dictionary();
            Utils.forEach(this.nodes, function (nOriginal) {
                var nCopy = nOriginal.clone();
                map.set(nOriginal, nCopy);
                copy.nodes.push(nCopy);

                if (save) {
                    copy.nodeMap.set(nCopy, nOriginal);
                }
            });

            Utils.forEach(this.links, function (linkOriginal) {
                if (map.containsKey(linkOriginal.source) && map.containsKey(linkOriginal.target)) {
                    var linkCopy = copy.addLink(map.get(linkOriginal.source), map.get(linkOriginal.target));
                    if (save) {
                        copy.linkMap.set(linkCopy, linkOriginal);
                    }
                }
            });

            return copy;
        },

        /**
         * The parsing allows a quick way to create graphs.
         *  - ["n1->n2", "n2->n3"]: creates the three nodes and adds the links
         *  - ["n1->n2", {id: "QSDF"}, "n2->n3"]: same as previous but also performs a deep extend of the link between n1 and n2 with the given object.
         */
        linearize: function (addIds) {
            return Graph.Utils.linearize(this, addIds);
        },

        /**
         * Performs a depth-first traversal starting at the given node.
         * @param startNode a node or id of a node in this graph
         * @param action
         */
        depthFirstTraversal: function (startNode, action) {
            if (Utils.isUndefined(startNode)) {
                throw "You need to supply a starting node.";
            }
            if (Utils.isUndefined(action)) {
                throw "You need to supply an action.";
            }
            if (!this.hasNode(startNode)) {
                throw "The given start-node is not part of this graph";
            }
            var foundNode = this.getNode(startNode);// case the given one is an Id
            var visited = [];
            this._dftIterator(foundNode, action, visited);
        },

        _dftIterator: function (node, action, visited) {

            action(node);
            visited.push(node);
            var children = node.getChildren();
            for (var i = 0, len = children.length; i < len; i++) {
                var child = children[i];
                if (contains(visited, child)) {
                    continue;
                }
                this._dftIterator(child, action, visited);
            }
        },

        /**
         * Performs a breadth-first traversal starting at the given node.
         * @param startNode a node or id of a node in this graph
         * @param action
         */
        breadthFirstTraversal: function (startNode, action) {

            if (Utils.isUndefined(startNode)) {
                throw "You need to supply a starting node.";
            }
            if (Utils.isUndefined(action)) {
                throw "You need to supply an action.";
            }

            if (!this.hasNode(startNode)) {
                throw "The given start-node is not part of this graph";
            }
            var foundNode = this.getNode(startNode);// case the given one is an Id
            var queue = new Queue();
            var visited = [];
            queue.enqueue(foundNode);

            while (queue.length > 0) {
                var node = queue.dequeue();
                action(node);
                visited.push(node);
                var children = node.getChildren();
                for (var i = 0, len = children.length; i < len; i++) {
                    var child = children[i];
                    if (contains(visited, child) || contains(queue, child)) {
                        continue;
                    }
                    queue.enqueue(child);
                }
            }
        },

        /**
         * This is the classic Tarjan algorithm for strongly connected components.
         * See e.g. http://en.wikipedia.org/wiki/Tarjan's_strongly_connected_components_algorithm
         * @param excludeSingleItems Whether isolated nodes should be excluded from the analysis.
         * @param node The start node from which the analysis starts.
         * @param indices  Numbers the nodes consecutively in the order in which they are discovered.
         * @param lowLinks The smallest index of any node known to be reachable from the node, including the node itself
         * @param connected The current component.
         * @param stack The bookkeeping stack of things to visit.
         * @param index The counter of visited nodes used to assign the indices.
         * @private
         */
        _stronglyConnectedComponents: function (excludeSingleItems, node, indices, lowLinks, connected, stack, index) {
            indices.add(node, index);
            lowLinks.add(node, index);
            index++;

            stack.push(node);

            var children = node.getChildren(), next;
            for (var i = 0, len = children.length; i < len; i++) {
                next = children[i];
                if (!indices.containsKey(next)) {
                    this._stronglyConnectedComponents(excludeSingleItems, next, indices, lowLinks, connected, stack, index);
                    lowLinks.add(node, Math.min(lowLinks.get(node), lowLinks.get(next)));
                }
                else if (contains(stack, next)) {
                    lowLinks.add(node, Math.min(lowLinks.get(node), indices.get(next)));
                }
            }
            // If v is a root node, pop the stack and generate a strong component
            if (lowLinks.get(node) === indices.get(node)) {
                var component = [];
                do {
                    next = stack.pop();
                    component.push(next);
                }
                while (next !== node);
                if (!excludeSingleItems || (component.length > 1)) {
                    connected.push(component);
                }
            }
        },

        /**
         * Returns the cycles found in this graph.
         * The returned arrays consist of the nodes which are strongly coupled.
         * @param excludeSingleItems Whether isolated nodes should be excluded.
         * @returns {Array} The array of cycles found.
         */
        findCycles: function (excludeSingleItems) {
            if (Utils.isUndefined(excludeSingleItems)) {
                excludeSingleItems = true;
            }
            var indices = new Dictionary();
            var lowLinks = new Dictionary();
            var connected = [];
            var stack = [];
            for (var i = 0, len = this.nodes.length; i < len; i++) {
                var node = this.nodes[i];
                if (indices.containsKey(node)) {
                    continue;
                }
                this._stronglyConnectedComponents(excludeSingleItems, node, indices, lowLinks, connected, stack, 0);
            }
            return connected;
        },

        /**
         * Returns whether this graph is acyclic.
         * @returns {*}
         */
        isAcyclic: function () {
            return Utils.isEmpty(this.findCycles());
        },

        /**
         * Returns whether the given graph is a subgraph of this one.
         * @param other Another graph instance.
         */
        isSubGraph: function (other) {
            var otherArray = other.linearize();
            var thisArray = this.linearize();
            return Utils.all(otherArray, function (s) {
                return contains(thisArray, s);
            });
        },

        /**
         *  Makes an acyclic graph from the current (connected) one.
         * * @returns {Array} The reversed links.
         */
        makeAcyclic: function () {
            // if empty or almost empty
            if (this.isEmpty() || this.nodes.length <= 1 || this.links.length <= 1) {
                return [];
            }
            // singular case of just two nodes
            if (this.nodes.length == 2) {
                var result = [];
                if (this.links.length > 1) {
                    var oneLink = this.links[0];
                    var oneNode = oneLink.source;
                    for (var i = 0, len = this.links.length; i < len; i++) {
                        var link = this.links[i];
                        if (link.source == oneNode) {
                            continue;
                        }
                        var rev = link.reverse();
                        result.push(rev);
                    }
                }
                return result;
            }

            var copy = this.clone(true); // copy.nodeMap tells you the mapping
            var N = this.nodes.length;

            var intensityCatalog = new Dictionary();

            /**
             * If there are both incoming and outgoing links this will return the flow intensity (out-in).
             * Otherwise the node acts as a flow source with N specifying the (equal) intensity.
             * @param node
             * @returns {number}
             */
            var flowIntensity = function (node) {
                if (node.outgoing.length === 0) {
                    return (2 - N);
                }
                else if (node.incoming.length === 0) {
                    return (N - 2);
                }
                else {
                    return node.outgoing.length - node.incoming.length;
                }
            };

            /**
             * Collects the nodes with the same intensity.
             * @param node
             * @param intensityCatalog
             */
            var catalogEqualIntensity = function (node, intensityCatalog) {
                var intensity = flowIntensity(node, N);
                if (!intensityCatalog.containsKey(intensity)) {
                    intensityCatalog.set(intensity, []);
                }
                intensityCatalog.get(intensity).push(node);
            };

            Utils.forEach(copy.nodes, function (v) {
                catalogEqualIntensity(v, intensityCatalog);
            });

            var sourceStack = [];
            var targetStack = [];

            while (copy.nodes.length > 0) {
                var source, target, intensity;
                if (intensityCatalog.containsKey(2 - N)) {
                    var targets = intensityCatalog.get(2 - N); // nodes without outgoings
                    while (targets.length > 0) {
                        target = targets.pop();
                        for (var li = 0; li < target.links.length; li++) {
                            var targetLink = target.links[li];
                            source = targetLink.getComplement(target);
                            intensity = flowIntensity(source, N);
                            Utils.remove(intensityCatalog.get(intensity), source);
                            source.removeLink(targetLink);
                            catalogEqualIntensity(source, intensityCatalog);
                        }
                        Utils.remove(copy.nodes, target);
                        targetStack.unshift(target);
                    }
                }

                // move sources to sourceStack
                if (intensityCatalog.containsKey(N - 2)) {
                    var sources = intensityCatalog.get(N - 2); // nodes without incomings
                    while (sources.length > 0) {
                        source = sources.pop();
                        for (var si = 0; si < source.links.length; si++) {
                            var sourceLink = source.links[si];
                            target = sourceLink.getComplement(source);
                            intensity = flowIntensity(target, N);
                            Utils.remove(intensityCatalog.get(intensity), target);
                            target.removeLink(sourceLink);
                            catalogEqualIntensity(target, intensityCatalog);
                        }
                        sourceStack.push(source);
                        Utils.remove(copy.nodes, source);
                    }
                }

                if (copy.nodes.length > 0) {
                    for (var k = N - 3; k > 2 - N; k--) {
                        if (intensityCatalog.containsKey(k) &&
                            intensityCatalog.get(k).length > 0) {
                            var maxdiff = intensityCatalog.get(k);
                            var v = maxdiff.pop();
                            for (var ri = 0; ri < v.links.length; ri++) {
                                var ril = v.links[ri];
                                var u = ril.getComplement(v);
                                intensity = flowIntensity(u, N);
                                Utils.remove(intensityCatalog.get(intensity), u);
                                u.removeLink(ril);
                                catalogEqualIntensity(u, intensityCatalog);
                            }
                            sourceStack.push(v);
                            Utils.remove(copy.nodes, v);
                            break;
                        }
                    }
                }
            }

            sourceStack = sourceStack.concat(targetStack);

            var vertexOrder = new Dictionary();
            for (var kk = 0; kk < this.nodes.length; kk++) {
                vertexOrder.set(copy.nodeMap.get(sourceStack[kk]), kk);
            }

            var reversedEdges = [];
            Utils.forEach(this.links, function (link) {
                if (vertexOrder.get(link.source) > vertexOrder.get(link.target)) {
                    link.reverse();
                    reversedEdges.push(link);
                }
            });
            return reversedEdges;
        }
    });

    /**
     * A collection of predefined graphs for demo and testing purposes.
     */
    Graph.Predefined = {
        /**
         * Eight-shapes graph all connected in a cycle.
         * @returns {*}
         * @constructor
         */
        EightGraph: function () {
            return Graph.Utils.parse([ "1->2", "2->3", "3->4", "4->1", "3->5", "5->6", "6->7", "7->3"]);
        },

        /**
         * Creates a typical mindmap diagram.
         * @returns {*}
         * @constructor
         */
        Mindmap: function () {
            return Graph.Utils.parse(["0->1", "0->2", "0->3", "0->4", "0->5", "1->6", "1->7", "7->8", "2->9", "9->10", "9->11", "3->12",
                "12->13", "13->14", "4->15", "4->16", "15->17", "15->18", "18->19", "18->20", "14->21", "14->22", "5->23", "23->24", "23->25", "6->26"]);
        },

        /**
         * Three nodes connected in a cycle.
         * @returns {*}
         * @constructor
         */
        ThreeGraph: function () {
            return Graph.Utils.parse([ "1->2", "2->3", "3->1"]);
        },

        /**
         * A tree with each node having two children.
         * @param levels How many levels the binary tree should have.
         * @returns {diagram.Graph}
         * @constructor
         */
        BinaryTree: function (levels) {
            if (Utils.isUndefined(levels)) {
                levels = 5;
            }
            return Graph.Utils.createBalancedTree(levels, 2);
        },

        /**
         * A linear graph (discrete line segment).
         * @param length How many segments (the node count is hence (length+1)).
         * @returns {diagram.Graph}
         * @constructor
         */
        Linear: function (length) {
            if (Utils.isUndefined(length)) {
                length = 10;
            }
            return Graph.Utils.createBalancedTree(length, 1);
        },

        /**
         * A standard tree-graph with the specified levels and children (siblings) count.
         * Note that for a balanced tree of level N and sibling count s, counting the root as level zero:
         *  - NodeCount = (1-s^(N+1))/(1-s)]
         *  - LinkCount = s.(1-s^N)/(1-s)
         * @param levels How many levels the tree should have.
         * @param siblingsCount How many siblings each level should have.
         * @returns {diagram.Graph}
         * @constructor
         */
        Tree: function (levels, siblingsCount) {
            return Graph.Utils.createBalancedTree(levels, siblingsCount);
        },

        /**
         * Creates a forest.
         * Note that for a balanced forest of level N, sibling count s and tree count t, counting the root as level zero:
         *  - NodeCount = t.(1-s^(N+1))/(1-s)]
         *  - LinkCount = t.s.(1-s^N)/(1-s)
         * @param levels How many levels the tree should have.
         * @param siblingsCount How many siblings each level should have.
         * @param trees The amount of trees the forest should have.
         * @returns {diagram.Graph}
         * @constructor
         */
        Forest: function (levels, siblingsCount, trees) {
            return Graph.Utils.createBalancedForest(levels, siblingsCount, trees);
        },

        /**
         * A workflow-like graph with cycles.
         * @returns {*}
         * @constructor
         */
        Workflow: function () {
            return Graph.Utils.parse(
                ["0->1", "1->2", "2->3", "1->4", "4->3", "3->5", "5->6", "6->3", "6->7", "5->4"]
            );
        },

        /**
         * A grid graph with the direction of the links avoiding cycles.
         * Node count: (n+1).(m+1)
         * Link count: n.(m+1) + m.(n+1)
         * @param n Horizontal count of grid cells. If zero this will result in a linear graph.
         * @param m Vertical count of grid cells. If zero this will result in a linear graph.
         * @constructor
         */
        Grid: function (n, m) {
            var g = new diagram.Graph();
            if (n <= 0 && m <= 0) {
                return g;
            }

            for (var i = 0; i < n + 1; i++) {
                var previous = null;
                for (var j = 0; j < m + 1; j++) {
                    // using x-y coordinates to name the nodes
                    var node = new Node(i.toString() + "." + j.toString());
                    g.addNode(node);
                    if (previous) {
                        g.addLink(previous, node);
                    }
                    if (i > 0) {
                        var left = g.getNode((i - 1).toString() + "." + j.toString());
                        g.addLink(left, node);
                    }
                    previous = node;
                }
            }
            return g;
        }

    };

    /**
     * Graph generation and other utilities.
     */
    Graph.Utils = {
        /**
         * The parsing allows a quick way to create graphs.
         *  - ["n1->n2", "n2->n3"]: creates the three nodes and adds the links
         *  - ["n1->n2", {id: "id177"}, "n2->n3"]: same as previous but also performs a deep extend of the link between n1 and n2 with the given object.
         */
        parse: function (graphString) {

            var previousLink, graph = new diagram.Graph(), parts = graphString.slice();
            for (var i = 0, len = parts.length; i < len; i++) {
                var part = parts[i];
                if (Utils.isString(part)) // link spec
                {
                    if (part.indexOf("->") < 0) {
                        throw "The link should be specified as 'a->b'.";
                    }
                    var p = part.split("->");
                    if (p.length != 2) {
                        throw "The link should be specified as 'a->b'.";
                    }
                    previousLink = new Link(p[0], p[1]);
                    graph.addLink(previousLink);
                }
                if (Utils.isObject(part)) {
                    if (!previousLink) {
                        throw "Specification found before Link definition.";
                    }
                    kendo.deepExtend(previousLink, part);
                }
            }
            return graph;
        },

        /**
         * Returns a linearized representation of the given Graph.
         * See also the Graph.Utils.parse method for the inverse operation.
         */
        linearize: function (graph, addIds) {
            if (Utils.isUndefined(graph)) {
                throw "Expected an instance of a Graph object in slot one.";
            }
            if (Utils.isUndefined(addIds)) {
                addIds = false;
            }
            var lin = [];
            for (var i = 0, len = graph.links.length; i < len; i++) {
                var link = graph.links[i];
                lin.push(link.source.id + "->" + link.target.id);
                if (addIds) {
                    lin.push({id: link.id});
                }
            }
            return lin;
        },

        /**
         * The method used by the diagram creation to instantiate a shape.
         * @param kendoDiagram The Kendo diagram where the diagram will be created.
         * @param p The position at which to place the shape.
         * @param shapeDefaults Optional Shape options.
         * @param id Optional identifier of the shape.
         * @returns {*}
         * @private
         */
        _addShape: function (kendoDiagram, p, id, shapeDefaults) {
            if (Utils.isUndefined(p)) {
                p = new diagram.Point(0, 0);
            }

            if (Utils.isUndefined(id)) {
                id = randomId();
            }

            shapeDefaults = kendo.deepExtend({
                width: 20,
                height: 20,
                id: id,
                radius: 10,
                background: "#778899",
                data: "circle",
                undoable: false,
                x: p.x,
                y: p.y
            }, shapeDefaults);

            return kendoDiagram.addShape(shapeDefaults);
        },
        /**
         * The method used by the diagram creation to instantiate a connection.
         * @param diagram he Kendo diagram where the diagram will be created.
         * @param from The source shape.
         * @param to The target shape.
         * @param options Optional Connection options.
         * @returns {*}
         * @private
         */
        _addConnection: function (diagram, from, to, options) {
            return diagram.connect(from, to, options);
        },

        /**
         * Creates a diagram from the given Graph.
         * @param diagram The Kendo diagram where the diagram will be created.
         * @param graph The graph structure defining the diagram.
         */
        createDiagramFromGraph: function (diagram, graph, doLayout, randomSize) {

            if (Utils.isUndefined(diagram)) {
                throw "The diagram surface is undefined.";
            }
            if (Utils.isUndefined(graph)) {
                throw "No graph specification defined.";
            }
            if (Utils.isUndefined(doLayout)) {
                doLayout = true;
            }
            if (Utils.isUndefined(randomSize)) {
                randomSize = false;
            }

            var width = diagram.element.clientWidth || 200;
            var height = diagram.element.clientHeight || 200;
            var map = [], node, shape;
            for (var i = 0, len = graph.nodes.length; i < len; i++) {
                node = graph.nodes[i];
                var p = node.position;
                if (Utils.isUndefined(p)) {
                    if (Utils.isDefined(node.x) && Utils.isDefined(node.y)) {
                        p = new Point(node.x, node.y);
                    }
                    else {
                        p = new Point(Utils.randomInteger(10, width - 20), Utils.randomInteger(10, height - 20));
                    }
                }
                var opt = {};

                if (node.id === "0") {
                    /* kendo.deepExtend(opt,
                     {
                     background: "Orange",
                     data: 'circle',
                     width: 100,
                     height: 100,
                     center: new Point(50, 50)
                     });*/
                }
                else if (randomSize) {
                    kendo.deepExtend(opt, {
                        width: Math.random() * 150 + 20,
                        height: Math.random() * 80 + 50,
                        data: 'rectangle',
                        background: "#778899"
                    });
                }

                shape = this._addShape(diagram, p, node.id, opt);
                //shape.content(node.id);

                var bounds = shape.bounds();
                if (Utils.isDefined(bounds)) {
                    node.x = bounds.x;
                    node.y = bounds.y;
                    node.width = bounds.width;
                    node.height = bounds.height;
                }
                map[node.id] = shape;
            }
            for (var gli = 0; gli < graph.links.length; gli++) {
                var link = graph.links[gli];
                var sourceShape = map[link.source.id];
                if (Utils.isUndefined(sourceShape)) {
                    continue;
                }
                var targetShape = map[link.target.id];
                if (Utils.isUndefined(targetShape)) {
                    continue;
                }
                this._addConnection(diagram, sourceShape, targetShape, {id: link.id});

            }
            if (doLayout) {
                var l = new diagram.SpringLayout(diagram);
                l.layoutGraph(graph, {limitToView: false});
                for (var shi = 0; shi < graph.nodes.length; shi++) {
                    node = graph.nodes[shi];
                    shape = map[node.id];
                    shape.bounds(new Rect(node.x, node.y, node.width, node.height));
                }
            }
        },

        /**
         * Creates a balanced tree with the specified number of levels and siblings count.
         * Note that for a balanced tree of level N and sibling count s, counting the root as level zero:
         *  - NodeCount = (1-s^(N+1))/(1-s)]
         *  - LinkCount = s.(1-s^N)/(1-s)
         * @param levels How many levels the tree should have.
         * @param siblingsCount How many siblings each level should have.
         * @returns {diagram.Graph}
         */
        createBalancedTree: function (levels, siblingsCount) {
            if (Utils.isUndefined(levels)) {
                levels = 3;
            }
            if (Utils.isUndefined(siblingsCount)) {
                siblingsCount = 3;
            }

            var g = new diagram.Graph(), counter = -1, lastAdded = [], news;
            if (levels <= 0 || siblingsCount <= 0) {
                return g;
            }
            var root = new Node((++counter).toString());
            g.addNode(root);
            g.root = root;
            lastAdded.push(root);
            for (var i = 0; i < levels; i++) {
                news = [];
                for (var j = 0; j < lastAdded.length; j++) {
                    var parent = lastAdded[j];
                    for (var k = 0; k < siblingsCount; k++) {
                        var item = new Node((++counter).toString());
                        g.addLink(parent, item);
                        news.push(item);
                    }
                }
                lastAdded = news;
            }
            return g;
        },

        /**
         * Creates a balanced tree with the specified number of levels and siblings count.
         * Note that for a balanced forest of level N, sibling count s and tree count t, counting the root as level zero:
         *  - NodeCount = t.(1-s^(N+1))/(1-s)]
         *  - LinkCount = t.s.(1-s^N)/(1-s)
         * @param levels How many levels the tree should have.
         * @param siblingsCount How many siblings each level should have.
         * @returns {diagram.Graph}
         * @param treeCount The number of trees the forest should have.
         */
        createBalancedForest: function (levels, siblingsCount, treeCount) {
            if (Utils.isUndefined(levels)) {
                levels = 3;
            }
            if (Utils.isUndefined(siblingsCount)) {
                siblingsCount = 3;
            }
            if (Utils.isUndefined(treeCount)) {
                treeCount = 5;
            }
            var g = new diagram.Graph(), counter = -1, lastAdded = [], news;
            if (levels <= 0 || siblingsCount <= 0 || treeCount <= 0) {
                return g;
            }

            for (var t = 0; t < treeCount; t++) {
                var root = new Node((++counter).toString());
                g.addNode(root);
                lastAdded = [root];
                for (var i = 0; i < levels; i++) {
                    news = [];
                    for (var j = 0; j < lastAdded.length; j++) {
                        var parent = lastAdded[j];
                        for (var k = 0; k < siblingsCount; k++) {
                            var item = new Node((++counter).toString());
                            g.addLink(parent, item);
                            news.push(item);
                        }
                    }
                    lastAdded = news;
                }
            }
            return g;
        },

        /**
         * Creates a random graph (uniform distribution) with the specified amount of nodes.
         * @param nodeCount The amount of nodes the random graph should have.
         * @param maxIncidence The maximum allowed degree of the nodes.
         * @param isTree Whether the return graph should be a tree (default: false).
         * @returns {diagram.Graph}
         */
        createRandomConnectedGraph: function (nodeCount, maxIncidence, isTree) {

            /* Swa's Mathematica export of random Bernoulli graphs
             gr[n_,p_]:=Module[{g=RandomGraph[BernoulliGraphDistribution[n,p],VertexLabels->"Name",DirectedEdges->True]},
             While[Not[ConnectedGraphQ[g]],g=RandomGraph[BernoulliGraphDistribution[n,p],VertexLabels->"Name",DirectedEdges->True]];g];
             project[a_]:=("\""<>ToString[Part[#,1]]<>"->"<>ToString[Part[#,2]]<>"\"")&     @ a;
             export[g_]:=project/@ EdgeList[g]
             g = gr[12,.1]
             export [g]
             */

            if (Utils.isUndefined(nodeCount)) {
                nodeCount = 40;
            }
            if (Utils.isUndefined(maxIncidence)) {
                maxIncidence = 4;
            }
            if (Utils.isUndefined(isTree)) {
                isTree = false;
            }

            var g = new diagram.Graph(), counter = -1;
            if (nodeCount <= 0) {
                return g;
            }

            var root = new Node((++counter).toString());
            g.addNode(root);
            if (nodeCount === 1) {
                return g;
            }
            if (nodeCount > 1) {
                // random tree
                for (var i = 1; i < nodeCount; i++) {
                    var poolNode = g.takeRandomNode([], maxIncidence);
                    if (!poolNode) {
                        //failed to find one so the graph will have less nodes than specified
                        break;
                    }
                    var newNode = g.addNode(i.toString());
                    g.addLink(poolNode, newNode);
                }
                if (!isTree && nodeCount > 1) {
                    var randomAdditions = Utils.randomInteger(1, nodeCount);
                    for (var ri = 0; ri < randomAdditions; ri++) {
                        var n1 = g.takeRandomNode([], maxIncidence);
                        var n2 = g.takeRandomNode([], maxIncidence);
                        if (n1 && n2 && !g.areConnected(n1, n2)) {
                            g.addLink(n1, n2);
                        }
                    }
                }
                return g;
            }
        },

        /**
         * Generates a random diagram.
         * @param diagram The host diagram.
         * @param shapeCount The number of shapes the random diagram should contain.
         * @param maxIncidence The maximum degree the shapes can have.
         * @param isTree Whether the generated diagram should be a tree
         * @param layoutType The optional layout type to apply after the diagram is generated.
         */
        randomDiagram: function (diagram, shapeCount, maxIncidence, isTree, randomSize) {
            var g = diagram.Graph.Utils.createRandomConnectedGraph(shapeCount, maxIncidence, isTree);
            Graph.Utils.createDiagramFromGraph(diagram, g, false, randomSize);
        }
    };

    kendo.deepExtend(diagram, {
        init: function (element) {
            kendo.init(element, diagram.ui);
        },

        Point: Point,
        Intersect: Intersect,
        Geometry: Geometry,
        Rect: Rect,
        Size: Size,
        RectAlign: RectAlign,
        Matrix: Matrix,
        MatrixVector: MatrixVector,
        normalVariable: normalVariable,
        randomId: randomId,
        Dictionary: Dictionary,
        HashTable: HashTable,
        Queue: Queue,
        Set: Set,
        Node: Node,
        Link: Link,
        Graph: Graph,
        PathDefiner: PathDefiner
    });
})(window.kendo.jQuery);

(function ($, undefined) {
    // Imports ================================================================
    var kendo = window.kendo,
        Observable = kendo.Observable,
        diagram = kendo.dataviz.diagram,
        Class = kendo.Class,
        deepExtend = kendo.deepExtend,
        dataviz = kendo.dataviz,
        Point = diagram.Point,
        Rect = diagram.Rect,
        RectAlign = diagram.RectAlign,
        Matrix = diagram.Matrix,
        Utils = diagram.Utils,
        isUndefined = Utils.isUndefined,
        MatrixVector = diagram.MatrixVector;

    // Constants ==============================================================
    var SVGNS = "http://www.w3.org/2000/svg",
        SVGXLINK = "http://www.w3.org/1999/xlink",
        Markers = {
            none: "none",
            arrowStart: "ArrowStart",
            filledCircle: "FilledCircle",
            arrowEnd: "ArrowEnd"
        },
        DEFAULTWIDTH = 100,
        DEFAULTHEIGHT = 100,
        FULL_CIRCLE_ANGLE = 360;

    diagram.Markers = Markers;

    var Scale = Class.extend({
        init: function (x, y) {
            this.x = x;
            this.y = y;
        },
        toMatrix: function () {
            return Matrix.scaling(this.x, this.y);
        },
        toString: function () {
            return kendo.format("scale({0},{1})", this.x, this.y);
        },
        invert: function() {
            return new Scale(1/this.x, 1/this.y);
        }
    });

    var Translation = Class.extend({
        init: function (x, y) {
            this.x = x;
            this.y = y;
        },
        toMatrixVector: function () {
            return new MatrixVector(0, 0, 0, 0, this.x, this.y);
        },
        toMatrix: function () {
            return Matrix.translation(this.x, this.y);
        },
        toString: function () {
            return kendo.format("translate({0},{1})", this.x, this.y);
        },
        plus: function (delta) {
            this.x += delta.x;
            this.y += delta.y;
        },
        times: function (factor) {
            this.x *= factor;
            this.y *= factor;
        },
        length: function () {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        },
        normalize: function () {
            if (this.Length === 0) {
                return;
            }
            this.times(1 / this.length());
        },
        invert: function() {
            return new Translation(-this.x, -this.y);
        }
    });

    var Rotation = Class.extend({
        init: function (angle, x, y) {
            this.x = x || 0;
            this.y = y || 0;
            this.angle = angle;
        },
        toString: function () {
            if (this.x && this.y) {
                return kendo.format("rotate({0},{1},{2})", this.angle, this.x, this.y);
            } else {
                return kendo.format("rotate({0})", this.angle);
            }
        },
        toMatrix: function () {
            return Matrix.rotation(this.angle, this.x, this.y); // T*R*T^-1
        },
        center: function () {
            return new Point(this.x, this.y);
        },
        invert: function() {
            return new Rotation(FULL_CIRCLE_ANGLE - this.angle, this.x, this.y);
        }
    });

    Rotation.create = function (rotation) {
        return new Rotation(rotation.angle, rotation.x, rotation.y);
    };

    Rotation.parse = function (str) {
        var values = str.slice(1, str.length - 1).split(","),
            angle = values[0],
            x = values[1],
            y = values[2];
        var rotation = new Rotation(angle, x, y);
        return rotation;
    };

    var CompositeTransform = Class.extend({
        init: function (x, y, scaleX, scaleY, angle, center) {
            this.translate = new Translation(x, y);
            if (scaleX !== undefined && scaleY !== undefined) {
                this.scale = new Scale(scaleX, scaleY);
            }
            if (angle !== undefined) {
                this.rotate = center ? new Rotation(angle, center.x, center.y) : new Rotation(angle);
            }
        },
        toString: function () {
            var toString = function (transform) {
                return transform ? transform.toString() : "";
            };

            return toString(this.translate) +
                toString(this.rotate) +
                toString(this.scale);
        },
        render: function (visual) {
            visual.setAttribute("transform", this.toString());
        },
        toMatrix: function () {
            var m = Matrix.unit();

            if (this.translate) {
                m = m.times(this.translate.toMatrix());
            }
            if (this.rotate) {
                m = m.times(this.rotate.toMatrix());
            }
            if (this.scale) {
                m = m.times(this.scale.toMatrix());
            }
            return m;
        },
        invert: function() {
            var rotate = this.rotate ? this.rotate.invert() : undefined,
                rotateMatrix = rotate ? rotate.toMatrix() : Matrix.unit(),
                scale = this.scale ? this.scale.invert() : undefined,
                scaleMatrix = scale ? scale.toMatrix() : Matrix.unit();

            var translatePoint = new Point(-this.translate.x, -this.translate.y);
            translatePoint = rotateMatrix.times(scaleMatrix).apply(translatePoint);
            var translate = new Translation(translatePoint.x, translatePoint.y);

            var transform = new CompositeTransform();
            transform.translate = translate;
            transform.rotate = rotate;
            transform.scale = scale;

            return transform;
        }
    });

    function sizeTransform(element) {
        var scaleX = element._originWidth ? element.options.width / element._originWidth : 1,
            scaleY = element._originHeight ? element.options.height / element._originHeight : 1,
            x = element.options.x || 0,
            y = element.options.y || 0;

        element._transform.translate = new Translation(x, y);
        element._transform.scale = new Scale(scaleX, scaleY);
        element._renderTransform();
    }

    var Element = Class.extend({
        init: function (domElement, options) {
            var element = this;
            this._originSize = Rect.empty();
            this._visible = true;
            this._transform = new CompositeTransform();
            element.domElement = domElement;
            element.options = deepExtend({}, element.options, options);
            element.redraw();
        },
        visible: function (value) {
            if (isUndefined(value)) {
                return this._visible;
            } else {
                this._visible = value;
                this.domElement.setAttribute("visibility", (value ? "visible" : "hidden"));
            }
        },
        setAtr: function (atr, prop) {
            if (isUndefined(prop) || isUndefined(this.options[prop])) {
                return;
            }
            if (this.options[prop] !== undefined) {
                this.domElement.setAttribute(atr, this.options[prop]);
            }
        },
        redraw: function (options) {
            if (options) {
                deepExtend(this.options, options);
            }
            this.setAtr("id", "id");
        },
        position: function (x, y) {
            if (y !== undefined) {
                this.options.x = x;
                this.options.y = y;
                this._pos = new Point(x, y);
            }
            else if (x instanceof Point) {
                this._pos = x;
                this.options.x = this._pos.x;
                this.options.y = this._pos.y;
            }
            this._transform.translate = new Translation(this.options.x, this.options.y);
            this._renderTransform();
            return this._pos;
        },
        rotate: function (angle, center) {
            if (angle !== undefined) {
                this._transform.rotate = new Rotation(angle, center.x, center.y);
                this._renderTransform();
            }
            return this._transform.rotate || new Rotation(0);
        },
        _renderTransform: function () {
            this._transform.render(this.domElement);
        },
        _hover: function () {
        },
        _measure: function (force) {
            var box, n = this.domElement;
            if (!this._measured || force) {
                try {
                    box = n.getBBox();
                    if (box.width && box.height) {
                        this._originSize = new Rect(box.left, box.right, box.width, box.height);
                        this._originWidth = box.width;
                        this._originHeight = box.height;
                        this._measured = true;
                        return this._originSize;
                    }
                }
                catch (e) {
                }
            }
        }
    });

    // Visual but with no size.
    var VisualBase = Element.extend({
        init: function (domElement, options) {
            var that = this;
            Element.fn.init.call(that, domElement, options);
            domElement._kendoElement = this;
        },
        options: {
            stroke: {
                color: "gray",
                width: 1,
                dashType: "none"
            }
        },
        background: function (value) {
            if (value !== undefined) {
                this.options.background = value;
            }
            this._background(this.options.background);
        },
        redraw: function (options) {
            var that = this;
            Element.fn.redraw.call(that, options);
            that._setStroke();
            that.setAtr("fill-opacity", "fillOpacity");
            that.background();
        },
        _setStroke: function() {
            var stroke = this.options.stroke || {};
            this.domElement.setAttribute("stroke", stroke.color);
            this.domElement.setAttribute("stroke-dasharray", this._renderDashType());
            this.domElement.setAttribute("stroke-width", stroke.width);
        },
        _renderDashType: function() {
            var stroke = this.options.stroke || {},
                width = stroke.width || 1,
                dashType = stroke.dashType;

            if (dashType && dashType != "solid") {
                var dashArray = dataviz.DASH_ARRAYS[dashType.toLowerCase()] || [],
                    result = [],
                    i;

                for (i = 0; i < dashArray.length; i++) {
                    result.push(dashArray[i] * width);
                }

                return result.join(" ");
            }
        },
        _hover: function (value) {
            var color = this.options.background;

            if (value && Utils.isDefined(this.options.hover.background)) {
                color = this.options.hover.background;
            }

            this._background(color);
        },
        _background: function (value) {
            this.domElement.setAttribute("fill", this._getColor(value));
        },
        _getColor: function (value) {
            var bg;
            if (value != "none") {
                var color = new dataviz.Color(value);
                bg = color.toHex();
            }
            else {
                bg = value;
            }
            return bg;
        }
    });

    var Visual = VisualBase.extend({
        init: function (domElement, options) {
            var that = this;
            VisualBase.fn.init.call(that, domElement, options);
        },
        redraw: function (options) {
            var that = this;
            VisualBase.fn.redraw.call(that, options);

            if (that.options.x !== undefined && that.options.y !== undefined) {
                that.position(that.options.x, that.options.y);
            }
            that.size();
        },
        size: function (value) {
            if (value !== undefined) {
                this.options.width = value.width;
                this.options.height = value.height;
            }
            this._sz = { width: this.options.width, height: this.options.height };
            this.setAtr("width", "width");
            this.setAtr("height", "height");
            this.setAtr("background", "background");
            return this._sz;
        }
    });

    var TextBlock = VisualBase.extend({
        init: function (options) {
            var that = this;
            Visual.fn.init.call(that, document.createElementNS(SVGNS, "text"), options);
            this.domElement.setAttribute("dominant-baseline", "hanging");
        },
        options: {
            stroke: {
                color: "none",
                width: 0,
                dashType: "none"
            },
            fontSize: 15,
            fontVariant: "normal",
            fontWeight: "normal",
            anchor: "middle",
            background: "black",
            align: ""
        },
        content: function (text) {
            if (text !== undefined) {
                this.domElement.textContent = this.options.text = text;
                this._align();
            }

            return this.options.text;
        },
        redraw: function (options) {
            Visual.fn.redraw.call(this, options);
            this.setAtr("font-family", "fontFamily");
            this.setAtr("font-variant", "fontVariant");
            this.setAtr("font-size", "fontSize");
            this.setAtr("font-weight", "fontWeight");
            this.setAtr("font-style", "fontStyle");
            this.setAtr("text-decoration", "textDecoration");
            this.setAtr("fill", "color");
            this.content(this.options.text);
        },
        size: function () {
            sizeTransform(this);
        },
        bounds: function () {
            var o = this.options,
                containerRect = new Rect(0, 0, o.width, o.height);
            return containerRect;
        },
        align: function (alignment) {
            this.options.align = alignment;
            this._align(alignment);
        },
        _align: function () {
            if (!this.options.align) {
                return;
            }
            this._measure(true);
            var o = this.options,
                containerRect = this.bounds(),
                aligner = new RectAlign(containerRect),
                contentBounds = aligner.align(this._originSize, o.align);

            this.position(contentBounds.topLeft());
            o.width = contentBounds.width;
            o.height = contentBounds.height;
            this.size();
        }
    });

    var TextBlockEditor = Observable.extend({
        init: function (domElement, options) {
            Observable.fn.init.call(this);

            var element = this;
            element.domElement = domElement || this._createEditor();
            element.options = deepExtend({}, element.options, options);
            element.redraw();
        },
        visible: function (value) {
            if (value !== undefined) {
                this._isVisible = value;
                this.domElement.style.visibility = value ? "visible" : "hidden";
            }
            return this._isVisible;
        },
        position: function (x, y) {
            if (y !== undefined) {
                this.options.x = x;
                this.options.y = y;
                this._pos = new Point(x, y);
            }
            else if (x instanceof Point) {
                this._pos = x;
                this.options.x = this._pos.x;
                this.options.y = this._pos.y;
            }

            $(this.domElement).css({left: this._pos.x + "px", top: this._pos.y + "px"});
            return this._pos;
        },
        size: function (w, h) {
            var isSet = false;
            if (h !== undefined) {
                this._size = { width: w, height: h };
                isSet = true;
            }
            else if (w === Object(w)) {
                this._size = { width: w.width, height: w.height };
                isSet = true;
            }

            if (isSet) {
                deepExtend(this.options, this._size);
                $(this.domElement).css({width: this._size.width + "px", height: this._size.height + "px"});
            }

            return this._size;
        },
        focus: function () {
            $(this.domElement).focus();
        },
        content: function (text) {
            if (!isUndefined(text)) {
                this.domElement.value = this.options.text = text;
            }

            return this.domElement.value;
        },
        redraw: function (options) {
            this.options = deepExtend(this.options, options);
            this.content(this.options.text);
        },
        _createEditor: function () {
            var that = this,
                input = $("<input type='text' class='textEditable' />")
                    .css({ position: "absolute", zIndex: 100, fontSize: "16px" })
                    .on("mousedown mouseup click dblclick", function (e) {
                        e.stopPropagation();
                    })
                    .on("keydown", function (e) {
                        e.stopPropagation();
                    })
                    .on("keypress", function (e) {
                        if (e.keyCode == kendo.keys.ENTER) {
                            that.trigger("finishEdit", e);
                        }
                        e.stopPropagation();
                    })
                    .on("focusout", function (e) {
                        that.trigger("finishEdit", e);
                        e.stopPropagation();
                    });
            return input[0];
        }
    });

    var Rectangle = Visual.extend({
        init: function (options) {
            Visual.fn.init.call(this, document.createElementNS(SVGNS, "rect"), options);
        },
        options: {
            stroke: {},
            background: "none"
        },
        redraw: function (options) {
            Visual.fn.redraw.call(this, options);
            this.setAtr("rx", "cornerRadius");
            this.setAtr("ry", "cornerRadius");
            this._setStroke();
        }
    });

    var Path = VisualBase.extend({
        init: function (options) {
            var that = this;
            VisualBase.fn.init.call(that, document.createElementNS(SVGNS, "path"), options);
        },
        options: {
            autoSize: true
        },
        data: function (value) {
            if (value) {
                this.options.data = value;
            }
            else {
                return this.options.data;
            }
        },
        size: function () {
            sizeTransform(this);
        },
        redraw: function (options) {
            var that = this;
            VisualBase.fn.redraw.call(that, options);
            that.size();
            that.setAtr("d", "data");
            if (this.options.startCap && this.options.startCap !== Markers.none) {
                this.domElement.setAttribute("marker-start", "url(#" + this.options.startCap + ")");
            }
            else {
                this.domElement.removeAttribute("marker-start");
            }
            if (this.options.endCap && this.options.endCap !== Markers.none) {
                this.domElement.setAttribute("marker-end", "url(#" + this.options.endCap + ")");
            }
            else {
                this.domElement.removeAttribute("marker-end");
            }

            // SVG markers are not refreshed after the line has changed. This fixes the problem.
            if (this.domElement.parentNode && navigator.appVersion.indexOf("MSIE 10") != -1) {
                this.domElement.parentNode.insertBefore(this.domElement, this.domElement);
            }
        }
    });

    var Marker = Element.extend({
        init: function (options) {
            var that = this, childElement;
            Element.fn.init.call(that, document.createElementNS(SVGNS, "marker"), options);
            var o = that.options;

            if (o.path) {
                childElement = new Path(o.path);
            }
            else if (o.circle) {
                childElement = new Circle(o.circle);
            }
            if (childElement) {
                this.domElement.appendChild(childElement.domElement);
            }
        },
        redraw: function (options) {
            Element.fn.redraw.call(this, options);
            var that = this, o = that.options;
            if (o.ref) {
                that.domElement.refX.baseVal.value = o.ref.x;
                that.domElement.refY.baseVal.value = o.ref.y;
            }
            if (o.width) {
                that.domElement.markerWidth.baseVal.value = o.width;
            }
            if (o.height) {
                that.domElement.markerHeight.baseVal.value = o.height;
            }
            this.setAtr("orient", "orientation");
            this.setAtr("viewBox", "viewBox");
        }
    });

    var Mask = Element.extend({
        init: function (options) {
            var that = this, childElement;
            Element.fn.init.call(that, document.createElementNS(SVGNS, "mask"), options);
            var o = that.options;

            if (o.path) {
                childElement = new Path(o.path);
            }
            else if (o.circle) {
                childElement = new Circle(o.circle);
            }
            else if (o.rectangle) {
                childElement = new Rectangle(o.rectangle);
            }
            if (childElement) {
                this.domElement.appendChild(childElement.domElement);
            }
            this.setAtr("id", "id");
        },
        redraw: function (options) {
            Element.fn.redraw.call(this, options);
            var that = this, o = that.options;

            if (o.width) {
                that.domElement.width.baseVal.value = o.width;
            }
            if (o.height) {
                that.domElement.height.baseVal.value = o.height;
            }

        }
    });

    var Line = VisualBase.extend({
        init: function (options) {
            VisualBase.fn.init.call(this, document.createElementNS(SVGNS, "line"), options);
            this.options.from = this.options.from || new Point();
            this.options.to = this.options.to || new Point();
        },
        redraw: function (options) {
            VisualBase.fn.redraw.call(this, options);
            if (this.options.from) {
                this.domElement.setAttribute("x1", this.options.from.x.toString());
                this.domElement.setAttribute("y1", this.options.from.y.toString());
            }
            if (this.options.to) {
                this.domElement.setAttribute("x2", this.options.to.x.toString());
                this.domElement.setAttribute("y2", this.options.to.y.toString());
            }
            if (this.options.startCap && this.options.startCap !== Markers.none) {
                this.domElement.setAttribute("marker-start", "url(#" + this.options.startCap + ")");
            }
            else {
                this.domElement.removeAttribute("marker-start");
            }
            if (this.options.endCap && this.options.endCap !== Markers.none) {
                this.domElement.setAttribute("marker-end", "url(#" + this.options.endCap + ")");
            }
            else {
                this.domElement.removeAttribute("marker-end");
            }

            // SVG markers are not refreshed after the line has changed. This fixes the problem.
            if (this.domElement.parentNode && navigator.appVersion.indexOf("MSIE 10") != -1) {
                this.domElement.parentNode.insertBefore(this.domElement, this.domElement);
            }
        }
    });

    var Polyline = VisualBase.extend({
        init: function (options) {
            var that = this;
            VisualBase.fn.init.call(that, document.createElementNS(SVGNS, "polyline"), options);
            if (Utils.isDefined(options) && options.points !== null) {
                this.points(that.options.points);
            }

            this.background("none");

        },
        refresh: function () {
            if (this._points === null || this._points.length === 0) {
                return;
            }
            var pointsString = "", i;
            for (i = 0; i < this._points.length; i++) {
                // todo: toArray and fromArray to allow Point and Tuple
                pointsString += " " + this._points[i].x + "," + this._points[i].y;
            }
            this.domElement.setAttribute("points", pointsString.trim());
            this.domElement.setAttribute("stroke", "Orange");
            this.domElement.setAttribute("stroke-width", "5");

        },
        points: function (value) {
            if (isUndefined(value)) {
                return this._points;
            }
            else {
                this._points = value;
                this.refresh();
            }
        },
        redraw: function () {
            this.refresh();
        },
        options: {
            stroke: {
                color: "gray",
                width: 1
            },
            backgrounds: "none",
            points: []
        }
    });

    var Image = Element.extend({
        init: function (options) {
            Element.fn.init.call(this, document.createElementNS(SVGNS, "image"), options);
        },
        options: {
            autoSize: true
        },
        redraw: function (options) {
            Element.fn.redraw.call(this, options);
            this.domElement.setAttributeNS(SVGXLINK, "href", this.options.source);
            this.setAtr("width", "width");
            this.setAtr("height", "height");
            this.setAtr("x", "x");
            this.setAtr("y", "x");
        }
    });

    var Group = Element.extend({
        init: function (options) {
            Element.fn.init.call(this, document.createElementNS(SVGNS, "g"), options);
            this.width = this.options.width;
            this.height = this.options.height;
        },
        options: {
            autoSize: true
        },
        append: function (visual) {
            this.domElement.appendChild(visual.domElement);
            visual.canvas = this.canvas;
        },
        remove: function (visual) {
            if (visual.domElement) {
                this.domElement.removeChild(visual.domElement);
            }
            else {
                this.domElement.removeChild(visual);
            }
        },
        clear: function () {
            while (this.domElement.lastChild) {
                this.domElement.removeChild(this.domElement.lastChild);
            }
        },
        toFront: function (visuals) {
            var visual, i, n = this.domElement;

            for (i = 0; i < visuals.length; i++) {
                visual = visuals[i];
                n.appendChild(visual.domElement);
            }
        },
        toBack: function (visuals) {
            var visual, i;
            for (i = 0; i < visuals.length; i++) {
                visual = visuals[i];
                this.domElement.insertBefore(visual.domElement, this.domElement.firstChild);
            }
        },
        toIndex: function (visuals, indices) { // bring the items to the following index
            var visual, i, index;
            for (i = 0; i < visuals.length; i++) {
                visual = visuals[i];
                index = indices[i];
                this.domElement.insertBefore(visual.domElement, this.domElement.children[index]);
            }
        },
        size: function () {
            sizeTransform(this);
        },
        redraw: function (options) {
            Element.fn.redraw.call(this, options);
            this.size();
        }
    });

    var Circle = VisualBase.extend({
        init: function (options) {
            var that = this;
            if (options && options.radius) {
                options.width = options.radius * 2;
                options.height = options.radius * 2;
            }
            VisualBase.fn.init.call(that, document.createElementNS(SVGNS, "ellipse"), options);
        },
        redraw: function (options) {
            if (options && Utils.isNumber(options.width) && Utils.isNumber(options.height)) {
                options.center = new Point(options.width / 2, options.height / 2);
            }
            VisualBase.fn.redraw.call(this, options);
            var n = this.domElement,
                o = this.options,
                rx = o.width / 2 || o.rx, ry = o.height / 2 || o.rx;

            if (rx && ry) {
                n.rx.baseVal.value = rx;
                n.ry.baseVal.value = ry;
            }

            if (o.center) {
                n.cx.baseVal.value = o.center.x;
                n.cy.baseVal.value = o.center.y;
            } else if (Utils.isDefined(o.x) && Utils.isDefined(o.y)) {
                n.cx.baseVal.value = o.x + rx;
                n.cy.baseVal.value = o.y + ry;
            } else {
                n.cx.baseVal.value = rx;
                n.cy.baseVal.value = ry;
            }
        }
    });

    var Canvas = Visual.extend({
        init: function (element, options) {
            var that = this;
            Visual.fn.init.call(that, document.createElementNS(SVGNS, "svg"), options);
            this.markers = [];
            this.gradients = [];
            this.visuals = [];
            this.defsNode = document.createElementNS(SVGNS, "defs");
            this.domElement.appendChild(this.defsNode);
            if (element.context) {
                this.element = element.context; // kendo wrapped object
            }
            else {
                this.element = element;
            }
            $(this.domElement).css({
                width: this.options.width,
                height: this.options.height
            });
            this.element.appendChild(that.domElement);
            this.domElement.style.background = that.options.background;
            this.domElement.setAttribute('xmlns', SVGNS);
            this.domElement.setAttribute('xmlns:xlink', SVGXLINK);
            this.element.setAttribute("tabindex", "0"); //ensure tabindex so the the canvas receives key events
            this._markers();
            this.masks = [];
        },
        options: {
            width: "100%",
            height: "100%",
            background: "none",
            id: "SVGRoot"
        },
        bounds: function () {
            var box = this.domElement.getBBox();
            return new Rect(0, 0, box.width, box.height);
        },
        focus: function () {
            this.element.focus();
        },
        size: function () {
            var canvas = this,
                size = Visual.fn.size.apply(canvas, arguments),
                viewBox = this.viewBox();

            this._styledSize(canvas.domElement);

            viewBox.width = size.width;
            viewBox.height = size.height;
            this.viewBox(viewBox);

            return size;
        },
        _styledSize: function (node) {
            var size = this._sz;
            $(node).css(size);
        },
        viewBox: function (rect) {
            var canvas = this;

            if (isUndefined(rect)) {
                return canvas.domElement.viewBox.baseVal ? Rect.toRect(canvas.domElement.viewBox.baseVal) : Rect.empty();
            }

            rect = Rect.toRect(rect);
            if (!isNaN(rect.width) && !isNaN(rect.height)) {
                this.domElement.setAttribute("viewBox", rect.toString(","));
            }
        },
        append: function (shape) {
            this.domElement.appendChild(shape.domElement);
            shape.canvas = this;
            this.visuals.push(shape);
            return this;
        },
        remove: function (visual) {
            if (Utils.indexOf(this.visuals, visual) >= 0) {
                this.domElement.removeChild(visual.domElement);
                visual.canvas = undefined;
                Utils.remove(this.visuals, visual);
                return this;
            }
        },
        insertBefore: function (visual, beforeVisual) {
            this.domElement.insertBefore(visual.domElement, beforeVisual.domElement);
            visual.canvas = this;
            this.visuals.push(visual);
            return this;
        },
        addMarker: function (marker) {
            this.defsNode.appendChild(marker.domElement);
            this.markers.push(marker);
        },
        removeMarker: function (marker) {
            if (marker && Utils.contains.contains(this.markers, marker)) {
                this.defsNode.removeChild(marker.domElement);
                Utils.remove(this.markers, marker);
            }
        },
        addMask: function (mask) {
            this.defsNode.appendChild(mask.domElement);
            this.masks.push(mask);
        },
        removeMask: function (mask) {
            if (mask && Utils.contains(this.masks, mask)) {
                this.defsNode.removeChild(mask.domElement);
                Utils.remove(this.masks, mask);
            }
        },
        removeGradient: function (gradient) {
            if (gradient && Utils.contains(this.gradients, gradient)) {
                this.defsNode.removeChild(gradient.domElement);
                Utils.remove(this.gradients, gradient);
            }
        },
        addGradient: function (gradient) {
            this.defsNode.appendChild(gradient.domElement);
            this.gradients.push(gradient);
        },
        clearMarkers: function () {
            var i;
            if (this.markers.length === 0) {
                return;
            }
            for (i = 0; i < this.markers.length; i++) {
                this.defsNode.removeChild(this.markers[i].domElement);
            }
            this.markers = [];
        },
        clear: function () {
            while (this.visuals.length) {
                this.remove(this.visuals[0]);
            }
        },
        mask: function (mask) {
            if (mask === null) {
                this.domElement.removeAttribute("mask");
            }
            else {
                this.domElement.setAttribute("mask", "url(#" + mask.domElement.id + ")");
            }

        },
        _markers: function () {
            this.addMarker(new Marker({
                path: {
                    data: "M 0 0 L 10 5 L 0 10 L 3 5 z",
                    background: "Black"
                },
                id: Markers.arrowEnd,
                orientation: "auto",
                width: 10,
                height: 10,
                ref: new Point(10, 5)
            }));
            this.addMarker(new Marker({
                path: {
                    data: "M 0 5 L 10 0 L 7 5 L 10 10 z",
                    background: "Black"
                },
                id: Markers.arrowStart,
                orientation: "auto",
                width: 10,
                height: 10,
                ref: new Point(0, 5)
            }));
            this.addMarker(new Marker({
                circle: {
                    width: 6,
                    height: 6,
                    center: new Point(5, 5),
                    stroke: {
                        width: 1
                    },
                    background: "black"
                },
                width: 10,
                height: 10,
                id: Markers.filledCircle,
                ref: new Point(5, 5),
                orientation: "auto"
            }));
        },
        destroy: function(clearHtml) {
            if(clearHtml) {
                $(this.element).remove();
            }
        }
    });

    kendo.deepExtend(diagram, {
        init: function (element) {
            kendo.init(element, diagram.ui);
        },
        Scale: Scale,
        Translation: Translation,
        Rotation: Rotation,
        Circle: Circle,
        Group: Group,
        Rectangle: Rectangle,
        Canvas: Canvas,
        Path: Path,
        Line: Line,
        Marker: Marker,
        Polyline: Polyline,
        CompositeTransform: CompositeTransform,
        TextBlock: TextBlock,
        TextBlockEditor: TextBlockEditor,
        Image: Image,
        Mask: Mask,
        Visual: Visual,
        VisualBase: VisualBase
    });
})(window.kendo.jQuery);

(function ($, undefined) {
        // Imports ================================================================
        var kendo = window.kendo,
            diagram = kendo.dataviz.diagram,
            Class = kendo.Class,
            Group = diagram.Group,
            TextBlock = diagram.TextBlock,
            Rect = diagram.Rect,
            Rectangle = diagram.Rectangle,
            Utils = diagram.Utils,
            isUndefined = Utils.isUndefined,
            Point = diagram.Point,
            Circle = diagram.Circle,
            Path = diagram.Path,
            Ticker = diagram.Ticker,
            deepExtend = kendo.deepExtend,
            Movable = kendo.ui.Movable;
        // Constants ==============================================================
        var Cursors = {
                arrow: "default",
                grip: "pointer",
                cross: "pointer",
                add: "pointer",
                move: "move",
                select: "pointer",
                south: "s-resize",
                east: "e-resize",
                west: "w-resize",
                north: "n-resize",
                rowresize: "row-resize",
                colresize: "col-resize"
            },
            HITTESTDISTANCE = 10,
            AUTO = "Auto",
            TOP = "Top",
            RIGHT = "Right",
            LEFT = "Left",
            BOTTOM = "Bottom",
            DEFAULTCONNECTORNAMES = [TOP, RIGHT, BOTTOM, LEFT, AUTO],
            ITEMROTATE = "itemRotate",
            ITEMBOUNDSCHANGE = "itemBoundsChange",
            ZOOMSTART = "zoomStart",
            ZOOMEND = "zoomEnd",
            SCROLL_MIN = -20000,
            SCROLL_MAX = 20000;

        diagram.Cursors = Cursors;

        function selectSingle(item, meta) {
            if (item.isSelected) {
                if (meta.ctrlKey) {
                    item.select(false);
                }
            } else {
                item.diagram.select(item, {addToSelection: meta.ctrlKey});
            }
        }

        var PositionAdapter = kendo.Class.extend({
            init: function (layoutState) {
                this.layoutState = layoutState;
                this.diagram = layoutState.diagram;
            },
            initState: function () {
                this.froms = [];
                this.tos = [];
                this.subjects = [];
                function pusher(id, bounds) {
                    var shape = this.diagram.getShapeById(id);
                    if (shape) {
                        this.subjects.push(shape);
                        this.froms.push(shape.bounds().topLeft());
                        this.tos.push(bounds.topLeft());
                    }
                }

                this.layoutState.nodeMap.forEach(pusher, this);
            },
            update: function (tick) {
                if (this.subjects.length <= 0) {
                    return;
                }
                for (var i = 0; i < this.subjects.length; i++) {
                    //todo: define a Lerp function instead
                    this.subjects[i].position(
                        new Point(this.froms[i].x + (this.tos[i].x - this.froms[i].x) * tick, this.froms[i].y + (this.tos[i].y - this.froms[i].y) * tick)
                    );
                }
            }
        });

        var LayoutUndoUnit = Class.extend({
            init: function (initialState, finalState, animate) {
                if (isUndefined(animate)) {
                    this.animate = false;
                }
                else {
                    this.animate = animate;
                }
                this._initialState = initialState;
                this._finalState = finalState;
                this.title = "Diagram layout";
            },
            undo: function () {
                this.setState(this._initialState);
            },
            redo: function () {
                this.setState(this._finalState);
            },
            setState: function (state) {
                var diagram = state.diagram;
                if (this.animate) {
                    state.linkMap.forEach(
                        function (id, points) {
                            var conn = diagram.getShapeById(id);
                            conn.visible(false);
                            if (conn) {
                                conn.points(points);
                            }
                        }
                    );
                    var ticker = new Ticker();
                    ticker.addAdapter(new PositionAdapter(state));
                    ticker.onComplete(function () {
                        state.linkMap.forEach(
                            function (id) {
                                var conn = diagram.getShapeById(id);
                                conn.visible(true);
                            }
                        );
                    });
                    ticker.play();
                }
                else {
                    state.nodeMap.forEach(function (id, bounds) {
                        var shape = diagram.getShapeById(id);
                        if (shape) {
                            shape.position(bounds.topLeft());
                        }
                    });
                    state.linkMap.forEach(
                        function (id, points) {
                            var conn = diagram.getShapeById(id);
                            if (conn) {
                                conn.points(points);
                            }
                        }
                    );
                }
            }
        });

        var CompositeUnit = Class.extend({
            init: function (unit) {
                this.units = [];
                this.title = "Composite unit";
                if (unit !== undefined) {
                    this.units.push(unit);
                }
            },
            add: function (undoUnit) {
                this.units.push(undoUnit);
            },
            undo: function () {
                for (var i = 0; i < this.units.length; i++) {
                    this.units[i].undo();
                }
            },
            redo: function () {
                for (var i = 0; i < this.units.length; i++) {
                    this.units[i].redo();
                }
            }
        });

        /**
         * Unit for content editing.
         */
        var ContentChangedUndoUnit = Class.extend({
            /**
             * Instantiates the unit.
             * @param element The element being edited.
             * @param newcontent The new content.
             * @param oldcontent The old content.
             */
            init: function (element, oldcontent, newcontent) {
                this.item = element;
                this._undoContent = oldcontent;
                this._redoContent = newcontent;
                this.title = "Content Editing";
                this.rebuild = this.item.options.hasOwnProperty("rebuild") ? this.item.options.rebuild : null;
            },
            undo: function () {
                this.item.content(this._undoContent);
                if (this.rebuild) {
                    this.rebuild.call(this.item, this.item);
                }
            },
            redo: function () {
                this.item.content(this._redoContent);
                if (this.rebuild) {
                    this.rebuild.call(this.item, this.item);
                }
            }

        });

        var ConnectionEditUnit = Class.extend({
            init: function (item, redoSource, redoTarget) {
                this.item = item;
                this._redoSource = redoSource;
                this._redoTarget = redoTarget;
                this._undoSource = item.source();
                this._undoTarget = item.target();
                this.title = "Connection Editing";
            },
            undo: function () {
                if (this._undoSource !== undefined) {
                    this.item.source(this._undoSource, false);
                }
                if (this._undoTarget !== undefined) {
                    this.item.target(this._undoTarget, false);
                }
            },
            redo: function () {
                if (this._redoSource !== undefined) {
                    this.item.source(this._redoSource, false);
                }
                if (this._redoTarget !== undefined) {
                    this.item.target(this._redoTarget, false);
                }
            }
        });

        var ConnectionEditUndoUnit = Class.extend({
            init: function (item, undoSource, undoTarget) {
                this.item = item;
                this._undoSource = undoSource;
                this._undoTarget = undoTarget;
                this._redoSource = item.source();
                this._redoTarget = item.target();
                this.title = "Connection Editing";
            },
            undo: function () {
                this.item.source(this._undoSource, false);
                this.item.target(this._undoTarget, false);
            },
            redo: function () {
                this.item.source(this._redoSource, false);
                this.item.target(this._redoTarget, false);
            }
        });

        var DeleteConnectionUnit = Class.extend({
            init: function (connection) {
                this.connection = connection;
                this.diagram = connection.diagram;
                this.targetConnector = connection.targetConnector;
                this.title = "Delete connection";
            },
            undo: function () {
                this.diagram.addConnection(this.connection, false);
            },
            redo: function () {
                this.diagram.remove(this.connection, false);
            }
        });

        var DeleteShapeUnit = Class.extend({
            init: function (shape) {
                this.shape = shape;
                this.diagram = shape.diagram;
                this.title = "Deletion";
            },
            undo: function () {
                this.diagram.addShape(this.shape, {undoable: false});
                this.shape.select(false);
            },
            redo: function () {
                this.shape.select(false);
                this.diagram.remove(this.shape, false);
            }
        });
        /**
         * Holds the undoredo state when performing a rotation, translation or scaling. The adorner is optional.
         * @type {*}
         */
        var TransformUnit = Class.extend({
            init: function (shapes, undoStates, adorner) {
                this.shapes = shapes;
                this.undoStates = undoStates;
                this.title = "Transformation";
                this.redoStates = [];
                this.adorner = adorner;
                for (var i = 0; i < this.shapes.length; i++) {
                    var shape = this.shapes[i];
                    this.redoStates.push(shape.bounds());
                }
            },
            undo: function () {
                for (var i = 0; i < this.shapes.length; i++) {
                    var shape = this.shapes[i];
                    shape.bounds(this.undoStates[i]);
                    if (shape.hasOwnProperty("layout")) {
                        shape.layout(shape, this.redoStates[i], this.undoStates[i]);
                    }
                }
                if (this.adorner) {
                    this.adorner.refreshBounds();
                    this.adorner.refresh();
                }
            },
            redo: function () {
                for (var i = 0; i < this.shapes.length; i++) {
                    var shape = this.shapes[i];
                    shape.bounds(this.redoStates[i]);
                    // the 'layout' property, if implemented, lets the shape itself work out what to do with the new bounds
                    if (shape.hasOwnProperty("layout")) {
                        shape.layout(shape, this.undoStates[i], this.redoStates[i]);
                    }
                }
                if (this.adorner) {
                    this.adorner.refreshBounds();
                    this.adorner.refresh();
                }
            }
        });

        var AddConnectionUnit = Class.extend({
            init: function (connection, diagram) {
                this.connection = connection;
                this.diagram = diagram;
                this.title = "New connection";
            },
            undo: function () {
                this.diagram.remove(this.connection, false);
            },
            redo: function () {
                this.diagram.addConnection(this.connection, false);
            }
        });

        var AddShapeUnit = Class.extend({
            init: function (shape, diagram) {
                this.shape = shape;
                this.diagram = diagram;
                this.title = "New shape";
            },
            undo: function () {
                this.diagram.remove(this.shape, false);
            },
            redo: function () {
                this.diagram.addShape(this.shape, {undoable: false});
            }
        });

        var PanUndoUnit = Class.extend({
            init: function (initialPosition, finalPosition, diagram) {
                this.initial = initialPosition;
                this.final = finalPosition;
                this.diagram = diagram;
                this.title = "Pan Unit";
            },
            undo: function () {
                this.diagram.pan(this.initial);
            },
            redo: function () {
                this.diagram.pan(this.final);
            }
        });

        var RotateUnit = Class.extend({
            init: function (adorner, shapes, undoRotates) {
                this.shapes = shapes;
                this.undoRotates = undoRotates;
                this.title = "Rotation";
                this.redoRotates = [];
                this.redoAngle = adorner._angle;
                this.adorner = adorner;
                this.center = adorner._innerBounds.center();
                for (var i = 0; i < this.shapes.length; i++) {
                    var shape = this.shapes[i];
                    this.redoRotates.push(shape.rotate().angle);
                }
            },
            undo: function () {
                var i, shape;
                for (i = 0; i < this.shapes.length; i++) {
                    shape = this.shapes[i];
                    shape.rotate(this.undoRotates[i], this.center);
                    if (shape.hasOwnProperty("layout")) {
                        shape.layout(shape);
                    }
                }
                if (this.adorner) {
                    this.adorner._initialize();
                    this.adorner.refresh();
                }
            },
            redo: function () {
                var i, shape;
                for (i = 0; i < this.shapes.length; i++) {
                    shape = this.shapes[i];
                    shape.rotate(this.redoRotates[i], this.center);
                    if (shape.hasOwnProperty("layout")) {
                        shape.layout(shape);
                    }
                }
                if (this.adorner) {
                    this.adorner._initialize();
                    this.adorner.refresh();
                }
            }
        });

        var ToFrontUnit = Class.extend({
            init: function (diagram, items, initialIndices) {
                this.diagram = diagram;
                this.indices = initialIndices;
                this.items = items;
                this.title = "Rotate Unit";
            },
            undo: function () {
                this.diagram._toIndex(this.items, this.indices);
            },
            redo: function () {
                this.diagram.toFront(this.items, false);
            }
        });

        var ToBackUnit = Class.extend({
            init: function (diagram, items, initialIndices) {
                this.diagram = diagram;
                this.indices = initialIndices;
                this.items = items;
                this.title = "Rotate Unit";
            },
            undo: function () {
                this.diagram._toIndex(this.items, this.indices);
            },
            redo: function () {
                this.diagram.toBack(this.items, false);
            }
        });

        /**
         * Undo-redo service.
         */
        var UndoRedoService = Class.extend({
            init: function () {
                this.stack = [];
                this.index = 0;
                this.capacity = 100;
            },

            /**
             * Starts the collection of units. Add those with
             * the addCompositeItem method and call commit. Or cancel to forget about it.
             */
            begin: function () {
                this.composite = new CompositeUnit();
            },

            /**
             * Cancels the collection process of unit started with 'begin'.
             */
            cancel: function () {
                this.composite = undefined;
            },

            /**
             * Commits a batch of units.
             */
            commit: function () {
                if (this.composite.units.length > 0) {
                    this._restart(this.composite);
                }
                this.composite = undefined;
            },

            /**
             * Adds a unit as part of the begin-commit batch.
             * @param undoUnit
             */
            addCompositeItem: function (undoUnit) {
                if (this.composite) {
                    this.composite.add(undoUnit);
                } else {
                    this.add(undoUnit);
                }
            },

            /**
             * Standard addition of a unit. See also the batch version; begin-addCompositeUnit-commit methods.
             * @param undoUnit The unit to be added.
             * @param execute If false, the unit will be added but not executed.
             */
            add: function (undoUnit, execute) {
                this._restart(undoUnit, execute);
            },

            /**
             * Returns the number of undoable unit in the stack.
             * @returns {Number}
             */
            count: function () {
                return this.stack.length;
            },

            /**
             * Rollback of the unit on top of the stack.
             */
            undo: function () {
                if (this.index > 0) {
                    this.index--;
                    this.stack[this.index].undo();
                }
            },

            /**
             * Redo of the last undone action.
             */
            redo: function () {
                if (this.stack.length > 0 && this.index < this.stack.length) {
                    this.stack[this.index].redo();
                    this.index++;
                }
            },

            _restart: function (composite, execute) {
                // throw away anything beyond this point if this is a new branch
                this.stack.splice(this.index, this.stack.length - this.index);
                this.stack.push(composite);
                if (isUndefined(execute) || (execute && (execute === true))) {
                    this.redo();
                }
                else {
                    this.index++;
                }
                // check the capacity
                if (this.stack.length > this.capacity) {
                    this.stack.splice(0, this.stack.length - this.capacity);
                    this.index = this.capacity; //points to the end of the stack
                }
            },

            /**
             * Clears the stack.
             */
            clear: function () {
                this.stack = [];
                this.index = 0;
            }
        });

// Tools =========================================

        var EmptyTool = Class.extend({
            init: function (toolService) {
                this.toolService = toolService;
            },
            start: function () {
            },
            move: function () {
            },
            end: function () {
            },
            doubleClick: function () {
            },
            tryActivate: function (p, meta) {
                return false;
            },
            getCursor: function () {
                return Cursors.arrow;
            }
        });

        var PanTool = EmptyTool.extend({
            init: function (toolService) {
                EmptyTool.fn.init.call(this, toolService);
            },
            tryActivate: function (p, meta) {
                return this.toolService.hoveredItem === undefined && meta.ctrlKey;
            },
            start: function (p) {
                this.toolService.isPanning = true;
                this.panStart = this.toolService.diagram._pan;
                this.panOffset = p;
                this.panDelta = new Point();//relative to root
            },
            move: function (p) {
                var diagram = this.toolService.diagram;
                this.panDelta = p.plus(this.panDelta).minus(this.panOffset);
                diagram.pan(this.panStart.plus(this.panDelta), {delta: p.minus(this.panOffset).times(1 / this.toolService.diagram.zoom())});
            },
            end: function () {
                var diagram = this.toolService.diagram;
                diagram.undoRedoService.begin();
                diagram.undoRedoService.add(new PanUndoUnit(this.panStart, diagram._pan, diagram));
                diagram.undoRedoService.commit();
                this.toolService.isPanning = false;
            },
            getCursor: function () {
                return Cursors.move;
            }
        });

        var ScrollerTool = EmptyTool.extend({
            init: function (toolService) {
                var tool = this;
                EmptyTool.fn.init.call(tool, toolService);

                var diagram = tool.toolService.diagram,
                    canvas = diagram.canvas;

                var scroller = diagram.scroller = tool.scroller = $(diagram.scrollable).kendoMobileScroller({
                    scroll: $.proxy(tool._move, tool)
                }).data("kendoMobileScroller");

                tool.movableCanvas = new Movable(canvas.element);
                var virtualScroll = function (dimension, min, max) {
                    dimension.makeVirtual();
                    dimension.virtualSize(min || SCROLL_MIN, max || SCROLL_MAX);
                };

                virtualScroll(scroller.dimensions.x);
                virtualScroll(scroller.dimensions.y);
                scroller.disable();
            },
            tryActivate: function (p, meta) {
                return this.toolService.hoveredItem === undefined && meta.ctrlKey;
            },
            start: function () {
                var diagram = this.toolService.diagram,
                    canvas = diagram.canvas,
                    canvasSize = canvas.size();

                this.scroller.enable();
                this.currentCanvasSize = canvasSize;
            },
            move: function () {
            },//the tool itself should not handle the scrolling. Let kendo scroller take care of this part. Check _move
            _move: function (args) {
                var tool = this,
                    diagram = tool.toolService.diagram,
                    canvas = diagram.canvas,
                    canvasSize = tool.currentCanvasSize || canvas.size(),
                    scrollPos = new Point(args.scrollLeft, args.scrollTop),
                    viewBox = new Rect(scrollPos.x, scrollPos.y, parseInt(canvasSize.width, 10), parseInt(canvasSize.height, 10));

                diagram._storePan(scrollPos.times(-1));
                tool.movableCanvas.moveTo(scrollPos);
                canvas.viewBox(viewBox);
            },
            end: function () {
                this.currentCanvasSize = undefined;
                this.scroller.disable();
            },
            getCursor: function () {
                return Cursors.move;
            }
        });

        /**
         * The tool handling the transformations via the adorner.
         * @type {*}
         */
        var PointerTool = Class.extend({
            init: function (toolService) {
                this.toolService = toolService;
            },
            tryActivate: function (p, meta) {
                return true; // the pointer tool is last and handles all others requests.
            },
            start: function (p, meta) {
                var diagram = this.toolService.diagram,
                    hoveredItem = this.toolService.hoveredItem;
                if (hoveredItem) {
                    selectSingle(hoveredItem, meta);
                    if (hoveredItem.adorner) { //connection
                        this.adorner = hoveredItem.adorner;
                        this.handle = this.adorner._hitTest(p);
                    }
                }
                if (!this.handle) {
                    this.handle = diagram._resizingAdorner._hitTest(p);
                    if (this.handle) {
                        this.adorner = diagram._resizingAdorner;
                    }
                }
                if (this.adorner) {
                    this.adorner.start(p);
                }
            },
            move: function (p) {
                var that = this;
                if (this.adorner) {
                    this.adorner.move(that.handle, p);
                }
            },
            end: function (p, meta) {
                var diagram = this.toolService.diagram,
                    service = this.toolService,
                    unit;

                if (this.adorner) {
                    unit = this.adorner.stop();
                    if (unit) {
                        diagram.undoRedoService.add(unit, false);
                    }
                }
                if(service.hoveredItem) {
                    this.toolService.triggerClick({item: service.hoveredItem, point: p, meta: meta});
                }
                this.adorner = undefined;
                this.handle = undefined;
            },
            getCursor: function (p) {
                return this.toolService.hoveredItem ? this.toolService.hoveredItem._getCursor(p) : Cursors.arrow;
            }
        });

        var SelectionTool = Class.extend({
            init: function (toolService) {
                this.toolService = toolService;
            },
            tryActivate: function (p, meta) {
                return this.toolService.diagram._canRectSelect() && this.toolService.hoveredItem === undefined && this.toolService.hoveredAdorner === undefined;
            },
            start: function (p) {
                var diagram = this.toolService.diagram;
                diagram.select(false);
                diagram.selector.start(p);
            },
            move: function (p) {
                var diagram = this.toolService.diagram;
                diagram.selector.move(p);
            },
            end: function (p, meta) {
                var diagram = this.toolService.diagram, hoveredItem = this.toolService.hoveredItem;
                var rect = diagram.selector.bounds();
                if ((!hoveredItem || !hoveredItem.isSelected) && !meta.ctrlKey) {
                    diagram.select(false);
                }
                if (!rect.isEmpty()) {
                    diagram.select(rect);
                }
                diagram.selector.end();
            },
            getCursor: function () {
                return Cursors.arrow;
            }
        });

        var ConnectionTool = Class.extend({
            init: function (toolService) {
                this.toolService = toolService;
                this.type = "ConnectionTool";
            },
            tryActivate: function (p, meta) {
                return this.toolService._hoveredConnector && !meta.ctrlKey; // connector it seems
            },
            start: function (p, meta) {
                var diagram = this.toolService.diagram,
                    connector = this.toolService._hoveredConnector,
                    connection = diagram.connect(connector._c, p);

                this.toolService._connectionManipulation(connection, connector._c.shape, true);
                this.toolService._removeHover();
                selectSingle(this.toolService.activeConnection, meta);
            },
            move: function (p) {
                this.toolService.activeConnection.target(p);
                return true;
            },
            end: function () {
                var nc = this.toolService.activeConnection, hi = this.toolService.hoveredItem, connector = this.toolService._hoveredConnector;
                if (connector && connector._c != nc.sourceConnector) {
                    nc.target(connector._c);
                }
                else if (hi) {
                    nc.target(hi);
                }
                this.toolService._connectionManipulation();
            },
            getCursor: function () {
                return Cursors.arrow;
            }
        });

        var ConnectionEditTool = Class.extend({
            init: function (toolService) {
                this.toolService = toolService;
                this.type = "ConnectionTool";
            },
            tryActivate: function (p, meta) {
                var item = this.toolService.hoveredItem,
                    isActive = item && item.path; // means it is connection
                if (isActive) {
                    this._c = item;
                }
                return isActive;
            },
            start: function (p, meta) {
                selectSingle(this._c, meta);
                this.handle = this._c.adorner._hitTest(p);
                this._c.adorner.start(p);
            },
            move: function (p) {
                this._c.adorner.move(this.handle, p);
                return true;
            },
            end: function (p, meta) {
                this.toolService.triggerClick({item: this._c, point: p, meta: meta});
                var unit = this._c.adorner.stop(p);
                this.toolService.diagram.undoRedoService.add(unit, false);
            },
            getCursor: function () {
                return Cursors.move;
            }
        });

        var ContentEditTool = EmptyTool.extend({
            init: function (toolService) {
                EmptyTool.fn.init.call(this, toolService);
            },
            doubleClick: function () {
                this.toolService.diagram.editor(this.toolService.hoveredItem);
            },
            tryActivate: function (p, meta) {
                return meta.doubleClick && this.toolService.hoveredItem;
            }
        });

        function testKey(key, str) {
            return str.charCodeAt(0) == key || str.toUpperCase().charCodeAt(0) == key;
        }

        /**
         * The service managing the tools.
         * @type {*}
         */
        var ToolService = Class.extend({
            init: function (diagram) {
                this.diagram = diagram;
                this.tools = [
                    new ContentEditTool(this),
                    diagram.options.useScroller ? new ScrollerTool(this) : new PanTool(this),
                    new ConnectionEditTool(this),
                    new ConnectionTool(this),
                    new SelectionTool(this),
                    new PointerTool(this)
                ]; // the order matters.

                this.activeTool = undefined;
            },
            start: function (p, meta) {
                meta = deepExtend({}, meta);
                this._updateHoveredItem(p);
                this._activateTool(p, meta);
                this.activeTool.start(p, meta);
                this._updateCursor(p);
                this.diagram.focus();
                this.startPoint = p;
                return true;
            },
            move: function (p, meta) {
                meta = deepExtend({}, meta);
                var updateHovered = true;
                if (this.activeTool) {
                    updateHovered = this.activeTool.move(p, meta);
                }
                if (updateHovered) {
                    this._updateHoveredItem(p);
                }
                this._updateCursor(p);
                return true;
            },
            end: function (p, meta) {
                meta = deepExtend({}, meta);
                if (this.activeTool) {
                    this.activeTool.end(p, meta);
                }
                this.activeTool = undefined;
                this._updateCursor(p);
                return true;
            },
            doubleClick: function (p, meta) {
                this._activateTool(p, deepExtend(meta, { doubleClick: true }));
                if (this.activeTool.doubleClick) {
                    this.activeTool.doubleClick(p, meta);
                }
                this._updateCursor(p);
            },
            keyDown: function (key, meta) {
                var diagram = this.diagram;
                meta = deepExtend({ ctrlKey: false, metaKey: false, altKey: false }, meta);
                if ((meta.ctrlKey || meta.metaKey) && !meta.altKey) {// ctrl or option
                    if (testKey(key, "a")) {// A: select all
                        diagram.select("All");
                        return true;
                    } else if (testKey(key, "z")) {// Z: undo
                        diagram.undo();
                        return true;
                    } else if (testKey(key, "y")) {// y: redo
                        diagram.redo();
                        return true;
                    } else if (testKey(key, "c")) {
                        diagram.copy();
                    } else if (testKey(key, "x")) {
                        diagram.cut();
                    } else if (testKey(key, "v")) {
                        diagram.paste();
                    } else if (testKey(key, "l")) {
                        diagram.layout();
                    } else if (testKey(key, "d")) {
                        diagram.copy();
                        diagram.paste();
                    }
                } else if (key === 46 || key === 8) {// del: deletion
                    diagram.remove(diagram.select(), true);
                    return true;
                } else if (key === 27) {// ESC: stop any action
                    this._discardNewConnection();
                    diagram.select(false);
                    return true;
                }
            },
            wheel: function (p, meta) {
                var diagram = this.diagram,
                    delta = meta.delta,
                    z = diagram.zoom(),
                    zoomRate = diagram.options.zoomRate,
                    zoomOptions = {location: p, meta: meta, zoom: z};

                diagram.trigger(ZOOMSTART, zoomOptions);

                if (delta < 0) {
                    z *= zoomRate;
                } else {
                    z /= zoomRate;
                }

                z = Math.round(Math.max(0.7, Math.min(2.0, z)) * 10) / 10;
                zoomOptions.zoom = z;

                diagram.zoom(z, zoomOptions);
                diagram.trigger(ZOOMEND, zoomOptions);

                return true;
            },
            setTool: function (tool, index) {
                tool.toolService = this;
                this.tools[index] = tool;
            },
            triggerClick: function(data) {
                if(this.startPoint.equals(data.point)) {
                    this.diagram.trigger("click", data);
                }

            },
            _discardNewConnection: function () {
                if (this.newConnection) {
                    this.diagram.remove(this.newConnection);
                    this.newConnection = undefined;
                }
            },
            _activateTool: function (p, meta) {
                for (var i = 0; i < this.tools.length; i++) {
                    var tool = this.tools[i];
                    if (tool.tryActivate(p, meta)) {
                        this.activeTool = tool;
                        break; // activating the first available tool in the loop.
                    }
                }
            },
            _updateCursor: function (p) {
                var cursor = this.activeTool ? this.activeTool.getCursor(p) : (this.hoveredAdorner ? this.hoveredAdorner._getCursor(p) : (this.hoveredItem ? this.hoveredItem._getCursor(p) : Cursors.arrow));

                $(this.diagram.canvas.domElement).css({cursor: cursor});
            },
            _connectionManipulation: function (connection, disabledShape, isNew) {
                this.activeConnection = connection;
                this.disabledShape = disabledShape;
                if (isNew) {
                    this.newConnection = this.activeConnection;
                } else {
                    this.newConnection = undefined;
                }
            },
            _updateHoveredItem: function (p) {
                var hit = this._hitTest(p);

                if (hit != this.hoveredItem && (!this.disabledShape || hit != this.disabledShape)) {
                    if (this.hoveredItem) {
                        this.hoveredItem._hover(false);
                    }

                    if (hit && hit.options.enable) {
                        this.hoveredItem = hit; // Shape, connection or connector
                        this.hoveredItem._hover(true);
                    } else {
                        this.hoveredItem = undefined;
                    }
                }
            },
            _removeHover: function () {
                if (this.hoveredItem) {
                    this.hoveredItem._hover(false);
                    this.hoveredItem = undefined;
                }
            },
            _hitTest: function (point) {
                var hit, d = this.diagram, item, i;

                // connectors
                if (this._hoveredConnector) {
                    this._hoveredConnector._hover(false);
                    this._hoveredConnector = undefined;
                }
                if (d._connectorsAdorner._visible) {
                    hit = d._connectorsAdorner._hitTest(point);
                    if (hit) {
                        return hit;
                    }
                }

                hit = this.diagram._resizingAdorner._hitTest(point);
                if (hit) {
                    this.hoveredAdorner = d._resizingAdorner;
                    if (hit.x !== 0 && hit.y !== 0) { // hit testing for resizers or rotator, otherwise if (0,0) than pass through.
                        return;
                    }
                    hit = undefined;
                } else {
                    this.hoveredAdorner = undefined;
                }

                if (!this.activeTool || this.activeTool.type !== "ConnectionTool") {
                    var selectedConnections = []; // only the connections should have higher presence because the connection edit point is on top of connector.
                    // TODO: This should be reworked. The connection adorner should be one for all selected connections and should be hit tested prior the connections and shapes itself.
                    for (i = 0; i < d._selectedItems.length; i++) {
                        item = d._selectedItems[i];
                        if (item instanceof diagram.Connection) {
                            selectedConnections.push(item);
                        }
                    }
                    hit = this._hitTestItems(selectedConnections, point);
                }
                // Shapes | Connectors
                return hit || this._hitTestItems(d.shapes, point) || this._hitTestItems(d.connections, point);
            },
            _hitTestItems: function (array, point) {
                var i, item, hit;
                for (i = array.length - 1; i >= 0; i--) {
                    item = array[i];
                    hit = item._hitTest(point);
                    if (hit) {
                        return hit;
                    }
                }
            }
        });

// Routing =========================================

        /**
         * Base class for connection routers.
         */
        var ConnectionRouterBase = kendo.Class.extend({
            init: function () {
            }
            /*route: function (connection) {
             },
             hitTest: function (p) {

             },
             getBounds: function () {

             }*/
        });

        /**
         * Base class for polyline and cascading routing.
         */
        var LinearConnectionRouter = ConnectionRouterBase.extend({
            init: function (connection) {
                var that = this;
                ConnectionRouterBase.fn.init.call(that);
                this.connection = connection;
            },
            /**
             * Hit testing for polyline paths.
             */
            hitTest: function (p) {
                var rec = this.getBounds().inflate(10);
                if (!rec.contains(p)) {
                    return false;
                }
                return diagram.Geometry.distanceToPolyline(p, this.connection.allPoints()) < HITTESTDISTANCE;
            },

            /**
             * Bounds of a polyline.
             * @returns {kendo.dataviz.diagram.Rect}
             */
            getBounds: function () {
                var points = this.connection.allPoints(),
                    s = points[0],
                    e = points[points.length - 1],
                    right = Math.max(s.x, e.x),
                    left = Math.min(s.x, e.x),
                    top = Math.min(s.y, e.y),
                    bottom = Math.max(s.y, e.y);

                for (var i = 1; i < points.length - 1; ++i) {
                    right = Math.max(right, points[i].x);
                    left = Math.min(left, points[i].x);
                    top = Math.min(top, points[i].y);
                    bottom = Math.max(bottom, points[i].y);
                }

                return new Rect(left, top, right - left, bottom - top);
            }
        });

        /**
         * A simple poly-linear routing which does not alter the intermediate points.
         * Does hold the underlying hit, bounds....logic.
         * @type {*|Object|void|extend|Zepto.extend|b.extend}
         */
        var PolylineRouter = LinearConnectionRouter.extend({
            init: function (connection) {
                var that = this;
                LinearConnectionRouter.fn.init.call(that);
                this.connection = connection;
            },
            route: function () {
                // just keep the points as is
            }
        });

        var CascadingRouter = LinearConnectionRouter.extend({
            init: function (connection) {
                var that = this;
                LinearConnectionRouter.fn.init.call(that);
                this.connection = connection;
            },
            route: function () {
                var link = this.connection;
                var start = this.connection.sourcePoint();
                var end = this.connection.targetPoint(),
                    points = [start, start, end, end],
                    deltaX = end.x - start.x, // can be negative
                    deltaY = end.y - start.y,
                    l = points.length,
                    shiftX,
                    shiftY,
                    sourceConnectorName = null,
                    targetConnectorName = null;

                if (Utils.isDefined(link._resolvedSourceConnector)) {
                    sourceConnectorName = link._resolvedSourceConnector.options.name;
                }
                if (Utils.isDefined(link._resolvedTargetConnector)) {
                    targetConnectorName = link._resolvedTargetConnector.options.name;
                }
                function startHorizontal() {
                    if (sourceConnectorName !== null) {
                        if (sourceConnectorName === RIGHT || sourceConnectorName === LEFT) {
                            return true;
                        }
                        if (sourceConnectorName === TOP || sourceConnectorName === BOTTOM) {
                            return false;
                        }
                    }
                    //fallback for custom connectors
                    return Math.abs(start.x - end.x) > Math.abs(start.y - end.y);
                }

                if (sourceConnectorName !== null && targetConnectorName !== null && Utils.contains(DEFAULTCONNECTORNAMES, sourceConnectorName) && Utils.contains(DEFAULTCONNECTORNAMES, targetConnectorName)) {
                    // custom routing for the default connectors
                    if (sourceConnectorName === TOP || sourceConnectorName == BOTTOM) {
                        if (targetConnectorName == TOP || targetConnectorName == BOTTOM) {
                            this.connection.points([new Point(start.x, start.y + deltaY / 2), new Point(end.x, start.y + deltaY / 2)]);
                        } else {
                            this.connection.points([new Point(start.x, start.y + deltaY)]);
                        }
                    } else { // LEFT or RIGHT
                        if (targetConnectorName == LEFT || targetConnectorName == RIGHT) {
                            this.connection.points([new Point(start.x + deltaX / 2, start.y), new Point(start.x + deltaX / 2, start.y + deltaY)]);
                        } else {
                            this.connection.points([new Point(end.x, start.y)]);
                        }
                    }

                }
                else { // general case for custom and floating connectors
                    this.connection.cascadeStartHorizontal = startHorizontal(this.connection);

                    // note that this is more generic than needed for only two intermediate points.
                    for (var k = 1; k < l - 1; ++k) {
                        if (link.cascadeStartHorizontal) {
                            if (k % 2 !== 0) {
                                shiftX = deltaX / (l / 2);
                                shiftY = 0;
                            }
                            else {
                                shiftX = 0;
                                shiftY = deltaY / ((l - 1) / 2);
                            }
                        }
                        else {
                            if (k % 2 !== 0) {
                                shiftX = 0;
                                shiftY = deltaY / (l / 2);
                            }
                            else {
                                shiftX = deltaX / ((l - 1) / 2);
                                shiftY = 0;
                            }
                        }
                        points[k] = new Point(points[k - 1].x + shiftX, points[k - 1].y + shiftY);
                    }
                    // need to fix the wrong 1.5 factor of the last intermediate point
                    k--;
                    if ((link.cascadeStartHorizontal && (k % 2 !== 0)) || (!link.cascadeStartHorizontal && (k % 2 === 0))) {
                        points[l - 2] = new Point(points[l - 1].x, points[l - 2].y);
                    }
                    else {
                        points[l - 2] = new Point(points[l - 2].x, points[l - 1].y);
                    }

                    this.connection.points([points[1], points[2]]);
                }
            }

        });

// Adorners =========================================

        var AdornerBase = Class.extend({
            init: function (diagram, options) {
                var that = this;
                that.diagram = diagram;
                that.options = deepExtend({}, that.options, options);
                that.visual = new Group();
                that.diagram._adorners.push(that);
            },
            refresh: function () {

            }
        });

        var ConnectionEditAdorner = AdornerBase.extend({
            init: function (connection, options) {
                var that = this, diagram;
                that.connection = connection;
                diagram = that.connection.diagram;
                that._ts = diagram.toolService;
                AdornerBase.fn.init.call(that, diagram, options);
                var sp = that.connection.sourcePoint();
                var tp = that.connection.targetPoint();
                that.spVisual = new Circle(deepExtend(that.options.handles, { center: sp }));
                that.epVisual = new Circle(deepExtend(that.options.handles, { center: tp }));
                that.visual.append(that.spVisual);
                that.visual.append(that.epVisual);
            },
            options: {
                handles: {}
            },
            _getCursor: function () {
                return Cursors.move;
            },
            start: function (p) {
                this.handle = this._hitTest(p);
                this.startPoint = p;
                this._initialSource = this.connection.source();
                this._initialTarget = this.connection.target();
                switch (this.handle) {
                    case -1:
                        if (this.connection.targetConnector) {
                            this._ts._connectionManipulation(this.connection, this.connection.targetConnector.shape);
                        }
                        break;
                    case 1:
                        if (this.connection.sourceConnector) {
                            this._ts._connectionManipulation(this.connection, this.connection.sourceConnector.shape);
                        }
                        break;
                }
            },
            move: function (handle, p) {
                switch (handle) {
                    case -1:
                        this.connection.source(p);
                        break;
                    case 1:
                        this.connection.target(p);
                        break;
                    default:
                        var delta = p.minus(this.startPoint);
                        this.startPoint = p;
                        if (!this.connection.sourceConnector) {
                            this.connection.source(this.connection.sourcePoint().plus(delta));
                        }
                        if (!this.connection.targetConnector) {
                            this.connection.target(this.connection.targetPoint().plus(delta));
                        }
                        break;
                }
                this.refresh();
                return true;
            },
            stop: function (p) {
                var ts = this.diagram.toolService, item = ts.hoveredItem, target;
                if (ts._hoveredConnector) {
                    target = ts._hoveredConnector._c;
                } else if (item && !item.line) {
                    target = item;
                }
                else {
                    target = p;
                }
                if (this.handle !== undefined) {
                    switch (this.handle) {
                        case -1:
                            this.connection.source(target);
                            break;
                        case 1:
                            this.connection.target(target);
                            break;
                    }
                }

                this.handle = undefined;
                this._ts._connectionManipulation();
                return new ConnectionEditUndoUnit(this.connection, this._initialSource, this._initialTarget);
            },
            _hitTest: function (p) {
                var sp = this.connection.sourcePoint(),
                    tp = this.connection.targetPoint(),
                    rx = this.options.handles.width / 2,
                    ry = this.options.handles.height / 2,
                    sb = new Rect(sp.x, sp.y).inflate(rx, ry),
                    tb = new Rect(tp.x, tp.y).inflate(rx, ry);
                return sb.contains(p) ? -1 : (tb.contains(p) ? 1 : 0);
            },
            refresh: function () {
                this.spVisual.redraw({ center: this.diagram.modelToLayer(this.connection.sourcePoint()) });
                this.epVisual.redraw({ center: this.diagram.modelToLayer(this.connection.targetPoint()) });
            }
        });

        var ConnectorsAdorner = AdornerBase.extend({
            init: function (diagram, options) {
                var that = this;
                AdornerBase.fn.init.call(that, diagram, options);
                that._refreshHandler = function (e) {
                    if (e.item == that.shape) {
                        that.refresh();
                    }
                };
            },
            show: function (shape) {
                var that = this, len, i, ctr;
                that._visible = true;
                that.shape = shape;
                that.diagram.bind(ITEMBOUNDSCHANGE, that._refreshHandler);
                len = shape.connectors.length;
                that.connectors = [];
                that.visual.clear();
                for (i = 0; i < len; i++) {
                    ctr = new ConnectorVisual(shape.connectors[i]);
                    that.connectors.push(ctr);
                    that.visual.append(ctr.visual);
                }
                that.visual.visible(true);
                that.refresh();
            },
            destroy: function () {
                var that = this;
                that.diagram.unbind(ITEMBOUNDSCHANGE, that._refreshHandler);
                that.shape = undefined;
                that._visible = undefined;
                that.visual.visible(false);
            },
            _hitTest: function (p) {
                var ctr, i;
                for (i = 0; i < this.connectors.length; i++) {
                    ctr = this.connectors[i];
                    if (ctr._hitTest(p)) {
                        ctr._hover(true);
                        this.diagram.toolService._hoveredConnector = ctr;
                        break;
                    }
                }
            },
            refresh: function () {
                if (this.shape) {
                    var bounds = this.shape.bounds();
                        bounds = this.diagram.modelToLayer(bounds);
                    this.visual.position(bounds.topLeft());
                    $.each(this.connectors, function () {
                        this.refresh();
                    });
                }
            }
        });

        function hitToOppositeSide(hit, bounds) {
            var result;

            if (hit.x == -1 && hit.y == -1) {
                result = bounds.bottomRight();
            } else if (hit.x == 1 && hit.y == 1) {
                result = bounds.topLeft();
            } else if (hit.x == -1 && hit.y == 1) {
                result = bounds.topRight();
            } else if (hit.x == 1 && hit.y == -1) {
                result = bounds.bottomLeft();
            } else if (hit.x === 0 && hit.y == -1) {
                result = bounds.bottom();
            } else if (hit.x === 0 && hit.y == 1) {
                result = bounds.top();
            } else if (hit.x == 1 && hit.y === 0) {
                result = bounds.left();
            } else if (hit.x == -1 && hit.y === 0) {
                result = bounds.right();
            }

            return result;
        }

        var ResizingAdorner = AdornerBase.extend({
            init: function (diagram, options) {
                var that = this;
                AdornerBase.fn.init.call(that, diagram, options);
                that._manipulating = false;
                that.map = [];
                that.shapes = [];
                that.rect = new Rectangle(options.editable.select);
                that.visual.append(that.rect);
                that._createHandles();
                that.text = new TextBlock();
                that.visual.append(that.text);
                that._createThumb();
                that.redraw();
                that.diagram.bind("select", function (e) {
                    that._initialize(e.selected);
                });

                that._refreshHandler = function () {
                    if (!that._internalChange) {
                        that.refreshBounds();
                        that.refresh();
                    }
                };

                that._rotatedHandler = function () {
                    if (that.shapes.length == 1) {
                        that._angle = that.shapes[0].rotate().angle;
                    }
                    that._refreshHandler();
                };

                that.diagram.bind(ITEMBOUNDSCHANGE, that._refreshHandler).bind(ITEMROTATE, that._rotatedHandler);
                that.refreshBounds();
                that.refresh();
            },
            options: {
                editable: {
                    rotate: {
                        thumb: {
                            data: "M7.115,16C3.186,16,0,12.814,0,8.885C0,5.3,2.65,2.336,6.099,1.843V0l4.85,2.801l-4.85,2.8V3.758 c-2.399,0.473-4.21,2.588-4.21,5.126c0,2.886,2.34,5.226,5.226,5.226s5.226-2.34,5.226-5.226c0-1.351-0.513-2.582-1.354-3.51 l1.664-0.961c0.988,1.222,1.581,2.777,1.581,4.472C14.23,12.814,11.045,16,7.115,16L7.115,16z",
                            y: -30
                        }
                    }
                },
                offset: 10
            },

            _createThumb: function() {
                var that = this,
                    editable = that.options.editable,
                    rotate = editable.rotate;

                if (editable && rotate) {
                    that.rotationThumb = new Path(rotate.thumb);
                    that.visual.append(that.rotationThumb);
                }
            },

            _createHandles: function() {
                var editable = this.options.editable,
                    handles, item, i, y, x;

                if (editable && editable.resize) {
                    handles = editable.resize.handles;
                    for (x = -1; x <= 1; x++) {
                        for (y = -1; y <= 1; y++) {
                            if ((x !== 0) || (y !== 0)) { // (0, 0) element, (-1, -1) top-left, (+1, +1) bottom-right
                                item = new Rectangle(handles);
                                item.domElement._hover = $.proxy(this._hover, this);
                                this.map.push({ x: x, y: y, visual: item });
                                this.visual.append(item);
                            }
                        }
                    }
                }
            },

            bounds: function (value) {
                if (value) {
                    this._innerBounds = value.clone();
                    this._bounds = this.diagram.modelToLayer(value).inflate(this.options.offset, this.options.offset);
                } else {
                    return this._bounds;
                }
            },
            _hitTest: function (p) {
                var tp = this.diagram.modelToLayer(p),
                    editable = this.options.editable,
                    i, hit, handleBounds, handlesCount = this.map.length, handle;

                if (this._angle) {
                    tp = tp.clone().rotate(this._bounds.center(), this._angle);
                }

                if (editable && editable.rotate && this._rotationThumbBounds) {
                    if (this._rotationThumbBounds.contains(tp)) {
                        return new Point(-1, -2);
                    }
                }

                if (editable && editable.resize) {
                    for (i = 0; i < handlesCount; i++) {
                        handle = this.map[i];
                        hit = new Point(handle.x, handle.y);
                        handleBounds = this._getHandleBounds(hit); //local coordinates
                        handleBounds.offset(this._bounds.x, this._bounds.y);
                        if (handleBounds.contains(tp)) {
                            return hit;
                        }
                    }
                }

                if (this._bounds.contains(tp)) {
                    return new Point(0, 0);
                }
            },
            _getHandleBounds: function (p) {
                var editable = this.options.editable;
                if (editable && editable.resize) {
                    var handles = editable.resize.handles || {},
                        w = handles.width,
                        h = handles.height,
                        r = new Rect(0, 0, w, h);

                    if (p.x < 0) {
                        r.x = - w / 2;
                    } else if (p.x === 0) {
                        r.x = Math.floor(this._bounds.width / 2) - w / 2;
                    } else if (p.x > 0) {
                        r.x = this._bounds.width + 1.0 - w / 2;
                    } if (p.y < 0) {
                        r.y = - h / 2;
                    } else if (p.y === 0) {
                        r.y = Math.floor(this._bounds.height / 2) - h / 2;
                    } else if (p.y > 0) {
                        r.y = this._bounds.height + 1.0 - h / 2;
                    }

                    return r;
                }
            },
            _getCursor: function (point) {
                var hit = this._hitTest(point);
                if (hit && (hit.x >= -1) && (hit.x <= 1) && (hit.y >= -1) && (hit.y <= 1) && this.options.editable && this.options.editable.resize) {
                    var angle = this._angle;
                    if (angle) {
                        angle = 360 - angle;
                        hit.rotate(new Point(0, 0), angle);
                        hit = new Point(Math.round(hit.x), Math.round(hit.y));
                    }

                    if (hit.x == -1 && hit.y == -1) {
                        return "nw-resize";
                    }
                    if (hit.x == 1 && hit.y == 1) {
                        return "se-resize";
                    }
                    if (hit.x == -1 && hit.y == 1) {
                        return "sw-resize";
                    }
                    if (hit.x == 1 && hit.y == -1) {
                        return "ne-resize";
                    }
                    if (hit.x === 0 && hit.y == -1) {
                        return "n-resize";
                    }
                    if (hit.x === 0 && hit.y == 1) {
                        return "s-resize";
                    }
                    if (hit.x == 1 && hit.y === 0) {
                        return "e-resize";
                    }
                    if (hit.x == -1 && hit.y === 0) {
                        return "w-resize";
                    }
                }
                return this._manipulating ? Cursors.move : Cursors.select;
            },
            _initialize: function() {
                var that = this, i, item,
                    items = that.diagram.select();

                that.shapes = [];
                for (i = 0; i < items.length; i++) {
                    item = items[i];
                    if (item instanceof diagram.Shape) {
                        that.shapes.push(item);
                        item._rotationOffset = new Point();
                    }
                }

                that._angle = that.shapes.length == 1 ? that.shapes[0].rotate().angle : 0;
                that._startAngle = that._angle;
                that._rotates();
                that._positions();
                that.refreshBounds();
                that.refresh();
                that.redraw();
            },
            _rotates: function () {
                var that = this, i, shape;
                that.initialRotates = [];
                for (i = 0; i < that.shapes.length; i++) {
                    shape = that.shapes[i];
                    that.initialRotates.push(shape.rotate().angle);
                }
            },
            _positions: function () {
                var that = this, i, shape;
                that.initialStates = [];
                for (i = 0; i < that.shapes.length; i++) {
                    shape = that.shapes[i];
                    that.initialStates.push(shape.bounds());
                }
            },
            _hover: function(value, element) {
                var editable = this.options.editable;
                if (editable && editable.resize) {
                    var handleOptions = editable.resize.handles,
                        hover = handleOptions.hover,
                        stroke = handleOptions.stroke,
                        background = handleOptions.background;

                    if (value && Utils.isDefined(hover.stroke)) {
                        stroke = deepExtend({}, stroke, hover.stroke);
                    }

                    if (value && Utils.isDefined(hover.background)) {
                        background = hover.background;
                    }

                    element.redraw({
                        stroke: stroke,
                        background: background
                    });
                }
            },
            start: function (p) {
                this._sp = p;
                this._cp = p;
                this._lp = p;
                this._manipulating = true;
                this._internalChange = true;
                this.shapeStates = [];
                for (var i = 0; i < this.shapes.length; i++) {
                    var shape = this.shapes[i];
                    this.shapeStates.push(shape.bounds());
                }
            },
            redraw: function () {
                var that = this, i, handle,
                    editable = that.options.editable,
                    resize = editable.resize,
                    rotate = editable.rotate,
                    display = editable && resize ? "inline" : "none",
                    rotationDisplay = editable && rotate ? "inline" : "none";

                for (i = 0; i < this.map.length; i++) {
                    handle = this.map[i];
                    $(handle.visual.domElement).css("display", display);
                }
                if (that.rotationThumb) {
                    $(that.rotationThumb.domElement).css("display", rotationDisplay);
                }
            },
            move: function (handle, p) {
                var delta, dragging,
                    dtl = new Point(),
                    dbr = new Point(),
                    bounds, center, shape,
                    i, angle, newBounds,
                    changed = 0, staticPoint,
                    scaleX, scaleY;

                if (handle.y === -2 && handle.x === -1) {
                    center = this._innerBounds.center();
                    this._angle = this._truncateAngle(Utils.findAngle(center, p));
                    for (i = 0; i < this.shapes.length; i++) {
                        shape = this.shapes[i];
                        angle = (this._angle + this.initialRotates[i] - this._startAngle) % 360;
                        shape.rotate(angle, center);
                        if (shape.hasOwnProperty("layout")) {
                            shape.layout(shape);
                        }
                        this._rotating = true;
                    }
                    this.refresh();
                } else {
                    if (this.diagram.options.snap.enabled === true) {
                        var thr = this._truncateDistance(p.minus(this._lp));
                        // threshold
                        if (thr.x === 0 && thr.y === 0) {
                            this._cp = p;
                            return;
                        }
                        delta = thr;
                        this._lp = new Point(this._lp.x + thr.x, this._lp.y + thr.y);
                    } else {
                        delta = p.minus(this._cp);
                    }

                    if (handle.x === 0 && handle.y === 0) {
                        dbr = dtl = delta; // dragging
                        dragging = true;
                    } else {
                        if (this._angle) { // adjust the delta so that resizers resize in the correct direction after rotation.
                            delta.rotate(new Point(0, 0), this._angle);
                        }
                        if (handle.x == -1) {
                            dtl.x = delta.x;
                        } else if (handle.x == 1) {
                            dbr.x = delta.x;
                        }
                        if (handle.y == -1) {
                            dtl.y = delta.y;
                        } else if (handle.y == 1) {
                            dbr.y = delta.y;
                        }
                    }

                    if (!dragging) {
                        staticPoint = hitToOppositeSide(handle, this._innerBounds);
                        scaleX = (this._innerBounds.width + delta.x * handle.x) / this._innerBounds.width;
                        scaleY = (this._innerBounds.height + delta.y * handle.y) / this._innerBounds.height;
                    }

                    for (i = 0; i < this.shapes.length; i++) {
                        shape = this.shapes[i];
                        bounds = shape.bounds();
                        if (dragging) {
                            newBounds = this._displaceBounds(bounds, dtl, dbr, dragging);
                        } else {
                            newBounds = bounds.clone();
                            newBounds.scale(scaleX, scaleY, staticPoint, this._innerBounds.center(), shape.rotate().angle);
                            var newCenter = newBounds.center(); // fixes the new rotation center.
                            newCenter.rotate(bounds.center(), -this._angle);
                            newBounds = new Rect(newCenter.x - newBounds.width / 2, newCenter.y - newBounds.height / 2, newBounds.width, newBounds.height);
                        }
                        if (newBounds.width >= shape.options.minWidth && newBounds.height >= shape.options.minHeight) { // if we up-size very small shape
                            var oldBounds = bounds;
                            shape.bounds(newBounds);
                            if (shape.hasOwnProperty("layout")) {
                                shape.layout(shape, oldBounds, newBounds);
                            }
                            shape.rotate(shape.rotate().angle); // forces the rotation to update it's rotation center
                            changed += 1;
                        }
                    }

                    if (changed == i) {
                        newBounds = this._displaceBounds(this._innerBounds, dtl, dbr, dragging);
                        this.bounds(newBounds);
                        this.refresh();
                    }

                    this._positions();
                }

                this._cp = p;
            },
            _truncatePositionToGuides: function (bounds) {
                if (this.diagram.ruler) {
                    return this.diagram.ruler.truncatePositionToGuides(bounds);
                }
                return bounds;
            },
            _truncateSizeToGuides: function (bounds) {
                if (this.diagram.ruler) {
                    return this.diagram.ruler.truncateSizeToGuides(bounds);
                }
                return bounds;
            },
            _truncateAngle: function (a) {
                var snapAngle = Math.max(this.diagram.options.snap.angle, 5);
                return this.diagram.options.snap.enabled === true ? Math.floor((a % 360) / snapAngle) * snapAngle : (a % 360);
            },
            _truncateDistance: function (d) {
                if (d instanceof diagram.Point) {
                    return new diagram.Point(this._truncateDistance(d.x), this._truncateDistance(d.y));
                } else {
                    var snapSize = Math.max(this.diagram.options.snap.size, 5);
                    return this.diagram.options.snap.enabled === true ? Math.floor(d / snapSize) * snapSize : d;
                }
            },
            _displaceBounds: function (bounds, dtl, dbr, dragging) {
                var tl = bounds.topLeft().plus(dtl),
                    br = bounds.bottomRight().plus(dbr),
                    newBounds = Rect.fromPoints(tl, br),
                    newCenter;
                if (!dragging) {
                    newCenter = newBounds.center();
                    newCenter.rotate(bounds.center(), -this._angle);
                    newBounds = new Rect(newCenter.x - newBounds.width / 2, newCenter.y - newBounds.height / 2, newBounds.width, newBounds.height);
                }
                return newBounds;
            },
            stop: function () {
                var unit;
                if (this._cp != this._sp) {
                    if (this._rotating) {
                        unit = new RotateUnit(this, this.shapes, this.initialRotates);
                        this._rotating = false;
                    } else {
                        if (this.diagram.ruler) {
                            for (var i = 0; i < this.shapes.length; i++) {
                                var shape = this.shapes[i];
                                var bounds = shape.bounds();
                                bounds = this._truncateSizeToGuides(this._truncatePositionToGuides(bounds));
                                shape.bounds(bounds);
                                this.refreshBounds();
                                this.refresh();
                            }
                        }
                        unit = new TransformUnit(this.shapes, this.shapeStates, this);
                    }
                }

                this._manipulating = undefined;
                this._internalChange = undefined;
                this._rotating = undefined;
                return unit;
            },
            refreshBounds: function () {
                var bounds = this.shapes.length == 1 ?
                    this.shapes[0].bounds().clone() :
                    this.diagram.boundingBox(this.shapes, true);

                this.bounds(bounds);
            },
            refresh: function () {
                var that = this, b, bounds;
                if (this.shapes.length > 0) {
                    bounds = this.bounds();
                    this.visual.visible(true);
                    this.visual.position(bounds.topLeft());
                    $.each(this.map, function () {
                        b = that._getHandleBounds(new Point(this.x, this.y));
                        this.visual.position(b.topLeft());
                    });
                    this.visual.position(bounds.topLeft());

                    var center = new Point(bounds.width / 2, bounds.height / 2);
                    this.visual.rotate(this._angle, center);
                    this.rect.redraw({ width: bounds.width, height: bounds.height });
                    if (this.rotationThumb) {
                        var thumb = this.options.editable.rotate.thumb;
                        this._rotationThumbBounds = new Rect(bounds.center().x, bounds.y + thumb.y, 0, 0).inflate(thumb.width);
                        this.rotationThumb.redraw({ x: bounds.width / 2 - thumb.width / 2 });
                    }
                } else {
                    this.visual.visible(false);
                }
            }
        });

        var Selector = Class.extend({
            init: function (diagram) {
                this.visual = new Rectangle(this.options);
                this.diagram = diagram;
            },
            options: {
                stroke: {
                    color: "#778899",
                    width: 1,
                    dashType: "dash"
                },
                background: "none"
            },
            start: function (p) {
                this._sp = this._ep = p;
                this.refresh();
                this.diagram._adorn(this, true);
            },
            end: function () {
                this._sp = this._ep = undefined;
                this.diagram._adorn(this, false);
            },
            bounds: function (value) {
                if (value) {
                    this._bounds = value;
                }
                return this._bounds;
            },
            move: function (p) {
                this._ep = p;
                this.refresh();
            },
            refresh: function () {
                if (this._sp) {
                    var visualBounds = Rect.fromPoints(this.diagram.modelToLayer(this._sp), this.diagram.modelToLayer(this._ep));
                    this.bounds(Rect.fromPoints(this._sp, this._ep));
                    this.visual.position(visualBounds.topLeft());
                    this.visual.redraw({ height: visualBounds.height + 1, width: visualBounds.width + 1 });
                }
            }
        });

        var ConnectorVisual = Class.extend({
            init: function (connector) {
                this.options = deepExtend({}, connector.options);
                this._c = connector;
                this.visual = new Circle(this.options);
                this.refresh();
            },
            _hover: function (value) {
                var options = this.options,
                    hover = options.hover,
                    stroke = options.stroke,
                    background = options.background;

                if (value && Utils.isDefined(hover.stroke)) {
                    stroke = deepExtend({}, stroke, hover.stroke);
                }

                if (value && Utils.isDefined(hover.background)) {
                    background = hover.background;
                }

                this.visual.redraw({
                    stroke: stroke,
                    background: background
                });
            },
            refresh: function () {
                var p = this._c.shape.diagram.modelToView(this._c.position()),
                    relative = p.minus(this._c.shape.bounds("transformed").topLeft()),
                    value = new Rect(p.x, p.y, 0, 0);
                value.inflate(this.options.width / 2, this.options.height / 2);
                this._visualBounds = value;
                this.visual.redraw({ center: new Point(relative.x, relative.y) });
            },
            _hitTest: function (p) {
                var tp = this._c.shape.diagram.modelToView(p);
                return this._visualBounds.contains(tp);
            }
        });

        deepExtend(diagram, {
            CompositeUnit: CompositeUnit,
            TransformUnit: TransformUnit,
            PanUndoUnit: PanUndoUnit,
            AddShapeUnit: AddShapeUnit,
            AddConnectionUnit: AddConnectionUnit,
            DeleteShapeUnit: DeleteShapeUnit,
            DeleteConnectionUnit: DeleteConnectionUnit,
            ContentChangedUndoUnit: ContentChangedUndoUnit,
            ConnectionEditAdorner: ConnectionEditAdorner,
            UndoRedoService: UndoRedoService,
            ResizingAdorner: ResizingAdorner,
            Selector: Selector,
            ToolService: ToolService,
            ConnectorsAdorner: ConnectorsAdorner,
            LayoutUndoUnit: LayoutUndoUnit,
            ConnectionEditUnit: ConnectionEditUnit,
            ToFrontUnit: ToFrontUnit,
            ToBackUnit: ToBackUnit,
            ConnectionRouterBase: ConnectionRouterBase,
            PolylineRouter: PolylineRouter,
            CascadingRouter: CascadingRouter,
            SelectionTool: SelectionTool
        });
})(window.kendo.jQuery);

(function ($, undefined) {
    var kendo = window.kendo,
        diagram = kendo.dataviz.diagram,
        Graph = diagram.Graph,
        Node = diagram.Node,
        Link = diagram.Link,
        deepExtend = kendo.deepExtend,
        Size = diagram.Size,
        Rect = diagram.Rect,
        Dictionary = diagram.Dictionary,
        Set = diagram.Set,
        HyperTree = diagram.Graph,
        Utils = diagram.Utils,
        Point = diagram.Point,
        EPSILON = 1e-06,
        DEG_TO_RAD = Math.PI / 180,
        contains = Utils.contains,
        grep = $.grep;

    /**
     * Base class for layout algorithms.
     * @type {*}
     */
    var LayoutBase = kendo.Class.extend({
        defaultOptions: {
            type: "Tree",
            subtype: "Down",
            roots: null,
            animate: false,
            //-------------------------------------------------------------------
            /**
             * Force-directed option: whether the motion of the nodes should be limited by the boundaries of the diagram surface.
             */
            limitToView: false,
            /**
             * Force-directed option: the amount of friction applied to the motion of the nodes.
             */
            friction: 0.9,
            /**
             * Force-directed option: the optimal distance between nodes (minimum energy).
             */
            nodeDistance: 50,
            /**
             * Force-directed option: the number of time things are being calculated.
             */
            iterations: 300,
            //-------------------------------------------------------------------
            /**
             * Tree option: the separation in one direction (depends on the subtype what direction this is).
             */
            horizontalSeparation: 90,
            /**
             * Tree option: the separation in the complementary direction (depends on the subtype what direction this is).
             */
            verticalSeparation: 50,

            //-------------------------------------------------------------------
            /**
             * Tip-over tree option: children-to-parent vertical distance.
             */
            underneathVerticalTopOffset: 15,
            /**
             * Tip-over tree option: children-to-parent horizontal distance.
             */
            underneathHorizontalOffset: 15,
            /**
             * Tip-over tree option: leaf-to-next-branch vertical distance.
             */
            underneathVerticalSeparation: 15,
            //-------------------------------------------------------------------
            /**
             * Settings object to organize the different components of the diagram in a grid layout structure
             */
            grid: {
                /**
                 * The width of the grid in which components are arranged. Beyond this width a component will be on the next row.
                 */
                width: 1500,
                /**
                 * The left offset of the grid.
                 */
                offsetX: 50,
                /**
                 * The top offset of the grid.
                 */
                offsetY: 50,
                /**
                 * The horizontal padding within a cell of the grid where a single component resides.
                 */
                componentSpacingX: 20,
                /**
                 * The vertical padding within a cell of the grid where a single component resides.
                 */
                componentSpacingY: 20
            },

            //-------------------------------------------------------------------
            /**
             * Layered option: the separation height/width between the layers.
             */
            layerSeparation: 50,
            /**
             * Layered option: how many rounds of shifting and fine-tuning.
             */
            layeredIterations: 2,
            /**
             * Tree-radial option: the angle at which the layout starts.
             */
            startRadialAngle: 0,
            /**
             * Tree-radial option: the angle at which the layout starts.
             */
            endRadialAngle: 360,
            /**
             * Tree-radial option: the separation between levels.
             */
            radialSeparation: 150,
            /**
             * Tree-radial option: the separation between the root and the first level.
             */
            radialFirstLevelSeparation: 200,
            /**
             * Tree-radial option: whether a virtual roots bing the components in one radial layout.
             */
            keepComponentsInOneRadialLayout: false,
            //-------------------------------------------------------------------

            // TODO: ensure to change this to false when containers are around
            ignoreContainers: true,
            layoutContainerChildren: false,
            ignoreInvisible: true,
            animateTransitions: false
        },
        init: function () {
        },

        /**
         * Organizes the components in a grid.
         * Returns the final set of nodes (not the Graph).
         * @param components
         */
        gridLayoutComponents: function (components) {
            if (!components) {
                throw "No components supplied.";
            }

            // calculate and cache the bounds of the components
            Utils.forEach(components, function (c) {
                c.calcBounds();
            });

            // order by decreasing width
            components.sort(function (a, b) {
                return b.bounds.width - a.bounds.width;
            });

            var maxWidth = this.options.grid.width,
                offsetX = this.options.grid.componentSpacingX,
                offsetY = this.options.grid.componentSpacingY,
                height = 0,
                startX = this.options.grid.offsetX,
                startY = this.options.grid.offsetY,
                x = startX,
                y = startY,
                i,
                resultLinkSet = [],
                resultNodeSet = [];

            while (components.length > 0) {
                if (x >= maxWidth) {
                    // start a new row
                    x = startX;
                    y += height + offsetY;
                    // reset the row height
                    height = 0;
                }
                var component = components.pop();
                this.moveToOffset(component, new Point(x, y));
                for (i = 0; i < component.nodes.length; i++) {
                    resultNodeSet.push(component.nodes[i]); // to be returned in the end
                }
                for (i = 0; i < component.links.length; i++) {
                    resultLinkSet.push(component.links[i]);
                }
                var boundingRect = component.bounds;
                var currentHeight = boundingRect.height;
                if (currentHeight <= 0 || isNaN(currentHeight)) {
                    currentHeight = 0;
                }
                var currentWidth = boundingRect.width;
                if (currentWidth <= 0 || isNaN(currentWidth)) {
                    currentWidth = 0;
                }

                if (currentHeight >= height) {
                    height = currentHeight;
                }
                x += currentWidth + offsetX;
            }

            return {
                nodes: resultNodeSet,
                links: resultLinkSet
            };
        },

        moveToOffset: function (component, p) {
            var i, j,
                bounds = component.bounds,
                deltax = p.x - bounds.x,
                deltay = p.y - bounds.y;

            for (i = 0; i < component.nodes.length; i++) {
                var node = component.nodes[i];
                var nodeBounds = node.bounds();
                if (nodeBounds.width === 0 && nodeBounds.height === 0 && nodeBounds.x === 0 && nodeBounds.y === 0) {
                    nodeBounds = new Rect(0, 0, 0, 0);
                }
                nodeBounds.x += deltax;
                nodeBounds.y += deltay;
                node.bounds(nodeBounds);
            }
            for (i = 0; i < component.links.length; i++) {
                var link = component.links[i];
                if (link.points) {
                    var newpoints = [];
                    var points = link.points;
                    for (j = 0; j < points.length; j++) {
                        var pt = points[j];
                        pt.x += deltax;
                        pt.y += deltay;
                        newpoints.push(pt);
                    }
                    link.points = newpoints;
                }
            }
            this.currentHorizontalOffset += bounds.width + this.options.grid.offsetX;
            return new Point(deltax, deltay);
        },

        transferOptions: function (options) {

            // Size options lead to stackoverflow and need special handling

            this.options = kendo.deepExtend({}, this.defaultOptions);
            if (Utils.isUndefined(options)) {
                return;
            }

            this.options = kendo.deepExtend(this.options, options || {});
        }
    });

    /**
     * The data bucket a hypertree holds in its nodes.     *
     * @type {*}
     */
    /* var ContainerGraph = kendo.Class.extend({
     init: function (diagram) {
     this.diagram = diagram;
     this.graph = new Graph(diagram);
     this.container = null;
     this.containerNode = null;
     }

     });*/

    /**
     * Adapter between the diagram control and the graph representation. It converts shape and connections to nodes and edges taking into the containers and their collapsef state,
     * the visibility of items and more. If the layoutContainerChildren is true a hypertree is constructed which holds the hierarchy of containers and many conditions are analyzed
     * to investigate how the effective graph structure looks like and how the layout has to be performed.
     * @type {*}
     */
    var DiagramToHyperTreeAdapter = kendo.Class.extend({
        init: function (diagram) {

            /**
             * The mapping to/from the original nodes.
             * @type {Dictionary}
             */
            this.nodeMap = new Dictionary();

            /**
             * Gets the mapping of a shape to a container in case the shape sits in a collapsed container.
             * @type {Dictionary}
             */
            this.shapeMap = new Dictionary();

            /**
             * The nodes being mapped.
             * @type {Dictionary}
             */
            this.nodes = [];

            /**
             * The connections being mapped.
             * @type {Dictionary}
             */
            this.edges = [];

            // the mapping from an edge to all the connections it represents, this can be both because of multiple connections between
            // two shapes or because a container holds multiple connections to another shape or container.
            this.edgeMap = new Dictionary();

            /**
             * The resulting set of Nodes when the analysis has finished.
             * @type {Array}
             */
            this.finalNodes = [];

            /**
             * The resulting set of Links when the analysis has finished.
             * @type {Array}
             */
            this.finalLinks = [];

            /**
             * The items being omitted because of multigraph edges.
             * @type {Array}
             */
            this.ignoredConnections = [];

            /**
             * The items being omitted because of containers, visibility and other factors.
             * @type {Array}
             */
            this.ignoredShapes = [];

            /**
             * The map from a node to the partition/hypernode in which it sits. This hyperMap is null if 'options.layoutContainerChildren' is false.
             * @type {Dictionary}
             */
            this.hyperMap = new Dictionary();

            /**
             * The hypertree contains the hierarchy defined by the containers.
             * It's in essence a Graph of Graphs with a tree structure defined by the hierarchy of containers.
             * @type {HyperTree}
             */
            this.hyperTree = new Graph();

            /**
             * The resulting graph after conversion. Note that this does not supply the information contained in the
             * ignored connection and shape collections.
             * @type {null}
             */
            this.finalGraph = null;

            this.diagram = diagram;
        },

        /**
         * The hyperTree is used when the 'options.layoutContainerChildren' is true. It contains the hierarchy of containers whereby each node is a ContainerGraph.
         * This type of node has a Container reference to the container which holds the Graph items. There are three possible situations during the conversion process:
         *  - Ignore the containers: the container are non-existent and only normal shapes are mapped. If a shape has a connection to a container it will be ignored as well
         *    since there is no node mapped for the container.
         *  - Do not ignore the containers and leave the content of the containers untouched: the top-level elements are being mapped and the children within a container are not altered.
         *  - Do not ignore the containers and organize the content of the containers as well: the hypertree is constructed and there is a partitioning of all nodes and connections into the hypertree.
         *    The only reason a connection or node is not being mapped might be due to the visibility, which includes the visibility change through a collapsed parent container.
         * @param options
         */
        convert: function (options) {

            if (Utils.isUndefined(this.diagram)) {
                throw "No diagram to convert.";
            }

            this.options = kendo.deepExtend({
                    ignoreInvisible: true,
                    ignoreContainers: true,
                    layoutContainerChildren: false
                },
                options || {}
            );

            this.clear();
            // create the nodes which participate effectively in the graph analysis
            this._renormalizeShapes();

            // recreate the incoming and outgoing collections of each and every node
            this._renormalizeConnections();

            // export the resulting graph
            this.finalNodes = new Dictionary(this.nodes);
            this.finalLinks = new Dictionary(this.edges);

            this.finalGraph = new Graph();
            this.finalNodes.forEach(function (n) {
                this.finalGraph.addNode(n);
            }, this);
            this.finalLinks.forEach(function (l) {
                this.finalGraph.addExistingLink(l);
            }, this);
            return this.finalGraph;
        },

        /**
         * Maps the specified connection to an edge of the graph deduced from the given diagram.
         * @param connection
         * @returns {*}
         */
        mapConnection: function (connection) {
            return this.edgeMap.first(function (edge) {
                return contains(this.edgeMap.get(edge), connection);
            });
        },

        /**
         * Maps the specified shape to a node of the graph deduced from the given diagram.
         * @param shape
         * @returns {*}
         */
        mapShape: function (shape) {
            var keys = this.nodeMap.keys();
            for (var i = 0, len = keys.length; i < len; i++) {
                var key = keys[i];
                if (contains(this.nodeMap.get(key), shape)) {
                    return key;
                }
            }
        },

        /**
         * Gets the edge, if any, between the given nodes.
         * @param a
         * @param b
         */
        getEdge: function (a, b) {
            return Utils.first(a.links, function (link) {
                return link.getComplement(a) === b;
            });
        },

        /**
         * Clears all the collections used by the conversion process.
         */
        clear: function () {
            this.finalGraph = null;
            this.hyperTree = (!this.options.ignoreContainers && this.options.layoutContainerChildren) ? new HyperTree() : null;
            this.hyperMap = (!this.options.ignoreContainers && this.options.layoutContainerChildren) ? new Dictionary() : null;
            this.nodeMap = new Dictionary();
            this.shapeMap = new Dictionary();
            this.nodes = [];
            this.edges = [];
            this.edgeMap = new Dictionary();
            this.ignoredConnections = [];
            this.ignoredShapes = [];
            this.finalNodes = [];
            this.finalLinks = [];
        },

        /**
         * The path from a given ContainerGraph to the root (container).
         * @param containerGraph
         * @returns {Array}
         */
        listToRoot: function (containerGraph) {
            var list = [];
            var s = containerGraph.container;
            if (!s) {
                return list;
            }
            list.push(s);
            while (s.parentContainer) {
                s = s.parentContainer;
                list.push(s);
            }
            list.reverse();
            return list;
        },

        firstNonIgnorableContainer: function (shape) {

            if (shape.isContainer && !this._isIgnorableItem(shape)) {
                return shape;
            }
            return !shape.parentContainer ? null : this.firstNonIgnorableContainer(shape.parentContainer);
        },
        isContainerConnection: function (a, b) {
            if (a.isContainer && this.isDescendantOf(a, b)) {
                return true;
            }
            return b.isContainer && this.isDescendantOf(b, a);
        },

        /**
         * Returns true if the given shape is a direct child or a nested container child of the given container.
         * If the given container and shape are the same this will return false since a shape cannot be its own child.
         * @param scope
         * @param a
         * @returns {boolean}
         */
        isDescendantOf: function (scope, a) {
            if (!scope.isContainer) {
                throw "Expecting a container.";
            }
            if (scope === a) {
                return false;
            }
            if (contains(scope.children, a)) {
                return true;
            }
            var containers = [];
            for (var i = 0, len = scope.children.length; i < len; i++) {
                var c = scope.children[i];
                if (c.isContainer && this.isDescendantOf(c, a)) {
                    containers.push(c);
                }
            }

            return containers.length > 0;
        },
        isIgnorableItem: function (shape) {
            if (this.options.ignoreInvisible) {
                if (shape.isCollapsed && this._isVisible(shape)) {
                    return false;
                }
                if (!shape.isCollapsed && this._isVisible(shape)) {
                    return false;
                }
                return true;
            }
            else {
                return shape.isCollapsed && !this._isTop(shape);
            }
        },

        /**
         *  Determines whether the shape is or needs to be mapped to another shape. This occurs essentially when the shape sits in
         * a collapsed container hierarchy and an external connection needs a node endpoint. This node then corresponds to the mapped shape and is
         * necessarily a container in the parent hierarchy of the shape.
         * @param shape
         */
        isShapeMapped: function (shape) {
            return shape.isCollapsed && !this._isVisible(shape) && !this._isTop(shape);
        },

        leastCommonAncestor: function (a, b) {
            if (!a) {
                throw "Parameter should not be null.";
            }
            if (!b) {
                throw "Parameter should not be null.";
            }

            if (!this.hyperTree) {
                throw "No hypertree available.";
            }
            var al = this.listToRoot(a);
            var bl = this.listToRoot(b);
            var found = null;
            if (Utils.isEmpty(al) || Utils.isEmpty(bl)) {
                return this.hyperTree.root.data;
            }
            var xa = al[0];
            var xb = bl[0];
            var i = 0;
            while (xa === xb) {
                found = al[i];
                i++;
                if (i >= al.length || i >= bl.length) {
                    break;
                }
                xa = al[i];
                xb = bl[i];
            }
            if (!found) {
                return this.hyperTree.root.data;
            }
            else {
                return grep(this.hyperTree.nodes, function (n) {
                    return  n.data.container === found;
                });
            }
        },
        /**
         * Determines whether the specified item is a top-level shape or container.
         * @param item
         * @returns {boolean}
         * @private
         */
        _isTop: function (item) {
            return !item.parentContainer;
        },

        /**
         * Determines iteratively (by walking up the container stack) whether the specified shape is visible.
         * This does NOT tell whether the item is not visible due to an explicit Visibility change or due to a collapse state.
         * @param shape
         * @returns {*}
         * @private
         */
        _isVisible: function (shape) {

            if (!shape.visible()) {
                return false;
            }
            return !shape.parentContainer ? shape.visible() : this._isVisible(shape.parentContainer);
        },

        _isCollapsed: function (shape) {

            if (shape.isContainer && shape.isCollapsed) {
                return true;
            }
            return shape.parentContainer && this._isCollapsed(shape.parentContainer);
        },

        /**
         * First part of the graph creation; analyzing the shapes and containers and deciding whether they should be mapped to a Node.
         * @private
         */
        _renormalizeShapes: function () {
            // add the nodes, the adjacency structure will be reconstructed later on
            if (this.options.ignoreContainers) {
                for (var i = 0, len = this.diagram.shapes.length; i < len; i++) {
                    var shape = this.diagram.shapes[i];

                    // if not visible (and ignoring the invisible ones) or a container we skip
                    if ((this.options.ignoreInvisible && !this._isVisible(shape)) || shape.isContainer) {
                        this.ignoredShapes.push(shape);
                        continue;
                    }
                    var node = new Node(shape.id, shape);
                    node.isVirtual = false;

                    // the mapping will always contain singletons and the hyperTree will be null
                    this.nodeMap.add(node, [shape]);
                    this.nodes.push(node);
                }
            }
            else {
                throw "Containers are not supported yet, but stay tuned.";
            }
        },

        /**
         * Second part of the graph creation; analyzing the connections and deciding whether they should be mapped to an edge.
         * @private
         */
        _renormalizeConnections: function () {
            if (this.diagram.connections.length === 0) {
                return;
            }
            for (var i = 0, len = this.diagram.connections.length; i < len; i++) {
                var conn = this.diagram.connections[i];

                if (this.isIgnorableItem(conn)) {
                    this.ignoredConnections.push(conn);
                    continue;
                }

                var source = !conn.sourceConnector ? null : conn.sourceConnector.shape;
                var sink = !conn.targetConnector ? null : conn.targetConnector.shape;

                // no layout for floating connections
                if (!source || !sink) {
                    this.ignoredConnections.push(conn);
                    continue;
                }

                if (contains(this.ignoredShapes, source) && !this.shapeMap.containsKey(source)) {
                    this.ignoredConnections.push(conn);
                    continue;
                }
                if (contains(this.ignoredShapes, sink) && !this.shapeMap.containsKey(sink)) {
                    this.ignoredConnections.push(conn);
                    continue;
                }

                // if the endpoint sits in a collapsed container we need the container rather than the shape itself
                if (this.shapeMap.containsKey(source)) {
                    source = this.shapeMap[source];
                }
                if (this.shapeMap.containsKey(sink)) {
                    sink = this.shapeMap[sink];
                }

                var sourceNode = this.mapShape(source);
                var sinkNode = this.mapShape(sink);
                if ((sourceNode === sinkNode) || this.areConnectedAlready(sourceNode, sinkNode)) {
                    this.ignoredConnections.push(conn);
                    continue;
                }

                if (sourceNode === null || sinkNode === null) {
                    throw "A shape was not mapped to a node.";
                }
                if (this.options.ignoreContainers) {
                    // much like a floating connection here since at least one end is attached to a container
                    if (sourceNode.isVirtual || sinkNode.isVirtual) {
                        this.ignoredConnections.push(conn);
                        continue;
                    }
                    var newEdge = new Link(sourceNode, sinkNode, conn.id, conn);

                    this.edgeMap.add(newEdge, [conn]);
                    this.edges.push(newEdge);
                }
                else {
                    throw "Containers are not supported yet, but stay tuned.";
                }
            }
        },

        areConnectedAlready: function (n, m) {
            return Utils.any(this.edges, function (l) {
                return l.source === n && l.target === m || l.source === m && l.target === n;
            });
        }

        /**
         * Depth-first traversal of the given container.
         * @param container
         * @param action
         * @param includeStart
         * @private
         */
        /* _visitContainer: function (container, action, includeStart) {

         *//*if (container == null) throw new ArgumentNullException("container");
         if (action == null) throw new ArgumentNullException("action");
         if (includeStart) action(container);
         if (container.children.isEmpty()) return;
         foreach(
         var item
         in
         container.children.OfType < IShape > ()
         )
         {
         var childContainer = item
         as
         IContainerShape;
         if (childContainer != null) this.VisitContainer(childContainer, action);
         else action(item);
         }*//*
         }*/


    });

    /**
     * The classic spring-embedder (aka force-directed, Fruchterman-Rheingold, barycentric) algorithm.
     * http://en.wikipedia.org/wiki/Force-directed_graph_drawing
     *  - Chapter 12 of Tamassia et al. "Handbook of graph drawing and visualization".
     *  - Kobourov on preprint arXiv; http://arxiv.org/pdf/1201.3011.pdf
     *  - Fruchterman and Rheingold in SOFTWARE-PRACTICE AND EXPERIENCE, VOL. 21(1 1), 1129-1164 (NOVEMBER 1991)
     * @type {*}
     */
    var SpringLayout = LayoutBase.extend({
        init: function (diagram) {
            var that = this;
            LayoutBase.fn.init.call(that);
            if (Utils.isUndefined(diagram)) {
                throw "Diagram is not specified.";
            }
            this.diagram = diagram;
        },

        layout: function (options) {

            this.transferOptions(options);

            var adapter = new DiagramToHyperTreeAdapter(this.diagram);
            var graph = adapter.convert(options);
            if (graph.isEmpty()) {
                return;
            }
            // split into connected components
            var components = graph.getConnectedComponents();
            if (Utils.isEmpty(components)) {
                return;
            }
            for (var i = 0; i < components.length; i++) {
                var component = components[i];
                this.layoutGraph(component, options);
            }
            var finalNodeSet = this.gridLayoutComponents(components);
            return new diagram.LayoutState(this.diagram, finalNodeSet);
        },

        layoutGraph: function (graph, options) {

            if (Utils.isDefined(options)) {
                this.transferOptions(options);
            }
            this.graph = graph;

            var initialTemperature = this.options.nodeDistance * 9;
            this.temperature = initialTemperature;

            var guessBounds = this._expectedBounds();
            this.width = guessBounds.width;
            this.height = guessBounds.height;

            for (var step = 0; step < this.options.iterations; step++) {
                this.refineStage = step >= this.options.iterations * 5 / 6;
                this.tick();
                // exponential cooldown
                this.temperature = this.refineStage ?
                    initialTemperature / 30 :
                    initialTemperature * (1 - step / (2 * this.options.iterations ));
            }
        },

        /**
         * Single iteration of the simulation.
         */
        tick: function () {
            var i;
            // collect the repulsive forces on each node
            for (i = 0; i < this.graph.nodes.length; i++) {
                this._repulsion(this.graph.nodes[i]);
            }

            // collect the attractive forces on each node
            for (i = 0; i < this.graph.links.length; i++) {
                this._attraction(this.graph.links[i]);
            }
            // update the positions
            for (i = 0; i < this.graph.nodes.length; i++) {
                var node = this.graph.nodes[i];
                var offset = Math.sqrt(node.dx * node.dx + node.dy * node.dy);
                if (offset === 0) {
                    return;
                }
                node.x += Math.min(offset, this.temperature) * node.dx / offset;
                node.y += Math.min(offset, this.temperature) * node.dy / offset;
                if (this.options.limitToView) {
                    node.x = Math.min(this.width, Math.max(node.width / 2, node.x));
                    node.y = Math.min(this.height, Math.max(node.height / 2, node.y));
                }
            }
        },

        /**
         * Shakes the node away from its current position to escape the deadlock.
         * @param node A Node.
         * @private
         */
        _shake: function (node) {
            // just a simple polar neighborhood
            var rho = Math.random() * this.options.nodeDistance / 4;
            var alpha = Math.random() * 2 * Math.PI;
            node.x += rho * Math.cos(alpha);
            node.y -= rho * Math.sin(alpha);
        },

        /**
         * The typical Coulomb-Newton force law F=k/r^2
         * @remark This only works in dimensions less than three.
         * @param d
         * @param n A Node.
         * @param m Another Node.
         * @returns {number}
         * @private
         */
        _InverseSquareForce: function (d, n, m) {
            var force;
            if (!this.refineStage) {
                force = Math.pow(d, 2) / Math.pow(this.options.nodeDistance, 2);
            }
            else {
                var deltax = n.x - m.x;
                var deltay = n.y - m.y;

                var wn = n.width / 2;
                var hn = n.height / 2;
                var wm = m.width / 2;
                var hm = m.height / 2;

                force = (Math.pow(deltax, 2) / Math.pow(wn + wm + this.options.nodeDistance, 2)) + (Math.pow(deltay, 2) / Math.pow(hn + hm + this.options.nodeDistance, 2));
            }
            return force * 4 / 3;
        },

        /**
         * The typical Hooke force law F=kr^2
         * @param d
         * @param n
         * @param m
         * @returns {number}
         * @private
         */
        _SquareForce: function (d, n, m) {
            return 1 / this._InverseSquareForce(d, n, m);
        },

        _repulsion: function (n) {
            n.dx = 0;
            n.dy = 0;
            Utils.forEach(this.graph.nodes, function (m) {
                if (m === n) {
                    return;
                }
                while (n.x === m.x && n.y === m.y) {
                    this._shake(m);
                }
                var vx = n.x - m.x;
                var vy = n.y - m.y;
                var distance = Math.sqrt(vx * vx + vy * vy);
                var r = this._SquareForce(distance, n, m) * 2;
                n.dx += (vx / distance) * r;
                n.dy += (vy / distance) * r;
            }, this);
        },
        _attraction: function (link) {
            var t = link.target;
            var s = link.source;
            if (s === t) {
                // loops induce endless shakes
                return;
            }
            while (s.x === t.x && s.y === t.y) {
                this._shake(t);
            }

            var vx = s.x - t.x;
            var vy = s.y - t.y;
            var distance = Math.sqrt(vx * vx + vy * vy);

            var a = this._InverseSquareForce(distance, s, t) * 5;
            var dx = (vx / distance) * a;
            var dy = (vy / distance) * a;
            t.dx += dx;
            t.dy += dy;
            s.dx -= dx;
            s.dy -= dy;
        },

        /**
         * Calculates the expected bounds after layout.
         * @returns {*}
         * @private
         */
        _expectedBounds: function () {

            var size, N = this.graph.nodes.length, /*golden ration optimal?*/ ratio = 1.5, multiplier = 4;
            if (N === 0) {
                return size;
            }
            size = Utils.fold(this.graph.nodes, function (s, node) {
                var area = node.width * node.height;
                if (area > 0) {
                    s += Math.sqrt(area);
                    return s;
                }
                return 0;
            }, 0, this);
            var av = size / N;
            var squareSize = av * Math.ceil(Math.sqrt(N));
            var width = squareSize * Math.sqrt(ratio);
            var height = squareSize / Math.sqrt(ratio);
            return { width: width * multiplier, height: height * multiplier };
        }

    });

    var TreeLayoutProcessor = kendo.Class.extend({

        init: function (options) {
            this.center = null;
            this.options = options;
        },
        layout: function (treeGraph, root) {
            this.graph = treeGraph;
            if (!this.graph.nodes || this.graph.nodes.length === 0) {
                return;
            }

            if (!contains(this.graph.nodes, root)) {
                throw "The given root is not in the graph.";
            }

            this.center = root;
            this.graph.cacheRelationships();
            /* var nonull = this.graph.nodes.where(function (n) {
             return n.associatedShape != null;
             });*/

            // transfer the rects
            /*nonull.forEach(function (n) {
             n.Location = n.associatedShape.Position;
             n.NodeSize = n.associatedShape.ActualBounds.ToSize();
             }

             );*/

            // caching the children
            /* nonull.forEach(function (n) {
             n.children = n.getChildren();
             });*/

            this.layoutSwitch();

            // apply the layout to the actual visuals
            // nonull.ForEach(n => n.associatedShape.Position = n.Location);
        },

        layoutLeft: function (left) {
            this.setChildrenDirection(this.center, "Left", false);
            this.setChildrenLayout(this.center, "Default", false);
            var h = 0, w = 0, y, i, node;
            for (i = 0; i < left.length; i++) {
                node = left[i];
                node.TreeDirection = "Left";
                var s = this.measure(node, Size.Empty);
                w = Math.max(w, s.Width);
                h += s.height + this.options.verticalSeparation;
            }

            h -= this.options.verticalSeparation;
            var x = this.center.x - this.options.horizontalSeparation;
            y = this.center.y + ((this.center.height - h) / 2);
            for (i = 0; i < left.length; i++) {
                node = left[i];
                var p = new Point(x - node.Size.width, y);

                this.arrange(node, p);
                y += node.Size.height + this.options.verticalSeparation;
            }
        },

        layoutRight: function (right) {
            this.setChildrenDirection(this.center, "Right", false);
            this.setChildrenLayout(this.center, "Default", false);
            var h = 0, w = 0, y, i, node;
            for (i = 0; i < right.length; i++) {
                node = right[i];
                node.TreeDirection = "Right";
                var s = this.measure(node, Size.Empty);
                w = Math.max(w, s.Width);
                h += s.height + this.options.verticalSeparation;
            }

            h -= this.options.verticalSeparation;
            var x = this.center.x + this.options.horizontalSeparation + this.center.width;
            y = this.center.y + ((this.center.height - h) / 2);
            for (i = 0; i < right.length; i++) {
                node = right[i];
                var p = new Point(x, y);
                this.arrange(node, p);
                y += node.Size.height + this.options.verticalSeparation;
            }
        },

        layoutUp: function (up) {
            this.setChildrenDirection(this.center, "Up", false);
            this.setChildrenLayout(this.center, "Default", false);
            var w = 0, y, node, i;
            for (i = 0; i < up.length; i++) {
                node = up[i];
                node.TreeDirection = "Up";
                var s = this.measure(node, Size.Empty);
                w += s.width + this.options.horizontalSeparation;
            }

            w -= this.options.horizontalSeparation;
            var x = this.center.x + (this.center.width / 2) - (w / 2);

            // y = this.center.y -verticalSeparation -this.center.height/2 - h;
            for (i = 0; i < up.length; i++) {
                node = up[i];
                y = this.center.y - this.options.verticalSeparation - node.Size.height;
                var p = new Point(x, y);
                this.arrange(node, p);
                x += node.Size.width + this.options.horizontalSeparation;
            }
        },

        layoutDown: function (down) {
            var node, i;
            this.setChildrenDirection(this.center, "Down", false);
            this.setChildrenLayout(this.center, "Default", false);
            var w = 0, y;
            for (i = 0; i < down.length; i++) {
                node = down[i];
                node.treeDirection = "Down";
                var s = this.measure(node, Size.Empty);
                w += s.width + this.options.horizontalSeparation;
            }

            w -= this.options.horizontalSeparation;
            var x = this.center.x + (this.center.width / 2) - (w / 2);
            y = this.center.y + this.options.verticalSeparation + this.center.height;
            for (i = 0; i < down.length; i++) {
                node = down[i];
                var p = new Point(x, y);
                this.arrange(node, p);
                x += node.Size.width + this.options.horizontalSeparation;
            }
        },

        layoutRadialTree: function () {
            // var rmax = children.Aggregate(0D, (current, node) => Math.max(node.SectorAngle, current));
            this.setChildrenDirection(this.center, "Radial", false);
            this.setChildrenLayout(this.center, "Default", false);
            this.previousRoot = null;
            var startAngle = this.options.startRadialAngle * DEG_TO_RAD;
            var endAngle = this.options.endRadialAngle * DEG_TO_RAD;
            if (endAngle <= startAngle) {
                throw "Final angle should not be less than the start angle.";
            }

            this.maxDepth = 0;
            this.origin = new Point(this.center.x, this.center.y);
            this.calculateAngularWidth(this.center, 0);

            // perform the layout
            if (this.maxDepth > 0) {
                this.radialLayout(this.center, this.options.radialFirstLevelSeparation, startAngle, endAngle);
            }

            // update properties of the root node
            this.center.Angle = endAngle - startAngle;
        },

        tipOverTree: function (down, startFromLevel) {
            if (Utils.isUndefined(startFromLevel)) {
                startFromLevel = 0;
            }

            this.setChildrenDirection(this.center, "Down", false);
            this.setChildrenLayout(this.center, "Default", false);
            this.setChildrenLayout(this.center, "Underneath", false, startFromLevel);
            var w = 0, y, node, i;
            for (i = 0; i < down.length; i++) {
                node = down[i];

                // if (node.IsSpecial) continue;
                node.TreeDirection = "Down";
                var s = this.measure(node, Size.Empty);
                w += s.width + this.options.horizontalSeparation;
            }

            w -= this.options.horizontalSeparation;

            // putting the root in the center with respect to the whole diagram is not a nice result, let's put it with respect to the first level only
            w -= down[down.length - 1].width;
            w += down[down.length - 1].associatedShape.bounds().width;

            var x = this.center.x + (this.center.width / 2) - (w / 2);
            y = this.center.y + this.options.verticalSeparation + this.center.height;
            for (i = 0; i < down.length; i++) {
                node = down[i];
                // if (node.IsSpecial) continue;
                var p = new Point(x, y);
                this.arrange(node, p);
                x += node.Size.width + this.options.horizontalSeparation;
            }

            /*//let's place the special node, assuming there is only one
             if (down.Count(n => n.IsSpecial) > 0)
             {
             var special = (from n in down where n.IsSpecial select n).First();
             if (special.Children.Count > 0)
             throw new DiagramException("The 'special' element should not have children.");
             special.Data.Location = new Point(Center.Data.Location.X + Center.AssociatedShape.BoundingRectangle.Width + this.options.HorizontalSeparation, Center.Data.Location.Y);
             }*/
        },
        calculateAngularWidth: function (n, d) {
            if (d > this.maxDepth) {
                this.maxDepth = d;
            }

            var aw = 0, w = 1000, h = 1000, diameter = d === 0 ? 0 : Math.sqrt((w * w) + (h * h)) / d;

            if (n.children.length > 0) {
                // eventually with n.IsExpanded
                for (var i = 0, len = n.children.length; i < len; i++) {
                    var child = n.children[i];
                    aw += this.calculateAngularWidth(child, d + 1);
                }
                aw = Math.max(diameter, aw);
            }
            else {
                aw = diameter;
            }

            n.sectorAngle = aw;
            return aw;
        },
        sortChildren: function (n) {
            var basevalue = 0, i;

            // update basevalue angle for node ordering
            if (n.parents.length > 1) {
                throw "Node is not part of a tree.";
            }
            var p = n.parents[0];
            if (p) {
                var pl = new Point(p.x, p.y);
                var nl = new Point(n.x, n.y);
                basevalue = this.normalizeAngle(Math.atan2(pl.y - nl.y, pl.x - nl.x));
            }

            var count = n.children.length;
            if (count === 0) {
                return null;
            }

            var angle = [];
            var idx = [];

            for (i = 0; i < count; ++i) {
                var c = n.children[i];
                var l = new Point(c.x, c.y);
                idx[i] = i;
                angle[i] = this.normalizeAngle(-basevalue + Math.atan2(l.y - l.y, l.x - l.x));
            }

            Utils.bisort(angle, idx);
            var col = []; // list of nodes
            var children = n.children;
            for (i = 0; i < count; ++i) {
                col.push(children[idx[i]]);
            }

            return col;
        },

        normalizeAngle: function (angle) {
            while (angle > Math.PI * 2) {
                angle -= 2 * Math.PI;
            }
            while (angle < 0) {
                angle += Math.PI * 2;
            }
            return angle;
        },
        radialLayout: function (node, radius, startAngle, endAngle) {
            var deltaTheta = endAngle - startAngle;
            var deltaThetaHalf = deltaTheta / 2.0;
            var parentSector = node.sectorAngle;
            var fraction = 0;
            var sorted = this.sortChildren(node);
            for (var i = 0, len = sorted.length; i < len; i++) {
                var childNode = sorted[i];
                var cp = childNode;
                var childAngleFraction = cp.sectorAngle / parentSector;
                if (childNode.children.length > 0) {
                    this.radialLayout(childNode,
                        radius + this.options.radialSeparation,
                        startAngle + (fraction * deltaTheta),
                        startAngle + ((fraction + childAngleFraction) * deltaTheta));
                }

                this.setPolarLocation(childNode, radius, startAngle + (fraction * deltaTheta) + (childAngleFraction * deltaThetaHalf));
                cp.angle = childAngleFraction * deltaTheta;
                fraction += childAngleFraction;
            }
        },
        setPolarLocation: function (node, radius, angle) {
            node.x = this.origin.x + (radius * Math.cos(angle));
            node.y = this.origin.y + (radius * Math.sin(angle));
            node.BoundingRectangle = new Rect(node.x, node.y, node.width, node.height);
        },

        /**
         * Sets the children direction recursively.
         * @param node
         * @param direction
         * @param includeStart
         */
        setChildrenDirection: function (node, direction, includeStart) {
            var rootDirection = node.treeDirection;
            this.graph.depthFirstTraversal(node, function (n) {
                n.treeDirection = direction;
            });
            if (!includeStart) {
                node.treeDirection = rootDirection;
            }
        },

        /**
         * Sets the children layout recursively.
         * @param node
         * @param layout
         * @param includeStart
         * @param startFromLevel
         */
        setChildrenLayout: function (node, layout, includeStart, startFromLevel) {
            if (Utils.isUndefined(startFromLevel)) {
                startFromLevel = 0;
            }
            var rootLayout = node.childrenLayout;
            if (startFromLevel > 0) {
                // assign levels to the Node.Level property
                this.graph.assignLevels(node);

                // assign the layout on the condition that the level is at least the 'startFromLevel'
                this.graph.depthFirstTraversal(
                    node, function (s) {
                        if (s.level >= startFromLevel + 1) {
                            s.childrenLayout = layout;
                        }
                    }
                );
            }
            else {
                this.graph.depthFirstTraversal(node, function (s) {
                    s.childrenLayout = layout;
                });

                // if the start should not be affected we put the state back
                if (!includeStart) {
                    node.childrenLayout = rootLayout;
                }
            }
        },

        /**
         * Returns the actual size of the node. The given size is the allowed space wherein the node can lay out itself.
         * @param node
         * @param givenSize
         * @returns {Size}
         */
        measure: function (node, givenSize) {
            var w = 0, h = 0, s;
            var result = new Size(0, 0);
            if (!node) {
                throw "";
            }
            var b = node.associatedShape.bounds();
            var shapeWidth = b.width;
            var shapeHeight = b.height;
            if (node.parents.length !== 1) {
                throw "Node not in a spanning tree.";
            }

            var parent = node.parents[0];
            if (node.treeDirection === "Undefined") {
                node.treeDirection = parent.treeDirection;
            }

            if (Utils.isEmpty(node.children)) {
                result = new Size(
                    Math.abs(shapeWidth) < EPSILON ? 50 : shapeWidth,
                    Math.abs(shapeHeight) < EPSILON ? 25 : shapeHeight);
            }
            else if (node.children.length === 1) {
                switch (node.treeDirection) {
                    case "Radial":
                        s = this.measure(node.children[0], givenSize); // child size
                        w = shapeWidth + (this.options.radialSeparation * Math.cos(node.AngleToParent)) + s.width;
                        h = shapeHeight + Math.abs(this.options.radialSeparation * Math.sin(node.AngleToParent)) + s.height;
                        break;
                    case "Left":
                    case "Right":
                        switch (node.childrenLayout) {

                            case "TopAlignedWithParent":
                                break;

                            case "BottomAlignedWithParent":
                                break;

                            case "Underneath":
                                s = this.measure(node.children[0], givenSize);
                                w = shapeWidth + s.width + this.options.underneathHorizontalOffset;
                                h = shapeHeight + this.options.underneathVerticalTopOffset + s.height;
                                break;

                            case "Default":
                                s = this.measure(node.children[0], givenSize);
                                w = shapeWidth + this.options.horizontalSeparation + s.width;
                                h = Math.max(shapeHeight, s.height);
                                break;

                            default:
                                throw "Unhandled TreeDirection in the Radial layout measuring.";
                        }
                        break;
                    case "Up":
                    case "Down":
                        switch (node.childrenLayout) {

                            case "TopAlignedWithParent":
                            case "BottomAlignedWithParent":
                                break;

                            case "Underneath":
                                s = this.measure(node.children[0], givenSize);
                                w = Math.max(shapeWidth, s.width + this.options.underneathHorizontalOffset);
                                h = shapeHeight + this.options.underneathVerticalTopOffset + s.height;
                                break;

                            case "Default":
                                s = this.measure(node.children[0], givenSize);
                                h = shapeHeight + this.options.verticalSeparation + s.height;
                                w = Math.max(shapeWidth, s.width);
                                break;

                            default:
                                throw "Unhandled TreeDirection in the Down layout measuring.";
                        }
                        break;
                    default:
                        throw "Unhandled TreeDirection in the layout measuring.";
                }

                result = new Size(w, h);
            }
            else {
                var i, childNode;
                switch (node.treeDirection) {
                    case "Left":
                    case "Right":
                        switch (node.childrenLayout) {

                            case "TopAlignedWithParent":
                            case "BottomAlignedWithParent":
                                break;

                            case "Underneath":
                                w = shapeWidth;
                                h = shapeHeight + this.options.underneathVerticalTopOffset;
                                for (i = 0; i < node.children.length; i++) {
                                    childNode = node.children[i];
                                    s = this.measure(childNode, givenSize);
                                    w = Math.max(w, s.width + this.options.underneathHorizontalOffset);
                                    h += s.height + this.options.underneathVerticalSeparation;
                                }

                                h -= this.options.underneathVerticalSeparation;
                                break;

                            case "Default":
                                w = shapeWidth;
                                h = 0;
                                for (i = 0; i < node.children.length; i++) {
                                    childNode = node.children[i];
                                    s = this.measure(childNode, givenSize);
                                    w = Math.max(w, shapeWidth + this.options.horizontalSeparation + s.width);
                                    h += s.height + this.options.verticalSeparation;
                                }
                                h -= this.options.verticalSeparation;
                                break;

                            default:
                                throw "Unhandled TreeDirection in the Right layout measuring.";
                        }

                        break;
                    case "Up":
                    case "Down":

                        switch (node.childrenLayout) {

                            case "TopAlignedWithParent":
                            case "BottomAlignedWithParent":
                                break;

                            case "Underneath":
                                w = shapeWidth;
                                h = shapeHeight + this.options.underneathVerticalTopOffset;
                                for (i = 0; i < node.children.length; i++) {
                                    childNode = node.children[i];
                                    s = this.measure(childNode, givenSize);
                                    w = Math.max(w, s.width + this.options.underneathHorizontalOffset);
                                    h += s.height + this.options.underneathVerticalSeparation;
                                }

                                h -= this.options.underneathVerticalSeparation;
                                break;

                            case "Default":
                                w = 0;
                                h = 0;
                                for (i = 0; i < node.children.length; i++) {
                                    childNode = node.children[i];
                                    s = this.measure(childNode, givenSize);
                                    w += s.width + this.options.horizontalSeparation;
                                    h = Math.max(h, s.height + this.options.verticalSeparation + shapeHeight);
                                }

                                w -= this.options.horizontalSeparation;
                                break;

                            default:
                                throw "Unhandled TreeDirection in the Down layout measuring.";
                        }

                        break;
                    default:
                        throw "Unhandled TreeDirection in the layout measuring.";
                }

                result = new Size(w, h);
            }

            node.SectorAngle = Math.sqrt((w * w / 4) + (h * h / 4));
            node.Size = result;
            return result;
        },
        arrange: function (n, p) {
            var i, pp, child, node, childrenwidth, b = n.associatedShape.bounds();
            var shapeWidth = b.width;
            var shapeHeight = b.height;
            if (Utils.isEmpty(n.children)) {
                n.x = p.x;
                n.y = p.y;
                n.BoundingRectangle = new Rect(p.x, p.y, shapeWidth, shapeHeight);
            }
            else {
                var x, y;
                var selfLocation;
                switch (n.treeDirection) {
                    case "Left":
                        switch (n.childrenLayout) {
                            case "TopAlignedWithParent":
                            case "BottomAlignedWithParent":
                                break;

                            case "Underneath":
                                selfLocation = p;
                                n.x = selfLocation.x;
                                n.y = selfLocation.y;
                                n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                                y = p.y + shapeHeight + this.options.underneathVerticalTopOffset;
                                for (i = 0; i < node.children.length; i++) {
                                    node = node.children[i];
                                    x = selfLocation.x - node.associatedShape.width - this.options.underneathHorizontalOffset;
                                    pp = new Point(x, y);
                                    this.arrange(node, pp);
                                    y += node.Size.height + this.options.underneathVerticalSeparation;
                                }
                                break;

                            case "Default":
                                selfLocation = new Point(p.x + n.Size.width - shapeWidth, p.y + ((n.Size.height - shapeHeight) / 2));
                                n.x = selfLocation.x;
                                n.y = selfLocation.y;
                                n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                                x = selfLocation.x - this.options.horizontalSeparation; // alignment of children
                                y = p.y;
                                for (i = 0; i < n.children.length; i++) {
                                    node = n.children[i];
                                    pp = new Point(x - node.Size.width, y);
                                    this.arrange(node, pp);
                                    y += node.Size.height + this.options.verticalSeparation;
                                }
                                break;

                            default:
                                throw   "Unsupported TreeDirection";
                        }

                        break;
                    case "Right":
                        switch (n.childrenLayout) {
                            case "TopAlignedWithParent":
                            case "BottomAlignedWithParent":
                                break;

                            case "Underneath":
                                selfLocation = p;
                                n.x = selfLocation.x;
                                n.y = selfLocation.y;
                                n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                                x = p.x + shapeWidth + this.options.underneathHorizontalOffset;

                                // alignment of children left-underneath the parent
                                y = p.y + shapeHeight + this.options.underneathVerticalTopOffset;
                                for (i = 0; i < n.children.length; i++) {
                                    node = n.children[i];
                                    pp = new Point(x, y);
                                    this.arrange(node, pp);
                                    y += node.Size.height + this.options.underneathVerticalSeparation;
                                }

                                break;

                            case "Default":
                                selfLocation = new Point(p.x, p.y + ((n.Size.height - shapeHeight) / 2));
                                n.x = selfLocation.x;
                                n.y = selfLocation.y;
                                n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                                x = p.x + shapeWidth + this.options.horizontalSeparation; // alignment of children
                                y = p.y;
                                for (i = 0; i < n.children.length; i++) {
                                    node = n.children[i];
                                    pp = new Point(x, y);
                                    this.arrange(node, pp);
                                    y += node.Size.height + this.options.verticalSeparation;
                                }
                                break;

                            default:
                                throw   "Unsupported TreeDirection";
                        }

                        break;
                    case "Up":
                        selfLocation = new Point(p.x + ((n.Size.width - shapeWidth) / 2), p.y + n.Size.height - shapeHeight);
                        n.x = selfLocation.x;
                        n.y = selfLocation.y;
                        n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                        if (Math.abs(selfLocation.x - p.x) < EPSILON) {
                            childrenwidth = 0;
                            // means there is an aberration due to the oversized Element with respect to the children
                            for (i = 0; i < n.children.length; i++) {
                                child = n.children[i];
                                childrenwidth += child.Size.width + this.options.horizontalSeparation;
                            }
                            childrenwidth -= this.options.horizontalSeparation;
                            x = p.x + ((shapeWidth - childrenwidth) / 2);
                        }
                        else {
                            x = p.x;
                        }

                        for (i = 0; i < n.children.length; i++) {
                            node = n.children[i];
                            y = selfLocation.y - this.options.verticalSeparation - node.Size.height;
                            pp = new Point(x, y);
                            this.arrange(node, pp);
                            x += node.Size.width + this.options.horizontalSeparation;
                        }
                        break;

                    case "Down":

                        switch (n.childrenLayout) {
                            case "TopAlignedWithParent":
                            case "BottomAlignedWithParent":
                                break;
                            case "Underneath":
                                selfLocation = p;
                                n.x = selfLocation.x;
                                n.y = selfLocation.y;
                                n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                                x = p.x + this.options.underneathHorizontalOffset; // alignment of children left-underneath the parent
                                y = p.y + shapeHeight + this.options.underneathVerticalTopOffset;
                                for (i = 0; i < n.children.length; i++) {
                                    node = n.children[i];
                                    pp = new Point(x, y);
                                    this.arrange(node, pp);
                                    y += node.Size.height + this.options.underneathVerticalSeparation;
                                }
                                break;

                            case    "Default":
                                selfLocation = new Point(p.x + ((n.Size.width - shapeWidth) / 2), p.y);
                                n.x = selfLocation.x;
                                n.y = selfLocation.y;
                                n.BoundingRectangle = new Rect(n.x, n.y, n.width, n.height);
                                if (Math.abs(selfLocation.x - p.x) < EPSILON) {
                                    childrenwidth = 0;
                                    // means there is an aberration due to the oversized Element with respect to the children
                                    for (i = 0; i < n.children.length; i++) {
                                        child = n.children[i];
                                        childrenwidth += child.Size.width + this.options.horizontalSeparation;
                                    }

                                    childrenwidth -= this.options.horizontalSeparation;
                                    x = p.x + ((shapeWidth - childrenwidth) / 2);
                                }
                                else {
                                    x = p.x;
                                }

                                for (i = 0; i < n.children.length; i++) {
                                    node = n.children[i];
                                    y = selfLocation.y + this.options.verticalSeparation + shapeHeight;
                                    pp = new Point(x, y);
                                    this.arrange(node, pp);
                                    x += node.Size.width + this.options.horizontalSeparation;
                                }
                                break;

                            default:
                                throw   "Unsupported TreeDirection";
                        }
                        break;

                    case "None":
                        break;

                    default:
                        throw   "Unsupported TreeDirection";
                }
            }
        },
        layoutSwitch: function () {
            if (!this.center) {
                return;
            }

            if (Utils.isEmpty(this.center.children)) {
                return;
            }

            var type = this.options.subtype;
            if (Utils.isUndefined(type)) {
                type = "Down";
            }
            var single, male, female, leftcount;
            var children = this.center.children;
            switch (type.toLowerCase()) {
                case "radial":
                case "radialtree":
                    this.layoutRadialTree();
                    break;

                case "mindmaphorizontal":
                case "mindmap":
                    single = this.center.children;

                    if (this.center.children.length === 1) {
                        this.layoutRight(single);
                    }
                    else {
                        // odd number will give one more at the right
                        leftcount = children.length / 2;
                        male = grep(this.center.children, function (n) {
                            return Utils.indexOf(children, n) < leftcount;
                        });
                        female = grep(this.center.children, function (n) {
                            return Utils.indexOf(children, n) >= leftcount;
                        });

                        this.layoutLeft(male);
                        this.layoutRight(female);
                    }
                    break;

                case "mindmapvertical":
                    single = this.center.children;

                    if (this.center.children.length === 1) {
                        this.layoutDown(single);
                    }
                    else {
                        // odd number will give one more at the right
                        leftcount = children.length / 2;
                        male = grep(this.center.children, function (n) {
                            return Utils.indexOf(children, n) < leftcount;
                        });
                        female = grep(this.center.children, function (n) {
                            return Utils.indexOf(children, n) >= leftcount;
                        });
                        this.layoutUp(male);
                        this.layoutDown(female);
                    }
                    break;

                case "right":
                    this.layoutRight(this.center.children);
                    break;

                case "left":
                    this.layoutLeft(this.center.children);
                    break;

                case "up":
                case "bottom":
                    this.layoutUp(this.center.children);
                    break;

                case "down":
                case "top":
                    this.layoutDown(this.center.children);
                    break;

                case "tipover":
                case "tipovertree":
                    if (this.options.tipOverTreeStartLevel < 0) {
                        throw  "The tip-over level should be a positive integer.";
                    }
                    this.tipOverTree(this.center.children, this.options.tipOverTreeStartLevel);
                    break;

                case "undefined":
                case "none":
                    break;
            }
        }
    });

    /**
     * The various tree layout algorithms.
     * @type {*}
     */
    var TreeLayout = LayoutBase.extend({
        init: function (diagram) {
            var that = this;
            LayoutBase.fn.init.call(that);
            if (Utils.isUndefined(diagram)) {
                throw "No diagram specified.";
            }
            this.diagram = diagram;
        },

        /**
         * Arranges the diagram in a tree-layout with the specified options and tree subtype.
         */
        layout: function (options) {

            this.transferOptions(options);

            // transform the diagram into a Graph
            var adapter = new DiagramToHyperTreeAdapter(this.diagram);

            /**
             * The Graph reduction from the given diagram.
             * @type {*}
             */
            this.graph = adapter.convert();

            var finalNodeSet = this.layoutComponents();

            // note that the graph contains the original data and
            // the components are another instance of nodes referring to the same set of shapes
            return new diagram.LayoutState(this.diagram, finalNodeSet);
        },

        layoutComponents: function () {
            if (this.graph.isEmpty()) {
                return;
            }

            // split into connected components
            var components = this.graph.getConnectedComponents();
            if (Utils.isEmpty(components)) {
                return;
            }

            var layout = new TreeLayoutProcessor(this.options);
            var trees = [];
            // find a spanning tree for each component
            for (var i = 0; i < components.length; i++) {
                var component = components[i];

                var treeGraph = this.getTree(component);
                if (!treeGraph) {
                    throw "Failed to find a spanning tree for the component.";
                }
                var root = treeGraph.root;
                var tree = treeGraph.tree;
                layout.layout(tree, root);

                trees.push(tree);
            }

            return this.gridLayoutComponents(trees);

        },

        /**
         * Gets a spanning tree (and root) for the given graph.
         * Ensure that the given graph is connected!
         * @param graph
         * @returns {*} A literal object consisting of the found root and the spanning tree.
         */
        getTree: function (graph) {
            var root = null;
            if (this.options.roots && this.options.roots.length > 0) {
                for (var i = 0, len = graph.nodes.length; i < len; i++) {
                    var node = graph.nodes[i];
                    for (var j = 0; j < this.options.roots.length; j++) {
                        var givenRootShape = this.options.roots[j];
                        if (givenRootShape === node.associatedShape) {
                            root = node;
                            break;
                        }
                    }
                }
            }
            if (!root) {
                // finds the most probable root on the basis of the longest path in the component
                root = graph.root();
                // should not happen really
                if (!root) {
                    throw "Unable to find a root for the tree.";
                }
            }
            return this.getTreeForRoot(graph, root);
        },

        getTreeForRoot: function (graph, root) {

            var tree = graph.getSpanningTree(root);
            if (Utils.isUndefined(tree) || tree.isEmpty()) {
                return null;
            }
            return {
                tree: tree,
                root: tree.root
            };
        }

    });

    /**
     * The Sugiyama aka layered layout algorithm.
     * @type {*}
     */
    var LayeredLayout = LayoutBase.extend({
        init: function (diagram) {
            var that = this;
            LayoutBase.fn.init.call(that);
            if (Utils.isUndefined(diagram)) {
                throw "Diagram is not specified.";
            }
            this.diagram = diagram;
        },

        layout: function (options) {

            this.transferOptions(options);

            var adapter = new DiagramToHyperTreeAdapter(this.diagram);
            var graph = adapter.convert(options);
            if (graph.isEmpty()) {
                return;
            }
            // split into connected components
            var components = graph.getConnectedComponents();
            if (Utils.isEmpty(components)) {
                return;
            }
            for (var i = 0; i < components.length; i++) {
                var component = components[i];
                this.layoutGraph(component, options);
            }
            var finalNodeSet = this.gridLayoutComponents(components);
            return new diagram.LayoutState(this.diagram, finalNodeSet);

        },

        /**
         * Initializes the runtime data properties of the layout.
         * @private
         */
        _initRuntimeProperties: function () {
            for (var k = 0; k < this.graph.nodes.length; k++) {
                var node = this.graph.nodes[k];
                node.layer = -1;
                node.downstreamLinkCount = 0;
                node.upstreamLinkCount = 0;

                node.isVirtual = false;

                node.uBaryCenter = 0.0;
                node.dBaryCenter = 0.0;

                node.upstreamPriority = 0;
                node.downstreamPriority = 0;

                node.gridPosition = 0;
            }
        },
        _prepare: function (graph) {
            var current = [], i, l, link;
            for (l = 0; l < graph.links.length; l++) {
                // of many dummies have been inserted to make things work
                graph.links[l].depthOfDumminess = 0;
            }

            // defines a mapping of a node to the layer index
            var layerMap = new Dictionary();

            Utils.forEach(graph.nodes, function (node) {
                if (node.incoming.length === 0) {
                    layerMap.set(node, 0);
                    current.push(node);
                }
            });

            while (current.length > 0) {
                var next = current.shift();
                for (i = 0; i < next.outgoing.length; i++) {
                    link = next.outgoing[i];
                    var target = link.target;

                    if (layerMap.containsKey(target)) {
                        layerMap.set(target, Math.max(layerMap.get(next) + 1, layerMap.get(target)));
                    } else {
                        layerMap.set(target, layerMap.get(next) + 1);
                    }

                    if (!contains(current, target)) {
                        current.push(target);
                    }
                }
            }

            // the node count in the map defines how many layers w'll need
            var layerCount = 0;
            layerMap.forEachValue(function (nodecount) {
                layerCount = Math.max(layerCount, nodecount);
            });

            var sortedNodes = [];
            Utils.addRange(sortedNodes, layerMap.keys());
            sortedNodes.sort(function (o1, o2) {
                var o1layer = layerMap.get(o1);
                var o2layer = layerMap.get(o2);
                return Utils.sign(o2layer - o1layer);
            });

            for (var n = 0; n < sortedNodes.length; ++n) {
                var node = sortedNodes[n];
                var minLayer = Number.MAX_VALUE;

                if (node.outgoing.length === 0) {
                    continue;
                }

                for (l = 0; l < node.outgoing.length; ++l) {
                    link = node.outgoing[l];
                    minLayer = Math.min(minLayer, layerMap.get(link.target));
                }

                if (minLayer > 1) {
                    layerMap.set(node, minLayer - 1);
                }
            }

            this.layers = [];
            for (i = 0; i < layerCount + 1; i++) {
                this.layers.push([]);
            }

            layerMap.forEach(function (node, layer) {
                node.layer = layer;
                this.layers[layer].push(node);
            }, this);

            // set initial grid positions
            for (l = 0; l < this.layers.length; l++) {
                var layer = this.layers[l];
                for (i = 0; i < layer.length; i++) {
                    layer[i].gridPosition = i;
                }
            }
        },

        /**
         * Performs the layout of a single component.
         */
        layoutGraph: function (graph, options) {
            if (Utils.isUndefined(graph)) {
                throw "No graph given or graph analysis of the diagram failed.";
            }
            if (Utils.isDefined(options)) {
                this.transferOptions(options);
            }
            this.graph = graph;

            // sets unique indices on the nodes
            graph.setItemIndices();

            // ensures no cycles present for this layout
            var reversedEdges = graph.makeAcyclic();

            // define the runtime props being used by the layout algorithm
            this._initRuntimeProperties();

            this._prepare(graph, options);

            this._dummify();

            this._optimizeCrossings();

            this._swapPairs();

            this.arrangeNodes();

            this._moveThingsAround();

            this._dedummify();

            // re-reverse the links which were switched earlier
            Utils.forEach(reversedEdges, function (e) {
                if (e.points) {
                    e.points.reverse();
                }
            });
        },

        setMinDist: function (m, n, minDist) {
            var l = m.layer;
            var i = m.layerIndex;
            this.minDistances[l][i] = minDist;
        },

        getMinDist: function (m, n) {
            var dist = 0,
                i1 = m.layerIndex,
                i2 = n.layerIndex,
                l = m.layer,
                min = Math.min(i1, i2),
                max = Math.max(i1, i2);
            // use Sum()?
            for (var k = min; k < max; ++k) {
                dist += this.minDistances[l][k];
            }
            return dist;
        },

        placeLeftToRight: function (leftClasses) {
            var leftPos = new Dictionary(), n, node;
            for (var c = 0; c < this.layers.length; ++c) {
                var classNodes = leftClasses[c];
                if (!classNodes) {
                    continue;
                }

                for (n = 0; n < classNodes.length; n++) {
                    node = classNodes[n];
                    if (!leftPos.containsKey(node)) {
                        this.placeLeft(node, leftPos, c);
                    }
                }

                // adjust class
                var d = Number.POSITIVE_INFINITY;
                for (n = 0; n < classNodes.length; n++) {
                    node = classNodes[n];
                    var rightSibling = this.rightSibling(node);
                    if (rightSibling && this.nodeLeftClass.get(rightSibling) !== c) {
                        d = Math.min(d, leftPos.get(rightSibling) - leftPos.get(node) - this.getMinDist(node, rightSibling));
                    }
                }
                if (d === Number.POSITIVE_INFINITY) {
                    var D = [];
                    for (n = 0; n < classNodes.length; n++) {
                        node = classNodes[n];
                        var neighbors = [];
                        Utils.addRange(neighbors, this.upNodes.get(node));
                        Utils.addRange(neighbors, this.downNodes.get(node));

                        for (var e = 0; e < neighbors.length; e++) {
                            var neighbor = neighbors[e];
                            if (this.nodeLeftClass.get(neighbor) < c) {
                                D.push(leftPos.get(neighbor) - leftPos.get(node));
                            }
                        }
                    }
                    D.sort();
                    if (D.length === 0) {
                        d = 0;
                    }
                    else if (D.length % 2 === 1) {
                        d = D[this.intDiv(D.length, 2)];
                    }
                    else {
                        d = (D[this.intDiv(D.length, 2) - 1] + D[this.intDiv(D.length, 2)]) / 2;
                    }
                }
                for (n = 0; n < classNodes.length; n++) {
                    node = classNodes[n];
                    leftPos.set(node, leftPos.get(node) + d);
                }
            }
            return leftPos;
        },

        placeRightToLeft: function (rightClasses) {
            var rightPos = new Dictionary(), n, node;
            for (var c = 0; c < this.layers.length; ++c) {
                var classNodes = rightClasses[c];
                if (!classNodes) {
                    continue;
                }

                for (n = 0; n < classNodes.length; n++) {
                    node = classNodes[n];
                    if (!rightPos.containsKey(node)) {
                        this.placeRight(node, rightPos, c);
                    }
                }

                // adjust class
                var d = Number.NEGATIVE_INFINITY;
                for (n = 0; n < classNodes.length; n++) {
                    node = classNodes[n];
                    var leftSibling = this.leftSibling(node);
                    if (leftSibling && this.nodeRightClass.get(leftSibling) !== c) {
                        d = Math.max(d, rightPos.get(leftSibling) - rightPos.get(node) + this.getMinDist(leftSibling, node));
                    }
                }
                if (d === Number.NEGATIVE_INFINITY) {
                    var D = [];
                    for (n = 0; n < classNodes.length; n++) {
                        node = classNodes[n];
                        var neighbors = [];
                        Utils.addRange(neighbors, this.upNodes.get(node));
                        Utils.addRange(neighbors, this.downNodes.get(node));

                        for (var e = 0; e < neighbors.length; e++) {
                            var neighbor = neighbors[e];
                            if (this.nodeRightClass.get(neighbor) < c) {
                                D.push(rightPos.get(node) - rightPos.get(neighbor));
                            }
                        }
                    }
                    D.sort();
                    if (D.length === 0) {
                        d = 0;
                    }
                    else if (D.length % 2 === 1) {
                        d = D[this.intDiv(D.length, 2)];
                    }
                    else {
                        d = (D[this.intDiv(D.length, 2) - 1] + D[this.intDiv(D.length, 2)]) / 2;
                    }
                }
                for (n = 0; n < classNodes.length; n++) {
                    node = classNodes[n];
                    rightPos.set(node, rightPos.get(node) + d);
                }
            }
            return rightPos;
        },

        _getLeftWing: function () {
            var leftWing = { value: null };
            var result = this.computeClasses(leftWing, 1);
            this.nodeLeftClass = leftWing.value;
            return result;
        },

        _getRightWing: function () {
            var rightWing = { value: null };
            var result = this.computeClasses(rightWing, -1);
            this.nodeRightClass = rightWing.value;
            return result;
        },

        computeClasses: function (wingPair, d) {
            var currentWing = 0,
                wing = wingPair.value = new Dictionary();

            for (var l = 0; l < this.layers.length; ++l) {
                currentWing = l;

                var layer = this.layers[l];
                for (var n = d === 1 ? 0 : layer.length - 1; 0 <= n && n < layer.length; n += d) {
                    var node = layer[n];
                    if (!wing.containsKey(node)) {
                        wing.set(node, currentWing);
                        if (node.isVirtual) {
                            var ndsinl = this._nodesInLink(node);
                            for (var kk = 0; kk < ndsinl.length; kk++) {
                                var vnode = ndsinl[kk];
                                wing.set(vnode, currentWing);
                            }
                        }
                    }
                    else {
                        currentWing = wing.get(node);
                    }
                }
            }

            var wings = [];
            for (var i = 0; i < this.layers.length; i++) {
                wings.push(null);
            }
            wing.forEach(function (node, classIndex) {
                if (wings[classIndex] === null) {
                    wings[classIndex] = [];
                }
                wings[classIndex].push(node);
            });

            return wings;
        },
        _isVerticalLayout: function () {
            return this.options.subtype.toLowerCase() === "up" || this.options.subtype.toLowerCase() === "down" || this.options.subtype.toLowerCase() === "vertical";
        },

        _isHorizontalLayout: function () {
            return this.options.subtype.toLowerCase() === "right" || this.options.subtype.toLowerCase() === "left" || this.options.subtype.toLowerCase() === "horizontal";
        },
        _isIncreasingLayout: function () {
            // meaning that the visiting of the layers goes in the natural order of increasing layer index
            return this.options.subtype.toLowerCase() === "right" || this.options.subtype.toLowerCase() === "down";
        },
        _moveThingsAround: function () {
            var i, l, node, layer, n, w;
            // sort the layers by their grid position
            for (l = 0; l < this.layers.length; ++l) {
                layer = this.layers[l];
                layer.sort(this._gridPositionComparer);
            }

            this.minDistances = [];
            for (l = 0; l < this.layers.length; ++l) {
                layer = this.layers[l];
                this.minDistances[l] = [];
                for (n = 0; n < layer.length; ++n) {
                    node = layer[n];
                    node.layerIndex = n;
                    this.minDistances[l][n] = this.options.nodeDistance;
                    if (n < layer.length - 1) {
                        if (this._isVerticalLayout()) {
                            this.minDistances[l][n] += (node.width + layer[n + 1].width) / 2;
                        }
                        else {
                            this.minDistances[l][n] += (node.height + layer[n + 1].height) / 2;
                        }
                    }
                }
            }

            this.downNodes = new Dictionary();
            this.upNodes = new Dictionary();
            Utils.forEach(this.graph.nodes, function (node) {
                this.downNodes.set(node, []);
                this.upNodes.set(node, []);
            }, this);
            Utils.forEach(this.graph.links, function (link) {
                var origin = link.source;
                var dest = link.target;
                var down = null, up = null;
                if (origin.layer > dest.layer) {
                    down = link.source;
                    up = link.target;
                }
                else {
                    up = link.source;
                    down = link.target;
                }
                this.downNodes.get(up).push(down);
                this.upNodes.get(down).push(up);
            }, this);
            this.downNodes.forEachValue(function (list) {
                list.sort(this._gridPositionComparer);
            });
            this.upNodes.forEachValue(function (list) {
                list.sort(this._gridPositionComparer);
            });

            for (l = 0; l < this.layers.length - 1; ++l) {
                layer = this.layers[l];
                for (w = 0; w < layer.length - 1; w++) {
                    var currentNode = layer[w];
                    if (!currentNode.isVirtual) {
                        continue;
                    }

                    var currDown = this.downNodes.get(currentNode)[0];
                    if (!currDown.isVirtual) {
                        continue;
                    }

                    for (n = w + 1; n < layer.length; ++n) {
                        node = layer[n];
                        if (!node.isVirtual) {
                            continue;
                        }

                        var downNode = this.downNodes.get(node)[0];
                        if (!downNode.isVirtual) {
                            continue;
                        }

                        if (currDown.gridPosition > downNode.gridPosition) {
                            var pos = currDown.gridPosition;
                            currDown.gridPosition = downNode.gridPosition;
                            downNode.gridPosition = pos;
                            var i1 = currDown.layerIndex;
                            var i2 = downNode.layerIndex;
                            this.layers[l + 1][i1] = downNode;
                            this.layers[l + 1][i2] = currDown;
                            currDown.layerIndex = i2;
                            downNode.layerIndex = i1;
                        }
                    }
                }
            }


            var leftClasses = this._getLeftWing();
            var rightClasses = this._getRightWing();


            var leftPos = this.placeLeftToRight(leftClasses);
            var rightPos = this.placeRightToLeft(rightClasses);
            var x = new Dictionary();
            Utils.forEach(this.graph.nodes, function (node) {
                x.set(node, (leftPos.get(node) + rightPos.get(node)) / 2);
            });


            var order = new Dictionary();
            var placed = new Dictionary();
            for (l = 0; l < this.layers.length; ++l) {
                layer = this.layers[l];
                var sequenceStart = -1, sequenceEnd = -1;
                for (n = 0; n < layer.length; ++n) {
                    node = layer[n];
                    order.set(node, 0);
                    placed.set(node, false);
                    if (node.isVirtual) {
                        if (sequenceStart === -1) {
                            sequenceStart = n;
                        }
                        else if (sequenceStart === n - 1) {
                            sequenceStart = n;
                        }
                        else {
                            sequenceEnd = n;
                            order.set(layer[sequenceStart], 0);
                            if (x.get(node) - x.get(layer[sequenceStart]) === this.getMinDist(layer[sequenceStart], node)) {
                                placed.set(layer[sequenceStart], true);
                            }
                            else {
                                placed.set(layer[sequenceStart], false);
                            }
                            sequenceStart = n;
                        }
                    }
                }
            }
            var directions = [1, -1];
            Utils.forEach(directions, function (d) {
                var start = d === 1 ? 0 : this.layers.length - 1;
                for (var l = start; 0 <= l && l < this.layers.length; l += d) {
                    var layer = this.layers[l];
                    var virtualStartIndex = this._firstVirtualNode(layer);
                    var virtualStart = null;
                    var sequence = null;
                    if (virtualStartIndex !== -1) {
                        virtualStart = layer[virtualStartIndex];
                        sequence = [];
                        for (i = 0; i < virtualStartIndex; i++) {
                            sequence.push(layer[i]);
                        }
                    }
                    else {
                        virtualStart = null;
                        sequence = layer;
                    }
                    if (sequence.length > 0) {
                        this._sequencer(x, null, virtualStart, d, sequence);
                        for (i = 0; i < sequence.length - 1; ++i) {
                            this.setMinDist(sequence[i], sequence[i + 1], x.get(sequence[i + 1]) - x.get(sequence[i]));
                        }
                        if (virtualStart) {
                            this.setMinDist(sequence[sequence.length - 1], virtualStart, x.get(virtualStart) - x.get(sequence[sequence.length - 1]));
                        }
                    }

                    while (virtualStart) {
                        var virtualEnd = this.nextVirtualNode(layer, virtualStart);
                        if (!virtualEnd) {
                            virtualStartIndex = virtualStart.layerIndex;
                            sequence = [];
                            for (i = virtualStartIndex + 1; i < layer.length; i++) {
                                sequence.push(layer[i]);
                            }
                            if (sequence.length > 0) {
                                this._sequencer(x, virtualStart, null, d, sequence);
                                for (i = 0; i < sequence.length - 1; ++i) {
                                    this.setMinDist(sequence[i], sequence[i + 1], x.get(sequence[i + 1]) - x.get(sequence[i]));
                                }
                                this.setMinDist(virtualStart, sequence[0], x.get(sequence[0]) - x.get(virtualStart));
                            }
                        }
                        else if (order.get(virtualStart) === d) {
                            virtualStartIndex = virtualStart.layerIndex;
                            var virtualEndIndex = virtualEnd.layerIndex;
                            sequence = [];
                            for (i = virtualStartIndex + 1; i < virtualEndIndex; i++) {
                                sequence.push(layer[i]);
                            }
                            if (sequence.length > 0) {
                                this._sequencer(x, virtualStart, virtualEnd, d, sequence);
                            }
                            placed.set(virtualStart, true);
                        }
                        virtualStart = virtualEnd;
                    }
                    this.adjustDirections(l, d, order, placed);
                }
            }, this);


            var fromLayerIndex = this._isIncreasingLayout() ? 0 : this.layers.length - 1;
            var reachedFinalLayerIndex = function (k, ctx) {
                if (ctx._isIncreasingLayout()) {
                    return k < ctx.layers.length;
                }
                else {
                    return k >= 0;
                }
            };
            var layerIncrement = this._isIncreasingLayout() ? +1 : -1, offset = 0;

            /**
             * Calcs the max height of the given layer.
             */
            function maximumHeight(layer, ctx) {
                var height = Number.MIN_VALUE;
                for (var n = 0; n < layer.length; ++n) {
                    var node = layer[n];
                    if (ctx._isVerticalLayout()) {
                        height = Math.max(height, node.height);
                    }
                    else {
                        height = Math.max(height, node.width);
                    }
                }
                return height;
            }

            for (i = fromLayerIndex; reachedFinalLayerIndex(i, this); i += layerIncrement) {
                layer = this.layers[i];
                var height = maximumHeight(layer, this);

                for (n = 0; n < layer.length; ++n) {
                    node = layer[n];
                    if (this._isVerticalLayout()) {
                        node.x = x.get(node);
                        node.y = offset + height / 2;
                    }
                    else {
                        node.x = offset + height / 2;
                        node.y = x.get(node);
                    }
                }

                offset += this.options.layerSeparation + height;
            }
        },

        adjustDirections: function (l, d, order, placed) {
            if (l + d < 0 || l + d >= this.layers.length) {
                return;
            }

            var prevBridge = null, prevBridgeTarget = null;
            var layer = this.layers[l + d];
            for (var n = 0; n < layer.length; ++n) {
                var nextBridge = layer[n];
                if (nextBridge.isVirtual) {
                    var nextBridgeTarget = this.getNeighborOnLayer(nextBridge, l);
                    if (nextBridgeTarget.isVirtual) {
                        if (prevBridge) {
                            var p = placed.get(prevBridgeTarget);
                            var clayer = this.layers[l];
                            var i1 = prevBridgeTarget.layerIndex;
                            var i2 = nextBridgeTarget.layerIndex;
                            for (var i = i1 + 1; i < i2; ++i) {
                                if (clayer[i].isVirtual) {
                                    p = p && placed.get(clayer[i]);
                                }
                            }
                            if (p) {
                                order.set(prevBridge, d);
                                var j1 = prevBridge.layerIndex;
                                var j2 = nextBridge.layerIndex;
                                for (var j = j1 + 1; j < j2; ++j) {
                                    if (layer[j].isVirtual) {
                                        order.set(layer[j], d);
                                    }
                                }
                            }
                        }
                        prevBridge = nextBridge;
                        prevBridgeTarget = nextBridgeTarget;
                    }
                }
            }
        },

        getNeighborOnLayer: function (node, l) {
            var neighbor = this.upNodes.get(node)[0];
            if (neighbor.layer === l) {
                return neighbor;
            }
            neighbor = this.downNodes.get(node)[0];
            if (neighbor.layer === l) {
                return neighbor;
            }
            return null;
        },

        _sequencer: function (x, virtualStart, virtualEnd, dir, sequence) {
            if (sequence.length === 1) {
                this._sequenceSingle(x, virtualStart, virtualEnd, dir, sequence[0]);
            }

            if (sequence.length > 1) {
                var r = sequence.length, t = this.intDiv(r, 2);
                this._sequencer(x, virtualStart, virtualEnd, dir, sequence.slice(0, t));
                this._sequencer(x, virtualStart, virtualEnd, dir, sequence.slice(t));
                this.combineSequences(x, virtualStart, virtualEnd, dir, sequence);
            }
        },

        _sequenceSingle: function (x, virtualStart, virtualEnd, dir, node) {
            var neighbors = dir === -1 ? this.downNodes.get(node) : this.upNodes.get(node);

            var n = neighbors.length;
            if (n !== 0) {
                if (n % 2 === 1) {
                    x.set(node, x.get(neighbors[this.intDiv(n, 2)]));
                }
                else {
                    x.set(node, (x.get(neighbors[this.intDiv(n, 2) - 1]) + x.get(neighbors[this.intDiv(n, 2)])) / 2);
                }

                if (virtualStart) {
                    x.set(node, Math.max(x.get(node), x.get(virtualStart) + this.getMinDist(virtualStart, node)));
                }
                if (virtualEnd) {
                    x.set(node, Math.min(x.get(node), x.get(virtualEnd) - this.getMinDist(node, virtualEnd)));
                }
            }
        },

        combineSequences: function (x, virtualStart, virtualEnd, dir, sequence) {
            var r = sequence.length, t = this.intDiv(r, 2);

            // collect left changes
            var leftHeap = [], i, c, n, neighbors, neighbor, pair;
            for (i = 0; i < t; ++i) {
                c = 0;
                neighbors = dir === -1 ? this.downNodes.get(sequence[i]) : this.upNodes.get(sequence[i]);
                for (n = 0; n < neighbors.length; ++n) {
                    neighbor = neighbors[n];
                    if (x.get(neighbor) >= x.get(sequence[i])) {
                        c++;
                    }
                    else {
                        c--;
                        leftHeap.push({ k: x.get(neighbor) + this.getMinDist(sequence[i], sequence[t - 1]), v: 2 });
                    }
                }
                leftHeap.push({ k: x.get(sequence[i]) + this.getMinDist(sequence[i], sequence[t - 1]), v: c });
            }
            if (virtualStart) {
                leftHeap.push({ k: x.get(virtualStart) + this.getMinDist(virtualStart, sequence[t - 1]), v: Number.MAX_VALUE });
            }
            leftHeap.sort(this._positionDescendingComparer);

            // collect right changes
            var rightHeap = [];
            for (i = t; i < r; ++i) {
                c = 0;
                neighbors = dir === -1 ? this.downNodes.get(sequence[i]) : this.upNodes.get(sequence[i]);
                for (n = 0; n < neighbors.length; ++n) {
                    neighbor = neighbors[n];
                    if (x.get(neighbor) <= x.get(sequence[i])) {
                        c++;
                    }
                    else {
                        c--;
                        rightHeap.push({ k: x.get(neighbor) - this.getMinDist(sequence[i], sequence[t]), v: 2 });
                    }
                }
                rightHeap.push({ k: x.get(sequence[i]) - this.getMinDist(sequence[i], sequence[t]), v: c });
            }
            if (virtualEnd) {
                rightHeap.push({ k: x.get(virtualEnd) - this.getMinDist(virtualEnd, sequence[t]), v: Number.MAX_VALUE });
            }
            rightHeap.sort(this._positionAscendingComparer);

            var leftRes = 0, rightRes = 0;
            var m = this.getMinDist(sequence[t - 1], sequence[t]);
            while (x.get(sequence[t]) - x.get(sequence[t - 1]) < m) {
                if (leftRes < rightRes) {
                    if (leftHeap.length === 0) {
                        x.set(sequence[t - 1], x.get(sequence[t]) - m);
                        break;
                    }
                    else {
                        pair = leftHeap.shift();
                        leftRes = leftRes + pair.v;
                        x.set(sequence[t - 1], pair.k);
                        x.set(sequence[t - 1], Math.max(x.get(sequence[t - 1]), x.get(sequence[t]) - m));
                    }
                }
                else {
                    if (rightHeap.length === 0) {
                        x.set(sequence[t], x.get(sequence[t - 1]) + m);
                        break;
                    }
                    else {
                        pair = rightHeap.shift();
                        rightRes = rightRes + pair.v;
                        x.set(sequence[t], pair.k);
                        x.set(sequence[t], Math.min(x.get(sequence[t]), x.get(sequence[t - 1]) + m));
                    }
                }
            }
            for (i = t - 2; i >= 0; i--) {
                x.set(sequence[i], Math.min(x.get(sequence[i]), x.get(sequence[t - 1]) - this.getMinDist(sequence[i], sequence[t - 1])));
            }
            for (i = t + 1; i < r; i++) {
                x.set(sequence[i], Math.max(x.get(sequence[i]), x.get(sequence[t]) + this.getMinDist(sequence[i], sequence[t])));
            }
        },

        placeLeft: function (node, leftPos, leftClass) {
            var pos = Number.NEGATIVE_INFINITY;
            Utils.forEach(this._getComposite(node), function (v) {
                var leftSibling = this.leftSibling(v);
                if (leftSibling && this.nodeLeftClass.get(leftSibling) === this.nodeLeftClass.get(v)) {
                    if (!leftPos.containsKey(leftSibling)) {
                        this.placeLeft(leftSibling, leftPos, leftClass);
                    }
                    pos = Math.max(pos, leftPos.get(leftSibling) + this.getMinDist(leftSibling, v));
                }
            }, this);
            if (pos === Number.NEGATIVE_INFINITY) {
                pos = 0;
            }
            Utils.forEach(this._getComposite(node), function (v) {
                leftPos.set(v, pos);
            });
        },

        placeRight: function (node, rightPos, rightClass) {
            var pos = Number.POSITIVE_INFINITY;
            Utils.forEach(this._getComposite(node), function (v) {
                var rightSibling = this.rightSibling(v);
                if (rightSibling && this.nodeRightClass.get(rightSibling) === this.nodeRightClass.get(v)) {
                    if (!rightPos.containsKey(rightSibling)) {
                        this.placeRight(rightSibling, rightPos, rightClass);
                    }
                    pos = Math.min(pos, rightPos.get(rightSibling) - this.getMinDist(v, rightSibling));
                }
            }, this);
            if (pos === Number.POSITIVE_INFINITY) {
                pos = 0;
            }
            Utils.forEach(this._getComposite(node), function (v) {
                rightPos.set(v, pos);
            });
        },

        leftSibling: function (node) {
            var layer = this.layers[node.layer],
                layerIndex = node.layerIndex;
            return layerIndex === 0 ? null : layer[layerIndex - 1];
        },

        rightSibling: function (node) {
            var layer = this.layers[node.layer];
            var layerIndex = node.layerIndex;
            return layerIndex === layer.length - 1 ? null : layer[layerIndex + 1];

        },

        _getComposite: function (node) {
            return node.isVirtual ? this._nodesInLink(node) : [node];
        },

        arrangeNodes: function () {
            var i, l, ni, layer, node;
            // Initialize node's base priority
            for (l = 0; l < this.layers.length; l++) {
                layer = this.layers[l];

                for (ni = 0; ni < layer.length; ni++) {
                    node = layer[ni];
                    node.upstreamPriority = node.upstreamLinkCount;
                    node.downstreamPriority = node.downstreamLinkCount;
                }
            }

            // Layout is invoked after MinimizeCrossings
            // so we may assume node's barycenters are initially correct

            var maxLayoutIterations = 2;
            for (var it = 0; it < maxLayoutIterations; it++) {
                for (i = this.layers.length - 1; i >= 1; i--) {
                    this.layoutLayer(false, i);
                }

                for (i = 0; i < this.layers.length - 1; i++) {
                    this.layoutLayer(true, i);
                }
            }

            // Offset the whole structure so that there are no gridPositions < 0
            var gridPos = Number.MAX_VALUE;
            for (l = 0; l < this.layers.length; l++) {
                layer = this.layers[l];

                for (ni = 0; ni < layer.length; ni++) {
                    node = layer[ni];
                    gridPos = Math.min(gridPos, node.gridPosition);
                }
            }

            if (gridPos < 0) {
                for (l = 0; l < this.layers.length; l++) {
                    layer = this.layers[l];

                    for (ni = 0; ni < layer.length; ni++) {
                        node = layer[ni];
                        node.gridPosition = node.gridPosition - gridPos;
                    }
                }
            }
        },

        /// <summary>
        /// Layout of a single layer.
        /// </summary>
        /// <param name="layerIndex">The layer to organize.</param>
        /// <param name="movingDownwards">If set to <c>true</c> we move down in the layer stack.</param>
        /// <seealso cref="OptimizeCrossings()"/>
        layoutLayer: function (down, layer) {
            var iconsidered;
            var considered;

            if (down) {
                considered = this.layers[iconsidered = layer + 1];
            }
            else {
                considered = this.layers[iconsidered = layer - 1];
            }

            // list containing the nodes in the considered layer sorted by priority
            var sorted = [];
            for (var n = 0; n < considered.length; n++) {
                sorted.push(considered[n]);
            }
            sorted.sort(function (n1, n2) {
                var n1Priority = (n1.upstreamPriority + n1.downstreamPriority) / 2;
                var n2Priority = (n2.upstreamPriority + n2.downstreamPriority) / 2;

                if (Math.abs(n1Priority - n2Priority) < 0.0001) {
                    return 0;
                }
                if (n1Priority < n2Priority) {
                    return 1;
                }
                return -1;
            });

            // each node strives for its barycenter; high priority nodes start first
            Utils.forEach(sorted, function (node) {
                var nodeGridPos = node.gridPosition;
                var nodeBaryCenter = this.calcBaryCenter(node);
                var nodePriority = (node.upstreamPriority + node.downstreamPriority) / 2;

                if (Math.abs(nodeGridPos - nodeBaryCenter) < 0.0001) {
                    // This node is exactly at its barycenter -> perfect
                    return;
                }

                if (Math.abs(nodeGridPos - nodeBaryCenter) < 0.25 + 0.0001) {
                    // This node is close enough to the barycenter -> should work
                    return;
                }

                if (nodeGridPos < nodeBaryCenter) {
                    // Try to move the node to the right in an
                    // attempt to reach its barycenter
                    while (nodeGridPos < nodeBaryCenter) {
                        if (!this.moveRight(node, considered, nodePriority)) {
                            break;
                        }

                        nodeGridPos = node.gridPosition;
                    }
                }
                else {
                    // Try to move the node to the left in an
                    // attempt to reach its barycenter
                    while (nodeGridPos > nodeBaryCenter) {
                        if (!this.moveLeft(node, considered, nodePriority)) {
                            break;
                        }

                        nodeGridPos = node.gridPosition;
                    }
                }
            }, this);

            // after the layer has been rearranged we need to recalculate the barycenters
            // of the nodes in the surrounding layers
            if (iconsidered > 0) {
                this.calcDownData(iconsidered - 1);
            }
            if (iconsidered < this.layers.length - 1) {
                this.calcUpData(iconsidered + 1);
            }
        },

        /// <summary>
        /// Moves the node to the right and returns <c>true</c> if this was possible.
        /// </summary>
        /// <param name="node">The node.</param>
        /// <param name="layer">The layer.</param>
        /// <returns>Returns <c>true</c> if the shift was possible, otherwise <c>false</c>.</returns>
        moveRight: function (node, layer, priority) {
            var index = Utils.indexOf(layer, node);
            if (index === layer.length - 1) {
                // this is the last node in the layer, so we can move to the right without troubles
                node.gridPosition = node.gridPosition + 0.5;
                return true;
            }

            var rightNode = layer[index + 1];
            var rightNodePriority = (rightNode.upstreamPriority + rightNode.downstreamPriority) / 2;

            // check if there is space between the right and the current node
            if (rightNode.gridPosition > node.gridPosition + 1) {
                node.gridPosition = node.gridPosition + 0.5;
                return true;
            }

            // we have reached a node with higher priority; no movement is allowed
            if (rightNodePriority > priority ||
                Math.abs(rightNodePriority - priority) < 0.0001) {
                return false;
            }

            // the right node has lower priority - try to move it
            if (this.moveRight(rightNode, layer, priority)) {
                node.gridPosition = node.gridPosition + 0.5;
                return true;
            }

            return false;
        },

        /// <summary>
        /// Moves the node to the left and returns <c>true</c> if this was possible.
        /// </summary>
        /// <param name="node">The node.</param>
        /// <param name="layer">The layer.</param>
        /// <returns>Returns <c>true</c> if the shift was possible, otherwise <c>false</c>.</returns>
        moveLeft: function (node, layer, priority) {
            var index = Utils.indexOf(layer, node);
            if (index === 0) {
                // this is the last node in the layer, so we can move to the left without troubles
                node.gridPosition = node.gridPosition - 0.5;
                return true;
            }

            var leftNode = layer[index - 1];
            var leftNodePriority = (leftNode.upstreamPriority + leftNode.downstreamPriority) / 2;

            // check if there is space between the left and the current node
            if (leftNode.gridPosition < node.gridPosition - 1) {
                node.gridPosition = node.gridPosition - 0.5;
                return true;
            }

            // we have reached a node with higher priority; no movement is allowed
            if (leftNodePriority > priority ||
                Math.abs(leftNodePriority - priority) < 0.0001) {
                return false;
            }

            // The left node has lower priority - try to move it
            if (this.moveLeft(leftNode, layer, priority)) {
                node.gridPosition = node.gridPosition - 0.5;
                return true;
            }

            return false;
        },

        mapVirtualNode: function (node, link) {
            this.nodeToLinkMap.set(node, link);
            if (!this.linkToNodeMap.containsKey(link)) {
                this.linkToNodeMap.set(link, []);
            }
            this.linkToNodeMap.get(link).push(node);
        },

        _nodesInLink: function (node) {
            return this.linkToNodeMap.get(this.nodeToLinkMap.get(node));
        },

        /// <summary>
        /// Inserts dummy nodes to break long links.
        /// </summary>
        _dummify: function () {
            this.linkToNodeMap = new Dictionary();
            this.nodeToLinkMap = new Dictionary();

            var layer, pos, newNode, node, r, newLink, i, l, links = this.graph.links.slice(0);
            for (l = 0; l < links.length; l++) {
                var link = links[l];
                var o = link.source;
                var d = link.target;

                var oLayer = o.layer;
                var dLayer = d.layer;
                var oPos = o.gridPosition;
                var dPos = d.gridPosition;

                var step = (dPos - oPos) / Math.abs(dLayer - oLayer);

                var p = o;
                if (oLayer - dLayer > 1) {
                    for (i = oLayer - 1; i > dLayer; i--) {
                        newNode = new Node();
                        newNode.x = o.x;
                        newNode.y = o.y;
                        newNode.width = o.width / 100;
                        newNode.height = o.height / 100;

                        layer = this.layers[i];
                        pos = (i - dLayer) * step + oPos;
                        if (pos > layer.length) {
                            pos = layer.length;
                        }

                        // check if origin and dest are both last
                        if (oPos >= this.layers[oLayer].length - 1 &&
                            dPos >= this.layers[dLayer].length - 1) {
                            pos = layer.length;
                        }

                        // check if origin and destination are both first
                        else if (oPos === 0 && dPos === 0) {
                            pos = 0;
                        }

                        newNode.layer = i;
                        newNode.uBaryCenter = 0.0;
                        newNode.dBaryCenter = 0.0;
                        newNode.upstreamLinkCount = 0;
                        newNode.downstreamLinkCount = 0;
                        newNode.gridPosition = pos;
                        newNode.isVirtual = true;

                        Utils.insert(layer, newNode, pos);

                        // translate rightwards nodes' positions
                        for (r = pos + 1; r < layer.length; r++) {
                            node = layer[r];
                            node.gridPosition = node.gridPosition + 1;
                        }

                        newLink = new Link(p, newNode);
                        newLink.depthOfDumminess = 0;
                        p = newNode;

                        // add the new node and the new link to the graph
                        this.graph.nodes.push(newNode);
                        this.graph.addLink(newLink);

                        newNode.index = this.graph.nodes.length - 1;
                        this.mapVirtualNode(newNode, link);
                    }

                    // set the origin of the real arrow to the last dummy
                    link.changeSource(p);
                    link.depthOfDumminess = oLayer - dLayer - 1;
                }

                if (oLayer - dLayer < -1) {
                    for (i = oLayer + 1; i < dLayer; i++) {
                        newNode = new Node();
                        newNode.x = o.x;
                        newNode.y = o.y;
                        newNode.width = o.width / 100;
                        newNode.height = o.height / 100;

                        layer = this.layers[i];
                        pos = (i - oLayer) * step + oPos;
                        if (pos > layer.length) {
                            pos = layer.length;
                        }

                        // check if origin and dest are both last
                        if (oPos >= this.layers[oLayer].length - 1 &&
                            dPos >= this.layers[dLayer].length - 1) {
                            pos = layer.length;
                        }

                        // check if origin and destination are both first
                        else if (oPos === 0 && dPos === 0) {
                            pos = 0;
                        }

                        newNode.layer = i;
                        newNode.uBaryCenter = 0.0;
                        newNode.dBaryCenter = 0.0;
                        newNode.upstreamLinkCount = 0;
                        newNode.downstreamLinkCount = 0;
                        newNode.gridPosition = pos;
                        newNode.isVirtual = true;

                        pos &= pos; // truncates to int
                        Utils.insert(layer, newNode, pos);

                        // translate rightwards nodes' positions
                        for (r = pos + 1; r < layer.length; r++) {
                            node = layer[r];
                            node.gridPosition = node.gridPosition + 1;
                        }

                        newLink = new Link(p, newNode);
                        newLink.depthOfDumminess = 0;
                        p = newNode;

                        // add the new node and the new link to the graph
                        this.graph.nodes.push(newNode);
                        this.graph.addLink(newLink);

                        newNode.index = this.graph.nodes.length - 1;
                        this.mapVirtualNode(newNode, link);
                    }

                    // Set the origin of the real arrow to the last dummy
                    link.changeSource(p);
                    link.depthOfDumminess = dLayer - oLayer - 1;
                }
            }
        },

        /// <summary>
        /// Removes the dummy nodes inserted earlier to break long links.
        /// </summary>
        /// <remarks>The virtual nodes are effectively turned into intermediate connection points.</remarks>
        _dedummify: function () {
            var dedum = true;
            while (dedum) {
                dedum = false;

                for (var l = 0; l < this.graph.links.length; l++) {
                    var link = this.graph.links[l];
                    if (link.depthOfDumminess === 0) {
                        continue;
                    }

                    var points = [];

                    // add points in reverse order
                    points.unshift({ x: link.target.x, y: link.target.y });
                    points.unshift({ x: link.source.x, y: link.source.y });

                    // _dedummify the link
                    var temp = link;
                    var depthOfDumminess = link.depthOfDumminess;
                    for (var d = 0; d < depthOfDumminess; d++) {
                        var node = temp.source;
                        var prevLink = node.incoming[0];

                        points.unshift({ x: prevLink.source.x, y: prevLink.source.y });

                        temp = prevLink;
                    }

                    // restore the original link origin
                    link.changeSource(temp.source);

                    // reset dummification flag
                    link.depthOfDumminess = 0;

                    // note that we only need the intermediate points, floating links have been dropped in the analysis
                    if (points.length > 2) {
                        // first and last are the endpoints
                        points.splice(0, 1);
                        points.splice(points.length - 1);
                        link.points = points;
                    }
                    else {
                        link.points = [];
                    }

                    // we are not going to delete the dummy elements;
                    // they won't be needed anymore anyway.

                    dedum = true;
                    break;
                }
            }
        },

        /// <summary>
        /// Optimizes/reduces the crossings between the layers by turning the crossing problem into a (combinatorial) number ordering problem.
        /// </summary>
        _optimizeCrossings: function () {
            var moves = -1, i;
            var maxIterations = 3;
            var iter = 0;

            while (moves !== 0) {
                if (iter++ > maxIterations) {
                    break;
                }

                moves = 0;

                for (i = this.layers.length - 1; i >= 1; i--) {
                    moves += this.optimizeLayerCrossings(false, i);
                }

                for (i = 0; i < this.layers.length - 1; i++) {
                    moves += this.optimizeLayerCrossings(true, i);
                }
            }
        },

        calcUpData: function (layer) {
            if (layer === 0) {
                return;
            }

            var considered = this.layers[layer], i, l, link;
            var upLayer = new Set();
            var temp = this.layers[layer - 1];
            for (i = 0; i < temp.length; i++) {
                upLayer.add(temp[i]);
            }

            for (i = 0; i < considered.length; i++) {
                var node = considered[i];

                // calculate barycenter
                var sum = 0;
                var total = 0;

                for (l = 0; l < node.incoming.length; l++) {
                    link = node.incoming[l];
                    if (upLayer.contains(link.source)) {
                        total++;
                        sum += link.source.gridPosition;
                    }
                }

                for (l = 0; l < node.outgoing.length; l++) {
                    link = node.outgoing[l];
                    if (upLayer.contains(link.target)) {
                        total++;
                        sum += link.target.gridPosition;
                    }
                }

                if (total > 0) {
                    node.uBaryCenter = sum / total;
                    node.upstreamLinkCount = total;
                }
                else {
                    node.uBaryCenter = i;
                    node.upstreamLinkCount = 0;
                }
            }
        },

        calcDownData: function (layer) {
            if (layer === this.layers.length - 1) {
                return;
            }

            var considered = this.layers[layer], i , l, link;
            var downLayer = new Set();
            var temp = this.layers[layer + 1];
            for (i = 0; i < temp.length; i++) {
                downLayer.add(temp[i]);
            }

            for (i = 0; i < considered.length; i++) {
                var node = considered[i];

                // calculate barycenter
                var sum = 0;
                var total = 0;

                for (l = 0; l < node.incoming.length; l++) {
                    link = node.incoming[l];
                    if (downLayer.contains(link.source)) {
                        total++;
                        sum += link.source.gridPosition;
                    }
                }

                for (l = 0; l < node.outgoing.length; l++) {
                    link = node.outgoing[l];
                    if (downLayer.contains(link.target)) {
                        total++;
                        sum += link.target.gridPosition;
                    }
                }

                if (total > 0) {
                    node.dBaryCenter = sum / total;
                    node.downstreamLinkCount = total;
                }
                else {
                    node.dBaryCenter = i;
                    node.downstreamLinkCount = 0;
                }
            }
        },

        /// <summary>
        /// Optimizes the crossings.
        /// </summary>
        /// <remarks>The big trick here is the usage of weights or values attached to connected nodes which turn a problem of crossing links
        /// to an a problem of ordering numbers.</remarks>
        /// <param name="layerIndex">The layer index.</param>
        /// <param name="movingDownwards">If set to <c>true</c> we move down in the layer stack.</param>
        /// <returns>The number of nodes having moved, i.e. the number of crossings reduced.</returns>
        optimizeLayerCrossings: function (down, layer) {
            var iconsidered;
            var considered;

            if (down) {
                considered = this.layers[iconsidered = layer + 1];
            }
            else {
                considered = this.layers[iconsidered = layer - 1];
            }

            // remember what it was
            var presorted = considered.slice(0);

            // calculate barycenters for all nodes in the considered layer
            if (down) {
                this.calcUpData(iconsidered);
            }
            else {
                this.calcDownData(iconsidered);
            }

            var that = this;
            // sort nodes within this layer according to the barycenters
            considered.sort(function(n1, n2) {
                var n1BaryCenter = that.calcBaryCenter(n1),
                    n2BaryCenter = that.calcBaryCenter(n2);
                if (Math.abs(n1BaryCenter - n2BaryCenter) < 0.0001) {
                    // in case of coinciding barycenters compare by the count of in/out links
                    if (n1.degree() === n2.degree()) {
                        return that.compareByIndex(n1, n2);
                    }
                    else if (n1.degree() < n2.degree()) {
                        return 1;
                    }
                    return -1;
                }
                var compareValue = (n2BaryCenter - n1BaryCenter) * 1000;
                if (compareValue > 0) {
                    return -1;
                }
                else if (compareValue < 0) {
                    return 1;
                }
                return that.compareByIndex(n1, n2);
            });

            // count relocations
            var i, moves = 0;
            for (i = 0; i < considered.length; i++) {
                if (considered[i] !== presorted[i]) {
                    moves++;
                }
            }

            if (moves > 0) {
                // now that the boxes have been arranged, update their grid positions
                var inode = 0;
                for (i = 0; i < considered.length; i++) {
                    var node = considered[i];
                    node.gridPosition = inode++;
                }
            }

            return moves;
        },

        /// <summary>
        /// Swaps a pair of nodes in a layer.
        /// </summary>
        /// <param name="layerIndex">Index of the layer.</param>
        /// <param name="n">The Nth node in the layer.</param>
        _swapPairs: function () {
            var maxIterations = this.options.layeredIterations;
            var iter = 0;

            while (true) {
                if (iter++ > maxIterations) {
                    break;
                }

                var downwards = (iter % 4 <= 1);
                var secondPass = (iter % 4 === 1);

                for (var l = (downwards ? 0 : this.layers.length - 1);
                     downwards ? l <= this.layers.length - 1 : l >= 0; l += (downwards ? 1 : -1)) {
                    var layer = this.layers[l];
                    var hasSwapped = false;

                    // there is no need to recalculate crossings if they were calculated
                    // on the previous step and nothing has changed
                    var calcCrossings = true;
                    var memCrossings = 0;

                    for (var n = 0; n < layer.length - 1; n++) {
                        // count crossings
                        var up = 0;
                        var down = 0;
                        var crossBefore = 0;

                        if (calcCrossings) {
                            if (l !== 0) {
                                up = this.countLinksCrossingBetweenTwoLayers(l - 1, l);
                            }
                            if (l !== this.layers.length - 1) {
                                down = this.countLinksCrossingBetweenTwoLayers(l, l + 1);
                            }
                            if (downwards) {
                                up *= 2;
                            }
                            else {
                                down *= 2;
                            }

                            crossBefore = up + down;
                        }
                        else {
                            crossBefore = memCrossings;
                        }

                        if (crossBefore === 0) {
                            continue;
                        }

                        // Swap nodes
                        var node1 = layer[n];
                        var node2 = layer[n + 1];

                        var node1GridPos = node1.gridPosition;
                        var node2GridPos = node2.gridPosition;
                        layer[n] = node2;
                        layer[n + 1] = node1;
                        node1.gridPosition = node2GridPos;
                        node2.gridPosition = node1GridPos;

                        // count crossings again and if worse than before, restore swapping
                        up = 0;
                        if (l !== 0) {
                            up = this.countLinksCrossingBetweenTwoLayers(l - 1, l);
                        }
                        down = 0;
                        if (l !== this.layers.length - 1) {
                            down = this.countLinksCrossingBetweenTwoLayers(l, l + 1);
                        }
                        if (downwards) {
                            up *= 2;
                        }
                        else {
                            down *= 2;
                        }
                        var crossAfter = up + down;

                        var revert = false;
                        if (secondPass) {
                            revert = crossAfter >= crossBefore;
                        }
                        else {
                            revert = crossAfter > crossBefore;
                        }

                        if (revert) {
                            node1 = layer[n];
                            node2 = layer[n + 1];

                            node1GridPos = node1.gridPosition;
                            node2GridPos = node2.gridPosition;
                            layer[n] = node2;
                            layer[n + 1] = node1;
                            node1.gridPosition = node2GridPos;
                            node2.gridPosition = node1GridPos;

                            // nothing has changed, remember the crossings so that
                            // they are not calculated again on the next step
                            memCrossings = crossBefore;
                            calcCrossings = false;
                        }
                        else {
                            hasSwapped = true;
                            calcCrossings = true;
                        }
                    }

                    if (hasSwapped) {
                        if (l !== this.layers.length - 1) {
                            this.calcUpData(l + 1);
                        }
                        if (l !== 0) {
                            this.calcDownData(l - 1);
                        }
                    }
                }
            }
        },

        /// <summary>
        /// Counts the number of links crossing between two layers.
        /// </summary>
        /// <param name="layerIndex1">The layer index.</param>
        /// <param name="layerIndex2">Another layer index.</param>
        /// <returns></returns>
        countLinksCrossingBetweenTwoLayers: function (ulayer, dlayer) {
            var i, crossings = 0;

            var upperLayer = new Set();
            var temp1 = this.layers[ulayer];
            for (i = 0; i < temp1.length; i++) {
                upperLayer.add(temp1[i]);
            }

            var lowerLayer = new Set();
            var temp2 = this.layers[dlayer];
            for (i = 0; i < temp2.length; i++) {
                lowerLayer.add(temp2[i]);
            }

            // collect the links located between the layers
            var dlinks = new Set();
            var links = [];
            var temp = [];

            upperLayer.forEach(function (node) {
                //throw "";
                Utils.addRange(temp, node.incoming);
                Utils.addRange(temp, node.outgoing);
            });

            for (var ti = 0; ti < temp.length; ti++) {
                var link = temp[ti];

                if (upperLayer.contains(link.source) &&
                    lowerLayer.contains(link.target)) {
                    dlinks.add(link);
                    links.push(link);
                }
                else if (lowerLayer.contains(link.source) &&
                    upperLayer.contains(link.target)) {
                    links.push(link);
                }
            }

            for (var l1 = 0; l1 < links.length; l1++) {
                var link1 = links[l1];
                for (var l2 = 0; l2 < links.length; l2++) {
                    if (l1 === l2) {
                        continue;
                    }

                    var link2 = links[l2];

                    var n11, n12;
                    var n21, n22;

                    if (dlinks.contains(link1)) {
                        n11 = link1.source;
                        n12 = link1.target;
                    }
                    else {
                        n11 = link1.target;
                        n12 = link1.source;
                    }

                    if (dlinks.contains(link2)) {
                        n21 = link2.source;
                        n22 = link2.target;
                    }
                    else {
                        n21 = link2.target;
                        n22 = link2.source;
                    }

                    var n11gp = n11.gridPosition;
                    var n12gp = n12.gridPosition;
                    var n21gp = n21.gridPosition;
                    var n22gp = n22.gridPosition;

                    if ((n11gp - n21gp) * (n12gp - n22gp) < 0) {
                        crossings++;
                    }
                }
            }

            return crossings / 2;
        },

        calcBaryCenter: function (node) {
            var upstreamLinkCount = node.upstreamLinkCount;
            var downstreamLinkCount = node.downstreamLinkCount;
            var uBaryCenter = node.uBaryCenter;
            var dBaryCenter = node.dBaryCenter;

            if (upstreamLinkCount > 0 && downstreamLinkCount > 0) {
                return (uBaryCenter + dBaryCenter) / 2;
            }
            if (upstreamLinkCount > 0) {
                return uBaryCenter;
            }
            if (downstreamLinkCount > 0) {
                return dBaryCenter;
            }

            return 0;
        },

        _gridPositionComparer: function (x, y) {
            if (x.gridPosition < y.gridPosition) {
                return -1;
            }
            if (x.gridPosition > y.gridPosition) {
                return 1;
            }
            return 0;
        },

        _positionAscendingComparer: function (x, y) {
            return x.k < y.k ? -1 : x.k > y.k ? 1 : 0;
        },

        _positionDescendingComparer: function (x, y) {
            return x.k < y.k ? 1 : x.k > y.k ? -1 : 0;
        },

        _firstVirtualNode: function (layer) {
            for (var c = 0; c < layer.length; c++) {
                if (layer[c].isVirtual) {
                    return c;
                }
            }
            return -1;
        },

        compareByIndex: function (o1, o2) {
            var i1 = o1.index;
            var i2 = o2.index;

            if (i1 < i2) {
                return 1;
            }

            if (i1 > i2) {
                return -1;
            }

            return 0;
        },

        intDiv: function (numerator, denominator) {
            return (numerator - numerator % denominator) / denominator;
        },

        nextVirtualNode: function (layer, node) {
            var nodeIndex = node.layerIndex;
            for (var i = nodeIndex + 1; i < layer.length; ++i) {
                if (layer[i].isVirtual) {
                    return layer[i];
                }
            }
            return null;
        }

    });

    /**
     * Captures the state of a diagram; node positions, link points and so on.
     * @type {*}
     */
    var LayoutState = kendo.Class.extend({
        init: function (diagram, graphOrNodes) {
            if (Utils.isUndefined(diagram)) {
                throw "No diagram given";
            }
            this.diagram = diagram;
            this.nodeMap = new Dictionary();
            this.linkMap = new Dictionary();
            this.capture(graphOrNodes ? graphOrNodes : diagram);
        },

        /**
         * Will capture either
         * - the state of the shapes and the intermediate points of the connections in the diagram
         * - the bounds of the nodes contained in the Graph together with the intermediate points of the links in the Graph
         * - the bounds of the nodes in the Array<Node>
         * - the links points and node bounds in the literal object
         * @param diagramOrGraphOrNodes
         */
        capture: function (diagramOrGraphOrNodes) {
            var node,
                nodes,
                shape,
                i,
                conn,
                link,
                links;

            if (diagramOrGraphOrNodes instanceof diagram.Graph) {

                for (i = 0; i < diagramOrGraphOrNodes.nodes.length; i++) {
                    node = diagramOrGraphOrNodes.nodes[i];
                    shape = node.associatedShape;
                    //shape.bounds(new Rect(node.x, node.y, node.width, node.height));
                    this.nodeMap.set(shape.visual.domElement.id, new Rect(node.x, node.y, node.width, node.height));
                }
                for (i = 0; i < diagramOrGraphOrNodes.links.length; i++) {
                    link = diagramOrGraphOrNodes.links[i];
                    conn = link.associatedConnection;
                    this.linkMap.set(conn.visual.domElement.id, link.points());
                }
            }
            else if (diagramOrGraphOrNodes instanceof Array) {
                nodes = diagramOrGraphOrNodes;
                for (i = 0; i < nodes.length; i++) {
                    node = nodes[i];
                    shape = node.associatedShape;
                    if (shape) {
                        this.nodeMap.set(shape.visual.domElement.id, new Rect(node.x, node.y, node.width, node.height));
                    }
                }
            }
            else if (diagramOrGraphOrNodes.hasOwnProperty("links") && diagramOrGraphOrNodes.hasOwnProperty("nodes")) {
                nodes = diagramOrGraphOrNodes.nodes;
                links = diagramOrGraphOrNodes.links;
                for (i = 0; i < nodes.length; i++) {
                    node = nodes[i];
                    shape = node.associatedShape;
                    if (shape) {
                        this.nodeMap.set(shape.visual.domElement.id, new Rect(node.x, node.y, node.width, node.height));
                    }
                }
                for (i = 0; i < links.length; i++) {
                    link = links[i];
                    conn = link.associatedConnection;
                    if (conn) {
                        this.linkMap.set(conn.visual.domElement.id, link.points);
                    }
                }
            }
            else { // capture the diagram
                var shapes = this.diagram.shapes;
                var connections = this.diagram.connections;
                for (i = 0; i < shapes.length; i++) {
                    shape = shapes[i];
                    this.nodeMap.set(shape.visual.domElement.id, shape.bounds());
                }
                for (i = 0; i < connections.length; i++) {
                    conn = connections[i];
                    this.linkMap.set(conn.visual.domElement.id, conn.points());
                }
            }
        }
    });

    deepExtend(diagram, {
        init: function (element) {
            kendo.init(element, diagram.ui);
        },
        SpringLayout: SpringLayout,
        TreeLayout: TreeLayout,
        GraphAdapter: DiagramToHyperTreeAdapter,
        LayeredLayout: LayeredLayout,
        LayoutBase: LayoutBase,
        LayoutState: LayoutState
    });
})(window.kendo.jQuery);

(function ($, undefined) {
        // Imports ================================================================
        var dataviz = kendo.dataviz,
            diagram = dataviz.diagram,
            ui = dataviz.ui,
            Widget = kendo.ui.Widget,
            Class = kendo.Class,
            proxy = $.proxy,
            deepExtend = kendo.deepExtend,
            HierarchicalDataSource = kendo.data.HierarchicalDataSource,
            Canvas = diagram.Canvas,
            Group = diagram.Group,
            Visual = diagram.Visual,
            Rectangle = diagram.Rectangle,
            Circle = diagram.Circle,
            CompositeTransform = diagram.CompositeTransform,
            Rect = diagram.Rect,
            Path = diagram.Path,
            DeleteShapeUnit = diagram.DeleteShapeUnit,
            DeleteConnectionUnit = diagram.DeleteConnectionUnit,
            TextBlock = diagram.TextBlock,
            Image = diagram.Image,
            Point = diagram.Point,
            Intersect = diagram.Intersect,
            ConnectionEditAdorner = diagram.ConnectionEditAdorner,
            UndoRedoService = diagram.UndoRedoService,
            ToolService = diagram.ToolService,
            Selector = diagram.Selector,
            ResizingAdorner = diagram.ResizingAdorner,
            ConnectorsAdorner = diagram.ConnectorsAdorner,
            Cursors = diagram.Cursors,
            Utils = diagram.Utils,
            Observable = kendo.Observable,
            Ticker = diagram.Ticker,
            ToBackUnit = diagram.ToBackUnit,
            ToFrontUnit = diagram.ToFrontUnit,
            Dictionary = diagram.Dictionary,
            PolylineRouter = diagram.PolylineRouter,
            CascadingRouter = diagram.CascadingRouter,
            isUndefined = Utils.isUndefined,
            isDefined = Utils.isDefined,
            isArray = $.isArray,
            isFunction = kendo.isFunction,
            isString = Utils.isString;

        // Constants ==============================================================
        var NS = ".kendoDiagram",
            CASCADING = "Cascading",
            POLYLINE = "Polyline",
            ITEMBOUNDSCHANGE = "itemBoundsChange",
            CHANGE = "change",
            CLICK = "click",
            ERROR = "error",
            AUTO = "Auto",
            TOP = "Top",
            RIGHT = "Right",
            LEFT = "Left",
            BOTTOM = "Bottom",
            MAXINT = 9007199254740992,
            SELECT = "select",
            ITEMROTATE = "itemRotate",
            PAN = "pan",
            ZOOM_START = "zoomStart",
            ZOOM_END = "zoomEnd",
            CONNECTION_CSS = "k-connection",
            SHAPE_CSS = "k-shape",
            SINGLE = "single",
            NONE = "none",
            MULTIPLE = "multiple",
            DEFAULT_SHAPE_TYPE = "rectangle",
            DEFAULT_SHAPE_WIDTH = 100,
            DEFAULT_SHAPE_HEIGHT = 100,
            DEFAULT_SHAPE_MINWIDTH = 20,
            DEFAULT_SHAPE_MINHEIGHT = 20,
            DEFAULT_SHAPE_POSITION = 0,
            DEFAULT_SHAPE_BACKGROUND = "SteelBlue",
            DEFAULT_CONNECTION_BACKGROUND = "Yellow",
            DEFAULT_CONNECTOR_SIZE = 8,
            DEFAULT_HOVER_COLOR = "#70CAFF",
            ALL = "all",
            ABSOLUTE = "absolute",
            TRANSFORMED = "transformed",
            ROTATED = "rotated";

        diagram.DefaultConnectors = [{
            name: TOP,
            description: "Top Connector"
        }, {
            name: RIGHT,
            description: "Right Connector"
        }, {
            name: BOTTOM,
            description: "Bottom Connector"
        }, {
            name: LEFT,
            Description: "Left Connector"
        }, {
            name: AUTO,
            Description: "Auto Connector",
            position: function (shape) {
                return shape.getPosition("center");
            }
        }];

        diagram.shapeDefaults = function(extra) {
            var defaults = {
                type: DEFAULT_SHAPE_TYPE,
                path: "",
                visual: null,
                x: DEFAULT_SHAPE_POSITION,
                y: DEFAULT_SHAPE_POSITION,
                minWidth: DEFAULT_SHAPE_MINWIDTH,
                minHeight: DEFAULT_SHAPE_MINHEIGHT,
                width: DEFAULT_SHAPE_WIDTH,
                height: DEFAULT_SHAPE_HEIGHT,
                hover: {},
                connectors: diagram.DefaultConnectors,
                rotation: {
                    angle: 0
                }
            };

            Utils.simpleExtend(defaults, extra);

            return defaults;
        };

        var PanAdapter = kendo.Class.extend({
            init: function (panState) {
                this.pan = panState.pan;
                this.diagram = panState.diagram;
            },
            initState: function () {
                this.from = this.diagram.pan();
                this.to = this.pan;
            },
            update: function (tick) {
                var diagram = this.diagram;
                diagram._storePan(new Point(this.from.x + (this.to.x - this.from.x) * tick, this.from.y + (this.to.y - this.from.y) * tick));
                diagram._transformMainLayer();
            }
        });

        function isAutoConnector(connector) {
            return connector.options.name.toLowerCase() === AUTO.toLowerCase();
        }

        function resolveConnectors(connection) {
            var minDist = MAXINT,
                sourcePoint, targetPoint,
                source = connection.source(),
                target = connection.target(),
                autoSourceShape,
                autoTargetShape,
                sourceConnector,
                preferred = [0, 2, 3, 1, 4],
                k;
            if (source instanceof Point) {
                sourcePoint = source;
            }
            else if (source instanceof Connector) {
                if (isAutoConnector(source)) {
                    autoSourceShape = source.shape;
                }
                else {
                    connection._resolvedSourceConnector = source;
                    sourcePoint = source.position();
                }
            }

            if (target instanceof Point) {
                targetPoint = target;
            }
            else if (target instanceof Connector) {
                if (isAutoConnector(target)) {
                    autoTargetShape = target.shape;
                }
                else {
                    connection._resolvedTargetConnector = target;
                    targetPoint = target.position();
                }
            }

            if (sourcePoint) {
                if (autoTargetShape) {
                    connection._resolvedTargetConnector = closestConnector(sourcePoint, autoTargetShape);
                }
            } else if (autoSourceShape) {
                if (targetPoint) {
                    connection._resolvedSourceConnector = closestConnector(targetPoint, autoSourceShape);
                } else if (autoTargetShape) {

                    for (var i = 0; i < autoSourceShape.connectors.length; i++) {
                        if (autoSourceShape.connectors.length == 5) // presuming this means the default connectors
                        {
                            // will emphasize the vertical or horizontal direction, which matters when using the cascading router and distances which are equal for multiple connectors.
                            k = preferred[i];
                        }
                        else {
                            k = i;
                        }
                        sourceConnector = autoSourceShape.connectors[k];
                        if (!isAutoConnector(sourceConnector)) {
                            var currentSourcePoint = sourceConnector.position(),
                                currentTargetConnector = closestConnector(currentSourcePoint, autoTargetShape);
                            var dist = Math.round(currentTargetConnector.position().distanceTo(currentSourcePoint)); // rounding prevents some not needed connectors switching.
                            if (dist < minDist) {
                                minDist = dist;
                                connection._resolvedSourceConnector = sourceConnector;
                                connection._resolvedTargetConnector = currentTargetConnector;
                            }
                        }
                    }
                }
            }
        }

        function closestConnector(point, shape) {
            var minimumDistance = MAXINT, resCtr, ctrs = shape.connectors;
            for (var i = 0; i < ctrs.length; i++) {
                var ctr = ctrs[i];
                if (!isAutoConnector(ctr)) {
                    var dist = point.distanceTo(ctr.position());
                    if (dist < minimumDistance) {
                        minimumDistance = dist;
                        resCtr = ctr;
                    }
                }
            }
            return resCtr;
        }

        function indicesOfItems(group, visuals) {
            var i, indices = [], visual;
            for (i = 0; i < visuals.length; i++) {
                visual = visuals[i];
                for (var j = 0; j < group.children.length; j++) {
                    var other = group.children[j];
                    if (other == visual.domElement) {
                        indices.push(j);
                        break;
                    }
                }
            }
            return indices;
        }

        function deserializeConnector(diagram, value) {
            var point = Point.parse(value), ctr;
            if (point) {
                return point;
            }
            ctr = Connector.parse(diagram, value);
            if (ctr) {
                return ctr;
            }
        }

        var DiagramElement = Observable.extend({
            init: function (options, model) {
                var that = this;
                Observable.fn.init.call(that);
                that.options = deepExtend({ id: diagram.randomId() }, that.options, options);
                that.isSelected = false;
                that.model = model;
                that.visual = new Group({
                    id: that.options.id
                });
                that._template();
            },
            options: {
                background: "Green",
                hover: {},
                cursor: Cursors.grip,
                content: {
                    align: "center middle",
                    text: ""
                },
                selectable: true,
                serializable: true,
                enable: true
            },
            _getCursor: function (point) {
                if (this.adorner) {
                    return this.adorner._getCursor(point);
                }
                return this.options.cursor;
            },
            visible: function (value) {
                if (isUndefined(value)) {
                    return this.visual.visible();
                }
                else {
                    this.visual.visible(value);
                }
            },
            bounds: function () {
            },
            refresh: function () {
                this.visual.redraw();
            },
            position: function (point) {
                this.options.x = point.x;
                this.options.y = point.y;
                this.visual.position(point);
            },
            toString: function () {
                return this.options.id;
            },
            serialize: function () {
                // the options json object describes the shape perfectly. So this object can serve as shape serialization.
                var json = deepExtend({}, {options: this.options});
                if (this.model) {
                    json.model = this.model.toString();
                }
                return json;
            },
            content: function (content) {
                if (content !== undefined) {
                    var bounds = this.bounds(),
                        options = deepExtend({ text: "", width: bounds.width, height: bounds.height }, this.options.content);

                    if (diagram.Utils.isString(content)) {
                        this.options.content.text = content;
                        options.text = content;
                    } else {
                        this.options.content = options;
                    }

                    if (this.shapeVisual instanceof TextBlock) {
                        this._contentVisual = this.shapeVisual;
                    }

                    if (!this._contentVisual && this.options.content.text) {
                        this._contentVisual = new TextBlock();
                        this.visual.append(this._contentVisual);
                    }

                    if (this._contentVisual) {
                        this._contentVisual.redraw(options);
                    }
                }

                return this.options.content.text;
            },
            _hitTest: function (point) {
                var bounds = this.bounds();
                return this.visible() && bounds.contains(point) && this.options.enable;
            },
            _template: function () {
                var that = this;
                if (that.options.content.template) {
                    var data = that.model || {},
                        elementTemplate = kendo.template(that.options.content.template, {
                            paramName: "item"
                        });

                    that.options.content.text = elementTemplate(data);
                }
            },
            _canSelect: function () {
                return this.options.selectable && this.diagram.options.selectable.type !== NONE;
            }
        });

        var Connector = Class.extend({
            init: function (shape, options) {
                this.options = deepExtend({}, this.options, options);
                this.connections = [];
                this.shape = shape;
            },
            options: {
                width: 7,
                height: 7,
                background: DEFAULT_CONNECTION_BACKGROUND,
                hover: {}
            },
            position: function () {
                if (this.options.position) {
                    return this.options.position(this.shape);
                } else {
                    return this.shape.getPosition(this.options.name);
                }
            },
            toString: function () {
                return {
                    shapeId: this.shape.toString(),
                    connector: this.options.name
                };
            }
        });

        Connector.parse = function (diagram, str) {
            var tempStr = str.split(":"),
                id = tempStr[0],
                name = tempStr[1] || AUTO;

            for (var i = 0; i < diagram.shapes.length; i++) {
                var shape = diagram.shapes[i];
                if (shape.options.id == id) {
                    return shape.getConnector(name.trim());
                }
            }
        };

        var Shape = DiagramElement.extend({
            init: function (options, model) {
                var that = this;
                var diagram = options.diagram;
                delete options.diagram; // avoid stackoverflow and reassign later on again
                DiagramElement.fn.init.call(that, options, model);
                that.options.diagram = diagram;
                options = that.options;
                that.connectors = [];
                that.type = options.type;
                that.shapeVisual = Shape.createShapeVisual(that.options);
                that.visual.append(this.shapeVisual);
                that.bounds(new Rect(options.x, options.y, Math.floor(options.width), Math.floor(options.height)));
                // TODO: Swa added for phase 2; included here already because the GraphAdapter takes it into account
                that._createConnectors();
                that.parentContainer = null;
                that.isContainer = false;
                that.isCollapsed = false;
                that.id = that.visual.domElement.id;
                that.content(that.content());
                that._rotate();
                if (options.hasOwnProperty("layout") && options.layout!==undefined) {
                    // pass the defined shape layout, it overtakes the default resizing
                    that.layout = options.layout.bind(options);
                }
            },

            options: diagram.shapeDefaults(),

            _createConnectors: function() {
                var options = this.options,
                    length = options.connectors.length,
                    connectorDefaults = options.connectorDefaults,
                    connector, i;

                for (i = 0; i < length; i++) {
                    connector = new Connector(
                        this, deepExtend({},
                            connectorDefaults,
                            options.connectors[i]
                        )
                    );
                    this.connectors.push(connector);
                }
            },

            bounds: function (value) {
                var point, size, bounds, options;
                options = this.options;
                if (value) {
                    if (isString(value)) {
                        switch (value) {
                            case TRANSFORMED :
                                bounds = this._transformedBounds();
                                break;
                            case ABSOLUTE :
                                bounds = this._transformedBounds();
                                var pan = this.diagram._pan;
                                bounds.x += pan.x;
                                bounds.y += pan.y;
                                break;
                            case ROTATED :
                                bounds = this._rotatedBounds();
                                break;
                            default:
                                bounds = this._bounds;
                        }
                    } else { // we assume Rect.
                        point = value.topLeft();
                        options.x = point.x;
                        options.y = point.y;
                        options.width = Math.max(value.width, options.minWidth);
                        options.height = Math.max(value.height, options.minHeight);
                        this._bounds = new Rect(options.x, options.y, options.width, options.height);
                        this.visual.position(point);
                        this.redraw({ width: options.width, height: options.height });
                        this.refreshConnections();
                        this._triggerBoundsChange();
                    }
                } else {
                    bounds = this._bounds;
                } if (!this.shapeVisual._measured) { // no dimensions, assuming autosize for paths, groups...
                    size = this.shapeVisual._measure();
                    if (size) {
                        if (this.shapeVisual.options.autoSize) {
                            this.bounds(new Rect(options.x, options.y, size.width, size.height));
                        } else {
                            this.shapeVisual.redraw();
                        }
                    }
                }
                return bounds;
            },
            position: function (point) {
                if (point) {
                    this.bounds(new Rect(point.x, point.y, this._bounds.width, this._bounds.height));
                } else {
                    return this._bounds.topLeft();
                }
            },
            /**
             * Returns a clone of this shape.
             * @returns {Shape}
             */
            clone: function () {
                var json = this.serialize();
                json.options.id = diagram.randomId();
                var clone = new Shape(json.options);
                clone.diagram = this.diagram;
                /*clone.visual.native.id = clone.id;
                 clone.visual.id = clone.id;
                 clone.options.id = clone.id;*/
                return clone;
            },
            select: function (value) {
                var diagram = this.diagram, selected, deselected, type;
                if (isUndefined(value)) {
                    value = true;
                }
                if (this._canSelect()) {
                    if (this.isSelected != value) {
                        type = this.diagram.options.selectable.type;
                        selected = [];
                        deselected = [];
                        this.isSelected = value;
                        if (this.isSelected) {
                            if (type === SINGLE) {
                                this.diagram.select(false);
                            }
                            diagram._selectedItems.push(this);
                            selected.push(this);
                        } else {
                            Utils.remove(diagram._selectedItems, this);
                            deselected.push(this);
                        }
                        if (!diagram._internalSelection) {
                            diagram._selectionChanged(selected, deselected);
                        }
                        return true;
                    }
                }
            },
            rotate: function (angle, center) { // we assume the center is always the center of the shape.
                var rotate = this.visual.rotate();
                if (angle !== undefined) {
                    var b = this.bounds(),
                        sc = new Point(b.width / 2, b.height / 2),
                        deltaAngle,
                        newPosition;

                    if (center) {
                        deltaAngle = angle - rotate.angle;
                        newPosition = b.center().rotate(center, 360 - deltaAngle).minus(sc);
                        this._rotationOffset = this._rotationOffset.plus(newPosition.minus(b.topLeft()));
                        this.position(newPosition);
                    }
                    this.visual.rotate(angle, sc);
                    this.options.rotation.angle = angle;

                    if (this.diagram && this.diagram._connectorsAdorner) {
                        this.diagram._connectorsAdorner.refresh();
                    }
                    this.refreshConnections();
                    if (this.diagram) {
                        this.diagram.trigger(ITEMROTATE, {item: this});
                    }
                }

                return rotate;
            },
            connections: function (type) { // in, out, undefined = both
                var result = [], i, j, con, cons, ctr;

                for (i = 0; i < this.connectors.length; i++) {
                    ctr = this.connectors[i];
                    cons = ctr.connections;
                    for (j = 0, cons; j < cons.length; j++) {
                        con = cons[j];
                        if (type == "out") {
                            var source = con.source();
                            if (source.shape && source.shape == this) {
                                result.push(con);
                            }
                        } else if (type == "in") {
                            var target = con.target();
                            if (target.shape && target.shape == this) {
                                result.push(con);
                            }
                        } else {
                            result.push(con);
                        }
                    }
                }
                return result;
            },
            refreshConnections: function () {
                $.each(this.connections(), function () {
                    this.refresh();
                });
            },
            /**
             * Gets a connector of this shape either by the connector's supposed name or
             * via a Point in which case the closest connector will be returned.
             * @param nameOrPoint The name of a Connector or a Point.
             * @returns {Connector}
             */
            getConnector: function (nameOrPoint) {
                var i, ctr;
                if (isString(nameOrPoint)) {
                    nameOrPoint = nameOrPoint.toLocaleLowerCase();
                    for (i = 0; i < this.connectors.length; i++) {
                        ctr = this.connectors[i];
                        if (ctr.options.name.toLocaleLowerCase() == nameOrPoint) {
                            return ctr;
                        }
                    }
                } else if (nameOrPoint instanceof Point) {
                    return closestConnector(nameOrPoint, this);
                } else {
                    return this.connectors.length ? this.connectors[0] : null;
                }
            },
            getPosition: function (side) {
                var b = this.bounds(),
                    fnName = side[0].toLowerCase() + side.slice(1);
                if (isFunction(b[fnName])) {
                    return this._transformPoint(b[fnName]());
                }
                return b.center();
            },
            redraw: function (options) {
                if (options) {
                    this.options = deepExtend({}, this.options, options);
                }

                if (this._contentVisual) {
                    this._contentVisual.redraw({ width: this.options.width, height: this.options.height });
                }
                this.shapeVisual.redraw(options);
                if (options && options.content) {
                    this.content(options.content);
                }
            },
            _triggerBoundsChange: function () {
                if (this.diagram) {
                    this.diagram.trigger(ITEMBOUNDSCHANGE, {item: this, bounds: this._bounds.clone()}); // the trigger modifies the arguments internally.
                }
            },
            _transformPoint: function (point) {
                var rotate = this.rotate(),
                    bounds = this.bounds(),
                    tl = bounds.topLeft();

                if (rotate.angle) {
                    point.rotate(rotate.center().plus(tl), 360 - rotate.angle);
                }
                return point;
            },
            _transformedBounds: function () {
                var bounds = this.bounds(),
                    tl = bounds.topLeft(),
                    br = bounds.bottomRight();
                return Rect.fromPoints(this.diagram.modelToView(tl), this.diagram.modelToView(br));
            },
            _rotatedBounds: function () {
                var bounds = this.bounds().rotatedBounds(this.rotate().angle),
                    tl = bounds.topLeft(),
                    br = bounds.bottomRight();

                return Rect.fromPoints(tl, br);
            },
            _rotate: function () {
                var rotation = this.options.rotation;
                if (rotation && rotation.angle) {
                    this.rotate(rotation.angle);
                }
                this._rotationOffset = new Point();
            },
            _hover: function (value) {
                var options = this.options,
                    hover = options.hover,
                    stroke = options.stroke,
                    background = options.background;

                if (value && isDefined(hover.stroke)) {
                    stroke = deepExtend({}, stroke, hover.stroke);
                }

                if (value && isDefined(hover.background)) {
                    background = hover.background;
                }

                this.shapeVisual.redraw({
                    stroke: stroke,
                    background: background
                });

                this.diagram._showConnectors(this, value);
            },
            _hitTest: function (value) {
                if (this.visible()) {
                    var bounds = this.bounds(), rotatedPoint,
                        angle = this.rotate().angle;

                    if (value.isEmpty && !value.isEmpty()) { // rect selection
                        return Intersect.rects(value, bounds, angle ? angle : 0);
                    } else { // point
                        rotatedPoint = value.clone().rotate(bounds.center(), angle); // cloning is important because rotate modifies the point inline.
                        if (bounds.contains(rotatedPoint)) {
                            return this;
                        }
                    }
                }
            }
        });

        Shape.createShapeVisual = function(options) {
            var diagram = options.diagram;
            delete options.diagram; // avoid stackoverflow and reassign later on again
            var shapeDefaults = deepExtend({}, options, { x: 0, y: 0 }),
                visualTemplate = shapeDefaults.visual, // Shape visual should not have position in its parent group.
                type = shapeDefaults.type;

            function externalLibraryShape(libraryShapeName, options, shapeDefaults) {
                // if external serializationSource we need to consult the attached libraries
                // shapeDefaults.diagram is set when the diagram starts deserializing
                if (diagram.libraries && diagram.libraries.length > 0) {
                    for (var i = 0; i < diagram.libraries.length; i++) {
                        var library = diagram.libraries[i];
                        for (var j = 0; j < library.length; j++) {
                            var shapeDefinition = library[j];
                            if (shapeDefinition.options.name === libraryShapeName && shapeDefinition.options.serializationSource === "external") {
                                // the JSON options do not contain the funcs managing the complex layout, so need to transfer them
                                options.layout = shapeDefinition.options.layout;
                                options.visual = shapeDefinition.options.visual;
                                options.rebuild = shapeDefinition.options.rebuild;
                                return shapeDefinition.options.visual(shapeDefaults);
                            }
                        }
                    }
                }
            }

            function simpleShape(name, shapeDefaults) {
                switch (name.toLocaleLowerCase()) {
                    case "rectangle":
                        return new Rectangle(shapeDefaults);
                    case "circle":
                        return new Circle(shapeDefaults);
                    case "text": // Maybe should be something else.
                        return new TextBlock(shapeDefaults);
                    case "image":
                        return new Image(shapeDefaults);
                    case "svg":
                        return svgShape(shapeDefaults.definition);
                    default:
                        var p = new Path(shapeDefaults);
                        return p;
                }
            }

            function pathShape(path, shapeDefaults) {
                shapeDefaults.data = path;

                return new Path(shapeDefaults);
            }

            function functionShape(func, context, shapeDefaults) {
                return func.call(context, shapeDefaults);
            }

            var parseXml;

            if (typeof window.DOMParser != "undefined") {
                parseXml = function (xmlStr) {
                    return ( new window.DOMParser() ).parseFromString(xmlStr, "image/svg+xml");
                };
            } else if (typeof window.ActiveXObject != "undefined" && new window.ActiveXObject("Microsoft.XMLDOM")) {
                parseXml = function (xmlStr) {
                    var xmlDoc = new window.ActiveXObject("Microsoft.XMLDOM");
                    xmlDoc.async = "false";
                    xmlDoc.loadXML(xmlStr);
                    return xmlDoc;
                };
            } else {
                throw new Error("No XML parser found");
            }

            function svgShape(svgString) {
                var fullString = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:svg="http://www.w3.org/2000/svg">' + svgString + '</svg>';
                var result = parseXml(fullString);
                var importedNode = document.importNode(result.childNodes[0].childNodes[0], true);
                var g = new Group();
                g.append(new Visual(importedNode));
                return g;
            }

            if (!kendo.isFunction(visualTemplate) && shapeDefaults.serializationSource === "external") {
                return externalLibraryShape(shapeDefaults.name, options, shapeDefaults);
            } else if (shapeDefaults.path) {
                return pathShape(shapeDefaults.path, shapeDefaults);
            } else if (isFunction(visualTemplate)) { // custom template
                return functionShape(visualTemplate, this, shapeDefaults);
            } else if (isString(type)) {
                return simpleShape(shapeDefaults.type.toLocaleLowerCase(), shapeDefaults);
            } else {
                return new Rectangle(shapeDefaults);
            }
        };

        /**
         * The visual link between two Shapes through the intermediate of Connectors.
         */
        var Connection = DiagramElement.extend({
            init: function (from, to, options, model) {
                var that = this;
                DiagramElement.fn.init.call(that, options, model);
                that._router = new PolylineRouter(this);
                that.path = new Path(that.options);
                that.path.background(NONE);
                that.visual.append(that.path);
                that._sourcePoint = that._targetPoint = new Point();
                that.source(from);
                that.target(to);
                that.content(that.options.content.text);
                that.definers = [];
                if (isDefined(options) && options.points) {
                    that.points(options.points);
                }
                that.refresh();
            },
            options: {
                hover: {
                    stroke: {}
                },
                startCap: NONE,
                endCap: NONE,
                points: [],
                selectable: true
            },

            /**
             * Gets the Point where the source of the connection resides.
             * If the endpoint in Auto-connector the location of the resolved connector will be returned.
             * If the endpoint is floating the location of the endpoint is returned.
             */
            sourcePoint: function () {
                return this._resolvedSourceConnector ? this._resolvedSourceConnector.position() : this._sourcePoint;
            },

            /**
             * Gets or sets the Point where the source of the connection resides.
             * @param source The source of this connection. Can be a Point, Shape, Connector.
             * @param undoable Whether the change or assignment should be undoable.
             */
            source: function (source, undoable) {
                if (isDefined(source)) {
                    if (undoable && this.diagram) {
                        this.diagram.undoRedoService.addCompositeItem(new diagram.ConnectionEditUnit(this, source));
                    } else {
                        if (source !== undefined) {
                            this.from = source;
                        }
                        if (source === null) { // detach
                            if (this.sourceConnector) {
                                this._sourcePoint = this._resolvedSourceConnector.position();
                                this._clearSourceConnector();
                            }
                        } else if (source instanceof Connector) {
                            this.sourceConnector = source;
                            this.sourceConnector.connections.push(this);
                            this.refresh();
                        } else if (source instanceof Point) {
                            this._sourcePoint = source;
                            if (this.sourceConnector) {
                                this._clearSourceConnector();
                            }
                            this.refresh();
                        } else if (source instanceof Shape) {
                            this.sourceConnector = source.getConnector(AUTO);// source.getConnector(this.targetPoint());
                            this.sourceConnector.connections.push(this);
                            this.refresh();
                        }
                    }
                }
                return this.sourceConnector ? this.sourceConnector : this._sourcePoint;
            },

            /**
             * Gets or sets the PathDefiner of the sourcePoint.
             * The left part of this definer is always null since it defines the source tangent.
             * @param value
             * @returns {*}
             */
            sourceDefiner: function (value) {
                if (value) {
                    if (value instanceof diagram.PathDefiner) {
                        value.left = null;
                        this._sourceDefiner = value;
                        this.source(value.point); // refresh implicit here
                    } else {
                        throw "The sourceDefiner needs to be a PathDefiner.";
                    }
                } else {
                    if (!this._sourceDefiner) {
                        this._sourceDefiner = new diagram.PathDefiner(this.sourcePoint(), null, null);
                    }
                    return this._sourceDefiner;
                }
            },

            /**
             * Gets  the Point where the target of the connection resides.
             */
            targetPoint: function () {
                return this._resolvedTargetConnector ? this._resolvedTargetConnector.position() : this._targetPoint;
            },
            /**
             * Gets or sets the Point where the target of the connection resides.
             * @param target The target of this connection. Can be a Point, Shape, Connector.
             * @param undoable  Whether the change or assignment should be undoable.
             */
            target: function (target, undoable) {
                if (isDefined(target)) {
                    if (undoable && this.diagram) {
                        this.diagram.undoRedoService.addCompositeItem(new diagram.ConnectionEditUnit(this, target));
                    } else {
                        if (target !== undefined) {
                            this.to = target;
                        }
                        if (target === null) { // detach
                            if (this.targetConnector) {
                                this._targetPoint = this._resolvedTargetConnector.position();
                                this._clearTargetConnector();
                            }
                        } else if (target instanceof Connector) {
                            this.targetConnector = target;
                            this.targetConnector.connections.push(this);
                            this.refresh();
                        } else if (target instanceof Point) {
                            this._targetPoint = target;
                            if (this.targetConnector) {
                                this._clearTargetConnector();
                            }
                            this.refresh();
                        } else if (target instanceof Shape) {
                            this.targetConnector = target.getConnector(AUTO);// target.getConnector(this.sourcePoint());
                            if (this.targetConnector) {
                                this.targetConnector.connections.push(this);
                                this.refresh();
                            }
                        }
                    }
                }
                return this.targetConnector ? this.targetConnector : this._targetPoint;
            },
            /**
             * Gets or sets the PathDefiner of the targetPoint.
             * The right part of this definer is always null since it defines the target tangent.
             * @param value
             * @returns {*}
             */
            targetDefiner: function (value) {
                if (value) {
                    if (value instanceof diagram.PathDefiner) {
                        value.right = null;
                        this._targetDefiner = value;
                        this.target(value.point); // refresh implicit here
                    } else {
                        throw "The sourceDefiner needs to be a PathDefiner.";
                    }
                } else {
                    if (!this._targetDefiner) {
                        this._targetDefiner = new diagram.PathDefiner(this.targetPoint(), null, null);
                    }
                    return this._targetDefiner;
                }
            },

            content: function(content) {
                var result = DiagramElement.fn.content.call(this, content);
                this.refresh();

                return result;
            },

            /**
             * Selects or unselects this connections.
             * @param value True to select, false to unselect.
             */
            select: function (value) {
                var diagram = this.diagram, selected, deselected, type;
                if (this._canSelect()) {
                    if (this.isSelected !== value) {
                        this.isSelected = value;
                        selected = [];
                        deselected = [];
                        type = this.diagram.options.selectable.type;
                        if (this.isSelected) {
                            if (type === SINGLE) {
                                this.diagram.select(false);
                            }
                            this.adorner = new ConnectionEditAdorner(this, this.options.select);
                            diagram._adorn(this.adorner, true);
                            diagram._selectedItems.push(this);
                            selected.push(this);
                        } else {
                            if (this.adorner) {
                                diagram._adorn(this.adorner, false);
                                Utils.remove(diagram._selectedItems, this);
                                this.adorner = undefined;
                                deselected.push(this);
                            }
                        }
                        this.refresh();
                        if (!diagram._internalSelection) {
                            diagram._selectionChanged(selected, deselected);
                        }
                        return true;
                    }
                }
            },
            /**
             * Gets or sets the bounds of this connection.
             * @param value A Rect object.
             * @remark This is automatically set in the refresh().
             * @returns {Rect}
             */
            bounds: function (value) {
                if (value && !isString(value)) {
                    this._bounds = value;
                } else {
                    return this._bounds;
                }
            },
            /**
             * Gets or sets the connection type (see ConnectionType enumeration).
             * @param value A ConnectionType value.
             * @returns {ConnectionType}
             */
            type: function (value) {
                if (value) {
                    if (value !== this._type) {
                        this._type = value;
                        switch (value.toLowerCase()) {
                            case CASCADING.toLowerCase():
                                this._router = new CascadingRouter(this);
                                break;
                            case POLYLINE.toLowerCase():
                                this._router = new PolylineRouter(this);
                                break;
                            default:
                                throw "Unsupported connection type.";
                        }
                        this.refresh();
                    }
                } else {
                    return this._type;
                }
            },
            /**
             * Gets or sets the collection of *intermediate* points.
             * The 'allPoints()' property will return all the points.
             * The 'definers' property returns the definers of the intermediate points.
             * The 'sourceDefiner' and 'targetDefiner' return the definers of the endpoints.
             * @param value
             */
            points: function (value) {
                if (value) {
                    this.definers = [];
                    for (var i = 0; i < value.length; i++) {
                        var definition = value[i];
                        if (definition instanceof diagram.Point) {
                            this.definers.push(new diagram.PathDefiner(definition));
                        } else if (definition.hasOwnProperty("x") && definition.hasOwnProperty("y")) { // e.g. Clipboard does not preserve the Point definition and tunred into an Object
                            this.definers.push(new diagram.PathDefiner(new Point(definition.x, definition.y)));
                        } else {
                            throw "A Connection point needs to be a Point or an object with x and y properties.";
                        }
                    }

                } else {
                    var pts = [];
                    if (isDefined(this.definers)) {
                        for (var k = 0; k < this.definers.length; k++) {
                            pts.push(this.definers[k].point);
                        }
                    }
                    return pts;
                }
            },
            /**
             * Gets all the points of this connection. This is the combination of the sourcePoint, the points and the targetPoint.
             * @returns {Array}
             */
            allPoints: function () {
                var pts = [this.sourcePoint()];
                if (this.definers) {
                    for (var k = 0; k < this.definers.length; k++) {
                        pts.push(this.definers[k].point);
                    }
                }
                pts.push(this.targetPoint());
                return pts;
            },
            refresh: function () {
                resolveConnectors(this);
                var globalSourcePoint = this.sourcePoint(), globalSinkPoint = this.targetPoint(),
                    boundsTopLeft, localSourcePoint, localSinkPoint, middle;

                this._refreshPath();

                boundsTopLeft = this._bounds.topLeft();
                localSourcePoint = globalSourcePoint.minus(boundsTopLeft);
                localSinkPoint = globalSinkPoint.minus(boundsTopLeft);
                if (this._contentVisual) {
                    middle = Point.fn.middleOf(localSourcePoint, localSinkPoint);
                    this._contentVisual.position(new Point(middle.x + boundsTopLeft.x, middle.y + boundsTopLeft.y));
                }

                if (this.adorner) {
                    this.adorner.refresh();
                }
            },
            redraw: function (options) {
                this.options = deepExtend({}, this.options, options);
                this.content(this.options.content.text);
                if (isDefined(this.options.points) && this.options.points.length > 0) {
                    this.points(this.options.points);
                    this._refreshPath();
                }
                this.path.redraw(options);
            },
            /**
             * Returns a clone of this connection.
             * @returns {Connection}
             */
            clone: function () {
                var json = this.serialize(),
                    clone = new Connection(this.from, this.to, json.options);
                clone.diagram = this.diagram;

                return clone;
            },
            /**
             * Returns a serialized connection in json format. Consist of the options and the model.
             * @returns {Connection}
             */
            serialize: function () {
                var json = deepExtend({}, {
                    options: this.options,
                    from: this.from.toString(),
                    to: this.to.toString()
                });
                if (this.model) {
                    json.model = this.model.toString();
                }
                json.options.points = this.points();
                return json;
            },

            /**
             * Returns whether the given Point or Rect hits this connection.
             * @param value
             * @returns {Connection}
             * @private
             */
            _hitTest: function (value) {
                if (this.visible()) {
                    var p = new Point(value.x, value.y), from = this.sourcePoint(), to = this.targetPoint();
                    if (value.isEmpty && !value.isEmpty() && value.contains(from) && value.contains(to)) {
                        return this;
                    }
                    if (this._router.hitTest(p)) {
                        return this;
                    }
                }
            },
            _hover: function (value) {
                var color = (this.options.stroke || {}).color;

                if (value && isDefined(this.options.hover.stroke.color)) {
                    color = this.options.hover.stroke.color;
                }

                this.path.redraw({
                    stroke: {
                        color: color
                    }
                });
            },
            /**
             * Using the current router with the endpoints and intermediate points, this returns the Path data to be drawn.
             * @private
             */
            _calcPathData: function () {
                if (this._router) {
                    this._router.route(); // sets the intermediate points
                }
                function pr(point) {
                    return point.x + " " + point.y;
                }

                // for now let's take the heuristic approach, more complete API later
                var from = this.sourcePoint();
                var end = this.targetPoint();
                var data = "M" + pr(from);

                var points = this.points();
                for (var i = 0; i < points.length; i++) {
                    var point = points[i];
                    data += " L" + pr(point);
                }
                return data + " L" + pr(end);
            },
            _refreshPath: function () {
                if (isUndefined(this.path)) {
                    return;
                }
                this._drawPath(this._calcPathData());
                this.bounds(this._router.getBounds());
            },
            _drawPath: function (data) {
                this.path.redraw({ data: data  });
            },
            _clearSourceConnector: function () {
                Utils.remove(this.sourceConnector.connections, this);
                this.sourceConnector = undefined;
                this._resolvedSourceConnector = undefined;
            },
            _clearTargetConnector: function () {
                Utils.remove(this.targetConnector.connections, this);
                this.targetConnector = undefined;
                this._resolvedTargetConnector = undefined;
            }
        });

        var Diagram = Widget.extend({
            init: function (element, options) {
                var that = this;
                kendo.destroy(element);
                Widget.fn.init.call(that, element, options);
                element = that.element; // the hosting element
                element.empty();
                that._initTheme();
                that._extendLayoutOptions(that.options);

                that.element.addClass("k-widget k-diagram").attr("role", "diagram");
                var canvasContainer = $("<div class='k-canvas-container'></div>").appendTo(element)[0];
                that.canvas = new Canvas(canvasContainer); // the root SVG Canvas
                if (that.options.useScroller) {
                    that.scrollable = $("<div />").appendTo(that.element).append(that.canvas.element);
                }

                this.mainLayer = new Group({
                    id: "main-layer"
                });
                this.canvas.append(this.mainLayer);
                this.adornerLayer = new Group({
                    id: "adorner-layer"
                });
                this.canvas.append(this.adornerLayer);
                this.libraries = []; // shape libraries needed to deserialize complex shapes/controls with composite geometries and layout
                this.toolService = new ToolService(this);
                this._attachEvents();
                that._initialize();
                that._fetchFreshData();
                this._resizingAdorner = new ResizingAdorner(this, { editable: this.options.editable });
                this._connectorsAdorner = new ConnectorsAdorner(this);

                this._adorn(this._resizingAdorner, true);
                this._adorn(this._connectorsAdorner, true);
                that.element
                    .on("mousemove" + NS, proxy(that._mouseMove, that))
                    .on("mouseup" + NS, proxy(that._mouseUp, that))
                    .on("dblclick" + NS, proxy(that._doubleClick, that))
                    .on("mousedown" + NS, proxy(that._mouseDown, that))
                    .mousewheel(proxy(that._wheel, that), { ns: NS })
                    .on("keydown" + NS, proxy(that._keydown, that))
                    .on("mouseover" + NS, proxy(that._mouseover, that))
                    .on("mouseout" + NS, proxy(that._mouseout, that));
                that.selector = new Selector(that);
                // TODO: We may consider using real Clipboard API once is supported by the standard.
                that._clipboard = [];
                that._drop();
                that._initEditor();
                that._autosizeCanvas();

                if (that.options.layout) {
                    that.layout(that.options.layout);
                }
                this.pauseMouseHandlers = false;

                that._createShapes();
                that._createConnections();
            },
            options: {
                name: "Diagram",
                zoomRate: 1.1,
                dataSource: {},
                draggable: true,
                template: "",
                autoBind: true,
                editable: {
                    rotate: {},
                    resize: {},
                    text: true
                },
                useScroller: true,
                tooltip: { enabled: true, format: "{0}" },
                copy: {
                    enabled: true,
                    offsetX: 20,
                    offsetY: 20
                },
                editor: {
                    height: 20,
                    margin: 10,
                    fontSize: 15
                },
                selectable: { // none, extended, multiple
                    type: MULTIPLE,
                    inclusive: true
                },
                snap: {
                    enabled: true,
                    size: 10,
                    angle: 10
                },
                shapeDefaults: diagram.shapeDefaults({ undoable: true }),
                connectionDefaults: {},
                shapes: [],
                connections: []
            },

            events: [ZOOM_END, ZOOM_START, PAN, SELECT, ITEMROTATE, ITEMBOUNDSCHANGE, CHANGE, CLICK],

            _mouseover: function(e) {
                if (e.target._hover) {
                    e.target._hover(true, e.target._kendoElement);
                }
            },

            _mouseout: function(e) {
                if (e.target._hover) {
                    e.target._hover(false, e.target._kendoElement);
                }
            },

            _initTheme: function() {
                var that = this,
                    themes = ui.themes || {},
                    themeName = ((that.options || {}).theme || "").toLowerCase(),
                    themeOptions = (themes[themeName] || {}).diagram;

                that.options = deepExtend({}, themeOptions, that.options);
            },

            _createShapes: function() {
                var that = this,
                    options = that.options,
                    shapes = options.shapes,
                    shape, i;

                for (i = 0; i < shapes.length; i++) {
                    shape = shapes[i];
                    that.addShape(shape);
                }
            },

            _createConnections: function() {
                var diagram = this,
                    options = diagram.options,
                    defaults = options.connectionDefaults,
                    connections = options.connections,
                    conn, source, target, i;

                for(i = 0; i < connections.length; i++) {
                    conn = connections[i];
                    source = diagram._findConnectionShape(conn.from);
                    target = diagram._findConnectionShape(conn.to);

                    diagram.connect(source, target, deepExtend({}, defaults, conn));
                }
            },

            _findConnectionShape: function(options) {
                var diagram = this;

                var shape = diagram.getShapeById(options.shapeId);

                return shape.getConnector(options.connector || AUTO);
            },

            destroy: function () {
                var that = this;
                Widget.fn.destroy.call(that);

                that.clear();
                that.element.off(NS);
                that.canvas.destroy(true);
                that.canvas = undefined;

                that.destroyScroller();

                if (that.options.draggable && ui.DropTarget) {
                    that.element.kendoDropTarget("destroy");
                }
            },
            destroyScroller: function () {
                var scroller = this.scroller;

                if (!scroller) {
                    return;
                }

                scroller.destroy();
                scroller.element.remove();
                this.scroller = null;
            },
            save: function () {
                var json = {}, i;

                json.shapes = [];
                json.connections = [];

                for (i = 0; i < this.shapes.length; i++) {
                    var shape = this.shapes[i];
                    if (shape.options.serializable) {
                        json.shapes.push(shape.options);
                    }
                }

                for (i = 0; i < this.connections.length; i++) {
                    var con = this.connections[i];
                    var conOptions = deepExtend({}, { from: con.from.toString(), to: con.to.toString() }, con.options);
                    json.connections.push(conOptions);
                }

                return json;
            },
            focus: function () {
                var x = window.scrollX, y = window.scrollY;
                this.canvas.focus();
                window.scrollTo(x, y); // prevent the annoying scroll to top of the canvas (div).
            },
            load: function(options) {
                this.clear();

                this.setOptions(options);
                this._createShapes();
                this._createConnections();
            },
            clear: function () {
                var that = this;
                if (that.dataSource) {
                    that._unbindDataSource();
                }

                that.select(false);
                that.mainLayer.clear();
                that._initialize();
            },
            /**
             * Connects two items.
             * @param source Shape, Connector, Point.
             * @param target Shape, Connector, Point.
             * @param options Connection options that will be passed to the newly created connection.
             * @returns The newly created connection.
             */
            connect: function (source, target, options) {
                var conOptions = deepExtend({}, this.options.connectionDefaults, options),
                    connection = new Connection(source, target, conOptions);
                return this.addConnection(connection);
            },
            /**
             * Determines whether the the two items are connected.
             * @param source Shape, Connector, Point.
             * @param target Shape, Connector, Point.
             * @returns true if the two items are connected.
             */
            connected: function (source, target) {
                for (var i = 0; i < this.connections.length; i++) {
                    var c = this.connections[i];
                    if (c.from == source && c.to == target) {
                        return true;
                    }
                }
                return false;
            },
            /**
             * Adds connection to the diagram.
             * @param connection Connection.
             * @param undoable Boolean.
             * @returns The newly created connection.
             */
            addConnection: function (connection, undoable) {
                if (undoable === undefined) {
                    undoable = true;
                }
                if (undoable) {
                    var unit = new diagram.AddConnectionUnit(connection, this);
                    this.undoRedoService.add(unit);
                } else {
                    connection.diagram = this;
                    this.mainLayer.append(connection.visual);
                    this.connections.push(connection);
                }

                return connection;
            },
            /**
             * Adds shape to the diagram.
             * @param item Shape, Point. If point is passed it will be created new Shape and positioned at that point.
             * @param options. The options to be passed to the newly created Shape.
             * @returns The newly created shape.
             */
            addShape: function (item, options) {
                var shape,
                    shapeDefaults = this.options.shapeDefaults;

                if (item instanceof Shape) {
                    shapeDefaults = deepExtend({}, shapeDefaults, options);
                    item.redraw(options);
                    shape = item;
                }
                else { // consider it a point
                    shapeDefaults = deepExtend({}, shapeDefaults, item);
                    shape = new Shape(shapeDefaults);
                }

                if (shapeDefaults.undoable) {
                    this.undoRedoService.add(new diagram.AddShapeUnit(shape, this));
                }
                else {
                    this.shapes.push(shape);
                    shape.diagram = this;
                    this.mainLayer.append(shape.visual);
                }

                this.trigger(CHANGE, {
                    added: [shape],
                    removed: []
                });

                shape.redraw();

                // for shapes which have their own internal layout mechanism
                if (shape.hasOwnProperty("layout")) {
                    shape.layout(shape);
                }

                return shape;
            },
            /**
             * Removes items (or single item) from the diagram.
             * @param items DiagramElement, Array of Items.
             * @param undoable.
             */
            remove: function (items, undoable) {
                var isMultiple = isArray(items);

                if (isUndefined(undoable)) {
                    undoable = true;
                }
                if (undoable) {
                    this.undoRedoService.begin();
                }
                if (isMultiple) {
                    items = items.slice(0);
                    for (var i = 0; i < items.length; i++) {
                        this._removeItem(items[i], undoable);
                    }
                } else if (items instanceof Shape || items instanceof Connection) {
                    this._removeItem(items, undoable);
                }
                if (undoable) {
                    this.undoRedoService.commit();
                }

                this.trigger(CHANGE, {
                    added: [],
                    removed: isMultiple ? items : [items]
                });
            },
            /**
             * Executes the next undoable action on top of the undo stack if any.
             */
            undo: function () {
                this.undoRedoService.undo();
            },
            /**
             * Executes the previous undoable action on top of the redo stack if any.
             */
            redo: function () {
                this.undoRedoService.redo();
            },
            /**
             * Selects items on the basis of the given input or returns the current selection if none.
             * @param itemsOrRect DiagramElement, Array of elements, "All", false or Rect. A value 'false' will deselect everything.
             * @param options
             * @returns {Array}
             */
            select: function (itemsOrRect, options) {
                var i, item, items, rect, selected, deselected, valueString;
                options = deepExtend({  addToSelection: false }, options);
                var addToSelection = options.addToSelection;
                if (itemsOrRect !== undefined) {
                    this._internalSelection = true;
                    deselected = [];
                    selected = [];
                    if (!addToSelection) {
                        while (this._selectedItems.length > 0) {
                            item = this._selectedItems[0];
                            if (item.select(false)) {
                                deselected.push(item);
                            }
                        }
                    }
                    if (Utils.isBoolean(itemsOrRect)) {
                        if (itemsOrRect !== false) {
                            this.select(ALL);
                        }
                    }
                    else if (itemsOrRect instanceof Rect) {
                        rect = itemsOrRect;
                        items = this.shapes.concat(this.connections);
                        for (i = 0; i < items.length; i++) {
                            item = items[i];
                            if ((!rect || item._hitTest(rect)) && item.options.enable) {
                                if (item.select(true)) {
                                    selected.push(item);
                                }
                            }
                        }
                    } else if (itemsOrRect instanceof Array) {
                        for (i = 0; i < itemsOrRect.length; i++) {
                            item = itemsOrRect[i];
                            if (item instanceof DiagramElement) {
                                if (item.select(true)) {
                                    selected.push(item);
                                }
                            }
                        }
                    } else if (itemsOrRect instanceof DiagramElement) {
                        if (itemsOrRect.select(true)) {
                            selected.push(itemsOrRect);
                        }
                    } else { // string with special meaning...
                        valueString = itemsOrRect.toString().toLowerCase();
                        if (valueString === NONE) {
                            this.select(false);
                        } else if (valueString === ALL) {
                            items = this.shapes.concat(this.connections);
                            for (i = 0; i < items.length; i++) {
                                item = items[i];
                                if (item.select(true)) {
                                    selected.push(item);
                                }
                            }
                        }
                    }

                    if (selected.length > 0 || deselected.length > 0) {
                        this._selectionChanged(selected, deselected);
                    }

                    this._internalSelection = false;
                }
                else {
                    return this._selectedItems; // returns all selected items.
                }
            },
            /**
             * Brings to front the passed items.
             * @param items DiagramElement, Array of Items.
             * @param undoable. By default the action is undoable.
             */
            toFront: function (items, undoable) {
                if (!items) {
                    items = this._selectedItems.slice();
                }
                var result = this._getDiagramItems(items), indices;
                if (isUndefined(undoable) || undoable) {
                    indices = indicesOfItems(this.mainLayer.domElement, result.visuals);
                    var unit = new ToFrontUnit(this, items, indices);
                    this.undoRedoService.add(unit);
                }
                else {
                    this.mainLayer.toFront(result.visuals);
                    this._fixOrdering(result, true);
                }
            },
            /**
             * Sends to back the passed items.
             * @param items DiagramElement, Array of Items.
             * @param undoable. By default the action is undoable.
             */
            toBack: function (items, undoable) {
                if (!items) {
                    items = this._selectedItems.slice();
                }
                var result = this._getDiagramItems(items), indices;
                if (isUndefined(undoable) || undoable) {
                    indices = indicesOfItems(this.mainLayer.domElement, result.visuals);
                    var unit = new ToBackUnit(this, items, indices);
                    this.undoRedoService.add(unit);
                }
                else {
                    this.mainLayer.toBack(result.visuals);
                    this._fixOrdering(result, false);
                }
            },
            /**
             * Bring into view the passed item(s) or rectangle.
             * @param items DiagramElement, Array of Items, Rect.
             * @param options. align - controls the position of the calculated rectangle relative to the viewport.
             * "Center middle" will position the items in the center. animate - controls if the pan should be animated.
             */
            bringIntoView: function (item, options) { // jQuery|Item|Array|Rect
                var rect,
                    viewport = this.viewport();

                options = deepExtend({animate: false, align: "center middle"}, options);
                if (item instanceof DiagramElement) {
                    rect = item.bounds(TRANSFORMED);
                }
                else if (isArray(item)) {
                    rect = this.boundingBox(item);
                }
                else if (item instanceof Rect) {
                    rect = item.clone();
                }
                if (options.align !== "none" || !viewport.contains(rect.center())) {
                    if (options.align === "none") {
                        options.align = "center middle";
                    }
                    var old = rect.clone(),
                        align = new diagram.RectAlign(viewport);

                    align.align(rect, options.align);

                    var newPan = rect.topLeft().minus(old.topLeft());
                    if (!this.options.useScroller) {
                        newPan = this.pan().plus(newPan);
                    }
                    this.pan(newPan, options.animate);
                }
            },
            alignShapes: function (direction) {
                if (isUndefined(direction)) {
                    direction = "Left";
                }
                var items = this.select(),
                    val,
                    item,
                    i;
                if (items.length === 0) {
                    return;
                }
                switch (direction.toLowerCase()) {
                    case "left":
                    case "top":
                        val = Number.MAX_VALUE;
                        break;
                    case "right":
                    case "bottom":
                        val = Number.MIN_VALUE;
                        break;
                }
                for (i = 0; i < items.length; i++) {
                    item = items[i];
                    if (item instanceof Shape) {
                        switch (direction.toLowerCase()) {
                            case "left":
                                val = Math.min(val, item.options.x);
                                break;
                            case "top":
                                val = Math.min(val, item.options.y);
                                break;
                            case "right":
                                val = Math.max(val, item.options.x);
                                break;
                            case "bottom":
                                val = Math.max(val, item.options.y);
                                break;
                        }
                    }
                }
                var undoStates = [];
                var shapes = [];
                for (i = 0; i < items.length; i++) {
                    item = items[i];
                    if (item instanceof Shape) {
                        shapes.push(item);
                        undoStates.push(item.bounds());
                        switch (direction.toLowerCase()) {
                            case "left":
                            case "right":
                                item.position(new Point(val, item.options.y));
                                break;
                            case "top":
                            case "bottom":
                                item.position(new Point(item.options.x, val));
                                break;
                        }
                        //item.refresh();
                    }
                }
                var unit = new diagram.TransformUnit(shapes, undoStates);
                this.undoRedoService.add(unit, false);
            },
            zoom: function (zoom, options) {
                if (zoom) {
                    var staticPoint = options ? options.location : new diagram.Point(0, 0);
                    // var meta = options ? options.meta : 0;
                    zoom = this._zoom = this._getValidZoom(zoom);

                    if (!isUndefined(staticPoint)) {//Viewpoint vector is constant
                        staticPoint = new diagram.Point(Math.round(staticPoint.x), Math.round(staticPoint.y));
                        var zoomedPoint = staticPoint.times(zoom);
                        var viewportVector = this.modelToView(staticPoint);
                        var raw = viewportVector.minus(zoomedPoint);//pan + zoomed point = viewpoint vector
                        this._storePan(new diagram.Point(Math.round(raw.x), Math.round(raw.y)));
                    }
                    if (options) {
                        options.zoom = zoom;
                    }

                    this._panTransform();

                    this._autosizeCanvas();
                    this._updateAdorners();
                }
                return this._zoom;
            },
            pan: function (pan, options) {
                options = options || {animated: false};
                var animated = options.animated;
                if (pan instanceof Point && !pan.equals(this._pan)) {
                    this._animatePan(pan, !animated);
                    this._storePan(pan);

                    this.trigger(PAN, {total: pan, delta: options.delta});
                    this._autosizeCanvas();
                    this._updateAdorners();
                }

                return this._pan;
            },
            viewport: function () {
                var element = this.element;

                return new Rect(0, 0, element.width(), element.height());
            },
            copy: function () {
                if (this.options.copy.enabled) {
                    this._clipboard = [];
                    this._copyOffset = 1;
                    for (var i = 0; i < this._selectedItems.length; i++) {
                        var item = this._selectedItems[i];
                        this._clipboard.push(item);
                    }
                }
            },
            cut: function () {
                if (this.options.copy.enabled) {
                    this._clipboard = [];
                    this._copyOffset = 0;
                    for (var i = 0; i < this._selectedItems.length; i++) {
                        var item = this._selectedItems[i];
                        this._clipboard.push(item);
                    }
                    this.remove(this._clipboard);
                }
            },
            paste: function () {
                var offsetX, offsetY, item, copied, connector, shape, i;
                if (this._clipboard.length > 0) {
                    var mapping = new Dictionary();

                    offsetX = this._copyOffset * this.options.copy.offsetX;
                    offsetY = this._copyOffset * this.options.copy.offsetY;
                    this.select(false);
                    // first the shapes
                    for (i = 0; i < this._clipboard.length; i++) {
                        item = this._clipboard[i];
                        if (item instanceof Connection) {
                            continue;
                        }
                        copied = item.clone();
                        mapping.set(item.id, copied.id);
                        this._addItem(copied);
                        copied.position(new Point(item.options.x + offsetX, item.options.y + offsetY));
                        copied.select(true);
                    }
                    // then the connections
                    for (i = 0; i < this._clipboard.length; i++) {
                        item = this._clipboard[i];
                        if (item instanceof Shape) {
                            continue;
                        }
                        copied = item.clone();
                        if (item.source() instanceof Connector) { // if Point then it's a floating end
                            connector = item.source();
                            if (mapping.containsKey(connector.shape.id)) { // occurs when an attached connection is pasted with unselected shape parents
                                shape = this.getShapeById(mapping.get(connector.shape.id));
                                copied.source(shape.getConnector(connector.options.name));
                            } else {
                                copied.source(new Point(item.sourcePoint().x + offsetX, item.sourcePoint().y + offsetY));
                            }
                        }
                        if (item.target() instanceof Connector) {
                            connector = item.target();
                            if (mapping.containsKey(connector.shape.id)) {
                                shape = this.getShapeById(mapping.get(connector.shape.id));
                                copied.target(shape.getConnector(connector.options.name));
                            }
                            else {
                                copied.target(new Point(item.targetPoint().x + offsetX, item.targetPoint().y + offsetY));
                            }
                        }
                        this._addItem(copied);
                        copied.position(new Point(item.options.x + offsetX, item.options.y + offsetY));
                        copied.select(true);
                    }
                    this._copyOffset += 1;
                }
            },
            /**
             * Gets the bounding rectangle of the given items.
             * @param items DiagramElement, Array of elements.
             * @param origin Boolean. Pass 'true' if you need to get the bounding box of the shapes without their rotation offset.
             * @returns {Rect}
             */
            boundingBox: function (items, origin) {
                var rect = Rect.empty(), temp,
                    di = isDefined(items) ? this._getDiagramItems(items) : {shapes: this.shapes};
                if (di.shapes.length > 0) {
                    var item = di.shapes[0];
                    if (origin === true) {
                        rect.x -= item._rotationOffset.x;
                        rect.y -= item._rotationOffset.y;
                    }
                    rect = item.bounds(ROTATED);
                    for (var i = 1; i < di.shapes.length; i++) {
                        item = di.shapes[i];
                        temp = item.bounds(ROTATED);
                        if (origin === true) {
                            temp.x -= item._rotationOffset.x;
                            temp.y -= item._rotationOffset.y;
                        }
                        rect = rect.union(temp);
                    }
                }
                return rect;
            },
            documentToView: function(point) {
                var containerOffset = $(this.canvas.element).offset();

                return new Point(point.x - containerOffset.left, point.y - containerOffset.top);
            },
            viewToDocument: function(point) {
                var containerOffset = $(this.canvas.element).offset();

                return new Point(point.x + containerOffset.left, point.y + containerOffset.top);
            },
            viewToModel: function(point) {
                return this._transformWithMatrix(point, this._matrixInvert);
            },
            modelToView: function(point) {
                return this._transformWithMatrix(point, this._matrix);
            },
            modelToLayer: function(point) {
                return this._transformWithMatrix(point, this._layerMatrix);
            },
            layerToModel: function(point) {
                return this._transformWithMatrix(point, this._layerMatrixInvert);
            },
            documentToModel: function(point) {
                return this.viewToModel(this.documentToView(point));
            },
            modelToDocument: function(point) {
                return this.viewToDocument(this.modelToView(point));
            },
            _transformWithMatrix: function(point, matrix) {
                var result = point;
                if (point instanceof Point) {
                    if (matrix) {
                        result = matrix.apply(point);
                    }
                }
                else {
                    var tl = this._transformWithMatrix(point.topLeft(), matrix),
                        br = this._transformWithMatrix(point.bottomRight(), matrix);
                    result = Rect.fromPoints(tl, br);
                }
                return result;
            },

            setDataSource: function (dataSource) {
                this.options.dataSource = dataSource;
                this._dataSource();
                if (this.options.autoBind) {
                    this.dataSource.fetch();
                }
            },
            /**
             * Performs a diagram layout of the given type.
             * @param layoutType The layout algorithm to be applied (TreeLayout, LayeredLayout, SpringLayout).
             * @param options Layout-specific options.
             */
            layout: function (options) {
                this.isLayouting = true;
                // TODO: raise layout event?
                var type;
                if(isUndefined(options)) {
                    options = this.options.layout;
                }
                if (isUndefined(options) || isUndefined(options.type)) {
                    type = "Tree";
                }
                else {
                    type = options.type;
                }
                var l;
                switch (type.toLowerCase()) {
                    case "tree":
                        l = new diagram.TreeLayout(this);
                        break;

                    case "layered":
                        l = new diagram.LayeredLayout(this);
                        break;

                    case "forcedirected":
                    case "force":
                    case "spring":
                    case "springembedder":
                        l = new diagram.SpringLayout(this);
                        break;
                    default:
                        throw "Layout algorithm '" + type + "' is not supported.";
                }
                var initialState = new diagram.LayoutState(this);
                var finalState = l.layout(options);
                if (finalState) {
                    var unit = new diagram.LayoutUndoUnit(initialState, finalState, options ? options.animate : null);
                    this.undoRedoService.add(unit);
                }
                this.isLayouting = false;
            },
            /**
             * Gets a shape on the basis of its identifier.
             * @param id (string) the identifier of a shape.
             * @returns {Shape}
             */
            getShapeById: function (id) {
                var found;
                found = Utils.first(this.shapes, function (s) {
                    return s.visual.domElement.id === id;
                });
                if (found) {
                    return found;
                }
                found = Utils.first(this.connections, function (c) {
                    return c.visual.domElement.id === id;
                });
                return found;
            },
            /**
             * Shows the built-in editor of target item.
             * @options object. Preset options to customize the editor look and behavior.
             */
            editor: function (item, options) { // support custome editors via the options for vNext
                var editor = this._editor;
                if (isUndefined(item.options.editable) || item.options.editable.text) {
                    editor.options = deepExtend(this.options.editor, options);
                    this._editItem = item;
                    this._showEditor();
                    var shapeContent = item.content();
                    editor._originalContent = shapeContent;
                    editor.content(shapeContent);
                    editor.focus();
                }
                return editor;
            },
            _initEditor: function () {
                this._editor = new diagram.TextBlockEditor();
                this._editor.bind("finishEdit", $.proxy(this._finishEditShape, this));
            },
            _showEditor: function () {
                var editor = this._editor;

                editor.visible(true);
                this.element.context.appendChild(editor.domElement);
                this._positionEditor();
            },
            _positionEditor: function () {
                var editor = this._editor,
                    options = editor.options,
                    nativeEditor = $(editor.domElement),
                    bounds = this.modelToView(this._editItem.bounds()),
                    cssDim = function (prop) {
                        return parseInt(nativeEditor.css(prop), 10);
                    },
                    nativeOffset = new Point(cssDim("borderLeftWidth") + cssDim("paddingLeft"), cssDim("borderTopWidth") + cssDim("paddingTop")),
                    formattingOffset = new Point(options.margin, bounds.height / 2 - options.height / 2).minus(nativeOffset);

                editor.size((bounds.width - 2 * options.margin), options.height);
                editor.position(bounds.topLeft().plus(formattingOffset));
                nativeEditor.css({ fontSize: options.fontSize });
            },
            _extendLayoutOptions: function(options) {
                if(options.layout) {
                    options.layout = deepExtend(diagram.LayoutBase.fn.defaultOptions || {}, options.layout);
                }
            },
            _finishEditShape: function () {
                var editor = this._editor, item = this._editItem;
                if (item) {
                    var unit = new diagram.ContentChangedUndoUnit(item, editor._originalContent, editor.content());
                    this.undoRedoService.add(unit);
                    editor.visible(false);
                }
            },
            _selectionChanged: function (selected, deselected) {
                this.trigger(SELECT, {selected: selected, deselected: deselected});
            },
            _getValidZoom: function (zoom) {
                return Math.min(Math.max(zoom, 0.55), 2.0); //around 0.5 something exponential happens...!?
            },
            _panTransform: function (pos) {
                var diagram = this,
                    pan = pos || diagram._pan;

                if (this.scroller) {
                    diagram.scroller.scrollTo(pan.x, pan.y);
                    diagram._zoomMainLayer();
                }
                else {
                    diagram._pan = pan;
                    diagram._transformMainLayer();
                }
            },
            _animatePan: function (pan, skipAnimation) {
                var diagram = this;

                if (skipAnimation) {
                    this._panTransform(pan);
                }
                else {
                    if (diagram.scroller) {
                        diagram.scroller.animatedScrollTo(pan.x, pan.y);
                        diagram._zoomMainLayer();
                    }
                    else {
                        var t = new Ticker();
                        t.addAdapter(new PanAdapter({pan: pan, diagram: this}));
                        t.onStep = function () {
                            diagram._finishPan();
                        };
                        t.play();
                    }
                }
            },
            _finishPan: function () {
                this.trigger(PAN, {total: this._pan, delta: Number.NaN});
            },
            _storePan: function (pan) {
                this._pan = pan;
                this._storeViewMatrix();
            },
            _zoomMainLayer: function () {
                var zoom = this._zoom;

                var transform = new CompositeTransform(0, 0, zoom, zoom);
                transform.render(this.mainLayer.domElement);
                this._storeLayerMatrix(transform);
                this._storeViewMatrix();
            },
            _transformMainLayer: function () {
                var pan = this._pan,
                    zoom = this._zoom;

                var transform = new CompositeTransform(pan.x, pan.y, zoom, zoom);
                transform.render(this.mainLayer.domElement);
                this._storeLayerMatrix(transform);
                this._storeViewMatrix();
            },
            _storeLayerMatrix: function(canvasTransform) {
                this._layerMatrix = canvasTransform.toMatrix();
                this._layerMatrixInvert = canvasTransform.invert().toMatrix();
            },
            _storeViewMatrix: function() {
                var pan = this._pan,
                    zoom = this._zoom;

                var transform = new CompositeTransform(pan.x, pan.y, zoom, zoom);
                this._matrix = transform.toMatrix();
                this._matrixInvert = transform.invert().toMatrix();
            },
            _toIndex: function (items, indices) {
                var result = this._getDiagramItems(items);
                this.mainLayer.toIndex(result.visuals, indices);
                this._fixOrdering(result, false);
            },
            _drop: function () {
                var that = this,
                    options = that.options;
                if (options.draggable && ui.DropTarget) {
                    this.element.kendoDropTarget({
                        drop: function (e) {
                            var item, pos;
                            if (e.draggable && e.draggable.hint) {
                                item = e.draggable.hint.data("data");
                                pos = e.draggable.hintOffset;
                                pos = new Point(pos.left, pos.top);
                                var transformed = that.documentToModel(pos);
                                item.x = transformed.x;
                                item.y = transformed.y;

                                that.addShape(item);
                            }
                        }
                    });
                }
            },
            _fixOrdering: function (result, toFront) {
                var shapePos = toFront ? this.shapes.length - 1 : 0,
                    conPos = toFront ? this.connections.length - 1 : 0,
                    i, item;
                for (i = 0; i < result.shapes.length; i++) {
                    item = result.shapes[i];
                    Utils.remove(this.shapes, item);
                    Utils.insert(this.shapes, item, shapePos);
                }
                for (i = 0; i < result.cons.length; i++) {
                    item = result.cons[i];
                    Utils.remove(this.connections, item);
                    Utils.insert(this.connections, item, conPos);
                }
            },
            _getDiagramItems: function (items) {
                var i, result = {}, args = items;
                result.visuals = [];
                result.shapes = [];
                result.cons = [];

                if (!items) {
                    args = this._selectedItems.slice();
                }
                else if (!isArray(items)) {
                    args = [items];
                }
                for (i = 0; i < args.length; i++) {
                    var item = args[i];
                    if (item instanceof Shape) {
                        result.shapes.push(item);
                        result.visuals.push(item.visual);
                    }
                    else if (item instanceof Connection) {
                        result.cons.push(item);
                        result.visuals.push(item.visual);
                    }
                }
                return result;
            },
            _removeItem: function (item, undoable) {
                item.select(false);
                if (item instanceof Shape) {
                    this._removeShape(item, undoable);
                }
                else if (item instanceof Connection) {
                    this._removeConnection(item, undoable);
                }
                if (!undoable) {
                    this.mainLayer.remove(item.visual);
                }
            },
            _removeShape: function (shape, undoable) {
                var i, connection, connector,
                    sources = [], targets = [];
                this.toolService._removeHover();

                if (undoable) {
                    this.undoRedoService.addCompositeItem(new DeleteShapeUnit(shape));
                }
                else {
                    Utils.remove(this.shapes, shape);
                }
                for (i = 0; i < shape.connectors.length; i++) {
                    connector = shape.connectors[i];
                    for (var j = 0; j < connector.connections.length; j++) {
                        connection = connector.connections[j];
                        if (connection.sourceConnector == connector) {
                            sources.push(connection);
                        } else if (connection.targetConnector == connector) {
                            targets.push(connection);
                        }
                    }
                }

                for (i = 0; i < sources.length; i++) {
                    sources[i].source(null, undoable);
                }
                for (i = 0; i < targets.length; i++) {
                    targets[i].target(null, undoable);
                }
            },
            _removeConnection: function (connection, undoable) {
                if (connection.sourceConnector) {
                    Utils.remove(connection.sourceConnector.connections, connection);
                }
                if (connection.targetConnector) {
                    Utils.remove(connection.targetConnector.connections, connection);
                }
                if (undoable) {
                    this.undoRedoService.addCompositeItem(new DeleteConnectionUnit(connection));
                }
                else {
                    Utils.remove(this.connections, connection);
                }
            },
            _canRectSelect: function () {
                var type = this.options.selectable.type;
                return type === MULTIPLE;
            },
            _refreshSource: function (e) {
                var that = this,
                    node = e.node,
                    action = e.action,
                    items = e.items,
                    options = that.options,
                    i;

                function addShape(dataItem) {
                    if (isUndefined(dataItem)) { // happens on updating dataSource
                        return;
                    }
                    var shape = Utils.first(that._dataMap, function (item) {
                        return item.uid === dataItem.uid;
                    });
                    if (shape) {
                        return shape.shape;
                    }

                    var opt = deepExtend({}, that.options.shapeDefaults, {
                        dataItem: dataItem
                    });
                    shape = new Shape(opt, dataItem);
                    that.addShape(shape);
                    that._dataMap.push({ uid: dataItem.uid, shape: shape });
                    return shape;
                }

                function append(parent, children) {
                    for (var i = 0; i < children.length; i++) {
                        var node = children[i],
                            shape = addShape(node),
                            parentShape = addShape(parent);
                        if (parentShape && !that.connected(parentShape, shape)) { // check if connected to not duplicate connections.
                            var con = that.connect(parentShape, shape);
                            //var con = that.connect(parentShape.connectors[2], shape.connectors[0]);
                            con.type(CASCADING);
                        }
                    }
                }

                if (!e.field) { // field means any field in the data source has changed - like selected, expanded... We don't have to update in that case.
                    if (action === "add") {
                        append(node, items);
                    } else if (action === "remove") {
                        //Remove
                    } else if (action === "itemchange") {
                        if (node) {
                            if (!items.length) {
                                //Update
                            } else {
                                append(node, items);
                            }
                        } else {
                            for (i = 0; i < items.length; i++) {
                                addShape(items[i]); // roots
                            }
                        }
                    }

                    for (i = 0; i < items.length; i++) {
                        items[i].load();
                    }
                }

                if (that.options.layout) {
                    that.layout(that.options.layout);
                }
            },
            _mouseDown: function (e) {
                if (this.pauseMouseHandlers) {
                    return;
                }
                var p = this._calculatePosition(e);
                if (e.button === 0 && this.toolService.start(p, this._meta(e))) {
                    e.preventDefault();
                }
            },
            _addItem: function (item) {
                if (item instanceof Shape) {
                    this.addShape(item);
                } else if (item instanceof Connection) {
                    this.addConnection(item);
                }
            },
            _mouseUp: function (e) {
                if (this.pauseMouseHandlers) {
                    return;
                }
                var p = this._calculatePosition(e);
                if (e.button === 0 && this.toolService.end(p, this._meta(e))) {
                    e.preventDefault();
                }
            },
            _mouseMove: function (e) {
                if (this.pauseMouseHandlers) {
                    return;
                }
                var p = this._calculatePosition(e);
                if (e.button === 0 && this.toolService.move(p, this._meta(e))) {
                    e.preventDefault();
                }
            },
            _doubleClick: function (e) {
                var p = this._calculatePosition(e);
                if (e.button === 0 && this.toolService.doubleClick(p, this._meta(e))) {
                    e.preventDefault();
                }
            },
            _keydown: function (e) {
                if (this.toolService.keyDown(e.keyCode, this._meta(e))) {
                    e.preventDefault();
                }
            },
            _wheel: function (e) {
                var p = this._calculatePosition(e),
                    meta = deepExtend(this._meta(e), { delta: e.data.delta });

                if (this.toolService.wheel(p, meta)) {
                    e.preventDefault();
                }
            },
            _meta: function (e) {
                return { ctrlKey: e.ctrlKey, metaKey: e.metaKey, altKey: e.altKey };
            },
            _calculatePosition: function (e) {
                var pointEvent = (e.pageX === undefined ? e.originalEvent : e),
                    point = new Point(pointEvent.pageX, pointEvent.pageY),
                    offset = this.documentToModel(point);

                return offset;
            },
            _normalizePointZoom: function (point) {
                return point.times(1 / this.zoom());
            },
            _initialize: function () {
                this._zoom = 1.0;
                this._pan = new Point();
                this.shapes = [];
                this._selectedItems = [];
                this.connections = [];
                this._adorners = [];
                this._dataMap = [];
                this.undoRedoService = new UndoRedoService();
                this.id = diagram.randomId();
            },
            _attachEvents: function () {
                var diagram = this;

                if (diagram.scroller) {
                    diagram.bind(ITEMBOUNDSCHANGE, $.proxy(this._autosizeCanvas, this));
                    diagram.bind(CHANGE, $.proxy(this._autosizeCanvas, this));
                    diagram.bind(ZOOM_END, $.proxy(this._autosizeCanvas, this));
                }
            },
            _fetchFreshData: function () {
                this._dataSource();
                if (this.options.autoBind) {
                    this.dataSource.fetch();
                }
            },
            _dataSource: function () {
                var that = this,
                    options = that.options,
                    dataSource = options.dataSource;

                dataSource = isArray(dataSource) ? { data: dataSource } : dataSource;

                if (!dataSource.fields) {
                    dataSource.fields = [
                        { field: "text" },
                        { field: "url" },
                        { field: "spriteCssClass" },
                        { field: "imageUrl" }
                    ];
                }
                if (that.dataSource && that._refreshHandler) {
                    that._unbindDataSource();
                }

                that._refreshHandler = proxy(that._refreshSource, that);
                that._errorHandler = proxy(that._error, that);

                that.dataSource = HierarchicalDataSource.create(dataSource)
                    .bind(CHANGE, that._refreshHandler)
                    .bind(ERROR, that._errorHandler);
            },
            _unbindDataSource: function () {
                var that = this;

                that.dataSource.unbind(CHANGE, that._refreshHandler).unbind(ERROR, that._errorHandler);
            },
            _error: function () {
                // TODO: Do something?
            },
            _adorn: function (adorner, isActive) {
                if (isActive !== undefined && adorner) {
                    if (isActive) {
                        this._adorners.push(adorner);
                        this.adornerLayer.append(adorner.visual);
                    }
                    else {
                        Utils.remove(this._adorners, adorner);
                        this.adornerLayer.remove(adorner.visual);
                    }
                }
            },
            _showConnectors: function (shape, value) {
                if (value) {
                    this._connectorsAdorner.show(shape);
                } else {
                    this._connectorsAdorner.destroy();
                }
            },
            _autosizeCanvas: function (args) {
                if(!this.scroller) { //no need to resize the canvas when no scroller is present
                    return;
                }

                var diagram = (args || {}).sender || this,
                    editor = this._editor,
                    zoom = diagram.zoom(),
                    viewport = diagram.element,
                    viewportSize = new Rect(0, 0, viewport.width(), viewport.height()),
                    cumulativeSize = diagram.boundingBox(diagram.shapes);

                cumulativeSize.width = (cumulativeSize.width + cumulativeSize.x) * zoom;
                cumulativeSize.height = (cumulativeSize.height + cumulativeSize.y) * zoom;

                cumulativeSize = cumulativeSize.union(viewportSize);

                diagram.canvas.size(cumulativeSize);
                if (editor && editor.visible()) {
                    this._positionEditor();
                }
            },
            _updateAdorners: function() {
                var adorners = this._adorners;

                for(var i = 0; i < adorners.length; i++) {
                    var adorner = adorners[i];

                    if (adorner.refreshBounds) {
                        adorner.refreshBounds();
                    }
                    adorner.refresh();
                }
            },

            _refresh: function () {
                var i;
                for (i = 0; i < this.connections.length; i++) {
                    this.connections[i].refresh();
                }
            }
        });

        ui.plugin(Diagram);

        kendo.deepExtend(diagram, {
            Shape: Shape,
            Connection: Connection,
            Connector: Connector
        });
})(window.kendo.jQuery);

return window.kendo;

}, typeof define == 'function' && define.amd ? define : function(_, f){ f(); });