/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PartnerTransactionDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
         autoScroll : true,

        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 150,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("PartnerID"),
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.PartnerID._name,
                anchor : '100%'
            },
//            {
//                xtype : 'displayfield',
//                anchor : '100%',
//                fieldLabel : _('Partner TradeName'),
//                itemId:'sourceusername',
//                name : CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TradeName._name
//            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Reference ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransactionID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Amount"),
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ShareAmount._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source MDN'),
                itemId:'sourceusername',
                name : CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.SourceMDN._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('CreateTime'),
                anchor : '100%',
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.CreateTime._name
            }
            
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 160,
            items : [
            {
                xtype: "displayfield",
                fieldLabel: _('PocketID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.PocketID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Status'),
                name : CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransferStatusText._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('CreateTime'),
                name : CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.CreateTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("LastUpdateTime"),
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.LastUpdateTime._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('LastUpdatedBy'),
                itemId:'sourceusername',
                name : CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.UpdatedBy._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Channel'),
                anchor : '100%',
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ChannelSourceApplicationText._name
            }
            ]
        }
            
        ]
    });

    mFino.widget.PartnerTransactionDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.PartnerTransactionDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.PartnerTransactionDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
//        var transactionType = record.data[CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransactionUICategory._name];
//        if(transactionType === CmFinoFIX.TransactionUICategory.Distribute_LOP){
//            this.setSourceUsername();
//        }
//        //TODO: Need to change and get the value properly from Backend.
//        if(transactionType === CmFinoFIX.TransactionUICategory.Shareload || transactionType === CmFinoFIX.TransactionUICategory.MA_Topup ||transactionType === CmFinoFIX.TransactionUICategory.BulkTopup ||transactionType === CmFinoFIX.TransactionUICategory.Dompet_Self_Topup ||transactionType === CmFinoFIX.TransactionUICategory.Dompet_Topup_another ||transactionType === CmFinoFIX.TransactionUICategory.Bank_Channel_Topup ){
//            this.setDestPocketType();
//        }
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
//   

});

Ext.reg("partnertransactiondetails", mFino.widget.PartnerTransactionDetails);

