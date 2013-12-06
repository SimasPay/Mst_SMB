/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CashOutView = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });
    mFino.widget.CashOutView.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CashOutView, Ext.form.FormPanel, {
   
    initComponent : function () {
	 	this.labelWidth = 120;
	 	this.labelPad = 20;
	 	this.autoScroll = true;
	 	this.frame = true;
	 	this.items = [
        {
        	layout:'column',
            items : [
                     {
                     columnWidth: 0.5,
                     items : [{
                    	 width: 300,
                         autoHeight: true,
                         allowBlank: false,
                         layout: 'form',
                         items: [
                                 {
			                         xtype : 'displayfield',
			                         fieldLabel:'SourceMDN',
			                         itemId : 'cashoutview.form.mdn',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDN._name
                                 },
			                     {
			                         xtype:'hidden',
			                         fieldLabel: _('TransferID'),
			                         itemId : 'cashoutview.form.transferid',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.ID._name
			                     },
			                     {
			                         xtype:'displayfield',
			                         fieldLabel: _('TransferID'),
			                         itemId : 'cashoutview.form.sctlID',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.IDSearch._name
			                     },
			                     {
			                         xtype : 'displayfield',
			                         fieldLabel:'Amount',
//			                         itemId : 'cashoutview.form.mdn',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.Amount._name
			                     },
			                     {
			                         xtype:'hidden',
			                         fieldLabel: _('PocketID'),
			                         itemId : 'cashoutview.form.pocketid',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketID._name
			                     }]
                     }]
                     },
                     {
                     columnWidth: 0.5,
                     items : [{
                    	 width: 300,
                         autoHeight: true,
                         allowBlank: false,
                         layout: 'form',
                         items: [
                                 {
			                         xtype : 'displayfield',
			                         fieldLabel:'DestinationMDN',
			                         itemId : 'cashoutview.form.destmdn',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name
			                     },
			                     
			                     {
			                         xtype:'displayfield',
			                         fieldLabel: _('Status'),
			                         itemId : 'cashoutview.form.status',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStatusText._name
			                         
			                     },
			                     {
			                         xtype : 'displayfield',
			                         fieldLabel:'Charges',
//			                         itemId : 'cashoutview.form.mdn',
			                         labelSeparator: '',
			                         name: CmFinoFIX.message.JSCommodityTransfer.Entries.Charges._name
			                     }]
                     }]
                     }]
			        }, {
		                xtype:'displayfield',
		                labelSeparator:''
		            },
		            {
		                xtype:'displayfield',
		                labelSeparator:''
		            },{
							xtype : 'textfield',
								inputType: 'password',
								fieldLabel:'Pin',
							    itemId : 'cashoutview.form.pin',
							    vtype:'pin',
							    allowBlank: false,
							    anchor : '50%',
							    name: ''
							}];
        mFino.widget.CashIn.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
     },
     
       setRecord : function(record){
            this.getForm().reset();
            this.record = record;
            this.getForm().loadRecord(record);
            this.getForm().clearInvalid();
            if(record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStateText._name)===CmFinoFIX.TransferStateValue.Pending){
            this.form.items.get("cashoutview.form.status").setValue(CmFinoFIX.TransferStateValue.Pending);
            }
        },  
         
        confirm : function(formWindow){
        	if(this.record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStateText._name)===CmFinoFIX.TransferStateValue.Pending){
        		 Ext.ux.Toast.msg(_("Error"), "Transaction status is  Pending. Only successful transactions are allowed to  approve ");
        	}else if(this.record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStatus._name)===CmFinoFIX.TransferStatus.Failed){
        		 Ext.ux.Toast.msg(_("Error"), "Transaction status is Failed. Only successful transactions are allowed to  approve ");
        	 }else{
        		 if(this.getForm().isValid()){
        		 formWindow.disable();	 
	        	 var msg= new CmFinoFIX.message.JSBankTellerCashOutConfirm();
	        	 msg.m_pMDN = this.form.items.get("cashoutview.form.destmdn").getValue();
	        	 msg.m_pCommodityTransferID = this.form.items.get("cashoutview.form.transferid").getValue();
	        	 msg.m_pPocketID = this.form.items.get("cashoutview.form.pocketid").getValue();
	        	 msg.m_pPin = this.form.items.get("cashoutview.form.pin").getValue();
	             var params = mFino.util.showResponse.getDisplayParam();
	             params.formWindow=formWindow;
	             mFino.util.fix.send(msg, params);
	             }
        	 }
        }
    
});

Ext.reg("cashoutview", mFino.widget.CashOutView);

