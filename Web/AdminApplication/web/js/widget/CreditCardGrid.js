/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.CreditCardGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSCreditCardTransaction);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View Credit Card Transaction Details')
        }
        ]
    });
    var sbun = new Ext.Toolbar.Button({
        enableToggle: false,
        text: _('Export to Excel'),
        iconCls: 'mfino-button-excel',
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
    });
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        plugins:[this.action],
        viewConfig: {
            emptyText: Config.grid_no_data
        },
        listeners : {
            render : function(){
                this.fireEvent("creditCardSearch");
            }
        },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default,
            items: [sbun]
        }),
        autoScroll : true,
        columns: [
        this.action,
        {
            header: _("CC Txn ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.ID._name
        },
        {
            header: _("Reference ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.TransactionID._name
        },
        {
            header: _("BucketType"),
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CCBucketTypeText._name
        },
        {
            header: _("Opeartion"),
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.Operation._name
        },
//        {
//            header: _("Infinitium Txn ID"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.TransactionID._name
//        },
        {
            header: _("Destination MDN"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.MDN._name
        },
        {
            header: _("Currency Name"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CurrencyName._name
        },
        {
            header: _("Transaction Date"),
            width : 150,
            renderer: "date",
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CreateTime._name
        },
        {
            header: _("NSIATransCompletionTime"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.NSIATransCompletionTime._name
        },
        {
            header: _("Amount"),
            renderer: "money",
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.Amount._name
        },
        {
            header: _("Card No Partial"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CardNoPartial._name
        },
        {
            header: _("Acquirer Bank"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.AcquirerBank._name
        },
        {
            header: _("Bank Res Code"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.BankResCode._name
        },
        {
            header: _("Bank Res Msg"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.BankResMsg._name
        },
        {
            header: _("Auth ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.AuthID._name
        },
        {
            header: _("Bill Reference Number"),
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.BillReferenceNumber._name
        },
//        {
//            header: _('Create Time'),
//            renderer: "date",
//            width : 150,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CreateTime._name
//        },
        {

            header: _('Created By'),
            width : 150,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CreatedBy._name
        },
        {
            header: _('Last Update Time'),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.LastUpdateTime._name
        },
        {
            header: _('Updated By'),
            width : 150,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.UpdatedBy._name
        }, 
        {
            header: _("Trans Status"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.TransStatus._name
        },
        {
            header: _("Failure Reason"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CCFailureReasonText._name
        }
        //these are the fields that are present in the Infinitum
//        {
//            header: _("Payment Method"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.PaymentMethod._name
//        },
//        {
//            header: _("Err Code"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.ErrCode._name
//        },
//        {
//            header: _("User Code"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.UserCode._name
//        },
//        {
//            header: _("EUI"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.EUI._name
//        },
//        {
//            header: _("Trans Type"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.TransType._name
//        },
//        {
//            header: _("IsBlackListed"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.IsBlackListed._name
//        },
//        {
//            header: _("Fraud Risk Level"),
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.FraudRiskLevel._name
//        },
//        {
//            header: _("Fraud Risk Score"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.FraudRiskScore._name
//        },
//        {
//            header: _("Exceed High Risk"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.ExceedHighRisk._name
//        },
//        {
//            header: _("Card Type"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CardType._name
//        },
//        {
//            header: _("Card Name"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.CardName._name
//        },
//        {
//            header: _("Bank Ref Number"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.BankReference._name
//        },
//        {
//            header: _("White List Card"),
//            width : 100,
//            dataIndex: CmFinoFIX.message.JSCreditCardTransaction.Entries.WhiteListCard._name
//        }    
        ]
    });

    mFino.widget.CreditCardGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CreditCardGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.CreditCardGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.onSearch();
        }
    },
    excelView: function(){
        this.fireEvent("downloadCC");
    }
});

Ext.reg("CreditCardgrid", mFino.widget.CreditCardGrid);

