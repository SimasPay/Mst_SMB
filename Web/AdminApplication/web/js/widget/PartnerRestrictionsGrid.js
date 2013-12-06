/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PartnerRestrictionsGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	
	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	};

	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPartnerRestrictions);
	}
	
	var isAllowedChk = new Ext.ux.grid.CheckColumn(
	{
		header : _('Is Active'),
		dataIndex : CmFinoFIX.message.JSPartnerRestrictions.Entries.IsAllowed._name,
		width : 100
	});
	
	this.action = new Ext.ux.grid.RowActions({
		header:'',
		keepSelection:true,
		actions:[
			{
				iconCls:'mfino-button-remove',
				itemId:'partnerrestrictions.grid.delete',
				tooltip: _('Delete This Restriction')
			}
		]
	});

	this.action.on('action', this.onDelete, this);

	this.transactionTypeCombo = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        id: "partnerrestrictions.form.id.transactiontype",
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionTypeForPartnerRestrictions),
        RPCObject : CmFinoFIX.message.JSTransactionTypeForPartnerRestrictions,
        displayField: CmFinoFIX.message.JSTransactionType.Entries.TransactionName._name,
        valueField : CmFinoFIX.message.JSTransactionType.Entries.ID._name,
		name: CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name,
		lazyRender: true
	});
	
	//this.transactionTypeCombo.on('select', this.checkDuplicate, this);
	//validateedit event can be used for editor grid panel.

	this.restrictionTypeCombo = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        id: "partnerrestrictions.form.id.relationshiptype",
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSRelationshipType),
        RPCObject : CmFinoFIX.message.JSRelationshipType,
        displayField: CmFinoFIX.message.JSRelationshipType.Entries.Description._name,
        valueField : CmFinoFIX.message.JSRelationshipType.Entries.ID._name,
		name: CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name,
		lazyRender: true
	});

	//this.restrictionTypeCombo.on('select', this.checkDuplicate, this);

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
					id: 'partnerrestrictions.actions.add'
				} 
		],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns :[
				this.action,
				{
					header : _("Transaction"),
					dataIndex : CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name,
					width : 175,
		            editor: this.transactionTypeCombo,
		            renderer: Ext.util.Format.comboRenderer(this.transactionTypeCombo),
					allowBlank: false
				},								
				{
					header : _("Restriction Type"),
					dataIndex : CmFinoFIX.message.JSPartnerRestrictions.Entries.RelationShipType._name,
					width : 175,
		            editor: this.restrictionTypeCombo,
		            renderer: Ext.util.Format.comboRenderer(this.restrictionTypeCombo),
					allowBlank: false
				},
				isAllowedChk,
				{
					header : _(""),
					dataIndex : CmFinoFIX.message.JSPartnerRestrictions.Entries.IsValid._name,
					width : 40,
		            renderer: renderIcon,
					allowBlank: false
				}		
			]
	});	
	
	mFino.widget.PartnerRestrictionsGrid.superclass.constructor.call(this, localConfig);
}

Ext.extend(mFino.widget.PartnerRestrictionsGrid , Ext.grid.EditorGridPanel, {
    initComponent : function () {
		this.actions = new Ext.ux.grid.RowActions({header : '', keepSelection : true});
        mFino.widget.PartnerRestrictionsGrid.superclass.initComponent.call(this);
		this.addEvents("addclick");
    },
    
	onAdd : function() {
		var store = this.store;
		var record = new store.recordType();
		var size = store.getCount();
		record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.ID._name] = -1;
		
		if(this.templateID) {
			record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.DCTID._name] = this.templateID;
		}
		if(this.PartnerID){
			record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.PartnerID._name] = this.PartnerID;
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
		this.PartnerID = values.PartnerID;
		
		this.store.lastOptions = {
			params : {
				PartnerIDSearch: this.PartnerID,
				DCTIDSearch: this.TemplateID
			}
		};

		this.store.baseParams['PartnerIDSearch'] = this.PartnerID;
		this.store.baseParams['DCTIDSearch'] = this.templateID;

		this.transactionTypeCombo.store.baseParams['DCTID'] = this.templateID;
		this.transactionTypeCombo.store.baseParams['PartnerID'] = this.PartnerID;
		this.transactionTypeCombo.store.load();

		this.store.load();

		this.restrictionTypeCombo.store.baseParams['DCTID'] = this.templateID;
		this.restrictionTypeCombo.store.baseParams['PartnerID'] = this.PartnerID;
		this.restrictionTypeCombo.store.load();
		

		Ext.getCmp("partnerrestrictions.actions.add").setDisabled(false);
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
			if((modifiedRecords[i].data[CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name] == null) ||
				(modifiedRecords[i].data[CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name] == "")){
				flag = false;
				Ext.ux.Toast.msg(_('Error'), _("Transaction Type empty for one or more restrictions."));
				break;
			}
			else if((modifiedRecords[i].data[CmFinoFIX.message.JSPartnerRestrictions.Entries.RelationShipType._name] == null) ||
				(modifiedRecords[i].data[CmFinoFIX.message.JSPartnerRestrictions.Entries.RelationShipType._name] == "")){
				Ext.ux.Toast.msg(_('Error'), _("Relationship Type empty for one or more restrictions."));
				flag = false;
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
		var transactionTypeId = record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name];
		var restrictionType = record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.RelationShipType._name];

		alert('transactionTypeId='+transactionTypeId+', restrictionType='+restrictionType);
		
		if(transactionTypeId) {
			if(restrictionType){
				var size = this.store.getCount();
				for ( var i = 0; ((i < size) && (i != index)); i++) {
					var tmpTransactionTypeId = record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.TransactionTypeID._name];
					var tmpRestrictionType = record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.RelationShipType._name];
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

function renderIcon(val, p, record) {
	if(record.data[CmFinoFIX.message.JSPartnerRestrictions.Entries.IsValid._name] == false){
	    return '<img onclick="javascript:showMsg();" style="cursor:pointer;height:14px;" src="/AdminApplication/extjs/resources/images/gray/window/icon-warning.gif"/>';
	}

	return "";
}

function showMsg(){
	Ext.Msg.show({title:"Warning", msg:"This transaction is restricted for all partners in Distribution Chain.", buttons: Ext.Msg.OK, closable:false, icon: Ext.MessageBox.WARNING});
}

Ext.reg("PartnerRestrictionsGrid", mFino.widget.PartnerRestrictionsGrid);

//'<div style="width:100%;height:10px;background-image:url(/AdminApplication/extjs/resources/images/gray/window/icon-warning.gif);background-position:center center;background-repeat:no-repeat;">&nbsp;</div>';