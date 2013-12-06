/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.transaction = function(config){

    var detailsForm = new mFino.widget.TransactionDetails(Ext.apply({
        }, config));

    var searchBox = new mFino.widget.TransactionSearchForm(Ext.apply({
        }, config));

    var transactionDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.TransactionDetails(config),
        mode : "close",
        title : _('Transaction Details'),
        width:1000,
        height:600
    },config));
    var ccDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.CCTransactionDetails(config),
        mode : "close",
        title : _('Credit Card Transaction Details'),
        width:800,
        height:410
    },config));
    var resolveWindow = new mFino.widget.ResolveWindow(config);
    var transactionGrid = new mFino.widget.TransactionsGrid(Ext.apply({
        // title : _('Transaction Search Results'),
    	height: 490
    }, config));
    var bulkResolveGrid = new mFino.widget.BulkResolveGrid(Ext.apply({
        // title : _('Transaction Search Results'),
    	height: 490
    }, config));
    var creditCardGrid = new mFino.widget.CreditCardGrid(Ext.apply({
    	height: 390
    }, config));
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
    creditCardGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                ccDetailsWindow.show();
                ccDetailsWindow.setRecord(record);
                ccDetailsWindow.setStore(grid.store);
            }
        }
    });
    transactionGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                transactionDetailsWindow.show();
                transactionDetailsWindow.setRecord(record);
                transactionDetailsWindow.setStore(grid.store);
            }
        }
    });
    var creditCardSearch = new mFino.widget.CreditCardSearchForm(Ext.apply({
        }, config));
    var creditCardpanel = new Ext.Panel({
        items:[ creditCardSearch ,creditCardGrid ]
    });
    var tabPanel = new Ext.TabPanel({
        activeTab:0,
        items:[
        {
            title: _('Transaction Search Results'),
            layout:'fit',
            items:  [ transactionGrid ]
        },
        {
            title: _('Bulk Resolve'),
            layout:'fit',
            items:  [ bulkResolveGrid ]
        }/*,
        {
            title: _('Credit Card Transactions'),
            layout:'fit',
            itemId:'transaction.page.ccgrid',
            items:  [ creditCardpanel ]
        }*/
        ]
    });

//    if(!mFino.auth.isEnabledItem('transaction.page.ccgrid')){
//        var tabName = tabPanel.get(2).itemId;
//        var tab = tabPanel.getComponent(tabName);
//        tabPanel.remove(tab);
//    }

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            height : 181,
            autoScroll : true,
            tbar : [
            //            {
            //                cls:'x-form-tbar',
            //                html: _('Transaction Details')
            //            },
            '<b class= x-form-tbar>' + _('Transaction Details') + '</b>',
            '->',
            {
                iconCls : "mfino-button-resolve",
                tooltip : _('Resolve Transaction'),
                text : _('Resolve'),
                id : 'resolve',
                itemId: 'tran.resolve',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Transaction selected!"));
                    }else{
                        resolveWindow.show();
                        resolveWindow.setRecord(detailsForm.record);
                    }
                }
            }
            ],
            items: [ detailsForm]
        },
        {
            layout : "fit",
            anchor : "100%, -181",
            items: [ tabPanel ]
        }
        ]
    });

    var mainItem = panelCenter.items.get(0);
    bulkResolveGrid.on('render',function(){
        var topbar= bulkResolveGrid.getTopToolbar();
        if(!mFino.auth.isEnabledItem('transactions.bulkresolve')){
            topbar.find('itemId','transactions.bulkresolve')[0].hide();
        }
    });
    mainItem.on('render', function(){
        var tb = mainItem.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
        //        for(i = 0; i < itemIDs.length; i++){
        //            var itemID = itemIDs[i];
        //            if(!mFino.auth.isEnabledItem(itemID)){
        //                var item = tb.find('itemId', itemID)[0];
        //                tb.remove(item);
        //            }
        //        }

      if(!mFino.auth.isEnabledItem('tran.resolve')){
            var item = tb.find('itemId', 'tran.resolve')[0];
            tb.remove(item);
        }
    });
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
    creditCardSearch.on("creditCardSearch",function(values) {
    	creditCardGrid.store.baseParams = values;
    	creditCardGrid.store.baseParams[CmFinoFIX.message.JSCreditCardTransaction.StartDateSearch._name] = Ext.getCmp('ccdaterange').startDateField.getValue();
    	creditCardGrid.store.baseParams[CmFinoFIX.message.JSCreditCardTransaction.EndDateSearch._name] = Ext.getCmp('ccdaterange').endDateField.getValue();
    	creditCardGrid.store.baseParams[CmFinoFIX.message.JSCreditCardTransaction.LastUpdateStartTime._name] = Ext.getCmp('cclastupdatedaterange').startDateField.getValue();
    	creditCardGrid.store.baseParams[CmFinoFIX.message.JSCreditCardTransaction.LastUpdateEndTime._name] = Ext.getCmp('cclastupdatedaterange').endDateField.getValue();
    	
    	creditCardGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(creditCardGrid.store.lastOptions.params, values);
        creditCardGrid.store.load(creditCardGrid.store.lastOptions);
    });
    searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.TransactionsTransferStatus === "undefined"){
            values.TransactionsTransferStatus =null;
        }
        if(values[CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name] === "undefined"){
            values.SourceApplicationSearch = null;
        } 
        if(values.TransactionUICategory === "undefined"){
            values.TransactionUICategory =null;
        }
        if(values.CreateTimeSearch){
            var date = new Date(values.CreateTimeSearch);
            values.CreateTimeSearch = date.format("Ymd-H:i:s.u");
        }
       /* if(values.dateRange === "<select dates...>" || values.dateRange === "") {
            values.startDate = null;
            values.endDate = null;
        }*/
        transactionGrid.store.baseParams = values;
        transactionGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.StartTime._name] = values.startDate;
        transactionGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.EndTime._name] = values.endDate;
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
    creditCardGrid.on("creditCardSearch", function() {
        creditCardSearch.searchHandler();

    });

    bulkResolveGrid.on("bulkResolve",function(){
        BulkResolve = new mFino.widget.FormWindowLOP(Ext.apply({
            form : new mFino.widget.BulkResolve(config),
            title : _('Bulk Resolve'),
            height : 280,
            width:400,
            mode:"bulk"
        },config));
        //BulkResolveForm.form.getForm().reset();
        // Ext.get('form-file-bulkresolve').dom.value =null;
        BulkResolve.show();
    });
    transactionGrid.on("download", function() {
        var queryString;
        var values = searchBox.getForm().getValues();
        var idSearch = values[CmFinoFIX.message.JSCommodityTransfer.IDSearch._name];
        var transactionsTransferStatus = values[CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name];
        var transferState =  values[CmFinoFIX.message.JSCommodityTransfer.TransferState._name];
        var startDateSearch = Ext.getCmp('tradaterange').startDateField.getValue();
        var endDateSearch = Ext.getCmp('tradaterange').endDateField.getValue();
        var accessMethod =  values[CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name];
        var sourceMdn = values[CmFinoFIX.message.JSCommodityTransfer.SourceMDN._name];
        var destMdn = values[CmFinoFIX.message.JSCommodityTransfer.DestMDN._name];
        var sourceRefId = values[CmFinoFIX.message.JSCommodityTransfer.SourceReferenceID._name];
        var destRefId = values[CmFinoFIX.message.JSCommodityTransfer.OperatorAuthorizationCode._name];
        var msgtype = values[CmFinoFIX.message.JSCommodityTransfer.TransactionUICategory._name];
        var sourcedestmdn = values[CmFinoFIX.message.JSCommodityTransfer.SourceDestnMDN._name];
        var createTime = values[CmFinoFIX.message.JSCommodityTransfer.CreateTimeSearch._name];
        //alert(accessMethod);
        if(transferState === (CmFinoFIX.TransferState.Pending + "")){
            queryString = "dType=pendingcommoditytransfer";
        }else{
            queryString = "dType=commoditytransfer";
        }
        if(idSearch){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.IDSearch._name+"="+idSearch;
        }
        if(transactionsTransferStatus!== null && !(transactionsTransferStatus === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name+"="+transactionsTransferStatus;
        }
        if(startDateSearch){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.StartTime._name+"="+getUTCdate(startDateSearch);
        }
        if(endDateSearch){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.EndTime._name+"="+getUTCdate(endDateSearch);
        }
        if(accessMethod!== null && !(accessMethod === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name+"="+accessMethod;
        }
        if(sourceMdn){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceMDN._name+"="+sourceMdn;
        }
        if(destMdn){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.DestMDN._name+"="+destMdn;
        }
        if(sourceRefId){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceReferenceID._name+"="+sourceRefId;
        }
        if(destRefId){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.OperatorAuthorizationCode._name+"="+destRefId;
        }
        if(msgtype!== null && !(msgtype === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.TransactionUICategory._name+"="+msgtype;
        }
        if(sourcedestmdn){
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceDestnMDN._name+"="+sourcedestmdn;
        }
        if(createTime){
            var creationTime = new Date(createTime);
            createTime = creationTime.format("Ymd-H:i:s.u");
            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.CreateTimeSearch._name+"="+createTime;
        }
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });

    creditCardGrid.on("downloadCC", function() {
        var queryString = "dType=creditCard";
        var values = creditCardSearch.getForm().getValues();
        var idSearch = values[CmFinoFIX.message.JSCreditCardTransaction.IDSearch._name];
        var authIdSearch = values[CmFinoFIX.message.JSCreditCardTransaction.AuthIdSearch._name];
        var transactionIdSearch =  values[CmFinoFIX.message.JSCreditCardTransaction.TransactionIdSearch._name];
        var startDateSearch = Ext.getCmp('ccdaterange').startDateField.getValue();
        var endDateSearch = Ext.getCmp('ccdaterange').endDateField.getValue();
        var lastUpdateStartDateSearch = Ext.getCmp('cclastupdatedaterange').startDateField.getValue();
        var lastUpdateEndDateSearch = Ext.getCmp('cclastupdatedaterange').endDateField.getValue();
        var bankReferenceNumberSearch = values[CmFinoFIX.message.JSCreditCardTransaction.BankReferenceNumberSearch._name];
        var destMDNSearch = values[CmFinoFIX.message.JSCreditCardTransaction.DestMDNSearch._name];
        var operationSearch = values[CmFinoFIX.message.JSCreditCardTransaction.OperationSearch._name];
        var transStatusSearch = values[CmFinoFIX.message.JSCreditCardTransaction.TransStatusSearch._name];
        
        if(idSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.IDSearch._name+"="+idSearch;
        }
        if(authIdSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.AuthIdSearch._name+"="+authIdSearch;
        }
        if(startDateSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.StartDateSearch._name+"="+getUTCdate(startDateSearch);
        }
        if(endDateSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.EndDateSearch._name+"="+getUTCdate(endDateSearch);
        }
        if(transactionIdSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.TransactionIdSearch._name+"="+transactionIdSearch;
        }
        if(bankReferenceNumberSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.BankReferenceNumberSearch._name+"="+bankReferenceNumberSearch;
        }
        if(destMDNSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.DestMDNSearch._name+"="+destMDNSearch;
        }
        if(operationSearch && !(operationSearch === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.OperationSearch._name+"="+operationSearch;
        }
        if(transStatusSearch && !(transStatusSearch === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.TransStatusSearch._name+"="+transStatusSearch;
        }
        if(lastUpdateStartDateSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.LastUpdateStartTime._name+"="+getUTCdate(lastUpdateStartDateSearch);
        }
        if(lastUpdateEndDateSearch){
            queryString += "&"+CmFinoFIX.message.JSCreditCardTransaction.LastUpdateEndTime._name+"="+getUTCdate(lastUpdateEndDateSearch);
        }
        
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });

    transactionGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(transactionGrid.store);
        if(Ext.getCmp('resolve') && (record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.OperatorActionRequired._name] ||
            record.data[CmFinoFIX.message.JSCommodityTransfer.Entries.BankReversalRequired._name])){
            Ext.getCmp('resolve').show();
        }
        else if(Ext.getCmp('resolve')){
            Ext.getCmp('resolve').hide();
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
