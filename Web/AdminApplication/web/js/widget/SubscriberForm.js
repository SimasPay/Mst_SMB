/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.SubscriberForm.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.SubscriberForm, Ext.form.FormPanel, {
	
    initComponent : function ()
    {
    	this.subscribercombo = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSSubscriberMDN);
        this.labelWidth = 120;
        this.labelPad = 20;
        this.autoScroll = true;
        this.frame = true;
   
        this.items = [ {
            layout:'column',
            
            items : [
            {
                columnWidth: 0.5,
                items : [
                {
                    //width: 300,
                    autoHeight: true,
                    allowBlank: false,
                    layout: 'form',
                    items: [
                            {
                                xtype : 'textfield',
                                fieldLabel: mobile,
                                itemId : 'sub.form.mobileno',
                                name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
                                allowBlank: false,
                                vtype: 'smarttelcophoneAddMore',
                                listeners: {
                                    change: function(field) {
                                    	this.findParentByType('subscriberform').onMDN(field);
                                    }
                                },
                                emptyText: _(''),
                                blankText : _('Mobile Number is required'),
                                anchor : '100%'
                            },                            
                   {
                        xtype : "remotedropdown",
                        anchor : '100%',
                        allowBlank: false, 
                        blankText : _('KYC is required'),
                        itemId : 'sub.form.KYCLevel',
                        id : 'sub.form.KYCLevel',
                        fieldLabel :kyc,
                        emptyText : _('<select one..>'),
                        RPCObject : CmFinoFIX.message.JSKYCCheck,
                        displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
                        valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
                        name: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
                        listeners: {
                            select: function(field) {
                            	var kyc= field.getValue();
                            	if(kyc === 0) {
                            		Ext.MessageBox.alert(_("Alert"), _("Subscriber with NoKyc is not allowed to create"));
                            		field.clearValue();
                            		return;
                            	}
                            	var kf_combo = Ext.getCmp("sub.form.kycfield");
                            	kf_combo.store.reload({
                    				params: {KYCFieldsLevelID : kyc },
                    				callback:function(){}
                    			});
                            	var subscriberForm = this.findParentByType('subscriberform');
                            	subscriberForm.onKYCDropdown(kyc);
                            	var subscriberID = subscriberForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name);
                            	if(subscriberID == null) { //while subscriber registration
                            		if(kyc == CmFinoFIX.RecordType.SubscriberFullyBanked
                            				|| kyc == CmFinoFIX.RecordType.SubscriberSemiBanked){
                            			subscriberForm.setAccountAndTemplateDisplay(true);
                            			subscriberForm.loadBankPocketTemplateCombo();
                                	} else {
                                		subscriberForm.setAccountAndTemplateDisplay(false);
                                	}
                            	} else { //while subscriber update
                            		if(kyc == CmFinoFIX.RecordType.SubscriberFullyBanked
                            				|| kyc == CmFinoFIX.RecordType.SubscriberSemiBanked){
                                		var actualKyc = subscriberForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name);
                                		if(actualKyc == CmFinoFIX.RecordType.SubscriberUnBanked || actualKyc === 0) {
                                			subscriberForm.setAccountAndTemplateDisplay(true);
                                			subscriberForm.loadBankPocketTemplateCombo();
                                		}
                                	}else{                            		
                                		subscriberForm.setAccountAndTemplateDisplay(false);
                                	}
                            	}                                
                            }
                        }
                       
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel:firstname,
                        itemId : 'sub.form.firstname',
                        maxLength     : 255,
                        allowBlank: false,
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel: lastname,
                        maxLength     : 255,
                        allowBlank: false,
                        itemId : 'sub.form.lastname',
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name
                    },
                    {
                        xtype : 'textfield',
                        //fieldLabel: nickname,
                        fieldLabel: _("Nickname"),
                        maxLength     : 255,
                        itemId : 'sub.form.nickname',
                        allowBlank: true,
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nickname._name
                    },
                   {
                   	 xtype : 'datefield',
                   	 allowBlank: true,
                   	 editable: false,
                	 fieldLabel: dateofbirth,
                	 itemId : 'sub.form.dateofbirth',
                	 anchor : '100%',
                	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.DateOfBirth._name,
                     maxValue:new Date().add('d',-1),
                     maxText:'Date of birth should not be future date',
                     listeners: {
                         change: function(field) {
                        	 this.findParentByType('subscriberform').onDOBSelect(field);
                        	                          }}
                	},
                    {
                        xtype : 'textfield',
                        fieldLabel: city,
                        maxLength : 255,
                        allowBlank: true,
                        itemId : 'sub.form.city',
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.City._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel: email,
                        maxLength     : 255,
                        itemId : 'sub.form.email',
                        vtype: 'email',
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name,
                        listeners: {
                        	blur: function(field) {
//                        		this.findParentByType('subscriberform').updateEmailCheck(field);
                        	}
                        }
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel: _("Reg Branch Code"),
                        maxLength     :255,
                        allowBlank: false,
                        itemId : 'sub.form.applicationid',
						vtype: 'name',
						anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApplicationID._name
                    },
                    {
                	    xtype : 'textfield',
                	    fieldLabel: subsrefaccount,                	    
                	    itemId : 'sub.form.subsrefaccount',
                	    //allowBlank: false,
                	    anchor : '100%',
                	    vtype:'number19',
                	    name: CmFinoFIX.message.JSSubscriberMDN.Entries.ReferenceAccount._name
                    },
                    {
                    	xtype : "combo",
                        itemId : 'sub.form.kycfield',
                        id : 'sub.form.kycfield',
                        hidden:true,
                        lastQuery: '',
                        store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSKYCCheckFields),
                        displayField: CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsName._name,
                        valueField : CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsLevelID._name,            
                        name: CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsLevelID._name,
                        listeners: {
                            reload: function(field) {
                            	var kf_combo = Ext.getCmp("sub.form.kycfield");
                            	alert("      "+kf_combo.store.getCount());
                            	                            }
                        		}
                     }]
                }]
            },
            {columnWidth: 0.5,
                items : [subsBasicDetail]}
            ]
        },
        {

            xtype:'tabpanel',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            itemId:'tabpanelmerchant',
            defaults:{
                bodyStyle:'padding:10px'
            },
            items:[
            {
                title: _('Additional Detail'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[subsotdetail]
            },{
                title: _('Security Question'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[
                {
                    columnWidth:0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:[subsMoreDetail]
                }]
            },
            {
                title : _('Authorization'),
                layout : 'column',
                frame:true,
                autoHeight: true,
                items :
                [subsAthorizingDetail]
            },
            {
                title: _('Notification Method'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[subsNotificationMethod]
            },
            {
                title: _('Restriction'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[subsRestrictions]
            }]
        }] ;
        this.subscribercombo.on("load", this.onLoad.createDelegate(this));
        mFino.widget.SubscriberForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    onLoad: function(){
        var record = this.subscribercombo.getAt(0);
        var srecord = this.record;
      	   if(record!=null&&record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
      		  record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.NotificationMethod._name, CmFinoFIX.NotificationMethod.SMS+CmFinoFIX.NotificationMethod.Email);
              record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name, CmFinoFIX.MDNStatus.Initialized);
      		 this.setRecord(record);  
//      		 this.removeRecord(record);
//      		 this.store.insert(0,this.record);
      		 this.unregisterRecord  = record;
      		 this.store.modified =[this.record];
      		 this.onStatusDropdown( CmFinoFIX.MDNStatus.Initialized);
           	 this.find("itemId", "sub.form.mobileno")[0].disable(); 
           	 this.find("itemId", "sub.form.status")[0].disable(); 
      		 this.subscribercombo.remove(record);
          }else if(record!=null){
       	   Ext.MessageBox.alert(_("Alert"), _("MDN already Registered")); 
       	   this.subscribercombo.remove(record);
           }else{           	
           	 this.onStatusDropdown( CmFinoFIX.MDNStatus.Initialized);
           	 this.find("itemId", "sub.form.mobileno")[0].disable(); 
           	 this.find("itemId", "sub.form.status")[0].disable(); 
           	 
//           	srecord.set(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,this.find("itemId", "sub.form.mobileno")[0].getValue());
//   		    srecord.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Language._name,CmFinoFIX.Language.English);
//   		    srecord.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Timezone._name,CmFinoFIX.Timezone.UTC);
//   		    srecord.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Currency._name,CmFinoFIX.Currency.NGN);
           	 }
//      	 srecord.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name, CmFinoFIX.MDNStatus.Initialized);
//    	 this.subscribercombo.remove(record);
//      	 srecord.data['ID'] = null;
//    	 this.setRecord(srecord);
       },
       addRecord: function(record){
    	   var i = 0;
    	   var id = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
    	   for(;i<15;i++){
    		   var storerecord = this.store.getAt(i);
    		   if(id === storerecord.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name)){
    			   i=15;
    			   this.store.remove(storerecord);
    		   }
    	   }
    	   this.store.insert(0,record);
       },
    onStatusDropdown : function(status){
    	 var items = ['sub.form.KYCLevel','sub.form.firstname','sub.form.lastname','sub.form.nickname','sub.form.dateofbirth','sub.form.city','sub.form.email','sub.form.applicationid','sub.form.plotno','sub.form.streetaddress','sub.form.regionname','sub.form.country','sub.form.idtype','sub.form.idnumber','sub.form.expirationtime','sub.form.proofofaddress','sub.form.typeofbankaccount','sub.form.bankaccid','SMS','Email1','Suspended','SecurityLocked','AbsoluteLocked','NoFundMovement','sub.form.securityquestion','sub.form.secretanswer','sub.form.birthplace','sub.form.nationality','sub.form.companyname','sub.form.subscribermobilecompany','sub.form.coi','sub.form.authofirstname','sub.form.authofirstname','sub.form.autholastname','sub.form.authorizingpersonid','sub.form.authodateofbirth','sub.form.authoiddescription','sub.form.status','sub.form.language','sub.form.currency','sub.form.timezone','sub.form.kinname','sub.form.kinmdn','sub.form.streetname','sub.form.group','sub.form.accountnumber','sub.form.bankPocketTemplate','sub.form.otherMDN'];

    	if(status == CmFinoFIX.MDNStatus.PendingRetirement || status==CmFinoFIX.MDNStatus.Retired){
            for(var i=0;i<items.length;i++){
            	this.find('itemId',items[i])[0].disable();
            }
            
            if (status==CmFinoFIX.MDNStatus.Retired && typeof(this.record) != "undefined") {
            	this.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.IsForceCloseRequested._name, true);
            }
        }else{
           // items = ['SMS','Email1','SelfSuspended','Suspended','SecurityLocked','AbsoluteLocked','sub.form.mobileno','sub.form.firstname','sub.form.lastname','form.email','sub.form.timezone','sub.form.language','sub.form.currency','sub.form.secretanswer','sub.form.timezone','sub.form.status'];
            for(i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].enable();
            }
            
			this.loadBankPocketTemplateCombo();
			if((SYSTEM_DEFAULT_ACCOUNT_NUMBER_MANDATORY) && (SYSTEM_DEFAULT_KYC == "3")){
				this.find("itemId", "sub.form.accountnumber")[0].allowBlank = false;
			}
			
			 if(SYSTEM_DEFAULT_KYC != "3"){
				 this.find('itemId','sub.form.accountnumber')[0].disable();
				 this.find("itemId", "sub.form.bankPocketTemplate")[0].disable();
           	 }
			 
            //this.find("itemId", "sub.form.bankPocketTemplate")[0].disable();
            
            if((status == CmFinoFIX.MDNStatus.Active) || (status == CmFinoFIX.MDNStatus.Initialized)) {
            	this.find('itemId','sub.form.status')[0].enable();
            } else {
            	this.find('itemId','sub.form.status')[0].disable();
            }
        }
    },
    
    loadBankPocketTemplateCombo : function() {
    	var kycLevel = this.find('itemId','sub.form.KYCLevel')[0].getValue();
    	var group = this.find('itemId','sub.form.group')[0].getValue();    	
    	var bankPocketField = this.find('itemId','sub.form.bankPocketTemplate')[0];
    	bankPocketField.store.reload({
    	      params: { 
    	    	  CommodityTypeSearch : CmFinoFIX.Commodity.Money,
	    	       SubscriberType : CmFinoFIX.SubscriberType.Subscriber,
	    	       PocketTypeSearch : CmFinoFIX.PocketType.BankAccount,
	    	       KYCLevel : kycLevel,
	    	       GroupID : group
    	      },
    	      callback : function( records, options, success){ 
	                if(success){ 
	                	if(records[0]) { //set the first value in the generated list as default if list is not empty
	                		this.setValue(records[0].data.ID); //set the ptc id but not pocket template id
	                		this.setRawValue(records[0].data.PocketTemplateDescription);	                		
	                	} else { // or else set it to null
	                		this.setValue(null);
	                	}	                	                 
	                }
    	      },
    	      scope : bankPocketField
    	});    	
    },

    setReadOnly : function(readOnly) {
    	this.getForm().items.each(function(item) {
				if (item.getXType() == 'textfield'
						|| item.getXType() == 'combo'
						|| item.getXType() == 'textarea'
						|| item.getXType() == 'numberfield'
						|| item.getXType() == 'datefield'
						|| item.getXType() == 'checkbox'
						|| item.getXType() == 'enumdropdown'
						|| item.getXType() == 'remotedropdown') {
					// Disable the item
					item.readOnly=true;
				}
				if (item.getXType() == 'checkbox'){
					item.disable();
				}
			});
	},
    onKYCDropdown : function(kyc){
    	if(kyc!="")
    	{

			var kf_combo = Ext.getCmp("sub.form.kycfield");
			var kf_combo_id='sub.form.kyc';
			
			/*````````````````````````````````````````````````````````````````*/
			kf_combo.store.clearFilter(); 
			kf_combo.store.reload({
				isAutoLoad: true,
				params: {KYCFieldsLevelID : kyc },
				callback:function(){}
			});
			
			var kf_combo = Ext.getCmp("sub.form.kycfield");
			var kycFieldSize = kf_combo.store.getCount();

			var fn;
			var items=new Array();
			for ( var i = 0; i < kycFieldSize; i++) {
				fn=kf_combo.store.getAt(i).data.KYCFieldsName;
				items[i]="sub.form."+fn;
			}

			for(var i=0;i<items.length;i++){
				this.find('itemId',items[i])[0].allowBlank = true;
			}
			
    	}
    },
    onDOBSelect : function(field)
    {
    	var age = 18;
   	 day = field.getValue().getDate();
   	 month = field.getValue().getMonth() + 1;
   	 year = field.getValue().getFullYear();
	  	 
   	 var mydate = new Date();
		 mydate.setFullYear(year, month-1, day);
   	 var currdate = new Date();
		 currdate.setFullYear(currdate.getFullYear() - age);
		 var aitems = ['sub.form.authofirstname','sub.form.autholastname','sub.form.authorizingpersonid','sub.form.authodateofbirth'];
		 if ((currdate - mydate) < 0){			
	            for(var i=0;i<aitems.length;i++){
	            	this.find('itemId',aitems[i])[0].allowBlank = false;
	            }
   		 }else{
   			 for(var i=0;i<aitems.length;i++){
	            	this.find('itemId',aitems[i])[0].allowBlank = true;
	            }
   		 }

    },
    
    updateEmailCheck : function(field) {
    	var email = field.getValue();
    	var emailCheckBox = this.form.items.get("Email1");
    	if(email == "") {
    		emailCheckBox.setValue(0);
    	} else {
    		emailCheckBox.setValue(1);
    	}    	
    },
    
    onAuthoDOBSelect : function(field)
    {
    	var age = 18;
   	 day = field.getValue().getDate();
   	 month = field.getValue().getMonth() + 1;
   	 year = field.getValue().getFullYear();
	  	 
   	 var mydate = new Date();
		 mydate.setFullYear(year, month-1, day);
   	 var currdate = new Date();
		 currdate.setFullYear(currdate.getFullYear() - age);
		 if ((currdate - mydate) < 0){
			alert("Date should be greater than 18 years");
			this.form.items.get("sub.form.authodateofbirth").setValue('');
			}

    },
    save : function(){
    	if(this.getForm().isValid()){
    		
            this.getForm().updateRecord(this.record);
           /* var isEmailChecked = this.form.items.get("Email1").getValue();
	        if (this.form.items.get("sub.form.email").getValue() === "" && isEmailChecked) {
	       		Ext.ux.Toast.msg(_("Error"), _("Please enter the Email Address."),	3);
				return;
			} */
            var notiValue = 0;
            if(this.form.items.get("SMS").checked){
                notiValue = notiValue + CmFinoFIX.NotificationMethod.SMS;
            }
            if(this.form.items.get("Email1").checked){
                notiValue = notiValue + CmFinoFIX.NotificationMethod.Email;
            }

            var resValue = 0;
//            if(this.form.items.get("SelfSuspended").checked){
//                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SelfSuspended;
//            }
            if(this.form.items.get("Suspended").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.Suspended;
            }
            if(this.form.items.get("SecurityLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SecurityLocked;
            }
            if(this.form.items.get("AbsoluteLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.AbsoluteLocked;
            }
            if(this.form.items.get("NoFundMovement").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.NoFundMovement;
            }

            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.NotificationMethod._name, notiValue);
            this.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.MDNRestrictions._name, resValue);
            this.record.endEdit();

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
        this.unregisterRecord = null;
        this.getForm().loadRecord(record);

        var resValue = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDNRestrictions._name);
//        this.form.items.get("SelfSuspended").setValue( (resValue & CmFinoFIX.SubscriberRestrictions.SelfSuspended) > 0);
        this.form.items.get("Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
        this.form.items.get("SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
        this.form.items.get("AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
        this.form.items.get("NoFundMovement").setValue((resValue & CmFinoFIX.SubscriberRestrictions.NoFundMovement) > 0);

        var notiValue = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.NotificationMethod._name);
        this.form.items.get("SMS").setValue( (notiValue & CmFinoFIX.NotificationMethod.SMS) > 0);
        this.form.items.get("Email1").setValue( ( notiValue & CmFinoFIX.NotificationMethod.Email) > 0);

        this.find('itemId','sub.form.subsrefaccount')[0].disable();
        this.find('itemId','sub.form.creditcheck')[0].disable();

        this.getForm().clearInvalid();
    },
    resetAll : function() {
		for ( var j = 0; j < this.form.items.length; j++) {
			this.form.items.get(j).setValue(null);
		}
		this.items.get("tabpanelmerchant").setActiveTab(0);
	},
    onMDN : function(field){
    	if(this.getForm().isValid()){
    	this.subscribercombo.baseParams[CmFinoFIX.message.JSSubscriberMDN.ExactMDNSearch._name] =field.getValue(); 
    	this.subscribercombo.load();
    	}
    },
    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
    disableNotPermittedItems: function(){
        var checkAbleItems = ['sub.form.mobileno'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
    },
    enablePermittedItems: function(){
        var checkAbleItems = ['sub.form.mobileno'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            checkItem.enable();
        }
    },
    onStoreUpdate: function(){
        this.setRecord(this.record);
    },
    onSuspendClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("Suspended").checked) {
            this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
        } else {
        	if (CmFinoFIX.SubscriberStatus.Suspend === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("sub.form.status").setValue(currentStatus);
        }
    },
    onSecurityLockClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("SecurityLocked").checked) {
            this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("sub.form.status").setValue(currentStatus);
        }
    },
    onAbsoluteLockClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("AbsoluteLocked").checked) {
            this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("sub.form.status").setValue(currentStatus);
        }
    },
     onNoFundMovementClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("NoFundMovement").checked) {
            this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("sub.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Active;
        		if(this.form.items.get("Suspended").checked) {
                	currentStatus = CmFinoFIX.SubscriberStatus.Suspend;
            	}
        	}
            this.form.items.get("sub.form.status").setValue(currentStatus);
        }
    },
	onCheckEmail: function(){
	    var isEmailChecked = this.form.items.get("Email1").getValue();
	       if (this.form.items.get("sub.form.email").getValue() === "" && isEmailChecked) {
	       	alert("Enter a valid email");
			this.form.items.get("Email1").setValue(false);
	    }     
	},
	setAccountAndTemplateDisplay: function(isVisible) {
		var accNo = this.find('itemId','sub.form.accountnumber')[0];
		var bankTemplate = this.find('itemId','sub.form.bankPocketTemplate')[0];
		if(isVisible) {
			accNo.show();
			accNo.enable();
			bankTemplate.show();
			//bankTemplate.enable();
			accNo.getEl().up('.x-form-item').setDisplayed(true);
			bankTemplate.getEl().up('.x-form-item').setDisplayed(true);
		} else {
			accNo.hide();
			accNo.disable();
			bankTemplate.hide();
			bankTemplate.disable();
			accNo.getEl().up('.x-form-item').setDisplayed(false);
			bankTemplate.getEl().up('.x-form-item').setDisplayed(false);
		}
	}    
});

/*
 * subsAdditional
 **/
var subsAdditional = {
    title: '',
    autoHeight: true,
    width: 280,
    layout: 'form',
    items: [{
		 xtype : 'textfield',
		 fieldLabel: plotno,
		 maxLength     :255,
		 itemId : 'sub.form.plotno',
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.PlotNo._name
	 },{
   	 xtype : 'textfield',
	 fieldLabel: streetaddress,
	 maxLength     :255,
	 itemId : 'sub.form.streetaddress',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.StreetAddress._name
 },{
	 xtype : 'textfield',
	 fieldLabel: regionname,
	 maxLength     :255,
	 itemId : 'sub.form.regionname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.RegionName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: country,
	 maxLength   :255,
	 itemId : 'sub.form.country',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Country._name
 },{
	 xtype : 'textfield',
	 fieldLabel: idtype,
	 maxLength     :255,
	 itemId : 'sub.form.idtype',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDType._name
 },{
	 xtype : 'textfield',
	 fieldLabel: idnumber,
	 maxLength     :255,
	 itemId : 'sub.form.idnumber',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDNumber._name
 },{
	 xtype : 'datefield',
	 fieldLabel: expirationtime,
	 editable: false,
	 itemId : 'sub.form.expirationtime',
	 anchor : '100%',
	 minValue:new Date().add('d',1),
     minText:'Must be future date',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.ExpirationTime._name
 },{
	 xtype : 'textfield',
	 fieldLabel: proofofaddress,
	 maxLength     :255,
	 itemId : 'sub.form.proofofaddress',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.ProofofAddress._name
 },{
     xtype : 'textfield',
     itemId : 'sub.form.typeofbankaccount',    
     hidden:true,
     anchor : '100%',
     name: CmFinoFIX.message.JSSubscriberMDN.Entries.TypeofBankAccount._name
 },{
	 xtype : 'textfield',
	 itemId : 'sub.form.bankaccid',	
	 hidden:true,
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.PANBankAccountID._name
 }]
};

/*
 * checkReference
 **/
var subsReference = {
    xtype: 'fieldset',
    title: 'Reference Details',
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: []
};

/*
 * Notification Method
 **/
var subsNotificationMethod = {
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
        items: [{
        columnWidth: 0.5,
        xtype : 'checkbox',
        itemId : 'SMS',
        boxLabel: sms
    },
    {
        columnWidth: 0.5,
        xtype : 'checkbox',
        itemId : 'Email1',
        boxLabel: email,
		listeners: {
            check: function() {
                //this.findParentByType("subscriberform").onCheckEmail();  //commented to fix #3381
            }
        }
    }]
    }]
};

/*
 * Subscriber Restrictions
 **/
var subsRestrictions = {
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
                    this.findParentByType("subscriberform").onSecurityLockClick();
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
                    this.findParentByType("subscriberform").onAbsoluteLockClick();
                }
            }
        },
//        {
//            columnWidth: 0.5,
//            xtype : 'checkbox',
//            itemId : 'SelfSuspended',
//            boxLabel: selfsuspended
//        },
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'Suspended',
            boxLabel: suspended,
            listeners: {
                check: function() {
                    this.findParentByType("subscriberform").onSuspendClick();
                }
            }
        },
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'NoFundMovement',
            boxLabel: 'NoFundMovement',
            listeners: {
                check: function() {
                    this.findParentByType("subscriberform").onNoFundMovementClick();
                }
            }
        }]
    }]
};


/*
 * Subscriber more Details
 **/
var subsMoreDetail = {
    title: _(''),
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [{
        xtype : 'textfield',
        anchor : '100%',
        allowBlank: true,
        maxLength   :255,
        itemId : 'sub.form.securityquestion',
        fieldLabel : secretquestion,
        name : CmFinoFIX.message.JSSubscriberMDN.Entries.SecurityQuestion._name
    },
    {
        xtype : 'textfield',
        fieldLabel : secretanswer,
        maxLength   :255,
        anchor : '100%',
        allowBlank: true,
        itemId : 'sub.form.secretanswer',
        blankText : _('Answer is required'),
        vtype: 'numberchar',
        name : CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhrase._name
    },
    {
        xtype: "displayfield",
        fieldLabel: subscriberid,
        allowBlank: true,
        anchor : '100%',
        name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name
    },
    {
        xtype : 'displayfield',
         anchor : '100%',
        allowBlank: true,
        fieldLabel : subscribertype,
        name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberTypeText._name
    }]
};

/*
 * subsOtherDetail
 **/
var subsOtherDetail = {
		xtype: 'fieldset',
	    title: 'Other Details',
	    autoHeight: true,
	    width: 280,
	    layout: 'form',
    items: [{
	 xtype : 'textfield',
	 fieldLabel: birthplace,
	 maxLength   :255,
	 itemId : 'sub.form.birthplace',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
 },{
	 xtype : 'textfield',
	 fieldLabel: nationality,
	 maxLength   :255,
	 itemId : 'sub.form.nationality',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nationality._name
 },{
	 xtype : 'textfield',
	 fieldLabel: companyname,
	 maxLength   :255,
	 itemId : 'sub.form.companyname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CompanyName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: subscribermobilecompany,
	 maxLength   :255,
	 itemId : 'sub.form.subscribermobilecompany',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberMobileCompany._name
 },{
	 xtype : 'textfield',
	 fieldLabel: coi,
	 maxLength   :255,
	 itemId : 'sub.form.coi',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CertofIncorporation._name
 }]
};
/*
 * subsAthorizingDetail
 **/
var subsAthorizingDetail = {
    title: '',
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [{
	 xtype : 'textfield',
	 fieldLabel: firstname,
	 maxLength   :255,
	 itemId : 'sub.form.authofirstname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoFirstName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: lastname,
	 maxLength   :255,
	 itemId : 'sub.form.autholastname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoLastName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: idnumber,
	 maxLength   :255,
	 itemId : 'sub.form.authorizingpersonid',
//	 vtype:'number19',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthorizingPersonIDNumber._name
 },{
	 xtype : 'datefield',
	 fieldLabel: dateofbirth,
	 editable: false,
	 itemId : 'sub.form.authodateofbirth',
	 anchor : '100%',
	 maxValue:new Date().add('d',-1),
     maxText:'Date of birth should not be future date',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoDateofBirth._name,
	 listeners: {
         change: function(field) {
        	 this.findParentByType('subscriberform').onAuthoDOBSelect(field);
        	                          }}
 },{
	 xtype : 'textfield',
	 fieldLabel: description,
	 maxLength   :255,
	 itemId : 'sub.form.authoiddescription',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoIDDescription._name
 }]
};




/*
 * Basic Detail
 **/
var subsBasicDetail = {
    title: '',
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [{
                        xtype : "enumdropdown",
                         anchor : '100%',
                        allowBlank: false,
                        blankText : _('Status is required'),
                        itemId : 'sub.form.status',
                        //emptyText:'Initialized',
                        emptyText : _('<select one..>'),
                        fieldLabel :status,
                        addEmpty: false,
                        enumId : CmFinoFIX.TagID.SubscriberStatus,
                        name : CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name,
                        value : CmFinoFIX.MDNStatus.Initialized,
                        listeners : {
                            select :  function(field){
                                var status= field.getValue();
                                this.findParentByType('subscriberform').onStatusDropdown(status);
                            }
                        }
                    },{
    xtype : "enumdropdown",
     anchor : '100%',
    fieldLabel :language,
    itemId : 'sub.form.language',
    emptyText : _('<select one..>'),
    allowBlank: false,
    blankText : _('Language is required'),
    enumId : CmFinoFIX.TagID.Language,
    name : CmFinoFIX.message.JSSubscriberMDN.Entries.Language._name

},
{
    xtype : "enumdropdown",
     anchor : '100%',
    fieldLabel :currency,
    itemId : 'sub.form.currency',
    emptyText : _('<select one..>'),
    allowBlank: false,
    blankText : _('Currency is required'),
    enumId : CmFinoFIX.TagID.Currency,
    name : CmFinoFIX.message.JSSubscriberMDN.Entries.Currency._name

},
{
    xtype : "enumdropdown",
     anchor : '100%',
    fieldLabel :timezone,
    emptyText : _('<select one..>'),
    allowBlank: false,
    itemId : 'sub.form.timezone',
    enumId: CmFinoFIX.TagID.Timezone,
    name : CmFinoFIX.message.JSSubscriberMDN.Entries.Timezone._name
},
{
    xtype : 'textfield',
    fieldLabel: _("Next Of Kin"),
    maxLength   :255,
    itemId : 'sub.form.kinname',
    allowBlank: true,
    anchor : '100%',
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.KinName._name
},
{
    xtype : 'textfield',
    fieldLabel: 'Next Of Kin No',
    maxLength   :255,
    itemId : 'sub.form.kinmdn',
    allowBlank: true,
    anchor : '100%',
    vtype: 'smarttelcophoneAdd',
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.KinMDN._name,
    listeners: {
        change: function(field) {
        						}
    			}
},
{
	xtype : "remotedropdown",
	anchor : '100%',
	allowBlank: false,
	addEmpty: false,
	itemId : 'sub.form.group',
	id : 'sub.form.item.group',
	fieldLabel :"Group",
	pageSize : 5,
	emptyText : _('<select one..>'),
	RPCObject : CmFinoFIX.message.JSGroup,
	displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
	valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
	name: CmFinoFIX.message.JSSubscriberMDN.Entries.GroupID._name,
	params: {SystemGroupSearch : "true"},
	listeners: {
		select : function() {
			var bankPocketField = this.findParentByType('subscriberform').find('itemId','sub.form.bankPocketTemplate')[0];
			if(!bankPocketField.disabled) {
				this.findParentByType('subscriberform').loadBankPocketTemplateCombo();
			}			
		}
	}
},
{
    xtype : 'textfield',
    fieldLabel: _("Account No:"),
    itemId : 'sub.form.accountnumber',
    vtype:'tendigitnumber',
    labelSeparator : '',
    anchor : '100%',
    disabled: false,
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.AccountNumber._name            
},
{
	xtype : "remotedropdown",
	anchor : '100%',	
	addEmpty: false,
	itemId : 'sub.form.bankPocketTemplate',
	id : 'sub.form.item.bankPocketTemplate',
	fieldLabel :"Bank Pocket Template",
	emptyText : _('<select one..>'),
	RPCObject : CmFinoFIX.message.JSPocketTemplateConfig,
	displayField: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateDescription._name,
	valueField : CmFinoFIX.message.JSPocketTemplateConfig.Entries.ID._name,
	name: CmFinoFIX.message.JSSubscriberMDN.Entries.PocketTemplateConfigID._name
},
{
    xtype : 'textfield',
    fieldLabel: _("Other MDN:"),    
    itemId : 'sub.form.otherMDN',
    vtype:'smarttelcophoneAdd',
    labelSeparator : '',
    anchor : '100%',
    disabled: false,
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.OtherMDN._name            
}
]
};
var subsotdetail = {
	    title: _(''),
	    autoHeight: true,
	    width: 600,
	    layout: 'form',
	    items : [{
	        layout : 'column',
	        autoHeight: true,
	        columns: 2,
	        style : {
	            margin: '5px'
	        },
	        items: [{
	        columnWidth: 0.5,
	        layout: 'form',
	        items: [ subsAdditional]
	    },
	    {
	        columnWidth: 0.5,
	        autoHeight: true,
		    layout: 'form',
		    bodyStyle: 'padding:0 10px 0;',
	    items: [{
	   	 xtype : 'textfield',
		 fieldLabel: creditcheck,
		 maxLength   :255,
		 itemId : 'sub.form.creditcheck',
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CreditCheck._name
	 },{
	  	 xtype : 'textfield',
		 itemId : 'sub.form.streetname',
		 maxLength :255,
		 hidden:true,
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.StreetName._name
	},subsOtherDetail]
	    }]
	    }]
	};
Ext.reg("subscriberform", mFino.widget.SubscriberForm);
