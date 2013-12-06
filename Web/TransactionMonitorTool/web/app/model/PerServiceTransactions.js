Ext.define('Mfino.model.PerServiceTransactions', {
	extend: 'Ext.data.Model',
	fields : [ {
		name : 'serviceName'
	}, {
		name : 'serviceID',
		type : 'int'
	}, {
		name : 'count',
		type : 'int'
	}, {
		name : 'successful',
		type : 'int'
	}, {
		name : 'pending',
		type : 'int'
	}, {
		name : 'failed',
		type : 'int'
	}, {
		name : 'processing',
		type : 'int'
	}, {
		name : 'reversals',
		type : 'int'
	}, {
		name : 'intermediate',
		type : 'int'
	} ]
});