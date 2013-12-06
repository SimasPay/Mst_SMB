/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.TransactionsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSCommodityTransfer);
    }
    var index = 0;
    var actionItems = [{}];
    if(mFino.auth.isEnabledItem('txngrid.view.ledgers')){
    	actionItems[index++]={
                iconCls:'mfino-button-history',
                itemId: 'txngrid.view.ledgers',
                tooltip: _('View Ledger Details')
            };
    }
    if(mFino.auth.isEnabledItem('txngrid.view.transactions')){
    	actionItems[index++]={
                iconCls:'mfino-button-View',
                itemId: 'txngrid.view.transactions',
                tooltip: _('View Transaction Details')
            };
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:actionItems
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
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store, 
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default,
            items: [sbun]
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        this.action,
        {
            header: _("Transfer ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.ID._name
        },
        {
            header: _("Time"),
            renderer: "date",
            width : 135,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.StartTime._name
        },
        {
            header: _("Transaction Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionTypeText._name
        },
		{
			header : _("Internal Txn Type"),
			width : 120,
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.InternalTxnType._name
		},	        
        {
        	header: _("Source Message"),
        	width : 135,
        	dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMessage._name
		},
        {
            header: _("From"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDN._name,
            renderer: function(value, metadata, record){
                var sourceUserName = record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.SourceUserName._name];
                var uiCaterogy = record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionUICategory._name];
                if(uiCaterogy == CmFinoFIX.TransactionUICategory.Distribute_LOP && value === '628819999901')
                {
                    return "Operator";
                }
                if(sourceUserName && sourceUserName.length > 0){
                    return record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.SourceUserName._name];
                }
               return value;
            }
        },
        {
            header: _("To"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name,
            renderer: function(value, metadata, record){
                var destUserName = record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestinationUserName._name];
                if(destUserName && destUserName.length > 0){
                    return record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestinationUserName._name];
                }
               return value;
            }
        },
        {
            header: _("Amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.Amount._name
        },
        {
            header: _("Charges"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.Charges._name
        },
        /*{
            header: _("State"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStateText._name
        },*/
        {
            header: _("Status"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStateText._name,
            renderer : function(value,a,b){
            	if(value==='Pending'){
            		return value;
            	}
            	return b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStatusText._name];
            }
        },
        {
            header: _("Status Reason"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferFailureReasonText._name
           
        },
        {
            header: _("Notification Code"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.NotificationCodeName._name,
            renderer : function(value){
                if(value === null){
                    return "--";
                }else{
                    return value;
                }
            }
        },
        {
        	header: _("RRN"),
        	width : 120,
        	dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.BankRetrievalReferenceNumber._name
		},
        {
            header: _("Channel Name"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.AccessMethodText._name
        }
        ]
    });

    mFino.widget.TransactionsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.TransactionsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    excelView: function(){
        this.fireEvent("transactiondownload");
    }
});

Ext.reg("transactionsgrid", mFino.widget.TransactionsGrid);
