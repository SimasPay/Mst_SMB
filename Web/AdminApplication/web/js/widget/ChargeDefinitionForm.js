/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeDefinitionForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.ChargeDefinitionForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeDefinitionForm, Ext.FormPanel, {
    initComponent : function () {
		this.pricingGrid = new mFino.widget.ChargePricingGrid({
	        itemId:'pricinggrid',
	        bodyStyle:'padding:5px',
	        height: 160,
	        frame:true,
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
                    itemId : 'ChargeDefinition.form.name',
                    labelSeparator:':',
                    anchor : '90%',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.Name._name,
                    listeners: {
                	    change: function(field) {
                			this.findParentByType('chargedefinitionform').onChangeName(field);
                        }
                    }
                },
                {
                    xtype : 'textfield',
                    fieldLabel: _("Description"),
                    itemId : 'ChargeDefinition.form.description',
                    labelSeparator:':',
                    anchor : '90%',
                    maxLength : 255,
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.Description._name
                },
                {
                    xtype : "remotedropdown",
                    anchor : '90%',
                    fieldLabel :_("Charge Type"),
                    allowBlank: false,
					emptyText : _('<select one..>'),
                    itemId : 'chargedefinition.form.chargetype',
                    RPCObject : CmFinoFIX.message.JSChargeType,
                    displayField: CmFinoFIX.message.JSChargeType.Entries.Name._name,
                    valueField : CmFinoFIX.message.JSChargeType.Entries.ID._name,
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.ChargeTypeID._name,
                    pageSize: 10,
                    listeners: {
                        select: function(field) {
                			this.findParentByType('chargedefinitionform').getDependantChargeTypes(field.getValue());
                        }                        
                    }
                },
                {
                    xtype:'checkbox',
                    fieldLabel: _('Is Charge From Customer'),
                    width:150,
                    itemId:'chargedefinition.form.ischargefromcustomer',
                    id:'chargedefinition.form.ischargefromcustomer',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.IsChargeFromCustomer._name,
                    listeners: {
                		check: function(field) {
                			this.findParentByType('chargedefinitionform').hideOrShowFundingPartner(field.getValue());
                		}
                	}
                },                
                {
                    xtype : "combo",
                    anchor : '90%',
                    fieldLabel :_("Dependant Charge Type"),
					emptyText : _('<select one..>'),
                    itemId : 'chargedefinition.form.dependantchargetype',
                    triggerAction: "all",
                    forceSelection : true,
                    pageSize : 10,
                    lastQuery: '',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSChargeType),
                    displayField: CmFinoFIX.message.JSChargeType.Entries.Name._name,
                    valueField : CmFinoFIX.message.JSChargeType.Entries.ID._name,
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.DependantChargeTypeID._name
                },
                {
                    xtype : "combo",
                    anchor : '90%',
                    fieldLabel :_("Funding Partner"),
					emptyText : _('<select one..>'),
                    itemId : 'chargedefinition.form.fundingpartner',
                    id : 'chargedefinition.form.fundingpartner',
                    triggerAction: "all",
                    forceSelection : true,
                    pageSize : 10,
                    lastQuery: '',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner),
                    displayField: CmFinoFIX.message.JSPartner.Entries.TradeName._name,
                    valueField : CmFinoFIX.message.JSPartner.Entries.ID._name,
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.FundingPartnerID._name,
                    listeners: {
                        select: function(field) {
                			this.findParentByType('chargedefinitionform').getPockets(field.getValue());
                        }
                    }
                },
                {
                    xtype : "combo",
                    anchor : '90%',
                    fieldLabel :_("Funding Pocket"),
					emptyText : _('<select one..>'),
                    itemId : 'chargedefinition.form.fundingpocket',
                    id : 'chargedefinition.form.fundingpocket',
                    triggerAction: "all",
                    forceSelection : true,
                    pageSize : 10,
                    lastQuery: '',
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
                    displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
                    valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.PocketID._name
                },                
               
                {
                    xtype:'checkbox',
                    fieldLabel: _('Is Charge Taxable'),
                    width:150,
                    itemId:'chargedefinition.form.istaxable',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.IsTaxable._name
                }                
                ]
            }
            ]
        },
        {
            xtype:'tabpanel',
            itemId:'tabelpanelChargeDefinition',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            defaults:{
                layout:'form',
                bodyStyle:'padding:10px'
            },
            items:[
		    {
		       title: _('Charge Pricings'),
		       autoHeight: true,
		       padding: '0 0 0 0',
		       itemId : 'chargedefinition.form.pricing',
		       items:[ this.pricingGrid ]
		    }
		    ]
        },
        {
            xtype : "displayfield",
            itemId : 'ChargeDefinition.form.note',
            value : "Note:"
        }
        ];
        mFino.widget.ChargeDefinitionForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    onChangeName : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSChargeType.Entries.Name._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSDuplicateNameCheck();
            msg.m_pName = field.getValue();
            msg.m_pTableName = "Charge Definition";
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    
    getDependantChargeTypes : function(field) {
    	var sr_combo = this.find('itemId','chargedefinition.form.dependantchargetype')[0];
    	sr_combo.clearValue();
    	sr_combo.store.baseParams[CmFinoFIX.message.JSChargeType.NotEqualID._name] = field;
    	sr_combo.store.reload({
    		params: {NotEqualID : field}
    	});
    	return sr_combo;
    },
    
    getPockets : function(fieldvalue) {
    	var pocket_combo = this.find('itemId', 'chargedefinition.form.fundingpocket')[0];
    	pocket_combo.clearValue();
    	if (typeof(fieldvalue) != "undefined" && fieldvalue != null && fieldvalue != "") {
	    	pocket_combo.store.baseParams[CmFinoFIX.message.JSPocket.PartnerIDSearch._name] = fieldvalue;
	    	pocket_combo.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;    	
	    	pocket_combo.store.baseParams[CmFinoFIX.message.JSPocket.PocketType._name] = CmFinoFIX.PocketType.SVA;
	    	pocket_combo.store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;
            pocket_combo.store.baseParams[CmFinoFIX.message.JSPocket.IsCollectorPocket._name] = 1;
	    	pocket_combo.store.reload({
	    		params: {
	    			PartnerIDSearch: fieldvalue,
	    			NoCompanyFilter: true,
	    			PocketType: CmFinoFIX.PocketType.SVA,
	    			Commodity: CmFinoFIX.Commodity.Money
	    		}
	    	});
    	}
    	return pocket_combo;
    },
    
    getFundingPartners : function() {
    	var searchPartners = CmFinoFIX.BusinessPartnerType.ServicePartner + "," + CmFinoFIX.BusinessPartnerType.SolutionPartner + 
								"," + CmFinoFIX.BusinessPartnerType.RegulatoryBody;
    	var partner_combo = this.find('itemId', 'chargedefinition.form.fundingpartner')[0];
    	partner_combo.clearValue();
    	partner_combo.store.baseParams[CmFinoFIX.message.JSPartner.PartnerTypeSearchString._name] = searchPartners;
    	partner_combo.store.reload({
    		params: {
    			PartnerTypeSearchString: searchPartners
    		}
    	});
    	return partner_combo;
    },
    
    checkFundingPartner : function(field) {
    	var value = this.find('itemId','chargedefinition.form.fundingpartner')[0].getValue();
    	if (typeof(value) === "undefined" || value === null || value === "") {
    	} else {
			Ext.ux.Toast.msg(_("Error"), _("Either Dependant Charge Type or Funding Partner can be selected"),5);
    		this.find('itemId',field.getItemId())[0].clearValue();
    	}
    },
    
    checkDependantChargeType : function(field) {
    	var value = this.find('itemId','chargedefinition.form.dependantchargetype')[0].getValue();
    	if (typeof(value) === "undefined" || value === null || value === "") {
    	} else {
			Ext.ux.Toast.msg(_("Error"), _("Either Dependant Charge Type or Funding Partner can be selected"),5);
    		this.find('itemId',field.getItemId())[0].clearValue();
    	}
    },
    
    hideOrShowFundingPartner : function(value) {
		var fundingPartner = this.find('itemId','chargedefinition.form.fundingpartner')[0];
		var fundingPocket = this.find('itemId', 'chargedefinition.form.fundingpocket')[0];
		var dependantChargetype = this.find('itemId','chargedefinition.form.dependantchargetype')[0];
    	if (value) {
    		fundingPartner.clearValue();
    		fundingPocket.clearValue();
    		fundingPartner.disable();
    		fundingPocket.disable();
    		/*dependantChargetype.enable();*/
    	} else {
    		fundingPartner.enable();
    		fundingPocket.enable();
    		/*dependantChargetype.disable();
    		dependantChargetype.clearValue();*/
    	}
    },
    
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
            this.record.beginEdit();
            if (!(this.form.items.get("chargedefinition.form.ischargefromcustomer").checked)) {
            	this.record.set(CmFinoFIX.message.JSChargeDefinition.Entries.IsChargeFromCustomer._name,  false);
            }
            if (!(this.form.items.get("chargedefinition.form.istaxable").checked)) {
            	this.record.set(CmFinoFIX.message.JSChargeDefinition.Entries.IsTaxable._name,  false);
            }
            this.record.endEdit();
            
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
        this.pricingGrid.setParentTemplateData(this.record.get('ID'));
        this.pricingGrid.store.save();
      },
    
    
    setRecord : function(record){
        this.getForm().reset();
        this.pricingGrid.reset();
        this.record = record;
        this.templateID = null;
//    	var sr_combo = this.find('itemId','chargedefinition.form.dependantchargetype')[0];
//    	sr_combo.store.reload({
//    		params: {NotEqualID: this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.ChargeTypeID._name)}
//    	});
        var sr_combo = this.getDependantChargeTypes(this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.ChargeTypeID._name));
    	var partner_combo = this.getFundingPartners();
    	var pocket_combo = this.getPockets(this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.FundingPartnerID._name));
    	this.hideOrShowFundingPartner(this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.IsChargeFromCustomer._name));
        if(!this.record.phantom){
            this.templateID = record.data[CmFinoFIX.message.JSChargeDefinition.Entries.ID._name];
            this.pricingGrid.setParentTemplateData(this.templateID);
            this.pricingGrid.reloadGrid();
        }
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    	sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.DependantChargeTypeName._name));
    	partner_combo.setRawValue(this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.TradeName._name));
    	pocket_combo.setRawValue(this.record.get(CmFinoFIX.message.JSChargeDefinition.Entries.PocketDispText._name));
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

Ext.reg("chargedefinitionform", mFino.widget.ChargeDefinitionForm);