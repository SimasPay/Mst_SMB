Ext.ns("mFino.widget");

mFino.widget.FundingForAgentGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.AgentCashIn);
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
	    {
	        header:  _("SCTL ID"),
	        width : 100,
	        dataIndex: CmFinoFIX.message.AgentCashIn.Entries.SctlId._name
	    },
        {
            header:  _("Source MDN"),
            width : 100,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.SourceMDN._name
        },
        {
            header:  _("Destination MDN"),
 		    width : 150,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.DestMDN._name
        },
        {
	        header:  _("Destination Partner ID"),
	        width : 100,
	        dataIndex: CmFinoFIX.message.AgentCashIn.Entries.DestPartnerID._name
        },
        {
            header:  _("Source PocketID"),
            width : 100,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.SourcePocketID._name
        },
        {
            header:  _("Destination PocketID"),
            width : 100,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.DestPocketID._name
        },
        {
            header:  _("Transfer Amount"),
		    width : 150,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.Amount._name
        },
        {
            header:  _("Transaction Status"),
            width : 100,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.AgentCashInTrxnStatusText._name
        },
        {
            header:  _("Transaction Status Reason"),
            width : 100,
            dataIndex: CmFinoFIX.message.AgentCashIn.Entries.AgentCashInTrxnStatusReason._name
        }
        ]
    });

    mFino.widget.FundingForAgentGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FundingForAgentGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.FundingForAgentGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("fundingForAgentGrid", mFino.widget.FundingForAgentGrid);
