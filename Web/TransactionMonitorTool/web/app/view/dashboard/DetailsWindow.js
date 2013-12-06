Ext.define('Mfino.view.dashboard.DetailsWindow', {
    extend: 'Ext.window.Window',  
    alias: 'widget.detailsWindow', 
    modal : true,
    width: 1000,
    height: 600,    
    resizable: false,    
    
    initComponent: function() {
    	var searchPanel = Ext.create('Mfino.view.dashboard.SearchPanel');
    	var resultsPanel = Ext.create('Mfino.view.dashboard.ResultsPanel',{
    		searchParams: this.initialConfig.searchParams
    	}); 
        Ext.apply(this, {   
        	layout: {
        		type: 'hbox',
        		align: 'stretch'
        	},
        	items: [searchPanel, resultsPanel]
        });
                
        this.callParent(arguments);
    }
});