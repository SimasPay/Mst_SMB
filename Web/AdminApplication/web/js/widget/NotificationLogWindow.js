/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.NotificationLogWindow = function (config){
	var localConfig = Ext.apply({}, config);
	localConfig = Ext.applyIf(localConfig, {
		modal:true,
		layout:'fit',
		floating: true,
		resizable : false,
		width: 810,
		height:550,
		closable:true,
		plain:true
	});
	mFino.widget.NotificationLogWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.NotificationLogWindow, Ext.Window, {
	initComponent : function(){

		this.items = [{
			tbar : [
			        '<b class= x-form-tbar>' + _('Notification Log') + '</b>',
			        '->',
			        {
			        	iconCls : "mfino-button-reverse",
			        	tooltip : _('Resend Notification'),
			        	text : _('Resend Notification'),
			        	id : 'resendNotification',
			        	itemId: 'notification.resend',
			        	handler : function(){
			        		if(!this.grid.selModel.hasSelection()){
			        			Ext.MessageBox.alert(_("Alert"), _("No Notification selected!"));
			        		} else{
								Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Resend the selected Notifications?"),
								function(btn){
									if(btn !== "yes"){
										return;
									}
									var msg = new CmFinoFIX.message.JSResendNotification();
									var listOfNotificationLogIDs = "";
									for(var i = 0; i< this.grid.selModel.getCount(); i++) {                
										listOfNotificationLogIDs += this.grid.selModel.selections.get(i).get(CmFinoFIX.message.JSNotificationLog.Entries.NotificationLogID._name) + ",";                
									}
									msg.m_pListOfNotificationLogIDs = listOfNotificationLogIDs;
									var params = mFino.util.showResponse.getDisplayParam();
									mFino.util.fix.send(msg, params);                          
								}, this);
			        		}
			        	},
			        	scope: this
			        }
			        ],
			        layout: "fit",
			        items: [this.grid ]
		}
		];
		mFino.widget.NotificationLogWindow.superclass.initComponent.call(this);
	},

	close : function(){
		this.hide();
	},
	setStore : function(store){
		this.store = store;
	},
	setTitle : function(title){
		this.title = title;
	}
});