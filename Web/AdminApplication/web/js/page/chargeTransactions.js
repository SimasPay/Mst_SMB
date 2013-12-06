/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.chargeTransactions = function(config){

    var detailsForm = new mFino.widget.ChargeTransactionDetails(Ext.apply({
        }, config));

    var searchBox = new mFino.widget.ChargeTransactionSearchForm(Ext.apply({
        }, config));
    var resolveWindow = new mFino.widget.ResolveWindow(config);
    var notificationLogWindow = new mFino.widget.NotificationLogWindow(Ext.apply({
    	grid : new mFino.widget.NotificationLogGrid(config),
        title :_( 'Notification Log')
    },config));
    
    var transactionDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.ChargeTransactionDetails(config),
        mode : "close",
        title : _('Transaction Details'),
        width:400,
        height:400
    },config));
    
    
    var bulkResolveGrid = new mFino.widget.BulkResolveGrid(Ext.apply({
    	height: 490
    }, config));
    var transactionGrid = new mFino.widget.ChargeTransactionsGrid(Ext.apply({
    	height: 480
    }, config));
    
    var reverseTransactionForm = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.ReverseTransactionForm(config),
        height : 500,
        width: 500,
        mode:"reverseTransaction"
    },config));
    
    var reverseTransactionStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSReverseTransaction);
    
    var approveWindow = new mFino.widget.ApproveRejectReverseTransactionWindow(config);
   
    var transactionsView = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
        title : " Transactions  ",
        grid : new mFino.widget.TransactionsGrid(config),
        height : 466,
        width: 800
    },config));
    
    var chargeDistributionview = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
        title : " Charge Distribution Details  ",
        grid : new mFino.widget.ChargeDistributionViewGrid(config),
        height : 466,
        width: 800
    },config));
    
    transactionGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
               chargeDistributionview.grid.store.lastOptions = {
                    params : {
                        start : 0,
                        limit : CmFinoFIX.PageSize.Default
                    }
                };
                chargeDistributionview.grid.store.baseParams[CmFinoFIX.message.JSTransactionAmountDistributionLog.ServiceChargeTransactionLogID._name] = record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
                chargeDistributionview.grid.store.load(chargeDistributionview.grid.store.lastOptions);
                chargeDistributionview.setStore(chargeDistributionview.grid.store);
                chargeDistributionview.show();
            }else if(action === 'mfino-button-history'){               
                transactionsView.grid.store.lastOptions = {
                    params : {
                        start : 0,
                        limit : CmFinoFIX.PageSize.Default
                    }
                };
                transactionsView.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name] = record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
                transactionsView.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.JSMsgType._name] = CmFinoFIX.MsgType.JSChargeTransactions;
                transactionsView.grid.store.load(transactionsView.grid.store.lastOptions);
                transactionsView.setStore(transactionsView.grid.store);
                transactionsView.setTitle("Transactions related to Reference Id " + record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name));
                transactionsView.show();
            }
        }
    });
    
    var transferDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.TransactionDetails(config),
        mode : "close",
        title : _('Transaction Details'),
        width:1000,
        height:600
    },config));
    transactionsView.grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
            	transferDetailsWindow.show();
            	transferDetailsWindow.setRecord(record);
            	transferDetailsWindow.setStore(grid.store);
            }
            else if(action === 'mfino-button-history'){
        		  if(record){
        			  mFino.widget.ShowLedgersWindow({dataUrl:config.dataUrl,CommodityTransferID:record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.ID._name)});
        		  }
            }
        }
    });
    
    
    bulkResolveGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                var PendingResolveEntryView = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
                    title : "Details for "+ record.get(CmFinoFIX.message.JSPendingTransactionsFile.Entries.ID._name),
                    grid : new mFino.widget.BulkUploadPendingFileViewGrid(config),
                    height : 466,
                    width: 800
                },config));
                PendingResolveEntryView.grid.store.lastOptions = {
                    params : {
                        start : 0,
                        limit : CmFinoFIX.PageSize.Default
                    }
                };
                PendingResolveEntryView.grid.store.baseParams[CmFinoFIX.message.JSPendingTransactionsEntry.PendingTransactionsFileID._name] = record.get(CmFinoFIX.message.JSPendingTransactionsFile.Entries.ID._name);
                PendingResolveEntryView.grid.store.load(PendingResolveEntryView.grid.store.lastOptions);
                PendingResolveEntryView.setStore(PendingResolveEntryView.grid.store);
                PendingResolveEntryView.show();
            }
        }
    });
    
    var tabPanel = new Ext.TabPanel({
        activeTab:0,
        items:[
        {
            title: _('Transaction Search Results'),
            layout:'fit',
            items:  [ transactionGrid ]
        }/*,
        {
            title: _('Bulk Resolve'),
            layout:'fit',
            items:  [ bulkResolveGrid ]
        }*/
        ]
    });


    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            height : 205,
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('Transaction Details') + '</b>',
            '->',
//            {
//                iconCls: 'mfino-button-refresh',
//                tooltip : _('Refresh Transaction'),
//                handler : function(){
//                    if(detailsForm.record)
//                    {
//                    	transactionGrid.store.lastOptions.params[CmFinoFIX.message.JSServiceChargeTransactions.IDSearch._name] = 
//                    		detailsForm.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
//                    	transactionGrid.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
//                    	transactionGrid.store.load(transactionGrid.store.lastOptions);
//                    }
//                    else
//                    {
//                        Ext.ux.Toast.msg(_('Error'), _("No Transaction is Selected"));
//                    }
//                }
//            },
            {
                iconCls: 'mfino-button-reverse',
                itemId: 'chargeTransaction.reverse',
                id: 'chargeTransaction.reverse',
                tooltip : _('Reverse Transaction'),
                handler : function(){
                    if(detailsForm.record) {
                    	Ext.getCmp('chargeTransaction.reverse').hide();
                    	reverseTransactionStore.baseParams[CmFinoFIX.message.JSReverseTransaction.ServiceChargeTransactionLogID._name] = 
                    		detailsForm.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
                    		reverseTransactionStore.load();
                    }
                    else {
                        Ext.ux.Toast.msg(_('Error'), _("No Transaction is Selected"));
                    }
                }
            },
            {
                iconCls: 'mfino-button-key',
                itemId: 'chargeTransaction.resendAccessCode',
                id: 'chargeTransaction.resendAccessCode',
                tooltip : _('Resend Access Code'), 
                handler : function(){
                    if(detailsForm.record) {
                    	Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Resend Access Code?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }else{
                                	var msg = new CmFinoFIX.message.JSResendAccessCode();
                                	msg.m_pSctlId = detailsForm.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
                                	msg.m_pSenderMDN = detailsForm.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.SourceMDN._name);
                                	var params = mFino.util.showResponse.getDisplayParam();
                                    mFino.util.fix.send(msg, params);
                                }
                            }, this);
                    	
                    }
                    else {
                        Ext.ux.Toast.msg(_('Error'), _("No Transaction is Selected"));
                    }
                }
            },
            {
                iconCls : "mfino-button-resolve",
                tooltip : _('Approve/Reject Reverse Transaction Request'),
                itemId: 'chargeTransaction.approve',
                id: 'chargeTransaction.approve',
                handler : function(){
                    if(detailsForm.record) {
                    	Ext.getCmp('chargeTransaction.approve').hide();
                        approveWindow.show();
                        approveWindow.setRecord(detailsForm.record);
                    }
                    else {
                    	Ext.MessageBox.alert(_("Alert"), _("No Transaction is selected"));
                    }
              	}
            },
            {
                iconCls : "mfino-button-resolve",
                tooltip : _('Resolve Transaction'),
                text : _('Resolve'),
                id : 'tran.resolve',
                itemId: 'tran.resolve',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Transaction selected!"));
                    }else{
                        resolveWindow.show();
                        resolveWindow.setRecord(detailsForm.record);
                    }
                }
            },
            {
                iconCls : "mfino-button-history",
                tooltip : _('View NotificationLog'),
                //text : _('NotificationLog'),
                id : 'viewNotificationLog',
                itemId: 'notificationLog.view',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Transaction selected!"));
                    }else{
                    	notificationLogWindow.grid.store.lastOptions = {
        	                    params : {
        	                        start : 0,
        	                        limit : CmFinoFIX.PageSize.Default
        	                    }
        	          };
                    	
                    	notificationLogWindow.grid.store.baseParams[CmFinoFIX.message.JSNotificationLog.SctlId._name] = detailsForm.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
                    	notificationLogWindow.grid.store.load();
            	        notificationLogWindow.show();
                    }
                }
            }
            ],
            items: [ detailsForm]
        },
        {
            layout : "fit",
            anchor : "100%, -200",
            items: [ tabPanel ]
        }
        ]
    });
    
   /* var mainItem = panelCenter.items.get(0);
    bulkResolveGrid.on('render',function(){
        var topbar= bulkResolveGrid.getTopToolbar();
        if(!mFino.auth.isEnabledItem('transactions.bulkresolve')){
            topbar.find('itemId','transactions.bulkresolve')[0].hide();
        }
    });*/
 /*   var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){       
        var tb = mainItem.getTopToolbar();
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
    });*/
    bulkResolveGrid.on("bulkResolveExcelDownload",function(values){
        var queryString;
        queryString = "dType=pendingtransactionsfile";
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });
    bulkResolveGrid.on("bulkSearch",function(values){
    	bulkResolveGrid.store.lastOptions = {
                params : {
                    start : 0,
                    limit : CmFinoFIX.PageSize.Default
                }
    	};
    	bulkResolveGrid.store.load(bulkResolveGrid.store.lastOptions);
    });
    bulkResolveGrid.on("bulkResolve",function(){
        BulkResolve = new mFino.widget.FormWindowLOP(Ext.apply({
            form : new mFino.widget.BulkResolve(config),
            title : _('Bulk Resolve'),
            height : 280,
            width:400,
            mode:"bulk"
        },config));
        BulkResolve.show();
    });
    reverseTransactionStore.on("load", function(){
        var record = reverseTransactionStore.getAt(0);
        if (record != null) {
        	reverseTransactionStore.remove(record);
        	reverseTransactionForm.setTitle(_("Reverse Transaction of Reference ID ") + record.get(CmFinoFIX.message.JSReverseTransaction.Entries.ParentSCTLID._name));
        	
        	reverseTransactionForm.setStore(reverseTransactionStore);
        	reverseTransactionForm.setRecord(record);
        	reverseTransactionForm.enable();

			reverseTransactionForm.show();
        }
    });
    searchBox.on('render',function(){
    	 var tb = searchBox.items.get('items');
    	 var itemIDs = [];
         for(var i = 0; i <  tb.items.length; i++){
      	   if(tb.items.get(i).itemId){
             itemIDs.push(tb.items.get(i).itemId);
      	   }
         }	  
         for(i = 0; i < itemIDs.length; i++){
             var itemID = itemIDs[i];
             if(!mFino.auth.isEnabledItem(itemID)){
                 var item = tb.items.get(itemID);
                 tb.remove(item);
             }
         }
   });
   searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        transactionGrid.store.baseParams = values;
        transactionGrid.store.baseParams[CmFinoFIX.message.JSServiceChargeTransactions.StartDateSearch._name] = values.startDate;
        transactionGrid.store.baseParams[CmFinoFIX.message.JSServiceChargeTransactions.EndDateSearch._name] = values.endDate;
        transactionGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(transactionGrid.store.lastOptions.params, values);
        transactionGrid.store.load(transactionGrid.store.lastOptions);
    });
    
    transactionGrid.on("defaultSearch", function() {
        searchBox.searchHandler();
    });
   
    transactionGrid.on("download", function() {
        var queryString;
        var values = searchBox.getForm().getValues();
        var idSearch = values[CmFinoFIX.message.JSServiceChargeTransactions.IDSearch._name];
        var accessMethod =  values[CmFinoFIX.message.JSServiceChargeTransactions.SourceApplicationSearch._name];
        var transactionID = values[CmFinoFIX.message.JSServiceChargeTransactions.TransactionIdSearch._name];
        var refID = values[CmFinoFIX.message.JSServiceChargeTransactions.TransferID._name];
        var sourceMdn = values[CmFinoFIX.message.JSServiceChargeTransactions.SourceMDN._name];
        var destMdn = values[CmFinoFIX.message.JSServiceChargeTransactions.DestMDN._name];
        var sourcePartnerCode = values[CmFinoFIX.message.JSServiceChargeTransactions.SourcePartnerCode._name];
        var destPartnerCode = values[CmFinoFIX.message.JSServiceChargeTransactions.DestPartnerCode._name];
        var status =  values[CmFinoFIX.message.JSServiceChargeTransactions.Status._name];
        var startDateSearch = Ext.getCmp('sctldaterange').startDateField.getValue();
        var endDateSearch = Ext.getCmp('sctldaterange').endDateField.getValue();
        var rrn = values[CmFinoFIX.message.JSServiceChargeTransactions.BankRetrievalReferenceNumber._name];
        var transactionType = values[CmFinoFIX.message.JSServiceChargeTransactions.TransactionTypeID._name];
        var billerCode = values[CmFinoFIX.message.JSServiceChargeTransactions.MFSBillerCode._name];
        
        queryString = "dType=sctlLogs";       
        if(idSearch){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.IDSearch._name+"="+idSearch;
        }
        if(status && !(status === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.Status._name+"="+status;
        }
        if(startDateSearch){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.StartDateSearch._name+"="+getUTCdate(startDateSearch);
        }
        if(endDateSearch){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.EndDateSearch._name+"="+getUTCdate(endDateSearch);
        }
        if(accessMethod && !(accessMethod === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.SourceApplicationSearch._name+"="+accessMethod;
        }
        if(sourceMdn){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.SourceMDN._name+"="+sourceMdn;
        }
        if(destMdn){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.DestMDN._name+"="+destMdn;
        }
        if(sourcePartnerCode){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.SourcePartnerCode._name+"="+sourcePartnerCode;
        }
        if(destPartnerCode){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.DestPartnerCode._name+"="+destPartnerCode;
        }
        if(transactionID){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.TransactionIdSearch._name+"="+sourceRefId;
        }
        if(refID){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.TransferID._name+"="+destRefId;
        }
        if(rrn){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.BankRetrievalReferenceNumber._name+"="+rrn;
        }
        if(transactionType){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.TransactionTypeID._name+"="+transactionType;
        }
        if(billerCode){
            queryString += "&"+CmFinoFIX.message.JSServiceChargeTransactions.MFSBillerCode._name+"="+billerCode;
        }
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });

   
    transactionGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(transactionGrid.store);
//        if(		((Ext.getCmp('chargeTransaction.reverse') && (mFino.auth.isEnabledItem('chargeTransaction.reverse'))) &&
//        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.ParentSCTLID._name] === null) &&
//        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name] === 'Transfer' ||
//       			 record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name] === 'Sub Bulk Transfer' ||
//       			 record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name] === 'TransferToUnRegistered') &&
//        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionAmount._name] > 0) && 
//				((record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Confirmed) ||
//        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Distribution_Started) ||
//        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Distribution_Completed) ||
//        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Distribution_Failed) 
//				)) && 
//				(
//        		  ((record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.AmtRevStatus._name] === null) ||
//        		  (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.AmtRevStatus._name] === "") ||
//        		  (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.AmtRevStatus._name] === CmFinoFIX.SCTLStatus.Reverse_Failed))  
//				  ||
//			     (
//				  (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.CalculatedCharge._name] > 0) &&
//        		  ((record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.ChrgRevStatus._name] === null) ||
//        		  (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.ChrgRevStatus._name] === "") ||
//        		  (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.ChrgRevStatus._name] === CmFinoFIX.SCTLStatus.Reverse_Failed))
//				 )
//				) 
//			) {
        if ((Ext.getCmp('chargeTransaction.reverse') && (mFino.auth.isEnabledItem('chargeTransaction.reverse'))) && 
        	(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.IsReverseAllowed._name]  === true)) {
        	
            Ext.getCmp('chargeTransaction.reverse').show();
        }
        else if(Ext.getCmp('chargeTransaction.reverse')) {
            Ext.getCmp('chargeTransaction.reverse').hide();
        }
        
        if( (Ext.getCmp('chargeTransaction.resendAccessCode') && (mFino.auth.isEnabledItem('chargeTransaction.resendAccessCode')) ) && 
            	( (( (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name] === 'TransferToUnRegistered') ||
            			(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name] === 'Fund Allocation') )&&  
            			((record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Confirmed) ||
                    	 (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Distribution_Started) ||
                    	 (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Distribution_Completed) ||
                    	 (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Distribution_Failed))
                   ) ||
                   ((record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name] === 'Cash Out At ATM') &&  
                     	(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Processing))
                 )
       			) {

            Ext.getCmp('chargeTransaction.resendAccessCode').show();
        }
        else if(Ext.getCmp('chargeTransaction.resendAccessCode')) {
            Ext.getCmp('chargeTransaction.resendAccessCode').hide();
        }
        
        if (Ext.getCmp('chargeTransaction.approve') && (mFino.auth.isEnabledItem('chargeTransaction.approve')) &&
        		(record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Reverse_Initiated)) {
        	Ext.getCmp('chargeTransaction.approve').show();
        } 
        else if (Ext.getCmp('chargeTransaction.approve')) {
        	Ext.getCmp('chargeTransaction.approve').hide();
        }
        if (Ext.getCmp('tran.resolve')) {
				if (mFino.auth.isEnabledItem('tran.resolve')
							&& (record.data[CmFinoFIX.message.JSServiceChargeTransactions.Entries.Status._name] === CmFinoFIX.SCTLStatus.Pending)) {
						Ext.getCmp('tran.resolve').show();
				 } else {
						Ext.getCmp('tran.resolve').hide();
						}
		}
        
    });
    
    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 260,
            layout : "fit",
            items:[ searchBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        }
        ]
    });
    return panel;
};
