/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerFormspView = function (config) {
	 var localConfig = Ext.apply({}, config);
	  	 localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.ServicePartnerFormspView.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ServicePartnerFormspView, Ext.form.FormPanel, {
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
            //width:860,
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
	                            anchor : '100%',
	                            allowBlank: true,
	                            maxLength : 255,
	           	                itemId : 'servicepartner.form.username',
	           	                name: CmFinoFIX.message.JSAgent.Username._name
	           	            },
	       	               {
	       	                   xtype : "textfield",
	       	                   fieldLabel :_("Nomor KTP"),
	                           anchor : '100%',
	                           allowBlank: true,
	                           maxLength : 255,
	       	                   itemId  : 'servicepartner.form.ktpid',
	       	                   name: CmFinoFIX.message.JSAgent.KTPID._name
	       	               },
	       	               {
	       	                   xtype : "textfield",
	       	                   fieldLabel :_("Nomor Telepon Selular (Handphone)"),
	                           anchor : '100%',
	                           allowBlank: true,
	                           maxLength : 255,
	       	                   itemId : 'servicepartner.form.mdn',
	       	                   name: CmFinoFIX.message.JSAgent.MDN._name
	       	               },
	       	               {
	       	                   xtype : "textfield",
	       	                   fieldLabel :_("Nomor Rekening Bank Sinarmas"),
	                           anchor : '100%',
	                           allowBlank: true,
	                           maxLength : 255,
	       	                   itemId  : 'servicepartner.form.AccountnumberofBankSinarmas',
	       	                   name: CmFinoFIX.message.JSAgent.AccountnumberofBankSinarmas._name
	       	               },
	       	               {
	       	                   xtype : "textfield",
	       	                   fieldLabel :_("Cabang"),
	                           anchor : '100%',
	                           allowBlank: true,
	                           maxLength : 255,
	       	                   itemId  : 'servicepartner.form.BranchCode',
	       	                   name: CmFinoFIX.message.JSAgent.BranchCodeText._name
	       	               },
                           {
                               xtype : 'label',
                               text :'DATA AGEN',
                               name: 'PersonalData',
                               anchor : '100%',
                               style: 'font-weight:bold;'
                           },
							{
							    xtype : 'textfield',
							    fieldLabel :_("Nomor Identifikasi Agen"),
							    anchor : '100%',
							    allowBlank: true,
							    maxLength : 255,
							    blankText : _('Agent Code is required'),
							    itemId  : 'servicepartner.form.AgentCode',
							    name    : CmFinoFIX.message.JSAgent.AgentCode._name
							},
							{
							   xtype : "textfield",
							   fieldLabel :_('Jenis Agen'),
							   anchor : '100%',
							   allowBlank: true,
							   emptyText : _('<select one..>'),
							   blankText : _('Agent Type is required'),
							   enumId: CmFinoFIX.TagID.AgentType,
							   itemId : 'servicepartner.form.AgentType',
							   name : CmFinoFIX.message.JSAgent.AgentTypeText._name
							},
                    	   {
                    		   xtype : "textfield",
                    		   fieldLabel :_('Klasifikasi Agen'),
                    		   anchor : '100%',
                    		   allowBlank: true,
                    		   emptyText : _('<select one..>'),
                    		   blankText : _('Classification Agent is required'),
                    		   enumId: CmFinoFIX.TagID.ClassificationAgent,
                    		   itemId : 'servicepartner.form.ClassificationAgent',
                    		   name : CmFinoFIX.message.JSAgent.ClassificationAgentText._name
                    	   },
                     	   {
                     		   xtype : "textfield",
                     		   fieldLabel :_('Jenis Usaha Agen'),
                     		   anchor : '100%',
                     		   allowBlank: true,
                     		   emptyText : _('<select one..>'),
                     		   blankText : _('Type of Business Agent is required'),
                     		   enumId: CmFinoFIX.TagID.TypeofBusinessAgent,
                     		   itemId : 'servicepartner.form.TypeofBusinessAgent',
                     		   name : CmFinoFIX.message.JSAgent.TypeofBusinessAgentText._name
                     	   },
                     	   {
                     		   xtype : "textfield",
                     		   fieldLabel :_('Yang Digunakan'),
                     		   anchor : '100%',
                     		   allowBlank: true,
                     		   emptyText : _('<select one..>'),
                     		   blankText : _('Electonic Devie used is required'),
                     		   enumId: CmFinoFIX.TagID.ElectonicDevieused,
                     		   itemId : 'servicepartner.form.ElectonicDevieused',
                     		   name : CmFinoFIX.message.JSAgent.ElectonicDevieusedText._name
                     	   },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Nomor Perjanjan Kerjasama"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Agreement Number is required'),
                               itemId  : 'servicepartner.form.AgreementNumber',
                               name: CmFinoFIX.message.JSAgent.AgreementNumber._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Tanggal Perjanjan Kerjasama"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Agreement Date is required'),
                               itemId  : 'servicepartner.form.AgreementDate',
                               name: CmFinoFIX.message.JSAgent.AgreementDate._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Tanggal Pelaksanaan"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,					   
                               blankText : _('Implementation date is required'),
                               itemId  : 'servicepartner.form.Implementationdate',
                               name: CmFinoFIX.message.JSAgent.Implementationdate._name
                           },
                           {
                               xtype : 'label',
                               text :'DATA PRIBADI',
                               name: 'PersonalData',
                               anchor : '100%',
                               style: 'font-weight:bold;'
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Tempat"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Place of birth is required'),
                               itemId  : 'servicepartner.form.PlaceofBirth',
                               name: CmFinoFIX.message.JSAgent.PlaceofBirth._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Tanggal Lahir"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Date of birth is required'),
                               itemId  : 'servicepartner.form.DateofBirth',
                               name: CmFinoFIX.message.JSAgent.DateofBirth._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Alamat (Sesuai KTP)"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Alamat (In Accordance Identity) is required'),
                               itemId  : 'servicepartner.form.AlamatInAccordanceIdentity',
                               name: CmFinoFIX.message.JSAgent.AlamatInAccordanceIdentity._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("RT"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('RT is required'),
                               itemId  : 'servicepartner.form.RTAl',
                               name: CmFinoFIX.message.JSAgent.RTAl._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("RW"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('RW is required'),
                               itemId  : 'servicepartner.form.RWAl',
                               name: CmFinoFIX.message.JSAgent.RWAl._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kelurahan/Desa"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Village is required'),
                               itemId  : 'servicepartner.form.VillageAl',
                               name: CmFinoFIX.message.JSAgent.VillageAl._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kecamatan"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('District is required'),
                               itemId  : 'servicepartner.form.DistrictAl',
                               name: CmFinoFIX.message.JSAgent.DistrictAl._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kabupaten/Kota"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('City is required'),
                               itemId  : 'servicepartner.form.CityAl',
                               name: CmFinoFIX.message.JSAgent.CityAl._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Propinsi"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Provincial is required'),
                               itemId  : 'servicepartner.form.ProvincialAl',
                               name: CmFinoFIX.message.JSAgent.ProvincialAl._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kode Pos"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Potal Code is required'),
                               itemId  : 'servicepartner.form.PotalCodeAl',
                               name: CmFinoFIX.message.JSAgent.PotalCodeAl._name
                           },
                           {
                               xtype:'textfield',
                               fieldLabel: _('Alamat Email'),
                               anchor : '100%',
                               allowBlank : true,
                               maxLength : 255,
                               vtype: 'email',
                               blankText : _('E-Mail is required'),
                               itemId : 'servicepartner.form.email',
                               name: CmFinoFIX.message.JSAgent.EMail._name
                           },
                           {
                               xtype : 'label',
                               text :'DATA PERUSAHAAN',
                               name: 'CompanyData',
                               anchor : '100%',
                               style: 'font-weight:bold;'
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Nama Perusahaan"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Company Name is required'),
                               itemId  : 'servicepartner.form.AgentCompanyName',
                               name: CmFinoFIX.message.JSAgent.AgentCompanyName._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Alamat"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Company Address is required'),
                               itemId  : 'servicepartner.form.CompanyAddress',
                               name: CmFinoFIX.message.JSAgent.CompanyAddress._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("RT"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('RT is required'),
                               itemId  : 'servicepartner.form.RTCom',
                               name: CmFinoFIX.message.JSAgent.RTCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("RW"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('RW is required'),
                               itemId  : 'servicepartner.form.RWCom',
                               name: CmFinoFIX.message.JSAgent.RWCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kelurahan/Desa"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Village is required'),
                               itemId  : 'servicepartner.form.VillageCom',
                               name: CmFinoFIX.message.JSAgent.VillageCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kecamatan"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('District is required'),
                               itemId  : 'servicepartner.form.DistrictCom',
                               name: CmFinoFIX.message.JSAgent.DistrictCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kabupaten/Kota"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('City is required'),
                               itemId  : 'servicepartner.form.CityCom',
                               name: CmFinoFIX.message.JSAgent.CityCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Propinsi"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Provincial is required'),
                               itemId  : 'servicepartner.form.ProvincialCom',
                               name: CmFinoFIX.message.JSAgent.ProvincialCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Kode Pos"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Potal Code is required'),
                               itemId  : 'servicepartner.form.PotalCodeCom',
                               name: CmFinoFIX.message.JSAgent.PotalCodeCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Latitude"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Latitude is required'),
                               itemId  : 'servicepartner.form.Latitude',
                               name: CmFinoFIX.message.JSAgent.Latitude._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Longitude"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Longitude is required'),
                               itemId  : 'servicepartner.form.Longitude',
                               name: CmFinoFIX.message.JSAgent.Longitude._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Nomor Telephon (Fixed Line)"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Phone Number is required'),
                               itemId  : 'servicepartner.form.PhoneNumber',
                               name: CmFinoFIX.message.JSAgent.PhoneNumber._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Alamat Email"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Phone Number is required'),
                               itemId  : 'servicepartner.form.CompanyEmailId',
                               name: CmFinoFIX.message.JSAgent.CompanyEmailId._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Jaringan Kantor Bank"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('User Bank Branch is required'),
                               itemId  : 'servicepartner.form.UserBankBranch',
                               name: CmFinoFIX.message.JSAgent.UserBankBranch._name
                           },
                     	   {
                     		   xtype : "textfield",
                     		   fieldLabel :_('Status'),
                     		   anchor : '100%',
                     		   allowBlank: true,
                     		   emptyText : _('<select one..>'),
                     		   blankText : _('BankAccountStatus is required'),
                     		   enumId: CmFinoFIX.TagID.BankAccountStatus,
                     		   itemId : 'servicepartner.form.BankAccountStatus',
                     		   name : CmFinoFIX.message.JSAgent.BankAccountStatusText._name
                     	   }
                ]
            },
            {
                columnWidth: 0.5,
                xtype: 'panel',
                layout: 'form',
                itemId:'details2',
                labelWidth:120,
                items:[
                ]
            },
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : [
                ]
            } ]
        }];   
        mFino.widget.ServicePartnerFormspView.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.subscribercombo.on("load", this.onLoad.createDelegate(this));
    },
    
    onLoad: function(){
     var record = this.subscribercombo.getAt(0);
   	 var convertAgentStore = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSAgent);
       var mrecord = this.record;
       if(record!=null&&record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberType._name)===CmFinoFIX.SubscriberType.Partner){
    	   Ext.MessageBox.alert(_("Alert"), _("MDN already registered as Partner or Agent"));   
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
    disableNotPermittedItems: function(){
        var checkAbleItems = ['servicepartner.form.status'];//, 'servicepartner.form.DCT', 'servicepartner.form.parentId','servicepartner.form.mdn','servicepartner.form.sourceIp'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
    },
    enablePermittedItems: function(){
        var checkAbleItems = ['servicepartner.form.status', 'servicepartner.form.DCT', 'servicepartner.form.parentId'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            // The above items are disabled only for Edit operation. They should be available for Add.
          //  checkItem.enable();
        }
    },
    onPartnerType : function(partnerType){
    	var st_combo = this.find('itemId','servicepartner.form.type')[0];
    	st_combo.clearValue();
    	st_combo.store.reload({
    		params: {
    		TagIDSearch : partnerType
    		}
    	});
    	st_combo.store.sort(CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name);
    },
    onSpecificPartnerType : function(partnerType){
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
    },
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
//            if(this.form.items.get("SelfSuspended").checked){
//                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SelfSuspended;
//            }
/*            if(this.form.items.get("Suspended").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.Suspended;
            }
            if(this.form.items.get("SecurityLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SecurityLocked;
            }
            if(this.form.items.get("AbsoluteLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.AbsoluteLocked;
            }*/

            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSAgent.Entries.Restrictions._name, resValue);
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
        this.getForm().loadRecord(record);

        var partnerId = this.record.get(CmFinoFIX.message.JSAgent.Entries.ID._name);

        var parentID = this.find('itemId', 'servicepartner.form.parentId')[0];
        if(parentID) {
            parentID.setRawValue(record.data["ParentName"]);
        }
/*        var pt_combo = this.find('itemId','servicepartner.form.type')[0];
        pt_combo.setRawValue(this.record.get(CmFinoFIX.message.JSAgent.Entries.BusinessPartnerTypeText._name));
        
        var resValue = record.get(CmFinoFIX.message.JSAgent.Entries.Restrictions._name);
//        this.form.items.get("SelfSuspended").setValue( (resValue & CmFinoFIX.SubscriberRestrictions.SelfSuspended) > 0);
        this.form.items.get("Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
        this.form.items.get("SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
        this.form.items.get("AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
        */
        this.getForm().clearInvalid();
    },
    resetAll : function() {
    	for ( var j = 0; j < this.form.items.length; j++) {
			this.form.items.get(j).setValue(null);
		}
    	//this.items.get("tabpanelmerchant").setActiveTab(0);
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
    onTradeName : function(field){
        var value = this.record.get(CmFinoFIX.message.JSAgent.Entries.TradeName._name);
        if(!value||(field.getValue().toUpperCase() != value.toUpperCase())){
            var msg = new CmFinoFIX.message.JSTradeNameCheck();
            msg.m_pTradeName = field.getValue();
            msg.m_pCheckIfExists = true;
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    onPartnerCode : function(field){
        var value = this.record.get(CmFinoFIX.message.JSAgent.Entries.PartnerCode._name);
        if(!value||(field.getValue().toUpperCase() != value.toUpperCase())){
            var msg = new CmFinoFIX.message.JSDuplicatePartnerCodeCheck();
            msg.m_pPartnerCode = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    },
    onMDN : function(field){ 
    	if(this.getForm().isValid()){
    	this.subscribercombo.baseParams[CmFinoFIX.message.JSSubscriberMDN.ExactMDNSearch._name] =field.getValue(); 
    	this.subscribercombo.load();
    	}
    },
       
//    onTypeChange : function(field){
//    	if(field.getValue()!=""){
//    		if(CmFinoFIX.BusinessPartnerType.DirectAgent<=field.getValue()){
//    			this.find('itemId','servicepartner.form.mobileno')[0].allowBlank = false;
//    			}else{
//    				this.find('itemId','servicepartner.form.mobileno')[0].allowBlank = true;
//    				}
//    		}
//    	},
    checkServicePartnerType : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSCheckServicePartner();
            msg.m_pPartnerTypeSearch = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    setCombo : function(combo){
    	subscribercombo = combo;
    },
    onSuspendClick: function(){
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
    },
    onSelfSuspendClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be suspended"));
            this.form.items.get("SelfSuspended").setValue(false);
    	} 
    },
    onSecurityLockClick: function(){
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
    },
    disableItems : function(){
      	this.find('itemId','servicepartner.form.username')[0].disable();
      	this.find('itemId','servicepartner.form.ktpid')[0].disable();
      	this.find('itemId','servicepartner.form.mdn')[0].disable();
      	this.find('itemId','servicepartner.form.AccountnumberofBankSinarmas')[0].disable();
      	this.find('itemId','servicepartner.form.BranchofBankSinarmas')[0].disable();
      	
      	this.find('itemId','servicepartner.form.AgentCode')[0].disable();
      	this.find('itemId','servicepartner.form.AgentType')[0].disable();
      	this.find('itemId','servicepartner.form.TypeofBusinessAgent')[0].disable();
      	this.find('itemId','servicepartner.form.AgreementNumber')[0].disable();
      	this.find('itemId','servicepartner.form.AgreementDate')[0].disable();
      	this.find('itemId','servicepartner.form.Implementationdate')[0].disable();
      	this.find('itemId','servicepartner.form.PlaceofBirth')[0].disable();
      	this.find('itemId','servicepartner.form.DateofBirth')[0].disable();
      	this.find('itemId','servicepartner.form.UserBankBranch')[0].disable();
      	
      	this.find('itemId','servicepartner.form.AlamatInAccordanceIdentity')[0].disable();
      	this.find('itemId','servicepartner.form.RTRWAl')[0].disable();
      	this.find('itemId','servicepartner.form.VillageAl')[0].disable();
      	this.find('itemId','servicepartner.form.DistrictAl')[0].disable();
      	this.find('itemId','servicepartner.form.CityAl')[0].disable();
      	this.find('itemId','servicepartner.form.ProvincialAl')[0].disable();
      	this.find('itemId','servicepartner.form.PotalCodeAl')[0].disable();
      	this.find('itemId','servicepartner.form.email')[0].disable();
    },
    simpan : function(formWindow){
       	if(this.getForm().isValid()){
       		
               var msg= new CmFinoFIX.message.JSAgent();
               var values = this.form.getValues();
               
               var AgentCode = values[CmFinoFIX.message.JSAgent.AgentCode._name];
               var AgentType = values[CmFinoFIX.message.JSAgent.AgentType._name];
               var ClassificationAgent = values[CmFinoFIX.message.JSAgent.ClassificationAgent._name];
               var TypeofBusinessAgent = values[CmFinoFIX.message.JSAgent.TypeofBusinessAgent._name];
               var ElectonicDevieused = values[CmFinoFIX.message.JSAgent.ElectonicDevieused._name];
               var AgreementNumber = values[CmFinoFIX.message.JSAgent.AgreementNumber._name];
               var AgreementDate = values[CmFinoFIX.message.JSAgent.AgreementDate._name];
               var Implementationdate = values[CmFinoFIX.message.JSAgent.Implementationdate._name];
               var PlaceofBirth = values[CmFinoFIX.message.JSAgent.PlaceofBirth._name];
               var DateofBirth = values[CmFinoFIX.message.JSAgent.DateofBirth._name];
               var AlamatInAccordanceIdentity = values[CmFinoFIX.message.JSAgent.AlamatInAccordanceIdentity._name];
               var RTRWAl = values[CmFinoFIX.message.JSAgent.RTRWAl._name];
               var VillageAl = values[CmFinoFIX.message.JSAgent.VillageAl._name];
               var DistrictAl = values[CmFinoFIX.message.JSAgent.DistrictAl._name];
               var CityAl = values[CmFinoFIX.message.JSAgent.CityAl._name];
               var ProvincialAl = values[CmFinoFIX.message.JSAgent.ProvincialAl._name];
               var PotalCodeAl = values[CmFinoFIX.message.JSAgent.PotalCodeAl._name];
               var EMail = values[CmFinoFIX.message.JSAgent.EMail._name];
               var AgentCompanyName = values[CmFinoFIX.message.JSAgent.AgentCompanyName._name];
               var CompanyAddress = values[CmFinoFIX.message.JSAgent.CompanyAddress._name];
               var RTRWCom = values[CmFinoFIX.message.JSAgent.RTRWCom._name];
               var VillageCom = values[CmFinoFIX.message.JSAgent.VillageCom._name];
               var DistrictCom = values[CmFinoFIX.message.JSAgent.DistrictCom._name];
               var CityCom = values[CmFinoFIX.message.JSAgent.CityCom._name];
               var ProvincialCom = values[CmFinoFIX.message.JSAgent.ProvincialCom._name];
               var PotalCodeCom = values[CmFinoFIX.message.JSAgent.PotalCodeCom._name];
               var LatitudeLongitude = values[CmFinoFIX.message.JSAgent.LatitudeLongitude._name];
               var PhoneNumber = values[CmFinoFIX.message.JSAgent.PhoneNumber._name];
               var CompanyEmailId = values[CmFinoFIX.message.JSAgent.CompanyEmailId._name];
               var UserBankBranch = values[CmFinoFIX.message.JSAgent.UserBankBranch._name];
               var AgentDescription = values[CmFinoFIX.message.JSAgent.AgentDescription._name];
               
               var Username = values[CmFinoFIX.message.JSAgent.Username._name];
               var KTPID = values[CmFinoFIX.message.JSAgent.KTPID._name];
               var MDN = values[CmFinoFIX.message.JSAgent.MDN._name];
               var AccountnumberofBankSinarmas = values[CmFinoFIX.message.JSAgent.AccountnumberofBankSinarmas._name];
               var BranchofBankSinarmas = values[CmFinoFIX.message.JSAgent.BranchofBankSinarmas._name];
               var BranchCode = values[CmFinoFIX.message.JSAgent.BranchCode._name];
               
               
               msg.m_paction = "create";
               msg.m_pAgentCode = AgentCode;
               msg.m_pPartnerCode = AgentCode;
               msg.m_pAgentType = AgentType;
               msg.m_pBusinessPartnerType = AgentType;
               msg.m_pClassificationAgent = ClassificationAgent;
               msg.m_pTypeofBusinessAgent = TypeofBusinessAgent;
               msg.m_pElectonicDevieused = ElectonicDevieused;
               msg.m_pAgreementNumber = AgreementNumber;
               msg.m_pAgreementDate = 	AgreementDate;
               msg.m_pImplementationdate = Implementationdate;
               msg.m_pPlaceofBirth = PlaceofBirth;
               msg.m_pDateofBirth = DateofBirth;
               msg.m_pAlamatInAccordanceIdentity = AlamatInAccordanceIdentity;
               msg.m_pRTRWAl = RTRWAl;
               msg.m_pVillageAl = VillageAl;
               msg.m_pDistrictAl = DistrictAl;
               msg.m_pCityAl = CityAl;
               msg.m_pProvincialAl = ProvincialAl;
               msg.m_pPotalCodeAl = PotalCodeAl;
               msg.m_pEMail = EMail;
               msg.m_pAuthorizedEmail = EMail;
               msg.m_pAgentCompanyName = AgentCompanyName;
               msg.m_pCompanyAddress = CompanyAddress;
               msg.m_pRTRWCom = RTRWCom;
               msg.m_pVillageCom = VillageCom;
               msg.m_pDistrictCom = DistrictCom;
               msg.m_pCityCom = CityCom;
               msg.m_pProvincialCom = ProvincialCom;
               msg.m_pPotalCodeCom = PotalCodeCom;
               msg.m_pLatitudeLongitude = LatitudeLongitude;
               msg.m_pPhoneNumber = PhoneNumber;
               msg.m_pCompanyEmailId = CompanyEmailId;
               msg.m_pUserBankBranch = UserBankBranch;
               msg.m_pAgentDescription = AgentDescription;
               
               msg.m_pUsername = Username;
               msg.m_pKTPID = KTPID;
               msg.m_pMDN = MDN;
               msg.m_pAccountnumberofBankSinarmas = AccountnumberofBankSinarmas;
               msg.m_pBranchofBankSinarmas = BranchofBankSinarmas;
               msg.m_pBranchCode = BranchCode;

               
               var params = mFino.util.showResponse.getDisplayParam();
               params.formWindow = formWindow;
               mFino.util.fix.send(msg, params);
               
               Ext.apply(params, {
            	 success :  function(response){
          		 if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                     Ext.Msg.show({
                         title: 'Info',
                         minProgressWidth:600,
                         msg: response.m_pErrorDescription,
                         buttons: Ext.MessageBox.OK,
                         multiline: false
                     });
          		 }
    		        if(response.m_psuccess == true){
                        Ext.Msg.show({
                            title: 'Info',
                            minProgressWidth:600,
                            //msg: response.m_pErrorDescription,
                            msg: "Record saved successfully",
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
    		     }
          	 },
               failure : function(response){
                   Ext.Msg.show({
                       title: 'Error',
                       minProgressWidth:250,
                       msg: "Your transaction is having a problem. Please check your recent transaction on pending transaction list or contact Customer Care :881",
                       buttons: Ext.MessageBox.OK,
                       multiline: false
                   });
               }});
               return params;
            }
      },
      setDetails : function(Username,KTPID, MDN, AccountnumberofBankSinarmas, BranchofBankSinarmas, BranchCode){
	        this.getForm().reset();
	        this.form.items.get("servicepartner.form.username").setValue(Username);
	      	this.form.items.get("servicepartner.form.ktpid").setValue(KTPID);
	      	this.form.items.get("servicepartner.form.mdn").setValue(MDN);
	      	this.form.items.get("servicepartner.form.AccountnumberofBankSinarmas").setValue(AccountnumberofBankSinarmas);
	      	this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(BranchofBankSinarmas);
	      	this.form.items.get("servicepartner.form.BranchCode").setValue(BranchCode);
	      	this.find('itemId','servicepartner.form.username')[0].disable();
	      	this.find('itemId','servicepartner.form.ktpid')[0].disable();
	      	this.find('itemId','servicepartner.form.mdn')[0].disable();
	      	this.find('itemId','servicepartner.form.AccountnumberofBankSinarmas')[0].disable();
	      	this.find('itemId','servicepartner.form.BranchofBankSinarmas')[0].disable();
	      	this.find('itemId','servicepartner.form.BranchCode')[0].disable();
      },
    onAbsoluteLockClick: function(){
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
    }
});

/*
 * Partner / Agent Restrictions
 **/
var partnerRestrictions = {
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
                    this.findParentByType("ServicePartnerFormspView").onSecurityLockClick();
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
                    this.findParentByType("ServicePartnerFormspView").onAbsoluteLockClick();
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
//                    this.findParentByType("ServicePartnerFormspView").onSelfSuspendClick();
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
                    this.findParentByType("ServicePartnerFormspView").onSuspendClick();
                }
            }
        }]
    }]
};








Ext.reg("ServicePartnerFormspView", mFino.widget.ServicePartnerFormspView);
