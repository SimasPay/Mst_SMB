/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DCTDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        id : "dctdetails",
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("DC Template Name"),
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.DistributionChainName._name               
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Created Date"),
                itemId: 'createdon',
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.CreateTime._name,
                renderer: "date"
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Modified Date"),
                itemId: 'updatedon',
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.LastUpdateTime._name,
                renderer: "date"
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Description"),
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.Description._name                
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Service"),
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.ServiceName._name                
            }            
            ]},
            {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Levels"),
                //TODO: Find out the levels form the backend
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.LevelNumber._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Created By"),
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.CreatedBy._name

            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Modified By"),
                name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.UpdatedBy._name
            }
            ]}
        ]
    });
    
    mFino.widget.DCTDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.DCTDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.DCTDetails.superclass.initComponent.call(this);
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

Ext.reg("dctDetails", mFino.widget.DCTDetails);

