/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AgentClosingInquiry = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    });
    
    mFino.widget.AgentClosingInquiry.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AgentClosingInquiry, Ext.FormPanel, {
   
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
                         fieldLabel:'Agent Name',
                         itemId : 'agentcloseinq.form.name',
                         allowBlank: false,
                         maxLength : 15,
                     },
                     {
                         xtype : 'textfield',
                         fieldLabel:'Agent Code',
                         itemId : 'agentcloseinq.form.code',
                         allowBlank: false,
                         maxLength : 15,
                     },
                     {
                         xtype : 'textfield',
                         fieldLabel:'Agent MDN',
                         itemId : 'agentcloseinq.form.mdnID',
                         allowBlank: false,
                         maxLength : 15,
                         name: CmFinoFIX.message.JSAgentClosingInquiry.DestMDN._name
                     },
                     {
                         xtype : 'textarea',
                         fieldLabel:'Comment',
                         itemId : 'agentcloseinq.form.comments',
                         labelSeparator: '',
                         hieght: 70,
                         width: 200,
                         name: CmFinoFIX.message.JSAgentClosingInquiry.Comments._name
                     }]
        }];

        mFino.widget.AgentClosingInquiry.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
   
    closeaccount: function(formWindow) {
    	
        if(this.getForm().isValid()){
        	
        	this.find('itemId','agentcloseinq.form.mdnID')[0].enable();
        	
        	var msg= new CmFinoFIX.message.JSAgentClosingInquiry();            
            var values = this.form.getValues();
            var subscribermdn = values[CmFinoFIX.message.JSAgentClosingInquiry.DestMDN._name];
            var comments = values[CmFinoFIX.message.JSAgentClosingInquiry.Comments._name];
            msg.m_pDestMDN = subscribermdn;
            msg.m_pComments = comments;
            msg.m_paction = "create";
            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);
            
            Ext.apply(params, {
				success :  function(response){
							
							if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
		                        Ext.Msg.show({
		                            title: 'Info',
		                            minProgressWidth:600,
		                            msg: response.m_pErrorDescription,
		                            buttons: Ext.MessageBox.OK,
		                            multiline: false
		                        });
							}
							
							if(response.m_psuccess == true){
								
								var ConfirmClose = new mFino.widget.FormWindowLOP(Ext.apply({
			                    form : new mFino.widget.AgentClosing(this),
			                    title : _("Agent Account Closing"),
			                    height : 270,
			                    width:400,
			                    mode:"confirm"
			                	},this));
								
			                	ConfirmClose.show();			                	
			                	ConfirmClose.form.setDetails(response.m_pDestMDN,response.m_pDestinationUserName,response.m_pTransferID,comments);
							}
				},
                failure : function(response){
                    Ext.Msg.show({
                        title: 'Error',
                        minProgressWidth:250,
                        msg: "Your transaction is having a problem. Please check your recent transaction on pending transaction list or contact Customer Care :881",
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                }});
				
				return params;
        	}
    },
    setDetails : function(name,agentcode,subMDN){
        this.getForm().reset();
        
        this.form.items.get("agentcloseinq.form.name").setValue(name);
    	this.find('itemId','agentcloseinq.form.name')[0].disable();
    	
    	this.form.items.get("agentcloseinq.form.code").setValue(agentcode);
    	this.find('itemId','agentcloseinq.form.code')[0].disable();
    	
        this.form.items.get("agentcloseinq.form.mdnID").setValue(subMDN);
    	this.find('itemId','agentcloseinq.form.mdnID')[0].disable();
    }
    
});

Ext.reg("agentclosinginquiry", mFino.widget.AgentClosingInquiry);