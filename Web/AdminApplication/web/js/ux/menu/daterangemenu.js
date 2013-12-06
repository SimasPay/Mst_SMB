Ext.namespace('Mfino.ux.menu');

/**
 * @class Mfino.ux.menu.DateRangeMenu
 * @extends Ext.menu.Menu
 * @constructor
 * 
 * @param {Object} config The configuration options
 */
Mfino.ux.menu.DateRangeMenu = function(config) {
	Mfino.ux.menu.DateRangeMenu.superclass.constructor.call(this, config);
	this.plain = true;
	var dp = new Mfino.ux.DateRangePicker();
	this.relayEvents(dp, ["select"]);
        dp.on("select", function(){this.hide();}, this);
	this.add(dp);
	
}

Ext.extend(Mfino.ux.menu.DateRangeMenu, Ext.menu.Menu, {
	cls:'x-date-menu'
});

Ext.reg('daterangemenu', Mfino.ux.menu.DateRangeMenu);
