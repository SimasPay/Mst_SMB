/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CCDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
//        id : "ccreviwerdetails",
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 150,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("CardID"),
                name: CmFinoFIX.message.JSCardInfo.Entries.ID._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('First 6Digits'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardF6._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('last 4Digits'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardL4._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Card Issuer Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardIssuerName._name
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('BillingAddress'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardBillingLine1._name 
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('BillingAddress Line2'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardBillingLine2._name 
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('BillingAddress City'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardBillingCity._name 
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('BillingAddress State'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardBillingState._name 
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('BillingAddress Zipcode'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardBillingZipCode._name 
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.UpdatedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified on'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSCardInfo.Entries.LastUpdateTime._name
            }
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 150,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _('SubscriberID'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.SubscriberID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Pocket ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardPocketID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardStatusText._name
            },
             {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Name on Card'),
                name: CmFinoFIX.message.JSCardInfo.Entries.CardNameOnCard._name
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('Address'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardLine1._name
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('AddressLine2'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardLine2._name
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('City'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardCity._name
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('State'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardState._name
            },
            {
            	xtype : 'displayfield',
                fieldLabel: _('ZipCode'),
                anchor : '75%',
                name: CmFinoFIX.message.JSCardInfo.Entries.CardZipCode._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Created on'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSCardInfo.Entries.CreateTime._name
            }           
            ]
        }
        ]
    });

    mFino.widget.CCDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CCDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.CCDetails.superclass.initComponent.call(this);
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

Ext.reg("ccreviewerDetails", mFino.widget.CCDetails);
