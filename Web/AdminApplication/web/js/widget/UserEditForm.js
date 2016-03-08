/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UserEditForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle: 'padding:5px 5px 0',
        defaultType: 'textfield',
        frame : true
    });
    mFino.widget.UserEditForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UserEditForm, Ext.FormPanel, {
    initComponent : function () {
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
        
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [
        {
            fieldLabel: _('Username'),
            xtype:'displayfield',
            name:CmFinoFIX.message.JSUsers.Entries.Username._name
        },
        {
            fieldLabel: _('First Name'),
            width : 150,
            allowBlank: false,
            vtype:'name',
            maxLength : 255,
            name:CmFinoFIX.message.JSUsers.Entries.FirstName._name
        },
        {
            fieldLabel: _('Last Name'),
            width : 150,
            vtype:'name',
            maxLength : 255,
            name:CmFinoFIX.message.JSUsers.Entries.LastName._name
        },
        {
            fieldLabel: _('Email'),
            width : 150,
            allowBlank: false,
            itemId : 'userEmail',
            blankText : _('Email is required'),
            vtype: 'email',
            name:CmFinoFIX.message.JSUsers.Entries.Email._name,
            maxLength : 255,
            vtypeText: _("Email should be in the format of user@domain.com")
        },
        {
        	xtype: 'remotedropdown',
            fieldLabel  : _('Role'),
            itemId : 'userRole',
            width : 150,
            allowBlank: false,
            addEmpty : false,
            emptyText : _('<Select one..>'),
            pageSize: 10,
            params: {start:0, limit:10},
            name: CmFinoFIX.message.JSUsers.Entries.Role._name,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSRole), 
            displayField: CmFinoFIX.message.JSRole.Entries.DisplayText._name, 
            valueField : CmFinoFIX.message.JSRole.Entries.ID._name, 
            hiddenName : CmFinoFIX.message.JSUsers.Entries.Role._name,
            pageSize: 10,
            params: {start:0, limit:10}
        },
        {
            fieldLabel: _('Language'),
            xtype: 'enumdropdown',
            allowBlank: false,
           width : 150,
            enumId: CmFinoFIX.TagID.Language,
            name: CmFinoFIX.message.JSUsers.Entries.Language._name
        },
        {
            fieldLabel: _('Time zone'),
            xtype: 'enumdropdown',
            allowBlank: false,
            width : 150,
            enumId: CmFinoFIX.TagID.Timezone,
            name:CmFinoFIX.message.JSUsers.Entries.Timezone._name
        },
        {
            fieldLabel: _('Security Lock'),
            xtype: 'checkbox',
            itemId:'usersecuritylock'
        },
        {
            fieldLabel: _('Suspend'),
            xtype: 'checkbox',
            itemId:'usersuspend'
        },
        {
            xtype: 'enumdropdown',
            fieldLabel  : _('Status'),
            enumId: CmFinoFIX.TagID.UserStatus,
            name: CmFinoFIX.message.JSUsers.Entries.UserStatus._name,
            width : 150,
            mode: 'local',
            triggerAction: 'all',
            emptyText : _('<Select one..>')
        },
        {
            fieldLabel: _('Administrative Comment'),
            xtype: 'textarea',
            width : 150,
            maxLength : 1000,
            name:CmFinoFIX.message.JSUsers.Entries.AdminComment._name
        }
        ];
        mFino.widget.UserEditForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },

    setRecord: function(record) {
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        var role_combo = this.find('itemId','userRole')[0];
        role_combo.setRawValue(this.record.get(CmFinoFIX.message.JSUsers.Entries.RoleText._name));
        var restrictions = this.record.get(CmFinoFIX.message.JSUsers.Entries.UserRestrictions._name);
        var suspended = this.getComponent('usersuspend');
        var secLock = this.getComponent('usersecuritylock');
        suspended.setValue((restrictions & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
        secLock.setValue((restrictions & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
    },


    save: function() {
        if (this.getForm().isValid()) {
            this.getForm().updateRecord(this.record);

            var securityLock = this.getComponent('usersecuritylock');
            var suspend = this.getComponent('usersuspend');
            var rest = 0;
            if (securityLock.checked) {
                rest += CmFinoFIX.SubscriberRestrictions.SecurityLocked;
            }
            if (suspend.checked) {
                rest += CmFinoFIX.SubscriberRestrictions.Suspended;
            }

            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSUsers.Entries.UserRestrictions._name, rest);
            this.record.endEdit();
            
            if(this.store){
                this.store.save();
            }
        }
    }
});

Ext.reg("usereditform", mFino.widget.UserEditForm);

