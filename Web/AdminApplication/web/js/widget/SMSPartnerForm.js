/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSPartnerForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.SMSPartnerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSPartnerForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 100;
        this.labelPad = 20;
        this.items = [ 
        {
            layout: 'form',
            autoHeight: true,
            items : [
            {
                xtype : 'textfield',
                fieldLabel: _("Partner Name"),
                allowBlank: false,
                name: CmFinoFIX.message.JSSMSPartner.Entries.PartnerName._name,
                anchor : '75%'
            },
            {
                xtype : 'textfield',
                fieldLabel: _("User Name"),
                allowBlank: false,
                itemId : 'sms.partner.username',
                name: CmFinoFIX.message.JSSMSPartner.Entries.Username._name,
                anchor : '75%',
                listeners: {
                    change: function(field) {
                        this.findParentByType('SMSPartnerform').onName(field);
                    }
                }
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Password"),
                allowBlank: false,
                inputType: 'password',
                itemId : 'sms.partner.password',
                name: CmFinoFIX.message.JSSMSPartner.Entries.Password._name,
                anchor : '75%'
            },
            {
                xtype : 'textfield',
                fieldLabel: _('Contact Name'),
                anchor : '75%',
                allowBlank: false,
                name: CmFinoFIX.message.JSSMSPartner.Entries.ContactName._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _('Contact Email'),
                anchor : '75%',
                allowBlank: false,
                vtype: 'email',
                name: CmFinoFIX.message.JSSMSPartner.Entries.ContactEmail._name
            },
            {
                xtype : 'numberfield',
                allowDecimals:false,
                anchor : '75%',
                allowBlank: false,
                fieldLabel : _('Contact Phone'),
                vtype: 'smarttelcophoneAdd',
                name : CmFinoFIX.message.JSSMSPartner.Entries.ContactPhone._name
            },
            {
                xtype : "textfield",
                anchor : '75%',
                allowBlank: false,
                fieldLabel : _('Server IP'),
                vtype: 'ipAddress',
                name : CmFinoFIX.message.JSSMSPartner.Entries.ServerIP._name
            },
            {
                fieldLabel: _('Send Report'),
                xtype:'enumdropdown',
                anchor : '75%',
                allowBlank: false,
                itemId : 'sms.partner.sendreport',
                enumId: CmFinoFIX.TagID.Boolean,
                name: CmFinoFIX.message.JSSMSPartner.Entries.SendReport._name
            }
            ]
        }
        ] ;
        mFino.widget.SMSPartnerForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
   
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            if(this.store){
                if(this.record.phantom && !(this.record.store)){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
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
    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});

Ext.reg("SMSPartnerform", mFino.widget.SMSPartnerForm);
