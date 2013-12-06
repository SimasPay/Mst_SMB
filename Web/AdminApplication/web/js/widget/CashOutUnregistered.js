/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CashOutUnregistered = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
//        bodyStyle: 'padding:5px 5px 0',
//        defaultType: 'textfield',
//        frame : true
    });
    this.cashoutview = new mFino.widget.FormWindowLOP(Ext.apply({
	        form : new mFino.widget.CashOutView(config),
	        title : _("CashOut Transaction Status"),
	        height : 250,
	        width:550,
	        mode:"cashoutview"
	    },config));
    
    mFino.widget.CashOutUnregistered.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CashOutUnregistered, Ext.FormPanel, {
   
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
  		                xtype:'displayfield',
  		                labelSeparator:''
  		            },
					{
                         xtype : 'textfield',
                         fieldLabel:'MDN',
                         itemId : 'cashout.form.mdn',
                         vtype: 'smarttelcophoneAddMore',
                         allowBlank: false,
                         // anchor : '100%',
                         name: CmFinoFIX.message.JSCashOutUnregisteredInquiry.SourceMDN._name,
                         listeners: {
                             change: function(field) {/*
                             	field.isValid(true);
                             	var valmdn=/^[2]{1}[3]{1}[4]{1}[0-9]{10}$/;
                             	var valmdn1=/^[2]{1}[3]{1}[4]{1}[0-9]{7}$/;
                             	var mdn = field.getValue();
                             	if(mdn.length==13)
                         		{
                             		if(!valmdn.test(mdn))
                             		{
                             			field.markInvalid("MDN start with 234");
                             		}
                         		}else if(mdn.length>10){
                         			field.markInvalid("MDN starting with 234 should be 13 digits or 10 digits");
                         		} else if(valmdn1.test(mdn)){
                            		
                        			field.markInvalid("MDN should be 13 digits");
                        		
                         		}
                         */}
                         }
                     },
                     {
 		                xtype:'displayfield',
 		                labelSeparator:''
 		            },
                     {
                         xtype:'textfield',
                         fieldLabel: _('Reference ID'),
                         itemId : 'cashout.form.transferid',
                         allowBlank:false,
                         vtype:'number19',
                         labelSeparator: '',
                         maxLength : 16,
                         name: CmFinoFIX.message.JSCashOutUnregisteredInquiry.OriginalReferenceID._name
                     },
                     {
  		                xtype:'displayfield',
  		                labelSeparator:''
  		            },
                     {
                         xtype:'textfield',
                         fieldLabel: _('Pin'),
                         itemId : 'cashout.form.pin',
                         allowBlank:false,
                         inputType: 'password',
                         vtype:'pin',
                         labelSeparator: '',
                         maxLength : 16,
                         name: CmFinoFIX.message.JSCashOutUnregisteredInquiry.Pin._name
                     },
                     {
  		                xtype:'displayfield',
  		                labelSeparator:''
  		            },
                     {
                         xtype:'textfield',
                         fieldLabel: _('SecretCode'),
                         itemId : 'cashout.form.code',
                         allowBlank:false,
                         inputType: 'password',
                         vtype:'number16',
                         labelSeparator: '',
                         maxLength : 16,
                         name: CmFinoFIX.message.JSCashOutUnregisteredInquiry.DigestedPIN._name
                     }
                    ]
        }];
        mFino.widget.CashIn.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
     
     reset : function(){
    	 this.getForm().reset();
     },
     
     search: function(formWindow) {
    	if(this.getForm().isValid()){
    		this.formWindow = formWindow;
    		formWindow.disable();	
        	var values = this.form.getValues();
        	 var msg= new CmFinoFIX.message.JSCashOutUnregisteredInquiry();
        	 msg.m_pSourceMDN = values.SourceMDN;
        	 msg.m_pOriginalReferenceID = values.OriginalReferenceID;
        	 msg.m_pPin = values.Pin;
        	 msg.m_pDigestedPIN = values.DigestedPIN;
             var params = {
                     success : function(response){
                    	 formWindow.hide();	
                    	 formWindow.enable();	
                    	 if(response.m_psuccess){
                             Ext.Msg.show({
                                 title: 'Confirm',
                                 minProgressWidth:600,
                                 msg: "You requested to cashout"+response.m_pAmount+" from  "+response.m_pDestMDN+" charges:"+ response.m_pCharges ,
                                 buttons: Ext.MessageBox.OK,
                                 closable:false,
                                 multiline: false,
                                 fn: function(btn) {
                                 	 var msg= new CmFinoFIX.message.JSCashOutUnregisteredConfirm();
                                 	 msg.m_pDestMDN = response.m_pDestMDN;
                                 	 msg.m_pSourceMDN = response.m_pSourceMDN;
                                 	 msg.m_pServiceChargeTransactionLogID = response.m_pServiceChargeTransactionLogID;
                                 	 msg.m_pPin = response.m_pPin;
                                 	 msg.m_pTransferID = response.m_pTransferID;
                                 	 msg.m_pParentTransactionID = response.m_pParentTransactionID;
                                 	 msg.m_pConfirmed = true;
                                 	 msg.m_pSourcePocketID = response.m_pSourcePocketID;
                                 	 msg.m_pDestPocketID = response.m_pDestPocketID;
                                     var params = mFino.util.showResponse.getDisplayParam();
                                     mFino.util.fix.send(msg, params);
                                     }
                             });

                         }else{
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
                    	 formWindow.hide();	
                    	 formWindow.enable();	
                         Ext.Msg.show({
                             title: 'Error',
                             minProgressWidth:250,
                             msg: "Your transaction is having a problem. Please check your recent transaction on pending transaction list or contact Customer Care :881",
                             buttons: Ext.MessageBox.OK,
                             multiline: false
                         });
                     }
                 };
             params.formWindow=formWindow;
             mFino.util.fix.send(msg, params);
           }
        }    
});

Ext.reg("cashoutunregistered", mFino.widget.CashOutUnregistered);

