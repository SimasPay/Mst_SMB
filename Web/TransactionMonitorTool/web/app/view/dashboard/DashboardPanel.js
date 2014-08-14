Ext.define('Mfino.view.dashboard.DashboardPanel', {
    extend: 'Mfino.view.app.PortalPanel',      
    
    alias: 'widget.dashboardPanel',    

    getTools: function(){
        return [{
            xtype: 'tool',
            type: 'gear',
            handler: function(e, target, panelHeader, tool){
                var portlet = panelHeader.ownerCt;
                portlet.setLoading('Loading...');
                Ext.defer(function() {
                    portlet.setLoading(false);
                }, 2000);
            }
        }];
    },
    
    initComponent: function() {
    	Ext.Ajax.on("requestcomplete", function(conn, response, options){
    	    //We can do nicer things here, like showing a login window and let
    	    //the user login on the same screen. But now we simply redirects the
    	    //user to the login page.
    		if(options.url == 'j_spring_security_logout') { 
    			//When the user is logged out on clicking logout option, the 'sessionExpired' param should not be added to query string  
    			//so that sessionExpired popup wont be shown in this case. Admin app doesn't have this check as logout is simply an anchor tag 
    			//which generates synchronous request unlike here where its an ajax request, hence wont be caught by this 'requestcomplete' listener.
    			window.location = "login.htm";
    	        return;
    		}
    		if(response.responseText.indexOf("<html>") > 0){
    			window.location = "login.htm?sessionExpired=true";
    	        return;
    	    }
    		if(response.responseText.indexOf("Session Expired") > 0){ 
    			window.location = "login.htm?sessionExpired=true";
    			return;
    		}    		
    	});
    	
        Ext.apply(this, {
        	id: 'dashboardPanel',
            items: [{
                id: 'col-1',
                items: [{
                    id: 'TransactionSummaryPortlet',
                    title: 'Transaction Summary',
                    items: Ext.create('Mfino.view.portlet.TransactionSummaryPortlet')                    
                },{
                    id: 'PerServiceTransactionsPortlet',                    
                    title: 'Per Service Transaction Details',
                    items: Ext.create('Mfino.view.portlet.PerServiceTransactionsPortlet')
                }]
            },{
                id: 'col-2',
                items: [{
                    id: 'BalancePortlet',   
                    header: false,
                    frame: false,
                    height: 60,
                    items: Ext.create('Mfino.view.portlet.BalancePortlet')
                },
                {
                    id: 'FailedTransactionsPortlet',
                    title: 'Last 5 Failed Transactions',
                    items: Ext.create('Mfino.view.portlet.FailedTransactionsPortlet')
                },{
                    id: 'PerChannelTransactionsPortlet',
                    title: 'Per Channel Transactions',
                    items: Ext.create('Mfino.view.portlet.PerChannelTransactionsPortlet')
                }
                ]
            }]
            
        });
                
        this.callParent(arguments);
    },
    beforeRender: function(){
    	this.callParent(arguments);
    	if(!mFino.auth.isEnabledItem('transactionMonitor.viewFloatBalance')){
    		var col2 = this.getComponent('col-2');
    		var floatPortlet = col2.getComponent('BalancePortlet');
    		floatPortlet.hide();    	
    	}    	
    }
});