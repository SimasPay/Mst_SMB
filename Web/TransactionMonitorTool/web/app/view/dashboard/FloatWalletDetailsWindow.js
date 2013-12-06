Ext.define('Mfino.view.dashboard.FloatWalletDetailsWindow', {
    extend: 'Ext.window.Window',  
    alias: 'widget.floatWalletDetailsWindow', 
    modal : true,
    width: 810,
    height:550,
    resizable: false,   
    layout: {
    	type: 'vbox',
    	align: 'stretch'
    },    
    title : 'Pocket Transaction Details',
    
    initComponent: function() {
    	var searchPanel = Ext.create('Mfino.view.dashboard.FloatWalletSearchPanel');
    	var resultsPanel = Ext.create('Mfino.view.dashboard.FloatWalletResultsGrid',{
    		searchParams: this.initialConfig.searchParams
    	}); 
        Ext.apply(this, {           	
        	items: [searchPanel, resultsPanel]
        });
                
        this.callParent(arguments);
    }
});