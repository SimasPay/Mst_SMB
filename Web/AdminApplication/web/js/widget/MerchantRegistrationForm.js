/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantRegistrationForm = function (config) {
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        width:300,
        height:100,
        frame : true

    });

    mFino.widget.MerchantRegistrationForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantRegistrationForm, Ext.FormPanel, {

    initComponent : function () {

        this.labelWidth = 60;
        this.labelPad = 10;
        this.items = [{
            xtype : 'textfield',
            vtype: 'smarttelcophoneAdd',
            fieldLabel  : _("MDN"),
            itemId:'merchantmdn1',
            labelSeparator :'',
            maxLength : 16,
            allowBlank : false
        },
        {
            xtype       : "textfield",
            fieldLabel  : _("Pin"),
            itemId:'merchantpin1',
            labelSeparator :'',
            inputType   : 'password',
            allowBlank : false
        //            listeners   : {
        //                specialkey: this.enterKeyHandler.createDelegate(this)
        //            }
        }];

        mFino.widget.MerchantRegistrationForm.superclass.initComponent.call(this);

        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.checkdetails();
        }
    },
    checkdetails : function(params)
    {
        if(this.getForm().isValid()){
            this.getForm().submit({
                url: 'merchant_registration.htm',
                waitMsg: _('Checking the details...'),
                reset: false,
                params:{
                    type: 'merchantmdnandpin',
                    merchantmdn1: this.find('itemId', "merchantmdn1")[0].getValue(),
                    merchantpin1: this.find('itemId', "merchantpin1")[0].getValue()
                },
                success : function(fp, action){
                    params.hide();
                    var  config =  {
                        bodyStyle         : "padding:5px;",
                        frame             : true,
                        dataUrl:'merchant_registration.htm'
                    };
                    var mvalidation = new mFino.widget.MerchantRegistration({
                        form : new mFino.widget.MerchantRegistrationPasswordForm(config),
                        width:350,
                        height:240
                    });
                    mvalidation.setUserName(action.result.username);
                    mvalidation.show();
                   
                },
                failure : function(fp, action){
                    Ext.Msg.show({
                        title: _('Error'),
                        minProgressWidth:250,
                        msg: action.result.Error,
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                    params.form.getForm().reset();
                }

            });
        }
    }
});

Ext.reg("merchantregistrationform", mFino.widget.MerchantRegistrationForm);
