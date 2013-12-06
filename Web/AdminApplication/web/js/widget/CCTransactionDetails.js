/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


Ext.ns("mFino.widget");

mFino.widget.CCTransactionDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        //      id : "CCTransactionDetails",
        layout:'column',
        frame : true,
        width:400,
        items: [        {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("CC Txn ID"),
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.ID._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Reference ID"),
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.TransactionID._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Dest MDN"),
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.MDN._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Operation'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.Operation._name
            },
//            {
//                xtype : 'displayfield',
//                fieldLabel: _("Err Code"),
//                anchor : '75%',
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.ErrCode._name
//            },
//            {
//                xtype : 'displayfield',
//                fieldLabel: _('User Code'),
//                anchor : '100%',
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.UserCode._name
//            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Trans Status'),
                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.TransStatus._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Currency Name'),
                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.CurrencyName._name
            },
//            {
//                xtype : "displayfield",
//                anchor : '60%',
//                fieldLabel :_('EUI'),
//                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.EUI._name
//            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Transaction Date'),
                renderer: "date",
                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.CreateTime._name
            },
//            {
//                xtype: "displayfield",
//                fieldLabel: _('Create Time'),
//                anchor : '100%',
//                renderer: "date",
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.CreateTime._name
//            },
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.CreatedBy._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Failure Reason'),
                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.CCFailureReasonText._name
            }
//            {
//                xtype : 'displayfield',
//                anchor : '100%',
//                fieldLabel : _('IsBlackListed'),
//                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.IsBlackListed._name
//            },{
//                xtype : 'displayfield',
//                anchor : '100%',
//                fieldLabel : _('Fraud Risk Level'),
//                name : CmFinoFIX.message.JSCreditCardTransaction.Entries.FraudRiskLevel._name
//            },
//            {
//                xtype: "displayfield",
//                anchor : '100%',
//                fieldLabel: _('Fraud Risk Score'),
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.FraudRiskScore._name
//            }
            ]
        },
        {
            columnWidth: 0.45,
            layout: 'form',
            labelWidth : 130,
            items : [
//            {
//            xtype : 'displayfield',
//            fieldLabel: _("Infinitium Txn ID"),
//            name: CmFinoFIX.message.JSCreditCardTransaction.Entries.TransactionID._name,	
//            anchor : '75%'
//            },
//            {
//                xtype: "displayfield",
//                fieldLabel: _('Card Type'),
//                anchor : '100%',
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.CardType._name
//            },
            {
                xtype: "displayfield",
                fieldLabel: _('Card No Partial'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.CardNoPartial._name
            },
//            {
//                xtype: "displayfield",
//                fieldLabel: _('Card Name'),
//                anchor : '100%',
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.CardName._name
//            },
            {
                xtype: "displayfield",
                fieldLabel: _('Acquirer Bank'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.AcquirerBank._name
            },{
                xtype: "displayfield",
                fieldLabel: _('Bank Res Msg'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.BankResCode._name
            },{
                xtype: "displayfield",
                fieldLabel: _('Auth ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.AuthID._name
            },{
                xtype: "displayfield",
                fieldLabel: _('Amount'),
                renderer: "money",
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.Amount._name
            },{
                xtype: "displayfield",
                fieldLabel: _('Bank Ref Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.BankReference._name
            },
//            {
//                xtype: "displayfield",
//                fieldLabel: _('White List Card'),
//                anchor : '100%',
//                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.WhiteListCard._name
//            },
           {
                xtype: "displayfield",
                fieldLabel: _('Last Update Time'),
                anchor : '100%',
                renderer: "date",
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.LastUpdateTime._name
            },{
                xtype: "displayfield",
                fieldLabel: _('Updated By'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.UpdatedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Exceed High Risk'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCreditCardTransaction.Entries.ExceedHighRisk._name
            }
            ]
        }]
    });

    mFino.widget.CCTransactionDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CCTransactionDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.CCTransactionDetails.superclass.initComponent.call(this);
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

Ext.reg("cctransactiondetails", mFino.widget.CCTransactionDetails);

