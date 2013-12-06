/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.AdjustmentsChargeTransactionsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSServiceChargeTransactions);
    }
    
    var index = 0;
    var actionItems = [{}];
    actionItems[index++] = {
            iconCls:'mfino-button-history',
            itemId: 'txngrid.view.subtxns',
            tooltip: _('View Transactions')
    };
    
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:actionItems
    });
  
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },        
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),        
        autoScroll : true,
        plugins:[this.action],
        columns: [
        this.action,
        {
            header: _("Reference ID"),
            width : 80,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name
        },
        {
            header: _("Transaction Type"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name
        },
        {
            header: _("Transaction Amount"),
            renderer : "money",
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionAmount._name
        },
        {
            header: _("Adjustment Status"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.AdjustmentStatusText._name
        },
        {
            header: _("Charge From Source"),
            renderer : "money",
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.CalculatedCharge._name
        },
        {
            header: _("Charge Mode"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ChargeModeText._name
        },
        {
            header: _("Status"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransferStatusText._name
        },
        {
            header: _("Status Reason"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.FailureReason._name
        },
        {
        	 header: _("Transaction Time"),
             width : 200,
             dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionTime._name,
             renderer: "date"
        },                
        {
            header: _("SourceMDN"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.SourceMDN._name
        },
        {
            header: _("DestinationMDN"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.DestMDN._name            
        },
        {
        	header: _("AdditionalInfo"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.AdditionalInfo._name
		},
        {
            header: _("Source PartnerCode"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.SourcePartnerCode._name            
        },
        {
            header: _("Destination PartnerCode"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.DestPartnerCode._name            
        },
        {
            header: _("Biller Code"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.MFSBillerCode._name            
        },
        {
        	header: _("IntegrationRRN"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.BankRetrievalReferenceNumber._name
		},            
        {
            header: _("Channel Name"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.AccessMethodText._name
        },        
        {
        	header: _("Service Name"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ServiceName._name
		},
        {
        	header: _("Info1"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.Info1._name
		},
		{
        	header: _("Invoice No."),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.InvoiceNo._name
		},
		{
        	header: _("IntegrationType"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.IntegrationType._name
		},
		{
        	header: _("Description"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.Description._name
		},
		{
        	header: _("ReconcilationID1"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReconcilationID1._name
		},
		{
        	header: _("ReconcilationID2"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReconcilationID2._name
		},
		{
        	header: _("ReconcilationID3"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReconcilationID3._name
		}
        ]
    });

    mFino.widget.AdjustmentsChargeTransactionsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsChargeTransactionsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.AdjustmentsChargeTransactionsGrid.superclass.initComponent.call(this);        
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("adjustmentschargetransactionsgrid", mFino.widget.AdjustmentsChargeTransactionsGrid);
