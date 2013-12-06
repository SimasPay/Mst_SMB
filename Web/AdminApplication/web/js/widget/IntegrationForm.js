/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.IntegrationForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.IntegrationForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.IntegrationForm, Ext.FormPanel, {
    initComponent : function () {
		this.ipMappingGrid = new mFino.widget.IPMappingGrid({
	        bodyStyle:'padding:5px',
	        height: 170,
	        frame:false,
	        border:true,
	        dataUrl:this.initialConfig.dataUrl			
		});	
        this.labelWidth = 150;
        this.labelPad = 20;
        this.items = [
        {
            xtype : "textfield",
            fieldLabel :_("Institution ID"),
            anchor : '95%',
            allowBlank : false,
            name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.InstitutionID._name
        },
        {
            xtype : "textfield",
            fieldLabel :_("Integration Name"),
            anchor : '95%',
            allowBlank : false,
            name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IntegrationName._name
        },
        
        {
        	   xtype : "remotedropdown", 
	    	   fieldLabel: _('Partner Code'), 
	    	   labelSeparator : '', 	    	   
	    	   pageSize : 10,
	    	   params:{start:0, limit:10},
	    	   anchor : '98%',
	    	   itemId :'integrationForm.PartnerCode',
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner), 
	    	   displayField: CmFinoFIX.message.JSPartner.Entries.PartnerCode._name, 
	    	   valueField : CmFinoFIX.message.JSPartner.Entries.ID._name, 
	    	   hiddenName : CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.PartnerID._name, 
	    	   name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.PartnerID._name	    	   
	    },
     {
	    	   xtype : "remotedropdown", 
	    	   fieldLabel: _('MFSBiller Code'), 
	    	   labelSeparator : '', 	    	   
	    	   pageSize : 10,
	    	   params:{start:0, limit:10},
	    	   anchor : '98%', 
	    	   itemId : 'integrationForm.MFSBillerCode',
	    	   emptyText : _('<select  >'), 
	    	   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSMFSBiller), 
	    	   displayField: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerCode._name, 
	    	   valueField : CmFinoFIX.message.JSMFSBiller.Entries.ID._name, 
	    	   hiddenName : CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.MFSBillerId._name, 
	    	   name: CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.MFSBillerId._name	    	   
	      },
          {
          	xtype :"checkbox",
          	anchor : '95%',
          	fieldLabel :_("Is Authentication Key Enabled"),
			name :CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsAuthenticationKeyEnabled._name    				
		  },
		  {
	          	xtype :"checkbox",
	          	anchor : '95%',
	          	itemId: 'integrationForm.IsLoginEnabled',
	          	fieldLabel :_("Is Login Enabled"),
				name :CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsLoginEnabled._name,
					listeners:{
					check: function(field) {
                			this.findParentByType('integrationForm').hideOrShowIsAppTypeCheckEnabled(field.getValue());
                		}
				}   				
		  },
		  {
	          	xtype :"checkbox",
	          	anchor : '95%',
	          	itemId: 'integrationForm.isAppTypeCheckEnabled',
	          	fieldLabel :_("Is AppType Check Enabled"),
				name :CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.IsAppTypeCheckEnabled._name    				
			  },
		  this.ipMappingGrid
        
        ];        

        mFino.widget.IntegrationForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.on("render", function(){
        	this.reloadRemoteDropDown();
            this.find('itemId','integrationForm.isAppTypeCheckEnabled')[0].disable();
        });
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
	    	if(item.getXType() == 'remotedropdown') {
	    		item.reload();
	    	}
    	});
    },
    
    hideOrShowIsAppTypeCheckEnabled : function(value) {
		var isAppTypeCheckEnabled = this.find('itemId','integrationForm.isAppTypeCheckEnabled')[0];
		if (value) {
			isAppTypeCheckEnabled.enable();
    	} else {
    		isAppTypeCheckEnabled.setValue(false);
    		isAppTypeCheckEnabled.disable();
    	}
    },
    
    save : function(){
        if(this.getForm().isValid()){
			var validationResult = this.ipMappingGrid.validateIPMappingGrid();
        	if (validationResult == -1) {
        		return;
        	}
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
        this.ipMappingGrid.setTemplateID(this.record.get('ID'));
        this.ipMappingGrid.store.save();          
      },
    
    
    setRecord : function(record){
        this.getForm().reset();
        this.ipMappingGrid.reset();
        this.record = record;
        this.templateID = null;
        var pc_combo = this.find('itemId','integrationForm.PartnerCode')[0];
        var mc_combo = this.find('itemId','integrationForm.MFSBillerCode')[0];
    	if(!this.record.phantom){
        	this.templateID = record.data[CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.ID._name];
            this.ipMappingGrid.setTemplateID(this.templateID);
            this.ipMappingGrid.reloadGrid(this.templateID);
        } 
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        pc_combo.setRawValue(this.record.get(CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.PartnerCode._name));
        mc_combo.setRawValue(this.record.get(CmFinoFIX.message.JSIntegrationPartnerMapping.Entries.MFSBillerCode._name));
    },
    
    disableGrid : function() {
    	this.ipMappingGrid.disable();
    },
	
	validate : function() {
    	if(this.getForm().isValid()) {
			var pc_combo = this.find('itemId','integrationForm.PartnerCode')[0];
			var mc_combo = this.find('itemId','integrationForm.MFSBillerCode')[0];
			if(pc_combo.getValue() == "" && mc_combo.getValue() == ""){
				Ext.ux.Toast.msg(_('Error'), _("Both Partner and MFSBiller can't be null. One of them should exist"));
				return false;
			}
    		return true;
    	} else {
    		return false;
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

Ext.reg("integrationForm", mFino.widget.IntegrationForm);

