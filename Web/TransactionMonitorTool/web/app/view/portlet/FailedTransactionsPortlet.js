Ext.define('Mfino.view.portlet.FailedTransactionsPortlet', {

    extend: 'Ext.grid.Panel',
    alias: 'widget.failedTransactionsPortlet',
    id: 'failed-transactions-grid',

    initComponent: function(){
    	var failedStore = Ext.create('Mfino.store.FailedTransactions');
    	failedStore.load();
    	
    	var task = {
            	run: function() {            		
            		failedStore.load();
            	},
                interval: 300000 //5 mins
            };
        var runner = new Ext.util.TaskRunner();        		 
        runner.start(task);
    	
        Ext.apply(this, {
            width: 635, 
            height: 200,            
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
				{text:"Transaction Name",width:110,dataIndex:"transactionType",hideable: false,menuDisabled: true},
				{text:"Date",width:110,dataIndex:"txnDateTime",hideable: false,menuDisabled: true},
				{text:"RC Code",width:110,dataIndex:"rcCode",hideable: false,menuDisabled: true},
				{text:"Failure Reason",flex:1,dataIndex:"reason",hideable: false,menuDisabled: true}]
        });

        this.callParent(arguments);
    }
});
