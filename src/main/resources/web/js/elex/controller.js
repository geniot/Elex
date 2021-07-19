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
        //vertical window resize
        onWindowResize(function () {
            controller.getIndexViewByAction(SEARCH_NAVIGATION_ACTION, indexView.getSelectedValue());
        });

        //up and down arrows should change index pos
        $(document).keydown(function (e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 38 || code == 40) {
                if (code == 38) {
                    indexView.shiftTo(-1);
                } else if (code == 40) {
                    indexView.shiftTo(1);
                }
            }
        });

        //history back/forward event handler
        $.History.bind(function (state) {
            if (state == '' || model.langs == null) {
                return;
            }
            var indexHw = encodeURIComponent(indexView.getSelectedValue());
            var langs = langSelectors.getSelectedLanguagePair();
            var dicIds = model.getDicIdsStr();

            var stateHw = state.split('/')[1];
            var stateDicsIds = state.split('/')[2];
            var stateDisabledDicsIds = state.split('/')[3];
            var stateLangs = decodeURIComponent(state.split('/')[4]);
            var stateViewOffset = state.split('/')[5];

            model.viewOffset = stateViewOffset;
            $.cookie(LAST_SELECTED_VALUE_COOKIE_NAME, decodeURIComponent(stateHw), {expires: 365});
            $.cookie(SELECTED_PAIR_COOKIE_NAME, stateLangs, {expires: 365});
            $.cookie(VIEW_OFFSET_COOKIE_NAME, stateViewOffset, {expires: 365});

            var stateDicsArr = stateDicsIds.split('_');
            var cookieList = $.fn.cookieList(ENABLED_DICTIONARIES_COOKIE_NAME);
            for (var i = 0; i < stateDicsArr.length; i++) {
                if ($.isNumeric(stateDicsArr[i])) {
                    var dic = model.getDicById(stateDicsArr[i]);
                    if (dic != null) {
                        var dicName = dic['name'];
                        cookieList.add(dicName);
                    }
                }
            }

            var stateDisabledDicsArr = stateDisabledDicsIds.split('_');
            for (i = 0; i < stateDisabledDicsArr.length; i++) {
                if ($.isNumeric(stateDisabledDicsArr[i])) {
                    var dic = model.getDicById(stateDisabledDicsArr[i]);
                    if (dic != null) {
                        var dicName = dic['name'];
                        cookieList.remove(dicName);
                    }
                }
            }

            if (langs != stateLangs || stateDicsIds != dicIds) {
                langSelectors.setLangs();
                controller.getDictionaries();
            } else if (indexHw != stateHw && indexHw != '' && stateHw != '') {
                controller.getIndexViewByAction(SEARCH_NAVIGATION_ACTION, encodeURIComponent($.cookie(LAST_SELECTED_VALUE_COOKIE_NAME)));
            }
        });

        controller.getLangs();
    },
    getLangs: function () {
        $.ajax({
            url: 'info?'
                + 'a=langs' +
                '&ts=' + Date.now(),
            contentType: "text/plain;charset=utf-8"
        }).done(function (data) {
            if (!$.isEmptyObject(data)) {
                model.langs = data.sort();
                langSelectors.setLangs();
                controller.getDictionaries();
            }
        });
    },
    getDictionaries: function () {
        var sourceLang = langSelectors.getSourceLang();
        var targetLang = langSelectors.getTargetLang();
        if (sourceLang != null && targetLang != null) {

            $.ajax({
                url: 'info?' +
                    'a=dics'
                    + '&sl=' + sourceLang +
                    '&tl=' + targetLang +
                    '&ts=' + Date.now(),
                contentType: "text/plain;charset=utf-8"
            }).done(function (data) {
                model.dics = data;
                dictionariesToolbar.setDics();
                searchResultsView.clear();
                contentView.clear();
                var onStylesLoaded = function () {
                    if ($.cookie(LAST_SELECTED_VALUE_COOKIE_NAME) != null) {
                        controller.getIndexViewByAction(SEARCH_NAVIGATION_ACTION, encodeURIComponent($.cookie(LAST_SELECTED_VALUE_COOKIE_NAME)));
                    } else {
                        controller.getIndexViewByAction(TO_START_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
                    }
                };
                var onDictionaryScriptsLoaded = function () {
                    controller.updateStyles(onStylesLoaded);
                };
                controller.updateDictionaryScripts(onDictionaryScriptsLoaded);
            });
        }
    },
    onDictionarySelectionChanged: function () {
        searchResultsView.clear();
        contentView.clear();
        var onStylesLoaded = function () {
            controller.getIndexViewByAction(SEARCH_NAVIGATION_ACTION, encodeURIComponent($.cookie(LAST_SELECTED_VALUE_COOKIE_NAME)));
        };
        var onDictionaryScriptsLoaded = function () {
            controller.updateStyles(onStylesLoaded);
        };
        controller.updateDictionaryScripts(onDictionaryScriptsLoaded);
    },
    updateStyles: function (callback) {
        var dicIdsStr = model.getDicIdsStr();
        if (dicIdsStr == "") {
            callback();
            return;
        }
        var path = 'res?a=css&dics=' + dicIdsStr + '&ts=' + Date.now();
        $('#userTheme').remove();
        $.get(path, function (response) {
            //Check if the user theme element is in place - if not, create it.
            if (!$('#userTheme').length) {
                $('head').append('<style id="userTheme">' + response + '</style>');
            }
            callback();
        });
    },
    updateDictionaryScripts: function (callback) {
        $.getScript('res?a=js&ts=' + Date.now(), function () {
            callback();
        });
    },
    getIndexViewByAction: function (action, value) {
        var dicIdsStr = model.getDicIdsStr();
        var viewOffset = model.viewOffset != null ? model.viewOffset : indexView.getViewOffset();
        var pageSize = indexView.getPageSize();
        if (viewOffset >= pageSize) viewOffset = pageSize - 1;

        if (model.getDicIds().length == 0) {
            $.History.go('/' +
                '/' + encodeURIComponent(model.getDicIdsStr()) +
                '/' + encodeURIComponent(model.getDisabledDicIdsStr()) +
                '/' + encodeURIComponent(langSelectors.getSelectedLanguagePair()) +
                '/' + indexView.getViewOffset()
            );
            controller.onIndexLoaded(null, viewOffset);
            return;
        }

        $.ajax({
            url: 'index?' +
                'dics=' + dicIdsStr +
                '&ps=' + pageSize +
                '&vo=' + viewOffset +
                '&sv=' + value +
                '&a=' + action +
                '&ts=' + Date.now(),

            contentType: "text/plain;charset=utf-8"
        }).done(function (data) {
            if (action == SEARCH_NAVIGATION_ACTION || action == SEARCH_BY_INDEX_NAVIGATION_ACTION) {
                viewOffset = data['viewOffset'];
            }
            controller.onIndexLoaded(data, viewOffset);
        });
    },
    runFullTextSearch: function (value) {
        if (!myLayout['east']['state'].isClosed) {
            $.ajax({
                url: 'search?' +
                    'dics=' + model.getDicIdsStr() +
                    '&sv=' + value +
                    '&ts=' + Date.now(),

                contentType: "text/plain;charset=utf-8"
            }).done(function (data) {
                searchResultsView.setResults(data);
            });
        }
    },
    //dictionaries can use this method to get view positioned at entry indexed with value by field
    getIndexViewByIndexedFieldValue: function (shortDicName, value, field, scrollTo) {
        var viewOffset = indexView.getViewOffset();
        var pageSize = indexView.getPageSize();
        if (viewOffset >= pageSize) viewOffset = pageSize - 1;

        if (!(typeof scrollTo === "undefined") && scrollTo != '') {
            $.cookie(SCROLL_TO_COOKIE_NAME, scrollTo);
        }

        $.ajax({
            url: 'index?' +
                'dics=' + model.getDicIdsStr() +
                '&ps=' + pageSize +
                '&vo=' + viewOffset +
                '&sv=' + encodeURIComponent(value) +
                '&sdn=' + shortDicName +
                '&f=' + field +
                '&a=' + SEARCH_BY_INDEX_NAVIGATION_ACTION +
                '&ts=' + Date.now(),

            contentType: "text/plain;charset=utf-8"
        }).done(function (data) {
            viewOffset = data['viewOffset'];
            controller.onIndexLoaded(data, viewOffset);
        });
    },
    onIndexLoaded: function (data, viewOffset) {
        model.index = data;

        searchField.focus();
        if (model.index == null || model.index['index'].length == 0) {
            indexView.setView();
            pagination.update();
            contentView.clear();
            searchResultsView.clear();
            searchField.disable();
        } else {
            indexView.setView();
            indexView.setViewOffset(viewOffset);
            pagination.update();
        }
    },
    loadEntry: function (hw) {
        $.ajax({
            url: 'entry?dics=' + model.getDicIdsStr() +
                '&hw=' + encodeURIComponent(hw) +
                '&ts=' + Date.now() +
                '&sv=' + encodeURIComponent($('#searchable').val()) +
                '&hl=' + controller.highlightOn,
            contentType: "text/plain;charset=utf-8"
        }).done(function (data) {
            contentView.setEntry(model.getDicIds(), data);
            $.History.go('/' + encodeURIComponent(hw) +
                '/' + encodeURIComponent(model.getDicIdsStr()) +
                '/' + encodeURIComponent(model.getDisabledDicIdsStr()) +
                '/' + encodeURIComponent(langSelectors.getSelectedLanguagePair()) +
                '/' + indexView.getViewOffset()
            );
            controller.highlightOn = false;
            searchField.focus();
        });
    },
    playAudio: function (dicShortName, resId) {
        var resUrl = '/res?a=aux&dicName=' + dicShortName + '&resId=' + encodeURIComponent(resId) + '&ts=' + Date.now();
        var audioElement = document.createElement('audio');
        audioElement.setAttribute('src', resUrl);
        audioElement.setAttribute('autoplay', 'autoplay');
        audioElement.play();
    }
}

function debouncer(func, timeout) {
    var timeoutID, timeout = timeout || 200;
    return function () {
        var scope = this, args = arguments;
        clearTimeout(timeoutID);
        timeoutID = setTimeout(function () {
            func.apply(scope, Array.prototype.slice.call(args));
        }, timeout);
    }
}

