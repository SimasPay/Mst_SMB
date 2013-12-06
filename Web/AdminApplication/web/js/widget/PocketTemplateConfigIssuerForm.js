/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketTemplateConfigIssuerForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    mFino.widget.PocketTemplateConfigIssuerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTemplateConfigIssuerForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 150;
        this.labelPad = 20;
        this.IsDefaultAvailable = false;
        this.items = [    
				{
                    xtype: 'enumdropdown',                   
                    fieldLabel: _('Subscriber Type'),
                    itemId : 'ptc.form.subscriberType',
                    labelSeparator:':',
                    emptyText : _('<select one..>'),
                    anchor:'95%',
                    allowBlank: false,
                    addEmpty : false,
                    editable: false,
                    enumId : CmFinoFIX.TagID.SubscriberType,
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.SubscriberType._name,
					listeners: {
						select: function() {
                            this.findParentByType('pocketTemplateConfigIssuerForm').disableBusinessPartnerType();
                        }
                    }
                },
				 {
                    xtype: 'enumdropdown',                   
                    fieldLabel: _('Business Partner Type'),
                    itemId : 'ptc.form.businessType',
                    labelSeparator:':',
                    anchor:'95%',
                    allowBlank: true,
                    addEmpty : false,
                    editable: false,
                    enumId : CmFinoFIX.TagID.BusinessPartnerType,
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.BusinessPartnerType._name				
                },
				{
                        xtype : "remotedropdown",
                        anchor : '95%',
                        allowBlank: false,
                        blankText : _('KYC is required'),
                        itemId : 'ptc.form.kyclevel',
                        id : 'ptc.form.kyclevel',
                        fieldLabel :kyc,
                        emptyText : _('<select one..>'),
                        RPCObject : CmFinoFIX.message.JSKYCCheck,
                        displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
                        valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
                        name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.KYCLevel._name
                        
                },
                {
                    xtype: 'enumdropdown',                   
                    fieldLabel: _('Pocket Type'),
                    itemId : 'ptc.form.pocketType',
                    labelSeparator:':',
                    emptyText : _('<select one..>'),
                    anchor:'95%',
                    allowBlank: false,
                    addEmpty : false,
                    editable: false,
                    enumId : CmFinoFIX.TagID.PocketType,
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketType._name,
                    listeners: {
                	select: function() {
							this.findParentByType("pocketTemplateConfigIssuerForm").setCommodityType();
                            this.findParentByType('pocketTemplateConfigIssuerForm').getPocketTemplateID();
                        }
                    }
                },
				{
                    xtype: 'enumdropdown',
                    fieldLabel: _('Commodity Type'),
                    anchor:'95%',
                    labelSeparator:':',
                    emptyText : _('<select one..>'),
                    addEmpty : false,
                    itemId : 'ptc.form.commodityType',
                    allowBlank: false,
                    enumId : CmFinoFIX.TagID.Commodity,
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.Commodity._name,
                    listeners:{
                        select: function(){
                            this.findParentByType("pocketTemplateConfigIssuerForm").getPocketTemplateID();
                        }
                    }
                },
				{
                    xtype: 'checkbox',                   
                    fieldLabel: _('Collector Pocket'),
                    itemId : 'ptc.form.collectorPocket',
                    labelSeparator:':',
                    anchor:'95%',
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsCollectorPocket._name,
					listeners:{
                        check: function(){
                            this.findParentByType("pocketTemplateConfigIssuerForm").getPocketTemplateID();
                        }
                    }
                },
                {
                    xtype: 'checkbox',                   
                    fieldLabel: _('Suspense Pocket'),
                    itemId : 'ptc.form.suspencePocket',
                    labelSeparator:':',
                    anchor:'95%',
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsSuspencePocket._name,
					listeners:{
                        check: function(){
                            this.findParentByType("pocketTemplateConfigIssuerForm").getPocketTemplateID();
                        }
                    }
                },
				{
					xtype : "combo",
					fieldLabel :_("Pocket Template"),
					itemId : "ptc.form.pockettemplate",
					allowBlank : false,
					editable : false,
					anchor : '95%',
					triggerAction: "all",
					minChars : 2,
					emptyText : _('<select one..>'),
					forceSelection : true,
					pageSize : 20,
					store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocketTemplate),
					RPCObject : CmFinoFIX.message.JSPocketTemplate,
					displayField: CmFinoFIX.message.JSPocketTemplate.Entries.Description._name,
					valueField : CmFinoFIX.message.JSPocketTemplate.Entries.ID._name,
					name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateID._name
				},
				{
					xtype : "remotedropdown",
					fieldLabel :_("Group"),
					itemId : "ptc.form.group",
					addEmpty: false,
					allowBlank : false,
					editable : false,
					anchor : '95%',					
					emptyText : _('<select one..>'),					
					pageSize : 10,
					store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSGroup),					
					displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
					valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
					name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name	
				},
				{
                    xtype: 'checkbox',                   
                    fieldLabel: _('IsDefault'),
                    itemId : 'ptc.form.isdefault',
                    labelSeparator:':',
                    anchor:'95%',
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsDefault._name                    
                }];
        mFino.widget.PocketTemplateConfigIssuerForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.on("render", function(){
            this.setEditable(this.initialConfig.isEditable);
        });
    },
    
    saveCallback : function(){
        if(this.getForm().isValid()){                       
            this.record.beginEdit();            
			if(this.store){
	            if(this.record.phantom
	                && this.store.getAt(0)!= this.record){
	                this.store.insert(0, this.record);
	            }
	            this.store.save();	            
            	if(this.store.modified.length > 0){
            		 this.store.on("write", function(){
                		this.formWindow.hide();
                    }, this,
                    {
                        single : true
                    });
                }else{                	
                	this.formWindow.hide();
                }
	    	}
        }
    },    
    
    checkDefaultAndSave : function(formWindow){
    	this.formWindow = formWindow;
    	this.getForm().updateRecord(this.record); 
   		var msg = new CmFinoFIX.message.JSPocketTemplateConfigCheck();
   		msg.m_pID = this.record.get("ID");
   		msg.m_pSubscriberType = this.find('itemId','ptc.form.subscriberType')[0].getValue();
   		msg.m_pBusinessPartnerType = this.find('itemId','ptc.form.businessType')[0].getValue();
   		msg.m_pKYCLevel = this.find('itemId','ptc.form.kyclevel')[0].getValue();
   		msg.m_pPocketType = this.find('itemId','ptc.form.pocketType')[0].getValue();
   		msg.m_pCommodity = this.find('itemId','ptc.form.commodityType')[0].getValue();
   		msg.m_pPocketTemplateID = this.find('itemId','ptc.form.pockettemplate')[0].getValue();
   		msg.m_pGroupID = this.find('itemId','ptc.form.group')[0].getValue();
		if ((this.form.items.get("ptc.form.collectorPocket").checked)) {
				msg.m_pIsCollectorPocket = true;
            }else{
            	msg.m_pIsCollectorPocket = false;
			}
            if ((this.form.items.get("ptc.form.suspencePocket").checked)) {
            	msg.m_pIsSuspencePocket = true;
            }else{
            	msg.m_pIsSuspencePocket = false;
			}
			if ((this.form.items.get("ptc.form.isdefault").checked)) {
				msg.m_pIsDefault = true;
            }else{
            	msg.m_pIsDefault = false;
			}
		ResponseMsg = this.getDefaultPocketTemplateConfigInfo(msg,this.saveCallback);		
    },
    
    getDefaultPocketTemplateConfigInfo : function(msgToSend, saveCallback){
        var BufToSend = new FIX.CMultiXBuffer();
        msgToSend.ToFIX(BufToSend);
        Ext.Ajax.request({
            url: mFino.DATA_URL,
            method: 'POST',
            scope: this,
            xmlData: BufToSend.DataPtr(),
            success: function(response,options) { 
                    if (!(response.responseText)) {
                        throw {
                            message: "FIXReader.read: FIX Message not available"
                        };
                    }
                    var Buf = new FIX.CMultiXBuffer();
                    var ResponseMsg = new FIX.CFIXMsg();
                    Buf.Append(response.responseText);
                    ResponseMsg = ResponseMsg.FromFIX(Buf);
					if (!ResponseMsg) {
                        throw {
                            message: "FIXReader.read: Invalid FIX Message"
                        };
                    }
					if(ResponseMsg.m_pErrorCode == CmFinoFIX.ErrorCode.DuplicatePocketTemplateConfig) {
						//when ptc with exact config exits
						Ext.ux.Toast.msg(_("Error"), _("PTC with similar configuration exists"),5);
					} else if(ResponseMsg.m_pErrorCode== CmFinoFIX.ErrorCode.LastDefaultPocketTemplateConfig) {
						//when the default ptc(one n only) is tried to set as non-dafault
						Ext.ux.Toast.msg(_("Error"), _("Atleast one configuration need to be default"),5);
					} else if(ResponseMsg.m_pErrorCode == CmFinoFIX.ErrorCode.DefaultPTCwithSimillarConfigExists){
						//when ptc is created where already ptc with similar config exits
						if(msgToSend.m_pIsDefault == true && this.record.isModified(CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsDefault._name)) {
							Ext.Msg.confirm('Confirmation','Do you want to set this configuration as default?',function(btn, text){  
        						if(btn == 'yes'){
        							//this.record.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsDefault._name,  true);
        							this.saveCallback();
      							}else{  
           						}  
    						},this);
						} else {
							this.saveCallback();
						}					    	
				    } else if(ResponseMsg.m_pErrorCode== CmFinoFIX.ErrorCode.NoError){	
				    	//when new ptc is created
				    	if(msgToSend.m_pIsDefault == false) {
				    		Ext.Msg.confirm('Confirmation','Setting the current PTC as default for this config type',function(btn, text){  
        						if(btn == 'yes'){
        							this.record.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsDefault._name,  true);
        							this.saveCallback();
      							}else{  
           						}  
    						},this);							
						} else {
							this.saveCallback();
						}				    	
				    }
                    return ResponseMsg;

            },
            failure: function(response, options) {
            },
            callback: function(opt,success,resp){
            	//this.saveCallback();
            }
        });
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        var kyc = this.find('itemId', 'ptc.form.kyclevel')[0];
        kyc.setValue(record.get(CmFinoFIX.message.JSPocketTemplateConfig.Entries.KYCLevel._name));
        this.getForm().clearInvalid();
		var group_combo=this.find('itemId', 'ptc.form.group')[0];
        group_combo.store.load();
        group_combo.setRawValue(record.get(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupName._name));
    },

    onChangePT : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSPocketTemplateConfig.Entries.Description._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSPocketTemplateConfigCheck();
            msg.m_pPocketTemplateName = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    onChangeType : function(){
    	 var value = this.form.items.get("pocketType").getValue();
      if(Number(value) === CmFinoFIX.PocketType.BankAccount){
    	  this.form.items.get('bankAccount').enable();
    	  this.form.items.get('typeofcheck').enable();
        }else{
        	 this.form.items.get('bankAccount').disable();
        	 this.form.items.get('typeofcheck').disable();
        	 this.form.items.get('typeofcheck').setValue(CmFinoFIX.TypeOfCheck.None);
        	 this.form.items.get('bankAccount').setValue("");
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
    },

    setEditable : function(isEditable){
        if(isEditable){
            this.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield'|| item.getXType()=='checkbox'||item.getXType()=='checkboxgroup'||item.getXType()=='enumdropdown') {
                    //enable the item
                    item.enable();
                }
            });
        }else{
            this.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield'|| item.getXType()=='checkbox'||item.getXType()=='checkboxgroup'||item.getXType()=='enumdropdown') {
                    //Disable the item
                    item.disable();
                }
            });
        }
    },

	getPocketTemplateID : function(){
    	var p_combo = this.find('itemId','ptc.form.pockettemplate')[0];
    	p_combo.clearValue();
    	p_combo.store.baseParams[CmFinoFIX.message.JSPocketTemplate.PocketTypeSearch._name] =
    		this.find('itemId','ptc.form.pocketType')[0].getValue();
    	p_combo.store.baseParams[CmFinoFIX.message.JSPocketTemplate.CommodityTypeSearch._name] = 
    		this.find('itemId','ptc.form.commodityType')[0].getValue();
		p_combo.store.baseParams[CmFinoFIX.message.JSPocketTemplate.IsCollectorPocketAllowed._name] = 
    		this.find('itemId','ptc.form.collectorPocket')[0].getValue();
		p_combo.store.baseParams[CmFinoFIX.message.JSPocketTemplate.IsSuspencePocketAllowed._name] = 
    		this.find('itemId','ptc.form.suspencePocket')[0].getValue();
    	p_combo.store.reload({
    		params: {
    			PocketTypeSearch : this.find('itemId','ptc.form.pocketType')[0].getValue(),
    			CommodityTypeSearch : this.find('itemId','ptc.form.commodityType')[0].getValue(),
    			IsCollectorPocketAllowed : this.find('itemId','ptc.form.collectorPocket')[0].getValue(),
    			IsSuspencePocketAllowed : this.find('itemId','ptc.form.suspencePocket')[0].getValue()
    		}
    	});
    },
	
	disableBusinessPartnerType : function(){
		 if(this.find('itemId','ptc.form.subscriberType')[0].getValue()=="0"){
			this.find('itemId','ptc.form.businessType')[0].setValue("");
			this.find('itemId','ptc.form.businessType')[0].allowBlank=true;
			this.find('itemId','ptc.form.businessType')[0].disable();		
			this.find('itemId','ptc.form.kyclevel')[0].enable();
		 }else{
			this.find('itemId','ptc.form.businessType')[0].enable();
			this.find('itemId','ptc.form.businessType')[0].allowBlank=false;
			this.find('itemId','ptc.form.kyclevel')[0].setValue("3");
			this.find('itemId','ptc.form.kyclevel')[0].disable();
		 }
	},
	
	setCommodityType : function(){
		 if(this.find('itemId','ptc.form.pocketType')[0].getValue()=="3"){
			this.find('itemId','ptc.form.commodityType')[0].setValue("4");
			this.find('itemId','ptc.form.commodityType')[0].disable();
		 }else{
			this.find('itemId','ptc.form.commodityType')[0].enable();
		 }
	}
});

Ext.reg("pocketTemplateConfigIssuerForm", mFino.widget.PocketTemplateConfigIssuerForm);

