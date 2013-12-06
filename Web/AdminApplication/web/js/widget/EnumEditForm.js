/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.EnumEditForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.EnumEditForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.EnumEditForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 80;
        this.labelPad = 20;
        this.items = [
        {
            xtype : "displayfield",
            fieldLabel :_("Tag Name"),
            width:200,
            readOnly:true,
            name: CmFinoFIX.message.JSEnumText.Entries.TagName._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_("Tag ID"),
            width:200,
            readOnly:true,
            name: CmFinoFIX.message.JSEnumText.Entries.TagID._name
        },
        {
             xtype : "displayfield",
             fieldLabel :_("Field Name"),
             width:200,
             readOnly:true,
            name: CmFinoFIX.message.JSEnumText.Entries.EnumValue._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_("Enum Code"),
            width:200,
            readOnly:true,
            name: CmFinoFIX.message.JSEnumText.Entries.EnumCode._name
        },
        {
            xtype : "displayfield",
            fieldLabel: _('Language'),
            width:200,
            readOnly:true,
            name: CmFinoFIX.message.JSEnumText.Entries.LanguageText._name
        },
        {
            xtype: "textarea",
            fieldLabel:  _("Display Text"),
            width:200,
            height: 100,
            maxLength:255,
            name: CmFinoFIX.message.JSEnumText.Entries.DisplayText._name,
            allowBlank: false
        }];

        mFino.widget.NotificationAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            if(this.store){
                if(this.record.phantom){
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

Ext.reg("EnumEditForm", mFino.widget.EnumEditForm);

