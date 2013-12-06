Ext.define('Mfino.controller.SettingsPanel', {
    extend: 'Ext.app.Controller', 

    init: function() {
    	this.control({	        
            '#settingsGrid': {                
                	select : this.showPortlet,
                	deselect : this.hidePortlet,
                	viewready: this.updateCheckBoxSelections
            	}
	    });	
    },
    
    showPortlet: function(sm, record, rowIndex){
		var portlet = Ext.get(record.data.sectionId);
		portlet.show(); 
		var dashboardController = this.getController('Dashboard');
		if(record.data.sectionId == 'TransactionSummaryPortlet'){
			dashboardController.reloadTransactionSummaryPortlet();
		} else if(record.data.sectionId == 'PerServiceTransactionsPortlet'){
			dashboardController.reloadPerServiceTransactionsPortlet();
		} else if(record.data.sectionId == 'FailedTransactionsPortlet'){
			dashboardController.reloadFailedTransactionsPortlet();
		} else if(record.data.sectionId == 'PerChannelTransactionsPortlet'){
			dashboardController.reloadPerChannelTransactionsPortlet();
		}			
		var dbPanel = Ext.getCmp('dashboardPanel');
		dbPanel.fireEvent('drop',dbPanel);
	},
	
	hidePortlet: function(sm, record, rowIndex){
		var portlet = Ext.get(record.data.sectionId);    			
		portlet.hide();    		
	},
	
	updateCheckBoxSelections: function(grid){
		var sm = grid.getSelectionModel();
		grid.store.each(function(record, index){
			var portlet = Ext.get(record.data.sectionId);
			if(portlet.isDisplayed()){
				sm.select(record, true, true);
			}
		});	
	}
});