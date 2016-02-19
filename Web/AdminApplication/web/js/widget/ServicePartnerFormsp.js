/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerFormsp = function (config) {
	 var localConfig = Ext.apply({}, config);
	  	 localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.ServicePartnerFormsp.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ServicePartnerFormsp, Ext.form.FormPanel, {
	  initComponent : function ()
    {
		this.subscribercombo =  new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSSubscriberMDN);
		this.labelWidth = 120;
        this.labelPad = 20;
        this.autoScroll = true;
        this.frame = true;
        this.items = [
        {
            xtype: 'fieldset',
            title : _('Details'),
            layout : 'column',
            autoHeight: true,
            width:860,
            items : [
            {
                columnWidth: 0.5,
                xtype: 'panel',
                itemId:'details',
                layout: 'form',
                items:[
       				{
    	                xtype : 'textfield',
    	                fieldLabel: _("Nama Agen (Sesuai KTP)"),
    	                allowBlank: false,
    	                anchor : '100%',
    	                maxLength : 100,
    	                itemId : 'servicepartner.form.username',
    	                name: CmFinoFIX.message.JSAgent.Username._name,
    	                listeners: {
    	                    change: function(field) {
    	                        this.findParentByType('ServicePartnerFormsp').onName(field);
    	                    }
    	                }
    	            },
	               {
	                   xtype : "textfield",
	                   fieldLabel :_("Nomor KTP"),
	                   allowBlank: false,
	                   anchor : '100%',
	                   maxLength : 100,
	                   itemId  : 'servicepartner.form.ktpid',
	                   name: CmFinoFIX.message.JSAgent.KTPID._name,
	               },
	               {
	                   xtype : "textfield",
	                   fieldLabel :_("Nomor Telepon Selular (Handphone)"),
	                   allowBlank: false,
	                   anchor : '100%',
	                   maxLength : 100,
	                   vtype: 'smarttelcophoneAddMore',
	                   itemId : 'servicepartner.form.mdn',
	                   name: CmFinoFIX.message.JSAgent.MDN._name
	               },
                   {
                       xtype : 'numberfield',
                       fieldLabel : _('Rekening Sinarmas'),
                       allowDecimals:false,
                       allowBlank: false,
                       anchor : '100%',
                       maxLength : 100,
                       blankText : _('AccountnumberofBankSinarmas is required'),
                       itemId  : 'servicepartner.form.AccountnumberofBankSinarmas',
                       name: CmFinoFIX.message.JSAgent.AccountnumberofBankSinarmas._name
                   },
	               {
	                   xtype: 'remotedropdown',
	                   fieldLabel :_("Cabang"),
	                   anchor : '100%',
	                   allowBlank: false,
	                   addEmpty : false,
	                   itemId : 'servicepartner.form.BranchofBankSinarmas',
	                   emptyText : '<Select one..>',
	                   name: CmFinoFIX.message.JSAgent.BranchofBankSinarmas._name,
	                   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSBranchCodes), 
	                   displayField: CmFinoFIX.message.JSBranchCodes.Entries.BranchName._name, 
	                   valueField : CmFinoFIX.message.JSBranchCodes.Entries.ID._name, 
	                   hiddenName : CmFinoFIX.message.JSAgent.BranchofBankSinarmas._name,
	                   pageSize: 10,
	                   params: {start:0, limit:10},
		                listeners: {
		                    select: function(field) {
		                        this.findParentByType('ServicePartnerFormsp').onBranchName(field);
		                    }
		                }
	               },
/*                   {
						 xtype : "combo",
						 anchor : '100%',
						 //fieldLabel :_("Provincial"),
						 fieldLabel :_("Propinsi"),
						 itemId : 'servicepartner.form.ProvincialCom',
						 triggerAction: "all",
						 emptyText : '<Select one..>',
						 name: CmFinoFIX.message.JSAgent.ProvincialCom._name,
						 store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSProvince), 
						 displayField: CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
						 valueField : CmFinoFIX.message.JSProvince.Entries.ID._name,
						 hiddenName : CmFinoFIX.message.JSAgent.ProvincialCom._name,
						 listeners: {
							 	select: function(field) {
							 	this.findParentByType('ServicePartnerFormsp').onProvince2(field);
						    }
						}
	                },
	                {
	                	 xtype : "combo",
	                     anchor : '100%',
	                     //fieldLabel :_("Region/City"),
	                     fieldLabel :_("Kabupaten/Kota"),
	                     itemId : 'servicepartner.form.CityCom',
	                     triggerAction: "all",
	                     emptyText : '<Select one..>',
	                     mode: 'local',
	                     name: CmFinoFIX.message.JSAgent.CityCom._name,
	                     store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSProvinceRegion), 
	                     displayField: CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
	                     valueField : CmFinoFIX.message.JSProvinceRegion.Entries.ID._name,
	                     hiddenName : CmFinoFIX.message.JSAgent.CityCom._name,
						 listeners: {
							 	select: function(field) {
							 	this.findParentByType('ServicePartnerFormsp').onProvinceRegion2(field);
						    }
						}
	                },
	                {
	                	 xtype : "combo",
	                     anchor : '100%',
                         //fieldLabel :_("District"),
                         fieldLabel :_("Kecamatan"),
                         itemId : 'servicepartner.form.DistrictCom',
	                     triggerAction: "all",
	                     emptyText : '<Select one..>',
	                     mode: 'local',
	                     name: CmFinoFIX.message.JSAgent.DistrictCom._name,
  	                     store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSDistrict), 
	                     displayField: CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
	                     valueField : CmFinoFIX.message.JSDistrict.Entries.ID._name, 
	                     hiddenName : CmFinoFIX.message.JSAgent.DistrictCom._name,
						 listeners: {
							 	select: function(field) {
							 	this.findParentByType('ServicePartnerFormsp').onDistrict2(field);
						    }
						}
	                },
	                {
	                   xtype : "combo",
	                   anchor : '100%',
                       //fieldLabel :_("Village"),
                       fieldLabel :_("Kelurahan/Desa"),
                       itemId : 'servicepartner.form.VillageCom',
	                   triggerAction: "all",
	                   emptyText : '<Select one..>',
	                   mode: 'local',
  	                   name: CmFinoFIX.message.JSAgent.VillageCom._name,
	                   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSVillage), 
	                   displayField: CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
	                   valueField : CmFinoFIX.message.JSVillage.Entries.ID._name, 
	                   hiddenName : CmFinoFIX.message.JSAgent.VillageCom._name,
	                },*/
	               {
	                   xtype : "hidden",
	                   fieldLabel :_('Branch code'),
	                   anchor : '100%',
	                   allowBlank: true,
	                   maxLength : 255,
	                   itemId : 'servicepartner.form.BranchCode',
	                   name : CmFinoFIX.message.JSAgent.BranchCode._name
	               }
	            ]
            },
            {
                columnWidth: 0.5,
                xtype: 'panel',
                layout: 'form',
                itemId:'details2',
                labelWidth:120,
                items:[]
            },
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : []
            }]
        }];
        mFino.widget.ServicePartnerFormsp.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.subscribercombo.on("load", this.onLoad.createDelegate(this));
    },
    
    onLoad: function(){
     var record = this.subscribercombo.getAt(0);
   	 var convertAgentStore = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSAgent);
       var mrecord = this.record;
       if(record!=null&&record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberType._name)===CmFinoFIX.SubscriberType.Partner){
    	   Ext.MessageBox.alert(_("Alert"), _("MDN already registered as Partnerrrrrr or Agentttttttt"));   
         }else{
        	 if(record!=null){
             Ext.apply(mrecord.data, record.data);
        	 }else{
        		 mrecord.set(CmFinoFIX.message.JSAgent.Entries.MDN._name,this.find("itemId", "servicepartner.form.mobileno")[0].getValue());
        		 mrecord.set(CmFinoFIX.message.JSAgent.Entries.Language._name,CmFinoFIX.Language.English);
        		 mrecord.set(CmFinoFIX.message.JSAgent.Entries.Timezone._name,SYSTEM_DEFAULT_TIMEZONE);
        		 mrecord.set(CmFinoFIX.message.JSAgent.Entries.Currency._name,SYSTEM_DEFAULT_CURRENCY);
                 mrecord.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name,1);
                 mrecord.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupName._name,'ANY');
                 // mrecord.set(CmFinoFIX.message.JSAgent.Entries.TypeAgentObject._name,'simaspay');
        	 }
        	 mrecord.set(CmFinoFIX.message.JSAgent.Entries.PartnerStatus._name, CmFinoFIX.MDNStatus.Initialized);
        	 this.subscribercombo.remove(record);
        	 mrecord.data['ID'] = null;
        	 this.setRecord(mrecord);
        	 this.onStatusDropdown( CmFinoFIX.MDNStatus.Initialized);
        	 this.find("itemId", "servicepartner.form.mobileno")[0].disable(); 
        	 this.find("itemId", "servicepartner.form.status")[0].disable(); 
        	 this.find("itemId", "servicepartner.form.username")[0].enable();
        	 }
    },
/*    onProvince2 : function(field){
        var value=field.getValue();
        alert(value);
    	var region_combo = this.find('itemId','servicepartner.form.CityCom')[0];
    	region_combo.clearValue();
    	region_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdProvince : value
    		}
    	});
    },
    onProvinceRegion2 : function(field){
        var value=field.getValue();
        alert(value);
    	var district_combo = this.find('itemId','servicepartner.form.DistrictCom')[0];
    	district_combo.clearValue();
    	district_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdRegion : value
    		}
    	});
    },
    onDistrict2 : function(field){
        var value=field.getValue();
        alert(value);
    	var village_combo = this.find('itemId','servicepartner.form.VillageCom')[0];
    	village_combo.clearValue();
    	village_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdDistrict : value
    		}
    	});
    },*/
/*    disableNotPermittedItems: function(){
        var checkAbleItems = ['servicepartner.form.status'];//, 'servicepartner.form.DCT', 'servicepartner.form.parentId','servicepartner.form.mdn','servicepartner.form.sourceIp'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
    },*/
/*    enablePermittedItems: function(){
        var checkAbleItems = ['servicepartner.form.status', 'servicepartner.form.DCT', 'servicepartner.form.parentId'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            // The above items are disabled only for Edit operation. They should be available for Add.
          //  checkItem.enable();
        }
    },*/
/*    onPartnerType : function(partnerType){
    	var st_combo = this.find('itemId','servicepartner.form.type')[0];
    	st_combo.clearValue();
    	st_combo.store.reload({
    		params: {
    		TagIDSearch : partnerType
    		}
    	});
    	st_combo.store.sort(CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name);
    },*/
 /*   onSpecificPartnerType : function(partnerType){
    	var partnerTagID = CmFinoFIX.TagID.BusinessPartnerType;
    	var st_combo = this.find('itemId','servicepartner.form.type')[0];
    	st_combo.clearValue();
    	st_combo.store.reload({
    		params: {
    		TagIDSearch : partnerTagID ,
    		EnumCode : partnerType
    		}
    	});
    	st_combo.store.sort(CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name);
    },*/
    onStatusDropdown : function(status){
        if(status == CmFinoFIX.SubscriberStatus.PendingRetirement||status==CmFinoFIX.SubscriberStatus.Retired){
        	this.getForm().items.each(function(item) {
				if (item.getXType() == 'textfield'
						|| item.getXType() == 'combo'
						|| item.getXType() == 'textarea'
						|| item.getXType() == 'numberfield'
						|| item.getXType() == 'datefield'
						|| item.getXType() == 'checkboxgroup'
						|| item.getXType() == 'checkbox'
						|| item.getXType() == 'enumdropdown') {
							item.disable();
				}
			});
            if (status == CmFinoFIX.SubscriberStatus.Retired && typeof(this.record) != "undefined") {
            	this.record.set(CmFinoFIX.message.JSAgent.Entries.IsForceCloseRequested._name, true);
            }
        }else {
        	this.getForm().items.each(function(item) {
        		if(item.getItemId() != 'servicepartner.form.username'){
				if (item.getXType() == 'textfield'
						|| item.getXType() == 'combo'
						|| item.getXType() == 'textarea'
						|| item.getXType() == 'numberfield'
						|| item.getXType() == 'datefield'
						|| item.getXType() == 'checkboxgroup'
						|| item.getXType() == 'checkbox'
						|| item.getXType() == 'remotedropdown'
						|| item.getXType() == 'enumdropdown') {
					
							item.enable();
					}
				}
			});
        	
            if (status == CmFinoFIX.SubscriberStatus.Active) {
            	this.find('itemId','servicepartner.form.status')[0].enable();
            } else {
            	this.find('itemId','servicepartner.form.status')[0].disable();
            }
        }
        this.getForm().items.get('servicepartner.form.mobileno').disable();
    },
    setReadOnly : function(readOnly) {
    	this.getForm().items.each(function(item) {
				if (item.getXType() == 'textfield'
						|| item.getXType() == 'combo'
						|| item.getXType() == 'textarea'
						|| item.getXType() == 'numberfield'
						|| item.getXType() == 'datefield'
						|| item.getXType() == 'checkboxgroup'
						|| item.getXType() == 'enumdropdown'
						|| item.getXType() == 'remotedropdown') {
					// Disable the item
					item.readOnly=true;
				}if (item.getXType() == 'checkbox'){
					item.disable();
				}
			});
	},
    save : function(){
    	//this.checkServicePartnerType(this.getForm().findField('servicepartner.form.type'));
    	if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            var notiValue = 0;
            
            var resValue = 0;

            if(this.store){
                if(this.record.phantom && !(this.record.store)){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },   
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);

        var partnerId = this.record.get(CmFinoFIX.message.JSAgent.Entries.ID._name);

        var parentID = this.find('itemId', 'servicepartner.form.parentId')[0];
        if(parentID) {
            parentID.setRawValue(record.data["ParentName"]);
        }
        var pt_combo = this.find('itemId','servicepartner.form.type')[0];
       // pt_combo.setRawValue(this.record.get(CmFinoFIX.message.JSAgent.Entries.BusinessPartnerTypeText._name));
        
        var resValue = record.get(CmFinoFIX.message.JSAgent.Entries.Restrictions._name);
        this.getForm().clearInvalid();
    },
    resetAll : function() {
    	for ( var j = 0; j < this.form.items.length; j++) {
			this.form.items.get(j).setValue(null);
		}
    },
    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
    
    onName : function(field){
        var value = this.record.get(CmFinoFIX.message.JSUsers.Entries.Username._name);
        if(!value||(field.getValue().toUpperCase() != value.toUpperCase())){
            var msg = new CmFinoFIX.message.JSUsernameCheck();
            msg.m_pUsername = field.getValue();
            msg.m_pCheckIfExists = true;
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
/*    onTradeName : function(field){
        var value = this.record.get(CmFinoFIX.message.JSAgent.Entries.TradeName._name);
        if(!value||(field.getValue().toUpperCase() != value.toUpperCase())){
            var msg = new CmFinoFIX.message.JSTradeNameCheck();
            msg.m_pTradeName = field.getValue();
            msg.m_pCheckIfExists = true;
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },*/
/*    onPartnerCode : function(field){
        var value = this.record.get(CmFinoFIX.message.JSAgent.Entries.PartnerCode._name);
        if(!value||(field.getValue().toUpperCase() != value.toUpperCase())){
            var msg = new CmFinoFIX.message.JSDuplicatePartnerCodeCheck();
            msg.m_pPartnerCode = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },*/

    onStoreUpdate: function(){
        this.setRecord(this.record);
    },
/*    onMDN : function(field){ 
    	if(this.getForm().isValid()){
    	this.subscribercombo.baseParams[CmFinoFIX.message.JSSubscriberMDN.ExactMDNSearch._name] =field.getValue(); 
    	this.subscribercombo.load();
    	}
    },*/
       
/*    checkServicePartnerType : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSCheckServicePartner();
            msg.m_pPartnerTypeSearch = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },*/
    setCombo : function(combo){
    	subscribercombo = combo;
    },
/*    onSuspendClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be suspended"));
            this.form.items.get("Suspended").setValue(false);
    	} else {
        	var currentStatus = this.record.data[CmFinoFIX.message.JSAgent.Entries.PartnerStatus._name];
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("servicepartner.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            } else {
            	if (CmFinoFIX.SubscriberStatus.Suspend === currentStatus) {
            		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
            	}
                this.form.items.get("servicepartner.form.status").setValue(currentStatus);
            }
    	}
    },*/
/*    onSelfSuspendClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be suspended"));
            this.form.items.get("SelfSuspended").setValue(false);
    	} 
    },*/
/*    onSecurityLockClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be Locked"));
            this.form.items.get("SecurityLocked").setValue(false);
    	} else {
        	var currentStatus = this.record.data[CmFinoFIX.message.JSAgent.Entries.PartnerStatus._name];
            if(this.form.items.get("SecurityLocked").checked) {
                this.form.items.get("servicepartner.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
                if(this.form.items.get("Suspended").checked) {
                    this.form.items.get("servicepartner.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
                }
            } else {
            	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
            		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
            	}
                this.form.items.get("servicepartner.form.status").setValue(currentStatus);
            }
    	}
    },*/
/*    onAbsoluteLockClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be Locked"));
            this.form.items.get("AbsoluteLocked").setValue(false);
    	} else {
        	var currentStatus = this.record.data[CmFinoFIX.message.JSAgent.Entries.PartnerStatus._name];
            if(this.form.items.get("AbsoluteLocked").checked) {
                this.form.items.get("servicepartner.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
                if(this.form.items.get("Suspended").checked) {
                    this.form.items.get("servicepartner.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
                }
            } else {
            	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
            		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
            	}
                this.form.items.get("servicepartner.form.status").setValue(currentStatus);
            }
    	}
    },*/
    onBranchName : function(field){
        var value=field.getValue();
        //var value2=field.getRawValue();
        this.form.items.get("servicepartner.form.BranchCode").setValue(value);
        //this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(value2);
    },
    further : function(formWindow){
    	if(this.getForm().isValid()){
            var msg= new CmFinoFIX.message.JSAgent();
            
            var values = this.form.getValues();
            var Username = values[CmFinoFIX.message.JSAgent.Username._name];
            var KTPID = values[CmFinoFIX.message.JSAgent.KTPID._name];
            var MDN = values[CmFinoFIX.message.JSAgent.MDN._name];
            var AccountnumberofBankSinarmas = values[CmFinoFIX.message.JSAgent.AccountnumberofBankSinarmas._name];
            var BranchofBankSinarmas = values[CmFinoFIX.message.JSAgent.BranchofBankSinarmas._name];
            var BranchCode = values[CmFinoFIX.message.JSAgent.BranchCode._name];
            
            msg.m_pUsername = Username;
            msg.m_pKTPID = KTPID;
            msg.m_pMDN = MDN;
            msg.m_pAccountnumberofBankSinarmas = AccountnumberofBankSinarmas;
            msg.m_pBranchofBankSinarmas = BranchofBankSinarmas;
            msg.m_pBranchCode = BranchCode;
            msg.m_pTypeAgentObject = "agentprimarydata";
            msg.m_paction = "create";
            
            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);
            
            Ext.apply(params, {
            success :  function(response){
       		 if(response.m_psuccess == false || response.m_pErrorCode == 1)
       		 {
                  Ext.Msg.show({
                      title: 'Info',
                      minProgressWidth:600,
                      msg: response.m_pErrorDescription,
                      buttons: Ext.MessageBox.OK,
                      multiline: false
                  });
       		 }
       
	        if(response.m_psuccess == true || response.m_pErrorCode == 0)
	        {
		         var ConfirmAgent = new mFino.widget.FormWindowsp(Ext.apply({
		               form : new mFino.widget.ServicePartnerFormspAdd(this),
		               height : 600,
		               width:900,
		               title : _("PENDAFTARAN AGEN LAKU PANDAI BANK SINARMAS"),
		               mode:"addagentdata"
		         },this));
		         ConfirmAgent.show(); 
		         ConfirmAgent.form.setDetails(
	 				 msg.m_pUsername,
	        		 msg.m_pKTPID,
	        		 msg.m_pMDN,
	        		 msg.m_pAccountnumberofBankSinarmas,
	        		 msg.m_pBranchofBankSinarmas,
	        		 msg.m_pBranchCode,
	        		 response.m_pAlamatInAccordanceIdentity,
	        		 response.m_pRTAl,
	        		 response.m_pRWAl,
	        		 response.m_pVillageAl,
	        		 response.m_pDistrictAl,
	        		 response.m_pCityAl,
	        		 response.m_pProvincialAl,
	        		 response.m_pPotalCodeAl,
	        		 response.m_pUserBankBranch);
	        }
       	 },
            failure : function(response){
                Ext.Msg.show({
                    title: 'Error',
                    minProgressWidth:250,
                    msg: "Your Registration is having a problem. Please contact Customer Care",
                    buttons: Ext.MessageBox.OK,
                    multiline: false
                });
            }});
            return params;
         }
   },
});

/*
 * Partner / Agent Restrictions
 **/
/*var partnerRestrictions = {
    title: _(''),
    autoHeight: true,
    width: 300,
    layout: 'form',
    items : [{
        layout : 'column',
        autoHeight: true,
        columns: 2,
        style : {
            margin: '5px'
        },
        items: [
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'SecurityLocked',
            boxLabel: securitylocked,
            listeners: {
                check: function() {
                    this.findParentByType("ServicePartnerFormsp").onSecurityLockClick();
                }
            }
        },
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'AbsoluteLocked',
            boxLabel: absolutelocked,
            listeners: {
                check: function() {
                    this.findParentByType("ServicePartnerFormsp").onAbsoluteLockClick();
                }
            }
        },
//        {
//            columnWidth: 0.5,
//            xtype : 'checkbox',
//            itemId : 'SelfSuspended',
//            boxLabel: selfsuspended,
//            listeners: {
//                check: function() {
//                    this.findParentByType("ServicePartnerFormsp").onSelfSuspendClick();
//                }
//            }
//        },
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'Suspended',
            boxLabel: suspended,
            listeners: {
                check: function() {
                    this.findParentByType("ServicePartnerFormsp").onSuspendClick();
                }
            }
        }]
    }]
};*/


Ext.reg("ServicePartnerFormsp", mFino.widget.ServicePartnerFormsp);
