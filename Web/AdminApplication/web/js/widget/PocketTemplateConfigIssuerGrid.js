/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PocketTemplateConfigIssuerGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);

    var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPocketTemplateConfig);

    if(mFino.auth.isEnabledItem('pockettemplate.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Pocket Template Config Details'),
                align:'right'
            },
            {
                iconCls:'mfino-button-edit',
                itemId:'pockettemplate.grid.edit',
                tooltip: _('Edit Pocket Template Config')
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
                tooltip: _('View Pocket Template Config Details'),
                align:'right'
            }
            ]
        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
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
            header: _("Pocket Template Config ID"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.ID._name
        },
		{
            header: _("Subscriber Type"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.SubscriberTypeText._name
        },
		{
            header: _("Business Partner Type"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.BusinessPartnerTypeText._name
        },
		{
            header: _("KYCLevel"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.KYCLevelText._name
        },
        {
            header: _("IsDefault"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsDefault._name
        },
		{
            header: _("Pocket Template ID"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateID._name
        },
		{
            header: _("Pocket Template Description"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateDescription._name
        },
		{
            header: _("Commodity Type"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.CommodityTypeText._name
        },
        {
            header: _("Pocket Type"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTypeText._name
        },
        {
            header: _("Is Collector Pocket"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsCollectorPocket._name
        },
        {
            header: _("Is Suspense Pocket"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsSuspencePocket._name
        },
        {
            header: _("Group"),
            dataIndex: CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupName._name
        }
        ]
    });

    mFino.widget.PocketTemplateConfigIssuerGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTemplateConfigIssuerGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.PocketTemplateConfigIssuerGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("PocketTemplateConfigIssuerGrid", mFino.widget.PocketTemplateConfigIssuerGrid);







