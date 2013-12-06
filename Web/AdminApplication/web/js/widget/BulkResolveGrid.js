/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkResolveGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPendingTransactionsFile);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View BulkResolve Details')
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
        plugins:[this.action],
        viewConfig: {
            emptyText: Config.grid_no_data
        },
        listeners : {
            render : function(){
                this.fireEvent("bulkSearch");
            }
        },
        tbar : [
        {
            text:'Bulk Resolve',
            tooltip:'Resolve Multiple Transactions',
            iconCls:'mfino-button-bulkResolve',
            itemId : 'transactions.bulkresolve',
            handler: this.OnBulkResolve.createDelegate(this)
        }
        ],
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        autoScroll : true,
        columns: [
        this.action,
        {
            header: _('ID'),
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.ID._name
        },
        {
            header: _('Line Count'),
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.RecordCount._name
        },
        {
            header: _('File Status'),
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.UploadStatusText._name
        },
        {
            header: _('Resolve As'),
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.ResolveAsText._name
        },
        {
            header: _('Uploaded By'),
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.CreatedBy._name
        },
        {
            header: _('Last Updated By'),
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.UpdatedBy._name
        },
        {
            header: _('Create Time'),
            renderer: "date",
            width:150,
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.CreateTime._name
        },
        
        {
            header: _('Line Update Time'),
            renderer: "date",
            width:150,
            dataIndex: CmFinoFIX.message.JSPendingTransactionsFile.Entries.LastUpdateTime._name
        }
        ]
    });

    mFino.widget.BulkResolveGrid.superclass.constructor.call(this, localConfig);
    this.getBottomToolbar().add('->',sbun);
};

Ext.extend(mFino.widget.BulkResolveGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.TransactionsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    excelView: function(){
        this.fireEvent("bulkResolveExcelDownload");
    },
    OnBulkResolve: function(){
        this.fireEvent("bulkResolve");
    }
});

Ext.reg("bulkresolvegrid", mFino.widget.BulkResolveGrid);

