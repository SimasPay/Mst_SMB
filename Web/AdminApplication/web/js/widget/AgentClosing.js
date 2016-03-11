/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AgentClosing = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    });
    
    mFino.widget.AgentClosing.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AgentClosing, Ext.FormPanel, {
   
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
                         fieldLabel:'Agent MDN',
                         itemId : 'agentclose.form.mdnID',
                         allowBlank: false,
                         disabled: true,
                         maxLength : 15,
                         name: CmFinoFIX.message.JSAgentClosing.DestMDN._name
                     },{
                         xtype : 'textfield',
                         fieldLabel:'Agent Name',
                         itemId : 'agentclose.form.name',
                         allowBlank: false,
                         disabled: true,
                         maxLength : 25,
                         name: CmFinoFIX.message.JSAgentClosing.DestSubscriberName._name
                     },{
                         xtype : 'textfield',
                         fieldLabel:'Transaction Id',
                         itemId : 'agentclose.form.transactionId',
                         allowBlank: false,
                         disabled: true,
                         maxLength : 10,
                         name: CmFinoFIX.message.JSAgentClosing.SctlId._name
                     },
                     {
                         xtype : 'textarea',
                         fieldLabel:'Comment',
                         itemId : 'agentclose.form.comment',
                         labelSeparator: '',
                         hieght: 70,
                         width: 200,
                         name: CmFinoFIX.message.JSAgentClosing.Comments._name
                     },{
                         xtype : 'textfield',
                         fieldLabel:'One Time Passcode',
                         itemId : 'agentclose.form.onetimepasscode',
                         allowBlank: false,
                         inputType   : 'password',
                         maxLength : 10,
                         name: CmFinoFIX.message.JSAgentClosing.OneTimePassCode._name
                     }]
        }];

        mFino.widget.AgentClosing.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
   
    confirm: function(formWindow) {
        
    	if(this.getForm().isValid()){
            
    		this.find('itemId','agentclose.form.mdnID')[0].enable();
        	this.find('itemId','agentclose.form.transactionId')[0].enable();
        	this.find('itemId','agentclose.form.comment')[0].enable();
    		
        	var msg= new CmFinoFIX.message.JSAgentClosing();
            var values = this.form.getValues();
            var subscribermdn = values[CmFinoFIX.message.JSAgentClosing.DestMDN._name];
            var txnId = values[CmFinoFIX.message.JSAgentClosing.SctlId._name];
            var otp = values[CmFinoFIX.message.JSAgentClosing.OneTimePassCode._name];
            var comments = values[CmFinoFIX.message.JSAgentClosing.Comments._name];
            
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
        
    	this.form.items.get("agentclose.form.mdnID").setValue(mdnID);
    	this.form.items.get("agentclose.form.name").setValue(mdnname);
    	this.form.items.get("agentclose.form.transactionId").setValue(transactionId);
    	this.form.items.get("agentclose.form.comment").setValue(comments);
    	
    	this.find('itemId','agentclose.form.mdnID')[0].disable();
    	this.find('itemId','agentclose.form.name')[0].disable();
    	this.find('itemId','agentclose.form.transactionId')[0].disable();
    	this.find('itemId','agentclose.form.comment')[0].disable();
    }
    
});

Ext.reg("AgentClosing", mFino.widget.AgentClosing);