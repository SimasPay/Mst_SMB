/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.NotificationDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.6,
            layout: 'form',
            labelWidth : 75,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Code"),
                name: CmFinoFIX.message.JSNotification.Entries.NotificationCode._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Code Name"),
                name: CmFinoFIX.message.JSNotification.Entries.NotificationCodeName._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Language'),
                anchor : '75%',
                name: CmFinoFIX.message.JSNotification.Entries.LanguageText._name
            }
            ]
        },
        {
            columnWidth: 0.4,
            layout: 'form',
            labelWidth : 75,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Message ID"),
                name: CmFinoFIX.message.JSNotification.Entries.ID._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Mode"),
                name: CmFinoFIX.message.JSNotification.Entries.NotificationMethod._name,
                renderer: function(value){
                    var str="";
                    if((value & CmFinoFIX.NotificationMethod.SMS)===CmFinoFIX.NotificationMethod.SMS){
                        str+=" SMS ";
                    }else if((value & CmFinoFIX.NotificationMethod.Email)===CmFinoFIX.NotificationMethod.Email){
                         str+=" Email ";
                    }else if((value & CmFinoFIX.NotificationMethod.Web)===CmFinoFIX.NotificationMethod.Web){
                         str+=" Web ";
                    }else if((value & CmFinoFIX.NotificationMethod.WebService)===CmFinoFIX.NotificationMethod.WebService){
                         str+=" WebService ";
                    }else if((value & CmFinoFIX.NotificationMethod.BankChannel)===CmFinoFIX.NotificationMethod.BankChannel){
                         str+=" BankChannel ";
                    }else {
                        return "--";
                    }
                    return str;
                },
                anchor : '75%'
            }
            ]
        },
        {
            columnWidth: 1.0,
            layout: 'form',
            labelWidth : 75,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _('Message'),
                anchor : '100%',
                name: CmFinoFIX.message.JSNotification.Entries.NotificationText._name
            }
            ]
        }
    ]
    });

    mFino.widget.NotificationDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.NotificationDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.NotificationDetails.superclass.initComponent.call(this);
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

Ext.reg("notificationdetails", mFino.widget.NotificationDetails);

