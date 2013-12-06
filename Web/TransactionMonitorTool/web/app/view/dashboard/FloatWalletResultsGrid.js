Ext.define('Mfino.view.dashboard.FloatWalletResultsGrid', {

    extend: 'Ext.grid.GridPanel',
    alias: 'widget.floatWalletResultsGrid',
    id: 'floatWalletResultsGrid',	
	flex: 3,
	

    initComponent: function(){  
    	var gridStore = Ext.create("Mfino.store.FloatWalletResultsGrid");   
    	var myParams = this.initialConfig.searchParams;
    	gridStore.on('beforeload',function(store, operation,eOpts){
    		if(myParams['page']){
            	myParams['page'] = store.currentPage;
            	myParams['start'] = (store.currentPage-1)* myParams['limit'];
            }
            operation.params = myParams;            
    	},this);
    	gridStore.load();
    	Ext.apply(this, {    		
    		store: gridStore,
            frame: false,
            loadMask : true,
            viewConfig: { emptyText: 'No transactions' },
            bbar: new Ext.PagingToolbar({
                store: gridStore, 
                displayInfo: true                    
            }),
            selType: 'rowmodel',
            columns:[
		            {
		                header: "Reference ID",
		                width : 80,
		                dataIndex: 'serviceChargeTransactionLogID'
		            },  
		            {
		                header: "Transfer ID",
		                width : 80,
		                dataIndex: 'transactionID'		                
		            },
		            {
		                header: "Date",
		                width : 135,
		                dataIndex: 'transactionTime',
		                renderer: function(v){
		                	if(!v || v == ""){
		                        return '';
		                    }
		                	v = v.split('.')[0]; //removing milli sec as the parse doesnt support ms
		                    v = Ext.Date.parse(v, "Y-m-d H:i:s");
		                    return v? Ext.Date.format(v,'m/d/Y h:i:s A') : '';
		                }
		            },
		            {
		                header: "Transaction Type",
		                width : 100,
		                dataIndex: 'transactionName'		                
		            },
		            {
		                header: "Credit Amount",
		                width : 80,
		                dataIndex: 'creditAmount',
		                renderer : function(value){
							if(value == ""){
								return "--";
							}else{
								return Ext.util.Format.number(value, '0,000.00');
							}
						}
		            },
		            {
		                header: "Debit Amount",
		                width : 80,
		                dataIndex: 'debitAmount',
		                renderer : function(value){
							if(value == ""){
								return "--";
							}else{
								return Ext.util.Format.number(value, '0,000.00');
							}
						}
		            },
//		            {
//		                header: "Status",
//		                width : 100,
//		                dataIndex: 'transferStatusText'		                
//		            },
		            {
		                header: "Commodity",
		                width : 100,
		                dataIndex: 'commodityText'		                
		            },
		            {
		                header: "Channel Name",
		                width : 100,
		                dataIndex: 'accessMethodText'		                
		            }] 
        	});
    	
    	this.callParent(arguments);
    }
});
