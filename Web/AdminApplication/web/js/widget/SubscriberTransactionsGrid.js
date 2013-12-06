/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
var currRecord = null;
var mdnid;
mFino.widget.SubscriberTransactionsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSCommodityTransfer);
    }
	
	var index = 0;
	var actionItems = [{}];
	if(mFino.auth.isEnabledItem('txngrid.view.chargedetails') && config.parenttab=="dhpartnertabpanel"){
		actionItems[index++]={
			iconCls:'mfino-button-View',
			itemId: 'txngrid.view.chargedetails',
			tooltip: _('View Charge Distribution Details')
		};
	}
	this.action = new Ext.ux.grid.RowActions({
		header:'',
		keepSelection:true,
		actions:actionItems
	});
		
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
        layout : 'fit',
        loadMask : true,
        viewConfig: { 
            emptyText: Config.grid_no_data
        },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
		plugins:[this.action],
	
        columns: [
		this.action,
		{
		    header: _("Reference ID"),
		    dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.ServiceChargeTransactionLogID._name
		},                  
        {
            header: _("Transfer ID"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionID._name
        },
        {
            header: _("Bank RRN"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.BankRetrievalReferenceNumber._name
        },        
        {
            header: _("Date"),
            renderer : "date",
            width : 135,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.StartTime._name
        },
        {
            header: _("Channel Name"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.AccessMethodText._name
        },
        {
            header: _("Transaction Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionTypeText._name
        },
        {
            header: _("Internal Txn Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.InternalTxnType._name
        },        
        {
			header : _("Source"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDN._name/*,
			renderer : function(value, a, b) {
				var credit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name];
				var debit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name];
				if (credit !== null) {
					return value;
				} else {
					return b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name];
				}
			}*/
		},
		 {
			header : _("Destination"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name
			
		},
		{
			header : _("Source PocketID"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketID._name/*,
			renderer : function(value, a, b) {
				var credit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name];
				var debit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name];
				if (credit !== null) {
					return b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketID._name];
				} else {
					return value;
				}
			}*/
		},
		{
			header : _("Destination PocketID"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketID._name
		},
		{
			header : _("Credit Amount"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name,
			renderer : function(value){
			if(value===null){
				return "--";
			}else{
				return Ext.util.Format.money(value);
			}
			}
		},
		{
			header : _("Debit Amount"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name,
			renderer : function(value){
			if(value===null){
				return "--";
			}else{
				return Ext.util.Format.money(value);
			}
			}
		},
		/*{
			header : _("Opening Balance"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketBalance._name,
			renderer : function(value, a, b) {
				var credit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name];
				var debit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name];
				if (debit !== null) {
					if (value === null) {
						return "--";
					} else {
						return Ext.util.Format.money(value);
					}
				} else {
					if (b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketBalance._name] === null) {
						return "--";
					} else {
						return Ext.util.Format.money(b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketBalance._name]);
					}
				}
			}
		},
		{
			header : _("Closing Balance"),
			dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketClosingBalance._name,
			renderer : function(value, a, b) {
				var credit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name];
				var debit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name];
				if (debit !== null) {
					if (value === null) {
						return "--";
					} else {
						return Ext.util.Format.money(value);
					}
				} else {
					if (b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketClosingBalance._name] === null) {
						return "--";
					} else {
						return Ext.util.Format.money(b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketClosingBalance._name]);
					}
				}
			}
		},*/
        {
            header: _("Commodity"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.CommodityText._name
        },
        {
            header: _("Status"),
            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStatusText._name
        }
        ]
    });

    mFino.widget.SubscriberTransactionsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberTransactionsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        this.tbar =  ['->'/*,
        //   '<a href="#"  style="color:blue"  onclick="mFino.widget.SubscriberTransactionsGrid.prototype.transaction();"> Goto Transaction Page </a>',
        {
            xtype : 'button',
            iconCls : "mfino-button-forward",
            itemId : 'trans.button',
            tooltip : _('Goto Transaction Page'),
            handler :this.propagateData.createDelegate(this)
        }*/];
        mFino.widget.SubscriberTransactionsGrid.superclass.initComponent.call(this);
        this.on('render', this.removeDisabled, this);
    },

/*    propagateData: function() {
        var mdnId =this.mdnid;
        var tSearch = Ext.getCmp('transactionsearchfrom');
        if(tSearch){
        tSearch.getForm().items.each(function(item) {
            if(item.getXType() == 'textfield' || item.getXType() == 'daterangefield' ||
                item.getXType() == 'numberfield' || item.getXType()=='enumdropdown') {
                item.reset();
            }
        });

        tSearch.find("itemId","transferstate")[0].setValue(CmFinoFIX.TransferState.Complete);
        tSearch.find("itemId","subscriberid")[0].setValue( this.currRecord.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name]);
        tSearch.stateChangeOutSide(CmFinoFIX.TransferState.Complete);

//        var compEl = Ext.get('tradaterange');
//        if(compEl){
//            Ext.query("*[type=text]",compEl.dom)[0].value = "";
//            Ext.query("*[type=text]",compEl.dom)[1].value = "";
//            Ext.query("*[type=text]",compEl.dom)[2].value = "";
//        }

        tSearch.searchTransactions();
    }
    },*/
    
    setRecord : function(record){
        this.currRecord = record;
        this.setMDNID(this.currRecord.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name]);
    },
    
    setMDNID : function(mdnid){
        this.mdnid = mdnid;
    },
    
    removeDisabled: function(){
        var tb = this.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
        for(i = 0; i < itemIDs.length; i++){
            var itemID = itemIDs[i];
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    }
});

Ext.reg("subscribertransactionsgrid", mFino.widget.SubscriberTransactionsGrid);
