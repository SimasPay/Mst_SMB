/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.MFSDenominationsGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	var numberEditor = new Ext.form.NumberField( {
		allowNegative : false,
		allowDecimals : true,
		allowBlank: false,
		decimalPrecision : 2
	});
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMFSDenominations);
	}
	
	this.rowDelete = new Ext.ux.grid.RowActions({
		header : '',
		keepSelection : true,
		actions : [{
			iconCls : 'mfino-button-remove',
			tooltip : _('Delete Row'),
			align : 'center'
		}],
		callbacks:{
			'mfino-button-remove' : function(grid, record, action, rowIndex, colIndex){
				Ext.MessageBox.confirm(_('Delete Denomination?'), _('Do you want to delete the denomination [' 
						+ record.data[CmFinoFIX.message.JSMFSDenominations.Entries.DenominationAmount._name] + '] ?'),
					function(btn) {
						if (btn === 'yes') {
							if (grid.store) {
								grid.store.remove(record);
							}
						}
					}
				);
			 }
		}		
	});
	
	localConfig = Ext.apply(localConfig, {
		dataUrl : "fix.htm",
		loadMask : true,
		plugins : [ this.rowDelete ],
		tbar : [{
			iconCls : 'mfino-button-add',
			text : _('Add Denominations'),
			handler : this.onAdd.createDelegate(this)
		}],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns : [
		        this.rowDelete,
				{
					header : _("Denomination"),
					dataIndex : CmFinoFIX.message.JSMFSDenominations.Entries.DenominationAmount._name,
					width : 80,
					editor : {
						xtype: 'numberfield',
						allowNegative : false,
						allowDecimals : true,
						allowBlank: false,
						itemId: 'md.grid.denomination',
						decimalPrecision : 2						
					}
				},
				{
					header : _("Description"),
					dataIndex : CmFinoFIX.message.JSMFSDenominations.Entries.Description._name,
					width : 200,
					editor : {
						xtype: 'textfield',
						itemId: 'md.grid.description'				
					}
				},
				{
					header : _("Product Code"),
					dataIndex : CmFinoFIX.message.JSMFSDenominations.Entries.ProductCode._name,
					width : 80,
					editor : {
						xtype: 'textfield',
						itemId: 'md.grid.productcode'				
					}
				}
				
				]
	});

	mFino.widget.MFSDenominationsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSDenominationsGrid, Ext.grid.EditorGridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.MFSDenominationsGrid.superclass.initComponent.call(this);

	},
	
	onAdd : function() {
		var record = new this.store.recordType();
		record.disable = false;
		var size = this.store.getCount();
		record.data[CmFinoFIX.message.JSMFSDenominations.Entries.ID._name] = -1;
		if (this.templateID) {
			record.data[CmFinoFIX.message.JSMFSDenominations.Entries.MFSID._name] = this.templateID;
		}
		this.store.add(record);
	},

	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},

	setParentTemplateData : function(templateID) {
		this.templateID = templateID;
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('MFSID', templateID);
		}
	},
					
	reloadGrid : function() {
		this.store.lastOptions = {
			params : {
				start : 0
//				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.templateID) {
			Ext.apply(this.store.lastOptions.params, {
				"MFSID" : this.templateID
			});
		}
		this.store.load(this.store.lastOptions);
	},
	
	validateDenominationsGrid : function() {
		var size = this.store.getCount();		
		if(size == 0) {
			Ext.ux.Toast.msg(_("Error"), _("Please add a denomination"),5);
			return false; // failure
		}
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var denomination = rec.data[CmFinoFIX.message.JSMFSDenominations.Entries.DenominationAmount._name];
			var productCode = rec.data[CmFinoFIX.message.JSMFSDenominations.Entries.ProductCode._name];			
			if (denomination == "" || denomination == null) {
				Ext.ux.Toast.msg(_("Error"), _("Please enter denomination at row: ") + (i+1),5);
				return false; // failure				
			}
			if (productCode == "" || productCode == null ) {
				Ext.ux.Toast.msg(_("Error"), _("Please enter product code at row: ") + (i+1),5);
				return false; // failure				
			}
		}				
		return true; //success
	}
});

Ext.reg("mfsdenominationsgrid", mFino.widget.MFSDenominationsGrid);
