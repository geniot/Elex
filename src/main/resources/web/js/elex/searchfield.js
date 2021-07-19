function SearchField() {
    //enter key should run search
    $('#searchable').keypress(function (e) {
        if (e.which == 13) {
            if ($.trim($('#searchable').val()).length > 0) {
                controller.getIndexViewByAction(SEARCH_NAVIGATION_ACTION, $('#searchable').val());
                controller.runFullTextSearch($('#searchable').val());
            }
        }
    });
}

SearchField.prototype = {
    constructor:SearchField,
    focus:function (l) {
        $('#searchable').attr("disabled", false);
        $('#searchable').attr("readonly", false);
        $('#searchable').focus();
    },
    disable:function () {
        $('#searchable').attr("disabled", true);
        $('#searchable').attr("readonly", true);
    }
}


