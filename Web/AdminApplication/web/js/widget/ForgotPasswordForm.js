/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ForgotPasswordForm = function (config) {
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        width:300,
        height:100,
        frame : true

    });

    mFino.widget.ForgotPasswordForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ForgotPasswordForm, Ext.FormPanel, {

    initComponent : function () {

        this.labelWidth = 60;
        this.labelPad = 10;
        this.items = [{
            xtype : 'textfield',
            allowBlank: false,
            fieldLabel  : _("UserName"),
            itemId:'username',
            labelSeparator :'',
            maxLength : 100,
            allowBlank : false
        }];

        mFino.widget.ForgotPasswordForm.superclass.initComponent.call(this);

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
                url: 'forgotpassword.htm',
                waitMsg: _('Sending Reset link...'),
                reset: false,
                params:{
                    type: 'forgotpassword',
                    username: this.find('itemId', "username")[0].getValue()
                 },
                success : function(fp, action){
                	params.hide();
                	 Ext.Msg.show({
                         title: _('Info'),
                         minProgressWidth:250,
                         msg:'Reset Password link sent to your email id ',
                         buttons: Ext.MessageBox.OK,
                         multiline: false
                     });
                },
                failure : function(fp, action){
                	params.hide();
                	 Ext.Msg.show({
                         title: _('Info'),
                         minProgressWidth:250,
                         msg:action.result.message,
                         buttons: Ext.MessageBox.OK,
                         multiline: false
                     });
                }

            });
        }
    }
});

Ext.reg("forgotpasswordform", mFino.widget.ForgotPasswordForm);
