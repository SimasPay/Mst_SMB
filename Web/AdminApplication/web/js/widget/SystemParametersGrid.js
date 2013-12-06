Ext.ns("mFino.widget");
mFino.widget.SystemParametersGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSystemParameters);
    }

    /*if(mFino.auth.isEnabledItem('systemParameters.view') && mFino.auth.isEnabledItem('systemParameters.edit') ){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-View',
            itemId: 'systemParameters.view',
            tooltip: _('View System Parameter'),
            align:'right'
        },
        {
            iconCls:'mfino-button-edit',
            itemId: 'systemParameters.edit',
            tooltip: _('Edit System Parameter'),
            align:'right'
        }]
    });
    } else if(mFino.auth.isEnabledItem('systemParameters.view')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-View',
            itemId: 'systemParameters.view',
            tooltip: _('View System Parameter'),
            align:'right'
        }]
    });
    }else*/
    
    if(mFino.auth.isEnabledItem('systemParameters.edit')){
        this.action = new Ext.ux.grid.RowActions({
	        header:'',
	        keepSelection:true,
	        actions:[
	        {
	            iconCls:'mfino-button-edit',
	            itemId: 'systemParameters.edit',
	            tooltip: _('Edit System Parameter'),
	            align:'right'
	        }]
        });
    } else {
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
           header:  _("Parameter Name"),
           width : 300,
           dataIndex: CmFinoFIX.message.JSSystemParameters.Entries.ParameterName._name
        },
        {
           header:  _("Parameter Value"),
           dataIndex: CmFinoFIX.message.JSSystemParameters.Entries.ParameterValue._name
        },
        {
            header:  _("Description"),
            width : 500,
            dataIndex: CmFinoFIX.message.JSSystemParameters.Entries.Description._name
         }
        ]
    });

    mFino.widget.SystemParametersGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SystemParametersGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.SystemParametersGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("systemparametersgrid", mFino.widget.SystemParametersGrid);
