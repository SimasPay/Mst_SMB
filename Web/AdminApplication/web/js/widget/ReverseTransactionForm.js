/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ReverseTransactionForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.ReverseTransactionForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ReverseTransactionForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 250;
        this.labelPad = 20;
        this.items = [
        {
            layout:'column',
            items : [
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : [
				{
					xtype : 'hidden',
				    itemId : 'rt.form.amtrevsctlid',
				    name: CmFinoFIX.message.JSReverseTransaction.Entries.AmountReversalSCTLID._name
				}, 
				{
					xtype : 'hidden',
				    itemId : 'rt.form.chrgrevsctlid',
				    name: CmFinoFIX.message.JSReverseTransaction.Entries.ChargeReversalSCTLID._name
				},  						
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Transaction Type"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.TransactionName._name
                },                         
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Source MDN"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.SourceMDN._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Destination MDN"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.DestMDN._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Original Transaction Amount"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.OriginalTransactionAmount._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Original Charge Collected"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.OriginalCharge._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Reverse Transaction Amount"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.ReverseTxnAmount._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Charge on 'Reverse Transaction Amount'"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.ChargeOnReverseTxnAmount._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Reverse Charge Amount"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.ReverseChargeAmount._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Charge on 'Reverse Charge Amount'"),
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.ChargeOnReverseChargeAmount._name
                },                
                {
                    xtype : 'textfield',
                    fieldLabel: _("Description"),
                    labelSeparator:':',
                    anchor : '95%',
                    itemId : 'rt.form.description',
                    maxLength : 255,
                    allowBlank: false,
                    name: CmFinoFIX.message.JSReverseTransaction.Entries.FailureReason._name
                },
                {
                    xtype : 'checkbox',
                    labelSeparator:':',
                    fieldLabel: _("Reverse Amount"),
					itemId: 'rt.form.isRevAmt'
                }, 
                {
                    xtype : 'checkbox',
                    labelSeparator:':',
                    fieldLabel: _("Reverse Charge"),
					itemId: 'rt.form.isRevChrg'
                }
                ]
            },
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 30,
                labelPad : 3,
                items : [
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Note"),
                    labelSeparator:':',
                    renderer: function(value) {
                    	return 'Charge for Reversal is always inclusive of Reverse Transaction Amount and Reverse Charge Amount';
                    }
                }
				]
            }
            ]
        }
        ];
        mFino.widget.ReverseTransactionForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    reverseTransaction: function(formWindow) {
    	if(this.getForm().isValid()){
    		formWindow.disable();
    		
			if(!((this.form.items.get("rt.form.isRevAmt").getValue()) || (this.form.items.get("rt.form.isRevChrg").getValue()))){
					Ext.ux.Toast.msg(_("Message"), _("Please fill all required fields to proceed."));
					formWindow.enable();
			}
			else{
				var msg= new CmFinoFIX.message.JSReverseTransactionConfirm();
				
				msg.m_pAmountReversalSCTLID = this.form.items.get("rt.form.amtrevsctlid").getValue();
				msg.m_pIsReverseAmount = this.form.items.get("rt.form.isRevAmt").getValue();
				
				msg.m_pChargeReversalSCTLID = this.form.items.get("rt.form.chrgrevsctlid").getValue();
				msg.m_pIsReverseCharges = this.form.items.get("rt.form.isRevChrg").getValue();
				
				msg.m_pReversalReason = this.form.items.get("rt.form.description").getValue();
				var params = mFino.util.showResponse.handleReverseTransaction();
				mFino.util.fix.send(msg, params);
				formWindow.close();
				Ext.ux.Toast.msg(_("Message"), _("Reverse for transaction initiated."));
            }
			//Ext.getCmp("chargetransactionsearchfrom").searchTransactions();
    		
        }         
    },
    
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();

		var amtReversalSctlId = this.record.get(CmFinoFIX.message.JSReverseTransaction.Entries.AmountReversalSCTLID._name);
		var chrgReversalSctlId = this.record.get(CmFinoFIX.message.JSReverseTransaction.Entries.ChargeReversalSCTLID._name);
		
		this.form.items.get("rt.form.isRevAmt").setDisabled(false);
		this.form.items.get("rt.form.isRevChrg").setDisabled(false);
		
		if(amtReversalSctlId == null){
			this.form.items.get("rt.form.isRevAmt").setDisabled(true);
		}
		else{
			this.form.items.get("rt.form.isRevAmt").checked = true;
		}
		if(chrgReversalSctlId == null){
			this.form.items.get("rt.form.isRevChrg").setDisabled(true);
		}
		else{
			this.form.items.get("rt.form.isRevAmt").checked = true;
		}
    },

    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});

Ext.reg("reversetransactionform", mFino.widget.ReverseTransactionForm);