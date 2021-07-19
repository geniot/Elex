var ELEXIMAGE_LAYOUT_COOKIE_NAME = "ELEXIMAGE_LAYOUT_COOKIE_NAME";

if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function (obj, start) {
        for (var i = (start || 0), j = this.length; i < j; i++) {
            if (this[i] === obj) {
                return i;
            }
        }
        return -1;
    }
}

function onWindowResize(callback) {
    var width = $(window).width(),
        height = $(window).height();

    $(window).resize(debouncer(function () {
        var newWidth = $(window).width(),
            newHeight = $(window).height();

        if (newWidth !== width || newHeight !== height) {
            width = newWidth;
            height = newHeight;
            callback();
        }
    }));
}

if (!Date.now) {
    Date.now = function now() {
        return new Date().getTime();
    };
}

jQuery.fn.scrollTo = function(elem, speed) {
    $(this).animate({
        scrollTop:  $(this).scrollTop() - $(this).offset().top + $(elem).offset().top
    }, speed == undefined ? 1000 : speed);
    return this;
};

(function ($) {
    $.fn.extend({
        cookieList:function (cookieName, expireTime) {

            var cookie = $.cookie(cookieName);
            var items = cookie ? $.secureEvalJSON(cookie) : [];

            return {
                add:function (val) {
                    var index = items.indexOf(val);
                    // Note: Add only unique values.
                    if (index == -1) {
                        items.push(val);
                        $.cookie(cookieName, $.toJSON(items), { expires:expireTime, path:'/' });
                    }
                },
                remove:function (val) {
                    var index = items.indexOf(val);

                    if (index != -1) {
                        items.splice(index, 1);
                        $.cookie(cookieName, $.toJSON(items), { expires:expireTime, path:'/' });
                    }
                },
                indexOf:function (val) {
                    return items.indexOf(val);
                },
                clear:function () {
                    items = null;
                    $.cookie(cookieName, null, { expires:expireTime, path:'/' });
                },
                items:function () {
                    return items;
                },
                length:function () {
                    return items.length;
                },
                join:function (separator) {
                    return items.join(separator);
                }
            };
        }
    });
})(jQuery);

var contentView, controller, indexView, model, pagination, searchField;
var myLayout;


$(function () {
    myLayout = $("body").layout({
        initClosed:true, west__size:150, east__size:150, east__fxSpeed:0, west__fxSpeed:0, livePaneResizing:true, animatePaneSizing:true // changes in pane-sizes when resetting state will be animated

        , stateManagement__enabled:true // enable stateManagement - automatic cookie load & save enabled by default
        , stateManagement__cookie: { name: ELEXIMAGE_LAYOUT_COOKIE_NAME, path: "/" }
        /*	sample formats for customizing stateManagement.keys
         ,	stateManagement__stateKeys:	"west.size,east.size,west.isClosed,east.isClosed"
         ,	stateManagement__stateKeys:	"west.size,north.size"		// state-keys in sub-key format
         ,	stateManagement__stateKeys:	"west__size,north__size"	// state-keys in flat-format
         */

        /*	enable this block to use the CUSTOM state-management functions above
         ,	stateManagement__autoLoad:	false // disable automatic cookie-load
         ,	stateManagement__autoSave:	false // disable automatic cookie-save
         ,	onload:						customLoadState // run custom state-code when Layout loads
         ,	onunload:					customSaveState // ditto when page unloads OR Layout is 'destroyed'
         */
    });
    //debugData( myLayout.options.stateManagement, 'options.stateManagement' );
    //debugData( myLayout.options.west.fxSpeed, 'options.west.fxSpeed' );

    // sync checkbox with layout state options
    var state = myLayout.options.stateManagement;
    $('#autoSaveState').attr("checked", state.enabled && state.autoSave);

    // save ALL states of ALL panes to test loadState (reset button)
    window.fullState = myLayout.readState(
        "north.size,south.size,east.size,west.size," +
        "north.isClosed,south.isClosed,east.isClosed,west.isClosed," +
        "north.isHidden,south.isHidden,east.isHidden,west.isHidden"
    );

    if ($.cookie(ELEXIMAGE_LAYOUT_COOKIE_NAME) == null){
        $.each(["west"], function(i, pane){
            myLayout.toggle( pane );
        });
    }


    contentView = new ContentView();
    controller = new Controller();
    indexView = new IndexView();
    model = new Model();
    pagination = new Pagination();
    searchField = new SearchField();
    alert('tes24t')
    controller.init();
});





