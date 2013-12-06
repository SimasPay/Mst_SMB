/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        id : "merchantdetails",
        layout:'column',
        frame : true,
        width:659,
        items: [ {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("MDN"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.MDN._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Username"),
                name: CmFinoFIX.message.JSMerchant.Entries.Username._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Status"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.SubscriberStatusText._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Dist Chain Template'),
                name : CmFinoFIX.message.JSMerchant.Entries.DistributionChainName._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Region Name'),
                name : CmFinoFIX.message.JSMerchant.Entries.RegionName._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Source IP'),
                name : CmFinoFIX.message.JSMerchant.Entries.H2HAllowedIP._name
            },
            {
                xtype:'displayfield',
                fieldLabel: _('Admin Comment'),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.AdminComment._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _("Restrictions"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.SubscriberRestrictionsText._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _("Website"),
                name: CmFinoFIX.message.JSMerchant.Entries.WebSite._name
            },
////        Based on ticket #481 Moving the code to DCT/DCL
//            {
//                xtype : "displayfield",
//                anchor : '100%',
//                fieldLabel :_('Maximum Weekly Purchase Amount'),
//                renderer : "money",
//                name : CmFinoFIX.message.JSMerchant.Entries.MaxWeeklyPurchaseAmount._name
//            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Creation Time'),
                renderer : 'date',
                name: CmFinoFIX.message.JSMerchant.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Update Time'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSMerchant.Entries.LastUpdateTime._name
            }            
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 90,
            items : [
            {
                xtype: "displayfield",
                fieldLabel: _("Merchant ID"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.ID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Trade Name"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.TradeName._name
            },
             {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Partner Type'),
                name : CmFinoFIX.message.JSMerchant.Entries.PartnerTypeText._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _("Parent"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.ParentName._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _("Group ID"),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.GroupIDDisplayText._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_("Fax No"),
                name : CmFinoFIX.message.JSMerchant.Entries.FaxNumber._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _("Email"),
                name : CmFinoFIX.message.JSMerchant.Entries.Email._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _("Type of Org"),
                name : CmFinoFIX.message.JSMerchant.Entries.TypeOfOrganization._name
            },        
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Updated By'),
                anchor : '100%',
                name: CmFinoFIX.message.JSMerchant.Entries.UpdatedBy._name
            }
            ]
        }]
    });

    mFino.widget.MerchantDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.MerchantDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.MerchantDetails.superclass.initComponent.call(this);       
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
Ext.reg("merchantdetails", mFino.widget.MerchantDetails);
