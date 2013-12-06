/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BankAdminSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Bank Admin Search'),
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',
        
        items: [{
            fieldLabel: _('Username'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSBankAdmin.UsernameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }, {
            fieldLabel: _('First Name'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSBankAdmin.FirstNameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            fieldLabel: _('Last Name'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSBankAdmin.LastNameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            xtype:'enumdropdown',
            fieldLabel  : _('Role'),
            labelSeparator : '',
            anchor : '95%',
            mode: 'local',
            triggerAction: 'all',
            emptyText : '<Select one..>',
            enumId: CmFinoFIX.TagID.BankRoles,
            name: CmFinoFIX.message.JSBankAdmin.RoleSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }, {
            fieldLabel: _('Status'),
            xtype:'enumdropdown',
            anchor : '95%',
            emptyText : '<Any>',
            enumId: CmFinoFIX.TagID.UserStatus,
            labelSeparator : '',
            name: CmFinoFIX.message.JSBankAdmin.StatusSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            fieldLabel: _('Restrictions'),
            width  : 130,
            labelSeparator : '',
            anchor : '95%',
            emptyText:'<Any>',
            xtype:'enumdropdown',
            enumId: CmFinoFIX.TagID.UserRestrictions,
            name: CmFinoFIX.message.JSBankAdmin.RestrictionsSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }
        ]
    });

    mFino.widget.BankAdminSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BankAdminSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        mFino.widget.BankAdminSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
    },
    
    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            this.fireEvent("search", values);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
