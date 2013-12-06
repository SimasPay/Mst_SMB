
Ext.ns("mFino.widget");

mFino.widget.FundDefinitionDetails = function (config)
{
	var localConfig = Ext.apply({}, config);
	
    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        frame : true,
		
        items: [        
		{
            columnWidth: 0.5,
            layout: 'form',
			labelWidth : 150,
            items : [
			{
            	 xtype : "displayfield",
            	 fieldLabel :_("FundDefinition ID"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.ID._name
            },
            {
            	xtype : "displayfield",
            	fieldLabel :_("Partner code"),
            	name: CmFinoFIX.message.JSFundDefinitions.Entries.PurposeCode._name
            },
			{
        		xtype : "displayfield",
        		fieldLabel :_("FAC Prefix"),
        		name: CmFinoFIX.message.JSFundDefinitions.Entries.FACPrefix._name
        	},
        	{
            	xtype : "displayfield",
            	fieldLabel :_("FAC Length"),
            	name: CmFinoFIX.message.JSFundDefinitions.Entries.FACLength._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Expiry Time"),
        		name: CmFinoFIX.message.JSFundDefinitions.Entries.ExpiryValue._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Max Failure Attempts Allowed"),
        		name: CmFinoFIX.message.JSFundDefinitions.Entries.MaxFailAttemptsAllowed._name
        	},
			{
            	 xtype : "displayfield",
            	 fieldLabel :_("On FundAllocation Time Expiry"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.OnFundAllocationTimeExpiryText._name
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
            	 fieldLabel :_("On Failed Attempts Exceeded"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.OnFailedAttemptsExceededText._name
            },
            {
            	 xtype : "displayfield",
            	 fieldLabel :_("Generation of FAC On Failure"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.GenerationOfOTPOnFailureText._name
            },
            {
            	 xtype : "displayfield",
            	 fieldLabel :_("Is Multiple Withdrawal Allowed"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.IsMultipleWithdrawalAllowed._name
            },
            {
            	 xtype : "displayfield",
            	 fieldLabel :_("Create Time"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.CreateTime._name
            },
        	{
            	 xtype : "displayfield",
            	 fieldLabel :_("Created By"),
            	 name: CmFinoFIX.message.JSFundDefinitions.Entries.CreatedBy._name
        	},
        	{
        		 xtype : "displayfield",
        		 fieldLabel :_("Last Update Time"),
        		 name: CmFinoFIX.message.JSFundDefinitions.Entries.LastUpdateTime._name
        	},
        	{
    			 xtype : "displayfield",
    			 fieldLabel :_("Updated By"),
    			 name: CmFinoFIX.message.JSFundDefinitions.Entries.UpdatedBy._name
    		}
            ]
        }
		]	
    });
	
	mFino.widget.IntegrationDetails.superclass.constructor.call(this, localConfig);

}

Ext.extend(mFino.widget.FundDefinitionDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.FundDefinitionDetails.superclass.initComponent.call(this);
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

Ext.reg("fundDefinitionDetails", mFino.widget.FundDefinitionDetails);