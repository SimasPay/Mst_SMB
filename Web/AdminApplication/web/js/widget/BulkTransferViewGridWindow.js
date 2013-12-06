/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkTransferViewGridWindow = function (config){
    var localConfig = Ext.apply({}, config);
    this.searchform = new mFino.widget.BulkTransferEntrySearchForm(Ext.apply({
    	autoHeight: true,
    	header: false,
        width: 800
    }, config));
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
    mFino.widget.BulkTransferViewGridWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransferViewGridWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [{
	             itemId: "ok",
	             text: _('OK'),
	             handler: this.OK.createDelegate(this)
             }
             ];
       this.items = [{
       		layout: "fit",
	            items: [this.searchform,this.grid ]
	        }
	       ];
        mFino.widget.BulkTransferViewGridWindow.superclass.initComponent.call(this);
        
        this.grid.on("bulkTransferDownload", function() {
            var bulkUploadId = this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.IDSearch._name];
            var queryString;
            queryString = "dType=bulkTransferDownload";
            if(bulkUploadId){
                queryString += "&"+CmFinoFIX.message.JSBulkUploadEntry.IDSearch._name+"="+bulkUploadId;
            }
            var URL = "download.htm?" + queryString;
            window.open(URL,'mywindow','width=400,height=200');
        }, this);
        
		this.grid.on('render', function(){
		    var tb = this.grid.getBottomToolbar();
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
		}, this);
        
        this.grid.on("verified", function() {
        	var selectedRows = this.grid.getSelectionModel().getSelections();
        	var uploadId;
        	var nonRegisteredIdStr = "";
			if (selectedRows.length === 0) {
				Ext.MessageBox.alert(_("Alert"), _("Please select atleast one Non Registered Transfer entry to send Fund access code"));
				return;
			}
        	for (var i=0; i < selectedRows.length; i++) {
        		var id = selectedRows[i].get(CmFinoFIX.message.JSBulkUploadEntry.Entries.LineNumber._name);
        		if (nonRegisteredIdStr === "") {
        			nonRegisteredIdStr = id;
        		} else {
        			nonRegisteredIdStr = nonRegisteredIdStr+","+id;
        		}
        	}
        	uploadId = selectedRows[0].get(CmFinoFIX.message.JSBulkUploadEntry.Entries.UploadID._name);
        	
        	var msg = new CmFinoFIX.message.JSVerifyNonRegisteredBulkTransfer();
        	msg.m_pBulkUploadID = uploadId;
        	msg.m_pNonRegisteredIdsStr = nonRegisteredIdStr;
            var params = mFino.util.showResponse.getDisplayParam();
            mFino.util.fix.send(msg, params);
        }, this);
        
        this.searchform.on("search", function(values){
        	this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.TransactionsTransferStatus._name] = values.TransactionsTransferStatus;
            this.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.IsUnRegistered._name] = values.IsUnRegistered;           
            this.grid.store.lastOptions = {
                params : {
                    start : 0,
                    limit : CmFinoFIX.PageSize.Default
                }
            };
            this.grid.store.load(this.grid.store.lastOptions);
        });
        
        this.searchform.on("verify", function(){
        	this.grid.store.save();
        });

    },
    
    OK : function(){
        this.hide();
    },
    
    setStore : function(store){
        this.store = store;
    }
});