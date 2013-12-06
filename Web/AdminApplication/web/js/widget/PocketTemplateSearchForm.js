/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketTemplateSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Pocket Template Search'),
        bodyStyle:'padding:5px 5px 0',
        items: [
        {
            xtype : 'textfield',
            fieldLabel: _('Description'),
            labelSeparator : '',
            anchor:'98%',
            maxLength:255,
            name: CmFinoFIX.message.JSPocketTemplate.DescriptionSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype: 'enumdropdown',
            labelWidth : 100,
            fieldLabel: _('Pocket Type'),
            labelSeparator : '',
			emptyText : _('<select one..>'),
            anchor:'98%',
            enumId : CmFinoFIX.TagID.PocketType,
            name: CmFinoFIX.message.JSPocketTemplate.PocketTypeSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }

        },
        {
            xtype: 'enumdropdown',
            fieldLabel: _('Commodity Type'),
            labelSeparator : '',
			emptyText : _('<select one..>'),
            anchor:'98%',
            enumId : CmFinoFIX.TagID.Commodity,
            name: CmFinoFIX.message.JSPocketTemplate.CommodityTypeSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'daterangefield',
            fieldLabel: _('Date range'),
            labelSeparator : '',
            anchor :'98%',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }
//        ,{
//            layout : 'column',
//            labelWidth : 200,
//            items : [{
//                columnWidth: 1,
//                layout : 'form',
//                items : [{
//                    xtype:'displayfield',
//                    anchor: '95%',
//                    fieldLabel: _('Date Range'),
//                    labelSeparator:''
//                }]
//            },
//            {
//                columnWidth: 1,
//                layout : 'form',
//                labelWidth : 20,
//                items:[
//                {
//                    xtype: 'daterangefield',
//                    anchor : '95%',
//                    //  width : 200,
//                    listeners   : {
//                        specialkey: this.enterKeyHandler.createDelegate(this)
//                    }
//                }
//                ]
//            }
//            ]
//        }
        ]
    });

    mFino.widget.PocketTemplateSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTemplateSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        mFino.widget.PocketTemplateSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },

    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            this.fireEvent("search", values);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
