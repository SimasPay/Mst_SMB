/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.partnertransaction = function(config){

    var detailsForm = new mFino.widget.PartnerTransactionDetails(Ext.apply({
        }, config));

    var searchBox = new mFino.widget.PartnerTransactionSearchForm(Ext.apply({
        }, config));

    var transactionDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.PartnerTransactionDetails(config),
        mode : "close",
        title : _('Partner Transaction Details'),
        width:1000,
        height:600
    },config));
    
   // var resolveWindow = new mFino.widget.ResolveWindow(config);
    var transactionGrid = new mFino.widget.PartnerTransactionsGrid(Ext.apply({
        // title : _('Transaction Search Results'),
    	height: 490,
    	width: 300
    }, config));
    
    transactionGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                transactionDetailsWindow.show();
                transactionDetailsWindow.setRecord(record);
                transactionDetailsWindow.setStore(grid.store);
            }
        }
    });
    
    var tabPanel = new Ext.TabPanel({
        activeTab:0,
        items:[
        {
            title: _('PartnerTransaction Search Results'),
            layout:'fit',
            items:  [ transactionGrid ]
        }
       ]
    });


    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            height : 180,
            autoScroll : true,
            tbar : [
            //            {
            //                cls:'x-form-tbar',
            //                html: _('Transaction Details')
            //            },
            '<b class= x-form-tbar>' + _('PartnerTransaction Details') + '</b>',
            '->'
            ],
            items: [ detailsForm]
        },
        {
            layout : "fit",
            anchor : "100%",
            items: [ tabPanel ]
        }
        ]
    });

    var mainItem = panelCenter.items.get(0);
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

//      if(!mFino.auth.isEnabledItem('tran.resolve')){
//            var item = tb.find('itemId', 'tran.resolve')[0];
//            tb.remove(item);
//        }
  });
    searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.TransactionsTransferStatus === "undefined"){
            values.TransactionsTransferStatus =null;
        }
        if(values[CmFinoFIX.message.JSTransactionAmountDistributionLog.TradeNameSearch._name] === "undefined"){
            values.TradeNameSearch = null;
        } 
        if(values.ServiceChargeTransactionLogID === "undefined"){
            values.ServiceChargeTransactionLogID =null;
        }
//        if(values.CreateTimeSearch){
//            var date = new Date(values.CreateTimeSearch);
//            values.CreateTimeSearch = date.format("Ymd-H:i:s.u");
//        }
       /* if(values.dateRange === "<select dates...>" || values.dateRange === "") {
            values.startDate = null;
            values.endDate = null;
        }*/
        transactionGrid.store.baseParams = values;
        transactionGrid.store.baseParams[CmFinoFIX.message.JSTransactionAmountDistributionLog.StartTime._name] = values.startDate;
        transactionGrid.store.baseParams[CmFinoFIX.message.JSTransactionAmountDistributionLog.EndTime._name] = values.endDate;
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
    
       
//    transactionGrid.on("download", function() {
//        var queryString;
//        var values = searchBox.getForm().getValues();
//        var idSearch = values[CmFinoFIX.message.JSCommodityTransfer.IDSearch._name];
//        var transactionsTransferStatus = values[CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name];
//        var transferState =  values[CmFinoFIX.message.JSCommodityTransfer.TransferState._name];
//        var startDateSearch = Ext.getCmp('tradaterange').startDateField.getValue();
//        var endDateSearch = Ext.getCmp('tradaterange').endDateField.getValue();
//       if(transferState === (CmFinoFIX.TransferState.Pending + "")){
//            queryString = "dType=pendingcommoditytransfer";
//        }else{
//            queryString = "dType=commoditytransfer";
//        }
//        if(idSearch){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.IDSearch._name+"="+idSearch;
//        }
//        if(transactionsTransferStatus!== null && !(transactionsTransferStatus === "undefined")){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name+"="+transactionsTransferStatus;
//        }
//        if(startDateSearch){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.StartTime._name+"="+getUTCdate(startDateSearch);
//        }
//        if(endDateSearch){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.EndTime._name+"="+getUTCdate(endDateSearch);
//        }
//        if(accessMethod!== null && !(accessMethod === "undefined")){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name+"="+accessMethod;
//        }
//        if(sourceMdn){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceMDN._name+"="+sourceMdn;
//        }
//        if(destMdn){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.DestMDN._name+"="+destMdn;
//        }
//        if(sourceRefId){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceReferenceID._name+"="+sourceRefId;
//        }
//        if(destRefId){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.OperatorAuthorizationCode._name+"="+destRefId;
//        }
//        if(msgtype!== null && !(msgtype === "undefined")){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.TransactionUICategory._name+"="+msgtype;
//        }
//        if(mdnId){
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SubscriberMDNID._name+"="+mdnId;
//        }
//        if(createTime){
//            var creationTime = new Date(createTime);
//            createTime = creationTime.format("Ymd-H:i:s.u");
//            queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.CreateTimeSearch._name+"="+createTime;
//        }
//        var URL = "download.htm?" + queryString;
//        window.open(URL,'mywindow','width=400,height=200');
//    });

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
