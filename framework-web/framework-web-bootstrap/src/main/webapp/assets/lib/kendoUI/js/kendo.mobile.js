/*
* Kendo UI Complete v2014.1.318 (http://kendoui.com)
* Copyright 2014 Telerik AD. All rights reserved.
*
* Kendo UI Complete commercial licenses may be obtained at
* http://www.telerik.com/purchase/license-agreement/kendo-ui-complete
* If you do not own a commercial license, this file shall be governed by the trial license terms.
*/
(function(f, define){
    define([], f);
})(function(){

/*jshint eqnull: true, loopfunc: true, evil: true, boss: true, freeze: false*/
(function($, undefined) {
    var kendo = window.kendo = window.kendo || { cultures: {} },
        extend = $.extend,
        each = $.each,
        isArray = $.isArray,
        proxy = $.proxy,
        noop = $.noop,
        math = Math,
        Template,
        JSON = window.JSON || {},
        support = {},
        percentRegExp = /%/,
        formatRegExp = /\{(\d+)(:[^\}]+)?\}/g,
        boxShadowRegExp = /(\d+(?:\.?)\d*)px\s*(\d+(?:\.?)\d*)px\s*(\d+(?:\.?)\d*)px\s*(\d+)?/i,
        numberRegExp = /^(\+|-?)\d+(\.?)\d*$/,
        FUNCTION = "function",
        STRING = "string",
        NUMBER = "number",
        OBJECT = "object",
        NULL = "null",
        BOOLEAN = "boolean",
        UNDEFINED = "undefined",
        getterCache = {},
        setterCache = {},
        slice = [].slice,
        globalize = window.Globalize;

    kendo.version = "2014.1.318";

    function Class() {}

    Class.extend = function(proto) {
        var base = function() {},
            member,
            that = this,
            subclass = proto && proto.init ? proto.init : function () {
                that.apply(this, arguments);
            },
            fn;

        base.prototype = that.prototype;
        fn = subclass.fn = subclass.prototype = new base();

        for (member in proto) {
            if (proto[member] != null && proto[member].constructor === Object) {
                // Merge object members
                fn[member] = extend(true, {}, base.prototype[member], proto[member]);
            } else {
                fn[member] = proto[member];
            }
        }

        fn.constructor = subclass;
        subclass.extend = that.extend;

        return subclass;
    };

    Class.prototype._initOptions = function(options) {
        this.options = deepExtend({}, this.options, options);
    };

    var isFunction = kendo.isFunction = function(fn) {
        return typeof fn === "function";
    };

    var preventDefault = function() {
        this._defaultPrevented = true;
    };

    var isDefaultPrevented = function() {
        return this._defaultPrevented === true;
    };

    var Observable = Class.extend({
        init: function() {
            this._events = {};
        },

        bind: function(eventName, handlers, one) {
            var that = this,
                idx,
                eventNames = typeof eventName === STRING ? [eventName] : eventName,
                length,
                original,
                handler,
                handlersIsFunction = typeof handlers === FUNCTION,
                events;

            if (handlers === undefined) {
                for (idx in eventName) {
                    that.bind(idx, eventName[idx]);
                }
                return that;
            }

            for (idx = 0, length = eventNames.length; idx < length; idx++) {
                eventName = eventNames[idx];

                handler = handlersIsFunction ? handlers : handlers[eventName];

                if (handler) {
                    if (one) {
                        original = handler;
                        handler = function() {
                            that.unbind(eventName, handler);
                            original.apply(that, arguments);
                        };
                    }
                    events = that._events[eventName] = that._events[eventName] || [];
                    events.push(handler);
                }
            }

            return that;
        },

        one: function(eventNames, handlers) {
            return this.bind(eventNames, handlers, true);
        },

        first: function(eventName, handlers) {
            var that = this,
                idx,
                eventNames = typeof eventName === STRING ? [eventName] : eventName,
                length,
                handler,
                handlersIsFunction = typeof handlers === FUNCTION,
                events;

            for (idx = 0, length = eventNames.length; idx < length; idx++) {
                eventName = eventNames[idx];

                handler = handlersIsFunction ? handlers : handlers[eventName];

                if (handler) {
                    events = that._events[eventName] = that._events[eventName] || [];
                    events.unshift(handler);
                }
            }

            return that;
        },

        trigger: function(eventName, e) {
            var that = this,
                events = that._events[eventName],
                idx,
                length;

            if (events) {
                e = e || {};

                e.sender = that;

                e._defaultPrevented = false;

                e.preventDefault = preventDefault;

                e.isDefaultPrevented = isDefaultPrevented;

                events = events.slice();

                for (idx = 0, length = events.length; idx < length; idx++) {
                    events[idx].call(that, e);
                }

                return e._defaultPrevented === true;
            }

            return false;
        },

        unbind: function(eventName, handler) {
            var that = this,
                events = that._events[eventName],
                idx;

            if (eventName === undefined) {
                that._events = {};
            } else if (events) {
                if (handler) {
                    for (idx = events.length - 1; idx >= 0; idx--) {
                        if (events[idx] === handler) {
                            events.splice(idx, 1);
                        }
                    }
                } else {
                    that._events[eventName] = [];
                }
            }

            return that;
        }
    });


     function compilePart(part, stringPart) {
         if (stringPart) {
             return "'" +
                 part.split("'").join("\\'")
                     .split('\\"').join('\\\\\\"')
                     .replace(/\n/g, "\\n")
                     .replace(/\r/g, "\\r")
                     .replace(/\t/g, "\\t") + "'";
         } else {
             var first = part.charAt(0),
                 rest = part.substring(1);

             if (first === "=") {
                 return "+(" + rest + ")+";
             } else if (first === ":") {
                 return "+e(" + rest + ")+";
             } else {
                 return ";" + part + ";o+=";
             }
         }
     }

    var argumentNameRegExp = /^\w+/,
        encodeRegExp = /\$\{([^}]*)\}/g,
        escapedCurlyRegExp = /\\\}/g,
        curlyRegExp = /__CURLY__/g,
        escapedSharpRegExp = /\\#/g,
        sharpRegExp = /__SHARP__/g,
        zeros = ["", "0", "00", "000", "0000"];

    Template = {
        paramName: "data", // name of the parameter of the generated template
        useWithBlock: true, // whether to wrap the template in a with() block
        render: function(template, data) {
            var idx,
                length,
                html = "";

            for (idx = 0, length = data.length; idx < length; idx++) {
                html += template(data[idx]);
            }

            return html;
        },
        compile: function(template, options) {
            var settings = extend({}, this, options),
                paramName = settings.paramName,
                argumentName = paramName.match(argumentNameRegExp)[0],
                useWithBlock = settings.useWithBlock,
                functionBody = "var o,e=kendo.htmlEncode;",
                fn,
                parts,
                idx;

            if (isFunction(template)) {
                if (template.length === 2) {
                    //looks like jQuery.template
                    return function(d) {
                        return template($, { data: d }).join("");
                    };
                }
                return template;
            }

            functionBody += useWithBlock ? "with(" + paramName + "){" : "";

            functionBody += "o=";

            parts = template
                .replace(escapedCurlyRegExp, "__CURLY__")
                .replace(encodeRegExp, "#=e($1)#")
                .replace(curlyRegExp, "}")
                .replace(escapedSharpRegExp, "__SHARP__")
                .split("#");

            for (idx = 0; idx < parts.length; idx ++) {
                functionBody += compilePart(parts[idx], idx % 2 === 0);
            }

            functionBody += useWithBlock ? ";}" : ";";

            functionBody += "return o;";

            functionBody = functionBody.replace(sharpRegExp, "#");

            try {
                fn = new Function(argumentName, functionBody);
                fn._slotCount = Math.floor(parts.length / 2);
                return fn;
            } catch(e) {
                throw new Error(kendo.format("Invalid template:'{0}' Generated code:'{1}'", template, functionBody));
            }
        }
    };

function pad(number, digits, end) {
    number = number + "";
    digits = digits || 2;
    end = digits - number.length;

    if (end) {
        return zeros[digits].substring(0, end) + number;
    }

    return number;
}

    //JSON stringify
(function() {
    var escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {
            "\b": "\\b",
            "\t": "\\t",
            "\n": "\\n",
            "\f": "\\f",
            "\r": "\\r",
            "\"" : '\\"',
            "\\": "\\\\"
        },
        rep,
        toString = {}.toString;


    if (typeof Date.prototype.toJSON !== FUNCTION) {

        Date.prototype.toJSON = function () {
            var that = this;

            return isFinite(that.valueOf()) ?
                pad(that.getUTCFullYear(), 4) + "-" +
                pad(that.getUTCMonth() + 1)   + "-" +
                pad(that.getUTCDate())        + "T" +
                pad(that.getUTCHours())       + ":" +
                pad(that.getUTCMinutes())     + ":" +
                pad(that.getUTCSeconds())     + "Z" : null;
        };

        String.prototype.toJSON = Number.prototype.toJSON = Boolean.prototype.toJSON = function () {
            return this.valueOf();
        };
    }

    function quote(string) {
        escapable.lastIndex = 0;
        return escapable.test(string) ? "\"" + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === STRING ? c :
                "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4);
        }) + "\"" : "\"" + string + "\"";
    }

    function str(key, holder) {
        var i,
            k,
            v,
            length,
            mind = gap,
            partial,
            value = holder[key],
            type;

        if (value && typeof value === OBJECT && typeof value.toJSON === FUNCTION) {
            value = value.toJSON(key);
        }

        if (typeof rep === FUNCTION) {
            value = rep.call(holder, key, value);
        }

        type = typeof value;
        if (type === STRING) {
            return quote(value);
        } else if (type === NUMBER) {
            return isFinite(value) ? String(value) : NULL;
        } else if (type === BOOLEAN || type === NULL) {
            return String(value);
        } else if (type === OBJECT) {
            if (!value) {
                return NULL;
            }
            gap += indent;
            partial = [];
            if (toString.apply(value) === "[object Array]") {
                length = value.length;
                for (i = 0; i < length; i++) {
                    partial[i] = str(i, value) || NULL;
                }
                v = partial.length === 0 ? "[]" : gap ?
                    "[\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "]" :
                    "[" + partial.join(",") + "]";
                gap = mind;
                return v;
            }
            if (rep && typeof rep === OBJECT) {
                length = rep.length;
                for (i = 0; i < length; i++) {
                    if (typeof rep[i] === STRING) {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ": " : ":") + v);
                        }
                    }
                }
            } else {
                for (k in value) {
                    if (Object.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ": " : ":") + v);
                        }
                    }
                }
            }

            v = partial.length === 0 ? "{}" : gap ?
                "{\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "}" :
                "{" + partial.join(",") + "}";
            gap = mind;
            return v;
        }
    }

    if (typeof JSON.stringify !== FUNCTION) {
        JSON.stringify = function (value, replacer, space) {
            var i;
            gap = "";
            indent = "";

            if (typeof space === NUMBER) {
                for (i = 0; i < space; i += 1) {
                    indent += " ";
                }

            } else if (typeof space === STRING) {
                indent = space;
            }

            rep = replacer;
            if (replacer && typeof replacer !== FUNCTION && (typeof replacer !== OBJECT || typeof replacer.length !== NUMBER)) {
                throw new Error("JSON.stringify");
            }

            return str("", {"": value});
        };
    }
})();

// Date and Number formatting
(function() {
    var dateFormatRegExp = /dddd|ddd|dd|d|MMMM|MMM|MM|M|yyyy|yy|HH|H|hh|h|mm|m|fff|ff|f|tt|ss|s|"[^"]*"|'[^']*'/g,
        standardFormatRegExp =  /^(n|c|p|e)(\d*)$/i,
        literalRegExp = /(\\.)|(['][^']*[']?)|(["][^"]*["]?)/g,
        commaRegExp = /\,/g,
        EMPTY = "",
        POINT = ".",
        COMMA = ",",
        SHARP = "#",
        ZERO = "0",
        PLACEHOLDER = "??",
        EN = "en-US",
        objectToString = {}.toString;

    //cultures
    kendo.cultures["en-US"] = {
        name: EN,
        numberFormat: {
            pattern: ["-n"],
            decimals: 2,
            ",": ",",
            ".": ".",
            groupSize: [3],
            percent: {
                pattern: ["-n %", "n %"],
                decimals: 2,
                ",": ",",
                ".": ".",
                groupSize: [3],
                symbol: "%"
            },
            currency: {
                pattern: ["($n)", "$n"],
                decimals: 2,
                ",": ",",
                ".": ".",
                groupSize: [3],
                symbol: "$"
            }
        },
        calendars: {
            standard: {
                days: {
                    names: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
                    namesAbbr: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
                    namesShort: [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]
                },
                months: {
                    names: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
                    namesAbbr: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
                },
                AM: [ "AM", "am", "AM" ],
                PM: [ "PM", "pm", "PM" ],
                patterns: {
                    d: "M/d/yyyy",
                    D: "dddd, MMMM dd, yyyy",
                    F: "dddd, MMMM dd, yyyy h:mm:ss tt",
                    g: "M/d/yyyy h:mm tt",
                    G: "M/d/yyyy h:mm:ss tt",
                    m: "MMMM dd",
                    M: "MMMM dd",
                    s: "yyyy'-'MM'-'ddTHH':'mm':'ss",
                    t: "h:mm tt",
                    T: "h:mm:ss tt",
                    u: "yyyy'-'MM'-'dd HH':'mm':'ss'Z'",
                    y: "MMMM, yyyy",
                    Y: "MMMM, yyyy"
                },
                "/": "/",
                ":": ":",
                firstDay: 0,
                twoDigitYearMax: 2029
            }
        }
    };


     function findCulture(culture) {
        if (culture) {
            if (culture.numberFormat) {
                return culture;
            }

            if (typeof culture === STRING) {
                var cultures = kendo.cultures;
                return cultures[culture] || cultures[culture.split("-")[0]] || null;
            }

            return null;
        }

        return null;
    }

    function getCulture(culture) {
        if (culture) {
            culture = findCulture(culture);
        }

        return culture || kendo.cultures.current;
    }

    function expandNumberFormat(numberFormat) {
        numberFormat.groupSizes = numberFormat.groupSize;
        numberFormat.percent.groupSizes = numberFormat.percent.groupSize;
        numberFormat.currency.groupSizes = numberFormat.currency.groupSize;
    }

    kendo.culture = function(cultureName) {
        var cultures = kendo.cultures, culture;

        if (cultureName !== undefined) {
            culture = findCulture(cultureName) || cultures[EN];
            culture.calendar = culture.calendars.standard;
            cultures.current = culture;

            if (globalize) {
                expandNumberFormat(culture.numberFormat);
            }

        } else {
            return cultures.current;
        }
    };

    kendo.findCulture = findCulture;
    kendo.getCulture = getCulture;

    //set current culture to en-US.
    kendo.culture(EN);

    function formatDate(date, format, culture) {
        culture = getCulture(culture);

        var calendar = culture.calendars.standard,
            days = calendar.days,
            months = calendar.months;

        format = calendar.patterns[format] || format;

        return format.replace(dateFormatRegExp, function (match) {
            var result;

            if (match === "d") {
                result = date.getDate();
            } else if (match === "dd") {
                result = pad(date.getDate());
            } else if (match === "ddd") {
                result = days.namesAbbr[date.getDay()];
            } else if (match === "dddd") {
                result = days.names[date.getDay()];
            } else if (match === "M") {
                result = date.getMonth() + 1;
            } else if (match === "MM") {
                result = pad(date.getMonth() + 1);
            } else if (match === "MMM") {
                result = months.namesAbbr[date.getMonth()];
            } else if (match === "MMMM") {
                result = months.names[date.getMonth()];
            } else if (match === "yy") {
                result = pad(date.getFullYear() % 100);
            } else if (match === "yyyy") {
                result = pad(date.getFullYear(), 4);
            } else if (match === "h" ) {
                result = date.getHours() % 12 || 12;
            } else if (match === "hh") {
                result = pad(date.getHours() % 12 || 12);
            } else if (match === "H") {
                result = date.getHours();
            } else if (match === "HH") {
                result = pad(date.getHours());
            } else if (match === "m") {
                result = date.getMinutes();
            } else if (match === "mm") {
                result = pad(date.getMinutes());
            } else if (match === "s") {
                result = date.getSeconds();
            } else if (match === "ss") {
                result = pad(date.getSeconds());
            } else if (match === "f") {
                result = math.floor(date.getMilliseconds() / 100);
            } else if (match === "ff") {
                result = math.floor(date.getMilliseconds() / 10);
            } else if (match === "fff") {
                result = date.getMilliseconds();
            } else if (match === "tt") {
                result = date.getHours() < 12 ? calendar.AM[0] : calendar.PM[0];
            }

            return result !== undefined ? result : match.slice(1, match.length - 1);
        });
    }

    //number formatting
    function formatNumber(number, format, culture) {
        culture = getCulture(culture);

        var numberFormat = culture.numberFormat,
            groupSize = numberFormat.groupSize[0],
            groupSeparator = numberFormat[COMMA],
            decimal = numberFormat[POINT],
            precision = numberFormat.decimals,
            pattern = numberFormat.pattern[0],
            literals = [],
            symbol,
            isCurrency, isPercent,
            customPrecision,
            formatAndPrecision,
            negative = number < 0,
            integer,
            fraction,
            integerLength,
            fractionLength,
            replacement = EMPTY,
            value = EMPTY,
            idx,
            length,
            ch,
            hasGroup,
            hasNegativeFormat,
            decimalIndex,
            sharpIndex,
            zeroIndex,
            hasZero, hasSharp,
            percentIndex,
            currencyIndex,
            startZeroIndex,
            start = -1,
            end;

        //return empty string if no number
        if (number === undefined) {
            return EMPTY;
        }

        if (!isFinite(number)) {
            return number;
        }

        //if no format then return number.toString() or number.toLocaleString() if culture.name is not defined
        if (!format) {
            return culture.name.length ? number.toLocaleString() : number.toString();
        }

        formatAndPrecision = standardFormatRegExp.exec(format);

        // standard formatting
        if (formatAndPrecision) {
            format = formatAndPrecision[1].toLowerCase();

            isCurrency = format === "c";
            isPercent = format === "p";

            if (isCurrency || isPercent) {
                //get specific number format information if format is currency or percent
                numberFormat = isCurrency ? numberFormat.currency : numberFormat.percent;
                groupSize = numberFormat.groupSize[0];
                groupSeparator = numberFormat[COMMA];
                decimal = numberFormat[POINT];
                precision = numberFormat.decimals;
                symbol = numberFormat.symbol;
                pattern = numberFormat.pattern[negative ? 0 : 1];
            }

            customPrecision = formatAndPrecision[2];

            if (customPrecision) {
                precision = +customPrecision;
            }

            //return number in exponential format
            if (format === "e") {
                return customPrecision ? number.toExponential(precision) : number.toExponential(); // toExponential() and toExponential(undefined) differ in FF #653438.
            }

            // multiply if format is percent
            if (isPercent) {
                number *= 100;
            }

            number = round(number, precision);
            negative = number < 0;
            number = number.split(POINT);

            integer = number[0];
            fraction = number[1];

            //exclude "-" if number is negative.
            if (negative) {
                integer = integer.substring(1);
            }

            value = integer;
            integerLength = integer.length;

            //add group separator to the number if it is longer enough
            if (integerLength >= groupSize) {
                value = EMPTY;
                for (idx = 0; idx < integerLength; idx++) {
                    if (idx > 0 && (integerLength - idx) % groupSize === 0) {
                        value += groupSeparator;
                    }
                    value += integer.charAt(idx);
                }
            }

            if (fraction) {
                value += decimal + fraction;
            }

            if (format === "n" && !negative) {
                return value;
            }

            number = EMPTY;

            for (idx = 0, length = pattern.length; idx < length; idx++) {
                ch = pattern.charAt(idx);

                if (ch === "n") {
                    number += value;
                } else if (ch === "$" || ch === "%") {
                    number += symbol;
                } else {
                    number += ch;
                }
            }

            return number;
        }

        //custom formatting
        //
        //separate format by sections.

        //make number positive
        if (negative) {
            number = -number;
        }

        if (format.indexOf("'") > -1 || format.indexOf("\"") > -1 || format.indexOf("\\") > -1) {
            format = format.replace(literalRegExp, function (match) {
                var quoteChar = match.charAt(0).replace("\\", ""),
                    literal = match.slice(1).replace(quoteChar, "");

                literals.push(literal);

                return PLACEHOLDER;
            });
        }

        format = format.split(";");
        if (negative && format[1]) {
            //get negative format
            format = format[1];
            hasNegativeFormat = true;
        } else if (number === 0) {
            //format for zeros
            format = format[2] || format[0];
            if (format.indexOf(SHARP) == -1 && format.indexOf(ZERO) == -1) {
                //return format if it is string constant.
                return format;
            }
        } else {
            format = format[0];
        }

        percentIndex = format.indexOf("%");
        currencyIndex = format.indexOf("$");

        isPercent = percentIndex != -1;
        isCurrency = currencyIndex != -1;

        //multiply number if the format has percent
        if (isPercent) {
            number *= 100;
        }

        if (isCurrency && format[currencyIndex - 1] === "\\") {
            format = format.split("\\").join("");
            isCurrency = false;
        }

        if (isCurrency || isPercent) {
            //get specific number format information if format is currency or percent
            numberFormat = isCurrency ? numberFormat.currency : numberFormat.percent;
            groupSize = numberFormat.groupSize[0];
            groupSeparator = numberFormat[COMMA];
            decimal = numberFormat[POINT];
            precision = numberFormat.decimals;
            symbol = numberFormat.symbol;
        }

        hasGroup = format.indexOf(COMMA) > -1;
        if (hasGroup) {
            format = format.replace(commaRegExp, EMPTY);
        }

        decimalIndex = format.indexOf(POINT);
        length = format.length;

        if (decimalIndex != -1) {
            fraction = number.toString().split("e");
            if (fraction[1]) {
                fraction = round(number, Math.abs(fraction[1]));
            } else {
                fraction = fraction[0];
            }
            fraction = fraction.split(POINT)[1] || EMPTY;
            zeroIndex = format.lastIndexOf(ZERO) - decimalIndex;
            sharpIndex = format.lastIndexOf(SHARP) - decimalIndex;
            hasZero = zeroIndex > -1;
            hasSharp = sharpIndex > -1;
            idx = fraction.length;

            if (!hasZero && !hasSharp) {
                format = format.substring(0, decimalIndex) + format.substring(decimalIndex + 1);
                length = format.length;
                decimalIndex = -1;
                idx = 0;
            } if (hasZero && zeroIndex > sharpIndex) {
                idx = zeroIndex;
            } else if (sharpIndex > zeroIndex) {
                if (hasSharp && idx > sharpIndex) {
                    idx = sharpIndex;
                } else if (hasZero && idx < zeroIndex) {
                    idx = zeroIndex;
                }
            }

            if (idx > -1) {
                number = round(number, idx);
            }
        } else {
            number = round(number);
        }

        sharpIndex = format.indexOf(SHARP);
        startZeroIndex = zeroIndex = format.indexOf(ZERO);

        //define the index of the first digit placeholder
        if (sharpIndex == -1 && zeroIndex != -1) {
            start = zeroIndex;
        } else if (sharpIndex != -1 && zeroIndex == -1) {
            start = sharpIndex;
        } else {
            start = sharpIndex > zeroIndex ? zeroIndex : sharpIndex;
        }

        sharpIndex = format.lastIndexOf(SHARP);
        zeroIndex = format.lastIndexOf(ZERO);

        //define the index of the last digit placeholder
        if (sharpIndex == -1 && zeroIndex != -1) {
            end = zeroIndex;
        } else if (sharpIndex != -1 && zeroIndex == -1) {
            end = sharpIndex;
        } else {
            end = sharpIndex > zeroIndex ? sharpIndex : zeroIndex;
        }

        if (start == length) {
            end = start;
        }

        if (start != -1) {
            value = number.toString().split(POINT);
            integer = value[0];
            fraction = value[1] || EMPTY;

            integerLength = integer.length;
            fractionLength = fraction.length;

            if (negative && (number * -1) >= 0) {
                negative = false;
            }

            //add group separator to the number if it is longer enough
            if (hasGroup) {
                if (integerLength === groupSize && integerLength < decimalIndex - startZeroIndex) {
                    integer = groupSeparator + integer;
                } else if (integerLength > groupSize) {
                    value = EMPTY;
                    for (idx = 0; idx < integerLength; idx++) {
                        if (idx > 0 && (integerLength - idx) % groupSize === 0) {
                            value += groupSeparator;
                        }
                        value += integer.charAt(idx);
                    }

                    integer = value;
                }
            }

            number = format.substring(0, start);

            if (negative && !hasNegativeFormat) {
                number += "-";
            }

            for (idx = start; idx < length; idx++) {
                ch = format.charAt(idx);

                if (decimalIndex == -1) {
                    if (end - idx < integerLength) {
                        number += integer;
                        break;
                    }
                } else {
                    if (zeroIndex != -1 && zeroIndex < idx) {
                        replacement = EMPTY;
                    }

                    if ((decimalIndex - idx) <= integerLength && decimalIndex - idx > -1) {
                        number += integer;
                        idx = decimalIndex;
                    }

                    if (decimalIndex === idx) {
                        number += (fraction ? decimal : EMPTY) + fraction;
                        idx += end - decimalIndex + 1;
                        continue;
                    }
                }

                if (ch === ZERO) {
                    number += ch;
                    replacement = ch;
                } else if (ch === SHARP) {
                    number += replacement;
                }
            }

            if (end >= start) {
                number += format.substring(end + 1);
            }

            //replace symbol placeholders
            if (isCurrency || isPercent) {
                value = EMPTY;
                for (idx = 0, length = number.length; idx < length; idx++) {
                    ch = number.charAt(idx);
                    value += (ch === "$" || ch === "%") ? symbol : ch;
                }
                number = value;
            }

            length = literals.length;

            if (length) {
                for (idx = 0; idx < length; idx++) {
                    number = number.replace(PLACEHOLDER, literals[idx]);
                }
            }
        }

        return number;
    }

    var round = function(value, precision) {
        precision = precision || 0;

        value = value.toString().split('e');
        value = Math.round(+(value[0] + 'e' + (value[1] ? (+value[1] + precision) : precision)));

        value = value.toString().split('e');
        value = +(value[0] + 'e' + (value[1] ? (+value[1] - precision) : -precision));

        return value.toFixed(precision);
    };

    var toString = function(value, fmt, culture) {
        if (fmt) {
            if (objectToString.call(value) === "[object Date]") {
                return formatDate(value, fmt, culture);
            } else if (typeof value === NUMBER) {
                return formatNumber(value, fmt, culture);
            }
        }

        return value !== undefined ? value : "";
    };

    if (globalize) {
        toString = function(value, format, culture) {
            if ($.isPlainObject(culture)) {
                culture = culture.name;
            }

            return globalize.format(value, format, culture);
        };
    }

    kendo.format = function(fmt) {
        var values = arguments;

        return fmt.replace(formatRegExp, function(match, index, placeholderFormat) {
            var value = values[parseInt(index, 10) + 1];

            return toString(value, placeholderFormat ? placeholderFormat.substring(1) : "");
        });
    };

    kendo._extractFormat = function (format) {
        if (format.slice(0,3) === "{0:") {
            format = format.slice(3, format.length - 1);
        }

        return format;
    };

    kendo._activeElement = function() {
        try {
            return document.activeElement;
        } catch(e) {
            return document.documentElement.activeElement;
        }
    };

    kendo._round = round;
    kendo.toString = toString;
})();


(function() {
    var nonBreakingSpaceRegExp = /\u00A0/g,
        exponentRegExp = /[eE][\-+]?[0-9]+/,
        shortTimeZoneRegExp = /[+|\-]\d{1,2}/,
        longTimeZoneRegExp = /[+|\-]\d{1,2}:\d{2}/,
        dateRegExp = /^\/Date\((.*?)\)\/$/,
        signRegExp = /[+-]/,
        formatsSequence = ["G", "g", "d", "F", "D", "y", "m", "T", "t"],
        numberRegExp = {
            2: /^\d{1,2}/,
            3: /^\d{1,3}/,
            4: /^\d{4}/
        },
        objectToString = {}.toString;

    function outOfRange(value, start, end) {
        return !(value >= start && value <= end);
    }

    function designatorPredicate(designator) {
        return designator.charAt(0);
    }

    function mapDesignators(designators) {
        return $.map(designators, designatorPredicate);
    }

    //if date's day is different than the typed one - adjust
    function adjustDST(date, hours) {
        if (!hours && date.getHours() === 23) {
            date.setHours(date.getHours() + 2);
        }
    }

    function lowerArray(data) {
        var idx = 0,
            length = data.length,
            array = [];

        for (; idx < length; idx++) {
            array[idx] = (data[idx] + "").toLowerCase();
        }

        return array;
    }

    function lowerLocalInfo(localInfo) {
        var newLocalInfo = {}, property;

        for (property in localInfo) {
            newLocalInfo[property] = lowerArray(localInfo[property]);
        }

        return newLocalInfo;
    }

    function parseExact(value, format, culture) {
        if (!value) {
            return null;
        }

        var lookAhead = function (match) {
                var i = 0;
                while (format[idx] === match) {
                    i++;
                    idx++;
                }
                if (i > 0) {
                    idx -= 1;
                }
                return i;
            },
            getNumber = function(size) {
                var rg = numberRegExp[size] || new RegExp('^\\d{1,' + size + '}'),
                    match = value.substr(valueIdx, size).match(rg);

                if (match) {
                    match = match[0];
                    valueIdx += match.length;
                    return parseInt(match, 10);
                }
                return null;
            },
            getIndexByName = function (names, lower) {
                var i = 0,
                    length = names.length,
                    name, nameLength,
                    subValue;

                for (; i < length; i++) {
                    name = names[i];
                    nameLength = name.length;
                    subValue = value.substr(valueIdx, nameLength);

                    if (lower) {
                        subValue = subValue.toLowerCase();
                    }

                    if (subValue == name) {
                        valueIdx += nameLength;
                        return i + 1;
                    }
                }
                return null;
            },
            checkLiteral = function() {
                var result = false;
                if (value.charAt(valueIdx) === format[idx]) {
                    valueIdx++;
                    result = true;
                }
                return result;
            },
            calendar = culture.calendars.standard,
            year = null,
            month = null,
            day = null,
            hours = null,
            minutes = null,
            seconds = null,
            milliseconds = null,
            idx = 0,
            valueIdx = 0,
            literal = false,
            date = new Date(),
            twoDigitYearMax = calendar.twoDigitYearMax || 2029,
            defaultYear = date.getFullYear(),
            ch, count, length, pattern,
            pmHour, UTC, ISO8601, matches,
            amDesignators, pmDesignators,
            hoursOffset, minutesOffset,
            hasTime, match;

        if (!format) {
            format = "d"; //shord date format
        }

        //if format is part of the patterns get real format
        pattern = calendar.patterns[format];
        if (pattern) {
            format = pattern;
        }

        format = format.split("");
        length = format.length;

        for (; idx < length; idx++) {
            ch = format[idx];

            if (literal) {
                if (ch === "'") {
                    literal = false;
                } else {
                    checkLiteral();
                }
            } else {
                if (ch === "d") {
                    count = lookAhead("d");
                    if (!calendar._lowerDays) {
                        calendar._lowerDays = lowerLocalInfo(calendar.days);
                    }

                    day = count < 3 ? getNumber(2) : getIndexByName(calendar._lowerDays[count == 3 ? "namesAbbr" : "names"], true);

                    if (day === null || outOfRange(day, 1, 31)) {
                        return null;
                    }
                } else if (ch === "M") {
                    count = lookAhead("M");
                    if (!calendar._lowerMonths) {
                        calendar._lowerMonths = lowerLocalInfo(calendar.months);
                    }
                    month = count < 3 ? getNumber(2) : getIndexByName(calendar._lowerMonths[count == 3 ? 'namesAbbr' : 'names'], true);

                    if (month === null || outOfRange(month, 1, 12)) {
                        return null;
                    }
                    month -= 1; //because month is zero based
                } else if (ch === "y") {
                    count = lookAhead("y");
                    year = getNumber(count);

                    if (year === null) {
                        return null;
                    }

                    if (count == 2) {
                        if (typeof twoDigitYearMax === "string") {
                            twoDigitYearMax = defaultYear + parseInt(twoDigitYearMax, 10);
                        }

                        year = (defaultYear - defaultYear % 100) + year;
                        if (year > twoDigitYearMax) {
                            year -= 100;
                        }
                    }
                } else if (ch === "h" ) {
                    lookAhead("h");
                    hours = getNumber(2);
                    if (hours == 12) {
                        hours = 0;
                    }
                    if (hours === null || outOfRange(hours, 0, 11)) {
                        return null;
                    }
                } else if (ch === "H") {
                    lookAhead("H");
                    hours = getNumber(2);
                    if (hours === null || outOfRange(hours, 0, 23)) {
                        return null;
                    }
                } else if (ch === "m") {
                    lookAhead("m");
                    minutes = getNumber(2);
                    if (minutes === null || outOfRange(minutes, 0, 59)) {
                        return null;
                    }
                } else if (ch === "s") {
                    lookAhead("s");
                    seconds = getNumber(2);
                    if (seconds === null || outOfRange(seconds, 0, 59)) {
                        return null;
                    }
                } else if (ch === "f") {
                    count = lookAhead("f");

                    match = value.substr(valueIdx, count).match(numberRegExp[3]);
                    milliseconds = getNumber(count);

                    if (milliseconds !== null) {
                        match = match[0].length;

                        if (match < 3) {
                            milliseconds *= Math.pow(10, (3 - match));
                        }

                        if (count > 3) {
                            milliseconds = parseInt(milliseconds.toString().substring(0, 3), 10);
                        }
                    }

                    if (milliseconds === null || outOfRange(milliseconds, 0, 999)) {
                        return null;
                    }

                } else if (ch === "t") {
                    count = lookAhead("t");
                    amDesignators = calendar.AM;
                    pmDesignators = calendar.PM;

                    if (count === 1) {
                        amDesignators = mapDesignators(amDesignators);
                        pmDesignators = mapDesignators(pmDesignators);
                    }

                    pmHour = getIndexByName(pmDesignators);
                    if (!pmHour && !getIndexByName(amDesignators)) {
                        return null;
                    }
                }
                else if (ch === "z") {
                    UTC = true;
                    count = lookAhead("z");

                    if (value.substr(valueIdx, 1) === "Z") {
                        if (!ISO8601) {
                            return null;
                        }

                        checkLiteral();
                        continue;
                    }

                    matches = value.substr(valueIdx, 6)
                                   .match(count > 2 ? longTimeZoneRegExp : shortTimeZoneRegExp);

                    if (!matches) {
                        return null;
                    }

                    matches = matches[0];
                    valueIdx = matches.length;
                    matches = matches.split(":");

                    hoursOffset = parseInt(matches[0], 10);
                    if (outOfRange(hoursOffset, -12, 13)) {
                        return null;
                    }

                    if (count > 2) {
                        minutesOffset = parseInt(matches[1], 10);
                        if (isNaN(minutesOffset) || outOfRange(minutesOffset, 0, 59)) {
                            return null;
                        }
                    }
                } else if (ch === "T") {
                    ISO8601 = checkLiteral();
                } else if (ch === "'") {
                    literal = true;
                    checkLiteral();
                } else if (!checkLiteral()) {
                    return null;
                }
            }
        }

        hasTime = hours !== null || minutes !== null || seconds || null;

        if (year === null && month === null && day === null && hasTime) {
            year = defaultYear;
            month = date.getMonth();
            day = date.getDate();
        } else {
            if (year === null) {
                year = defaultYear;
            }

            if (day === null) {
                day = 1;
            }
        }

        if (pmHour && hours < 12) {
            hours += 12;
        }

        if (UTC) {
            if (hoursOffset) {
                hours += -hoursOffset;
            }

            if (minutesOffset) {
                minutes += -minutesOffset;
            }

            value = new Date(Date.UTC(year, month, day, hours, minutes, seconds, milliseconds));
        } else {
            value = new Date(year, month, day, hours, minutes, seconds, milliseconds);
            adjustDST(value, hours);
        }

        if (year < 100) {
            value.setFullYear(year);
        }

        if (value.getDate() !== day && UTC === undefined) {
            return null;
        }

        return value;
    }

    kendo.parseDate = function(value, formats, culture) {
        if (objectToString.call(value) === "[object Date]") {
            return value;
        }

        var idx = 0;
        var date = null;
        var length, patterns;
        var tzoffset;

        if (value && value.indexOf("/D") === 0) {
            date = dateRegExp.exec(value);
            if (date) {
                tzoffset = date = date[1];

                date = parseInt(date, 10);

                tzoffset = tzoffset.substring(1).split(signRegExp)[1];

                if (tzoffset) {
                    date -= (parseInt(tzoffset, 10) * kendo.date.MS_PER_MINUTE);
                }

                return new Date(date);
            }
        }

        culture = kendo.getCulture(culture);

        if (!formats) {
            formats = [];
            patterns = culture.calendar.patterns;
            length = formatsSequence.length;

            for (; idx < length; idx++) {
                formats[idx] = patterns[formatsSequence[idx]];
            }

            idx = 0;

            formats = [
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm",
                "yyyy/MM/dd",
                "ddd MMM dd yyyy HH:mm:ss",
                "yyyy-MM-ddTHH:mm:ss.fffffffzzz",
                "yyyy-MM-ddTHH:mm:ss.fffzzz",
                "yyyy-MM-ddTHH:mm:sszzz",
                "yyyy-MM-ddTHH:mmzzz",
                "yyyy-MM-ddTHH:mmzz",
                "yyyy-MM-ddTHH:mm:ss",
                "yyyy-MM-ddTHH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd",
                "HH:mm:ss",
                "HH:mm"
            ].concat(formats);
        }

        formats = isArray(formats) ? formats: [formats];
        length = formats.length;

        for (; idx < length; idx++) {
            date = parseExact(value, formats[idx], culture);
            if (date) {
                return date;
            }
        }

        return date;
    };

    kendo.parseInt = function(value, culture) {
        var result = kendo.parseFloat(value, culture);
        if (result) {
            result = result | 0;
        }
        return result;
    };

    kendo.parseFloat = function(value, culture, format) {
        if (!value && value !== 0) {
           return null;
        }

        if (typeof value === NUMBER) {
           return value;
        }

        value = value.toString();
        culture = kendo.getCulture(culture);

        var number = culture.numberFormat,
            percent = number.percent,
            currency = number.currency,
            symbol = currency.symbol,
            percentSymbol = percent.symbol,
            negative = value.indexOf("-"),
            parts, isPercent;

        //handle exponential number
        if (exponentRegExp.test(value)) {
            value = parseFloat(value.replace(number["."], "."));
            if (isNaN(value)) {
                value = null;
            }
            return value;
        }

        if (negative > 0) {
            return null;
        } else {
            negative = negative > -1;
        }

        if (value.indexOf(symbol) > -1 || (format && format.toLowerCase().indexOf("c") > -1)) {
            number = currency;
            parts = number.pattern[0].replace("$", symbol).split("n");
            if (value.indexOf(parts[0]) > -1 && value.indexOf(parts[1]) > -1) {
                value = value.replace(parts[0], "").replace(parts[1], "");
                negative = true;
            }
        } else if (value.indexOf(percentSymbol) > -1) {
            isPercent = true;
            number = percent;
            symbol = percentSymbol;
        }

        value = value.replace("-", "")
                     .replace(symbol, "")
                     .replace(nonBreakingSpaceRegExp, " ")
                     .split(number[","].replace(nonBreakingSpaceRegExp, " ")).join("")
                     .replace(number["."], ".");

        value = parseFloat(value);

        if (isNaN(value)) {
            value = null;
        } else if (negative) {
            value *= -1;
        }

        if (value && isPercent) {
            value /= 100;
        }

        return value;
    };

    if (globalize) {
        kendo.parseDate = function (value, format, culture) {
            if (objectToString.call(value) === "[object Date]") {
                return value;
            }

            return globalize.parseDate(value, format, culture);
        };

        kendo.parseFloat = function (value, culture) {
            if (typeof value === NUMBER) {
                return value;
            }

            if (value === undefined || value === null) {
               return null;
            }

            if ($.isPlainObject(culture)) {
                culture = culture.name;
            }

            value = globalize.parseFloat(value, culture);

            return isNaN(value) ? null : value;
        };
    }
})();

    function getShadows(element) {
        var shadow = element.css(kendo.support.transitions.css + "box-shadow") || element.css("box-shadow"),
            radius = shadow ? shadow.match(boxShadowRegExp) || [ 0, 0, 0, 0, 0 ] : [ 0, 0, 0, 0, 0 ],
            blur = math.max((+radius[3]), +(radius[4] || 0));

        return {
            left: (-radius[1]) + blur,
            right: (+radius[1]) + blur,
            bottom: (+radius[2]) + blur
        };
    }

    function wrap(element, autosize) {
        var browser = support.browser,
            percentage,
            isRtl = element.css("direction") == "rtl";

        if (!element.parent().hasClass("k-animation-container")) {
            var shadows = getShadows(element),
                width = element[0].style.width,
                height = element[0].style.height,
                percentWidth = percentRegExp.test(width),
                percentHeight = percentRegExp.test(height);

            if (browser.opera) { // Box shadow can't be retrieved in Opera
                shadows.left = shadows.right = shadows.bottom = 5;
            }

            percentage = percentWidth || percentHeight;

            if (!percentWidth && (!autosize || (autosize && width))) { width = element.outerWidth(); }
            if (!percentHeight && (!autosize || (autosize && height))) { height = element.outerHeight(); }

            element.wrap(
                         $("<div/>")
                         .addClass("k-animation-container")
                         .css({
                             width: width,
                             height: height,
                             marginLeft: shadows.left * (isRtl ? 1 : -1),
                             paddingLeft: shadows.left,
                             paddingRight: shadows.right,
                             paddingBottom: shadows.bottom
                         }));

            if (percentage) {
                element.css({
                    width: "100%",
                    height: "100%",
                    boxSizing: "border-box",
                    mozBoxSizing: "border-box",
                    webkitBoxSizing: "border-box"
                });
            }
        } else {
            var wrapper = element.parent(".k-animation-container"),
                wrapperStyle = wrapper[0].style;

            if (wrapper.is(":hidden")) {
                wrapper.show();
            }

            percentage = percentRegExp.test(wrapperStyle.width) || percentRegExp.test(wrapperStyle.height);

            if (!percentage) {
                wrapper.css({
                    width: element.outerWidth(),
                    height: element.outerHeight(),
                    boxSizing: "content-box",
                    mozBoxSizing: "content-box",
                    webkitBoxSizing: "content-box"
                });
            }
        }

        if (browser.msie && math.floor(browser.version) <= 7) {
            element.css({ zoom: 1 });
            element.children(".k-menu").width(element.width());
        }

        return element.parent();
    }

    function deepExtend(destination) {
        var i = 1,
            length = arguments.length;

        for (i = 1; i < length; i++) {
            deepExtendOne(destination, arguments[i]);
        }

        return destination;
    }

    function deepExtendOne(destination, source) {
        var ObservableArray = kendo.data.ObservableArray,
            DataSource = kendo.data.DataSource,
            HierarchicalDataSource = kendo.data.HierarchicalDataSource,
            property,
            propValue,
            propType,
            destProp;

        for (property in source) {
            propValue = source[property];
            propType = typeof propValue;
            if (propType === OBJECT && propValue !== null &&
                propValue.constructor !== Array && propValue.constructor !== ObservableArray &&
                propValue.constructor !== DataSource && propValue.constructor !== HierarchicalDataSource) {
                if (propValue instanceof Date) {
                    destination[property] = new Date(propValue.getTime());
                } else if (isFunction(propValue.clone)) {
                    destination[property] = propValue.clone();
                } else {
                    destProp = destination[property];
                    if (typeof (destProp) === OBJECT) {
                        destination[property] = destProp || {};
                    } else {
                        destination[property] = {};
                    }
                    deepExtendOne(destination[property], propValue);
                }
            } else if (propType !== UNDEFINED) {
                destination[property] = propValue;
            }
        }

        return destination;
    }

    function testRx(agent, rxs, dflt) {
        for (var rx in rxs) {
            if (rxs.hasOwnProperty(rx) && rxs[rx].test(agent)) {
                return rx;
            }
        }
        return dflt !== undefined ? dflt : agent;
    }

    function toHyphens(str) {
        return str.replace(/([a-z][A-Z])/g, function (g) {
            return g.charAt(0) + '-' + g.charAt(1).toLowerCase();
        });
    }

    function toCamelCase(str) {
        return str.replace(/\-(\w)/g, function (strMatch, g1) {
            return g1.toUpperCase();
        });
    }

    function getComputedStyles(element, properties) {
        var styles = {}, computedStyle;

        if (document.defaultView && document.defaultView.getComputedStyle) {
            computedStyle = document.defaultView.getComputedStyle(element, "");

            if (properties) {
                $.each(properties, function(idx, value) {
                    styles[value] = computedStyle.getPropertyValue(value);
                });
            }
        } else {
            computedStyle = element.currentStyle;

            if (properties) {
                $.each(properties, function(idx, value) {
                    styles[value] = computedStyle[toCamelCase(value)];
                });
            }
        }

        if (!kendo.size(styles)) {
            styles = computedStyle;
        }

        return styles;
    }

    (function() {
        support.scrollbar = function() {
            var div = document.createElement("div"),
                result;

            div.style.cssText = "overflow:scroll;overflow-x:hidden;zoom:1;clear:both";
            div.innerHTML = "&nbsp;";
            document.body.appendChild(div);

            result = div.offsetWidth - div.scrollWidth;

            document.body.removeChild(div);
            return result;
        };

        support.isRtl = function(element) {
            return $(element).closest(".k-rtl").length > 0;
        };

        var table = document.createElement("table");

        // Internet Explorer does not support setting the innerHTML of TBODY and TABLE elements
        try {
            table.innerHTML = "<tr><td></td></tr>";

            support.tbodyInnerHtml = true;
        } catch (e) {
            support.tbodyInnerHtml = false;
        }

        support.touch = "ontouchstart" in window;
        support.msPointers = window.MSPointerEvent;
        support.pointers = window.PointerEvent;

        var transitions = support.transitions = false,
            transforms = support.transforms = false,
            elementProto = "HTMLElement" in window ? HTMLElement.prototype : [];

        support.hasHW3D = ("WebKitCSSMatrix" in window && "m11" in new window.WebKitCSSMatrix()) || "MozPerspective" in document.documentElement.style || "msPerspective" in document.documentElement.style;

        each([ "Moz", "webkit", "O", "ms" ], function () {
            var prefix = this.toString(),
                hasTransitions = typeof table.style[prefix + "Transition"] === STRING;

            if (hasTransitions || typeof table.style[prefix + "Transform"] === STRING) {
                var lowPrefix = prefix.toLowerCase();

                transforms = {
                    css: (lowPrefix != "ms") ? "-" + lowPrefix + "-" : "",
                    prefix: prefix,
                    event: (lowPrefix === "o" || lowPrefix === "webkit") ? lowPrefix : ""
                };

                if (hasTransitions) {
                    transitions = transforms;
                    transitions.event = transitions.event ? transitions.event + "TransitionEnd" : "transitionend";
                }

                return false;
            }
        });

        table = null;

        support.transforms = transforms;
        support.transitions = transitions;

        support.devicePixelRatio = window.devicePixelRatio === undefined ? 1 : window.devicePixelRatio;

        try {
            support.screenWidth = window.outerWidth || window.screen ? window.screen.availWidth : window.innerWidth;
            support.screenHeight = window.outerHeight || window.screen ? window.screen.availHeight : window.innerHeight;
        } catch(e) {
            //window.outerWidth throws error when in IE showModalDialog.
            support.screenWidth = window.screen.availWidth;
            support.screenHeight = window.screen.availHeight;
        }

        support.detectOS = function (ua) {
            var os = false, minorVersion, match = [],
                notAndroidPhone = !/mobile safari/i.test(ua),
                agentRxs = {
                    fire: /(Silk)\/(\d+)\.(\d+(\.\d+)?)/,
                    android: /(Android|Android.*(?:Opera|Firefox).*?\/)\s*(\d+)\.(\d+(\.\d+)?)/,
                    iphone: /(iPhone|iPod).*OS\s+(\d+)[\._]([\d\._]+)/,
                    ipad: /(iPad).*OS\s+(\d+)[\._]([\d_]+)/,
                    meego: /(MeeGo).+NokiaBrowser\/(\d+)\.([\d\._]+)/,
                    webos: /(webOS)\/(\d+)\.(\d+(\.\d+)?)/,
                    blackberry: /(BlackBerry|BB10).*?Version\/(\d+)\.(\d+(\.\d+)?)/,
                    playbook: /(PlayBook).*?Tablet\s*OS\s*(\d+)\.(\d+(\.\d+)?)/,
                    wp: /(Windows Phone(?: OS)?)\s(\d+)\.(\d+(\.\d+)?)/,
                    windows: /(MSIE)\s+(\d+)\.(\d+(\.\d+)?)/,
                    tizen: /(tizen).*?Version\/(\d+)\.(\d+(\.\d+)?)/i,
                    sailfish: /(sailfish).*rv:(\d+)\.(\d+(\.\d+)?).*firefox/i,
                    ffos: /(Mobile).*rv:(\d+)\.(\d+(\.\d+)?).*Firefox/
                },
                osRxs = {
                    ios: /^i(phone|pad|pod)$/i,
                    android: /^android|fire$/i,
                    blackberry: /^blackberry|playbook/i,
                    windows: /windows/,
                    wp: /wp/,
                    flat: /sailfish|ffos|tizen/i,
                    meego: /meego/
                },
                formFactorRxs = {
                    tablet: /playbook|ipad|fire/i
                },
                browserRxs = {
                    omini: /Opera\sMini/i,
                    omobile: /Opera\sMobi/i,
                    firefox: /Firefox|Fennec/i,
                    mobilesafari: /version\/.*safari/i,
                    chrome: /chrome|crios/i,
                    webkit: /webkit/i,
                    ie: /MSIE|Windows\sPhone/i
                };

            for (var agent in agentRxs) {
                if (agentRxs.hasOwnProperty(agent)) {
                    match = ua.match(agentRxs[agent]);
                    if (match) {
                        if (agent == "windows" && "plugins" in navigator) { return false; } // Break if not Metro/Mobile Windows

                        os = {};
                        os.device = agent;
                        os.tablet = testRx(agent, formFactorRxs, false);
                        os.browser = testRx(ua, browserRxs, "default");
                        os.name = testRx(agent, osRxs);
                        os[os.name] = true;
                        os.majorVersion = match[2];
                        os.minorVersion = match[3].replace("_", ".");
                        minorVersion = os.minorVersion.replace(".", "").substr(0, 2);
                        os.flatVersion = os.majorVersion + minorVersion + (new Array(3 - (minorVersion.length < 3 ? minorVersion.length : 2)).join("0"));
                        os.cordova = typeof window.PhoneGap !== UNDEFINED || typeof window.cordova !== UNDEFINED; // Use file protocol to detect appModes.
                        os.appMode = window.navigator.standalone || (/file|local|wmapp/).test(window.location.protocol) || os.cordova; // Use file protocol to detect appModes.

                        if (os.android && (support.devicePixelRatio < 1.5 && os.flatVersion < 400 || notAndroidPhone) && (support.screenWidth > 800 || support.screenHeight > 800)) {
                            os.tablet = agent;
                        }

                        break;
                    }
                }
            }
            return os;
        };

        var mobileOS = support.mobileOS = support.detectOS(navigator.userAgent);

        support.wpDevicePixelRatio = mobileOS.wp ? screen.width / 320 : 0;
        support.kineticScrollNeeded = mobileOS && (support.touch || support.msPointers || support.pointers);

        support.hasNativeScrolling = false;

        if (mobileOS.ios || (mobileOS.android && mobileOS.majorVersion > 2) || mobileOS.wp) {
            support.hasNativeScrolling = mobileOS;
        }

        support.mouseAndTouchPresent = support.touch && !(support.mobileOS.ios || support.mobileOS.android);

        support.detectBrowser = function(ua) {
            var browser = false, match = [],
                browserRxs = {
                    webkit: /(chrome)[ \/]([\w.]+)/i,
                    safari: /(webkit)[ \/]([\w.]+)/i,
                    opera: /(opera)(?:.*version|)[ \/]([\w.]+)/i,
                    msie: /(msie\s|trident.*? rv:)([\w.]+)/i,
                    mozilla: /(mozilla)(?:.*? rv:([\w.]+)|)/i
                };

            for (var agent in browserRxs) {
                if (browserRxs.hasOwnProperty(agent)) {
                    match = ua.match(browserRxs[agent]);
                    if (match) {
                        browser = {};
                        browser[agent] = true;
                        browser[match[1].toLowerCase()] = true;
                        browser.version = parseInt(document.documentMode || match[2], 10);

                        break;
                    }
                }
            }

            return browser;
        };

        support.browser = support.detectBrowser(navigator.userAgent);

        support.zoomLevel = function() {
            try {
                return support.touch ? (document.documentElement.clientWidth / window.innerWidth) :
                       support.browser.msie && support.browser.version >= 10 ? ((top || window).document.documentElement.offsetWidth / (top || window).innerWidth) : 1;
            } catch(e) {
                return 1;
            }
        };

        support.cssBorderSpacing = typeof document.documentElement.style.borderSpacing != "undefined" && !(support.browser.msie && support.browser.version < 8);

        (function(browser) {
            // add browser-specific CSS class
            var cssClass,
                majorVersion = parseInt(browser.version, 10);

            if (browser.msie) {
                cssClass = "ie";
            } else if (browser.mozilla) {
                cssClass = "ff";
            } else if (browser.safari) {
                cssClass = "safari";
            } else if (browser.webkit) {
                cssClass = "webkit";
            } else if (browser.opera) {
                cssClass = "opera";
            }

            if (cssClass) {
                $(document.documentElement).addClass("k-" + cssClass + " k-" + cssClass + majorVersion);
            }
        })(support.browser);

        support.eventCapture = document.documentElement.addEventListener;

        var input = document.createElement("input");

        support.placeholder = "placeholder" in input;
        support.propertyChangeEvent = "onpropertychange" in input;

        support.input = (function() {
            var types = ["number", "date", "time", "month", "week", "datetime", "datetime-local"];
            var length = types.length;
            var value = "test";
            var result = {};
            var idx = 0;
            var type;

            for (;idx < length; idx++) {
                type = types[idx];
                input.setAttribute("type", type);
                input.value = value;

                result[type.replace("-", "")] = input.type !== "text" && input.value !== value;
            }

            return result;
        })();

        input.style.cssText = "float:left;";

        support.cssFloat = !!input.style.cssFloat;

        input = null;

        support.stableSort = (function() {
            // Chrome sort is not stable for more than *10* items
            // IE9+ sort is not stable for than *512* items
            var threshold = 513;

            var sorted = [{
                index: 0,
                field: "b"
            }];

            for (var i = 1; i < threshold; i++) {
                sorted.push({
                    index: i,
                    field: "a"
                });
            }

            sorted.sort(function(a, b) {
                return a.field > b.field ? 1 : (a.field < b.field ? -1 : 0);
            });

            return sorted[0].index === 1;
        })();

        support.matchesSelector = elementProto.webkitMatchesSelector || elementProto.mozMatchesSelector ||
                                  elementProto.msMatchesSelector || elementProto.oMatchesSelector || elementProto.matchesSelector ||
          function( selector ) {
              var nodeList = document.querySelectorAll ? ( this.parentNode || document ).querySelectorAll( selector ) || [] : $(selector),
                  i = nodeList.length;

              while (i--) {
                  if (nodeList[i] == this) {
                      return true;
                  }
              }

              return false;
          };

        support.pushState = window.history && window.history.pushState;

        var documentMode = document.documentMode;

        support.hashChange = ("onhashchange" in window) && !(support.browser.msie && (!documentMode || documentMode <= 8)); // old IE detection
    })();


    function size(obj) {
        var result = 0, key;
        for (key in obj) {
            if (obj.hasOwnProperty(key) && key != "toJSON") { // Ignore fake IE7 toJSON.
                result++;
            }
        }

        return result;
    }

    function getOffset(element, type, positioned) {
        if (!type) {
            type = "offset";
        }

        var result = element[type](),
            mobileOS = support.mobileOS;

        // IE10 touch zoom is living in a separate viewport
        if (support.browser.msie && (support.pointers || support.msPointers) && !positioned) {
            result.top -= (window.pageYOffset - document.documentElement.scrollTop);
            result.left -= (window.pageXOffset - document.documentElement.scrollLeft);
        }

        return result;
    }

    var directions = {
        left: { reverse: "right" },
        right: { reverse: "left" },
        down: { reverse: "up" },
        up: { reverse: "down" },
        top: { reverse: "bottom" },
        bottom: { reverse: "top" },
        "in": { reverse: "out" },
        out: { reverse: "in" }
    };

    function parseEffects(input) {
        var effects = {};

        each((typeof input === "string" ? input.split(" ") : input), function(idx) {
            effects[idx] = this;
        });

        return effects;
    }

    function fx(element) {
        return new kendo.effects.Element(element);
    }

    var effects = {};

    $.extend(effects, {
        enabled: true,
        Element: function(element) {
            this.element = $(element);
        },

        promise: function(element, options) {
            if (!element.is(":visible")) {
                element.css({ display: element.data("olddisplay") || "block" }).css("display");
            }

            if (options.hide) {
                element.data("olddisplay", element.css("display")).hide();
            }

            if (options.init) {
                options.init();
            }

            if (options.completeCallback) {
                options.completeCallback(element); // call the external complete callback with the element
            }

            element.dequeue();
        },

        disable: function() {
            this.enabled = false;
            this.promise = this.promiseShim;
        },

        enable: function() {
            this.enabled = true;
            this.promise = this.animatedPromise;
        }
    });

    effects.promiseShim = effects.promise;

    function prepareAnimationOptions(options, duration, reverse, complete) {
        if (typeof options === STRING) {
            // options is the list of effect names separated by space e.g. animate(element, "fadeIn slideDown")

            // only callback is provided e.g. animate(element, options, function() {});
            if (isFunction(duration)) {
                complete = duration;
                duration = 400;
                reverse = false;
            }

            if (isFunction(reverse)) {
                complete = reverse;
                reverse = false;
            }

            if (typeof duration === BOOLEAN){
                reverse = duration;
                duration = 400;
            }

            options = {
                effects: options,
                duration: duration,
                reverse: reverse,
                complete: complete
            };
        }

        return extend({
            //default options
            effects: {},
            duration: 400, //jQuery default duration
            reverse: false,
            init: noop,
            teardown: noop,
            hide: false
        }, options, { completeCallback: options.complete, complete: noop }); // Move external complete callback, so deferred.resolve can be always executed.

    }

    function animate(element, options, duration, reverse, complete) {
        var idx = 0,
            length = element.length,
            instance;

        for (; idx < length; idx ++) {
            instance = $(element[idx]);
            instance.queue(function() {
                effects.promise(instance, prepareAnimationOptions(options, duration, reverse, complete));
            });
        }

        return element;
    }

    function toggleClass(element, classes, options, add) {
        if (classes) {
            classes = classes.split(" ");

            each(classes, function(idx, value) {
                element.toggleClass(value, add);
            });
        }

        return element;
    }

    if (!("kendoAnimate" in $.fn)) {
        extend($.fn, {
            kendoStop: function(clearQueue, gotoEnd) {
                return this.stop(clearQueue, gotoEnd);
            },

            kendoAnimate: function(options, duration, reverse, complete) {
                return animate(this, options, duration, reverse, complete);
            },

            kendoAddClass: function(classes, options){
                return kendo.toggleClass(this, classes, options, true);
            },

            kendoRemoveClass: function(classes, options){
                return kendo.toggleClass(this, classes, options, false);
            },
            kendoToggleClass: function(classes, options, toggle){
                return kendo.toggleClass(this, classes, options, toggle);
            }
        });
    }

    var ampRegExp = /&/g,
        ltRegExp = /</g,
        quoteRegExp = /"/g,
        aposRegExp = /'/g,
        gtRegExp = />/g;
    function htmlEncode(value) {
        return ("" + value).replace(ampRegExp, "&amp;").replace(ltRegExp, "&lt;").replace(gtRegExp, "&gt;").replace(quoteRegExp, "&quot;").replace(aposRegExp, "&#39;");
    }

    var eventTarget = function (e) {
        return e.target;
    };

    if (support.touch) {

        eventTarget = function(e) {
            var touches = "originalEvent" in e ? e.originalEvent.changedTouches : "changedTouches" in e ? e.changedTouches : null;

            return touches ? document.elementFromPoint(touches[0].clientX, touches[0].clientY) : e.target;
        };

        each(["swipe", "swipeLeft", "swipeRight", "swipeUp", "swipeDown", "doubleTap", "tap"], function(m, value) {
            $.fn[value] = function(callback) {
                return this.bind(value, callback);
            };
        });
    }

    if (support.touch) {
        if (!support.mobileOS) {
            support.mousedown = "mousedown touchstart";
            support.mouseup = "mouseup touchend";
            support.mousemove = "mousemove touchmove";
            support.mousecancel = "mouseleave touchcancel";
            support.click = "click";
            support.resize = "resize";
        } else {
            support.mousedown = "touchstart";
            support.mouseup = "touchend";
            support.mousemove = "touchmove";
            support.mousecancel = "touchcancel";
            support.click = "touchend";
            support.resize = "orientationchange";
        }
    } else if (support.pointers) {
        support.mousemove = "pointermove";
        support.mousedown = "pointerdown";
        support.mouseup = "pointerup";
        support.mousecancel = "pointercancel";
        support.click = "pointerup";
        support.resize = "orientationchange resize";
    } else if (support.msPointers) {
        support.mousemove = "MSPointerMove";
        support.mousedown = "MSPointerDown";
        support.mouseup = "MSPointerUp";
        support.mousecancel = "MSPointerCancel";
        support.click = "MSPointerUp";
        support.resize = "orientationchange resize";
    } else {
        support.mousemove = "mousemove";
        support.mousedown = "mousedown";
        support.mouseup = "mouseup";
        support.mousecancel = "mouseleave";
        support.click = "click";
        support.resize = "resize";
    }

    var wrapExpression = function(members, paramName) {
        var result = paramName || "d",
            index,
            idx,
            length,
            member,
            count = 1;

        for (idx = 0, length = members.length; idx < length; idx++) {
            member = members[idx];
            if (member !== "") {
                index = member.indexOf("[");

                if (index !== 0) {
                    if (index == -1) {
                        member = "." + member;
                    } else {
                        count++;
                        member = "." + member.substring(0, index) + " || {})" + member.substring(index);
                    }
                }

                count++;
                result += member + ((idx < length - 1) ? " || {})" : ")");
            }
        }
        return new Array(count).join("(") + result;
    },
    localUrlRe = /^([a-z]+:)?\/\//i;

    extend(kendo, {
        ui: kendo.ui || {},
        fx: kendo.fx || fx,
        effects: kendo.effects || effects,
        mobile: kendo.mobile || { },
        data: kendo.data || {},
        dataviz: kendo.dataviz || {ui: { roles: {}}},
        keys: {
            INSERT: 45,
            DELETE: 46,
            BACKSPACE: 8,
            TAB: 9,
            ENTER: 13,
            ESC: 27,
            LEFT: 37,
            UP: 38,
            RIGHT: 39,
            DOWN: 40,
            END: 35,
            HOME: 36,
            SPACEBAR: 32,
            PAGEUP: 33,
            PAGEDOWN: 34,
            F2: 113,
            F10: 121,
            F12: 123,
            NUMPAD_PLUS: 107,
            NUMPAD_MINUS: 109,
            NUMPAD_DOT: 110
        },
        support: kendo.support || support,
        animate: kendo.animate || animate,
        ns: "",
        attr: function(value) {
            return "data-" + kendo.ns + value;
        },
        getShadows: getShadows,
        wrap: wrap,
        deepExtend: deepExtend,
        getComputedStyles: getComputedStyles,
        size: size,
        toCamelCase: toCamelCase,
        toHyphens: toHyphens,
        getOffset: kendo.getOffset || getOffset,
        parseEffects: kendo.parseEffects || parseEffects,
        toggleClass: kendo.toggleClass || toggleClass,
        directions: kendo.directions || directions,
        Observable: Observable,
        Class: Class,
        Template: Template,
        template: proxy(Template.compile, Template),
        render: proxy(Template.render, Template),
        stringify: proxy(JSON.stringify, JSON),
        eventTarget: eventTarget,
        htmlEncode: htmlEncode,
        isLocalUrl: function(url) {
            return url && !localUrlRe.test(url);
        },

        expr: function(expression, safe, paramName) {
            expression = expression || "";

            if (typeof safe == STRING) {
                paramName = safe;
                safe = false;
            }

            paramName = paramName || "d";

            if (expression && expression.charAt(0) !== "[") {
                expression = "." + expression;
            }

            if (safe) {
                expression = wrapExpression(expression.split("."), paramName);
            } else {
                expression = paramName + expression;
            }

            return expression;
        },

        getter: function(expression, safe) {
            return getterCache[expression] = getterCache[expression] || new Function("d", "return " + kendo.expr(expression, safe));
        },

        setter: function(expression) {
            return setterCache[expression] = setterCache[expression] || new Function("d,value", kendo.expr(expression) + "=value");
        },

        accessor: function(expression) {
            return {
                get: kendo.getter(expression),
                set: kendo.setter(expression)
            };
        },

        guid: function() {
            var id = "", i, random;

            for (i = 0; i < 32; i++) {
                random = math.random() * 16 | 0;

                if (i == 8 || i == 12 || i == 16 || i == 20) {
                    id += "-";
                }
                id += (i == 12 ? 4 : (i == 16 ? (random & 3 | 8) : random)).toString(16);
            }

            return id;
        },

        roleSelector: function(role) {
            return role.replace(/(\S+)/g, "[" + kendo.attr("role") + "=$1],").slice(0, -1);
        },

        triggeredByInput: function(e) {
            return (/^(label|input|textarea|select)$/i).test(e.target.tagName);
        },

        logToConsole: function(message) {
            var console = window.console;

            if (typeof(console) != "undefined" && console.log) {
                console.log(message);
            }
        }
    });

    var Widget = Observable.extend( {
        init: function(element, options) {
            var that = this;

            that.element = kendo.jQuery(element).handler(that);

            Observable.fn.init.call(that);

            options = that.options = extend(true, {}, that.options, options);

            if (!that.element.attr(kendo.attr("role"))) {
                that.element.attr(kendo.attr("role"), (options.name || "").toLowerCase());
            }

            that.element.data("kendo" + options.prefix + options.name, that);

            that.bind(that.events, options);
        },

        events: [],

        options: {
            prefix: ""
        },

        _hasBindingTarget: function() {
            return !!this.element[0].kendoBindingTarget;
        },

        _tabindex: function(target) {
            target = target || this.wrapper;

            var element = this.element,
                TABINDEX = "tabindex",
                tabindex = target.attr(TABINDEX) || element.attr(TABINDEX);

            element.removeAttr(TABINDEX);

            target.attr(TABINDEX, !isNaN(tabindex) ? tabindex : 0);
        },

        setOptions: function(options) {
            this._setEvents(options);
            $.extend(this.options, options);
        },

        _setEvents: function(options) {
            var that = this,
                idx = 0,
                length = that.events.length,
                e;

            for (; idx < length; idx ++) {
                e = that.events[idx];
                if (that.options[e] && options[e]) {
                    that.unbind(e, that.options[e]);
                }
            }

            that.bind(that.events, options);
        },

        resize: function(force) {
            var size = this.getSize(),
                currentSize = this._size;

            if (force || !currentSize || size.width !== currentSize.width || size.height !== currentSize.height) {
                this._resize(size);
                this.trigger("resize", size);
                this._size = size;
            }
        },

        getSize: function() {
            return kendo.dimensions(this.element);
        },

        size: function(size) {
            if (!size) {
                return this.getSize();
            } else {
                this.setSize(size);
            }
        },

        setSize: $.noop,
        _resize: $.noop,

        destroy: function() {
            var that = this;

            that.element.removeData("kendo" + that.options.prefix + that.options.name);
            that.element.removeData("handler");
            that.unbind();
        }
    });

    kendo.dimensions = function(element, dimensions) {
        var domElement = element[0];

        if (dimensions) {
            element.css(dimensions);
        }

        return { width: domElement.offsetWidth, height: domElement.offsetHeight };
    };

    kendo.notify = noop;

    var templateRegExp = /template$/i,
        jsonRegExp = /^\s*(?:\{(?:.|\r\n|\n)*\}|\[(?:.|\r\n|\n)*\])\s*$/,
        jsonFormatRegExp = /^\{(\d+)(:[^\}]+)?\}|^\[[A-Za-z_]*\]$/,
        dashRegExp = /([A-Z])/g;

    function parseOption(element, option) {
        var value;

        if (option.indexOf("data") === 0) {
            option = option.substring(4);
            option = option.charAt(0).toLowerCase() + option.substring(1);
        }

        option = option.replace(dashRegExp, "-$1");
        value = element.getAttribute("data-" + kendo.ns + option);

        if (value === null) {
            value = undefined;
        } else if (value === "null") {
            value = null;
        } else if (value === "true") {
            value = true;
        } else if (value === "false") {
            value = false;
        } else if (numberRegExp.test(value)) {
            value = parseFloat(value);
        } else if (jsonRegExp.test(value) && !jsonFormatRegExp.test(value)) {
            value = new Function("return (" + value + ")")();
        }

        return value;
    }

    function parseOptions(element, options) {
        var result = {},
            option,
            value;

        for (option in options) {
            value = parseOption(element, option);

            if (value !== undefined) {

                if (templateRegExp.test(option)) {
                    value = kendo.template($("#" + value).html());
                }

                result[option] = value;
            }
        }

        return result;
    }

    kendo.initWidget = function(element, options, roles) {
        var result,
            option,
            widget,
            idx,
            length,
            role,
            value,
            dataSource;

        // Preserve backwards compatibility with (element, options, namespace) signature, where namespace was kendo.ui
        if (!roles) {
            roles = kendo.ui.roles;
        } else if (roles.roles) {
            roles = roles.roles;
        }

        element = element.nodeType ? element : element[0];

        role = element.getAttribute("data-" + kendo.ns + "role");

        if (!role) {
            return;
        }

        if (role.indexOf(".") === -1) {
            widget = roles[role];
        } else { // full namespace path - like kendo.ui.Widget
            widget = kendo.getter(role)(window);
        }

        if (!widget) {
            return;
        }

        dataSource = parseOption(element, "dataSource");

        options = $.extend({}, parseOptions(element, widget.fn.options), options);

        if (dataSource) {
            if (typeof dataSource === STRING) {
                options.dataSource = kendo.getter(dataSource)(window);
            } else {
                options.dataSource = dataSource;
            }
        }

        for (idx = 0, length = widget.fn.events.length; idx < length; idx++) {
            option = widget.fn.events[idx];

            value = parseOption(element, option);

            if (value !== undefined) {
                options[option] = kendo.getter(value)(window);
            }
        }

        result = $(element).data("kendo" + widget.fn.options.prefix + widget.fn.options.name);

        if (!result) {
            result = new widget(element, options);
        } else {
            result.setOptions(options);
        }

        return result;
    };

    kendo.rolesFromNamespaces = function(namespaces) {
        var roles = [],
            idx,
            length;

        if (!namespaces[0]) {
            namespaces = [kendo.ui, kendo.dataviz.ui];
        }

        for (idx = 0, length = namespaces.length; idx < length; idx ++) {
            roles[idx] = namespaces[idx].roles;
        }

        return extend.apply(null, [{}].concat(roles.reverse()));
    };

    kendo.init = function(element) {
        var roles = kendo.rolesFromNamespaces(slice.call(arguments, 1));

        $(element).find("[data-" + kendo.ns + "role]").addBack().each(function(){
            kendo.initWidget(this, {}, roles);
        });
    };

    kendo.destroy = function(element) {
        $(element).find("[data-" + kendo.ns + "role]").addBack().each(function(){
            var widget = kendo.widgetInstance($(this));

            if (widget) {
                widget.destroy();
            }
        });
    };

    function containmentComparer(a, b) {
        return $.contains(a, b) ? -1 : 1;
    }

    function resizableWidget() {
        var widget = $(this);
        return ($.inArray(widget.attr("data-role"), ["slider", "rangeslider"]) > 0) || widget.is(":visible");
    }

    kendo.resize = function(element) {
        var widgets = $(element).find("[data-" + kendo.ns + "role]").addBack().filter(resizableWidget);

        if (!widgets.length) {
            return;
        }

        // sort widgets based on their parent-child relation
        var widgetsArray = $.makeArray(widgets);
        widgetsArray.sort(containmentComparer);

        // resize widgets
        $.each(widgetsArray, function () {
            var widget = kendo.widgetInstance($(this));
            if (widget) {
                widget.resize();
            }
        });
    };

    kendo.parseOptions = parseOptions;

    extend(kendo.ui, {
        Widget: Widget,
        roles: {},
        progress: function(container, toggle) {
            var mask = container.find(".k-loading-mask"),
                support = kendo.support,
                browser = support.browser,
                isRtl, leftRight, webkitCorrection, containerScrollLeft;

            if (toggle) {
                if (!mask.length) {
                    isRtl = support.isRtl(container);
                    leftRight = isRtl ? "right" : "left";
                    containerScrollLeft = container.scrollLeft();
                    webkitCorrection = browser.webkit ? (!isRtl ? 0 : container[0].scrollWidth - container.width() - 2 * containerScrollLeft) : 0;

                    mask = $("<div class='k-loading-mask'><span class='k-loading-text'>Loading...</span><div class='k-loading-image'/><div class='k-loading-color'/></div>")
                        .width("100%").height("100%")
                        .css("top", container.scrollTop())
                        .css(leftRight, Math.abs(containerScrollLeft) + webkitCorrection)
                        .prependTo(container);
                }
            } else if (mask) {
                mask.remove();
            }
        },
        plugin: function(widget, register, prefix) {
            var name = widget.fn.options.name,
                getter;

            register = register || kendo.ui;
            prefix = prefix || "";

            register[name] = widget;

            register.roles[name.toLowerCase()] = widget;

            getter = "getKendo" + prefix + name;
            name = "kendo" + prefix + name;

            $.fn[name] = function(options) {
                var value = this,
                    args;

                if (typeof options === STRING) {
                    args = slice.call(arguments, 1);

                    this.each(function(){
                        var widget = $.data(this, name),
                            method,
                            result;

                        if (!widget) {
                            throw new Error(kendo.format("Cannot call method '{0}' of {1} before it is initialized", options, name));
                        }

                        method = widget[options];

                        if (typeof method !== FUNCTION) {
                            throw new Error(kendo.format("Cannot find method '{0}' of {1}", options, name));
                        }

                        result = method.apply(widget, args);

                        if (result !== undefined) {
                            value = result;
                            return false;
                        }
                    });
                } else {
                    this.each(function() {
                        new widget(this, options);
                    });
                }

                return value;
            };

            $.fn[getter] = function() {
                return this.data(name);
            };
        }
    });

    var ContainerNullObject = { bind: function () { return this; }, nullObject: true };

    var MobileWidget = Widget.extend({
        init: function(element, options) {
            Widget.fn.init.call(this, element, options);
            this.element.autoApplyNS();
            this.wrapper = this.element;
            this.element.addClass("km-widget");
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.element.kendoDestroy();
        },

        options: {
            prefix: "Mobile"
        },

        events: [],

        view: function() {
            var viewElement = this.element.closest(kendo.roleSelector("view splitview modalview drawer"));
            return kendo.widgetInstance(viewElement, kendo.mobile.ui);
        },

        viewHasNativeScrolling: function() {
            var view = this.view();
            return view && view.options.useNativeScrolling;
        },

        container: function() {
            var element = this.element.closest(kendo.roleSelector("view layout modalview drawer"));
            return kendo.widgetInstance(element, kendo.mobile.ui) || ContainerNullObject;
        }
    });

    extend(kendo.mobile, {
        init: function(element) {
            kendo.init(element, kendo.mobile.ui, kendo.ui, kendo.dataviz.ui);
        },

        appLevelNativeScrolling: function() {
            return kendo.mobile.application && kendo.mobile.application.options && kendo.mobile.application.options.useNativeScrolling;
        },

        ui: {
            Widget: MobileWidget,
            roles: {},
            plugin: function(widget) {
                kendo.ui.plugin(widget, kendo.mobile.ui, "Mobile");
            }
        }
    });

    kendo.touchScroller = function(elements, options) {
        // return the first touch scroller
        return $(elements).map(function(idx, element) {
            element = $(element);
            if (support.kineticScrollNeeded && kendo.mobile.ui.Scroller && !element.data("kendoMobileScroller")) {
                element.kendoMobileScroller(options);
                return element.data("kendoMobileScroller");
            } else {
                return false;
            }
        })[0];
    };

    kendo.preventDefault = function(e) {
        e.preventDefault();
    };

    kendo.widgetInstance = function(element, suites) {
        var role = element.data(kendo.ns + "role"),
            widgets = [], i, length;

        if (role) {
            // HACK!!! mobile view scroller widgets are instantiated on data-role="content" elements. We need to discover them when resizing.
            if (role === "content") {
                role = "scroller";
            }

            if (suites) {
                if (suites[0]) {
                    for (i = 0, length = suites.length; i < length; i ++) {
                        widgets.push(suites[i].roles[role]);
                    }
                } else {
                    widgets.push(suites.roles[role]);
                }
            }
            else {
                widgets = [ kendo.ui.roles[role], kendo.dataviz.ui.roles[role],  kendo.mobile.ui.roles[role] ];
            }

            if (role.indexOf(".") >= 0) {
                widgets = [ kendo.getter(role)(window) ];
            }

            for (i = 0, length = widgets.length; i < length; i ++) {
                var widget = widgets[i];
                if (widget) {
                    var instance = element.data("kendo" + widget.fn.options.prefix + widget.fn.options.name);
                    if (instance) {
                        return instance;
                    }
                }
            }
        }
    };

    kendo.onResize = function(callback) {
        var handler = callback;
        if (support.mobileOS.android) {
            handler = function() { setTimeout(callback, 600); };
        }

        $(window).on(support.resize, handler);
        return handler;
    };

    kendo.unbindResize = function(callback) {
        $(window).off(support.resize, callback);
    };

    kendo.attrValue = function(element, key) {
        return element.data(kendo.ns + key);
    };

    kendo.days = {
        Sunday: 0,
        Monday: 1,
        Tuesday: 2,
        Wednesday: 3,
        Thursday: 4,
        Friday: 5,
        Saturday: 6
    };

    function focusable(element, isTabIndexNotNaN) {
        var nodeName = element.nodeName.toLowerCase();

        return (/input|select|textarea|button|object/.test(nodeName) ?
                !element.disabled :
                "a" === nodeName ?
                element.href || isTabIndexNotNaN :
                isTabIndexNotNaN
               ) &&
            visible(element);
    }

    function visible(element) {
        return !$(element).parents().addBack().filter(function() {
            return $.css(this,"visibility") === "hidden" || $.expr.filters.hidden(this);
        }).length;
    }

    $.extend($.expr[ ":" ], {
        kendoFocusable: function(element) {
            var idx = $.attr(element, "tabindex");
            return focusable(element, !isNaN(idx) && idx > -1);
        }
    });

    var MOUSE_EVENTS = ["mousedown", "mousemove", "mouseenter", "mouseleave", "mouseover", "mouseout", "mouseup", "click"];
    var EXCLUDE_BUST_CLICK_SELECTOR = "label, input, [data-rel=external]";

    var MouseEventNormalizer = {
        setupMouseMute: function() {
            var idx = 0,
                length = MOUSE_EVENTS.length,
                element = document.documentElement;

            if (MouseEventNormalizer.mouseTrap || !support.eventCapture) {
                return;
            }

            MouseEventNormalizer.mouseTrap = true;

            MouseEventNormalizer.bustClick = false;
            MouseEventNormalizer.captureMouse = false;

            var handler = function(e) {
                if (MouseEventNormalizer.captureMouse) {
                    if (e.type === "click") {
                        if (MouseEventNormalizer.bustClick && !$(e.target).is(EXCLUDE_BUST_CLICK_SELECTOR)) {
                            e.preventDefault();
                            e.stopPropagation();
                        }
                    } else {
                        e.stopPropagation();
                    }
                }
            };

            for (; idx < length; idx++) {
                element.addEventListener(MOUSE_EVENTS[idx], handler, true);
            }
        },

        muteMouse: function(e) {
            MouseEventNormalizer.captureMouse = true;
            if (e.data.bustClick) {
                MouseEventNormalizer.bustClick = true;
            }
            clearTimeout(MouseEventNormalizer.mouseTrapTimeoutID);
        },

        unMuteMouse: function() {
            clearTimeout(MouseEventNormalizer.mouseTrapTimeoutID);
            MouseEventNormalizer.mouseTrapTimeoutID = setTimeout(function() {
                MouseEventNormalizer.captureMouse = false;
                MouseEventNormalizer.bustClick = false;
            }, 400);
        }
    };

    var eventMap = {
        down: "touchstart mousedown",
        move: "mousemove touchmove",
        up: "mouseup touchend touchcancel",
        cancel: "mouseleave touchcancel"
    };

    if (support.touch && (support.mobileOS.ios || support.mobileOS.android)) {
        eventMap = {
            down: "touchstart",
            move: "touchmove",
            up: "touchend touchcancel",
            cancel: "touchcancel"
        };
    } else if (support.pointers) {
        eventMap = {
            down: "pointerdown",
            move: "pointermove",
            up: "pointerup",
            cancel: "pointercancel pointerleave"
        };
    } else if (support.msPointers) {
        eventMap = {
            down: "MSPointerDown",
            move: "MSPointerMove",
            up: "MSPointerUp",
            cancel: "MSPointerCancel MSPointerLeave"
        };
    }

    if (support.msPointers && !("onmspointerenter" in window)) { // IE10
        // Create MSPointerEnter/MSPointerLeave events using mouseover/out and event-time checks
        $.each({
            MSPointerEnter: "MSPointerOver",
            MSPointerLeave: "MSPointerOut"
        }, function( orig, fix ) {
            $.event.special[ orig ] = {
                delegateType: fix,
                bindType: fix,

                handle: function( event ) {
                    var ret,
                        target = this,
                        related = event.relatedTarget,
                        handleObj = event.handleObj;

                    // For mousenter/leave call the handler if related is outside the target.
                    // NB: No relatedTarget if the mouse left/entered the browser window
                    if ( !related || (related !== target && !$.contains( target, related )) ) {
                        event.type = handleObj.origType;
                        ret = handleObj.handler.apply( this, arguments );
                        event.type = fix;
                    }
                    return ret;
                }
            };
        });
    }


    var getEventMap = function(e) { return (eventMap[e] || e); },
        eventRegEx = /([^ ]+)/g;

    kendo.applyEventMap = function(events, ns) {
        events = events.replace(eventRegEx, getEventMap);

        if (ns) {
            events = events.replace(eventRegEx, "$1." + ns);
        }

        return events;
    };

    var on = $.fn.on;

    function kendoJQuery(selector, context) {
        return new kendoJQuery.fn.init(selector, context);
    }

    extend(true, kendoJQuery, $);

    kendoJQuery.fn = kendoJQuery.prototype = new $();

    kendoJQuery.fn.constructor = kendoJQuery;

    kendoJQuery.fn.init = function(selector, context) {
        if (context && context instanceof $ && !(context instanceof kendoJQuery)) {
            context = kendoJQuery(context);
        }

        return $.fn.init.call(this, selector, context, rootjQuery);
    };

    kendoJQuery.fn.init.prototype = kendoJQuery.fn;

    var rootjQuery = kendoJQuery(document);

    extend(kendoJQuery.fn, {
        handler: function(handler) {
            this.data("handler", handler);
            return this;
        },

        autoApplyNS: function(ns) {
            this.data("kendoNS", ns || kendo.guid());
            return this;
        },

        on: function() {
            var that = this,
                ns = that.data("kendoNS");

            // support for event map signature
            if (arguments.length === 1) {
                return on.call(that, arguments[0]);
            }

            var context = that,
                args = slice.call(arguments);

            if (typeof args[args.length -1] === UNDEFINED) {
                args.pop();
            }

            var callback =  args[args.length - 1],
                events = kendo.applyEventMap(args[0], ns);

            // setup mouse trap
            if (support.mouseAndTouchPresent && events.search(/mouse|click/) > -1 && this[0] !== document.documentElement) {
                MouseEventNormalizer.setupMouseMute();

                var selector = args.length === 2 ? null : args[1],
                    bustClick = events.indexOf("click") > -1 && events.indexOf("touchend") > -1;

                on.call(this,
                    {
                        touchstart: MouseEventNormalizer.muteMouse,
                        touchend: MouseEventNormalizer.unMuteMouse
                    },
                    selector,
                    {
                        bustClick: bustClick
                    });
            }

            if (typeof callback === STRING) {
                context = that.data("handler");
                callback = context[callback];

                args[args.length - 1] = function(e) {
                    callback.call(context, e);
                };
            }

            args[0] = events;

            on.apply(that, args);

            return that;
        },

        kendoDestroy: function(ns) {
            ns = ns || this.data("kendoNS");

            if (ns) {
                this.off("." + ns);
            }

            return this;
        }
    });

    kendo.jQuery = kendoJQuery;
    kendo.eventMap = eventMap;

    kendo.timezone = (function(){
        var months =  { Jan: 0, Feb: 1, Mar: 2, Apr: 3, May: 4, Jun: 5, Jul: 6, Aug: 7, Sep: 8, Oct: 9, Nov: 10, Dec: 11 };
        var days = { Sun: 0, Mon: 1, Tue: 2, Wed: 3, Thu: 4, Fri: 5, Sat: 6 };

        function ruleToDate(year, rule) {
            var date;
            var targetDay;
            var ourDay;
            var month = rule[3];
            var on = rule[4];
            var time = rule[5];
            var cache = rule[8];

            if (!cache) {
                rule[8] = cache = {};
            }

            if (cache[year]) {
                return cache[year];
            }

            if (!isNaN(on)) {
                date = new Date(Date.UTC(year, months[month], on, time[0], time[1], time[2], 0));
            } else if (on.indexOf("last") === 0) {
                date = new Date(Date.UTC(year, months[month] + 1, 1, time[0] - 24, time[1], time[2], 0));

                targetDay = days[on.substr(4, 3)];
                ourDay = date.getUTCDay();

                date.setUTCDate(date.getUTCDate() + targetDay - ourDay - (targetDay > ourDay ? 7 : 0));
            } else if (on.indexOf(">=") >= 0) {
                date = new Date(Date.UTC(year, months[month], on.substr(5), time[0], time[1], time[2], 0));

                targetDay = days[on.substr(0, 3)];
                ourDay = date.getUTCDay();

                date.setUTCDate(date.getUTCDate() + targetDay - ourDay + (targetDay < ourDay ? 7 : 0));
            }

            return cache[year] = date;
        }

        function findRule(utcTime, rules, zone) {
            rules = rules[zone];

            if (!rules) {
                var time = zone.split(":");
                var offset = 0;

                if (time.length > 1) {
                    offset = time[0] * 60 + Number(time[1]);
                }

                return [-1000000, 'max', '-', 'Jan', 1, [0, 0, 0], offset, '-'];
            }

            var year = new Date(utcTime).getUTCFullYear();

            rules = jQuery.grep(rules, function(rule) {
                var from = rule[0];
                var to = rule[1];

                return from <= year && (to >= year || (from == year && to == "only") || to == "max");
            });

            rules.push(utcTime);

            rules.sort(function(a, b) {
                if (typeof a != "number") {
                    a = Number(ruleToDate(year, a));
                }

                if (typeof b != "number") {
                    b = Number(ruleToDate(year, b));
                }

                return a - b;
            });

            var rule = rules[jQuery.inArray(utcTime, rules) - 1] || rules[rules.length - 1];

            return isNaN(rule) ? rule : null;
        }

        function findZone(utcTime, zones, timezone) {
            var zoneRules = zones[timezone];

            if (typeof zoneRules === "string") {
                zoneRules = zones[zoneRules];
            }

            if (!zoneRules) {
                throw new Error('Timezone "' + timezone + '" is either incorrect, or kendo.timezones.min.js is not included.');
            }

            for (var idx = zoneRules.length - 1; idx >= 0; idx--) {
                var until = zoneRules[idx][3];

                if (until && utcTime > until) {
                    break;
                }
            }

            var zone = zoneRules[idx + 1];

            if (!zone) {
                throw new Error('Timezone "' + timezone + '" not found on ' + utcTime + ".");
            }

            return zone;
        }

        function zoneAndRule(utcTime, zones, rules, timezone) {
            if (typeof utcTime != NUMBER) {
                utcTime = Date.UTC(utcTime.getFullYear(), utcTime.getMonth(),
                    utcTime.getDate(), utcTime.getHours(), utcTime.getMinutes(),
                    utcTime.getSeconds(), utcTime.getMilliseconds());
            }

            var zone = findZone(utcTime, zones, timezone);

            return {
                zone: zone,
                rule: findRule(utcTime, rules, zone[1])
            };
        }

        function offset(utcTime, timezone) {
            if (timezone == "Etc/UTC" || timezone == "Etc/GMT") {
                return 0;
            }

            var info = zoneAndRule(utcTime, this.zones, this.rules, timezone);
            var zone = info.zone;
            var rule = info.rule;

            return rule? zone[0] - rule[6] : zone[0];
        }

        function abbr(utcTime, timezone) {
            var info = zoneAndRule(utcTime, this.zones, this.rules, timezone);
            var zone = info.zone;
            var rule = info.rule;

            var base = zone[2];

            if (base.indexOf("/") >= 0) {
                return base.split("/")[rule && +rule[6] ? 1 : 0];
            } else if (base.indexOf("%s") >= 0) {
                return base.replace("%s", (!rule || rule[7] == "-") ? '' : rule[7]);
            }

            return base;
        }

        function convert(date, fromOffset, toOffset) {
            if (typeof fromOffset == STRING) {
                fromOffset = this.offset(date, fromOffset);
            }

            if (typeof toOffset == STRING) {
                toOffset = this.offset(date, toOffset);
            }

            var fromLocalOffset = date.getTimezoneOffset();

            date = new Date(date.getTime() + (fromOffset - toOffset) * 60000);

            var toLocalOffset = date.getTimezoneOffset();

            return new Date(date.getTime() + (toLocalOffset - fromLocalOffset) * 60000);
        }

        function apply(date, timezone) {
           return this.convert(date, date.getTimezoneOffset(), timezone);
        }

        function remove(date, timezone) {
           return this.convert(date, timezone, date.getTimezoneOffset());
        }

        function toLocalDate(time) {
            return this.apply(new Date(time), "Etc/UTC");
        }

        return {
           zones: {},
           rules: {},
           offset: offset,
           convert: convert,
           apply: apply,
           remove: remove,
           abbr: abbr,
           toLocalDate: toLocalDate
        };
    })();

    kendo.date = (function(){
        var MS_PER_MINUTE = 60000,
            MS_PER_DAY = 86400000;

        function adjustDST(date, hours) {
            if (hours === 0 && date.getHours() === 23) {
                date.setHours(date.getHours() + 2);
                return true;
            }

            return false;
        }

        function setDayOfWeek(date, day, dir) {
            var hours = date.getHours();

            dir = dir || 1;
            day = ((day - date.getDay()) + (7 * dir)) % 7;

            date.setDate(date.getDate() + day);
            adjustDST(date, hours);
        }

        function dayOfWeek(date, day, dir) {
            date = new Date(date);
            setDayOfWeek(date, day, dir);
            return date;
        }

        function firstDayOfMonth(date) {
            return new Date(
                date.getFullYear(),
                date.getMonth(),
                1
            );
        }

        function lastDayOfMonth(date) {
            var last = new Date(date.getFullYear(), date.getMonth() + 1, 0),
                first = firstDayOfMonth(date),
                timeOffset = Math.abs(last.getTimezoneOffset() - first.getTimezoneOffset());

            if (timeOffset) {
                last.setHours(first.getHours() + (timeOffset / 60));
            }

            return last;
        }

        function getDate(date) {
            date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
            adjustDST(date, 0);
            return date;
        }

        function toUtcTime(date) {
            return Date.UTC(date.getFullYear(), date.getMonth(),
                        date.getDate(), date.getHours(), date.getMinutes(),
                        date.getSeconds(), date.getMilliseconds());
        }

        function getMilliseconds(date) {
            return date.getTime() - getDate(date);
        }

        function isInTimeRange(value, min, max) {
            var msMin = getMilliseconds(min),
                msMax = getMilliseconds(max),
                msValue;

            if (!value || msMin == msMax) {
                return true;
            }

            if (min >= max) {
                max += MS_PER_DAY;
            }

            msValue = getMilliseconds(value);

            if (msMin > msValue) {
                msValue += MS_PER_DAY;
            }

            if (msMax < msMin) {
                msMax += MS_PER_DAY;
            }

            return msValue >= msMin && msValue <= msMax;
        }

        function isInDateRange(value, min, max) {
            var msMin = min.getTime(),
                msMax = max.getTime(),
                msValue;

            if (msMin >= msMax) {
                msMax += MS_PER_DAY;
            }

            msValue = value.getTime();

            return msValue >= msMin && msValue <= msMax;
        }

        function addDays(date, offset) {
            var hours = date.getHours();
                date = new Date(date);

            setTime(date, offset * MS_PER_DAY);
            adjustDST(date, hours);
            return date;
        }

        function setTime(date, milliseconds, ignoreDST) {
            var offset = date.getTimezoneOffset();
            var difference;

            date.setTime(date.getTime() + milliseconds);

            if (!ignoreDST) {
                difference = date.getTimezoneOffset() - offset;
                date.setTime(date.getTime() + difference * MS_PER_MINUTE);
            }
        }

        function today() {
            return getDate(new Date());
        }

        function isToday(date) {
           return getDate(date).getTime() == today().getTime();
        }

        function toInvariantTime(date) {
            var staticDate = new Date(1980, 1, 1, 0, 0, 0);

            if (date) {
                staticDate.setHours(date.getHours(), date.getMinutes(), date.getSeconds(), date.getMilliseconds());
            }

            return staticDate;
        }

        return {
            adjustDST: adjustDST,
            dayOfWeek: dayOfWeek,
            setDayOfWeek: setDayOfWeek,
            getDate: getDate,
            isInDateRange: isInDateRange,
            isInTimeRange: isInTimeRange,
            isToday: isToday,
            nextDay: function(date) {
                return addDays(date, 1);
            },
            previousDay: function(date) {
                return addDays(date, -1);
            },
            toUtcTime: toUtcTime,
            MS_PER_DAY: MS_PER_DAY,
            MS_PER_HOUR: 60 * MS_PER_MINUTE,
            MS_PER_MINUTE: MS_PER_MINUTE,
            setTime: setTime,
            addDays: addDays,
            today: today,
            toInvariantTime: toInvariantTime,
            firstDayOfMonth: firstDayOfMonth,
            lastDayOfMonth: lastDayOfMonth,
            getMilliseconds: getMilliseconds
            //TODO methods: combine date portion and time portion from arguments - date1, date 2
        };
    })();


    kendo.stripWhitespace = function(element) {
        var iterator = document.createNodeIterator(element, NodeFilter.SHOW_TEXT, function(node) {
                return node.parentNode == element ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_REJECT;
            }, false);

        while (iterator.nextNode()) {
            if (iterator.referenceNode && !iterator.referenceNode.textContent.trim()) {
                iterator.referenceNode.parentNode.removeChild(iterator.referenceNode);
            }
        }
    };

    var animationFrame  = window.requestAnimationFrame       ||
                          window.webkitRequestAnimationFrame ||
                          window.mozRequestAnimationFrame    ||
                          window.oRequestAnimationFrame      ||
                          window.msRequestAnimationFrame     ||
                          function(callback){ setTimeout(callback, 1000 / 60); };

    kendo.animationFrame = function(callback) {
        animationFrame.call(window, callback);
    };

    var animationQueue = [];

    kendo.queueAnimation = function(callback) {
        animationQueue[animationQueue.length] = callback;
        if (animationQueue.length === 1) {
            kendo.runNextAnimation();
        }
    };

    kendo.runNextAnimation = function() {
        kendo.animationFrame(function() {
            if (animationQueue[0]) {
                animationQueue.shift()();
                if (animationQueue[0]) {
                    kendo.runNextAnimation();
                }
            }
        });
    };

    kendo.parseQueryStringParams = function(url) {
        var queryString = url.split('?')[1] || "",
            params = {},
            paramParts = queryString.split(/&|=/),
            length = paramParts.length,
            idx = 0;

        for (; idx < length; idx += 2) {
            if(paramParts[idx] !== "") {
                params[decodeURIComponent(paramParts[idx])] = decodeURIComponent(paramParts[idx + 1]);
            }
        }

        return params;
    };

    var OS = kendo.support.mobileOS,
        invalidZeroEvents = OS && OS.android,
        mobileChrome = (invalidZeroEvents && OS.browser == "chrome");

    kendo.elementUnderCursor = function(e) {
        if (mobileChrome) {
            return document.elementFromPoint(e.x.screen, e.y.screen);
        } else {
            return document.elementFromPoint(e.x.client, e.y.client);
        }
    };

    kendo.wheelDeltaY = function(jQueryEvent) {
        var e = jQueryEvent.originalEvent,
            deltaY = e.wheelDeltaY,
            delta;

            if (e.wheelDelta) { // Webkit and IE
                if (deltaY === undefined || deltaY) { // IE does not have deltaY, thus always scroll (horizontal scrolling is treated as vertical)
                    delta = e.wheelDelta;
                }
            } else if (e.detail && e.axis === e.VERTICAL_AXIS) { // Firefox and Opera
                delta = (-e.detail) * 10;
            }

        return delta;
    };

    kendo.caret = function (element, start, end) {
        var rangeElement;
        var isPosition = start !== undefined;

        if (end === undefined) {
            end = start;
        }

        if (element[0]) {
            element = element[0];
        }

        if (isPosition && element.disabled) {
            return;
        }

        try {
            if (element.selectionStart !== undefined) {
                if (isPosition) {
                    element.focus();
                    element.setSelectionRange(start, end);
                } else {
                    start = [element.selectionStart, element.selectionEnd];
                }
            } else if (document.selection) {
                if ($(element).is(":visible")) {
                    element.focus();
                }

                rangeElement = element.createTextRange();

                if (isPosition) {
                    rangeElement.collapse(true);
                    rangeElement.moveStart("character", start);
                    rangeElement.moveEnd("character", end - start);
                    rangeElement.select();
                } else {
                    var rangeDuplicated = rangeElement.duplicate(),
                        selectionStart, selectionEnd;

                        rangeElement.moveToBookmark(document.selection.createRange().getBookmark());
                        rangeDuplicated.setEndPoint('EndToStart', rangeElement);
                        selectionStart = rangeDuplicated.text.length;
                        selectionEnd = selectionStart + rangeElement.text.length;

                    start = [selectionStart, selectionEnd];
                }
            }
        } catch(e) {
            /* element is not focused or it is not in the DOM */
            start = [];
        }

        return start;
    };

})(jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        fx = kendo.effects,
        each = $.each,
        extend = $.extend,
        proxy = $.proxy,
        support = kendo.support,
        browser = support.browser,
        transforms = support.transforms,
        transitions = support.transitions,
        scaleProperties = { scale: 0, scalex: 0, scaley: 0, scale3d: 0 },
        translateProperties = { translate: 0, translatex: 0, translatey: 0, translate3d: 0 },
        hasZoom = (typeof document.documentElement.style.zoom !== "undefined") && !transforms,
        matrix3dRegExp = /matrix3?d?\s*\(.*,\s*([\d\.\-]+)\w*?,\s*([\d\.\-]+)\w*?,\s*([\d\.\-]+)\w*?,\s*([\d\.\-]+)\w*?/i,
        cssParamsRegExp = /^(-?[\d\.\-]+)?[\w\s]*,?\s*(-?[\d\.\-]+)?[\w\s]*/i,
        translateXRegExp = /translatex?$/i,
        oldEffectsRegExp = /(zoom|fade|expand)(\w+)/,
        singleEffectRegExp = /(zoom|fade|expand)/,
        unitRegExp = /[xy]$/i,
        transformProps = ["perspective", "rotate", "rotatex", "rotatey", "rotatez", "rotate3d", "scale", "scalex", "scaley", "scalez", "scale3d", "skew", "skewx", "skewy", "translate", "translatex", "translatey", "translatez", "translate3d", "matrix", "matrix3d"],
        transform2d = ["rotate", "scale", "scalex", "scaley", "skew", "skewx", "skewy", "translate", "translatex", "translatey", "matrix"],
        transform2units = { "rotate": "deg", scale: "", skew: "px", translate: "px" },
        cssPrefix = transforms.css,
        round = Math.round,
        BLANK = "",
        PX = "px",
        NONE = "none",
        AUTO = "auto",
        WIDTH = "width",
        HEIGHT = "height",
        HIDDEN = "hidden",
        ORIGIN = "origin",
        ABORT_ID = "abortId",
        OVERFLOW = "overflow",
        TRANSLATE = "translate",
        POSITION = "position",
        COMPLETE_CALLBACK = "completeCallback",
        TRANSITION = cssPrefix + "transition",
        TRANSFORM = cssPrefix + "transform",
        BACKFACE = cssPrefix + "backface-visibility",
        PERSPECTIVE = cssPrefix + "perspective",
        DEFAULT_PERSPECTIVE = "1500px",
        TRANSFORM_PERSPECTIVE = "perspective(" + DEFAULT_PERSPECTIVE + ")",
        ios7 = support.mobileOS && support.mobileOS.majorVersion == 7,
        directions = {
            left: {
                reverse: "right",
                property: "left",
                transition: "translatex",
                vertical: false,
                modifier: -1
            },
            right: {
                reverse: "left",
                property: "left",
                transition: "translatex",
                vertical: false,
                modifier: 1
            },
            down: {
                reverse: "up",
                property: "top",
                transition: "translatey",
                vertical: true,
                modifier: 1
            },
            up: {
                reverse: "down",
                property: "top",
                transition: "translatey",
                vertical: true,
                modifier: -1
            },
            top: {
                reverse: "bottom"
            },
            bottom: {
                reverse: "top"
            },
            "in": {
                reverse: "out",
                modifier: -1
            },
            out: {
                reverse: "in",
                modifier: 1
            },

            vertical: {
                reverse: "vertical"
            },

            horizontal: {
                reverse: "horizontal"
            }
        };

    kendo.directions = directions;

    extend($.fn, {
        kendoStop: function(clearQueue, gotoEnd) {
            if (transitions) {
                return fx.stopQueue(this, clearQueue || false, gotoEnd || false);
            } else {
                return this.stop(clearQueue, gotoEnd);
            }
        }
    });

    /* jQuery support for all transform animations (FF 3.5/3.6, Opera 10.x, IE9 */

    if (transforms && !transitions) {
        each(transform2d, function(idx, value) {
            $.fn[value] = function(val) {
                if (typeof val == "undefined") {
                    return animationProperty(this, value);
                } else {
                    var that = $(this)[0],
                        transformValue = value + "(" + val + transform2units[value.replace(unitRegExp, "")] + ")";

                    if (that.style.cssText.indexOf(TRANSFORM) == -1) {
                        $(this).css(TRANSFORM, transformValue);
                    } else {
                        that.style.cssText = that.style.cssText.replace(new RegExp(value + "\\(.*?\\)", "i"), transformValue);
                    }
                }
                return this;
            };

            $.fx.step[value] = function (fx) {
                $(fx.elem)[value](fx.now);
            };
        });

        var curProxy = $.fx.prototype.cur;
        $.fx.prototype.cur = function () {
            if (transform2d.indexOf(this.prop) != -1) {
                return parseFloat($(this.elem)[this.prop]());
            }

            return curProxy.apply(this, arguments);
        };
    }

    kendo.toggleClass = function(element, classes, options, add) {
        if (classes) {
            classes = classes.split(" ");

            if (transitions) {
                options = extend({
                    exclusive: "all",
                    duration: 400,
                    ease: "ease-out"
                }, options);

                element.css(TRANSITION, options.exclusive + " " + options.duration + "ms " + options.ease);
                setTimeout(function() {
                    element.css(TRANSITION, "").css(HEIGHT);
                }, options.duration); // TODO: this should fire a kendoAnimate session instead.
            }

            each(classes, function(idx, value) {
                element.toggleClass(value, add);
            });
        }

        return element;
    };

    kendo.parseEffects = function(input, mirror) {
        var effects = {};

        if (typeof input === "string") {
            each(input.split(" "), function(idx, value) {
                var redirectedEffect = !singleEffectRegExp.test(value),
                    resolved = value.replace(oldEffectsRegExp, function(match, $1, $2) {
                        return $1 + ":" + $2.toLowerCase();
                    }), // Support for old zoomIn/fadeOut style, now deprecated.
                    effect = resolved.split(":"),
                    direction = effect[1],
                    effectBody = {};

                if (effect.length > 1) {
                    effectBody.direction = (mirror && redirectedEffect ? directions[direction].reverse : direction);
                }

                effects[effect[0]] = effectBody;
            });
        } else {
            each(input, function(idx) {
                var direction = this.direction;

                if (direction && mirror && !singleEffectRegExp.test(idx)) {
                    this.direction = directions[direction].reverse;
                }

                effects[idx] = this;
            });
        }

        return effects;
    };

    function parseInteger(value) {
        return parseInt(value, 10);
    }

    function parseCSS(element, property) {
        return parseInteger(element.css(property));
    }

    function keys(obj) {
        var acc = [];
        for (var propertyName in obj) {
            acc.push(propertyName);
        }
        return acc;
    }

    function strip3DTransforms(properties) {
        for (var key in properties) {
            if (transformProps.indexOf(key) != -1 && transform2d.indexOf(key) == -1) {
                delete properties[key];
            }
        }

        return properties;
    }

    function normalizeCSS(element, properties) {
        var transformation = [], cssValues = {}, lowerKey, key, value, isTransformed;

        for (key in properties) {
            lowerKey = key.toLowerCase();
            isTransformed = transforms && transformProps.indexOf(lowerKey) != -1;

            if (!support.hasHW3D && isTransformed && transform2d.indexOf(lowerKey) == -1) {
                delete properties[key];
            } else {
                value = properties[key];

                if (isTransformed) {
                    transformation.push(key + "(" + value + ")");
                } else {
                    cssValues[key] = value;
                }
            }
        }

        if (transformation.length) {
            cssValues[TRANSFORM] = transformation.join(" ");
        }

        return cssValues;
    }

    if (transitions) {
        extend(fx, {
            transition: function(element, properties, options) {
                var css,
                    delay = 0,
                    oldKeys = element.data("keys") || [],
                    timeoutID;

                options = extend({
                        duration: 200,
                        ease: "ease-out",
                        complete: null,
                        exclusive: "all"
                    },
                    options
                );

                var stopTransitionCalled = false;

                var stopTransition = function() {
                    if (!stopTransitionCalled) {
                        stopTransitionCalled = true;

                        if (timeoutID) {
                            clearTimeout(timeoutID);
                            timeoutID = null;
                        }

                        element
                        .removeData(ABORT_ID)
                        .dequeue()
                        .css(TRANSITION, "")
                        .css(TRANSITION);

                        options.complete.call(element);
                    }
                };

                options.duration = $.fx ? $.fx.speeds[options.duration] || options.duration : options.duration;

                css = normalizeCSS(element, properties);

                $.merge(oldKeys, keys(css));
                element
                    .data("keys", $.unique(oldKeys))
                    .height();

                element.css(TRANSITION, options.exclusive + " " + options.duration + "ms " + options.ease).css(TRANSITION);
                element.css(css).css(TRANSFORM);

                /**
                 * Use transitionEnd event for browsers who support it - but duplicate it with setTimeout, as the transitionEnd event will not be triggered if no CSS properties change.
                 * This should be cleaned up at some point (widget by widget), and refactored to widgets not relying on the complete callback if no transition occurs.
                 *
                 * For IE9 and below, resort to setTimeout.
                 */
                if (transitions.event) {
                    element.one(transitions.event, stopTransition);
                    if (options.duration !== 0) {
                        delay = 500;
                    }
                }

                timeoutID = setTimeout(stopTransition, options.duration + delay);
                element.data(ABORT_ID, timeoutID);
                element.data(COMPLETE_CALLBACK, stopTransition);
            },

            stopQueue: function(element, clearQueue, gotoEnd) {
                var cssValues,
                    taskKeys = element.data("keys"),
                    retainPosition = (!gotoEnd && taskKeys),
                    completeCallback = element.data(COMPLETE_CALLBACK);

                if (retainPosition) {
                    cssValues = kendo.getComputedStyles(element[0], taskKeys);
                }

                if (completeCallback) {
                    completeCallback();
                }

                if (retainPosition) {
                    element.css(cssValues);
                }

                return element
                        .removeData("keys")
                        .stop(clearQueue);
            }
        });
    }

    function animationProperty(element, property) {
        if (transforms) {
            var transform = element.css(TRANSFORM);
            if (transform == NONE) {
                return property == "scale" ? 1 : 0;
            }

            var match = transform.match(new RegExp(property + "\\s*\\(([\\d\\w\\.]+)")),
                computed = 0;

            if (match) {
                computed = parseInteger(match[1]);
            } else {
                match = transform.match(matrix3dRegExp) || [0, 0, 0, 0, 0];
                property = property.toLowerCase();

                if (translateXRegExp.test(property)) {
                    computed = parseFloat(match[3] / match[2]);
                } else if (property == "translatey") {
                    computed = parseFloat(match[4] / match[2]);
                } else if (property == "scale") {
                    computed = parseFloat(match[2]);
                } else if (property == "rotate") {
                    computed = parseFloat(Math.atan2(match[2], match[1]));
                }
            }

            return computed;
        } else {
            return parseFloat(element.css(property));
        }
    }

    var EffectSet = kendo.Class.extend({
        init: function(element, options) {
            var that = this;

            that.element = element;
            that.effects = [];
            that.options = options;
            that.restore = [];
        },

        run: function(effects) {
            var that = this,
                effect,
                idx, jdx,
                length = effects.length,
                element = that.element,
                options = that.options,
                deferred = $.Deferred(),
                start = {},
                end = {},
                target,
                children,
                childrenLength;

            that.effects = effects;

            deferred.then($.proxy(that, "complete"));

            element.data("animating", true);

            for (idx = 0; idx < length; idx ++) {
                effect = effects[idx];

                effect.setReverse(options.reverse);
                effect.setOptions(options);

                that.addRestoreProperties(effect.restore);

                effect.prepare(start, end);

                children = effect.children();

                for (jdx = 0, childrenLength = children.length; jdx < childrenLength; jdx ++) {
                    children[jdx].duration(options.duration).run();
                }
            }

            // legacy support for options.properties
            for (var effectName in options.effects) {
                extend(end, options.effects[effectName].properties);
            }

            // Show the element initially
            if (!element.is(":visible")) {
                extend(start, { display: element.data("olddisplay") || "block" });
            }

            if (transforms && !options.reset) {
                target = element.data("targetTransform");

                if (target) {
                    start = extend(target, start);
                }
            }

            start = normalizeCSS(element, start);

            if (transforms && !transitions) {
                start = strip3DTransforms(start);
            }

            element.css(start)
                   .css(TRANSFORM); // Nudge

            for (idx = 0; idx < length; idx ++) {
                effects[idx].setup();
            }

            if (options.init) {
                options.init();
            }

            element.data("targetTransform", end);
            fx.animate(element, end, extend({}, options, { complete: deferred.resolve }));

            return deferred.promise();
        },

        stop: function() {
            $(this.element).kendoStop(true, true);
        },

        addRestoreProperties: function(restore) {
            var element = this.element,
                value,
                i = 0,
                length = restore.length;

            for (; i < length; i ++) {
                value = restore[i];

                this.restore.push(value);

                if (!element.data(value)) {
                    element.data(value, element.css(value));
                }
            }
        },

        restoreCallback: function() {
            var element = this.element;

            for (var i = 0, length = this.restore.length; i < length; i ++) {
                var value = this.restore[i];
                element.css(value, element.data(value));
            }
        },

        complete: function() {
            var that = this,
                idx = 0,
                element = that.element,
                options = that.options,
                effects = that.effects,
                length = effects.length;

            element
                .removeData("animating")
                .dequeue(); // call next animation from the queue

            if (options.hide) {
                element.data("olddisplay", element.css("display")).hide();
            }

            this.restoreCallback();

            if (hasZoom && !transforms) {
                setTimeout($.proxy(this, "restoreCallback"), 0); // Again jQuery callback in IE8-
            }

            for (; idx < length; idx ++) {
                effects[idx].teardown();
            }

            if (options.completeCallback) {
                options.completeCallback(element);
            }
        }
    });

    fx.promise = function(element, options) {
        var effects = [],
            effectClass,
            effectSet = new EffectSet(element, options),
            parsedEffects = kendo.parseEffects(options.effects),
            effect;

        options.effects = parsedEffects;

        for (var effectName in parsedEffects) {
            effectClass = fx[capitalize(effectName)];

            if (effectClass) {
                effect = new effectClass(element, parsedEffects[effectName].direction);
                effects.push(effect);
           }
        }

        if (effects[0]) {
            effectSet.run(effects);
        } else { // Not sure how would an fx promise reach this state - means that you call kendoAnimate with no valid effects? Why?
            if (!element.is(":visible")) {
                element.css({ display: element.data("olddisplay") || "block" }).css("display");
            }

            if (options.init) {
                options.init();
            }

            element.dequeue();
            effectSet.complete();
        }
    };

    extend(fx, {
        animate: function(elements, properties, options) {
            var useTransition = options.transition !== false;
            delete options.transition;

            if (transitions && "transition" in fx && useTransition) {
                fx.transition(elements, properties, options);
            } else {
                if (transforms) {
                    elements.animate(strip3DTransforms(properties), { queue: false, show: false, hide: false, duration: options.duration, complete: options.complete }); // Stop animate from showing/hiding the element to be able to hide it later on.
                } else {
                    elements.each(function() {
                        var element = $(this),
                            multiple = {};

                        each(transformProps, function(idx, value) { // remove transforms to avoid IE and older browsers confusion
                            var params,
                                currentValue = properties ? properties[value]+ " " : null; // We need to match

                            if (currentValue) {
                                var single = properties;

                                if (value in scaleProperties && properties[value] !== undefined) {
                                    params = currentValue.match(cssParamsRegExp);
                                    if (transforms) {
                                        extend(single, { scale: +params[0] });
                                    }
                                } else {
                                    if (value in translateProperties && properties[value] !== undefined) {
                                        var position = element.css(POSITION),
                                            isFixed = (position == "absolute" || position == "fixed");

                                        if (!element.data(TRANSLATE)) {
                                            if (isFixed) {
                                                element.data(TRANSLATE, {
                                                    top: parseCSS(element, "top") || 0,
                                                    left: parseCSS(element, "left") || 0,
                                                    bottom: parseCSS(element, "bottom"),
                                                    right: parseCSS(element, "right")
                                                });
                                            } else {
                                                element.data(TRANSLATE, {
                                                    top: parseCSS(element, "marginTop") || 0,
                                                    left: parseCSS(element, "marginLeft") || 0
                                                });
                                            }
                                        }

                                        var originalPosition = element.data(TRANSLATE);

                                        params = currentValue.match(cssParamsRegExp);
                                        if (params) {

                                            var dX = value == TRANSLATE + "y" ? +null : +params[1],
                                                dY = value == TRANSLATE + "y" ? +params[1] : +params[2];

                                            if (isFixed) {
                                                if (!isNaN(originalPosition.right)) {
                                                    if (!isNaN(dX)) { extend(single, { right: originalPosition.right - dX }); }
                                                } else {
                                                    if (!isNaN(dX)) { extend(single, { left: originalPosition.left + dX }); }
                                                }

                                                if (!isNaN(originalPosition.bottom)) {
                                                    if (!isNaN(dY)) { extend(single, { bottom: originalPosition.bottom - dY }); }
                                                } else {
                                                    if (!isNaN(dY)) { extend(single, { top: originalPosition.top + dY }); }
                                                }
                                            } else {
                                                if (!isNaN(dX)) { extend(single, { marginLeft: originalPosition.left + dX }); }
                                                if (!isNaN(dY)) { extend(single, { marginTop: originalPosition.top + dY }); }
                                            }
                                        }
                                    }
                                }

                                if (!transforms && value != "scale" && value in single) {
                                    delete single[value];
                                }

                                if (single) {
                                    extend(multiple, single);
                                }
                            }
                        });

                        if (browser.msie) {
                            delete multiple.scale;
                        }

                        element.animate(multiple, { queue: false, show: false, hide: false, duration: options.duration, complete: options.complete }); // Stop animate from showing/hiding the element to be able to hide it later on.
                    });
                }
            }
        }
    });

    fx.animatedPromise = fx.promise;

    var Effect = kendo.Class.extend({
        init: function(element, direction) {
            var that = this;
            that.element = element;
            that._direction = direction;
            that.options = {};
            that._additionalEffects = [];

            if (!that.restore) {
                that.restore = [];
            }
        },

// Public API
        reverse: function() {
            this._reverse = true;
            return this.run();
        },

        play: function() {
            this._reverse = false;
            return this.run();
        },

        add: function(additional) {
            this._additionalEffects.push(additional);
            return this;
        },

        direction: function(value) {
            this._direction = value;
            return this;
        },

        duration: function(duration) {
            this._duration = duration;
            return this;
        },

        compositeRun: function() {
            var that = this,
                effectSet = new EffectSet(that.element, { reverse: that._reverse, duration: that._duration }),
                effects = that._additionalEffects.concat([ that ]);

            return effectSet.run(effects);
        },

        run: function() {
            if (this._additionalEffects && this._additionalEffects[0]) {
                return this.compositeRun();
            }

            var that = this,
                element = that.element,
                idx = 0,
                restore = that.restore,
                length = restore.length,
                value,
                deferred = $.Deferred(),
                start = {},
                end = {},
                target,
                children = that.children(),
                childrenLength = children.length;

            deferred.then($.proxy(that, "_complete"));

            element.data("animating", true);

            for (idx = 0; idx < length; idx ++) {
                value = restore[idx];

                if (!element.data(value)) {
                    element.data(value, element.css(value));
                }
            }

            for (idx = 0; idx < childrenLength; idx ++) {
                children[idx].duration(that._duration).run();
            }

            that.prepare(start, end);

            if (!element.is(":visible")) {
                extend(start, { display: element.data("olddisplay") || "block" });
            }

            if (transforms) {
                target = element.data("targetTransform");

                if (target) {
                    start = extend(target, start);
                }
            }

            start = normalizeCSS(element, start);

            if (transforms && !transitions) {
                start = strip3DTransforms(start);
            }

            element.css(start).css(TRANSFORM); // Trick webkit into re-rendering

            that.setup();

            element.data("targetTransform", end);
            fx.animate(element, end, { duration: that._duration, complete: deferred.resolve });

            return deferred.promise();
        },

        stop: function() {
            var idx = 0,
                children = this.children(),
                childrenLength = children.length;

            for (idx = 0; idx < childrenLength; idx ++) {
                children[idx].stop();
            }

            $(this.element).kendoStop(true, true);
            return this;
        },

        restoreCallback: function() {
            var element = this.element;

            for (var i = 0, length = this.restore.length; i < length; i ++) {
                var value = this.restore[i];
                element.css(value, element.data(value));
            }
        },

        _complete: function() {
            var that = this,
                element = that.element;

            element
                .removeData("animating")
                .dequeue(); // call next animation from the queue

            that.restoreCallback();

            if (that.shouldHide()) {
                element.data("olddisplay", element.css("display")).hide();
            }

            if (hasZoom && !transforms) {
                setTimeout($.proxy(that, "restoreCallback"), 0); // Again jQuery callback in IE8-
            }

            that.teardown();
        },

        /////////////////////////// Support for kendo.animate;
        setOptions: function(options) {
            extend(true, this.options, options);
        },

        children: function() {
            return [];
        },

        shouldHide: $.noop,

        setup: $.noop,
        prepare: $.noop,
        teardown: $.noop,
        directions: [],

        setReverse: function(reverse) {
            this._reverse = reverse;
            return this;
        }
    });

    function capitalize(word) {
        return word.charAt(0).toUpperCase() + word.substring(1);
    }

    function createEffect(name, definition) {
        var effectClass = Effect.extend(definition),
            directions = effectClass.prototype.directions;

        fx[capitalize(name)] = effectClass;

        fx.Element.prototype[name] = function(direction, opt1, opt2, opt3) {
            return new effectClass(this.element, direction, opt1, opt2, opt3);
        };

        each(directions, function(idx, theDirection) {
            fx.Element.prototype[name + capitalize(theDirection)] = function(opt1, opt2, opt3) {
                return new effectClass(this.element, theDirection, opt1, opt2, opt3);
            };
        });
    }

    var FOUR_DIRECTIONS = ["left", "right", "up", "down"],
        IN_OUT = ["in", "out"];

    createEffect("slideIn", {
        directions: FOUR_DIRECTIONS,

        divisor: function(value) {
            this.options.divisor = value;
            return this;
        },

        prepare: function(start, end) {
            var that = this,
                tmp,
                element = that.element,
                direction = directions[that._direction],
                offset = -direction.modifier * (direction.vertical ? element.outerHeight() : element.outerWidth()),
                startValue = offset / (that.options && that.options.divisor || 1) + PX,
                endValue = "0px";

            if (that._reverse) {
                tmp = start;
                start = end;
                end = tmp;
            }

            if (transforms) {
                start[direction.transition] = startValue;
                end[direction.transition] = endValue;
            } else {
                start[direction.property] = startValue;
                end[direction.property] = endValue;
            }
        }
    });

    createEffect("tile", {
        directions: FOUR_DIRECTIONS,

        init: function(element, direction, previous) {
            Effect.prototype.init.call(this, element, direction);
            this.options = { previous: previous };
        },

        previousDivisor: function(value) {
            this.options.previousDivisor = value;
            return this;
        },

        children: function() {
            var that = this,
                reverse = that._reverse,
                previous = that.options.previous,
                divisor = that.options.previousDivisor || 1,
                dir = that._direction;

            var children = [ kendo.fx(that.element).slideIn(dir).setReverse(reverse) ];

            if (previous) {
                children.push( kendo.fx(previous).slideIn(directions[dir].reverse).divisor(divisor).setReverse(!reverse) );
            }

            return children;
        }
    });

    function createToggleEffect(name, property, defaultStart, defaultEnd) {
        createEffect(name, {
            directions: IN_OUT,

            startValue: function(value) {
                this._startValue = value;
                return this;
            },

            endValue: function(value) {
                this._endValue = value;
                return this;
            },

            shouldHide: function() {
               return this._shouldHide;
            },

            prepare: function(start, end) {
                var that = this,
                    startValue,
                    endValue,
                    out = this._direction === "out",
                    startDataValue = that.element.data(property),
                    startDataValueIsSet = !(isNaN(startDataValue) || startDataValue == defaultStart);

                if (startDataValueIsSet) {
                    startValue = startDataValue;
                } else if (typeof this._startValue !== "undefined") {
                    startValue = this._startValue;
                } else {
                    startValue = out ? defaultStart : defaultEnd;
                }

                if (typeof this._endValue !== "undefined") {
                    endValue = this._endValue;
                } else {
                    endValue = out ? defaultEnd : defaultStart;
                }

                if (this._reverse) {
                    start[property] = endValue;
                    end[property] = startValue;
                } else {
                    start[property] = startValue;
                    end[property] = endValue;
                }

                that._shouldHide = end[property] === defaultEnd;
            }
        });
    }

    createToggleEffect("fade", "opacity", 1, 0);
    createToggleEffect("zoom", "scale", 1, 0.01);

    createEffect("slideMargin", {
        prepare: function(start, end) {
            var that = this,
                element = that.element,
                options = that.options,
                origin = element.data(ORIGIN),
                offset = options.offset,
                margin,
                reverse = that._reverse;

            if (!reverse && origin === null) {
                element.data(ORIGIN, parseFloat(element.css("margin-" + options.axis)));
            }

            margin = (element.data(ORIGIN) || 0);
            end["margin-" + options.axis] = !reverse ? margin + offset : margin;
        }
    });

    createEffect("slideTo", {
        prepare: function(start, end) {
            var that = this,
                element = that.element,
                options = that.options,
                offset = options.offset.split(","),
                reverse = that._reverse;

            if (transforms) {
                end.translatex = !reverse ? offset[0] : 0;
                end.translatey = !reverse ? offset[1] : 0;
            } else {
                end.left = !reverse ? offset[0] : 0;
                end.top = !reverse ? offset[1] : 0;
            }
            element.css("left");
        }
    });

    createEffect("expand", {
        directions: ["horizontal", "vertical"],

        restore: [ OVERFLOW ],

        prepare: function(start, end) {
            var that = this,
                element = that.element,
                options = that.options,
                reverse = that._reverse,
                property = that._direction === "vertical" ? HEIGHT : WIDTH,
                setLength = element[0].style[property],
                oldLength = element.data(property),
                length = parseFloat(oldLength || setLength),
                realLength = round(element.css(property, AUTO)[property]());

            start.overflow = HIDDEN;

            length = (options && options.reset) ? realLength || length : length || realLength;

            end[property] = (reverse ? 0 : length) + PX;
            start[property] = (reverse ? length : 0) + PX;

            if (oldLength === undefined) {
                element.data(property, setLength);
            }
        },

        shouldHide: function() {
           return this._reverse;
        },

        teardown: function() {
            var that = this,
                element = that.element,
                property = that._direction === "vertical" ? HEIGHT : WIDTH,
                length = element.data(property);

            if (length == AUTO || length === BLANK) {
                setTimeout(function() { element.css(property, AUTO).css(property); }, 0); // jQuery animate complete callback in IE is called before the last animation step!
            }
        }
    });

    var TRANSFER_START_STATE = { position: "absolute", marginLeft: 0, marginTop: 0, scale: 1 };
    /**
     * Intersection point formulas are taken from here - http://zonalandeducation.com/mmts/intersections/intersectionOfTwoLines1/intersectionOfTwoLines1.html
     * Formula for a linear function from two points from here - http://demo.activemath.org/ActiveMath2/search/show.cmd?id=mbase://AC_UK_calculus/functions/ex_linear_equation_two_points
     * The transform origin point is the intersection point of the two lines from the top left corners/top right corners of the element and target.
     * The math and variables below MAY BE SIMPLIFIED (zeroes removed), but this would make the formula too cryptic.
     */
    createEffect("transfer", {
        init: function(element, target) {
            this.element = element;
            this.options = { target: target };
            this.restore = [];
        },

        setup: function() {
            this.element.appendTo(document.body);
        },

        prepare: function(start, end) {
            var that = this,
                element = that.element,
                options = that.options,
                reverse = that._reverse,
                target = options.target,
                offset,
                currentScale = animationProperty(element, "scale"),
                targetOffset = target.offset(),
                scale = target.outerHeight() / element.outerHeight();

            extend(start, TRANSFER_START_STATE);
            end.scale = 1;

            element.css(TRANSFORM, "scale(1)").css(TRANSFORM);
            offset = element.offset();
            element.css(TRANSFORM, "scale(" + currentScale + ")");

            var x1 = 0,
                y1 = 0,

                x2 = targetOffset.left - offset.left,
                y2 = targetOffset.top - offset.top,

                x3 = x1 + element.outerWidth(),
                y3 = y1,

                x4 = x2 + target.outerWidth(),
                y4 = y2,

                Z1 = (y2 - y1) / (x2 - x1),
                Z2 = (y4 - y3) / (x4 - x3),

                X = (y1 - y3 - Z1 * x1 + Z2 * x3) / (Z2 - Z1),
                Y = y1 + Z1 * (X - x1);

            start.top = offset.top;
            start.left = offset.left;
            start.transformOrigin = X + PX + " " + Y + PX;

            if (reverse) {
                start.scale = scale;
            } else {
                end.scale = scale;
            }
        }
    });


    var CLIPS = {
        top: "rect(auto auto $size auto)",
        bottom: "rect($size auto auto auto)",
        left: "rect(auto $size auto auto)",
        right: "rect(auto auto auto $size)"
    };

    var ROTATIONS = {
        top:    { start: "rotatex(0deg)", end: "rotatex(180deg)" },
        bottom: { start: "rotatex(-180deg)", end: "rotatex(0deg)" },
        left:   { start: "rotatey(0deg)", end: "rotatey(-180deg)" },
        right:  { start: "rotatey(180deg)", end: "rotatey(0deg)" }
    };

    function clipInHalf(container, direction) {
        var vertical = kendo.directions[direction].vertical,
            size = (container[vertical ? HEIGHT : WIDTH]() / 2) + "px";

        return CLIPS[direction].replace("$size", size);
    }

    createEffect("turningPage", {
        directions: FOUR_DIRECTIONS,

        init: function(element, direction, container) {
            Effect.prototype.init.call(this, element, direction);
            this._container = container;
        },

        prepare: function(start, end) {
            var that = this,
                reverse = that._reverse,
                direction = reverse ? directions[that._direction].reverse : that._direction,
                rotation = ROTATIONS[direction];

            start.zIndex = 1;

            if (that._clipInHalf) {
               start.clip = clipInHalf(that._container, kendo.directions[direction].reverse);
            }

            start[BACKFACE] = HIDDEN;

            end[TRANSFORM] = TRANSFORM_PERSPECTIVE + (reverse ? rotation.start : rotation.end);
            start[TRANSFORM] = TRANSFORM_PERSPECTIVE + (reverse ? rotation.end : rotation.start);
        },

        setup: function() {
            this._container.append(this.element);
        },

        face: function(value) {
            this._face = value;
            return this;
        },

        shouldHide: function() {
            var that = this,
                reverse = that._reverse,
                face = that._face;

            return (reverse && !face) || (!reverse && face);
        },

        clipInHalf: function(value) {
            this._clipInHalf = value;
            return this;
        },

        temporary: function() {
            this.element.addClass('temp-page');
            return this;
        }
    });

    createEffect("staticPage", {
        directions: FOUR_DIRECTIONS,

        init: function(element, direction, container) {
            Effect.prototype.init.call(this, element, direction);
            this._container = container;
        },

        restore: ["clip"],

        prepare: function(start, end) {
            var that = this,
                direction = that._reverse ? directions[that._direction].reverse : that._direction;

            start.clip = clipInHalf(that._container, direction);
            start.opacity = 0.999;
            end.opacity = 1;
        },

        shouldHide: function() {
            var that = this,
                reverse = that._reverse,
                face = that._face;

            return (reverse && !face) || (!reverse && face);
        },

        face: function(value) {
            this._face = value;
            return this;
        }
    });

    createEffect("pageturn", {
        directions: ["horizontal", "vertical"],

        init: function(element, direction, face, back) {
            Effect.prototype.init.call(this, element, direction);
            this.options = {};
            this.options.face = face;
            this.options.back = back;
        },

        children: function() {
            var that = this,
                options = that.options,
                direction = that._direction === "horizontal" ? "left" : "top",
                reverseDirection = kendo.directions[direction].reverse,
                reverse = that._reverse,
                temp,
                faceClone = options.face.clone(true).removeAttr("id"),
                backClone = options.back.clone(true).removeAttr("id"),
                element = that.element;

            if (reverse) {
                temp = direction;
                direction = reverseDirection;
                reverseDirection = temp;
            }

            return [
                kendo.fx(options.face).staticPage(direction, element).face(true).setReverse(reverse),
                kendo.fx(options.back).staticPage(reverseDirection, element).setReverse(reverse),
                kendo.fx(faceClone).turningPage(direction, element).face(true).clipInHalf(true).temporary().setReverse(reverse),
                kendo.fx(backClone).turningPage(reverseDirection, element).clipInHalf(true).temporary().setReverse(reverse)
            ];
        },

        prepare: function(start, end) {
            start[PERSPECTIVE] = DEFAULT_PERSPECTIVE;
            start.transformStyle = "preserve-3d";
            // hack to trigger transition end.
            start.opacity = 0.999;
            end.opacity = 1;
        },

        teardown: function() {
            this.element.find(".temp-page").remove();
        }
    });

    createEffect("flip", {
        directions: ["horizontal", "vertical"],

        init: function(element, direction, face, back) {
            Effect.prototype.init.call(this, element, direction);
            this.options = {};
            this.options.face = face;
            this.options.back = back;
        },

        children: function() {
            var that = this,
                options = that.options,
                direction = that._direction === "horizontal" ? "left" : "top",
                reverseDirection = kendo.directions[direction].reverse,
                reverse = that._reverse,
                temp,
                element = that.element;

            if (reverse) {
                temp = direction;
                direction = reverseDirection;
                reverseDirection = temp;
            }

            return [
                kendo.fx(options.face).turningPage(direction, element).face(true).setReverse(reverse),
                kendo.fx(options.back).turningPage(reverseDirection, element).setReverse(reverse)
            ];
        },

        prepare: function(start) {
            start[PERSPECTIVE] = DEFAULT_PERSPECTIVE;
            start.transformStyle = "preserve-3d";
        }
    });

    var RESTORE_OVERFLOW = !support.mobileOS.android;

    createEffect("replace", {
        init: function(element, previous, transitionClass) {
            Effect.prototype.init.call(this, element);
            this._previous = $(previous);
            this._transitionClass = transitionClass;
        },

        duration: function() {
            throw new Error("The replace effect does not support duration setting; the effect duration may be customized through the transition class rule");
        },

        _both: function() {
            return $().add(this._element).add(this._previous);
        },

        _containerClass: function() {
            var direction = this._direction,
                containerClass = "k-fx k-fx-start k-fx-" + this._transitionClass;

            if (direction) {
                containerClass += " k-fx-" + direction;
            }

            if (this._reverse) {
                containerClass += " k-fx-reverse";
            }

            return containerClass;
        },

        complete: function() {
            if (!this.deferred) {
                return;
            }
            var container = this.container;

            container.removeClass("k-fx-end").removeClass(this._containerClass());
            this._previous.hide().removeClass("k-fx-current");
            this.element.removeClass("k-fx-next");

            if (RESTORE_OVERFLOW) {
                container.css(OVERFLOW, "");
            }

            if (!this.isAbsolute) {
                this._both().css(POSITION, "");
            }

            this.deferred.resolve();
            delete this.deferred;
        },

        run: function() {
            if (this._additionalEffects && this._additionalEffects[0]) {
                return this.compositeRun();
            }

            var that = this,
                element = that.element,
                previous = that._previous,
                direction = that._direction,
                container = element.parents().filter(previous.parents()).first(),
                both = that._both(),
                deferred = $.Deferred(),
                originalPosition = element.css(POSITION),
                originalOverflow;

            // edge case for grid/scheduler, where the previous is already destroyed.
            if (!container.length) {
                container = element.parent();
            }

            this.container = container;
            this.deferred = deferred;
            this.isAbsolute = originalPosition  == "absolute";

            if (!this.isAbsolute) {
                both.css(POSITION, "absolute");
            }

            if (RESTORE_OVERFLOW) {
                originalOverflow = container.css(OVERFLOW);
                container.css(OVERFLOW, "hidden");
            }

            if (!transitions) {
                this.complete();
            } else {
                element.addClass("k-fx-hidden");

                container.addClass(this._containerClass());

                container.one(transitions.event, $.proxy(this, "complete"));

                kendo.animationFrame(function() {
                    element.removeClass("k-fx-hidden").addClass("k-fx-next");
                    previous.css("display", "").addClass("k-fx-current");

                    kendo.animationFrame(function() {
                        container.removeClass("k-fx-start").addClass("k-fx-end");
                    });
                });
            }

            return deferred.promise();
        },

        stop: function() {
            this.complete();
        }
    });

    var Animation = kendo.Class.extend({
        init: function() {
            var that = this;
            that._tickProxy = proxy(that._tick, that);
            that._started = false;
        },

        tick: $.noop,
        done: $.noop,
        onEnd: $.noop,
        onCancel: $.noop,

        start: function() {
            if (!this.enabled()) {
                return;
            }

            if (!this.done()) {
                this._started = true;
                kendo.animationFrame(this._tickProxy);
            } else {
                this.onEnd();
            }
        },

        enabled: function() {
            return true;
        },

        cancel: function() {
            this._started = false;
            this.onCancel();
        },

        _tick: function() {
            var that = this;
            if (!that._started) { return; }

            that.tick();

            if (!that.done()) {
                kendo.animationFrame(that._tickProxy);
            } else {
                that._started = false;
                that.onEnd();
            }
        }
    });

    var Transition = Animation.extend({
        init: function(options) {
            var that = this;
            extend(that, options);
            Animation.fn.init.call(that);
        },

        done: function() {
            return this.timePassed() >= this.duration;
        },

        timePassed: function() {
            return Math.min(this.duration, (new Date()) - this.startDate);
        },

        moveTo: function(options) {
            var that = this,
                movable = that.movable;

            that.initial = movable[that.axis];
            that.delta = options.location - that.initial;

            that.duration = typeof options.duration == "number" ? options.duration : 300;

            that.tick = that._easeProxy(options.ease);

            that.startDate = new Date();
            that.start();
        },

        _easeProxy: function(ease) {
            var that = this;

            return function() {
                that.movable.moveAxis(that.axis, ease(that.timePassed(), that.initial, that.delta, that.duration));
            };
        }
    });

    extend(Transition, {
        easeOutExpo: function (t, b, c, d) {
            return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
        },

        easeOutBack: function (t, b, c, d, s) {
            s = 1.70158;
            return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
        }
    });

    fx.Animation = Animation;
    fx.Transition = Transition;
    fx.createEffect = createEffect;
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        extend = $.extend,
        odataFilters = {
            eq: "eq",
            neq: "ne",
            gt: "gt",
            gte: "ge",
            lt: "lt",
            lte: "le",
            contains : "substringof",
            doesnotcontain: "substringof",
            endswith: "endswith",
            startswith: "startswith"
        },
        mappers = {
            pageSize: $.noop,
            page: $.noop,
            filter: function(params, filter) {
                if (filter) {
                    params.$filter = toOdataFilter(filter);
                }
            },
            sort: function(params, orderby) {
                var expr = $.map(orderby, function(value) {
                    var order = value.field.replace(/\./g, "/");

                    if (value.dir === "desc") {
                        order += " desc";
                    }

                    return order;
                }).join(",");

                if (expr) {
                    params.$orderby = expr;
                }
            },
            skip: function(params, skip) {
                if (skip) {
                    params.$skip = skip;
                }
            },
            take: function(params, take) {
                if (take) {
                    params.$top = take;
                }
            }
        },
        defaultDataType = {
            read: {
                dataType: "jsonp"
            }
        };

    function toOdataFilter(filter) {
        var result = [],
            logic = filter.logic || "and",
            idx,
            length,
            field,
            type,
            format,
            operator,
            value,
            ignoreCase,
            filters = filter.filters;

        for (idx = 0, length = filters.length; idx < length; idx++) {
            filter = filters[idx];
            field = filter.field;
            value = filter.value;
            operator = filter.operator;

            if (filter.filters) {
                filter = toOdataFilter(filter);
            } else {
                ignoreCase = filter.ignoreCase;
                field = field.replace(/\./g, "/");
                filter = odataFilters[operator];

                if (filter && value !== undefined) {
                    type = $.type(value);
                    if (type === "string") {
                        format = "'{1}'";
                        value = value.replace(/'/g, "''");

                        if (ignoreCase === true) {
                            field = "tolower(" + field + ")";
                        }

                    } else if (type === "date") {
                        format = "datetime'{1:yyyy-MM-ddTHH:mm:ss}'";
                    } else {
                        format = "{1}";
                    }

                    if (filter.length > 3) {
                        if (filter !== "substringof") {
                            format = "{0}({2}," + format + ")";
                        } else {
                            format = "{0}(" + format + ",{2})";
                            if (operator === "doesnotcontain") {
                                format += " eq false";
                            }
                        }
                    } else {
                        format = "{2} {0} " + format;
                    }

                    filter = kendo.format(format, filter, value, field);
                }
            }

            result.push(filter);
        }

        filter = result.join(" " + logic + " ");

        if (result.length > 1) {
            filter = "(" + filter + ")";
        }

        return filter;
    }

    extend(true, kendo.data, {
        schemas: {
            odata: {
                type: "json",
                data: function(data) {
                    return data.d.results || [data.d];
                },
                total: "d.__count"
            }
        },
        transports: {
            odata: {
                read: {
                    cache: true, // to prevent jQuery from adding cache buster
                    dataType: "jsonp",
                    jsonp: "$callback"
                },
                update: {
                    cache: true,
                    dataType: "json",
                    contentType: "application/json", // to inform the server the the request body is JSON encoded
                    type: "PUT" // can be PUT or MERGE
                },
                create: {
                    cache: true,
                    dataType: "json",
                    contentType: "application/json",
                    type: "POST" // must be POST to create new entity
                },
                destroy: {
                    cache: true,
                    dataType: "json",
                    type: "DELETE"
                },
                parameterMap: function(options, type) {
                    var params,
                        value,
                        option,
                        dataType;

                    options = options || {};
                    type = type || "read";
                    dataType = (this.options || defaultDataType)[type];
                    dataType = dataType ? dataType.dataType : "json";

                    if (type === "read") {
                        params = {
                            $inlinecount: "allpages"
                        };

                        if (dataType != "json") {
                            params.$format = "json";
                        }

                        for (option in options) {
                            if (mappers[option]) {
                                mappers[option](params, options[option]);
                            } else {
                                params[option] = options[option];
                            }
                        }
                    } else {
                        if (dataType !== "json") {
                            throw new Error("Only json dataType can be used for " + type + " operation.");
                        }

                        if (type !== "destroy") {
                            for (option in options) {
                                value = options[option];
                                if (typeof value === "number") {
                                    options[option] = value + "";
                                }
                            }

                            params = kendo.stringify(options);
                        }
                    }

                    return params;
                }
            }
        }
    });
})(window.kendo.jQuery);

/*jshint  eqnull: true, boss: true */
(function($, undefined) {
    var kendo = window.kendo,
        isArray = $.isArray,
        isPlainObject = $.isPlainObject,
        map = $.map,
        each = $.each,
        extend = $.extend,
        getter = kendo.getter,
        Class = kendo.Class;

    var XmlDataReader = Class.extend({
        init: function(options) {
            var that = this,
                total = options.total,
                model = options.model,
                parse = options.parse,
                errors = options.errors,
                serialize = options.serialize,
                data = options.data;

            if (model) {
                if (isPlainObject(model)) {
                    var base = options.modelBase || kendo.data.Model;

                    if (model.fields) {
                        each(model.fields, function(field, value) {
                            if (isPlainObject(value) && value.field) {
                                value = extend(value, { field: that.getter(value.field) });
                            } else {
                                value = { field: that.getter(value) };
                            }
                            model.fields[field] = value;
                        });
                    }

                    var id = model.id;
                    if (id) {
                        var idField = {};

                        idField[that.xpathToMember(id, true)] = { field : that.getter(id) };
                        model.fields = extend(idField, model.fields);
                        model.id = that.xpathToMember(id);
                    }
                    model = base.define(model);
                }

                that.model = model;
            }

            if (total) {
                if (typeof total == "string") {
                    total = that.getter(total);
                    that.total = function(data) {
                        return parseInt(total(data), 10);
                    };
                } else if (typeof total == "function"){
                    that.total = total;
                }
            }

            if (errors) {
                if (typeof errors == "string") {
                    errors = that.getter(errors);
                    that.errors = function(data) {
                        return errors(data) || null;
                    };
                } else if (typeof errors == "function"){
                    that.errors = errors;
                }
            }

            if (data) {
                if (typeof data == "string") {
                    data = that.xpathToMember(data);
                    that.data = function(value) {
                        var result = that.evaluate(value, data),
                            modelInstance;

                        result = isArray(result) ? result : [result];

                        if (that.model && model.fields) {
                            modelInstance = new that.model();

                            return map(result, function(value) {
                                if (value) {
                                    var record = {}, field;

                                    for (field in model.fields) {
                                        record[field] = modelInstance._parse(field, model.fields[field].field(value));
                                    }

                                    return record;
                                }
                            });
                        }

                        return result;
                    };
                } else if (typeof data == "function") {
                    that.data = data;
                }
            }

            if (typeof parse == "function") {
                var xmlParse = that.parse;

                that.parse = function(data) {
                    var xml = parse.call(that, data);
                    return xmlParse.call(that, xml);
                };
            }

            if (typeof serialize == "function") {
                that.serialize = serialize;
            }
        },
        total: function(result) {
            return this.data(result).length;
        },
        errors: function(data) {
            return data ? data.errors : null;
        },
        serialize: function(data) {
            return data;
        },
        parseDOM: function(element) {
            var result = {},
                parsedNode,
                node,
                nodeType,
                nodeName,
                member,
                attribute,
                attributes = element.attributes,
                attributeCount = attributes.length,
                idx;

            for (idx = 0; idx < attributeCount; idx++) {
                attribute = attributes[idx];
                result["@" + attribute.nodeName] = attribute.nodeValue;
            }

            for (node = element.firstChild; node; node = node.nextSibling) {
                nodeType = node.nodeType;

                if (nodeType === 3 || nodeType === 4) {
                    // text nodes or CDATA are stored as #text field
                    result["#text"] = node.nodeValue;
                } else if (nodeType === 1) {
                    // elements are stored as fields
                    parsedNode = this.parseDOM(node);

                    nodeName = node.nodeName;

                    member = result[nodeName];

                    if (isArray(member)) {
                        // elements of same nodeName are stored as array
                        member.push(parsedNode);
                    } else if (member !== undefined) {
                        member = [member, parsedNode];
                    } else {
                        member = parsedNode;
                    }

                    result[nodeName] = member;
                }
            }
            return result;
        },

        evaluate: function(value, expression) {
            var members = expression.split("."),
                member,
                result,
                length,
                intermediateResult,
                idx;

            while (member = members.shift()) {
                value = value[member];

                if (isArray(value)) {
                    result = [];
                    expression = members.join(".");

                    for (idx = 0, length = value.length; idx < length; idx++) {
                        intermediateResult = this.evaluate(value[idx], expression);

                        intermediateResult = isArray(intermediateResult) ? intermediateResult : [intermediateResult];

                        result.push.apply(result, intermediateResult);
                    }

                    return result;
                }
            }

            return value;
        },

        parse: function(xml) {
            var documentElement,
                tree,
                result = {};

            documentElement = xml.documentElement || $.parseXML(xml).documentElement;

            tree = this.parseDOM(documentElement);

            result[documentElement.nodeName] = tree;

            return result;
        },

        xpathToMember: function(member, raw) {
            if (!member) {
                return "";
            }

            member = member.replace(/^\//, "") // remove the first "/"
                           .replace(/\//g, "."); // replace all "/" with "."

            if (member.indexOf("@") >= 0) {
                // replace @attribute with '["@attribute"]'
                return member.replace(/\.?(@.*)/, raw? '$1':'["$1"]');
            }

            if (member.indexOf("text()") >= 0) {
                // replace ".text()" with '["#text"]'
                return member.replace(/(\.?text\(\))/, raw? '#text':'["#text"]');
            }

            return member;
        },
        getter: function(member) {
            return getter(this.xpathToMember(member), true);
        }
    });

    $.extend(true, kendo.data, {
        XmlDataReader: XmlDataReader,
        readers: {
            xml: XmlDataReader
        }
    });
})(window.kendo.jQuery);

/*jshint eqnull: true, loopfunc: true, evil: true */
(function($, undefined) {
    var extend = $.extend,
        proxy = $.proxy,
        isPlainObject = $.isPlainObject,
        isEmptyObject = $.isEmptyObject,
        isArray = $.isArray,
        grep = $.grep,
        ajax = $.ajax,
        map,
        each = $.each,
        noop = $.noop,
        kendo = window.kendo,
        isFunction = kendo.isFunction,
        Observable = kendo.Observable,
        Class = kendo.Class,
        STRING = "string",
        FUNCTION = "function",
        CREATE = "create",
        READ = "read",
        UPDATE = "update",
        DESTROY = "destroy",
        CHANGE = "change",
        SYNC = "sync",
        GET = "get",
        ERROR = "error",
        REQUESTSTART = "requestStart",
        PROGRESS = "progress",
        REQUESTEND = "requestEnd",
        crud = [CREATE, READ, UPDATE, DESTROY],
        identity = function(o) { return o; },
        getter = kendo.getter,
        stringify = kendo.stringify,
        math = Math,
        push = [].push,
        join = [].join,
        pop = [].pop,
        splice = [].splice,
        shift = [].shift,
        slice = [].slice,
        unshift = [].unshift,
        toString = {}.toString,
        stableSort = kendo.support.stableSort,
        dateRegExp = /^\/Date\((.*?)\)\/$/,
        newLineRegExp = /(\r+|\n+)/g,
        quoteRegExp = /(?=['\\])/g;

    var ObservableArray = Observable.extend({
        init: function(array, type) {
            var that = this;

            that.type = type || ObservableObject;

            Observable.fn.init.call(that);

            that.length = array.length;

            that.wrapAll(array, that);
        },

        toJSON: function() {
            var idx, length = this.length, value, json = new Array(length);

            for (idx = 0; idx < length; idx++){
                value = this[idx];

                if (value instanceof ObservableObject) {
                    value = value.toJSON();
                }

                json[idx] = value;
            }

            return json;
        },

        parent: noop,

        wrapAll: function(source, target) {
            var that = this,
                idx,
                length,
                parent = function() {
                    return that;
                };

            target = target || [];

            for (idx = 0, length = source.length; idx < length; idx++) {
                target[idx] = that.wrap(source[idx], parent);
            }

            return target;
        },

        wrap: function(object, parent) {
            var that = this,
                observable;

            if (object !== null && toString.call(object) === "[object Object]") {
                observable = object instanceof that.type || object instanceof Model;

                if (!observable) {
                    object = object instanceof ObservableObject ? object.toJSON() : object;
                    object = new that.type(object);
                }

                object.parent = parent;

                object.bind(CHANGE, function(e) {
                    that.trigger(CHANGE, {
                        field: e.field,
                        node: e.node,
                        index: e.index,
                        items: e.items || [this],
                        action: e.node  ? (e.action || "itemchange") : "itemchange"
                    });
                });
            }

            return object;
        },

        push: function() {
            var index = this.length,
                items = this.wrapAll(arguments),
                result;

            result = push.apply(this, items);

            this.trigger(CHANGE, {
                action: "add",
                index: index,
                items: items
            });

            return result;
        },

        slice: slice,

        join: join,

        pop: function() {
            var length = this.length, result = pop.apply(this);

            if (length) {
                this.trigger(CHANGE, {
                    action: "remove",
                    index: length - 1,
                    items:[result]
                });
            }

            return result;
        },

        splice: function(index, howMany, item) {
            var items = this.wrapAll(slice.call(arguments, 2)),
                result, i, len;

            result = splice.apply(this, [index, howMany].concat(items));

            if (result.length) {
                this.trigger(CHANGE, {
                    action: "remove",
                    index: index,
                    items: result
                });

                for (i = 0, len = result.length; i < len; i++) {
                    if (result[i].children) {
                        result[i].unbind(CHANGE);
                    }
                }
            }

            if (item) {
                this.trigger(CHANGE, {
                    action: "add",
                    index: index,
                    items: items
                });
            }
            return result;
        },

        shift: function() {
            var length = this.length, result = shift.apply(this);

            if (length) {
                this.trigger(CHANGE, {
                    action: "remove",
                    index: 0,
                    items:[result]
                });
            }

            return result;
        },

        unshift: function() {
            var items = this.wrapAll(arguments),
                result;

            result = unshift.apply(this, items);

            this.trigger(CHANGE, {
                action: "add",
                index: 0,
                items: items
            });

            return result;
        },

        indexOf: function(item) {
            var that = this,
                idx,
                length;

            for (idx = 0, length = that.length; idx < length; idx++) {
                if (that[idx] === item) {
                    return idx;
                }
            }
            return -1;
        },

        forEach: function(callback) {
            var idx = 0,
                length = this.length;

            for (; idx < length; idx++) {
                callback(this[idx], idx, this);
            }
        },

        map: function(callback) {
            var idx = 0,
                result = [],
                length = this.length;

            for (; idx < length; idx++) {
                result[idx] = callback(this[idx], idx, this);
            }

            return result;
        },

        filter: function(callback) {
            var idx = 0,
                result = [],
                item,
                length = this.length;

            for (; idx < length; idx++) {
                item = this[idx];
                if (callback(item, idx, this)) {
                    result[result.length] = item;
                }
            }

            return result;
        },

        find: function(callback) {
            var idx = 0,
                item,
                length = this.length;

            for (; idx < length; idx++) {
                item = this[idx];
                if (callback(item, idx, this)) {
                    return item;
                }
            }
        },

        every: function(callback) {
            var idx = 0,
                item,
                length = this.length;

            for (; idx < length; idx++) {
                item = this[idx];
                if (!callback(item, idx, this)) {
                    return false;
                }
            }

            return true;
        },

        some: function(callback) {
            var idx = 0,
                item,
                length = this.length;

            for (; idx < length; idx++) {
                item = this[idx];
                if (callback(item, idx, this)) {
                    return true;
                }
            }

            return false;
        },

        // non-standard collection methods
        remove: function(item) {
            this.splice(this.indexOf(item), 1);
        },

        empty: function() {
            this.splice(0, this.length);
        }
    });

    function eventHandler(context, type, field, prefix) {
        return function(e) {
            var event = {}, key;

            for (key in e) {
                event[key] = e[key];
            }

            if (prefix) {
                event.field = field + "." + e.field;
            } else {
                event.field = field;
            }

            if (type == CHANGE && context._notifyChange) {
                context._notifyChange(event);
            }

            context.trigger(type, event);
        };
    }

    var ObservableObject = Observable.extend({
        init: function(value) {
            var that = this,
                member,
                field,
                parent = function() {
                    return that;
                };

            Observable.fn.init.call(this);

            for (field in value) {
                member = value[field];

                if (field.charAt(0) != "_") {
                    member = that.wrap(member, field, parent);
                }

                that[field] = member;
            }

            that.uid = kendo.guid();
        },

        shouldSerialize: function(field) {
            return this.hasOwnProperty(field) && field !== "_events" && typeof this[field] !== FUNCTION && field !== "uid";
        },

        forEach: function(f) {
            for (var i in this) {
                if (this.shouldSerialize(i)) {
                    f(this[i], i);
                }
            }
        },

        toJSON: function() {
            var result = {}, value, field;

            for (field in this) {
                if (this.shouldSerialize(field)) {
                    value = this[field];

                    if (value instanceof ObservableObject || value instanceof ObservableArray) {
                        value = value.toJSON();
                    }

                    result[field] = value;
                }
            }

            return result;
        },

        get: function(field) {
            var that = this, result;

            that.trigger(GET, { field: field });

            if (field === "this") {
                result = that;
            } else {
                result = kendo.getter(field, true)(that);
            }

            return result;
        },

        _set: function(field, value) {
            var that = this;
            var composite = field.indexOf(".") >= 0;

            if (composite) {
                var paths = field.split("."),
                    path = "";

                while (paths.length > 1) {
                    path += paths.shift();
                    var obj = kendo.getter(path, true)(that);
                    if (obj instanceof ObservableObject) {
                        obj.set(paths.join("."), value);
                        return composite;
                    }
                    path += ".";
                }
            }

            kendo.setter(field)(that, value);

            return composite;
        },

        set: function(field, value) {
            var that = this,
                current = kendo.getter(field, true)(that);

            if (current !== value) {

                if (!that.trigger("set", { field: field, value: value })) {
                    if (!that._set(field, that.wrap(value, field, function() { return that; })) || field.indexOf("(") >= 0 || field.indexOf("[") >= 0) {
                        that.trigger(CHANGE, { field: field });
                    }
                }
            }
        },

        parent: noop,

        wrap: function(object, field, parent) {
            var that = this,
                type = toString.call(object);

            if (object != null && (type === "[object Object]" || type === "[object Array]")) {
                var isObservableArray = object instanceof ObservableArray;
                var isDataSource = object instanceof DataSource;

                if (type === "[object Object]" && !isDataSource && !isObservableArray) {
                    if (!(object instanceof ObservableObject)) {
                        object = new ObservableObject(object);
                    }

                    if (object.parent() != parent()) {
                        object.bind(GET, eventHandler(that, GET, field, true));
                        object.bind(CHANGE, eventHandler(that, CHANGE, field, true));
                    }
                } else if (type === "[object Array]" || isObservableArray || isDataSource) {
                    if (!isObservableArray && !isDataSource) {
                        object = new ObservableArray(object);
                    }

                    if (object.parent() != parent()) {
                        object.bind(CHANGE, eventHandler(that, CHANGE, field, false));
                    }
                }

                object.parent = parent;
            }

            return object;
        }
    });

    function equal(x, y) {
        if (x === y) {
            return true;
        }

        var xtype = $.type(x), ytype = $.type(y), field;

        if (xtype !== ytype) {
            return false;
        }

        if (xtype === "date") {
            return x.getTime() === y.getTime();
        }

        if (xtype !== "object" && xtype !== "array") {
            return false;
        }

        for (field in x) {
            if (!equal(x[field], y[field])) {
                return false;
            }
        }

        return true;
    }

    var parsers = {
        "number": function(value) {
            return kendo.parseFloat(value);
        },

        "date": function(value) {
            return kendo.parseDate(value);
        },

        "boolean": function(value) {
            if (typeof value === STRING) {
                return value.toLowerCase() === "true";
            }
            return value != null ? !!value : value;
        },

        "string": function(value) {
            return value != null ? (value + "") : value;
        },

        "default": function(value) {
            return value;
        }
    };

    var defaultValues = {
        "string": "",
        "number": 0,
        "date": new Date(),
        "boolean": false,
        "default": ""
    };

    function getFieldByName(obj, name) {
        var field,
            fieldName;

        for (fieldName in obj) {
            field = obj[fieldName];
            if (isPlainObject(field) && field.field && field.field === name) {
                return field;
            } else if (field === name) {
                return field;
            }
        }
        return null;
    }

    var Model = ObservableObject.extend({
        init: function(data) {
            var that = this;

            if (!data || $.isEmptyObject(data)) {
                data = $.extend({}, that.defaults, data);
            }

            ObservableObject.fn.init.call(that, data);

            that.dirty = false;

            if (that.idField) {
                that.id = that.get(that.idField);

                if (that.id === undefined) {
                    that.id = that._defaultId;
                }
            }
        },

        shouldSerialize: function(field) {
            return ObservableObject.fn.shouldSerialize.call(this, field) && field !== "uid" && !(this.idField !== "id" && field === "id") && field !== "dirty" && field !== "_accessors";
        },

        _parse: function(field, value) {
            var that = this,
                fieldName = field,
                fields = (that.fields || {}),
                parse;

            field = fields[field];
            if (!field) {
                field = getFieldByName(fields, fieldName);
            }
            if (field) {
                parse = field.parse;
                if (!parse && field.type) {
                    parse = parsers[field.type.toLowerCase()];
                }
            }

            return parse ? parse(value) : value;
        },

        _notifyChange: function(e) {
            var action = e.action;

            if (action == "add" || action == "remove") {
                this.dirty = true;
            }
        },

        editable: function(field) {
            field = (this.fields || {})[field];
            return field ? field.editable !== false : true;
        },

        set: function(field, value, initiator) {
            var that = this;

            if (that.editable(field)) {
                value = that._parse(field, value);

                if (!equal(value, that.get(field))) {
                    that.dirty = true;
                    ObservableObject.fn.set.call(that, field, value, initiator);
                }
            }
        },

        accept: function(data) {
            var that = this,
                parent = function() { return that; },
                field;

            for (field in data) {
                var value = data[field];

                if (field.charAt(0) != "_") {
                    value = that.wrap(data[field], field, parent);
                }

                that._set(field, value);
            }

            if (that.idField) {
                that.id = that.get(that.idField);
            }

            that.dirty = false;
        },

        isNew: function() {
            return this.id === this._defaultId;
        }
    });

    Model.define = function(base, options) {
        if (options === undefined) {
            options = base;
            base = Model;
        }

        var model,
            proto = extend({ defaults: {} }, options),
            name,
            field,
            type,
            value,
            idx,
            length,
            fields = {},
            originalName,
            id = proto.id;

        if (id) {
            proto.idField = id;
        }

        if (proto.id) {
            delete proto.id;
        }

        if (id) {
            proto.defaults[id] = proto._defaultId = "";
        }

        if (toString.call(proto.fields) === "[object Array]") {
            for (idx = 0, length = proto.fields.length; idx < length; idx++) {
                field = proto.fields[idx];
                if (typeof field === STRING) {
                    fields[field] = {};
                } else if (field.field) {
                    fields[field.field] = field;
                }
            }
            proto.fields = fields;
        }

        for (name in proto.fields) {
            field = proto.fields[name];
            type = field.type || "default";
            value = null;
            originalName = name;

            name = typeof (field.field) === STRING ? field.field : name;

            if (!field.nullable) {
                value = proto.defaults[originalName !== name ? originalName : name] = field.defaultValue !== undefined ? field.defaultValue : defaultValues[type.toLowerCase()];
            }

            if (options.id === name) {
                proto._defaultId = value;
            }

            proto.defaults[originalName !== name ? originalName : name] = value;

            field.parse = field.parse || parsers[type];
        }

        model = base.extend(proto);
        model.define = function(options) {
            return Model.define(model, options);
        };

        if (proto.fields) {
            model.fields = proto.fields;
            model.idField = proto.idField;
        }

        return model;
    };

    var Comparer = {
        selector: function(field) {
            return isFunction(field) ? field : getter(field);
        },

        compare: function(field) {
            var selector = this.selector(field);
            return function (a, b) {
                a = selector(a);
                b = selector(b);

                if (a == null && b == null) {
                    return 0;
                }

                if (a == null) {
                    return -1;
                }

                if (b == null) {
                    return 1;
                }

                if (a.localeCompare) {
                    return a.localeCompare(b);
                }

                return a > b ? 1 : (a < b ? -1 : 0);
            };
        },

        create: function(sort) {
            var compare = sort.compare || this.compare(sort.field);

            if (sort.dir == "desc") {
                return function(a, b) {
                    return compare(b, a, true);
                };
            }

            return compare;
        },

        combine: function(comparers) {
            return function(a, b) {
                var result = comparers[0](a, b),
                    idx,
                    length;

                for (idx = 1, length = comparers.length; idx < length; idx ++) {
                    result = result || comparers[idx](a, b);
                }

                return result;
            };
        }
    };

    var StableComparer = extend({}, Comparer, {
        asc: function(field) {
            var selector = this.selector(field);
            return function (a, b) {
                var valueA = selector(a);
                var valueB = selector(b);

                if (valueA && valueA.getTime && valueB && valueB.getTime) {
                    valueA = valueA.getTime();
                    valueB = valueB.getTime();
                }

                if (valueA === valueB) {
                    return a.__position - b.__position;
                }

                if (valueA == null) {
                    return -1;
                }

                if (valueB == null) {
                    return 1;
                }

                if (valueA.localeCompare) {
                    return valueA.localeCompare(valueB);
                }

                return valueA > valueB ? 1 : -1;
            };
        },

        desc: function(field) {
            var selector = this.selector(field);
            return function (a, b) {
                var valueA = selector(a);
                var valueB = selector(b);

                if (valueA && valueA.getTime && valueB && valueB.getTime) {
                    valueA = valueA.getTime();
                    valueB = valueB.getTime();
                }

                if (valueA === valueB) {
                    return a.__position - b.__position;
                }

                if (valueA == null) {
                    return 1;
                }

                if (valueB == null) {
                    return -1;
                }

                if (valueB.localeCompare) {
                    return valueB.localeCompare(valueA);
                }

                return valueA < valueB ? 1 : -1;
            };
        },
        create: function(sort) {
           return this[sort.dir](sort.field);
        }
    });

    map = function (array, callback) {
        var idx, length = array.length, result = new Array(length);

        for (idx = 0; idx < length; idx++) {
            result[idx] = callback(array[idx], idx, array);
        }

        return result;
    };

    var operators = (function(){

        function quote(value) {
            return value.replace(quoteRegExp, "\\").replace(newLineRegExp, "");
        }

        function operator(op, a, b, ignore) {
            var date;

            if (b != null) {
                if (typeof b === STRING) {
                    b = quote(b);
                    date = dateRegExp.exec(b);
                    if (date) {
                        b = new Date(+date[1]);
                    } else if (ignore) {
                        b = "'" + b.toLowerCase() + "'";
                        a = "(" + a + " || '').toLowerCase()";
                    } else {
                        b = "'" + b + "'";
                    }
                }

                if (b.getTime) {
                    //b looks like a Date
                    a = "(" + a + "?" + a + ".getTime():" + a + ")";
                    b = b.getTime();
                }
            }

            return a + " " + op + " " + b;
        }

        return {
            eq: function(a, b, ignore) {
                return operator("==", a, b, ignore);
            },
            neq: function(a, b, ignore) {
                return operator("!=", a, b, ignore);
            },
            gt: function(a, b, ignore) {
                return operator(">", a, b, ignore);
            },
            gte: function(a, b, ignore) {
                return operator(">=", a, b, ignore);
            },
            lt: function(a, b, ignore) {
                return operator("<", a, b, ignore);
            },
            lte: function(a, b, ignore) {
                return operator("<=", a, b, ignore);
            },
            startswith: function(a, b, ignore) {
                if (ignore) {
                    a = "(" + a + " || '').toLowerCase()";
                    if (b) {
                        b = b.toLowerCase();
                    }
                }

                if (b) {
                    b = quote(b);
                }

                return a + ".lastIndexOf('" + b + "', 0) == 0";
            },
            endswith: function(a, b, ignore) {
                if (ignore) {
                    a = "(" + a + " || '').toLowerCase()";
                    if (b) {
                        b = b.toLowerCase();
                    }
                }

                if (b) {
                    b = quote(b);
                }

                return a + ".indexOf('" + b + "', " + a + ".length - " + (b || "").length + ") >= 0";
            },
            contains: function(a, b, ignore) {
                if (ignore) {
                    a = "(" + a + " || '').toLowerCase()";
                    if (b) {
                        b = b.toLowerCase();
                    }
                }

                if (b) {
                    b = quote(b);
                }

                return a + ".indexOf('" + b + "') >= 0";
            },
            doesnotcontain: function(a, b, ignore) {
                if (ignore) {
                    a = "(" + a + " || '').toLowerCase()";
                    if (b) {
                        b = b.toLowerCase();
                    }
                }

                if (b) {
                    b = quote(b);
                }

                return a + ".indexOf('" + b + "') == -1";
            }
        };
    })();

    function Query(data) {
        this.data = data || [];
    }

    Query.filterExpr = function(expression) {
        var expressions = [],
            logic = { and: " && ", or: " || " },
            idx,
            length,
            filter,
            expr,
            fieldFunctions = [],
            operatorFunctions = [],
            field,
            operator,
            filters = expression.filters;

        for (idx = 0, length = filters.length; idx < length; idx++) {
            filter = filters[idx];
            field = filter.field;
            operator = filter.operator;

            if (filter.filters) {
                expr = Query.filterExpr(filter);
                //Nested function fields or operators - update their index e.g. __o[0] -> __o[1]
                filter = expr.expression
                .replace(/__o\[(\d+)\]/g, function(match, index) {
                    index = +index;
                    return "__o[" + (operatorFunctions.length + index) + "]";
                })
                .replace(/__f\[(\d+)\]/g, function(match, index) {
                    index = +index;
                    return "__f[" + (fieldFunctions.length + index) + "]";
                });

                operatorFunctions.push.apply(operatorFunctions, expr.operators);
                fieldFunctions.push.apply(fieldFunctions, expr.fields);
            } else {
                if (typeof field === FUNCTION) {
                    expr = "__f[" + fieldFunctions.length +"](d)";
                    fieldFunctions.push(field);
                } else {
                    expr = kendo.expr(field);
                }

                if (typeof operator === FUNCTION) {
                    filter = "__o[" + operatorFunctions.length + "](" + expr + ", " + filter.value + ")";
                    operatorFunctions.push(operator);
                } else {
                    filter = operators[(operator || "eq").toLowerCase()](expr, filter.value, filter.ignoreCase !== undefined? filter.ignoreCase : true);
                }
            }

            expressions.push(filter);
        }

        return  { expression: "(" + expressions.join(logic[expression.logic]) + ")", fields: fieldFunctions, operators: operatorFunctions };
    };

    function normalizeSort(field, dir) {
        if (field) {
            var descriptor = typeof field === STRING ? { field: field, dir: dir } : field,
            descriptors = isArray(descriptor) ? descriptor : (descriptor !== undefined ? [descriptor] : []);

            return grep(descriptors, function(d) { return !!d.dir; });
        }
    }

    var operatorMap = {
        "==": "eq",
        equals: "eq",
        isequalto: "eq",
        equalto: "eq",
        equal: "eq",
        "!=": "neq",
        ne: "neq",
        notequals: "neq",
        isnotequalto: "neq",
        notequalto: "neq",
        notequal: "neq",
        "<": "lt",
        islessthan: "lt",
        lessthan: "lt",
        less: "lt",
        "<=": "lte",
        le: "lte",
        islessthanorequalto: "lte",
        lessthanequal: "lte",
        ">": "gt",
        isgreaterthan: "gt",
        greaterthan: "gt",
        greater: "gt",
        ">=": "gte",
        isgreaterthanorequalto: "gte",
        greaterthanequal: "gte",
        ge: "gte",
        notsubstringof: "doesnotcontain"
    };

    function normalizeOperator(expression) {
        var idx,
        length,
        filter,
        operator,
        filters = expression.filters;

        if (filters) {
            for (idx = 0, length = filters.length; idx < length; idx++) {
                filter = filters[idx];
                operator = filter.operator;

                if (operator && typeof operator === STRING) {
                    filter.operator = operatorMap[operator.toLowerCase()] || operator;
                }

                normalizeOperator(filter);
            }
        }
    }

    function normalizeFilter(expression) {
        if (expression && !isEmptyObject(expression)) {
            if (isArray(expression) || !expression.filters) {
                expression = {
                    logic: "and",
                    filters: isArray(expression) ? expression : [expression]
                };
            }

            normalizeOperator(expression);

            return expression;
        }
    }

    Query.normalizeFilter = normalizeFilter;

    function normalizeAggregate(expressions) {
        return isArray(expressions) ? expressions : [expressions];
    }

    function normalizeGroup(field, dir) {
        var descriptor = typeof field === STRING ? { field: field, dir: dir } : field,
        descriptors = isArray(descriptor) ? descriptor : (descriptor !== undefined ? [descriptor] : []);

        return map(descriptors, function(d) { return { field: d.field, dir: d.dir || "asc", aggregates: d.aggregates }; });
    }

    Query.prototype = {
        toArray: function () {
            return this.data;
        },
        range: function(index, count) {
            return new Query(this.data.slice(index, index + count));
        },
        skip: function (count) {
            return new Query(this.data.slice(count));
        },
        take: function (count) {
            return new Query(this.data.slice(0, count));
        },
        select: function (selector) {
            return new Query(map(this.data, selector));
        },
        order: function(selector, dir) {
            var sort = { dir: dir };

            if (selector) {
                if (selector.compare) {
                    sort.compare = selector.compare;
                } else {
                    sort.field = selector;
                }
            }

            return new Query(this.data.slice(0).sort(Comparer.create(sort)));
        },
        orderBy: function(selector) {
            return this.order(selector, "asc");
        },
        orderByDescending: function(selector) {
            return this.order(selector, "desc");
        },
        sort: function(field, dir, comparer) {
            var idx,
            length,
            descriptors = normalizeSort(field, dir),
            comparers = [];

            comparer = comparer || Comparer;

            if (descriptors.length) {
                for (idx = 0, length = descriptors.length; idx < length; idx++) {
                    comparers.push(comparer.create(descriptors[idx]));
                }

                return this.orderBy({ compare: comparer.combine(comparers) });
            }

            return this;
        },

        filter: function(expressions) {
            var idx,
            current,
            length,
            compiled,
            predicate,
            data = this.data,
            fields,
            operators,
            result = [],
            filter;

            expressions = normalizeFilter(expressions);

            if (!expressions || expressions.filters.length === 0) {
                return this;
            }

            compiled = Query.filterExpr(expressions);
            fields = compiled.fields;
            operators = compiled.operators;

            predicate = filter = new Function("d, __f, __o", "return " + compiled.expression);

            if (fields.length || operators.length) {
                filter = function(d) {
                    return predicate(d, fields, operators);
                };
            }

            for (idx = 0, length = data.length; idx < length; idx++) {
                current = data[idx];

                if (filter(current)) {
                    result.push(current);
                }
            }
            return new Query(result);
        },

        group: function(descriptors, allData) {
            descriptors =  normalizeGroup(descriptors || []);
            allData = allData || this.data;

            var that = this,
            result = new Query(that.data),
            descriptor;

            if (descriptors.length > 0) {
                descriptor = descriptors[0];
                result = result.groupBy(descriptor).select(function(group) {
                    var data = new Query(allData).filter([ { field: group.field, operator: "eq", value: group.value, ignoreCase: false } ]);
                    return {
                        field: group.field,
                        value: group.value,
                        items: descriptors.length > 1 ? new Query(group.items).group(descriptors.slice(1), data.toArray()).toArray() : group.items,
                        hasSubgroups: descriptors.length > 1,
                        aggregates: data.aggregate(descriptor.aggregates)
                    };
                });
            }
            return result;
        },

        groupBy: function(descriptor) {
            if (isEmptyObject(descriptor) || !this.data.length) {
                return new Query([]);
            }

            var field = descriptor.field,
                sorted = this._sortForGrouping(field, descriptor.dir || "asc"),
                accessor = kendo.accessor(field),
                item,
                groupValue = accessor.get(sorted[0], field),
                group = {
                    field: field,
                    value: groupValue,
                    items: []
                },
                currentValue,
                idx,
                len,
                result = [group];

            for(idx = 0, len = sorted.length; idx < len; idx++) {
                item = sorted[idx];
                currentValue = accessor.get(item, field);
                if(!groupValueComparer(groupValue, currentValue)) {
                    groupValue = currentValue;
                    group = {
                        field: field,
                        value: groupValue,
                        items: []
                    };
                    result.push(group);
                }
                group.items.push(item);
            }
            return new Query(result);
        },

        _sortForGrouping: function(field, dir) {
            var idx, length,
                data = this.data;

            if (!stableSort) {
                for (idx = 0, length = data.length; idx < length; idx++) {
                    data[idx].__position = idx;
                }

                data = new Query(data).sort(field, dir, StableComparer).toArray();

                for (idx = 0, length = data.length; idx < length; idx++) {
                    delete data[idx].__position;
                }
                return data;
            }
            return this.sort(field, dir).toArray();
        },

        aggregate: function (aggregates) {
            var idx,
            len,
            result = {};

            if (aggregates && aggregates.length) {
                for(idx = 0, len = this.data.length; idx < len; idx++) {
                    calculateAggregate(result, aggregates, this.data[idx], idx, len);
                }
            }
            return result;
        }
    };

    function groupValueComparer(a, b) {
        if (a && a.getTime && b && b.getTime) {
            return a.getTime() === b.getTime();
        }
        return a === b;
    }

    function calculateAggregate(accumulator, aggregates, item, index, length) {
        aggregates = aggregates || [];
        var idx,
        aggr,
        functionName,
        len = aggregates.length;

        for (idx = 0; idx < len; idx++) {
            aggr = aggregates[idx];
            functionName = aggr.aggregate;
            var field = aggr.field;
            accumulator[field] = accumulator[field] || {};
            accumulator[field][functionName] = functions[functionName.toLowerCase()](accumulator[field][functionName], item, kendo.accessor(field), index, length);
        }
    }

    var functions = {
        sum: function(accumulator, item, accessor) {
            var value = accessor.get(item);

            if (!isNumber(accumulator)) {
                accumulator = value;
            } else if (isNumber(value)) {
                accumulator += value;
            }

            return accumulator;
        },
        count: function(accumulator) {
            return (accumulator || 0) + 1;
        },
        average: function(accumulator, item, accessor, index, length) {
            var value = accessor.get(item);

            if (!isNumber(accumulator)) {
                accumulator = value;
            } else if (isNumber(value)) {
                accumulator += value;
            }

            if(index == length - 1 && isNumber(accumulator)) {
                accumulator = accumulator / length;
            }
            return accumulator;
        },
        max: function(accumulator, item, accessor) {
            var value = accessor.get(item);

            if (!isNumber(accumulator)) {
                accumulator = value;
            }

            if(accumulator < value && isNumber(value)) {
                accumulator = value;
            }
            return accumulator;
        },
        min: function(accumulator, item, accessor) {
            var value = accessor.get(item);

            if (!isNumber(accumulator)) {
                accumulator = value;
            }

            if(accumulator > value && isNumber(value)) {
                accumulator = value;
            }
            return accumulator;
        }
    };

    function isNumber(val) {
        return typeof val === "number" && !isNaN(val);
    }

    function toJSON(array) {
        var idx, length = array.length, result = new Array(length);

        for (idx = 0; idx < length; idx++) {
            result[idx] = array[idx].toJSON();
        }

        return result;
    }

    Query.process = function(data, options) {
        options = options || {};

        var query = new Query(data),
            group = options.group,
            sort = normalizeGroup(group || []).concat(normalizeSort(options.sort || [])),
            total,
            filter = options.filter,
            skip = options.skip,
            take = options.take;

        if (filter) {
            query = query.filter(filter);
            total = query.toArray().length;
        }

        if (sort) {
            query = query.sort(sort);

            if (group) {
                data = query.toArray();
            }
        }

        if (skip !== undefined && take !== undefined) {
            query = query.range(skip, take);
        }

        if (group) {
            query = query.group(group, data);
        }

        return {
            total: total,
            data: query.toArray()
        };
    };

    function calculateAggregates(data, options) {
        options = options || {};

        var query = new Query(data),
            aggregates = options.aggregate,
            filter = options.filter;

        if(filter) {
            query = query.filter(filter);
        }

        return query.aggregate(aggregates);
    }

    var LocalTransport = Class.extend({
        init: function(options) {
            this.data = options.data;
        },

        read: function(options) {
            options.success(this.data);
        },
        update: function(options) {
            options.success(options.data);
        },
        create: function(options) {
            options.success(options.data);
        },
        destroy: function(options) {
            options.success(options.data);
        }
    });

    var RemoteTransport = Class.extend( {
        init: function(options) {
            var that = this, parameterMap;

            options = that.options = extend({}, that.options, options);

            each(crud, function(index, type) {
                if (typeof options[type] === STRING) {
                    options[type] = {
                        url: options[type]
                    };
                }
            });

            that.cache = options.cache? Cache.create(options.cache) : {
                find: noop,
                add: noop
            };

            parameterMap = options.parameterMap;

            if (isFunction(options.push)) {
                that.push = options.push;
            }

            if (!that.push) {
                that.push = identity;
            }

            that.parameterMap = isFunction(parameterMap) ? parameterMap : function(options) {
                var result = {};

                each(options, function(option, value) {
                    if (option in parameterMap) {
                        option = parameterMap[option];
                        if (isPlainObject(option)) {
                            value = option.value(value);
                            option = option.key;
                        }
                    }

                    result[option] = value;
                });

                return result;
            };
        },

        options: {
            parameterMap: identity
        },

        create: function(options) {
            return ajax(this.setup(options, CREATE));
        },

        read: function(options) {
            var that = this,
                success,
                error,
                result,
                cache = that.cache;

            options = that.setup(options, READ);

            success = options.success || noop;
            error = options.error || noop;

            result = cache.find(options.data);

            if(result !== undefined) {
                success(result);
            } else {
                options.success = function(result) {
                    cache.add(options.data, result);

                    success(result);
                };

                $.ajax(options);
            }
        },

        update: function(options) {
            return ajax(this.setup(options, UPDATE));
        },

        destroy: function(options) {
            return ajax(this.setup(options, DESTROY));
        },

        setup: function(options, type) {
            options = options || {};

            var that = this,
                parameters,
                operation = that.options[type],
                data = isFunction(operation.data) ? operation.data(options.data) : operation.data;

            options = extend(true, {}, operation, options);
            parameters = extend(true, {}, data, options.data);

            options.data = that.parameterMap(parameters, type);

            if (isFunction(options.url)) {
                options.url = options.url(parameters);
            }

            return options;
        }
    });

    var Cache = Class.extend({
        init: function() {
            this._store = {};
        },
        add: function(key, data) {
            if(key !== undefined) {
                this._store[stringify(key)] = data;
            }
        },
        find: function(key) {
            return this._store[stringify(key)];
        },
        clear: function() {
            this._store = {};
        },
        remove: function(key) {
            delete this._store[stringify(key)];
        }
    });

    Cache.create = function(options) {
        var store = {
            "inmemory": function() { return new Cache(); }
        };

        if (isPlainObject(options) && isFunction(options.find)) {
            return options;
        }

        if (options === true) {
            return new Cache();
        }

        return store[options]();
    };

    function serializeRecords(data, getters, modelInstance, originalFieldNames, fieldNames) {
        var record,
            getter,
            originalName,
            idx,
            length;

        for (idx = 0, length = data.length; idx < length; idx++) {
            record = data[idx];
            for (getter in getters) {
                originalName = fieldNames[getter];

                if (originalName && originalName !== getter) {
                    record[originalName] = getters[getter](record);
                    delete record[getter];
                }
            }
        }
    }

    function convertRecords(data, getters, modelInstance, originalFieldNames, fieldNames) {
        var record,
            getter,
            originalName,
            idx,
            length;

        for (idx = 0, length = data.length; idx < length; idx++) {
            record = data[idx];
            for (getter in getters) {
                record[getter] = modelInstance._parse(getter, getters[getter](record));

                originalName = fieldNames[getter];
                if (originalName && originalName !== getter) {
                    delete record[originalName];
                }
            }
        }
    }

    function convertGroup(data, getters, modelInstance, originalFieldNames, fieldNames) {
        var record,
            idx,
            fieldName,
            length;

        for (idx = 0, length = data.length; idx < length; idx++) {
            record = data[idx];

            fieldName = originalFieldNames[record.field];
            if (fieldName && fieldName != record.field) {
                record.field = fieldName;
            }

            record.value = modelInstance._parse(record.field, record.value);

            if (record.hasSubgroups) {
                convertGroup(record.items, getters, modelInstance, originalFieldNames, fieldNames);
            } else {
                convertRecords(record.items, getters, modelInstance, originalFieldNames, fieldNames);
            }
        }
    }

    function wrapDataAccess(originalFunction, model, converter, getters, originalFieldNames, fieldNames) {
        return function(data) {
            data = originalFunction(data);

            if (data && !isEmptyObject(getters)) {
                if (toString.call(data) !== "[object Array]" && !(data instanceof ObservableArray)) {
                    data = [data];
                }

                converter(data, getters, new model(), originalFieldNames, fieldNames);
            }

            return data || [];
        };
    }

    var DataReader = Class.extend({
        init: function(schema) {
            var that = this, member, get, model, base;

            schema = schema || {};

            for (member in schema) {
                get = schema[member];

                that[member] = typeof get === STRING ? getter(get) : get;
            }

            base = schema.modelBase || Model;

            if (isPlainObject(that.model)) {
                that.model = model = base.define(that.model);
            }

            if (that.model) {
                var dataFunction = proxy(that.data, that),
                    groupsFunction = proxy(that.groups, that),
                    serializeFunction = proxy(that.serialize, that),
                    originalFieldNames = {},
                    getters = {},
                    serializeGetters = {},
                    fieldNames = {},
                    shouldSerialize = false,
                    fieldName;

                model = that.model;

                if (model.fields) {
                    each(model.fields, function(field, value) {
                        var fromName;

                        fieldName = field;

                        if (isPlainObject(value) && value.field) {
                            fieldName = value.field;
                        } else if (typeof value === STRING) {
                            fieldName = value;
                        }

                        if (isPlainObject(value) && value.from) {
                            fromName = value.from;
                        }

                        shouldSerialize = shouldSerialize || (fromName && fromName !== field) || fieldName !== field;

                        getters[field] = getter(fromName || fieldName);
                        serializeGetters[field] = getter(field);
                        originalFieldNames[fromName || fieldName] = field;
                        fieldNames[field] = fromName || fieldName;
                    });

                    if (!schema.serialize && shouldSerialize) {
                        that.serialize = wrapDataAccess(serializeFunction, model, serializeRecords, serializeGetters, originalFieldNames, fieldNames);
                    }
                }

                that.data = wrapDataAccess(dataFunction, model, convertRecords, getters, originalFieldNames, fieldNames);
                that.groups = wrapDataAccess(groupsFunction, model, convertGroup, getters, originalFieldNames, fieldNames);
            }
        },
        errors: function(data) {
            return data ? data.errors : null;
        },
        parse: identity,
        data: identity,
        total: function(data) {
            return data.length;
        },
        groups: identity,
        aggregates: function() {
            return {};
        },
        serialize: function(data) {
            return data;
        }
    });

    function mergeGroups(target, dest, skip, take) {
        var group,
            idx = 0,
            items;

        while (dest.length && take) {
            group = dest[idx];
            items = group.items;

            var length = items.length;

            if (target && target.field === group.field && target.value === group.value) {
                if (target.hasSubgroups && target.items.length) {
                    mergeGroups(target.items[target.items.length - 1], group.items, skip, take);
                } else {
                    items = items.slice(skip, skip + take);
                    target.items = target.items.concat(items);
                }
                dest.splice(idx--, 1);
            } else if (group.hasSubgroups && items.length) {
                mergeGroups(group, items, skip, take);
            } else {
                items = items.slice(skip, skip + take);
                group.items = items;

                if (!group.items.length) {
                    dest.splice(idx--, 1);
                }
            }

            if (items.length === 0) {
                skip -= length;
            } else {
                skip = 0;
                take -= items.length;
            }

            if (++idx >= dest.length) {
                break;
            }
        }

        if (idx < dest.length) {
            dest.splice(idx, dest.length - idx);
        }
    }

    function flattenGroups(data) {
        var idx, length, result = [];

        for (idx = 0, length = data.length; idx < length; idx++) {
            if (data[idx].hasSubgroups) {
                result = result.concat(flattenGroups(data[idx].items));
            } else {
                result = result.concat(data[idx].items.slice());
            }
        }
        return result;
    }

    function wrapGroupItems(data, model) {
        var idx, length, group, items;
        if (model) {
            for (idx = 0, length = data.length; idx < length; idx++) {
                group = data[idx];
                items = group.items;

                if (group.hasSubgroups) {
                    wrapGroupItems(items, model);
                } else if (items.length && !(items[0] instanceof model)) {
                    items.type = model;
                    items.wrapAll(items, items);
                }
            }
        }
    }

    function eachGroupItems(data, func) {
        var idx, length;

        for (idx = 0, length = data.length; idx < length; idx++) {
            if (data[idx].hasSubgroups) {
                if (eachGroupItems(data[idx].items, func)) {
                    return true;
                }
            } else if (func(data[idx].items, data[idx])) {
                return true;
            }
        }
    }

    function removeModel(data, model) {
        var idx, length;

        for (idx = 0, length = data.length; idx < length; idx++) {
            if (data[idx].uid == model.uid) {
                model = data[idx];
                data.splice(idx, 1);
                return model;
            }
        }
    }

    function wrapInEmptyGroup(groups, model) {
        var parent,
            group,
            idx,
            length;

        for (idx = groups.length-1, length = 0; idx >= length; idx--) {
            group = groups[idx];
            parent = {
                value: model.get(group.field),
                field: group.field,
                items: parent ? [parent] : [model],
                hasSubgroups: !!parent,
                aggregates: {}
            };
        }

        return parent;
    }

    function indexOfPristineModel(data, model) {
        if (model) {
            return indexOf(data, function(item) {
                return item[model.idField] === model.id;
            });
        }
        return -1;
    }

    function indexOfModel(data, model) {
        if (model) {
            return indexOf(data, function(item) {
                return item.uid == model.uid;
            });
        }
        return -1;
    }

    function indexOf(data, comparer) {
        var idx, length;

        for (idx = 0, length = data.length; idx < length; idx++) {
            if (comparer(data[idx])) {
                return idx;
            }
        }

        return -1;
    }

    function fieldNameFromModel(fields, name) {
        if (fields && !isEmptyObject(fields)) {
            var descriptor = fields[name];
            var fieldName;
            if (isPlainObject(descriptor)) {
                fieldName = descriptor.from || descriptor.field || name;
            } else {
                fieldName = fields[name] || name;
            }

            if (isFunction(fieldName)) {
                return name;
            }

            return fieldName;
        }
        return name;
    }

    function convertFilterDescriptorsField(descriptor, model) {
        var idx,
            length,
            target = {};

        for (var field in descriptor) {
            if (field !== "filters") {
                target[field] = descriptor[field];
            }
        }

        if (descriptor.filters) {
            target.filters = [];
            for (idx = 0, length = descriptor.filters.length; idx < length; idx++) {
                target.filters[idx] = convertFilterDescriptorsField(descriptor.filters[idx], model);
            }
        } else {
            target.field = fieldNameFromModel(model.fields, target.field);
        }
        return target;
    }

    function convertDescriptorsField(descriptors, model) {
        var idx,
            length,
            result = [],
            target,
            descriptor;

        for (idx = 0, length = descriptors.length; idx < length; idx ++) {
            target = {};

            descriptor = descriptors[idx];

            for (var field in descriptor) {
                target[field] = descriptor[field];
            }

            target.field = fieldNameFromModel(model.fields, target.field);

            if (target.aggregates && isArray(target.aggregates)) {
                target.aggregates = convertDescriptorsField(target.aggregates, model);
            }
            result.push(target);
        }
        return result;
    }

    var DataSource = Observable.extend({
        init: function(options) {
            var that = this, model, data;

            if (options) {
                data = options.data;
            }

            options = that.options = extend({}, that.options, options);

            that._map = {};
            that._prefetch = {};
            that._data = [];
            that._pristineData = [];
            that._ranges = [];
            that._view = [];
            that._pristineTotal = 0;
            that._destroyed = [];
            that._pageSize = options.pageSize;
            that._page = options.page  || (options.pageSize ? 1 : undefined);
            that._sort = normalizeSort(options.sort);
            that._filter = normalizeFilter(options.filter);
            that._group = normalizeGroup(options.group);
            that._aggregate = options.aggregate;
            that._total = options.total;

            Observable.fn.init.call(that);

            that.transport = Transport.create(options, data);

            if (isFunction(that.transport.push)) {
                that.transport.push({
                    pushCreate: proxy(that._pushCreate, that),
                    pushUpdate: proxy(that._pushUpdate, that),
                    pushDestroy: proxy(that._pushDestroy, that)
                });
            }

            that.reader = new kendo.data.readers[options.schema.type || "json" ](options.schema);

            model = that.reader.model || {};

            that._data = that._observe(that._data);

            that.bind(["push", ERROR, CHANGE, REQUESTSTART, SYNC, REQUESTEND, PROGRESS], options);
        },

        options: {
            data: [],
            schema: {
               modelBase: Model
            },
            serverSorting: false,
            serverPaging: false,
            serverFiltering: false,
            serverGrouping: false,
            serverAggregates: false,
            batch: false
        },

        _isServerGrouped: function() {
            var group = this.group() || [];

            return this.options.serverGrouping && group.length;
        },

        _pushCreate: function(result) {
            this._push(result, "pushCreate");
        },

        _pushUpdate: function(result) {
            this._push(result, "pushUpdate");
        },

        _pushDestroy: function(result) {
            this._push(result, "pushDestroy");
        },

        _push: function(result, operation) {
            var data = this._readData(result);

            if (!data) {
                data = result;
            }

            this[operation](data);
        },

        _flatData: function(data) {
            if (this._isServerGrouped()) {
                return flattenGroups(data);
            }
            return data;
        },

        parent: noop,

        get: function(id) {
            var idx, length, data = this._flatData(this._data);

            for (idx = 0, length = data.length; idx < length; idx++) {
                if (data[idx].id == id) {
                    return data[idx];
                }
            }
        },

        getByUid: function(id) {
            var idx, length, data = this._flatData(this._data);

            if (!data) {
                return;
            }

            for (idx = 0, length = data.length; idx < length; idx++) {
                if (data[idx].uid == id) {
                    return data[idx];
                }
            }
        },

        indexOf: function(model) {
            return indexOfModel(this._data, model);
        },

        at: function(index) {
            return this._data[index];
        },

        data: function(value) {
            var that = this;
            if (value !== undefined) {
                that._data = this._observe(value);

                that._pristineData = value.slice(0);

                that._ranges = [];
                that._addRange(that._data);

                that._total = that._data.length;
                that._pristineTotal = that._total;

                that._process(that._data);
            } else {
                return that._data;
            }
        },

        view: function() {
            return this._view;
        },

        add: function(model) {
            return this.insert(this._data.length, model);
        },

        _createNewModel: function(model) {
            if (this.reader.model) {
                return  new this.reader.model(model);
            }

            return new ObservableObject(model);
        },

        insert: function(index, model) {
            if (!model) {
                model = index;
                index = 0;
            }

            if (!(model instanceof Model)) {
                model = this._createNewModel(model);
            }

            if (this._isServerGrouped()) {
                this._data.splice(index, 0, wrapInEmptyGroup(this.group(), model));
            } else {
                this._data.splice(index, 0, model);
            }

            return model;
        },

        pushCreate: function(items) {
            if (!isArray(items)) {
                items = [items];
            }

            var pushed = [];

            for (var idx = 0; idx < items.length; idx ++) {
                var item = items[idx];

                var result = this.add(item);

                pushed.push(result);

                var pristine = result.toJSON();

                if (this._isServerGrouped()) {
                    pristine = wrapInEmptyGroup(this.group(), pristine);
                }

                this._pristineData.push(pristine);
            }

            if (pushed.length) {
                this.trigger("push", {
                    type: "create",
                    items: pushed
                });
            }
        },

        pushUpdate: function(items) {
            if (!isArray(items)) {
                items = [items];
            }

            var pushed = [];

            for (var idx = 0; idx < items.length; idx ++) {
                var item = items[idx];
                var model = this._createNewModel(item);

                var target = this.get(model.id);

                if (target) {
                    pushed.push(target);

                    target.accept(item);

                    target.trigger("change");

                    this._updatePristineForModel(target, item);
                } else {
                    this.pushCreate(item);
                }
            }

            if (pushed.length) {
                this.trigger("push", {
                    type: "update",
                    items: pushed
                });
            }
        },

        pushDestroy: function(items) {
            if (!isArray(items)) {
                items = [items];
            }

            var pushed = [];
            var autoSync = this.options.autoSync;
            this.options.autoSync = false;
            try {
                for (var idx = 0; idx < items.length; idx ++) {
                    var item = items[idx];
                    var model = this._createNewModel(item);
                    var found = false;

                    this._eachItem(this._data, function(items){
                        for (var idx = 0; idx < items.length; idx++) {
                            if (items[idx].id === model.id) {
                                pushed.push(items[idx]);
                                items.splice(idx, 1);
                                found = true;
                                break;
                            }
                        }
                    });

                    if (found) {
                        this._removePristineForModel(model);
                        this._destroyed.pop();
                    }
                }
            } finally {
                this.options.autoSync = autoSync;
            }

            if (pushed.length) {
                this.trigger("push", {
                    type: "destroy",
                    items: pushed
                });
            }
        },

        remove: function(model) {
            var result,
                that = this,
                hasGroups = that._isServerGrouped();

            this._eachItem(that._data, function(items) {
                result = removeModel(items, model);
                if (result && hasGroups) {
                    if (!result.isNew || !result.isNew()) {
                        that._destroyed.push(result);
                    }
                    return true;
                }
            });

            this._removeModelFromRanges(model);

            this._updateRangesLength();

            return model;
        },

        sync: function() {
            var that = this,
                idx,
                length,
                created = [],
                updated = [],
                destroyed = that._destroyed,
                data = that._flatData(that._data);

            if (!that.reader.model) {
                return;
            }

            for (idx = 0, length = data.length; idx < length; idx++) {
                if (data[idx].isNew()) {
                    created.push(data[idx]);
                } else if (data[idx].dirty) {
                    updated.push(data[idx]);
                }
            }

            var promises = that._send("create", created);

            promises.push.apply(promises ,that._send("update", updated));
            promises.push.apply(promises ,that._send("destroy", destroyed));

            $.when.apply(null, promises)
                .then(function() {
                    var idx, length;

                    for (idx = 0, length = arguments.length; idx < length; idx++){
                        that._accept(arguments[idx]);
                    }

                    that._change({ action: "sync" });

                    that.trigger(SYNC);
                });
        },

        cancelChanges: function(model) {
            var that = this;

            if (model instanceof kendo.data.Model) {
                that._cancelModel(model);
            } else {
                that._destroyed = [];
                that._data = that._observe(that._pristineData);
                if (that.options.serverPaging) {
                    that._total = that._pristineTotal;
                }
                that._change();
            }
        },

        hasChanges: function() {
            var idx,
                length,
                data = this._data;

            if (this._destroyed.length) {
                return true;
            }

            for (idx = 0, length = data.length; idx < length; idx++) {
                if (data[idx].isNew() || data[idx].dirty) {
                    return true;
                }
            }

            return false;
        },

        _accept: function(result) {
            var that = this,
                models = result.models,
                response = result.response,
                idx = 0,
                serverGroup = that._isServerGrouped(),
                pristine = that._pristineData,
                type = result.type,
                length;

            that.trigger(REQUESTEND, { response: response, type: type });

            if (response && !isEmptyObject(response)) {
                response = that.reader.parse(response);

                if (that._handleCustomErrors(response)) {
                    return;
                }

                response = that.reader.data(response);

                if (!isArray(response)) {
                    response = [response];
                }
            } else {
                response = $.map(models, function(model) { return model.toJSON(); } );
            }

            if (type === "destroy") {
                that._destroyed = [];
            }

            for (idx = 0, length = models.length; idx < length; idx++) {
                if (type !== "destroy") {
                    models[idx].accept(response[idx]);

                    if (type === "create") {
                        pristine.push(serverGroup ? wrapInEmptyGroup(that.group(), models[idx]) : response[idx]);
                    } else if (type === "update") {
                        that._updatePristineForModel(models[idx], response[idx]);
                    }
                } else {
                    that._removePristineForModel(models[idx]);
                }
            }
        },

        _updatePristineForModel: function(model, values) {
            this._executeOnPristineForModel(model, function(index, items) {
                kendo.deepExtend(items[index], values);
            });
        },

        _executeOnPristineForModel: function(model, callback) {
            this._eachPristineItem(
                function(items) {
                    var index = indexOfPristineModel(items, model);
                    if (index > -1) {
                        callback(index, items);
                        return true;
                    }
                });
        },

        _removePristineForModel: function(model) {
            this._executeOnPristineForModel(model, function(index, items) {
                items.splice(index, 1);
            });
        },

        _readData: function(data) {
            var read = !this._isServerGrouped() ? this.reader.data : this.reader.groups;
            return read(data);
        },

        _eachPristineItem: function(callback) {
            this._eachItem(this._pristineData, callback);
        },

       _eachItem: function(data, callback) {
            if (data && data.length) {
                if (this._isServerGrouped()) {
                    eachGroupItems(data, callback);
                } else {
                    callback(data);
                }
            }
        },

        _pristineForModel: function(model) {
            var pristine,
                idx,
                callback = function(items) {
                    idx = indexOfPristineModel(items, model);
                    if (idx > -1) {
                        pristine = items[idx];
                        return true;
                    }
                };

            this._eachPristineItem(callback);

            return pristine;
        },

        _cancelModel: function(model) {
            var pristine = this._pristineForModel(model),
                idx;

           this._eachItem(this._data, function(items) {
                idx = indexOfModel(items, model);
                if (idx != -1) {
                    if (!model.isNew() && pristine) {
                        items[idx].accept(pristine);
                    } else {
                        items.splice(idx, 1);
                    }
                }
            });
        },

        _promise: function(data, models, type) {
            var that = this,
            transport = that.transport;

            return $.Deferred(function(deferred) {

                that.trigger(REQUESTSTART, { type: type });

                transport[type].call(transport, extend({
                    success: function(response) {
                        deferred.resolve({
                            response: response,
                            models: models,
                            type: type
                        });
                    },
                    error: function(response, status, error) {
                        deferred.reject(response);
                        that.error(response, status, error);
                    }
                }, data)
                );
            }).promise();
        },

        _send: function(method, data) {
            var that = this,
                idx,
                length,
                promises = [],
                converted = that.reader.serialize(toJSON(data));

            if (that.options.batch) {
                if (data.length) {
                    promises.push(that._promise( { data: { models: converted } }, data , method));
                }
            } else {
                for (idx = 0, length = data.length; idx < length; idx++) {
                    promises.push(that._promise( { data: converted[idx] }, [ data[idx] ], method));
                }
            }

            return promises;
        },

        read: function(data) {
            var that = this, params = that._params(data);

            that._queueRequest(params, function() {
                if (!that.trigger(REQUESTSTART, { type: "read" })) {
                    that.trigger(PROGRESS);

                    that._ranges = [];
                    that.transport.read({
                        data: params,
                        success: proxy(that.success, that),
                        error: proxy(that.error, that)
                    });
                } else {
                    that._dequeueRequest();
                }
            });
        },

        success: function(data) {
            var that = this,
                options = that.options;

            that.trigger(REQUESTEND, { response: data, type: "read" });

            data = that.reader.parse(data);

            if (that._handleCustomErrors(data)) {
                that._dequeueRequest();
                return;
            }

            that._total = that.reader.total(data);
            that._pristineTotal = that._total;

            if (that._aggregate && options.serverAggregates) {
                that._aggregateResult = that.reader.aggregates(data);
            }

            data = that._readData(data);

            that._pristineData = data.slice(0);

            that._data = that._observe(data);

            that._addRange(that._data);

            that._process(that._data);
            that._dequeueRequest();
        },

        _addRange: function(data) {
            var that = this,
                start = that._skip || 0,
                end = start + that._flatData(data).length;

            that._ranges.push({ start: start, end: end, data: data });
            that._ranges.sort( function(x, y) { return x.start - y.start; } );
        },

        error: function(xhr, status, errorThrown) {
            this._dequeueRequest();
            this.trigger(REQUESTEND, { });
            this.trigger(ERROR, { xhr: xhr, status: status, errorThrown: errorThrown });
        },

        _params: function(data) {
            var that = this,
                options =  extend({
                    take: that.take(),
                    skip: that.skip(),
                    page: that.page(),
                    pageSize: that.pageSize(),
                    sort: that._sort,
                    filter: that._filter,
                    group: that._group,
                    aggregate: that._aggregate
                }, data);

            if (!that.options.serverPaging) {
                delete options.take;
                delete options.skip;
                delete options.page;
                delete options.pageSize;
            }

            if (!that.options.serverGrouping) {
                delete options.group;
            } else if (that.reader.model && options.group) {
                options.group = convertDescriptorsField(options.group, that.reader.model);
            }

            if (!that.options.serverFiltering) {
                delete options.filter;
            } else if (that.reader.model && options.filter) {
               options.filter = convertFilterDescriptorsField(options.filter, that.reader.model);
            }

            if (!that.options.serverSorting) {
                delete options.sort;
            } else if (that.reader.model && options.sort) {
                options.sort = convertDescriptorsField(options.sort, that.reader.model);
            }

            if (!that.options.serverAggregates) {
                delete options.aggregate;
            } else if (that.reader.model && options.aggregate) {
                options.aggregate = convertDescriptorsField(options.aggregate, that.reader.model);
            }

            return options;
        },

        _queueRequest: function(options, callback) {
            var that = this;
            if (!that._requestInProgress) {
                that._requestInProgress = true;
                that._pending = undefined;
                callback();
            } else {
                that._pending = { callback: proxy(callback, that), options: options };
            }
        },

        _dequeueRequest: function() {
            var that = this;
            that._requestInProgress = false;
            if (that._pending) {
                that._queueRequest(that._pending.options, that._pending.callback);
            }
        },

        _handleCustomErrors: function(response) {
            if (this.reader.errors) {
                var errors = this.reader.errors(response);
                if (errors) {
                    this.trigger(ERROR, { xhr: null, status: "customerror", errorThrown: "custom error", errors: errors });
                    return true;
                }
            }
            return false;
        },
        _observe: function(data) {
            var that = this,
                model = that.reader.model,
                wrap = false;

            if (model && data.length) {
                wrap = !(data[0] instanceof model);
            }

            if (data instanceof ObservableArray) {
                if (wrap) {
                    data.type = that.reader.model;
                    data.wrapAll(data, data);
                }
            } else {
                data = new ObservableArray(data, that.reader.model);
                data.parent = function() { return that.parent(); };
            }

            if (that._isServerGrouped()) {
                wrapGroupItems(data, model);
            }

            if (that._changeHandler && that._data && that._data instanceof ObservableArray) {
                that._data.unbind(CHANGE, that._changeHandler);
            } else {
                that._changeHandler = proxy(that._change, that);
            }

            return data.bind(CHANGE, that._changeHandler);
        },

        _change: function(e) {
            var that = this, idx, length, action = e ? e.action : "";

            if (action === "remove") {
                for (idx = 0, length = e.items.length; idx < length; idx++) {
                    if (!e.items[idx].isNew || !e.items[idx].isNew()) {
                        that._destroyed.push(e.items[idx]);
                    }
                }
            }

            if (that.options.autoSync && (action === "add" || action === "remove" || action === "itemchange")) {
                that.sync();
            } else {
                var total = parseInt(that._total || that._pristineTotal, 10);
                if (action === "add") {
                    total += e.items.length;
                } else if (action === "remove") {
                    total -= e.items.length;
                } else if (action !== "itemchange" && action !== "sync" && !that.options.serverPaging) {
                    total = that._pristineTotal;
                } else if (action === "sync") {
                    total = that._pristineTotal = parseInt(that._total, 10);
                }

                that._total = total;

                that._process(that._data, e);
            }
        },

        _process: function (data, e) {
            var that = this,
                options = {},
                result;

            if (that.options.serverPaging !== true) {
                options.skip = that._skip;
                options.take = that._take || that._pageSize;

                if(options.skip === undefined && that._page !== undefined && that._pageSize !== undefined) {
                    options.skip = (that._page - 1) * that._pageSize;
                }
            }

            if (that.options.serverSorting !== true) {
                options.sort = that._sort;
            }

            if (that.options.serverFiltering !== true) {
                options.filter = that._filter;
            }

            if (that.options.serverGrouping !== true) {
                options.group = that._group;
            }

            if (that.options.serverAggregates !== true) {
                options.aggregate = that._aggregate;
                that._aggregateResult = calculateAggregates(data, options);
            }

            result = Query.process(data, options);

            that._view = result.data;

            if (result.total !== undefined && !that.options.serverFiltering) {
                that._total = result.total;
            }

            e = e || {};

            e.items = e.items || that._view;

            that.trigger(CHANGE, e);
        },

        _mergeState: function(options) {
            var that = this;

            if (options !== undefined) {
                that._pageSize = options.pageSize;
                that._page = options.page;
                that._sort = options.sort;
                that._filter = options.filter;
                that._group = options.group;
                that._aggregate = options.aggregate;
                that._skip = options.skip;
                that._take = options.take;

                if(that._skip === undefined) {
                    that._skip = that.skip();
                    options.skip = that.skip();
                }

                if(that._take === undefined && that._pageSize !== undefined) {
                    that._take = that._pageSize;
                    options.take = that._take;
                }

                if (options.sort) {
                    that._sort = options.sort = normalizeSort(options.sort);
                }

                if (options.filter) {
                    that._filter = options.filter = normalizeFilter(options.filter);
                }

                if (options.group) {
                    that._group = options.group = normalizeGroup(options.group);
                }
                if (options.aggregate) {
                    that._aggregate = options.aggregate = normalizeAggregate(options.aggregate);
                }
            }
            return options;
        },

        query: function(options) {
            var that = this,
                result,
                remote = that.options.serverSorting || that.options.serverPaging || that.options.serverFiltering || that.options.serverGrouping || that.options.serverAggregates;

            if (remote || ((that._data === undefined || that._data.length === 0) && !that._destroyed.length)) {
                that.read(that._mergeState(options));
            } else {
                if (!that.trigger(REQUESTSTART, { type: "read" })) {
                    that.trigger(PROGRESS);

                    result = Query.process(that._data, that._mergeState(options));

                    if (!that.options.serverFiltering) {
                        if (result.total !== undefined) {
                            that._total = result.total;
                        } else {
                            that._total = that._data.length;
                        }
                    }

                    that._view = result.data;
                    that._aggregateResult = calculateAggregates(that._data, options);
                    that.trigger(REQUESTEND, { });
                    that.trigger(CHANGE, { items: result.data });
                }
            }
        },

        fetch: function(callback) {
            var that = this;

            return $.Deferred(function(deferred) {
                var success = function(e) {
                    that.unbind(ERROR, error);

                    deferred.resolve();

                    if (callback) {
                        callback.call(that, e);
                    }
                };

                var error = function(e) {
                    deferred.reject(e);
                };

                that.one(CHANGE, success);
                that.one(ERROR, error);
                that._query();
            }).promise();
        },

        _query: function(options) {
            var that = this;

            that.query(extend({}, {
                page: that.page(),
                pageSize: that.pageSize(),
                sort: that.sort(),
                filter: that.filter(),
                group: that.group(),
                aggregate: that.aggregate()
            }, options));
        },

        next: function(options) {
            var that = this,
                page = that.page(),
                total = that.total();

            options = options || {};

            if (!page || (total && page + 1 > that.totalPages())) {
                return;
            }

            that._skip = page * that.take();

            page += 1;
            options.page = page;

            that._query(options);

            return page;
        },

        prev: function(options) {
            var that = this,
                page = that.page();

            options = options || {};

            if (!page || page === 1) {
                return;
            }

            that._skip = that._skip - that.take();

            page -= 1;
            options.page = page;

            that._query(options);

            return page;
        },

        page: function(val) {
            var that = this,
            skip;

            if(val !== undefined) {
                val = math.max(math.min(math.max(val, 1), that.totalPages()), 1);
                that._query({ page: val });
                return;
            }
            skip = that.skip();

            return skip !== undefined ? math.round((skip || 0) / (that.take() || 1)) + 1 : undefined;
        },

        pageSize: function(val) {
            var that = this;

            if(val !== undefined) {
                that._query({ pageSize: val, page: 1 });
                return;
            }

            return that.take();
        },

        sort: function(val) {
            var that = this;

            if(val !== undefined) {
                that._query({ sort: val });
                return;
            }

            return that._sort;
        },

        filter: function(val) {
            var that = this;

            if (val === undefined) {
                return that._filter;
            }

            that._query({ filter: val, page: 1 });
        },

        group: function(val) {
            var that = this;

            if(val !== undefined) {
                that._query({ group: val });
                return;
            }

            return that._group;
        },

        total: function() {
            return parseInt(this._total || 0, 10);
        },

        aggregate: function(val) {
            var that = this;

            if(val !== undefined) {
                that._query({ aggregate: val });
                return;
            }

            return that._aggregate;
        },

        aggregates: function() {
            return this._aggregateResult;
        },

        totalPages: function() {
            var that = this,
            pageSize = that.pageSize() || that.total();

            return math.ceil((that.total() || 0) / pageSize);
        },

        inRange: function(skip, take) {
            var that = this,
            end = math.min(skip + take, that.total());

            if (!that.options.serverPaging && that.data.length > 0) {
                return true;
            }

            return that._findRange(skip, end).length > 0;
        },

        lastRange: function() {
            var ranges = this._ranges;
            return ranges[ranges.length - 1] || { start: 0, end: 0, data: [] };
        },

        firstItemUid: function() {
            var ranges = this._ranges;
            return ranges.length && ranges[0].data.length && ranges[0].data[0].uid;
        },

        range: function(skip, take) {
            skip = math.min(skip || 0, this.total());

            var that = this,
                pageSkip = math.max(math.floor(skip / take), 0) * take,
                size = math.min(pageSkip + take, that.total()),
                data;

            data = that._findRange(skip, math.min(skip + take, that.total()));

            if (data.length) {
                that._skip = skip > that.skip() ? math.min(size, (that.totalPages() - 1) * that.take()) : pageSkip;

                that._take = take;

                var paging = that.options.serverPaging;
                var sorting = that.options.serverSorting;
                var filtering = that.options.serverFiltering;
                try {
                    that.options.serverPaging = true;
                    if (!that._isServerGrouped() && !(that.group() && that.group().length)) {
                        that.options.serverSorting = true;
                    }
                    that.options.serverFiltering = true;
                    if (paging) {
                        that._data = data = that._observe(data);
                    }
                    that._process(data);
                } finally {
                    that.options.serverPaging = paging;
                    that.options.serverSorting = sorting;
                    that.options.serverFiltering = filtering;
                }

                return;
            }

            if (take !== undefined) {
                if (!that._rangeExists(pageSkip, size)) {
                    that.prefetch(pageSkip, take, function() {
                        if (skip > pageSkip && size < that.total() && !that._rangeExists(size, math.min(size + take, that.total()))) {
                            that.prefetch(size, take, function() {
                                that.range(skip, take);
                            });
                        } else {
                            that.range(skip, take);
                        }
                    });
                } else if (pageSkip < skip) {
                    that.prefetch(size, take, function() {
                        that.range(skip, take);
                    });
                }
            }
        },

        _findRange: function(start, end) {
            var that = this,
                ranges = that._ranges,
                range,
                data = [],
                skipIdx,
                takeIdx,
                startIndex,
                endIndex,
                rangeData,
                rangeEnd,
                processed,
                options = that.options,
                remote = options.serverSorting || options.serverPaging || options.serverFiltering || options.serverGrouping || options.serverAggregates,
                flatData,
                count,
                length;

            for (skipIdx = 0, length = ranges.length; skipIdx < length; skipIdx++) {
                range = ranges[skipIdx];
                if (start >= range.start && start <= range.end) {
                    count = 0;

                    for (takeIdx = skipIdx; takeIdx < length; takeIdx++) {
                        range = ranges[takeIdx];
                        flatData = that._flatData(range.data);

                        if (flatData.length && start + count >= range.start) {
                            rangeData = range.data;
                            rangeEnd = range.end;

                            if (!remote) {
                                var sort = normalizeGroup(that.group() || []).concat(normalizeSort(that.sort() || []));
                                processed = Query.process(range.data, { sort: sort, filter: that.filter() });
                                flatData = rangeData = processed.data;

                                if (processed.total !== undefined) {
                                    rangeEnd = processed.total;
                                }
                            }

                            startIndex = 0;
                            if (start + count > range.start) {
                                startIndex = (start + count) - range.start;
                            }
                            endIndex = flatData.length;
                            if (rangeEnd > end) {
                                endIndex = endIndex - (rangeEnd - end);
                            }
                            count += endIndex - startIndex;
                            data = that._mergeGroups(data, rangeData, startIndex, endIndex);

                            if (end <= range.end && count == end - start) {
                                return data;
                            }
                        }
                    }
                    break;
                }
            }
            return [];
        },

        _mergeGroups: function(data, range, skip, take) {
            if (this._isServerGrouped()) {
                var temp = range.toJSON(),
                    prevGroup;

                if (data.length) {
                    prevGroup = data[data.length - 1];
                }

                mergeGroups(prevGroup, temp, skip, take);

                return data.concat(temp);
            }
            return data.concat(range.slice(skip, take));
        },

        skip: function() {
            var that = this;

            if (that._skip === undefined) {
                return (that._page !== undefined ? (that._page  - 1) * (that.take() || 1) : undefined);
            }
            return that._skip;
        },

        take: function() {
            return this._take || this._pageSize;
        },

        _prefetchSuccessHandler: function (skip, size, callback) {
            var that = this;

            return function(data) {
                var found = false,
                    range = { start: skip, end: size, data: [] },
                    idx,
                    length,
                    temp;

                that._dequeueRequest();

                that.trigger(REQUESTEND, { response: data, type: "read" });

                data = that.reader.parse(data);

                temp = that._readData(data);

                if (temp.length) {
                    for (idx = 0, length = that._ranges.length; idx < length; idx++) {
                        if (that._ranges[idx].start === skip) {
                            found = true;
                            range = that._ranges[idx];
                            break;
                        }
                    }
                    if (!found) {
                        that._ranges.push(range);
                    }
                }

                range.data = that._observe(temp);
                range.end = range.start + that._flatData(range.data).length;
                that._ranges.sort( function(x, y) { return x.start - y.start; } );
                that._total = that.reader.total(data);

                if (callback && temp.length) {
                    callback();
                } else {
                    that.trigger(CHANGE, {});
                }
            };
        },

        prefetch: function(skip, take, callback) {
            var that = this,
                size = math.min(skip + take, that.total()),
                options = {
                    take: take,
                    skip: skip,
                    page: skip / take + 1,
                    pageSize: take,
                    sort: that._sort,
                    filter: that._filter,
                    group: that._group,
                    aggregate: that._aggregate
                };

            if (!that._rangeExists(skip, size)) {
                clearTimeout(that._timeout);

                that._timeout = setTimeout(function() {
                    that._queueRequest(options, function() {
                        if (!that.trigger(REQUESTSTART, { type: "read" })) {
                            that.transport.read({
                                data: that._params(options),
                                success: that._prefetchSuccessHandler(skip, size, callback)
                            });
                        } else {
                            that._dequeueRequest();
                        }
                    });
                }, 100);
            } else if (callback) {
                callback();
            }
        },

        _rangeExists: function(start, end) {
            var that = this,
                ranges = that._ranges,
                idx,
                length;

            for (idx = 0, length = ranges.length; idx < length; idx++) {
                if (ranges[idx].start <= start && ranges[idx].end >= end) {
                    return true;
                }
            }
            return false;
        },

        _removeModelFromRanges: function(model) {
            var result,
                found,
                range;

            for (var idx = 0, length = this._ranges.length; idx < length; idx++) {
                range = this._ranges[idx];

                this._eachItem(range.data, function(items) {
                    result = removeModel(items, model);
                    if (result) {
                        found = true;
                    }
                });

                if (found) {
                    break;
                }
            }
        },

        _updateRangesLength: function() {
            var startOffset = 0,
                range,
                rangeLength;

            for (var idx = 0, length = this._ranges.length; idx < length; idx++) {
                range = this._ranges[idx];
                range.start = range.start - startOffset;

                rangeLength = this._flatData(range.data).length;
                startOffset = range.end - rangeLength;
                range.end = range.start + rangeLength;
            }
        }
    });

    var Transport = {};

    Transport.create = function(options, data) {
        var transport,
            transportOptions = options.transport;

        if (transportOptions) {
            transportOptions.read = typeof transportOptions.read === STRING ? { url: transportOptions.read } : transportOptions.read;

            if (options.type) {
                if (kendo.data.transports[options.type] && !isPlainObject(kendo.data.transports[options.type])) {
                    transport = new kendo.data.transports[options.type](extend(transportOptions, { data: data }));
                } else {
                    transportOptions = extend(true, {}, kendo.data.transports[options.type], transportOptions);
                }

                options.schema = extend(true, {}, kendo.data.schemas[options.type], options.schema);
            }

            if (!transport) {
                transport = isFunction(transportOptions.read) ? transportOptions : new RemoteTransport(transportOptions);
            }
        } else {
            transport = new LocalTransport({ data: options.data });
        }
        return transport;
    };

    DataSource.create = function(options) {
        if (isArray(options) || options instanceof ObservableArray) {
           options = { data: options };
        }

        var dataSource = options || {},
            data = dataSource.data,
            fields = dataSource.fields,
            table = dataSource.table,
            select = dataSource.select,
            idx,
            length,
            model = {},
            field;

        if (!data && fields && !dataSource.transport) {
            if (table) {
                data = inferTable(table, fields);
            } else if (select) {
                data = inferSelect(select, fields);
            }
        }

        if (kendo.data.Model && fields && (!dataSource.schema || !dataSource.schema.model)) {
            for (idx = 0, length = fields.length; idx < length; idx++) {
                field = fields[idx];
                if (field.type) {
                    model[field.field] = field;
                }
            }

            if (!isEmptyObject(model)) {
                dataSource.schema = extend(true, dataSource.schema, { model:  { fields: model } });
            }
        }

        dataSource.data = data;
        table = null;
        dataSource.table = null;

        return dataSource instanceof DataSource ? dataSource : new DataSource(dataSource);
    };

    function inferSelect(select, fields) {
        var options = $(select)[0].children,
            idx,
            length,
            data = [],
            record,
            firstField = fields[0],
            secondField = fields[1],
            value,
            option;

        for (idx = 0, length = options.length; idx < length; idx++) {
            record = {};
            option = options[idx];

            if (option.disabled) {
                continue;
            }

            record[firstField.field] = option.text;

            value = option.attributes.value;

            if (value && value.specified) {
                value = option.value;
            } else {
                value = option.text;
            }

            record[secondField.field] = value;

            data.push(record);
        }

        return data;
    }

    function inferTable(table, fields) {
        var tbody = $(table)[0].tBodies[0],
        rows = tbody ? tbody.rows : [],
        idx,
        length,
        fieldIndex,
        fieldCount = fields.length,
        data = [],
        cells,
        record,
        cell,
        empty;

        for (idx = 0, length = rows.length; idx < length; idx++) {
            record = {};
            empty = true;
            cells = rows[idx].cells;

            for (fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
                cell = cells[fieldIndex];
                if(cell.nodeName.toLowerCase() !== "th") {
                    empty = false;
                    record[fields[fieldIndex].field] = cell.innerHTML;
                }
            }
            if(!empty) {
                data.push(record);
            }
        }

        return data;
    }

    var Node = Model.define({
        init: function(value) {
            var that = this,
                hasChildren = that.hasChildren || value && value.hasChildren,
                childrenField = "items",
                childrenOptions = {};

            kendo.data.Model.fn.init.call(that, value);

            if (typeof that.children === STRING) {
                childrenField = that.children;
            }

            childrenOptions = {
                schema: {
                    data: childrenField,
                    model: {
                        hasChildren: hasChildren,
                        id: that.idField
                    }
                }
            };

            if (typeof that.children !== STRING) {
                extend(childrenOptions, that.children);
            }

            childrenOptions.data = value;

            if (!hasChildren) {
                hasChildren = childrenOptions.schema.data;
            }

            if (typeof hasChildren === STRING) {
                hasChildren = kendo.getter(hasChildren);
            }

            if (isFunction(hasChildren)) {
                that.hasChildren = !!hasChildren.call(that, that);
            }

            that._childrenOptions = childrenOptions;

            if (that.hasChildren) {
                that._initChildren();
            }

            that._loaded = !!(value && (value[childrenField] || value._loaded));
        },

        _initChildren: function() {
            var that = this;
            var children, transport, parameterMap;

            if (!(that.children instanceof HierarchicalDataSource)) {
                children = that.children = new HierarchicalDataSource(that._childrenOptions);

                transport = children.transport;
                parameterMap = transport.parameterMap;

                transport.parameterMap = function(data) {
                    data[that.idField || "id"] = that.id;

                    if (parameterMap) {
                        data = parameterMap(data);
                    }

                    return data;
                };

                children.parent = function(){
                    return that;
                };

                children.bind(CHANGE, function(e){
                    e.node = e.node || that;
                    that.trigger(CHANGE, e);
                });

                children.bind(ERROR, function(e){
                    var collection = that.parent();

                    if (collection) {
                        e.node = e.node || that;
                        collection.trigger(ERROR, e);
                    }
                });

                that._updateChildrenField();
            }
        },

        append: function(model) {
            this._initChildren();
            this.loaded(true);
            this.children.add(model);
        },

        hasChildren: false,

        level: function() {
            var parentNode = this.parentNode(),
                level = 0;

            while (parentNode && parentNode.parentNode) {
                level++;
                parentNode = parentNode.parentNode ? parentNode.parentNode() : null;
            }

            return level;
        },

        _updateChildrenField: function() {
            var fieldName = this._childrenOptions.schema.data;

            this[fieldName || "items"] = this.children.data();
        },

        _childrenLoaded: function() {
            this._loaded = true;

            this._updateChildrenField();
        },

        load: function() {
            var options = {};
            var method = "_query";
            var children;

            if (this.hasChildren) {
                this._initChildren();

                children = this.children;

                options[this.idField || "id"] = this.id;

                if (!this._loaded) {
                    children._data = undefined;
                    method = "read";
                }

                children.one(CHANGE, proxy(this._childrenLoaded, this));
                children[method](options);
            } else {
                this.loaded(true);
            }
        },

        parentNode: function() {
            var array = this.parent();

            return array.parent();
        },

        loaded: function(value) {
            if (value !== undefined) {
                this._loaded = value;
            } else {
                return this._loaded;
            }
        },

        shouldSerialize: function(field) {
            return Model.fn.shouldSerialize.call(this, field) &&
                    field !== "children" &&
                    field !== "_loaded" &&
                    field !== "hasChildren" &&
                    field !== "_childrenOptions";
        }
    });

    function dataMethod(name) {
        return function() {
            var data = this._data,
                result = DataSource.fn[name].apply(this, slice.call(arguments));

            if (this._data != data) {
                this._attachBubbleHandlers();
            }

            return result;
        };
    }

    var HierarchicalDataSource = DataSource.extend({
        init: function(options) {
            var node = Node.define({
                children: options
            });

            DataSource.fn.init.call(this, extend(true, {}, { schema: { modelBase: node, model: node } }, options));

            this._attachBubbleHandlers();
        },

        _attachBubbleHandlers: function() {
            var that = this;

            that._data.bind(ERROR, function(e) {
                that.trigger(ERROR, e);
            });
        },

        remove: function(node){
            var parentNode = node.parentNode(),
                dataSource = this,
                result;

            if (parentNode && parentNode._initChildren) {
                dataSource = parentNode.children;
            }

            result = DataSource.fn.remove.call(dataSource, node);

            if (parentNode && !dataSource.data().length) {
                parentNode.hasChildren = false;
            }

            return result;
        },

        success: dataMethod("success"),

        data: dataMethod("data"),

        insert: function(index, model) {
            var parentNode = this.parent();

            if (parentNode && parentNode._initChildren) {
                parentNode.hasChildren = true;
                parentNode._initChildren();
            }

            return DataSource.fn.insert.call(this, index, model);
        },

        _find: function(method, value) {
            var idx, length, node, data, children;

            node = DataSource.fn[method].call(this, value);

            if (node) {
                return node;
            }

            data = this._flatData(this.data());

            if (!data) {
                return;
            }

            for (idx = 0, length = data.length; idx < length; idx++) {
                children = data[idx].children;

                if (!(children instanceof HierarchicalDataSource)) {
                    continue;
                }

                node = children[method](value);

                if (node) {
                    return node;
                }
            }
        },

        get: function(id) {
            return this._find("get", id);
        },

        getByUid: function(uid) {
            return this._find("getByUid", uid);
        }
    });

    function inferList(list, fields) {
        var items = $(list).children(),
            idx,
            length,
            data = [],
            record,
            textField = fields[0].field,
            urlField = fields[1] && fields[1].field,
            spriteCssClassField = fields[2] && fields[2].field,
            imageUrlField = fields[3] && fields[3].field,
            item,
            id,
            textChild,
            className,
            children;

        function elements(collection, tagName) {
            return collection.filter(tagName).add(collection.find(tagName));
        }

        for (idx = 0, length = items.length; idx < length; idx++) {
            record = { _loaded: true };
            item = items.eq(idx);

            textChild = item[0].firstChild;
            children = item.children();
            list = children.filter("ul");
            children = children.filter(":not(ul)");

            id = item.attr("data-id");

            if (id) {
                record.id = id;
            }

            if (textChild) {
                record[textField] = textChild.nodeType == 3 ? textChild.nodeValue : children.text();
            }

            if (urlField) {
                record[urlField] = elements(children, "a").attr("href");
            }

            if (imageUrlField) {
                record[imageUrlField] = elements(children, "img").attr("src");
            }

            if (spriteCssClassField) {
                className = elements(children, ".k-sprite").prop("className");
                record[spriteCssClassField] = className && $.trim(className.replace("k-sprite", ""));
            }

            if (list.length) {
                record.items = inferList(list.eq(0), fields);
            }

            if (item.attr("data-hasChildren") == "true") {
                record.hasChildren = true;
            }

            data.push(record);
        }

        return data;
    }

    HierarchicalDataSource.create = function(options) {
        options = options && options.push ? { data: options } : options;

        var dataSource = options || {},
            data = dataSource.data,
            fields = dataSource.fields,
            list = dataSource.list;

        if (data && data._dataSource) {
            return data._dataSource;
        }

        if (!data && fields && !dataSource.transport) {
            if (list) {
                data = inferList(list, fields);
            }
        }

        dataSource.data = data;

        return dataSource instanceof HierarchicalDataSource ? dataSource : new HierarchicalDataSource(dataSource);
    };

    var Buffer = kendo.Observable.extend({
        init: function(dataSource, viewSize, disablePrefetch) {
            kendo.Observable.fn.init.call(this);

            this._prefetching = false;
            this.dataSource = dataSource;
            this.prefetch = !disablePrefetch;

            var buffer = this;

            dataSource.bind("change", function() {
                buffer._change();
            });

            this._syncWithDataSource();

            this.setViewSize(viewSize);
        },

        setViewSize: function(viewSize) {
            this.viewSize = viewSize;
            this._recalculate();
        },

        at: function(index)  {
            var pageSize = this.pageSize, item;

            if (index >= this.total()) {
                this.trigger("endreached", {index: index });
                return;
            }

            if (!this.useRanges) {
               return this.dataSource.view()[index];
            }
            if (this.useRanges) {
                // out of range request
                if (index < this.dataOffset || index > this.skip + pageSize) {
                    var offset = Math.floor(index / pageSize) * pageSize;
                    this.range(offset);
                }

                // prefetch
                if (index === this.prefetchThreshold) {
                    this._prefetch();
                }

                // mid-range jump - prefetchThreshold and nextPageThreshold may be equal, do not change to else if
                if (index === this.midPageThreshold) {
                    this.range(this.nextMidRange);
                }
                // next range jump
                else if (index === this.nextPageThreshold) {
                    this.range(this.nextFullRange);
                }
                // pull-back
                else if (index === this.pullBackThreshold) {
                    if (this.offset === this.skip) { // from full range to mid range
                        this.range(this.previousMidRange);
                    } else { // from mid range to full range
                        this.range(this.previousFullRange);
                    }
                }

                item = this.dataSource.at(index - this.dataOffset);
            }

            if (item === undefined) {
                this.trigger("endreached", { index: index });
            }

            return item;
        },

        indexOf: function(item) {
            return this.dataSource.data().indexOf(item) + this.dataOffset;
        },

        total: function() {
            return parseInt(this.dataSource.total(), 10);
        },

        next: function() {
            var buffer = this,
                pageSize = buffer.pageSize,
                offset = buffer.skip - buffer.viewSize, // this calculation relies that the buffer has already jumped into the mid range segment
                pageSkip = math.max(math.floor(offset / pageSize), 0) * pageSize + pageSize;

            this.offset = offset;
            this.dataSource.prefetch(pageSkip, pageSize, function() {
                buffer._goToRange(offset, true);
            });
        },

        range: function(offset) {
            if (this.offset === offset) {
                return;
            }

            var buffer = this,
                pageSize = this.pageSize,
                pageSkip = math.max(math.floor(offset / pageSize), 0) * pageSize + pageSize,
                dataSource = this.dataSource;

            this.offset = offset;
            this._recalculate();
            if (dataSource.inRange(offset, pageSize)) {
                this._goToRange(offset);
            } else if (this.prefetch) {
                dataSource.prefetch(pageSkip, pageSize, function() {
                    buffer._goToRange(offset, true);
                });
            }
        },

        syncDataSource: function() {
            var offset = this.offset;
            this.offset = null;
            this.range(offset);
        },

        destroy: function() {
            this.unbind();
        },

        _prefetch: function() {
            var buffer = this,
                pageSize = this.pageSize,
                prefetchOffset = this.skip + pageSize,
                dataSource = this.dataSource;

            if (!dataSource.inRange(prefetchOffset, pageSize) && !this._prefetching && this.prefetch) {
                this._prefetching = true;
                this.trigger("prefetching", { skip: prefetchOffset, take: pageSize });

                dataSource.prefetch(prefetchOffset, pageSize, function() {
                    buffer._prefetching = false;
                    buffer.trigger("prefetched", { skip: prefetchOffset, take: pageSize });
                });
            }
        },

        _goToRange: function(offset, expanding) {
            if (this.offset !== offset) {
                return;
            }

            this.dataOffset = offset;
            this._expanding = expanding;
            this.dataSource.range(offset, this.pageSize);
        },

        _change: function() {
            var dataSource = this.dataSource,
                firstItemUid = dataSource.firstItemUid();

            this.length = this.useRanges ? dataSource.lastRange().end : dataSource.view().length;

            if (this._firstItemUid !== firstItemUid || !this.useRanges) {
                this._syncWithDataSource();
                this._recalculate();
                this.trigger("reset", { offset: this.offset });
            }

            this.trigger("resize");

            if (this._expanding) {
                this.trigger("expand");
            }

            delete this._expanding;
        },

        _syncWithDataSource: function() {
            var dataSource = this.dataSource;

            this._firstItemUid = dataSource.firstItemUid();
            this.dataOffset = this.offset = dataSource.skip() || 0;
            this.pageSize = dataSource.pageSize();
            this.useRanges = dataSource.options.serverPaging;
        },

        _recalculate: function() {
            var pageSize = this.pageSize,
                offset = this.offset,
                viewSize = this.viewSize,
                skip = Math.ceil(offset / pageSize) * pageSize;

            this.skip = skip;
            this.midPageThreshold = skip + pageSize - 1;
            this.nextPageThreshold = skip + viewSize - 1;
            this.prefetchThreshold = skip + Math.floor(pageSize / 3 * 2);
            this.pullBackThreshold = this.offset - 1;

            this.nextMidRange = skip + pageSize - viewSize;
            this.nextFullRange = skip;
            this.previousMidRange = offset - viewSize;
            this.previousFullRange = skip - pageSize;
        }
    });

    var BatchBuffer = kendo.Observable.extend({
        init: function(dataSource, batchSize) {
            var batchBuffer = this;

            kendo.Observable.fn.init.call(batchBuffer);

            this.dataSource = dataSource;
            this.batchSize = batchSize;
            this._total = 0;

            this.buffer = new Buffer(dataSource, batchSize * 3);

            this.buffer.bind({
                "endreached": function (e) {
                    batchBuffer.trigger("endreached", { index: e.index });
                },
                "prefetching": function (e) {
                    batchBuffer.trigger("prefetching", { skip: e.skip, take: e.take });
                },
                "prefetched": function (e) {
                    batchBuffer.trigger("prefetched", { skip: e.skip, take: e.take });
                },
                "reset": function () {
                    batchBuffer._total = 0;
                    batchBuffer.trigger("reset");
                },
                "resize": function () {
                    batchBuffer._total = Math.ceil(this.length / batchBuffer.batchSize);
                    batchBuffer.trigger("resize", { total: batchBuffer.total(), offset: this.offset });
                }
            });
        },

        syncDataSource: function() {
            this.buffer.syncDataSource();
        },

        at: function(index) {
            var buffer = this.buffer,
                skip = index * this.batchSize,
                take = this.batchSize,
                view = [],
                item;

            if (buffer.offset > skip) {
                buffer.at(buffer.offset - 1);
            }

            for (var i = 0; i < take; i++) {
                item = buffer.at(skip + i);

                if (item === undefined) {
                    break;
                }

                view.push(item);
            }

            return view;
        },

        total: function() {
            return this._total;
        },

        destroy: function() {
            this.buffer.destroy();
            this.unbind();
        }
    });

    extend(true, kendo.data, {
        readers: {
            json: DataReader
        },
        Query: Query,
        DataSource: DataSource,
        HierarchicalDataSource: HierarchicalDataSource,
        Node: Node,
        ObservableObject: ObservableObject,
        ObservableArray: ObservableArray,
        LocalTransport: LocalTransport,
        RemoteTransport: RemoteTransport,
        Cache: Cache,
        DataReader: DataReader,
        Model: Model,
        Buffer: Buffer,
        BatchBuffer: BatchBuffer
    });
})(window.kendo.jQuery);

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

/*jshint eqnull: true */
(function ($, undefined) {
    var kendo = window.kendo,
        Observable = kendo.Observable,
        ObservableObject = kendo.data.ObservableObject,
        ObservableArray = kendo.data.ObservableArray,
        toString = {}.toString,
        binders = {},
        splice = Array.prototype.splice,
        Class = kendo.Class,
        innerText,
        proxy = $.proxy,
        VALUE = "value",
        SOURCE = "source",
        EVENTS = "events",
        CHECKED = "checked",
        deleteExpando = true,
        CHANGE = "change";

    (function() {
        var a = document.createElement("a");

        if (a.innerText !== undefined) {
            innerText = "innerText";
        } else if (a.textContent !== undefined) {
            innerText = "textContent";
        }

        try {
            delete a.test;
        } catch(e) {
            deleteExpando = false;
        }
    })();

    var Binding = Observable.extend( {
        init: function(parents, path) {
            var that = this;

            Observable.fn.init.call(that);

            that.source = parents[0];
            that.parents = parents;
            that.path = path;
            that.dependencies = {};
            that.dependencies[path] = true;
            that.observable = that.source instanceof Observable;

            that._access = function(e) {
                that.dependencies[e.field] = true;
            };

            if (that.observable) {
                that._change = function(e) {
                    that.change(e);
                };

                that.source.bind(CHANGE, that._change);
            }
        },

        _parents: function() {
            var parents = this.parents;
            var value = this.get();

            if (value && typeof value.parent == "function") {
                var parent = value.parent();

                if ($.inArray(parent, parents) < 0) {
                    parents = [parent].concat(parents);
                }
            }

            return parents;
        },

        change: function(e) {
            var dependency,
                ch,
                field = e.field,
                that = this;

            if (that.path === "this") {
                that.trigger(CHANGE, e);
            } else {
                for (dependency in that.dependencies) {
                    if (dependency.indexOf(field) === 0) {
                       ch = dependency.charAt(field.length);

                       if (!ch || ch === "." || ch === "[") {
                            that.trigger(CHANGE, e);
                            break;
                       }
                    }
                }
            }
        },

        start: function(source) {
            source.bind("get", this._access);
        },

        stop: function(source) {
            source.unbind("get", this._access);
        },

        get: function() {

            var that = this,
                source = that.source,
                index = 0,
                path = that.path,
                result = source;

            if (!that.observable) {
                return result;
            }

            that.start(that.source);

            result = source.get(path);

            // Traverse the observable hierarchy if the binding is not resolved at the current level.
            while (result === undefined && source) {

                source = that.parents[++index];

                if (source instanceof ObservableObject) {
                    result = source.get(path);
                }
            }

            // second pass try to get the parent from the object hierarchy
            if (result === undefined) {
                source = that.source; //get the initial source

                while (result === undefined && source) {
                    source = source.parent();

                    if (source instanceof ObservableObject) {
                        result = source.get(path);
                    }
                }
            }

            // If the result is a function - invoke it
            if (typeof result === "function") {
                index = path.lastIndexOf(".");

                // If the function is a member of a nested observable object make that nested observable the context (this) of the function
                if (index > 0) {
                    source = source.get(path.substring(0, index));
                }

                // Invoke the function
                that.start(source);

                if (source !== that.source) {
                    result = result.call(source, that.source);
                } else {
                    result = result.call(source);
                }

                that.stop(source);
            }

            // If the binding is resolved by a parent object
            if (source && source !== that.source) {

                that.currentSource = source; // save parent object

                // Listen for changes in the parent object
                source.unbind(CHANGE, that._change)
                      .bind(CHANGE, that._change);
            }

            that.stop(that.source);

            return result;
        },

        set: function(value) {
            var source = this.currentSource || this.source;

            var field = kendo.getter(this.path)(source);

            if (typeof field === "function") {
                if (source !== this.source) {
                    field.call(source, this.source, value);
                } else {
                    field.call(source, value);
                }
            } else {
                source.set(this.path, value);
            }
        },

        destroy: function() {
            if (this.observable) {
                this.source.unbind(CHANGE, this._change);
            }
        }
    });

    var EventBinding = Binding.extend( {
        get: function() {
            var source = this.source,
                path = this.path,
                index = 0,
                handler;

            handler = source.get(path);

            while (!handler && source) {
                source = this.parents[++index];

                if (source instanceof ObservableObject) {
                    handler = source.get(path);
                }
            }

            return proxy(handler, source);
        }
    });

    var TemplateBinding = Binding.extend( {
        init: function(source, path, template) {
            var that = this;

            Binding.fn.init.call(that, source, path);

            that.template = template;
        },

        render: function(value) {
            var html;

            this.start(this.source);

            html = kendo.render(this.template, value);

            this.stop(this.source);

            return html;
        }
    });

    var Binder = Class.extend({
        init: function(element, bindings, options) {
            this.element = element;
            this.bindings = bindings;
            this.options = options;
        },

        bind: function(binding, attribute) {
            var that = this;

            binding = attribute ? binding[attribute] : binding;

            binding.bind(CHANGE, function(e) {
                that.refresh(attribute || e);
            });

            that.refresh(attribute);
        },

        destroy: function() {
        }
    });

    binders.attr = Binder.extend({
        refresh: function(key) {
            this.element.setAttribute(key, this.bindings.attr[key].get());
        }
    });

    binders.style = Binder.extend({
        refresh: function(key) {
            this.element.style[key] = this.bindings.style[key].get() || "";
        }
    });

    binders.enabled = Binder.extend({
        refresh: function() {
            if (this.bindings.enabled.get()) {
                this.element.removeAttribute("disabled");
            } else {
                this.element.setAttribute("disabled", "disabled");
            }
        }
    });

    binders.readonly = Binder.extend({
       refresh: function() {
            if (this.bindings.readonly.get()) {
                this.element.setAttribute("readonly", "readonly");
            } else {
                this.element.removeAttribute("readonly");
            }
       }
    });

    binders.disabled = Binder.extend({
        refresh: function() {
            if (this.bindings.disabled.get()) {
                this.element.setAttribute("disabled", "disabled");
            } else {
                this.element.removeAttribute("disabled");
            }
        }
    });

    binders.events = Binder.extend({
        init: function(element, bindings, options) {
            Binder.fn.init.call(this, element, bindings, options);
            this.handlers = {};
        },

        refresh: function(key) {
            var element = $(this.element),
                binding = this.bindings.events[key],
                handler = this.handlers[key];

            if (handler) {
                element.off(key, handler);
            }

            handler = this.handlers[key] = binding.get();

            element.on(key, binding.source, handler);
        },

        destroy: function() {
            var element = $(this.element),
                handler;

            for (handler in this.handlers) {
                element.off(handler, this.handlers[handler]);
            }
        }
    });

    binders.text = Binder.extend({
        refresh: function() {
            var text = this.bindings.text.get();

            if (text == null) {
                text = "";
            }

            this.element[innerText] = text;
        }
    });

    binders.visible = Binder.extend({
        refresh: function() {
            if (this.bindings.visible.get()) {
                this.element.style.display = "";
            } else {
                this.element.style.display = "none";
            }
        }
    });

    binders.invisible = Binder.extend({
        refresh: function() {
            if (!this.bindings.invisible.get()) {
                this.element.style.display = "";
            } else {
                this.element.style.display = "none";
            }
        }
    });

    binders.html = Binder.extend({
        refresh: function() {
            this.element.innerHTML = this.bindings.html.get();
        }
    });

    binders.value = Binder.extend({
        init: function(element, bindings, options) {
            Binder.fn.init.call(this, element, bindings, options);

            this._change = proxy(this.change, this);
            this.eventName = options.valueUpdate || CHANGE;

            $(this.element).on(this.eventName, this._change);

            this._initChange = false;
        },

        change: function() {
            this._initChange = this.eventName != CHANGE;

            var value = this.element.value;

            var type = this.element.type;

            if (type == "date") {
                value = kendo.parseDate(value, "yyyy-MM-dd");
            } else if (type == "datetime-local") {
                value = kendo.parseDate(value, ["yyyy-MM-ddTHH:mm:ss", "yyyy-MM-ddTHH:mm"] );
            } else if (type == "number") {
                value = kendo.parseFloat(value);
            }

            this.bindings[VALUE].set(value);

            this._initChange = false;
        },

        refresh: function() {
            if (!this._initChange) {
                var value = this.bindings[VALUE].get();

                if (value == null) {
                    value = "";
                }

                var type = this.element.type;

                if (type == "date") {
                    value = kendo.toString(value, "yyyy-MM-dd");
                } else if (type == "datetime-local") {
                    value = kendo.toString(value, "yyyy-MM-ddTHH:mm:ss");
                }

                this.element.value = value;
            }

            this._initChange = false;
        },

        destroy: function() {
            $(this.element).off(this.eventName, this._change);
        }
    });

    binders.source = Binder.extend({
        init: function(element, bindings, options) {
            Binder.fn.init.call(this, element, bindings, options);

            var source = this.bindings.source.get();

            if (source instanceof kendo.data.DataSource && options.autoBind !== false) {
                source.fetch();
            }
        },

        refresh: function(e) {
            var that = this,
                source = that.bindings.source.get();

            if (source instanceof ObservableArray || source instanceof kendo.data.DataSource) {
                e = e || {};

                if (e.action == "add") {
                    that.add(e.index, e.items);
                } else if (e.action == "remove") {
                    that.remove(e.index, e.items);
                } else if (e.action != "itemchange") {
                    that.render();
                }
            } else {
                that.render();
            }
        },

        container: function() {
            var element = this.element;

            if (element.nodeName.toLowerCase() == "table") {
                if (!element.tBodies[0]) {
                    element.appendChild(document.createElement("tbody"));
                }
                element = element.tBodies[0];
            }

            return element;
        },

        template: function() {
            var options = this.options,
                template = options.template,
                nodeName = this.container().nodeName.toLowerCase();

            if (!template) {
                if (nodeName == "select") {
                    if (options.valueField || options.textField) {
                        template = kendo.format('<option value="#:{0}#">#:{1}#</option>',
                            options.valueField || options.textField, options.textField || options.valueField);
                    } else {
                        template = "<option>#:data#</option>";
                    }
                } else if (nodeName == "tbody") {
                    template = "<tr><td>#:data#</td></tr>";
                } else if (nodeName == "ul" || nodeName == "ol") {
                    template = "<li>#:data#</li>";
                } else {
                    template = "#:data#";
                }

                template = kendo.template(template);
            }

            return template;
        },

        add: function(index, items) {
            var element = this.container(),
                parents,
                idx,
                length,
                child,
                clone = element.cloneNode(false),
                reference = element.children[index];

            $(clone).html(kendo.render(this.template(), items));

            if (clone.children.length) {
                parents = this.bindings.source._parents();

                for (idx = 0, length = items.length; idx < length; idx++) {
                    child = clone.children[0];
                    element.insertBefore(child, reference || null);
                    bindElement(child, items[idx], this.options.roles, [items[idx]].concat(parents));
                }
            }
        },

        remove: function(index, items) {
            var idx, element = this.container();

            for (idx = 0; idx < items.length; idx++) {
                var child = element.children[index];
                unbindElementTree(child);
                element.removeChild(child);
            }
        },

        render: function() {
            var source = this.bindings.source.get(),
                parents,
                idx,
                length,
                element = this.container(),
                template = this.template();

            if (source instanceof kendo.data.DataSource) {
                source = source.view();
            }

            if (!(source instanceof ObservableArray) && toString.call(source) !== "[object Array]") {
                source = [source];
            }

            if (this.bindings.template) {
                unbindElementChildren(element);

                $(element).html(this.bindings.template.render(source));

                if (element.children.length) {
                    parents = this.bindings.source._parents();

                    for (idx = 0, length = source.length; idx < length; idx++) {
                        bindElement(element.children[idx], source[idx], this.options.roles, [source[idx]].concat(parents));
                    }
                }
            }
            else {
                $(element).html(kendo.render(template, source));
            }
        }
    });

    binders.input = {
        checked: Binder.extend({
            init: function(element, bindings, options) {
                Binder.fn.init.call(this, element, bindings, options);
                this._change = proxy(this.change, this);

                $(this.element).change(this._change);
            },
            change: function() {
                var element = this.element;
                var value = this.value();

                if (element.type == "radio") {
                    this.bindings[CHECKED].set(value);
                } else if (element.type == "checkbox") {
                    var source = this.bindings[CHECKED].get();
                    var index;

                    if (source instanceof ObservableArray) {
                        value = this.element.value;

                        if (value !== "on" && value !== "off") {
                            index = source.indexOf(value);
                            if (index > -1) {
                                source.splice(index, 1);
                            } else {
                                source.push(value);
                            }
                        }
                    } else {
                        this.bindings[CHECKED].set(value);
                    }
                }
            },

            refresh: function() {
                var value = this.bindings[CHECKED].get(),
                    source = value,
                    element = this.element;

                if (element.type == "checkbox") {
                    if (source instanceof ObservableArray) {
                        value = this.element.value;
                        if (source.indexOf(value) >= 0) {
                            value = true;
                        }
                    }

                    element.checked = value === true;
                } else if (element.type == "radio" && value != null) {
                    if (element.value === value.toString()) {
                        element.checked = true;
                    }
                }
            },

            value: function() {
                var element = this.element,
                    value = element.value;

                if (element.type == "checkbox") {
                    value = element.checked;
                }

                return value;
            },
            destroy: function() {
                $(this.element).off(CHANGE, this._change);
            }
        })
    };

    binders.select = {
        value: Binder.extend({
            init: function(target, bindings, options) {
                Binder.fn.init.call(this, target, bindings, options);

                this._change = proxy(this.change, this);
                $(this.element).change(this._change);
            },

            change: function() {
                var values = [],
                    element = this.element,
                    source,
                    field = this.options.valueField || this.options.textField,
                    valuePrimitive = this.options.valuePrimitive,
                    option,
                    valueIndex,
                    value,
                    idx,
                    length;

                for (idx = 0, length = element.options.length; idx < length; idx++) {
                    option = element.options[idx];

                    if (option.selected) {
                        value = option.attributes.value;

                        if (value && value.specified) {
                            value = option.value;
                        } else {
                            value = option.text;
                        }

                        values.push(value);
                    }
                }

                if (field) {
                    source = this.bindings.source.get();
                    for (valueIndex = 0; valueIndex < values.length; valueIndex++) {
                        for (idx = 0, length = source.length; idx < length; idx++) {
                            if (source[idx].get(field) == values[valueIndex]) {
                                values[valueIndex] = source[idx];
                                break;
                            }
                        }
                    }
                }

                value = this.bindings[VALUE].get();
                if (value instanceof ObservableArray) {
                    value.splice.apply(value, [0, value.length].concat(values));
                } else if (!valuePrimitive && (value instanceof ObservableObject || !field)) {
                    this.bindings[VALUE].set(values[0]);
                } else {
                    this.bindings[VALUE].set(values[0].get(field));
                }
            },
            refresh: function() {
                var optionIndex,
                    element = this.element,
                    options = element.options,
                    value = this.bindings[VALUE].get(),
                    values = value,
                    field = this.options.valueField || this.options.textField,
                    found = false,
                    optionValue;

                if (!(values instanceof ObservableArray)) {
                    values = new ObservableArray([value]);
                }

                element.selectedIndex = -1;

                for (var valueIndex = 0; valueIndex < values.length; valueIndex++) {
                    value = values[valueIndex];

                    if (field && value instanceof ObservableObject) {
                        value = value.get(field);
                    }

                    for (optionIndex = 0; optionIndex < options.length; optionIndex++) {
                        optionValue = options[optionIndex].value;

                        if (optionValue === "" && value !== "") {
                            optionValue = options[optionIndex].text;
                        }

                        if (optionValue == value) {
                            options[optionIndex].selected = true;
                            found = true;
                        }
                    }
                }
            },
            destroy: function() {
                $(this.element).off(CHANGE, this._change);
            }
        })
    };

    binders.widget = {
        events : Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);
                this.widget = widget;
                this.handlers = {};
            },

            refresh: function(key) {
                var binding = this.bindings.events[key],
                    handler = this.handlers[key];

                if (handler) {
                    this.widget.unbind(key, handler);
                }

                handler = binding.get();

                this.handlers[key] = function(e) {
                    e.data = binding.source;

                    handler(e);

                    if (e.data === binding.source) {
                        delete e.data;
                    }
                };

                this.widget.bind(key, this.handlers[key]);
            },

            destroy: function() {
                var handler;

                for (handler in this.handlers) {
                    this.widget.unbind(handler, this.handlers[handler]);
                }
            }
        }),

        checked: Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);

                this.widget = widget;
                this._change = proxy(this.change, this);
                this.widget.bind(CHANGE, this._change);
            },
            change: function() {
                this.bindings[CHECKED].set(this.value());
            },

            refresh: function() {
                this.widget.check(this.bindings[CHECKED].get() === true);
            },

            value: function() {
                var element = this.element,
                    value = element.value;

                if (value == "on" || value == "off") {
                    value = element.checked;
                }

                return value;
            },

            destroy: function() {
                this.widget.unbind(CHANGE, this._change);
            }
        }),

        visible: Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);

                this.widget = widget;
            },

            refresh: function() {
                var visible = this.bindings.visible.get();
                this.widget.wrapper[0].style.display = visible ? "" : "none";
            }
        }),

        invisible: Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);

                this.widget = widget;
            },

            refresh: function() {
                var invisible = this.bindings.invisible.get();
                this.widget.wrapper[0].style.display = invisible ? "none" : "";
            }
        }),

        enabled: Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);

                this.widget = widget;
            },

            refresh: function() {
                if (this.widget.enable) {
                    this.widget.enable(this.bindings.enabled.get());
                }
            }
        }),

        disabled: Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);

                this.widget = widget;
            },

            refresh: function() {
                if (this.widget.enable) {
                    this.widget.enable(!this.bindings.disabled.get());
                }
            }
        }),

        source: Binder.extend({
            init: function(widget, bindings, options) {
                var that = this;

                Binder.fn.init.call(that, widget.element[0], bindings, options);

                that.widget = widget;
                that._dataBinding = proxy(that.dataBinding, that);
                that._dataBound = proxy(that.dataBound, that);
                that._itemChange = proxy(that.itemChange, that);
            },

            itemChange: function(e) {
                bindElement(e.item[0], e.data, this._ns(e.ns), [e.data].concat(this.bindings.source._parents()));
            },

            dataBinding: function() {
                var idx,
                    length,
                    widget = this.widget,
                    items = widget.items();

                for (idx = 0, length = items.length; idx < length; idx++) {
                    unbindElementTree(items[idx]);
                }
            },

            _ns: function(ns) {
                ns = ns || kendo.ui;
                var all = [ kendo.ui, kendo.dataviz.ui, kendo.mobile.ui ];
                all.splice($.inArray(ns, all), 1);
                all.unshift(ns);

                return kendo.rolesFromNamespaces(all);
            },

            dataBound: function(e) {
                var idx,
                    length,
                    widget = this.widget,
                    items = widget.items(),
                    dataSource = widget.dataSource,
                    view = dataSource.view(),
                    parents,
                    groups = dataSource.group() || [];

                if (items.length) {
                    if (groups.length) {
                        view = flattenGroups(view);
                    }

                    parents = this.bindings.source._parents();

                    for (idx = 0, length = view.length; idx < length; idx++) {
                        bindElement(items[idx], view[idx], this._ns(e.ns), [view[idx]].concat(parents));
                    }
                }
            },

            refresh: function(e) {
                var that = this,
                    source,
                    widget = that.widget;

                e = e || {};

                if (!e.action) {
                    that.destroy();

                    widget.bind("dataBinding", that._dataBinding);
                    widget.bind("dataBound", that._dataBound);
                    widget.bind("itemChange", that._itemChange);

                    source = that.bindings.source.get();

                    if (widget.dataSource instanceof kendo.data.DataSource && widget.dataSource != source) {
                        if (source instanceof kendo.data.DataSource) {
                            widget.setDataSource(source);
                        } else if (source && source._dataSource) {
                            widget.setDataSource(source._dataSource);
                        } else {
                            widget.dataSource.data(source);
                        }
                    }
                }
            },

            destroy: function() {
                var widget = this.widget;

                widget.unbind("dataBinding", this._dataBinding);
                widget.unbind("dataBound", this._dataBound);
                widget.unbind("itemChange", this._itemChange);
            }
        }),

        value: Binder.extend({
            init: function(widget, bindings, options) {
                Binder.fn.init.call(this, widget.element[0], bindings, options);

                this.widget = widget;
                this._change = $.proxy(this.change, this);
                this.widget.first(CHANGE, this._change);

                var value = this.bindings.value.get();

                this._valueIsObservableObject = !options.valuePrimitive && (value == null || value instanceof ObservableObject);
                this._valueIsObservableArray = value instanceof ObservableArray;
                this._initChange = false;
            },

            change: function() {
                var value = this.widget.value(),
                    field = this.options.dataValueField || this.options.dataTextField,
                    isArray = toString.call(value) === "[object Array]",
                    isObservableObject = this._valueIsObservableObject,
                    valueIndex, valueLength, values = [],
                    sourceItem, sourceValue,
                    idx, length, source;

                this._initChange = true;

                if (field) {

                    if (this.bindings.source) {
                        source = this.bindings.source.get();
                    }

                    if (value === "" && (isObservableObject || this.options.valuePrimitive)) {
                        value = null;
                    } else {
                        if (!source || source instanceof kendo.data.DataSource) {
                            source = this.widget.dataSource.view();
                        }

                        if (isArray) {
                            valueLength = value.length;
                            values = value.slice(0);
                        }

                        for (idx = 0, length = source.length; idx < length; idx++) {
                            sourceItem = source[idx];
                            sourceValue = sourceItem.get(field);

                            if (isArray) {
                                for (valueIndex = 0; valueIndex < valueLength; valueIndex++) {
                                    if (sourceValue == values[valueIndex]) {
                                        values[valueIndex] = sourceItem;
                                        break;
                                    }
                                }
                            } else if (sourceValue == value) {
                                value = isObservableObject ? sourceItem : sourceValue;
                                break;
                            }
                        }

                        if (values[0]) {
                            if (this._valueIsObservableArray) {
                                value = values;
                            } else if (isObservableObject || !field) {
                                value = values[0];
                            } else {
                                value = values[0].get(field);
                            }
                        }
                    }
                }

                this.bindings.value.set(value);
                this._initChange = false;
            },

            refresh: function() {

                if (!this._initChange) {
                    var field = this.options.dataValueField || this.options.dataTextField,
                        value = this.bindings.value.get(),
                              idx = 0, length,
                              values = [];

                    if (field) {
                        if (value instanceof ObservableArray) {
                            for (length = value.length; idx < length; idx++) {
                                values[idx] = value[idx].get(field);
                            }
                            value = values;
                        } else if (value instanceof ObservableObject) {
                            value = value.get(field);
                        }
                    }
                    this.widget.value(value);
                }

                this._initChange = false;
            },

            destroy: function() {
                this.widget.unbind(CHANGE, this._change);
            }
        }),

        multiselect: {
            value: Binder.extend({
                init: function(widget, bindings, options) {
                    Binder.fn.init.call(this, widget.element[0], bindings, options);

                    this.widget = widget;
                    this._change = $.proxy(this.change, this);
                    this.widget.first(CHANGE, this._change);
                    this._initChange = false;
                },

                change: function() {
                    var that = this,
                        oldValues = that.bindings[VALUE].get(),
                        valuePrimitive = that.options.valuePrimitive,
                        newValues = valuePrimitive ? that.widget.value() : that.widget.dataItems();

                    var field = this.options.dataValueField || this.options.dataTextField;

                    newValues = newValues.slice(0);

                    that._initChange = true;

                    if (oldValues instanceof ObservableArray) {
                        var remove = [];

                        var newLength = newValues.length;

                        var i = 0, j = 0;
                        var old = oldValues[i];
                        var same = false;
                        var removeIndex;
                        var newValue;
                        var found;

                        var oldIdx = 0;
                        var oldLength = oldValues.length + 1;

                        while (old) {
                            found = false;
                            for (j = 0; j < newLength; j++) {
                                if (valuePrimitive) {
                                    same = newValues[j] == old;
                                } else {
                                    newValue = newValues[j];

                                    newValue = newValue.get ? newValue.get(field) : newValue;
                                    same = newValue == (old.get ? old.get(field) : old);
                                }

                                if (same) {
                                    newValues.splice(j, 1);
                                    newLength -= 1;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                remove.push(old);
                                splice.call(oldValues, i, 1);
                                removeIndex = i;
                            } else {
                                i += 1;
                            }

                            old = oldValues[i];
                            oldIdx++;

                            if (oldIdx > oldLength) {
                                break;
                            }
                        }

                        splice.apply(oldValues, [oldValues.length, 0].concat(newValues));

                        if (remove.length) {
                            oldValues.trigger("change", {
                                action: "remove",
                                items: remove,
                                index: removeIndex
                            });
                        }

                        if (newValues.length) {
                            oldValues.trigger("change", {
                                action: "add",
                                items: newValues,
                                index: oldValues.length - 1
                            });
                        }
                    } else {
                        that.bindings[VALUE].set(newValues);
                    }

                    that._initChange = false;
                },

                refresh: function() {
                    if (!this._initChange) {
                        var field = this.options.dataValueField || this.options.dataTextField,
                            value = this.bindings.value.get(),
                            idx = 0, length,
                            values = [],
                            selectedValue;

                        if (field) {
                            if (value instanceof ObservableArray) {
                                for (length = value.length; idx < length; idx++) {
                                    selectedValue = value[idx];
                                    values[idx] = selectedValue.get ? selectedValue.get(field) : selectedValue;
                                }
                                value = values;
                            } else if (value instanceof ObservableObject) {
                                value = value.get(field);
                            }
                        }

                        this.widget.value(value);
                    }
                },

                destroy: function() {
                    this.widget.unbind(CHANGE, this._change);
                }

            })
        }
    };

    var BindingTarget = Class.extend( {
        init: function(target, options) {
            this.target = target;
            this.options = options;
            this.toDestroy = [];
        },

        bind: function(bindings) {
            var nodeName = this.target.nodeName.toLowerCase(),
                key,
                hasValue,
                hasSource,
                hasEvents,
                specificBinders = binders[nodeName] || {};

            for (key in bindings) {
                if (key == VALUE) {
                    hasValue = true;
                } else if (key == SOURCE) {
                    hasSource = true;
                } else if (key == EVENTS) {
                    hasEvents = true;
                } else {
                    this.applyBinding(key, bindings, specificBinders);
                }
            }

            if (hasSource) {
                this.applyBinding(SOURCE, bindings, specificBinders);
            }

            if (hasValue) {
                this.applyBinding(VALUE, bindings, specificBinders);
            }

            if (hasEvents) {
                this.applyBinding(EVENTS, bindings, specificBinders);
            }
        },

        applyBinding: function(name, bindings, specificBinders) {
            var binder = specificBinders[name] || binders[name],
                toDestroy = this.toDestroy,
                attribute,
                binding = bindings[name];

            if (binder) {
                binder = new binder(this.target, bindings, this.options);

                toDestroy.push(binder);

                if (binding instanceof Binding) {
                    binder.bind(binding);
                    toDestroy.push(binding);
                } else {
                    for (attribute in binding) {
                        binder.bind(binding, attribute);
                        toDestroy.push(binding[attribute]);
                    }
                }
            } else if (name !== "template") {
                throw new Error("The " + name + " binding is not supported by the " + this.target.nodeName.toLowerCase() + " element");
            }
        },

        destroy: function() {
            var idx,
                length,
                toDestroy = this.toDestroy;

            for (idx = 0, length = toDestroy.length; idx < length; idx++) {
                toDestroy[idx].destroy();
            }
        }
    });

    var WidgetBindingTarget = BindingTarget.extend( {
        bind: function(bindings) {
            var that = this,
                binding,
                hasValue = false,
                hasSource = false,
                specificBinders = binders.widget[that.target.options.name.toLowerCase()] || {};

            for (binding in bindings) {
                if (binding == VALUE) {
                    hasValue = true;
                } else if (binding == SOURCE) {
                    hasSource = true;
                } else {
                    that.applyBinding(binding, bindings, specificBinders);
                }
            }

            if (hasSource) {
                that.applyBinding(SOURCE, bindings, specificBinders);
            }

            if (hasValue) {
                that.applyBinding(VALUE, bindings, specificBinders);
            }
        },

        applyBinding: function(name, bindings, specificBinders) {
            var binder = specificBinders[name] || binders.widget[name],
                toDestroy = this.toDestroy,
                attribute,
                binding = bindings[name];

            if (binder) {
                binder = new binder(this.target, bindings, this.target.options);

                toDestroy.push(binder);


                if (binding instanceof Binding) {
                    binder.bind(binding);
                    toDestroy.push(binding);
                } else {
                    for (attribute in binding) {
                        binder.bind(binding, attribute);
                        toDestroy.push(binding[attribute]);
                    }
                }
            } else {
                throw new Error("The " + name + " binding is not supported by the " + this.target.options.name + " widget");
            }
        }
    });

    function flattenGroups(data) {
        var idx,
            result = [],
            length,
            items,
            itemIndex;

        for (idx = 0, length = data.length; idx < length; idx++) {
            if (data[idx].hasSubgroups) {
                result = result.concat(flattenGroups(data[idx].items));
            } else {
                items = data[idx].items;
                for (itemIndex = 0; itemIndex < items.length; itemIndex++) {
                    result.push(items[itemIndex]);
                }
            }
        }
        return result;
    }

    function bindingTargetForRole(element, roles) {
        var widget = kendo.initWidget(element, {}, roles);

        if (widget) {
            return new WidgetBindingTarget(widget);
        }
    }

    var keyValueRegExp = /[A-Za-z0-9_\-]+:(\{([^}]*)\}|[^,}]+)/g,
        whiteSpaceRegExp = /\s/g;

    function parseBindings(bind) {
        var result = {},
            idx,
            length,
            token,
            colonIndex,
            key,
            value,
            tokens;

        tokens = bind.match(keyValueRegExp);

        for (idx = 0, length = tokens.length; idx < length; idx++) {
            token = tokens[idx];
            colonIndex = token.indexOf(":");

            key = token.substring(0, colonIndex);
            value = token.substring(colonIndex + 1);

            if (value.charAt(0) == "{") {
                value = parseBindings(value);
            }

            result[key] = value;
        }

        return result;
    }

    function createBindings(bindings, source, type) {
        var binding,
            result = {};

        for (binding in bindings) {
            result[binding] = new type(source, bindings[binding]);
        }

        return result;
    }

    function bindElement(element, source, roles, parents) {
        var role = element.getAttribute("data-" + kendo.ns + "role"),
            idx,
            bind = element.getAttribute("data-" + kendo.ns + "bind"),
            children = element.children,
            childrenCopy = [],
            deep = true,
            bindings,
            options = {},
            target;

        parents = parents || [source];

        if (role || bind) {
            unbindElement(element);
        }

        if (role) {
            target = bindingTargetForRole(element, roles);
        }

        if (bind) {
            bind = parseBindings(bind.replace(whiteSpaceRegExp, ""));

            if (!target) {
                options = kendo.parseOptions(element, {textField: "", valueField: "", template: "", valueUpdate: CHANGE, valuePrimitive: false, autoBind: true});
                options.roles = roles;
                target = new BindingTarget(element, options);
            }

            target.source = source;

            bindings = createBindings(bind, parents, Binding);

            if (options.template) {
                bindings.template = new TemplateBinding(parents, "", options.template);
            }

            if (bindings.click) {
                bind.events = bind.events || {};
                bind.events.click = bind.click;
                delete bindings.click;
            }

            if (bindings.source) {
                deep = false;
            }

            if (bind.attr) {
                bindings.attr = createBindings(bind.attr, parents, Binding);
            }

            if (bind.style) {
                bindings.style = createBindings(bind.style, parents, Binding);
            }

            if (bind.events) {
                bindings.events = createBindings(bind.events, parents, EventBinding);
            }

            target.bind(bindings);
        }

        if (target) {
            element.kendoBindingTarget = target;
        }

        if (deep && children) {
            // https://github.com/telerik/kendo/issues/1240 for the weirdness.
            for (idx = 0; idx < children.length; idx++) {
                childrenCopy[idx] = children[idx];
            }

            for (idx = 0; idx < childrenCopy.length; idx++) {
                bindElement(childrenCopy[idx], source, roles, parents);
            }
        }
    }

    function bind(dom, object) {
        var idx,
            length,
            node,
            roles = kendo.rolesFromNamespaces([].slice.call(arguments, 2));

        object = kendo.observable(object);
        dom = $(dom);

        for (idx = 0, length = dom.length; idx < length; idx++) {
            node = dom[idx];
            if (node.nodeType === 1) {
                bindElement(node, object, roles);
            }
        }
    }

    function unbindElement(element) {
        var bindingTarget = element.kendoBindingTarget;

        if (bindingTarget) {
            bindingTarget.destroy();

            if (deleteExpando) {
                delete element.kendoBindingTarget;
            } else if (element.removeAttribute) {
                element.removeAttribute("kendoBindingTarget");
            } else {
                element.kendoBindingTarget = null;
            }
        }
    }

    function unbindElementTree(element) {
        unbindElement(element);

        unbindElementChildren(element);
    }

    function unbindElementChildren(element) {
        var children = element.children;

        if (children) {
            for (var idx = 0, length = children.length; idx < length; idx++) {
                unbindElementTree(children[idx]);
            }
        }
    }

    function unbind(dom) {
        var idx, length;

        dom = $(dom);

        for (idx = 0, length = dom.length; idx < length; idx++ ) {
            unbindElementTree(dom[idx]);
        }
    }

    function notify(widget, namespace) {
        var element = widget.element,
            bindingTarget = element[0].kendoBindingTarget;

        if (bindingTarget) {
            bind(element, bindingTarget.source, namespace);
        }
    }

    kendo.unbind = unbind;
    kendo.bind = bind;
    kendo.data.binders = binders;
    kendo.data.Binder = Binder;
    kendo.notify = notify;

    kendo.observable = function(object) {
        if (!(object instanceof ObservableObject)) {
            object = new ObservableObject(object);
        }

        return object;
    };

    kendo.observableHierarchy = function(array) {
        var dataSource = kendo.data.HierarchicalDataSource.create(array);

        function recursiveRead(data) {
            var i, children;

            for (i = 0; i < data.length; i++) {
                data[i]._initChildren();

                children = data[i].children;

                children.fetch();

                data[i].items = children.data();

                recursiveRead(data[i].items);
            }
        }

        dataSource.fetch();

        recursiveRead(dataSource.data());

        dataSource._data._dataSource = dataSource;

        return dataSource._data;
    };

})(window.kendo.jQuery);

/* jshint eqnull: true */
(function($, undefined) {
    var kendo = window.kendo,
        Widget = kendo.ui.Widget,
        NS = ".kendoValidator",
        INVALIDMSG = "k-invalid-msg",
        invalidMsgRegExp = new RegExp(INVALIDMSG,'i'),
        INVALIDINPUT = "k-invalid",
        emailRegExp = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i,
        urlRegExp = /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i,
        INPUTSELECTOR = ":input:not(:button,[type=submit],[type=reset],[disabled],[readonly])",
        CHECKBOXSELECTOR = ":checkbox:not([disabled],[readonly])",
        NUMBERINPUTSELECTOR = "[type=number],[type=range]",
        BLUR = "blur",
        NAME = "name",
        FORM = "form",
        NOVALIDATE = "novalidate",
        proxy = $.proxy,
        patternMatcher = function(value, pattern) {
            if (typeof pattern === "string") {
                pattern = new RegExp('^(?:' + pattern + ')$');
            }
            return pattern.test(value);
        },
        matcher = function(input, selector, pattern) {
            var value = input.val();

            if (input.filter(selector).length && value !== "") {
                return patternMatcher(value, pattern);
            }
            return true;
        },
        hasAttribute = function(input, name) {
            if (input.length)  {
                return input[0].attributes[name] != null;
            }
            return false;
        };

    if (!kendo.ui.validator) {
        kendo.ui.validator = { rules: {}, messages: {} };
    }

    function resolveRules(element) {
        var resolvers = kendo.ui.validator.ruleResolvers || {},
            rules = {},
            name;

        for (name in resolvers) {
            $.extend(true, rules, resolvers[name].resolve(element));
        }
        return rules;
    }

    function decode(value) {
        return value.replace(/&amp/g, '&amp;')
            .replace(/&quot;/g, '"')
            .replace(/&#39;/g, "'")
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>');
    }

    function numberOfDecimalDigits(value) {
        value = (value + "").split('.');
        if (value.length > 1) {
            return value[1].length;
        }
        return 0;
    }

    function parseHtml(text) {
        if ($.parseHTML) {
            return $($.parseHTML(text));
        }
        return $(text);
    }

    function searchForMessageContainer(elements, fieldName) {
        var containers = $(),
            element,
            attr;

        for (var idx = 0, length = elements.length; idx < length; idx++) {
            element = elements[idx];
            if (invalidMsgRegExp.test(element.className)) {
                attr = element.getAttribute(kendo.attr("for"));
                if (attr === fieldName) {
                    containers = containers.add(element);
                }
            }
        }
        return containers;
    }

    var Validator = Widget.extend({
        init: function(element, options) {
            var that = this,
                resolved = resolveRules(element);

            options = options || {};

            options.rules = $.extend({}, kendo.ui.validator.rules, resolved.rules, options.rules);
            options.messages = $.extend({}, kendo.ui.validator.messages, resolved.messages, options.messages);

            Widget.fn.init.call(that, element, options);

            that._errorTemplate = kendo.template(that.options.errorTemplate);

            if (that.element.is(FORM)) {
                that.element.attr(NOVALIDATE, NOVALIDATE);
            }

            that._errors = {};
            that._attachEvents();
        },

        events: [ "validate" ],

        options: {
            name: "Validator",
            errorTemplate: '<span class="k-widget k-tooltip k-tooltip-validation">' +
                '<span class="k-icon k-warning"> </span> #=message#</span>',
            messages: {
                required: "{0} is required",
                pattern: "{0} is not valid",
                min: "{0} should be greater than or equal to {1}",
                max: "{0} should be smaller than or equal to {1}",
                step: "{0} is not valid",
                email: "{0} is not valid email",
                url: "{0} is not valid URL",
                date: "{0} is not valid date"
            },
            rules: {
                required: function(input) {
                    var checkbox = input.filter("[type=checkbox]").length && !input.is(":checked"),
                        value = input.val();

                    return !(hasAttribute(input, "required") && (value === "" || !value  || checkbox));
                },
                pattern: function(input) {
                    if (input.filter("[type=text],[type=email],[type=url],[type=tel],[type=search],[type=password]").filter("[pattern]").length && input.val() !== "") {
                        return patternMatcher(input.val(), input.attr("pattern"));
                    }
                    return true;
                },
                min: function(input) {
                    if (input.filter(NUMBERINPUTSELECTOR + ",[" + kendo.attr("type") + "=number]").filter("[min]").length && input.val() !== "") {
                        var min = parseFloat(input.attr("min")) || 0,
                            val = kendo.parseFloat(input.val());

                        return min <= val;
                    }
                    return true;
                },
                max: function(input) {
                    if (input.filter(NUMBERINPUTSELECTOR + ",[" + kendo.attr("type") + "=number]").filter("[max]").length && input.val() !== "") {
                        var max = parseFloat(input.attr("max")) || 0,
                            val = kendo.parseFloat(input.val());

                        return max >= val;
                    }
                    return true;
                },
                step: function(input) {
                    if (input.filter(NUMBERINPUTSELECTOR + ",[" + kendo.attr("type") + "=number]").filter("[step]").length && input.val() !== "") {
                        var min = parseFloat(input.attr("min")) || 0,
                            step = parseFloat(input.attr("step")) || 1,
                            val = parseFloat(input.val()),
                            decimals = numberOfDecimalDigits(step),
                            raise;

                        if (decimals) {
                            raise = Math.pow(10, decimals);
                            return (((val-min)*raise)%(step*raise)) / Math.pow(100, decimals) === 0;
                        }
                        return ((val-min)%step) === 0;
                    }
                    return true;
                },
                email: function(input) {
                    return matcher(input, "[type=email],[" + kendo.attr("type") + "=email]", emailRegExp);
                },
                url: function(input) {
                    return matcher(input, "[type=url],[" + kendo.attr("type") + "=url]", urlRegExp);
                },
                date: function(input) {
                    if (input.filter("[type^=date],[" + kendo.attr("type") + "=date]").length && input.val() !== "") {
                        return kendo.parseDate(input.val(), input.attr(kendo.attr("format"))) !== null;
                    }
                    return true;
                }
            },
            validateOnBlur: true
        },

        destroy: function() {
            Widget.fn.destroy.call(this);

            this.element.off(NS);
        },

        _submit: function(e) {
            if (!this.validate()) {
                e.stopPropagation();
                e.stopImmediatePropagation();
                e.preventDefault();
                return false;
            }
            return true;
        },

        _attachEvents: function() {
            var that = this;

            if (that.element.is(FORM)) {
                that.element.on("submit" + NS, proxy(that._submit, that));
            }

            if (that.options.validateOnBlur) {
                if (!that.element.is(INPUTSELECTOR)) {
                    that.element.on(BLUR + NS, INPUTSELECTOR, function() {
                        that.validateInput($(this));
                    });

                    that.element.on("click" + NS, CHECKBOXSELECTOR, function() {
                        that.validateInput($(this));
                    });
                } else {
                    that.element.on(BLUR + NS, function() {
                        that.validateInput(that.element);
                    });

                    if (that.element.is(CHECKBOXSELECTOR)) {
                        that.element.on("click" + NS, function() {
                            that.validateInput(that.element);
                        });
                    }
                }
            }
        },

        validate: function() {
            var inputs;
            var idx;
            var result = false;
            var length;

            this._errors = {};

            if (!this.element.is(INPUTSELECTOR)) {
                var invalid = false;

                inputs = this.element.find(INPUTSELECTOR);

                for (idx = 0, length = inputs.length; idx < length; idx++) {
                    if (!this.validateInput(inputs.eq(idx))) {
                        invalid = true;
                    }
                }

                result = !invalid;
            } else {
                result = this.validateInput(this.element);
            }

            this.trigger("validate", { valid: result });

            return result;
        },

        validateInput: function(input) {
            input = $(input);

            var that = this,
                template = that._errorTemplate,
                result = that._checkValidity(input),
                valid = result.valid,
                className = "." + INVALIDMSG,
                fieldName = (input.attr(NAME) || ""),
                lbl = that._findMessageContainer(fieldName).add(input.next(className)).hide(),
                messageText;

            input.removeAttr("aria-invalid");

            if (!valid) {
                messageText = that._extractMessage(input, result.key);
                that._errors[fieldName] = messageText;
                var messageLabel = parseHtml(template({ message: decode(messageText) }));

                that._decorateMessageContainer(messageLabel, fieldName);

                if (!lbl.replaceWith(messageLabel).length) {
                    messageLabel.insertAfter(input);
                }
                messageLabel.show();

                input.attr("aria-invalid", true);
            }

            input.toggleClass(INVALIDINPUT, !valid);

            return valid;
        },

        hideMessages: function() {
            var that = this,
                className = "." + INVALIDMSG,
                element = that.element;

            if (!element.is(INPUTSELECTOR)) {
                element.find(className).hide();
            } else {
                element.next(className).hide();
            }
        },

        _findMessageContainer: function(fieldName) {
            var locators = kendo.ui.validator.messageLocators,
                name,
                containers = $();

            for (var idx = 0, length = this.element.length; idx < length; idx++) {
                containers = containers.add(searchForMessageContainer(this.element[idx].getElementsByTagName("*"), fieldName));
            }

            for (name in locators) {
                containers = containers.add(locators[name].locate(this.element, fieldName));
            }

            return containers;
        },

        _decorateMessageContainer: function(container, fieldName) {
            var locators = kendo.ui.validator.messageLocators,
                name;

            container.addClass(INVALIDMSG)
                .attr(kendo.attr("for"), fieldName || "");

            for (name in locators) {
                locators[name].decorate(container, fieldName);
            }

            container.attr("role", "alert");
        },

        _extractMessage: function(input, ruleKey) {
            var that = this,
                customMessage = that.options.messages[ruleKey],
                fieldName = input.attr(NAME);

            customMessage = kendo.isFunction(customMessage) ? customMessage(input) : customMessage;

            return kendo.format(input.attr(kendo.attr(ruleKey + "-msg")) || input.attr("validationMessage") || input.attr("title") || customMessage || "", fieldName, input.attr(ruleKey));
        },

        _checkValidity: function(input) {
            var rules = this.options.rules,
                rule;

            for (rule in rules) {
                if (!rules[rule](input)) {
                    return { valid: false, key: rule };
                }
            }

            return { valid: true };
        },

        errors: function() {
            var results = [],
                errors = this._errors,
                error;

            for (error in errors) {
                results.push(errors[error]);
            }
            return results;
        }
    });

    kendo.ui.plugin(Validator);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        CHANGE = "change",
        BACK = "back",
        SAME = "same",
        support = kendo.support,
        location = window.location,
        history = window.history,
        CHECK_URL_INTERVAL = 50,
        hashStrip = /^#*/,
        document = window.document;

    function absoluteURL(path, pathPrefix) {
        if (!pathPrefix) {
            return path;
        }

        if (path + "/" === pathPrefix) {
            path = pathPrefix;
        }

        var regEx = new RegExp("^" + pathPrefix, "i");

        if (!regEx.test(path)) {
            path = pathPrefix + "/" + path;
        }

        return location.protocol + '//' + (location.host + "/" + path).replace(/\/\/+/g, '/');
    }

    function locationHash() {
        return location.href.split("#")[1] || "";
    }

    function stripRoot(root, url) {
        if (url.indexOf(root) === 0) {
            return (url.substr(root.length)).replace(/\/\//g, '/');
        } else {
            return url;
        }
    }

    var HistoryAdapter = kendo.Class.extend({
        back: function() {
            history.back();
        },

        forward: function() {
            history.forward();
        },

        length: function() {
            return history.length;
        },

        replaceLocation: function(url) {
            location.replace(url);
        }
    });

    var PushStateAdapter = HistoryAdapter.extend({
        init: function(root) {
            this.root = root;
        },

        navigate: function(to) {
            history.pushState({}, document.title, absoluteURL(to, this.root));
        },

        replace: function(to) {
            history.replaceState({}, document.title, absoluteURL(to, this.root));
        },

        normalize: function(url) {
            return stripRoot(this.root, url);
        },

        current: function() {
            var current = location.pathname;

            if (location.search) {
                current += location.search;
            }

            return stripRoot(this.root, current);
        },

        change: function(callback) {
            $(window).bind("popstate.kendo", callback);
        },

        stop: function() {
            $(window).unbind("popstate.kendo");
        },

        normalizeCurrent: function(options) {
            var fixedUrl,
                root = options.root,
                pathname = location.pathname,
                hash = locationHash();

            if (root === pathname + "/") {
                fixedUrl = root;
            }

            if (root === pathname && hash) {
                fixedUrl = absoluteURL(hash.replace(hashStrip, ''), root);
            }

            if (fixedUrl) {
                history.pushState({}, document.title, fixedUrl);
            }
        }
    });

    var HashAdapter = HistoryAdapter.extend({
        init: function() {
            this._id = kendo.guid();
        },

        navigate: function(to) {
            location.hash = to;
        },

        replace: function(to) {
            this.replaceLocation("#" + to.replace(/^#/, ''));
        },

        normalize: function(url) {
            return url;
        },

        change: function(callback) {
            if (support.hashChange) {
                $(window).on("hashchange." + this._id, callback);
            } else {
                this._interval = setInterval(callback, CHECK_URL_INTERVAL);
            }
        },

        stop: function() {
            $(window).off("hashchange." + this._id);
            clearInterval(this._interval);
        },

        current: function() {
            return locationHash();
        },

        normalizeCurrent: function(options) {
            var pathname = location.pathname,
                root = options.root;

            if (options.pushState && root !== pathname) {
                this.replaceLocation(root + '#' + stripRoot(root, pathname));
                return true; // browser will reload at this point.
            }

            return false;
        }
    });

    var History = kendo.Observable.extend({
        start: function(options) {
            options = options || {};

            this.bind([CHANGE, BACK, SAME], options);

            if (this._started) {
                return;
            }

            this._started = true;

            options.root = options.root || "/";

            var adapter = this.createAdapter(options),
                current;

            // adapter may reload the document
            if (adapter.normalizeCurrent(options)) {
                return;
            }

            current = adapter.current();

            $.extend(this, {
                adapter: adapter,
                root: options.root,
                historyLength: adapter.length(),
                current: current,
                locations: [current],
            });

            adapter.change($.proxy(this, "_checkUrl"));
        },

        createAdapter:function(options) {
           return support.pushState && options.pushState ? new PushStateAdapter(options.root) : new HashAdapter();
        },

        stop: function() {
            if (!this._started) {
                return;
            }
            this.adapter.stop();
            this.unbind(CHANGE);
            this._started = false;
        },

        change: function(callback) {
            this.bind(CHANGE, callback);
        },

        replace: function(to, silent) {

            this._navigate(to, silent, function(adapter) {
                adapter.replace(to);
                this.locations[this.locations - 1] = this.current;
            });
        },

        navigate: function(to, silent) {
            if (to === "#:back") {
                this.adapter.back();
                return;
            }

            this._navigate(to, silent, function(adapter) {
                adapter.navigate(to);
                this.locations.push(this.current);
            });
        },

        _navigate: function(to, silent, callback) {
            var adapter = this.adapter;

            to = to.replace(hashStrip, '');

            if (this.current === to || this.current === decodeURIComponent(to)) {
                this.trigger(SAME);
                return;
            }

            if (!silent) {
                if (this.trigger(CHANGE, { url: to })) {
                    return;
                }
            }

            this.current = adapter.normalize(to);

            callback.call(this, adapter);

            this.historyLength = adapter.length();
        },

        _checkUrl: function() {
            var adapter = this.adapter,
                current = adapter.current(),
                newLength = adapter.length(),
                navigatingInExisting = this.historyLength === newLength,
                back = current === this.locations[this.locations.length - 2] && navigatingInExisting,
                prev = this.current;

            if (this.current === current || this.current === decodeURIComponent(current)) {
                return true;
            }

            this.historyLength = newLength;

            this.current = current;

            if (back && this.trigger("back", { url: prev, to: current })) {
                adapter.forward();
                this.current = prev;
                return;
            }

            if (this.trigger(CHANGE, { url: current })) {
                if (back) {
                    adapter.forward();
                } else {
                    adapter.back();
                    this.historyLength --;
                }
                this.current = prev;
                return;
            }

            if (back) {
                this.locations.pop();
            } else {
                this.locations.push(current);
            }
        }
    });

    kendo.History = History;
    kendo.History.HistoryAdapter = HistoryAdapter;
    kendo.History.HashAdapter = HashAdapter;
    kendo.History.PushStateAdapter = PushStateAdapter;
    kendo.absoluteURL = absoluteURL;
    kendo.history = new History();
})(window.kendo.jQuery);

(function() {
    var kendo = window.kendo,
        history = kendo.history,
        Observable = kendo.Observable,
        INIT = "init",
        ROUTE_MISSING = "routeMissing",
        CHANGE = "change",
        BACK = "back",
        SAME = "same",
        optionalParam = /\((.*?)\)/g,
        namedParam = /(\(\?)?:\w+/g,
        splatParam = /\*\w+/g,
        escapeRegExp = /[\-{}\[\]+?.,\\\^$|#\s]/g;

    function namedParamReplace(match, optional) {
        return optional ? match : '([^\/]+)';
    }

    function routeToRegExp(route) {
        return new RegExp('^' + route
            .replace(escapeRegExp, '\\$&')
            .replace(optionalParam, '(?:$1)?')
            .replace(namedParam, namedParamReplace)
            .replace(splatParam, '(.*?)') + '$');
    }

    function stripUrl(url) {
        return url.replace(/(\?.*)|(#.*)/g, "");
    }

    var Route = kendo.Class.extend({
        init: function(route, callback) {
            if (!(route instanceof RegExp)) {
                route = routeToRegExp(route);
            }

            this.route = route;
            this._callback = callback;
        },

        callback: function(url) {
            var params,
                idx = 0,
                length,
                queryStringParams = kendo.parseQueryStringParams(url);

            url = stripUrl(url);
            params = this.route.exec(url).slice(1);
            length = params.length;

            for (; idx < length; idx ++) {
                if (typeof params[idx] !== 'undefined') {
                    params[idx] = decodeURIComponent(params[idx]);
                }
            }

            params.push(queryStringParams);

            this._callback.apply(null, params);
        },

        worksWith: function(url) {
            if (this.route.test(stripUrl(url))) {
                this.callback(url);
                return true;
            } else {
                return false;
            }
        }
    });

    var Router = Observable.extend({
        init: function(options) {
            Observable.fn.init.call(this);
            this.routes = [];
            this.pushState = options ? options.pushState : false;
            if (options && options.root) {
                this.root = options.root;
            }
            this.bind([INIT, ROUTE_MISSING, CHANGE, SAME], options);
        },

        destroy: function() {
            history.unbind(CHANGE, this._urlChangedProxy);
            history.unbind(SAME, this._sameProxy);
            history.unbind(BACK, this._backProxy);
            this.unbind();
        },

        start: function() {
            var that = this,
                sameProxy = function() { that._same(); },
                backProxy = function(e) { that._back(e); },
                urlChangedProxy = function(e) { that._urlChanged(e); };

            history.start({
                same: sameProxy,
                change: urlChangedProxy,
                back: backProxy,
                pushState: that.pushState,
                root: that.root
            });

            var initEventObject = { url: history.current || "/", preventDefault: $.noop };

            if (!that.trigger(INIT, initEventObject)) {
                that._urlChanged(initEventObject);
            }

            this._urlChangedProxy = urlChangedProxy;
            this._backProxy = backProxy;
        },

        route: function(route, callback) {
            this.routes.push(new Route(route, callback));
        },

        navigate: function(url, silent) {
            kendo.history.navigate(url, silent);
        },

        replace: function(url, silent) {
            kendo.history.replace(url, silent);
        },

        _back: function(e) {
            if (this.trigger(BACK, { url: e.url, to: e.to })) {
                e.preventDefault();
            }
        },

        _same: function(e) {
            this.trigger(SAME);
        },

        _urlChanged: function(e) {
            var url = e.url;

            if (!url) {
                url = "/";
            }

            if (this.trigger(CHANGE, { url: e.url, params: kendo.parseQueryStringParams(e.url) })) {
                e.preventDefault();
                return;
            }

            var idx = 0,
                routes = this.routes,
                route,
                length = routes.length;

            for (; idx < length; idx ++) {
                 route = routes[idx];

                 if (route.worksWith(url)) {
                    return;
                 }
            }

            if (this.trigger(ROUTE_MISSING, { url: url, params: kendo.parseQueryStringParams(url) })) {
                e.preventDefault();
            }
        }
    });

    kendo.Router = Router;
})();

(function($, undefined) {
    var kendo = window.kendo,
        Observable = kendo.Observable,
        SCRIPT = "SCRIPT",
        INIT = "init",
        SHOW = "show",
        HIDE = "hide",

        ATTACH = "attach",
        DETACH = "detach",
        sizzleErrorRegExp = /unrecognized expression/;

    var View = Observable.extend({
        init: function(content, options) {
            var that = this;
            options = options || {};

            Observable.fn.init.call(that);
            that.content = content;
            that.id = kendo.guid();
            that.tagName = options.tagName || "div";
            that.model = options.model;
            that._wrap = options.wrap !== false;
            that._fragments = {};

            that.bind([ INIT, SHOW, HIDE ], options);
        },

        render: function(container) {
            var that = this,
                notInitialized = !that.element;

            // The order below matters - kendo.bind should happen when the element is in the DOM, and show should be triggered after init.

            if (notInitialized) {
                that.element = that._createElement();
            }

            if (container) {
                $(container).append(that.element);
            }

            if (notInitialized) {
                kendo.bind(that.element, that.model);
                that.trigger(INIT);
            }

            if (container) {
                that._eachFragment(ATTACH);
                that.trigger(SHOW);
            }

            return that.element;
        },

        clone: function(back) {
            return new ViewClone(this);
        },

        triggerBeforeShow: function() {
            return true;
        },

        showStart: function() {
            this.element.css("display", "");
        },

        showEnd: function() {

        },

        hideStart: function() {

        },

        hideEnd: function() {
            this.hide();
        },

        hide: function() {
            this._eachFragment(DETACH);
            this.element.detach();
            this.trigger(HIDE);
        },

        destroy: function() {
            var element = this.element;

            if (element) {
                kendo.unbind(element);
                kendo.destroy(element);
                element.remove();
            }
        },

        fragments: function(fragments) {
            $.extend(this._fragments, fragments);
        },

        _eachFragment: function(methodName) {
            for (var placeholder in this._fragments) {
                this._fragments[placeholder][methodName](this, placeholder);
            }
        },

        _createElement: function() {
            var that = this,
                wrap = that._wrap,
                wrapper = "<" + that.tagName + " />",
                element,
                content;

            try {
                content = $(document.getElementById(that.content) || that.content); // support passing id without #

                if (content[0].tagName === SCRIPT) {
                    content = content.html();
                }
            } catch(e) {
                if (sizzleErrorRegExp.test(e.message)) {
                    content = that.content;
                }
            }

            if (typeof content === "string") {
                element = $(wrapper).append(content);
                kendo.stripWhitespace(element[0]);
                // drop the wrapper if asked - this seems like the easiest (although not very intuitive) way to avoid messing up templates with questionable content, like the one below
                // <script id="my-template">
                // foo
                // <span> Span </span>
                // </script>
                if (!wrap) {
                   element = element.contents();
                }
            } else {
                element = content;
                if (wrap) {
                    element = element.wrap(wrapper).parent();
                }
            }

            return element;
        }
    });

    var ViewClone = kendo.Class.extend({
        init: function(view) {
            $.extend(this, {
                element: view.element.clone(true),
                transition: view.transition,
                id: view.id
            });

            view.element.parent().append(this.element);
        },

        hideStart: $.noop,

        hideEnd: function() {
            this.element.remove();
        }
    });

    var Layout = View.extend({
        init: function(content, options) {
            View.fn.init.call(this, content, options);
            this.containers = {};
        },

        container: function(selector) {
            var container = this.containers[selector];

            if (!container) {
                container = this._createContainer(selector);
                this.containers[selector] = container;
            }

            return container;
        },

        showIn: function(selector, view, transition) {
            this.container(selector).show(view, transition);
        },

        _createContainer: function(selector) {
            var element = this.render().find(selector),
                container = new ViewContainer(element);

            container.bind("accepted", function(e) {
                element.append(e.view.render());
            });

            return container;
        }
    });

    var Fragment = View.extend({
        attach: function(view, placeholder) {
            view.element.find(placeholder).replaceWith(this.render());
        },

        detach: function() {
            console.log('detach', arguments);
        },
    });

    var transitionRegExp = /^(\w+)(:(\w+))?( (\w+))?$/;

    function parseTransition(transition) {
        if (!transition){
            return {};
        }

        var matches = transition.match(transitionRegExp) || [];

        return {
            type: matches[1],
            direction: matches[3],
            reverse: matches[5] === "reverse"
        };
    }

    var ViewContainer = Observable.extend({
        init: function(container) {
            Observable.fn.init.call(this);
            this.container = container;
            this.history = [];
            this.view = null;
            this.running = false;
        },

        after: function() {
            this.running = false;
            this.trigger("complete", {view: this.view});
            this.trigger("after");
        },

        end: function() {
            this.view.showEnd();
            this.previous.hideEnd();
            this.after();
        },

        show: function(view, transition, locationID) {
            if (!view.triggerBeforeShow()) {
                this.trigger("after");
                return false;
            }

            locationID = locationID || view.id;

            var that = this,
                current = (view === that.view) ? view.clone() : that.view,
                history = that.history,
                previousEntry = history[history.length - 2] || {},
                back = previousEntry.id === locationID,
                // If explicit transition is set, it will be with highest priority
                // Next we will try using the history record transition or the view transition configuration
                theTransition = transition || ( back ? history[history.length - 1].transition : view.transition ),
                transitionData = parseTransition(theTransition);

            if (that.running) {
                that.effect.stop();
            }

            if (theTransition === "none") {
                theTransition = null;
            }

            that.trigger("accepted", { view: view });
            that.view = view;
            that.previous = current;
            that.running = true;

            if (!back) {
                history.push({ id: locationID, transition: theTransition });
            } else {
                history.pop();
            }

            if (!current) {
                view.showStart();
                view.showEnd();
                that.after();
                return true;
            }

            current.hideStart();
            view.showStart();

            if (!theTransition || !kendo.effects.enabled) {
                that.end();
            } else {
                // do not reverse the explicit transition
                if (back && !transition) {
                    transitionData.reverse = !transitionData.reverse;
                }

                that.effect = kendo.fx(view.element).replace(current.element, transitionData.type)
                    .direction(transitionData.direction)
                    .setReverse(transitionData.reverse);

                that.effect.run().then(function() { that.end(); });
            }

            return true;
        }
    });

    kendo.ViewContainer = ViewContainer;
    kendo.Fragment = Fragment;
    kendo.Layout = Layout;
    kendo.View = View;
    kendo.ViewClone = ViewClone;

})(window.kendo.jQuery);

(function ($, undefined) {
    var kendo = window.kendo,
        support = kendo.support,
        document = window.document,
        Class = kendo.Class,
        Observable = kendo.Observable,
        now = $.now,
        extend = $.extend,
        OS = support.mobileOS,
        invalidZeroEvents = OS && OS.android,
        DEFAULT_MIN_HOLD = 800,
        DEFAULT_THRESHOLD = support.browser.msie ? 5 : 0, // WP8 and W8 are very sensitive and always report move.

        // UserEvents events
        PRESS = "press",
        HOLD = "hold",
        SELECT = "select",
        START = "start",
        MOVE = "move",
        END = "end",
        CANCEL = "cancel",
        TAP = "tap",
        RELEASE = "release",
        GESTURESTART = "gesturestart",
        GESTURECHANGE = "gesturechange",
        GESTUREEND = "gestureend",
        GESTURETAP = "gesturetap";

    function touchDelta(touch1, touch2) {
        var x1 = touch1.x.location,
            y1 = touch1.y.location,
            x2 = touch2.x.location,
            y2 = touch2.y.location,
            dx = x1 - x2,
            dy = y1 - y2;

        return {
            center: {
               x: (x1 + x2) / 2,
               y: (y1 + y2) / 2
            },

            distance: Math.sqrt(dx*dx + dy*dy)
        };
    }

    function getTouches(e) {
        var touches = [],
            originalEvent = e.originalEvent,
            currentTarget = e.currentTarget,
            idx = 0, length,
            changedTouches,
            touch;

        if (e.api) {
            touches.push({
                id: 2,  // hardcoded ID for API call;
                event: e,
                target: e.target,
                currentTarget: e.target,
                location: e
            });
        }
        else if (e.type.match(/touch/)) {
            changedTouches = originalEvent ? originalEvent.changedTouches : [];
            for (length = changedTouches.length; idx < length; idx ++) {
                touch = changedTouches[idx];
                touches.push({
                    location: touch,
                    event: e,
                    target: touch.target,
                    currentTarget: currentTarget,
                    id: touch.identifier
                });
            }
        }
        else if (support.pointers || support.msPointers) {
            touches.push({
                location: originalEvent,
                event: e,
                target: e.target,
                currentTarget: currentTarget,
                id: originalEvent.pointerId
            });
        } else {
            touches.push({
                id: 1, // hardcoded ID for mouse event;
                event: e,
                target: e.target,
                currentTarget: currentTarget,
                location: e
            });
        }

        return touches;
    }

    var TouchAxis = Class.extend({
        init: function(axis, location) {
            var that = this;

            that.axis = axis;

            that._updateLocationData(location);

            that.startLocation = that.location;
            that.velocity = that.delta = 0;
            that.timeStamp = now();
        },

        move: function(location) {
            var that = this,
                offset = location["page" + that.axis],
                timeStamp = now(),
                timeDelta = (timeStamp - that.timeStamp) || 1; // Firing manually events in tests can make this 0;

            if (!offset && invalidZeroEvents) {
                return;
            }

            that.delta = offset - that.location;

            that._updateLocationData(location);

            that.initialDelta = offset - that.startLocation;
            that.velocity = that.delta / timeDelta;
            that.timeStamp = timeStamp;
        },

        _updateLocationData: function(location) {
            var that = this, axis = that.axis;

            that.location = location["page" + axis];
            that.client = location["client" + axis];
            that.screen = location["screen" + axis];
        }
    });

    var Touch = Class.extend({
        init: function(userEvents, target, touchInfo) {
            extend(this, {
                x: new TouchAxis("X", touchInfo.location),
                y: new TouchAxis("Y", touchInfo.location),
                userEvents: userEvents,
                target: target,
                currentTarget: touchInfo.currentTarget,
                initialTouch: touchInfo.target,
                id: touchInfo.id,
                pressEvent: touchInfo,
                _moved: false,
                _finished: false
            });
        },

        press: function() {
            this._holdTimeout = setTimeout($.proxy(this, "_hold"), this.userEvents.minHold);
            this._trigger(PRESS, this.pressEvent);
        },

        _hold: function() {
            this._trigger(HOLD, this.pressEvent);
        },

        move: function(touchInfo) {
            var that = this;

            if (that._finished) { return; }

            that.x.move(touchInfo.location);
            that.y.move(touchInfo.location);

            if (!that._moved) {
                if (that._withinIgnoreThreshold()) {
                    return;
                }

                if (!UserEvents.current || UserEvents.current === that.userEvents) {
                    that._start(touchInfo);
                } else {
                    return that.dispose();
                }
            }

            // Event handlers may cancel the drag in the START event handler, hence the double check for pressed.
            if (!that._finished) {
                that._trigger(MOVE, touchInfo);
            }
        },

        end: function(touchInfo) {
            var that = this;

            that.endTime = now();

            if (that._finished) { return; }

            // Mark the object as finished if there are blocking operations in the event handlers (alert/confirm)
            that._finished = true;

            if (that._moved) {
                that._trigger(END, touchInfo);
            } else {
                that._trigger(TAP, touchInfo);
            }

            clearTimeout(that._holdTimeout);
            that._trigger(RELEASE, touchInfo);

            that.dispose();
        },

        dispose: function() {
            var userEvents = this.userEvents,
                activeTouches = userEvents.touches;

            this._finished = true;
            this.pressEvent = null;
            clearTimeout(this._holdTimeout);

            activeTouches.splice($.inArray(this, activeTouches), 1);
        },

        skip: function() {
            this.dispose();
        },

        cancel: function() {
            this.dispose();
        },

        isMoved: function() {
            return this._moved;
        },

        _start: function(touchInfo) {
            clearTimeout(this._holdTimeout);

            this.startTime = now();
            this._moved = true;
            this._trigger(START, touchInfo);
        },

        _trigger: function(name, touchInfo) {
            var that = this,
                jQueryEvent = touchInfo.event,
                data = {
                    touch: that,
                    x: that.x,
                    y: that.y,
                    target: that.target,
                    event: jQueryEvent
                };

            if(that.userEvents.notify(name, data)) {
                jQueryEvent.preventDefault();
            }
        },

        _withinIgnoreThreshold: function() {
            var xDelta = this.x.initialDelta,
                yDelta = this.y.initialDelta;

            return Math.sqrt(xDelta * xDelta + yDelta * yDelta) <= this.userEvents.threshold;
        }
    });

    function preventTrigger(e) {
        e.preventDefault();

        var target = $(e.data.root),   // Determine the correct parent to receive the event and bubble.
            parent = target.closest(".k-widget").parent();

        if (!parent[0]) {
            parent = target.parent();
        }

        var fakeEventData = $.extend(true, {}, e, { target: target[0] });
        parent.trigger($.Event(e.type, fakeEventData));
    }

    function withEachUpEvent(callback) {
        var downEvents = kendo.eventMap.up.split(" "),
            idx = 0,
            length = downEvents.length;

        for(; idx < length; idx ++) {
            callback(downEvents[idx]);
        }
    }

    var UserEvents = Observable.extend({
        init: function(element, options) {
            var that = this,
                filter,
                ns = kendo.guid();

            options = options || {};
            filter = that.filter = options.filter;
            that.threshold = options.threshold || DEFAULT_THRESHOLD;
            that.minHold = options.minHold || DEFAULT_MIN_HOLD;
            that.touches = [];
            that._maxTouches = options.multiTouch ? 2 : 1;
            that.allowSelection = options.allowSelection;
            that.captureUpIfMoved = options.captureUpIfMoved;
            that.eventNS = ns;

            element = $(element).handler(that);
            Observable.fn.init.call(that);

            extend(that, {
                element: element,
                surface: options.global ? $(document.documentElement) : $(options.surface || element),
                stopPropagation: options.stopPropagation,
                pressed: false
            });

            that.surface.handler(that)
                .on(kendo.applyEventMap("move", ns), "_move")
                .on(kendo.applyEventMap("up cancel", ns), "_end");

            element.on(kendo.applyEventMap("down", ns), filter, "_start");

            if (support.pointers || support.msPointers) {
                element.css("-ms-touch-action", "pinch-zoom double-tap-zoom");
            }

            if (options.preventDragEvent) {
                element.on(kendo.applyEventMap("dragstart", ns), kendo.preventDefault);
            }

            element.on(kendo.applyEventMap("mousedown selectstart", ns), filter, { root: element }, "_select");

            if (that.captureUpIfMoved && support.eventCapture) {
                var surfaceElement = that.surface[0],
                    preventIfMovingProxy = $.proxy(that.preventIfMoving, that);

                withEachUpEvent(function(eventName) {
                    surfaceElement.addEventListener(eventName, preventIfMovingProxy, true);
                });
            }

            that.bind([
            PRESS,
            HOLD,
            TAP,
            START,
            MOVE,
            END,
            RELEASE,
            CANCEL,
            GESTURESTART,
            GESTURECHANGE,
            GESTUREEND,
            GESTURETAP,
            SELECT
            ], options);
        },

        preventIfMoving: function(e) {
            if (this._isMoved()) {
                e.preventDefault();
            }
        },

        destroy: function() {
            var that = this;

            if (that._destroyed) {
                return;
            }

            that._destroyed = true;

            if (that.captureUpIfMoved && support.eventCapture) {
                var surfaceElement = that.surface[0];
                withEachUpEvent(function(eventName) {
                    surfaceElement.removeEventListener(eventName, that.preventIfMoving);
                });
            }

            that.element.kendoDestroy(that.eventNS);
            that.surface.kendoDestroy(that.eventNS);
            that.element.removeData("handler");
            that.surface.removeData("handler");
            that._disposeAll();

            that.unbind();
            delete that.surface;
            delete that.element;
            delete that.currentTarget;
        },

        capture: function() {
            UserEvents.current = this;
        },

        cancel: function() {
            this._disposeAll();
            this.trigger(CANCEL);
        },

        notify: function(eventName, data) {
            var that = this,
                touches = that.touches;

            if (this._isMultiTouch()) {
                switch(eventName) {
                    case MOVE:
                        eventName = GESTURECHANGE;
                        break;
                    case END:
                        eventName = GESTUREEND;
                        break;
                    case TAP:
                        eventName = GESTURETAP;
                        break;
                }

                extend(data, {touches: touches}, touchDelta(touches[0], touches[1]));
            }

            return this.trigger(eventName, data);
        },

        // API
        press: function(x, y, target) {
            this._apiCall("_start", x, y, target);
        },

        move: function(x, y) {
            this._apiCall("_move", x, y);
        },

        end: function(x, y) {
            this._apiCall("_end", x, y);
        },

        _isMultiTouch: function() {
            return this.touches.length > 1;
        },

        _maxTouchesReached: function() {
            return this.touches.length >= this._maxTouches;
        },

        _disposeAll: function() {
            var touches = this.touches;
            while (touches.length > 0) {
                touches.pop().dispose();
            }
        },

        _isMoved: function() {
            return $.grep(this.touches, function(touch) {
                return touch.isMoved();
            }).length;
        },

        _select: function(e) {
           if (!this.allowSelection || this.trigger(SELECT, { event: e })) {
                preventTrigger(e);
           }
        },

        _start: function(e) {
            var that = this,
                idx = 0,
                filter = that.filter,
                target,
                touches = getTouches(e),
                length = touches.length,
                touch,
                which = e.which;

            if (which && which > 1){
                return;
            }

            if (that._maxTouchesReached()) {
                return;
            }

            UserEvents.current = null;

            that.currentTarget = e.currentTarget;

            if (that.stopPropagation) {
                e.stopPropagation();
            }

            for (; idx < length; idx ++) {
                if (that._maxTouchesReached()) {
                    break;
                }

                touch = touches[idx];

                if (filter) {
                    target = $(touch.currentTarget); // target.is(filter) ? target : target.closest(filter, that.element);
                } else {
                    target = that.element;
                }

                if (!target.length) {
                    continue;
                }

                touch = new Touch(that, target, touch);
                that.touches.push(touch);
                touch.press();

                if (that._isMultiTouch()) {
                    that.notify("gesturestart", {});
                }
            }
        },

        _move: function(e) {
            this._eachTouch("move", e);
        },

        _end: function(e) {
            this._eachTouch("end", e);
        },

        _eachTouch: function(methodName, e) {
            var that = this,
                dict = {},
                touches = getTouches(e),
                activeTouches = that.touches,
                idx,
                touch,
                touchInfo,
                matchingTouch;

            for (idx = 0; idx < activeTouches.length; idx ++) {
                touch = activeTouches[idx];
                dict[touch.id] = touch;
            }

            for (idx = 0; idx < touches.length; idx ++) {
                touchInfo = touches[idx];
                matchingTouch = dict[touchInfo.id];

                if (matchingTouch) {
                    matchingTouch[methodName](touchInfo);
                }
            }
        },

        _apiCall: function(type, x, y, target) {
            this[type]({
                api: true,
                pageX: x,
                pageY: y,
                clientX: x,
                clientY: y,
                target: $(target || this.element)[0],
                stopPropagation: $.noop,
                preventDefault: $.noop
            });
        }
    });

    UserEvents.minHold = function(value) {
        DEFAULT_MIN_HOLD = value;
    };

    kendo.getTouches = getTouches;
    kendo.touchDelta = touchDelta;
    kendo.UserEvents = UserEvents;
 })(window.kendo.jQuery);

(function ($, undefined) {
    var kendo = window.kendo,
        support = kendo.support,
        document = window.document,
        Class = kendo.Class,
        Widget = kendo.ui.Widget,
        Observable = kendo.Observable,
        UserEvents = kendo.UserEvents,
        proxy = $.proxy,
        extend = $.extend,
        getOffset = kendo.getOffset,
        draggables = {},
        dropTargets = {},
        dropAreas = {},
        lastDropTarget,
        elementUnderCursor = kendo.elementUnderCursor,
        KEYUP = "keyup",
        CHANGE = "change",

        // Draggable events
        DRAGSTART = "dragstart",
        HOLD = "hold",
        DRAG = "drag",
        DRAGEND = "dragend",
        DRAGCANCEL = "dragcancel",

        // DropTarget events
        DRAGENTER = "dragenter",
        DRAGLEAVE = "dragleave",
        DROP = "drop";

    function contains(parent, child) {
        try {
            return $.contains(parent, child) || parent == child;
        } catch (e) {
            return false;
        }
    }

    function numericCssPropery(element, property) {
        return parseInt(element.css(property), 10) || 0;
    }

    function within(value, range) {
        return Math.min(Math.max(value, range.min), range.max);
    }

    function containerBoundaries(container, element) {
        var offset = getOffset(container),
            minX = offset.left + numericCssPropery(container, "borderLeftWidth") + numericCssPropery(container, "paddingLeft"),
            minY = offset.top + numericCssPropery(container, "borderTopWidth") + numericCssPropery(container, "paddingTop"),
            maxX = minX + container.width() - element.outerWidth(true),
            maxY = minY + container.height() - element.outerHeight(true);

        return {
            x: { min: minX, max: maxX },
            y: { min: minY, max: maxY }
        };
    }

    function checkTarget(target, targets, areas) {
        var theTarget, theFilter, i = 0,
            targetLen = targets && targets.length,
            areaLen = areas && areas.length;

        while (target && target.parentNode) {
            for (i = 0; i < targetLen; i ++) {
                theTarget = targets[i];
                if (theTarget.element[0] === target) {
                    return { target: theTarget, targetElement: target };
                }
            }

            for (i = 0; i < areaLen; i ++) {
                theFilter = areas[i];
                if (support.matchesSelector.call(target, theFilter.options.filter)) {
                    return { target: theFilter, targetElement: target };
                }
            }

            target = target.parentNode;
        }

        return undefined;
    }

    var TapCapture = Observable.extend({
        init: function(element, options) {
            var that = this,
                domElement = element[0];

            that.capture = false;

            if (domElement.addEventListener) {
                $.each(kendo.eventMap.down.split(" "), function() {
                    domElement.addEventListener(this, proxy(that._press, that), true);
                });
                $.each(kendo.eventMap.up.split(" "), function() {
                    domElement.addEventListener(this, proxy(that._release, that), true);
                });
            } else {
                $.each(kendo.eventMap.down.split(" "), function() {
                    domElement.attachEvent(this, proxy(that._press, that));
                });
                $.each(kendo.eventMap.up.split(" "), function() {
                    domElement.attachEvent(this, proxy(that._release, that));
                });
            }

            Observable.fn.init.call(that);

            that.bind(["press", "release"], options || {});
        },

        captureNext: function() {
            this.capture = true;
        },

        cancelCapture: function() {
            this.capture = false;
        },

        _press: function(e) {
            var that = this;
            that.trigger("press");
            if (that.capture) {
                e.preventDefault();
            }
        },

        _release: function(e) {
            var that = this;
            that.trigger("release");

            if (that.capture) {
                e.preventDefault();
                that.cancelCapture();
            }
        }
    });

    var PaneDimension = Observable.extend({
        init: function(options) {
            var that = this;
            Observable.fn.init.call(that);

            that.forcedEnabled = false;

            $.extend(that, options);

            that.scale = 1;

            if (that.horizontal) {
                that.measure = "offsetWidth";
                that.scrollSize = "scrollWidth";
                that.axis = "x";
            } else {
                that.measure = "offsetHeight";
                that.scrollSize = "scrollHeight";
                that.axis = "y";
            }
        },

        makeVirtual: function() {
            $.extend(this, {
                virtual: true,
                forcedEnabled: true,
                _virtualMin: 0,
                _virtualMax: 0
            });
        },

        virtualSize: function(min, max) {
            if (this._virtualMin !== min || this._virtualMax !== max) {
                this._virtualMin = min;
                this._virtualMax = max;
                this.update();
            }
        },

        outOfBounds: function(offset) {
            return  offset > this.max || offset < this.min;
        },

        forceEnabled: function() {
            this.forcedEnabled = true;
        },

        getSize: function() {
            return this.container[0][this.measure];
        },

        getTotal: function() {
            return this.element[0][this.scrollSize];
        },

        rescale: function(scale) {
            this.scale = scale;
        },

        update: function(silent) {
            var that = this,
                total = that.virtual ? that._virtualMax : that.getTotal(),
                scaledTotal = total * that.scale,
                size = that.getSize();

            that.max = that.virtual ? -that._virtualMin : 0;
            that.size = size;
            that.total = scaledTotal;
            that.min = Math.min(that.max, size - scaledTotal);
            that.minScale = size / total;
            that.centerOffset = (scaledTotal - size) / 2;

            that.enabled = that.forcedEnabled || (scaledTotal > size);

            if (!silent) {
                that.trigger(CHANGE, that);
            }
        }
    });

    var PaneDimensions = Observable.extend({
        init: function(options) {
            var that = this;

            Observable.fn.init.call(that);

            that.x = new PaneDimension(extend({horizontal: true}, options));
            that.y = new PaneDimension(extend({horizontal: false}, options));
            that.container = options.container;
            that.forcedMinScale = options.minScale;
            that.maxScale = options.maxScale || 100;

            that.bind(CHANGE, options);
        },

        rescale: function(newScale) {
            this.x.rescale(newScale);
            this.y.rescale(newScale);
            this.refresh();
        },

        centerCoordinates: function() {
            return { x: Math.min(0, -this.x.centerOffset), y: Math.min(0, -this.y.centerOffset) };
        },

        refresh: function() {
            var that = this;
            that.x.update();
            that.y.update();
            that.enabled = that.x.enabled || that.y.enabled;
            that.minScale = that.forcedMinScale || Math.min(that.x.minScale, that.y.minScale);
            that.fitScale = Math.max(that.x.minScale, that.y.minScale);
            that.trigger(CHANGE);
        }
    });

    var PaneAxis = Observable.extend({
        init: function(options) {
            var that = this;
            extend(that, options);
            Observable.fn.init.call(that);
        },

        dragMove: function(delta) {
            var that = this,
                dimension = that.dimension,
                axis = that.axis,
                movable = that.movable,
                position = movable[axis] + delta;

            if (!dimension.enabled) {
                return;
            }

            if ((position < dimension.min && delta < 0) || (position > dimension.max && delta > 0)) {
                delta *= that.resistance;
            }

            movable.translateAxis(axis, delta);
            that.trigger(CHANGE, that);
        }
    });

    var Pane = Class.extend({

        init: function(options) {
            var that = this,
                x,
                y,
                resistance,
                movable;

            extend(that, {elastic: true}, options);

            resistance = that.elastic ? 0.5 : 0;
            movable = that.movable;

            that.x = x = new PaneAxis({
                axis: "x",
                dimension: that.dimensions.x,
                resistance: resistance,
                movable: movable
            });

            that.y = y = new PaneAxis({
                axis: "y",
                dimension: that.dimensions.y,
                resistance: resistance,
                movable: movable
            });

            that.userEvents.bind(["move", "end", "gesturestart", "gesturechange"], {
                gesturestart: function(e) {
                    that.gesture = e;
                    that.offset = that.dimensions.container.offset();
                },

                gesturechange: function(e) {
                    var previousGesture = that.gesture,
                        previousCenter = previousGesture.center,

                        center = e.center,

                        scaleDelta = e.distance / previousGesture.distance,

                        minScale = that.dimensions.minScale,
                        maxScale = that.dimensions.maxScale,
                        coordinates;

                    if (movable.scale <= minScale && scaleDelta < 1) {
                        // Resist shrinking. Instead of shrinking from 1 to 0.5, it will shrink to 0.5 + (1 /* minScale */ - 0.5) * 0.8 = 0.9;
                        scaleDelta += (1 - scaleDelta) * 0.8;
                    }

                    if (movable.scale * scaleDelta >= maxScale) {
                        scaleDelta = maxScale / movable.scale;
                    }

                    var offsetX = movable.x + that.offset.left,
                        offsetY = movable.y + that.offset.top;

                    coordinates = {
                        x: (offsetX - previousCenter.x) * scaleDelta + center.x - offsetX,
                        y: (offsetY - previousCenter.y) * scaleDelta + center.y - offsetY
                    };

                    movable.scaleWith(scaleDelta);

                    x.dragMove(coordinates.x);
                    y.dragMove(coordinates.y);

                    that.dimensions.rescale(movable.scale);
                    that.gesture = e;
                    e.preventDefault();
                },

                move: function(e) {
                    if (e.event.target.tagName.match(/textarea|input/i)) {
                        return;
                    }

                    if (x.dimension.enabled || y.dimension.enabled) {
                        x.dragMove(e.x.delta);
                        y.dragMove(e.y.delta);
                        e.preventDefault();
                    } else {
                        e.touch.skip();
                    }
                },

                end: function(e) {
                    e.preventDefault();
                }
            });
        }
    });

    var TRANSFORM_STYLE = support.transitions.prefix + "Transform",
        translate;


    if (support.hasHW3D) {
        translate = function(x, y, scale) {
            return "translate3d(" + x + "px," + y +"px,0) scale(" + scale + ")";
        };
    } else {
        translate = function(x, y, scale) {
            return "translate(" + x + "px," + y +"px) scale(" + scale + ")";
        };
    }

    var Movable = Observable.extend({
        init: function(element) {
            var that = this;

            Observable.fn.init.call(that);

            that.element = $(element);
            that.element[0].style.webkitTransformOrigin = "left top";
            that.x = 0;
            that.y = 0;
            that.scale = 1;
            that._saveCoordinates(translate(that.x, that.y, that.scale));
        },

        translateAxis: function(axis, by) {
            this[axis] += by;
            this.refresh();
        },

        scaleTo: function(scale) {
            this.scale = scale;
            this.refresh();
        },

        scaleWith: function(scaleDelta) {
            this.scale *= scaleDelta;
            this.refresh();
        },

        translate: function(coordinates) {
            this.x += coordinates.x;
            this.y += coordinates.y;
            this.refresh();
        },

        moveAxis: function(axis, value) {
            this[axis] = value;
            this.refresh();
        },

        moveTo: function(coordinates) {
            extend(this, coordinates);
            this.refresh();
        },

        refresh: function() {
            var that = this,
                x = that.x,
                y = that.y,
                newCoordinates;

            if (that.round) {
                x = Math.round(x);
                y = Math.round(y);
            }

            newCoordinates = translate(x, y, that.scale);

            if (newCoordinates != that.coordinates) {
                if (kendo.support.browser.msie && kendo.support.browser.version < 10) {
                    that.element[0].style.position = "absolute";
                    that.element[0].style.left = that.x + "px";
                    that.element[0].style.top = that.y + "px";
                } else {
                    that.element[0].style[TRANSFORM_STYLE] = newCoordinates;
                }
                that._saveCoordinates(newCoordinates);
                that.trigger(CHANGE);
            }
        },

        _saveCoordinates: function(coordinates) {
            this.coordinates = coordinates;
        }
    });

    var DropTarget = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            var group = that.options.group;

            if (!(group in dropTargets)) {
                dropTargets[group] = [ that ];
            } else {
                dropTargets[group].push( that );
            }
        },

        events: [
            DRAGENTER,
            DRAGLEAVE,
            DROP
        ],

        options: {
            name: "DropTarget",
            group: "default"
        },

        destroy: function() {
            var groupName = this.options.group,
                group = dropTargets[groupName] || dropAreas[groupName],
                i;

            if (group.length > 1) {
                Widget.fn.destroy.call(this);

                for (i = 0; i < group.length; i++) {
                    if (group[i] == this) {
                        group.splice(i, 1);
                        break;
                    }
                }
            } else {
                DropTarget.destroyGroup(groupName);
            }
        },

        _trigger: function(eventName, e) {
            var that = this,
                draggable = draggables[that.options.group];

            if (draggable) {
                return that.trigger(eventName, extend({}, e.event, {
                           draggable: draggable,
                           dropTarget: e.dropTarget
                       }));
            }
        },

        _over: function(e) {
            this._trigger(DRAGENTER, e);
        },

        _out: function(e) {
            this._trigger(DRAGLEAVE, e);
        },

        _drop: function(e) {
            var that = this,
                draggable = draggables[that.options.group];

            if (draggable) {
                draggable.dropped = !that._trigger(DROP, e);
            }
        }
    });

    DropTarget.destroyGroup = function(groupName) {
        var group = dropTargets[groupName] || dropAreas[groupName],
            i;

        if (group) {
            for (i = 0; i < group.length; i++) {
                Widget.fn.destroy.call(group[i]);
            }

            group.length = 0;
            delete dropTargets[groupName];
            delete dropAreas[groupName];
        }
    };

    DropTarget._cache = dropTargets;

    var DropTargetArea = DropTarget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            var group = that.options.group;

            if (!(group in dropAreas)) {
                dropAreas[group] = [ that ];
            } else {
                dropAreas[group].push( that );
            }
        },

        options: {
            name: "DropTargetArea",
            group: "default",
            filter: null
        }
    });

    var Draggable = Widget.extend({
        init: function (element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            that._activated = false;

            that.userEvents = new UserEvents(that.element, {
                global: true,
                stopPropagation: true,
                filter: that.options.filter,
                threshold: that.options.distance,
                start: proxy(that._start, that),
                hold: proxy(that._hold, that),
                move: proxy(that._drag, that),
                end: proxy(that._end, that),
                cancel: proxy(that._cancel, that)
            });

            that._afterEndHandler = proxy(that._afterEnd, that);
            that.captureEscape = function(e) {
                if (e.keyCode === kendo.keys.ESC) {
                    that._trigger(DRAGCANCEL, {event: e});
                    that.userEvents.cancel();
                }
            };
        },

        events: [
            HOLD,
            DRAGSTART,
            DRAG,
            DRAGEND,
            DRAGCANCEL
        ],

        options: {
            name: "Draggable",
            distance: 5,
            group: "default",
            cursorOffset: null,
            axis: null,
            container: null,
            filter: null,
            ignore: null,
            holdToDrag: false,
            dropped: false
        },

        cancelHold: function() {
            this._activated = false;
        },

        _updateHint: function(e) {
            var that = this,
                coordinates,
                options = that.options,
                boundaries = that.boundaries,
                axis = options.axis,
                cursorOffset = that.options.cursorOffset;

            if (cursorOffset) {
               coordinates = { left: e.x.location + cursorOffset.left, top: e.y.location + cursorOffset.top };
            } else {
               that.hintOffset.left += e.x.delta;
               that.hintOffset.top += e.y.delta;
               coordinates = $.extend({}, that.hintOffset);
            }

            if (boundaries) {
                coordinates.top = within(coordinates.top, boundaries.y);
                coordinates.left = within(coordinates.left, boundaries.x);
            }

            if (axis === "x") {
                delete coordinates.top;
            } else if (axis === "y") {
                delete coordinates.left;
            }

            that.hint.css(coordinates);
        },

        _start: function(e) {
            var that = this,
                options = that.options,
                ignoreSelector = options.ignore,
                ignore = ignoreSelector && $(e.touch.initialTouch).is(ignoreSelector),
                container = options.container,
                hint = options.hint;

            if (ignore || (options.holdToDrag && !that._activated)) {
                that.userEvents.cancel();
                return;
            }

            that.currentTarget = e.target;
            that.currentTargetOffset = getOffset(that.currentTarget);

            if (hint) {
                if (that.hint) {
                    that.hint.stop(true, true).remove();
                }

                that.hint = kendo.isFunction(hint) ? $(hint.call(that, that.currentTarget)) : hint;

                var offset = getOffset(that.currentTarget);
                that.hintOffset = offset;

                that.hint.css( {
                    position: "absolute",
                    zIndex: 20000, // the Window's z-index is 10000 and can be raised because of z-stacking
                    left: offset.left,
                    top: offset.top
                })
                .appendTo(document.body);
            }

            draggables[options.group] = that;

            that.dropped = false;

            if (container) {
                that.boundaries = containerBoundaries(container, that.hint);
            }

            if (that._trigger(DRAGSTART, e)) {
                that.userEvents.cancel();
                that._afterEnd();
            }

            $(document).on(KEYUP, that.captureEscape);
        },

        _hold: function(e) {
            this.currentTarget = e.target;

            if (this._trigger(HOLD, e)) {
                this.userEvents.cancel();
            } else {
                this._activated = true;
            }
        },

        _drag: function(e) {
            var that = this;

            e.preventDefault();

            that._withDropTarget(e, function(target, targetElement) {
                if (!target) {
                    if (lastDropTarget) {
                        lastDropTarget._trigger(DRAGLEAVE, extend(e, { dropTarget: $(lastDropTarget.targetElement) }));
                        lastDropTarget = null;
                    }
                    return;
                }

                if (lastDropTarget) {
                    if (targetElement === lastDropTarget.targetElement) {
                        return;
                    }

                    lastDropTarget._trigger(DRAGLEAVE, extend(e, { dropTarget: $(lastDropTarget.targetElement) }));
                }

                target._trigger(DRAGENTER, extend(e, { dropTarget: $(targetElement) }));
                lastDropTarget = extend(target, { targetElement: targetElement });
            });

            that._trigger(DRAG, e);

            if (that.hint) {
                that._updateHint(e);
            }
        },

        _end: function(e) {
            var that = this;

            that._withDropTarget(e, function(target, targetElement) {
                if (target) {
                    target._drop(extend({}, e, { dropTarget: $(targetElement) }));
                    lastDropTarget = null;
                }
            });

            that._trigger(DRAGEND, e);
            that._cancel(e.event);
        },

        _cancel: function() {
            var that = this;

            that._activated = false;

            if (that.hint && !that.dropped) {
                setTimeout(function() {
                    that.hint.stop(true, true).animate(that.currentTargetOffset, "fast", that._afterEndHandler);
                }, 0);

            } else {
                that._afterEnd();
            }
        },

        _trigger: function(eventName, e) {
            var that = this;

            return that.trigger(
                eventName, extend(
                {},
                e.event,
                {
                    x: e.x,
                    y: e.y,
                    currentTarget: that.currentTarget,
                    dropTarget: e.dropTarget
                }
            ));
        },

        _withDropTarget: function(e, callback) {
            var that = this,
                target, result,
                options = that.options,
                targets = dropTargets[options.group],
                areas = dropAreas[options.group];

            if (targets && targets.length || areas && areas.length) {

                target = elementUnderCursor(e);

                if (that.hint && contains(that.hint[0], target)) {
                    that.hint.hide();
                    target = elementUnderCursor(e);
                    // IE8 does not return the element in iframe from first attempt
                    if (!target) {
                        target = elementUnderCursor(e);
                    }
                    that.hint.show();
                }

                result = checkTarget(target, targets, areas);

                if (result) {
                    callback(result.target, result.targetElement);
                } else {
                    callback();
                }
            }
        },

        destroy: function() {
            var that = this;

            Widget.fn.destroy.call(that);

            that._afterEnd();

            that.userEvents.destroy();

            that.currentTarget = null;
        },

        _afterEnd: function() {
            var that = this;

            if (that.hint) {
                that.hint.remove();
            }

            delete draggables[that.options.group];

            that.trigger("destroy");
            $(document).off(KEYUP, that.captureEscape);
        }
    });

    kendo.ui.plugin(DropTarget);
    kendo.ui.plugin(DropTargetArea);
    kendo.ui.plugin(Draggable);
    kendo.TapCapture = TapCapture;
    kendo.containerBoundaries = containerBoundaries;

    extend(kendo.ui, {
        Pane: Pane,
        PaneDimensions: PaneDimensions,
        Movable: Movable
    });

 })(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.ui,
        Widget = ui.Widget,
        support = kendo.support,
        getOffset = kendo.getOffset,
        activeElement = kendo._activeElement,
        OPEN = "open",
        CLOSE = "close",
        DEACTIVATE = "deactivate",
        ACTIVATE = "activate",
        CENTER = "center",
        LEFT = "left",
        RIGHT = "right",
        TOP = "top",
        BOTTOM = "bottom",
        ABSOLUTE = "absolute",
        HIDDEN = "hidden",
        BODY = "body",
        LOCATION = "location",
        POSITION = "position",
        VISIBLE = "visible",
        EFFECTS = "effects",
        ACTIVE = "k-state-active",
        ACTIVEBORDER = "k-state-border",
        ACTIVEBORDERREGEXP = /k-state-border-(\w+)/,
        ACTIVECHILDREN = ".k-picker-wrap, .k-dropdown-wrap, .k-link",
        MOUSEDOWN = "down",
        WINDOW = $(window),
        DOCUMENT_ELEMENT = $(document.documentElement),
        RESIZE_SCROLL = "resize scroll",
        cssPrefix = support.transitions.css,
        TRANSFORM = cssPrefix + "transform",
        extend = $.extend,
        NS = ".kendoPopup",
        styles = ["font-size",
                  "font-family",
                  "font-stretch",
                  "font-style",
                  "font-weight",
                  "line-height"];

    function contains(container, target) {
        return container === target || $.contains(container, target);
    }

    var Popup = Widget.extend({
        init: function(element, options) {
            var that = this, parentPopup;

            options = options || {};

            if (options.isRtl) {
                options.origin = options.origin || BOTTOM + " " + RIGHT;
                options.position = options.position || TOP + " " + RIGHT;
            }

            Widget.fn.init.call(that, element, options);

            element = that.element;
            options = that.options;

            that.collisions = options.collision ? options.collision.split(" ") : [];

            if (that.collisions.length === 1) {
                that.collisions.push(that.collisions[0]);
            }

            parentPopup = $(that.options.anchor).closest(".k-popup,.k-group").filter(":not([class^=km-])"); // When popup is in another popup, make it relative.
            options.appendTo = $($(options.appendTo)[0] || parentPopup[0] || BODY);

            that.element.hide()
                .addClass("k-popup k-group k-reset")
                .toggleClass("k-rtl", !!options.isRtl)
                .css({ position : ABSOLUTE })
                .appendTo(options.appendTo)
                .on("mouseenter" + NS, function() {
                    that._hovered = true;
                })
                .on("mouseleave" + NS, function() {
                    that._hovered = false;
                });

            that.wrapper = $();

            if (options.animation === false) {
                options.animation = { open: { effects: {} }, close: { hide: true, effects: {} } };
            }

            extend(options.animation.open, {
                complete: function() {
                    that.wrapper.css({ overflow: VISIBLE }); // Forcing refresh causes flickering in mobile.
                    that.trigger(ACTIVATE);
                }
            });

            extend(options.animation.close, {
                complete: function() {
                    that.wrapper.hide();

                    var location = that.wrapper.data(LOCATION),
                        anchor = $(options.anchor),
                        direction, dirClass;

                    if (location) {
                        that.wrapper.css(location);
                    }

                    if (options.anchor != BODY) {
                        direction = (anchor[0].className.match(ACTIVEBORDERREGEXP) || ["", "down"])[1];
                        dirClass = ACTIVEBORDER + "-" + direction;

                        anchor
                            .removeClass(dirClass)
                            .children(ACTIVECHILDREN)
                            .removeClass(ACTIVE)
                            .removeClass(dirClass);

                        element.removeClass(ACTIVEBORDER + "-" + kendo.directions[direction].reverse);
                    }

                    that._closing = false;
                    that.trigger(DEACTIVATE);
                }
            });

            that._mousedownProxy = function(e) {
                that._mousedown(e);
            };

            that._resizeProxy = function(e) {
                that._resize(e);
            };

            if (options.toggleTarget) {
                $(options.toggleTarget).on(options.toggleEvent + NS, $.proxy(that.toggle, that));
            }
        },

        events: [
            OPEN,
            ACTIVATE,
            CLOSE,
            DEACTIVATE
        ],

        options: {
            name: "Popup",
            toggleEvent: "click",
            origin: BOTTOM + " " + LEFT,
            position: TOP + " " + LEFT,
            anchor: BODY,
            collision: "flip fit",
            viewport: window,
            copyAnchorStyles: true,
            autosize: false,
            modal: false,
            animation: {
                open: {
                    effects: "slideIn:down",
                    transition: true,
                    duration: 200
                },
                close: { // if close animation effects are defined, they will be used instead of open.reverse
                    duration: 100,
                    hide: true
                }
            }
        },

        destroy: function() {
            var that = this,
                options = that.options,
                element = that.element.off(NS),
                parent;

            Widget.fn.destroy.call(that);

            if (options.toggleTarget) {
                $(options.toggleTarget).off(NS);
            }

            if (!options.modal) {
                DOCUMENT_ELEMENT.unbind(MOUSEDOWN, that._mousedownProxy);
                WINDOW.unbind(RESIZE_SCROLL, that._resizeProxy);
            }

            kendo.destroy(that.element.children());
            element.removeData();

            if (options.appendTo[0] === document.body) {
                parent = element.parent(".k-animation-container");

                if (parent[0]) {
                    parent.remove();
                } else {
                    element.remove();
                }
            }
        },

        open: function(x, y) {
            var that = this,
                fixed = { isFixed: !isNaN(parseInt(y,10)), x: x, y: y },
                element = that.element,
                options = that.options,
                direction = "down",
                animation, wrapper,
                anchor = $(options.anchor),
                mobile = element[0] && element.hasClass("km-widget");

            if (!that.visible()) {
                if (options.copyAnchorStyles) {
                    if (mobile && styles[0] == "font-size") {
                        styles.shift();
                    }
                    element.css(kendo.getComputedStyles(anchor[0], styles));
                }

                if (element.data("animating") || that.trigger(OPEN)) {
                    return;
                }

                if (!options.modal) {
                    DOCUMENT_ELEMENT.unbind(MOUSEDOWN, that._mousedownProxy)
                                .bind(MOUSEDOWN, that._mousedownProxy);

                    // this binding hangs iOS in editor
                    if (!(support.mobileOS.ios || support.mobileOS.android)) {
                        WINDOW.unbind(RESIZE_SCROLL, that._resizeProxy)
                              .bind(RESIZE_SCROLL, that._resizeProxy);
                    }
                }

                that.wrapper = wrapper = kendo.wrap(element, options.autosize)
                                        .css({
                                            overflow: HIDDEN,
                                            display: "block",
                                            position: ABSOLUTE
                                        });

                if (support.mobileOS.android) {
                    wrapper.add(anchor).css(TRANSFORM, "translatez(0)"); // Android is VERY slow otherwise. Should be tested in other droids as well since it may cause blur.
                }

                wrapper.css(POSITION);

                if ($(options.appendTo)[0] == document.body) {
                    wrapper.css(TOP, "-10000px");
                }

                animation = extend(true, {}, options.animation.open);
                that.flipped = that._position(fixed);
                animation.effects = kendo.parseEffects(animation.effects, that.flipped);

                direction = animation.effects.slideIn ? animation.effects.slideIn.direction : direction;

                if (options.anchor != BODY) {
                    var dirClass = ACTIVEBORDER + "-" + direction;

                    element.addClass(ACTIVEBORDER + "-" + kendo.directions[direction].reverse);

                    anchor
                        .addClass(dirClass)
                        .children(ACTIVECHILDREN)
                        .addClass(ACTIVE)
                        .addClass(dirClass);
                }

                element.data(EFFECTS, animation.effects)
                       .kendoStop(true)
                       .kendoAnimate(animation);
            }
        },

        toggle: function() {
            var that = this;

            that[that.visible() ? CLOSE : OPEN]();
        },

        visible: function() {
            return this.element.is(":" + VISIBLE);
        },

        close: function() {
            var that = this,
                options = that.options, wrap,
                animation, openEffects, closeEffects;

            if (that.visible()) {
                wrap = (that.wrapper[0] ? that.wrapper : kendo.wrap(that.element).hide());

                if (that._closing || that.trigger(CLOSE)) {
                    return;
                }

                // Close all inclusive popups.
                that.element.find(".k-popup").each(function () {
                    var that = $(this),
                        popup = that.data("kendoPopup");

                    if (popup) {
                        popup.close();
                    }
                });

                DOCUMENT_ELEMENT.unbind(MOUSEDOWN, that._mousedownProxy);
                WINDOW.unbind(RESIZE_SCROLL, that._resizeProxy);

                animation = extend(true, {}, options.animation.close);
                openEffects = that.element.data(EFFECTS);
                closeEffects = animation.effects;

                if (!closeEffects && !kendo.size(closeEffects) && openEffects && kendo.size(openEffects)) {
                    animation.effects = openEffects;
                    animation.reverse = true;
                }

                that._closing = true;

                that.element.kendoStop(true);
                wrap.css({ overflow: HIDDEN }); // stop callback will remove hidden overflow
                that.element.kendoAnimate(animation);
            }
        },

        _resize: function(e) {
            var that = this;

            if (e.type === "resize") {
                clearTimeout(that._resizeTimeout);
                that._resizeTimeout = setTimeout(function() {
                    that._position();
                    that._resizeTimeout = null;
                }, 50);
            } else {
                if (!that._hovered && !contains(that.element[0], activeElement())) {
                    that.close();
                }
            }
        },

        _mousedown: function(e) {
            var that = this,
                container = that.element[0],
                options = that.options,
                anchor = $(options.anchor)[0],
                toggleTarget = options.toggleTarget,
                target = kendo.eventTarget(e),
                popup = $(target).closest(".k-popup"),
                mobile = popup.parent().parent(".km-shim").length;

            popup = popup[0];
            if (!mobile && popup && popup !== that.element[0]){
                return;
            }

            // This MAY result in popup not closing in certain cases.
            if ($(e.target).closest("a").data("rel") === "popover") {
                return;
            }

            if (!contains(container, target) && !contains(anchor, target) && !(toggleTarget && contains($(toggleTarget)[0], target))) {
                that.close();
            }
        },

        _fit: function(position, size, viewPortSize) {
            var output = 0;

            if (position + size > viewPortSize) {
                output = viewPortSize - (position + size);
            }

            if (position < 0) {
                output = -position;
            }

            return output;
        },

        _flip: function(offset, size, anchorSize, viewPortSize, origin, position, boxSize) {
            var output = 0;
                boxSize = boxSize || size;

            if (position !== origin && position !== CENTER && origin !== CENTER) {
                if (offset + boxSize > viewPortSize) {
                    output += -(anchorSize + size);
                }

                if (offset + output < 0) {
                    output += anchorSize + size;
                }
            }
            return output;
        },

        _position: function(fixed) {
            var that = this,
                documentElement = $(document.documentElement),
                element = that.element.css(POSITION, ""),
                wrapper = that.wrapper,
                options = that.options,
                viewport = $(options.viewport),
                viewportOffset = viewport.offset(),
                anchor = $(options.anchor),
                origins = options.origin.toLowerCase().split(" "),
                positions = options.position.toLowerCase().split(" "),
                collisions = that.collisions,
                zoomLevel = support.zoomLevel(),
                siblingContainer, parents,
                parentZIndex, zIndex = 10002,
                idx = 0, length, viewportWidth, viewportHeight;

            // $(window).height() uses documentElement to get the height
            documentElement.css({ overflowX: "hidden", overflowY: "hidden" });
            viewportWidth = viewport.width();
            viewportHeight = viewport.height();
            documentElement.css({ overflowX: "", overflowY: "" });

            siblingContainer = anchor.parents().filter(wrapper.siblings());

            if (siblingContainer[0]) {
                parentZIndex = Number($(siblingContainer).css("zIndex"));
                if (parentZIndex) {
                    zIndex = parentZIndex + 1;
                } else {
                    parents = anchor.parentsUntil(siblingContainer);
                    for (length = parents.length; idx < length; idx++) {
                        parentZIndex = Number($(parents[idx]).css("zIndex"));
                        if (parentZIndex && zIndex < parentZIndex) {
                            zIndex = parentZIndex + 1;
                        }
                    }
                }
            }

            wrapper.css("zIndex", zIndex);

            if (fixed && fixed.isFixed) {
                wrapper.css({ left: fixed.x, top: fixed.y });
            } else {
                wrapper.css(that._align(origins, positions));
            }

            var pos = getOffset(wrapper, POSITION, anchor[0] === wrapper.offsetParent()[0]),
                offset = getOffset(wrapper),
                anchorParent = anchor.offsetParent().parent(".k-animation-container,.k-popup,.k-group"); // If the parent is positioned, get the current positions

            if (anchorParent.length) {
                pos = getOffset(wrapper, POSITION, true);
                offset = getOffset(wrapper);
            }

            if (viewport[0] === window) {
                offset.top -= (window.pageYOffset || document.documentElement.scrollTop || 0);
                offset.left -= (window.pageXOffset || document.documentElement.scrollLeft || 0);
            }
            else {
                offset.top -= viewportOffset.top;
                offset.left -= viewportOffset.left;
            }

            if (!that.wrapper.data(LOCATION)) { // Needed to reset the popup location after every closure - fixes the resize bugs.
                wrapper.data(LOCATION, extend({}, pos));
            }

            var offsets = extend({}, offset),
                location = extend({}, pos);

            if (collisions[0] === "fit") {
                location.top += that._fit(offsets.top, wrapper.outerHeight(), viewportHeight / zoomLevel);
            }

            if (collisions[1] === "fit") {
                location.left += that._fit(offsets.left, wrapper.outerWidth(), viewportWidth / zoomLevel);
            }

            var flipPos = extend({}, location);

            if (collisions[0] === "flip") {
                location.top += that._flip(offsets.top, element.outerHeight(), anchor.outerHeight(), viewportHeight / zoomLevel, origins[0], positions[0], wrapper.outerHeight());
            }

            if (collisions[1] === "flip") {
                location.left += that._flip(offsets.left, element.outerWidth(), anchor.outerWidth(), viewportWidth / zoomLevel, origins[1], positions[1], wrapper.outerWidth());
            }

            element.css(POSITION, ABSOLUTE);
            wrapper.css(location);

            return (location.left != flipPos.left || location.top != flipPos.top);
        },

        _align: function(origin, position) {
            var that = this,
                element = that.wrapper,
                anchor = $(that.options.anchor),
                verticalOrigin = origin[0],
                horizontalOrigin = origin[1],
                verticalPosition = position[0],
                horizontalPosition = position[1],
                anchorOffset = getOffset(anchor),
                appendTo = $(that.options.appendTo),
                appendToOffset,
                width = element.outerWidth(),
                height = element.outerHeight(),
                anchorWidth = anchor.outerWidth(),
                anchorHeight = anchor.outerHeight(),
                top = anchorOffset.top,
                left = anchorOffset.left,
                round = Math.round;

            if (appendTo[0] != document.body) {
                appendToOffset = getOffset(appendTo);
                top -= appendToOffset.top;
                left -= appendToOffset.left;
            }


            if (verticalOrigin === BOTTOM) {
                top += anchorHeight;
            }

            if (verticalOrigin === CENTER) {
                top += round(anchorHeight / 2);
            }

            if (verticalPosition === BOTTOM) {
                top -= height;
            }

            if (verticalPosition === CENTER) {
                top -= round(height / 2);
            }

            if (horizontalOrigin === RIGHT) {
                left += anchorWidth;
            }

            if (horizontalOrigin === CENTER) {
                left += round(anchorWidth / 2);
            }

            if (horizontalPosition === RIGHT) {
                left -= width;
            }

            if (horizontalPosition === CENTER) {
                left -= round(width / 2);
            }

            return {
                top: top,
                left: left
            };
        }
    });

    ui.plugin(Popup);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        Widget = kendo.ui.Widget,
        proxy = $.proxy,
        abs = Math.abs,
        MAX_DOUBLE_TAP_DISTANCE = 20;

    var Swipe = kendo.Class.extend({
        init: function(element, callback, options) {
            options = $.extend({
                minXDelta: 30,
                maxYDelta: 20,
                maxDuration: 1000
            }, options);

            new kendo.UserEvents(element, {
                surface: options.surface,
                allowSelection: true,

                start: function(e) {
                    if (abs(e.x.velocity) * 2 >= abs(e.y.velocity)) {
                        e.sender.capture();
                    }
                },

                move: function(e) {
                    var touch = e.touch,
                    duration = e.event.timeStamp - touch.startTime,
                    direction = touch.x.initialDelta > 0 ? "right" : "left";

                    if (
                        abs(touch.x.initialDelta) >= options.minXDelta &&
                        abs(touch.y.initialDelta) < options.maxYDelta &&
                    duration < options.maxDuration)
                    {
                        callback({
                            direction: direction,
                            touch: touch,
                            target: touch.target
                        });

                        touch.cancel();
                    }
                }
            });
        }
    });

    var Touch = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);
            options = that.options;

            element = that.element;

            function eventProxy(name) {
                return function(e) {
                    that._triggerTouch(name, e);
                };
            }

            function gestureEventProxy(name) {
                return function(e) {
                    that.trigger(name, { touches: e.touches, distance: e.distance, center: e.center, event: e.event });
                };
            }

            that.events = new kendo.UserEvents(element, {
                filter: options.filter,
                surface: options.surface,
                minHold: options.minHold,
                multiTouch: options.multiTouch,
                allowSelection: true,
                press: eventProxy("touchstart"),
                hold: eventProxy("hold"),
                tap: proxy(that, "_tap"),
                gesturestart: gestureEventProxy("gesturestart"),
                gesturechange: gestureEventProxy("gesturechange"),
                gestureend: gestureEventProxy("gestureend")
            });

            if (options.enableSwipe) {
                that.events.bind("start", proxy(that, "_swipestart"));
                that.events.bind("move", proxy(that, "_swipemove"));
            } else {
                that.events.bind("start", proxy(that, "_dragstart"));
                that.events.bind("move", eventProxy("drag"));
                that.events.bind("end", eventProxy("dragend"));
            }

            kendo.notify(that);
        },

        events: [
            "touchstart",
            "dragstart",
            "drag",
            "dragend",
            "tap",
            "doubletap",
            "hold",
            "swipe",
            "gesturestart",
            "gesturechange",
            "gestureend"
        ],

        options: {
            name: "Touch",
            surface: null,
            global: false,
            multiTouch: false,
            enableSwipe: false,
            minXDelta: 30,
            maxYDelta: 20,
            maxDuration: 1000,
            minHold: 800,
            doubleTapTimeout: 800
        },

        cancel: function() {
            this.events.cancel();
        },

        _triggerTouch: function(type, e) {
            if (this.trigger(type, { touch: e.touch, event: e.event })) {
                e.preventDefault();
            }
        },

        _tap: function(e) {
            var that = this,
                lastTap = that.lastTap,
                touch = e.touch;

            if (lastTap &&
                (touch.endTime - lastTap.endTime < that.options.doubleTapTimeout) &&
                kendo.touchDelta(touch, lastTap).distance < MAX_DOUBLE_TAP_DISTANCE
                ) {

               that._triggerTouch("doubletap", e);
               that.lastTap = null;
            } else {
                that._triggerTouch("tap", e);
                that.lastTap = touch;
            }
        },

        _dragstart: function(e) {
            this._triggerTouch("dragstart", e);
        },

        _swipestart: function(e) {
            if (abs(e.x.velocity) * 2 >= abs(e.y.velocity)) {
                e.sender.capture();
            }
        },

        _swipemove: function(e) {
            var that = this,
                options = that.options,
                touch = e.touch,
                duration = e.event.timeStamp - touch.startTime,
                direction = touch.x.initialDelta > 0 ? "right" : "left";

            if (
                abs(touch.x.initialDelta) >= options.minXDelta &&
                abs(touch.y.initialDelta) < options.maxYDelta &&
                duration < options.maxDuration
                )
            {
                that.trigger("swipe", {
                    direction: direction,
                    touch: e.touch
                });

                touch.cancel();
            }
        }
    });


    window.jQuery.fn.kendoMobileSwipe = function(callback, options) {
        this.each(function() {
            new Swipe(this, callback, options);
        });
    };

    kendo.ui.plugin(Touch);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        fx = kendo.effects,
        ui = mobile.ui,
        proxy = $.proxy,
        extend = $.extend,
        Widget = ui.Widget,
        Class = kendo.Class,
        Movable = kendo.ui.Movable,
        Pane = kendo.ui.Pane,
        PaneDimensions = kendo.ui.PaneDimensions,
        Transition = fx.Transition,
        Animation = fx.Animation,
        abs = Math.abs,
        SNAPBACK_DURATION = 500,
        SCROLLBAR_OPACITY = 0.7,
        FRICTION = 0.96,
        VELOCITY_MULTIPLIER = 10,
        MAX_VELOCITY = 55,
        OUT_OF_BOUNDS_FRICTION = 0.5,
        ANIMATED_SCROLLER_PRECISION = 5,
        RELEASECLASS = "km-scroller-release",
        REFRESHCLASS = "km-scroller-refresh",
        PULL = "pull",
        CHANGE = "change",
        RESIZE = "resize",
        SCROLL = "scroll",
        MOUSE_WHEEL_ID = 2;

    var ZoomSnapBack = Animation.extend({
        init: function(options) {
            var that = this;
            Animation.fn.init.call(that);
            extend(that, options);

            that.userEvents.bind("gestureend", proxy(that.start, that));
            that.tapCapture.bind("press", proxy(that.cancel, that));
        },

        enabled: function() {
          return this.movable.scale < this.dimensions.minScale;
        },

        done: function() {
            return this.dimensions.minScale - this.movable.scale < 0.01;
        },

        tick: function() {
            var movable = this.movable;
            movable.scaleWith(1.1);
            this.dimensions.rescale(movable.scale);
        },

        onEnd: function() {
            var movable = this.movable;
            movable.scaleTo(this.dimensions.minScale);
            this.dimensions.rescale(movable.scale);
        }
    });

    var DragInertia = Animation.extend({
        init: function(options) {
            var that = this;

            Animation.fn.init.call(that);

            extend(that, options, {
                transition: new Transition({
                    axis: options.axis,
                    movable: options.movable,
                    onEnd: function() { that._end(); }
                })
            });

            that.tapCapture.bind("press", function() { that.cancel(); });
            that.userEvents.bind("end", proxy(that.start, that));
            that.userEvents.bind("gestureend", proxy(that.start, that));
            that.userEvents.bind("tap", proxy(that.onEnd, that));
        },

        onCancel: function() {
            this.transition.cancel();
        },

        freeze: function(location) {
            var that = this;
            that.cancel();
            that._moveTo(location);
        },

        onEnd: function() {
            var that = this;
            if (that._outOfBounds()) {
                that._snapBack();
            } else {
                that._end();
            }
        },

        done: function() {
            return abs(this.velocity) < 1;
        },

        start: function(e) {
            var that = this,
                velocity;

            if (!that.dimension.enabled) { return; }


            if (that._outOfBounds()) {
                that._snapBack();
            } else {
                velocity = e.touch.id === MOUSE_WHEEL_ID ? 0 : e.touch[that.axis].velocity;
                that.velocity = Math.max(Math.min(velocity * that.velocityMultiplier, MAX_VELOCITY), -MAX_VELOCITY);

                that.tapCapture.captureNext();
                Animation.fn.start.call(that);
            }
        },

        tick: function() {
            var that = this,
                dimension = that.dimension,
                friction = that._outOfBounds() ? OUT_OF_BOUNDS_FRICTION : that.friction,
                delta = (that.velocity *= friction),
                location = that.movable[that.axis] + delta;

                if (!that.elastic && dimension.outOfBounds(location)) {
                    location = Math.max(Math.min(location, dimension.max), dimension.min);
                    that.velocity = 0;
                }

            that.movable.moveAxis(that.axis, location);
        },

        _end: function() {
            this.tapCapture.cancelCapture();
            this.end();
        },

        _outOfBounds: function() {
            return this.dimension.outOfBounds(this.movable[this.axis]);
        },

        _snapBack: function() {
            var that = this,
                dimension = that.dimension,
                snapBack = that.movable[that.axis] > dimension.max ? dimension.max : dimension.min;
            that._moveTo(snapBack);
        },

        _moveTo: function(location) {
            this.transition.moveTo({ location: location, duration: SNAPBACK_DURATION, ease: Transition.easeOutExpo });
        }
    });

    var AnimatedScroller = Animation.extend({
        init: function(options) {
            var that = this;

            kendo.effects.Animation.fn.init.call(this);

            extend(that, options, {
                origin: {},
                destination: {},
                offset: {}
            });
        },

        tick: function() {
            this._updateCoordinates();
            this.moveTo(this.origin);
        },

        done: function() {
            return abs(this.offset.y) < ANIMATED_SCROLLER_PRECISION && abs(this.offset.x) < ANIMATED_SCROLLER_PRECISION;
        },

        onEnd: function() {
            this.moveTo(this.destination);
        },

        setCoordinates: function(from, to) {
            this.offset = {};
            this.origin = from;
            this.destination = to;
        },

        _updateCoordinates: function() {
            this.offset = {
                x: (this.destination.x - this.origin.x) / 4,
                y: (this.destination.y - this.origin.y) / 4
            };

            this.origin = {
                y: this.origin.y + this.offset.y,
                x: this.origin.x + this.offset.x
            };
        }
    });

    var ScrollBar = Class.extend({
        init: function(options) {
            var that = this,
                horizontal = options.axis === "x",
                element = $('<div class="km-touch-scrollbar km-' + (horizontal ? "horizontal" : "vertical") + '-scrollbar" />');

            extend(that, options, {
                element: element,
                elementSize: 0,
                movable: new Movable(element),
                scrollMovable: options.movable,
                size: horizontal ? "width" : "height"
            });

            that.scrollMovable.bind(CHANGE, proxy(that._move, that));
            that.container.append(element);
        },

        _move: function() {
            var that = this,
                axis = that.axis,
                dimension = that.dimension,
                paneSize = dimension.size,
                scrollMovable = that.scrollMovable,
                sizeRatio = paneSize / dimension.total,
                position = Math.round(-scrollMovable[axis] * sizeRatio),
                size = Math.round(paneSize * sizeRatio);

                if (position + size > paneSize) {
                    size = paneSize - position;
                } else if (position < 0) {
                    size += position;
                    position = 0;
                }

            if (that.elementSize != size) {
                that.element.css(that.size, size + "px");
                that.elementSize = size;
            }

            that.movable.moveAxis(axis, position);
        },

        show: function() {
            this.element.css({opacity: SCROLLBAR_OPACITY, visibility: "visible"});
        },

        hide: function() {
            this.element.css({opacity: 0});
        }
    });

    var Scroller = Widget.extend({
        init: function(element, options) {
            var that = this;
            Widget.fn.init.call(that, element, options);

            element = that.element;

            that._native = that.options.useNative && kendo.support.hasNativeScrolling;
            if (that._native) {
                element.addClass("km-native-scroller")
                    .prepend('<div class="km-scroll-header"/>');

                extend(that, {
                    scrollElement: element,
                    fixedContainer: element.children().first()
                });

                return;
            }

            element
                .css("overflow", "hidden")
                .addClass("km-scroll-wrapper")
                .wrapInner('<div class="km-scroll-container"/>')
                .prepend('<div class="km-scroll-header"/>');

            var inner = element.children().eq(1),

                tapCapture = new kendo.TapCapture(element),

                movable = new Movable(inner),

                dimensions = new PaneDimensions({
                    element: inner,
                    container: element,
                    forcedEnabled: that.options.zoom
                }),

                avoidScrolling = this.options.avoidScrolling,

                userEvents = new kendo.UserEvents(element, {
                    allowSelection: true,
                    preventDragEvent: true,
                    captureUpIfMoved: true,
                    multiTouch: that.options.zoom,
                    start: function(e) {
                        dimensions.refresh();

                        var velocityX = abs(e.x.velocity),
                            velocityY = abs(e.y.velocity),
                            horizontalSwipe  = velocityX * 2 >= velocityY,
                            originatedFromFixedContainer = $.contains(that.fixedContainer[0], e.event.target),
                            verticalSwipe = velocityY * 2 >= velocityX;


                        if (!originatedFromFixedContainer && !avoidScrolling(e) && that.enabled && (dimensions.x.enabled && horizontalSwipe || dimensions.y.enabled && verticalSwipe)) {
                            userEvents.capture();
                        } else {
                            userEvents.cancel();
                        }
                    }
                }),

                pane = new Pane({
                    movable: movable,
                    dimensions: dimensions,
                    userEvents: userEvents,
                    elastic: that.options.elastic
                }),

                zoomSnapBack = new ZoomSnapBack({
                    movable: movable,
                    dimensions: dimensions,
                    userEvents: userEvents,
                    tapCapture: tapCapture
                }),

                animatedScroller = new AnimatedScroller({
                    moveTo: function(coordinates) {
                        that.scrollTo(coordinates.x, coordinates.y);
                    }
                });

            movable.bind(CHANGE, function() {
                that.scrollTop = - movable.y;
                that.scrollLeft = - movable.x;

                that.trigger(SCROLL, {
                    scrollTop: that.scrollTop,
                    scrollLeft: that.scrollLeft
                });
            });

            if (that.options.mousewheelScrolling) {
                element.on("DOMMouseScroll mousewheel",  proxy(this, "_wheelScroll"));
            }

            extend(that, {
                movable: movable,
                dimensions: dimensions,
                zoomSnapBack: zoomSnapBack,
                animatedScroller: animatedScroller,
                userEvents: userEvents,
                pane: pane,
                tapCapture: tapCapture,
                pulled: false,
                enabled: true,
                scrollElement: inner,
                scrollTop: 0,
                scrollLeft: 0,
                fixedContainer: element.children().first()
            });

            that._initAxis("x");
            that._initAxis("y");

            // build closure
            that._wheelEnd = function() {
                that._wheel = false;
                that.userEvents.end(0, that._wheelY);
            };

            dimensions.refresh();

            if (that.options.pullToRefresh) {
                that._initPullToRefresh();
            }
        },

        _wheelScroll: function(e) {
            if (!this._wheel) {
                this._wheel = true;
                this._wheelY = 0;
                this.userEvents.press(0, this._wheelY);
            }

            clearTimeout(this._wheelTimeout);
            this._wheelTimeout = setTimeout(this._wheelEnd, 50);

            var delta = kendo.wheelDeltaY(e);

            if (delta) {
                this._wheelY += delta;
                this.userEvents.move(0, this._wheelY);
            }

            e.preventDefault();
        },

        makeVirtual: function() {
            this.dimensions.y.makeVirtual();
        },

        virtualSize: function(min, max) {
            this.dimensions.y.virtualSize(min, max);
        },

        height: function() {
            return this.dimensions.y.size;
        },

        scrollHeight: function() {
            return this.scrollElement[0].scrollHeight;
        },

        scrollWidth: function() {
            return this.scrollElement[0].scrollWidth;
        },

        options: {
            name: "Scroller",
            zoom: false,
            pullOffset: 140,
            elastic: true,
            useNative: false,
            mousewheelScrolling: true,
            avoidScrolling: function() { return false; },
            pullToRefresh: false,
            pullTemplate: "Pull to refresh",
            releaseTemplate: "Release to refresh",
            refreshTemplate: "Refreshing"
        },

        events: [
            PULL,
            SCROLL,
            RESIZE
        ],

        _resize: function() {
            if (!this._native) {
                this.dimensions.refresh();
            }
            this.reset();
        },

        setOptions: function(options) {
            var that = this;
            Widget.fn.setOptions.call(that, options);
            if (options.pullToRefresh) {
                that._initPullToRefresh();
            }
        },

        reset: function() {
            if (this._native) {
                this.scrollElement.scrollTop(0);
            } else {
                this.movable.moveTo({x: 0, y: 0});
                this._scale(1);
            }
        },

        zoomOut: function() {
            var dimensions = this.dimensions;
            dimensions.refresh();
            this._scale(dimensions.fitScale);
            this.movable.moveTo(dimensions.centerCoordinates());
        },

        enable: function() {
            this.enabled = true;
        },

        disable: function() {
            this.enabled = false;
        },

        scrollTo: function(x, y) {
            if (this._native) {
                this.scrollElement.scrollLeft(abs(x));
                this.scrollElement.scrollTop(abs(y));
            } else {
                this.dimensions.refresh();
                this.movable.moveTo({x: x, y: y});
            }
        },

        animatedScrollTo: function(x, y) {
            var from,
                to;

            if(this._native) {
                this.scrollTo(x, y);
            } else {
                from = { x: this.movable.x, y: this.movable.y };
                to = { x: x, y: y };

                this.animatedScroller.setCoordinates(from, to);
                this.animatedScroller.start();
            }
        },

        pullHandled: function() {
            var that = this;
            that.refreshHint.removeClass(REFRESHCLASS);
            that.hintContainer.html(that.pullTemplate({}));
            that.yinertia.onEnd();
            that.xinertia.onEnd();
            that.userEvents.cancel();
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            if (this.userEvents) {
                this.userEvents.destroy();
            }
        },

        _scale: function(scale) {
            this.dimensions.rescale(scale);
            this.movable.scaleTo(scale);
        },

        _initPullToRefresh: function() {
            var that = this;

            that.dimensions.y.forceEnabled();
            that.pullTemplate = kendo.template(that.options.pullTemplate);
            that.releaseTemplate = kendo.template(that.options.releaseTemplate);
            that.refreshTemplate = kendo.template(that.options.refreshTemplate);

            that.scrollElement.prepend('<span class="km-scroller-pull"><span class="km-icon"></span><span class="km-loading-left"></span><span class="km-loading-right"></span><span class="km-template">' + that.pullTemplate({}) + '</span></span>');
            that.refreshHint = that.scrollElement.children().first();
            that.hintContainer = that.refreshHint.children(".km-template");

            that.pane.y.bind("change", proxy(that._paneChange, that));
            that.userEvents.bind("end", proxy(that._dragEnd, that));
        },

        _dragEnd: function() {
            var that = this;

            if(!that.pulled) {
                return;
            }

            that.pulled = false;
            that.refreshHint.removeClass(RELEASECLASS).addClass(REFRESHCLASS);
            that.hintContainer.html(that.refreshTemplate({}));
            that.yinertia.freeze(that.options.pullOffset / 2);
            that.trigger("pull");
        },

        _paneChange: function() {
            var that = this;

            if (that.movable.y / OUT_OF_BOUNDS_FRICTION > that.options.pullOffset) {
                if (!that.pulled) {
                    that.pulled = true;
                    that.refreshHint.removeClass(REFRESHCLASS).addClass(RELEASECLASS);
                    that.hintContainer.html(that.releaseTemplate({}));
                }
            } else if (that.pulled) {
                that.pulled = false;
                that.refreshHint.removeClass(RELEASECLASS);
                that.hintContainer.html(that.pullTemplate({}));
            }
        },

        _initAxis: function(axis) {
            var that = this,
                movable = that.movable,
                dimension = that.dimensions[axis],
                tapCapture = that.tapCapture,
                scrollBar = new ScrollBar({
                    axis: axis,
                    movable: movable,
                    dimension: dimension,
                    container: that.element
                });

            that.pane[axis].bind(CHANGE, function() {
                scrollBar.show();
            });

            that[axis + "inertia"] = new DragInertia({
                axis: axis,
                movable: movable,
                tapCapture: tapCapture,
                userEvents: that.userEvents,
                dimension: dimension,
                elastic: that.options.elastic,
                friction: that.options.friction || FRICTION,
                velocityMultiplier: that.options.velocityMultiplier || VELOCITY_MULTIPLIER,
                end: function() {
                    scrollBar.hide();
                    that.trigger("scrollEnd", {
                        axis: axis,
                        scrollTop: that.scrollTop,
                        scrollLeft: that.scrollLeft
                    });
                }
            });
        }
    });

    ui.plugin(Scroller);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        ui = mobile.ui,
        attr = kendo.attr,
        Class = kendo.Class,
        Widget = ui.Widget,
        ViewClone = kendo.ViewClone,
        INIT = "init",
        UI_OVERLAY = '<div style="height: 100%; width: 100%; position: absolute; top: 0; left: 0; z-index: 20000; display: none" />',
        BEFORE_SHOW = "beforeShow",
        SHOW = "show",
        AFTER_SHOW = "afterShow",
        BEFORE_HIDE = "beforeHide",
        HIDE = "hide",
        Z_INDEX = "z-index",
        attrValue = kendo.attrValue,
        roleSelector = kendo.roleSelector;

    function initPopOvers(element) {
        var popovers = element.find(roleSelector("popover")),
            idx, length,
            roles = ui.roles;

        for (idx = 0, length = popovers.length; idx < length; idx ++) {
            kendo.initWidget(popovers[idx], {}, roles);
        }
    }

    function preventScrollIfNotInput(e) {
        if (!kendo.triggeredByInput(e)) {
            e.preventDefault();
        }
    }

    var View = Widget.extend({
        init: function(element, options) {
            Widget.fn.init.call(this, element, options);

            this.params = {};

            $.extend(this, options);

            this.transition = this.transition || this.defaultTransition;

            this._id();
            this._layout();
            this._overlay();
            this._scroller();
            this._model();
        },

        events: [
            INIT,
            BEFORE_SHOW,
            SHOW,
            AFTER_SHOW,
            BEFORE_HIDE,
            HIDE
        ],

        options: {
            name: "View",
            title: "",
            reload: false,
            transition: "",
            defaultTransition: "",
            useNativeScrolling: false,
            stretch: false,
            zoom: false,
            model: null,
            initWidgets: true
        },

        enable: function(enable) {
            if(typeof enable == "undefined") {
                enable = true;
            }

            if(enable) {
                this.overlay.hide();
            } else {
                this.overlay.show();
            }
        },

        destroy: function() {
            if (this.layout) {
                this.layout.detach(this);
            }

            Widget.fn.destroy.call(this);

            if (this.scroller) {
                this.scroller.destroy();
            }

            kendo.destroy(this.element);
        },

        purge: function() {
            this.destroy();
            this.element.remove();
        },

        triggerBeforeShow: function() {
            if (this.trigger(BEFORE_SHOW, { view: this })) {
                return false;
            }
            return true;
        },

        showStart: function() {
            var that = this;
            that.element.css("display", "");

            if (!that.inited) {
                that.inited = true;
                that.trigger(INIT, {view: that});
            }

            if (that.layout) {
                that.layout.attach(that);
            }

            that._padIfNativeScrolling();
            that.trigger(SHOW, {view: that});
            kendo.resize(that.element);
        },

        showEnd: function() {
            this.trigger(AFTER_SHOW, {view: this});
            this._padIfNativeScrolling();
        },

        hideStart: function() {
            this.trigger(BEFORE_HIDE, {view: this});
        },

        hideEnd: function() {
            var that = this;
            that.element.hide();
            that.trigger(HIDE, {view: that});
        },

        _padIfNativeScrolling: function() {
            if (mobile.appLevelNativeScrolling()) {
                var isAndroid = kendo.support.mobileOS && kendo.support.mobileOS.android,
                    topContainer = isAndroid ? "footer" : "header",
                    bottomContainer = isAndroid ? "header" : "footer";

                this.content.css({
                    paddingTop: this[topContainer].height(),
                    paddingBottom: this[bottomContainer].height()
                });
            }
        },

        contentElement: function() {
            var that = this;

            return that.options.stretch ? that.content : that.scrollerContent;
        },

        clone: function(back) {
            return new ViewClone(this);
        },

        _scroller: function() {
            var that = this;

            if (mobile.appLevelNativeScrolling()) {
                return;
            }
            if (that.options.stretch) {
                that.content.addClass("km-stretched-view");
            } else {
                that.content.kendoMobileScroller({ zoom: that.options.zoom, useNative: that.options.useNativeScrolling });

                that.scroller = that.content.data("kendoMobileScroller");
                that.scrollerContent = that.scroller.scrollElement;
            }

            // prevent accidental address bar display when pulling the header
            if (kendo.support.kineticScrollNeeded) {
                $(that.element).on("touchmove", ".km-header", preventScrollIfNotInput);
                if (!that.options.useNativeScrolling) {
                    $(that.element).on("touchmove", ".km-content", preventScrollIfNotInput);
                }
            }
        },

        _model: function() {
            var that = this,
                element = that.element,
                model = that.options.model;

            if (typeof model === "string") {
                model = kendo.getter(model)(window);
            }

            that.model = model;

            initPopOvers(element);

            that.element.css("display", "");
            if (that.options.initWidgets) {
                if (model) {
                    kendo.bind(element.children(), model, ui, kendo.ui, kendo.dataviz.ui);
                } else {
                    mobile.init(element.children());
                }
            }
            that.element.css("display", "none");
        },

        _id: function() {
            var element = this.element,
                idAttrValue = element.attr("id") || "";

            this.id = attrValue(element, "url") || "#" + idAttrValue;

            if (this.id == "#") {
                this.id = kendo.guid();
                element.attr("id", this.id);
            }
        },

        _layout: function() {
            var that = this,
                contentSelector = roleSelector("content"),
                element = that.element;

            element.data("kendoView", that).addClass("km-view");

            that.header = element.children(roleSelector("header")).addClass("km-header");
            that.footer = element.children(roleSelector("footer")).addClass("km-footer");

            if (!element.children(contentSelector)[0]) {
              element.wrapInner("<div " + attr("role") + '="content"></div>');
            }

            that.content = element.children(roleSelector("content"))
                                .addClass("km-content");

            that.element.prepend(that.header).append(that.footer);


            if (that.layout) {
                that.layout.setup(that);
            }
        },

        _overlay: function() {
            this.overlay = $(UI_OVERLAY).appendTo(this.element);
        }
    });

    function initWidgets(collection) {
        collection.each(function() {
            kendo.initWidget($(this), {}, ui.roles);
        });
    }

    var Layout = Widget.extend({
        init: function(element, options) {
            var that = this;
            Widget.fn.init.call(that, element, options);

            element = that.element;

            that.header = element.children(roleSelector("header")).addClass("km-header");
            that.footer = element.children(roleSelector("footer")).addClass("km-footer");
            that.elements = that.header.add(that.footer);

            initPopOvers(element);

            kendo.mobile.init(that.element.children());
            that.element.detach();
            that.trigger(INIT, {layout: that});
        },

        options: {
            name: "Layout"
        },

        events: [
            INIT,
            SHOW,
            HIDE
        ],

        setup: function(view) {
            if (!view.header[0]) { view.header = this.header; }
            if (!view.footer[0]) { view.footer = this.footer; }
        },

        detach: function(view) {
            var that = this;
            if (view.header === that.header && that.header[0]) {
                view.element.prepend(that.header.detach()[0].cloneNode(true));
            }

            if (view.footer === that.footer && that.footer.length) {
                view.element.append(that.footer.detach()[0].cloneNode(true));
            }

            that.trigger(HIDE, {layout: that, view: view});
        },

        attach: function(view) {
            var that = this,
                previousView = that.currentView;

            if (previousView) {
                that.detach(previousView);
            }

            if (view.header === that.header) {
                that.header.detach();
                view.element.children(roleSelector("header")).remove();
                view.element.prepend(that.header);
            }

            if (view.footer === that.footer) {
                that.footer.detach();
                view.element.children(roleSelector("footer")).remove();
                view.element.append(that.footer);
            }

            that.trigger(SHOW, {layout: that, view: view});
            that.currentView = view;
        }
    });

    var Observable = kendo.Observable,
        bodyRegExp = /<body[^>]*>(([\u000a\u000d\u2028\u2029]|.)*)<\/body>/i,
        LOAD_START = "loadStart",
        LOAD_COMPLETE = "loadComplete",
        SHOW_START = "showStart",
        SAME_VIEW_REQUESTED = "sameViewRequested",
        VIEW_SHOW = "viewShow",
        AFTER = "after";

    var ViewEngine = Observable.extend({
        init: function(options) {
            var that = this,
                views,
                errorMessage,
                container;

            Observable.fn.init.call(that);

            $.extend(that, options);
            that.sandbox = $("<div />");
            container = that.container;

            views = that._hideViews(container);
            that.rootView = views.first();

            if (!that.rootView[0] && options.rootNeeded) {
                if (container[0] == kendo.mobile.application.element[0]) {
                    errorMessage = 'Your kendo mobile application element does not contain any direct child elements with data-role="view" attribute set. Make sure that you instantiate the mobile application using the correct container.';
                } else {
                    errorMessage = 'Your pane element does not contain any direct child elements with data-role="view" attribute set.';
                }
                throw new Error(errorMessage);
            }

            that.layouts = {};

            that.viewContainer = new kendo.ViewContainer(that.container);

            that.viewContainer.bind("accepted", function(e) {
                e.view.params = that.params;
            });

            that.viewContainer.bind("complete", function(e) {
                that.trigger(VIEW_SHOW, { view: e.view });
            });

            that.viewContainer.bind(AFTER, function(e) {
                that.trigger(AFTER);
            });

            that._setupLayouts(container);

            initWidgets(container.children(roleSelector("modalview drawer")));
        },

        destroy: function() {
            kendo.destroy(this.container);

            for (var id in this.layouts) {
                this.layouts[id].destroy();
            }
        },

        view: function() {
            return this.viewContainer.view;
        },

        showView: function(url, transition, params) {
            url = url.replace(new RegExp("^" + this.remoteViewURLPrefix), "");
            if (url === "" && this.remoteViewURLPrefix) {
                url = "/";
            }

            if (url === this.url) {
                this.trigger(SAME_VIEW_REQUESTED);
                return false;
            }

            this.trigger(SHOW_START);

            var that = this,
                showClosure = function(view) {
                    return that.viewContainer.show(view, transition, url);
                },
                element = that._findViewElement(url),
                view = element.data("kendoView");

            that.url = url;
            that.params = params;

            if (view && view.reload) {
                view.purge();
                element = [];
            }

            this.trigger("viewTypeDetermined", { remote: element.length === 0, url: url });

            if (element[0]) {
                if (!view) {
                    view = that._createView(element);
                }

                return showClosure(view);
            } else {
                that._loadView(url, showClosure);
                return true;
            }
        },

        append: function(html, url) {
            var that = this,
                sandbox = that.sandbox,
                urlPath = (url || "").split("?")[0],
                container = that.container,
                views,
                modalViews,
                view;

            if (bodyRegExp.test(html)) {
                html = RegExp.$1;
            }

            sandbox[0].innerHTML = html;

            container.append(sandbox.children("script, style"));

            views = that._hideViews(sandbox);
            view = views.first();

            // Generic HTML content found as remote view - no remote view markers
            if (!view.length) {
                views = view = sandbox.wrapInner("<div data-role=view />").children(); // one element
            }

            if (urlPath) {
                view.hide().attr(attr("url"), urlPath);
            }

            that._setupLayouts(sandbox);

            modalViews = sandbox.children(roleSelector("modalview drawer"));

            container.append(sandbox.children(roleSelector("layout modalview drawer")).add(views));

            // Initialize the modalviews after they have been appended to the final container
            initWidgets(modalViews);

            return that._createView(view);
        },


        _findViewElement: function(url) {
            var element,
                urlPath = url.split("?")[0];

            if (!urlPath) {
                return this.rootView;
            }

            element = this.container.children("[" + attr("url") + "='" + urlPath + "']");

            // do not try to search for "#/foo/bar" id, jQuery throws error
            if (!element[0] && urlPath.indexOf("/") === -1) {
                element = this.container.children(urlPath.charAt(0) === "#" ? urlPath : "#" + urlPath);
            }

            return element;
        },

        _createView: function(element) {
            var that = this,
                viewOptions,
                layout = attrValue(element, "layout");

            if (typeof layout === "undefined") {
                layout = that.layout;
            }

            if (layout) {
                layout = that.layouts[layout];
            }

            viewOptions = {
                defaultTransition: that.transition,
                loader: that.loader,
                container: that.container,
                layout: layout,
                reload: attrValue(element, "reload")
            };

            return kendo.initWidget(element, viewOptions, ui.roles);
        },

        _loadView: function(url, callback) {
            var that = this;

            if (this.serverNavigation) {
                location.href = url;
                return;
            }

            if (that._xhr) {
                that._xhr.abort();
            }

            that.trigger(LOAD_START);

            that._xhr = $.get(kendo.absoluteURL(url, that.remoteViewURLPrefix), function(html) {
                            that.trigger(LOAD_COMPLETE);
                            callback(that.append(html, url));
                        }, 'html')
                        .fail(function(request) {
                            that.trigger(LOAD_COMPLETE);
                            if (request.status === 0 && request.responseText) {
                                callback(that.append(request.responseText, url));
                            }
                        });
        },

        _hideViews: function(container) {
            return container.children(roleSelector("view splitview")).hide();
        },

        _setupLayouts: function(element) {
            var that = this;

            element.children(roleSelector("layout")).each(function() {
                var layout = $(this),
                    platform = attrValue(layout,  "platform");

                if (platform === undefined || platform === mobile.application.os.name) {
                    that.layouts[kendo.attrValue(layout, "id")] = kendo.initWidget(layout, {}, ui.roles);
                }
            });
        }
    });

    kendo.mobile.ViewEngine = ViewEngine;

    ui.plugin(View);
    ui.plugin(Layout);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Widget = ui.Widget,
        CAPTURE_EVENTS = $.map(kendo.eventMap, function(value) { return value; }).join(" ").split(" ");

    var Loader = Widget.extend({
        init: function(container, options) {
            var that = this,
                element = $('<div class="km-loader"><span class="km-loading km-spin"></span><span class="km-loading-left"></span><span class="km-loading-right"></span></div>');

            Widget.fn.init.call(that, element, options);

            that.container = container;
            that.captureEvents = false;

            that._attachCapture();

            element.append(that.options.loading).hide().appendTo(container);
        },

        options: {
            name: "Loader",
            loading: "<h1>Loading...</h1>",
            timeout: 100
        },

        show: function() {
            var that = this;

            clearTimeout(that._loading);

            if (that.options.loading === false) {
                return;
            }

            that.captureEvents = true;
            that._loading = setTimeout(function() {
                that.element.show();
            }, that.options.timeout);
        },

        hide: function() {
            this.captureEvents = false;
            clearTimeout(this._loading);
            this.element.hide();
        },

        changeMessage: function(message) {
            this.options.loading = message;
            this.element.find(">h1").html(message);
        },

        transition: function() {
            this.captureEvents = true;
            this.container.css("pointer-events", "none");
        },

        transitionDone: function() {
            this.captureEvents = false;
            this.container.css("pointer-events", "");
        },

        _attachCapture: function() {
            var that = this;
            that.captureEvents = false;

            function capture(e) {
                if (that.captureEvents) {
                    e.preventDefault();
                }
            }

            for (var i = 0; i < CAPTURE_EVENTS.length; i ++) {
                that.container[0].addEventListener(CAPTURE_EVENTS[i], capture, true);
            }
        }
    });

    ui.plugin(Loader);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        roleSelector = kendo.roleSelector,
        ui = mobile.ui,
        Widget = ui.Widget,
        ViewEngine = mobile.ViewEngine,
        View = ui.View,
        Loader = mobile.ui.Loader,

        EXTERNAL = "external",
        HREF = "href",
        DUMMY_HREF = "#!",

        NAVIGATE = "navigate",
        VIEW_SHOW = "viewShow",
        SAME_VIEW_REQUESTED = "sameViewRequested",

        WIDGET_RELS = /popover|actionsheet|modalview|drawer/,
        BACK = "#:back",

        attrValue = kendo.attrValue,
        // navigation element roles
        buttonRoles = "button backbutton detailbutton listview-link",
        linkRoles = "tab";

    var Pane = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            options = that.options;
            element = that.element;

            element.addClass("km-pane");

            if (that.options.collapsible) {
                element.addClass("km-collapsible-pane");
            }

            this.history = [];

            this.historyCallback = function(url, params) {
                var transition = that.transition;
                that.transition = null;
                return that.viewEngine.showView(url, transition, params);
            };

            this._historyNavigate = function(url) {
                var params = kendo.parseQueryStringParams(url);

                if (url === BACK) {
                    if (that.history.length === 1) {
                        return;
                    }

                    that.history.pop();
                    url = that.history[that.history.length - 1];
                } else {
                    that.history.push(url);
                }

                that.historyCallback(url, params);
            };

            this._historyReplace = function(url) {
                var params = kendo.parseQueryStringParams(url);
                that.history[that.history.length - 1] = url;
                that.historyCallback(url, params);
            };

            that.loader = new Loader(element, {
                loading: that.options.loading
            });

            that.viewEngine = new ViewEngine({
                container: element,
                transition: options.transition,
                rootNeeded: !options.initial,
                serverNavigation: options.serverNavigation,
                remoteViewURLPrefix: options.root || "",
                layout: options.layout,
                loader: that.loader
            });

            that.viewEngine.bind("showStart", function() {
                that.loader.transition();
                that.closeActiveDialogs();
            });

            that.viewEngine.bind("after", function(e) {
                that.loader.transitionDone();
            });

            that.viewEngine.bind(VIEW_SHOW, function(e) {
                that.trigger(VIEW_SHOW, e);
            });

            that.viewEngine.bind("loadStart", function() {
                that.loader.show();
            });

            that.viewEngine.bind("loadComplete", function() {
                that.loader.hide();
            });

            that.viewEngine.bind(SAME_VIEW_REQUESTED, function() {
                that.trigger(SAME_VIEW_REQUESTED);
            });

            that.viewEngine.bind("viewTypeDetermined", function(e) {
                if (!e.remote || !that.options.serverNavigation)  {
                    that.trigger(NAVIGATE, { url: e.url });
                }
            });

            this._setPortraitWidth();

            kendo.onResize(function() {
                that._setPortraitWidth();
            });

            that._setupAppLinks();
        },

        closeActiveDialogs: function() {
            var dialogs = this.element.find(roleSelector("actionsheet popover modalview")).filter(":visible");
            dialogs.each(function() {
                kendo.widgetInstance($(this), ui).close();
            });
        },

        navigateToInitial: function() {
            var initial = this.options.initial;

            if (initial) {
                this.navigate(initial);
            }
        },

        options: {
            name: "Pane",
            portraitWidth: "",
            transition: "",
            layout: "",
            collapsible: false,
            initial: null,
            loading: "<h1>Loading...</h1>"
        },

        events: [
            NAVIGATE,
            VIEW_SHOW,
            SAME_VIEW_REQUESTED
        ],

        append: function(html) {
            return this.viewEngine.append(html);
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.viewEngine.destroy();
        },

        navigate: function(url, transition) {
            if (url instanceof View) {
                url = url.id;
            }

            this.transition = transition;

            this._historyNavigate(url);
        },

        replace: function(url, transition) {
            if (url instanceof View) {
                url = url.id;
            }

            this.transition = transition;

            this._historyReplace(url);
        },

        bindToRouter: function(router) {
            var that = this,
                options = that.options,
                initial = options.initial,
                viewEngine = this.viewEngine;

            router.bind("init", function(e) {
                var url = e.url,
                    attrUrl = router.pushState ? url : "/";

                viewEngine.rootView.attr(kendo.attr("url"), attrUrl);

                if (url === "/" && initial) {
                    router.navigate(initial, true);
                    e.preventDefault(); // prevents from executing routeMissing, by default
                }
            });

            router.bind("routeMissing", function(e) {
                if (!that.historyCallback(e.url, e.params)) {
                    e.preventDefault();
                }
            });

            router.bind("same", function() {
                that.trigger(SAME_VIEW_REQUESTED);
            });

            that._historyNavigate = function(url) {
                router.navigate(url);
            };

            that._historyReplace = function(url) {
                router.replace(url);
            };
        },

        hideLoading: function() {
            this.loader.hide();
        },

        showLoading: function() {
            this.loader.show();
        },

        changeLoadingMessage: function(message) {
            this.loader.changeMessage(message);
        },

        view: function() {
            return this.viewEngine.view();
        },

        _setPortraitWidth: function() {
            var width,
                portraitWidth = this.options.portraitWidth;

            if (portraitWidth) {
                width = kendo.mobile.application.element.is(".km-vertical") ? portraitWidth : "auto";
                this.element.css("width", width);
            }
        },

        _setupAppLinks: function() {
            this.element.handler(this)
                .on("down", roleSelector(linkRoles), "_mouseup")
                .on("up", roleSelector(buttonRoles), "_mouseup")
                .on("click", roleSelector(linkRoles + " " + buttonRoles), "_appLinkClick");
        },

        _appLinkClick: function (e) {
            var href = $(e.currentTarget).attr("href");
            var remote = href && href[0] !== "#" && this.options.serverNavigation;

            if(!remote && attrValue($(e.currentTarget), "rel") != EXTERNAL) {
                e.preventDefault();
            }
        },

        _mouseup: function(e) {
            if (e.which > 1 || e.isDefaultPrevented()) {
                return;
            }

            var pane = this,
                link = $(e.currentTarget),
                transition = attrValue(link, "transition"),
                rel = attrValue(link, "rel") || "",
                target = attrValue(link, "target"),
                href = link.attr(HREF),
                remote = href && href[0] !== "#" && this.options.serverNavigation;

            if (remote || rel === EXTERNAL || (typeof href === "undefined") || href === DUMMY_HREF) {
                return;
            }

            // Prevent iOS address bar progress display for in app navigation
            link.attr(HREF, DUMMY_HREF);
            setTimeout(function() { link.attr(HREF, href); });

            if (rel.match(WIDGET_RELS)) {
                kendo.widgetInstance($(href), ui).openFor(link);
                // if propagation is not stopped and actionsheet is opened from tabstrip,
                // the actionsheet is closed immediately.
                if (rel === "actionsheet") {
                    e.stopPropagation();
                }
            } else {
                if (target === "_top") {
                    pane = mobile.application.pane;
                }
                else if (target) {
                    pane = $("#" + target).data("kendoMobilePane");
                }

                pane.navigate(href, transition);
            }

            e.preventDefault();
        }
    });

    Pane.wrap = function(element) {
        if (!element.is(roleSelector("view"))) {
            element = element.wrap('<div data-' + kendo.ns + 'role="view" data-stretch="true"></div>').parent();
        }

        var paneContainer = element.wrap('<div class="km-pane-wrapper"><div></div></div>').parent(),
            pane = new Pane(paneContainer);

        pane.navigate("");

        return pane;
    };
    ui.plugin(Pane);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        support = kendo.support,
        Pane = mobile.ui.Pane,

        DEFAULT_OS = "ios7",
        OS = support.mobileOS,
        BERRYPHONEGAP = OS.device == "blackberry" && OS.flatVersion >= 600 && OS.flatVersion < 1000 && OS.appMode,
        VERTICAL = "km-vertical",
        CHROME =  OS.browser === "chrome",
        BROKEN_WEBVIEW_RESIZE = OS.ios && OS.flatVersion >= 700 && (OS.appMode || CHROME),
        HORIZONTAL = "km-horizontal",

        MOBILE_PLATFORMS = {
            ios7: { ios: true, browser: "default", device: "iphone", flatVersion: "700", majorVersion: "7", minorVersion: "0.0", name: "ios", tablet: false },
            ios: { ios: true, browser: "default", device: "iphone", flatVersion: "612", majorVersion: "6", minorVersion: "1.2", name: "ios", tablet: false },
            android: { android: true, browser: "default", device: "android", flatVersion: "442", majorVersion: "4", minorVersion: "4.2", name: "android", tablet: false },
            blackberry: { blackberry: true, browser: "default", device: "blackberry", flatVersion: "710", majorVersion: "7", minorVersion: "1.0", name: "blackberry", tablet: false },
            meego: { meego: true, browser: "default", device: "meego", flatVersion: "850", majorVersion: "8", minorVersion: "5.0", name: "meego", tablet: false },
            wp: { wp: true, browser: "default", device: "wp", flatVersion: "800", majorVersion: "8", minorVersion: "0.0", name: "wp", tablet: false }
        },

        viewportTemplate = kendo.template('<meta content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no#=data.height#" name="viewport" />', {usedWithBlock: false}),
        systemMeta = kendo.template('<meta name="apple-mobile-web-app-capable" content="#= data.webAppCapable === false ? \'no\' : \'yes\' #" /> ' +
                     '<meta name="apple-mobile-web-app-status-bar-style" content="#=data.statusBarStyle#" /> ' +
                     '<meta name="msapplication-tap-highlight" content="no" /> ', {usedWithBlock: false}),
        clipTemplate = kendo.template('<style>.km-view { clip: rect(0 #= data.width #px #= data.height #px 0); }</style>', {usedWithBlock: false}),
        ENABLE_CLIP = OS.android && OS.browser != "chrome" || OS.blackberry,
        viewportMeta = viewportTemplate({ height: "" }),

        iconMeta = kendo.template('<link rel="apple-touch-icon' + (OS.android ? '-precomposed' : '') + '" # if(data.size) { # sizes="#=data.size#" #}# href="#=data.icon#" />', {usedWithBlock: false}),

        HIDEBAR = (OS.device == "iphone" || OS.device == "ipod") && OS.majorVersion < 7,
        SUPPORT_SWIPE_TO_GO_BACK = (OS.device == "iphone" || OS.device == "ipod") && OS.majorVersion >= 7,
        HISTORY_TRANSITION = SUPPORT_SWIPE_TO_GO_BACK ? "none" : null,
        BARCOMPENSATION = OS.browser == "mobilesafari" ? 60 : 0,
        STATUS_BAR_HEIGHT = 20,
        WINDOW = $(window),
        SCREEN = window.screen,
        HEAD = $("head"),

        // mobile app events
        INIT = "init",
        proxy = $.proxy;

    function osCssClass(os, options) {
        var classes = [];

        if (OS) {
            classes.push("km-on-" + OS.name);
        }

        if (os.skin) {
            classes.push("km-" + os.skin);
        } else {
            if (os.name == "ios" && os.majorVersion > 6) {
                classes.push("km-ios7");
            } else {
                classes.push("km-" + os.name);
            }
        }
        if ((os.name == "ios" && os.majorVersion < 7) || os.name != "ios") {
            classes.push("km-" + os.name + os.majorVersion);
        }
        classes.push("km-" + os.majorVersion);
        classes.push("km-m" + (os.minorVersion ? os.minorVersion[0] : 0));

        if (os.variant) {
            classes.push("km-" + (os.skin ? os.skin : os.name) + "-" + os.variant);
        }

        if (os.cordova) {
            classes.push("km-cordova");
        }
        if (os.appMode) {
            classes.push("km-app");
        } else {
            classes.push("km-web");
        }

        if (options && options.statusBarStyle) {
            classes.push("km-" + options.statusBarStyle + "-status-bar");
        }

        return classes.join(" ");
    }

    function wp8Background() {
        return parseInt($("<div style='background: Background' />").css("background-color").split(",")[1], 10) === 0 ? 'dark' : 'light';
    }

    function isOrientationHorizontal(element) {
        return OS.wp ? element.css("animation-name") == "-kendo-landscape" : (Math.abs(window.orientation) / 90 == 1);
    }

    function getOrientationClass(element) {
        return isOrientationHorizontal(element) ? HORIZONTAL : VERTICAL;
    }

    function setMinimumHeight(pane) {
        pane.parent().addBack()
               .css("min-height", window.innerHeight);
    }

    function applyViewportHeight() {
        $("meta[name=viewport]").remove();
        HEAD.append(viewportTemplate({
            height: ", width=device-width" +  // width=device-width was removed for iOS6, but this should stay for BB PhoneGap.
                        (isOrientationHorizontal() ?
                            ", height=" + window.innerHeight + "px"  :
                            (support.mobileOS.flatVersion >= 600 && support.mobileOS.flatVersion < 700) ?
                                ", height=" + window.innerWidth + "px" :
                                ", height=device-height")
        }));
    }

    var Application = kendo.Observable.extend({
        init: function(element, options) {
            var that = this;

            mobile.application = that; // global reference to current application

            that.options = $.extend({
                hideAddressBar: true,
                useNativeScrolling: false,
                statusBarStyle: "black",
                transition: "",
                historyTransition: HISTORY_TRANSITION,
                updateDocumentTitle: true
            }, options);

            kendo.Observable.fn.init.call(that, that.options);
            that.bind(that.events, that.options);

            $(function(){
                element = $(element);
                that.element = element[0] ? element : $(document.body);
                that._setupPlatform();
                that._attachMeta();
                that._setupElementClass();
                that._attachHideBarHandlers();
                that.pane = new Pane(that.element, that.options);
                that.pane.navigateToInitial();

                if (that.options.updateDocumentTitle) {
                    that._setupDocumentTitle();
                }

                that._startHistory();
                that.trigger(INIT);
            });
        },

        events: [
            INIT
        ],

        navigate: function(url, transition) {
            this.pane.navigate(url, transition);
        },

        replace: function(url, transition) {
            this.pane.replace(url, transition);
        },

        scroller: function() {
            return this.view().scroller;
        },

        hideLoading: function() {
            if (this.pane) {
                this.pane.hideLoading();
            } else {
                throw new Error("The mobile application instance is not fully instantiated. Please consider activating loading in the application init event handler.");
            }
        },

        showLoading: function() {
            if (this.pane) {
                this.pane.showLoading();
            } else {
                throw new Error("The mobile application instance is not fully instantiated. Please consider activating loading in the application init event handler.");
            }
        },

        changeLoadingMessage: function(message) {
            if (this.pane) {
                this.pane.changeLoadingMessage(message);
            } else {
                throw new Error("The mobile application instance is not fully instantiated. Please consider changing the message in the application init event handler.");
            }
        },

        view: function() {
            return this.pane.view();
        },

        skin: function(skin) {
            var that = this;

            if (!arguments.length) {
                return that.options.skin;
            }

            that.options.skin = skin || "";
            that.element[0].className = "km-pane";
            that._setupPlatform();
            that._setupElementClass();

            return that.options.skin;
        },

        destroy: function() {
            this.pane.destroy();
            this.router.destroy();
        },

        _setupPlatform: function() {
            var that = this,
                platform = that.options.platform,
                skin = that.options.skin,
                split = [],
                os = OS || MOBILE_PLATFORMS[DEFAULT_OS];

            if (platform) {
                if (typeof platform === "string") {
                    split = platform.split("-");
                    os = $.extend({ variant: split[1] }, os, MOBILE_PLATFORMS[split[0]]);
                } else {
                    os = platform;
                }
            }

            if (skin) {
                split = skin.split("-");
                os = $.extend({}, os, { skin: split[0], variant: split[1] });
            }

            that.os = os;

            that.osCssClass = osCssClass(that.os, that.options);

            if (os.wp) {
                $(window).off("focusin", refreshBackgroundColor);
                document.removeEventListener("resume", refreshBackgroundColor);

                if (!os.skin) {
                    that.element.parent().css("overflow", "hidden");

                    var refreshBackgroundColor = function() {
                        that.element.removeClass("km-wp-dark km-wp-light").addClass("km-wp-" + wp8Background());
                    };

                    $(window).on("focusin", refreshBackgroundColor); // Restore theme on browser focus (requires click).
                    document.addEventListener("resume", refreshBackgroundColor); // PhoneGap fires resume.

                    refreshBackgroundColor();
                }
            }
        },

        _startHistory: function() {
            this.router = new kendo.Router({ pushState: this.options.pushState, root: this.options.root });
            this.pane.bindToRouter(this.router);
            this.router.start();
        },

        _resizeToScreenHeight: function() {
            var includeStatusBar = $("meta[name=apple-mobile-web-app-status-bar-style]").attr("content").match(/black-translucent|hidden/),
                element = this.element,
                height;

            if (CHROME) {
                height = window.innerHeight;
            } else {
                if (isOrientationHorizontal(element)) {
                    if (includeStatusBar) {
                        height = SCREEN.availWidth;
                    } else {
                        height = SCREEN.availWidth - STATUS_BAR_HEIGHT;
                    }
                } else {
                    if (includeStatusBar) {
                        height = SCREEN.availHeight + STATUS_BAR_HEIGHT;
                    } else {
                        height = SCREEN.availHeight;
                    }
                }
            }

            element.height(height);
        },

        _setupElementClass: function() {
            var that = this, size,
                element = that.element;

            element.parent().addClass("km-root km-" + (that.os.tablet ? "tablet" : "phone"));
            element.addClass(that.osCssClass + " " + getOrientationClass(element));
            if (this.options.useNativeScrolling) {
                element.parent().addClass("km-native-scrolling");
            }

            if (CHROME) {
                element.addClass("km-ios-chrome");
            }

            if (support.wpDevicePixelRatio) {
                element.parent().css("font-size", support.wpDevicePixelRatio + "em");
            }

            if (BERRYPHONEGAP) {
                applyViewportHeight();
            }
            if (that.options.useNativeScrolling) {
                element.parent().addClass("km-native-scrolling");
            } else if (ENABLE_CLIP) {
                size = (screen.availWidth > screen.availHeight ? screen.availWidth : screen.availHeight) + 200;
                $(clipTemplate({ width: size, height: size })).appendTo(HEAD);
            }

            if (BROKEN_WEBVIEW_RESIZE) {
                that._resizeToScreenHeight();
            }

            kendo.onResize(function() {
                element
                    .removeClass("km-horizontal km-vertical")
                    .addClass(getOrientationClass(element));

                if (that.options.useNativeScrolling) {
                    setMinimumHeight(element);
                }

                if (BROKEN_WEBVIEW_RESIZE) {
                    that._resizeToScreenHeight();
                }

                if (BERRYPHONEGAP) {
                    applyViewportHeight();
                }

                kendo.resize(element);
            });
        },

        _clearExistingMeta: function() {
            HEAD.find("meta")
                .filter("[name|='apple-mobile-web-app'],[name|='msapplication-tap'],[name='viewport']")
                .remove();
        },

        _attachMeta: function() {
            var options = this.options,
                icon = options.icon, size;

            this._clearExistingMeta();

            if (!BERRYPHONEGAP) {
                HEAD.prepend(viewportMeta);
            }

            HEAD.prepend(systemMeta(options));

            if (icon) {
                if (typeof icon === "string") {
                    icon = { "" : icon };
                }

                for(size in icon) {
                    HEAD.prepend(iconMeta({ icon: icon[size], size: size }));
                }
            }

            if (options.useNativeScrolling) {
                setMinimumHeight(this.element);
            }
        },

        _attachHideBarHandlers: function() {
            var that = this,
                hideBar = proxy(that, "_hideBar");

            if (support.mobileOS.appMode || !that.options.hideAddressBar || !HIDEBAR || that.options.useNativeScrolling) {
                return;
            }

            that._initialHeight = {};

            WINDOW.on("load", hideBar);

            kendo.onResize(function () {
                setTimeout(window.scrollTo, 0, 0, 1);
            });
        },

        _setupDocumentTitle: function() {
            var that = this,
                defaultTitle = document.title;

            that.pane.bind("viewShow", function(e) {
                var title = e.view.title;
                document.title = title !== undefined ? title : defaultTitle;
            });
        },

        _hideBar: function() {
            var that = this,
                element = that.element;

            element.height(kendo.support.transforms.css + "calc(100% + " + BARCOMPENSATION + "px)");
            $(window).trigger(kendo.support.resize);
        }
    });

    kendo.mobile.Application = Application;
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        ui = mobile.ui,
        SHOW = "show",
        HIDE = "hide",
        OPEN = "open",
        CLOSE = "close",
        WRAPPER = '<div class="km-popup-wrapper" />',
        ARROW = '<div class="km-popup-arrow" />',
        OVERLAY = '<div class="km-popup-overlay" />',
        DIRECTION_CLASSES = "km-up km-down km-left km-right",
        Widget = ui.Widget,
        DIRECTIONS = {
            "down": {
                origin: "bottom center",
                position: "top center"
            },
            "up": {
                origin: "top center",
                position: "bottom center"
            },
            "left": {
                origin: "center left",
                position: "center right",
                collision: "fit flip"
            },
            "right": {
                origin: "center right",
                position: "center left",
                collision: "fit flip"
            }
        },

        ANIMATION = {
            animation: {
                open: {
                    effects: "fade:in",
                    duration: 0
                },
                close: {
                    effects: "fade:out",
                    duration: 400
                }
            }
        },
        DIMENSIONS = {
            "horizontal": { offset: "top", size: "height" },
            "vertical": { offset: "left", size: "width" }
        },

        REVERSE = {
            "up": "down",
            "down": "up",
            "left": "right",
            "right": "left"
        };

    var Popup = Widget.extend({
        init: function(element, options) {
            var that = this,
                containerPopup = element.closest(".km-modalview-wrapper"),
                viewport = element.closest(".km-root").children('.km-pane').first(),
                container = containerPopup[0] ? containerPopup : viewport,
                popupOptions,
                axis;

            if (options.viewport) {
                viewport = options.viewport;
            } else if (!viewport[0]) {
                viewport = window;
            }

            if (options.container) {
                container = options.container;
            } else if (!container[0]) {
                container = document.body;
            }

            popupOptions = {
                viewport: viewport,
                open: function() {
                    that.overlay.show();
                },

                activate: $.proxy(that._activate, that),

                deactivate: function() {
                    that.overlay.hide();
                    that.trigger(HIDE);
                }
            };

            Widget.fn.init.call(that, element, options);

            element = that.element;
            options = that.options;

            element.wrap(WRAPPER).addClass("km-popup").show();

            axis = that.options.direction.match(/left|right/) ? "horizontal" : "vertical";

            that.dimensions = DIMENSIONS[axis];

            that.wrapper = element.parent().css({
                width: options.width,
                height: options.height
            }).addClass("km-popup-wrapper km-" + options.direction).hide();

            that.arrow = $(ARROW).prependTo(that.wrapper).hide();

            that.overlay = $(OVERLAY).appendTo(container).hide();
            popupOptions.appendTo = that.overlay;
            popupOptions.copyAnchorStyles = false;
            popupOptions.autosize = true;

            if (options.className) {
                that.overlay.addClass(options.className);
            }

            that.popup = new kendo.ui.Popup(that.wrapper, $.extend(true, popupOptions, ANIMATION, DIRECTIONS[options.direction]));
        },

        options: {
            name: "Popup",
            width: 240,
            height: "",
            direction: "down",
            container: null,
            viewport: null
        },

        events: [
            SHOW,
            HIDE
        ],

        show: function(target) {
            var that = this,
                popup = that.popup;

            popup.options.anchor = $(target);
            popup.open();
        },

        target: function() {
            return this.popup.options.anchor;
        },

        hide: function() {
            this.popup.close();
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.popup.destroy();
            this.overlay.remove();
        },

        _activate: function() {
            var that = this,
                direction = that.options.direction,
                dimensions = that.dimensions,
                offset = dimensions.offset,
                popup = that.popup,
                anchor = popup.options.anchor,
                anchorOffset = $(anchor).offset(),
                elementOffset = $(popup.element).offset(),
                cssClass = popup.flipped ? REVERSE[direction] : direction,
                offsetAmount = anchorOffset[offset] - elementOffset[offset] + ($(anchor)[dimensions.size]() / 2);

            that.wrapper.removeClass(DIRECTION_CLASSES).addClass("km-" + cssClass);
            that.arrow.css(offset, offsetAmount).show();

            that.trigger(SHOW);
        }
    });

    var PopOver = Widget.extend({
        init: function(element, options) {
            var that = this,
                popupOptions;

            that.initialOpen = false;

            Widget.fn.init.call(that, element, options);

            popupOptions = $.extend({
                className: "km-popover-root",
                "show": function() { that.trigger(OPEN, { target: that.popup.target() }); },
                "hide": function() { that.trigger(CLOSE); }
            }, this.options.popup);

            that.popup = new Popup(that.element, popupOptions);

            that.pane = new ui.Pane(that.element, this.options.pane);
            that.pane.navigateToInitial();

            kendo.notify(that, ui);
        },

        options: {
            name: "PopOver",
            popup: { },
            pane: { }
        },

        events: [
            OPEN,
            CLOSE
        ],

        open: function(target) {
            this.openFor(target);
        },

        openFor: function(target) {
            this.popup.show(target);
            if (!this.initialOpen) {
                this.pane.navigate("");
                this.popup.popup._position();
                this.initialOpen = true;
            }
        },

        close: function() {
            this.popup.hide();
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.pane.destroy();
            this.popup.destroy();

            kendo.destroy(this.element);
        }
    });

    ui.plugin(Popup);
    ui.plugin(PopOver);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Popup = kendo.ui.Popup,
        SHIM = '<div class="km-shim"/>',
        Widget = ui.Widget;

    var Shim = Widget.extend({
        init: function(element, options) {
            var that = this,
                app = kendo.mobile.application,
                os = kendo.support.mobileOS,
                osname = app ? app.os.name : (os ? os.name : "ios"),
                ioswp = osname === "ios" || osname === "wp" || (app ? app.os.skin : false),
                bb = osname === "blackberry",
                align = options.align || (ioswp ?  "bottom center" : bb ? "center right" : "center center"),
                position = options.position || (ioswp ? "bottom center" : bb ? "center right" : "center center"),
                effect = options.effect || (ioswp ? "slideIn:up" : bb ? "slideIn:left" : "fade:in"),
                shim = $(SHIM).handler(that).hide();

            Widget.fn.init.call(that, element, options);

            that.shim = shim;
            element = that.element;
            options = that.options;

            if (options.className) {
                that.shim.addClass(options.className);
            }

            if (!options.modal) {
                that.shim.on("up", "hide");
            }

            (app ? app.element : $(document.body)).append(shim);

            that.popup = new Popup(that.element, {
                anchor: shim,
                modal: true,
                appendTo: shim,
                origin: align,
                position: position,
                animation: {
                    open: {
                        effects: effect,
                        duration: options.duration
                    },
                    close: {
                        duration: options.duration
                    }
                },
                deactivate: function() {
                    shim.hide();
                },
                open: function() {
                    shim.show();
                }
            });

            kendo.notify(that);
        },

        options: {
            name: "Shim",
            modal: false,
            align: undefined,
            position: undefined,
            effect: undefined,
            duration: 200
        },

        show: function() {
            this.popup.open();
        },

        hide: function(e) {
            if (!e || !$.contains(this.shim.children().children(".k-popup")[0], e.target)) {
                this.popup.close();
            }
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.shim.kendoDestroy();
            this.popup.destroy();
            this.shim.remove();
        }
    });

    ui.plugin(Shim);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Shim = ui.Shim,
        Widget = ui.Widget,
        OPEN = "open",
        CLOSE = "close",
        INIT = "init",
        WRAP = '<div class="km-modalview-wrapper" />';

    var ModalView = ui.View.extend({
        init: function(element, options) {
            var that = this, width, height;

            Widget.fn.init.call(that, element, options);

            element = that.element;
            options = that.options;

            width = element[0].style.width || "auto";
            height = element[0].style.height || "auto";

            element.addClass("km-modalview").wrap(WRAP);

            that.wrapper = element.parent().css({
                width: options.width || width || 300,
                height: options.height || height || 300
            });

            element.css({ width: "", height: "" });

            that.shim = new Shim(that.wrapper, {
                modal: options.modal,
                position: "center center",
                align: "center center",
                effect: "fade:in",
                className: "km-modalview-root"
            });

            if (kendo.support.mobileOS.wp) {
                that.shim.shim.on("click", false);
            }

            that._layout();
            that._scroller();
            that._model();
            that.element.css("display", "");

            that.trigger(INIT);
        },

        events: [
            INIT,
            OPEN,
            CLOSE
        ],

        options: {
            name: "ModalView",
            modal: true,
            width: null,
            height: null
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.shim.destroy();
        },

        open: function(target) {
            var that = this;
            that.target = $(target);
            that.shim.show();
            that.trigger("show", { view: that });
        },

        // Interface implementation, called from the pane click handlers
        openFor: function(target) {
            this.open(target);
            this.trigger(OPEN, { target: target });
        },

        close: function() {
            this.shim.hide();
            this.trigger(CLOSE);
        }
    });

    ui.plugin(ModalView);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        os = kendo.support.mobileOS,
        Transition = kendo.effects.Transition,
        roleSelector = kendo.roleSelector,
        AXIS = "x",
        ui = mobile.ui,
        SWIPE_TO_OPEN = !(os.ios && os.majorVersion == 7 && !os.appMode),
        BEFORE_SHOW = "beforeShow",
        INIT = "init",
        SHOW = "show",
        HIDE = "hide",
        NULL_VIEW = { enable: $.noop };

    var Drawer = ui.View.extend({
        init: function(element, options) {
            // move the drawer to the top, in order to hide it
            $(element).parent().prepend(element);

            mobile.ui.Widget.fn.init.call(this, element, options);

            this._layout();
            this._scroller();
            this._model();

            var pane = this.element.closest(roleSelector("pane")).data("kendoMobilePane"),
                userEvents;

            if (pane) {
                this.pane = pane;
                this.pane.bind("viewShow", function(e) {
                    drawer._viewShow(e);
                });

                this.pane.bind("sameViewRequested", function() {
                    drawer.hide();
                });

                userEvents = this.userEvents = new kendo.UserEvents(pane.element, {
                    filter: roleSelector("view splitview"),
                    allowSelection: true
                });

            } else {
                this.currentView = NULL_VIEW;
                var container = $(this.options.container);

                if (!container) {
                    throw new Error("The drawer needs a container configuration option set.");
                }

                userEvents = this.userEvents = new kendo.UserEvents(container, { allowSelection: true });
                this._attachTransition(container);
            }

            var drawer = this;

            var hide = function(e) {
                if (drawer.visible) {
                    drawer.hide();
                    e.preventDefault();
                }
            };

            if (this.options.swipeToOpen && SWIPE_TO_OPEN) {
                userEvents.bind("press", function(e) { drawer.transition.cancel(); });
                userEvents.bind("start", function(e) { drawer._start(e); });
                userEvents.bind("move", function(e) { drawer._update(e); });
                userEvents.bind("end", function(e) { drawer._end(e); });
                userEvents.bind("tap", hide);
            } else {
                userEvents.bind("press", hide);
            }

            this.leftPositioned = this.options.position === "left";

            this.visible = false;

            this.element.hide().addClass("km-drawer").addClass(this.leftPositioned ? "km-left-drawer" : "km-right-drawer");
            this.trigger(INIT);
        },

        options: {
            name: "Drawer",
            position: "left",
            views: [],
            swipeToOpen: true,
            title: "",
            container: null
        },

        events: [
            BEFORE_SHOW,
            HIDE,
            INIT,
            SHOW
        ],

        show: function() {
            if (this._activate()) {
                this._show();
            }
        },

        hide: function() {
            if (!this.currentView) {
                return;
            }

            this.currentView.enable();

            Drawer.current = null;
            this._moveViewTo(0);
            this.trigger(HIDE, { view: this });
        },

        // Alias in order to support popover/modalview etc. interface
        openFor: function() {
            if (this.visible) {
                this.hide();
            } else {
                this.show();
            }
        },

        destroy: function() {
            ui.View.fn.destroy.call(this);
            this.userEvents.destroy();
        },

        _activate: function() {
            if (this.visible) {
                return true;
            }

            var view,
                visibleOnCurrentView = true,
                views = this.options.views;

            if (this.pane && views.length) {
                view = this.pane.view();
                visibleOnCurrentView = this._viewsInclude(view.id.replace('#', '')) || this._viewsInclude(view.element.attr("id"));
            }

            if (this.trigger(BEFORE_SHOW, { view: this }) || !visibleOnCurrentView) {
                return false;
            }

            this._setAsCurrent();
            this.element.show();

            this.trigger(SHOW, { view: this });
            return true;
        },

        _viewsInclude: function(id) {
            return this.options.views.indexOf(id) > -1;
        },

        _show: function() {
            this.currentView.enable(false);

            this.visible = true;
            var offset = this.element.width();

            if (!this.leftPositioned) {
                offset = -offset;
            }

            this._moveViewTo(offset);
        },

        _setAsCurrent: function() {
            if (Drawer.last !== this) {
                if (Drawer.last) {
                    Drawer.last.element.hide();
                }
                this.element.show();
            }

            Drawer.last = this;
            Drawer.current = this;
        },

        _moveViewTo: function(offset) {
            this.userEvents.cancel();
            this.transition.moveTo({ location: offset, duration: 400, ease: Transition.easeOutExpo });
        },

        _viewShow: function(e) {
            if (this.currentView) {
                this.currentView.enable();
            }

            if (this.currentView === e.view) {
                this.hide();
                return;
            }

            this.currentView = e.view;
            this._attachTransition(e.view.element);
        },

        _attachTransition: function(element) {
            var that = this,
                movable = this.movable,
                currentOffset = movable && movable.x;


            if (this.transition) {
                this.transition.cancel();
                this.movable.moveAxis("x", 0);
            }

            movable = this.movable = new kendo.ui.Movable(element);

            this.transition = new Transition({
                axis: AXIS,
                movable: this.movable,
                onEnd: function() {
                    if (movable[AXIS] === 0) {
                        element[0].style.cssText = "";
                        that.element.hide();
                        that.visible = false;
                    }
                }
            });

            if (currentOffset) {
                kendo.animationFrame(function() {
                    that.movable.moveAxis(AXIS, currentOffset);
                    that.hide();
                });
            }
        },

        _start: function(e) {
            var userEvents = e.sender;

            // ignore non-horizontal swipes
            if (Math.abs(e.x.velocity) < Math.abs(e.y.velocity) || kendo.triggeredByInput(e.event)) {
                userEvents.cancel();
                return;
            }

            var leftPositioned = this.leftPositioned,
                visible = this.visible,
                canMoveLeft = leftPositioned && visible || !leftPositioned && !Drawer.current,
                canMoveRight = !leftPositioned && visible || leftPositioned && !Drawer.current,
                leftSwipe = e.x.velocity < 0;

            if ((canMoveLeft && leftSwipe) || (canMoveRight && !leftSwipe)) {
                if (this._activate()) {
                    userEvents.capture();
                    return;
                }
            }

            userEvents.cancel();
        },

        _update: function(e) {
            var movable = this.movable,
                newPosition = movable.x + e.x.delta,
                limitedPosition;

            if (this.leftPositioned) {
                limitedPosition = Math.min(Math.max(0, newPosition), this.element.width());
            } else {
                limitedPosition = Math.max(Math.min(0, newPosition), -this.element.width());
            }

            this.movable.moveAxis(AXIS, limitedPosition);
            e.event.preventDefault();
            e.event.stopPropagation();
        },

        _end: function(e) {
            var velocity = e.x.velocity,
                pastHalf = Math.abs(this.movable.x) > this.element.width() / 2,
                velocityThreshold = 0.8,
                shouldShow;

            if (this.leftPositioned) {
                shouldShow = velocity > -velocityThreshold && (velocity > velocityThreshold || pastHalf);
            } else {
                shouldShow = velocity < velocityThreshold && (velocity < -velocityThreshold || pastHalf);
            }

            if(shouldShow) {
                this._show();
            } else {
                this.hide();
            }
        }
    });

    ui.plugin(Drawer);
})(window.kendo.jQuery);

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

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        ui = mobile.ui,
        Widget = ui.Widget,
        support = kendo.support,
        os = support.mobileOS,
        ANDROID3UP = os.android && os.flatVersion >= 300,
        CLICK = "click",
        DISABLED = "disabled",
        DISABLEDSTATE = "km-state-disabled";

    function highlightButton(widget, event, highlight) {
        $(event.target).closest(".km-button,.km-detail").toggleClass("km-state-active", highlight);

        if (ANDROID3UP && widget.deactivateTimeoutID) {
            clearTimeout(widget.deactivateTimeoutID);
            widget.deactivateTimeoutID = 0;
        }
    }

    function createBadge(value) {
        return $('<span class="km-badge">' + value + '</span>');
    }

    var Button = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            that._wrap();
            that._style();

            that.options.enable = that.options.enable && !that.element.attr(DISABLED);
            that.enable(that.options.enable);

            that._userEvents = new kendo.UserEvents(that.element, {
                press: function(e) { that._activate(e); },
                tap: function(e) { that._release(e); },
                release: function(e) { highlightButton(that, e, false); },
                // prevent the navigation when native scrolling is present
                end: function(e) {
                    if (kendo.mobile.appLevelNativeScrolling() || that.viewHasNativeScrolling()) {
                        e.preventDefault();
                    }
                }
            });

            if (ANDROID3UP) {
                that.element.on("move", function(e) { that._timeoutDeactivate(e); });
            }
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this._userEvents.destroy();
        },

        events: [
            CLICK
        ],

        options: {
            name: "Button",
            icon: "",
            style: "",
            badge: "",
            enable: true
        },

        badge: function (value) {
            var badge = this.badgeElement = this.badgeElement || createBadge(value).appendTo(this.element);

            if (value || value === 0) {
                badge.html(value);
                return this;
            }

            if (value === false) {
                badge.empty().remove();
                this.badgeElement = false;
                return this;
            }

            return badge.html();
        },

        enable: function(enable) {
            var element = this.element;

            if(typeof enable == "undefined") {
                enable = true;
            }

            this.options.enable = enable;

            if(enable) {
                element.removeAttr(DISABLED);
            } else {
                element.attr(DISABLED, DISABLED);
            }

            element.toggleClass(DISABLEDSTATE, !enable);
        },

        _timeoutDeactivate: function(e) {
            if (!this.deactivateTimeoutID) {
                this.deactivateTimeoutID = setTimeout(highlightButton, 500, this, e, false);
            }
        },

        _activate: function(e) {
            var activeElement = document.activeElement,
                nodeName = activeElement ? activeElement.nodeName : "";

            if(this.options.enable) {
                highlightButton(this, e, true);

                if (nodeName == "INPUT" || nodeName == "TEXTAREA") {
                    activeElement.blur(); // Hide device keyboard
                }
            }
        },

        _release: function(e) {
            var that = this;

            if (e.which > 1) {
                return;
            }

            if(!that.options.enable) {
                e.preventDefault();
                return;
            }

            if (that.trigger(CLICK, {target: $(e.target), button: that.element})) {
                e.preventDefault();
            }
        },

        _style: function() {
            var style = this.options.style,
                element = this.element,
                styles;

            if (style) {
                styles = style.split(" ");
                $.each(styles, function() {
                    element.addClass("km-" + this);
                });
            }
        },

        _wrap: function() {
            var that = this,
                icon = that.options.icon,
                badge = that.options.badge,
                iconSpan = '<span class="km-icon km-' + icon,
                element = that.element.addClass("km-button"),
                span = element.children("span:not(.km-icon)").addClass("km-text"),
                image = element.find("img").addClass("km-image");

            if (!span[0] && element.html()) {
                span = element.wrapInner('<span class="km-text" />').children("span.km-text");
            }

            if (!image[0] && icon) {
                if (!span[0]) {
                    iconSpan += " km-notext";
                }
                that.iconElement = element.prepend($(iconSpan + '" />'));
            }

            if (badge || badge === 0) {
                that.badgeElement = createBadge(badge).appendTo(element);
            }
        }
    });

    var BackButton = Button.extend({
        options: {
            name: "BackButton",
            style: "back"
        },

        init: function(element, options) {
            var that = this;
            Button.fn.init.call(that, element, options);

            if (typeof that.element.attr("href") === "undefined") {
                that.element.attr("href", "#:back");
            }
        }
    });

    var DetailButton = Button.extend({
        options: {
            name: "DetailButton",
            style: ""
        },

        init: function(element, options) {
            Button.fn.init.call(this, element, options);
        },

        _style: function() {
            var style = this.options.style + " detail",
                element = this.element;

            if (style) {
                var styles = style.split(" ");
                $.each(styles, function() {
                    element.addClass("km-" + this);
                });
            }
        },

        _wrap: function() {
            var that = this,
                icon = that.options.icon,
                iconSpan = '<span class="km-icon km-' + icon,
                element = that.element,
                span = element.children("span"),
                image = element.find("img").addClass("km-image");

            if (!image[0] && icon) {
                if (!span[0]) {
                    iconSpan += " km-notext";
                }
                element.prepend($(iconSpan + '" />'));
            }
        }

    });

    ui.plugin(Button);
    ui.plugin(BackButton);
    ui.plugin(DetailButton);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Widget = ui.Widget,
        ACTIVE = "km-state-active",
        SELECT = "select",
        SELECTOR = "li:not(." + ACTIVE +")";

    function createBadge(value) {
        return $('<span class="km-badge">' + value + '</span>');
    }

    var ButtonGroup = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            that.element.addClass("km-buttongroup").find("li").each(that._button);

            that.element.on(that.options.selectOn, SELECTOR, "_select");

            that.select(that.options.index);
        },

        events: [
            SELECT
        ],

        options: {
            name: "ButtonGroup",
            selectOn: "down",
            index: -1
        },

        current: function() {
            return this.element.find("." + ACTIVE);
        },

        select: function (li) {
            var that = this,
                index = -1;

            if (li === undefined || li === -1) {
                return;
            }

            that.current().removeClass(ACTIVE);

            if (typeof li === "number") {
                index = li;
                li = $(that.element[0].children[li]);
            } else if (li.nodeType) {
                li = $(li);
                index = li.index();
            }

            li.addClass(ACTIVE);
            that.selectedIndex = index;
        },

        badge: function(item, value) {
            var buttongroup = this.element, badge;

            if (!isNaN(item)) {
                item = buttongroup.children().get(item);
            }

            item = buttongroup.find(item);
            badge = $(item.children(".km-badge")[0] || createBadge(value).appendTo(item));

            if (value || value === 0) {
                badge.html(value);
                return this;
            }

            if (value === false) {
                badge.empty().remove();
                return this;
            }

            return badge.html();
        },

        _button: function() {
            var button = $(this).addClass("km-button"),
                icon = kendo.attrValue(button, "icon"),
                badge = kendo.attrValue(button, "badge"),
                span = button.children("span"),
                image = button.find("img").addClass("km-image");

            if (!span[0]) {
                span = button.wrapInner("<span/>").children("span");
            }

            span.addClass("km-text");

            if (!image[0] && icon) {
                button.prepend($('<span class="km-icon km-' + icon + '"/>'));
            }

            if (badge || badge === 0) {
                createBadge(badge).appendTo(button);
            }
        },

        _select: function(e) {
            if (e.which > 1 || e.isDefaultPrevented()) {
                return;
            }

            this.select(e.currentTarget);
            this.trigger(SELECT, { index: this.selectedIndex });
        }
    });

    ui.plugin(ButtonGroup);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        Node = window.Node,
        mobile = kendo.mobile,
        ui = mobile.ui,
        DataSource = kendo.data.DataSource,
        Widget = ui.Widget,
        ITEM_SELECTOR = ".km-list > li, > li:not(.km-group-container)",
        HIGHLIGHT_SELECTOR = ".km-listview-link, .km-listview-label",
        ICON_SELECTOR = "[" + kendo.attr("icon") + "]",
        proxy = $.proxy,
        attrValue = kendo.attrValue,
        GROUP_CLASS = "km-group-title",
        ACTIVE_CLASS = "km-state-active",
        GROUP_WRAPPER = '<div class="' + GROUP_CLASS + '"><div class="km-text"></div></div>',
        GROUP_TEMPLATE = kendo.template('<li><div class="' + GROUP_CLASS + '"><div class="km-text">#= this.headerTemplate(data) #</div></div><ul>#= kendo.render(this.template, data.items)#</ul></li>'),
        WRAPPER = '<div class="km-listview-wrapper" />',
        SEARCH_TEMPLATE = kendo.template('<form class="km-filter-form"><div class="km-filter-wrap"><input type="search" placeholder="#=placeholder#"/><a href="\\#" class="km-filter-reset" title="Clear"><span class="km-icon km-clear"></span><span class="km-text">Clear</span></a></div></form>'),
        NS = ".kendoMobileListView",
        STYLED = "styled",
        DATA_BOUND = "dataBound",
        DATA_BINDING = "dataBinding",
        ITEM_CHANGE = "itemChange",
        CLICK = "click",
        CHANGE = "change",
        PROGRESS = "progress",
        FUNCTION = "function",

        whitespaceRegExp = /^\s+$/,
        buttonRegExp = /button/;

    function whitespace() {
        return this.nodeType === Node.TEXT_NODE && this.nodeValue.match(whitespaceRegExp);
    }

    function addIcon(item, icon) {
        if (icon && !item[0].querySelector(".km-icon")) {
            item.prepend('<span class="km-icon km-' + icon + '"/>');
        }
    }

    function enhanceItem(item) {
        addIcon(item, attrValue(item, "icon"));
        addIcon(item, attrValue(item.children(ICON_SELECTOR), "icon"));
    }

    function enhanceLinkItem(item) {
        var parent = item.parent(),
            itemAndDetailButtons = item.add(parent.children(kendo.roleSelector("detailbutton"))),
            otherNodes = parent.contents().not(itemAndDetailButtons).not(whitespace);

        if (otherNodes.length) {
            return;
        }

        item.addClass("km-listview-link")
            .attr(kendo.attr("role"), "listview-link");

        addIcon(item, attrValue(parent, "icon"));
        addIcon(item, attrValue(item, "icon"));
    }

    function enhanceCheckBoxItem(label) {
        if (!label[0].querySelector("input[type=checkbox],input[type=radio]")) {
            return;
        }

        var item = label.parent();

        if (item.contents().not(label).not(function() { return this.nodeType == 3; })[0]) {
            return;
        }

        label.addClass("km-listview-label");

        label.children("[type=checkbox],[type=radio]").addClass("km-widget km-icon km-check");
    }

    function putAt(element, top) {
        $(element).css('transform', 'translate3d(0px, ' + top + 'px, 0px)');
    }

    var HeaderFixer = kendo.Class.extend({
        init: function(listView) {
            var scroller = listView.scroller();

            if (!scroller) {
                return;
            }

            this.options = listView.options;
            this.element = listView.element;
            this.scroller = listView.scroller();
            this._shouldFixHeaders();

            var headerFixer = this;

            var cacheHeaders = function() {
                headerFixer._cacheHeaders();
            };

            listView.bind("resize", cacheHeaders);

            listView.bind(STYLED, cacheHeaders);
            listView.bind(DATA_BOUND, cacheHeaders);

            scroller.bind("scroll", function(e) {
                headerFixer._fixHeader(e);
            });
        },

        _fixHeader: function(e) {
            if (!this.fixedHeaders) {
                return;
            }

            var i = 0,
                scroller = this.scroller,
                headers = this.headers,
                scrollTop = e.scrollTop,
                headerPair,
                offset,
                header;

            do {
                headerPair = headers[i++];
                if (!headerPair) {
                    header = $("<div />");
                    break;
                }
                offset = headerPair.offset;
                header = headerPair.header;
            } while (offset + 1 > scrollTop);

            if (this.currentHeader != i) {
                scroller.fixedContainer.html(header.clone());
                this.currentHeader = i;
            }
        },

        _shouldFixHeaders: function() {
            this.fixedHeaders = this.options.type === "group" && this.options.fixedHeaders;
        },

        _cacheHeaders: function() {
            this._shouldFixHeaders();

            if (!this.fixedHeaders) {
                return;
            }

            var headers = [], offset = this.scroller.scrollTop;

            this.element.find("." + GROUP_CLASS).each(function(_, header) {
                header = $(header);
                headers.unshift({
                    offset: header.position().top + offset,
                    header: header
                });
            });

            this.headers = headers;
            this._fixHeader({ scrollTop: offset });
        }
    });

    var DEFAULT_PULL_PARAMETERS = function() {
        return { page: 1 };
    };

    var RefreshHandler = kendo.Class.extend({
        init: function(listView) {
            var handler = this,
                options = listView.options,
                scroller = listView.scroller(),
                pullParameters = options.pullParameters || DEFAULT_PULL_PARAMETERS;

            this.listView = listView;
            this.scroller = scroller;

            listView.bind("_dataSource", function(e) {
                handler.setDataSource(e.dataSource);
            });

            scroller.setOptions({
                pullToRefresh: true,
                pull: function() {
                    handler._pulled = true;
                    handler.dataSource.read(pullParameters.call(listView, handler._first));
                },
                pullTemplate: options.pullTemplate,
                releaseTemplate: options.releaseTemplate,
                refreshTemplate: options.refreshTemplate
            });
        },

        setDataSource: function(dataSource) {
            var handler = this;

            this._first = dataSource.view()[0];
            this.dataSource = dataSource;

            dataSource.bind("change", function() {
                handler._change();
            });
        },

        _change: function() {
            var scroller = this.scroller,
                dataSource = this.dataSource;

            if (this._pulled) {
                scroller.pullHandled();
            }

            if (this._pulled || !this._first) {
                var view = dataSource.view();

                if (view[0]) {
                    this._first = view[0];
                }
            }

            this._pulled = false;
        }
    });

    var VirtualList = kendo.Observable.extend({
        init: function(options) {
            var list = this;

            kendo.Observable.fn.init.call(list);

            list.buffer = options.buffer;
            list.height = options.height;
            list.item = options.item;
            list.items = [];
            list.footer = options.footer;

            list.buffer.bind("reset", function() {
                list.refresh();
            });

        },

        refresh: function() {
            var buffer = this.buffer,
                items = this.items,
                endReached = false;

            while(items.length) {
                items.pop().destroy();
            }

            this.offset = buffer.offset;

            var itemConstructor = this.item,
                prevItem,
                item;

            for (var idx = 0; idx < buffer.viewSize; idx ++) {
                if (idx === buffer.total()) {
                    endReached = true;
                    break;
                }
                item = itemConstructor(this.content(this.offset + items.length));
                item.below(prevItem);
                prevItem = item;
                items.push(item);
            }

            this.itemCount = items.length;

            this.trigger("reset");

            this._resize();

            if (endReached) {
                this.trigger("endReached");
            }
        },

        totalHeight: function() {
            if (!this.items[0]) {
                return 0;
            }

            var list = this,
                items = list.items,
                top = items[0].top,
                bottom = items[items.length - 1].bottom,
                averageItemHeight = (bottom - top) / list.itemCount,
                remainingItemsCount = list.buffer.length - list.offset - list.itemCount;

            return (this.footer ? this.footer.height : 0) + bottom + remainingItemsCount * averageItemHeight;
        },

        batchUpdate: function(top) {
            var height = this.height(),
                items = this.items,
                item,
                initialOffset = this.offset;

            if (!items[0]) {
                return;
            }

            if (this.lastDirection) { // scrolling up
                while(items[items.length - 1].bottom > top + height * 2) {
                    if (this.offset === 0) {
                        break;
                    }

                    this.offset --;
                    item = items.pop();
                    item.update(this.content(this.offset));
                    item.above(items[0]);
                    items.unshift(item);
                }
            } else { // scrolling down
                while (items[0].top < top - height) {
                    var nextIndex = this.offset + this.itemCount; // here, it should be offset + 1 + itemCount - 1.

                    if (nextIndex === this.buffer.total()) {
                        this.trigger("endReached");
                        break;
                    }

                    if (nextIndex === this.buffer.length) {
                        break;
                    }

                    item = items.shift();
                    item.update(this.content(this.offset + this.itemCount));
                    item.below(items[items.length - 1]);
                    items.push(item);
                    this.offset ++;
                }
            }

            if (initialOffset !== this.offset) {
                this._resize();
            }
        },

        update: function(top) {
            var list = this,
                items = this.items,
                item,
                firstItem,
                lastItem,
                height = this.height(),
                itemCount = this.itemCount,
                padding = height / 2,
                up = (this.lastTop || 0) > top,
                topBorder = top - padding,
                bottomBorder = top + height + padding;

            if (!items[0]) {
                return;
            }

            this.lastTop = top;
            this.lastDirection = up;

            if (up) { // scrolling up
               if (items[0].top > topBorder &&  // needs reorder
                   items[items.length - 1].bottom > bottomBorder + padding && // enough padding below
                   this.offset > 0 // we are not at the top
                  )
               {
                    this.offset --;
                    item = items.pop();
                    firstItem = items[0];
                    item.update(this.content(this.offset));
                    items.unshift(item);

                    item.above(firstItem);
                    list._resize();
               }
            } else { // scrolling down
                if (
                    items[items.length - 1].bottom < bottomBorder && // needs reorder
                    items[0].top < topBorder - padding // enough padding above
                )
                {
                    var nextIndex = this.offset + itemCount; // here, it should be offset + 1 + itemCount - 1.

                    if (nextIndex === this.buffer.total()) {
                        this.trigger("endReached");
                    } else if (nextIndex !== this.buffer.length) {
                        item = items.shift();
                        lastItem = items[items.length - 1];
                        items.push(item);
                        item.update(this.content(this.offset + this.itemCount));
                        list.offset ++;

                        item.below(lastItem);
                        list._resize();
                    }
                }
            }
        },

        content: function(index) {
            return this.buffer.at(index);
        },

        destroy: function() {
            this.unbind();
        },

        _resize: function() {
            var items = this.items,
                top = 0,
                bottom = 0,
                firstItem = items[0],
                lastItem = items[items.length - 1];

            if (firstItem) {
                top = firstItem.top;
                bottom = lastItem.bottom;
            }

            this.trigger("resize", { top: top, bottom: bottom });

            if (this.footer) {
                this.footer.below(lastItem);
            }
        }
    });

    // export for testing purposes
    kendo.mobile.ui.VirtualList = VirtualList;

    var VirtualListViewItem = kendo.Class.extend({
        init: function(listView, dataItem) {
            var element = listView.append([dataItem])[0],
                height = element.offsetHeight;

            $.extend(this, {
                top: 0,
                element: element,
                listView: listView,
                height: height,
                bottom: height
            });
        },

        update: function(dataItem) {
            this.element = this.listView.setDataItem(this.element, dataItem);
        },

        above: function(item) {
            if (item) {
                this.height = this.element.offsetHeight;
                this.top = item.top - this.height;
                this.bottom = item.top;
                putAt(this.element, this.top);
            }
        },

        below: function(item) {
            if (item) {
                this.height = this.element.offsetHeight;
                this.top = item.bottom;
                this.bottom = this.top + this.height;
                putAt(this.element, this.top);
            }
        },

        destroy: function() {
            kendo.destroy(this.element);
            $(this.element).remove();
        }
    });

    var LOAD_ICON = '<div><span class="km-icon"></span><span class="km-loading-left"></span><span class="km-loading-right"></span></div>';
    var VirtualListViewLoadingIndicator = kendo.Class.extend({
        init: function(listView) {
            this.element = $('<li class="km-load-more km-scroller-refresh" style="display: none"></li>').appendTo(listView.element);
            this._loadIcon = $(LOAD_ICON).appendTo(this.element);
        },

        enable: function() {
            this.element.show();
            this.height = this.element.outerHeight(true);
        },

        disable: function() {
            this.element.hide();
            this.height = 0;
        },

        below: function(item) {
            if (item) {
                this.top = item.bottom;
                this.bottom = this.height + this.top;
                putAt(this.element, this.top);
            }
        }
    });

    var VirtualListViewPressToLoadMore = VirtualListViewLoadingIndicator.extend({
        init: function(listView, buffer) {

            this._loadIcon = $(LOAD_ICON).hide();
            this._loadButton = $('<a class="km-load">' + listView.options.loadMoreText + '</a>').hide();
            this.element = $('<li class="km-load-more" style="display: none"></li>').append(this._loadIcon).append(this._loadButton).appendTo(listView.element);

            var loadMore = this;

            this._loadButton.kendoMobileButton().data("kendoMobileButton").bind("click", function() {
                loadMore._hideShowButton();
                buffer.next();
            });

            buffer.bind("resize", function() {
                loadMore._showLoadButton();
            });

            this.height = this.element.outerHeight(true);
            this.disable();
        },

        _hideShowButton: function() {
            this._loadButton.hide();
            this.element.addClass("km-scroller-refresh");
            this._loadIcon.css('display', 'block');
        },

        _showLoadButton: function() {
            this._loadButton.show();
            this.element.removeClass("km-scroller-refresh");
            this._loadIcon.hide();
        }
    });

    var VirtualListViewItemBinder = kendo.Class.extend({
        init: function(listView) {
            var binder = this;

            this.chromeHeight = listView.wrapper.children().not(listView.element).outerHeight() || 0;
            this.listView = listView;
            this.scroller = listView.scroller();
            this.options = listView.options;

            listView.bind("_dataSource", function(e) {
                binder.setDataSource(e.dataSource, e.empty);
            });

            listView.bind("resize", function() {
                if (!binder.list.items.length) {
                    return;
                }

                binder.scroller.reset();
                binder.buffer.range(0);
                binder.list.refresh();
            });

            this.scroller.makeVirtual();

            this.scroller.bind("scroll", function(e) {
                binder.list.update(e.scrollTop);
            });

            this.scroller.bind("scrollEnd", function(e) {
                binder.list.batchUpdate(e.scrollTop);
            });
        },

        destroy: function() {
            this.list.unbind();
            this.buffer.unbind();
        },

        setDataSource: function(dataSource, empty) {
            var binder = this,
                options = this.options,
                listView = this.listView,
                scroller = listView.scroller(),
                pressToLoadMore = options.loadMore,
                pageSize,
                buffer,
                footer;

            if (this.dataSource) {
                this._unbindDataSource();
            }

            this.dataSource = dataSource;

            pageSize = dataSource.pageSize() || options.virtualViewSize;

            if (!pageSize && !empty) {
                throw new Error("the DataSource does not have page size configured. Page Size setting is mandatory for the mobile listview virtual scrolling to work as expected.");
            }

            if (this.buffer) {
                this.buffer.destroy();
            }

            buffer = new kendo.data.Buffer(dataSource, Math.floor(pageSize / 2), pressToLoadMore);

            if (pressToLoadMore) {
                footer = new VirtualListViewPressToLoadMore(listView, buffer);
            } else {
                footer = new VirtualListViewLoadingIndicator(listView);
            }

            if (this.list) {
                this.list.destroy();
            }

            var list = new VirtualList({
                buffer: buffer,
                footer: footer,
                item: function(dataItem) { return new VirtualListViewItem(listView, dataItem); },
                height: function() { return scroller.height(); }
            });

            list.bind("resize", function() {
                binder.updateScrollerSize();
            });

            list.bind("reset", function() {
                binder.footer.enable();
            });

            list.bind("endReached", function() {
                footer.disable();
                binder.updateScrollerSize();
            });

            buffer.bind("expand", function() {
                list.lastDirection = false; // expand down
                list.batchUpdate(scroller.scrollTop);
            });

            $.extend(this, {
                buffer: buffer,
                scroller: scroller,
                list: list,
                footer: footer
            });
        },

        updateScrollerSize: function() {
            this.scroller.virtualSize(0, this.list.totalHeight() + this.chromeHeight);
        },

        refresh: function() {
            this.list.refresh();
        },

        reset: function() {
            this.buffer.range(0);
            this.list.refresh();
        },

        _unbindDataSource: function() {
            // TODO:
        }
    });

    var ListViewItemBinder = kendo.Class.extend({
        init: function(listView) {
            var binder = this;
            this.listView = listView;
            this.options = listView.options;

            var itemBinder = this;

            this._refreshHandler = function(e) {
                itemBinder.refresh(e);
            };

            this._progressHandler = function() {
                listView.showLoading();
            };

            listView.bind("_dataSource", function(e) {
                binder.setDataSource(e.dataSource);
            });
        },

        destroy: function() { },

        reset: function() { },

        refresh: function(e) {
            var action = e && e.action,
                dataItems = e && e.items,
                listView = this.listView,
                dataSource = this.dataSource,
                prependOnRefresh = this.options.appendOnRefresh,
                view = dataSource.view(),
                groups = dataSource.group(),
                groupedMode = groups && groups[0],
                item;

            if (action === "itemchange") {
                // the itemchange may come from a child collection
                item = listView.findByDataItem(dataItems)[0];
                if (item) {
                    listView.setDataItem(item, dataItems[0]);
                }
                return;
            }

            listView.trigger(DATA_BINDING);

            if (action === "add" && !groupedMode) {
                var index = view.indexOf(dataItems[0]);
                if (index > -1) {
                    listView.insertAt(dataItems, index);
                }
            } else if (action === "remove" && !groupedMode) {
                listView.remove(dataItems);
            } else if (groupedMode) {
                listView.replaceGrouped(view);
            }
            else if (prependOnRefresh && !listView._filter) {
                listView.prepend(view);
            }
            else {
                listView.replace(view);
            }

            if (this._shouldShowLoading()) {
                listView.hideLoading();
            }

            listView.trigger(DATA_BOUND, { ns: ui });
        },

        setDataSource: function(dataSource) {
            if (this.dataSource) {
                this._unbindDataSource();
            }

            this.dataSource = dataSource;
            dataSource.bind(CHANGE, this._refreshHandler);

            if (this._shouldShowLoading()) {
                this.dataSource.bind(PROGRESS, this._progressHandler);
            }
        },

        _unbindDataSource: function() {
            this.dataSource.unbind(CHANGE, this._refreshHandler).unbind(PROGRESS, this._progressHandler);
        },

        _shouldShowLoading: function() {
            var options = this.options;
            return !options.pullToRefresh && !options.loadMore && !options.endlessScroll;
        }
    });

    var ListViewFilter = kendo.Class.extend({
        init: function(listView) {
            var filter = this,
                filterable = listView.options.filterable,
                events = "change paste";

            this.listView = listView;
            this.options = filterable;

            listView.element.before(SEARCH_TEMPLATE({ placeholder: filterable.placeholder || "Search..." }));

            if (filterable.autoFilter !== false) {
                events += " keyup";
            }

            this.element = listView.wrapper.find(".km-search-form");

            this.searchInput = listView.wrapper.find("input[type=search]")
                .closest("form").on("submit" + NS, function(e) {
                    e.preventDefault();
                })
                .end()
                .on("focus" + NS, function() {
                    filter._oldFilter = filter.searchInput.val();
                })
                .on(events.split(" ").join(NS + " ") + NS, proxy(this._filterChange, this));

            this.clearButton = listView.wrapper.find(".km-filter-reset")
                .on(CLICK, proxy(this, "_clearFilter"))
                .hide();
        },

        _search: function(expr) {
            this._filter = true;
            this.clearButton[expr ? "show" : "hide"]();
            this.listView.dataSource.filter(expr);
        },

        _filterChange: function(e) {
            var filter = this;
            if (e.type == "paste" && this.options.autoFilter !== false) {
                setTimeout(function() {
                    filter._applyFilter();
                }, 1);
            } else {
                this._applyFilter();
            }
        },

        _applyFilter: function() {
            var options = this.options,
                value = this.searchInput.val(),
                expr = value.length ? {
                    field: options.field,
                    operator: options.operator || "startsWith",
                    ignoreCase: options.ignoreCase,
                    value: value
                } : null;

            if (value === this._oldFilter) {
                return;
            }

            this._oldFilter = value;
            this._search(expr);
        },

        _clearFilter: function(e) {
            this.searchInput.val("");
            this._search(null);

            e.preventDefault();
        }
    });

    var ListView = Widget.extend({
        init: function(element, options) {
            var listView = this;

            Widget.fn.init.call(this, element, options);

            element = this.element;

            options = this.options;

            // support for legacy typo in configuration options: scrollTreshold -> scrollThreshold.
            if (options.scrollTreshold) {
                options.scrollThreshold = options.scrollTreshold;
            }

            element
                .on("down", HIGHLIGHT_SELECTOR, "_highlight")
                .on("move up cancel", HIGHLIGHT_SELECTOR, "_dim");

            this._userEvents = new kendo.UserEvents(element, {
                filter: ITEM_SELECTOR,
                allowSelection: true,
                tap: function(e) {
                    listView._click(e);
                },
                // prevent the navigation when native scrolling is present
                end: function(e) {
                    if (kendo.mobile.appLevelNativeScrolling() || listView.viewHasNativeScrolling()) {
                        e.preventDefault();
                    }
                }
            });

            // HACK!!! to negate the ms touch action from the user events.
            element.css("-ms-touch-action", "auto");

            element.wrap(WRAPPER);

            this.wrapper = this.element.parent();

            this._headerFixer = new HeaderFixer(this);

            this._itemsCache = {};
            this._templates();

            this.virtual = options.endlessScroll || options.loadMore;

            this._style();

            if (this.options.pullToRefresh) {
                this._pullToRefreshHandler = new RefreshHandler(this);
            }

            if (this.options.filterable) {
                this._filter = new ListViewFilter(this);
            }

            if (this.virtual) {
                this._itemBinder = new VirtualListViewItemBinder(this);
            } else {
                this._itemBinder = new ListViewItemBinder(this);
            }

            this.setDataSource(options.dataSource);

            this._enhanceItems(this.items());

            kendo.notify(this, ui);
        },

        events: [
            CLICK,
            DATA_BINDING,
            DATA_BOUND,
            ITEM_CHANGE
        ],

        options: {
            name: "ListView",
            style: "",
            type: "flat",
            autoBind: true,
            fixedHeaders: false,
            template: "#:data#",
            headerTemplate: '<span class="km-text">#:value#</span>',
            appendOnRefresh: false,
            loadMore: false,
            loadMoreText: "Press to load more",
            endlessScroll: false,
            scrollThreshold: 30,
            pullToRefresh: false,
            pullTemplate: "Pull to refresh",
            releaseTemplate: "Release to refresh",
            refreshTemplate: "Refreshing",
            pullOffset: 140,
            filterable: false,
            virtualViewSize: null
        },

        refresh: function() {
            this._itemBinder.refresh();
        },

        reset: function() {
            this._itemBinder.reset();
        },

        setDataSource: function(dataSource) {
            // the listView should have a ready datasource for MVVM to function properly. But an empty datasource should not empty the element
            var emptyDataSource = !dataSource;
            this.dataSource = DataSource.create(dataSource);

            this.trigger("_dataSource", { dataSource: this.dataSource, empty: emptyDataSource });

            if (this.options.autoBind && !emptyDataSource) {
                this.items().remove();
                this.dataSource.fetch();
            }
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            kendo.destroy(this.element);
            this._userEvents.destroy();
            if (this._itemBinder) {
                this._itemBinder.destroy();
            }

            this.element.unwrap();
            delete this.element;
            delete this.wrapper;
            delete this._userEvents;
        },

        items: function() {
            if (this.options.type === "group") {
                return this.element.find(".km-list").children();
            } else {
                return this.element.children().not('.km-load-more');
            }
        },

        scroller: function() {
            if (!this._scrollerInstance) {
                this._scrollerInstance = this.element.closest(".km-scroll-wrapper").data("kendoMobileScroller");
            }

            return this._scrollerInstance;
        },

        showLoading: function() {
            var view = this.view();
            if (view && view.loader) {
                view.loader.show();
            }
        },

        hideLoading: function() {
            var view = this.view();
            if (view && view.loader) {
                view.loader.hide();
            }
        },

        insertAt: function(dataItems, index) {
            var listView = this;
            return this._renderItems(dataItems, function(items) {
                if (index === 0) {
                    listView.element.prepend(items);
                }
                else if (index === -1) {
                    listView.element.append(items);
                } else {
                    listView.items().eq(index - 1).after(items);
                }
                for (var idx = 0; idx < items.length; idx ++) {
                    listView.trigger(ITEM_CHANGE, { item: [items[idx]], data: dataItems[idx], ns: ui });
                }
            });
        },

        append: function(dataItems) {
            return this.insertAt(dataItems, -1);
        },

        prepend: function(dataItems) {
            return this.insertAt(dataItems, 0);
        },

        replace: function(dataItems) {
            this.options.type = "flat";
            this.element.empty();
            this._style();
            return this.insertAt(dataItems, 0);
        },

        replaceGrouped: function(groups) {
            this.options.type = "group";
            this.element.empty();
            var items = $(kendo.render(this.groupTemplate, groups));

            this._enhanceItems(items.children("ul").children("li"));
            this.element.append(items);
            mobile.init(items);
            this._style();
        },

        remove: function(dataItems) {
            var items = this.findByDataItem(dataItems);
            kendo.destroy(items);
            items.remove();
        },

        findByDataItem: function(dataItems) {
            var selectors = [];

            for (var idx = 0, length = dataItems.length; idx < length; idx ++) {
                selectors[idx] = "[data-" + kendo.ns + "uid=" + dataItems[idx].uid + "]";
            }

            return this.element.find(selectors.join(","));
        },

        // item is a DOM element, not jQuery object.
        setDataItem: function(item, dataItem) {
            var listView = this,
                replaceItem = function(items) {
                    var newItem = $(items[0]);
                    $(item).replaceWith(newItem);
                    listView.trigger(ITEM_CHANGE, { item: newItem, data: dataItem, ns: ui });
                };

            return this._renderItems([dataItem], replaceItem)[0];
        },

        _renderItems: function(dataItems, callback) {
            var items = $(kendo.render(this.template, dataItems));
            callback(items);
            mobile.init(items);
            this._enhanceItems(items);

            return items;
        },

        _dim: function(e) {
            this._toggle(e, false);
        },

        _highlight: function(e) {
            this._toggle(e, true);
        },

        _toggle: function(e, highlight) {
            if (e.which > 1) {
                return;
            }

            var clicked = $(e.currentTarget),
                item = clicked.parent(),
                role = attrValue(clicked, "role") || "",
                plainItem = (!role.match(buttonRegExp)),
                prevented = e.isDefaultPrevented();

            if (plainItem) {
                item.toggleClass(ACTIVE_CLASS, highlight && !prevented);
            }
        },

        _templates: function() {
            var template = this.options.template,
                headerTemplate = this.options.headerTemplate,
                dataIDAttribute = ' data-uid="#=arguments[0].uid || ""#"',
                templateProxy = {},
                groupTemplateProxy = {};

            if (typeof template === FUNCTION) {
                templateProxy.template = template;
                template = "#=this.template(data)#";
            }

            this.template = proxy(kendo.template("<li" + dataIDAttribute + ">" + template + "</li>"), templateProxy);

            groupTemplateProxy.template = this.template;

            if (typeof headerTemplate === FUNCTION) {
                groupTemplateProxy._headerTemplate = headerTemplate;
                headerTemplate = "#=this._headerTemplate(data)#";
            }

            groupTemplateProxy.headerTemplate = kendo.template(headerTemplate);

            this.groupTemplate = proxy(GROUP_TEMPLATE, groupTemplateProxy);
        },

        _click: function(e) {
            if (e.event.which > 1 || e.event.isDefaultPrevented()) {
                return;
            }

            var dataItem,
                item = e.target,
                target = $(e.event.target),
                buttonElement = target.closest(kendo.roleSelector("button", "detailbutton", "backbutton")),
                button = kendo.widgetInstance(buttonElement, ui),
                id = item.attr(kendo.attr("uid"));

            if (id) {
                dataItem = this.dataSource.getByUid(id);
            }

            if (this.trigger(CLICK, {target: target, item: item, dataItem: dataItem, button: button})) {
                e.preventDefault();
            }
        },

        _styleGroups: function() {
            var rootItems = this.element.children();

            rootItems.children("ul").addClass("km-list");

            rootItems.each(function() {
                var li = $(this),
                    groupHeader = li.contents().first();

                li.addClass("km-group-container");
                if (!groupHeader.is("ul") && !groupHeader.is("div." + GROUP_CLASS)) {
                    groupHeader.wrap(GROUP_WRAPPER);
                }
            });
        },

        _style: function() {
            var options = this.options,
                grouped = options.type === "group",
                element = this.element,
                inset = options.style === "inset";

            element.addClass("km-listview")
                .toggleClass("km-list", !grouped)
                .toggleClass("km-virtual-list", this.virtual)
                .toggleClass("km-listinset", !grouped && inset)
                .toggleClass("km-listgroup", grouped && !inset)
                .toggleClass("km-listgroupinset", grouped && inset);

            if (!element.parents(".km-listview")[0]) {
                element.closest(".km-content").toggleClass("km-insetcontent", inset); // iOS has white background when the list is not inset.
            }

            if (grouped) {
                this._styleGroups();
            }

            this.trigger(STYLED);
        },

        _enhanceItems: function(items) {
            items.each(function() {
                var item = $(this),
                    child,
                    enhanced = false;

                item.children().each(function() {
                    child = $(this);
                    if (child.is("a")) {
                        enhanceLinkItem(child);
                        enhanced = true;
                    } else if (child.is("label")) {
                        enhanceCheckBoxItem(child);
                        enhanced = true;
                    }
                });

                if (!enhanced) {
                    enhanceItem(item);
                }
            });
        }
    });

    ui.plugin(ListView);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        ui = mobile.ui,
        roleSelector = kendo.roleSelector,
        Widget = ui.Widget;

    function createContainer(align, element) {
        var items = element.find("[" + kendo.attr("align") + "=" + align + "]");

        if (items[0]) {
            return $('<div class="km-' + align + 'item" />').append(items).prependTo(element);
        }
    }

    function toggleTitle(centerElement) {
        var siblings = centerElement.siblings(),
            noTitle = !!centerElement.children("ul")[0],
            showTitle = (!!siblings[0] && $.trim(centerElement.text()) === "");

        centerElement.prevAll().toggleClass("km-absolute", noTitle);
        centerElement.toggleClass("km-show-title", showTitle);
        centerElement.toggleClass("km-fill-title", showTitle && !$.trim(centerElement.html()));
        centerElement.toggleClass("km-no-title", noTitle);
        centerElement.toggleClass("km-hide-title", centerElement.css("visibility") == "hidden" && !siblings.children().is(":visible"));
    }

    var NavBar = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            element = that.element;

            that.container().bind("show", $.proxy(this, "refresh"));

            element.addClass("km-navbar").wrapInner($('<div class="km-view-title km-show-title" />'));
            that.leftElement = createContainer("left", element);
            that.rightElement = createContainer("right", element);
            that.centerElement = element.find(".km-view-title");
        },

        options: {
            name: "NavBar"
        },

        title: function(value) {
            this.element.find(roleSelector("view-title")).text(value);
            toggleTitle(this.centerElement);
        },

        refresh: function(e) {
            var view = e.view;
            if (view.options.title) {
                this.title(view.options.title);
            } else {
                toggleTitle(this.centerElement);
            }
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            kendo.destroy(this.element);
        }
    });

    ui.plugin(NavBar);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        mobile = kendo.mobile,
        ui = mobile.ui,
        proxy = $.proxy,
        Transition = kendo.effects.Transition,
        Pane = kendo.ui.Pane,
        PaneDimensions = kendo.ui.PaneDimensions,
        Widget = ui.Widget,
        DataSource = kendo.data.DataSource,
        Buffer = kendo.data.Buffer,
        BatchBuffer = kendo.data.BatchBuffer,

        // Math
        math = Math,
        abs  = math.abs,
        ceil = math.ceil,
        round = math.round,
        max = math.max,
        min = math.min,
        floor = math.floor,

        CHANGE = "change",
        CHANGING = "changing",
        REFRESH = "refresh",
        CURRENT_PAGE_CLASS = "km-current-page",
        VIRTUAL_PAGE_CLASS = "km-virtual-page",
        FUNCTION = "function",
        ITEM_CHANGE = "itemChange",

        VIRTUAL_PAGE_COUNT = 3,
        LEFT_PAGE = -1,
        CETER_PAGE = 0,
        RIGHT_PAGE = 1,

        LEFT_SWIPE = -1,
        NUDGE = 0,
        RIGHT_SWIPE = 1;

    var Pager = kendo.Class.extend({
        init: function(scrollView) {
            var that = this,
                element = $("<ol class='km-pages'/>");

            scrollView.element.append(element);

            this._changeProxy = proxy(that, "_change");
            this._refreshProxy = proxy(that, "_refresh");
            scrollView.bind(CHANGE, this._changeProxy);
            scrollView.bind(REFRESH, this._refreshProxy);

            $.extend(that, { element: element, scrollView: scrollView });
        },

        items: function() {
            return this.element.children();
        },

        _refresh: function(e) {
            var pageHTML = "";

            for (var idx = 0; idx < e.pageCount; idx ++) {
                pageHTML += "<li/>";
            }

            this.element.html(pageHTML);
            this.items().eq(e.page).addClass(CURRENT_PAGE_CLASS);
        },

        _change: function(e) {
            this.items().removeClass(CURRENT_PAGE_CLASS).eq(e.page).addClass(CURRENT_PAGE_CLASS);
        },

        destroy: function() {
            this.scrollView.unbind(CHANGE, this._changeProxy);
            this.scrollView.unbind(REFRESH, this._refreshProxy);
            this.element.remove();
        }
    });

    kendo.mobile.ui.ScrollViewPager = Pager;

    var TRANSITION_END = "transitionEnd",
        DRAG_START = "dragStart",
        DRAG_END = "dragEnd";

    var ElasticPane = kendo.Observable.extend({
        init: function(element, options) {
            var that = this;

            kendo.Observable.fn.init.call(this);

            this.element = element;
            this.container = element.parent();

            var movable,
                transition,
                userEvents,
                dimensions,
                dimension,
                pane;

            movable = new kendo.ui.Movable(that.element);

            transition = new Transition({
                axis: "x",
                movable: movable,
                onEnd: function() {
                    that.trigger(TRANSITION_END);
                }
            });

            userEvents = new kendo.UserEvents(element, {
                start: function(e) {
                    if (abs(e.x.velocity) * 2 >= abs(e.y.velocity)) {
                        userEvents.capture();
                    } else {
                        userEvents.cancel();
                    }

                    that.trigger(DRAG_START, e);
                    transition.cancel();
                },
                allowSelection: true,
                end: function(e) {
                    that.trigger(DRAG_END, e);
                }
            });

            dimensions = new PaneDimensions({
                element: that.element,
                container: that.container
            });

            dimension = dimensions.x;

            dimension.bind(CHANGE, function() {
                that.trigger(CHANGE);
            });

            pane = new Pane({
                dimensions: dimensions,
                userEvents: userEvents,
                movable: movable,
                elastic: true
            });

            $.extend(that, {
                duration: options && options.duration || 1,
                movable: movable,
                transition: transition,
                userEvents: userEvents,
                dimensions: dimensions,
                dimension: dimension,
                pane: pane
            });

            this.bind([TRANSITION_END, DRAG_START, DRAG_END, CHANGE], options);
        },

        size: function() {
            return { width: this.dimensions.x.getSize(), height: this.dimensions.y.getSize() };
        },

        total: function() {
            return this.dimension.getTotal();
        },

        offset: function() {
            return -this.movable.x;
        },

        updateDimension: function() {
            this.dimension.update(true);
        },

        refresh: function() {
            this.dimensions.refresh();
        },

        moveTo: function(offset) {
            this.movable.moveAxis("x", -offset);
        },

        transitionTo: function(offset, ease, instant) {
            if (instant) {
                this.moveTo(-offset);
            } else {
                this.transition.moveTo({ location: offset, duration: this.duration, ease: ease });
            }
        }
    });

    kendo.mobile.ui.ScrollViewElasticPane = ElasticPane;

    var ScrollViewContent = kendo.Observable.extend({
        init: function(element, pane, options) {
            var that = this;

            kendo.Observable.fn.init.call(this);
            that.element = element;
            that.pane = pane;
            that._getPages();
            this.page = 0;
            this.pageSize = options.pageSize || 1;
            this.contentHeight = options.contentHeight;
            this.enablePager = options.enablePager;
        },

        scrollTo: function(page, instant) {
            this.page = page;
            this.pane.transitionTo(- page * this.pane.size().width, Transition.easeOutExpo, instant);
        },

        paneMoved: function(swipeType, bounce, callback, /*internal*/ instant) {
            var that = this,
                pane = that.pane,
                width = pane.size().width * that.pageSize,
                approx = round,
                ease = bounce ? Transition.easeOutBack : Transition.easeOutExpo,
                snap,
                nextPage;

            if (swipeType === LEFT_SWIPE) {
                approx = ceil;
            } else if (swipeType === RIGHT_SWIPE) {
                approx = floor;
            }

            nextPage = approx(pane.offset() / width);

            snap = max(that.minSnap, min(-nextPage * width, that.maxSnap));

            if (nextPage != that.page) {
                if (callback && callback({ currentPage: that.page, nextPage: nextPage })) {
                    snap = -that.page * pane.size().width;
                }
            }

            pane.transitionTo(snap, ease, instant);
        },

        updatePage: function() {
            var pane = this.pane,
                page = round(pane.offset() / pane.size().width);

            if (page != this.page) {
                this.page = page;
                return true;
            }

            return false;
        },

        forcePageUpdate: function() {
            return this.updatePage();
        },

        resizeTo: function(size) {
            var pane = this.pane,
                width = size.width;

            this.pageElements.width(width);

            if (this.contentHeight === "100%") {
                var containerHeight = this.element.parent().height();

                if(this.enablePager === true) {
                    var pager = this.element.parent().find("ol.km-pages");
                    if(pager.length) {
                        containerHeight -= pager.outerHeight(true);
                    }
                }

                this.element.css("height", containerHeight);
                this.pageElements.css("height", containerHeight);
            }

            // re-read pane dimension after the pageElements have been resized.
            pane.updateDimension();

            if (!this._paged) {
                this.page = floor(pane.offset() / width);
            }

            this.scrollTo(this.page, true);

            this.pageCount = ceil(pane.total() / width);
            this.minSnap = - (this.pageCount - 1) * width;
            this.maxSnap = 0;
        },

        _getPages: function() {
            this.pageElements = this.element.find("[data-role=page]");
            this._paged = this.pageElements.length > 0;
        }
    });

    kendo.mobile.ui.ScrollViewContent = ScrollViewContent;

    var VirtualScrollViewContent = kendo.Observable.extend({
        init: function(element, pane, options) {
            var that = this;

            kendo.Observable.fn.init.call(this);

            that.element = element;
            that.pane = pane;
            that.options = options;
            that._templates();
            that.page = options.page || 0;
            that.pages = [];
            that._initPages();
            that.resizeTo(that.pane.size());

            that.pane.dimension.forceEnabled();
        },

        setDataSource: function(dataSource) {
            this.dataSource = DataSource.create(dataSource);
            this._buffer();
            this._pendingPageRefresh = false;
            this._pendingWidgetRefresh = false;
        },

        _viewShow: function() {
            var that = this;
            if(that._pendingWidgetRefresh) {
                setTimeout(function() {
                    that._resetPages();
                }, 0);
                that._pendingWidgetRefresh = false;
            }
        },

        _buffer: function() {
            var itemsPerPage = this.options.itemsPerPage;

            if(this.buffer) {
                this.buffer.destroy();
            }

            if(itemsPerPage > 1) {
                this.buffer = new BatchBuffer(this.dataSource, itemsPerPage);
            } else {
                this.buffer = new Buffer(this.dataSource, itemsPerPage * 3);
            }

            this._resizeProxy = proxy(this, "_onResize");
            this._resetProxy = proxy(this, "_onReset");
            this._endReachedProxy = proxy(this, "_onEndReached");

            this.buffer.bind({
                "resize": this._resizeProxy,
                "reset": this._resetProxy,
                "endreached": this._endReachedProxy
            });
        },

        _templates: function() {
            var template = this.options.template,
                emptyTemplate = this.options.emptyTemplate,
                templateProxy = {},
                emptyTemplateProxy = {};

            if(typeof template === FUNCTION) {
                templateProxy.template = template;
                template = "#=this.template(data)#";
            }

            this.template = proxy(kendo.template(template), templateProxy);

            if(typeof emptyTemplate === FUNCTION) {
                emptyTemplateProxy.emptyTemplate = emptyTemplate;
                emptyTemplate = "#=this.emptyTemplate(data)#";
            }

            this.emptyTemplate = proxy(kendo.template(emptyTemplate), emptyTemplateProxy);
        },

        _initPages: function() {
            var pages = this.pages,
                element = this.element,
                page;

            for (var i = 0; i < VIRTUAL_PAGE_COUNT; i++) {
                page = new Page(element);
                pages.push(page);
            }

            this.pane.updateDimension();
        },

        resizeTo: function(size) {
            var pages = this.pages,
                pane = this.pane;

            for (var i = 0; i < pages.length; i++) {
                pages[i].setWidth(size.width);
            }

            if (this.options.contentHeight === "auto") {
                this.element.css("height", this.pages[1].element.height());
            }

            else if (this.options.contentHeight === "100%") {
                var containerHeight = this.element.parent().height();

                if(this.options.enablePager === true) {
                    var pager = this.element.parent().find("ol.km-pages");
                    if(pager.length) {
                        containerHeight -= pager.outerHeight(true);
                    }
                }

                this.element.css("height", containerHeight);
                pages[0].element.css("height", containerHeight);
                pages[1].element.css("height", containerHeight);
                pages[2].element.css("height", containerHeight);
            }

            pane.updateDimension();

            this._repositionPages();

            this.width = size.width;
        },

        scrollTo: function(page) {
            var buffer = this.buffer,
                dataItem;

            buffer.syncDataSource();
            dataItem = buffer.at(page);

            if(!dataItem) {
                return;
            }

            this._updatePagesContent(page);

            this.page = page;
        },

        paneMoved: function(swipeType, bounce, callback, /*internal*/ instant) {
            var that = this,
                pane = that.pane,
                width = pane.size().width,
                offset = pane.offset(),
                thresholdPassed = Math.abs(offset) >= width / 3,
                ease = bounce ? kendo.effects.Transition.easeOutBack : kendo.effects.Transition. easeOutExpo,
                isEndReached = that.page + 2 > that.buffer.total(),
                nextPage,
                delta = 0;

            if(swipeType === RIGHT_SWIPE) {
                if(that.page !== 0) {
                    delta = -1; //backward
                }
            } else if(swipeType === LEFT_SWIPE && !isEndReached) {
                delta = 1; //forward
            } else if(offset > 0 && (thresholdPassed && !isEndReached)) {
                delta = 1; //forward
            } else if(offset < 0 && thresholdPassed) {
                if(that.page !== 0) {
                    delta = -1; //backward
                }
            }

            nextPage = that.page;
            if(delta) {
                nextPage = (delta > 0) ? nextPage + 1 : nextPage - 1;
            }

            if(callback && callback({ currentPage: that.page, nextPage: nextPage })) {
                delta = 0;
            }

            if(delta === 0) {
                that._cancelMove(ease, instant);
            } else if (delta === -1) {
                that._moveBackward(instant);
            } else if (delta === 1) {
                that._moveForward(instant);
            }
        },

        updatePage: function() {
            var pages = this.pages;

            if(this.pane.offset() === 0) {
                return false;
            }

            if(this.pane.offset() > 0) {
                pages.push(this.pages.shift());//forward
                this.page++;
                this.setPageContent(pages[2], this.page + 1);
            } else {
                pages.unshift(this.pages.pop()); //back
                this.page--;
                this.setPageContent(pages[0], this.page - 1);
            }

            this._repositionPages();

            this._resetMovable();

            return true;
        },

        forcePageUpdate: function() {
            var offset = this.pane.offset(),
                threshold  = this.pane.size().width * 3/4;

            if(abs(offset) > threshold) {
                return this.updatePage();
            }

            return false;
        },

        _resetMovable: function() {
            this.pane.moveTo(0);
        },

        _moveForward: function(instant) {
            this.pane.transitionTo(-this.width, kendo.effects.Transition.easeOutExpo, instant);
        },

        _moveBackward: function(instant) {
            this.pane.transitionTo(this.width, kendo.effects.Transition.easeOutExpo, instant);
        },

        _cancelMove: function(ease, /*internal*/ instant) {
            this.pane.transitionTo(0, ease, instant);
        },

        _resetPages: function() {
            this.page = this.options.page || 0;

            this._updatePagesContent(this.page);
            this._repositionPages();

            this.trigger("reset");
        },

        _onResize: function() {
            var page = this.pages[2], //last page
                idx = this.page + 1;

            if(this._pendingPageRefresh) {
                this.setPageContent(page, idx);
                this._pendingPageRefresh = false;
            }
        },

        _onReset: function() {
            this.pageCount = ceil(this.dataSource.total() / this.options.itemsPerPage);

            if(this.element.is(":visible")) {
                this._resetPages();
            } else {
                this._widgetNeedsRefresh = true;
            }
        },

        _onEndReached: function() {
            this._pendingPageRefresh = true;
        },

        _repositionPages: function() {
            var pages = this.pages;

            pages[0].position(LEFT_PAGE);
            pages[1].position(CETER_PAGE);
            pages[2].position(RIGHT_PAGE);
        },

        _updatePagesContent: function(offset) {
            var pages = this.pages,
                currentPage = offset || 0;

            this.setPageContent(pages[0], currentPage - 1);
            this.setPageContent(pages[1], currentPage);
            this.setPageContent(pages[2], currentPage + 1);
        },

        setPageContent: function(page, index) {
            var buffer = this.buffer,
                template = this.template,
                emptyTemplate = this.emptyTemplate,
                view = null;

            if(index >= 0) {
                view = buffer.at(index);
                if ($.isArray(view) && !view.length) {
                    view = null;
                }
            }

            if(view) {
                page.content(template(view));
            } else {
                page.content(emptyTemplate({}));
            }

            kendo.mobile.init(page.element);
            this.trigger(ITEM_CHANGE, { item: page.element, data: view, ns: kendo.mobile.ui });

        }
    });

    kendo.mobile.ui.VirtualScrollViewContent = VirtualScrollViewContent;

    var Page = kendo.Class.extend({
        init: function(container) {
            this.element = $("<div class='" + VIRTUAL_PAGE_CLASS + "'></div>");
            this.width = container.width();
            this.element.width(this.width);
            container.append(this.element);
        },

        content: function(theContent) {
            this.element.html(theContent);
        },

        position: function(position) { //position can be -1, 0, 1
            this.element.css("transform", "translate3d(" + this.width * position + "px, 0, 0)");
        },

        setWidth: function(width) {
            this.width = width;
            this.element.width(width);
        }
    });

    kendo.mobile.ui.VirtualPage = Page;

    var ScrollView = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);

            options = that.options;
            element = that.element;

            kendo.stripWhitespace(element[0]);

            element
                .wrapInner("<div/>")
                .addClass("km-scrollview");

            if(this.options.enablePager) {
                this.pager = new Pager(this);
            }

            that.inner = element.children().first();
            that.page = 0;
            that.inner.css("height", options.contentHeight);

            that.pane = new ElasticPane(that.inner, {
                duration: this.options.duration,
                transitionEnd: proxy(this, "_transitionEnd"),
                dragStart: proxy(this, "_dragStart"),
                dragEnd: proxy(this, "_dragEnd"),
                change: proxy(this, REFRESH)
            });

            that.bind("resize", function() {
                that.pane.refresh();
            });

            that.page = options.page;

            var empty = this.inner.children().length === 0;

            that._content = empty ? new VirtualScrollViewContent(that.inner, that.pane, options) : new ScrollViewContent(that.inner, that.pane, options);
            that._content.page = that.page;

            that._content.bind("reset", function() {
                var content = that._content;

                that._syncWithContent();
                that.trigger(REFRESH, { pageCount: content.pageCount, page: content.page });
            });

            that._content.bind(ITEM_CHANGE, function(e) {
                that.trigger(ITEM_CHANGE, e);
            });

            that.setDataSource(options.dataSource);

            var mobileContainer = that.container();

            if(mobileContainer.nullObject) {
                that.viewInit();
                that.viewShow();
            } else {
                mobileContainer.bind("show", proxy(this, "viewShow")).bind("init", proxy(this, "viewInit"));
            }
        },

        options: {
            name: "ScrollView",
            page: 0,
            duration: 400,
            velocityThreshold: 0.8,
            contentHeight: "auto",
            pageSize: 1,
            itemsPerPage: 1,
            bounceVelocityThreshold: 1.6,
            enablePager: true,
            autoBind: true,
            template: "",
            emptyTemplate: ""
        },

        events: [
            CHANGING,
            CHANGE,
            REFRESH
        ],

        destroy: function() {
            Widget.fn.destroy.call(this);
            kendo.destroy(this.element);
        },

        viewInit: function() {
            if(this.options.autoBind) {
                this._content.scrollTo(this._content.page, true);
            }
        },

        viewShow: function() {
            this.pane.refresh();
        },

        refresh: function() {
            var content = this._content;

            content.resizeTo(this.pane.size());
            this.page = content.page;
            this.trigger(REFRESH, { pageCount: content.pageCount, page: content.page });
        },

        content: function(html) {
           this.element.children().first().html(html);
           this._content._getPages();
           this.pane.refresh();
        },

        scrollTo: function(page, instant) {
            this._content.scrollTo(page, instant);
            this._syncWithContent();
        },

        prev: function() {
            var that = this;

            that._content.paneMoved(RIGHT_SWIPE, undefined, function(eventData) {
                return that.trigger(CHANGING, eventData);
            });
        },

        next: function() {
            var that = this;

            that._content.paneMoved(LEFT_SWIPE, undefined, function(eventData) {
                return that.trigger(CHANGING, eventData);
            });
        },

        setDataSource: function(dataSource) {
            if (!(this._content instanceof VirtualScrollViewContent)) {
                return;
            }
            // the scrollview should have a ready datasource for MVVM to function properly. But an empty datasource should not empty the element
            var emptyDataSource = !dataSource;
            this.dataSource = DataSource.create(dataSource);

            this._content.setDataSource(this.dataSource);

            if (this.options.autoBind && !emptyDataSource) {
                // this.items().remove();
                this.dataSource.fetch();
            }
        },

        items: function() {
            return this.element.find("." + VIRTUAL_PAGE_CLASS);
        },

        _syncWithContent: function() {
            var pages = this._content.pages,
                buffer = this._content.buffer,
                data,
                element;

            this.page = this._content.page;

            data = buffer ? buffer.at(this.page) : undefined;
            if(!(data instanceof Array)) {
                data = [data];
            }
            element = pages ? pages[1].element : undefined;

            this.trigger(CHANGE, { page: this.page, element: element, data: data });
        },

        _dragStart: function() {
            if (this._content.forcePageUpdate()) {
                this._syncWithContent();
            }
        },

        _dragEnd: function(e) {
            var that = this,
                velocity = e.x.velocity,
                velocityThreshold = this.options.velocityThreshold,
                swipeType = NUDGE,
                bounce = abs(velocity) > this.options.bounceVelocityThreshold;

            if (velocity > velocityThreshold) {
                swipeType = RIGHT_SWIPE;
            } else if(velocity < -velocityThreshold) {
                swipeType = LEFT_SWIPE;
            }

            this._content.paneMoved(swipeType, bounce, function(eventData) {
                return that.trigger(CHANGING, eventData);
            });
        },

        _transitionEnd: function() {
            if (this._content.updatePage()) {
                this._syncWithContent();
            }
        }
    });

    ui.plugin(ScrollView);

})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Widget = ui.Widget,
        support = kendo.support,
        CHANGE = "change",
        SWITCHON = "km-switch-on",
        SWITCHOFF = "km-switch-off",
        MARGINLEFT = "margin-left",
        ACTIVE_STATE = "km-state-active",
        DISABLED_STATE = "km-state-disabled",
        DISABLED = "disabled",
        TRANSFORMSTYLE = support.transitions.css + "transform",
        proxy = $.proxy;

    function limitValue(value, minLimit, maxLimit) {
        return Math.max(minLimit, Math.min(maxLimit, value));
    }

    var SWITCH_MARKUP = '<span class="km-switch km-widget">\
        <span class="km-switch-wrapper"><span class="km-switch-background"></span></span> \
        <span class="km-switch-container"><span class="km-switch-handle" > \
            <span class="km-switch-label-on">{0}</span> \
            <span class="km-switch-label-off">{1}</span> \
        </span> \
    </span>';

    var Switch = Widget.extend({
        init: function(element, options) {
            var that = this, checked;

            Widget.fn.init.call(that, element, options);

            options = that.options;

            that.wrapper = $(kendo.format(SWITCH_MARKUP, options.onLabel, options.offLabel));
            that.handle = that.wrapper.find(".km-switch-handle");
            that.background = that.wrapper.find(".km-switch-background");
            that.wrapper.insertBefore(that.element).prepend(that.element);

            that._drag();

            that.origin = parseInt(that.background.css(MARGINLEFT), 10);

            that.constrain = 0;
            that.snapPoint = 0;

            element = that.element[0];
            element.type = "checkbox";
            that._animateBackground = true;

            checked = that.options.checked;

            if (checked === null) {
                checked = element.checked;
            }

            that.check(checked);

            that.options.enable = that.options.enable && !that.element.attr(DISABLED);
            that.enable(that.options.enable);

            that.refresh();
            kendo.notify(that, kendo.mobile.ui);
        },

        refresh: function() {
            var that = this,
                handleWidth = that.handle.outerWidth(true);

            that.width = that.wrapper.width();

            that.constrain  = that.width - handleWidth;
            that.snapPoint = that.constrain / 2;

            if (typeof that.origin != "number") {
                that.origin = parseInt(that.background.css(MARGINLEFT), 10);
            }

            that.background.data("origin", that.origin);

            that.check(that.element[0].checked);
        },

        events: [
            CHANGE
        ],

        options: {
            name: "Switch",
            onLabel: "on",
            offLabel: "off",
            checked: null,
            enable: true
        },

        check: function(check) {
            var that = this,
                element = that.element[0];

            if (check === undefined) {
                return element.checked;
            }

            that._position(check ? that.constrain : 0);
            element.checked = check;
            that.wrapper
                .toggleClass(SWITCHON, check)
                .toggleClass(SWITCHOFF, !check);
        },

        destroy: function() {
            Widget.fn.destroy.call(this);
            this.userEvents.destroy();
        },

        toggle: function() {
            var that = this;

            that.check(!that.element[0].checked);
        },

        enable: function(enable) {
            var element = this.element,
                wrapper = this.wrapper;

            if(typeof enable == "undefined") {
                enable = true;
            }

            this.options.enable = enable;

            if(enable) {
                element.removeAttr(DISABLED);
            } else {
                element.attr(DISABLED, DISABLED);
            }

            wrapper.toggleClass(DISABLED_STATE, !enable);
        },

        _resize: function() {
            this.refresh();
        },

        _move: function(e) {
            var that = this;
            e.preventDefault();
            that._position(limitValue(that.position + e.x.delta, 0, that.width - that.handle.outerWidth(true)));
        },

        _position: function(position) {
            var that = this;

            that.position = position;
            that.handle.css(TRANSFORMSTYLE, "translatex(" + position + "px)");

            if (that._animateBackground) {
                that.background.css(MARGINLEFT, that.origin + position);
            }
        },

        _start: function() {
            if(!this.options.enable) {
                this.userEvents.cancel();
            } else {
                this.userEvents.capture();
                this.handle.addClass(ACTIVE_STATE);
            }
        },

        _stop: function() {
            var that = this;

            that.handle.removeClass(ACTIVE_STATE);
            that._toggle(that.position > that.snapPoint);
        },

        _toggle: function (checked) {
            var that = this,
                handle = that.handle,
                element = that.element[0],
                value = element.checked,
                duration = kendo.mobile.application && kendo.mobile.application.os.wp ? 100 : 200,
                distance;

            that.wrapper
                .toggleClass(SWITCHON, checked)
                .toggleClass(SWITCHOFF, !checked);

            that.position = distance = checked * that.constrain;

            if (that._animateBackground) {
                that.background
                    .kendoStop(true, true)
                    .kendoAnimate({ effects: "slideMargin", offset: distance, reset: true, reverse: !checked, axis: "left", duration: duration });
            }

            handle
                .kendoStop(true, true)
                .kendoAnimate({
                    effects: "slideTo",
                    duration: duration,
                    offset: distance + "px,0",
                    reset: true,
                    complete: function () {
                        if (value !== checked) {
                            element.checked = checked;
                            that.trigger(CHANGE, { checked: checked });
                        }
                    }
                });
        },

        _drag: function() {
            var that = this;

            that.userEvents = new kendo.UserEvents(that.wrapper, {
                tap: function() {
                    if(that.options.enable) {
                        that._toggle(!that.element[0].checked);
                    }
                },
                start: proxy(that._start, that),
                move: proxy(that._move, that),
                end: proxy(that._stop, that)
            });
        }
    });

    ui.plugin(Switch);
})(window.kendo.jQuery);

(function($, undefined) {
    var kendo = window.kendo,
        ui = kendo.mobile.ui,
        Widget = ui.Widget,
        ACTIVE_STATE_CLASS = "km-state-active",
        SELECT = "select";

    function createBadge(value) {
        return $('<span class="km-badge">' + value + '</span>');
    }

    var TabStrip = Widget.extend({
        init: function(element, options) {
            var that = this;

            Widget.fn.init.call(that, element, options);
            that.container().bind("show", $.proxy(this, "refresh"));

            that.element
               .addClass("km-tabstrip")
               .find("a").each(that._buildButton)
               .eq(that.options.selectedIndex).addClass(ACTIVE_STATE_CLASS);

            that.element.on("down", "a", "_release");
        },

        events: [
            SELECT
        ],

        switchTo: function(url) {
            var tabs = this.element.find('a'),
                tab,
                path,
                idx = 0,
                length = tabs.length;

            if(isNaN(url)) {
                for (; idx < length; idx ++) {
                    tab = tabs[idx];
                    path = tab.href.replace(/(\#.+)(\?.+)$/, "$1"); // remove the fragment query string - http://www.foo.com?foo#bar**?baz=qux**

                    if (path.indexOf(url, path.length - url.length) !== -1) {
                        this._setActiveItem($(tab));
                        return true;
                    }
                }
            } else {
                this._setActiveItem(tabs.eq(url));
                return true;
            }

            return false;
        },

        switchByFullUrl: function(url) {
            var tab;

            tab = this.element.find("a[href$='" + url + "']");
            this._setActiveItem(tab);
        },

        clear: function() {
            this.currentItem().removeClass(ACTIVE_STATE_CLASS);
        },

        currentItem: function() {
            return this.element.children("." + ACTIVE_STATE_CLASS);
        },

        badge: function(item, value) {
            var tabstrip = this.element, badge;

            if (!isNaN(item)) {
                item = tabstrip.children().get(item);
            }

            item = tabstrip.find(item);
            badge = $(item.find(".km-badge")[0] || createBadge(value).insertAfter(item.children(".km-icon")));

            if (value || value === 0) {
                badge.html(value);
                return this;
            }

            if (value === false) {
                badge.empty().remove();
                return this;
            }

            return badge.html();
        },

        _release: function(e) {
            if (e.which > 1) {
                return;
            }

            var that = this,
                item = $(e.currentTarget);

            if (item[0] === that.currentItem()[0]) {
                return;
            }

            if (that.trigger(SELECT, {item: item})) {
                e.preventDefault();
            } else {
                that._setActiveItem(item);
            }
        },

        _setActiveItem: function(item) {
            if (!item[0]) {
                return;
            }
            this.clear();
            item.addClass(ACTIVE_STATE_CLASS);
        },

        _buildButton: function() {
            var button = $(this),
                icon = kendo.attrValue(button, "icon"),
                badge = kendo.attrValue(button, "badge"),
                image = button.find("img"),
                iconSpan = $('<span class="km-icon"/>');

            button
                .addClass("km-button")
                .attr(kendo.attr("role"), "tab")
                    .contents().not(image)
                    .wrapAll('<span class="km-text"/>');

            if (image[0]) {
                image.addClass("km-image").prependTo(button);
            } else {
                button.prepend(iconSpan);
                if (icon) {
                    iconSpan.addClass("km-" + icon);
                    if (badge || badge === 0) {
                        createBadge(badge).insertAfter(iconSpan);
                    }
                }
            }
        },

        refresh: function(e) {
            var url = e.view.element.attr(kendo.attr("url"));
            if (!this.switchTo(e.view.id) && url) {
                this.switchTo(url);
            }
        },

        options: {
            name: "TabStrip",
            selectedIndex: 0,
            enable: true
        }
    });

    ui.plugin(TabStrip);
})(window.kendo.jQuery);

return window.kendo;

}, typeof define == 'function' && define.amd ? define : function(_, f){ f(); });