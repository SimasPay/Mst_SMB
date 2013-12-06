/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSCAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.SMSCAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSCAddForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [
        {
            xtype : 'textfield',
            fieldLabel :_("Short Code"),
            allowBlank : false,
            vtype : 'number10',
            name: CmFinoFIX.message.JSSMSC.Entries.ShortCode._name
        },
        {
            xtype : 'textfield',
            fieldLabel: _('Long Number'),
            vtype : 'number20',
            name: CmFinoFIX.message.JSSMSC.Entries.LongNumber._name
        },
        {
            xtype: "textfield",
            allowBlank : false,
            fieldLabel: _('Smartfren SMSCID'),
            name: CmFinoFIX.message.JSSMSC.Entries.SmartfrenSMSCID._name
        },
        {
            xtype : 'textfield',
            allowBlank : false,
            fieldLabel: _("OtherLocalOperator SMSCID"),
            name: CmFinoFIX.message.JSSMSC.Entries.OtherLocalOperatorSMSCID._name
        },
        {
            xtype : 'numberfield',
            fieldLabel: _("Pricing"),
            allowBlank: false,
            allowNegative:false,
            allowDecimals:true,
            decimalPrecision : 2,
            name: CmFinoFIX.message.JSSMSC.Entries.Charging._name
        },
        {
            xtype:'textfield',
            fieldLabel: _("Header"),
            name: CmFinoFIX.message.JSSMSC.Entries.Header._name
        },
        {
            xtype:'textfield',
            fieldLabel: _("Footer"),
            name: CmFinoFIX.message.JSSMSC.Entries.Footer._name
        }
        ];
        mFino.widget.SMSCAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
            if(this.store){
                if(this.record.phantom
                    && this.store.getAt(0)!= this.record){
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

    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});
Ext.reg("smscaddform", mFino.widget.SMSCAddForm);
