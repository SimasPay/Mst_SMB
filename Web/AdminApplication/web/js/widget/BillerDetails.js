/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BillerDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        width: 660,
        height : 173,
        items: [    {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Biller Name"),
                name: CmFinoFIX.message.JSBiller.Entries.BillerName._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Biller Code"),
                name: CmFinoFIX.message.JSBiller.Entries.BillerCode._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Biller Type'),
                anchor : '75%',
                name: CmFinoFIX.message.JSBiller.Entries.BillerType._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Bank Code"),
                name: CmFinoFIX.message.JSBiller.Entries.BankCodeForRouting._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Bill Ref OffSet"),
                name: CmFinoFIX.message.JSBiller.Entries.BillRefOffSet._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                anchor : '75%',
                fieldLabel : _('Transaction Fee'),
                name : CmFinoFIX.message.JSBiller.Entries.TransactionFee._name
            }
            ]
        },
        {
            columnWidth: 0.45,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Biller ID'),
                name : CmFinoFIX.message.JSBiller.Entries.ID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Creation Time'),
                name : CmFinoFIX.message.JSBiller.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSBiller.Entries.CreatedBy._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Last Update Time'),
                name : CmFinoFIX.message.JSBiller.Entries.LastUpdateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Updated By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSBiller.Entries.UpdatedBy._name
            }
            ]
        }]
    });

    mFino.widget.BillerDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BillerDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.BillerDetails.superclass.initComponent.call(this);
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

Ext.reg("BillerDetails", mFino.widget.BillerDetails);

