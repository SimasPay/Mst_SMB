/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CreditCardSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px',
        width: 690,
        height:105,
        frame:true,
        border:true,
        labelWidth : 90,
        labelPad : 10,
        items: [{
            layout:'column',
            border:false,
            items:[{
                columnWidth:0.33,
                layout: 'form',
                labelWidth : 90,
                border:false,
                items: [
                {
                    xtype:'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('CC Txn ID'),
                    anchor: '95%',
                    name: CmFinoFIX.message.JSCreditCardTransaction.IDSearch._name,
                    listeners : {
                        specialkey: this.enterKeyHandler.createDelegate(this)
                    }
                },
                {
                    xtype:'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('DestinationMDN'),
                    anchor: '95%',
                    name: CmFinoFIX.message.JSCreditCardTransaction.DestMDNSearch._name,
                    listeners : {
                        specialkey: this.enterKeyHandler.createDelegate(this)
                    }
                }
                ]
            },
            {
                columnWidth:0.33,
                layout: 'form',
                labelWidth : 70,
                border:false,
                items: [
                {
                    xtype:'enumdropdown',
                    fieldLabel: _('Trans Status'),
                    labelSeparator : '',
                    anchor :'90%',
                    emptyText : _('<select one..>'),
                    enumId : CmFinoFIX.TagID.TransStatus,
                    name: CmFinoFIX.message.JSCreditCardTransaction.TransStatusSearch._name,
                    listeners : {
                        specialkey: this.enterKeyHandler.createDelegate(this)
                    }
                },
                {
                    xtype : "enumdropdown",
                    fieldLabel: _('Operation'),
                    labelSeparator : '',
                    anchor :'90%',
                    emptyText : _('<select one..>'),
                    enumId : CmFinoFIX.TagID.Operation,
//                    itemId:"transactionSearchFormAction",
                    name: CmFinoFIX.message.JSCreditCardTransaction.OperationSearch._name,
                    listeners   : {
                        specialkey: this.enterKeyHandler.createDelegate(this)
                    }
                }
                ]
            },
            {
                columnWidth:0.33,
                layout: 'form',
                border:false,
                items: [
                {
                    xtype : 'daterangefield',
                    fieldLabel: _('Transaction Date'),
                    labelSeparator : '',
                    anchor :'90%',
                    id: "ccdaterange",
                    listeners : {
                        specialkey: this.enterKeyHandler.createDelegate(this)
                    }
                },
                {
                	  xtype : 'daterangefield',
                      fieldLabel: _('LastUpdate Date'),
                      labelSeparator : '',
                      anchor :'90%',
                      id: "cclastupdatedaterange",
                      listeners : {
                          specialkey: this.enterKeyHandler.createDelegate(this)
                      }
                }
                ]
            }
            ]
        }]
    });

    mFino.widget.CreditCardSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CreditCardSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this.buttons = [
        {
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        },
        ' ',' ', ' '
        ];
        mFino.widget.SubscriberSearchForm.superclass.initComponent.call(this);
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            if(values.OperationSearch==="undefined")
            	values.OperationSearch=null;
            if(values.TransStatusSearch==="undefined")
            	values.TransStatusSearch=null;
            this.fireEvent("creditCardSearch", values);
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
