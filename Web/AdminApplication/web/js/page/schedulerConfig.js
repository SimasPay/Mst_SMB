/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.schedulerConfig = function(config){

	var detailsForm = new mFino.widget.schedulerConfigDetails(Ext.apply({
		height: 170
	}, config));


	var schedulerConfigMappingEditWindow = new mFino.widget.FormWindowForSchedulerConfig(Ext.apply({
		form : new mFino.widget.schedulerConfigAddForm(config),
		mode : 'edit',
		modal:true,
		itemId: 'schedulerConfigs.edit',
		title : _('Edit schedulerConfig'),
		layout:'fit',
		floating: true,
		width:400,
		height:410,
		plain:true
	},config));

	var schedulerConfigMappingViewWindow = new mFino.widget.FormWindowForSchedulerConfig(Ext.apply({
		form : new mFino.widget.schedulerConfigAddForm(config),
		mode : 'view',
		modal:true,
		isEditable: false,
		itemId: 'schedulerConfigs.view',
		title : _('View schedulerConfig'),
		layout:'fit',
		floating: true,
		width:400,
		height:410,
		plain:true
	},config));



	var schedulerConfigAddWindow = new mFino.widget.FormWindowForSchedulerConfig(Ext.apply({
		form : new mFino.widget.schedulerConfigAddForm(config),
		mode : 'add',
		modal:true,
		title : _('Add schedulerConfig'),
		layout:'fit',
		floating: true,
		width:400,
		height:410,
		plain:true
	},config));
	
		var searchBox = new mFino.widget.schedulerConfigSearchForm(Ext.apply({
		height : 80
	}, config));
		var grid = new mFino.widget.schedulerConfigGrid(Ext.apply({
		height: 420,
		title : _('schedulerConfigs Search Results')
	}, config));
	
	grid.action.on({
		action:function(grid, record, action, row, col) 
		{
			if(action === 'mfino-button-edit' && record !== null){
				schedulerConfigMappingEditWindow.show();				
				schedulerConfigMappingEditWindow.setRecord(record);
				schedulerConfigMappingEditWindow.form.disableAllTabItems();
				schedulerConfigMappingEditWindow.form.enableSingleTabItems();
				schedulerConfigMappingEditWindow.setStore(grid.store);
			}else if (action === 'mfino-button-View' && record !== null)
			{
				schedulerConfigMappingViewWindow.show();
				schedulerConfigMappingViewWindow.setRecord(record);
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
		        	         '<b class= x-form-tbar>' + _('schedulerConfig Details') + '</b>',
		        	         '->',
		        	      		        	         {
		        	        	 iconCls: 'mfino-button-add',
		        	        	 tooltip : _('Add schedulerConfig'),
		        	        	 text : _('New'),
		        	        	 itemId: 'schedulerConfigs.add',
		        	        	 handler: function(){
		        	        		 var record = new grid.store.recordType();
		        	        		 schedulerConfigAddWindow.show();
		        	        		 schedulerConfigAddWindow.setRecord(record);
		        	        		 schedulerConfigAddWindow.setStore(grid.store);
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
