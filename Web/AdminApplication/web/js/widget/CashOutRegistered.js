/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CashOutRegistered = function (config) {
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
    
    mFino.widget.CashOutRegistered.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CashOutRegistered, Ext.FormPanel, {
   
    initComponent : function () {
	 	this.labelWidth = 120;
	 	this.labelPad = 20;
	 	this.autoScroll = true;
	 	this.frame = true;
	 	this.store = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSCommodityTransfer);
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
                         name: CmFinoFIX.message.JSCommodityTransfer.SourceMDN._name,
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
                         		}else if(valmdn1.test(mdn)){
                            		
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
                         name: CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name
                     }
                    ]
        }];
        this.store.on("load", this.onLoad.createDelegate(this));
        mFino.widget.CashOutRegistered.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
     
     onLoad: function(){
         var record = this.store.getAt(0);
       	 if(record!=null){
       		 	this.cashoutview.enable();
       		 	this.cashoutview.setRecord(record);
       		 	this.cashoutview.show();
       		 	this.store.remove(record);
       		 	this.formWindow.close();
             }else{           	
            	 Ext.ux.Toast.msg(_("Error"), "No Transaction Found ");
            	 this.formWindow.enable();
            	 }
        },
        
     reset : function(){
    	 this.getForm().reset();
     },
     
     search: function(formWindow) {
    	if(this.getForm().isValid()){
    		this.formWindow = formWindow;
    		formWindow.disable();	
        	var values = this.form.getValues();
            this.store.baseParams = values;
            this.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.JSMsgType._name] = CmFinoFIX.MsgType.JSBankTellerCashOutInquiry;
            this.store.load(); 
            formWindow.enable();	
           }
        }    
});

Ext.reg("cashoutregistered", mFino.widget.CashOutRegistered);

