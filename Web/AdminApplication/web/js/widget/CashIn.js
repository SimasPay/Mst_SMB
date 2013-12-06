/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CashIn = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
//        bodyStyle: 'padding:5px 5px 0',
//        defaultType: 'textfield',
//        frame : true
    });
    mFino.widget.CashIn.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CashIn, Ext.FormPanel, {
   
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
						xtype : 'displayfield',
						fieldLabel:'Name',
					    itemId : 'cashin.form.subname',
					    allowBlank: false,
					    anchor : '100%',
					    name:""
					},
					{
						xtype : 'hidden',
						fieldLabel:'mdnid',
					    itemId : 'cashin.form.mdnid',
					    allowBlank: false,
					    anchor : '100%',
					    name: CmFinoFIX.message.JSBankTellerCashInInquiry.DestMDNID._name
					},
                     {
                         xtype : 'displayfield',
                         fieldLabel:'MDN',
                         itemId : 'cashin.form.mdn',
                         allowBlank: false,
                         disable : true,
                         // anchor : '100%',
                         name: CmFinoFIX.message.JSBankTellerCashInInquiry.DestMDN._name
                     },
                     
                     {
                         xtype:'textfield',
                         fieldLabel: _('Amount'),
                         allowBlank:false,
                         vtype:'numbercomma',
                         labelSeparator: '',
                         emptyText: _('eg: 1000'),
                         maxLength : 16,
                         name: CmFinoFIX.message.JSBankTellerCashInInquiry.Amount._name,
                         listeners: {
                             blur:  function(field){
                                 field.setValue(Ext.util.Format.number(field.getValue(), '0,000'));
                             }
                         }
                     },
                     {
 						xtype : 'textfield',
 						inputType: 'password',
 						fieldLabel:'Pin',
 					    itemId : 'cashin.form.pin',
 					    vtype:'pin',
 					    allowBlank: false,
// 					    anchor : '100%',
 					    name: CmFinoFIX.message.JSBankTellerCashInInquiry.Pin._name
 					},
                     {
                         xtype : 'textfield',
                         fieldLabel:'Comment',
                         labelSeparator: '',
                        // itemId : 'sub.form.firstname',
                         allowBlank: false,
//                         anchor : '100%',
                         name: CmFinoFIX.message.JSBankTellerCashInInquiry.AdminComment._name
                     }]
        }];

        mFino.widget.CashIn.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
   
    transfer: function(formWindow) {
        if(this.getForm().isValid()){
        	formWindow.disable();
			var mask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait... Processing transaction"});
            mask.show();
            var msg= new CmFinoFIX.message.JSBankTellerCashInInquiry();
            var values = this.form.getValues();
            var transferAmount = values[CmFinoFIX.message.JSBankTellerCashInInquiry.Amount._name];
            transferAmount =  transferAmount.replace(/\,/g,'');
            msg.m_pAmount = transferAmount;
            msg.m_pDestMDN = this.form.items.get("cashin.form.mdn").getValue();
            msg.m_pDestMDNID = this.form.items.get("cashin.form.mdnid").getValue();
            msg.m_pPin = this.form.items.get("cashin.form.pin").getValue();

            var params = {
                    success : function(response){
                    	if(response.m_psuccess){
							mask.hide();
                            Ext.Msg.show({
                                title: 'Confirm',
                                minProgressWidth:600,
                                msg: "your requested to cashin "+response.m_pAmount+" to  "+response.m_pDestMDN+" charges:"+ response.m_pCharges ,
                                buttons: Ext.MessageBox.OKCANCEL,
                                closable:false,
                                multiline: false,
                                fn: function(btn) {
                                	 if(btn == "cancel"){
										return false;
									 }
                                	 var msg= new CmFinoFIX.message.JSBankTellerCashInConfirm();
                                	 msg.m_pAmount = response.m_pAmount;
                                	 msg.m_pDestMDN = response.m_pDestMDN;
                                	 msg.m_pDestMDNID = response.m_pDestMDNID; 
                                	 msg.m_pServiceChargeTransactionLogID = response.m_pServiceChargeTransactionLogID;
                                	 msg.m_pPin = response.m_pPin;
                                	 msg.m_pTransferID = response.m_pTransferID;
                                     var params = mFino.util.showResponse.getDisplayParam();
                                    mFino.util.fix.send(msg, params);
                                    }
                            });

                        }else{
							mask.hide();
                            Ext.Msg.show({
                                title: 'Error',
                                minProgressWidth:600,
                                msg: response.m_pErrorDescription,
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }
                    },
                    failure : function(response){
						mask.hide();
                        Ext.Msg.show({
                            title: 'Error',
                            minProgressWidth:250,
                            msg: "Your transaction is having a problem. Please check your recent transaction on pending transaction list or contact Customer Care :881",
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }
                };
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);
        }
    },
    setSubscriberDetails : function(record){
        this.getForm().reset();
        this.form.items.get("cashin.form.mdn").setValue(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name));
        this.form.items.get("cashin.form.mdnid").setValue(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name));
    	this.form.items.get("cashin.form.subname").setValue(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name)+" "
														+record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name));
    }
    
});

Ext.reg("cashin", mFino.widget.CashIn);

