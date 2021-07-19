var SELECTED_PAIR_COOKIE_NAME = "SELECTED_PAIR_COOKIE_NAME";

function LangSelectors() {
}

LangSelectors.prototype = {
    constructor: LangSelectors,
    setLangs: function () {
        if (model.langs == null || model.langs.length == 0) {
            return;
        }

        $('#langsSelector').removeData("dropkick");
        $("#dk_container_langsSelector").remove();

        for (var i = 0; i < model.langs.length; i++) {
            $('#langsSelector')
                .append($("<option></option>")
                    .attr("value", model.langs[i])
                    .text(model.langs[i].toUpperCase()));
        }
        $('#langsSelector').dropkick(
            {
                change: function (value, label) {
                    $.cookie(SELECTED_PAIR_COOKIE_NAME, value, { expires: 365 });
                    controller.getDictionaries();
                }
            }
        );

        if (!$('#langSelector').is(':visible')) {
            $('#langSelector').show();
        }

        if ($.cookie(SELECTED_PAIR_COOKIE_NAME) != null) {
            if ($.inArray($.cookie(SELECTED_PAIR_COOKIE_NAME), model.langs) != -1) {
                this.setSelectedLanguage($.cookie(SELECTED_PAIR_COOKIE_NAME).toUpperCase());
            }
        }
    },
    getSourceLang: function () {
        return $('#dk_container_langsSelector span.dk_label').length > 0 ? $($('#dk_container_langsSelector span.dk_label')[0]).text().split(" ")[0] : "";
    },
    getTargetLang: function () {
        return $('#dk_container_langsSelector  span.dk_label').length > 0 ? $($('#dk_container_langsSelector span.dk_label')[0]).text().split(" ")[2] : "";
    },
    getSelectedLanguagePair: function () {
        var sl = langSelectors.getSourceLang().toLowerCase();
        var tl = langSelectors.getTargetLang().toLowerCase();
        if (sl != null && tl != null && model.langs != null) {
            for (var i = 0; i < model.langs.length; i++) {
                var langs = model.langs[i];
                if (langs.split(" ")[0] == sl && langs.split(" ")[2] == tl) {
                    return model.langs[i];
                }
            }
            return '';
        } else {
            return '';
        }
    },
    setSelectedLanguage: function (langPair) {
        $('#langsSelector').val(langPair);
        $("#dk_container_langsSelector .dk_toggle").children().text(langPair);
    }
}



