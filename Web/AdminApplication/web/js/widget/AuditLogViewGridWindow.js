/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AuditLogViewGridWindow = function (config){
    var localConfig = Ext.apply({}, config);
    /*this.searchform = new mFino.widget.BulkTransferEntrySearchForm(Ext.apply({
    	autoHeight: true,
    	header: false,
        width: 800
    }, config));*/
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        resizable : false,
        width: 810,
        height:525,
        closable:false,
        plain:true
    });
    mFino.widget.AuditLogViewGridWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AuditLogViewGridWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [{
	             itemId: "ok",
	             text: _('OK'),
	             handler: this.OK.createDelegate(this)
             }
             ];
       this.items = [{
       		layout: "fit",
	            items: [/*this.searchform,*/this.grid ]
	        }
	       ];
        mFino.widget.AuditLogViewGridWindow.superclass.initComponent.call(this);
        
        /*this.searchform.on("search", function(values){
        	this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.TransactionsTransferStatus._name] = values.TransactionsTransferStatus;
            this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.IsUnRegistered._name] = values.IsUnRegistered;           
            this.grid.store.lastOptions = {
                params : {
                    start : 0,
                    limit : CmFinoFIX.PageSize.Default
                }
            };
            this.grid.store.load(this.grid.store.lastOptions);
        });*/
        
    },
    
    OK : function(){
        this.hide();
    },
    
    setStore : function(store){
        this.store = store;
    }
});