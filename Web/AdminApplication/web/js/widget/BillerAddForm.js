/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BillerAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.BillerAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BillerAddForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 100;
        this.labelPad = 20;
        this.items = [
        {
            layout: 'form',
            autoHeight: true,
            items : [
            {
                xtype:'combo',
                fieldLabel :_("Bank Code"),
                width  : 150,
                allowBlank : false,
                anchor : '75%',
                triggerAction: "all",
                minChars : 2,
                forceSelection : true,
                pageSize : 10,
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSBank),
                RPCObject : CmFinoFIX.message.JSBank,
                displayField: CmFinoFIX.message.JSBank.Entries.BankCode._name,
                valueField : CmFinoFIX.message.JSBank.Entries.BankCode._name,
                name: CmFinoFIX.message.JSBiller.Entries.BankCodeForRouting._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Biller Name"),
                allowBlank: false,
                name: CmFinoFIX.message.JSBiller.Entries.BillerName._name,
                anchor : '75%'
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Biller Code"),
                allowBlank: false,
                name: CmFinoFIX.message.JSBiller.Entries.BillerCode._name,
                anchor : '75%'
            },
            {
                xtype: 'enumdropdown',
                enumId: CmFinoFIX.TagID.BillerType,
                fieldLabel: _("Biller Type"),
                allowBlank: false,
                mode: 'local',
                itemId : 'biller.billerType',
                triggerAction: 'all',
                emptyText : '<Select one..>',
                name: CmFinoFIX.message.JSBiller.Entries.BillerType._name,
                anchor : '75%'
            },
            {
                xtype : 'numberfield',
                fieldLabel: _('Bill Ref OffSet'),
                anchor : '75%',
                allowBlank: false,
                allowDecimals:false,
                name: CmFinoFIX.message.JSBiller.Entries.BillRefOffSet._name
            },
            {
                xtype : 'numberfield',
                fieldLabel: _('Transaction Fee'),
                anchor : '75%',
                allowBlank: false,
                allowNegative:false,
                allowDecimals:true,
                decimalPrecision : 2,
                name: CmFinoFIX.message.JSBiller.Entries.TransactionFee._name
            }
            ]
        }
        ] ;
        mFino.widget.BillerAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            if(this.store){
                if(this.record.phantom && !(this.record.store)){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
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

Ext.reg("BillerAddForm", mFino.widget.BillerAddForm);
