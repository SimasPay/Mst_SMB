/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSCodeSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('SMS Codes'),
        height:60,
        items:[
        {
            columnWidth:0.2,
            layout:'form',
            labelWidth:50,
            items:[
            {
                xtype : 'enumdropdown',
                fieldLabel: _("Status"),
                enumId : CmFinoFIX.TagID.SMSCodeStatus,
                anchor : '95%',
                name : CmFinoFIX.message.JSSMSCode.SMSCodeStatusSearch._name,
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
                xtype:'textfield',
                fieldLabel:'Code',
                anchor:'90%',
                name : CmFinoFIX.message.JSSMSCode.SMSCodeSearch._name,
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

    mFino.widget.SMSCodeSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSCodeSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.SMSCodeSearchForm.superclass.initComponent.call(this);
        this.addEvents("smsCodeSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("smsCodeSearch", values);
    }
});
Ext.reg('SMSCodeSearchForm',mFino.widget.SMSCodeSearchForm);