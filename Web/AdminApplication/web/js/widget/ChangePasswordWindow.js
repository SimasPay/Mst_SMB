/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChangePasswordWindow = Ext.extend(Ext.Component,{ //added for #2311
	tpl: new Ext.Template(
            '<div class="box_frame" style="width:500px;background-color:white;padding:15px 0px 15px 30px;">',
                    '<h2 align="left">Please change your password</h2>',
                    '<ul>',
                        '<li>Keep your new password secure. If must write it down, be sure to keep it in a safe place. </li>',
                        '<li>Your new password must meet the following requirements :   </li>',
                    '</ul>',
                    '<div style="padding:0px 0px 5px 30px;">',
                    '<ul>',
                        '<li>* Must be at least 6 characters long. </li>',
                        '<li>* Must be no more than 40 characters long. </li>',
                        '<li>* Must contain atleast one Number. </li>',
                        '<li>* Must contain atleast one capital letter. </li>',
                        '<li>* Password will be valid for 90 days. </li>',
                    '</ul>',
                    '</div>',
                    '<div id="changePasswordPanel"></div>',
             '</div>'
			),
	onRender : function(ct,position){
		if(!this.el){
		   this.el = document.createElement('div');
		}
		this.tpl.overwrite(this.el,{});
		mFino.widget.ChangePasswordWindow.superclass.onRender.call(this, ct, position);
	},
	afterRender : function(){
		mFino.widget.ChangePasswordWindow.superclass.afterRender.call(this);
		this.changePassword = new mFino.widget.ChangePassword({
			applyTo: Ext.get('changePasswordPanel')
		});
		if(!this.initialConfig.oldPasswordRequired){
			this.changePassword.removeOldPasswordItem();
		}
		if(this.initialConfig.promptPin != 'true'){
			this.changePassword.removeTransactionPinItem();
		}	
		if(this.initialConfig.removeCancelButton){
			this.changePassword.hideCancelButton();
		}
	}
});

Ext.reg("changepasswordwindow", mFino.widget.ChangePasswordWindow);