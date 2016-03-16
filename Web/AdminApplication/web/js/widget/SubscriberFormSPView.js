/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberFormSPView = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.SubscriberFormSPView.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.SubscriberFormSPView, Ext.form.FormPanel, {
	
    initComponent : function ()
    {
    	this.subscribercombo = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSSubscriberMDN);
        this.labelWidth = 180;
        this.labelPad = 20;
        this.autoScroll = true;
        this.frame = true;
   
        this.items = [ {
            layout:'column',
            
            items : [
            {
                //columnWidth: 0.6,
            	columnWidth: 1,
                items : [
                {
                    //width: 300,
                    autoHeight: true,
                    allowBlank: false,
                    layout: 'form',
                    items: [
								{
									xtype : 'displayfield',
									fieldLabel: _("Nama Nasabah (Sesuai KTP)"),
									anchor : '100%',
									allowBlank: true,
									maxLength : 255,
									itemId : 'subsp.form.firstname',
									name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name
								},
								{
									xtype : "displayfield",
									fieldLabel :_("Nomor KTP"),
									anchor : '100%',
									allowBlank: true,
									maxLength : 255,
									itemId  : 'subsp.form.ktpid',
									name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApplicationID._name
								},
								{
									xtype : "displayfield",
									fieldLabel :_("Tanggal Lahir"),
									anchor : '100%',
									allowBlank: true,
									maxLength : 255,
									itemId  : 'subsp.form.DateofBirth',
									name: CmFinoFIX.message.JSSubscriberMDN.Entries.DateOfBirthText._name
								},
								{
									xtype : "displayfield",
									fieldLabel :_("Is Id Lifetime"),
									anchor : '100%',
									allowBlank: true,
									maxLength : 255,
									itemId  : 'subsp.form.ISIDLifetime',
									name: CmFinoFIX.message.JSSubscriberMDN.Entries.IsIdLifetimeText._name
								},
								{
									xtype : "displayfield",
									fieldLabel :_("Masa Berlaku (Hingga)"),
									anchor : '100%',
									allowBlank: true,
									maxLength : 255,
									itemId  : 'subsp.form.IdValidUntil',
									name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDValidUntilText._name
								},
								{
									xtype : "displayfield",
									fieldLabel :_("Nomor Telepon Selular (Handphone)"),
									anchor : '100%',
									allowBlank: true,
									maxLength : 255,
									itemId : 'subsp.form.mdn',
									name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name
								},
	                           {
	                               xtype : 'label',
	                               text :'DATA PRIBADI',
	                               name: 'PersonalData',
	                               anchor : '100%',
	                               style: 'font-weight:bold;'
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Alamat (Sesuai KTP)"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.AlamatKTP',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.PlotNo._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("RT"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.RT',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.RT._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("RW"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.RW',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.RW._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Desa/Kelurahan"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.TownVillage',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubState._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Kecamatan"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.SubState',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.StreetAddress._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Kabupaten/Kota"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.City',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.City._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Propinsi"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.Country',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.RegionName._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Kode Pos"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.ZipCode',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.ZipCode._name
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Nama lbu Kandung"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.MothersMaidenName',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.MothersMaidenName._name
	                           },
	                           {
	                               xtype:'displayfield',
	                               fieldLabel: _('Alamat Email'),
	                               anchor : '100%',
	                               allowBlank : true,
	                               maxLength : 255,
	                               itemId : 'subsp.form.email',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name
	                           },
	                           {
	                               xtype : 'label',
	                               text :'	Dokumen Lampiran',
	                               name: 'Data1',
	                               anchor : '100%',
	                               style: 'font-weight:bold;'
	                           },
	                           //documents,
	                           {
	                               xtype: "displayfield",
	                               fieldLabel: _('1) KTPDocument'),
	                               anchor : '100%',
	               				   style: {
	               				   color: '#0000ff' ,
	               				   cursor:'pointer'
	               				},
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,
	                               listeners:{
	                               	 afterrender: function(component) {
	                               	      component.getEl().on('click', function() { 
	               						    mFino.widget.SubscriberFormSPView.prototype.showImage(component.getValue())
	                               	      });  
 	                               	   }
	                               }
	                           },
	               			   {
	                               xtype: "displayfield",
	                               fieldLabel: _('2) Subscriber Form'),
	                               anchor : '100%',
		               				style: {
		               					color: '#0000ff' ,
		               					cursor:'pointer'
		               				},
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name,
	                               listeners:{
	                               	 afterrender: function(component) {
	                               	      component.getEl().on('click', function() { 
	               						    mFino.widget.SubscriberFormSPView.prototype.showImage(component.getValue())
	                               	      });  
	                               	  }
	                               }
	                           },
	                           {
	                               xtype: "displayfield",
	                               fieldLabel: _('3) Supporting Document'),
	                               anchor : '100%',
	               				style: {
	               					color: '#0000ff' ,
	               					cursor:'pointer'
	               				},
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name,
	                               listeners:{
	                               	 afterrender: function(component) {
	                               	      component.getEl().on('click', function() { 
	               						    mFino.widget.SubscriberFormSPView.prototype.showImage(component.getValue())
	                               	      });  
	                               	   }
	                               }
	                           },
	                           {
	                               xtype : 'label',
	                               text :'Data Agen',
	                               name: 'Data1',
	                               anchor : '100%',
	                               style: 'font-weight:bold;'
	                           },
	                           {
	                               xtype : "displayfield",
	                               fieldLabel :_("Jaringan Kantor Bank"),
	                               anchor : '100%',
	                               allowBlank: true,
	                               maxLength : 255,
	                               itemId  : 'subsp.form.UserBankBranch',
	                               name: CmFinoFIX.message.JSSubscriberMDN.Entries.UserBankBranch._name
	                           },
		             			{
		           	                xtype : 'displayfield',
		           	                fieldLabel: _("Nama Agen"),
		                            anchor : '100%',
		                            allowBlank: true,
		                            maxLength : 255,
		           	                itemId : 'subsp.form.AgentName',
		           	                name: CmFinoFIX.message.JSSubscriberMDN.Entries.AgentName._name
		           	            },	                           
								{
								    xtype : 'displayfield',
								    fieldLabel :_("Nomor Identifikasi Agen"),
								    anchor : '100%',
								    allowBlank: true,
								    maxLength : 255,
								    itemId  : 'subsp.form.AgentCode',
								    name: CmFinoFIX.message.JSSubscriberMDN.Entries.AgentCode._name
								}                            
                            
/*                            {
                                xtype : 'textfield',
                                fieldLabel: mobile,
                                itemId : 'subsp.form.mobileno',
                                name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
                                allowBlank: false,
                                //vtype: 'smarttelcophoneAddMore',
                                listeners: {
                                    change: function(field) {
                                    	this.findParentByType('SubscriberFormSPView').onMDN(field);
                                    }
                                },
                                emptyText: _(''),
                                blankText : _('Mobile Number is required'),
                                anchor : '100%'
                            },*/                            
/*                   {
                        xtype : "remotedropdown",
                        anchor : '100%',
                        allowBlank: false, 
                        blankText : _('KYC is required'),
                        itemId : 'subsp.form.KYCLevel',
                        id : 'subsp.form.KYCLevel',
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
                            	var kf_combo = Ext.getCmp("subsp.form.kycfield");
                            	kf_combo.store.reload({
                    				params: {KYCFieldsLevelID : kyc },
                    				callback:function(){}
                    			});
                            	var SubscriberFormSPView = this.findParentByType('SubscriberFormSPView');
                            	SubscriberFormSPView.onKYCDropdown(kyc);
                            	var subscriberID = SubscriberFormSPView.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name);
                            	if(subscriberID == null) { //while subscriber registration
                            		if(kyc == CmFinoFIX.RecordType.SubscriberFullyBanked
                            				|| kyc == CmFinoFIX.RecordType.SubscriberSemiBanked){
                            			SubscriberFormSPView.setAccountAndTemplateDisplay(true);
                            			SubscriberFormSPView.loadBankPocketTemplateCombo();
                                	} else {
                                		SubscriberFormSPView.setAccountAndTemplateDisplay(false);
                                	}
                            	} else { //while subscriber update
                            		if(kyc == CmFinoFIX.RecordType.SubscriberFullyBanked
                            				|| kyc == CmFinoFIX.RecordType.SubscriberSemiBanked){
                                		var actualKyc = SubscriberFormSPView.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name);
                                		if(actualKyc == CmFinoFIX.RecordType.SubscriberUnBanked || actualKyc === 0) {
                                			SubscriberFormSPView.setAccountAndTemplateDisplay(true);
                                			SubscriberFormSPView.loadBankPocketTemplateCombo();
                                		}
                                	}else{                            		
                                		SubscriberFormSPView.setAccountAndTemplateDisplay(false);
                                	}
                            	}                                
                            }
                        }
                       
                    },*/
/*                    {
                        xtype : 'textfield',
                        //fieldLabel: _("kyc"),
                        fieldLabel :kyc,
                        itemId : 'subsp.form.KYCLevel',
                        anchor : '100%',
                        allowBlank: false,
                        disabled: false,
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevelText._name           
                    },*/
/*                    {
                        xtype : 'textfield',
                        fieldLabel:firstname,
                        itemId : 'subsp.form.firstname',
                        allowBlank: false,
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel: lastname,
                        allowBlank: false,
                        itemId : 'subsp.form.lastname',
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name
                    },*/
/*                    {
                        xtype : 'textfield',
                        //fieldLabel: nickname,
                        fieldLabel: _("Nicknameeeeeee"),
                        itemId : 'subsp.form.nickname',
                        allowBlank: true,
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nickname._name
                    },*/
/*                   {
                   	 xtype : 'datefield',
                   	 allowBlank: true,
                   	 editable: false,
                	 fieldLabel: dateofbirth,
                	 itemId : 'subsp.form.dateofbirth',
                	 anchor : '100%',
                	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.DateOfBirth._name,
                     maxValue:new Date().add('d',-1),
                     maxText:'Date of birth should not be future date',
                     listeners: {
                         change: function(field) {
                        	 this.findParentByType('SubscriberFormSPView').onDOBSelect(field);
                        	                          }}
                	},*/
/*                    {
                        xtype : 'textfield',
                        fieldLabel: city,
                        allowBlank: true,
                        itemId : 'subsp.form.city',
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.City._name
                    },*/
 /*                   {
                        xtype : 'textfield',
                        fieldLabel: email,
                        itemId : 'subsp.form.email',
                        vtype: 'email',
                        anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name,
                        listeners: {
                        	blur: function(field) {
//                        		this.findParentByType('SubscriberFormSPView').updateEmailCheck(field);
                        	}
                        }
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel: _("Reg Branch Code"),
                        allowBlank: false,
                        itemId : 'subsp.form.applicationid',
						vtype: 'name',
						anchor : '100%',
                        name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApplicationID._name
                    },
                    {
                	    xtype : 'textfield',
                	    fieldLabel: subsrefaccount,
                	    itemId : 'subsp.form.subsrefaccount',
                	    allowBlank: true,
                	    anchor : '100%',
                	    //vtype:'number19',
                	    name: CmFinoFIX.message.JSSubscriberMDN.Entries.ReferenceAccount._name
                    },
                    {
                    	xtype : "combo",
                        itemId : 'subsp.form.kycfield',
                        id : 'subsp.form.kycfield',
                        hidden:true,
                        lastQuery: '',
                        store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSKYCCheckFields),
                        displayField: CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsName._name,
                        valueField : CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsLevelID._name,            
                        name: CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsLevelID._name,
                        listeners: {
                            reload: function(field) {
                            	var kf_combo = Ext.getCmp("subsp.form.kycfield");
                            	alert("      "+kf_combo.store.getCount());
                            	                            }
                        		}
                     }*/]
                }]
            }
/*            {columnWidth: 0.5,
                items : [subsBasicDetailsp]}*/
            ]
        }/*,
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
                items:[subsotdetailsp]
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
                    items:[subsMoreDetailsp]
                }]
            },
            {
                title : _('Authorization'),
                layout : 'column',
                frame:true,
                autoHeight: true,
                items :
                [subsAthorizingDetailsp]
            },
            {
                title: _('Notification Method'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[subsNotificationMethodsp]
            },
            {
                title: _('Restriction'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[subsRestrictionssp]
            }]
        }*/] ;
        this.subscribercombo.on("load", this.onLoad.createDelegate(this));
        mFino.widget.SubscriberFormSPView.superclass.initComponent.call(this);
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
           	 this.find("itemId", "subsp.form.mobileno")[0].disable(); 
           	 this.find("itemId", "subsp.form.status")[0].disable(); 
      		 this.subscribercombo.remove(record);
          }else if(record!=null){
       	   Ext.MessageBox.alert(_("Alert"), _("MDN already Registered")); 
       	   this.subscribercombo.remove(record);
           }else{           	
           	 this.onStatusDropdown( CmFinoFIX.MDNStatus.Initialized);
           	 this.find("itemId", "subsp.form.mobileno")[0].disable(); 
           	 this.find("itemId", "subsp.form.status")[0].disable(); 
           	 
//           	srecord.set(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,this.find("itemId", "subsp.form.mobileno")[0].getValue());
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
    	 var items = ['subsp.form.KYCLevel','subsp.form.firstname','subsp.form.lastname','subsp.form.nickname','subsp.form.dateofbirth','subsp.form.city','subsp.form.email','subsp.form.applicationid','subsp.form.plotno','subsp.form.streetaddress','subsp.form.regionname','subsp.form.country','subsp.form.idtype','subsp.form.idnumber','subsp.form.expirationtime','subsp.form.proofofaddress','subsp.form.typeofbankaccount','subsp.form.bankaccid','SMS','Email1','Suspended','SecurityLocked','AbsoluteLocked','NoFundMovement','subsp.form.securityquestion','subsp.form.secretanswer','subsp.form.birthplace','subsp.form.nationality','subsp.form.companyname','subsp.form.subscribermobilecompany','subsp.form.coi','subsp.form.authofirstname','subsp.form.authofirstname','subsp.form.autholastname','subsp.form.authorizingpersonid','subsp.form.authodateofbirth','subsp.form.authoiddescription','subsp.form.status','subsp.form.language','subsp.form.currency','subsp.form.timezone','subsp.form.kinname','subsp.form.kinmdn','subsp.form.streetname','subsp.form.group','subsp.form.accountnumber','subsp.form.bankPocketTemplate','subsp.form.otherMDN'];

    	if(status == CmFinoFIX.MDNStatus.PendingRetirement || status==CmFinoFIX.MDNStatus.Retired){
            for(var i=0;i<items.length;i++){
            	this.find('itemId',items[i])[0].disable();
            }
            
            if (status==CmFinoFIX.MDNStatus.Retired && typeof(this.record) != "undefined") {
            	this.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.IsForceCloseRequested._name, true);
            }
        }else{
           // items = ['SMS','Email1','SelfSuspended','Suspended','SecurityLocked','AbsoluteLocked','subsp.form.mobileno','subsp.form.firstname','subsp.form.lastname','form.email','subsp.form.timezone','subsp.form.language','subsp.form.currency','subsp.form.secretanswer','subsp.form.timezone','subsp.form.status'];
            for(i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].enable();
            }
            
			this.loadBankPocketTemplateCombo();
			if((SYSTEM_DEFAULT_ACCOUNT_NUMBER_MANDATORY) && (SYSTEM_DEFAULT_KYC == "3")){
				this.find("itemId", "subsp.form.accountnumber")[0].allowBlank = false;
			}
			
			 if(SYSTEM_DEFAULT_KYC != "3"){
				 this.find('itemId','subsp.form.accountnumber')[0].disable();
				 this.find("itemId", "subsp.form.bankPocketTemplate")[0].disable();
           	 }
			 
            //this.find("itemId", "subsp.form.bankPocketTemplate")[0].disable();
            
            if((status == CmFinoFIX.MDNStatus.Active) || (status == CmFinoFIX.MDNStatus.Initialized)) {
            	this.find('itemId','subsp.form.status')[0].enable();
            } else {
            	this.find('itemId','subsp.form.status')[0].disable();
            }
        }
    },
    
    loadBankPocketTemplateCombo : function() {
    	var kycLevel = this.find('itemId','subsp.form.KYCLevel')[0].getValue();
    	var group = this.find('itemId','subsp.form.group')[0].getValue();    	
    	var bankPocketField = this.find('itemId','subsp.form.bankPocketTemplate')[0];
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

			var kf_combo = Ext.getCmp("subsp.form.kycfield");
			var kf_combo_id='subsp.form.kyc';
			
			/*````````````````````````````````````````````````````````````````*/
			kf_combo.store.clearFilter(); 
			kf_combo.store.reload({
				isAutoLoad: true,
				params: {KYCFieldsLevelID : kyc },
				callback:function(){}
			});
			
			var kf_combo = Ext.getCmp("subsp.form.kycfield");
			var kycFieldSize = kf_combo.store.getCount();

			var fn;
			var items=new Array();
			for ( var i = 0; i < kycFieldSize; i++) {
				fn=kf_combo.store.getAt(i).data.KYCFieldsName;
				items[i]="subsp.form."+fn;
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
		 var aitems = ['subsp.form.authofirstname','subsp.form.autholastname','subsp.form.authorizingpersonid','subsp.form.authodateofbirth'];
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
			this.form.items.get("subsp.form.authodateofbirth").setValue('');
			}

    },
    save : function(){
    	if(this.getForm().isValid()){
    		
            this.getForm().updateRecord(this.record);
           /* var isEmailChecked = this.form.items.get("Email1").getValue();
	        if (this.form.items.get("subsp.form.email").getValue() === "" && isEmailChecked) {
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

/*        this.find('itemId','subsp.form.subsrefaccount')[0].disable();
        this.find('itemId','subsp.form.creditcheck')[0].disable();*/

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
        var checkAbleItems = ['subsp.form.mobileno'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
    },
    enablePermittedItems: function(){
        var checkAbleItems = ['subsp.form.mobileno'];
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
            this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
        } else {
        	if (CmFinoFIX.SubscriberStatus.Suspend === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("subsp.form.status").setValue(currentStatus);
        }
    },
    onSecurityLockClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("SecurityLocked").checked) {
            this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("subsp.form.status").setValue(currentStatus);
        }
    },
    onAbsoluteLockClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("AbsoluteLocked").checked) {
            this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("subsp.form.status").setValue(currentStatus);
        }
    },
     onNoFundMovementClick: function(){
    	var currentStatus = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
        if(this.form.items.get("NoFundMovement").checked) {
            this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("Suspended").checked) {
                this.form.items.get("subsp.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive === currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Active;
        		if(this.form.items.get("Suspended").checked) {
                	currentStatus = CmFinoFIX.SubscriberStatus.Suspend;
            	}
        	}
            this.form.items.get("subsp.form.status").setValue(currentStatus);
        }
    },
	onCheckEmail: function(){
	    var isEmailChecked = this.form.items.get("Email1").getValue();
	       if (this.form.items.get("subsp.form.email").getValue() === "" && isEmailChecked) {
	       	alert("Enter a valid email");
			this.form.items.get("Email1").setValue(false);
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
	setAccountAndTemplateDisplay: function(isVisible) {
		var accNo = this.find('itemId','subsp.form.accountnumber')[0];
		var bankTemplate = this.find('itemId','subsp.form.bankPocketTemplate')[0];
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
		 itemId : 'subsp.form.plotno',
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.PlotNo._name
	 },{
   	 xtype : 'textfield',
	 fieldLabel: streetaddress,
	 itemId : 'subsp.form.streetaddress',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.StreetAddress._name
 },{
	 xtype : 'textfield',
	 fieldLabel: regionname,
	 itemId : 'subsp.form.regionname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.RegionName._name
 },/*{
	 xtype : 'textfield',
	 fieldLabel: country,
	 itemId : 'subsp.form.country',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Country._name
 },*/{
	 xtype : 'textfield',
	 fieldLabel: idtype,
	 itemId : 'subsp.form.idtype',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDType._name
 },{
	 xtype : 'textfield',
	 fieldLabel: idnumber,
	 itemId : 'subsp.form.idnumber',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDNumber._name
 },{
	 xtype : 'datefield',
	 fieldLabel: expirationtime,
	 editable: false,
	 itemId : 'subsp.form.expirationtime',
	 anchor : '100%',
	 minValue:new Date().add('d',1),
     minText:'Must be future date',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.ExpirationTime._name
 },{
	 xtype : 'textfield',
	 fieldLabel: proofofaddress,
	 itemId : 'subsp.form.proofofaddress',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.ProofofAddress._name
 },{
     xtype : 'textfield',
     itemId : 'subsp.form.typeofbankaccount',
     hidden:true,
     anchor : '100%',
     name: CmFinoFIX.message.JSSubscriberMDN.Entries.TypeofBankAccount._name
 },{
	 xtype : 'textfield',
	 itemId : 'subsp.form.bankaccid',
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
var subsNotificationMethodsp = {
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
                //this.findParentByType("SubscriberFormSPView").onCheckEmail();  //commented to fix #3381
            }
        }
    }]
    }]
};

/*
 * Subscriber Restrictions
 **/
var subsRestrictionssp = {
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
                    this.findParentByType("SubscriberFormSPView").onSecurityLockClick();
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
                    this.findParentByType("SubscriberFormSPView").onAbsoluteLockClick();
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
                    this.findParentByType("SubscriberFormSPView").onSuspendClick();
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
                    this.findParentByType("SubscriberFormSPView").onNoFundMovementClick();
                }
            }
        }]
    }]
};


/*
 * Subscriber more Details
 **/
var subsMoreDetailsp = {
    title: _(''),
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [{
        xtype : 'textfield',
        anchor : '100%',
        allowBlank: true,
        itemId : 'subsp.form.securityquestion',
        fieldLabel : secretquestion,
        name : CmFinoFIX.message.JSSubscriberMDN.Entries.SecurityQuestion._name
    },
    {
        xtype : 'textfield',
        fieldLabel : secretanswer,
        anchor : '100%',
        allowBlank: true,
        itemId : 'subsp.form.secretanswer',
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
var subsOtherDetailsp = {
		xtype: 'fieldset',
	    title: 'Other Details',
	    autoHeight: true,
	    width: 280,
	    layout: 'form',
    items: [{
	 xtype : 'textfield',
	 fieldLabel: birthplace,
	 itemId : 'subsp.form.birthplace',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
 },{
	 xtype : 'textfield',
	 fieldLabel: nationality,
	 itemId : 'subsp.form.nationality',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nationality._name
 }/*,{
	 xtype : 'textfield',
	 fieldLabel: companyname,
	 itemId : 'subsp.form.companyname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CompanyName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: subscribermobilecompany,
	 itemId : 'subsp.form.subscribermobilecompany',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberMobileCompany._name
 },{
	 xtype : 'textfield',
	 fieldLabel: coi,
	 itemId : 'subsp.form.coi',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CertofIncorporation._name
 }*/]
};


var documents = {
		xtype: 'fieldset',
	    title: 'Dokumen Lampiran',
	    autoHeight: true,
	    //width: 410,
	    columnWidth: 1,
	    layout: 'form',
    items: [
           /* {
                xtype: "displayfield",
                fieldLabel: _('KTP Document'),
                anchor : '150%',
				style: {
					color: '#0000ff' ,
					cursor:'pointer'
				},
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,
                listeners:{
                	 afterrender: function(component) {
                	      component.getEl().on('click', function() {
						    mFino.widget.SubscriberFormSPView.prototype.showImage(component.getValue())
                	      });  
                	 }
                }
            },
			{
                xtype: "displayfield",
                fieldLabel: _('Subscriber Form'),
                anchor : '150%',
				style: {
					color: '#0000ff' ,
					cursor:'pointer'
				},
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name,
                listeners:{
                	 afterrender: function(component) {
                	      component.getEl().on('click', function() { 
						    mFino.widget.SubscriberFormSPView.prototype.showImage(component.getValue())
                	      });  
                	      
                	 }
                }
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Supporting Document'),
                anchor : '150%',
				style: {
					color: '#0000ff' ,
					cursor:'pointer'
				},
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name,
                listeners:{
                	 afterrender: function(component) {
                	      component.getEl().on('click', function() { 
						    mFino.widget.SubscriberFormSPView.prototype.showImage(component.getValue())
                	      });  
                	 }
                }
            }*/
            
/*{
	 xtype : 'textfield',
	 fieldLabel: birthplace,
	 itemId : 'subsp.form.birthplace',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
 },{
	 xtype : 'textfield',
	 fieldLabel: nationality,
	 itemId : 'subsp.form.nationality',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nationality._name
 }*//*,{
	 xtype : 'textfield',
	 fieldLabel: companyname,
	 itemId : 'subsp.form.companyname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CompanyName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: subscribermobilecompany,
	 itemId : 'subsp.form.subscribermobilecompany',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberMobileCompany._name
 },{
	 xtype : 'textfield',
	 fieldLabel: coi,
	 itemId : 'subsp.form.coi',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CertofIncorporation._name
 }*/]
};



/*
 * subsAthorizingDetail
 **/
var subsAthorizingDetailsp = {
    title: '',
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [{
	 xtype : 'textfield',
	 fieldLabel: firstname,
	 itemId : 'subsp.form.authofirstname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoFirstName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: lastname,
	 itemId : 'subsp.form.autholastname',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoLastName._name
 },{
	 xtype : 'textfield',
	 fieldLabel: idnumber,
	 itemId : 'subsp.form.authorizingpersonid',
//	 vtype:'number19',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthorizingPersonIDNumber._name
 },{
	 xtype : 'datefield',
	 fieldLabel: dateofbirth,
	 editable: false,
	 itemId : 'subsp.form.authodateofbirth',
	 anchor : '100%',
	 maxValue:new Date().add('d',-1),
     maxText:'Date of birth should not be future date',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoDateofBirth._name,
	 listeners: {
         change: function(field) {
        	 this.findParentByType('SubscriberFormSPView').onAuthoDOBSelect(field);
        	                          }}
 },{
	 xtype : 'textfield',
	 fieldLabel: description,
	 itemId : 'subsp.form.authoiddescription',
	 anchor : '100%',
	 name: CmFinoFIX.message.JSSubscriberMDN.Entries.AuthoIDDescription._name
 }]
};




/*
 * Basic Detail
 **/
var subsBasicDetailsp = {
    title: '',
    autoHeight: true,
    width: 300,
    layout: 'form',
    items: [{
                        xtype : "enumdropdown",
                         anchor : '100%',
                        allowBlank: false,
                        blankText : _('Status is required'),
                        itemId : 'subsp.form.status',
                        //emptyText:'Initialized',
                        emptyText : _('<select one..>'),
                        fieldLabel :status,
                        addEmpty: false,
                        enumId : CmFinoFIX.TagID.SubscriberStatus,
                        name : CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name,
                        value : CmFinoFIX.MDNStatus.Initialized
/*                        listeners : {
                            select :  function(field){
                                var status= field.getValue();
                                this.findParentByType('SubscriberFormSPView').onStatusDropdown(status);
                            }
                        }*/
                    },{
    xtype : "enumdropdown",
     anchor : '100%',
    fieldLabel :language,
    itemId : 'subsp.form.language',
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
    itemId : 'subsp.form.currency',
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
    itemId : 'subsp.form.timezone',
    enumId: CmFinoFIX.TagID.Timezone,
    name : CmFinoFIX.message.JSSubscriberMDN.Entries.Timezone._name
},
/*{
    xtype : 'textfield',
    fieldLabel: _("Next Of Kin"),
    itemId : 'subsp.form.kinname',
    allowBlank: true,
    anchor : '100%',
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.KinName._name
},
{
    xtype : 'textfield',
    fieldLabel: 'Next Of Kin No',
    itemId : 'subsp.form.kinmdn',
    allowBlank: true,
    anchor : '100%',
    vtype: 'smarttelcophoneAdd',
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.KinMDN._name,
    listeners: {
        change: function(field) {
        						}
    			}
},*/
/*{
	xtype : "remotedropdown",
	anchor : '100%',
	allowBlank: false,
	addEmpty: false,
	itemId : 'subsp.form.group',
	id : 'subsp.form.item.group',
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
			var bankPocketField = this.findParentByType('SubscriberFormSPView').find('itemId','subsp.form.bankPocketTemplate')[0];
			if(!bankPocketField.disabled) {
				this.findParentByType('SubscriberFormSPView').loadBankPocketTemplateCombo();
			}			
		}
	}
},*/
{
    xtype : 'textfield',
    fieldLabel: _("Group"),
    itemId : 'subsp.form.group',
    anchor : '100%',
    disabled: false,
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.GroupName._name           
},
{
    xtype : 'textfield',
    fieldLabel: _("Account No:"),
    itemId : 'subsp.form.accountnumber',
    vtype:'tendigitnumber',
    labelSeparator : '',
    anchor : '100%',
    disabled: false,
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.AccountNumber._name            
},
/*{
	xtype : "remotedropdown",
	anchor : '100%',	
	addEmpty: false,
	itemId : 'subsp.form.bankPocketTemplate',
	id : 'subsp.form.item.bankPocketTemplate',
	fieldLabel :"Bank Pocket Template",
	emptyText : _('<select one..>'),
	RPCObject : CmFinoFIX.message.JSPocketTemplateConfig,
	displayField: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateDescription._name,
	valueField : CmFinoFIX.message.JSPocketTemplateConfig.Entries.ID._name,
	name: CmFinoFIX.message.JSSubscriberMDN.Entries.PocketTemplateConfigID._name
},
{
    xtype : 'textfield',
    fieldLabel: _("Bank Pocket Template"),
    itemId : 'subsp.form.bankPocketTemplate',
    anchor : '100%',
    disabled: false,
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.PocketTemplateConfigID._name            
},
{
    xtype : 'textfield',
    fieldLabel: _("Other MDN:"),
    itemId : 'subsp.form.otherMDN',
    vtype:'smarttelcophoneAdd',
    labelSeparator : '',
    anchor : '100%',
    disabled: false,
    name: CmFinoFIX.message.JSSubscriberMDN.Entries.OtherMDN._name            
}*/
]
};
var subsotdetailsp = {
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
	    items: [/*{
	   	 xtype : 'textfield',
		 fieldLabel: creditcheck,
		 itemId : 'subsp.form.creditcheck',
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.CreditCheck._name
	 },*//*{
	  	 xtype : 'textfield',
		 itemId : 'subsp.form.streetname',
		 hidden:true,
		 anchor : '100%',
		 name: CmFinoFIX.message.JSSubscriberMDN.Entries.StreetName._name
	},*/subsOtherDetailsp]
	    }]
	    }]
	};
Ext.reg("SubscriberFormSPView", mFino.widget.SubscriberFormSPView);
