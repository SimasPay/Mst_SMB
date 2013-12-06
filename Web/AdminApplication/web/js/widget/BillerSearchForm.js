/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BillerSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        frame:true,
        title: _('Biller Search'),
        bodyStyle:'padding:5px 5px 0',
        items: [
        {
            xtype : 'textfield',
            fieldLabel: _('Biller Name'),
            labelSeparator : '',
            anchor :'98%',
            maxLength:255,
            name: CmFinoFIX.message.JSBiller.BillerNameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'numberfield',
            allowDecimals:false,
            fieldLabel: _('Biller Code'),
            labelSeparator : '',
            anchor :'98%',
            maxLength:255,
            name: CmFinoFIX.message.JSBiller.BillerCodeSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype: 'enumdropdown',
            fieldLabel: _("Biller Type"),
            enumId: CmFinoFIX.TagID.BillerType,
            mode: 'local',
            triggerAction: 'all',
            emptyText : '<Select one..>',
            anchor :'98%',
            maxLength:255,
            name: CmFinoFIX.message.JSBiller.BillerTypeSearch._name,
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
        ]
    });

    mFino.widget.BillerSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BillerSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [
        {
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }
        ];
        mFino.widget.BillerSearchForm.superclass.initComponent.call(this);
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
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    } 
});
