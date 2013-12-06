Ext.define('Mfino.view.portlet.BalancePortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.balancePortlet',
    id: 'balancePortletView',

    initComponent: function(){
        Ext.apply(this, {
            layout: 'fit',            
            html: '<div class="portlet-body"><div class="portlet-content"></div></div>',
            tpl: new Ext.XTemplate('Balance(Float Wallet) : {currentBalance}')
        });
        this.callParent(arguments);
    },    
    
    afterRender: function(){
    	this.callParent(arguments);
    	var task = {
            	run: function() {            		
            		this.showBalance();
            	},
            	scope: this,
                interval: 30000 // 10 seconds
            };
            		 
        var runner = new Ext.util.TaskRunner();        		 
        runner.start(task);    	
    },
    showBalance: function(){
    	Ext.Ajax.request({ 
			url: 'getTransactions.htm',
			method: 'post',
			params: {'portlet': 'floatBalance'},
			success: function(response, options){
				var data = Ext.JSON.decode(response.responseText);
				var divRef = Ext.DomQuery.selectNode('div.portlet-content', this.el.dom);
				this.tpl.overwrite(divRef, data);
			}, 
			scope: this
		});
    }
});
