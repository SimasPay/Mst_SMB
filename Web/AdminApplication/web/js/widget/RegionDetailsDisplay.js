/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RegionDetailsDisplay = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        title: 'Region Details',
        frame : true,
        width: 926,
        items: [        {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: 'Company Name',
                name: CmFinoFIX.message.JSRegion.Entries.CompanyName._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Region Code',
                anchor : '75%',
                name: CmFinoFIX.message.JSRegion.Entries.RegionCode._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Region Name',
                anchor : '75%',                
                name: CmFinoFIX.message.JSRegion.Entries.RegionName._name
                }
            ]
        }]
    });

    mFino.widget.RegionDetailsDisplay.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RegionDetailsDisplay , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.RegionDetailsDisplay.superclass.initComponent.call(this);
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

Ext.reg("RegionDetailsDisplay", mFino.widget.RegionDetailsDisplay);