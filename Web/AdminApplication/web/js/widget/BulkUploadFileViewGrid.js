/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkUploadFileViewGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkUploadFileEntry);
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
            header: _('Line Num'),
            dataIndex: CmFinoFIX.message.JSBulkUploadFileEntry.Entries.LineNumber._name
        },
        {
            header: _('MDN'),
            dataIndex: CmFinoFIX.message.JSBulkUploadFileEntry.Entries.MDN._name
        },
        {
            header: _('Status'),
            dataIndex: CmFinoFIX.message.JSBulkUploadFileEntry.Entries.RecordStatusText._name
        },
        {
            header: _('Failure Reason'),
            width :470,
            dataIndex: CmFinoFIX.message.JSBulkUploadFileEntry.Entries.RecordMessage._name
        }
        ]
    });

    mFino.widget.BulkUploadFileViewGrid.superclass.constructor.call(this, localConfig);
    this.getBottomToolbar().add('->',sbun);
};

Ext.extend(mFino.widget.BulkUploadFileViewGrid, Ext.grid.GridPanel, {
    excelView: function(){
        this.fireEvent("filedownload");
    }
});

Ext.reg("bulkuploadfileviewgrid", mFino.widget.BulkUploadFileViewGrid);
