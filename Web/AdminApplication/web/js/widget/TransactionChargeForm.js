/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionChargeForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.TransactionChargeForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionChargeForm, Ext.FormPanel, {
    initComponent : function () {
		this.sharePartnerGrid = new mFino.widget.SharePartnerGrid({
	        itemId:'sharepartnergrid',
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
                labelWidth : 175,
                labelPad : 5,
                items : [
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Transaction Rule"),
                    emptyText : _('<select one..>'),
                    itemId : 'transactioncharge.form.transactionrule',
                    allowBlank: false,
                    addEmpty: false,
                    pageSize : 10,
                    params: {start: 0, limit: 10},
                    RPCObject : CmFinoFIX.message.JSTransactionRule,
                    displayField: CmFinoFIX.message.JSTransactionRule.Entries.Name._name,
                    valueField : CmFinoFIX.message.JSTransactionRule.Entries.ID._name,
                    name: CmFinoFIX.message.JSTransactionCharge.Entries.TransactionRuleID._name,
                    listeners: {
	                    select: function(field) {
	            			this.findParentByType('transactionchargeform').loadSharePartners(field.getValue());		                    
		                    this.findParentByType('transactionchargeform').checkDuplicateTransactionCharge(field);
		                    this.findParentByType('transactionchargeform').checkChargeDefinition(field);
	                    	this.findParentByType('transactionchargeform').checkDependantChargeType(field);
	                    }
                	}                    
                },                
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Charge Type"),
                    emptyText : _('<select one..>'),
                    itemId : 'transactioncharge.form.chargetype',
                    allowBlank: false,
                    addEmpty: false,
                    RPCObject : CmFinoFIX.message.JSChargeType,
                    displayField: CmFinoFIX.message.JSChargeType.Entries.Name._name,
                    valueField : CmFinoFIX.message.JSChargeType.Entries.ID._name,
                    name: CmFinoFIX.message.JSTransactionCharge.Entries.ChargeTypeID._name,
                    pageSize : 10,
                    params: {start: 0, limit: 10},
                    listeners: {
                        select: function(field) {
                        	this.findParentByType('transactionchargeform').checkDuplicateTransactionCharge(field);
                			this.findParentByType('transactionchargeform').getChargeDefinitions(field.getValue());
                        }                      
                    }                    
                },
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Charge Definition"),
                    emptyText : _('<select one..>'),
                    allowBlank: false,
                    addEmpty: false,
                    itemId : 'transactioncharge.form.chargedefinition',                    
                    pageSize : 10,
                    params: {start: 0, limit: 10},
                    lastQuery: '',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSChargeDefinition),
                    displayField: CmFinoFIX.message.JSChargeDefinition.Entries.Name._name,
                    valueField : CmFinoFIX.message.JSChargeDefinition.Entries.ID._name,
                    name: CmFinoFIX.message.JSTransactionCharge.Entries.ChargeDefinitionID._name,
                    listeners: {
	                    expand: function() {
                			this.findParentByType('transactionchargeform').checkChargeType();
	                    },
	                    select: function(field) {
	                    	this.findParentByType('transactionchargeform').checkChargeDefinition(field);
	                    	this.findParentByType('transactionchargeform').checkDependantChargeType(field);
	                    }
	                }                     
                },
                {
                	xtype :"checkbox",
                	anchor : '95%',
                	itemId : 'transactioncharge.form.isactive',
                    fieldLabel :_("Is Active"),
    				name :CmFinoFIX.message.JSTransactionCharge.Entries.IsActive._name    				
    			},
                {
                	xtype :"checkbox",
                	anchor : '95%',
                	itemId : 'transactioncharge.form.IsChrgDstrbApplicableToSrcSub',
                    fieldLabel :_("Is Charge Distribution Applicable to Subscriber As Source"),
    				name :CmFinoFIX.message.JSTransactionCharge.Entries.IsChrgDstrbApplicableToSrcSub._name    				
    			},
                {
                	xtype :"checkbox",
                	anchor : '95%',
                	itemId : 'transactioncharge.form.IsChrgDstrbApplicableToDestSub',
                    fieldLabel :_("Is Charge Distribution Applicable to Subscriber As Destination"),
    				name :CmFinoFIX.message.JSTransactionCharge.Entries.IsChrgDstrbApplicableToDestSub._name    				
    			}
                ]
            }
            ]
        },
        {
            xtype:'tabpanel',
            itemId:'tabelpanelTransactionCharge',
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
		       title: _('Charge Share'),
		       frame:true,
		       autoHeight: true,
		       padding: '0 0 0 0',
		       itemId : 'transactioncharge.form.sharing',
               layout: 'form',
               labelWidth : 175,
               labelPad : 5,
		       items:[ 
		       this.sharePartnerGrid
		       ]
		    }
		    ]
        }
        ];
        mFino.widget.TransactionChargeForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    getChargeDefinitions : function(value) {
    	var sr_combo = this.find('itemId','transactioncharge.form.chargedefinition')[0];
    	sr_combo.clearValue();
    	sr_combo.store.baseParams[CmFinoFIX.message.JSChargeDefinition.ChargeTypeSearch._name] = value;
    	sr_combo.store.reload({
    		params: {ChargeTypeSearch : value}
    	});
    },
    
    loadSharePartners : function(value) {
        var sharePartnersSize = this.sharePartnerGrid.store.getCount();

        if (sharePartnersSize > 0) {
    		for ( var i = sharePartnersSize-1; i >= 0; i--) {
    			var rec = this.sharePartnerGrid.store.getAt(i);
    			this.sharePartnerGrid.store.remove(rec);
    		}
        }

    	this.sharePartnerGrid.loadPartners(value);
    },
    
    checkChargeType : function() {
    	var chargeType = this.find('itemId','transactioncharge.form.chargetype')[0].getValue();
    	if (chargeType===null || chargeType==="" || typeof(chargeType)==='undefined') {
    		this.find('itemId','transactioncharge.form.chargedefinition')[0].store.removeAll();
    	}
    },
    
    checkDependantChargeType : function(field) {
    	var id = this.record.get('ID');
    	var transactionRule = this.find('itemId','transactioncharge.form.transactionrule')[0].getValue();
    	var chargeDefinition = this.find('itemId','transactioncharge.form.chargedefinition')[0].getValue();
    	if (transactionRule!=="" && chargeDefinition!=="" && field.getValue()!==""){
	        var msg = new CmFinoFIX.message.JSCheckDependantChargeType();
	        msg.m_pTransactionChargeID = id;
	        msg.m_pTransactionRuleID = transactionRule;
	        msg.m_pChargeDefinitionID = chargeDefinition;
	        var checkForExists=true;
	        mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    
    checkChargeDefinition : function(field) {
    	var transactionRule = this.find('itemId','transactioncharge.form.transactionrule')[0].getValue();
    	var chargeDefinition = this.find('itemId','transactioncharge.form.chargedefinition')[0].getValue();
    	if (transactionRule!=="" && chargeDefinition!==""){
	        var msg = new CmFinoFIX.message.JSCheckChargeDefinition();
	        msg.m_pTransactionRuleID = transactionRule;
	        msg.m_pChargeDefinitionID = chargeDefinition;
	        var checkForExists=true;
	        mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    
    checkDuplicateTransactionCharge : function (field) {
    	var transactionRule = this.find('itemId','transactioncharge.form.transactionrule')[0].getValue();
    	var chargetype = this.find('itemId','transactioncharge.form.chargetype')[0].getValue();
    	if (transactionRule!=="" && chargetype!==""){
	        var msg = new CmFinoFIX.message.JSCheckTransactionCharge();
	        msg.m_pTransactionRuleID = transactionRule;
	        msg.m_pChargeTypeID = chargetype;
	        var checkForExists=true;
	        mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
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
    
    saveGridData : function() {
        this.sharePartnerGrid.setTemplateID(this.record.get('ID'));
        this.sharePartnerGrid.store.save();          
      },
    
    
    setRecord : function(record){
        this.getForm().reset();
        this.sharePartnerGrid.reset();
        this.record = record;
        this.templateID = null;
    	var sr_combo = this.find('itemId','transactioncharge.form.chargedefinition')[0];
    	sr_combo.store.reload({
    		params: {ChargeTypeSearch: this.record.get(CmFinoFIX.message.JSTransactionCharge.Entries.ChargeTypeID._name)}
    	});        
        if(!this.record.phantom){
        	this.find('itemId','transactioncharge.form.transactionrule')[0].disable();
            this.templateID = record.data[CmFinoFIX.message.JSTransactionCharge.Entries.ID._name];
            this.transactionruleId = record.data[CmFinoFIX.message.JSTransactionCharge.Entries.TransactionRuleID._name];            
            this.sharePartnerGrid.setTemplateID(this.templateID);
            this.sharePartnerGrid.reloadGrid(this.transactionruleId);
        } else {
        	this.find('itemId','transactioncharge.form.transactionrule')[0].enable();
        	this.sharePartnerGrid.loadPartners();  
        }
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSTransactionCharge.Entries.ChargeDefinitionName._name));
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

Ext.reg("transactionchargeform", mFino.widget.TransactionChargeForm);