/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantCodeSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('Merchant Codes'),
        height:60,
        items:[
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth:100,
            items:[
            {
                xtype: 'textfield',
                allowDecimals:false,
                fieldLabel: _('Merchant Code'),
                anchor:'90%',
                name : CmFinoFIX.message.JSMerchantCode.MerchantCodeSearch._name,
                listeners : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.2,
            layout:'form',
            labelWidth:50,
            items:[
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _("MDN"),
                vtype: 'smarttelcophone',
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantCode.MDNSearch._name,
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

    mFino.widget.MerchantCodeSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantCodeSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.MerchantCodeSearchForm.superclass.initComponent.call(this);
        this.addEvents("merchantCodeSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("merchantCodeSearch", values);
    }
});
Ext.reg('MerchantCodeSearchForm',mFino.widget.MerchantCodeSearchForm);