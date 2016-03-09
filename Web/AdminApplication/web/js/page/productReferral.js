/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.productReferral = function(config){

	var detailsForm = new mFino.widget.ProductReferralDetails(Ext.apply({
		height: 120
	}, config));

	var searchBox = new mFino.widget.ProductReferralSearchForm(Ext.apply({
		height : 80
	}, config));

	/*var ProductReferralEditWindow = new mFino.widget.FormWindow(Ext.apply({
		form : new mFino.widget.ProductReferralEditForm(config),
		mode : 'edit',
		modal:true,
		title : _('Edit ProductReferral'),
		layout:'fit',
		floating: true,
		width:400,
		height:200,
		plain:true
	},config));*/

	var ProductReferralViewWindow = new mFino.widget.FormWindow(Ext.apply({
		form : new mFino.widget.ProductReferralViewForm(config),
		mode : 'view',
		modal:true,
		title : _('View Product Referral'),
		layout:'fit',
		floating: true,
		width:500,
		height:400,
		plain:true
	},config));

	var grid = new mFino.widget.ProductReferralGrid(Ext.apply({
		height: 470,
		title : _('Product Referral Search Results')
	}, config));

	/*var productReferralAddWindow = new mFino.widget.FormWindow(Ext.apply({
		form : new mFino.widget.ProductReferralAddForm(config),
		mode : 'add',
		modal:true,
		title : _('Add ProductReferral'),
		layout:'fit',
		floating: true,
		width:400,
		height:200,
		plain:true
	},config));*/

	grid.action.on({
		action:function(grid, record, action, row, col) 
		{
			/*if(action === 'mfino-button-edit' && record !== null){
				ProductReferralEditWindow.show();
				ProductReferralEditWindow.setRecord(record);
				ProductReferralEditWindow.setStore(grid.store);
			}else*/ if (action === 'mfino-button-View' && record !== null)
			{
				ProductReferralViewWindow.show();
				ProductReferralViewWindow.setRecord(record);
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
		        	         '<b class= x-form-tbar>' + _('Product Referral Details') + '</b>',
		        	         '->'
		        	         /*{
		        	        	 iconCls: 'mfino-button-pocket-add',
		        	        	 tooltip : _('Add SystemParameter'),
		        	        	 text : _('New'),
		        	        	 itemId: 'ProductReferral.add',
		        	        	 handler: function(){
		        	        		 var record = new grid.store.recordType();
		        	        		 // systemParameterAddWindow.setTitle(_("Add System Parameter"));
		        	        		 // systemParameterAddWindow.setMode("add");
		        	        		 systemParameterAddWindow.show();
		        	        		 systemParameterAddWindow.setRecord(record);
		        	        		 systemParameterAddWindow.setStore(grid.store);
		        	        	 }
		        	         }*/
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
