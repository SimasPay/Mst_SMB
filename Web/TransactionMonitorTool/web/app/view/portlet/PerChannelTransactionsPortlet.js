Ext.define('Mfino.view.portlet.PerChannelTransactionsPortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.perChannelTransactionsPortlet', 
    height: 600,
    
    requires: [
               'Mfino.store.PerChannelTransactions', 'Mfino.ux.grid.HeaderToolTip'             
           ],

    initComponent: function(){
    	
    	var channelStore = Ext.create('Mfino.store.PerChannelTransactions');
    	channelStore.load();
    	
    	Ext.apply(this, {            
            items: [{
            	xtype: 'panel',
            	id: 'channel-chart-panel',
            	height: 250,
            	layout: 'fit',
            	items: [{
	                    xtype: 'chart',
	                    id: 'channel-chart',
	                    theme: 'CustomTheme',
	                    animate: false,
	                    shadow: false,
	                    store: channelStore,
	                    legend: {
	                    	position: 'right',
	                        boxStrokeWidth: 0,
	                        boxFill: 'transparent'
	                    },
	                    axes : [ {
	                		type : 'Category',
	                		position : 'bottom',
	                		fields : [ 'channelName' ]	                		
	                	}, {
	                		type : 'Numeric',
	                		position : 'left',
	                		fields : [ 'successful', 'pending' , 'failed', 'processing', 'reversals', 'intermediate' ],
	                		title : 'Count',	
	                		minimum : 0,
	                		grid : true		
	                	} ],
	                	series : [ {
	                		type : 'column',
	                		axis : 'left',
	                		highlight : false,
	                		tips : {
	                			trackMouse : true,
	                			width : 140,
	                			height : 30,
	                			renderer : function(storeItem, item) {
	                				this.setTitle(String(item.value[1]) + ' ' + String(item.value[0]) + ' transactions '+ item.yField );
	                			}
	                		},
	                		xField : 'channelName',
	                		yField : [ 'successful', 'pending' , 'failed', 'processing', 'reversals', 'intermediate' ]
	                	} ]            	
	                }]
            },{
            	xtype: 'grid',
            	id: 'channel-bottom-grid',
            	plugins: ['headertooltip'],
            	width: 550,            	
            	store: channelStore,
                stripeRows:true,
    			columnLines:true,
    			columns:[
    				{text:"Channel",flex:1,sortable:false,dataIndex:"channelName",hideable: false, 
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="count">'+val+'</span>';
    					}
    				},    				
    				{text:"Successful",width:70,sortable:false,dataIndex:"successful",hideable: false, tooltip: 'Distribution_Completed, Expired',
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="successful">'+val+'</span>';
    					}
    				},
    				{text:"Failed",width:70,sortable:false,dataIndex:"failed",hideable: false, tooltip: 'Failed',
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="failed">'+val+'</span>';
    					}
    				},
    				{text:"Pending",width:70,sortable:false,dataIndex:"pending",hideable: false, tooltip: 'Pending',
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="pending">'+val+'</span>';
    					}
    				},
    				{text:"Processing",width:70,sortable:false,dataIndex:"processing",hideable: false, tooltip: 'Processing, Inquiry',
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="processing">'+val+'</span>';
    					}
    				},
    				{text:"Reversals",width:70,sortable:false,dataIndex:"reversals",hideable: false, tooltip: 'All Reversals',
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="reversals">'+val+'</span>';
    					}
    				},
    				{text:"Intermediate",width:70,sortable:false,dataIndex:"intermediate",hideable: false, tooltip: 'Distribution_Failed, Confirmed, Distribution_Started, Pending_Resolved, Pending_Resolved_Processing',
    					renderer: function(val, p, record){
    						return '<span class="channel-transactions-link" linkChannel="'+record.data.channelSourceApplication+'" linkStatus="intermediate">'+val+'</span>';
    					}
    				}]
            }] 
            
            
        });
    	
    	this.callParent(arguments);
    }
});
