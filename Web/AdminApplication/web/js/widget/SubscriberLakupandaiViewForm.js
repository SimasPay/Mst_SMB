/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberLakupandaiViewForm = function (config) {
	var isLoadRecord=false;
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.SubscriberLakupandaiViewForm.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.SubscriberLakupandaiViewForm, Ext.form.FormPanel, {
	
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
                columnWidth: 0.45,
                items : [
                {
                    //width: 300,
                    autoHeight: true,
                    allowBlank: false,
                    layout: 'form',
                    items: [
							{
							    xtype : 'displayfield',
							    fieldLabel: 'Nama (Sesuai KTP)',
							    itemId : 'sub.form.authofirstname',
							    name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name,
							    allowBlank: false,
							    listeners: {
							        change: function(field) {
							//        	this.findParentByType('SubscriberLakupandaiViewForm').onMDN(field);
							        }
							    },
							    emptyText: _('Subscriber\'s FirstName'),
							    blankText : _('FirstName is required'),
							    anchor : '100%'
							},
							
							{
		                      	 xtype : 'displayfield',
		                      	 allowBlank: false,
		                      	 editable: false,
		                      	 fieldLabel: 'Tanggal Lahir',
		                   	 	 itemId : 'sub.form.authodateofbirth',
		                      	 anchor : '100%',
		                      	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDateOfBirth._name,
		                      	 maxValue:new Date().add('d',-1),
		                      	 maxText:'Date of birth should not be future date',
		                      	 listeners: {
		                            change: function(field) {
//		                           	 this.findParentByType('SubscriberLakupandaiViewForm').onDOBSelect(field);
		                           	                          }}
		                   	},
		                   	{
                               xtype : 'displayfield',
                               fieldLabel: 'No. HP',
                               itemId : 'sub.form.mobileno',
                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
                               allowBlank: false,
                               vtype: 'smarttelcophoneAddMore',
                               listeners: {
                                   change: function(field) {
                                   	this.findParentByType('SubscriberLakupandaiViewForm').onMDN(field);
                                   }
                               },
                               emptyText: _('Unique mobile phone number'),
                               blankText : _('Mobile Number is required'),
                               anchor : '100%'
                           },
                           {
                               xtype : 'displayfield',
                               fieldLabel: 'Email',
                               itemId : 'sub.form.email',
                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name,
                               allowBlank: true,
//                               vtype: 'smarttelcophoneAddMore',
                               listeners: {
                                   change: function(field) {
//                                   	this.findParentByType('SubscriberLakupandaiViewForm').onMDN(field);
                                   }
                               },
                               emptyText: _(''),
                               blankText : _('Email is required'),
                               anchor : '100%'
                           },
                           {
                           	xtype : "displayfield",
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
                            }
		                   	,{
                               xtype : "displayfield",
                               anchor : '100%',
                               allowBlank: false, 
                               blankText : _('KYC is required'),
                               itemId : 'sub.form.KYCLevel',
//                               readOnly:true,
//                               id : 'sub.form.KYCLevel',
                               fieldLabel :'KYC',
                               emptyText : _('<select one..>'),
                               RPCObject : CmFinoFIX.message.JSKYCCheck,
                               displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
                               valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevelText._name,
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
                                   	var subscriberForm = this.findParentByType('SubscriberLakupandaiViewForm');
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
                              
                           }
                            ]
                }]
            },
            {
            	columnWidth: 0.1,
                items : [{
                	xtype:'displayfield'
                }
                ]
            },
            {columnWidth: 0.45,
                items : [subsBasicDetailLakuView]}
            ]
        },
        {
            layout:'form',
            
            items : [
            
            {
                items : [
                         {
                        	 html: '<hr style="color:silver;"/>'
                         }
                ]
            }
            ]
        },
        {
            layout:'column',
            
            items : [
            {
                columnWidth: 0.45,
                items : [
                {
                    //width: 300,
                    autoHeight: true,
                    allowBlank: false,
                    layout: 'form',
                    items: [
							/*{
							    xtype : 'displayfield',
							    fieldLabel: 'No CIF',
							//    itemId : 'sub.form.mobileno',
							    name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
							    allowBlank: false,
							//    vtype: 'smarttelcophoneAddMore',
							    listeners: {
							        change: function(field) {
							//        	this.findParentByType('SubscriberLakupandaiViewForm').onMDN(field);
							        }
							    },
							    emptyText: _(''),
							    blankText : _('Mobile Number is required'),
							    anchor : '100%'
							},
							{
							    xtype : 'displayfield',
							    fieldLabel: 'No Rekening',
							//    itemId : 'sub.form.mobileno',
							    name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
							    allowBlank: false,
							//    vtype: 'smarttelcophoneAddMore',
							    listeners: {
							        change: function(field) {
							//        	this.findParentByType('SubscriberLakupandaiViewForm').onMDN(field);
							        }
							    },
							    emptyText: _(''),
							    blankText : _('Mobile Number is required'),
							    anchor : '100%'
							}*/
							{
							    xtype : "displayfield",
							    anchor : '100%',
							    itemId:'sub.form.subscribertype',
							    fieldLabel :'Tipe Saku',
							    value:'Laku Pandai'
							}
                            ]
                }]
            },
            {
            	columnWidth: 0.1,
                items : [{
                	xtype:'displayfield'
                }
                ]
            },
            {columnWidth: 0.45,
                items : [
					{title: '',
					autoHeight: true,
					width: 300,
					layout: 'form',
					items: [
							
						]
					}
                         ]}
            ]
        },
        {
            layout:'form',
            
            items : [
            
            {
                items : [
                         {
                        	 html: '<hr style="color:silver;"/>'
                         }
                ]
            }
            ]
        },
        {

            layout:'column',
            
            items : [
            {
                columnWidth: 0.45,
                items : [
                {
                    //width: 300,
                    autoHeight: true,
                    allowBlank: false,
                    layout: 'form',
                    items: [
							/*{
							    xtype : "remotedropdown",
							    anchor : '100%',
							    allowBlank: false, 
							    blankText : _('KYC is required'),
							//    itemId : 'sub.form.KYCLevel',
							//    id : 'sub.form.KYCLevel',
							    fieldLabel :'Grup',
							    emptyText : _('<select one..>'),
							    RPCObject : CmFinoFIX.message.JSKYCCheck,
							    displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
							    valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
							    name: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name
							     
							},*/
							{
		                        xtype : "displayfield",
		                         anchor : '100%',
		                        allowBlank: false,
		                        blankText : _('Status is required'),
		                        itemId : 'sub.form.status',
		                        //emptyText:'Initialized',
		                        emptyText : _('<select one..>'),
		                        fieldLabel :status,
		                        addEmpty: false,
		                        enumId : CmFinoFIX.TagID.SubscriberStatus,
		                        name : CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberStatusText._name,
//		                        value : CmFinoFIX.MDNStatus.Initialized,
		                        listeners : {
		                            select :  function(field){
		                                var status= field.getValue();
		                                this.findParentByType('SubscriberLakupandaiViewForm').onStatusDropdown(status);
		                            }
		                        }
                    }
							
                            ]
                }]
            },
            {
            	columnWidth: 0.1,
                items : [{
                	xtype:'displayfield'
                }
                ]
            },
            {columnWidth: 0.45,
                items : [
					{title: '',
					autoHeight: true,
					width: 300,
					layout: 'form',
					items: [
							{
							    xtype : 'displayfield',
							    fieldLabel: 'Bahasa',
							    itemId:'sub.form.language',
							    value:'Indonesia',
							    anchor : '100%'
							},
							{
							    xtype : "displayfield",
							    anchor : '100%',
							    fieldLabel :'Mata Uang',
							    value:'IDR',
							    itemId:'sub.form.currency',
							    readOnly:true
							     
							},
							{
							    xtype : "displayfield",
							    anchor : '100%',
							    allowBlank: false, 
							    fieldLabel :'Zana Waktu',
							    emptyText : _('<select one..>'),
							    itemId : 'sub.form.timezone',
		                        enumId: CmFinoFIX.TagID.Timezone,
		                        name : CmFinoFIX.message.JSSubscriberMDN.Entries.Timezone._name,
		                        displayField:  CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name,
		                        valueField: CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name,
							}
						]
					}
                         ]}
            ]
        
        }
        ,
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
                title: _('Alamat'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[subsotdetailLakuView]
            },
            {
                title : _('Lain-Lain'),
                layout : 'column',
                frame:true,
                autoHeight: true,
                items :
                [subsAthorizingDetailLakuView]
            },{
                title: _('Keamanan'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[
                {
                    columnWidth:1,
                    xtype: 'panel',
                    layout: 'form',
                    items:[subsMoreDetailLakuView]
                }
                ]
            },
//            {
//                title: _('Notification Method'),
//                layout:'column',
//                frame:true,
//                autoHeight: true,
//                items:[subsNotificationMethod]
//            },
//            {
//                title: _('Restriction'),
//                layout:'column',
//                frame:true,
//                autoHeight: true,
//                items:[subsRestrictions]
//            }
            ]
        }] ;
        this.subscribercombo.on("load", this.onLoad.createDelegate(this));
        mFino.widget.SubscriberLakupandaiViewForm.superclass.initComponent.call(this);
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
//            	alert(this.form.items.get("sub.form.issameaddress").items.items[0].getValue()+"--"+this.form.items.get("sub.form.issameaddress").items.items[1].getValue())
            	if(this.form.items.get("sub.form.issameaddress").items.items[0].getValue()){
            		this.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.IsDomesticAddrIdentity._name,true );
            	}else{
            		this.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.IsDomesticAddrIdentity._name,false );
            	}
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
    showImage:function(imageName){
    	var imagePath=mFino.widget.SubscriberDetails.path+imageName
		if(imagePath.indexOf('.')!=-1){
			
		
			var window=new Ext.Window({
				layout:'anchor',
				width:500,
				height:500,
				autoScroll:true,
				bodyStyle:'backgroundColor:white',
				title:imageName,
				items:[{
					anchor : '100%',
					html: "<div display=\"block\" style=''>" + 
						
					"<div style=\"text-align:left;line-height:3px;padding:5px 3px 4px;\">" +
						"<span>" + 
								"<img height=200 width=200 alt=\"image\" src=\""+imagePath+"\" />" +
						"</span>" + 
					"</div>" +
				   "</div>"
				}]
			});
			window.show();
		}else{
			
			Ext.Msg.alert('Info', 'Document Not Available!');
		}
    },
    onIDLifeTime:function(value){
    	 if(value){
    		 this.find('itemId','sub.form.IdValidUntil')[0].setValue('');
//    		 this.find('itemId','sub.form.IdValidUntil')[0].setDisabled(true)
    	 }else{
    		 
    		 this.find('itemId','sub.form.IdValidUntil')[0].setDisabled(false)
    	 }
    },
    onWork:function(field,record){
//    	alert(record.get(CmFinoFIX.message.JSEnumTextSimple.Entries.EnumCode._name))
    	if(record.get(CmFinoFIX.message.JSEnumTextSimple.Entries.EnumCode._name)==CmFinoFIX.WorkList.Lainnya){
//    		this.form.items.get("sub.form.otherwork").setDisabled(false);
    	}else{
//    		this.form.items.get("sub.form.otherwork").setDisabled(true);
    		this.form.items.get("sub.form.otherwork").setValue('');
    	}
    },
    onProvince2 : function(field,record){
//        var value=field.getValue();
    	var value=record.get(CmFinoFIX.message.JSProvince.Entries.ID._name)
//        alert(value);
    	var region_combo = this.find('itemId','sub.form.CityCom')[0];
    	region_combo.clearValue();
    	region_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdProvince : value
    		}
    	});
    },
    onProvinceRegion2 : function(field,record){
//        var value=field.getValue();
    	var value=record.get(CmFinoFIX.message.JSProvinceRegion.Entries.ID._name)
        //alert(value);
    	var district_combo = this.find('itemId','sub.form.DistrictCom')[0];
    	district_combo.clearValue();
    	district_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdRegion : value
    		}
    	});
    },
    onDistrict2 : function(field,record){
//        var value=field.getValue();
        var value=record.get(CmFinoFIX.message.JSDistrict.Entries.ID._name)
        //alert(value);
    	var village_combo = this.find('itemId','sub.form.VillageCom')[0];
    	village_combo.clearValue();
    	village_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdDistrict : value
    		}
    	});
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.unregisterRecord = null;

		if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name)!=null){

			var docFullPath=record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name)
			docFullPath=docFullPath.replace("\\","/");
			docFullPath=docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberLakupandaiViewForm.path == null || mFino.widget.SubscriberLakupandaiViewForm.path == '' || mFino.widget.SubscriberLakupandaiViewForm.path == undefined){
				mFino.widget.SubscriberLakupandaiViewForm.path=docFullPath.substring(0,docFullPath.lastIndexOf('/')+1);
			}
			
			var docName=docFullPath.substring(docFullPath.lastIndexOf('/')+1,docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,docName);
		}
		
		
			if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name)!=null){

			var docFullPath=record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name)
			docFullPath=docFullPath.replace("\\","/");
			docFullPath=docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberLakupandaiViewForm.path == null || mFino.widget.SubscriberLakupandaiViewForm.path == '' || mFino.widget.SubscriberLakupandaiViewForm.path == undefined){
					mFino.widget.SubscriberLakupandaiViewForm.path=docFullPath.substring(0,docFullPath.lastIndexOf('/')+1);
			}
			
			var docName=docFullPath.substring(docFullPath.lastIndexOf('/')+1,docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name,docName);
		}
		

		
			if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name)!=null){
				
		
			var docFullPath=record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name)
			
					docFullPath=docFullPath.replace("\\","/");
			docFullPath=docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberLakupandaiViewForm.path == null || mFino.widget.SubscriberLakupandaiViewForm.path == '' || mFino.widget.SubscriberLakupandaiViewForm.path == undefined){
					mFino.widget.SubscriberLakupandaiViewForm.path=docFullPath.substring(0,docFullPath.lastIndexOf('/')+1);
			}
			
			var docName=docFullPath.substring(docFullPath.lastIndexOf('/')+1,docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name,docName);
		}
	
			if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.IsDomesticAddrIdentity._name)){
	        	this.form.items.get("sub.form.issameaddress").items.items[0].setValue(true)
	        }else{
	        	this.form.items.get("sub.form.issameaddress").items.items[1].setValue(true)
	        }
			mFino.widget.SubscriberLakupandaiViewForm.isLoadRecord=true;
        this.getForm().loadRecord(record);
//        this.form.items.get("sub.form.mobileno").setDisabled(true);
//        this.form.items.get("sub.form.ktpid").setDisabled(true);
//        this.form.items.get("sub.form.KYCLevel").setDisabled(true);
//        this.form.items.get("sub.form.authodateofbirth").setDisabled(true);
//        this.form.items.get("sub.form.subscribertype").setDisabled(true);
        this.form.items.get("sub.form.subscribertype").setValue('Laku Pandai');
//        this.form.items.get("sub.form.language").setDisabled(true);
        this.form.items.get("sub.form.language").setValue('Indonesia');
//        this.form.items.get("sub.form.currency").setDisabled(true);
        this.form.items.get("sub.form.currency").setValue('IDR');
        
//        this.form.items.get("sub.form.ktpplotno").setDisabled(true);
//        this.form.items.get("sub.form.KTPRT").setDisabled(true);
//        this.form.items.get("sub.form.KTPRW").setDisabled(true);
//        this.form.items.get("sub.form.KTPzipcode").setDisabled(true);
//        this.form.items.get("sub.form.regiionname").setDisabled(true);
//        this.form.items.get("sub.form.city").setDisabled(true);
//        this.form.items.get("sub.form.ktpstate").setDisabled(true);
//        this.form.items.get("sub.form.ktpsubstate").setDisabled(true);
//        this.form.items.get("sub.form.MothersMaidenName").setDisabled(true);

//        this.form.items.get("sub.form.authoiddescription").setDisabled(true);
//        this.form.items.get("sub.form.agentbranch").setDisabled(true);
//        this.form.items.get("sub.form.agentname").setDisabled(true);
//        this.form.items.get("sub.form.agentcode").setDisabled(true);
        
        

        if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.IsIdLifetimeText._name)=='true'){
        	this.form.items.get("sub.form.ISIDLifetime").setValue(true)
        	this.form.items.get("sub.form.IdValidUntil").setValue('')
        }else{
        	this.form.items.get("sub.form.ISIDLifetime").setValue(false)
        	this.form.items.get("sub.form.IdValidUntil").setValue( Ext.util.Format.date(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.IDValidUntil._name),'m/d/y'))
        }
        this.form.items.get("sub.form.authodateofbirth").setValue( Ext.util.Format.date(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDateOfBirth._name),'m/d/y'))
        
        
        if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Work._name)=='Lainnya'){
//        	this.form.items.get("sub.form.otherwork").setDisabled(false);
//        	this.form.items.get("sub.form.otherwork").setValue(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.OtherWork._name))
        }else{
//        	this.form.items.get("sub.form.otherwork").setDisabled(true);
        	this.form.items.get("sub.form.otherwork").setValue('');
        }
        
        var resValue = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDNRestrictions._name);
//        this.form.items.get("SelfSuspended").setValue( (resValue & CmFinoFIX.SubscriberRestrictions.SelfSuspended) > 0);
        this.form.items.get("Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
        this.form.items.get("SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
        this.form.items.get("AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
        this.form.items.get("NoFundMovement").setValue((resValue & CmFinoFIX.SubscriberRestrictions.NoFundMovement) > 0);

        var notiValue = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.NotificationMethod._name);
        this.form.items.get("SMS").setValue( (notiValue & CmFinoFIX.NotificationMethod.SMS) > 0);
        this.form.items.get("Email1").setValue( ( notiValue & CmFinoFIX.NotificationMethod.Email) > 0);

//        this.find('itemId','sub.form.subsrefaccount')[0].disable();
//        this.find('itemId','sub.form.creditcheck')[0].disable();
        
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
			//accNo.enable();
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
	},
	onDomisiliChange: function(isSameAddress){
		
			if(!mFino.widget.SubscriberLakupandaiViewForm.isLoadRecord){
				
				if(!isSameAddress){
			}
			
		}else{
			mFino.widget.SubscriberLakupandaiViewForm.isLoadRecord=false;
		}
		
	}    
});

/*
 * subsAdditional
 **/
var subsAdditionalLakuView = {
    title: '',
    autoHeight: true,
    width: 280,
    layout: 'form',
    items: [{
		 xtype : 'displayfield',
		 fieldLabel: 'Nama Gadis Ibu Kandung',
//		 disabled: true,
		 anchor : '100%',
		 itemId  : 'sub.form.MothersMaidenName',
         name: CmFinoFIX.message.JSSubscriberMDN.Entries.MothersMaidenName._name
	 },{
   	 xtype : 'displayfield',
	 fieldLabel: 'Alamat (Sesuai KTP)',
//	 disabled: true,
	 itemId : 'sub.form.ktpplotno',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPPlotNo._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: 'RT',
//	 disabled: true,
	 anchor : '100%',
	 maxLength : 255,
     itemId  : 'sub.form.KTPRT',
     name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPRT._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: 'RW',
//	 disabled: true,
	 anchor : '100%',
	 maxLength : 255,
     itemId  : 'sub.form.KTPRW',
     name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPRW._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: 'Kode Pos',
//	 disabled: true,
	 itemId : 'sub.form.KTPzipcode',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPZipCode._name
 },
 {
     fieldLabel: 'Domisili',
     xtype: 'radiogroup',
     itemId:'sub.form.issameaddress',
     columns:1,   
     items: [{
         name: CmFinoFIX.message.JSSubscriberMDN.Entries.IsDomesticAddrIdentity._name,
         itemId:'sub.form.acctoktp',
         inputValue: 'true',
         boxLabel: 'Sesual Dengan KTP',
//         checked: true
     },{
         name: CmFinoFIX.message.JSSubscriberMDN.Entries.IsDomesticAddrIdentity._name,
         itemId:'sub.form.contrtoktp',
         inputValue: 'false',
         boxLabel: 'Berbeda dengan KTP'
     }
     ],
     listeners: {
         change: function(radiogroup, radio){
             var isSameAddress = radio.inputValue == 'true';
             this.findParentByType("SubscriberLakupandaiViewForm").onDomisiliChange(isSameAddress);
//             alert(visible)
                         
         }
     }
 }
 ,{
	 xtype : 'displayfield',
	 fieldLabel: 'Alamat (Domisili)',
	 itemId : 'sub.form.plotno',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.PlotNo._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: 'RT',
	 itemId : 'sub.form.RT',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.RT._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: 'RW',
	 itemId : 'sub.form.RW',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.RW._name
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
var subsNotificationMethodLaku = {
    title: _(''),
    autoHeight: true,
//    width: 600,
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
                //this.findParentByType("SubscriberLakupandaiViewForm").onCheckEmail();  //commented to fix #3381
            }
        }
    }]
    }]
};

/*
 * Subscriber Restrictions
 **/
var subsRestrictionsLaku = {
    title: _(''),
    autoHeight: true,
//    width: 600,
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
            boxLabel: 'Keamanan Terkunci',
            listeners: {
                check: function() {
                    this.findParentByType("SubscriberLakupandaiViewForm").onSecurityLockClick();
                }
            }
        },
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'AbsoluteLocked',
            boxLabel: 'Terkunci Mutlak(Sepenuhnya)',
            listeners: {
                check: function() {
                    this.findParentByType("SubscriberLakupandaiViewForm").onAbsoluteLockClick();
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
            boxLabel: 'Ditangguhkan',
            listeners: {
                check: function() {
                    this.findParentByType("SubscriberLakupandaiViewForm").onSuspendClick();
                }
            }
        },
        {
            columnWidth: 0.5,
            xtype : 'checkbox',
            itemId : 'NoFundMovement',
            boxLabel: 'Tidak Ada Pergerakan Dana',
            listeners: {
                check: function() {
                    this.findParentByType("SubscriberLakupandaiViewForm").onNoFundMovementClick();
                }
            }
        }]
    }]
};


/*
 * Subscriber more Details
 **/
var subsMoreDetailLakuView = {
		
    title: _(''),
    autoHeight: true,
    width: 610,
    layout: 'form',
    items: [
            {
			xtype: 'fieldset',
			title: 'Pertanyaan Keamanan',
			autoHeight: true,
//			width: 280,
			layout: 'form',
			items: [
            {
        xtype : 'displayfield',
        anchor : '100%',
        allowBlank: true,
        itemId : 'sub.form.securityquestion',
        fieldLabel : 'Pertayaan Rohasia',
        name : CmFinoFIX.message.JSSubscriberMDN.Entries.SecurityQuestion._name
    },
    {
        xtype : 'displayfield',
        fieldLabel : 'Jawaban Rahasia',
        anchor : '100%',
        allowBlank: true,
        itemId : 'sub.form.secretanswer',
        blankText : _('Answer is required'),
        vtype: 'numberchar',
        name : CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhrase._name
    },
    {
        xtype: "hidden",
        fieldLabel: subscriberid,
        allowBlank: true,
        hidden:true,
        anchor : '100%',
        name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name
    },
    {
        xtype : 'hidden',
        hidden:true,
         anchor : '100%',
        allowBlank: true,
        fieldLabel : subscribertype,
        name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberTypeText._name
    }
    ]
},
{
	xtype: 'fieldset',
	title: 'Informasi',
	autoHeight: true,
//	width: 280,
	layout: 'form',
	items: [
	        subsNotificationMethodLaku
	        ]
},
{
	xtype: 'fieldset',
	title: 'Keamanan',
	autoHeight: true,
//	width: 280,
	layout: 'form',
	items: [
	        subsRestrictionsLaku
	        ]
}
    ]
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
	 xtype : 'displayfield',
	 fieldLabel: birthplace,
	 itemId : 'sub.form.birthplace',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: nationality,
	 itemId : 'sub.form.nationality',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nationality._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: companyname,
	 itemId : 'sub.form.companyname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CompanyName._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: subscribermobilecompany,
	 itemId : 'sub.form.subscribermobilecompany',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberMobileCompany._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: coi,
	 itemId : 'sub.form.coi',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CertofIncorporation._name
 }]
};
/*
 * subsAthorizingDetail
 **/
var subsAthorizingDetailLakuView = {
    title: '',
    autoHeight: true,
    width: 680,
    layout: 'form',
    items : [{
        layout : 'column',
        autoHeight: true,
        columns: 2,
        style : {
            margin: '5px'
        },
        items: [{
        columnWidth: 0.47,
    layout: 'form',
    items: [{
	 xtype : 'displayfield',
	 fieldLabel: 'Tempat Lahir',
	 anchor : '100%',
	 itemId : 'sub.form.birthplace',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
 },{
	 xtype : 'displayfield',
	 fieldLabel: 'Kewarganegaraan',
	 itemId : 'sub.form.nationality',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nationality._name
 },
 {
	    xtype : "displayfield",
	    anchor : '100%',
	    allowBlank: true, 
	    blankText : _('Work is required'),
	    itemId : 'sub.form.work',
	//    id : 'sub.form.KYCLevel',
	    fieldLabel :'Pekerjaan',
	    emptyText : _('<select one..>'),
	    enumId : CmFinoFIX.TagID.WorkList,
        name : CmFinoFIX.message.JSSubscriberMDN.Entries.Work._name,
        displayField:  CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name,
        valueField: CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name,
        listeners: {
            select: function(field,record) {
            	this.findParentByType('SubscriberLakupandaiViewForm').onWork(field,record);
//            	
            }
        }
        
	     
	},
 {
	 xtype : 'displayfield',
	 fieldLabel: 'Sebutkan',
	 itemId : 'sub.form.otherwork',
//	 vtype:'number19',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.OtherWork._name
 },
 {
     xtype: "displayfield",
     fieldLabel: _('Formulir(Foto)'),
     anchor : '100%',
		style: {
			color: '#0000ff' ,
			//text-decoration:'underline',
			//text-decoration: 'underline',
			cursor:'pointer'
		},
     name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name,
     listeners:{
     	 afterrender: function(component) {
				 
     	      component.getEl().on('click', function() { 
				  
					
				    mFino.widget.SubscriberDetails.prototype.showImage(component.getValue())
     	        
     	      });  
     	      
     	    }
                  	
     }
 }
/* ,{
	 xtype : 'displayfield',
	 fieldLabel: 'Tipe Pengguna',
	 itemId : 'sub.form.authoiddescription',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoIDDescription._name
 }*/
 ]
        },
        {
            columnWidth: 0.53,
            bodyStyle: 'padding:0 10px 0;',
        layout: 'form',
        items: [
				{
					 xtype : 'displayfield',
					 fieldLabel: 'Pendapatan Per Bulan',
//					 itemId : 'sub.form.authoiddescription',
					 anchor : '100%',
					 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Income._name
				},
				{
					 xtype : 'displayfield',
					 fieldLabel: 'Tujuan Pembukaan Rekening',
//					 itemId : 'sub.form.authoiddescription',
					 anchor : '100%',
					 name: CmFinoFIX.message.JSSubscriberMDN.Entries.GoalOfAcctOpening._name
				},

				{
					 xtype : 'displayfield',
					 fieldLabel: 'Sumber Dana',
//					 itemId : 'sub.form.authoiddescription',
					 anchor : '100%',
					 name: CmFinoFIX.message.JSSubscriberMDN.Entries.SourceOfFund._name
				},
				{
	                xtype: "displayfield",
	                fieldLabel: _('Dokumen Lain(Foto)'),
	                anchor : '100%',
					style: {
						color: '#0000ff' ,
						//text-decoration:'underline',
						//text-decoration: 'underline',
						cursor:'pointer'
					},
	                name: CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name,
	                listeners:{
	                	 afterrender: function(component) {
							 
	                	      component.getEl().on('click', function() { 
							  
								
							    mFino.widget.SubscriberDetails.prototype.showImage(component.getValue())
	                	        
	                	      });  
	                	      
	                	    }
	                             	
	                }
	            },
				{
					 xtype : 'displayfield',
					 fieldLabel: 'Jaringan Kantor Bank',
					 itemId : 'sub.form.agentbranch',
					 anchor : '100%',
					 name: CmFinoFIX.message.JSSubscriberMDN.Entries.UserBankBranch._name
				},

				{
					 xtype : 'displayfield',
					 fieldLabel: 'Nama Agen',
					 itemId : 'sub.form.agentname',
					 anchor : '100%',
					 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AgentName._name
				},

				{
					 xtype : 'displayfield',
					 fieldLabel: 'Nomor Identifikasi Agen',
					 itemId : 'sub.form.agentcode',
					 anchor : '100%',
					 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AgentCode._name
				}
                ]
        }
        ]
    }]
};




/*
 * Basic Detail
 **/
var subsBasicDetailLakuView = {
    title: '',
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [
			{
			    xtype : 'displayfield',
			    fieldLabel: _("CIF No."),
			    allowBlank: false,
			    labelSeparator : '',
			    anchor : '100%',
			    disabled: false,
			    maxLength : 255,
				itemId  : 'sub.form.cifno',
				name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApplicationID._name           
			},
			{
			    xtype : 'displayfield',
			    fieldLabel: _("No. KTP"),
			    allowBlank: false,
			    anchor : '100%',
			    disabled: false,
			    maxLength : 255,
				itemId  : 'sub.form.ktpid',
				name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPID._name           
			},
			{
			    xtype : 'displayfield',
			    fieldLabel: _("Berlaku Hingga"),
			    anchor : '100%',
			    disabled: false,
			    allowBlank: true,
			    editable:false,
				maxLength : 255,
				itemId  : 'sub.form.IdValidUntil',
				name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDValidUntil._name
			},{
				xtype : 'checkbox',
//		        itemId : 'SMS',
		        boxLabel: 'Seumur Hidup',
		        inputValue:'true',
		        itemId  : 'sub.form.ISIDLifetime',
				name: CmFinoFIX.message.JSSubscriberMDN.Entries.IsIdLifetimeText._name,
				listeners: {
			         check: function(thisObj,checked){
			        	 this.findParentByType('SubscriberLakupandaiViewForm').onIDLifeTime(checked);
//			             alert(checked)
			        	
			                         
			         }
				}
			}
            ]
};

var subsotdetailLakuView = {
	    title: _(''),
	    autoHeight: true,
	    width: 680,
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
	        items: [ subsAdditionalLakuView]
	    },
	    {
	        columnWidth: 0.5,
	        autoHeight: true,
		    layout: 'form',
		    bodyStyle: 'padding:0 10px 0;',
	    items: [
	            
	            {
	   	 xtype : 'displayfield',
		 fieldLabel: 'Propinsi',
		 itemId : 'sub.form.regiionname',
//		 disabled: true,
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPRegionName._name
	 },        {
	   	 xtype : 'displayfield',
		 fieldLabel: 'Kabupaten/Kota',
		 itemId : 'sub.form.city',
//		 disabled: true,
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPCity._name
	 },        {
	   	 xtype : 'displayfield',
		 fieldLabel: 'Kecamatan',
		 itemId : 'sub.form.ktpstate',
//		 disabled: true,
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPState._name
	 },
     {
	   	 xtype : 'displayfield',
		 fieldLabel: 'Desa/Kelurahan',
		 itemId : 'sub.form.ktpsubstate',
//		 disabled: true,
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPSubState._name
	 },
	 {
         xtype: "displayfield",
         fieldLabel: _('KTP (Foto)'),
         anchor : '100%',
			style: {
				color: '#0000ff' ,
				//text-decoration:'underline',
				//text-decoration: 'underline',
				cursor:'pointer'
			},
         name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,
         listeners:{
         	 afterrender: function(component) {
					 
         	      component.getEl().on('click', function() { 
					  
						
					    mFino.widget.SubscriberLakupandaiViewForm.prototype.showImage(component.getValue())
         	        
         	      });  
         	      
         	    }
                      	
         }
     },{
    	xtype:'displayfield',
    	height:45
     },{
    	xtype:'displayfield',
    	height:45 
     },{
    	xtype:'displayfield',
//    	height:5 
     },
     {
			 xtype : "displayfield",
			 anchor : '100%',
			 //fieldLabel :_("Provincial"),
			 fieldLabel :_("Propinsi"),
			 itemId : 'sub.form.ProvincialCom',
			 triggerAction: "all",
			 editable:false,
			 allowBlank: true,
			 emptyText : '<Select one..>',
			 name: CmFinoFIX.message.JSSubscriberMDN.Entries.RegionName._name,
			 store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvince), 
			 displayField: CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
			 valueField : CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
			 hiddenName : CmFinoFIX.message.JSSubscriberMDN.Entries.RegionName._name,
			 listeners: {
				 	select: function(field,record) {
				 	this.findParentByType('SubscriberLakupandaiViewForm').onProvince2(field,record);
			    }
			}
      },
      {
       	 xtype : "displayfield",
       	editable:false,
            anchor : '100%',
            //fieldLabel :_("Region/City"),
            fieldLabel :_("Kabupaten/Kota"),
            itemId : 'sub.form.CityCom',
            triggerAction: "all",
            allowBlank: true,
            emptyText : '<Select one..>',
            mode: 'local',
            name: CmFinoFIX.message.JSSubscriberMDN.Entries.City._name,
            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvinceRegion), 
            displayField: CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
            valueField : CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
            hiddenName :CmFinoFIX.message.JSSubscriberMDN.Entries.City._name,
			 listeners: {
				 	select: function(field,record) {
				 	this.findParentByType('SubscriberLakupandaiViewForm').onProvinceRegion2(field,record);
			    }
			}
       },
       {
        	 xtype : "displayfield",
        	 editable:false,
             anchor : '100%',
             //fieldLabel :_("District"),
             fieldLabel :_("Kecamatan"),
             itemId : 'sub.form.DistrictCom',
             triggerAction: "all",
             emptyText : '<Select one..>',
             mode: 'local',
             name: CmFinoFIX.message.JSSubscriberMDN.Entries.State._name,
               store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSDistrict), 
             displayField: CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
             valueField : CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
             hiddenName : CmFinoFIX.message.JSSubscriberMDN.Entries.State._name,
			 listeners: {
				 	select: function(field,record) {
				 	this.findParentByType('SubscriberLakupandaiViewForm').onDistrict2(field,record);
			    }
			}
        },
        {
              xtype : "displayfield",
              editable:false,
              anchor : '100%',
            //fieldLabel :_("Village"),
            fieldLabel :_("Desa/Kelurahan"),
            itemId : 'sub.form.VillageCom',
              triggerAction: "all",
              emptyText : '<Select one..>',
              mode: 'local',
            name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubState._name,
              store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSVillage), 
              displayField: CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
              valueField : CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
              hiddenName : CmFinoFIX.message.JSSubscriberMDN.Entries.SubState._name,
           },
		{
		   	 xtype : 'displayfield',
			 fieldLabel: 'Kode Pos',
			 itemId : 'sub.form.zipcode',
			 anchor : '100%',
			 name: CmFinoFIX.message.JSSubscriberMDN.Entries.ZipCode._name
		 }
	 ]
	    }]
	    }]
	};
Ext.reg("SubscriberLakupandaiViewForm", mFino.widget.SubscriberLakupandaiViewForm);
