/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DenominationDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',
        autoScroll: true,
        frame : true
    });
    mFino.widget.DenominationDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DenominationDetails,mFino.widget.Form, {
    initComponent : function () {
        this.labelWidth = 160;
        this.labelPad = 20;
        this.items = [
        {
            xtype : "displayfield",
            fieldLabel :_("Denomination"),
            name: CmFinoFIX.message.JSDenomination.Entries.DenominationAmount._name
        }
        ];
        mFino.widget.DenominationDetails.superclass.initComponent.call(this);
    }
});

Ext.reg("Denominationdetails", mFino.widget.DenominationDetails);

