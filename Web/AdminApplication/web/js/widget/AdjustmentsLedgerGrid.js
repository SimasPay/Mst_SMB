/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.AdjustmentsLedgerGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSLedger);
    }

    /*this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View Transaction Details')
        }]
    });*/
    
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        /*bbar: new Ext.PagingToolbar({
            store: localConfig.store, 
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),*/
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        //plugins:[this.action],        
        columns: [
        //this.action,
        {
            header: _("Ledger ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSLedger.Entries.ID._name
        },
        {
            header: _("Commodity Transfer ID"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSLedger.Entries.CommodityTransferID._name
        },
        {
            header: _("Source MDN"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSLedger.Entries.SourceMDN._name
        },{
        	header: _("Source Pocket"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSLedger.Entries.SourcePocketTemplateDescription._name
		},
		{
            header: _("Destination MDN"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSLedger.Entries.DestMDN._name
        },{
        	header: _("Destination Pocket"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSLedger.Entries.DestPocketTemplateDescription._name
		},
        {
            header: _("Amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSLedger.Entries.Amount._name
        }
		]
    });

    mFino.widget.AdjustmentsLedgerGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsLedgerGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.AdjustmentsLedgerGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("adjustmentsledgergrid", mFino.widget.AdjustmentsLedgerGrid);
