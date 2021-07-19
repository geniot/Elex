var PAGE_FORWARD_NAVIGATION_ACTION = "pf";
var PAGE_BACKWARD_NAVIGATION_ACTION = "pb";

var FAST_FORWARD_NAVIGATION_ACTION = "ff";
var FAST_BACKWARD_NAVIGATION_ACTION = "fb";

var TO_START_NAVIGATION_ACTION = "ts";
var TO_END_NAVIGATION_ACTION = "te";

var SEARCH_NAVIGATION_ACTION = "s";
var SEARCH_BY_INDEX_NAVIGATION_ACTION = "si";


function Controller() {
}

Controller.prototype = {
    constructor: Controller,
    hightlightOn: false,
    init: function () {
        $("body").addClass("js");
        controller.getDictionaries();
    },
    getDictionaries: function () {
            $.ajax({
                url: 'info?' +
                    'a=imagedics' +
                    '&ts=' + Date.now(),
                contentType: "text/plain;charset=utf-8"
            }).done(function (data) {
                    alert(data);
                    model.dics = data;
                    dictionariesToolbar.setDics();
                    searchResultsView.clear();
                    contentView.clear();
                });
    }
}

function debouncer(func, timeout) {
    var timeoutID , timeout = timeout || 200;
    return function () {
        var scope = this , args = arguments;
        clearTimeout(timeoutID);
        timeoutID = setTimeout(function () {
            func.apply(scope, Array.prototype.slice.call(args));
        }, timeout);
    }
}