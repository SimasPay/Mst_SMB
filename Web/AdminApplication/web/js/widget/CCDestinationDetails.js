/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CCDestinationDetails = function(config) {
	var localConfig = Ext.apply( {}, config);

	localConfig = Ext.applyIf(localConfig, {
						// id : "ccreviwerdetails",
						layout : 'form',
						frame : true,
						items : [
								{
									xtype : "displayfield",
									fieldLabel : _('ID'),
									anchor : '75%',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.ID._name
								},
								{
									xtype : 'displayfield',
									fieldLabel : _('SubsciiberID'),
									anchor : '75%',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.SubscriberID._name
								},
								{
									xtype : 'displayfield',
									fieldLabel : _('MDN'),
									anchor : '75%',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.DestMDN._name
								},
								{
									xtype : 'displayfield',
									fieldLabel : _('Status'),
									anchor : '75%',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.CCMDNStatusText._name
								},
								{
									xtype : "displayfield",
									fieldLabel : _('Last Modified By'),
									anchor : '75%',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.UpdatedBy._name
								},
								{
									xtype : "displayfield",
									fieldLabel : _('Last Modified on'),
									anchor : '100%',
									renderer : 'date',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.LastUpdateTime._name
								},

								{
									xtype : "displayfield",
									anchor : '100%',
									fieldLabel : _('Create on'),
									renderer : 'date',
									name : CmFinoFIX.message.JSCreditCardDestination.Entries.CreateTime._name
								} ]
					});

	mFino.widget.CCDestinationDetails.superclass.constructor.call(this,
			localConfig);
};

Ext.extend(mFino.widget.CCDestinationDetails, Ext.form.FormPanel, {
	initComponent : function() {
		this.labelWidth = 120;
		this.labelPad = 20;
		mFino.widget.CCDestinationDetails.superclass.initComponent.call(this);
	},

	setRecord : function(record) {
		this.getForm().reset();
		this.record = record;
		this.getForm().loadRecord(record);
		this.getForm().clearInvalid();
	},

	setStore : function(store) {
		if (this.store) {
			this.store.un("update", this.onStoreUpdate, this);
		}
		this.store = store;
		this.store.on("update", this.onStoreUpdate, this);
	},

	onStoreUpdate : function() {
		this.setRecord(this.record);
	}
});

Ext.reg("ccreviewerDetails", mFino.widget.CCDestinationDetails);
