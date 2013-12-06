Ext.define('Mfino.model.PerChannelTransactions', {
	extend: 'Ext.data.Model',
	fields : [ {
		name : 'channelName'
	},{
		name : 'channelSourceApplication',
		type : 'int'
	}, {
		name : 'successful',
		type : 'int'
	}, {
		name : 'failed',
		type : 'int'
	}, {
		name : 'pending',
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