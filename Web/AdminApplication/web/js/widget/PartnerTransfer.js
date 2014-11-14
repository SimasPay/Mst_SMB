/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PartnerTransfer = function(config) {
	var localConfig = Ext.apply({}, config);
	localConfig = Ext.applyIf(localConfig, {
	// bodyStyle: 'padding:5px 5px 0',
	// defaultType: 'textfield',
	// frame : true
	});
	mFino.widget.PartnerTransfer.superclass.constructor.call(this, localConfig);
};

Ext.extend(	mFino.widget.PartnerTransfer,Ext.FormPanel,
				{

					initComponent : function() {
						this.labelWidth = 120;
						this.labelPad = 20;
						this.autoScroll = true;
						this.frame = true;
						this.emoneyPocketStore = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket);
						this.items = [ {
							layout : 'form',
							items : [
									
									{
										xtype : 'displayfield',
										fieldLabel : 'From',
										itemId : 'transfer.form.sourcePocket',
										anchor : '100%'
//										name : CmFinoFIX.message.JSMoneyTransfer.SourcePocketID._name
									},
									{
										xtype : 'displayfield',
										fieldLabel : 'Balance',
										itemId : 'transfer.form.balance',
										allowBlank : false
										// anchor : '100%',
//										name : CmFinoFIX.message.JSMoneyTransfer.FirstName._name
									},
									{
					                	 xtype : "combo",
					                     anchor : '100%',
					                     fieldLabel :_("To Account"),
					                     allowBlank: false,
					                     itemId : 'transfer.form.destPocket',
					                     triggerAction: "all",
					                     forceSelection : true,
					                     lastQuery: '',
					                     addEmpty : false,
					                     editable: false,
					                     store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
					                     displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
					                     valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
					                     hiddenName: CmFinoFIX.message.JSMoneyTransfer.DestPocketID._name,					                     
					                     name : CmFinoFIX.message.JSMoneyTransfer.DestPocketID._name
					                },
									{
										xtype : 'textfield',
										fieldLabel : _('Amount'),
										allowBlank : false,
										vtype : 'numbercomma',
										anchor : '100%',
										labelSeparator : '',
										emptyText : _('eg: 1125'),
										maxLength : 16,
										name : CmFinoFIX.message.JSMoneyTransfer.Amount._name,
										listeners : {
											blur : function(field) {
												field.setValue(Ext.util.Format.number(field.getValue(),'0,000'));
											}
										}
									},
									{
										xtype : 'textfield',
										fieldLabel : 'Comment',
										labelSeparator : '',
										// itemId : 'sub.form.firstname',
										allowBlank : false,
										anchor : '100%',
										name : CmFinoFIX.message.JSMoneyTransfer.Description._name
									} ]
						} ];

						mFino.widget.PartnerTransfer.superclass.initComponent.call(this);
						markMandatoryFields(this.form);
						this.emoneyPocketStore.on("load", this.onEmoneyPocketLoad.createDelegate(this));
					},
					onEmoneyPocketLoad:function(){
						this.mask.hide();
						var emoneyPocket = this.emoneyPocketStore.getAt(0);
						if(emoneyPocket.get(CmFinoFIX.message.JSPocket.Entries.CurrentBalance._name)===0){
							Ext.ux.Toast.msg(_("Message"), _("Emoney Pocket Balance is Zero."));
							this.formWindow.hide();
						}else{
							this.form.items.get("transfer.form.sourcePocket").setValue(emoneyPocket.get(CmFinoFIX.message.JSPocket.Entries.PocketDispText._name));
							this.form.items.get("transfer.form.balance").setValue(emoneyPocket.get(CmFinoFIX.message.JSPocket.Entries.CurrentBalance._name));
						}						
					},
					transfer : function(formWindow) {
						var values = this.form.getValues();
						var transferAmount = values[CmFinoFIX.message.JSMoneyTransfer.Amount._name];
						transferAmount = transferAmount.replace(/\,/g, '');
						if(transferAmount<=0)
							Ext.ux.Toast.msg(_("Message"), _("Enter a valid amount."));
						else if (this.getForm().isValid()) {
							formWindow.disable();
							var mask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait... Processing transaction"});
				            mask.show();
							var msg = new CmFinoFIX.message.JSMoneyTransfer();
							msg.m_pAmount = transferAmount;
							msg.m_pSourcePocketID = this.emoneyPocketStore.getAt(0).get(CmFinoFIX.message.JSPocket.Entries.ID._name);
							msg.m_pDestPocketID = values[CmFinoFIX.message.JSMoneyTransfer.DestPocketID._name];
							msg.m_pDescription = values[CmFinoFIX.message.JSMoneyTransfer.Description._name];
							msg.m_pSourceMDN = this.emoneyPocketStore.getAt(0).get(CmFinoFIX.message.JSPocket.Entries.SubsMDN._name);
							msg.m_pDestMDN = this.emoneyPocketStore.getAt(0).get(CmFinoFIX.message.JSPocket.Entries.SubsMDN._name);
							var params = {
								success : function(response) {
									mask.hide();
									formWindow.enable();
									Ext.Msg.show({
										title : 'Info',
										minProgressWidth : 600,
										msg : response.m_pErrorDescription,
										buttons : Ext.MessageBox.OK,
										multiline : false
									});
								},
								failure : function(response) {
									mask.hide();
									formWindow.enable();
									Ext.Msg.show({
												title : 'Error',
												minProgressWidth : 250,
												msg : "Your transaction is having a problem. Please check your recent transaction on pending transaction list or contact Customer Care :881",
												buttons : Ext.MessageBox.OK,
												multiline : false
											});
								}
							};
							params.formWindow = formWindow;
							params.formWindow = formWindow;
							mFino.util.fix.send(msg, params);
						}
					},
					setDetails : function(formWindow) {
						 // get emoney pocket
						this.emoneyPocketStore.baseParams[CmFinoFIX.message.JSPocket.PartnerIDSearch._name] = mFino.auth.getPartnerId();
			            this.emoneyPocketStore.baseParams[CmFinoFIX.message.JSPocket.PocketType._name]=CmFinoFIX.PocketType.SVA;
			            this.emoneyPocketStore.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;
			            this.emoneyPocketStore.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = CmFinoFIX.PocketStatus.Active;
			            this.emoneyPocketStore.load();	
			            //get BankPockets
			            var store=this.form.items.get("transfer.form.destPocket").store;
			            store.baseParams[CmFinoFIX.message.JSPocket.PartnerIDSearch._name] = mFino.auth.getPartnerId();
			            store.baseParams[CmFinoFIX.message.JSPocket.PocketType._name]=CmFinoFIX.PocketType.BankAccount;
			            store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;
			            store.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = CmFinoFIX.PocketStatus.Active;
			            store.load();
			            this.mask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait... getting Details.."});
			            this.mask.show();
			            this.formWindow = formWindow;
					}
				});
Ext.reg("transfer", mFino.widget.PartnerTransfer);
