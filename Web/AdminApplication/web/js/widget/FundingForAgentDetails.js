Ext.ns("mFino.widget");

mFino.widget.FundingForAgentDetails = function (config)
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
            	 fieldLabel :_("AgentCashIn Transaction ID"),
            	 name: CmFinoFIX.message.AgentCashIn.Entries.ID._name
             },
            {
            	xtype : "displayfield",
            	fieldLabel :_("Source MDN"),
            	name: CmFinoFIX.message.AgentCashIn.Entries.SourceMDN._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Destination MDN"),
            	name: CmFinoFIX.message.AgentCashIn.Entries.DestMDN._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Destination Partner ID"),
        		name: CmFinoFIX.message.AgentCashIn.Entries.DestPartnerID._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Source PocketID"),
        		name: CmFinoFIX.message.AgentCashIn.Entries.SourcePocketID._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Destination Pocket ID"),
        		name: CmFinoFIX.message.AgentCashIn.Entries.DestPocketID._name
        	},
            {
           	 xtype : "displayfield",
           	 fieldLabel :_("Transfer Amount"),
           	 name: CmFinoFIX.message.AgentCashIn.Entries.Amount._name
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
            	 fieldLabel :_("Transaction Status"),
            	 name: CmFinoFIX.message.AgentCashIn.Entries.AgentCashInTrxnStatusText._name
             },
            {
            	xtype : "displayfield",
            	 fieldLabel :_("Transaction Status Reason"),
            	 name: CmFinoFIX.message.AgentCashIn.Entries.AgentCashInTrxnStatusReason._name
             },
             {
             	xtype : "displayfield",
             	 fieldLabel :_("SCTL ID"),
             	 name: CmFinoFIX.message.AgentCashIn.Entries.SctlId._name
              },
             {
            	xtype : "displayfield",
            	fieldLabel :_("Create Time"),
            	name:  CmFinoFIX.message.AgentCashIn.Entries.CreateTime._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Created By"),
            	name:  CmFinoFIX.message.AgentCashIn.Entries.CreatedBy._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Last Update Time"),
        		name:  CmFinoFIX.message.AgentCashIn.Entries.LastUpdateTime._name
        	},
        	{
    			xtype : "displayfield",
    			fieldLabel :_("Updated By"),
    			name:  CmFinoFIX.message.AgentCashIn.Entries.UpdatedBy._name
    		}
            ]
        }
    ]
    });

    mFino.widget.FundingForAgentDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.FundingForAgentDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.FundingForAgentDetails.superclass.initComponent.call(this);
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

Ext.reg("fundingForAgentDetails", mFino.widget.FundingForAgentDetails);

