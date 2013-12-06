/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SharePartnerGridView = function(config) {
	var localConfig = Ext.apply( {}, config);
	
	var typeEditor = new mFino.widget.EnumDropDown({
		addEmpty : true,
		enumId : CmFinoFIX.TagID.ShareHolderType
	});	
	
	var partnerEditor = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        pageSize : 10,
        id: "sharePartnerview.form.partner",
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPartner),
        RPCObject : CmFinoFIX.message.JSPartner,
        displayField: CmFinoFIX.message.JSPartner.Entries.TradeName._name,
        valueField : CmFinoFIX.message.JSPartner.Entries.ID._name	
	});

	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	}
	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSharePartner);
	}

	localConfig = Ext.apply(localConfig, {
		dataUrl : "fix.htm",
		loadMask : true,
		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns : [
				{
					header : _("Share Holder Type"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.ShareHolderType._name,
					width : 125,
					editor: typeEditor,
		            renderer: Ext.util.Format.comboRenderer(typeEditor)
				},
				{
					header : _("Partner Name"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.PartnerID._name,
					width : 125,
		            renderer: Ext.util.Format.comboRenderer(partnerEditor)
				},
				{
					header : _("Share"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.ActualSharePercentage._name,
					width : 75
				}/*,
				{
					header : _("Min Share"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.MinSharePercentage._name,
					width : 75
				},
				{
					header : _("Max Share"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.MaxSharePercentage._name,
					width : 75
				}*/
				]
	});

	mFino.widget.SharePartnerGridView.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SharePartnerGridView, Ext.grid.GridPanel, {
	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},

	setTemplateID : function(templateID) {
		this.templateID = templateID;
	},
					
	reloadGrid : function() {
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.templateID) {
			Ext.apply(this.store.lastOptions.params, {
				"TransactionChargeID" : this.templateID
			});
		}
		this.store.load(this.store.lastOptions);

		var sr_combo = Ext.getCmp("sharePartnerview.form.partner");
    	sr_combo.store.reload({
    	});
	}
});

Ext.reg("sharepartnergridview", mFino.widget.SharePartnerGridView);
