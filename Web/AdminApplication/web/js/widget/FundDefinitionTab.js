Ext.ns("mFino.widget");

mFino.widget.FundDefinitionTab = function(config){

    mFino.widget.FundDefinitionTab.superclass.constructor.call(this, config);
};

Ext.extend(mFino.widget.FundDefinitionTab, Ext.TabPanel, {
	initComponent : function(){
		var config = this.initialConfig;
		
		this.FundDefinitionAddWindow = new mFino.widget.FormWindow(Ext.apply({
			form : new mFino.widget.FundDefinitionForm(config),
			mode : 'add',
			modal:true,
			title : _('Add FundDefinition'),
			itemId: 'fundDefinitions.add',
			layout:'fit',
			floating: true,
			width:400,
			height:410,
			plain:true
		},config));
		
		this.FundDefinitionEditWindow = new mFino.widget.FormWindow(Ext.apply({
			form : new mFino.widget.FundDefinitionForm(config),
			mode : 'edit',
			modal:true,
			itemId: 'fundDefinitions.edit',
			title : _('Edit FundDefinition'),
			layout:'fit',
			floating: true,
			width:400,
			height:410,
			plain:true
		},config));
		
		this.fundDefinitionGrid = new mFino.widget.FundDefinitionsGrid(Ext.apply({
			layout: "fit",  
			height: 480
		}, config));
			
		this.fundDefinitionGrid.action.on({
			action: this.editOrViewFundDefinition.createDelegate(this)
		});
	
		this.fundDefinitionGrid.on({
			addclick : this.addFundDefinition.createDelegate(this)
		});
	
		this.fundDefinitionGrid.on("defaultSearch", function() {
			 this.store.lastOptions = {
					 params : {
						 start : 0,
						 limit : CmFinoFIX.PageSize.Default
					 }
			 };
			 this.store.load(this.store.lastOptions);
		});
		
		this.fundDefinitionGrid.selModel.on("rowselect", function(sm, rowIndex, record){
			config.myform.setRecord(record);        
			config.myform.setStore(this.grid.store);
		});
		
		this.purposeAddFormWindow = new mFino.widget.FormWindow(Ext.apply({
			form : new mFino.widget.PurposeAddForm(config),
			title : _("Add purpose"),
			itemId: 'purpose.add',
			mode :"add",
			width : 450,
			height : 170			
		},config));
		
		this.purposeGrid = new mFino.widget.PurposeGrid(Ext.apply({
			layout : "fit",
			height : 480
		},config));
		
		this.purposeGrid.on({
			addclick : this.addPurpose.createDelegate(this)
		});
		
		this.purposeGrid.on("defaultSearch", function() {
			 this.store.lastOptions = {
					 params : {
						 start : 0,
						 limit : CmFinoFIX.PageSize.Default
					 }
			 };
			 this.store.load(this.store.lastOptions);
		});
		
		this.expiryAddFormWindow = new mFino.widget.FormWindow(Ext.apply({
			form : new mFino.widget.ExpiryAddForm(config),
			title: _("Add Expiry Time"),
			mode : "add",
			itemId: 'expiry.add',
			width : 450,
			height :300
		},config));
		
		this.expiryGrid = new mFino.widget.ExpiryGrid(Ext.apply({
			layout : "fit",
			height : 480
		},config));
		
		this.expiryGrid.on({
			addclick : this.addExpiry.createDelegate(this)
		});
		
		this.expiryGrid.on("defaultSearch", function() {
			 this.store.lastOptions = {
					 params : {
						 start : 0,
						 limit : CmFinoFIX.PageSize.Default
					 }
			 };
			 this.store.load(this.store.lastOptions);
		});
        
		
        this.activeTab = 0;
        this.items = [
		{
            title: _('FundDefinition'),
            layout : "fit",
            items:  this.fundDefinitionGrid
        },
        {
            title: _('Purpose'),
            layout : "fit",
            items:  this.purposeGrid
        },
        {
            title: _('Expiry Time'),
            layout : "fit",
            items:  this.expiryGrid
        } 
        ];
		mFino.widget.FundDefinitionTab.superclass.initComponent.call(this);  
		
	},
	
	addPurpose : function(){
		var record = new this.purposeGrid.store.recordType();
		this.purposeAddFormWindow.setTitle(_("Add new Purpose"));
		this.purposeAddFormWindow.setMode("add");
		this.purposeAddFormWindow.show();
		record.set(CmFinoFIX.message.JSPurpose.Entries.Category._name,1);
		this.purposeAddFormWindow.setRecord(record);
		this.purposeAddFormWindow.setStore(this.purposeGrid.store);
		this.purposeAddFormWindow.form.find('itemId','PurposeAddForm.Category')[0].disable();
	},
	
	addExpiry : function(){
		var record = new this.expiryGrid.store.recordType();
		this.expiryAddFormWindow.setTitle(_("Add new Expiry Time"));
		this.expiryAddFormWindow.setMode("add");
		this.expiryAddFormWindow.show();
		record.set(CmFinoFIX.message.JSExpirationType.Entries.ExpiryType._name,1);
		record.set(CmFinoFIX.message.JSExpirationType.Entries.ExpiryMode._name,1);
		this.expiryAddFormWindow.setRecord(record);
		this.expiryAddFormWindow.setStore(this.expiryGrid.store);
		this.expiryAddFormWindow.form.find('itemId','ExpiryAddForm.ExpiryType')[0].disable();
		this.expiryAddFormWindow.form.find('itemId','ExpiryAddForm.ExpiryMode')[0].disable();
	},
	
	addFundDefinition :function(){
		var record = new this.fundDefinitionGrid.store.recordType();
		this.FundDefinitionAddWindow.setTitle(_("Add new FundDefinition"));
		this.FundDefinitionAddWindow.setMode("add");
		this.FundDefinitionAddWindow.show();
		this.FundDefinitionAddWindow.setRecord(record);
		this.FundDefinitionAddWindow.setStore(this.fundDefinitionGrid.store);
	},
	
	editOrViewFundDefinition :function(grid, record, action, row, col) 
	{
		if(action === 'mfino-button-edit' && record !== null){
			this.FundDefinitionEditWindow.show();
			this.FundDefinitionEditWindow.setRecord(record);
			this.FundDefinitionEditWindow.setStore(grid.store);
			this.FundDefinitionEditWindow.form.find('itemId','fundDefinitionForm.PartnerCode')[0].disable();
		}
	}
}
);