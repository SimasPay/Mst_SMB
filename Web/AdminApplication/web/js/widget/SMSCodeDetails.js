/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSCodeDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        title: 'SMS Code Details',
        frame : true,
        items: [        {
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("SMS Code"),
                name: CmFinoFIX.message.JSSMSCode.Entries.SMSCodeText._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Brand"),
                name: CmFinoFIX.message.JSSMSCode.Entries.BrandName._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Function"),
                name: CmFinoFIX.message.JSSMSCode.Entries.ServiceName._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Description"),
                name: CmFinoFIX.message.JSSMSCode.Entries.Description._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Status"),
                name: CmFinoFIX.message.JSSMSCode.Entries.SMSCodeStatusText._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Short Codes"),
                name: CmFinoFIX.message.JSSMSCode.Entries.ShortCodes._name
            }
            ]
        }
        ]
    });

    mFino.widget.SMSCodeDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.SMSCodeDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.SMSCodeDetails.superclass.initComponent.call(this);
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
Ext.reg("SMSCodeDetails", mFino.widget.SMSCodeDetails);