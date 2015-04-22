Ext.define('Mfino.view.portlet.PerRcTransactionsPortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.perRcTransactionsPortlet',
    cls: 'perRcTransactionsPortlet',
    //height: 600,

    requires: [
      	     'Ext.data.JsonStore',
             'Ext.chart.theme.Base',
             'Ext.chart.series.Line',
             'Mfino.ux.grid.HeaderToolTip',
             'Ext.data.ArrayStore',        
             'Mfino.themes.CustomTheme',
             'Ext.chart.series.Series',
             'Ext.chart.series.Pie',
             'Ext.chart.axis.Numeric'       
    ],

    initComponent: function(){
    	var rcTxnChartStore = Ext.create('Mfino.store.PerRcTransactions');
    	rcTxnChartStore.load();
    	
        Ext.apply(this, {            
	            items: [{
	            	xtype: 'panel',
	            	id: 'per-rc-panel',
	            	height: 300,
	            	layout: 'fit',            	
	            	items: [{
		            		xtype: 'chart', 
		            		id: 'perrc-chart',
		                    animate: false,
		                    shadow: false,		                    
		                    theme: 'CustomTheme',
		                    store: rcTxnChartStore,
		                    legend: {
		                    	position: 'right',
		                        boxStrokeWidth: 0,
		                        boxFill: 'transparent'
		                    },
		                    axes : [ {
		                		type : 'Category',
		                		position : 'bottom',
		                		//title : 'Response Code',
		                		label: {
		                            renderer: function(val){
		                             return Ext.String.ellipsis(val, 30, true);
		                            },		                            		                                                    
		                            
                             		orientation: 'vertical',
                             		rotate: {
                                        degrees: 270
                                    }},
		                		fields : [ 'rcCode' ],
		                		minimum : 0,		                		
		                		grid: true,
		                    	}, {
		                		type : 'Numeric',
		                		position : 'left',
		                		fields : [ 'count' ],
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
		                			height : 50,
		                			renderer : function(storeItem, item) {
		                				//this.setTitle(String(item.value[1]) + ' ' + String(item.value[0]) + ' RcStatus '+ item.yField );
		                				this.setTitle(String(item.value[1]) +' Records ');
		                			}
		                		},	
		                		renderer: function(sprite, record, attr, index, store) {
		                            //draw diffrent color                                         
		                            sprite.setAttributes({fill: 'red'}, true);
		                            return attr;
		                		},
		                		xField : 'rcCode',
		                		yField : [ 'count' ]		                		
		                	}]
		            	}]                            	
	            },{
            	xtype: 'grid',
            	id: 'perrc-bottom-grid',
            	plugins: ['headertooltip'],
            	//width: 600,            	
            	store: rcTxnChartStore,
                stripeRows:true,
    			columnLines:true,    			
    			columns:[
 						{text:"RCCode",width:150,dataIndex:"rcCode",hideable: false, menuDisabled: true},
 						{text:"RC Description",width:340,dataIndex:"rcDescription",hideable: false, menuDisabled: true},	
						{text:"Count",width:150,dataIndex:"count",hideable: false,menuDisabled: true}]	
            }]
        });

        this.callParent(arguments);
    }
});
