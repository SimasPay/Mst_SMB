/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CCReviewerSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('CCReviewer Search'),
        bodyStyle:'padding:5px 5px 0',
        //defaultType: 'textfield',
        items: [{
        	xtype:'textfield',
            fieldLabel: _('Username'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSUsers.UsernameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }, {
        	xtype:'textfield',
        	fieldLabel: _('First Name'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSUsers.FirstNameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            xtype:'textfield',
            fieldLabel: _('Last Name'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSUsers.LastNameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            fieldLabel: _('Status'),
            xtype:'enumdropdown',
            anchor : '95%',
            emptyText : '<Any>',
            enumId: CmFinoFIX.TagID.UserStatus,
            labelSeparator : '',
            name: CmFinoFIX.message.JSUsers.StatusSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype:'displayfield',
            labelSeparator:''
        },
        {
            xtype : 'daterangefield',
            fieldLabel: _('Registration Time'),
            anchor : '95%',
            id : 'ccRegistrationTime',
            labelSeparator : '',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'daterangefield',
            fieldLabel: _('Confirmation Time'),
            anchor : '95%',
            id : 'ccConfirmationTime',
            labelSeparator : '',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'daterangefield',
            fieldLabel: _('Activation Time'),
            anchor : '95%',
            id : 'ccActivationTime',
            labelSeparator : '',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },        
        {
            xtype : 'daterangefield',
            fieldLabel: _('Last Change Time'),
            anchor : '95%',
            id : 'ccLastChangeTime',
            labelSeparator : '',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            layout : 'column',
            items : [
            {
                columnWidth: 0.33,
                xtype:'displayfield',
                anchor:'50%',
                labelSeparator:''
            },
            {
                columnWidth: 0.33,
                xtype: 'button',
                text: _('Search'),
                style : {
                    align : 'right'
                },
                anchor:'50%',
                handler : this.searchHandler.createDelegate(this)
            },
            {
                columnWidth: 0.33,
                xtype: 'button',
                text: _('Reset'),
                anchor:'30%',
                handler : this.resetHandler.createDelegate(this)
            }
            ]
        }
//        {
//            xtype:'displayfield',
//            labelSeparator:''
//        },
//        {
//            layout : 'column',
//            items : [
////            {
////                columnWidth: 0.5,
////                xtype:'displayfield',
////                anchor:'50%',
////                labelSeparator:''
////            },
//            {
//                columnWidth: 0.5,
//                xtype: 'button',
//                text: _('Reset'),
//                anchor:'30%',
//                handler : this.resetHandler.createDelegate(this)
//            }
//            ]
//        }            
        ]
    });

    mFino.widget.CCReviewerSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CCReviewerSearchForm, Ext.FormPanel, {

    initComponent : function () {
//        this. buttons = [{
//            text: _('Search'),
//            handler : this.searchHandler.createDelegate(this)
//        },
//        {
//            text: _('Reset'),
//            handler : this.resetHandler.createDelegate(this)
//        }];
        mFino.widget.CCReviewerSearchForm.superclass.initComponent.call(this);
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
