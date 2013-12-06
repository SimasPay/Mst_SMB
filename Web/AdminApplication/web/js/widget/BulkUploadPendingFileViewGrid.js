/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkUploadPendingFileViewGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPendingTransactionsEntry);
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
                header: _('Transfer ID'),
                dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.TransferID._name
            },
            {
                header: _('Line Number'),
                width :70,
                dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.LineNumber._name
            },
            {
                header: _('Source MDN'),
                dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.SourceMDN._name,
                renderer: function(value, metadata, record)
                {
                    if(value===null){
                        return "--";
                    }
                    return value;

                }
            },
                {
                    header: _('Dest MDN'),
                    dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.DestMDN._name,
                    renderer: function(value, metadata, record){
                    if(value===null){
                        return "--";
                    }
                    return value;

                     }
                },
                {
                  header: _('Amount'),
                  dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.Amount._name
                },
                {
                    header: _('ID'),
                    dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.ID._name
                },
                {
                    header: _('Processsing Status'),
                    dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.ResolveStatusText._name
                },
                {
                    header: _('Failure Reason'),
                    dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.ResolveFailureReason._name
                },
                {
                    header: _('Process Time'),
                    renderer:'date',
                    width:150,
                    dataIndex: CmFinoFIX.message.JSPendingTransactionsEntry.Entries.LastUpdateTime._name
                }
            ]
        });

        mFino.widget.BulkUploadPendingFileViewGrid.superclass.constructor.call(this, localConfig);
        this.getBottomToolbar().add('->',sbun);
    };

    Ext.extend(mFino.widget.BulkUploadPendingFileViewGrid, Ext.grid.GridPanel, {
        excelView: function(){
            this.fireEvent("pendingtransactionsentry");
        }
    });

    Ext.reg("bulkuploadfilependingviewgrid", mFino.widget.BulkUploadPendingFileViewGrid);
