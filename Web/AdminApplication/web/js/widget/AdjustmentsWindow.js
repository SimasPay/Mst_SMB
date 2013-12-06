/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AdjustmentsWindow = function (config){
    var localConfig = Ext.apply({}, config);
    this.searchForm = new mFino.widget.AdjustmentsSctlSearchForm(Ext.apply({
    	autoHeight: true,
        width: 800
    }, config));
    
    this.chargeTxnGrid = new mFino.widget.AdjustmentsChargeTransactionsGrid(Ext.apply({
		title: 'Sctl Details',
	    height : 110
     },config));
    
    this.transactionsView = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
        title : " Transactions  ",
        grid : new mFino.widget.AdjustmentsSctlTransactionsView(config),
        height : 466,
        width: 800
    },config));
    
    this.ledgerGrid = new mFino.widget.AdjustmentsLedgerGrid(Ext.apply({
		title: 'Ledger Details',
	    height : 200
     },config));
    
    this.adjustmentForm = new mFino.widget.AdjustmentsForm(Ext.apply({
		title: 'Adjustment Form',
	    height : 170
     },config));
    
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        resizable : false,
        width: 810,
        height:560,
        closable:true,
        plain:true
    });
    mFino.widget.AdjustmentsWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsWindow, Ext.Window, {
    initComponent : function(){
       
       this.items = [{  	    
			       		layout: "fit",
				        items: [this.searchForm, this.chargeTxnGrid, this.ledgerGrid, this.adjustmentForm ]
	        	}];
       this.addEvents("adjustmentFormSubmit");
        mFino.widget.AdjustmentsWindow.superclass.initComponent.call(this);
        
        this.searchForm.on("search", function(values){
        	if(values.SctlId == ""){
        		return;
        	}
        	this.chargeTxnGrid.store.baseParams[CmFinoFIX.message.JSServiceChargeTransactions.IDSearch._name] = values.SctlId;
        	this.chargeTxnGrid.store.load();
        	
        	this.ledgerGrid.store.baseParams[CmFinoFIX.message.JSLedger.SctlId._name] = values.SctlId;
            this.ledgerGrid.store.load(); 
        }, this);
        
        this.chargeTxnGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        	if(this.mode == 'add') { // sctlSearchForm and adjustmentForm will be enabled only in add mode
        		if(record.data.AdjustmentStatus != null && 
        				record.data.AdjustmentStatus != CmFinoFIX.AdjustmentStatus.Failed && 
        				record.data.AdjustmentStatus != CmFinoFIX.AdjustmentStatus.Rejected) {
            		Ext.MessageBox.alert(_("Alert"), _("Adjustment is already in requested/completed status for this transaction"));
            		this.adjustmentForm.resetHandler();
            		this.adjustmentForm.setEditable(false);  		
            		return;
            	}
            	if(record.data.Status == CmFinoFIX.SCTLStatus.Pending || 
            			record.data.Status == CmFinoFIX.SCTLStatus.Pending_Resolved ||
            				record.data.Status == CmFinoFIX.SCTLStatus.Pending_Resolved_Processing ||
            					record.data.Status == CmFinoFIX.SCTLStatus.Distribution_Completed) {
            		Ext.MessageBox.alert(_("Alert"), _("Adjustment cannot be done for this pending/completed transaction"));
            		this.adjustmentForm.resetHandler();
            		this.adjustmentForm.setEditable(false);	
            		return;
            	}
            	this.adjustmentForm.setEditable(true);
            	this.adjustmentForm.setRecord(record);
        	}        	          
        }, this);
        
        this.chargeTxnGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-history'){
                	var transactionsView = this.transactionsView;
                    transactionsView.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name] = record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
                    transactionsView.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.JSMsgType._name] = CmFinoFIX.MsgType.JSChargeTransactions;
                    transactionsView.grid.store.load();
                    transactionsView.setStore(transactionsView.grid.store);
                    transactionsView.setTitle("Transactions related to Reference Id " + record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name));
                    transactionsView.show();
                }
            },
            scope: this
        });
        
        this.adjustmentForm.on("search", function(values){        	
        	var msg = new CmFinoFIX.message.JSAdjustments();
        	msg.m_pSctlId = values[CmFinoFIX.message.JSAdjustments.Entries.SctlId._name];
            msg.m_pSourcePocketID = values[CmFinoFIX.message.JSAdjustments.Entries.SourcePocketID._name];
            msg.m_pDestPocketID = values[CmFinoFIX.message.JSAdjustments.Entries.DestPocketID._name];
            msg.m_pAmount = values[CmFinoFIX.message.JSAdjustments.Entries.Amount._name];
            msg.m_pAdjustmentType = values[CmFinoFIX.message.JSAdjustments.Entries.AdjustmentType._name];
            msg.m_pDescription = values[CmFinoFIX.message.JSAdjustments.Entries.Description._name];
            msg.m_paction = "create";
            var params = mFino.util.showResponse.getDisplayParam();
            mFino.util.fix.send(msg, params);
            Ext.apply(params, {
                success :  function(response){
                	if(response.m_psuccess) {
                		Ext.ux.Toast.msg(_("Success"), _("Adjustment request for the transaction is added"));
                		this.scope.close();
                		this.scope.fireEvent("adjustmentFormSubmit");
                	} else {
                		Ext.ux.Toast.msg(_('Failure'),_(response.m_pErrorDescription));
                	}                	
                },
                scope: this
            });
        }, this);
    },
    
    resetWindow : function() {
    	//reset searchForm
    	this.searchForm.resetHandler();
    	//reset chargeTxnGrid only if view is ready
    	if(this.chargeTxnGrid.viewReady) {
    		this.chargeTxnGrid.store.clearData();
        	this.chargeTxnGrid.view.refresh();
    	}
    	//reset ledgerGrid only if view is ready
    	if(this.ledgerGrid.viewReady) {
    		this.ledgerGrid.store.clearData();
            this.ledgerGrid.view.refresh();
    	} 
    	
    	this.adjustmentForm.resetHandler();
    },
    
    setMode : function(mode) {
    	this.mode = mode;
    },
    
    close : function(){
    	this.hide();
    },
    setStore : function(store){
        this.store = store;
    },
    setTitle : function(title){
    this.title = title;
    }
});