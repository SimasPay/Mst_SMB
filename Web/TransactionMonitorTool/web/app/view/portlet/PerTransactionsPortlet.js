Ext.define('Mfino.view.portlet.PerTransactionsPortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.perTransactionsPortlet',
    cls: 'perTransactionsPortlet',
    //height: 600,

    requires: [
               'Ext.data.JsonStore',
               'Ext.chart.theme.Base',
               'Ext.chart.series.Series',
               'Ext.chart.series.Line',
               'Ext.chart.series.Cartesian',
               'Ext.chart.series.Bar',
               'Ext.chart.series.Column',
               'Ext.chart.axis.Numeric',
               'Ext.util.Format.numberRenderer',
               'Ext.util.Observable',	
               'Ext.chart.Label',
               'Ext.chart.Highlight',
               'Ext.chart.Tip',
               'Ext.chart.Callout',
               'Mfino.ux.grid.HeaderToolTip'
             
    ],

    initComponent: function(){
    	var transactionChartStore = Ext.create('Mfino.store.PerTransactions');
    	transactionChartStore.load();
    	
    	var task = {
            	run: function() {            		
            		//transactionChartStore.load();
            		var portlet = Ext.get('PerTransactionsPortlet');
					if(portlet.isDisplayed()){
						Ext.getStore('pertransactionsStore').load({
							params: {
								'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod}
						});    	
						Ext.getCmp('pertrns-chart').refresh();
						Ext.getCmp('pertransaction-bottom-grid').getView().refresh();
					}
            	},
                interval: 300000 //5 mins
            };
        var runner = new Ext.util.TaskRunner();        		 
        runner.start(task);
    	
        Ext.apply(this, {            
	            items: [{
	            	xtype: 'panel',
	            	id: 'per-trxn-panel',
	            	height: 300,
	            	layout: 'fit',            	
	            	items: [{
		            		xtype: 'chart', 
		            		id: 'pertrns-chart',
		                    animate: true,
		                    shadow: false,		                    
		                    theme: 'CustomTheme',
		                    store: transactionChartStore,
		                    legend: {
		                    	position: 'right',
		                        boxStrokeWidth: 0,
		                        boxFill: 'transparent'
		                    },
		                    axes : [ {
		                		type : 'Category',
		                		position : 'bottom',
		                		//title : 'Transaction Type',
		                		label: {
	                            	renderer: function(val){
	                            	return Ext.String.ellipsis(val, 30, true);
	                            	},
	                            	orientation: 'vertical',
	                            	rotate: {
                                    degrees: 270
	                            	}
		                           },
		                		fields : [ 'transactionType' ],
		                	}, {
		                		type : 'Numeric',
		                		position : 'left',
		                		fields : [ 'successful', 'pending' , 'failed', 'processing' ],
		                		title : 'Count',			                		
		                		minimum : 0,
		                		grid: true
		                	} ],
		                	series : [ {		                		
		                		type : 'column',
		                		axis : 'left',		     
		                		highlight: true,
		                		tips : {
		                			trackMouse : true,
		                			width : 140,
		                			height : 50,
		                			renderer : function(storeItem, item) {
		                				this.setTitle(String(item.value[1]) + ' ' + String(item.value[0]) + ' Transactions '+ item.yField );
		                				//this.setTitle(String(item.value[1]) + ' Records ');
		                			}
		                		},	
		                		renderer: function(sprite, record, attr, index, store) {
		                            //draw diffrent color                                         
		                            sprite.setAttributes({fill: 'red'}, true);
		                            return attr;
		                		},
		                		xField : 'transactionType',
		                		yField : [ 'successful', 'pending' , 'failed', 'processing' ]		                		
		                	}]
		            	}]                            	
	            },{
            	xtype: 'grid',
            	//id: 'per-trxn-grid',
            	id: 'pertransaction-bottom-grid',
            	plugins: ['headertooltip'],
            	//width: 600,            	
            	store: transactionChartStore,
                stripeRows:true,
    			columnLines:true,    			
    			columns:[
	    				{text:"Transaction Type",width:140,sortable:false,dataIndex:"transactionType",hideable: false,
	    					renderer: function(val, p, record)
	    					{
	    						return '<span class="pertrns-transactions-link" linkTxnID="'+record.data.txnTypeId+'" linkStatus="count">'+val+'</span>';
	    					}
	    				}, 						 					
	    				{text:"Total Count",width:100,sortable:false,dataIndex:"count",hideable: false,
	    					renderer: function(val, p, record)
	    					{
	    						return '<span class="pertrns-transactions-link" linkTxnID="'+record.data.txnTypeId+'" linkStatus="count">'+val+'</span>';
	    					}
	    				}, 						 						
	    				{text:"Successful",width:100,sortable:false,dataIndex:"successful",hideable: false,
	    					renderer: function(val, p, record)
	    					{
	    						return '<span class="pertrns-transactions-link" linkTxnID="'+record.data.txnTypeId+'" linkStatus="successful">'+val+'</span>';
	    					}
	    				},
	    				{text:"Pending",width:100,sortable:false,dataIndex:"pending",hideable: false,
	    					renderer: function(val, p, record)
	    					{
	    						return '<span class="pertrns-transactions-link" linkTxnID="'+record.data.txnTypeId+'" linkStatus="pending">'+val+'</span>';
	    					}
	    				},
	    				{text:"Failed",width:100,sortable:false,dataIndex:"failed",hideable: false,
	    					renderer: function(val, p, record)
	    					{
	    						return '<span class="pertrns-transactions-link" linkTxnID="'+record.data.txnTypeId+'" linkStatus="failed">'+val+'</span>';
	    					}
	    				},
	    				{text:"InProgress",width:100,sortable:false,dataIndex:"processing",hideable: false,
	    					renderer: function(val, p, record)
	    					{
	    						return '<span class="pertrns-transactions-link" linkTxnID="'+record.data.txnTypeId+'" linkStatus="processing">'+val+'</span>';
	    					}
	    				}
						]	
            }]
        });

        this.callParent(arguments);
    }
});
