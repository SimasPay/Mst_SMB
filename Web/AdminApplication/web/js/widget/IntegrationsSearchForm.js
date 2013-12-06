/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.IntegrationsSearchForm = function (config) {
	var localConfig = Ext.apply({}, config);

	localConfig = Ext.applyIf(localConfig, {
		labelPad : 10,
		layout:'column',
		labelWidth : 70,
		frame:true,
		title: _('Integrations'),
		bodyStyle:'padding:5px 5px 0',

		items: [

{
	columnWidth:0.18,
	layout:'form',
	labelWidth:60,
	items:[
	       {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('Institution ID'), 
	    	   labelSeparator : '', 	    	    
	    	   anchor : '98%', 
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSIntegrationPartnerMapping), 
	    	   displayField: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.InstitutionID._name, 
	    	   valueField : CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.InstitutionID._name, 
	    	   hiddenName : CmFinoFIX.message.JSIntegrationPartnerMapping.InstitutionIDSearch._name, 
	    	   name: CmFinoFIX.message.JSIntegrationPartnerMapping.InstitutionIDSearch._name, 
	    	   listeners   : { 
	    		   specialkey: this.enterKeyHandler.createDelegate(this) 
	    	   } 
	       }
	       ]
},
{
	columnWidth:0.02,
	layout:'form',
	items:[
	       {
	    	   xtype:'displayfield',
	    	   anchor:'50%'
	       }
	       ]
},

{
	columnWidth:0.25,
	layout:'form',
	labelWidth:80,
	items:[
	       {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('Integration Name'), 
	    	   labelSeparator : '', 
	    	   anchor : '98%', 
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSIntegrationPartnerMapping), 
	    	   displayField: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IntegrationName._name, 
	    	   valueField : CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IntegrationName._name, 
	    	   hiddenName : CmFinoFIX.message.JSIntegrationPartnerMapping.IntegrationNameSearch._name, 
	    	   name: CmFinoFIX.message.JSIntegrationPartnerMapping.IntegrationNameSearch._name, 
	    	   listeners   : { 
	    		   specialkey: this.enterKeyHandler.createDelegate(this) 
	    	   } 
	       }
	       ]
},
{
	columnWidth:0.02,
	layout:'form',
	items:[
	       {
	    	   xtype:'displayfield',
	    	   anchor:'50%'
	       }
	       ]
},
{
	columnWidth:0.16,
	layout:'form',
	labelWidth:50,
	items:[
	       {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('Partner ID'), 
	    	   labelSeparator : '', 
	    	   anchor : '98%', 
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner), 
	    	   displayField: CmFinoFIX.message.JSPartner.Entries.ID._name, 
	    	   valueField : CmFinoFIX.message.JSPartner.Entries.ID._name, 
	    	   hiddenName : CmFinoFIX.message.JSIntegrationPartnerMapping.PartnerSearch._name, 
	    	   name: CmFinoFIX.message.JSIntegrationPartnerMapping.PartnerSearch._name, 
	    	   listeners   : { 
	    		   specialkey: this.enterKeyHandler.createDelegate(this) 
	    	   } 
	       }
	       ]
},
{
	columnWidth:0.02,
	layout:'form',
	items:[
	       {
	    	   xtype:'displayfield',
	    	   anchor:'50%'
	       }
	       ]
},

{
	columnWidth:0.18,
	layout:'form',
	labelWidth:60,
	items:[
	       {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('MFSBiller ID'), 
	    	   labelSeparator : '',
	    	   anchor : '98%', 
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSMFSBiller), 
	    	   displayField: CmFinoFIX.message.JSMFSBiller.Entries.ID._name, 
	    	   valueField : CmFinoFIX.message.JSMFSBiller.Entries.ID._name, 
	    	   hiddenName : CmFinoFIX.message.JSIntegrationPartnerMapping.MFSBillerSearch._name, 
	    	   name: CmFinoFIX.message.JSIntegrationPartnerMapping.MFSBillerSearch._name, 
	    	   listeners   : { 
	    		   specialkey: this.enterKeyHandler.createDelegate(this) 
	    	   } 
	       }
	       ]
},

{
	columnWidth:0.03,
	layout:'form',
	items:[
	       {
	    	   xtype:'displayfield',
	    	   anchor:'50%'
	       }
	       ]
},
{
	columnWidth:0.06,
	layout:'form',
	items:[
	       {
	    	   xtype:'button',
	    	   text:'Search',
	    	   anchor:'60%',
	    	   handler : this.searchHandler.createDelegate(this)
	       }

	       ]
},
{
	columnWidth:0.06,
	layout:'form',
	items:[
	       {
	    	   xtype: 'button',
	    	   text: _('Reset'),
	    	   anchor:'60%',
	    	   handler : this.resetHandler.createDelegate(this)
	       }

	       ]
}
]
	});

	mFino.widget.IntegrationsSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.IntegrationsSearchForm, Ext.FormPanel, {

	initComponent : function () {
		mFino.widget.IntegrationsSearchForm.superclass.initComponent.call(this);
		this.addEvents("search");
		this.on("render", function(){
        	this.reloadRemoteDropDown();
        });
	},
	
	reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
	    	if(item.getXType() == 'remotedropdown') {
	    		item.reload();
	    	}
    	});
    },

	enterKeyHandler : function (f, e) {
		if (e.getKey() === e.ENTER) {
			this.searchHandler();
		}
	},

	searchHandler : function(){
		if(this.getForm().isValid()){
			var values = this.getForm().getValues();
			this.fireEvent("search", values);
		}else{
			Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
		}
	},
	resetHandler : function(){
		this.getForm().reset();
	}
});

