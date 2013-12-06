/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PartnerTransactionsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionAmountDistributionLog);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View Transaction Details')
        }]
    });
    var sbun = new Ext.Toolbar.Button({
        enableToggle: false,
        text: _('Export to Excel'),
        iconCls: 'mfino-button-excel',
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
    });
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store, 
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default,
            items: [sbun]
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        this.action,
        {
            header: _("Reference ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransactionID._name
        },
//        {
//            header: _("Time"),
//            renderer: "date",
//            width : 150,
//            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.StartTime._name
//        },
        {
            header: _("PartnerID"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.PartnerID._name
        },
        
//        {
//            header: _("TradeName"),
//            renderer : "money",
//            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TradeName._name
//        },
        {
            header: _("ShareAmount"),
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ShareAmount._name
        },
        {
            header: _("Status"),
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransferStatusText._name
        },
        {
            header: _("SourceMDN"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.SourceMDN._name
        },
        {
            header: _("CreateTime"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.CreateTime._name
        },
        {
            header: _("Channel Name"),
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ChannelSourceApplicationText._name
        },
        {
            header: _("PocketId"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.PocketID._name
        },
//        {
//            header: _("SourceSubscriberName"),
//            width : 200,
//            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.SourceSubscriberName._name
//        },
        {
            header: _("Channel Name"),
            dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ChannelSourceApplicationText._name
        }
        ]
    });

    mFino.widget.PartnerTransactionsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PartnerTransactionsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.PartnerTransactionsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    excelView: function(){
        this.fireEvent("download");
    }
});

Ext.reg("partnertransactionsgrid", mFino.widget.PartnerTransactionsGrid);
