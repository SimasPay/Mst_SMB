/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("FIX");
FIX.FIXWriter = function(config) {
	FIX.FIXWriter.superclass.constructor.call(this, config);
};

Ext.extend(FIX.FIXWriter, Ext.data.DataWriter, {
	render: function(action, rs, params, data) {
		Ext.apply(params, data);
	},
	createRecord: function(rec) {
		return this.toHash(rec);
	},
	updateRecord: function(rec) {
		return this.toHash(rec);

	},
	destroyRecord: function(rec) {
		return rec.id;
	}
});
