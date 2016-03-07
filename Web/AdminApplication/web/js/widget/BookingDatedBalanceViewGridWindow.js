/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BookingDatedBalanceViewGridWindow = function (config){
    var localConfig = Ext.apply({}, config);
    this.searchform = new mFino.widget.PocketTransactionsSearchForm(Ext.apply({
    	autoHeight: true,
        width: 500
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
    mFino.widget.BookingDatedBalanceViewGridWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BookingDatedBalanceViewGridWindow, Ext.Window, {
    initComponent : function(){
    	
    	var that = this;
    	
    	this.detailsForm = new Ext.FormPanel({
    		width: 500,
    		height: 150,
    		frame:true,
            border:true,
    		labelWidth : 130,
    		labelPad : 20,
    		items : [
		              {
		            	  xtype : "displayfield",
		            	  itemId : 'balanceDetails.currentBalance',
		            	  fieldLabel :_("Current Pocket Balance"),
		            	  anchor : '95%',
		            	  name: CmFinoFIX.message.JSSystemParameters.Entries.ParameterName._name
		              },
		              {
		            	  xtype : "displayfield",
		            	  itemId : 'balanceDetails.openingBalance',
		            	  fieldLabel :_("Opening Balance"),
		            	  anchor : '95%',
		            	  name: CmFinoFIX.message.JSSystemParameters.Entries.ParameterValue._name
		              },
		              {
		            	  xtype : "displayfield",
		            	  itemId : 'balanceDetails.closingBalance',
		            	  fieldLabel :_("Closing Balance"),
		            	  anchor : '95%',
		            	  name: CmFinoFIX.message.JSSystemParameters.Entries.Description._name
		              }
    		      ]
    	});
    	
		var panel = new Ext.Panel({
	        layout: "border",
	        border: false,
	        width : 1000,
	        items: [
	        {
	            region: 'north',	            
	            layout : "hbox",
	            height : 100,
	            items:[ this.searchform , this.detailsForm ]
	        },
	        {
	            region: 'center',	            
	            layout : "fit",
	            items: [ this.grid ]
	        }
	        ]
	    });       
      
		this.items = [{
       		layout: "fit",
	            items: [panel]
	        }
	       ];
        mFino.widget.BookingDatedBalanceViewGridWindow.superclass.initComponent.call(this);
        
        this.grid.on("filedownload", function(format) {
//        	alert(format)
            var sourceDestnPocketID = this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestnPocketID._name];
            var transferState=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name];
            var isMini=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name];
            var starttime =this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.StartTime._name];
            var endtime=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.EndTime._name];
            var transferstatus=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name];
            var sctlid=this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name];
            var queryString;
            queryString = "dType=ledger";
            queryString += "&dFormat="+format;
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
        	var sourceDestnPocketID = this.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestnPocketID._name];
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

            this.grid.store.load(this.grid.store.lastOptions);            
            if(that.record && that.record.get(CmFinoFIX.message.JSPocketTemplate.Entries.PocketTypeText._name) == 'SVA') {
            	if(values.startDate == null || values.startDate == "") { //If date range is not selected, show only current pocket balance
                	var message= new CmFinoFIX.message.JSCheckBalanceForSubscriber();
                    message.m_pPocketID = sourceDestnPocketID;
                    var params = mFino.util.showResponse.getDisplayParam();
                    mFino.util.fix.send(message, params);
                    Ext.apply(params, {
                        success :  function(response){
                     	   if(response.m_psuccess){
                     		   that.loadBalanceDetailsForm(response);
                     	   } else {
                     		   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription)); 	   
                     	   }
                        }
                    });
                } else { //If date range is selected, show current, opening and closing balances
                	var message= new CmFinoFIX.message.JSShowBalanceDetails();
                    message.m_pStartTime = values.startDate;
                    message.m_pEndTime = values.endDate;
                    message.m_pPocketID = sourceDestnPocketID;
                    var params = mFino.util.showResponse.getDisplayParam();                            
                    mFino.util.fix.send(message, params);
                    Ext.apply(params, {
                        success :  function(response){
                     	   if(response.m_psuccess){             		   
                     		   that.loadBalanceDetailsForm(response);
                     	   } else {
                 			   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription)); 	   
                     	   }
                        }
                    });
                }
            } else {
            	that.loadBalanceDetailsForm();
            }                        
        });

    },
    
    loadBalanceDetailsForm : function(response) {
    	var currentBalance = this.find('itemId','balanceDetails.currentBalance')[0];
    	var closingBalance = this.find('itemId','balanceDetails.closingBalance')[0];
		var openingBalance = this.find('itemId','balanceDetails.openingBalance')[0];
		if(response) { // in case of SVA pocket
			if(response.m_pEndTime == null) { //when date range is not selected
	    		closingBalance.hide();			
	    		closingBalance.getEl().up('.x-form-item').setDisplayed(false);
	    		openingBalance.hide();			
	    		openingBalance.getEl().up('.x-form-item').setDisplayed(false);
	    		currentBalance.setValue(response.m_pCurrency+" "+Ext.util.Format.number(response.m_pBalance, '0,000'));
	    	} else { //when date range is selected
	    		closingBalance.show();			
	    		closingBalance.getEl().up('.x-form-item').setDisplayed(true);
	    		openingBalance.show();			
	    		openingBalance.getEl().up('.x-form-item').setDisplayed(true);
	    		currentBalance.setValue(response.m_pCurrency+" "+Ext.util.Format.number(response.m_pCurrentBalance, '0,000'));
	    		closingBalance.setValue(response.m_pCurrency+" "+Ext.util.Format.number(response.m_pClosingBalance, '0,000'));
	    		openingBalance.setValue(response.m_pCurrency+" "+Ext.util.Format.number(response.m_pOpeningBalance, '0,000'));
	    	}
		} else {// in case of Bank pocket
			currentBalance.setValue('--');
			closingBalance.hide();			
    		closingBalance.getEl().up('.x-form-item').setDisplayed(false);
    		openingBalance.hide();			
    		openingBalance.getEl().up('.x-form-item').setDisplayed(false);
		}
    },
    
    close : function(){
    	this.hide();
    },
    setStore : function(store){
        this.store = store;
    },
    setTitle : function(title){
    	this.title = title;
    },
    setRecord : function(record) {
    	this.record = record;
    }
});