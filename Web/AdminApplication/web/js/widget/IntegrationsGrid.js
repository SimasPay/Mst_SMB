Ext.ns("mFino.widget");
mFino.widget.IntegrationsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSIntegrationPartnerMapping);
    }

    if(mFino.auth.isEnabledItem('integrations.view') && mFino.auth.isEnabledItem('integrations.edit') ){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-View',
            itemId: 'integrations.view',
            tooltip: _('View Integration'),
            align:'right'
        },
        {
            iconCls:'mfino-button-edit',
            itemId: 'integrations.edit',
            tooltip: _('Edit Integration'),
            align:'right'
        }]
    });
    } else if(mFino.auth.isEnabledItem('integrations.view')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-View',
            itemId: 'integrations.view',
            tooltip: _('View Integration'),
            align:'right'
        }]
    });
    }else if(mFino.auth.isEnabledItem('integrations.edit')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls:'mfino-button-edit',
            itemId: 'integrations.edit',
            tooltip: _('Edit Integration'),
            align:'right'
        }]
    });
    }else {
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[]
    });
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
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        this.action,
        {
            header:  _("Institution ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.InstitutionID._name
         },
         {
            header:  _("Integration Name"),
 		   width : 150,
            dataIndex: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IntegrationName._name
         },
         {
             header:  _("Partner ID"),
             width : 100,
             dataIndex: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.PartnerID._name
          },
          {
              header:  _("MFSBiller ID"),
              width : 100,
              dataIndex: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.MFSBillerId._name
           },
          {
              header:  _("List Of IPs For The Integration"),
              width : 400,
              dataIndex: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.ListOfIPsForIntegration._name
           }
        ]
    });

    mFino.widget.IntegrationsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.IntegrationsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.IntegrationsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("integrationsGrid", mFino.widget.IntegrationsGrid);
