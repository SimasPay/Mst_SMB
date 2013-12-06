Ext.ns("mFino.widget");
mFino.widget.schedulerConfigGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSScheduleTemplate);
    }

    /*
    if(mFino.auth.isEnabledItem('schedulerConfig.view') && mFino.auth.isEnabledItem('schedulerConfig.edit') ){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-View',
            itemId: 'schedulerConfig.view',
            tooltip: _('View Integration'),
            align:'right'
        },
        {
            iconCls:'mfino-button-edit',
            itemId: 'schedulerConfig.edit',
            tooltip: _('Edit Integration'),
            align:'right'
        }]
    });
    } else if(mFino.auth.isEnabledItem('schedulerConfig.view')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-View',
            itemId: 'schedulerConfig.view',
            tooltip: _('View Integration'),
            align:'right'
        }]
    });
    }else if(mFino.auth.isEnabledItem('schedulerConfig.edit')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls:'mfino-button-edit',
            itemId: 'schedulerConfig.edit',
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
    */

    this.action = new Ext.ux.grid.RowActions({
    header:'',
    keepSelection:true,
    actions:[
    {
        iconCls:'mfino-button-edit',
        itemId: 'schedulerConfig.edit',
        tooltip: _('Edit ScheduleTemplateDetails'),
        align:'right'
    }]
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
            header:  _("ScheduleTemplate Name"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSScheduleTemplate.Entries.Name._name
         },
         {
            header:  _("ScheduleTemplate Mode"),
 		   width : 150,
            dataIndex: CmFinoFIX.message.JSScheduleTemplate.Entries.ModeType._name
         },
         {
             header:  _("ScheduleTemplate Description"),
  		   width : 150,
             dataIndex: CmFinoFIX.message.JSScheduleTemplate.Entries.Description._name
          }
        ]
    });

    mFino.widget.schedulerConfigGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.schedulerConfigGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.schedulerConfigGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("schedulerConfigGrid", mFino.widget.schedulerConfigGrid);
