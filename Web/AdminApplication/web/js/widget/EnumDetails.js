/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.EnumDetails = function (config)
{
    var localConfig = Ext.apply({}, config);
 
    localConfig = Ext.applyIf(localConfig, {
        id : "EnumDetails",
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 90,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("ID"),
                name: CmFinoFIX.message.JSEnumText.Entries.ID._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Tag ID"),
                name: CmFinoFIX.message.JSEnumText.Entries.TagID._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Tag Name"),
                name: CmFinoFIX.message.JSEnumText.Entries.TagName._name
            }
            ]},
            {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 90,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Enum Code"),
                name: CmFinoFIX.message.JSEnumText.Entries.EnumCode._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Field Name"),
                name: CmFinoFIX.message.JSEnumText.Entries.EnumValue._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Language"),
                name: CmFinoFIX.message.JSEnumText.Entries.LanguageText._name
            }
            ]},
            {
                columnWidth: 1.0,
                layout: 'form',
                labelWidth : 75,
                items : [
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Display Text'),
                    anchor : '100%',
                    name: CmFinoFIX.message.JSEnumText.Entries.DisplayText._name
                }
                ]
            }
        ]
    });
    
    mFino.widget.EnumDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.EnumDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.EnumDetails.superclass.initComponent.call(this);
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

Ext.reg("EnumDetails", mFino.widget.EnumDetails);

