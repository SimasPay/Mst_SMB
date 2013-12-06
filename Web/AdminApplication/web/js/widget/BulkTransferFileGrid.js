/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkTransferFileGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkUploadEntry);
    }

    var exportButton = new Ext.Toolbar.Button({
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        text: _('Export to Excel'),
        tooltip : _('Export data to Excel Sheet'),
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
    });

    var sm2 = new Ext.grid.CheckboxSelectionModel({
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
    });
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
        sm:sm2,
        columns: [
        sm2,                  
        {
            header: _('Line No.'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.LineNumber._name,
            width: 50
        },                  
        {
            header: _('Reference Id'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.ServiceChargeTransactionLogID._name
        },
        {
            header: _('First Name'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.FirstName._name
        },
        {
            header: _('Last Name'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.LastName._name
        },        
        {
            header: _('Destination MDN'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.DestMDN._name
        },
        {
            header: _('Non Registered'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.IsUnRegistered._name,
			width : 75,
            renderer: function(value) {
				if (value) {
					return "Yes";
				} else {
					return "No";
				}
			}
        },        
        {
            header: _('Amount'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.Amount._name
        },
        {
            header: _('Status'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.TransferStatusText._name

        },
        {
            header: _('Failure Reason'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.FailureReason._name

        }
        ]
    });

    mFino.widget.BulkTransferFileGrid.superclass.constructor.call(this, localConfig);
    this.getBottomToolbar().add('->', exportButton, verifyButton);
};

Ext.extend(mFino.widget.BulkTransferFileGrid, Ext.grid.GridPanel, {
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

Ext.reg("BulkTransferFileGrid", mFino.widget.BulkTransferFileGrid);
