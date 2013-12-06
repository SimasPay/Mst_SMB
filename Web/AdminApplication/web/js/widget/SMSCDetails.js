/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSCDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',
        autoScroll: true,
        frame : true
    });

    mFino.widget.SMSCDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSCDetails,mFino.widget.Form, {
    initComponent : function () {
        this.labelWidth = 160;
        this.labelPad = 20;
        
        this.items = [
        {
            xtype : "displayfield",
            fieldLabel :_("ShortCode"),
            name: CmFinoFIX.message.JSSMSC.Entries.ShortCode._name
            
        },
        {
            xtype : "displayfield",
        	 fieldLabel: _('LongNumber'),
            name: CmFinoFIX.message.JSSMSC.Entries.LongNumber._name
        },
        {
            xtype: "displayfield",
            fieldLabel: _('SmartfrenSMSCID'),
            name: CmFinoFIX.message.JSSMSC.Entries.SmartfrenSMSCID._name
        },
        {
            xtype : "displayfield",
        	 fieldLabel: _("OtherLocalOperatorSMSCID"),
            name: CmFinoFIX.message.JSSMSC.Entries.OtherLocalOperatorSMSCID._name
        },
        {
            xtype : "displayfield",
        	 fieldLabel: _("Charging"),
            name: CmFinoFIX.message.JSSMSC.Entries.Charging._name
        },
        {
            xtype : "displayfield",
            fieldLabel: _("Header"),
            name: CmFinoFIX.message.JSSMSC.Entries.Header._name
        },
        {
            xtype : "displayfield",
            fieldLabel: _("Footer"),
            name: CmFinoFIX.message.JSSMSC.Entries.Footer._name
        }
        ];

        mFino.widget.SMSCDetails.superclass.initComponent.call(this);
    }
});

Ext.reg("smscdetails", mFino.widget.SMSCDetails);

