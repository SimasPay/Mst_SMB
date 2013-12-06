Ext.define('Mfino.model.ResultsPanel', {
	extend: 'Ext.data.Model',
	fields : [ {
		name : 'ID'
	}, {
		name : 'transactionName'		
	}, {
		name : 'transactionAmount'		
	}, {
		name : 'calculatedCharge'		
	}, {
		name : 'transferStatusText'		
	}, {
		name : 'failureReason'		
	}, {
		name : 'transactionTime'		
	}, {
		name : 'sourceMDN'		
	}, {
		name : 'destMDN'		
	}, {
		name : 'sourcePartnerCode'		
	}, {
		name : 'destPartnerCode'		
	}, {
		name : 'MFSBillerCode'		
	}, {
		name : 'accessMethodText'		
	}, {
		name : 'serviceName'		
	}]
});