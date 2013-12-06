/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.TransactionRuleGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);

    var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionRule);

    if(mFino.auth.isEnabledItem('transactionrule.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Transaction Rule Details'),
                align:'right'
            },
            {
                iconCls:'mfino-button-edit',
                itemId:'transactionrule.grid.edit',
                tooltip: _('Edit Transaction Rule')
            }
            ]
        });
    } else{
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Transaction Rule Details'),
                align:'right'
            }
            ]
        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl: "fix.htm",
        store: store,
        frame: true,
        width: 300,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: store,
            displayInfo: true,
            displayMsg: _('Displaying topics') + ('{0} - {1} of {2}'),
            emptyMsg: _("No Records to display"),
            pageSize: CmFinoFIX.PageSize.Default
            
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        this.action,
        {
            header: _("Name"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.Name._name
        },
        {
            header: _("Service Provider"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.ServiceProviderName._name
        },
        {
            header: _("Service"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.ServiceName._name
        },
        {
            header: _("Transaction Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.TransactionName._name
        },
        {
            header: _("Channel"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.ChannelName._name
        },
        {
            header: _("Charge Mode"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.ChargeModeText._name
        },
        {
            header: _("Source Group"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.SourceGroupName._name
        },
        {
            header: _("Destination Group"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.DestinationGroupName._name
        },
        {
            header: _("Has Additional Info"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.AdditionalInfo._name
        },
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.CreatedBy._name
        },
        {
            header: _("Created On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.CreateTime._name
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.UpdatedBy._name
        },
        {
            header: _("Modified On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSTransactionRule.Entries.LastUpdateTime._name
        }        
        ]
    });

    mFino.widget.TransactionRuleGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionRuleGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.TransactionRuleGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});
