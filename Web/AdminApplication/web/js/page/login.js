/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.login = function(config){
    if(!config || !config.renderTo){
        if(console){
            console.log("renderTo property is required in login page.");
        }
        return;
    }

    var login = new mFino.widget.Login();
    login.render(Ext.get(config.renderTo));
};
