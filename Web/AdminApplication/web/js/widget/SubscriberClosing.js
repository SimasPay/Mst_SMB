/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberClosing = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    });
    
    mFino.widget.SubscriberClosing.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberClosing, Ext.FormPanel, {
   
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
                         xtype : 'textfield',
                         fieldLabel:'Subscriber MDN',
                         itemId : 'subclose.form.mdnID',
                         allowBlank: false,
                         disabled: true,
                         maxLength : 15,
                         name: CmFinoFIX.message.JSSubscriberClosing.DestMDN._name
                     },{
                         xtype : 'textfield',
                         fieldLabel:'Subscriber Name',
                         itemId : 'subclose.form.name',
                         allowBlank: false,
                         disabled: true,
                         maxLength : 25,
                         name: CmFinoFIX.message.JSSubscriberClosing.DestSubscriberName._name
                     },{
                         xtype : 'textfield',
                         fieldLabel:'Transaction Id',
                         itemId : 'subclose.form.transactionId',
                         allowBlank: false,
                         disabled: true,
                         maxLength : 10,
                         name: CmFinoFIX.message.JSSubscriberClosing.SctlId._name
                     },
                     {
                         xtype : 'textarea',
                         fieldLabel:'Comment',
                         itemId : 'subclose.form.comment',
                         labelSeparator: '',
                         hieght: 70,
                         width: 200,
                         name: CmFinoFIX.message.JSSubscriberClosing.Comments._name
                     },{
                         xtype : 'textfield',
                         fieldLabel:'One Time Passcode',
                         itemId : 'subclose.form.onetimepasscode',
                         allowBlank: false,
                         inputType   : 'password',
                         maxLength : 10,
                         name: CmFinoFIX.message.JSSubscriberClosing.OneTimePassCode._name
                     }]
        }];

        mFino.widget.SubscriberClosing.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
   
    confirm: function(formWindow) {
        
    	if(this.getForm().isValid()){
            
    		this.find('itemId','subclose.form.mdnID')[0].enable();
        	this.find('itemId','subclose.form.transactionId')[0].enable();
        	this.find('itemId','subclose.form.comment')[0].enable();
    		
        	var msg= new CmFinoFIX.message.JSSubscriberClosing();
            var values = this.form.getValues();
            var subscribermdn = values[CmFinoFIX.message.JSSubscriberClosing.DestMDN._name];
            var txnId = values[CmFinoFIX.message.JSSubscriberClosing.SctlId._name];
            var otp = values[CmFinoFIX.message.JSSubscriberClosing.OneTimePassCode._name];
            var comments = values[CmFinoFIX.message.JSSubscriberClosing.Comments._name];
            
            msg.m_pDestMDN = subscribermdn;
            msg.m_pSctlId = txnId;
            msg.m_pOneTimePassCode = otp;
            msg.m_pComments = comments;
            msg.m_paction = "create";
            
            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            
            mFino.util.fix.send(msg, params);
        	}
    },
    setDetails : function(mdnID,mdnname,transactionId,comments){
        
    	this.getForm().reset();
        
    	this.form.items.get("subclose.form.mdnID").setValue(mdnID);
    	this.form.items.get("subclose.form.name").setValue(mdnname);
    	this.form.items.get("subclose.form.transactionId").setValue(transactionId);
    	this.form.items.get("subclose.form.comment").setValue(comments);
    	
    	this.find('itemId','subclose.form.mdnID')[0].disable();
    	this.find('itemId','subclose.form.name')[0].disable();
    	this.find('itemId','subclose.form.transactionId')[0].disable();
    	this.find('itemId','subclose.form.comment')[0].disable();
    }
    
});

Ext.reg("SubscriberClosing", mFino.widget.SubscriberClosing);