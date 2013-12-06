Ext.ns("mFino.widget");

mFino.widget.MerchantPrefixCodeSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('Merchant Prefix Codes'),
        height:60,
        items:[
        {
            columnWidth:0.35,
            layout:'form',
            labelWidth: 112,
            items:[
            {
                xtype: 'numberfield',
                allowDecimals:false,
                fieldLabel: _('Merchant Prefix Code'),
                anchor:'90%',
                name : CmFinoFIX.message.JSMerchantPrefixCode.MerchantPrefixCodeSearch._name,
                listeners : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth: 90,
            items:[
            {
                xtype : 'textfield',
                fieldLabel: _("Biller Name"),
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantPrefixCode.BillerNameSearch._name,
                listeners : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.05,
            layout:'form',
            labelWidth:50,
            items:[
            {
                xtype:'displayfield',
                anchor:'90%'
            }
            ]
        },{
            columnWidth:0.20,
            layout:'form',
            items:[
            {
                xtype:'button',
                text:'Search',
                anchor:'60%',
                handler : this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });
    this.superclass().constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantPrefixCodeSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this.superclass().initComponent.call(this);
        this.addEvents("merchantPrefixCodeSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("merchantPrefixCodeSearch", values);
    }
});
Ext.reg('MerchantPrefixCodeSearchForm',mFino.widget.MerchantPrefixCodeSearchForm);