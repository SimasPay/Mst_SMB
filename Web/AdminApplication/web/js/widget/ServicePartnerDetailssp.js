/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerDetailssp = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        width:750,
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
             /*{
                xtype : 'displayfield',
                fieldLabel: _('Code'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.PartnerCode._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Trade Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.TradeName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('User Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.Username._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Group'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.GroupName._name
            },*/
            {
	             xtype : "displayfield",
	             anchor : '75%',
	             fieldLabel :_("Mobile Number"),
	             name: CmFinoFIX.message.JSAgent.Entries.MobilePhoneNumber._name,
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Agent Type'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.BusinessPartnerTypeText._name
            },
			{
				xtype : "displayfield",
				anchor : '75%',
				fieldLabel :_("Agent Code"),
				name: CmFinoFIX.message.JSAgent.Entries.AgentCode._name
			},
	           {
	               xtype : "displayfield",
	               anchor : '75%',
	               fieldLabel :_("Name (In Accordance Identity)"),
	               name: CmFinoFIX.message.JSAgent.Entries.NameInAccordanceIdentity._name,
	           },
            {
                xtype : 'displayfield',
                fieldLabel: _('Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.PartnerStatusText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Approval Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.UpgradeStateText._name
            },

          /*  {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Type Of Organization'),
                name : CmFinoFIX.message.JSAgent.Entries.TypeOfOrganization._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Fax Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.FaxNumber._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('WebSite'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.WebSite._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Authorized Representative'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.AuthorizedRepresentative._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Representative Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.RepresentativeName._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Designation'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.Designation._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Franchise Phone Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.FranchisePhoneNumber._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Franchise OutletAddress ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.FranchiseOutletAddressID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Classification'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.Classification._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Number Of Outlets'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.NumberOfOutlets._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Industry Classification'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.IndustryClassification._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Year Established'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.YearEstablished._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Authorized Fax Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.AuthorizedFaxNumber._name
            },*/
            {
                xtype: "displayfield",
                renderer : "date",
                fieldLabel: _('Applied Time'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.AppliedTime._name
            },
/*            {
                xtype: "displayfield",
                anchor : '100%',
                renderer: "date",
                fieldLabel: _('Approve/Reject Time'),
                name: CmFinoFIX.message.JSAgent.Entries.ApproveOrRejectTime._name
            },*/
            
            //simaspay changes starts
/*			{
				xtype : "displayfield",
				anchor : '75%',
				fieldLabel :_("Agent Code"),
				name: CmFinoFIX.message.JSAgent.Entries.AgentCode._name
			},*/
/*     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('Agent Type'),
     		   name : CmFinoFIX.message.JSAgent.Entries.AgentType._name
     	   },*/
     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('Classification Agent'),
     		   name : CmFinoFIX.message.JSAgent.Entries.ClassificationAgentText._name
     	   },
     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('Type of Business Agent'),
     		   name : CmFinoFIX.message.JSAgent.Entries.TypeofBusinessAgentText._name
     	   },
     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('Electonic Devie used'),
     		   name : CmFinoFIX.message.JSAgent.Entries.ElectonicDevieusedText._name
     	   },
/*     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('Description'),
     		   name : CmFinoFIX.message.JSAgent.Entries.AgentDescriptionText._name
     	   },
     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('KC/KCP/KK Bank Sinarmas'),
     		   name : CmFinoFIX.message.JSAgent.Entries.KCKCPKKBankSinarmasText._name
     	   },*/
/*     	   {
     		   xtype : "displayfield",
     		   anchor : '75%',
     		   fieldLabel :_('Branch code'),
     		   name : CmFinoFIX.message.JSAgent.Entries.BranchCodeText._name
     	   },*/
          /* {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("CIF Number"),
               name: CmFinoFIX.message.JSAgent.Entries.CIFNumber._name,
           },*/
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Account number of Bank Sinarmas"),
               name: CmFinoFIX.message.JSAgent.Entries.AccountnumberofBankSinarmas._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Branch of Bank Sinarmas"),
               name: CmFinoFIX.message.JSAgent.Entries.BranchCodeText._name,
           },
           
           
          /* {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Types of business Entity"),
               name: CmFinoFIX.message.JSAgent.Entries.TypesofbusinessEntityText._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Other Types of business Entity"),
               name: CmFinoFIX.message.JSAgent.Entries.OtherTypesofbusinessEntity._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Name of Business Entity"),
               name: CmFinoFIX.message.JSAgent.Entries.NameofBusinessEntity._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Business fields"),
               name: CmFinoFIX.message.JSAgent.Entries.Businessfields._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Tax File Number (NPWP)"),
               name: CmFinoFIX.message.JSAgent.Entries.TaxFileNumberNPWP._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_('Status of Business Sites'),
               name: CmFinoFIX.message.JSAgent.Entries.StatusofBusinessSitesText._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Other Status of Business Sites"),
               name: CmFinoFIX.message.JSAgent.Entries.OtherStatusofBusinessSites._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Facsimile Number"),
               name: CmFinoFIX.message.JSAgent.Entries.FacsimileNumber._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Website"),
               name: CmFinoFIX.message.JSAgent.Entries.Website._name,
           },*/
/*           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Name (In Accordance Identity)"),
               name: CmFinoFIX.message.JSAgent.Entries.NameInAccordanceIdentity._name,
           },*/
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("KTP ID"),
               name: CmFinoFIX.message.JSAgent.Entries.KTPID._name,
           },
/*           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Expired Date"),
               name: CmFinoFIX.message.JSAgent.Entries.ExpiredDate._name,
           },*/
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Alamat (InAccordanceIdentity)"),
               name: CmFinoFIX.message.JSAgent.Entries.AlamatInAccordanceIdentity._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("RT"),
               name: CmFinoFIX.message.JSAgent.Entries.RTAl._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("RW"),
               name: CmFinoFIX.message.JSAgent.Entries.RWAl._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Provincial"),
               name: CmFinoFIX.message.JSAgent.Entries.ProvincialAl._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               //fieldLabel :_("City"),
               fieldLabel :_("Region"),
               name: CmFinoFIX.message.JSAgent.Entries.CityAl._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("District"),
               name: CmFinoFIX.message.JSAgent.Entries.DistrictAl._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Village"),
               name: CmFinoFIX.message.JSAgent.Entries.VillageAl._name,
           },
           {
               xtype : "displayfield",
               anchor : '75%',
               fieldLabel :_("Potal Code"),
               name: CmFinoFIX.message.JSAgent.Entries.PotalCodeAl._name,
           },
           {
               xtype: "displayfield",
               fieldLabel: _('Applied By'),
               anchor : '75%',
               name: CmFinoFIX.message.JSAgent.Entries.AppliedBy._name
           }

           //simaspay changes end
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
            /*{
                xtype : 'displayfield',
                fieldLabel: _("Authorized Email"),
                name: CmFinoFIX.message.JSAgent.Entries.AuthorizedEmail._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("MDN"),
                name: CmFinoFIX.message.JSAgent.Entries.MDN._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Currency"),
                name: CmFinoFIX.message.JSAgent.Entries.Currency._name,
                anchor : '100%'
            },              
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Last Update Time'),
                renderer: "date",
                name : CmFinoFIX.message.JSAgent.Entries.LastUpdateTime._name
            },
             {
                xtype : 'displayfield',
                fieldLabel: _('Updated By'),
                anchor : '100%',
                name: CmFinoFIX.message.JSAgent.Entries.UpdatedBy._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Create Time'),
                renderer : "money",
                renderer: "date",
                name : CmFinoFIX.message.JSAgent.Entries.CreateTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Created By'),
                name : CmFinoFIX.message.JSAgent.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Line1'),
                name: CmFinoFIX.message.JSAgent.Entries.MerchantAddressLine1._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Line2'),
                name : CmFinoFIX.message.JSAgent.Entries.MerchantAddressLine2._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('City'),
                name : CmFinoFIX.message.JSAgent.Entries.MerchantAddressCity._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('State'),
                name: CmFinoFIX.message.JSAgent.Entries.MerchantAddressState._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Country'),
                name: CmFinoFIX.message.JSAgent.Entries.MerchantAddressCountry._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Zipcode'),
                name: CmFinoFIX.message.JSAgent.Entries.MerchantAddressZipcode._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Line1'),
                name: CmFinoFIX.message.JSAgent.Entries.OutletAddressLine1._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Line2'),
                name: CmFinoFIX.message.JSAgent.Entries.OutletAddressLine2._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address City'),
                name: CmFinoFIX.message.JSAgent.Entries.OutletAddressCity._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address State'),
                name: CmFinoFIX.message.JSAgent.Entries.OutletAddressState._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Country'),
                name: CmFinoFIX.message.JSAgent.Entries.OutletAddressCountry._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Zipcode'),
                name: CmFinoFIX.message.JSAgent.Entries.OutletAddressZipcode._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Applied By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSAgent.Entries.AppliedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approved/Rejected By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSAgent.Entries.ApprovedOrRejectedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approve/Reject Comment'),
                anchor : '75%',
                name: CmFinoFIX.message.JSAgent.Entries.ApproveOrRejectComment._name
            },*/
            
            //simaspay changes starts
           /* {
      		   xtype : "displayfield",
      		   anchor : '75%',
      		   fieldLabel :_('Domicile Address'),
      		   name : CmFinoFIX.message.JSAgent.Entries.DomicileAddressText._name
      	   },
         
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Address (Domicile)"),
             name: CmFinoFIX.message.JSAgent.Entries.AddressDomicile._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("RT/RW"),
             name: CmFinoFIX.message.JSAgent.Entries.RTRWDom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("District"),
             name: CmFinoFIX.message.JSAgent.Entries.DistrictDom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Village"),
             name: CmFinoFIX.message.JSAgent.Entries.VillageDom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("City"),
             name: CmFinoFIX.message.JSAgent.Entries.CityDom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Potal Code"),
             name: CmFinoFIX.message.JSAgent.Entries.PotalCodeDom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Provincial"),
             name: CmFinoFIX.message.JSAgent.Entries.ProvincialDom._name,
         },*/
/*         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Mobile Phone Number"),
             name: CmFinoFIX.message.JSAgent.Entries.MobilePhoneNumber._name,
         },*/
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("E-Mail"),
             name: CmFinoFIX.message.JSAgent.Entries.AuthorizedEmail._name,
         },
		  /* {
			   xtype : "displayfield",
			   anchor : '75%',
			   fieldLabel :_('Jobs'),
			   name : CmFinoFIX.message.JSAgent.Entries.JobsText._name,
		   },
		   {
			   xtype : "displayfield",
			   anchor : '75%',
			   fieldLabel :_('Other Jobs'),
			   name : CmFinoFIX.message.JSAgent.Entries.OtherJobs._name,
		   },*/
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Agreement Number"),
             name: CmFinoFIX.message.JSAgent.Entries.AgreementNumber._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Agreement Date"),
             name: CmFinoFIX.message.JSAgent.Entries.AgreementDate._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Implementation date"),
             name: CmFinoFIX.message.JSAgent.Entries.Implementationdate._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Place of Birth"),
             name: CmFinoFIX.message.JSAgent.Entries.PlaceofBirth._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Date of Birth"),
             name: CmFinoFIX.message.JSAgent.Entries.DateofBirth._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Company Name"),
             name: CmFinoFIX.message.JSAgent.Entries.AgentCompanyName._name,
         },
         /*{
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Business fields"),
             name: CmFinoFIX.message.JSAgent.Entries.Businessfields._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Position"),
             name: CmFinoFIX.message.JSAgent.Entries.Position._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Length of work"),
             name: CmFinoFIX.message.JSAgent.Entries.Lengthofwork._name,
         },*/
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Company Address"),
             name: CmFinoFIX.message.JSAgent.Entries.CompanyAddress._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("RT"),
             name: CmFinoFIX.message.JSAgent.Entries.RTCom._name,
        },
        {
            xtype : "displayfield",
            anchor : '75%',
            fieldLabel :_("RW"),
            name: CmFinoFIX.message.JSAgent.Entries.RWCom._name,
       },
       {
           xtype : "displayfield",
           anchor : '75%',
           fieldLabel :_("Provincial"),
           name: CmFinoFIX.message.JSAgent.Entries.ProvincialCom._name,
       },
       {
           xtype : "displayfield",
           anchor : '75%',
           fieldLabel :_("Region"),
           name: CmFinoFIX.message.JSAgent.Entries.CityCom._name,
       },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("District"),
             name: CmFinoFIX.message.JSAgent.Entries.DistrictCom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Village"),
             name: CmFinoFIX.message.JSAgent.Entries.VillageCom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Potal Code"),
             name: CmFinoFIX.message.JSAgent.Entries.PotalCodeCom._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Latitude"),
             name: CmFinoFIX.message.JSAgent.Entries.Latitude._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Longitude"),
             name: CmFinoFIX.message.JSAgent.Entries.Longitude._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Company Phone Number"),
             name: CmFinoFIX.message.JSAgent.Entries.PhoneNumber._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Company EmailId"),
             name: CmFinoFIX.message.JSAgent.Entries.CompanyEmailId._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("User Bank Branch"),
             name: CmFinoFIX.message.JSAgent.Entries.UserBankBranch._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Bank Account Status"),
             name: CmFinoFIX.message.JSAgent.Entries.BankAccountStatusText._name,
         },
         {
             xtype: "displayfield",
             fieldLabel: _('Approved/Rejected By'),
             anchor : '75%',
             name: CmFinoFIX.message.JSAgent.Entries.ApprovedOrRejectedBy._name
         },
         {
             xtype: "displayfield",
             fieldLabel: _('Approve/Reject Comment'),
             anchor : '75%',
             name: CmFinoFIX.message.JSAgent.Entries.ApproveOrRejectComment._name
         },
         {
             xtype: "displayfield",
             anchor : '100%',
             renderer: "date",
             fieldLabel: _('Approve/Reject Time'),
             name: CmFinoFIX.message.JSAgent.Entries.ApproveOrRejectTime._name
         }
        /* {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Legal Relationship"),
             name: CmFinoFIX.message.JSAgent.Entries.LegalRelationshipText._name,
         },
         {
             xtype : "displayfield",
             anchor : '75%',
             fieldLabel :_("Other Legal Relationship"),
             name: CmFinoFIX.message.JSAgent.Entries.OtherLegalRelationship._name,
         }*/
         //simaspay changes end
            ]
        }]
    });

    mFino.widget.ServicePartnerDetailssp.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.ServicePartnerDetailssp , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.ServicePartnerDetailssp.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
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

Ext.reg("ServicePartnerDetailssp", mFino.widget.ServicePartnerDetailssp);

