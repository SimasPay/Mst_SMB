
Ext.ns("mFino.widget");

mFino.widget.FundDefinitionForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.FundDefinitionForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FundDefinitionForm, Ext.FormPanel, {
	initComponent : function () {
        this.labelWidth = 150;
        this.labelPad = 20;
        this.items = [
        {
     	   xtype : "remotedropdown", 
    	   fieldLabel: _('Partner Code'), 
    	   labelSeparator : '', 	    	   
    	   pageSize : 10,
    	   params:{start:0, limit:10},
    	   anchor : '98%',
           allowBlank: false,
		   addEmpty:false,
    	   itemId :'fundDefinitionForm.PartnerCode',
    	   emptyText : _('<select  >'), 
    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPurpose), 
    	   displayField: CmFinoFIX.message.JSPurpose.Entries.PurposeCode._name, 
    	   valueField : CmFinoFIX.message.JSPurpose.Entries.ID._name, 
    	   hiddenName : CmFinoFIX.message.JSFundDefinitions.Entries.PurposeID._name, 
    	   name: CmFinoFIX.message.JSFundDefinitions.Entries.PurposeID._name	    	   
	    },
        {
            xtype : "textfield",
            fieldLabel :_("FAC Prefix"),
            anchor : '95%',
			itemId :'fundDefinitionForm.FACPrefix',
            name: CmFinoFIX.message.JSFundDefinitions.Entries.FACPrefix._name,
			listeners: {
				change: function(field){
					this.findParentByType('fundDefinitionForm').resetLength(field);
				}
			}
        },
		{
            xtype : "textfield",
            fieldLabel :_("FAC Length"),
			vtype:'number',
            anchor : '95%',
			itemId :'fundDefinitionForm.FACLength',
			allowBlank: false,
            name: CmFinoFIX.message.JSFundDefinitions.Entries.FACLength._name,
			listeners: {
				change: function(field){
					this.findParentByType('fundDefinitionForm').lengthCheck(field);
				}
			}
        },
        {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('Expiry Time'), 
	    	   labelSeparator : '', 	    	   
	    	   pageSize : 10,
	    	   params:{start:0, limit:10},
	    	   anchor : '98%', 
               allowBlank: false,
			   addEmpty:false,
	    	   itemId : 'fundDefinitionForm.ExpiryTime',
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSExpirationType), 
	    	   displayField: CmFinoFIX.message.JSExpirationType.Entries.ExpiryValue._name, 
	    	   valueField : CmFinoFIX.message.JSExpirationType.Entries.ID._name, 
	    	   hiddenName : CmFinoFIX.message.JSFundDefinitions.Entries.ExpiryID._name, 
	    	   name: CmFinoFIX.message.JSFundDefinitions.Entries.ExpiryID._name,	   
				listeners: {
					change: function(field){
						this.findParentByType('fundDefinitionForm').notify();
					}
				}
	      },
	      {
	           xtype : "textfield",
	           fieldLabel :_("Max Failure Attempts Allowed"),
	           anchor : '95%',
			   vtype:'number',
               allowBlank: false,
	           name: CmFinoFIX.message.JSFundDefinitions.Entries.MaxFailAttemptsAllowed._name
	      }, 
	      {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('On FundAllocation Time Expiry'), 
	    	   labelSeparator : '', 	    	   
	    	   pageSize : 10,
	    	   params:{start:0, limit:10},
	    	   anchor : '98%', 
               allowBlank: false,
			   addEmpty:false,
	    	   itemId : 'fundDefinitionForm.OnFundAllocationTimeExpiry',
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSFundEvents), 
	    	   displayField: CmFinoFIX.message.JSFundEvents.Entries.OnFundAllocationTimeExpiryText._name, 
	    	   valueField : CmFinoFIX.message.JSFundEvents.Entries.OnFundAllocationTimeExpiry._name, 
	    	   hiddenName : CmFinoFIX.message.JSFundDefinitions.Entries.OnFundAllocationTimeExpiry._name, 
	    	   name: CmFinoFIX.message.JSFundDefinitions.Entries.OnFundAllocationTimeExpiry._name	    	   
	      },
	      {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('On Failed Attempts Exceeded'), 
	    	   labelSeparator : '', 	    	   
	    	   pageSize : 10,
	    	   params:{start:0, limit:10},
	    	   anchor : '98%', 
               allowBlank: false,
			   addEmpty:false,
	    	   itemId : 'fundDefinitionForm.OnFailedAttemptsExceeded',
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSFundEvents), 
	    	   displayField: CmFinoFIX.message.JSFundEvents.Entries.OnFailedAttemptsExceededText._name, 
	    	   valueField : CmFinoFIX.message.JSFundEvents.Entries.OnFailedAttemptsExceeded._name, 
	    	   hiddenName : CmFinoFIX.message.JSFundDefinitions.Entries.OnFailedAttemptsExceeded._name, 
	    	   name: CmFinoFIX.message.JSFundDefinitions.Entries.OnFailedAttemptsExceeded._name	    	   
	      },
	      {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('Generation of FAC On Failure'), 
	    	   labelSeparator : '', 	    	   
	    	   pageSize : 10,
	    	   params:{start:0, limit:10},
	    	   anchor : '98%', 
               allowBlank: false,
			   addEmpty:false,
	    	   itemId : 'fundDefinitionForm.GenerationOfOTPOnFailure',
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSFundEvents), 
	    	   displayField: CmFinoFIX.message.JSFundEvents.Entries.GenerationOfOTPOnFailureText._name, 
	    	   valueField : CmFinoFIX.message.JSFundEvents.Entries.GenerationOfOTPOnFailure._name, 
	    	   hiddenName : CmFinoFIX.message.JSFundDefinitions.Entries.GenerationOfOTPOnFailure._name, 
	    	   name: CmFinoFIX.message.JSFundDefinitions.Entries.GenerationOfOTPOnFailure._name	    	   
	      },
          {
          	xtype :"checkbox",
          	anchor : '95%',
            allowBlank: false,
          	fieldLabel :_("Is Multiple Withdrawal Allowed"),
			name :CmFinoFIX.message.JSFundDefinitions.Entries.IsMultipleWithdrawalAllowed._name    				
		  }
		  
        ];        

        mFino.widget.FundDefinitionForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
	    	if(item.getXType() == 'remotedropdown') {
	    		item.reload();
	    	}
    	});
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record); 
            if(this.store){
                if(this.record.phantom){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },   
    
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        
        var mc_combo = this.find('itemId','fundDefinitionForm.PartnerCode')[0];
        var et_combo = this.find('itemId','fundDefinitionForm.ExpiryTime')[0];
        var facExpiry_combo = this.find('itemId','fundDefinitionForm.OnFundAllocationTimeExpiry')[0];
        var failAtmpts_combo = this.find('itemId','fundDefinitionForm.OnFailedAttemptsExceeded')[0];
        var facGen_combo = this.find('itemId','fundDefinitionForm.GenerationOfOTPOnFailure')[0];

        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        
        mc_combo.setRawValue(this.record.get(CmFinoFIX.message.JSFundDefinitions.Entries.PurposeCode._name));
        et_combo.setRawValue(this.record.get(CmFinoFIX.message.JSFundDefinitions.Entries.ExpiryValue._name));
        facExpiry_combo.setRawValue(this.record.get(CmFinoFIX.message.JSFundDefinitions.Entries.OnFundAllocationTimeExpiryText._name));
        failAtmpts_combo.setRawValue(this.record.get(CmFinoFIX.message.JSFundDefinitions.Entries.OnFailedAttemptsExceededText._name));
        facGen_combo.setRawValue(this.record.get(CmFinoFIX.message.JSFundDefinitions.Entries.GenerationOfOTPOnFailureText._name));
    },
	
	lengthCheck : function(field){		
		if(this.form.items.get("fundDefinitionForm.FACLength").getValue() < 4){
			Ext.ux.Toast.msg(_("Error"), _("Minimum length of FAC should be 4"));
			field.reset();
		}
		else if(this.form.items.get("fundDefinitionForm.FACPrefix").getValue().length + 4 > this.form.items.get("fundDefinitionForm.FACLength").getValue()){
			var len = this.form.items.get("fundDefinitionForm.FACPrefix").getValue().length + 4;
			Ext.ux.Toast.msg(_("Error"), _("Minimum value for this field is 4 + FAC Prefix length : "+len));
			field.reset();
		}
	},
	
	notify : function(){
		Ext.ux.Toast.msg(_("Info"), _("The new expiry time for this fundDefinition will be applied only for new transactions"));
	},
	
	resetLength : function(field){
		this.form.items.get("fundDefinitionForm.FACLength").reset();
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

Ext.reg("fundDefinitionForm", mFino.widget.FundDefinitionForm);