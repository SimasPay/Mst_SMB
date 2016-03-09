/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ProductReferralDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 100,
            items : [
             {
            	 xtype : "displayfield",
            	 fieldLabel :_("ID"),
            	 name: CmFinoFIX.message.JSProductReferral.Entries.ID._name
             },
            
        	{
            	xtype : "displayfield",
            	fieldLabel :_("AgentMDN"),
            	name: CmFinoFIX.message.JSProductReferral.Entries.AgentMDN._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("SubscriberMDN"),
        		name: CmFinoFIX.message.JSProductReferral.Entries.SubscriberMDN._name
        	},
			
			{
            	xtype : "displayfield",
            	fieldLabel :_("Email"),
            	name: CmFinoFIX.message.JSProductReferral.Entries.Email._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("ProductDesired"),
        		name: CmFinoFIX.message.JSProductReferral.Entries.ProductDesired._name
        	},
			
			{
            	xtype : "displayfield",
            	fieldLabel :_("FullName"),
            	name: CmFinoFIX.message.JSProductReferral.Entries.FullName._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Others"),
        		name: CmFinoFIX.message.JSProductReferral.Entries.Others._name
        	}
			
			
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 100,
            items : [
            {
            	xtype : "displayfield",
            	fieldLabel :_("Create Time"),
            	name: CmFinoFIX.message.JSProductReferral.Entries.CreateTime._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Created By"),
            	name: CmFinoFIX.message.JSProductReferral.Entries.CreatedBy._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Last Update Time"),
        		name: CmFinoFIX.message.JSProductReferral.Entries.LastUpdateTime._name
        	},
        	{
    			xtype : "displayfield",
    			fieldLabel :_("Updated By"),
    			name: CmFinoFIX.message.JSProductReferral.Entries.UpdatedBy._name
    		}
            ]
        }
    ]
    });

    mFino.widget.ProductReferralDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.ProductReferralDetails, Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.ProductReferralDetails.superclass.initComponent.call(this);
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

Ext.reg("productreferraldetails", mFino.widget.ProductReferralDetails);

