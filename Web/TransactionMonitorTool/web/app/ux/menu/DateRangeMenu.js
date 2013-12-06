Ext.define('Mfino.ux.menu.DateRangeMenu',{
	extend: 'Ext.menu.Menu',
	cls:'x-date-menu',
	initComponent: function(){
		this.callParent(arguments);
		this.plain = true;
		var dp = Ext.create('Mfino.ux.DateRangePicker');
		this.relayEvents(dp, ["select"]);
	        dp.on("select", function(){this.hide();}, this);
		this.add(dp);		
	}
});
