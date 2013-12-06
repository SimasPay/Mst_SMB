/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AgentCashIn = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
//        bodyStyle: 'padding:5px 5px 0',
//        defaultType: 'textfield',
//        frame : true
    });
    mFino.widget.AgentCashIn.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AgentCashIn, Ext.FormPanel, {
   
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
                         fieldLabel:'Agent Trade Name',
                         itemId : 'agent.form.agentname',
                         allowBlank: false,
                         // anchor : '100%',
                         name: CmFinoFIX.message.AgentCashIn.FirstName._name
                     },
                     {
                         xtype : 'hidden',
                         fieldLabel:'AgentID',
                         itemId : 'agent.form.agentID',
                         allowBlank: false,
                         anchor : '100%',
                         name: CmFinoFIX.message.AgentCashIn.PartnerID._name
                     },
                     {
                         xtype:'textfield',
                         fieldLabel: _('Amount'),
                         allowBlank:false,
                         vtype:'numbercomma',
                         labelSeparator: '',
                         emptyText: _('eg: 1125'),
                         maxLength : 16,
                         name: CmFinoFIX.message.AgentCashIn.Amount._name,
                         listeners: {
                             blur:  function(field){
                                 field.setValue(Ext.util.Format.number(field.getValue(), '0,000'));
                             }
                         }
                     },
                     {
                         xtype : 'textfield',
                         fieldLabel:'Comment',
                         labelSeparator: '',
                        // itemId : 'sub.form.firstname',
                         allowBlank: false,
//                         anchor : '100%',
                         name: CmFinoFIX.message.AgentCashIn.AdminComment._name
                     }]
        }];

        mFino.widget.AgentCashIn.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
   
    transfer: function(formWindow) {
        if(this.getForm().isValid()){
            var msg= new CmFinoFIX.message.AgentCashIn();
            var values = this.form.getValues();
            var transferAmount = values[CmFinoFIX.message.AgentCashIn.Amount._name];
            var id=values[CmFinoFIX.message.AgentCashIn.PartnerID._name];
            transferAmount =  transferAmount.replace(/\,/g,'');
            msg.m_pAmount = transferAmount;
            msg.m_pPartnerID = id;
            msg.m_paction = "create";
            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);
        }
    },
    setDetails : function(agentName,ID){
        this.getForm().reset();
        this.form.items.get("agent.form.agentID").setValue(ID);
    	this.form.items.get("agent.form.agentname").setValue(agentName);
    	this.find('itemId','agent.form.agentname')[0].disable();
    }
    
});

Ext.reg("agencashin", mFino.widget.AgentCashIn);

