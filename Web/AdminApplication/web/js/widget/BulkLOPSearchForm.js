/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkLOPSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Bulk LOP Search'),
        bodyStyle:'padding:5px 5px 0',

        items: [
        {
            xtype : 'numberfield',
            allowDecimals:false,
            fieldLabel: _('LOP ID'),
            labelSeparator : '',
            anchor : '98%',
            maxLength:16,
            minValue:0,
            name: CmFinoFIX.message.JSBulkLOP.IDSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'textfield',
            fieldLabel: _('User Name'),
            labelSeparator : '',
            anchor : '98%',
            maxLength:255,
            name: CmFinoFIX.message.JSBulkLOP.UsernameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype: 'enumdropdown',
            fieldLabel: _('LOP Status'),
            labelSeparator : '',
            anchor : '98%',
            triggerAction: 'all',
            emptyText : _('<Select one...>'),
            enumId : CmFinoFIX.TagID.LOPStatus,
            name: CmFinoFIX.message.JSBulkLOP.LOPStatusSearch._name,
            value : CmFinoFIX.LOPStatus.Pending,
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
    mFino.widget.BulkLOPSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkLOPSearchForm, Ext.FormPanel, {

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
        mFino.widget.BulkLOPSearchForm.superclass.initComponent.call(this);
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
