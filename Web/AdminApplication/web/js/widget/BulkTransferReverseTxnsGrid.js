/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkTransferReverseTxnsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSServiceChargeTransactions);
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        height: 395,
        frame:true,
        loadMask : true,
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        autoScroll : true,
        columns: [
	          {
	              header: _('Reference ID'),
	              width : 80,
	              dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name
	          },
	          {
	              header: _('Transaction Type'),
	              width : 150,
	              dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name
	          },
	          {
	              header: _('Transaction Amount'),
	              width : 150,
	              dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionAmount._name
	          },        
	          {
	              header: _('Transaction Time'),
	              width : 150,
	              dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionTime._name,
	              renderer: "date"
	          },
	          {
	              header: _('Status'),
	              width : 150,
	              dataIndex: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransferStatusText._name
	          }       
        ]
    });

    mFino.widget.BulkTransferReverseTxnsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransferReverseTxnsGrid, Ext.grid.GridPanel, {

});

Ext.reg("BulkTransferReverseTxnsGrid", mFino.widget.BulkTransferReverseTxnsGrid);
