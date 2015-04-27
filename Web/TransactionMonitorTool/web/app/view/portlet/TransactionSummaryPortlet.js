Ext.define('Mfino.view.portlet.TransactionSummaryPortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.transactionSummaryPortlet',
    cls: 'transactionSummaryPortlet',
    height: 380,    

    requires: [
        'Ext.data.ArrayStore',        
        'Mfino.themes.CustomTheme',
        'Ext.chart.series.Series',
        'Ext.chart.series.Pie',
        'Ext.chart.axis.Numeric'
    ],

    initComponent: function(){
    	
    	var summaryChartStore = Ext.create('Mfino.store.TransactionSummary');
    	summaryChartStore.load();
    	/*summaryChartStore.filter(function(r) {
            var value = r.get('transactionStatus');
            return (value != 'Total Transactions');
        });	*/
    	
    	var task = {
            	run: function() {            		
            		//summaryChartStore.load();
            		
            		var portlet = Ext.get('TransactionSummaryPortlet');
					if(portlet.isDisplayed()){
						Ext.getStore('transactionSummaryStore').load({
							params: { 'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod}
						});    	
						Ext.getCmp('summary-chart').refresh();
						Ext.getCmp('summary-bottom-grid').getView().refresh();
					}
            	},
                interval: 300000 //5 mins
            };
        var runner = new Ext.util.TaskRunner();        		 
        runner.start(task);
                
        Ext.apply(this, {            
	            items: [{            	
	            	xtype: 'panel',
	            	id: 'summary-chart-panel',
	            	height: 160,
	            	layout: 'fit',
	            	items: [{
	                    xtype: 'chart',
	                    id: 'summary-chart',
	                    animate: true,
	                    theme: 'CustomTheme',
	                    shadow: true,	                    
	                    store: summaryChartStore,
	                    legend: {
	                        position: 'right',
	                        boxStrokeWidth: 0,
	                        boxFill: 'transparent',
	                        labelFont: '12px Arial'
	                    },
	                    series : [{
	                		type : 'pie',
	                		showInLegend : true,
	                		field : [ 'count' ],
	                		label : {
	                			field : 'transactionStatus',
	                			display : 'rotate',
	                			font : '8px Arial',
	                			renderer: function(value){
	                				return '';
	                			}
	                		},
	                		tips: {
	                            trackMouse: true,
	                            width: 140,
	                            height: 30,
	                            renderer: function(storeItem, item, a , b) {
	                                // calculate and display percentage on hover
	                                var total = 0;
	                                summaryChartStore.each(function(rec) {
	                                    total += rec.get('count');
	                                });
	                                this.setTitle(storeItem.get('transactionStatus') + ': ' + Math.round(storeItem.get('count') / total * 100) + '%');
	                            }
	                        }/*,
	                		highlight : {
	                			segment : {
	                				margin : '1px'
	                			}
	                		}*/
	                	}]	                	
	                }]
	            },{
	            	xtype: 'grid',
	            	id: 'summary-bottom-grid',
	            	//hideHeaders: true,
	            	scroll: false,
	            	width: 400,
	            	store: summaryChartStore,
	                stripeRows:true,
	    			columnLines:true,
	    			columns:[
	    				{text:"Transaction Status",flex:1,sortable:false,dataIndex:"transactionStatus",
	    					renderer: this.linkRenderer, menuDisabled: true
	    				},    				
	    				{text:"Count",sortable:false,dataIndex:"count", menuDisabled: true}]	    			
	            }]    
        }); 
        
        this.callParent(arguments);
    },
    
    linkRenderer: function(val, p, record){
		return '<span class="summary-transactions-link" linkStatus="'+val+'" >'+val+'</span>';
	}
});
