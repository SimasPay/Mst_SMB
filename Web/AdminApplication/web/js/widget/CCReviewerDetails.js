/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CCReviewerDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
//        id : "ccreviwerdetails",
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Username"),
                name: CmFinoFIX.message.JSUsers.Entries.Username._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('First Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.FirstName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Last Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.LastName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('DateOfBirth'),
                anchor : '75%',
                renderer: "date",
                format: "d-m-Y",
                name: CmFinoFIX.message.JSUsers.Entries.DateOfBirth._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Create on'),
                renderer : 'date',
                name: CmFinoFIX.message.JSUsers.Entries.CreateTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Expiration Time'),
                anchor : '75%',
                renderer: "date",
                name: CmFinoFIX.message.JSUsers.Entries.ExpirationTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.UpdatedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified on'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSUsers.Entries.LastUpdateTime._name
            }
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            items : [
            {
                xtype: "displayfield",
                fieldLabel: _('User ID'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.ID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Status'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.UserStatusText._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('HomePhone'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.HomePhone._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('WorkPhone'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.WorkPhone._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('SecurityQuestion'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.SecurityQuestion._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('SecurityAnswer'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.SecurityAnswer._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Time zone'),
                enumId: CmFinoFIX.TagID.Timezone,
                name : CmFinoFIX.message.JSUsers.Entries.Timezone._name
            },
           
           
            {
                xtype: "displayfield",
                fieldLabel: _('Activation Time'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSUsers.Entries.UserActivationTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Confirmation Time'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSUsers.Entries.ConfirmationTime._name
            }
            ]
        }
        ]
    });

    mFino.widget.CCReviewerDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CCReviewerDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.CCReviewerDetails.superclass.initComponent.call(this);
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

Ext.reg("ccreviewerDetails", mFino.widget.CCReviewerDetails);
