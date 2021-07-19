var VIEW_OFFSET_COOKIE_NAME = "VIEW_OFFSET_COOKIE_NAME";
var LAST_SELECTED_VALUE_COOKIE_NAME = "LAST_SELECTED_VALUE_COOKIE_NAME";

function IndexView() {
    //index items
    $("#selectable").selectable();

    $("#selectable").bind("selectableselected", debouncer(function (event, ui) {
        var offset = indexView.getViewOffset();
        model.viewOffset = offset;
        $.cookie(VIEW_OFFSET_COOKIE_NAME, offset, { expires: 365 });
        $.cookie(LAST_SELECTED_VALUE_COOKIE_NAME, indexView.getSelectedValue(), { expires: 365 });
        controller.loadEntry(indexView.getSelectedValue());
    }));

    $('#selectable').bind('mousewheel', function (event, delta) {
        indexView.shiftTo(delta * -1);
        return false;
    });

    $('#selectable').selectable({
        selecting: function (event, ui) {
            if ($(".ui-selected, .ui-selecting").length > 1) {
                $(ui.selecting).removeClass("ui-selecting");
            }
        }
    });

}

IndexView.prototype = {
    constructor: IndexView,
    shiftTo: function (one) {
        var newLocalIndex = this.getViewOffset() + one;
        if (newLocalIndex < 0 || newLocalIndex >= $("#selectable li").length) {
            return;
        }
        this.setViewOffset(newLocalIndex);
    },
    getPageSize: function () {
        var rowHeight = $("#selectable").find('li:first').outerHeight();
        if ($("#selectable").find('li').length == 0) {
            $("#selectable").append(this.wrapHeadword(1, '&nbsp;'));
            rowHeight = $("#selectable").find('li:first').outerHeight();
            this.clearView();
        }
        var tableHeight = $('#indexContainer').height();//$(window).height() - $('#searchable').outerHeight() - $('#pagination').outerHeight();
        var newRowCount = tableHeight / rowHeight;
        return Math.floor(newRowCount);
    },
    getViewOffset: function () {
        if ($('.ui-selected').length > 0) {
            var val = $('.ui-selected')[0].id.split("_")[1];
            return parseInt(val, 10) || 0;
        } else if ($.cookie(VIEW_OFFSET_COOKIE_NAME) == null) {
            return 0;
        } else {
            return parseInt($.cookie(VIEW_OFFSET_COOKIE_NAME), 10) || 0;
        }
    },
    setViewOffset: function (vo) {
        var ps = this.getPageSize();
        if (vo >= ps)vo = ps - 1;
        if (vo < 0)vo = 0;
        this.selectHeadword($("#selectable"), $("#selectable").children(":eq(" + vo + ")"));
    },
    getSelectedValue: function () {
        if (model.index == null || model.index['index'].length == 0)return '';
        return model.index['index'][this.getViewOffset()];
    },
    setView: function () {
        this.clearView();
        if (model.index != null) {
            for (var i = 0; i < model.index['index'].length; i++) {
                $("#selectable").append(this.wrapHeadword(i, model.index['index'][i]));
            }
        }
    },
    selectHeadword: function (selectableContainer, elementToSelect) {
        // add unselecting class to all elements in the styleboard canvas except current one
        jQuery("li", selectableContainer).each(function () {
            if (this != elementToSelect[0])
                jQuery(this).removeClass("ui-selected").addClass("ui-unselecting");
        });

        // add ui-selecting class to the element to select
        elementToSelect.addClass("ui-selecting");
        $("#selectable").selectable('refresh');
        // trigger the mouse stop event (this will select all .ui-selecting elements, and deselect all .ui-unselecting elements)
        selectableContainer.data("selectable")._mouseStop(null);
    },
    clearView: function () {
        $("#selectable").find('li').remove();
    },
    wrapHeadword: function (locId, hw) {
        return '<li id="id_' + locId + '" class="ui-widget-content index-item">' + hw + '</li>';
    }
}







