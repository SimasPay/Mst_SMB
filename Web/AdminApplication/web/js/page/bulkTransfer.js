/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.bulkTransfer = function(config){
    var detailsForm = new mFino.widget.BulkTransferDetails(Ext.apply({
    		height : 193
        }, config));

    var uploadGrid = new mFino.widget.BulkTransferGrid(Ext.apply({
        layout:'fit',
        title : _('Bulk Transfer Search Results'),
        frame:true,
        loadMask:true,
        height: 485,
        width: 925
    }, config));

    var bulkUploadSearchPanel = new mFino.widget.BulkTransferSearchForm(Ext.apply({
		height : 190
    }, config));
    
    var uploadFile = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.UploadTransferFile(config),
        title : _("Upload Transfer File"),
        height :350,
        width:450,
        mode:"bulkTransfer"
    },config));
    
    var approveWindow = new mFino.widget.ApproveRejectBulkTranferWindow(config);
    
    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            autoScroll : true,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Bulk Transfer Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-pocket-add',
                tooltip : _('Upload Bulk Transfer File'),
                text : _('Upload'),
                itemId: 'bulktransfer.upload',
                handler: function(){
                    uploadFile.show();
                    uploadFile.form.getForm().reset();
                    Ext.get('transfer.form.file-file').dom.value ='';
                }
            },
            {
                iconCls: 'mfino-button-remove',
                tooltip : _('Cancel Bulk Transfer'),
                text : _('Cancel'),
                itemId: 'bulktransfer.cancel',
                id: 'bulktransfer.cancel',
                handler: function(){
                	var id = detailsForm.record.get(CmFinoFIX.message.JSBulkUpload.Entries.ID._name);
                    Ext.Msg.confirm(_("Confirm?"), _("Do you want to cancel the Bulk Transfer " + id),
                        function(btn){
                            if(btn !== "yes"){
                                return;
                            }
                            Ext.getCmp('bulktransfer.cancel').hide();
                            var msg = new CmFinoFIX.message.JSCancelBulkTranfer();
                            msg.m_pBulkUploadID = id;
                            var params = mFino.util.showResponse.getDisplayParam();
                            params.store = detailsForm.store;
                            params.store.lastOptions.params[CmFinoFIX.message.JSBulkUpload.IDSearch._name] = id;
                            mFino.util.fix.send(msg, params);
                        }, this);
                }
            },            
            {
                iconCls : "mfino-button-resolve",
                tooltip : _('Approve/Reject Bulk Transfer'),
                text : _('Approve'),
                itemId: 'bulktransfer.approve',
                id: 'bulktransfer.approve',
                handler : function(){
                    if(detailsForm.record) {
                    	Ext.getCmp('bulktransfer.approve').hide();
                        approveWindow.show();
                        approveWindow.setRecord(detailsForm.record);
                    }
                    else {
                    	Ext.MessageBox.alert(_("Alert"), _("No Transfer is selected"));
                    }
              	}
            }            
            ],
            items: [ detailsForm ]
        }
        ]
    });
    
    bulkUploadSearchPanel.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;

        if(values.FileStatusSearch === "undefined"){
            values.FileStatusSearch = null;
        }
        if(values.StartDateSearch){
            var date = new Date(Date.parse(values.StartDateSearch)).dateFormat('Ymd-H:i:s:u');
            values.StartDateSearch = date;
        }
        if(values.PaymentDateSearch){
            var date = new Date(Date.parse(values.PaymentDateSearch)).dateFormat('Ymd-H:i:s:u');
            values.PaymentDateSearch = date;
        }
        uploadGrid.store.baseParams = values;

        uploadGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(uploadGrid.store.lastOptions.params, values);
        uploadGrid.store.load(uploadGrid.store.lastOptions);
    });

    uploadGrid.on("defaultSearch", function() {
        bulkUploadSearchPanel.searchHandler();
    });
    
    uploadGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                var transferFileView = new mFino.widget.BulkTransferViewGridWindow(Ext.apply({
                    title : "Details for "+ record.get(CmFinoFIX.message.JSBulkUpload.Entries.FileName._name),
                    grid : new mFino.widget.BulkTransferFileGrid(config)
                },config));
                transferFileView.grid.store.lastOptions = {
                    params : {
                        start : 0,
                        limit : CmFinoFIX.PageSize.Default
                    }
                };
                transferFileView.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.IDSearch._name] = record.get(CmFinoFIX.message.JSBulkUpload.Entries.ID._name);
                transferFileView.grid.store.load(transferFileView.grid.store.lastOptions);
                transferFileView.setStore(transferFileView.grid.store);
                transferFileView.show();
            }
            if(action === 'mfino-button-history'){
                var transferFileView = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
                    title : "Reverse Transaction Details for "+ record.get(CmFinoFIX.message.JSBulkUpload.Entries.FileName._name),
                    grid : new mFino.widget.BulkTransferReverseTxnsGrid(config)
                },config));
                transferFileView.grid.store.lastOptions = {
                    params : {
                        start : 0,
                        limit : CmFinoFIX.PageSize.Default
                    }
                };
                if (record.get(CmFinoFIX.message.JSBulkUpload.Entries.ServiceChargeTransactionLogID._name) != null) {
                    transferFileView.grid.store.baseParams[CmFinoFIX.message.JSServiceChargeTransactions.ParentSCTLID._name] = 
                    	record.get(CmFinoFIX.message.JSBulkUpload.Entries.ServiceChargeTransactionLogID._name);
                    transferFileView.grid.store.load(transferFileView.grid.store.lastOptions);
                    transferFileView.setStore(transferFileView.grid.store);
                    transferFileView.show();
                }
                else {
                	Ext.MessageBox.alert(_("Info"), _("There are no reverse transfer details generated."));
                }
            }
        }
    });

    uploadGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(uploadGrid.store);
        if (Ext.getCmp('bulktransfer.approve') && (mFino.auth.isEnabledItem('bulktransfer.approve')) &&
        		(record.data[CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadDeliveryStatus._name] === CmFinoFIX.BulkUploadDeliveryStatus.Uploaded)) {
        	Ext.getCmp('bulktransfer.approve').show();
        } 
        else if (Ext.getCmp('bulktransfer.approve')) {
        	Ext.getCmp('bulktransfer.approve').hide();
        }
        
        if (Ext.getCmp('bulktransfer.cancel') && (mFino.auth.isEnabledItem('bulktransfer.cancel')) &&
        		(record.data[CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadDeliveryStatus._name] === CmFinoFIX.BulkUploadDeliveryStatus.Uploaded) ) {
        	Ext.getCmp('bulktransfer.cancel').show();
        } 
        else if (Ext.getCmp('bulktransfer.cancel')) {
        	Ext.getCmp('bulktransfer.cancel').hide();
        }
    });
    
    var mainItem = panelCenter.items.get(0);
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
    });
   
    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 250,
            layout : "fit",
            items:[ bulkUploadSearchPanel ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        },
        {
            region: 'south',
            height: 500,
            layout : "fit",
            items: [ uploadGrid ]
        }]
    });
   
    return panel;
};