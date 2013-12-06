Ext.ns("mFino.widget");

mFino.widget.ChannelCodesGrid = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSChannelCode);
    }
    if(mFino.auth.isEnabledItem('channelCodes.grid.edit') && mFino.auth.isEnabledItem('channelCodes.grid.delete')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-edit',
                itemId : 'channelCodes.grid.edit',
                tooltip: _('Edit Channel Code')
            },
            {
                iconCls: 'mfino-button-remove',
                itemId : 'channelCodes.grid.delete',
                tooltip: _('Delete Channel Code')
            }
            ]
        });
    } else if(mFino.auth.isEnabledItem('channelCodes.grid.edit')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-edit',
                itemId : 'channelCodes.grid.edit',
                tooltip: _('Edit Channel Code')
            }
            ]
        });
    } else if(mFino.auth.isEnabledItem('channelCodes.grid.delete')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls: 'mfino-button-remove',
                itemId : 'channelCodes.grid.delete',
                tooltip: _('Delete Channel Code')
            }
            ]
        });
    } else {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[]
        });
    }
    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.ChannelCodeAddForm(config),
        height : 200,
        width : 350
    },config));
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        height: 510,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'channelCodes.grid.add',
            text:'New',
            tooltip: _('New Channel Code'),
            handler: function(){
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New Channel Code"));
                gridAddForm.setMode("add");
                gridAddForm.show();
                gridAddForm.setRecord(record);
                gridAddForm.setStore(localConfig.store);
            }
        }],
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        plugins:[
        this.action],
        columns: [
        this.action,
        {
            header: _("Channel Code"),
            width:280,
            dataIndex: CmFinoFIX.message.JSChannelCode.Entries.ChannelCode._name
        },
        {
            header: _("Channel Name"),
            width:280,
            dataIndex: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name
        },
        {
            header: _("Description"),
            width:280,
            dataIndex: CmFinoFIX.message.JSChannelCode.Entries.Description._name
        }
        ]
    });

    mFino.widget.ChannelCodesGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChannelCodesGrid, Ext.grid.GridPanel, {

    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.ChannelCodesGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});
