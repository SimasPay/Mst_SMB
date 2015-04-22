Ext.define('Mfino.model.PerRcTransactions', {
	extend: 'Ext.data.Model',
	fields:[{name:'rcCode'},
	        {name:'rcDescription'},
	        {name :'count', type : 'int'}]
});