/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UserAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle: 'padding:5px 5px 0',
        defaultType: 'textfield',
        width: 400,
        frame : true
    });

    mFino.widget.UserAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UserAddForm, Ext.FormPanel, {
    initComponent : function () {

        this.labelWidth = 120;
        this.labelPad = 20;

        this.items = [
        {
            fieldLabel: _('Username'),
            width : 150,
            name:CmFinoFIX.message.JSUsers.Entries.Username._name,
            allowBlank: false,
            itemId : 'UserAdd.form.name',
            vtype:'usernamechk',
            maxLength : 255,
            listeners: {
                change: function(field) {
                    this.findParentByType('useraddform').onName(field);
                }
            },
            emptyText:'',
            blankText : 'User name is required'
        },
        {
            fieldLabel: _('First Name'),
            width : 150,
            allowBlank: false,
            itemId : 'UserAdd.form.firstname',
            vtype:'name',
            maxLength : 255,
            name:CmFinoFIX.message.JSUsers.Entries.FirstName._name
        },
        {
            fieldLabel: _('Last Name'),
            width : 150,
            itemId : 'UserAdd.form.lastname',
            vtype:'name',
            maxLength : 255,
            name:CmFinoFIX.message.JSUsers.Entries.LastName._name
        },
        {
            fieldLabel: _('Email'),
            width : 150,
            allowBlank: false,
            blankText : _('Email is required'),
            vtype: 'email',
            itemId : 'UserAdd.form.email',
            maxLength : 255,
            emptyText: _('xyz@abc.com'),
            name:CmFinoFIX.message.JSUsers.Entries.Email._name,
            vtypeText: _("Email should be in the format of user@domain.com")
        },
        {
            xtype: 'remotedropdown',
            fieldLabel  : _('Role'),
            width  : 150,
            itemId : 'UserAdd.form.role',
            allowBlank: false,
            addEmpty : false,
            emptyText : '<Select one..>',
            name: CmFinoFIX.message.JSUsers.Entries.Role._name,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSRole), 
            displayField: CmFinoFIX.message.JSRole.Entries.DisplayText._name, 
            valueField : CmFinoFIX.message.JSRole.Entries.ID._name, 
            hiddenName : CmFinoFIX.message.JSUsers.Entries.Role._name,
            pageSize: 10,
            params: {start:0, limit:10}
        },
        {
            xtype: 'remotedropdown',
            fieldLabel  : _('BranchCode'),
            width  : 150,
            itemId : 'UserAdd.form.branchcode',
            allowBlank: false,
            addEmpty : false,
            emptyText : '<Select one..>',
            name: CmFinoFIX.message.JSUsers.Entries.BranchCodeID._name,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSBranchCodes), 
            displayField: CmFinoFIX.message.JSBranchCodes.Entries.BranchName._name, 
            valueField : CmFinoFIX.message.JSBranchCodes.Entries.ID._name, 
            hiddenName : CmFinoFIX.message.JSUsers.Entries.BranchCodeID._name,
            pageSize: 10,
            params: {start:0, limit:10}
        },
        {
            fieldLabel: _('Language'),
            xtype: 'enumdropdown',
            width  : 150,
            allowBlank: false,
            itemId : 'UserAdd.form.language',
            addEmpty : false,
            enumId: CmFinoFIX.TagID.Language,
            emptyText : _('<Select one..>'),
            name:CmFinoFIX.message.JSUsers.Entries.Language._name
        },
        {
            fieldLabel: _('Time zone'),
            xtype: 'enumdropdown',
            width  : 150,
            allowBlank: false,
            itemId : 'UserAdd.form.timezone',
            addEmpty : false,
            emptyText : _('<Select one..>'),
            enumId: CmFinoFIX.TagID.Timezone,
            name:CmFinoFIX.message.JSUsers.Entries.Timezone._name
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
            maxLength : 1000,
            itemId : 'UserAdd.form.administrator',
            name:CmFinoFIX.message.JSUsers.Entries.AdminComment._name
        }
        ] ;

        mFino.widget.UserAddForm.superclass.initComponent.call(this);
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
        var value = this.record.get(CmFinoFIX.message.JSUsers.Entries.Username.name);
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
                this.record.set(CmFinoFIX.message.JSUsers.Entries.UserRestrictions._name, rest);
                this.record.endEdit();

                if(this.record.phantom
                		&& this.store.getAt(0)!= this.record) {
                    this.store.insert(0, this.record);
                }

                this.store.save();
            }
        }
    }
});
Ext.reg("useraddform", mFino.widget.UserAddForm);
