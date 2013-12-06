/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChangePassword = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id                : "changePasswordDialog",
        //frame             : true,
        height            : 150,
        width             : 300,
        labelWidth        : 120,
        items   : this.buildItems(),
        buttons : this.buildButtons()
    });

    mFino.widget.ChangePassword.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChangePassword, Ext.FormPanel, {
    initComponent : function () {
        mFino.widget.ChangePassword.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    removeOldPasswordItem : function(){
    	var item = this.getComponent("oldpassword");
        this.remove(item);
    },
    
    removeTransactionPinItem : function(){
        var item = this.getComponent("transactionPin");
        this.remove(item);
    },
    
    hideCancelButton : function(){    	
    	Ext.each(this.form.buttons,function(button){
    		if(button.text == 'Cancel'){
    			button.hide();
    		}
    	},this)
    },

    buildItems : function(){
        return [

		 new Ext.form.TextField({
            fieldLabel: _("Current Password"),
            id: 'oldpassword',
            name: 'oldpassword',
            allowBlank: false,
            xtype: 'textfield',
            inputType: 'password',
//            vtype: 'validatePassword',
            width: 150,
            maxLength: 40
        }),
        new Ext.form.TextField({
            fieldLabel: _("New Password"),
            id: 'newpassword',
            name: 'newpassword',
            allowBlank: false,
            xtype: 'textfield',
            inputType: 'password',
            vtype: 'validatePassword',
            notEqualField: 'oldpassword',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            },
            width: 150,
            maxLength: 40
        }),
        new Ext.form.TextField({
            fieldLabel: _("Confirm Password"),
            id: 'passwordConfirm',
            name: 'passwordConfirm',
            xtype: 'textfield',
            inputType: 'password',
            vtype: 'validatePassword',
            allowBlank: false,
            width: 150,
            maxLength: 40,
            initialPasswordField: 'newpassword',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
        }),
        new Ext.form.TextField({
            fieldLabel: _("New Transaction Pin"),
            id: 'transactionPin',
            name: 'transactionPin',
            xtype: 'textfield',
            inputType: 'password',
            vtype: 'pin',
            allowBlank: false,
            width: 150,
            maxLength: 40,
            initialPasswordField: 'newpassword',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
        })

        ];
    },

    buildButtons : function(){
        return [
        new Ext.Button({
            text        : ("ChangePassword"),
            listeners   : {
                click   : this.changePassword.createDelegate(this)
            }
        }),
        new Ext.Button({
            text        : ("Cancel"),
            listeners   : {
                click   : this.cancel.createDelegate(this)
            }
        })
        ];
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.changePassword();
        }
    },
    
    cancel : function(){
    	var win = Ext.getCmp('changePasswordWindow');
    	win.close();
    },

    changePassword : function(){
        var form = Ext.getCmp('changePasswordDialog').getForm();
        if(form.isValid()){
            var password    = Ext.getCmp('newpassword').getValue();
            var oldpassword    = Ext.getCmp('oldpassword')?Ext.getCmp('oldpassword').getValue():""; // added for #2311
            var verifyPassword = Ext.getCmp('passwordConfirm').getValue();
            if(password===verifyPassword){
                form.submit({
                    url: "changepasswordrequest.htm",
                    reset: false,
                    params:{
                        type: 'passwordsave',
                        newpassword : password,
                        verifypassword : verifyPassword,
                        oldpassword : oldpassword,
                        oldpasswordRequired : Ext.getCmp('oldpassword')? "true":"false", // added for #2311
                        transactionPinRequired : Ext.getCmp('transactionPin')? "true":"false"	
                    },
                    success: function(form, action) {
                        Ext.Msg.show({
                            title: _('Info'),
                            minProgressWidth:250,
                            msg:'Your Password Change is Sucessfull, Please Re-Login with your New Password',
                            buttons: Ext.MessageBox.OK,
                            multiline: false,
                            fn: function(btn) {
                            	var win = Ext.getCmp('changePasswordWindow');
                            	win.close();
                            	var currentUrl = window.location.href;
                            	var newUrl = currentUrl.substring(0,currentUrl.indexOf('index.htm')) + 'j_spring_security_logout';
                            	window.location.href = newUrl;
                            }
                        });
                    },
                    failure: function(form, action){
                        if(action.result){
                            if(action.result.message){
                                Ext.MessageBox.alert(_("Error"), action.result.message);
                            }
                            else{
                                Ext.MessageBox.alert(_("ChangePassword Error"));
                            }
                        }else{
                            Ext.MessageBox.alert(_("ChangePassword Error"), _("Server Error"));
                        }
                    }
                });
            }else{
                Ext.getCmp('changePasswordDialog').getForm().reset();
                
            }
        }
    }
});

Ext.reg("changepassword", mFino.widget.ChangePassword);
