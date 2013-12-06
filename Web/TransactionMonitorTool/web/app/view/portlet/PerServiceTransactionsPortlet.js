Ext.define('Mfino.view.portlet.PerServiceTransactionsPortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.perServiceTransactionsPortlet',
    height: 600,

    requires: [
        'Ext.data.JsonStore',
        'Ext.chart.theme.Base',
        'Ext.chart.series.Series',
        'Ext.chart.series.Line',
        'Ext.chart.axis.Numeric',
        'Mfino.ux.grid.HeaderToolTip'
    ],

    initComponent: function(){
    	var perStore = Ext.create('Mfino.store.PerServiceTransactions');
    	perStore.load();
    	
        Ext.apply(this, {            
	            items: [{
	            	xtype: 'panel',
	            	id: 'service-chart-panel',
	            	height: 300,
	            	layout: 'fit',            	
	            	items: [{
		            		xtype: 'chart', 
		            		id: 'service-chart',
		                    animate: false,
		                    shadow: false,		                    
		                    theme: 'CustomTheme',
		                    store: perStore,
		                    legend: {
		                    	position: 'right',
		                        boxStrokeWidth: 0,
		                        boxFill: 'transparent'
		                    },
		                    axes : [ {
		                		type : 'Category',
		                		position : 'bottom',		                		
		                		/*label: {
		                			renderer: function(val){
		                				return '';
		                			}
		                		},
		                		hidden : true,*/
		                		fields : [ 'serviceName' ]                		
		                	}, {
		                		type : 'Numeric',
		                		position : 'left',
		                		fields : [ 'count', 'successful', 'pending' , 'failed', 'processing', 'reversals', 'intermediate' ],
		                		title : 'Count',			                		
		                		minimum : 0,
		                		grid: true
		                	} ],
		                	series : [ {
		                		type : 'column',		                		
		                		axis : 'left',		                		
		                		tips : {
		                			trackMouse : true,
		                			width : 140,
		                			height : 30,
		                			renderer : function(storeItem, item) {
		                				this.setTitle(String(item.value[1]) + ' ' + String(item.value[0]) + ' transactions '+ item.yField );
		                			}
		                		},	
		                		renderer: function(sprite, record, attr, index, store) {
		                            //draw diffrent color                                         
		                            sprite.setAttributes({fill: 'red'}, true);
		                            return attr;
		                		},
		                		xField : 'serviceName',
		                		yField : [ 'count', 'successful', 'pending' , 'failed', 'processing', 'reversals', 'intermediate' ]		                		
		                	}]
		            	}]                            	
	            },{
            	xtype: 'grid',
            	id: 'service-bottom-grid',
            	plugins: ['headertooltip'],
            	width: 600,            	
            	store: perStore,
                stripeRows:true,
    			columnLines:true,    			
    			columns:[
    				{text:"Service",flex:1,sortable:false,dataIndex:"serviceName",hideable: false,
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="count">'+val+'</span>';
    					}
    				},
    				{text:"Count",width:70,sortable:false,dataIndex:"count",hideable: false,
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="count">'+val+'</span>';
    					}
    				},
    				{text:"Successful",width:70,sortable:false,dataIndex:"successful",hideable: false, tooltip: 'Distribution_Completed, Expired',
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="successful">'+val+'</span>';
    					}
    				},
    				{text:"Failed",width:70,sortable:false,dataIndex:"failed",hideable: false, tooltip: 'Failed',
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="failed">'+val+'</span>';
    					}
    				},
    				{text:"Pending",width:70,sortable:false,dataIndex:"pending",hideable: false, tooltip: 'Pending',
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="pending">'+val+'</span>';
    					}
    				},
    				{text:"Processing",width:70,sortable:false,dataIndex:"processing",hideable: false, tooltip: 'Processing, Inquiry',
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="processing">'+val+'</span>';
    					}
    				},
    				{text:"Reversals",width:70,sortable:false,dataIndex:"reversals",hideable: false, tooltip: 'All Reversals',
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="reversals">'+val+'</span>';
    					}
    				},
    				{text:"Intermediate",width:70,sortable:false,dataIndex:"intermediate",hideable: false, tooltip: 'Distribution_Failed, Confirmed, Distribution_Started, Pending_Resolved, Pending_Resolved_Processing',
    					renderer: function(val, p, record){
    						return '<span class="service-transactions-link" linkServiceID="'+record.data.serviceID+'" linkStatus="intermediate">'+val+'</span>';
    					}
    				}]
            }
            ]
        });

        this.callParent(arguments);
    }
});
