/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DCTRestrictionsGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	
	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	};

	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSDCTRestrictions);
	}
	
	var isAllowedChk = new Ext.ux.grid.CheckColumn(
	{
		header : _('Is Active'),
		dataIndex : CmFinoFIX.message.JSDCTRestrictions.Entries.IsAllowed._name,
		width : 100
	});
	
	this.action = new Ext.ux.grid.RowActions({
		header:'',
		keepSelection:true,
		actions:[
			{
				iconCls:'mfino-button-remove',
				itemId:'dctrestrictions.grid.delete',
				tooltip: _('Delete This Permission')
			}
		]
	});

	this.action.on('action', this.onDelete, this);

	this.transactionTypeCombo = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        id: "dctrestrictions.form.id.transactiontype",
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionType),
        RPCObject : CmFinoFIX.message.JSTransactionType,
        displayField: CmFinoFIX.message.JSTransactionType.Entries.TransactionName._name,
        valueField : CmFinoFIX.message.JSTransactionType.Entries.ID._name,
		name: CmFinoFIX.message.JSDCTRestrictions.Entries.TransactionTypeID._name,
		lazyRender: true
	});
	
	//this.transactionTypeCombo.on('select', this.checkDuplicate, this);
	//validateedit event can be used for editor grid panel.

	this.restrictionTypeCombo = new mFino.widget.EnumDropDown({
        xtype: 'enumdropdown',
        fieldLabel  : _('Relationship Type'),
        enumId: CmFinoFIX.TagID.RelationShipType,
        itemId : 'restriction',
        name: CmFinoFIX.message.JSDCTRestrictions.Entries.RelationShipType._name,
        width  : 150,
        allowBlank: false,
        mode: 'local',
        triggerAction: 'all',
        emptyText : '<Select one..>'	
	});
	
	//this.restrictionTypeCombo.on('select', this.checkDuplicate, this);
	
	var levelsStore =new Ext.data.SimpleStore( {
		data:[],
		fields:["Level"]
	});
	
	this.levelsCombo = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        id: "dctrestrictions.form.id.levels",
        store: levelsStore,
		name: CmFinoFIX.message.JSDCTRestrictions.Entries.Level._name,
		displayField:'Level',
		valueField:'Level',
		mode: 'local'
	});

	localConfig = Ext.apply(localConfig,{
		dataUrl : "fix.htm",
		loadMask : true,
		clicksToEdit : 1,
		plugins : [ isAllowedChk, this.action ],
		tbar : [
				{
					iconCls : 'mfino-button-save',
					tooltip : _('Save Restrictions'),
					handler : this.onSave.createDelegate(this)
				}, 
				'->',
				{
					iconCls : 'mfino-button-add',
					tooltip : _('Add Restriction'),
					handler : this.onAdd.createDelegate(this),
					disabled: true,
					id: 'dctrestrictions.actions.add'
				} 
		],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns :[
				this.action,
				{
					header : _("Levels"),
					dataIndex : CmFinoFIX.message.JSDCTRestrictions.Entries.Level._name,
					width : 175,
		            editor: this.levelsCombo,
		            renderer: Ext.util.Format.comboRenderer(this.levelsCombo),
					allowBlank: false
				},
				{
					header : _("Transaction"),
					dataIndex : CmFinoFIX.message.JSDCTRestrictions.Entries.TransactionTypeID._name,
					width : 175,
		            editor: this.transactionTypeCombo,
		            renderer: Ext.util.Format.comboRenderer(this.transactionTypeCombo),
					allowBlank: false
				},								
				{
					header : _("Relationship Type"),
					dataIndex : CmFinoFIX.message.JSDCTRestrictions.Entries.RelationShipType._name,
					width : 175,
		            editor: this.restrictionTypeCombo,
		            renderer: Ext.util.Format.comboRenderer(this.restrictionTypeCombo),
					allowBlank: false
				},
				isAllowedChk 
			]
	});	
	
	mFino.widget.DCTRestrictionsGrid.superclass.constructor.call(this, localConfig);
}

Ext.extend(mFino.widget.DCTRestrictionsGrid , Ext.grid.EditorGridPanel, {
    initComponent : function () {
		this.actions = new Ext.ux.grid.RowActions({header : '', keepSelection : true});
        mFino.widget.DCTRestrictionsGrid.superclass.initComponent.call(this);
		this.addEvents("addclick");
    },
    
	onAdd : function() {
		var store = this.store;
		var record = new store.recordType();
		var size = store.getCount();
		record.data[CmFinoFIX.message.JSDCTRestrictions.Entries.ID._name] = -1;
		
		if(this.templateID) {
			record.data[CmFinoFIX.message.JSDCTRestrictions.Entries.DCTID._name] = this.templateID;
		}

		store.add(record);
	},

	onDeleteLast : function() {
		var size = this.store.getCount();
		
		if (size > 0) {
			var record = this.store.getAt(size - 1);
			var store = this.store;
			store.remove(record);
			if(!record.phantom) {
				store.save();
			}
		}
		else{
			Ext.ux.Toast.msg(_('Error'), _("No Records to Delete"));			
		}
	},
	
	onDelete : function(grid, record, action, row, col) {
		var store = this.store;
		store.remove(record);
		if(!record.phantom) {
			//store.save();
		}
	},

	setValues: function(values){
		this.templateID = values.TemplateID;
		this.ServiceID = values.ServiceID;
		this.LevelsCount = values.Levels;

		this.store.lastOptions = {
			params : {
				DCTIDSearch: this.templateID
			}
		};

		this.transactionTypeCombo.store.baseParams['ServiceIDSearch'] = values.ServiceID;
		this.transactionTypeCombo.store.load();

		this.store.load(this.store.lastOptions);
		Ext.getCmp("dctrestrictions.actions.add").setDisabled(false);

		var jsLevelsArray = new Array();

		for(var i=0; i < this.LevelsCount; i++){
			var levelArr = new Array();
			levelArr[0] = (i + 1);
			jsLevelsArray[i] = levelArr;
		}
		
		this.levelsCombo.store.loadData(jsLevelsArray);

//		this.transactionTypeCombo.store.lastOptions = {
//				params : {
//					ServiceIDSearch: values.ServiceID
//				}
//			};
		
		//this.transactionTypeCombo.store.load(this.transactionTypeCombo.store.lastOptions);
	},

	onSave: function(){

		var modifiedRecords = this.store.getModifiedRecords();
		var flag = true;
		for(var i=0; i < modifiedRecords.length; i++){
			if((modifiedRecords[i].data[CmFinoFIX.message.JSDCTRestrictions.Entries.Level._name] == null) ||
				(modifiedRecords[i].data[CmFinoFIX.message.JSDCTRestrictions.Entries.Level._name] == "")){
				flag = false;
				Ext.ux.Toast.msg(_('Error'), _("Level is empty for one or more permissions."));
				break;
			}
			else if((modifiedRecords[i].data[CmFinoFIX.message.JSDCTRestrictions.Entries.TransactionTypeID._name] == null) ||
				(modifiedRecords[i].data[CmFinoFIX.message.JSDCTRestrictions.Entries.TransactionTypeID._name] == "")){
				flag = false;
				Ext.ux.Toast.msg(_('Error'), _("Transaction Type empty for one or more permissions."));
				break;
			}
			else if((modifiedRecords[i].data[CmFinoFIX.message.JSDCTRestrictions.Entries.RelationShipType._name] == null) ||
				(modifiedRecords[i].data[CmFinoFIX.message.JSDCTRestrictions.Entries.RelationShipType._name] == "")){
				flag = false;
				Ext.ux.Toast.msg(_('Error'), _("Relationship Type empty for one or more permissions."));
				break;
			}
		}
		
		if(flag){
			this.store.save();
		}
	},
	
	onStateSave: function(){
		alert('save successfull');
	},

	checkDuplicate: function(combo, record, index){
		var duplicateFlag = false;
		var transactionTypeId = record.data[CmFinoFIX.message.JSDCTRestrictions.Entries.TransactionTypeID._name];
		var restrictionType = record.data[CmFinoFIX.message.JSDCTRestrictions.Entries.RelationShipType._name];

		alert('transactionTypeId='+transactionTypeId+', restrictionType='+restrictionType);
		
		if(transactionTypeId) {
			if(restrictionType){
				var size = this.store.getCount();
				for ( var i = 0; ((i < size) && (i != index)); i++) {
					var tmpTransactionTypeId = record.data[CmFinoFIX.message.JSDCTRestrictions.Entries.TransactionTypeID._name];
					var tmpRestrictionType = record.data[CmFinoFIX.message.JSDCTRestrictions.Entries.RelationShipType._name];
					alert('tmpTransactionTypeId='+tmpTransactionTypeId+', tmpRestrictionType='+tmpRestrictionType);
					if((tmpTransactionTypeId == transactionTypeId) && (tmpRestrictionType == restrictionType)){
						duplicateFlag = true;
					}
				}
			}
		}
		this.duplicatesExist = duplicateFlag;
		alert(duplicateFlag);
	}
});

Ext.reg("DCTRestrictionsGrid", mFino.widget.DCTRestrictionsGrid);