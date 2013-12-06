/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.NotificationLogGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl,
				CmFinoFIX.message.JSNotificationLog);
	}

	var checkboxselection = new Ext.grid.CheckboxSelectionModel({
		multiSelect: true
	});

	localConfig = Ext.applyIf(
			localConfig || {},
			{
				dataUrl : "fix.htm",
				layout : 'fit',
				width:800,
				height:411,
//				anchor : "100%, -181",
				frame : true,
				loadMask : true,
				viewConfig: { emptyText: Config.grid_no_data },
				selModel: checkboxselection,
				bbar : new Ext.PagingToolbar( {
					store : localConfig.store,
					displayInfo : true,
					pageSize : CmFinoFIX.PageSize.Default
				}),
				autoScroll : true,
				columns : [
							checkboxselection,
				           {
				        	   header: _("Notification Log ID"),
				        	   dataIndex: CmFinoFIX.message.JSNotificationLog.Entries.NotificationLogID._name
				           },						           
				           {
				        	   header : _("Reference ID"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.SctlId._name
				           },
				           {
				        	   header: _("Text"),
				        	   dataIndex: CmFinoFIX.message.JSNotificationLog.Entries.Text._name
				           },						           
				           {
				        	   header : _("Notification Code"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.NotificationCode._name
				           },
				           {
				        	   header : _("Code Name"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.NotificationCodeName._name
				           },
				           {
				        	   header: _("Notification Method"),
				        	   dataIndex: CmFinoFIX.message.JSNotificationLog.Entries.NotificationMethodText._name
				           },						           
				           {
				        	   header : _("Source Address"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.SourceAddress._name
				           },
				           {
				        	   header : _("Notification Receiver Type"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.NotificationReceiverTypeText._name
				           },
				           {
				        	   header : _("EmailSubject"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.EmailSubject._name
				           },
				           {
				        	   header : _("Count"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.Count._name
				           },
				           {
				        	   header : _("Successful Notifications Count"),
				        	   dataIndex : CmFinoFIX.message.JSNotificationLog.Entries.SuccessfulNotificationsCount._name
				           }

				           ]
			});

	mFino.widget.NotificationLogGrid.superclass.constructor.call(this, localConfig);


};

Ext.extend(mFino.widget.NotificationLogGrid, Ext.grid.GridPanel, {});


Ext.reg("notificationLogGrid", mFino.widget.NotificationLogGrid);
