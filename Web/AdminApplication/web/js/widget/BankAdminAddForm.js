/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BankAdminAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle: 'padding:5px 5px 0',
        defaultType: 'textfield',
        width: 400,
        frame : true
    });

    mFino.widget.BankAdminAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BankAdminAddForm, Ext.FormPanel, {
    initComponent : function () {

        this.labelWidth = 120;
        this.labelPad = 20;

        this.items = [
        {
            fieldLabel: _('Username'),
            width : 150,
            name:CmFinoFIX.message.JSBankAdmin.Entries.Username._name,
            allowBlank: false,
            vtype:'usernamechk',
            itemId:'bankadmin.form.username',
            maxLength : 255,
            listeners: {
                change: function(field) {
                    this.findParentByType('bankadminaddform').onName(field);
                }
            },
            emptyText:'david_andrew',
            blankText : 'User name is required'
        },
        {
            fieldLabel: _('First Name'),
            width : 150,
            allowBlank: false,
            vtype:'name',
            maxLength : 255,
            name:CmFinoFIX.message.JSBankAdmin.Entries.FirstName._name
        },
        {
            fieldLabel: _('Last Name'),
            width : 150,
            vtype:'name',
            maxLength : 255,
            name:CmFinoFIX.message.JSBankAdmin.Entries.LastName._name
        },
        {
            fieldLabel: _('Email'),
            width : 150,
            allowBlank: false,
            blankText : _('Email is required'),
            vtype: 'email',
            maxLength : 255,
            emptyText: _('xyz@abc.com'),
            name:CmFinoFIX.message.JSBankAdmin.Entries.Email._name,
            vtypeText: _("Email should be in the format of user@domain.com")
        },
        {
            xtype: 'enumdropdown',
            fieldLabel  : _('Role'),
            enumId: CmFinoFIX.TagID.BankRoles,
            itemId : 'bankRole',
            name: CmFinoFIX.message.JSBankAdmin.Entries.BankRoles._name,
            width  : 150,
            allowBlank: false,
            mode: 'local',
            triggerAction: 'all',
            emptyText : '<Select one..>'
        },
        {
            fieldLabel: _('Language'),
            xtype: 'enumdropdown',
            width  : 150,
            allowBlank: false,
            enumId: CmFinoFIX.TagID.Language,
            emptyText : _('<Select one..>'),
            name:CmFinoFIX.message.JSBankAdmin.Entries.Language._name
        },
        {
            fieldLabel: _('Time zone'),
            xtype: 'enumdropdown',
            width  : 150,
            allowBlank: false,
            emptyText : _('<Select one..>'),
            enumId: CmFinoFIX.TagID.Timezone,
            name:CmFinoFIX.message.JSBankAdmin.Entries.Timezone._name
        },
        {
            xtype:'remotedropdown',
            itemId : 'bankadmin.form.bankname',
            fieldLabel :_("Bank Name"),
            width  : 150,
            allowBlank : false,
            pageSize : 10,
            RPCObject : CmFinoFIX.message.JSBank,
            displayField: CmFinoFIX.message.JSBank.Entries.BankName._name,
            valueField : CmFinoFIX.message.JSBank.Entries.ID._name,
            name: CmFinoFIX.message.JSBankAdmin.Entries.BankID._name,
            listeners: {
                focus: function(){
                    this.reload();
                }
            }
        },
        {
            fieldLabel: _('Security Lock'),
            xtype: 'checkbox',
            itemId:'usersecuritylock_add'
        },
        {
            fieldLabel: _('Suspend'),
            xtype: 'checkbox',
            itemId:'usersuspend_add'
        },
        {
            fieldLabel: _('Administrative Comment'),
            xtype: 'textarea',
            width : 150,
            allowBlank: false,
            maxLength : 1000,
            name:CmFinoFIX.message.JSBankAdmin.Entries.AdminComment._name
        }
        ] ;

        mFino.widget.BankAdminAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    setRecord: function(record) {
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
    },
    onName : function(field){
        var value = this.record.get(CmFinoFIX.message.JSBankAdmin.Entries.Username.name);
        if(field.getValue() !== value){
            var msg = new CmFinoFIX.message.JSUsernameCheck();
            msg.m_pUsername = field.getValue();
            msg.m_pCheckIfExists = true;
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
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
    },

    save : function(){
        if (this.getForm().isValid()) {
            this.getForm().updateRecord(this.record);
            if(this.store){
                var securityLock = this.getComponent('usersecuritylock_add');
                var suspend = this.getComponent('usersuspend_add');
                var rest = 0;
                if (securityLock.checked) {
                    rest += CmFinoFIX.SubscriberRestrictions.SecurityLocked;
                }
                if (suspend.checked) {
                    rest += CmFinoFIX.SubscriberRestrictions.Suspended;
                }

                this.record.beginEdit();
                this.record.set(CmFinoFIX.message.JSBankAdmin.Entries.UserRestrictions._name, rest);              
                this.record.endEdit();

                if(this.record.phantom) {
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    }
});
Ext.reg("bankadminaddform", mFino.widget.BankAdminAddForm);
