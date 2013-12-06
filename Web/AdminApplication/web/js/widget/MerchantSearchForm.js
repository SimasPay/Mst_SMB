/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        items: [{
            xtype : 'textfield',
            vtype: 'smarttelcophone',
            fieldLabel: _('MDN'),
            maxLength : 16,
            labelSeparator : '',
            anchor : '98%',
//            width : 130,
            name: CmFinoFIX.message.JSMerchant.MDNSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'textfield',
            fieldLabel: _('First Name'),
            labelSeparator : '',
            anchor : '98%',
            maxLength:255,
//            width : 130,
            name: CmFinoFIX.message.JSMerchant.FirstNameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }
        ,{
            xtype : 'textfield',
            fieldLabel: _('Last Name'),
            labelSeparator : '',
            anchor : '98%',
            maxLength:255,
//            width : 130,
            name: CmFinoFIX.message.JSMerchant.LastNameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'textfield',
            fieldLabel: _('User Name'),
            itemId : 'merchantUsername',
            maxLength:255,
            labelSeparator : '',
            anchor : '98%',
//            width : 130,
            name: CmFinoFIX.message.JSMerchant.UsernameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype: 'enumdropdown',
            fieldLabel: _('Status'),
            labelSeparator : '',
            anchor : '98%',
//            width : 130,
            triggerAction: 'all',
            emptyText : _('<Select one..>'),
            enumId : CmFinoFIX.TagID.MDNStatus,
            name : 'merchantstatus',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype: 'enumdropdown',
            fieldLabel: _('Restrictions'),
            labelSeparator : '',
            anchor : '98%',
//            width : 130,
            triggerAction: 'all',
            emptyText : _('<Select one..>'),
            enumId : CmFinoFIX.TagID.MDNRestrictions,
            name : 'merchantrestrictions',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype:'combo',            
            fieldLabel: _('Group ID'),
            anchor : '98%',
//            width : 130,
            labelSeparator : '',
            triggerAction: "all",
            minChars : 2,
            forceSelection : true,
            pageSize : 10,
            store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSSAPGroupID),
            displayField: CmFinoFIX.message.JSSAPGroupID.Entries.DisplayText._name,
            valueField : CmFinoFIX.message.JSSAPGroupID.Entries.GroupID._name,
            hiddenName : CmFinoFIX.message.JSSAPGroupID.Entries.GroupID._name,
            name: CmFinoFIX.message.JSMerchant.ExactGroupIDSearch._name
        },
        {
            xtype: 'daterangefield',
            fieldLabel: _('Date Range'),
            labelSeparator : '',
            anchor : '98%',
//            width : 130,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }
//        {
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

    mFino.widget.MerchantSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantSearchForm, Ext.FormPanel, {

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
        mFino.widget.MerchantSearchForm.superclass.initComponent.call(this);
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
    defaultSearchHandler : function(){
        var values = this.getForm().getValues();
        if(mFino.auth.isMerchant()){
            values.ExactUsernameSearch = mFino.auth.getUsername();
            this.form.items.get('merchantUsername').setValue(values.ExactUsernameSearch);
        }
        this.fireEvent("search", values);
    },
    setMerchantUserName: function(username) {
        if(mFino.auth.isMerchant()) {
            this.form.items.get('merchantUsername').setValue("");
        } else {
            this.form.items.get('merchantUsername').setValue(username);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
