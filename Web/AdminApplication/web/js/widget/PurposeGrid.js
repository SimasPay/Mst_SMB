Ext.ns("mFino.widget");
mFino.widget.PurposeGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPurpose);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[]
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
				header:  _("Purpose ID"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSPurpose.Entries.ID._name
			},
			{
				header:  _("Partner Code"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSPurpose.Entries.PurposeCode._name
			},
			{
				header:  _("Category"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSPurpose.Entries.CategoryText._name
			}
        ]
    });

    mFino.widget.PurposeGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PurposeGrid, Ext.grid.GridPanel, {
    initComponent : function () {
		this.tbar =  [		
			'<b class= x-form-tbar>' + _('Purpose results') + '</b>',
				'->',
			{
				iconCls: 'mfino-button-add',
				itemId: 'purpose.add',
				tooltip: _('Add Purpose'),
				handler: this.onAdd.createDelegate(this)
			}
		];
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
		this.addEvents("addclick");
		this.on('render', function(){
	        var tb = this.getTopToolbar();
	        var itemIDs = [];
	        for(var i = 0; i < tb.items.length; i++){
	            itemIDs.push(tb.items.get(i).itemId);
	        }
	        for(i = 0; i < itemIDs.length; i++){
	            var itemID = itemIDs[i];
	            if(!mFino.auth.isEnabledItem(itemID)){
	                var item = tb.getComponent(itemID);
	                tb.remove(item);
	            }
	        }
	    });
        mFino.widget.PurposeGrid.superclass.initComponent.call(this);
    },
	onAdd : function(){
        this.fireEvent("addclick");
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("PurposeGrid", mFino.widget.PurposeGrid);