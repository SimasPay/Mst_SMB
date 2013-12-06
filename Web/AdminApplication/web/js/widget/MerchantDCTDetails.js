/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantDCTDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'form',
        frame : true,
        labelWidth : 250,
        items : [
        {
            xtype : "displayfield",
            anchor : '100%',
            itemId : 'dctname',
            fieldLabel :_('Distribution Chain Template')
        },
        {
            xtype : 'displayfield',
            fieldLabel: _("Level"),
            itemId : 'level',
            anchor : '100%'
        },
        {
            xtype : 'displayfield',
            fieldLabel: _("Permissions"),
            anchor : '100%',
            itemId : 'permissions'
        }
        ]
    });

    mFino.widget.MerchantDCTDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantDCTDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.MerchantDetails.superclass.initComponent.call(this);
    },
    setValues : function(dct,level,permissions){
        this.items.get("dctname").setValue(dct);
        this.items.get("level").setValue(level);
        this.items.get("permissions").setValue(permissions);
    }
});

Ext.reg("merchantdctdetails", mFino.widget.MerchantDCTDetails);

