/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.AuditLogGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSAuditLog);
    }

    /*var exportButton = new Ext.Toolbar.Button({
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        text: _('Export to Excel'),
        tooltip : _('Export all transactions to excel sheet'),
        handler : this.excelView.createDelegate(this)
    });
    
    var verifyButton = new Ext.Toolbar.Button({
        enableToggle: false,
        iconCls: 'mfino-button-resolve',
        itemId: 'bulktransfer.verify',
        id: 'bulktransfer.verify',
        text: _('Send Fund Access Code'),
        tooltip : _('Verify Non Registered to Send Fund Access Code'),
        handler : this.verify.createDelegate(this)
    });*/

    /*var sm2 = new Ext.grid.CheckboxSelectionModel({
    	checkOnly: true,
    	listeners: {
    		rowselect: function(selectionModel, rowIndex, record) {
    			record.set(CmFinoFIX.message.JSBulkUploadEntry.Entries.IsVerified._name, true);
    			var isNonRegistered = record.get(CmFinoFIX.message.JSBulkUploadEntry.Entries.IsUnRegistered._name);
    			var transferStatus = record.get(CmFinoFIX.message.JSBulkUploadEntry.Entries.Status._name);
    			if (! isNonRegistered || (transferStatus != CmFinoFIX.TransferStatus.Completed)) {
    				Ext.Msg.show({
                        title: _('Alert !'),
                        minProgressWidth:250,
                        msg: _("Please select only Non Registered and completed transactions for Verification"),
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
    				this.deselectRow(rowIndex);
    			} 
    			
    		},
    		rowdeselect: function(selectionModel, rowIndex, record) {
    		}
    	}
    });*/
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        height: 410,
        frame:true,
        loadMask : true,
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        autoScroll : true,
        //sm:sm2,
        columns: [
//        sm2,                  
        {
            header: _('Version'),
            dataIndex: CmFinoFIX.message.JSAuditLog.Entries.RecordVersion._name,
            width: 50
        },                  
        {
            header: _('Time'),
            dataIndex: CmFinoFIX.message.JSAuditLog.Entries.CreateTime._name
        },
        {
            header: _('User'),
            dataIndex: CmFinoFIX.message.JSAuditLog.Entries.CreatedBy._name
        },        
        {
            header: _('Message Name'),
            dataIndex: CmFinoFIX.message.JSAuditLog.Entries.MessageName._name
        },
        {
            header: _('Action'),
            dataIndex: CmFinoFIX.message.JSAuditLog.Entries.Action._name
        },
        {
            header: _('Fix Message'),
            dataIndex: CmFinoFIX.message.JSAuditLog.Entries.FixMessage._name,
            defaultWidth: 600,
            autoSize: true
        }
        ]
    });

    mFino.widget.BulkTransferFileGrid.superclass.constructor.call(this, localConfig);
    //this.getBottomToolbar().add('->', exportButton, verifyButton);
};

Ext.extend(mFino.widget.AuditLogGrid, Ext.grid.GridPanel, {
    excelView: function(){
		if (this.store.getCount() > 0) {
		    this.fireEvent("bulkTransferDownload");
		} else {
			Ext.MessageBox.alert(_("Alert"), _("No Transactions to Export to Excel"));
		}
    },
    verify: function() {
    	this.fireEvent("verified");
    }
});

Ext.reg("AuditLogGrid", mFino.widget.AuditLogGrid);
