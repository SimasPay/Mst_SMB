/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerFormspAdd = function (config) {
	 var localConfig = Ext.apply({}, config);
	  	 localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.ServicePartnerFormspAdd.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ServicePartnerFormspAdd, Ext.form.FormPanel, {
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
							    //fieldLabel :_("Agent Code"),
							    fieldLabel :_("Nomor Identifikasi Agen"),
							    anchor : '100%',
							    allowBlank: true,
							    maxLength : 255,
							    blankText : _('Agent Code is required'),
							    itemId  : 'servicepartner.form.AgentCode',
							    name    : CmFinoFIX.message.JSAgent.AgentCode._name
							},
/*							{
							   xtype : "enumdropdown",
							   //fieldLabel :_('Agent Type'),
							   fieldLabel :_('Jenis Agen'),
							   anchor : '100%',
							   allowBlank: false,
							   emptyText : _('<select one..>'),
							   blankText : _('Agent Type is required'),
							   enumId: CmFinoFIX.TagID.AgentType,
							   itemId : 'servicepartner.form.AgentType',
							   name : CmFinoFIX.message.JSAgent.AgentType._name
							},*/
							{
							   xtype : "enumdropdown",
							   //fieldLabel :_('Agent Type'),
							   fieldLabel :_('Jenis Agen'),
							   anchor : '100%',
							   allowBlank: false,
							   emptyText : _('<select one..>'),
							   blankText : _('Agent Type is required'),
							   enumId: CmFinoFIX.TagID.BusinessPartnerTypeAgent,
							   itemId : 'servicepartner.form.BusinessPartnerType',
							   name : CmFinoFIX.message.JSAgent.BusinessPartnerType._name
							},
                    	   {
                    		   xtype : "enumdropdown",
                    		   //fieldLabel :_('Classification Agent'),
                    		   fieldLabel :_('Klasifikasi Agen'),
                    		   anchor : '100%',
                    		   allowBlank: false,
                    		   emptyText : _('<select one..>'),
                    		   blankText : _('Classification Agent is required'),
                    		   enumId: CmFinoFIX.TagID.ClassificationAgent,
                    		   itemId : 'servicepartner.form.ClassificationAgent',
                    		   name : CmFinoFIX.message.JSAgent.ClassificationAgent._name
                    	   },
                     	   {
                     		   xtype : "enumdropdown",
                     		   //fieldLabel :_('Type of Business Agent'),
                     		  fieldLabel :_('Jenis Usaha Agen'),
                     		   anchor : '100%',
                     		   allowBlank: false,
                     		   emptyText : _('<select one..>'),
                     		   blankText : _('Type of Business Agent is required'),
                     		   enumId: CmFinoFIX.TagID.TypeofBusinessAgent,
                     		   itemId : 'servicepartner.form.TypeofBusinessAgent',
                     		   name : CmFinoFIX.message.JSAgent.TypeofBusinessAgent._name
                     	   },
                     	   {
                     		   xtype : "enumdropdown",
                     		   //fieldLabel :_('Electonic Devie used'),
                     		   fieldLabel :_('Yang Digunakan'),
                     		   anchor : '100%',
                     		   allowBlank: false,
                     		   emptyText : _('<select one..>'),
                     		   blankText : _('Electonic Devie used is required'),
                     		   enumId: CmFinoFIX.TagID.ElectonicDevieused,
                     		   itemId : 'servicepartner.form.ElectonicDevieused',
                     		   name : CmFinoFIX.message.JSAgent.ElectonicDevieused._name
                     	   },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("Agreement Number"),
                               fieldLabel :_("Nomor Perjanjan Kerjasama"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('Agreement Number is required'),
                               itemId  : 'servicepartner.form.AgreementNumber',
                               name: CmFinoFIX.message.JSAgent.AgreementNumber._name
                           },
                           {
                               xtype : "datefield",
                               //fieldLabel :_("Agreement Date"),
                               fieldLabel :_("Tanggal Perjanjan Kerjasama"),
                               anchor : '100%',
							   editable: false,
							   allowBlank: false,
                               blankText : _('Agreement Date is required'),
                               itemId  : 'servicepartner.form.AgreementDate',
                               name: CmFinoFIX.message.JSAgent.AgreementDate._name
                           },
                           {
                               xtype : "datefield",
                               //fieldLabel :_("Implementation date"),
                               fieldLabel :_("Tanggal Pelaksanaan"),
                               anchor : '100%',
							   editable: false,		
							   allowBlank: false,
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
                               //fieldLabel :_("Place of Birth"),
                               fieldLabel :_("Tempat"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('Place of birth is required'),
                               itemId  : 'servicepartner.form.PlaceofBirth',
                               name: CmFinoFIX.message.JSAgent.PlaceofBirth._name
                           },
                           {
                               xtype : "datefield",
                               //fieldLabel :_("Date of Birth"),
                               fieldLabel :_("Tanggal Lahir"),
                               anchor : '100%',
							   editable: false,
							   allowBlank: false,
                               blankText : _('Date of birth is required'),
                               itemId  : 'servicepartner.form.DateofBirth',
                               name: CmFinoFIX.message.JSAgent.DateofBirth._name,
                          	 	listeners: {
	                                 change: function(field) 
	                                 {
	                                	 this.findParentByType('ServicePartnerFormspAdd').onDOBSelect(field);
	                                 }
                          	 	}
                           },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("Alamat (InAccordanceIdentity)"),
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
                               //fieldLabel :_("Provincial"),
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
                               //fieldLabel :_("City"),
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
                               //fieldLabel :_("District"),
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
                               //fieldLabel :_("Village"),
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
                               //fieldLabel :_("Potal Code"),
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
                               itemId : 'servicepartner.form.EMail',
                               name: CmFinoFIX.message.JSAgent.EMail._name
                           },
                           {
                               xtype : 'label',                               text :'DATA PERUSAHAAN',

                               name: 'CompanyData',
                               anchor : '100%',
                               style: 'font-weight:bold;'
                           },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("Company Name"),
                               fieldLabel :_("Nama Perusahaan"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('Company Name is required'),
                               itemId  : 'servicepartner.form.AgentCompanyName',
                               name: CmFinoFIX.message.JSAgent.AgentCompanyName._name
                           },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("Company Address"),
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
/*                           {
                               xtype : "textfield",
                               //fieldLabel :_("Provincial"),
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
                               //fieldLabel :_("City"),
                               fieldLabel :_("Kabupaten/Kota"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('City is required'),
                               itemId  : 'servicepartner.form.CityCom',
                               name: CmFinoFIX.message.JSAgent.CityCom._name
                           },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("District"),
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
                               //fieldLabel :_("Village"),
                               fieldLabel :_("Kelurahan/Desa"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Village is required'),
                               itemId  : 'servicepartner.form.VillageCom',
                               name: CmFinoFIX.message.JSAgent.VillageCom._name
                           },*/
/*        	               {
        	                   xtype: 'remotedropdown',
                               //fieldLabel :_("Provincial"),
                               fieldLabel :_("Propinsi"),
        	                   anchor : '100%',
        	                   allowBlank: false,
        	                   addEmpty : false,
        	                   itemId : 'servicepartner.form.ProvincialCom',
        	                   emptyText : '<Select one..>',
        	                   name: CmFinoFIX.message.JSAgent.ProvincialCom._name,
        	                   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSProvince), 
        	                   displayField: CmFinoFIX.message.JSProvince.Entries.DisplayText._name, 
        	                   valueField : CmFinoFIX.message.JSProvince.Entries.ID._name, 
        	                   hiddenName : CmFinoFIX.message.JSAgent.ProvincialCom._name,
        	                   pageSize: 10,
        	                   params: {start:0, limit:10},
        		                listeners: {
        		                    select: function(field) {
        		                        this.findParentByType('ServicePartnerFormspAdd').onProvince(field);
        		                    }
        		                }
        	               },
                           {
        	                   xtype: 'remotedropdown',
                               //fieldLabel :_("City"),
                               fieldLabel :_("Kabupaten/Kota"),
        	                   anchor : '100%',
        	                   allowBlank: false,
        	                   addEmpty : false,
        	                   itemId : 'servicepartner.form.CityCom',
        	                   emptyText : '<Select one..>',
        	                   name: CmFinoFIX.message.JSAgent.CityCom._name,
        	                   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSProvinceRegion), 
        	                   displayField: CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name, 
        	                   valueField : CmFinoFIX.message.JSProvinceRegion.Entries.ID._name, 
        	                   hiddenName : CmFinoFIX.message.JSAgent.CityCom._name,
        	                   pageSize: 10,
        	                   params: {start:0, limit:10},
        		                listeners: {
        		                    select: function(field) {
        		                        this.findParentByType('ServicePartnerFormspAdd').onProvinceRegion(field);
        		                    }
        		                }
        	               },
                           {
        	                   xtype: 'remotedropdown',
                               //fieldLabel :_("District"),
                               fieldLabel :_("Kecamatan"),
        	                   anchor : '100%',
        	                   allowBlank: false,
        	                   addEmpty : false,
        	                   itemId : 'servicepartner.form.DistrictCom',
        	                   emptyText : '<Select one..>',
        	                   name: CmFinoFIX.message.JSAgent.DistrictCom._name,
        	                   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSDistrict), 
        	                   displayField: CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
        	                   valueField : CmFinoFIX.message.JSDistrict.Entries.ID._name, 
        	                   hiddenName : CmFinoFIX.message.JSAgent.DistrictCom._name,
        	                   pageSize: 10,
        	                   params: {start:0, limit:10},
        		                listeners: {
        		                    select: function(field) {
        		                        this.findParentByType('ServicePartnerFormspAdd').onDistrict(field);
        		                    }
        		                }
        	               },
                           {
        	                   xtype: 'remotedropdown',
                               //fieldLabel :_("Village"),
                               fieldLabel :_("Kelurahan/Desa"),
        	                   anchor : '100%',
        	                   allowBlank: false,
        	                   addEmpty : false,
        	                   itemId : 'servicepartner.form.VillageCom',
        	                   emptyText : '<Select one..>',
        	                   name: CmFinoFIX.message.JSAgent.VillageCom._name,
        	                   store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSVillage), 
        	                   displayField: CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
        	                   valueField : CmFinoFIX.message.JSVillage.Entries.ID._name, 
        	                   hiddenName : CmFinoFIX.message.JSAgent.VillageCom._name,
        	                   pageSize: 10,
        	                   params: {start:0, limit:10},
        		                listeners: {
        		                    select: function(field) {
        		                        this.findParentByType('ServicePartnerFormspAdd').onVillage(field);
        		                    }
        		                }
        	               },*/
	                           {
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
	      							 	this.findParentByType('ServicePartnerFormspAdd').onProvince2(field);
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
	      							 	this.findParentByType('ServicePartnerFormspAdd').onProvinceRegion2(field);
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
	      							 	this.findParentByType('ServicePartnerFormspAdd').onDistrict2(field);
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
	      	                },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("Potal Code"),
                               fieldLabel :_("Kode Pos"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('Potal Code is required'),
                               itemId  : 'servicepartner.form.PotalCodeCom',
                               name: CmFinoFIX.message.JSAgent.PotalCodeCom._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Latitude"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('Latitude is required'),
                               itemId  : 'servicepartner.form.Latitude',
                               name: CmFinoFIX.message.JSAgent.Latitude._name
                           },
                           {
                               xtype : "textfield",
                               fieldLabel :_("Longitude"),
                               anchor : '100%',
                               allowBlank: false,
                               maxLength : 255,
                               blankText : _('Longitude is required'),
                               itemId  : 'servicepartner.form.Longitude',
                               name: CmFinoFIX.message.JSAgent.Longitude._name
                           },
                           {
                        	   xtype : 'numberfield',
                               //fieldLabel :_("Phone Number"),
                               fieldLabel :_("Nomor Telephon (Fixed Line)"),
                               allowDecimals:false,
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('Phone Number is required'),
                               itemId  : 'servicepartner.form.PhoneNumber',
                               name: CmFinoFIX.message.JSAgent.PhoneNumber._name
                           },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("Company Email"),
                               fieldLabel :_("Alamat Email"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               vtype: 'email',
                               blankText : _('Company Email is required'),
                               itemId  : 'servicepartner.form.CompanyEmailId',
                               name: CmFinoFIX.message.JSAgent.CompanyEmailId._name
                           },
                           {
                               xtype : "textfield",
                               //fieldLabel :_("User Bank Branch"),
                               fieldLabel :_("Jaringan Kantor Bank"),
                               anchor : '100%',
                               allowBlank: true,
                               maxLength : 255,
                               blankText : _('User Bank Branch is required'),
                               itemId  : 'servicepartner.form.UserBankBranch',
                               name: CmFinoFIX.message.JSAgent.UserBankBranch._name
                           },
                     	   {
                     		   xtype : "enumdropdown",
                     		   fieldLabel :_('Status'),
                     		   anchor : '100%',
                     		   allowBlank: false,
                     		   emptyText : _('<select one..>'),
                     		   blankText : _('BankAccountStatus is required'),
                     		   enumId: CmFinoFIX.TagID.BankAccountStatus,
                     		   itemId : 'servicepartner.form.BankAccountStatus',
                     		   name : CmFinoFIX.message.JSAgent.BankAccountStatus._name
                     	   },
                     	   
              			{
           	                xtype : 'hidden',
           	                fieldLabel: _("Nama Agen (Sesuai KTP)"),
                            anchor : '100%',
                            allowBlank: true,
                            maxLength : 255,
           	                itemId : 'servicepartner.form.Username',
           	                name: CmFinoFIX.message.JSAgent.Username._name
           	            },
       	               {
       	                   xtype : "hidden",
       	                   fieldLabel :_("Nomor KTP"),
                           anchor : '100%',
                           allowBlank: true,
                           maxLength : 255,
       	                   itemId  : 'servicepartner.form.KTPID',
       	                   name: CmFinoFIX.message.JSAgent.KTPID._name
       	               },
       	               {
       	                   xtype : "hidden",
       	                   fieldLabel :_("Nomor Telepon Selular (Handphone)"),
                           anchor : '100%',
                           allowBlank: true,
                           maxLength : 255,
       	                   itemId : 'servicepartner.form.MDN',
       	                   name: CmFinoFIX.message.JSAgent.MDN._name
       	               },
       	               {
       	                   xtype : "hidden",
       	                   fieldLabel :_("Rekening Sinarmas"),
                           anchor : '100%',
                           allowBlank: true,
                           maxLength : 255,
       	                   itemId  : 'servicepartner.form.AccountnumberofBankSinarmas',
       	                   name: CmFinoFIX.message.JSAgent.AccountnumberofBankSinarmas._name
       	               },
       	               {
       	                   xtype : "hidden",
       	                   fieldLabel :_("Bank Sinarmas Branch"),
                           anchor : '100%',
                           allowBlank: true,
                           maxLength : 255,
       	                   itemId  : 'servicepartner.form.BranchofBankSinarmas',
       	                   name: CmFinoFIX.message.JSAgent.BranchofBankSinarmas._name
       	               },
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
        mFino.widget.ServicePartnerFormspAdd.superclass.initComponent.call(this);
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
            if(this.form.items.get("Suspended").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.Suspended;
            }
            if(this.form.items.get("SecurityLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SecurityLocked;
            }
            if(this.form.items.get("AbsoluteLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.AbsoluteLocked;
            }

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
        var pt_combo = this.find('itemId','servicepartner.form.type')[0];
        pt_combo.setRawValue(this.record.get(CmFinoFIX.message.JSAgent.Entries.BusinessPartnerTypeText._name));
        
        var resValue = record.get(CmFinoFIX.message.JSAgent.Entries.Restrictions._name);
//        this.form.items.get("SelfSuspended").setValue( (resValue & CmFinoFIX.SubscriberRestrictions.SelfSuspended) > 0);
        this.form.items.get("Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
        this.form.items.get("SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
        this.form.items.get("AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
        
        this.getForm().clearInvalid();
    },
    resetAll : function() {
    	for ( var j = 0; j < this.form.items.length; j++) {
			this.form.items.get(j).setValue(null);
		}
    	this.items.get("tabpanelmerchant").setActiveTab(0);
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
		 if ((currdate - mydate) < 0){
			alert("Date of Birth should be greater than 18 years");
			this.form.items.get("servicepartner.form.DateofBirth").setValue('');
			}

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
    onProvince2 : function(field){
        var value=field.getValue();
        //alert(value);
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
        //alert(value);
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
        //alert(value);
    	var village_combo = this.find('itemId','servicepartner.form.VillageCom')[0];
    	village_combo.clearValue();
    	village_combo.store.reload({
    		params: {
    			//start : 0, 
    			//limit : 10,
    			IdDistrict : value
    		}
    	});
    },
/*    onProvince : function(field){
        var value=field.getValue();
        alert(value);
        //var value2=field.getRawValue();
        //this.form.items.get("servicepartner.form.PotalCodeCom").setValue(value);
        //this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(value2);
    },
    onProvinceRegion : function(field){
        var value=field.getValue();
        alert(value);
        //var value2=field.getRawValue();
        //this.form.items.get("servicepartner.form.CityCom").setValue(value);
        //this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(value2);
    },
    onDistrict : function(field){
        var value=field.getValue();
        alert(value);
        //var value2=field.getRawValue();
        //this.form.items.get("servicepartner.form.CityCom").setValue(value);
        //this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(value2);
    },
    onVillage : function(field){
        var value=field.getValue();
        alert(value);
        //var value2=field.getRawValue();
        //this.form.items.get("servicepartner.form.CityCom").setValue(value);
        //this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(value2);
    },*/
    simpan : function(formWindow){
       	if(this.getForm().isValid()){
       		
	      	this.find('itemId','servicepartner.form.AgentCode')[0].enable();
	      	this.find('itemId','servicepartner.form.Username')[0].enable();
	      	this.find('itemId','servicepartner.form.KTPID')[0].enable();
	      	this.find('itemId','servicepartner.form.MDN')[0].enable();
	      	this.find('itemId','servicepartner.form.AccountnumberofBankSinarmas')[0].enable();
	      	//this.find('itemId','servicepartner.form.BranchofBankSinarmas')[0].enable();
	      	this.find('itemId','servicepartner.form.BranchCode')[0].enable();
	      	this.find('itemId','servicepartner.form.AlamatInAccordanceIdentity')[0].enable();
	      	this.find('itemId','servicepartner.form.RTAl')[0].enable();
	      	this.find('itemId','servicepartner.form.RWAl')[0].enable();
	      	this.find('itemId','servicepartner.form.VillageAl')[0].enable();
	      	this.find('itemId','servicepartner.form.DistrictAl')[0].enable();
	      	this.find('itemId','servicepartner.form.CityAl')[0].enable();
	      	this.find('itemId','servicepartner.form.ProvincialAl')[0].enable();
	      	this.find('itemId','servicepartner.form.PotalCodeAl')[0].enable();
	      	this.find('itemId','servicepartner.form.UserBankBranch')[0].enable();   
       		
       		var msg= new CmFinoFIX.message.JSAgent();
               var values = this.form.getValues();
               
               var AgentCode = values[CmFinoFIX.message.JSAgent.AgentCode._name];
               //var AgentType = values[CmFinoFIX.message.JSAgent.AgentType._name];
               //var BusinessPartnerTypeAgent = values[CmFinoFIX.message.JSAgent.BusinessPartnerTypeAgent._name];
               var BusinessPartnerType = values[CmFinoFIX.message.JSAgent.BusinessPartnerType._name];
               var ClassificationAgent = values[CmFinoFIX.message.JSAgent.ClassificationAgent._name];
               var TypeofBusinessAgent = values[CmFinoFIX.message.JSAgent.TypeofBusinessAgent._name];
               var ElectonicDevieused = values[CmFinoFIX.message.JSAgent.ElectonicDevieused._name];
               var AgreementNumber = values[CmFinoFIX.message.JSAgent.AgreementNumber._name];
               var AgreementDate = values[CmFinoFIX.message.JSAgent.AgreementDate._name];
               var Implementationdate = values[CmFinoFIX.message.JSAgent.Implementationdate._name];
               var PlaceofBirth = values[CmFinoFIX.message.JSAgent.PlaceofBirth._name];
               var DateofBirth = values[CmFinoFIX.message.JSAgent.DateofBirth._name];
               var AlamatInAccordanceIdentity = values[CmFinoFIX.message.JSAgent.AlamatInAccordanceIdentity._name];
               var RTAl = values[CmFinoFIX.message.JSAgent.RTAl._name];
               var RWAl = values[CmFinoFIX.message.JSAgent.RWAl._name];
               var VillageAl = values[CmFinoFIX.message.JSAgent.VillageAl._name];
               var DistrictAl = values[CmFinoFIX.message.JSAgent.DistrictAl._name];
               var CityAl = values[CmFinoFIX.message.JSAgent.CityAl._name];
               var ProvincialAl = values[CmFinoFIX.message.JSAgent.ProvincialAl._name];
               var PotalCodeAl = values[CmFinoFIX.message.JSAgent.PotalCodeAl._name];
               var EMail = values[CmFinoFIX.message.JSAgent.EMail._name];
               var AgentCompanyName = values[CmFinoFIX.message.JSAgent.AgentCompanyName._name];
               var CompanyAddress = values[CmFinoFIX.message.JSAgent.CompanyAddress._name];
               var RTCom = values[CmFinoFIX.message.JSAgent.RTCom._name];
               var RWCom = values[CmFinoFIX.message.JSAgent.RWCom._name];
               var VillageCom = values[CmFinoFIX.message.JSAgent.VillageCom._name];
               var DistrictCom = values[CmFinoFIX.message.JSAgent.DistrictCom._name];
               var CityCom = values[CmFinoFIX.message.JSAgent.CityCom._name];
               var ProvincialCom = values[CmFinoFIX.message.JSAgent.ProvincialCom._name];
               var PotalCodeCom = values[CmFinoFIX.message.JSAgent.PotalCodeCom._name];
               var Latitude = values[CmFinoFIX.message.JSAgent.Latitude._name];
               var Longitude = values[CmFinoFIX.message.JSAgent.Longitude._name];
               var PhoneNumber = values[CmFinoFIX.message.JSAgent.PhoneNumber._name];
               var CompanyEmailId = values[CmFinoFIX.message.JSAgent.CompanyEmailId._name];
               var UserBankBranch = values[CmFinoFIX.message.JSAgent.UserBankBranch._name];
               var BankAccountStatus = values[CmFinoFIX.message.JSAgent.BankAccountStatus._name];
               
               var Username = values[CmFinoFIX.message.JSAgent.Username._name];
               var KTPID = values[CmFinoFIX.message.JSAgent.KTPID._name];
               var MDN = values[CmFinoFIX.message.JSAgent.MDN._name];
               var AccountnumberofBankSinarmas = values[CmFinoFIX.message.JSAgent.AccountnumberofBankSinarmas._name];
               var BranchofBankSinarmas = values[CmFinoFIX.message.JSAgent.BranchofBankSinarmas._name];
               var BranchCode = values[CmFinoFIX.message.JSAgent.BranchCode._name];
               
               
               msg.m_paction = "create";
               msg.m_pAgentCode = AgentCode;
               msg.m_pPartnerCode = AgentCode;
               //msg.m_pAgentType = AgentType;
               //msg.m_pBusinessPartnerTypeAgent = BusinessPartnerTypeAgent;
               msg.m_pBusinessPartnerType = BusinessPartnerType;
               msg.m_pClassificationAgent = ClassificationAgent;
               msg.m_pTypeofBusinessAgent = TypeofBusinessAgent;
               msg.m_pElectonicDevieused = ElectonicDevieused;
               msg.m_pAgreementNumber = AgreementNumber;
               msg.m_pAgreementDate = 	AgreementDate;
               msg.m_pImplementationdate = Implementationdate;
               msg.m_pPlaceofBirth = PlaceofBirth;
               msg.m_pDateofBirth = DateofBirth;
               msg.m_pAlamatInAccordanceIdentity = AlamatInAccordanceIdentity;
               msg.m_pRTAl = RTAl;
               msg.m_pRWAl = RWAl;
               msg.m_pVillageAl = VillageAl;
               msg.m_pDistrictAl = DistrictAl;
               msg.m_pCityAl = CityAl;
               msg.m_pProvincialAl = ProvincialAl;
               msg.m_pPotalCodeAl = PotalCodeAl;
               msg.m_pEMail = EMail;
               msg.m_pAuthorizedEmail = EMail;
               msg.m_pAgentCompanyName = AgentCompanyName;
               msg.m_pCompanyAddress = CompanyAddress;
               msg.m_pRTCom = RTCom;
               msg.m_pRWCom = RWCom;
               msg.m_pVillageCom = VillageCom;
               msg.m_pDistrictCom = DistrictCom;
               msg.m_pCityCom = CityCom;
               msg.m_pProvincialCom = ProvincialCom;
               msg.m_pPotalCodeCom = PotalCodeCom;
               msg.m_pLatitude = Latitude;
               msg.m_pLongitude = Longitude;
               msg.m_pPhoneNumber = PhoneNumber;
               msg.m_pCompanyEmailId = CompanyEmailId;
               msg.m_pUserBankBranch = UserBankBranch;
               msg.m_pBankAccountStatus = BankAccountStatus;
               
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
                 		if(response.m_psuccess == true){
                            Ext.Msg.show({
                                title: 'Info',
                                minProgressWidth:600,
                                msg: response.m_pErrorDescription,
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                 		 }else{
                             Ext.Msg.show({
                                 title: 'Info',
                                 minProgressWidth:600,
                                 msg: response.m_pErrorDescription,
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
      setDetails : function(Username,KTPID, MDN, AccountnumberofBankSinarmas, BranchofBankSinarmas, BranchCode,
    		  AlamatInAccordanceIdentity, RTAl, RWAl, VillageAl, DistrictAl, CityAl, ProvincialAl, PotalCodeAl, UserBankBranch){
	        this.getForm().reset();
	        this.form.items.get("servicepartner.form.Username").setValue(Username);
	      	this.form.items.get("servicepartner.form.KTPID").setValue(KTPID);
	      	this.form.items.get("servicepartner.form.MDN").setValue(MDN);
	      	this.form.items.get("servicepartner.form.AccountnumberofBankSinarmas").setValue(AccountnumberofBankSinarmas);
	      	this.form.items.get("servicepartner.form.BranchofBankSinarmas").setValue(BranchofBankSinarmas);
	      	this.form.items.get("servicepartner.form.BranchCode").setValue(BranchCode);
	      	this.form.items.get("servicepartner.form.AlamatInAccordanceIdentity").setValue(AlamatInAccordanceIdentity);
	      	this.form.items.get("servicepartner.form.RTAl").setValue(RTAl);
	      	this.form.items.get("servicepartner.form.RWAl").setValue(RWAl);
	      	this.form.items.get("servicepartner.form.VillageAl").setValue(VillageAl);
	      	this.form.items.get("servicepartner.form.DistrictAl").setValue(DistrictAl);
	      	this.form.items.get("servicepartner.form.CityAl").setValue(CityAl);
	      	this.form.items.get("servicepartner.form.ProvincialAl").setValue(ProvincialAl);
	      	this.form.items.get("servicepartner.form.PotalCodeAl").setValue(PotalCodeAl);
	      	this.form.items.get("servicepartner.form.UserBankBranch").setValue(UserBankBranch);
	      	
	      	this.find('itemId','servicepartner.form.AgentCode')[0].disable();
	      	this.find('itemId','servicepartner.form.Username')[0].disable();
	      	this.find('itemId','servicepartner.form.KTPID')[0].disable();
	      	this.find('itemId','servicepartner.form.MDN')[0].disable();
	      	this.find('itemId','servicepartner.form.AccountnumberofBankSinarmas')[0].disable();
	      	this.find('itemId','servicepartner.form.BranchofBankSinarmas')[0].disable();
	      	this.find('itemId','servicepartner.form.BranchCode')[0].disable();
	      	this.find('itemId','servicepartner.form.AlamatInAccordanceIdentity')[0].disable();
	      	this.find('itemId','servicepartner.form.RTAl')[0].disable();
	      	this.find('itemId','servicepartner.form.RWAl')[0].disable();
	      	this.find('itemId','servicepartner.form.VillageAl')[0].disable();
	      	this.find('itemId','servicepartner.form.DistrictAl')[0].disable();
	      	this.find('itemId','servicepartner.form.CityAl')[0].disable();
	      	this.find('itemId','servicepartner.form.ProvincialAl')[0].disable();
	      	this.find('itemId','servicepartner.form.PotalCodeAl')[0].disable();
	      	this.find('itemId','servicepartner.form.UserBankBranch')[0].disable();

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
                    this.findParentByType("ServicePartnerFormspAdd").onSecurityLockClick();
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
                    this.findParentByType("ServicePartnerFormspAdd").onAbsoluteLockClick();
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
//                    this.findParentByType("ServicePartnerFormspAdd").onSelfSuspendClick();
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
                    this.findParentByType("ServicePartnerFormspAdd").onSuspendClick();
                }
            }
        }]
    }]
};








Ext.reg("ServicePartnerFormspAdd", mFino.widget.ServicePartnerFormspAdd);
