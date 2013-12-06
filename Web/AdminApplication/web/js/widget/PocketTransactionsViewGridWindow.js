/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketTransactionsViewGridWindow = function (config){
    var localConfig = Ext.apply({}, config);
    this.searchform = new mFino.widget.PocketTransactionsSearchForm(Ext.apply({
    	autoHeight: true,
         width: 800
    }, config));
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        resizable : false,
        width: 810,
        height:550,
        closable:true,
        plain:true
    });
    mFino.widget.PocketTransactionsViewGridWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTransactionsViewGridWindow, Ext.Window, {
    initComponent : function(){
       
       this.items = [{
//    	    tbar : [
//            '<b class= x-form-tbar>' + _('Pocket Transaction Details') + '</b>',
//            '->'
//       		],
       		layout: "fit",
	            items: [this.searchform,this.grid ]
	        }
	       ];
        mFino.widget.PocketTransactionsViewGridWindow.superclass.initComponent.call(this);
        
        this.grid.on("filedownload", function() {
            var sourceDestnPocketID = this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestnPocketID._name];
            var transferState=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name];
            var isMini=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name];
            var starttime =this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.StartTime._name];
            var endtime=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.EndTime._name];
            var transferstatus=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name];
            var sctlid=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name];
            var queryString;
            queryString = "dType=ledger";
            if(sourceDestnPocketID){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.SourceDestnPocketID._name+"="+sourceDestnPocketID;
            }
            if(transferState){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.TransferState._name+"="+transferState;
            }
            if(isMini){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name+"="+isMini;
            }
            if(starttime){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.StartTime._name+"="+starttime;
            }
            if(endtime){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.EndTime._name+"="+endtime;
            }
            if(transferstatus){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name+"="+transferstatus;
            }
            if(sctlid){
                queryString += "&"+CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name+"="+sctlid;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);
        
        this.searchform.on("search", function(values){
        	this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name] = values.TransactionsTransferStatus;
        	this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.StartTime._name] = values.startDate;
            this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.EndTime._name] = values.endDate; 
            this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name] = values.ServiceChargeTransactionLogID; 
            
            if(values.TransactionsTransferStatus&&values.TransactionsTransferStatus==CmFinoFIX.TransactionsTransferStatus.Pending){
            	this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name] = CmFinoFIX.TransferState.Pending;
            }else{
            	this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name] = CmFinoFIX.TransferState.Complete;	
            }
            this.grid.store.lastOptions = {
                params : {
                    start : 0,
                    limit : CmFinoFIX.PageSize.Default
                }
            };
//            Ext.apply(this.grid.store.lastOptions.params, values);
            this.grid.store.load(this.grid.store.lastOptions);
        });

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