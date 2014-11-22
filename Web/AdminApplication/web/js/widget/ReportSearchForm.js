/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ReportSearchForm = function(config) {

	var localConfig = Ext.apply({}, config);
	localConfig = Ext
			.applyIf(
					localConfig,
					{
						layout : 'form',
						autoScroll : true,
						frame : true,
						title : _('Report '),
						height : 370,
						items : [
								{
									xtype : 'fieldset',
									autoHeight : true,
									layout : 'column',
									width : 895,									
									items : [ {
										columnWidth : 1,
										align : 'centre',
										layout : 'form',
										labelWidth : 80,
										items : [ {
											xtype : "remotedropdown",
											fieldLabel : _('Report Name'),
											labelSeparator : '',
											// itemId: "report.name",
											anchor : '98%',
											triggerAction : "all",
											forceSelection : true,
											pageSize : 20,
											allowBlank : false,
											addEmpty : true,
											emptyText : _('<select one..>'),
											store : new FIX.FIXStore(
													mFino.DATA_URL,
													CmFinoFIX.message.JSOfflineReport),
											displayField : CmFinoFIX.message.JSOfflineReport.Entries.Name._name,
											valueField : CmFinoFIX.message.JSOfflineReport.Entries.ReportClass._name,
											name : CmFinoFIX.message.JSReport.ReportName._name,
											hiddenName : CmFinoFIX.message.JSReport.ReportName._name,
											listeners : {

												select : function(field) {
													this
															.findParentByType(
																	"reportsearchform")
															.showReportParameters(
																	field
																			.getValue());
												},
												specialkey : this.enterKeyHandler
														.createDelegate(this)
											}
										}

										]
									} ]

								},
								{
									xtype : 'fieldset',
									title : 'Report Parameters',
									autoHeight : true,
									width: 895,
									layout : 'column',
									items : [
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : 'fromDate',
												items : [ {
													xtype : 'datefield',
													fieldLabel : 'From Date',
													editable : false,
													//allowBlank : false,
													itemId : 'start',
													anchor : '80%',
													format : 'd/m/Y',
													maxValue : new Date().add(
															'd', -1),
													maxText : 'Date should not be future date',
													name : CmFinoFIX.message.JSReport.ReportStartDate._name
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : 'toDate',
												items : [ {
													xtype : 'datefield',
													fieldLabel : 'To Date',
													editable : false,
													itemId : 'end',
													anchor : '80%',
													format : 'd/m/Y',
													//allowBlank : false,
													maxValue : new Date().add('d', -1),
													maxText : 'Date should not be future date',
													name : CmFinoFIX.message.JSReport.ReportEndDate._name
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "email",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Email"),
													vtype: 'email',
													labelSeparator : '',
													maxLength : 40,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.Email._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : 'fromUpdatedTime',
												items : [ {
													xtype : 'datefield',
													fieldLabel : 'Updated From',
													editable : false,
													allowBlank : true,
													itemId : 'start',
													anchor : '80%',
													format : 'd/m/Y',
													maxValue : new Date().add(
															'd', -1),
													maxText : 'Date should not be future date',
													name : CmFinoFIX.message.JSReport.FromUpdatedTime._name
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : 'toUpdatedTime',
												items : [ {
													xtype : 'datefield',
													fieldLabel : 'Updated To',
													editable : false,
													itemId : 'end',
													anchor : '80%',
													format : 'd/m/Y',
													allowBlank : true,
													maxValue : new Date(),
													maxText : 'Date should not be future date',
													name : CmFinoFIX.message.JSReport.ToUpdatedTime._name
												} ]
											},
											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "subscriberStatus",
												items : [ {
													xtype : "enumdropdown",
													fieldLabel : _('Subscriber Status'),
													labelSeparator : '',
													anchor : '80%',
													triggerAction : "all",
													forceSelection : true,
													pageSize : 20,
													addEmpty : true,
													emptyText : _('<select one..>'),
													enumId : CmFinoFIX.TagID.SubscriberStatus,
													// store: new
													// FIX.FIXStore(mFino.DATA_URL,
													// CmFinoFIX.message.JSSubscriberMDN),
													// displayField:
													// CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberStatus._name,
													// valueField :
													// CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberStatus._name,
													name : CmFinoFIX.message.JSReport.SubscriberStatus._name
												} ]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "pocketTemplateDescription",
												items : [ {
													xtype : "remotedropdown",
													labelWidth : 100,
													fieldLabel : _('Pocket Template Description'),
													labelSeparator : '',
													emptyText : _('<select one..>'),
													anchor : '80%',
													lastQuery : '',
													pageSize : 15,
													store : new FIX.FIXStore(
															mFino.DATA_URL,
															CmFinoFIX.message.JSPocketTemplate),
													displayField : CmFinoFIX.message.JSPocketTemplate.Entries.Description._name,
													valueField : CmFinoFIX.message.JSPocketTemplate.Entries.ID._name,
													hiddenName : CmFinoFIX.message.JSReport.PocketTemplateID._name,
													name : CmFinoFIX.message.JSReport.PocketTemplateID._name
												}

												]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "subscriberRestrictions",
												items : [ {
													xtype : "enumdropdown",
													fieldLabel : _('Subscriber Restrictions'),
													labelSeparator : '',
													anchor : '80%',
													triggerAction : "all",
													forceSelection : true,
													pageSize : 20,
													addEmpty : true,
													emptyText : _('<select one..>'),
													enumId : CmFinoFIX.TagID.SubscriberRestrictions,
													name : CmFinoFIX.message.JSReport.SubscriberRestrictions._name
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "sourceMDN",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Source MDN"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.SourceMDN._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "transactionType",
												items : [ {
													xtype : "remotedropdown",
													labelWidth : 100,
													fieldLabel : _('Transaction Type'),
													labelSeparator : '',
													emptyText : _('<select one..>'),
													anchor : '80%',
													lastQuery : '',
													pageSize : 15,
													store : new FIX.FIXStore(
															mFino.DATA_URL,
															CmFinoFIX.message.JSTransactionType),
													displayField : CmFinoFIX.message.JSTransactionType.Entries.TransactionName._name,
													valueField : CmFinoFIX.message.JSTransactionType.Entries.ID._name,
													hiddenName : CmFinoFIX.message.JSReport.TransactionTypeID._name,
													name : CmFinoFIX.message.JSReport.TransactionTypeID._name
												} ]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "transactionStatus",
												items : [ {
													xtype : "enumdropdown",
													fieldLabel : _('Transaction Status'),
													labelSeparator : '',
													anchor : '80%',
													triggerAction : "all",
													forceSelection : true,
													pageSize : 20,
													addEmpty : true,
													emptyText : _('<select one..>'),
													enumId : CmFinoFIX.TagID.SCTLStatus,
													name : CmFinoFIX.message.JSReport.TransactionStatus._name
												} ]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "destinationPocketStatus",
												items : [ {
													xtype : "enumdropdown",
													fieldLabel : _('Destination Pocket Status'),
													labelSeparator : '',
													anchor : '80%',
													triggerAction : "all",
													forceSelection : true,
													pageSize : 20,
													addEmpty : true,
													emptyText : _('<select one..>'),
													enumId : CmFinoFIX.TagID.PocketStatus,
													name : CmFinoFIX.message.JSReport.DestinationPocketStatus._name
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "destMDN",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Destination MDN"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.DestMDN._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "partnerCode",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Partner Code"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.PartnerCode._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "billerCode",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Biller Code"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.BillerCode._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "partnerType",
												items : [ {
													xtype : "enumdropdown",
													fieldLabel : _('Partner Type'),
													labelSeparator : '',
													anchor : '80%',
													triggerAction : "all",
													forceSelection : true,
													pageSize : 20,
													addEmpty : true,
													emptyText : _('<select one..>'),
													enumId : CmFinoFIX.TagID.BusinessPartnerTypePartner,
													name : CmFinoFIX.message.JSReport.PartnerType._name
												} ]
											},

											{
												columnWidth : .5,
												layout : 'form',
												labelWidth : 80,
												itemId : "settlementStatus",
												items : [ {
													xtype : "enumdropdown",
													fieldLabel : _('Settlement Status'),
													labelSeparator : '',
													anchor : '80%',
													triggerAction : "all",
													forceSelection : true,
													pageSize : 20,
													addEmpty : true,
													emptyText : _('<select one..>'),
													enumId : CmFinoFIX.TagID.SettlementStatus,
													name : CmFinoFIX.message.JSReport.SettlementStatus._name
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "csrUserName",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("CSR User Name"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.CSRUserName._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "idCardNo",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("ID Card No"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.IDNumber._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "mdn",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("MDN"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.MDN._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "merchantID",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Merchant ID"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.MerchantID._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "merchantAccount",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("MerchantAccount"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.MerchantAccount._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},

											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "referenceNo",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Reference ID"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.ReferenceNumber._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "sourcePartnerCode",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Source PartnerCode"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.SourcePartnerCode._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "destPartnerCode",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Dest PartnerCode"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.DestPartnerCode._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "channelName",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("Channel Name"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.ChannelName._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											},
											{
												columnWidth : 0.5,
												layout : 'form',
												labelWidth : 80,
												itemId : "bankRRN",
												items : [ {
													xtype : 'textfield',
													allowDecimals : false,
													fieldLabel : _("BankRRN"),
													labelSeparator : '',
													maxLength : 13,
													minValue : 0,
													name : CmFinoFIX.message.JSReport.BankRetrievalReferenceNumber._name,
													anchor : '80%',
													listeners : {
														specialkey : this.enterKeyHandler
																.createDelegate(this)
													}
												} ]
											}

									]
								},
								{
									layout : 'column',
									items : [
											{
												columnWidth : 0.4,
												layout : 'form',
												items : [ {
													xtype : 'displayfield',
													anchor : '50%'
												} ]
											},
											{
												columnWidth : 0.1,
												layout : 'form',
												items : [ {
													xtype : 'button',
													fieldLabel : '',
													text : 'Get Report',
													anchor : '85%',
													handler : this.searchHandler
															.createDelegate(this)
												} ]
											},
											{
												columnWidth : 0.1,
												layout : 'form',
												items : [ {
													xtype : 'button',
													fieldLabel : '',
													text : 'Reset',
													anchor : '85%',
													handler : this.resetHandler
															.createDelegate(this)
												} ]
											} ]
								} ]
					});

	mFino.widget.ReportSearchForm.superclass.constructor
			.call(this, localConfig);

};

Ext
		.extend(
				mFino.widget.ReportSearchForm,
				Ext.FormPanel,
				{

					initComponent : function() {
						mFino.widget.ReportSearchForm.superclass.initComponent
								.call(this);
						this.addEvents("getReport");
						this.on("render", function() {
							this.reloadRemoteDropDown();
						});
						// this.hideItems();
					},

					showReportParameters : function(value) {

						this.hideItems();
						this.find('itemId', 'fromDate')[0].show();
						this.find('itemId', 'toDate')[0].show();
						this.find('itemId','email')[0].show();
						if (value === "SubscriberReport-Detailed"
								|| value === "SubscriberReport-Basic"
								|| value === "SubscriberDetailsReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'subscriberStatus')[0].show();
							this.find('itemId', 'pocketTemplateDescription')[0]
									.show();
							this.find('itemId', 'subscriberRestrictions')[0]
									.show();
						} else if (value === "EmoneyCashinReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'transactionStatus')[0].show();
						} else if (value === "EmoneyFinancialTransactionReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'sourceMDN')[0].show();
							this.find('itemId', 'transactionType')[0].show();
							this.find('itemId', 'transactionStatus')[0].show();
							this.find('itemId', 'destinationPocketStatus')[0]
									.show();
						} else if (value === "UnclaimedMoneyTransferReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'sourceMDN')[0].show();
							this.find('itemId', 'destMDN')[0].show();
						} else if (value === "EmoneyNonFinancialTransactionReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'sourceMDN')[0].show();
							this.find('itemId', 'transactionType')[0].show();
							this.find('itemId', 'transactionStatus')[0].show();
						} else if (value === "PurchaseReport"
								|| value === "BillsPaymentReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'partnerCode')[0].show();
							this.find('itemId', 'billerCode')[0].show();
							this.find('itemId', 'transactionStatus')[0].show();
						} else if (value === "PartnersReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'partnerType')[0].show();
						} else if (value === "PartnersTransactionReport"
								|| value === "PartnersTransactionCumulativeReport"
								|| value === "BillersTransactionCumulativeReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'partnerCode')[0].show();
							this.find('itemId', 'billerCode')[0].show();
						} else if (value === "PendingEmoneyTransactionsReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'transactionType')[0].show();
						} else if (value === "PartnersSettlementReport") {
							this.find('itemId', 'partnerCode')[0].show();
							this.find('itemId', 'settlementStatus')[0].show();
						} else if (value === "ResolvedEMoneyTransactionsReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'fromUpdatedTime')[0].show();
							this.find('itemId', 'toUpdatedTime')[0].show();
							this.find('itemId', 'transactionType')[0].show();
							this.find('itemId', 'csrUserName')[0].show();
						} else if (value === "EmoneyCashoutReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'transactionStatus')[0].show();
							this.find('itemId', 'sourceMDN')[0].show();
						} else if (value === "AMLReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							this.find('itemId', 'idCardNo')[0].show();
						} else if (value === "OmnibusTransactionReport") {
							// this.find('itemId','fromDate')[0].show();
							// this.find('itemId','toDate')[0].show();
							//this.find('itemId', 'merchantID')[0].show();
							//this.find('itemId', 'merchantAccount')[0].show();
							//this.find('itemId', 'referenceNo')[0].show();
						} else if (value === "EmoneyMovementReport-L1SummaryPerStatus"
								|| value === "EmoneyMovementReport-L1SummaryPerSubscriber") {
							this.find('itemId', 'subscriberStatus')[0].show();
							this.find('itemId', 'subscriberRestrictions')[0]
									.show();
						} else if (value === "EmoneyMovementReport-L2AdditionSummaryPerSubscriber"
								|| value === "EmoneyMovementReport-L2DeductionSummaryPerSubscriber") {
							this.find('itemId', 'subscriberStatus')[0].show();
							this.find('itemId', 'subscriberRestrictions')[0]
									.show();
							this.find('itemId', 'mdn')[0].show();
						}else if(value === "TransactionReport"){
							this.find('itemId', 'billerCode')[0].show();
							this.find('itemId', 'transactionStatus')[0].show();
						}else if(value === "TransactionMonthlyReport"){
//							this.find('itemId', 'billerCode')[0].show();
//							this.find('itemId', 'transactionStatus')[0].show();
						}else if(value === "TransactionExcelReport"){
							this.find('itemId', 'transactionStatus')[0].show();
							this.find('itemId', 'transactionType')[0].show();
							this.find('itemId', 'sourceMDN')[0].show();
							this.find('itemId', 'destMDN')[0].show();
							this.find('itemId', 'billerCode')[0].show();
							this.find('itemId', 'referenceNo')[0].show();
							this.find('itemId', 'sourcePartnerCode')[0].show();
							this.find('itemId', 'destPartnerCode')[0].show();
							this.find('itemId', 'channelName')[0].show();
							this.find('itemId', 'bankRRN')[0].show();
						}
						
					},

					hideItems : function() {
						// this.find('itemId','fromDate')[0].hide();
						// this.find('itemId','toDate')[0].hide();
						this.find('itemId', 'fromUpdatedTime')[0].hide();
						this.find('itemId', 'toUpdatedTime')[0].hide();
						this.find('itemId', 'subscriberStatus')[0].hide();
						this.find('itemId', 'pocketTemplateDescription')[0]
								.hide();
						this.find('itemId', 'subscriberRestrictions')[0].hide();
						this.find('itemId', 'sourceMDN')[0].hide();
						this.find('itemId', 'transactionType')[0].hide();
						this.find('itemId', 'transactionStatus')[0].hide();
						this.find('itemId', 'destinationPocketStatus')[0]
								.hide();
						this.find('itemId', 'destMDN')[0].hide();
						this.find('itemId', 'partnerCode')[0].hide();
						this.find('itemId', 'billerCode')[0].hide();
						this.find('itemId', 'partnerType')[0].hide();
						this.find('itemId', 'settlementStatus')[0].hide();
						this.find('itemId', 'csrUserName')[0].hide();
						this.find('itemId', 'mdn')[0].hide();
						this.find('itemId', 'merchantID')[0].hide();
						this.find('itemId', 'merchantAccount')[0].hide();
						this.find('itemId', 'referenceNo')[0].hide();
						this.find('itemId', 'idCardNo')[0].hide();
						this.find('itemId', 'sourcePartnerCode')[0].hide();
						this.find('itemId', 'destPartnerCode')[0].hide();
						this.find('itemId', 'channelName')[0].hide();
						this.find('itemId', 'bankRRN')[0].hide();
					},

					reloadRemoteDropDown : function() {
						this.getForm().items.each(function(item) {
							if (item.getXType() == 'remotedropdown') {
								item.reload();
							}
						});
					},
					enterKeyHandler : function(f, e) {
						if (e.getKey() === e.ENTER) {
							this.searchHandler();
						}
					},
					searchHandler : function() {
						if (this.getForm().isValid()) {
							var values = this.getForm().getValues();

							//			if(values.ReportStartDate){
							//			values.ReportStartDate = this.find('itemId','start')[0].getValue().format('Ymd-H:i:s:u');
							//			}
							//			if(values.ReportEndDate){
							//			values.ReportEndDate =this.find('itemId','end')[0].getValue().format('Ymd-H:i:s:u');
							//			}
//							if (!values.ReportStartDate) {
//								this.fireEvent("getReport", values);
							if (!values.ReportStartDate) {
								Ext.ux.Toast.msg(_("Error"), _("From Date can't be empty"), 5);
							} else if (!values.ReportEndDate) {
								Ext.ux.Toast.msg(_("Error"), _("To Date can't be empty"), 5);
							}  else if (!values.Email) {
								Ext.ux.Toast.msg(_("Error"), _("Email can't be empty"), 5);
							} else if (this.find('itemId', 'start')[0]
									.getValue() <= this.find('itemId', 'end')[0]
									.getValue()) {
								this.fireEvent("getReport", values);
							} else {
								Ext.ux.Toast.msg(_("Error"),
										_("FromDate must be less than ToDate"),
										5);
							}
						} else {
							Ext.ux.Toast
									.msg(
											_("Error"),
											_("Some fields have invalid information. <br/> Please fix the errors before search"),
											5);
						}
					},
					resetHandler : function(){
						this.getForm().reset();
						this.find('itemId', 'start')[0].setValue("");
						this.find('itemId', 'end')[0].setValue("");
					}
				});
Ext.reg('reportsearchform', mFino.widget.ReportSearchForm);
