Ext.define('Mfino.controller.DetailsWindow', {
    extend: 'Ext.app.Controller', 

    init: function() {
    	this.control({	        
    		'#search-button': {
        		click: this.searchTransactions
        	},
	        '#reset-button': {
	        	click: this.resetForm
	        },
	        '[xtype="searchPanel"] field': {
	        	specialkey : this.enterKeyHandler
	        }
	    });	
    },
    
    searchTransactions : function(button){
    	var form = button.up('form').getForm();
    	var resultsGridStore = Ext.getCmp('resultsGrid').getStore();
    	resultsGridStore.currentPage = 1;    	
    	resultsGridStore.on('beforeload',function(store, operation,eOpts){
            operation.params = form.getValues();
    	},this);
    	resultsGridStore.load();
    	Ext.getCmp('resultsGrid').getView().refresh();    	
    },
	
    resetForm: function(button) {
    	var form = button.up('form').getForm();
    	form.reset();
    },
    
    enterKeyHandler : function (field , event) {
        if (event.getKey() === event.ENTER) {
            this.searchTransactions(field); //sending field as 'button' param to searchTransactions method
        }
    }    
});