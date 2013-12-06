/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSPartnerDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        width: 660,
        height : 173,
        items: [    {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Partner Name"),
                name: CmFinoFIX.message.JSSMSPartner.Entries.PartnerName._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("User Name"),
                name: CmFinoFIX.message.JSSMSPartner.Entries.Username._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Contact Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSMSPartner.Entries.ContactName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Contact Email'),
                anchor : '100%',
                name: CmFinoFIX.message.JSSMSPartner.Entries.ContactEmail._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Contact Phone'),
                name : CmFinoFIX.message.JSSMSPartner.Entries.ContactPhone._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Server IP'),
                name : CmFinoFIX.message.JSSMSPartner.Entries.ServerIP._name
            }
            ]
        },
        {
            columnWidth: 0.45,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype: "displayfield",
                fieldLabel: _('Partner ID'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSMSPartner.Entries.ID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Creation Time'),
                name : CmFinoFIX.message.JSSMSPartner.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSMSPartner.Entries.CreatedBy._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Last Update Time'),
                name : CmFinoFIX.message.JSSMSPartner.Entries.LastUpdateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Updated By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSMSPartner.Entries.UpdatedBy._name
            }
            ]
        }]
    });

    mFino.widget.SMSPartnerDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSPartnerDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.SMSPartnerDetails.superclass.initComponent.call(this);
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

Ext.reg("SMSPartnerdetails", mFino.widget.SMSPartnerDetails);

