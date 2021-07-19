var INDEX_AT_PERCENT_COOKIE_NAME = "INDEX_AT_PERCENT_COOKIE_NAME";

function Pagination() {
    this.disableAll();

    $('#pagination').show();

    $('#toStartBtn').on('mouseup', function (e) {
        controller.getIndexViewByAction(TO_START_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
    });
    $('#toEndBtn').on('mouseup', function (e) {
        controller.getIndexViewByAction(TO_END_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
    });

    $('#fastBackwardBtn').on('mouseup', function (e) {
        controller.getIndexViewByAction(FAST_BACKWARD_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
    });
    $('#fastForwardBtn').on('mouseup', function (e) {
        controller.getIndexViewByAction(FAST_FORWARD_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
    });

    $('#pageBackBtn').on('mouseup', function (e) {
        controller.getIndexViewByAction(PAGE_BACKWARD_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
    });
    $('#pageForwardBtn').on('mouseup', function (e) {
        controller.getIndexViewByAction(PAGE_FORWARD_NAVIGATION_ACTION, encodeURIComponent(indexView.getSelectedValue()));
    });
}

Pagination.prototype = {
    constructor:Pagination,
    getPercent:function () {
        var percent = $.cookie(INDEX_AT_PERCENT_COOKIE_NAME) == null ? 0 : $.cookie(INDEX_AT_PERCENT_COOKIE_NAME);
        $.cookie(INDEX_AT_PERCENT_COOKIE_NAME, percent, { expires:365 });
        return percent;
    },
    disableAll:function () {
        $('#toStartBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        $('#fastBackwardBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        $('#pageBackBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        $('#pageForwardBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        $('#fastForwardBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        $('#toEndBtn').attr('disabled', 'disabled').css('opacity', 0.5);
    },
    enableAll:function () {
        $('#toStartBtn').removeAttr("disabled").css('opacity', 1);
        $('#fastBackwardBtn').removeAttr("disabled").css('opacity', 1);
        $('#pageBackBtn').removeAttr("disabled").css('opacity', 1);
        $('#pageForwardBtn').removeAttr("disabled").css('opacity', 1);
        $('#fastForwardBtn').removeAttr("disabled").css('opacity', 1);
        $('#toEndBtn').removeAttr("disabled").css('opacity', 1);
    },
    update:function () {
        if (model.index == null) {
            this.disableAll();
            return;
        }
        this.enableAll();

        if (model.index['isStartReached']) {
            $('#toStartBtn').attr('disabled', 'disabled').css('opacity', 0.5);
            $('#fastBackwardBtn').attr('disabled', 'disabled').css('opacity', 0.5);
            $('#pageBackBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        }
        if (model.index['isEndReached']) {
            $('#pageForwardBtn').attr('disabled', 'disabled').css('opacity', 0.5);
            $('#fastForwardBtn').attr('disabled', 'disabled').css('opacity', 0.5);
            $('#toEndBtn').attr('disabled', 'disabled').css('opacity', 0.5);
        }
    }
}

