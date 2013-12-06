/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CashOut = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
//        bodyStyle: 'padding:5px 5px 0',
//        defaultType: 'textfield',
//        frame : true
    });
    this.cashoutregistered = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.CashOutRegistered(config),
        title : _("CashOut Approval"),
        height : 200,
        width:350,
        mode:"cashout"
    },config));
    this.cashoutunregistered = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.CashOutUnregistered(config),
        title : _("CashOut Unregistered Subscriber"),
        height : 250,
        width:350,
        mode:"cashout"
    },config));
    mFino.widget.CashOut.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CashOut, Ext.FormPanel, {
   
    initComponent : function () {
	 	this.labelWidth = 120;
	 	this.labelPad = 20;
	 	this.autoScroll = true;
	 	this.frame = true;
	 	this.items = [{
	 	              	layout: 'form',
					 	 items:[  {
		  		                xtype:'displayfield',
		  		                labelSeparator:''
					 	 		}, 
					 	 		{
					 	 			
					                xtype:'button',
					                text:'Registered  Subscriber ',
					                handler: this.registered.createDelegate(this)
					            },
					            {
			  		                xtype:'displayfield',
			  		                labelSeparator:''
			  		            },
					            {
					                xtype:'button',
					                text:'UnRegistered Subscriber',
					                handler: this.unregistered.createDelegate(this)
					            }]
	 	}];
        mFino.widget.CashIn.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
     reset : function(){
    	 this.getForm().reset();
     },
     registered : function(){
    	 this.cashoutregistered.form.reset();
    	 this.cashoutregistered.show();
    	 this.formwindow.hide();
     },
     unregistered : function(){
    	 this.cashoutunregistered.form.reset();
    	 this.cashoutunregistered.show();
    	 this.formwindow.hide();
     },
     setFormWindow : function(formwindow){
    	 this.formwindow = formwindow;
     }
});

Ext.reg("cashout", mFino.widget.CashOut);

