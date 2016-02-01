/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.Login = function (config) {
    
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id                : "loginDialog",
        bodyStyle         : "padding:5px;",
        frame             : true,
        title             : ("Login "),
        height            : 150,
        width             : 310,
        labelWidth        : 140,
        items   : this.buildItems(),
        buttons : this.buildButtons()
    });
    mFino.widget.Login.superclass.constructor.call(this, localConfig);

};

Ext.extend(mFino.widget.Login, Ext.FormPanel, {
    initComponent : function () {
        mFino.widget.Login.superclass.initComponent.call(this);
    },

    buildItems : function(){
        return [
        new Ext.form.TextField({
            xtype       : "textfield",
            fieldLabel  : _("Username"),
            labelSeparator :'',
            name        : "j_username",
            allowBlank : false,
            value	: "user",
            width:140,
            blankText : _('User name is required'),
            listeners   : {
                render      : function (el) {
                    el.focus('', 1500);
                },
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }),
        new Ext.form.TextField({
            xtype       : "textfield",
            fieldLabel  : _("Password"),
            labelSeparator :'',
            name        : 'j_password',
            inputType   : 'password',
            allowBlank : false,
            value		: "User123",
            width:140,
            blankText : _('Password is required'),
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        })
        ,
        {
            html: '<a href="#"  style="color:blue"  onclick="mFino.widget.Login.prototype.onforgotpassword();">Forgot Password  </a>'
        }
               /* new Ext.form.Checkbox({
                    fieldLabel  : ("Remember me"),
                    name        : '_spring_security_remember_me',
                    inputType   : 'checkbox',
                    allowBlank  : true
                })*/
        ];
    },
    onmerchantregistration: function()
    {
        var  config =  {
            bodyStyle         : "padding:5px;",
            frame             : true,
            dataUrl:'merchant_registration.htm'
        };
        var mreg = new mFino.widget.MerchantRegistration({
            form : new mFino.widget.MerchantRegistrationForm(config)
        });
        mreg.show();
    },
    onforgotpassword: function()
    {
        var  config =  {
            bodyStyle         : "padding:5px;",
            frame             : true,
            dataUrl:'forgotpassword.htm'
        };
        var forgotpassword = new mFino.widget.MerchantRegistration({
        	 title: _('Send Reset Password Link'),
        	 width:300,
        	 height:130,
            form : new mFino.widget.ForgotPasswordForm(config)
        });
        forgotpassword.setMode("forgotpassword");
        forgotpassword.show();
    },
    buildButtons : function(){
        return [
        new Ext.Button({
            text        : _("Login"),
            
            listeners   : {
                click   : this.submitLogin.createDelegate(this)
            }
        })
        ];
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.submitLogin();
        }
    },
    
    submitLogin : function(){
        var form = Ext.getCmp('loginDialog').getForm();
        if(form.isValid()){

            form.submit({
                url: "j_spring_security_check",
                //                waitMsg: "... ...",
                //                waitTitle: ("Login now"),
                success: function(form, action) {
                    window.location = action.result.url;
                },
                failure: function(form, action){
                    if(action.result){
                        if(action.result.message){
                            Ext.MessageBox.alert(_("Login Error"), action.result.message);
                        }
                        else{
                            Ext.MessageBox.alert(_("Login Error"), action.result.message);
                        }
                    } else{
                        var responseText = action.response.responseText;
                	var statusText = action.response.statusText;
                        var re = new RegExp("Authentication Failed.*</h1>");
                        var resultToDisplay = re.exec(responseText) || statusText;
                        Ext.MessageBox.alert(_("Login Error"), _(resultToDisplay));
                    }
                    form.reset();
                }
            });
        }
    }
});

Ext.reg("logindialog", mFino.widget.Login);
