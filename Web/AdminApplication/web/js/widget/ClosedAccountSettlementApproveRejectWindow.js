/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ClosedAccountSettlementApproveRejectWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Settlement"),
        layout:'fit',
        floating: true,
        width:400,
        height:400,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.ClosedAccountSettlementApproveRejectWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ClosedAccountSettlementApproveRejectWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            text: _('Ok'),
            handler: this.ok.createDelegate(this)
        },
        {
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        }
        ];

        this.form = new Ext.form.FormPanel({
            frame : true,
            items : [
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
                                xtype : 'displayfield',
                                fieldLabel: _('Settlement mdn'),
                                itemId : 'sub.form.settlementmobileno',
                                name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.SettlementMDN._name,
                                emptyText: _(''),
                                anchor : '100%'
                                
                    },
					{
					xtype : 'displayfield',
					fieldLabel: _("Settlement Bank Account No:"),
					itemId : 'sub.form.settlementaccountno',
					anchor : '100%',
					name: CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.SettlementAccountNumber._name            
					},
					
					
					
               {
            	   fieldLabel: 'Comment'
               },{
                xtype : 'textarea',
                itemId :'comment',
                fieldLabel : _('Comments'),
                allowBlank: false,
                hideLabel: true,
                labelSeparator :'',
                anchor : '100%'
               },{
                xtype: 'fieldset',
                title : _('Select One'),
                layout : 'column',
                autoHeight: true,
                anchor : '100%',
                columns: 1,
                items: [
                {
                    columnWidth: 0.5,
                    xtype : 'radio',
                    itemId : 'approve',
                    name: 'selectone',
                    anchor : '90%',
                    checked : true,
                    boxLabel: _('Approve')
                },
                {
                    columnWidth: 0.5,
                    xtype : 'radio',
                    itemId : 'reject',
                    anchor : '90%',
                    name: 'selectone',
                    boxLabel: _('Reject')
                }]
            }]
        });
        this.items = [ this.form ];
        mFino.widget.ClosedAccountSettlementApproveRejectWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){
        if(!this.record){
            Ext.Msg.show({
                title: _('Alert !'),
                minProgressWidth:250,
                msg: _("No subscriber selected!"),
                buttons: Ext.MessageBox.OK,
                multiline: false
            });
            return;
        }
        if(this.form.getForm().isValid()){
            if(this.form.find('itemId','approve')[0].checked)
            {
                var amsg = new CmFinoFIX.message.JSApproveRejectSettlement();
                amsg.m_pSubscriberMDNID = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
                amsg.m_pAdminComment = this.form.items.get('comment').getValue();
                amsg.m_pAdminAction = CmFinoFIX.AdminAction.Approve;
                var aparams = mFino.util.showResponse.getDisplayParam();
                aparams.store = this.store;
                aparams.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                mFino.util.fix.send(amsg, aparams);
                this.hide();
            }
            else if(this.form.find('itemId','reject')[0].checked){
            	 if(this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name]===CmFinoFIX.UpgradeState.Rejected){
                     Ext.Msg.show({
                         title: _('Alert !'),
                         minProgressWidth:600,
                         msg: _("Only Approve allowed as Already Rejected "),
                         buttons: Ext.MessageBox.OK,
                         multiline: false
                     });
                 }else{
                var rmsg = new CmFinoFIX.message.JSApproveRejectSettlement();
                rmsg.m_pSubscriberMDNID = this.record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.MDNID._name];
                rmsg.m_pAdminComment = this.form.items.get('comment').getValue();
                rmsg.m_pAdminAction = CmFinoFIX.AdminAction.Reject;
                var rparams = mFino.util.showResponse.getDisplayParam();
                rparams.store = this.store;
                rparams.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                mFino.util.fix.send(rmsg, rparams);
                this.hide();
            }        
        }
        }     
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    setRecord : function(record){
		this.record=record;
		this.find('itemId', 'sub.form.gravedmobileno')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.GravedMDN._name]);
		this.find('itemId', 'sub.form.firstname')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.FirstName._name]);
		this.find('itemId', 'sub.form.lastname')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.LastName._name]);
		this.find('itemId', 'sub.form.dateofbirth')[0].setValue(record.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.DateOfBirth._name]);
		var message= new CmFinoFIX.message.JSClosedAccountSettlementMdn();
            message.m_pMDNID = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name);
            var params = mFino.util.showResponse.getDisplayParam();
            params.myForm = this.form;
            params.record=record;
            mFino.util.fix.send(message, params);
            Ext.apply(params, {
                success :  function(response){
             	   if(response.m_psuccess == true){
						if(response.Get_Entries()[0]){
					  params.myForm.items.get("sub.form.gravedmobileno").setValue(record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name]);	
				       params.myForm.items.get("sub.form.settlementmobileno").setValue(response.Get_Entries()[0].m_pSettlementMDN);
					   params.myForm.items.get("sub.form.settlementaccountno").setValue(response.Get_Entries()[0].m_pSettlementAccountNumber);
					   }else{
							params.myForm.items.get("sub.form.gravedmobileno").setValue(record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name]);
							params.myForm.items.get("sub.form.settlementmobileno").setValue(_(""));
							params.myForm.items.get("sub.form.settlementaccountno").setValue(_(""));
					   }
					   }else{
             			   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription));   	   
             	   }
                }
            });
		
    },

    setStore : function(store){
        this.store = store;
    }
});

