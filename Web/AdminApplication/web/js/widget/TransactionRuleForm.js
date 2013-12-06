/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionRuleForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.TransactionRuleForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionRuleForm, Ext.FormPanel, {
    initComponent : function () {
    	this.txnRuleAddnInfoGrid = new mFino.widget.TxnRuleAdditionalInfoGrid({
	        itemId:'txnRuleAddnInfoGrid',
	        bodyStyle:'padding:5px',
	        height: 150,
	        frame:false,
	        border:true,
	        dataUrl:this.initialConfig.dataUrl			
		});	
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
                    xtype : 'textfield',
                    fieldLabel: _("Name"),
                    itemId : 'transactionrule.form.name',
                    labelSeparator:':',
                    anchor : '95%',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSTransactionRule.Entries.Name._name,
                    listeners: {
                	    change: function(field) {
                			this.findParentByType('transactionruleform').onChangeName(field);
                        }
                    }
                },
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Service Provider"),
                    allowBlank: false,
                    addEmpty: false,
                    emptyText : _('<select one..>'),
                    itemId : 'transactionrule.form.serviceprovider',
                    RPCObject : CmFinoFIX.message.JSServiceProvider,
                    displayField: CmFinoFIX.message.JSServiceProvider.Entries.ServiceProviderName._name,
                    valueField : CmFinoFIX.message.JSServiceProvider.Entries.ID._name,
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ServiceProviderID._name,
                    listeners: {
                        select: function(field) {
                			this.findParentByType('transactionruleform').getServices(field.getValue());
                			//this.findParentByType('transactionruleform').checkDuplicateTransactionRule(field);
                        }                     
                    }
                },                
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Service"),
                    allowBlank: false,
                    addEmpty: false,
                    emptyText : _('<select one..>'),
                    itemId : 'transactionrule.form.servicetype',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSServicesForServiceProvider),
                    displayField: CmFinoFIX.message.JSServicesForServiceProvider.Entries.ServiceName._name,
                    valueField : CmFinoFIX.message.JSServicesForServiceProvider.Entries.ServiceID._name,
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ServiceID._name,
                    listeners: {
                        select: function(field) {
                			this.findParentByType('transactionruleform').getTransactions(field.getValue());
                			//this.findParentByType('transactionruleform').checkDuplicateTransactionRule(field);
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
                    itemId : 'transactionrule.form.transactiontype',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSTransactionsForService),
                    displayField: CmFinoFIX.message.JSTransactionsForService.Entries.TransactionName._name,
                    valueField : CmFinoFIX.message.JSTransactionsForService.Entries.TransactionTypeID._name,
                    name: CmFinoFIX.message.JSTransactionRule.Entries.TransactionTypeID._name,
                    listeners: {
	                    select: function(field) {
	                    	this.findParentByType('transactionruleform').checkChargeMode();
	                    	//this.findParentByType('transactionruleform').checkDuplicateTransactionRule(field);
	                    	this.findParentByType('transactionruleform').loadRuleKeys();
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
                    itemId : 'transactionrule.form.channel',
                    RPCObject : CmFinoFIX.message.JSChannelCode,
                    displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                    valueField : CmFinoFIX.message.JSChannelCode.Entries.ID._name,
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ChannelCodeID._name,
                    listeners: {
	                    select : function(field) {
	                    	//this.findParentByType('transactionruleform').checkDuplicateTransactionRule(field);
	                    }                         
	                }                    
                },
                {
                    xtype: 'enumdropdown',                   
                    fieldLabel: _('Charge Mode'),
                    allowBlank: false,
                    addEmpty: false,
                    emptyText : _('<select one..>'),
                    itemId : 'transactionrule.form.chargemode',
                    labelSeparator:':',
                    anchor:'95%',
                    enumId : CmFinoFIX.TagID.ChargeMode,
                    name : CmFinoFIX.message.JSTransactionRule.Entries.ChargeMode._name,
                    listeners: {
	                    select: function(field) {
	                    	this.findParentByType('transactionruleform').checkChargeMode();
	                    }                    	
	                }                    
                },
				{
					xtype : "remotedropdown",
					anchor : '95%',
					allowBlank: false,
					addEmpty: false,
					emptyText : _('<select one..>'),
					itemId : 'transactionrule.form.sourcegroup',
					fieldLabel :"Source Group",
					emptyText: _('<select one..>'),
					RPCObject : CmFinoFIX.message.JSGroup,
					displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
					valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
					name: CmFinoFIX.message.JSTransactionRule.Entries.SourceGroup._name,
					pageSize : 10,
					listeners: {
	                    select: function(field) {	                    	
	                    	//this.findParentByType('transactionruleform').checkDuplicateTransactionRule(field);
	                    }                    	
	                }
				},
				{
					xtype : "remotedropdown",
					anchor : '95%',
					allowBlank: false,
					addEmpty: false,
					emptyText : _('<select one..>'),
					itemId : 'transactionrule.form.destinationgroup',
					fieldLabel :"Destination Group",
					emptyText: _('<select one..>'),
					RPCObject : CmFinoFIX.message.JSGroup,
					displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
					valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
					name: CmFinoFIX.message.JSTransactionRule.Entries.DestinationGroup._name,
					pageSize : 10,
					listeners: {
	                    select: function(field) {	                    	
	                    	//this.findParentByType('transactionruleform').checkDuplicateTransactionRule(field);
	                    }                    	
	                }
				}					
                ]
            }
            ]
        },
        {
            xtype:'tabpanel',
            itemId:'transactionRuleTabPanel',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            defaults:{
                layout:'column',
                columnWidth: 1,
                bodyStyle:'padding:10px'
            },
            items:[
		    {
		       title: _('Additional Info'),
		       frame:true,
		       autoHeight: true,
		       padding: '0 0 0 0',
		       itemId : 'transactionrule.form.addnInfo',
               layout: 'form',
               labelWidth : 175,
               labelPad : 5,
		       items:[ 
		       this.txnRuleAddnInfoGrid
		       ]
		    }
		    ]
        }
        ];
        mFino.widget.TransactionRuleForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    onChangeName : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSChargeType.Entries.Name._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSDuplicateNameCheck();
            msg.m_pName = field.getValue();
            msg.m_pTableName = "Transaction Rule";
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    
    checkChargeMode : function() {
    	var transactionType = this.find('itemId','transactionrule.form.transactiontype')[0].getRawValue();
    	var chargeMode = this.find('itemId','transactionrule.form.chargemode')[0].getValue();
    	if (((transactionType == 'Reverse Transaction') || (transactionType == 'Reverse Charge')) && 
    			chargeMode === CmFinoFIX.ChargeMode.Exclusive+""){
    		this.find('itemId','transactionrule.form.chargemode')[0].clearValue();
    		Ext.ux.Toast.msg(_("Error"), _("For Reverse Transaction Charge Mode must be Inclusive"),5);
    		return;
    	}
    	if (((transactionType == 'Bulk Transfer') || (transactionType == 'Bulk Transfer')) && 
    			chargeMode === CmFinoFIX.ChargeMode.Inclusive+""){
    		this.find('itemId','transactionrule.form.chargemode')[0].clearValue();
    		Ext.ux.Toast.msg(_("Error"), _("For Bulk Transfer Charge Mode must be Exclusive"),5);
    		return;
    	}
    },
    
    checkDuplicateTransactionRule : function(field) {
    	var serviceProvider = this.find('itemId','transactionrule.form.serviceprovider')[0].getValue();
    	var service = this.find('itemId','transactionrule.form.servicetype')[0].getValue();
    	var transactionType = this.find('itemId','transactionrule.form.transactiontype')[0].getValue();
    	var channel = this.find('itemId','transactionrule.form.channel')[0].getValue();
    	var sourceGroup = this.find('itemId','transactionrule.form.sourcegroup')[0].getValue();
    	var destGroup = this.find('itemId','transactionrule.form.destinationgroup')[0].getValue();
    	if (serviceProvider!=="" && service!=="" && transactionType!=="" && channel!=="" && sourceGroup!=="" && destGroup!== ""){
	        var msg = new CmFinoFIX.message.JSCheckTransactionRule();
	        msg.m_pServiceProviderID = serviceProvider;
	        msg.m_pServiceID = service;
	        msg.m_pTransactionTypeID = transactionType;
	        msg.m_pChannelCodeID = channel;
	        msg.m_pSourceGroup = sourceGroup;
	        msg.m_pDestinationGroup = destGroup;
	        var checkForExists = true;
            mFino.util.fix.checkNameInDB(field, msg, checkForExists);
        }
    },
    
    getServices : function(field) {
    	var sr_combo = this.find('itemId','transactionrule.form.servicetype')[0];
    	sr_combo.clearValue();
    	sr_combo.store.reload({
    		params: {ServiceProviderID : field}
    	});
    	return sr_combo;
    },
    
    getTransactions : function(field) {
    	var tr_combo = this.find('itemId','transactionrule.form.transactiontype')[0];
    	tr_combo.clearValue();
    	tr_combo.store.reload({
    		params: {ServiceID : field}
    	});
    	return tr_combo;
    },    
    
    save : function(){
        if(this.getForm().isValid()){        	
            this.getForm().updateRecord(this.record); 
            if (this.record.get('ID') > 0) {
            	this.saveGridData();
            } else {
            	this.store.on("write", this.saveGridData, this);
            }   
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
    		if(this.txnRuleAddnInfoGrid.validateAddnInfoGrid()) {
    			return true;
    		} else {
    			return false;
    		}
    	} else {
    		return false;
    	}
    },
    
    setRecord : function(record){
        this.getForm().reset();
        this.txnRuleAddnInfoGrid.reset();
        this.transactionRuleID = null;
        this.record = record;
    	var sr_combo = this.getServices(this.record.get(CmFinoFIX.message.JSTransactionRule.Entries.ServiceProviderID._name));    	   	
    	var tr_combo = this.getTransactions(this.record.get(CmFinoFIX.message.JSTransactionRule.Entries.ServiceID._name));    		
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        if(!this.record.phantom){
            this.transactionRuleID = record.data[CmFinoFIX.message.JSTransactionRule.Entries.ID._name];          
            this.txnRuleAddnInfoGrid.setTransactionRuleID(this.transactionRuleID);
            var serviceID = record.data[CmFinoFIX.message.JSTransactionRule.Entries.ServiceID._name];
            var transactionTypeID = record.data[CmFinoFIX.message.JSTransactionRule.Entries.TransactionTypeID._name];
            this.txnRuleAddnInfoGrid.loadRuleKeys(serviceID, transactionTypeID);
            this.txnRuleAddnInfoGrid.reloadGrid();
            sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSTransactionRule.Entries.ServiceName._name));
            tr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSTransactionRule.Entries.TransactionName._name));
        }    	        
    },
    
    loadRuleKeys: function() {
    	this.txnRuleAddnInfoGrid.reset();
    	var serviceID = this.find('itemId','transactionrule.form.servicetype')[0].getValue();
    	var transactionTypeID = this.find('itemId','transactionrule.form.transactiontype')[0].getValue();
    	this.txnRuleAddnInfoGrid.loadRuleKeys(serviceID, transactionTypeID);
    },
    
    saveGridData : function() {
        this.txnRuleAddnInfoGrid.setTransactionRuleID(this.record.get('ID'));
        this.txnRuleAddnInfoGrid.store.save();          
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

Ext.reg("transactionruleform", mFino.widget.TransactionRuleForm);