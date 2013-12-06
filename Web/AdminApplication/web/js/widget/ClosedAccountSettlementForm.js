/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ClosedAccountSettlementForm = function (config) {
    var localConfig = Ext.apply({}, config);
	localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
		isEditable: true	    
    });
	
    mFino.widget.ClosedAccountSettlementForm.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.ClosedAccountSettlementForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
    	this.labelWidth = 120;
        this.labelPad = 20;
        this.autoScroll = true;
        this.frame = true;
		this.store = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSClosedAccountSettlementMdn);
		this.items = [ 
                            {
                                xtype : 'displayfield',
                                fieldLabel: _('Graved MDN'),
                                itemId : 'sub.form.gravedmobileno',
								name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.GravedMDN._name,
                                allowBlank: false,
                                anchor : '100%'
                                
                    },
					{
                        xtype : 'displayfield',
                        fieldLabel:firstname,
                        itemId : 'sub.form.firstname',
                        allowBlank: false,
                        anchor : '100%',
                        name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.FirstName._name
                    },
					{
                        xtype : 'displayfield',
                        fieldLabel: lastname,
                        allowBlank: false,
                        itemId : 'sub.form.lastname',
						anchor : '100%',
                        name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.LastName._name
                    },
					{
                   	 xtype : 'displayfield',
                   	 fieldLabel: dateofbirth,
                	 itemId : 'sub.form.dateofbirth',
                	 anchor : '100%',
					 renderer : Ext.util.Format.dateRenderer('m/d/Y'),
					 name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.DateOfBirth._name                     
                	},
					{
                    xtype:'checkbox',
                    fieldLabel: _('Settle To Bank Account'),
                    width:150,
					checked:false,
                    itemId:'sub.form.settletobankaccount',
					name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.ToBankAccount._name,
                    anchor : '100%',                	 
					listeners: {
                		check: function(field) {
                			this.findParentByType('closedAccountSettlementForm').hideOrShowBankAccountNumber(field.getValue());
                		}
                	}
					},
					{
                                xtype : 'textfield',
                                fieldLabel: _('Settlement mdn'),
                                itemId : 'sub.form.settlementmobileno',
                                name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.SettlementMDN._name,
                                vtype: 'smarttelcophoneAddMore',
                                listeners: {
                                    
                                },
                                emptyText: _(''),
                                anchor : '100%'
                                
                    },
					{
					xtype : 'textfield',
					fieldLabel: _("Settlement Bank Account No:"),
					itemId : 'sub.form.settlementaccountno',
					vtype:'tendigitnumber',
					labelSeparator : '',
					anchor : '100%',
					emptyText: _(''),
					name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.SettlementAccountNumber._name            
					}
							
        ];
		this.store.on("load", this.onLoad.createDelegate(this));        
        mFino.widget.ClosedAccountSettlementForm.superclass.initComponent.call(this);
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
       
     save : function(){
    	if(this.getForm().isValid()){
			this.getForm().updateRecord(this.record);
			var MDNID=this.record.get(CmFinoFIX.message.JSClosedAccountSettlementMdn.MDNID._name);
			var GravedMDN=this.form.items.get("sub.form.gravedmobileno").getValue();
    		var FirstName=this.form.items.get("sub.form.firstname").getValue();
			var LastName=this.form.items.get("sub.form.lastname").getValue();
			var DateOfBirth=this.form.items.get("sub.form.dateofbirth").getValue();
			var ToBankAccount = 0;
			if(this.form.items.get("sub.form.settletobankaccount").checked){
				ToBankAccount = 1;
			}
			var SettlementMDN=this.form.items.get("sub.form.settlementmobileno").getValue();
			var SettlementAccountNumber=this.form.items.get("sub.form.settlementaccountno").getValue();
			if(SettlementMDN ==="" && SettlementAccountNumber==="" ){
				Ext.ux.Toast.msg(_("Error"), "SettlementMDN or SettlementAccountNumber required");
            	this.formWindow.enable();
			}else{			
            this.record.beginEdit();
			this.record.set(CmFinoFIX.message.JSClosedAccountSettlementMdn.MDNID._name, MDNID);
            this.record.set(CmFinoFIX.message.JSClosedAccountSettlementMdn.ToBankAccount._name, ToBankAccount);
			this.record.set(CmFinoFIX.message.JSClosedAccountSettlementMdn.SettlementMDN._name, SettlementMDN);
			this.record.set(CmFinoFIX.message.JSClosedAccountSettlementMdn.SettlementAccountNumber._name, SettlementAccountNumber);
			this.record.set(CmFinoFIX.message.JSClosedAccountSettlementMdn.ApprovalState._name,0 );
            this.record.endEdit();

            if(this.store){
                if(this.record.phantom
                    && this.store.getAt(0)!= this.record){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
        }
    },

	hideOrShowBankAccountNumber : function(value) {
		var settlementMdn = this.find('itemId','sub.form.settlementmobileno')[0];
		var settlementAccountNumber = this.find('itemId', 'sub.form.settlementaccountno')[0];
		if (value) {
    		settlementMdn.setValue("");
    		settlementMdn.disable();
    		settlementAccountNumber.enable();
    	} else {
    		settlementAccountNumber.setValue("");
    		settlementAccountNumber.disable();
    		settlementMdn.enable();
    	}
    },

    setRecord : function(record){
        this.getForm().reset();
		this.record=record;
		this.find('itemId', 'sub.form.gravedmobileno')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.GravedMDN._name]);
		this.find('itemId', 'sub.form.firstname')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.FirstName._name]);
		this.find('itemId', 'sub.form.lastname')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.LastName._name]);
		this.find('itemId', 'sub.form.dateofbirth')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.DateOfBirth._name]);
		var message= new CmFinoFIX.message.JSClosedAccountSettlementMdn();
                message.m_pMDNID  = record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.MDNID._name];
            var params = mFino.util.showResponse.getDisplayParam();
            params.myForm = this.form;
            params.record=record;
            mFino.util.fix.send(message, params);
            Ext.apply(params, {
                success :  function(response){
             	   if(response.m_psuccess == true){
						if(response.Get_Entries()[0]){
							if(response.Get_Entries()[0].m_pToBankAccount == 1){
								params.myForm.items.get("sub.form.settletobankaccount").setValue(true);	
							}
							if(response.Get_Entries()[0].m_pApprovalState == 2){
								params.myForm.items.get("sub.form.settlementmobileno").enable();
								params.myForm.items.get("sub.form.settletobankaccount").enable();
							}else{
								params.myForm.items.get("sub.form.settlementmobileno").disable();
								params.myForm.items.get("sub.form.settletobankaccount").disable();
							}
							params.myForm.items.get("sub.form.settlementaccountno").disable();
							params.myForm.items.get("sub.form.settlementmobileno").setValue(response.Get_Entries()[0].m_pSettlementMDN);
							params.myForm.items.get("sub.form.settlementaccountno").setValue(response.Get_Entries()[0].m_pSettlementAccountNumber);
					   }else{
							params.myForm.items.get("sub.form.settlementmobileno").enable();
							params.myForm.items.get("sub.form.settlementaccountno").disable();
							params.myForm.items.get("sub.form.settletobankaccount").enable();
					   }
					}else{
						Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription)); 
						params.myForm.items.get("sub.form.settlementmobileno").disable();
						params.myForm.items.get("sub.form.settlementaccountno").disable();
						params.myForm.items.get("sub.form.settletobankaccount").disable();
             	   	}
                }
            });
    },
	setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
    resetAll : function() {
		for ( var j = 0; j < this.form.items.length; j++) {
			this.form.items.get(j).setValue(null);
		}
		
	}
   
});



Ext.reg("closedAccountSettlementForm", mFino.widget.ClosedAccountSettlementForm);
