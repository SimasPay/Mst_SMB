/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PReferralDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.PReferralDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PReferralDetails, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 160;
        this.labelPad = 20;
        this.autoScroll = true;
        this.items = [ {
            layout:'column',
            items : [
            {
                columnWidth:0.5,
                layout: 'form',
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
        }
        ];

        mFino.widget.PReferralDetails.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
    }
});

Ext.reg("PReferralDetails", mFino.widget.PReferralDetails);
