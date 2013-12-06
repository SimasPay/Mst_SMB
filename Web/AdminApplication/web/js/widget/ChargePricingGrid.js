/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.ChargePricingGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	var numberEditor = new Ext.form.NumberField( {
		allowNegative : false,
		allowDecimals : true,
		allowBlank: false,
		decimalPrecision : 2
	});
	
	var textEditor = new Ext.form.TextField({
		allowBlank: false,
		vtype: 'chargeDefValidation'
	});
	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSChargePricing);
	}
	
	var isDefaultColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _('Is Default'),
				dataIndex : CmFinoFIX.message.JSChargePricing.Entries.IsDefault._name,
				width : 55
			});

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
				Ext.MessageBox.confirm(_('Delete Charge Pricing?'), _('Do you want to delete the pricing range [' 
						+ record.data[CmFinoFIX.message.JSChargePricing.Entries.MinAmount._name]+' - '
						+ record.data[CmFinoFIX.message.JSChargePricing.Entries.MaxAmount._name]+ '] ?'),
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
		plugins : [ this.rowDelete, isDefaultColumn ],
		tbar : [{
			iconCls : 'mfino-button-add',
			text : _('Add Pricing Range'),
			handler : this.onAdd.createDelegate(this)
		}],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),
		
		listeners: {			
			beforeedit : function(e) {
				new Ext.ToolTip({
		            closable:true,
		            hideDelay : 10000,
		            dismissDelay : 10000,
		            padding: '0 0 0 0',		            
		            width: 300,		            
		            html: 	'Charge should be of pattern :<br/>' +
							'* d +/- amount*d% <br/>' +
							'* (amount +/- d) +/- (amount +/- d1)*d2%<br/>' +
							'* (amount +/- d) +/- amount*d1%<br/>' +
							'* amount +/- d<br/>' +
							'* d<br/>' +
							'* amount*d%<br/>' +
							'* (amount +/- d)*d1%<br/>' +
							'* (amount +/- d)*d1% +/- d2<br/>' +
							'* (amount +/- d)*d1% +/- (amount +/- d2)<br/>' +
							'* amount*d% +/- d1<br/>' +
							'* amount*d% +/- (amount +/- d1)<br/>'
		        }).showAt([this.el.getX() + 430, this.el.getY()]);
			},
			scope : this
	     },

		columns : [
		        this.rowDelete,
		        isDefaultColumn,
				{
					header : _("Minimum Tx. Limit"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MinAmount._name,
					width : 100,
					editor : numberEditor
				},
				{
					header : _("Maximum Tx. Limit"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MaxAmount._name,
					width : 100,
					editor : numberEditor
				},
				{
					header : _("Charge"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.Charge._name,
					width : 100,
					editor: textEditor
				},
				{
					header : _("Min Charge"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MinCharge._name,
					width : 100,
					editor: textEditor
				},
				{
					header : _("Max Charge"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MaxCharge._name,
					width : 100,
					editor: textEditor
				}
				]
	});

	mFino.widget.ChargePricingGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargePricingGrid, Ext.grid.EditorGridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.ChargePricingGrid.superclass.initComponent.call(this);

	},
	
	onAdd : function() {
		var record = new this.store.recordType();
		record.disable = false;
		var size = this.store.getCount();
		record.data[CmFinoFIX.message.JSChargePricing.Entries.ID._name] = -1;
		if (this.templateID) {
			record.data[CmFinoFIX.message.JSChargePricing.Entries.ChargeDefinitionID._name] = this.templateID;
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
			rec.set('ChargeDefinitionID', templateID);
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
				"ChargeDefinitionID" : this.templateID
			});
		}
		this.store.load(this.store.lastOptions);
	},
	
	checkPricingranges : function() {
		size = this.store.getCount();
		if (size === 0) {
			Ext.ux.Toast.msg(_("Error"), _("Please add the default Charge Pricing"),5);
			return 0; // failure
		}
		var lst = new Array(size-1);
		var defaultCount = 0;
		var c = 0;
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var minLmt = rec.data[CmFinoFIX.message.JSChargePricing.Entries.MinAmount._name];
			var maxLmt = rec.data[CmFinoFIX.message.JSChargePricing.Entries.MaxAmount._name];
			var charge = rec.data[CmFinoFIX.message.JSChargePricing.Entries.Charge._name];
			var minCharge = rec.data[CmFinoFIX.message.JSChargePricing.Entries.MinCharge._name];
			var maxCharge = rec.data[CmFinoFIX.message.JSChargePricing.Entries.MaxCharge._name];
			if(!charge || !minCharge || !maxCharge) {
				Ext.ux.Toast.msg(_("Error"), _("Enter the charge values for pricing range at row: ") + (i+1));
				return 0; // failure				
			}
			var isDefault = rec.data[CmFinoFIX.message.JSChargePricing.Entries.IsDefault._name];
			
			if ((!isDefault && ((minLmt===null) || (typeof(minLmt) === "undefined")
					|| (maxLmt===null) || (typeof(maxLmt) === "undefined")))
					|| (charge===null) || (typeof(charge) === "undefined")) {
				Ext.ux.Toast.msg(_("Error"), _("Enter the data for pricing range at row: ") + (i+1),5);
				return 0; // failure				
			}
			
			if (isDefault) {
				defaultCount++;
			}
			
			if (!(isDefault) && (maxLmt <= minLmt)) {
				Ext.ux.Toast.msg(_("Error"), _("Maximum limit is Less than the Minimum limit for pricing range at row: ") + (i+1),5);
				return 0; // failure
			}
			
			
			
			if (!isDefault) {
				lst[c] = new Array(2);
				lst[c][0] = minLmt;
				lst[c][1] = maxLmt;
				c++;
			}
		}
		
		if (defaultCount <= 0) {
			Ext.ux.Toast.msg(_("Error"), _("Atleast one default Charge Pricing should be defined"),5);
			return 0; // failure				
		} else if (defaultCount > 1) {
			Ext.ux.Toast.msg(_("Error"), _("Only one default Charge Pricing should be defined"),5);
			return 0; // failure
		}
		
		var slst = mFino.util.fix.sort(lst);
		size = slst.length;
		for (var i=0; i<size; i++ ){
			var minLmt = slst[i][0];
			var maxLmt = slst[i][1];
			
			// Checking for the duplicate ranges
			for (var j=i+1; j<size; j++) {
				var minLmt_j = slst[j][0];
				var maxLmt_j = slst[j][1];				
				if (minLmt_j <= minLmt || maxLmt_j <= minLmt
						|| minLmt_j <= maxLmt || maxLmt_j <= maxLmt) {
					Ext.ux.Toast.msg(_("Error"), _("Pricing ranges are not defined properly."),5);
					return 0; // failure						
				}
			}
			
			// checking whether all the pricing ranges defined properly or not.
			if ((i<size-1) && (slst[i+1][0] - slst[i][1]) != 1) {
				Ext.ux.Toast.msg(_("Error"), _("Pricing ranges are not defined properly."),5);
				return 0; // failure	
			}
		}
		return 1; //success
	}
});

Ext.reg("chargepricinggrid", mFino.widget.ChargePricingGrid);
