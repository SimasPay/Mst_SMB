Ext.ns("mFino.widget");

mFino.widget.MerchantPrefixCodesGrid = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSMerchantPrefixCode);
    }
    if(mFino.auth.isEnabledItem('merchantPrefixCodes.grid.edit') && mFino.auth.isEnabledItem('merchantPrefixCodes.grid.delete')) {
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-edit',
            itemId : 'merchantPrefixCodes.grid.edit',
            tooltip: _('Edit Merchant Prefix Code')
        }
//        {
//            iconCls: 'mfino-button-remove',
//            itemId : 'merchantPrefixCodes.grid.delete',
//            tooltip: _('Delete Merchant Prefix Code')
//        }
        ]
    });
    } else if(mFino.auth.isEnabledItem('merchantPrefixCodes.grid.edit')) {
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-edit',
            itemId : 'merchantPrefixCodes.grid.edit',
            tooltip: _('Edit Merchant Prefix Code')
        }
        ]
    });
    } else if(mFino.auth.isEnabledItem('merchantPrefixCodes.grid.delete')) {
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls: 'mfino-button-remove',
            itemId : 'merchantPrefixCodes.grid.delete',
            tooltip: _('Delete Merchant Prefix Code')
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
        form : new mFino.widget.MerchantPrefixCodeAddForm(config),
        height : 200,
        width : 350
    },config));
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        height: 485,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'merchantPrefixCodes.grid.add',
            text:'New',
            tooltip: _('New Merchant Code'),
            handler: function(){
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New Merchant Prefix Code"));
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
            header: _("Merchant Prefix Code"),
            width:170,
            dataIndex: CmFinoFIX.message.JSMerchantPrefixCode.Entries.MerchantPrefixCode._name
        },
        {
            header: _("Biller Name"),
            width:170,
            dataIndex: CmFinoFIX.message.JSMerchantPrefixCode.Entries.BillerName._name
        },
        {
            header: _("VA Service Name"),
            width:170,
            dataIndex: CmFinoFIX.message.JSMerchantPrefixCode.Entries.VAServiceName._name
        },
        {
            header: _("Create Time"),
            width:170,
            renderer : 'date',
            dataIndex: CmFinoFIX.message.JSMerchantPrefixCode.Entries.CreateTime._name
        },
        {
            header: _("Created By"),
            width:170,
            dataIndex: CmFinoFIX.message.JSMerchantPrefixCode.Entries.CreatedBy._name
        }
        ]
    });
    this.superclass().constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantPrefixCodesGrid, Ext.grid.GridPanel, {

    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        this.superclass().initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});