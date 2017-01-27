/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.subscriber = function(config){
    var gridEditForm = new mFino.widget.FormWindowForSubscriber(Ext.apply({
        form : new mFino.widget.SubscriberForm(config),
        width:750,
    	height:600
    	//anchor: '100%'
    	//width: browserWidth
    },config));
    var gridEditLakupandaiForm = new mFino.widget.FormWindowForSubscriber(Ext.apply({
        form : new mFino.widget.SubscriberLakupandaiFormView(config),
        width:770,
    	height:600
    	//anchor: '100%'
    	//width: browserWidth
    },config));
    
    var gridEditFormview = new mFino.widget.FormWindowForSubscriber(Ext.apply({
        form : new mFino.widget.SubscriberForm(config),
        width:770,
    	height:600
    	//anchor: '100%'
    	//width: browserWidth
    },config));
    var gridLakupadaiFormview = new mFino.widget.FormWindowForSubscriber(Ext.apply({
    	form : new mFino.widget.SubscriberLakupandaiViewForm(config),
        width:770,
    	height:600
    	//anchor: '100%'
    	//width: browserWidth
    },config));
    
	var closedAccountForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.ClosedAccountSettlementForm(config),
		store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSClosedAccountSettlementMdn),
        width:400,
    	height:275
    },config));
    
    var detailsForm = new mFino.widget.SubscriberDetails(config);

    var searchBox = new mFino.widget.SubscriberSearchForm(Ext.apply({
        height : 255,
        autoScroll:true
    }, config));

    var checkBalanceWindow = new mFino.widget.CheckBalanceSubscriber(Ext.apply({
        layout: "fit",
        height: 175
    }, config));
    
    var listBox = new mFino.widget.SubscriberList(Ext.apply({
    	height : 300,
    	anchor : "100%, -255"
    }, config));
    
    var approveWindow = new mFino.widget.ApproveRejectWindow(config);
    
    var createEmoneyPocketSuspenseRequestWindow = new mFino.widget.CreateSubEmoneyPocketSuspenseRequestWindow(config);
    
    var approveRejectEmoneyPocketSuspendRequestWindow = new mFino.widget.ApproveRejectSubEmoneyPocketSuspendRequestWindow(config);
    
    var subscriberUpgradeApproveRejectWindow = new mFino.widget.SubscriberUpgradeApproveRejectWindow(config);
    
    var subscriberUpgradeKycApproveRejectWindow = new mFino.widget.SubscriberUpgradeKycApproveRejectWindow(config);
    
    var subscriberUpgradeKycLevelWindow = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.SubscriberUpgradeKycLevelWindow(config),
        title : _("Upgrade from Non-KYC E-Money to KYC E-Money"),
        width:520,
        height: 550,
        mode:"upgradekyc"
    }, config));
    
    var subscriberAddPocketWindow = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.SubscriberAddPocketWindow(config),
        store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber),
        title : _("ADD Bank pocket for Emoney Subscriber"),
        width:450,
        height: 300,
        mode:"addpocket"
    }, config));
    
    var subscriberAddPocketApproveRejectWindow =new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.SubscriberAddPocketApproveRejectWindow(config),
        store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber),
        title : _("Approve/Reject Subscriber for MBanking Services"),
        width:450,
        height: 400,
        mode:"proceed"
    }, config));
    
    var subClosing = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.SubscriberClosingInquiry(config),
        title : _("Subscriber Account Closing"),
        height : 220,
        width:400,
        mode:"closeaccount"
    },config));
	
    var approveSettlementWindow = new mFino.widget.ClosedAccountSettlementApproveRejectWindow(config);
    	
    //this enables serverside form validation
    listBox.store.form = gridEditForm.form;
    var tabPanel = new mFino.widget.SubscriberTab(config);
    tabPanel.checkEnabledItems();
    
   
    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 200,
            anchor : "100%",
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('Subscriber Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-refresh',
                tooltip : _('Refresh Subscriber'),
                handler : function(){
                    if(detailsForm.record)
                    {
                        listBox.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                        listBox.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
                        listBox.store.load(listBox.store.lastOptions);
                    }
                    else
                    {
                        Ext.ux.Toast.msg(_('Error'), _("No Subscriber is Selected"));
                    }
                }
            },
            {
                iconCls: 'mfino-button-user-add',
                tooltip : _('Register Subscriber'),
                itemId : 'sub.details.add',
                handler:  function(){
                	//alert("hello");
            	
                    var record = new listBox.store.recordType();
                    gridEditForm.setTitle(_("Register Subscriber"));
                    gridEditForm.setMode("add");
                    gridEditForm.form.resetAll();
                    gridEditForm.show();
                    gridEditForm.setEditable(false);
//                    gridEditForm.form.onStatusDropdown(CmFinoFIX.MDNStatus.Initialized);
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.NotificationMethod._name, CmFinoFIX.NotificationMethod.SMS+CmFinoFIX.NotificationMethod.Email);
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberTypeText._name, "Subscriber");
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name, CmFinoFIX.MDNStatus.Initialized);
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Language._name,CmFinoFIX.Language.English);
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Timezone._name,SYSTEM_DEFAULT_TIMEZONE);
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.Currency._name,SYSTEM_DEFAULT_CURRENCY);
                    record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name,SYSTEM_DEFAULT_KYC);
                    record.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name,1);
                    gridEditForm.setRecord(record);
                    gridEditForm.setStore(listBox.store);
                    gridEditForm.form.find('itemId','sub.form.mobileno')[0].enable();
                    gridEditForm.form.setAccountAndTemplateDisplay(true);                    
                   }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit Subscriber'),
                itemId : 'sub.details.edit',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
                    	 Ext.MessageBox.alert(_("Info"), _("Unregistered Subscriber not allowed to edit"));
                    }else{
                    	var actualCount=tabPanel.pocketGrid.store.getCount();
                    	var isLakupandai=false;
                    	var count=0;
            			tabPanel.pocketGrid.store.each(function(record){
//            				alert(record.get(CmFinoFIX.message.JSPocket.Entries.PocketTypeText._name))
            				count++;
            				if(record.get(CmFinoFIX.message.JSPocket.Entries.PocketTypeText._name)== 'Laku Pandai' ){
            					isLakupandai=true;
            				}
            				if(count==actualCount){
            					
            					if(isLakupandai){
            						gridEditLakupandaiForm.setTitle( _("Edit Subscriber"));
            						gridEditLakupandaiForm.setMode("edit");
            						gridEditLakupandaiForm.form.resetAll();
            						gridEditLakupandaiForm.show();                        
            						gridEditLakupandaiForm.setRecord(detailsForm.record);
            						gridEditLakupandaiForm.setStore(detailsForm.store);
                                    var status=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name);
                                    gridEditLakupandaiForm.form.onStatusDropdown(status); //this shld be called after setRecord or else field values will be taken as empty by this methods
                                    gridEditLakupandaiForm.form.onKYCDropdown(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name));
                                    gridEditLakupandaiForm.form.disableNotPermittedItems();
                                    gridEditLakupandaiForm.form.find('itemId','sub.form.mobileno')[0].disable();
                                    gridEditLakupandaiForm.form.setAccountAndTemplateDisplay(false);
            					}else{
            						gridEditForm.setTitle( _("Edit Subscriber"));
                                    gridEditForm.setMode("edit");
                                    gridEditForm.form.resetAll();
                                    gridEditForm.show();  
                                    detailsForm.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhrase._name,"*********");
                                    gridEditForm.setRecord(detailsForm.record);
                                    gridEditForm.setStore(detailsForm.store);
                                    var status=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name);
                                    gridEditForm.form.onStatusDropdown(status); //this shld be called after setRecord or else field values will be taken as empty by this methods
                                    gridEditForm.form.onKYCDropdown(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name));
                                    gridEditForm.form.disableNotPermittedItems();
                                    gridEditForm.form.find('itemId','sub.form.mobileno')[0].disable();
                                    gridEditForm.form.find('itemId','sub.form.secretanswer')[0].disable();
                                    gridEditForm.form.find('itemId','sub.form.securityquestion')[0].disable();
                                    gridEditForm.form.setAccountAndTemplateDisplay(false);
            					}
            				}
            			})
                        
                    }
                }
            },
            {
                iconCls: 'mfino-button-closed-account-settlement',
                tooltip : _('Closed Account Settlement Details'),
				itemId : 'sub.settle.closed.account',                
                id : 'sub.settle.closed.account',
                handler : function(){
				if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    }
					else{
						var newrecord = new closedAccountForm.store.recordType();
						closedAccountForm.setTitle( _("Closed Account Settlement"));
						closedAccountForm.setMode("edit");
						closedAccountForm.form.resetAll();
						closedAccountForm.show();
						closedAccountForm.form.items.get("sub.form.settlementaccountno").disable();
						newrecord.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.MDNID._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
						newrecord.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.GravedMDN._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
						newrecord.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.FirstName._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name];
						newrecord.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.LastName._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name];
						newrecord.data[CmFinoFIX.message.JSClosedAccountSettlementMdn.Entries.DateOfBirth._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.DateOfBirth._name];
						closedAccountForm.setStore(closedAccountForm.store);                
						closedAccountForm.setRecord(newrecord);
					}				
				}
            },
			{
                iconCls: 'mfino-button-approve-reject-settlement',
                tooltip : _('Approve/Reject Settlement'),
                id : 'sub.settle.closed.account.approve.reject',
				itemId : 'sub.settle.closed.account.approve.reject',                
                handler : function(){
				if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    }
					else{
						var message= new CmFinoFIX.message.JSClosedAccountSettlementMdn();
						message.m_pMDNID = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
						var params = mFino.util.showResponse.getDisplayParam();
						mFino.util.fix.send(message, params);
						Ext.apply(params, {
							success :  function(response){
							   if(response.m_psuccess == true){
									if(response.Get_Entries()[0]){
										if(response.Get_Entries()[0].m_pApprovalState == 1){
											Ext.MessageBox.alert(_("Info"), _("Subscriber amount is already settled"));
										}
										else if(response.Get_Entries()[0].m_pApprovalState == 2){
											Ext.MessageBox.alert(_("Info"), _("Subscriber rejected earlier"));
										}
										else{
											approveSettlementWindow.show();
											approveSettlementWindow.setRecord(detailsForm.record);
											approveSettlementWindow.setStore(detailsForm.store);
										}				
								   }else{
										Ext.MessageBox.alert(_("Info"), _("Settlement Details not Provided")); 
								   }
								   }else{
									   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription));   	   
							   }
							}
						});						
					}				
				}
            },
            {
                iconCls: 'mfino-button-View',
                tooltip : _(' Subscriber Details'),
                itemId : 'sub.details.view',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    }else{
                    	var actualCount=tabPanel.pocketGrid.store.getCount();
                    	var isLakupandai=false;
                    	var count=0;
            			tabPanel.pocketGrid.store.each(function(record){
//            				alert(record.get(CmFinoFIX.message.JSPocket.Entries.PocketTypeText._name))
            				count++;
            				if(record.get(CmFinoFIX.message.JSPocket.Entries.PocketTypeText._name)== 'Laku Pandai' ){
            					isLakupandai=true;
            				}
            				if(count==actualCount){
            					
            					if(isLakupandai){
            						gridLakupadaiFormview.setTitle( _("PERSETUJUAN NASABAH LAKU PANDAI BANK SINARMAS"));
            						gridLakupadaiFormview.setMode("close");
//                        			gridEditFormview.form.setReadOnly(true);
            						gridLakupadaiFormview.show();
            						gridLakupadaiFormview.setRecord(detailsForm.record);
            						gridLakupadaiFormview.setStore(detailsForm.store);
            						gridLakupadaiFormview.form.setAccountAndTemplateDisplay(true);
            					}else{
            						  gridEditForm.setTitle( _(" Subscriber Details"));
          	                        gridEditForm.setMode("close");
          	                        gridEditForm.form.setReadOnly(true);
          	                        gridEditForm.show();
          	                       /* var status=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name);
          	                        gridEditForm.form.onStatusDropdown(status);
          	                        gridEditForm.form.disableNotPermittedItems();*/
          	                        gridEditForm.setRecord(detailsForm.record);
          	                        gridEditForm.setStore(detailsForm.store);
          	                        gridEditForm.form.setAccountAndTemplateDisplay(true);
            					}
            					
            				}
            			})
                    	
                    }
                }
            },
//           {
//                iconCls: 'mfino-button-currency',
//                tooltip : _('Check Balance'),
//                itemId: 'sub.details.checkBalance',
//                handler : function(){
//                    if(!detailsForm.record){
//                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
//                    }else{
//                        var message= new CmFinoFIX.message.JSCheckBalanceForSubscriber();
//                        message.SetSubscriberMDNID(detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name]);
//                        message.SetMDN(detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name]);
//                        var params = mFino.util.showResponse.getDisplayParam();
//                        params.myForm = this;
//                        params.record=detailsForm.record;
//                        mFino.util.fix.send(message, params);
//                        Ext.apply(params, {
//                            success :  function(response){
////                                checkBalanceWindow.buttons[0].enable();
//                                checkBalanceWindow.show();
////                                if(!mFino.auth.isEnabledItem('sub.balance.emptyButton')){
////                                    checkBalanceWindow.buttons[0].disable();
////                                }
//                                checkBalanceWindow.setValues(response, params.record);
//                            }
//                        });
//                    }
//                }
//            }, 
            {
                iconCls: 'mfino-button-key',
                tooltip : _('Reset PIN'),
                itemId : 'sub.details.resetpin',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    } if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
                    	 Ext.MessageBox.alert(_("Info"), _("Reset Pin not allowed for Unregistered Subscriber"));
                    }else{
                    	var mdn=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name);
                    	var name=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name)+" "
                    				+detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name);
                        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Reset Pin for "+mdn+", "+name+"?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg = mFino.util.fix.getResetPinMsgFromRecord(detailsForm.record);
                                msg.m_pServletPath = CmFinoFIX.ServletPath.WebAppFEForSubscribers;
                                var params = mFino.util.showResponse.getDisplayParam();
                                params.store = detailsForm.store;
                                params.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            },
//            {
//                iconCls: 'mfino-button-bulkupload',
//                tooltip : _('Bulk Link MDN to Card'),
//                itemId: 'sub.details.bulklinkcard',
//                handler : function(){
//                    var popup = new mFino.widget.BulkBankAccount({});
//                    popup.show();
//                }
//            },
            {
                iconCls : "mfino-button-resolve",
                tooltip : _('Approve/Reject Subscriber'),
                itemId: 'sub.approve',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
                    	 Ext.MessageBox.alert(_("Info"), _("Unregistered Subscriber not allowed for Approve/Reject "));
                    }else{
                    	if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.MDNStatus.Active
                    			&& detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.MDNStatus.Initialized){
                    		Ext.Msg.show({
                                title: _('Alert !'),
                                minProgressWidth:250,
                                msg: _("Invalid Subscriber Status"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name)===CmFinoFIX.UpgradeState.Upgradable|| 
                        		detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name)===CmFinoFIX.UpgradeState.Rejected){
                            approveWindow.show();
                            approveWindow.setRecord(detailsForm.record);
                            approveWindow.setStore(detailsForm.store);
                        } else if (detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name)===1) {
                            Ext.Msg.show({
                                title: _('Alert !'),
                                minProgressWidth:250,
                                msg: _("Not Allowed for UnBanked Subscribers"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        } else if (detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name)===CmFinoFIX.UpgradeState.Approved ) {
                            Ext.Msg.show({
                                title: _('Alert !'),
                                minProgressWidth:250,
                                msg: _("Subscriber already Approved"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }else{
                            Ext.Msg.show({
                                title: _('Alert !'),
                                minProgressWidth:250,
                                msg: _("Not Allowed for this Subscriber"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }
                    }
                }
            },
            {
                iconCls: 'mfino-button-resetotp',
                tooltip : _('Send new OTP'),
                itemId : 'sub.reset.otp',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
                    	 Ext.MessageBox.alert(_("Info"), _("Send New OTP not allowed for Unregistered Subscriber "));
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.MDNStatus.Initialized){
                    	 Ext.MessageBox.alert(_("Alert"), _("Send New OTP is allowed only for Initialized Subscribers"));                    	
                    }else{
                    	var mdn=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name);
                    	var name=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name)+" "
                    				+detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name);
                       
                        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to send New OTP for "+mdn+","+name+"?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg= new CmFinoFIX.message.JSResetOTP();
                                msg.m_pMDNID = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                                var params = mFino.util.showResponse.getDisplayParam();
                                params.store = detailsForm.store;
                                params.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            },
            {
            	iconCls: 'mfino-button-close-account',
            	tooltip : _('Subscriber Account Closing'),
            	itemId : 'close.account',
            	handler : function() {
            		if(!detailsForm.record){
            			Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
            		}
            		else {
            			
            			if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name) == CmFinoFIX.MDNStatus.Retired) {
            				Ext.MessageBox.alert(_("Alert"), _("Subscriber Closing is already Retired"));
            				return;
            			}
            			
            			if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name) != CmFinoFIX.MDNStatus.Active) {
            				Ext.MessageBox.alert(_("Alert"), _("Subscriber Closing is allowed only for active subscribers"));
            				return;
            			}
            			
            			subClosing.show();
            			subClosing.form.setDetails(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name));            			
            		}
            	}
	        },
	        {
                iconCls: 'mfino-button-upgrade',
                tooltip : _('Upgrade'),
                itemId : 'sub.details.upgrade',
                handler : function(){
                	
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    } if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                    	 Ext.MessageBox.alert(_("Info"), _("Subscriber Should be Active."));
                    }else{
                    	var mdn=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name);
                    	var name=detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name)+" "
                    				+detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name);
                        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Upgrade Subscriber for "+mdn+", "+name+"?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg = new CmFinoFIX.message.JSSubscriberUpgrade();
                                msg.m_pMDNID = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                                msg.m_paction="default"
                                var params = mFino.util.showResponse.getDisplayParam();
                                //params.store = detailsForm.store;
                                //params.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            },
            {
                iconCls: 'mfino-button-upgrade',
                tooltip : _('Upgrade to Kyc'),
                itemId : 'sub.details.upgrade.kyc',
                id : 'sub.details.upgrade.kyc', 
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    } if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                    	 Ext.MessageBox.alert(_("Info"), _("Subscriber Should be Active."));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name) != CmFinoFIX.SubscriberKYCLevel.NoKyc){
                    	Ext.MessageBox.alert(_("Info"), _("Subscriber's level is not Non-Kyc."));
                    } else{
                    	var amsg = new CmFinoFIX.message.JSSubscriberUpgradeKyc();
                        amsg.m_pID = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                        amsg.m_paction = "default";
                        var params = mFino.util.showResponse.getDisplayParam();
                        var existingEmail =  detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name);
                        var existingfullName =  detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name);
                        mFino.util.fix.send(amsg, params);
                        Ext.apply(params, {
                			success :  function(response){
                				if(response.m_psuccess == true){
                					subscriberUpgradeKycLevelWindow.show();
                					subscriberUpgradeKycLevelWindow.form.setDetails(response, amsg.m_pID, existingEmail, existingfullName); 
                			   }else{
                				   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription));   	   
                			   }
                			}
                		});
                    }
                }
            },
            {
                iconCls : "mfino-button-upgrade-approve",
                tooltip : _('Approve/Reject Subscriber Upgrade'),
                itemId: 'sub.approveUpgrade',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                    	 Ext.MessageBox.alert(_("Info"), _("Subscriber Should be Active!"));
                    }else{
                    	if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctStatus._name)==CmFinoFIX.SubscriberUpgradeStatus.Initialized){
                    		subscriberUpgradeApproveRejectWindow.show();
                        	subscriberUpgradeApproveRejectWindow.setRecord(detailsForm.record);
//                        	subscriberUpgradeApproveRejectWindow.setStore(detailsForm.store);
                    		
                        } else {
                        	Ext.Msg.show({
                                title: _('Alert !'),
                                minProgressWidth:250,
                                msg: _("Subscriber Upgrade Status Should be Initialized State Only!"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }
                    }
                }
            }, {
                iconCls: 'mfino-button-subscriber-addpocket',
                tooltip : _('Request for MBanking Services'),
                itemId : 'emoneysub.add.bankpocket',
                id : 'emoneysub.add.bankpocket', 
                handler : function(){
                	if(!detailsForm.record){
                            Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                        } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                        	 Ext.MessageBox.alert(_("Info"), _("Subscriber Should be Active."));
                        }else{
                        	var newrecord = new subscriberAddPocketWindow.store.recordType();
                        subscriberAddPocketWindow.show();
                        newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.MDNID._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
    					newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.MDN._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
    					newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.FirstName._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name];
    					subscriberAddPocketWindow.setStore(subscriberAddPocketWindow.store);                
    					subscriberAddPocketWindow.setRecord(newrecord);
    					}
                }
            },
            {
                iconCls: 'mfino-button-suspend-subscriber-emoneypocket',
                tooltip : _('Request for suspend E-money pocket'),
                itemId : 'create.sub.suspend.emoneypocket',
                id : 'create.sub.suspend.emoneypocket', 
                handler : function(){
                	if(!detailsForm.record){
                            Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                        	 Ext.MessageBox.alert(_("Info"), _("Subscriber is not Active."));
                    } else {
                        createEmoneyPocketSuspenseRequestWindow.show();
                        createEmoneyPocketSuspenseRequestWindow.setStore(detailsForm.store);                
                        createEmoneyPocketSuspenseRequestWindow.setRecord(detailsForm.record);
    				}
                }
            },
            {
                iconCls: 'mfino-button-suspend-subscriber-emoneypocket-approve',
                tooltip : _('Approve/Reject suspend E-money pocket request'),
                itemId : 'approve.sub.suspend.emoneypocket',
                id : 'approve.sub.suspend.emoneypocket', 
                handler : function(){
                	if(!detailsForm.record){
                            Ext.MessageBox.alert(_("Alert"), _("No Subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                        	 Ext.MessageBox.alert(_("Info"), _("Subscriber is not Active."));
                    } else {
                    	approveRejectEmoneyPocketSuspendRequestWindow.show();
                    	approveRejectEmoneyPocketSuspendRequestWindow.setStore(detailsForm.store);                
                    	approveRejectEmoneyPocketSuspendRequestWindow.setRecord(detailsForm.record);
    				}
                }
            },            
            {
                iconCls : "mfino-button-upgrade-approve",
                tooltip : _('Response Subscriber Upgrade Kyc'),
                itemId: 'sub.details.upgrade.kyc.checker',
                id: 'sub.details.upgrade.kyc.checker',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                    	 Ext.MessageBox.alert(_("Info"), _("Subscriber Should be Active!"));
                    }else{
                    	if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctStatus._name)==CmFinoFIX.SubscriberUpgradeStatus.Initialized){
                    		var amsg = new CmFinoFIX.message.JSSubscriberUpgradeKyc();
                            amsg.m_pID = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                            amsg.m_paction = "default";
                            var params = mFino.util.showResponse.getDisplayParam();
                            mFino.util.fix.send(amsg, params);
                            Ext.apply(params, {
                    			success :  function(response){
                    				if(response.m_psuccess == true){
                    					subscriberUpgradeKycApproveRejectWindow.show();
                    					var comments = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctComments._name);
                    					subscriberUpgradeKycApproveRejectWindow.setDetails(response, amsg.m_pID, comments); 
                    			   }else{
                    				   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription));   	   
                    			   }
                    			}
                    		});
                        } else {
                        	Ext.Msg.show({
                                title: _('Alert !'),
                                minProgressWidth:250,
                                msg: _("Subscriber Upgrade Status Should be Initialized State Only!"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }
                    }
                }
            },
            {
                iconCls : "mfino-button-subscriber-addpocket-approve",
                tooltip : _('Approve for MBanking Services'),
                itemId: 'emoneysub.add.bankpocket.checker',
                id: 'emoneysub.add.bankpocket.checker',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No subscriber selected!"));
                    } else if(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)!=CmFinoFIX.SubscriberStatus.Active){
                    	 Ext.MessageBox.alert(_("Info"), _("Subscriber Should be Active!"));
                    } else{
                    		var amsg = new CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber();
                            amsg.m_pMDNID = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                            amsg.m_paction = "read";
                            var params = mFino.util.showResponse.getDisplayParam();
                            mFino.util.fix.send(amsg, params);
                            Ext.apply(params, {
                    			success :  function(response){
                    				if(response.m_psuccess == true){
                    					var newrecord = new subscriberAddPocketApproveRejectWindow.store.recordType();
                    					
                    					subscriberAddPocketApproveRejectWindow.show();
                                        newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.MDNID._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
                    					newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.MDN._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
                    					newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.FirstName._name] = detailsForm.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name];
                    					newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.ApplicationID._name]=response.m_pApplicationID;
                    					newrecord.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.AccountNumber._name]=response.m_pAccountNumber;
                    					subscriberAddPocketApproveRejectWindow.setStore(subscriberAddPocketApproveRejectWindow.store);                
                    					subscriberAddPocketApproveRejectWindow.setRecord(newrecord);
                    					var comments = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctComments._name);
                    			   }else{
                    				   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription));   	   
                    			   }
                    			}
                    		});
                    }
                }
            }
            ],
            items: [ detailsForm ]
        },
        {
            anchor : "100%, -200",
            layout: "fit",
            items: [ tabPanel ]
        }
        ]
    });

    var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){
        var tb = mainItem.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
        for(i = 0; i < itemIDs.length; i++){
            var itemID = itemIDs[i];
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    });

    searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        listBox.store.baseParams = values;
        listBox.store.baseParams[CmFinoFIX.message.JSSubscriberMDN.StartDateSearch._name] = values.startDate;
        listBox.store.baseParams[CmFinoFIX.message.JSSubscriberMDN.EndDateSearch._name] = values.endDate;
        listBox.store.baseParams[CmFinoFIX.message.JSSubscriberMDN.SubscriberSearch._name] = true;
   
        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(listBox.store.lastOptions.params, values);
        listBox.store.load(listBox.store.lastOptions);
        tabPanel.setMDNRecord(null); //clear the tab panels
    });
    listBox.on("download", function() {
        var queryString;
        var values = searchBox.getForm().getValues();
        var firstNameSearch = values[CmFinoFIX.message.JSSubscriberMDN.FirstNameSearch._name];
        var lastNameSearch = values[CmFinoFIX.message.JSSubscriberMDN.LastNameSearch._name];
        var mdnSearch =  values[CmFinoFIX.message.JSSubscriberMDN.MDNSearch._name];
        var startDateSearch = getUTCdate(values.startDate);
        var endDateSearch = getUTCdate(values.endDate);
        var cardPan = values[CmFinoFIX.message.JSSubscriberMDN.CardPAN._name];
        var stateSearch = values[CmFinoFIX.message.JSSubscriberMDN.UpgradeStateSearch._name];
        var statusSearch = values[CmFinoFIX.message.JSSubscriberMDN.MDNStatus._name];
        queryString = "dType=subscriberMDN";
       
        
        if(firstNameSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.FirstNameSearch._name+"="+firstNameSearch;
        }
        if(lastNameSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.LastNameSearch._name+"="+lastNameSearch;
        }
        if(startDateSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.StartDateSearch._name+"="+startDateSearch;
        }
        if(endDateSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.EndDateSearch._name+"="+endDateSearch;
        }
        if(mdnSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.MDNSearch._name+"="+mdnSearch;
        }
        if(cardPan){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.CardPAN._name+"="+cardPan;
        }
        if(stateSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.UpgradeStateSearch._name+"="+stateSearch;
        }
        if(statusSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.MDNStatus._name+"="+statusSearch;
        }
        queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.SubscriberSearch._name+"="+true;
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });
    
    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });
    
    subscriberUpgradeKycLevelWindow.on("refresh", function() {
    	if(detailsForm.record)
        {
            listBox.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
            listBox.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
            listBox.store.load(listBox.store.lastOptions);
        }
    });
    
    subscriberUpgradeKycApproveRejectWindow.on("refresh", function(mdnid) {
    	if(detailsForm.record)
        {
    		listBox.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
            listBox.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
            listBox.store.load(listBox.store.lastOptions);
        }
    });
    
    listBox.on("clearSelected", function() {
    	 detailsForm.getForm().reset();
         detailsForm.record = null;
    	tabPanel.setMDNRecord(null);        
    });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        tabPanel.setMDNRecord(record);
        var sca = mainItem.getTopToolbar().getComponent('sub.settle.closed.account');
		var scaar = mainItem.getTopToolbar().getComponent('sub.settle.closed.account.approve.reject');
		var ars = mainItem.getTopToolbar().getComponent('sub.approve');
		var resendOtpComponent = mainItem.getTopToolbar().getComponent('sub.reset.otp');
		var subscriberUpgradeKyc = mainItem.getTopToolbar().getComponent('sub.details.upgrade.kyc');
		var subscriberUpgradeKycChecker = mainItem.getTopToolbar().getComponent('sub.details.upgrade.kyc.checker');
		var subscriberUpgrade = mainItem.getTopToolbar().getComponent('sub.details.upgrade');
		var subscriberUpgradeChecker = mainItem.getTopToolbar().getComponent('sub.approveUpgrade');
		var createEmoneyPocketSuspenseRequest =  mainItem.getTopToolbar().getComponent('create.sub.suspend.emoneypocket');
		var approveRejectEmoneyPocketSuspendRequest =  mainItem.getTopToolbar().getComponent('approve.sub.suspend.emoneypocket');
		var addBankPocketToEmoneySubscriber=  mainItem.getTopToolbar().getComponent('emoneysub.add.bankpocket');
		var approveAddBankPocketToEmoneySubscriber =  mainItem.getTopToolbar().getComponent('emoneysub.add.bankpocket.checker');
		if(addBankPocketToEmoneySubscriber){
         if((detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name) == 
			CmFinoFIX.SubscriberKYCLevel.NoKyc)||(detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevel._name) == 
				CmFinoFIX.SubscriberKYCLevel.UnBanked)&&detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.Active){
          addBankPocketToEmoneySubscriber.show(); 
	       }else{
		   addBankPocketToEmoneySubscriber.hide();
	      }
		}
		var amsg = new CmFinoFIX.message.JSPocket();
        amsg.m_pMDNIDSearch = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
        amsg.m_paction = "read";
        amsg.m_plimit = CmFinoFIX.PageSize.Default;
        var params = mFino.util.showResponse.getDisplayParam();
        mFino.util.fix.send(amsg, params);
        Ext.apply(params, {
			success :  function(response){
				if(response.m_psuccess == true){
					var isSVAActive = false;
					for(var i=0; i< response.m_pEntriesCount; i++){
						if(response.m_pEntries[i].m_pPocketType == CmFinoFIX.PocketType.SVA){
							if (response.m_pEntries[i].m_pPocketStatus == CmFinoFIX.PocketStatus.Active) {
								isSVAActive = true;
							}
							if(subscriberUpgradeKyc){
								subscriberUpgradeKyc.show();
							}
							if(subscriberUpgradeKycChecker){
								subscriberUpgradeKycChecker.show();
							}
							if(subscriberUpgrade){
								subscriberUpgrade.hide();
							}
							if(subscriberUpgradeChecker){
								subscriberUpgradeChecker.hide();
							}
						}else if(response.m_pEntries[i].m_pPocketType == CmFinoFIX.PocketType.LakuPandai){
							if(subscriberUpgradeKyc){
								subscriberUpgradeKyc.hide();
							}
							if(subscriberUpgradeKycChecker){
								subscriberUpgradeKycChecker.hide();
							}
							if(subscriberUpgrade){
								subscriberUpgrade.show();
							}
							if(subscriberUpgradeChecker){
								subscriberUpgradeChecker.show();
							}
						}
					}
					if(createEmoneyPocketSuspenseRequest){
					 if (isSVAActive == true) {
						 createEmoneyPocketSuspenseRequest.show();
					 }else {
						 createEmoneyPocketSuspenseRequest.hide();
					 }
				}
			    }
			}
		});
        
        var sumsg = new CmFinoFIX.message.GetSubscriberUpgradeDataRequest();
        sumsg.m_pMDNID = detailsForm.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
        sumsg.m_paction = "read";
        var params = mFino.util.showResponse.getDisplayParam();
        mFino.util.fix.send(sumsg, params);
        Ext.apply(params, {
			success :  function(response){
				if(response.m_psuccess == true){
					if(approveRejectEmoneyPocketSuspendRequest){
					if (CmFinoFIX.SubscriberActivity.Suspend_Emoney_Pocket == response.m_pSubscriberActivity) {
						approveRejectEmoneyPocketSuspendRequest.show();
					}else {
						approveRejectEmoneyPocketSuspendRequest.hide();	
					}
					}
					if(approveAddBankPocketToEmoneySubscriber){
					if (CmFinoFIX.SubscriberActivity.Enable_MBanking_For_Emoney_Subscriber == response.m_pSubscriberActivity) {
						approveAddBankPocketToEmoneySubscriber.show();
					}else {
						approveAddBankPocketToEmoneySubscriber.hide();
					}
					}
				}
			}
		});
		
		
		if(mFino.resendOtpCheck){
			mFino.resendOtpCheck(record,resendOtpComponent);
		}
        if(record.data.Status == 3){         
			 if(sca){
         sca.show();
			 }
			 if(scaar){
		 scaar.show();
			 }
			 if(ars){
		 ars.hide();
			 }		
        } else {
			if(sca){
         sca.hide();
			 }
			 if(scaar){
		 scaar.hide();
        }
			 if(ars){
				ars.show();
			 }
		}
    });

    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 266,
            layout : "anchor",
            items:[ searchBox , listBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        }
        ]
    });
    return panel;
};
