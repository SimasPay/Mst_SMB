/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.IPMappingGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	var commisionEditor = new Ext.form.NumberField( {
		allowNegative : false,
		allowDecimals : true,
		decimalPrecision : 2,
		maxValue : 100
	});

	var ipAddressEditor = 	new Ext.form.TextField({
            allowBlank: false,
            xtype: 'textfield',
            vtype: 'IntegrationIPAddress',
            width: 100
         });

	
	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	}
	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSIPMapping);
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
				Ext.MessageBox.confirm(_('Remove IP Address?'), _('Do you want to remove IPAddress at row: ' 
						+ (rowIndex+1) + ' ?'),
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
			text : _('Add IPAddress'),
			handler : this.onAdd.createDelegate(this)
		}],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns : [
		        this.rowDelete,
				{
					header : _("IP Address"),
					dataIndex : CmFinoFIX.message.JSIPMapping.Entries.IPAddress._name,
					width : 200,
		            editor: ipAddressEditor		          
				}
				]
	});

	mFino.widget.IPMappingGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.IPMappingGrid, Ext.grid.EditorGridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.IPMappingGrid.superclass.initComponent.call(this);

	},
	
	onAdd : function() {
		var record = new this.store.recordType();
		var size = this.store.getCount();
		record.data[CmFinoFIX.message.JSIPMapping.Entries.ID._name] = -1;
		if (this.templateID) {
			record.data[CmFinoFIX.message.JSIPMapping.Entries.IntegrationID._name] = this.templateID;
		}
		this.store.add(record);
	},

	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},

	validateIPMappingGrid: function() {
		size = this.store.getCount();
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var ipAddress = rec.data[CmFinoFIX.message.JSIPMapping.Entries.IPAddress._name];
			if (ipAddress == "" || ipAddress == null) {
				Ext.ux.Toast.msg(_("Error"), _("Please enter a valid IP Address at row: ") + (i+1),3);
				return -1; // failure				
			}			
		}
		return 0; //success
	},

	setTemplateID : function(templateID) {
		this.templateID = templateID;
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('IntegrationID', templateID);
		}
	},
					
	reloadGrid : function(integrationID) {
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.templateID) {
			Ext.apply(this.store.lastOptions.params, {
				"IntegrationID" : this.templateID
			});
		}
		this.store.load(this.store.lastOptions);
	}
	
});

Ext.reg("ipMappingGrid", mFino.widget.IPMappingGrid);
