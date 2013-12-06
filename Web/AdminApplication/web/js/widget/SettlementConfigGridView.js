/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SettlementConfigGridView = function(config) {
	var localConfig = Ext.apply( {}, config);

	

	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSServiceSettlementConfig);
	}

	localConfig = Ext.apply(localConfig, {
		dataUrl : "fix.htm",
		loadMask : true,

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns : [
				{
					header : _("Settlement Template"),
					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.SettlementName._name,
					width : 140
		          },	
				{
					header : _("Is Default"),
					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.IsDefault._name,
					width : 55,
		            renderer: function(value) {
						if (value) {
							return "True";
						} else {
							return "False";
						}
					}
//				},	
//				{
//					header : _("Start Date"),
//					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.StartDate._name,
//					width : 100,
//					renderer: formatDate
//				},
//				{
//					header : _("End Date"),
//					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.EndDate._name,
//					width : 100,
//					renderer: formatDate
				}
				]
	});

	mFino.widget.SettlementConfigGridView.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SettlementConfigGridView, Ext.grid.GridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.SettlementConfigGridView.superclass.initComponent.call(this);

	},

	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},

	setParentData : function(partnerServiceID, partnerId) {
		this.partnerServiceID = partnerServiceID;
		this.partnerId = partnerId;
		var size = this.store.getCount();
	},
					
	reloadGrid : function() {
		
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.partnerServiceID) {
			Ext.apply(this.store.lastOptions.params, {
				"PartnerServicesID" : this.partnerServiceID
			});
		}
		this.store.load(this.store.lastOptions);
	}
	
	

});

Ext.reg("settlementconfiggridview", mFino.widget.SettlementConfigGridView);
