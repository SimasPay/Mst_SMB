Ext.ns("mFino.widget");

mFino.widget.DHDCTTabPanel = function (config)
{
	mFino.widget.DHDCTTabPanel.superclass.constructor.call(this, config);
}

Ext.extend(mFino.widget.DHDCTTabPanel, Ext.TabPanel, {

	initComponent : function () {
		var config = this.initialConfig;
		this.activeTab = 0;

		this.dctRestrictionsGrid = new mFino.widget.DCTRestrictionsGrid(Ext.apply({
			layout: "fit",            
			height: 470
		}, config));


		this.items = [
			{
				 title: _('Permissions'),
				layout : "fit",
				itemId: "dct.restrictions.tab1",
				items:  [this.dctRestrictionsGrid]
			}
		];

		mFino.widget.DHDCTTabPanel.superclass.initComponent.call(this);     
		
		this.dctRestrictionsGrid.store.on("save", function(a,b,c) {
			if(a.reader.ResponseMsg.m_psuccess == true){
				Ext.ux.Toast.msg(_("Info"), "DCT Permissions saved successfully.", 2);				
			}
		});
		
//		this.dctRestrictionsGrid.store.on("exception", function(a,b,c) {
//			Ext.ux.Toast.msg(_("Info"), "Exception saving DCT Restrictions.", 2);
//		});

	},
	
	setValues: function(values){
		this.values = values;
		this.dctRestrictionsGrid.setValues(values);
	},

	onSaveSucess: function(s,b,n){
		Ext.ux.Toast.msg(_("Info"), "DCT Permissions saved successfully.");
	}
});

Ext.reg("DHDCTTabPanel", mFino.widget.DHDCTTabPanel);