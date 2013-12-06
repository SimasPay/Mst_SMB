/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantRegistrationPasswordForm = function (config) {
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        id: "merchantregistrationpasswordform",
        bodyStyle: 'padding:5px 5px 0',
        //defaultType: 'textfield',
//        width:450,
//        height:650,
        frame : true
    });

    mFino.widget.MerchantRegistrationPasswordForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantRegistrationPasswordForm, Ext.FormPanel, {

    initComponent : function () {

        this.labelWidth = 120;
        this.labelPad = 10;
        this.items = [
         {
            html: " * Must be at least 4 characters long. <br> * Must be no more than 40 characters long. <br> * Must contain atleast one Number.<br> <br> "
         },
        {
            xtype       : "displayfield",
            fieldLabel  : _("User Name"),
            itemId:'usernamemerchant',
            labelSeparator :'',
            allowBlank : false
        },
        {
            xtype       : "textfield",
            fieldLabel  : _("New Password"),
            itemId:'password',
            inputType   : 'password',
            labelSeparator :'',
            vtype: 'validatePassword',
            allowBlank : false
        },
        {
            xtype       : "textfield",
            fieldLabel  : _("Verify Password"),
            itemId:'verifypassword',
            inputType   : 'password',
            vtype: 'validatePassword',
            labelSeparator :'',
            allowBlank : false
//           listeners   : {
//                specialkey: this.enterKeyHandler.createDelegate(this)
//            }
        }];

        mFino.widget.MerchantRegistrationPasswordForm.superclass.initComponent.call(this);

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

            var newpassword    = this.find('itemId', "password")[0].getValue();
            var newverifypassword = this.find('itemId', "verifypassword")[0].getValue();
            if(newpassword===newverifypassword)
            {
                this.getForm().submit({
                    url: 'merchant_registration.htm',
                    waitMsg: _('Saving the contents...'),
                    reset: false,
                    params:{
                        type: 'merchantpasswordsave',
                        username: this.items.get("usernamemerchant").getValue(),
                        merchantnewpassword : newpassword,
                        merchantverifypassword : newverifypassword
                    },
                    success : function(fp, action){
                    params.hide();
                     Ext.Msg.show({
                        title: _('Info'),
                        minProgressWidth:250,
                        msg:'User Name ' + action.result.username + ' is registered successfully. Please use the registered user name to login',
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                    },
                    failure : function(fp, action){
                      Ext.Msg.show({
                        title: _('Error'),
                        minProgressWidth:250,
                        msg: action.result.Error,
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                    }
                });
            }
            else
                {
                      Ext.ux.Toast.msg(_('Failure'), _("Password Doesnot Match"));
                }
        }
    },

    setUsername: function(values)
    {
        this.items.get("usernamemerchant").setValue(values);
    }
});

Ext.reg("merchantregistrationpasswordform", mFino.widget.MerchantRegistrationPasswordForm);
