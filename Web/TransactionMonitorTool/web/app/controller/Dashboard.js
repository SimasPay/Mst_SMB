Ext.define('Mfino.controller.Dashboard', {
    extend: 'Ext.app.Controller',    

    stores: ['ResultsPanel'],

    //models: ['Stock', 'ChartStock'],*/
    
    //views: ['portlet.TransactionSummaryPortlet'],

    init: function() {
    	this.control({	        
            '#settings': {                
                click : this.managePortlets
            },
            '#headerCombo': {
            	select : this.onComboSelect            	
            },
            '#summary-bottom-grid > gridview': {
            	refresh: this.registerClickEvents
            },
            '#service-bottom-grid > gridview': {
            	refresh: this.registerClickEvents
            },
            '#channel-bottom-grid > gridview': {
            	refresh: this.registerClickEvents
            },
            '#failed-transactions-grid > gridview': {
            	refresh: this.registerClickEvents
	        },
	        '#balancePortletView': {
            	afterrender: this.registerBalanceClickEvent
	        }
	    });	
    },    
    
    onComboSelect: function(combo,item) {    	
    	Mfino.util.Utilities.monitoringPeriod = item[0].data.value;
    	this.reloadPortlets();
    },
    
    reloadPortlets: function(){
    	this.reloadTransactionSummaryPortlet();
    	this.reloadPerServiceTransactionsPortlet();
    	this.reloadPerChannelTransactionsPortlet();
    	this.reloadFailedTransactionsPortlet();
    },
    
    reloadTransactionSummaryPortlet: function(){
    	var portlet = Ext.get('TransactionSummaryPortlet');
    	if(portlet.isDisplayed()){
    		Ext.getStore('transactionSummaryStore').load({
        		params: { 'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod}
        	});    	
        	Ext.getCmp('summary-chart').refresh();
        	Ext.getCmp('summary-bottom-grid').getView().refresh();
    	}    	
    },
    
    reloadPerServiceTransactionsPortlet: function(){
    	var portlet = Ext.get('PerServiceTransactionsPortlet');
    	if(portlet.isDisplayed()){
    		Ext.getStore('serviceTransactionsStore').load({
        		params: { 'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod}
        	});    	
        	Ext.getCmp('service-chart').refresh();
        	Ext.getCmp('service-bottom-grid').getView().refresh();
    	}    	
    },
    
    reloadPerChannelTransactionsPortlet: function(){
    	var portlet = Ext.get('PerChannelTransactionsPortlet');
    	if(portlet.isDisplayed()){
    		Ext.getStore('channelTransactionsStore').load({
        		params: { 'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod}
        	});    	
        	Ext.getCmp('channel-chart').refresh();
        	Ext.getCmp('channel-bottom-grid').getView().refresh();
    	}    	
    },
    
    reloadFailedTransactionsPortlet: function(){
    	var portlet = Ext.get('FailedTransactionsPortlet');
    	if(portlet.isDisplayed()){
    		Ext.getStore('failedTransactionsStore').load({
        		params: { 'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod}
        	});	
        	Ext.getCmp('failed-transactions-grid').getView().refresh();
    	}    	
    },    
    
    managePortlets: function(button){
    	var win = Ext.create('Mfino.view.dashboard.SettingsPanel');
    	win.show();
    },
        
    registerClickEvents : function(cmp){
    	var grid = cmp.up('grid');
    	 if(grid.id == 'service-bottom-grid') {
    		var links = Ext.get(Ext.DomQuery.select('span.service-transactions-link',cmp.el.dom));
    		Ext.each(links, function(link){
    			link.on('click',function(evt, el, o){
    				var win = Ext.create('Mfino.view.dashboard.DetailsWindow',{
    					searchParams: {
    						'linkServiceID': el.getAttribute('linkServiceID'), 
    						'linkStatus': el.getAttribute('linkStatus'),
    						'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod
    						}
    				});
    		    	win.show();
        		});
    		});
    	} else if(grid.id == 'channel-bottom-grid') {
    		var links = Ext.get(Ext.DomQuery.select('span.channel-transactions-link',cmp.el.dom));
    		Ext.each(links, function(link){
    			link.on('click',function(evt, el, o){
    				var win = Ext.create('Mfino.view.dashboard.DetailsWindow',{
    					searchParams: {
    						'linkChannel': el.getAttribute('linkChannel'), 
    						'linkStatus': el.getAttribute('linkStatus'),
    						'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod
    						}
    				});
    		    	win.show();
        		});
    		});
    	} else if(grid.id == 'failed-transactions-grid') {
    		var links = Ext.get(Ext.DomQuery.select('span.failed-transactions-link',cmp.el.dom));
    		Ext.each(links, function(link){
    			link.on('click',function(evt, el, o){
    				var win = Ext.create('Mfino.view.dashboard.DetailsWindow',{
    					searchParams: {
    						'linkRefID': el.getAttribute('linkRefID')    						
    						}
    				});
    		    	win.show();
        		});
    		});
    	} else if(grid.id == 'summary-bottom-grid') {
    		var links = Ext.get(Ext.DomQuery.select('span.summary-transactions-link',cmp.el.dom));
    		Ext.each(links, function(link){
    			link.on('click',function(evt, el, o){
    				var value = el.getAttribute('linkStatus');
    				var linkStatus = null;
    				if(value == 'Total Successful Transactions'){
    					linkStatus = 'successful';
    				} else if(value == 'Total Failed Transactions'){
    					linkStatus = 'failed';
    				} else if(value == 'Total Pending Transactions'){
    					linkStatus = 'pending';
    				} else if(value == 'Total Processing Transactions'){
    					linkStatus = 'processing';
    				} else if(value == 'Total Reversals Transactions'){
    					linkStatus = 'reversals';
    				} else if(value == 'Total Intermediate Transactions'){
    					linkStatus = 'intermediate';
    				}
    				var win = Ext.create('Mfino.view.dashboard.DetailsWindow',{
    					searchParams: {
    						'linkStatus': linkStatus,
    						'monitoringPeriod': Mfino.util.Utilities.monitoringPeriod
    						}
    				});
    		    	win.show();
        		});
    		});
    	}
    },
    
    registerBalanceClickEvent: function(cmp){
    	//var links = Ext.get(Ext.DomQuery.select('div#balancePortletView',cmp.el.dom));
    	var links = Ext.select('div.portlet-body',cmp.el.dom);
    	Ext.each(links, function(link){
			link.on('click',function(evt, el, o){
				var win = Ext.create('Mfino.view.dashboard.FloatWalletDetailsWindow',{
					searchParams: {
						'linkRefID': el.getAttribute('linkRefID')    						
						}
				});
		    	win.show();
    		});
		});
    },    
    
    closeWindow: function(button) {
    	var win = button.up('window');
    	win.close();
    },
    
    showMsg: function(msg) {
        var el = Ext.get('app-msg'),
            msgId = Ext.id();

        this.msgId = msgId;
        el.update(msg).show();

        Ext.defer(this.clearMsg, 3000, this, [msgId]);
    },

    clearMsg: function(msgId) {
        if (msgId === this.msgId) {
            Ext.get('app-msg').hide();
        }
    }
});