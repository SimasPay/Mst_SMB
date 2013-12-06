Ext.define('Mfino.model.FloatWalletResultsGrid', {
	extend: 'Ext.data.Model',
	fields : [ {
		name : 'ID'
	}, {
		name : 'serviceChargeTransactionLogID'		
	}, {
		name : 'transactionID'		
	}, {
		name : 'transactionTime'		
	}, {
		name : 'transactionName'		
	}, {
		name : 'sourceMDN'		
	}, {
		name : 'destMDN'		
	}, {
		name : 'sourcePocketID'		
	}, {
		name : 'destPocketID'		
	}, {
		name : 'creditAmount'		
	}, {
		name : 'debitAmount'		
	}, {
		name : 'sourcePocketBalance'		
	}, {
		name : 'destPocketBalance'		
	}, {
		name : 'sourcePocketClosingBalance'		
	}, {
		name : 'destPocketClosingBalance'		
	}, {
		name : 'transferStatusText'		
	}, {
		name : 'commodityText'		
	}, {
		name : 'accessMethodText'		
	}]
});