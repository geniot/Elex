var ENABLED_DICTIONARIES_COOKIE_NAME = "ENABLED_DICTIONARIES_COOKIE_NAME";

function DictionariesToolbar() {
    if (!$('#toolbar').is(':visible')) {
        $('#toolbar').show();
    }
}

DictionariesToolbar.prototype = {
    constructor: DictionariesToolbar,
    setDics: function () {

        $('#dictionariesToolbar').empty();
        if (model.dics == null || model.dics.length == 0) {
            return;
        }
        for (var i = 0; i < model.dics.length; i++) {
            var id = model.dics[i]['DICTIONARY_ID'];
            var name = model.dics[i]['name'];
            $('#dictionariesToolbar').append('<div id="dictionaryButton_' + id + '" class="toolbar_button">' +
                '<a>' +
                '<img style="padding-left: 1px" class="localizable" alt="" title="' + name + '" src="/res?a=icon&id=' + id + '"/>' +
                '</a>' +
                '</div>');

            var cookieList = $.fn.cookieList(ENABLED_DICTIONARIES_COOKIE_NAME);
            if (cookieList.indexOf(name) >= 0) {
                $('#dictionaryButton_' + id).toggleClass("toolbar_button_down");
            }

            $('#dictionaryButton_' + id).click(function () {
                var locId = this.id.split("_")[1];
                $(this).toggleClass("toolbar_button_down");
                var cookieList = $.fn.cookieList(ENABLED_DICTIONARIES_COOKIE_NAME);
                var dicName = model.getDicById(locId)['name'];
                if ($(this).hasClass("toolbar_button_down")) {
                    cookieList.add(dicName);
                } else {
                    cookieList.remove(dicName);
                }
                controller.onDictionarySelectionChanged();
            });

        }
        $('#dictionariesToolbar').append('<div style="clear: both; height: 0; width: 100%;" />');


    }
}

