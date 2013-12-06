/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.ChargePricingGridView = function(config) {
	var localConfig = Ext.apply( {}, config);

	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSChargePricing);
	}

	localConfig = Ext.apply(localConfig, {
		dataUrl : "fix.htm",
		loadMask : true,
		frame: true,
		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns : [
				{
					header : _("Is Default"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.IsDefault._name,
					width : 55,
		            renderer: function(value) {
						if (value) {
							return "True";
						} else {
							return "False";
						}
					}
				},		           
				{
					header : _("Minimum Tx. Limit"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MinAmount._name,
					width : 100
				},
				{
					header : _("Maximum Tx. Limit"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MaxAmount._name,
					width : 100
				},
				{
					header : _("Charge"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.Charge._name,
					width : 100
				},
				{
					header : _("Min Charge"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MinCharge._name,
					width : 100
				},
				{
					header : _("Max Charge"),
					dataIndex : CmFinoFIX.message.JSChargePricing.Entries.MaxCharge._name,
					width : 100
				}
				]
	});

	mFino.widget.ChargePricingGridView.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargePricingGridView, Ext.grid.GridPanel, {
	
	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},

	setParentTemplateData : function(templateID) {
		this.templateID = templateID;
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
	}
});

Ext.reg("chargepricinggridview", mFino.widget.ChargePricingGridView);
