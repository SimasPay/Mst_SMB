Ext.define('Mfino.view.dashboard.ResultsPanel', {

    extend: 'Ext.grid.GridPanel',
    alias: 'widget.resultsPanel',
    id: 'resultsGrid',
    flex: 3,    
	title: 'Transaction Search Results',

    initComponent: function(){  
    	var resultsStore = Ext.create("Mfino.store.ResultsPanel");   
    	var myParams = this.initialConfig.searchParams;
    	resultsStore.on('beforeload',function(store, operation,eOpts){
    		if(myParams['page']){
            	myParams['page'] = store.currentPage;
            	myParams['start'] = (store.currentPage-1)* myParams['limit'];
            }
            operation.params = myParams;            
    	},this);
    	resultsStore.load();
    	Ext.apply(this, {    		
    		store: resultsStore,
            frame: false,
            loadMask : true,
            viewConfig: { emptyText: 'No transactions' },
            bbar: new Ext.PagingToolbar({
                store: resultsStore, 
                displayInfo: true                    
            }),
            selType: 'rowmodel',
            columns:[
		            {
		                header: "Reference ID",
		                width : 80,
		                dataIndex: 'ID'
		            },  
		            {
		                header: "Transaction Type",
		                width : 150,
		                dataIndex: 'transactionName'		                
		            },
		            {
		                header: "Transaction Amount",
		                width : 120,
		                dataIndex: 'transactionAmount'		                
		            },
		            {
		                header: "Charge From Source",
		                width : 125,
		                dataIndex: 'calculatedCharge'		                
		            },
		            {
		                header: "Status",
		                width : 80,
		                dataIndex: 'transferStatusText'		                
		            },
		            {
		                header: "Status Reason",
		                width : 150,
		                dataIndex: 'failureReason'		                
		            },
		            {
		                header: "Transaction Time",
		                width : 150,
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
		                header: "SourceMDN",
		                width : 100,
		                dataIndex: 'sourceMDN'		                
		            },
		            {
		                header: "DestinationMDN",
		                width : 100,
		                dataIndex: 'destMDN'		                
		            },
		            {
		                header: "Source PartnerCode",
		                width : 80,
		                dataIndex: 'sourcePartnerCode'		                
		            },
		            {
		                header: "Destination PartnerCode",
		                width : 80,
		                dataIndex: 'destPartnerCode'		                
		            },
		            {
		                header: "Biller Code",
		                width : 80,
		                dataIndex: 'MFSBillerCode'		                
		            },
		            {
		                header: "Channel Name",
		                width : 100,
		                dataIndex: 'accessMethodText'		                
		            },
		            {
		                header: "Service Name",
		                width : 100,
		                dataIndex: 'serviceName'		                
		            }] 
        	});
    	
    	this.callParent(arguments);
    }
});
