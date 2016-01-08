/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UserDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        id : "userdetails",
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
                fieldLabel: _('Email'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.Email._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Language'),
                enumId : CmFinoFIX.TagID.Language,
                name : CmFinoFIX.message.JSUsers.Entries.LanguageText._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSUsers.Entries.UpdatedBy._name
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
                fieldLabel: _('Role'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.RoleText._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Branch Code'),
                anchor : '100%',
                name: CmFinoFIX.message.JSUsers.Entries.BranchCodeText._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _('Security Lock'),
                itemId: "securitylockcb",
                name: CmFinoFIX.message.JSUsers.Entries.UserSecurityLocked._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _('Suspend'),
                itemId: "suspendcb",
                name: CmFinoFIX.message.JSUsers.Entries.UserSuspended._name
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
                anchor : '100%',
                fieldLabel: _('Create on'),
                renderer : 'date',                
                name: CmFinoFIX.message.JSUsers.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified on'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSUsers.Entries.LastUpdateTime._name
            }
            ]
        }
        ]
    });

    mFino.widget.UserDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UserDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.UserDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);

        //this.getForm().items.get("suspendcb").setValue(this.record.get('UserSuspended'));
        //this.getForm().items.get("securitylockcb").setValue(this.record.get('UserSecurityLocked'));

        this.find("itemId", "suspendcb")[0].setValue(this.record.get('UserSuspended'));
        this.find("itemId", "securitylockcb")[0].setValue(this.record.get('UserSecurityLocked'));
        
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

Ext.reg("userdetails", mFino.widget.UserDetails);

