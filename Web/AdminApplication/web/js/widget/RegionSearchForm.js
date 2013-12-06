/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RegionSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('Regions'),
        height:60,
        items:[
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:95,
            items:[
//            {
//                xtype : 'remotedropdown',
//                fieldLabel:'Company Name',
//                anchor:'90%',
//                forceSelection : true,
//                RPCObject : CmFinoFIX.message.JSCompany,
//                displayField: CmFinoFIX.message.JSCompany.Entries.CompanyName._name,
////                name: CmFinoFIX.message.JSRegion.CompanyNameSearch._name,
//                listeners   : {
//                    focus : function(){
//                        this.reload();
//                    },
//                    specialkey: this.enterKeyHandler.createDelegate(this)
//                }
//            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:75,
            items:[
            {
                xtype:'textfield',
                fieldLabel:'Region Code',
                name: CmFinoFIX.message.JSRegion.RegionCodeSearch._name,
                anchor:'95%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:80,
            items:[
            {
                xtype : "remotedropdown",
                fieldLabel: 'Region Name',
                anchor : '95%',
                RPCObject : CmFinoFIX.message.JSRegion,
                displayField: CmFinoFIX.message.JSRegion.Entries.RegionName._name,
                name: CmFinoFIX.message.JSRegion.RegionNameSearch._name,
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
            columnWidth:0.05,
            layout:'form',
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
                handler: this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });

    mFino.widget.RegionSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RegionSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.RegionSearchForm.superclass.initComponent.call(this);
        this.addEvents("regionSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("regionSearch", values);
    }
});
Ext.reg('regionsearchform',mFino.widget.RegionSearchForm);