Ext.ns("mFino.widget");
mFino.widget.FundDefinitionsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSFundDefinitions);
    }

    if(mFino.auth.isEnabledItem('fundDefinitions.edit')){
        this.action = new Ext.ux.grid.RowActions({
	        header:'',
	        keepSelection:true,
	        actions:[
	        {
	            iconCls:'mfino-button-edit',
	            itemId: 'fundDefinitions.edit',
	            tooltip: _('Edit fundDefinition'),
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
				header:  _("Partner Code"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.PurposeCode._name
			},
			{
				header:  _("FAC Prefix"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.FACPrefix._name
			},
			{
				header:  _("FAC Length"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.FACLength._name
			},
			{
				header:  _("Expiry Time"),
				width : 100,
				dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.ExpiryValue._name
			},
			{
				header:  _("OnFundAllocationTimeExpiry"),
				width : 150,
				dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.OnFundAllocationTimeExpiryText._name
			},
            {
	           	header:  _("On Failed Attempts Exceeded"),
	           	width : 150,
	           	dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.OnFailedAttemptsExceededText._name
            },
            {
	           	header:  _("Generation of FAC On Failure"),
	           	width : 150,
	           	dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.GenerationOfOTPOnFailureText._name
            },
            {
	           	header:  _("Is Multiple Withdrawal Allowed"),
	           	width : 100,
	           	dataIndex: CmFinoFIX.message.JSFundDefinitions.Entries.IsMultipleWithdrawalAllowed._name
            }
        ]
    });

    mFino.widget.FundDefinitionsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FundDefinitionsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
		this.tbar =  [		
				'<b class= x-form-tbar>' + _('Fund Definition results') + '</b>',
					'->',
				{
					iconCls: 'mfino-button-add',
					itemId: 'fundDefinitions.add',
					tooltip: _('Add FundDef'),
					handler: this.onAdd.createDelegate(this)
				}
		];

        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.FundDefinitionsGrid.superclass.initComponent.call(this);
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

Ext.reg("fundDefinitionsGrid", mFino.widget.FundDefinitionsGrid);
