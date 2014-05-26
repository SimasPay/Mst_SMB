/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MFSBillerPartnerForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.MFSBillerPartnerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSBillerPartnerForm, Ext.FormPanel, {
	initComponent : function () {
		this.denominationGrid = new mFino.widget.MFSDenominationsGrid({
	        itemId:'denominationsgrid',
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
	             		xtype:'combo',
	             		fieldLabel: _('Partner Name(Trader Name)'),
	             		itemId : 'mbp.form.partner',
	             		anchor : '95%',
	             		triggerAction: "all",
		                minChars : 2,
		                forceSelection : true,
		                allowBlank: false,
		                lastQuery: '',
		                pageSize : 10,
		                store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner),
		                displayField: CmFinoFIX.message.JSPartner.Entries.TradeName._name,
		                valueField : CmFinoFIX.message.JSPartner.Entries.ID._name,            
		                name: CmFinoFIX.message.JSMFSBillerPartner.Entries.PartnerID._name,
		                listeners: {
		
		            	}
            		},
		           	{
		                xtype : 'textfield',
		                fieldLabel: _("Partner Biller Code"),
		                labelSeparator:':',
		                anchor : '95%',
		                maxLength : 25,
		                name: CmFinoFIX.message.JSMFSBillerPartner.Entries.PartnerBillerCode._name
		            },
					{
		            	fieldLabel: _('Type'),
		            	xtype:'enumdropdown',
		            	itemId : 'mbp.form.billertype',
		            	anchor : '95%',
		            	enumId: CmFinoFIX.TagID.BillerPartnerType,
		            	name: CmFinoFIX.message.JSMFSBillerPartner.Entries.BillerPartnerType._name,
		            	listeners: {
								select :  function(field){
                   					this.findParentByType('mfsbillerpartnerform').onStatusDropdown(field.getValue());
                				}
		            	  }
		            },
		            {
		                xtype : 'enumdropdown',
		                fieldLabel: _("Integration Code"),
		                labelSeparator:':',
		                anchor : '95%',
						allowBlank: false,
		                itemId : 'mbp.form.integrationCode',
		                enumId: CmFinoFIX.TagID.IntegrationCode,
		                name: CmFinoFIX.message.JSMFSBillerPartner.Entries.IntegrationCode._name
		            },
		            {
		                xtype : 'enumdropdown',
		                fieldLabel: _("Charges Included"),
		                labelSeparator:':',
		                anchor : '95%',
		                enumId: CmFinoFIX.TagID.ChargesIncluded,
		                name: CmFinoFIX.message.JSMFSBillerPartner.Entries.ChargesIncluded._name          			                
		            }
		            ]
            }
            ]
        },
        {
            xtype:'tabpanel',
            itemId:'tabpanelTopupDenomination',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            disabled : true,
            defaults:{
                layout:'form',
                bodyStyle:'padding:10px'
            },
            items:[
		    {
		       title: _('Topup Denominations'),
		       autoHeight: true,
		       padding: '0 0 0 0',
		       itemId : 'mbp.form.topupdenominations',
		       items:[ this.denominationGrid ]
		    }
		    ]
        }
        
        ];
        
        mFino.widget.MFSBillerPartnerForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    onStatusDropdown : function(status){
    	if(status==CmFinoFIX.BillerPartnerType.Topup_Denomination){
    		this.find('itemId','tabpanelTopupDenomination')[0].enable();
    	}else{
    		this.find('itemId','tabpanelTopupDenomination')[0].disable();
    	}
    },
    validate : function() {
    	if(this.getForm().isValid()) {
    		if(this.find('itemId','tabpanelTopupDenomination')[0].disabled == true) {
    			return true;
    		} else {
    			return this.denominationGrid.validateDenominationsGrid();
    		}
    		
    	} else {
    		return false;
    	}
    },    
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
            if (this.record.get('ID') > 0) {
            	if(this.find('itemId','tabpanelTopupDenomination')[0].disabled == false) {
            		this.saveGridData();
            	}            	
            } else {
            	this.store.on("write", this.saveGridData, this);
            }
            if(this.store){
                if(this.record.phantom
                    && this.store.getAt(0)!= this.record){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
         
    },
    saveGridData : function() {
        this.denominationGrid.setParentTemplateData(this.record.get('ID'));
        this.denominationGrid.store.save();
    },
    setRecord : function(record){
        this.getForm().reset();
        this.denominationGrid.reset();
        this.record = record;
    	var sr_combo = this.find('itemId','mbp.form.partner')[0];
    	sr_combo.store.baseParams[CmFinoFIX.message.JSPartner.PartnerTypeSearch._name] = CmFinoFIX.BusinessPartnerType.Biller;
    	sr_combo.store.reload({
    		params: {PartnerTypeSearch: CmFinoFIX.BusinessPartnerType.Biller}
    	});
    	if(!this.record.phantom){
            this.templateID = record.data[CmFinoFIX.message.JSMFSBillerPartner.Entries.ID._name];
            this.denominationGrid.setParentTemplateData(this.templateID);
            this.denominationGrid.reloadGrid();
        }
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSMFSBillerPartner.Entries.PartnerName._name));
         if(this.find('itemId','mbp.form.billertype')[0].getValue() == CmFinoFIX.BillerPartnerType.Topup_Denomination){
        	this.find('itemId','tabpanelTopupDenomination')[0].enable();
        }else{
        this.find('itemId','tabpanelTopupDenomination')[0].disable();
        }
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
Ext.reg("mfsbillerpartnerform", mFino.widget.MFSBillerPartnerForm);
