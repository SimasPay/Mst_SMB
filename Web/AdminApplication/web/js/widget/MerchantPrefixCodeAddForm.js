Ext.ns("mFino.widget");

mFino.widget.MerchantPrefixCodeAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });
    this.superclass().constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantPrefixCodeAddForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [
        {
            layout: 'form',
            autoHeight: true,
            items : [
            {
                xtype : 'textfield',
                fieldLabel: _("Biller Name"),
                allowBlank: false,
                blankText : _('Biller Name is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantPrefixCode.Entries.BillerName._name
            },
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _("Merchant Prefix Code"),
                allowBlank: false,
                blankText : _('Merchant Prefix Code is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantPrefixCode.Entries.MerchantPrefixCode._name
            },
            {
                xtype : 'enumdropdown',
                fieldLabel: _("VA Service Name"),
                allowBlank: false,
                enumId : CmFinoFIX.TagID.VAServiceName,
                blankText : _('VA Service Name is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantPrefixCode.Entries.VAServiceName._name
            }
            ]
        }
        ];
        this.superclass().initComponent.call(this);
        markMandatoryFields(this.form);
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            if(this.store){
                if(this.record.phantom && !(this.record.store)){
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

Ext.reg("MerchantPrefixCodeAddForm", mFino.widget.MerchantPrefixCodeAddForm);