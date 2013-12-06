Ext.define('Mfino.model.FailedTransactions', {
	extend: 'Ext.data.Model',
	fields:[{name:"mobileNumber"},
	        {name:"refID"},
			{name:"amount"},
			{name:"transactionType"},
			{name:"channelName"},
			{name:"reason"}]
});