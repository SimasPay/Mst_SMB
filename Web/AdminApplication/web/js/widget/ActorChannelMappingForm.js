/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ActorChannelMappingForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.ActorChannelMappingForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ActorChannelMappingForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 200;
        this.labelPad = 20;
        this.items = [
        {
            layout:'column',
            items : [
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : [
				{
				    xtype : "enumdropdown",
				    fieldLabel: _('Actor'),
				    allowBlank: false,
                    addEmpty: false,
                    editable: true,                    
				    labelSeparator : '',
				    itemId: "actorchannel.form.actor",
				    anchor : '95%',
				    emptyText : _('<select one..>'),
				    enumId : CmFinoFIX.TagID.SubscriberType,
				    name: CmFinoFIX.message.JSActorChannelMapping.SubscriberType._name,
				    listeners   : {
				        select : function(field) {
				        	var partnerTypeField = this.findParentByType('actorChannelMappingForm').find('itemId','actorchannel.form.partnerType')[0];
				        	var kycField = this.findParentByType('actorChannelMappingForm').find('itemId','actorchannel.form.kyclevel')[0];
				        	if(field.value == CmFinoFIX.SubscriberType.Subscriber){
				        		partnerTypeField.reset();
				        		partnerTypeField.disable();
				        		kycField.enable();
				        	} else {
				        		partnerTypeField.enable();
				        		kycField.reset();
				        		kycField.disable();
				        	}
				        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
				        }
				    }
				},
				{
				    xtype : "enumdropdown",
				    fieldLabel: _('Partner Type'),
				    allowBlank: false,
                    addEmpty: false,
                    editable: true,
				    labelSeparator : '',
				    itemId: "actorchannel.form.partnerType",
				    anchor : '95%',
				    emptyText : _('<select one..>'),
				    enumId : CmFinoFIX.TagID.BusinessPartnerType,
				    name: CmFinoFIX.message.JSActorChannelMapping.BusinessPartnerType._name,
				    listeners: {
                        select: function(field) {                        	
                        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
                        }                      
                    }
				},             
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Service"),
                    allowBlank: false,
                    addEmpty: false,
                    editable: true,
                    emptyText : _('<select one..>'),
                    itemId : 'actorchannel.form.serviceType',
                    RPCObject : CmFinoFIX.message.JSService,
                    displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
                    valueField : CmFinoFIX.message.JSService.Entries.ID._name,
                    name: CmFinoFIX.message.JSActorChannelMapping.ServiceID._name,
                    listeners: {
                        select: function(field) {
                        	this.findParentByType('actorChannelMappingForm').getTransactions(field.getValue());
                        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
                        }                      
                    }                    
                },                
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Transaction Type"),
                    emptyText : _('<select one..>'),
                    allowBlank: false,
                    addEmpty: false,
                    editable: true,
                    itemId : 'actorchannel.form.transactionType',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSTransactionsForService),
                    displayField: CmFinoFIX.message.JSTransactionsForService.Entries.TransactionName._name,
                    valueField : CmFinoFIX.message.JSTransactionsForService.Entries.TransactionTypeID._name,
                    name: CmFinoFIX.message.JSActorChannelMapping.TransactionTypeID._name,
                    listeners: {
                        select: function(field) {                        	
                        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
                        }                      
                    }
                },
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Channel"),
                    emptyText : _('<select one..>'),
                    allowBlank: false,
                    addEmpty: false,
                    editable: true,
                    itemId : 'actorchannel.form.channel',
                    RPCObject : CmFinoFIX.message.JSChannelCode,
                    displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                    valueField : CmFinoFIX.message.JSChannelCode.Entries.ID._name,
                    name: CmFinoFIX.message.JSActorChannelMapping.ChannelCodeID._name,
                    listeners: {
                        select: function(field) {                        	
                        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
                        }                      
                    }
                },
                {
                    xtype : "remotedropdown",
                    anchor : '95%',                  
                    itemId : 'actorchannel.form.kyclevel',
                    fieldLabel : _("KYC"),
                    emptyText : _('<select one..>'),
                    allowBlank: false,
                    addEmpty: false,
                    editable: true,
                    RPCObject : CmFinoFIX.message.JSKYCCheck,
                    displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
                    valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
                    name: CmFinoFIX.message.JSActorChannelMapping.KYCLevel._name,
                    listeners: {
                        select: function(field) {                        	
                        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
                        }                      
                    }
                },
                {
					xtype : "remotedropdown",
					anchor : '95%',					
					itemId : 'actorchannel.form.group',
					fieldLabel : "Group",
					allowBlank: false,
                    addEmpty: false,
                    editable: true,
					emptyText: _('<select one..>'),
					pageSize : 50,
					params: {start: 0, limit: 50},
					RPCObject : CmFinoFIX.message.JSGroup,
					displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
					valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
					name: CmFinoFIX.message.JSActorChannelMapping.GroupID._name,
					listeners: {
                        select: function(field) {                        	
                        	this.findParentByType('actorChannelMappingForm').checkDuplicateACMapping(field);
                        }                      
                    }
                },
                {
                	xtype :"checkbox",
                	anchor : '95%',
                	itemId : 'actorchannel.form.isallowed',
                    fieldLabel :_("Is Allowed"),
    				name :CmFinoFIX.message.JSActorChannelMapping.IsAllowed._name    				
    			}
              ]
            }
            ]
        }
        ];
        mFino.widget.TransactionRuleForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    getTransactions : function(field) {
    	var tr_combo = this.find('itemId','actorchannel.form.transactionType')[0];
    	tr_combo.clearValue();
    	tr_combo.store.reload({
    		params: {ServiceID : field}
    	});
    	return tr_combo;
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
    
    validate : function() {
    	if(this.getForm().isValid()) {
    		return true;
    	} else {
    		return false;
    	}
    },
    
    checkDuplicateACMapping: function(field) {
    	var subscriberType = this.find('itemId','actorchannel.form.actor')[0].getValue();
    	var partnerType = this.find('itemId','actorchannel.form.partnerType')[0].getValue();
    	var serviceType = this.find('itemId','actorchannel.form.serviceType')[0].getValue();
    	var transactionType = this.find('itemId','actorchannel.form.transactionType')[0].getValue();
    	var kycLevel = this.find('itemId','actorchannel.form.kyclevel')[0].getValue();
    	var channel = this.find('itemId','actorchannel.form.channel')[0].getValue();
    	var group = this.find('itemId','actorchannel.form.group')[0].getValue();
    	if (subscriberType!=="" && serviceType!=="" && transactionType!=="" && channel!== "" && group!== ""){
    		if(subscriberType == CmFinoFIX.SubscriberType.Partner && partnerType == "") {
    			return;
    		} 
    		if(subscriberType == CmFinoFIX.SubscriberType.Subscriber && kycLevel == "") {
    			return;
    		} 
	        var msg = new CmFinoFIX.message.JSActorChannelMappingValidator();
	        msg.m_pID = this.record.get("ID");
	        msg.m_pSubscriberType = subscriberType;
	        msg.m_pBusinessPartnerType = partnerType;
	        msg.m_pServiceID = serviceType;
	        msg.m_pTransactionTypeID = transactionType;
	        msg.m_pChannelCodeID = channel;
	        msg.m_pKYCLevel = kycLevel;
	        msg.m_pGroupID = group;
	        var checkForExists = true;
            mFino.util.fix.checkNameInDB(field, msg, checkForExists);
        }
    },
    
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        var subscriberType = record.get(CmFinoFIX.message.JSActorChannelMapping.Entries.SubscriberType._name);
        if( subscriberType == CmFinoFIX.SubscriberType.Subscriber) {
        	this.find('itemId','actorchannel.form.partnerType')[0].disable();
        	this.find('itemId','actorchannel.form.kyclevel')[0].enable();
        } else if(subscriberType == CmFinoFIX.SubscriberType.Partner){
        	this.find('itemId','actorchannel.form.partnerType')[0].enable();
        	this.find('itemId','actorchannel.form.kyclevel')[0].disable();
        }
        var sr_combo = this.find('itemId','actorchannel.form.serviceType')[0];    	    	   	
    	var tr_combo = this.getTransactions(this.record.get(CmFinoFIX.message.JSActorChannelMapping.Entries.ServiceID._name));    		
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    	sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSActorChannelMapping.Entries.ServiceName._name));
        tr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSActorChannelMapping.Entries.TransactionName._name));
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

Ext.reg("actorChannelMappingForm", mFino.widget.ActorChannelMappingForm);