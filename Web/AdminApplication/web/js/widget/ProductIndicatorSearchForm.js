/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ProductIndicatorSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        layout:'column',
        frame:true,
        title: _('Product Indicators'),
        bodyStyle:'padding:5px 5px 0',

        items: [
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:103,
            items:[
            {
                xtype : "enumdropdown",
                fieldLabel: _('Transaction Type'),
                enumId : CmFinoFIX.TagID.TransactionUICategory,
                name: CmFinoFIX.message.JSProductIndicator.TransactionTypeSearch._name,
                anchor : '95%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:103,
            items:[
            {
                xtype : 'numberfield',
                fieldLabel: _('Product Indicator'),
                anchor : '95%',
                allowDecimals:false,
                name: CmFinoFIX.message.JSProductIndicator.ProductIndicatorCodeSearch._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.10,
            layout:'form',
            items:[
            {
                xtype:'displayfield',
                anchor:'90%'
            }
            ]
        },
        {
            columnWidth:0.20,
            layout:'form',
            items:[
            {
                xtype:'button',
                text: _('Search'),
                anchor:'60%',
                handler : this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });
    mFino.widget.ProductIndicatorSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ProductIndicatorSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.ProductIndicatorSearchForm.superclass.initComponent.call(this);
        this.addEvents("ProductIndicatorSearchEvent");
        this.reloadRemoteDropDown();
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    reloadRemoteDropDown : function(){
        this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("ProductIndicatorSearchEvent", values);
    }
});