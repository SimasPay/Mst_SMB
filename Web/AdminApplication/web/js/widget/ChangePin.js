/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChangePin = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
//        bodyStyle: 'padding:5px 5px 0',
//        defaultType: 'textfield',
//        frame : true
    });
    mFino.widget.ChangePin.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChangePin, Ext.FormPanel, {
   
    initComponent : function () {
	 	this.labelWidth = 120;
	 	this.labelPad = 20;
	 	this.autoScroll = true;
	 	this.frame = true;
        this.items = [
        {
        	layout: 'form',
            items : [
					{
						xtype: 'displayfield'
					},
					{
						xtype: 'textfield',
						fieldLabel:'Current Pin',
					    itemId : 'oldpin',
			            inputType: 'password',
			            vtype: 'pin',
			            allowBlank: false,
//					    anchor : '100%',
					    name: CmFinoFIX.message.JSChangePin.OldPin._name
					},
					{
						xtype: 'textfield',
						fieldLabel:'New Pin',
						itemId : 'newpin',
			            inputType: 'password',
			            vtype: 'pin',
			            notEqualField:'changepin.oldpin',
			            allowBlank: false,
//					    anchor : '100%',
					    name: CmFinoFIX.message.JSChangePin.NewPin._name
					},
					{
						xtype: 'textfield',
						fieldLabel:'Confirm Pin',
						itemId : 'confirmpin',
			            inputType: 'password',
			            vtype: 'pin',
			            allowBlank: false,
//					    anchor : '100%',
					    name: CmFinoFIX.message.JSChangePin.ConfirmPin._name
					} ]
        }];

        mFino.widget.ChangePin.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
   
    change: function(formWindow) {
        if(this.getForm().isValid()){
        	 var values = this.form.getValues();
        	if(values.OldPin===values.NewPin){
        		Ext.ux.Toast.msg(_("Error"), "New pin must not match oldpin.");
        		var newPin = this.form.items.get("newpin");
        		newPin.pinText = "New pin must not match oldpin.";
        	}
        	else if(values.ConfirmPin!=values.NewPin){
        		Ext.ux.Toast.msg(_("Error"), "Confirmation does not match your new pin.");
        		var confirm = this.form.items.get("confirmpin");
        		confirm.pinText = "Confirmation does not match your new pin .";
        	}
        	else{
        	formWindow.disable();
        	
            var msg= new CmFinoFIX.message.JSChangePin();
            msg.m_pMDN = this.record.get(CmFinoFIX.message.JSPartner.Entries.MDN._name);
            msg.m_pOldPin = values.OldPin;
            msg.m_pNewPin = values.NewPin;
            msg.m_pConfirmPin = values.ConfirmPin;
            var params = {
                    success : function(response){
                    	formWindow.enable();
                    	if(response.m_psuccess){
                        	 formWindow.hide();
                             formWindow.form.getForm().reset();
                        	Ext.ux.Toast.msg(_(""), "ChangePin Successful");
                        }else{
                        	formWindow.form.getForm().reset();
                        	Ext.ux.Toast.msg(_(""), response.m_pErrorDescription);
                        }
                    },
                    failure : function(response){
                    	formWindow.enable();
                    	formWindow.form.getForm().reset();
                    	Ext.ux.Toast.msg(_(""), "ChangePin failed");
                    }
                };
            mFino.util.fix.send(msg, params);
        }
        }
    },
    setRecord : function(record){
       this.record = record;
    }
    
});

Ext.reg("changepin", mFino.widget.ChangePin);

