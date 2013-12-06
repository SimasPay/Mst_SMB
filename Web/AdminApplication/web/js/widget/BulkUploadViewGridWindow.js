/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkUploadViewGridWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        width: 800,
        height:400,
        closable:false,
        plain:true
    });
    mFino.widget.BulkUploadViewGridWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkUploadViewGridWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            itemId: "ok",
            text: _('OK'),
            handler: this.OK.createDelegate(this)
        }
        ];
        this.items = [this.grid];
        mFino.widget.BulkUploadViewGridWindow.superclass.initComponent.call(this);

        this.grid.on("download", function() {
            var idSearch = this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.IDSearch._name];
            var queryString;
            queryString = "dType=bulkUploadEntry";
            if(idSearch){
                queryString += "&"+CmFinoFIX.message.JSBulkUploadEntry.IDSearch._name+"="+idSearch;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);

        this.grid.on("filedownload", function() {
            var idSearch = this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadFileEntry.IDSearch._name];
            var queryString;
            queryString = "dType=bulkUploadFileEntry";
            if(idSearch){
                queryString += "&"+CmFinoFIX.message.JSBulkUploadFileEntry.IDSearch._name+"="+idSearch;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);
        this.grid.on("pendingtransactionsentry", function() {
            var pendingTransactionsFileID = this.grid.store.baseParams[CmFinoFIX.message.JSPendingTransactionsEntry.PendingTransactionsFileID._name];
            var queryString;
            queryString = "dType=pendingtransactionsentry";
            if(pendingTransactionsFileID){
                queryString += "&"+CmFinoFIX.message.JSPendingTransactionsEntry.PendingTransactionsFileID._name+"="+pendingTransactionsFileID;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);
        this.grid.on("chargedistribution", function() {
            var sctlID = this.grid.store.baseParams[CmFinoFIX.message.JSTransactionAmountDistributionLog.ServiceChargeTransactionLogID._name];
            var queryString;
            queryString = "dType=tadlogs";
            if(sctlID){
                queryString += "&"+CmFinoFIX.message.JSTransactionAmountDistributionLog.ServiceChargeTransactionLogID._name+"="+sctlID;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);
        this.grid.on("transactiondownload", function() {
            var sctlID = this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name];
            var queryString;
            queryString = "dType=commoditytransfer";
            if(sctlID){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name+"="+sctlID;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);
        
        
    },
    OK : function(){
        this.hide();
    },
    setStore : function(store){
        this.store = store;
    }   
});