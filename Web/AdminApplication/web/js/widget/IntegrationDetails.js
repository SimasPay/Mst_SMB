/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.IntegrationDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 150,
            items : [
             {
            	 xtype : "displayfield",
            	 fieldLabel :_("Integration ID"),
            	 name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.ID._name
             },
            {
            	xtype : "displayfield",
            	fieldLabel :_("Institution ID"),
            	name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.InstitutionID._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Integration Name"),
            	name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IntegrationName._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Partner ID"),
        		name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.PartnerID._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("MFSBiller ID"),
        		name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.MFSBillerId._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("List Of IPs For The Integration"),
        		name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.ListOfIPsForIntegration._name
        	}
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 160,
            items : [
             {
            	 xtype : "displayfield",
            	 fieldLabel :_("Is Authentication Key Required"),
            	 name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsAuthenticationKeyEnabled._name
             },
             {
            	 xtype : "displayfield",
            	 fieldLabel :_("Is Login Enabled"),
            	 name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsLoginEnabled._name
             },
            {
            	xtype : "displayfield",
            	 fieldLabel :_("Is AppType Check Enabled"),
            	 name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsAppTypeCheckEnabled._name
             },
             {
            	xtype : "displayfield",
            	fieldLabel :_("Create Time"),
            	name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.CreateTime._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Created By"),
            	name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.CreatedBy._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Last Update Time"),
        		name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.LastUpdateTime._name
        	},
        	{
    			xtype : "displayfield",
    			fieldLabel :_("Updated By"),
    			name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.UpdatedBy._name
    		}
            ]
        }
    ]
    });

    mFino.widget.IntegrationDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.IntegrationDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.IntegrationDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },

    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});

Ext.reg("integrationDetails", mFino.widget.IntegrationDetails);

