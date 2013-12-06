/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BrandDetailsDisplay = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        title: 'MNO Parameters Details',
        frame : true,
        width: 926,
        items: [        {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 200,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: 'Company Name',
                name: CmFinoFIX.message.JSBrand.Entries.CompanyName._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'International Country Code',               
                anchor : '90%',
                name: CmFinoFIX.message.JSBrand.Entries.InternationalCountryCode._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Prefix Code',                
                anchor : '75%',
                name: CmFinoFIX.message.JSBrand.Entries.PrefixCode._name
             },
            {
                xtype : 'displayfield',
                fieldLabel: 'Name',               
                anchor : '75%',
                name: CmFinoFIX.message.JSBrand.Entries.BrandName._name
             }
            ]
        }]
    });

    mFino.widget.BrandDetailsDisplay.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BrandDetailsDisplay , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.BrandDetailsDisplay.superclass.initComponent.call(this);
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

Ext.reg("BrandDetailsDisplay", mFino.widget.BrandDetailsDisplay);