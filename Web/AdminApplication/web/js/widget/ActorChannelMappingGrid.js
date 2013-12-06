/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ActorChannelMappingGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);
    
    var isAllowedColumn = new Ext.ux.grid.CheckColumn({
				header : _('Is Allowed'),
				dataIndex :CmFinoFIX.message.JSActorChannelMapping.Entries.IsAllowed._name,
				width : 70
				});
     var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSActorChannelMapping);

    if(mFino.auth.isEnabledItem('actorChannelMapping.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            /*{
                iconCls:'mfino-button-View',
                tooltip: _('View Actor Channel Mapping Details'),
                align:'right'
            },*/
            {
                iconCls:'mfino-button-edit',
                itemId:'actorChannelMapping.edit',
                tooltip: _('Edit Actor Channel Mapping')
            }
            ]
        });
    } else{
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            /*{
                iconCls:'mfino-button-View',
                tooltip: _('View Actor Channel Mapping Details'),
                align:'right'
            }*/
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
        isAllowedColumn,
        {
            header: _("Actor"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.SubscriberTypeText._name
        },
        {
            header: _("Partner Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.BusinessPartnerTypeText._name
        },
        {
            header: _("Service name"),
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.ServiceName._name
        },
        {
            header: _("Transaction name"),
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.TransactionName._name
        },
        {
            header: _("Channel"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.ChannelName._name
        },        
        {
            header: _("KYC"),
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.KYCLevelText._name
        },
        {
            header: _("Group"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSActorChannelMapping.Entries.GroupName._name
        }             
        ]
    });

    mFino.widget.ActorChannelMappingGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ActorChannelMappingGrid, Ext.grid.GridPanel, {
    initComponent : function () {    	
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.ActorChannelMappingGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    } 
    
});