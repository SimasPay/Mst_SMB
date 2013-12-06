/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkUploadViewGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkUploadEntry);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View BulkUpload Details')
        }
        ]
    });
    var sbun = new Ext.Toolbar.Button({
//        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
    });
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        autoScroll : true,
        columns: [
        {
            header: _('No'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.LineNumber._name
        },
        {
            header: _('MDN'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.DestMDN._name
        },
        {
            header: _('Transaction Ref'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.TransferID._name
        },
        {
            header: _('Amount'),
            renderer : 'money',
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.Amount._name
        },
        {
            header: _('Status'),
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.TransferStatusText._name,
            renderer: function(value,metadata, record)
            {
            if(record.data[CmFinoFIX.message.JSBulkUploadEntry.Entries.Status._name] ==CmFinoFIX.TransferStatus.Completed || record.data[CmFinoFIX.message.JSBulkUploadEntry.Entries.Status._name] == CmFinoFIX.TransferStatus.Failed){
                    return value;
                }else{
                    return "Pending";
                }
            }
        },
        {
            header: _('Failure Reason'),
            width: 120,
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.TransferFailureReasonText._name
        },
        {
            header: _('Status Time'),
            renderer:'date',
            width: 140,
            dataIndex: CmFinoFIX.message.JSBulkUploadEntry.Entries.LastUpdateTime._name
        }
        ]
    });

    mFino.widget.BulkUploadViewGrid.superclass.constructor.call(this, localConfig);
    this.getBottomToolbar().add('->',sbun);
};

Ext.extend(mFino.widget.BulkUploadViewGrid, Ext.grid.GridPanel, {
    excelView: function(){
        this.fireEvent("download");        
    }
});

Ext.reg("bulkuploadviewgrid", mFino.widget.BulkUploadViewGrid);
