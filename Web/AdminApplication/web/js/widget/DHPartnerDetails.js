/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DHPartnerDetails = function (config)
{

	var localConfig = Ext.apply({}, config);
	localConfig = Ext.applyIf(localConfig, {
        id : "dHPartnerDetails",
        columnWidth: 0.5,
		labelWidth : 130
	});

	mFino.widget.DHPartnerDetails.superclass.constructor.call(this, localConfig);

}

Ext.extend(mFino.widget.DHPartnerDetails , mFino.widget.ServicePartnerDetails, {
    initComponent : function () {
        mFino.widget.DHPartnerDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
		this.store.on("load", this.onStoreLoad, this);
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    },

	onStoreLoad: function(){
		if((this.store) && (this.store.getCount() > 0)){
			this.record = this.store.getAt(0);
			this.setRecord(this.record);
		}		
	}
});

Ext.reg("DHPartnerDetails", mFino.widget.DHPartnerDetails);