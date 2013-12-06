/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerForm = function (config) {
	 var localConfig = Ext.apply({}, config);
	  	 localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.ServicePartnerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ServicePartnerForm, Ext.form.FormPanel, {
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
                        fieldLabel: mobile,
                        itemId : 'servicepartner.form.mobileno',
                        name: CmFinoFIX.message.JSPartner.Entries.MDN._name,
                       // disable: true,
                        allowBlank: false,
                        vtype: 'smarttelcophoneAddMore',
                        listeners: {
                            change: function(field) {
                            	this.findParentByType('ServicePartnerForm').onMDN(field);
                            	/*
                            	field.isValid(true);
                            	var valmdn=/^[2]{1}[3]{1}[4]{1}[0-9]{10}$/;
                            	var valmdn1=/^[2]{1}[3]{1}[4]{1}[0-9]{7}$/;
                            	var mdn = field.getValue();
                            	if(mdn.length==13)
                        		{
                            		if(!valmdn.test(mdn))
                            		{
                             			//alert("MDN should start from 234");
                            			field.markInvalid("MDN start with 234");
                            		}else{
                            			this.findParentByType('ServicePartnerForm').onMDN(field);
                            		}
                        		}else if(mdn.length>10){
                        			field.markInvalid("MDN starting with 234 should be 13 digits or 10 digits");
                        		}
                        		else{
                        			if(valmdn1.test(mdn))
                            		{
                            			field.markInvalid("MDN should be 13 digits");
                            		}else{
                        			this.findParentByType('ServicePartnerForm').onMDN(field);
                            		}
                        		}
                        */}
                        },
                        emptyText: _(''),
                        blankText : _('MDN is required'),
                         anchor : '98%'
                       
                    },
                    {
                	 xtype : "combo",
                     anchor : '98%',
                     fieldLabel :_("Type"),
                     allowBlank: false,
                     itemId : 'servicepartner.form.type',
                     triggerAction: "all",
                     forceSelection : true,
                     lastQuery: '',
                     addEmpty : false,
                     editable: false,
                     store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSEnumTextSimple),
                     displayField: CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name,
                     valueField : CmFinoFIX.message.JSEnumTextSimple.Entries.EnumCode._name,
                     name: CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name,
                	listeners : {
                		blur :  function(field){
							this.findParentByType('ServicePartnerForm').checkServicePartnerType(field);
					    }
					}
                },
                {
                    xtype: 'textfield',
                    fieldLabel: _('Trade Name'),
                    anchor : '98%',
                    allowBlank: false,
                    maxLength:255,
                    itemId : 'servicepartner.form.tradename',
                    name: CmFinoFIX.message.JSPartner.Entries.TradeName._name,
                    listeners: {
	                    change: function(field) {
	                        this.findParentByType('ServicePartnerForm').onTradeName(field);
	                    }
	                }
                },
                /*{
                    xtype:'textfield',
                    fieldLabel: _('First Name'),
                    allowBlank: false,
                    itemId : 'servicepartner.form.firstname',
                    maxLength : 16,
                    anchor : '98%',
                    name: CmFinoFIX.message.JSPartner.Entries.FirstName._name,
                    emptyText: _('David'),
                    blankText : _('First name is required')
                },
                {
                    xtype : "textfield",
                    anchor : '98%',
                    fieldLabel :_("Last Name"),
                    itemId  : 'servicepartner.form.lastname',
                    maxLength : 255,
                    async   : false,
                    name    : CmFinoFIX.message.JSPartner.Entries.LastName._name,
                    allowBlank: false,
                    emptyText: _('Andrew'),
                    blankText : _('Last name is required')
                },*/
                
				
				{
	                xtype : 'textfield',
	                fieldLabel: _("User Name"),
	                allowBlank: false,
	                itemId : 'servicepartner.form.username',
	                name: CmFinoFIX.message.JSPartner.Entries.Username._name,
	                anchor : '98%',
	                maxLength : 100,
	                listeners: {
	                    change: function(field) {
	                        this.findParentByType('ServicePartnerForm').onName(field);
	                    }
	                }
	            },
	            {
                    xtype : 'textfield',
                    anchor : '98%',
                    fieldLabel :_("Code"),
                    itemId  : 'servicepartner.form.partnercode',
                    name    : CmFinoFIX.message.JSPartner.Entries.PartnerCode._name,
                    allowBlank: false,
                    blankText : _('code is required'),
	                listeners: {
	                    change: function(field) {
	                        this.findParentByType('ServicePartnerForm').onPartnerCode(field);
	                    }
	                }
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
            	{
					xtype : "enumdropdown",
					anchor : '98%',
					allowBlank: false,
					blankText : _('Status is required'),
					itemId : 'servicepartner.form.status',
					fieldLabel :_('Status'),
					emptyText : _('<select one..>'),
					enumId : CmFinoFIX.TagID.MDNStatus,
					name : CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name,
					value : CmFinoFIX.MDNStatus.Initialized,
					listeners : {
					    select :  function(field){
						var status= field.getValue();
						this.findParentByType('ServicePartnerForm').onStatusDropdown(status);
					    }
					}
				},
				{
            		xtype : "enumdropdown",
            		 anchor : '98%',
            		fieldLabel :_('Language'),
            		emptyText : _('<select one..>'),
            		itemId : 'servicepartner.form.language',
            		allowBlank: false,
            		blankText : _('Language is required'),
            		enumId : CmFinoFIX.TagID.Language,
            		name : CmFinoFIX.message.JSPartner.Entries.Language._name
            		//value : CmFinoFIX.Language.Bahasa
            	},
            	{
             	   xtype : "enumdropdown",
             	   anchor : '98%',
             	   fieldLabel :_('Currency'),
             	   emptyText : _('<select one..>'),
             	   itemId : 'sub.form.currency',
             	   allowBlank: false,
             	   blankText : _('Currency is required'),
             	   enumId : CmFinoFIX.TagID.Currency,
             	   name : CmFinoFIX.message.JSPartner.Entries.Currency._name
             	   //value : CmFinoFIX.Currency.IDR
             	   },
             	   {
             		   xtype : "enumdropdown",
             		   anchor : '98%',
             		   fieldLabel :_('Time zone'),
             		   allowBlank: false,
             		   emptyText : _('<select one..>'),
             		   itemId : 'servicepartner.form.time',
             		   blankText : _('Time zone is required'),
             		   enumId: CmFinoFIX.TagID.Timezone,
             		   name : CmFinoFIX.message.JSPartner.Entries.Timezone._name
             	   },
				{
					xtype : "remotedropdown",
					anchor : '98%',
					allowBlank: false,
					addEmpty: false,
					itemId : 'ServicePartner.form.group',
					fieldLabel :"Group",
					pageSize : 5,
					emptyText : _('<select one..>'),
					RPCObject : CmFinoFIX.message.JSGroup,
					displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
					valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
					name: CmFinoFIX.message.JSPartner.Entries.GroupID._name,
					params: {SystemGroupSearch : "true"}
				}
                ]
            },
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : [

                ]
            }
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
                title: _('Contact Details'),
                layout:'column',
                frame:true,
                autoHeight: true,
                width:840,
                items:[
                {
                    columnWidth:0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:[{
                        xtype:'fieldset',
                        title: _('Address'),
                        autoHeight:true,
                        defaultType: 'textfield',
                        width:420,
                        items :[
                        {
                            fieldLabel: _('Line1'),
                            anchor : '90%',
                            itemId :'line1',
                            allowBlank : false,
                            maxLength : 255,
                            name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressLine1._name
                        },
                        {
                            fieldLabel: _('Line2'),
                            anchor : '90%',
                            itemId :'line2',
                            maxLength : 255,
                            name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressLine2._name
                        },
                        {
                            fieldLabel: _('City'),
                            anchor : '90%',
                            itemId :'city',
                            allowBlank : false,
                            maxLength : 255,
                            name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressCity._name
                        },
                        {
                            fieldLabel: _('State'),
                            anchor : '90%',
                            itemId :'state',
                            allowBlank : false,
                            maxLength : 255,
                            name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressState._name
                        },
                        {
                            fieldLabel: _('Country'),
                            anchor : '90%',
                            itemId :'country',
                            allowBlank : false,
                            maxLength : 255,
                            name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressCountry._name
                        },
                        {
                            fieldLabel: _('Postal Code'),
                            allowBlank: false,
                            allowDecimals:false,
                            itemId :'postalCode',
                            blankText : _('Postal Code is required'),
                            anchor : '90%',
                            maxLength : 16,
                            name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressZipcode._name
                        }]
                    }]
                }]
            },
            {
                title : _('Outlet Details'),
                layout : 'column',
                frame:true,
                autoHeight: true,
                width:840,
                items :
                [
                {
                    columnWidth: 0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:
                    [
                    {
                        xtype : "enumdropdown",
                        anchor : '90%',
                        fieldLabel :_('Outlet Classification'),
                        itemId : 'servicepartner.form.outlet',
                        enumId : CmFinoFIX.TagID.Classification,
                        name : CmFinoFIX.message.JSPartner.Entries.Classification._name
                    },
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Contact Number'),
                        allowBlank: false,
                        itemId : 'servicepartner.form.contactNumber',
                        blankText : _('Contact Number is required'),
                        anchor : '90%',
                        maxLength : 16,
                        name: CmFinoFIX.message.JSPartner.Entries.FranchisePhoneNumber._name
                    },
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Fax Number'),
                        anchor : '90%',
                        maxLength : 16,
                        itemId : 'servicepartner.form.faxNumber',
                        name: CmFinoFIX.message.JSPartner.Entries.FaxNumber._name
                    },
                    {
                        xtype : "enumdropdown",
                        anchor : '90%',
                        fieldLabel :_('Type of Organization'),
                        allowBlank: false,
                        emptyText : _('<select one..>'),
                        itemId : 'servicepartner.form.org',
                        blankText : _('Type Of Organization is required'),
                        maxLength:255,
                        enumId : CmFinoFIX.TagID.TypeOfOrganization, 
                        name : CmFinoFIX.message.JSPartner.Entries.TypeOfOrganization._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel : _('Line Of Businesses / Industries'), 
                        allowBlank: false,
                        blankText : _('Line Of Business is required'),
                        anchor : '90%',
                        itemId : 'servicepartner.form.business',
                        maxLength:255,
                        name : CmFinoFIX.message.JSPartner.Entries.IndustryClassification._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel : _('WebSite URL'),
                        anchor : '90%',
                        itemId : 'servicepartner.form.url',
                        maxLength:255,
                        name: CmFinoFIX.message.JSPartner.Entries.WebSite._name
                    },
                    {
                        xtype : "numberfield",
                        fieldLabel: _('Number of Outlet locations'),
                        anchor : '90%',
                        allowDecimals:false,
                        itemId : 'servicepartner.form.locations',
                        maxValue:2147483647,
                        minValue:0,
                        name: CmFinoFIX.message.JSPartner.Entries.NumberOfOutlets._name
                    }
                    ]
                },
                {
                    columnWidth: 0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:
                    [
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Year established'),
                        allowBlank: false,
                        blankText : _('Year established is required'),
                        itemId : 'servicepartner.form.year',
                        anchor : '88%',
                        vtype:'yearCheck',
                        minLength : 4,
                        maxLength : 4,
                        name : CmFinoFIX.message.JSPartner.Entries.YearEstablished._name
                    },
                    {
                        xtype:'fieldset',
                        title: _('Address'),
                        autoHeight:true,
                        defaults: {
                            width: 260
                        },
                        defaultType: 'textfield',
                        width:420,
                        columns: 2,
                        items :[
                        {
                            fieldLabel: _('Line1'),
                            anchor : '90%',
                            maxLength:255,
                            allowBlank : false,
                            itemId : 'servicepartner.form.line1',
                            name : CmFinoFIX.message.JSPartner.Entries.OutletAddressLine1._name
                        },
                        {
                            fieldLabel: _('Line2'),
                            anchor : '90%',
                            itemId : 'servicepartner.form.line2',
                            maxLength:255,
                            name : CmFinoFIX.message.JSPartner.Entries.OutletAddressLine2._name
                        },
                        {
                            fieldLabel: _('City'),
                            anchor : '90%',
                            maxLength:255,
                            allowBlank : false,
                            itemId : 'servicepartner.form.city',
                            name : CmFinoFIX.message.JSPartner.Entries.OutletAddressCity._name
                        },
                        {
                            fieldLabel: _('State'),
                            anchor : '90%',
                            maxLength:255,
                            allowBlank : false,
                            itemId : 'servicepartner.form.state',
                            name : CmFinoFIX.message.JSPartner.Entries.OutletAddressState._name
                        },
                        {
                            fieldLabel: _('Country'),
                            anchor : '90%',
                            maxLength:255,
                            allowBlank : false,
                            itemId : 'servicepartner.form.country',
                            name : CmFinoFIX.message.JSPartner.Entries.OutletAddressCountry._name
                        },
                        {
                            fieldLabel: _('Postal Code'),
                            allowBlank: false,
                            blankText : _('Postal Code is required'),
                            anchor : '90%',
                            maxLength : 16,
                            itemId : 'servicepartner.form.postalCode',
                            name : CmFinoFIX.message.JSPartner.Entries.OutletAddressZipcode._name
                        }
                        ]
                    }
                    ]
                }]
            },
            {
                title: _('Authorization'),
                layout:'column',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    columnWidth:0.5,
                    layout: 'form',
                    labelWidth : 150,
                    items :[
                    {
                        xtype: 'textfield',
                        fieldLabel: _('Authorized Representative'),
                        anchor : '85%',
                        maxLength:255,
                        itemId : 'servicepartner.form.authRepName',
                        name: CmFinoFIX.message.JSPartner.Entries.AuthorizedRepresentative._name
                    },                            
                    {
                        xtype: 'textfield',
                        fieldLabel: _('Representative Name'),
                        allowBlank: false,
                        blankText : _('Representative Name is required'),
                        anchor : '85%',
                        maxLength:255,
                        itemId : 'servicepartner.form.repName',
                        name: CmFinoFIX.message.JSPartner.Entries.RepresentativeName._name
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: _('Position'),
                        anchor : '85%',
                        itemId : 'servicepartner.form.position',
                        maxLength:255,
                        name: CmFinoFIX.message.JSPartner.Entries.Designation._name
                    },
/*                    {
                        xtype:'numberfield',
                        fieldLabel: _('Contact Number'),
                        allowBlank: false,
                        itemId : 'servicepartner.form.contact',
                        blankText : _('Contact Number is required'),
                        anchor : '85%',
                        maxLength : 16,
                        name: CmFinoFIX.message.JSMerchant.Entries.AuthenticationPhoneNumber._name
                    },*/
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Fax Number'),
                        anchor : '85%',
                        maxLength : 16,
                        itemId : 'servicepartner.form.fax',
                        name: CmFinoFIX.message.JSPartner.Entries.AuthorizedFaxNumber._name
                    },
                    {
                        xtype:'textfield',
                        fieldLabel: _('Email Address'),
                        anchor : '85%',
                        allowBlank : false,
                        vtype: 'email',
                        maxLength : 255,
                        itemId : 'servicepartner.form.email',
                        name: CmFinoFIX.message.JSPartner.Entries.AuthorizedEmail._name
                    }
                    ]
                }
                ]
            },
            {
                title: _('Restriction'),
                layout:'column',
                frame:true,
                autoHeight: true,
                items:[partnerRestrictions]
            }

            ]
        }
        ];   
        mFino.widget.ServicePartnerForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.subscribercombo.on("load", this.onLoad.createDelegate(this));
    },
    
    onLoad: function(){
     var record = this.subscribercombo.getAt(0);
   	 var convertAgentStore = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner);
       var mrecord = this.record;
       if(record!=null&&record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberType._name)===CmFinoFIX.SubscriberType.Partner){
    	   Ext.MessageBox.alert(_("Alert"), _("MDN already registered as Partner or Agent"));   
         }else{
        	 if(record!=null){
             Ext.apply(mrecord.data, record.data);
        	 }else{
        		 mrecord.set(CmFinoFIX.message.JSPartner.Entries.MDN._name,this.find("itemId", "servicepartner.form.mobileno")[0].getValue());
        		 mrecord.set(CmFinoFIX.message.JSPartner.Entries.Language._name,CmFinoFIX.Language.English);
        		 mrecord.set(CmFinoFIX.message.JSPartner.Entries.Timezone._name,SYSTEM_DEFAULT_TIMEZONE);
        		 mrecord.set(CmFinoFIX.message.JSPartner.Entries.Currency._name,SYSTEM_DEFAULT_CURRENCY);
                 mrecord.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name,1);
                 mrecord.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupName._name,'ANY');
        	 }
        	 mrecord.set(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name, CmFinoFIX.MDNStatus.Initialized);
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
            	this.record.set(CmFinoFIX.message.JSPartner.Entries.IsForceCloseRequested._name, true);
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
            this.record.set(CmFinoFIX.message.JSPartner.Entries.Restrictions._name, resValue);
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

        var partnerId = this.record.get(CmFinoFIX.message.JSPartner.Entries.ID._name);

        var parentID = this.find('itemId', 'servicepartner.form.parentId')[0];
        if(parentID) {
            parentID.setRawValue(record.data["ParentName"]);
        }
        var pt_combo = this.find('itemId','servicepartner.form.type')[0];
        pt_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartner.Entries.BusinessPartnerTypeText._name));
        
        var resValue = record.get(CmFinoFIX.message.JSPartner.Entries.Restrictions._name);
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
        var value = this.record.get(CmFinoFIX.message.JSPartner.Entries.TradeName._name);
        if(!value||(field.getValue().toUpperCase() != value.toUpperCase())){
            var msg = new CmFinoFIX.message.JSTradeNameCheck();
            msg.m_pTradeName = field.getValue();
            msg.m_pCheckIfExists = true;
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    onPartnerCode : function(field){
        var value = this.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerCode._name);
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
        var resValue = this.record.get(CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name);
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
    	var partnerType = this.record.data[CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be suspended"));
            this.form.items.get("Suspended").setValue(false);
    	} else {
        	var currentStatus = this.record.data[CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name];
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
    	var partnerType = this.record.data[CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be suspended"));
            this.form.items.get("SelfSuspended").setValue(false);
    	} 
    },
    onSecurityLockClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be Locked"));
            this.form.items.get("SecurityLocked").setValue(false);
    	} else {
        	var currentStatus = this.record.data[CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name];
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
    onAbsoluteLockClick: function(){
    	var partnerType = this.record.data[CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name];
    	if (partnerType === CmFinoFIX.BusinessPartnerType.ServicePartner) {
    		Ext.MessageBox.alert(_("Alert"), _("Service Partner can't be Locked"));
            this.form.items.get("AbsoluteLocked").setValue(false);
    	} else {
        	var currentStatus = this.record.data[CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name];
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
                    this.findParentByType("ServicePartnerForm").onSecurityLockClick();
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
                    this.findParentByType("ServicePartnerForm").onAbsoluteLockClick();
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
//                    this.findParentByType("ServicePartnerForm").onSelfSuspendClick();
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
                    this.findParentByType("ServicePartnerForm").onSuspendClick();
                }
            }
        }]
    }]
};








Ext.reg("ServicePartnerForm", mFino.widget.ServicePartnerForm);
