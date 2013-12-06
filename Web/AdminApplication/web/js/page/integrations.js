/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.integrations = function(config){

	var detailsForm = new mFino.widget.IntegrationDetails(Ext.apply({
		height: 170
	}, config));

	var searchBox = new mFino.widget.IntegrationsSearchForm(Ext.apply({
		height : 80
	}, config));

	var InterationMappingEditWindow = new mFino.widget.FormWindowForIntegrations(Ext.apply({
		form : new mFino.widget.IntegrationForm(config),
		mode : 'edit',
		modal:true,
		itemId: 'integrations.edit',
		title : _('Edit Integration'),
		layout:'fit',
		floating: true,
		width:400,
		height:410,
		plain:true
	},config));

	var InterationMappingViewWindow = new mFino.widget.FormWindowForIntegrations(Ext.apply({
		form : new mFino.widget.IntegrationForm(config),
		mode : 'view',
		modal:true,
		isEditable: false,
		itemId: 'integrations.view',
		title : _('View Integration'),
		layout:'fit',
		floating: true,
		width:400,
		height:410,
		plain:true
	},config));

	var grid = new mFino.widget.IntegrationsGrid(Ext.apply({
		height: 420,
		title : _('Integrations Search Results')
	}, config));

	var integrationAddWindow = new mFino.widget.FormWindowForIntegrations(Ext.apply({
		form : new mFino.widget.IntegrationForm(config),
		mode : 'add',
		modal:true,
		title : _('Add Integration'),
		layout:'fit',
		floating: true,
		width:400,
		height:410,
		plain:true
	},config));
	
	grid.action.on({
		action:function(grid, record, action, row, col) 
		{
			if(action === 'mfino-button-edit' && record !== null){
				InterationMappingEditWindow.show();
				InterationMappingEditWindow.setRecord(record);
				InterationMappingEditWindow.setStore(grid.store);
			}else if (action === 'mfino-button-View' && record !== null)
			{
				InterationMappingViewWindow.show();
				InterationMappingViewWindow.setRecord(record);
				InterationMappingViewWindow.form.disableGrid();
			}
		}
	});

	var panelCenter = new Ext.Panel({
		layout: 'anchor',
		items : [
		         {
		        	 autoScroll : true,
		        	 anchor : "100%",
		        	 tbar : [
		        	         '<b class= x-form-tbar>' + _('Integration Details') + '</b>',
		        	         '->',
		        	         
		        	         {
		        	                iconCls: 'mfino-button-key',
		        	                tooltip : _('Reset Authentication Key'),
		        	                handler : function(){
		        	                    if(!detailsForm.record){
		        	                        Ext.MessageBox.alert(_("Alert"), _("No Integration selected!"));
		        	                    } if(!detailsForm.record.get(CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsAuthenticationKeyEnabled._name)){
		        	                    	 Ext.MessageBox.alert(_("Info"), _("Authentication Key is not enabled for this Integration"));
		        	                    }else{
		        	                    	var integrationName=detailsForm.record.get(CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IntegrationName._name);
		        	                    	Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Reset Authentication Key for Integration "+integrationName+"?"),
		        	                            function(btn){
		        	                                if(btn !== "yes"){
		        	                                    return;
		        	                                }
		        	                                var msg = new CmFinoFIX.message.JSResetAuthenticationKeyForIntegration();
		        	                                msg.m_pResetAuthenticationKey = true;
		        	                                msg.m_pIntegrationID = detailsForm.record.get(CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.ID._name);
		        	                                var params = mFino.util.showResponse.getDisplayParam();
		        	                                mFino.util.fix.send(msg, params);
		        	                            }, this);
		        	                    }
		        	                }
		        	          },
		        	         {
		        	        	 iconCls: 'mfino-button-add',
		        	        	 tooltip : _('Add Integration'),
		        	        	 text : _('New'),
		        	        	 itemId: 'integrations.add',
		        	        	 handler: function(){
		        	        		 var record = new grid.store.recordType();
		        	        		 integrationAddWindow.show();
		        	        		 integrationAddWindow.setRecord(record);
		        	        		 integrationAddWindow.setStore(grid.store);
		        	        	 }
		        	         }
		        	         ],
		        	         items: [ detailsForm ]
		         }
		         ]
	});


	searchBox.on("search", function(values){
		detailsForm.getForm().reset();
		if(values.Language === "undefined" )
		{
			values.Language =null;
		}
		if(values.NotificationMethod === "undefined")
		{
			values.NotificationMethod =null;
		}
		if(values.NotificationCodeName === "undefined")
		{
			values.NotificationCodeName =null;
		}
		grid.store.baseParams = values;
		grid.store.lastOptions = {
				params : {
					start : 0,
					limit : CmFinoFIX.PageSize.Default
				}
		};
		Ext.apply(grid.store.lastOptions.params, values);
		grid.store.load(grid.store.lastOptions);
	});

	grid.on("defaultSearch", function() {
		searchBox.searchHandler();
	});

	grid.selModel.on("rowselect", function(sm, rowIndex, record){
		detailsForm.setRecord(record);        
		detailsForm.setStore(grid.store);
	});

	var panel = new Ext.Panel({
		broder: false,
		width : 1020,
		items: [ panelCenter, searchBox, grid]
	});
	return panel;
};
