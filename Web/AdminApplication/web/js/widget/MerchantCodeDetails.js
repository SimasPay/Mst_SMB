/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantCodeDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        title: 'Merchant Code Details',
        frame : true,
        items: [        {
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Merchant Code"),
                name: CmFinoFIX.message.JSMerchantCode.Entries.MerchantCode._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("MDN"),
                name: CmFinoFIX.message.JSMerchantCode.Entries.MDN._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Create Time"),
                renderer : 'date',
                name: CmFinoFIX.message.JSMerchantCode.Entries.CreateTime._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Created By"),
                name: CmFinoFIX.message.JSMerchantCode.Entries.CreatedBy._name
            }
            ]
        }
        ]
    });

    mFino.widget.MerchantCodeDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.MerchantCodeDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.MerchantCodeDetails.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },
    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});
Ext.reg("MerchantCodeDetails", mFino.widget.MerchantCodeDetails);