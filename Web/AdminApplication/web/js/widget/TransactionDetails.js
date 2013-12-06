/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionDetails = function (config)
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
                fieldLabel: _("Source MDN"),
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDN._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source Username'),
                itemId:'sourceusername',
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceUserName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Source Subscriber Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.SourceSubscriberName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Source Pocket Type'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketTypeText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Transaction Type'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionTypeText._name           
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Transfer Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStatusText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer: "date",
                fieldLabel : _('Start Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.StartTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer: "date",
                fieldLabel : _('End Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.EndTime._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Source Reference ID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceReferenceID._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Source MDNID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDNID._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Source SubscriberID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceSubscriberID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source PocketID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source Pocket Balance'),
                renderer : "money",
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketBalance._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source Pocket Description'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketTemplateDescription._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source ATM No'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceCardPAN._name,
                renderer: function(value)
                {
                    if(value)
                    {
                        var substring = value.substring(value.length-6,value.length);
                        var retval="",i;
                        for(i=0;i<(value.length-6);i++)
                        {
                            retval += 'X';
                        }
                        retval +=substring;
                        return retval;
                    }
                    else
                    {
                        return value;
                    }
                }
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Status Reason"),
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferFailureReasonText._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Operator Reject Reason'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorRejectReason._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Bank Reject Reason'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.BankRejectReason._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Billing Type'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.BillingTypeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Transfer Amount'),
                renderer : "money",
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.Amount._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Commodity'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CommodityText._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Channel Name'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.AccessMethodText._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Bank STAN'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankSystemTraceAuditNumber._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Bank RRN'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankRetrievalReferenceNumber._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                renderer: "date",
                fieldLabel :_('Bank Response Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankResponseTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bank Response Status'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankResponseCodeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bank Authorization Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankAuthorizationCode._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Reversal Count'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ReversalCount._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('LOP ID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.LOPID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('DCT Level Number'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.DistributionLevel._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source IP'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceIP._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source Terminal ID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceTerminalID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bulkupload ID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BulkUploadID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Source Message'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMessage._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Servlet Path'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ServletPath._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Topup Period'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.TopupPeriod._name,
                renderer : function(value){
                    if(value){
                        value = value.toString();
                        return  value.substr(0,4) + '-' +value.substr(4,2)+ '-' +value.substr(6,2);
                    }
                }
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('CSR UserID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CSRUserID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('CSR User Name'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CSRUserName._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Acquiring Institution Identification Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_AcquiringInstIdCode._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Card Acceptor Identification Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_CardAcceptorIdCode._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Local Transaction Time hh:mm:ss'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_LocalTxnTimeHhmmss._name,
                renderer : function(value){
                    if(value){
                        value = value.toString();
                        return  value.substr(0,2) + ':' +value.substr(2,2)+ ':' +value.substr(4,2);
                    }
                }
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Merchant Type'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_MerchantType._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                renderer : "date",
                fieldLabel :_('Creation Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CreateTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Last Update Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.LastUpdateTime._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Created By'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CreatedBy._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Updated By'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.UpdatedBy._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('CreditCard TransactionID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CreditCardTransactionID._name
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
                fieldLabel: _('Destination MDN'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Destination Username'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.DestinationUserName._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Destination Subscriber Name'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.DestSubscriberName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Destination Pocket Type'),
                anchor : '100%',
                itemId:'destinationpockettype',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketTypeText._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Reference ID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ID._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Transaction ID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionID._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Destination MDNID'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDNID._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Destination SubscriberID'),
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestSubscriberID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Destination PocketID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Destination Pocket Balance'),
                anchor : '100%',
                renderer : "money",
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketBalance._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Destination Pocket Description'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketTemplateDescription._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Destination ATM No'),
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestCardPAN._name,
                renderer: function(value)
                {
                    if(value)
                    {
                        var substring = value.substring(value.length-6,value.length);
                        var retval="",i;
                        for(i=0;i<(value.length-6);i++)
                        {
                            retval += 'X';
                        }
                        retval +=substring;
                        return retval;
                    }
                    else
                    {
                        return value;
                    }
                }
            },
            {
                xtype : 'displayfield',
                anchor : '90%',
                fieldLabel : _('Bucket Type'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BucketTypeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '90%',
                fieldLabel : _('Currency'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.Currency._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Notification Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.NotificationCodeName._name,
                renderer : function(value){
                    if(value ===null){
                        return "--";
                    }else{
                        return value;
                    }
                }
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Bank Code"),
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.BankCode._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Operator Code'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorCodeForRoutingText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Operator Response Time'),
                anchor : '100%',
                renderer: "date",
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorResponseTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Operator Response Code'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorResponseCodeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Operator Authorization Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorAuthorizationCode._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Bank Error Text'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankErrorText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Operator Error Text'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorErrorText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Last Reversal Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.LastReversalTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Bank Reversal Response Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankReversalResponseTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bank Reversal Response Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankReversalResponseCodeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bank Reversal Reject Reason'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankReversalRejectReasonText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bank Reversal Error Text'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankReversalErrorText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bulkupload Line No'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BulkUploadLineNumber._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Bank Reversal Authorization Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.BankReversalAuthorizationCode._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('CSR Action'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CSRActionText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('CSR ActionTime'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CSRActionTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('CSR Comment'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.CSRComment._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Response Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_ResponseCodeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('System Trace Audit Number'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_SystemTraceAuditNumber._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Retrieval Reference Number'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_RetrievalReferenceNum._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Processing Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_ProcessingCodeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('ISO8583_Variant'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ISO8583_VariantText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Operator Retrieval Reference Number'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorRRN._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Operator System Trace Audit Number'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorSTAN._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Operator Reversal Error Text'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorReversalErrorText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Operator Reversal Response Time'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorReversalResponseTime._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Operator Reversal Response Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorReversalResponseCodeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Operator Reversal Reject Reason'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorReversalRejectReason._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Product Indicator Code'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.ProductIndicatorCode._name
            }
            ]
        },
        {
            columnWidth: 1.0,
            layout: 'form',
            labelWidth : 150,
            items : [
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Level Permissions'),
                name : CmFinoFIX.message.JSCommodityTransfer.Entries.LevelPermissionsText._name
            }
            ]
        }]
    });

    mFino.widget.TransactionDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.TransactionDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.TransactionDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        var transactionType = record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionUICategory._name];
        if(transactionType === CmFinoFIX.TransactionUICategory.Distribute_LOP){
            this.setSourceUsername();
        }
        //TODO: Need to change and get the value properly from Backend.
        if(transactionType === CmFinoFIX.TransactionUICategory.Shareload || transactionType === CmFinoFIX.TransactionUICategory.MA_Topup ||transactionType === CmFinoFIX.TransactionUICategory.BulkTopup ||transactionType === CmFinoFIX.TransactionUICategory.Dompet_Self_Topup ||transactionType === CmFinoFIX.TransactionUICategory.Dompet_Topup_another ||transactionType === CmFinoFIX.TransactionUICategory.Bank_Channel_Topup ){
            this.setDestPocketType();
        }
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
    },
    setSourceUsername: function(){
        this.find("itemId","sourceusername")[0].setValue("Operator");
    },
    setDestPocketType: function(){
        this.find("itemId","destinationpockettype")[0].setValue("BOBAccount");
    }

});

Ext.reg("transactiondetails", mFino.widget.TransactionDetails);

