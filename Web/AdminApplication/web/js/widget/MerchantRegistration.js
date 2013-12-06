/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantRegistration = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
//        width:300,
//        height:200,
//        title: _('Merchant Registration'),
        closable: true,
        resizable: false,
        plain:true
        
    });
    mFino.widget.MerchantRegistration.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantRegistration, Ext.Window, {
    initComponent : function(){
        this.labelWidth = 60;
        this.labelPad = 20;
        this.frame = true;
        this.items = [
        ];
        this.buttons = [
        {
            text  : _("Next"),
            width:50,
            id : "next",
            labelSeparator :'',
            align: 'right',
            handler: this.next.createDelegate(this)
        },
        {
            text  : _("Cancel"),
            width:50,
            id : "cancel",
            labelSeparator :'',
            align: 'right',
            handler: this.cancel.createDelegate(this)
        },
        {
            text  : _("Send"),
            width:50,
            id : "send",
            labelSeparator :'',
            align: 'right',
            handler: this.next.createDelegate(this)
        }

        ];
        this.items = [this.form];
        mFino.widget.MerchantRegistration.superclass.initComponent.call(this);
    },

    
    cancel:function()
    {
        this.hide();
    },
    next : function(){
        if(this.form.getForm().isValid()){
            this.form.checkdetails(this);
        }
    },
    send : function(){
        if(this.form.getForm().isValid()){
            this.form.checkdetails(this);
         }
    },
    setUserName: function(values){
        this.form.setUsername(values);
    },
    setMode : function(mode) {
		Ext.each(this.buttons,function(item) {
							item.hide();
							if ((mode === "forgotpassword")
									&& (item.id === "send")) {
								item.show();
							} 
						});
	}
});