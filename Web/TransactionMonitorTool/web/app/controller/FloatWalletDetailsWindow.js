Ext.define('Mfino.controller.FloatWalletDetailsWindow', {
    extend: 'Ext.app.Controller', 

    init: function() {
    	this.control({	        
    		'#float-wallet-search': {
        		click: this.searchTransactions
        	},
	        '#float-wallet-reset': {
	        	click: this.resetForm
	        },
	        '[xtype="floatWalletSearchPanel"] field': {
	        	specialkey : this.enterKeyHandler
	        }
	    });	
    },
    
    searchTransactions : function(button){
    	var form = button.up('form').getForm();
    	var resultsGridStore = Ext.getCmp('floatWalletResultsGrid').getStore();
    	resultsGridStore.currentPage = 1;    	
    	resultsGridStore.on('beforeload',function(store, operation,eOpts){
            operation.params = form.getValues();
    	},this);
    	resultsGridStore.load();
    	Ext.getCmp('floatWalletResultsGrid').getView().refresh();    	
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