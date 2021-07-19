function SearchResultsView() {
}

SearchResultsView.prototype = {
    constructor: SearchResultsView,
    setResults: function (data) {
        this.clear();
        $('#searchMessage').text("Full-text search results for: ");
        $('#searchTerm').text($('#searchable').val());
        for (var i = data.length - 1; i >= 0; i--) {
            var dicId = data[i]['dictionaryId'];
            var dic = model.getDicById(dicId);
            var hw = data[i]['headword'];
            $('#searchResults').append('<div style="padding-bottom: 5px;">');

            $('#searchResults').append('<span class="dicName">' + dic['name.short'] + '</span>');

            var js = "javascript:controller.highlightOn=true;controller.getIndexViewByAction(SEARCH_NAVIGATION_ACTION,\'" + encodeURIComponent(encodeURIComponent(hw)) + "\');";
            $('#searchResults').append('<a href="' + js + '">' + hw + '</a>');
            $('#searchResults').append('<span>' + data[i]['text'] + '</span>');
            $('#searchResults').append('</div>');
        }
    },
    clear: function () {
        $('#searchMessage').text("");
        $('#searchTerm').text("");
        $('#searchResults').empty();

    }
}

