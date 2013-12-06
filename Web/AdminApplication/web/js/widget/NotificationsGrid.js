Ext.ns("mFino.widget");
mFino.widget.NotificationsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    var isActiveColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _('Is Enabled'),
				dataIndex :CmFinoFIX.message.JSNotification.Entries.IsActive._name,
				width : 85
				});
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSNotification);
    }

    if(mFino.auth.isEnabledItem('notification.grid.add') && mFino.auth.isEnabledItem('notification.grid.edit') ){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-add',
            itemId: 'notification.grid.add',
            tooltip: _('Add Notification Message'),
            align:'right'
        },
        {
            iconCls:'mfino-button-edit',
            itemId: 'notification.grid.edit',
            tooltip: _('Edit Notification Message'),
            align:'right'
        }]
    });
    } else if(mFino.auth.isEnabledItem('notification.grid.add')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls: 'mfino-button-add',
            itemId: 'notification.grid.add',
            tooltip: _('Add Notification Message'),
            align:'right'
        }]
    });
    }else if(mFino.auth.isEnabledItem('notification.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls:'mfino-button-edit',
            itemId: 'notification.grid.edit',
            tooltip: _('Edit Notification Message'),
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
            header: _("ID"),
            dataIndex: CmFinoFIX.message.JSNotification.Entries.ID._name
        },
        {
           header:  _("Code"),
           dataIndex: CmFinoFIX.message.JSNotification.Entries.NotificationCode._name
        },
        {
           header:  _("Code Name"),
           width : 300,
           dataIndex: CmFinoFIX.message.JSNotification.Entries.NotificationCodeName._name
        },
        {
            header: _("Mode"),
            dataIndex: CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name,
            renderer: function(value, a, b){
                    var str="";
                    if((b.data[CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name]& CmFinoFIX.NotificationMethod.SMS)===CmFinoFIX.NotificationMethod.SMS){
                        str+=" SMS ";
                    }else if((b.data[CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name]& CmFinoFIX.NotificationMethod.Email)===CmFinoFIX.NotificationMethod.Email){
                         str+=" Email ";
                    }else if((b.data[CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name]& CmFinoFIX.NotificationMethod.Web)===CmFinoFIX.NotificationMethod.Web){
                         str+=" Web ";
                    }else if((b.data[CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name]& CmFinoFIX.NotificationMethod.WebService)===CmFinoFIX.NotificationMethod.WebService){
                         str+=" WebService ";
                    }else if((b.data[CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name]& CmFinoFIX.NotificationMethod.BankChannel)===CmFinoFIX.NotificationMethod.BankChannel){
                         str+=" BankChannel ";
                    }else {
                        return "--";
                    }
                    return str;
             }
        },
        isActiveColumn,
        {
            header:  _('Language'),
            dataIndex: CmFinoFIX.message.JSNotification.Entries.LanguageText._name
        },
        {
            header:  _('Message'),
            width: 1100,
            dataIndex: CmFinoFIX.message.JSNotification.Entries.NotificationText._name
        }
        ]
    });

    mFino.widget.NotificationsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.NotificationsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.NotificationsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    onEnableAll : function(){
        for(var i=0;i<this.store.getCount();i++){
        	if(!this.store.getAt(i).data.IsActive){
        	this.store.getAt(i).beginEdit();
        	this.store.getAt(i).set(CmFinoFIX.message.JSNotification.Entries.IsActive._name,1);
        	this.store.getAt(i).endEdit();
        	}
        }
        this.store.save();
//        this.store.reload();
    },
    onDisableAll : function(){
        for(var i=0;i<this.store.getCount();i++){
        	if(this.store.getAt(i).data.IsActive){
        	this.store.getAt(i).beginEdit();
        	this.store.getAt(i).set(CmFinoFIX.message.JSNotification.Entries.IsActive._name,0);
        	this.store.getAt(i).endEdit();
        	}
        }
        this.store.save();
//        this.store.reload();
    }
});

Ext.reg("notificationsgrid", mFino.widget.NotificationsGrid);
