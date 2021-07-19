var SCROLL_TO_COOKIE_NAME = "SCROLL_TO_COOKIE_NAME";

function ContentView() {
}

ContentView.prototype = {
    constructor:ContentView,
    setEntry:function (dicIds, data) {
        var len = $.map(data,function (n, i) {
            return i;
        }).length;
        this.clear();
        window.scrollTo(0, 0);
        for (var i = 0; i < dicIds.length; i++) {
            var dicId = dicIds[i];
            if (data[dicId] == null) {
                continue;
            }
            $('#content').append('<span class="dic-name">' + model.getDicById(dicId)['name'] + '</span>');
            $('#content').append('<br/>');
            $('#content').append(data[dicId]);
            if (len > 1 && i < len - 1) {
                $('#content').append('<hr/>');
            }
        }
        myLayout.resizeAll("center");

        if ($.cookie(SCROLL_TO_COOKIE_NAME) != null) {
            $('#content').scrollTo($("#" + $.cookie(SCROLL_TO_COOKIE_NAME)));
            $.removeCookie(SCROLL_TO_COOKIE_NAME);
        }
    },
    clear:function () {
        $('#content').empty();

    }
}

