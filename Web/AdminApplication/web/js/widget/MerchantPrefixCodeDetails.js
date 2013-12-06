Ext.ns("mFino.widget");

mFino.widget.MerchantPrefixCodeDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        title: 'Merchant Prefix Code Details',
        frame : true,
        items: [        {
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Merchant Prefix Code"),
                name: CmFinoFIX.message.JSMerchantPrefixCode.Entries.MerchantPrefixCode._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Biller Name"),
                name: CmFinoFIX.message.JSMerchantPrefixCode.Entries.BillerName._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("VA Service Name"),
                name: CmFinoFIX.message.JSMerchantPrefixCode.Entries.VAServiceName._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Create Time"),
                renderer : 'date',
                name: CmFinoFIX.message.JSMerchantPrefixCode.Entries.CreateTime._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Created By"),
                name: CmFinoFIX.message.JSMerchantPrefixCode.Entries.CreatedBy._name
            }
            ]
        }
        ]
    });
    this.superclass().constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantPrefixCodeDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        this.superclass().initComponent.call(this);
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
Ext.reg("MerchantPrefixCodeDetails", mFino.widget.MerchantPrefixCodeDetails);