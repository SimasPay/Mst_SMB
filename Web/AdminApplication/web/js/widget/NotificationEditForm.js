/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.NotificationEditForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame:true
    });

    mFino.widget.NotificationEditForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.NotificationEditForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 80;
        this.labelPad = 20;
        this.items = [
        {
            xtype : "displayfield",
            fieldLabel :_("Code"),
            name: CmFinoFIX.message.JSNotification.Entries.NotificationCode._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_("Code Name"),
            name: CmFinoFIX.message.JSNotification.Entries.NotificationCodeName._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_("Mode"),
            name: CmFinoFIX.message.JSNotification.Entries.NotificationMethodText._name
        },
        {
            xtype: "displayfield",
            fieldLabel: _("Language"),
            name: CmFinoFIX.message.JSNotification.Entries.LanguageText._name
        },
        {
            xtype: "textfield",
            width:200,
            fieldLabel:  _("Access Code"),
            name: CmFinoFIX.message.JSNotification.Entries.AccessCode._name
        },
        {
            xtype: "textfield",
            fieldLabel:  _("SMS Notification Code"),
            width:200,
            name: CmFinoFIX.message.JSNotification.Entries.SMSNotificationCode._name
        },
        {
            xtype: "textarea",
            fieldLabel:  _("Message"),
            width:200,
            height: 100,
            maxLength : 1000,
            name: CmFinoFIX.message.JSNotification.Entries.NotificationText._name,
            allowBlank: false
        },
		{
                    xtype: 'checkbox',                   
                    fieldLabel: _('Is Enabled'),
                    itemId : 'ptc.form.isEnabled',
                    labelSeparator:':',
                    anchor:'95%',
                    name : CmFinoFIX.message.JSNotification.Entries.IsActive._name                    
        }];

        mFino.widget.NotificationEditForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSNotification.Entries.CompanyID._name, mFino.auth.getCompanyId());
            this.record.endEdit();
            if(this.store){
                if(this.record.phantom){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
			if ((this.form.items.get("ptc.form.isEnabled").checked)) {
				this.record.set(CmFinoFIX.message.JSNotification.Entries.IsActive._name,  true);
				
            }else{
				this.record.set(CmFinoFIX.message.JSNotification.Entries.IsActive._name,  false);
				
				
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

    onStoreUpdate: function(){
        this.setRecord(this.record);
    }

});

Ext.reg("notificationeditform", mFino.widget.NotificationEditForm);

