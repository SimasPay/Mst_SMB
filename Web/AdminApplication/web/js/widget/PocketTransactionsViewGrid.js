/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PocketTransactionsViewGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl,
				CmFinoFIX.message.JSCommodityTransfer);
	}

	// this.action = new Ext.ux.grid.RowActions({
	// header:'',
	// keepSelection:true,
	// actions:[{
	// iconCls:'mfino-button-View',
	// tooltip: _('View BulkUpload Details')
	// }
	// ]
	// });
	 var sbun = new Ext.Toolbar.Button({
	 // pressed: true,
	 enableToggle: false,
	 iconCls: 'mfino-button-excel',
	 tooltip : _('Export data to Excel Sheet'),
	 handler : this.excelView.createDelegate(this)
	 });
	 
	 var sPdfBtn = new Ext.Toolbar.Button({
		  pressed: true,
		 enableToggle: true,
		 iconCls: 'mfino-button-pdf',
		 text:'Print Account Statement',
		 tooltip : _('Export data to PDF'),
		 handler : this.pdfView.createDelegate(this,['pdf'])
		 });
	localConfig = Ext
			.applyIf(
					localConfig || {},
					{
						dataUrl : "fix.htm",
						layout : 'fit',
						width:800,
					    height:411,
//					    anchor : "100%, -181",
						frame : true,
						loadMask : true,
						viewConfig: { emptyText: Config.grid_no_data },
						bbar : new Ext.PagingToolbar( {
							store : localConfig.store,
							displayInfo : true,
							pageSize : CmFinoFIX.PageSize.Default
						}),
						autoScroll : true,
						columns : [
						   		{
								    header: _("Reference ID"),
								    dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.ServiceChargeTransactionLogID._name
								},						           
/*								{
									header : _("Transfer ID"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionID._name
								},
						        {
						            header: _("Bank RRN"),
						            dataIndex: CmFinoFIX.message.JSCommodityTransfer.Entries.BankRetrievalReferenceNumber._name
						        },								
*/								{
									header : _("Date"),
									renderer : "date",
									width : 135,
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.StartTime._name
								},
								
								{
									header : _("Transaction Type"),
									width : 120,
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionTypeText._name
								},
								
								{
									header : _("Internal Txn Type"),
									width : 120,
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.InternalTxnType._name
								},								
								
/*								{
									header : _("Source"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDN._name
//									renderer : function(value, a, b) {
//										var credit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name];
//										var debit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name];
//										if (credit !== null) {
//											return value;
//										} else {
//											return b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name];
//										}
//									}
								},
								{
									header : _("Destination"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.DestMDN._name
									
								},
								{
									header : _("Source PocketID"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.SourcePocketID._name,
									renderer : function(value, a, b) {
										var credit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name];
										var debit = b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name];
										if (credit !== null) {
											return value;
										} else {
											return b.data[CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketID._name];
										}
									}
								},
								{
									header : _("Destination PocketID"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.DestPocketID._name
								},
*/								{
									header : _("Credit Amount"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.CreditAmount._name,
									renderer : function(value){
									if(value===null){
										return "--";
									}else{
										return Ext.util.Format.money(value);
									}
									}
								},
								{
									header : _("Debit Amount"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.DebitAmount._name,
									renderer : function(value){
									if(value===null){
										return "--";
									}else{
										return Ext.util.Format.money(value);
									}
									}
								},
								{
									header : _("Status"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.TransferStatusText._name
								},
								{
									header : _("Commodity"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.CommodityText._name
								},
								
								{
									header : _("Channel Name"),
									dataIndex : CmFinoFIX.message.JSCommodityTransfer.Entries.AccessMethodText._name
								}]
					});

	mFino.widget.PocketTransactionsViewGrid.superclass.constructor.call(this,
			localConfig);
	if(mFino.auth.isEnabledItem('sub.pockettxn.download.excel')){	
		 this.getBottomToolbar().add('->',sbun);
	}
	 this.getBottomToolbar().add(sPdfBtn);
	
};

Ext.extend(mFino.widget.PocketTransactionsViewGrid, Ext.grid.GridPanel, {
		 excelView: function(){
				 this.fireEvent("filedownload");
				 },
		pdfView: function(format){
					 this.fireEvent("filedownload",format);
				 }
		});

Ext.reg("pockettransactionsviewgrid", mFino.widget.PocketTransactionsViewGrid);
