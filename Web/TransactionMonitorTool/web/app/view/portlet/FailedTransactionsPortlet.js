Ext.define('Mfino.view.portlet.FailedTransactionsPortlet', {

    extend: 'Ext.grid.Panel',
    alias: 'widget.failedTransactionsPortlet',
    id: 'failed-transactions-grid',

    initComponent: function(){
    	var failedStore = Ext.create('Mfino.store.FailedTransactions');
    	failedStore.load();
    	
        Ext.apply(this, {
            width: 600, 
            height: 180,            
            store: failedStore,
            stripeRows:true,
			columnLines:true,
			viewConfig: { emptyText: 'No transactions' },
			columns:[
				{id:"mobileNumber",text:"Mobile Number",width:110,dataIndex:"mobileNumber",hideable: false, menuDisabled: true,
					renderer: function(val, p, record){
						return '<span class="failed-transactions-link" linkRefID="'+record.data.refID+'">'+val+'</span>';
					}
				},
				{text:"Amount",width:75,dataIndex:"amount",hideable: false,menuDisabled: true},
				{text:"Transaction Type",width:120,dataIndex:"transactionType",hideable: false,menuDisabled: true},
				{text:"Channel",width:90,dataIndex:"channelName",hideable: false,menuDisabled: true},
				{text:"Reason For Failure",flex:1,dataIndex:"reason",hideable: false,menuDisabled: true}]
        });

        this.callParent(arguments);
    }
});
