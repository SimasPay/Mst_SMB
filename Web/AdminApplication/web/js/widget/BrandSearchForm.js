/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BrandSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('MNO Parameters'),
        height:60,
        items:[
       /* {
            columnWidth:0.25,
            layout:'form',
            labelWidth:95,
            items:[
//            {
//                xtype: 'remotedropdown',
//                fieldLabel :_('Company Name'),
//                anchor : '95%',
//                RPCObject : CmFinoFIX.message.JSCompany,
//                displayField: CmFinoFIX.message.JSCompany.Entries.CompanyName._name,
//                valueField: CmFinoFIX.message.JSCompany.Entries.ID._name,
////                hiddenName: CmFinoFIX.message.JSBrand.CompanyIDSearch._name,
//                name: CmFinoFIX.message.JSBrand.CompanyNameSearch._name,
//                listeners   : {
//                    focus : function(){
//                        this.reload();
//                    },
//                    specialkey: this.enterKeyHandler.createDelegate(this)
//                }
//            }
            ]
        },*/
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth:70,
            items:[
            {
                xtype: 'remotedropdown',
                fieldLabel:'Prefix Code',
                RPCObject : CmFinoFIX.message.JSBrand,
                displayField: CmFinoFIX.message.JSBrand.Entries.PrefixCode._name,
                name: CmFinoFIX.message.JSBrand.PrefixCodeSearch._name,
                anchor:'90%',
                listeners   : {
                    focus : function(){
                        this.reload();
                    },
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth:75,
            items:[
            {
                xtype: 'remotedropdown',
                fieldLabel:'Name',
                RPCObject : CmFinoFIX.message.JSBrand,
                displayField: CmFinoFIX.message.JSBrand.Entries.BrandName._name,
                name: CmFinoFIX.message.JSBrand.BrandNameSearch._name,
                anchor:'95%',
                listeners   : {
                    focus : function(){
                        this.reload();
                    },
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
       /* {
            columnWidth:0.05,
            layout:'form',
            items:[
            {
                xtype:'displayfield',
                anchor:'90%'
            }
            ]
        },*/
        {
            columnWidth:0.20,
            layout:'form',
            items:[
            {
                xtype:'button',
                text:'Search',
                anchor:'60%',
                handler: this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });

    mFino.widget.BrandSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BrandSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.BrandSearchForm.superclass.initComponent.call(this);
        this.addEvents("brandSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },   
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("brandSearch", values);
    }
});
Ext.reg('brandsearchform',mFino.widget.BrandSearchForm);