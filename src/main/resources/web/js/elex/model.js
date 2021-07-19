function Model() {
}

Model.prototype = {
    constructor: Model,
    index: [],
    dics: [],
    langs: [],
    viewOffset: null,
    getDicIdsStr: function () {
        var dicIdsStr = "";
        var dicIds = this.getDicIds();
        for (var i = 0; i < dicIds.length; i++) {
            dicIdsStr += "_" + dicIds[i];
        }
        return dicIdsStr;
    },

    getDisabledDicIdsStr: function () {
        var dicIdsStr = "";
        var dicIds = this.getDisabledDicIds();
        for (var i = 0; i < dicIds.length; i++) {
            dicIdsStr += "_" + dicIds[i];
        }
        return dicIdsStr;
    },

    getDicIds: function () {
        var myArray = [];

        var cookieList = $.fn.cookieList(ENABLED_DICTIONARIES_COOKIE_NAME);
        for (var i = 0; i < this.dics.length; i++) {
            var id = this.dics[i]['DICTIONARY_ID'];
            var dicName = this.dics[i]['name'];

            if (cookieList.indexOf(dicName) >= 0) {
                myArray.push(id);
            }
        }
        return myArray;
    },
    getDisabledDicIds: function () {
        var myArray = [];

        var cookieList = $.fn.cookieList(ENABLED_DICTIONARIES_COOKIE_NAME);
        for (var i = 0; i < this.dics.length; i++) {
            var id = this.dics[i]['DICTIONARY_ID'];
            var dicName = this.dics[i]['name'];

            if (cookieList.indexOf(dicName) < 0) {
                myArray.push(id);
            }
        }
        return myArray;
    },
    getDicById: function (idIn) {
        for (var i = 0; i < this.dics.length; i++) {
            var id = this.dics[i]['DICTIONARY_ID'];
            if (id == idIn) {
                return this.dics[i];
            }
        }
        return null;
    }
}


