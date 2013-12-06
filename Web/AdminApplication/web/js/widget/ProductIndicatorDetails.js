/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ProductIndicatorDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 120,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Transaction Type"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.TransactionUICategoryText._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Channel"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.ChannelSourceApplicationText._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Company Name"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.CompanyName._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Requestor ID"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.RequestorID._name
            }
            ]
            },
            {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 140,
            items : [          
            {
                xtype: 'displayfield',
                fieldLabel: _("Product Description"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.ProductDescription._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Channel Text"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.ChannelText._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Product Indicator"),
                name: CmFinoFIX.message.JSProductIndicator.Entries.ProductIndicatorCode._name
            }
            ]
            }
        ]
    });

    mFino.widget.ProductIndicatorDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ProductIndicatorDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.ProductIndicatorDetails.superclass.initComponent.call(this);
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

Ext.reg("ProductIndicatorDetails", mFino.widget.ProductIndicatorDetails);