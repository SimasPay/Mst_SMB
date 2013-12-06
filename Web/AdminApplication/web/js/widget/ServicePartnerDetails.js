/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerDetails = function (config)
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
             {
                xtype : 'displayfield',
                fieldLabel: _('Code'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.PartnerCode._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Trade Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.TradeName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('User Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.Username._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Group'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.GroupName._name
            },
           /* 
            {
                xtype : 'displayfield',
                fieldLabel: _('Subscriber ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.SubscriberID._name
            },
           */
            {
                xtype : 'displayfield',
                fieldLabel: _('Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.PartnerStatusText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Approval Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.UpgradeStateText._name
            },
           
            {
                xtype : 'displayfield',
                fieldLabel: _('Type'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.BusinessPartnerTypeText._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Type Of Organization'),
                name : CmFinoFIX.message.JSPartner.Entries.TypeOfOrganization._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Fax Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.FaxNumber._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('WebSite'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.WebSite._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Authorized Representative'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.AuthorizedRepresentative._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Representative Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.RepresentativeName._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Designation'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.Designation._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Franchise Phone Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.FranchisePhoneNumber._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Franchise OutletAddress ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.FranchiseOutletAddressID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Classification'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.Classification._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Number Of Outlets'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.NumberOfOutlets._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Industry Classification'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.IndustryClassification._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Year Established'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.YearEstablished._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Authorized Fax Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.AuthorizedFaxNumber._name
            },
            {
                xtype: "displayfield",
                renderer : "date",
                fieldLabel: _('Applied Time'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.AppliedTime._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                renderer: "date",
                fieldLabel: _('Approve/Reject Time'),
                name: CmFinoFIX.message.JSPartner.Entries.ApproveOrRejectTime._name
            }
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Authorized Email"),
                name: CmFinoFIX.message.JSPartner.Entries.AuthorizedEmail._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("MDN"),
                name: CmFinoFIX.message.JSPartner.Entries.MDN._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Currency"),
                name: CmFinoFIX.message.JSPartner.Entries.Currency._name,
                anchor : '100%'
            },              
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Last Update Time'),
                renderer: "date",
                name : CmFinoFIX.message.JSPartner.Entries.LastUpdateTime._name
            },
             {
                xtype : 'displayfield',
                fieldLabel: _('Updated By'),
                anchor : '100%',
                name: CmFinoFIX.message.JSPartner.Entries.UpdatedBy._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Create Time'),
                renderer : "money",
                renderer: "date",
                name : CmFinoFIX.message.JSPartner.Entries.CreateTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Created By'),
                name : CmFinoFIX.message.JSPartner.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Line1'),
                name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressLine1._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Line2'),
                name : CmFinoFIX.message.JSPartner.Entries.MerchantAddressLine2._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('City'),
                name : CmFinoFIX.message.JSPartner.Entries.MerchantAddressCity._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('State'),
                name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressState._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Country'),
                name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressCountry._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Zipcode'),
                name: CmFinoFIX.message.JSPartner.Entries.MerchantAddressZipcode._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Line1'),
                name: CmFinoFIX.message.JSPartner.Entries.OutletAddressLine1._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Line2'),
                name: CmFinoFIX.message.JSPartner.Entries.OutletAddressLine2._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address City'),
                name: CmFinoFIX.message.JSPartner.Entries.OutletAddressCity._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address State'),
                name: CmFinoFIX.message.JSPartner.Entries.OutletAddressState._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Country'),
                name: CmFinoFIX.message.JSPartner.Entries.OutletAddressCountry._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Outlet Address Zipcode'),
                name: CmFinoFIX.message.JSPartner.Entries.OutletAddressZipcode._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Applied By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSPartner.Entries.AppliedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approved/Rejected By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSPartner.Entries.ApprovedOrRejectedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approve/Reject Comment'),
                anchor : '75%',
                name: CmFinoFIX.message.JSPartner.Entries.ApproveOrRejectComment._name
            }
            ]
        }]
    });

    mFino.widget.ServicePartnerDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.ServicePartnerDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.ServicePartnerDetails.superclass.initComponent.call(this);
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

Ext.reg("ServicePartnerDetails", mFino.widget.ServicePartnerDetails);

