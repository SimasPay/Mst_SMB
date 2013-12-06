Ext.define('Mfino.view.dashboard.FloatWalletSearchPanel', {

    extend: 'Ext.form.Panel',
    
    requires: 'Mfino.ux.form.DateRangeField',
    alias: 'widget.floatWalletSearchPanel',
    flex: 1,
    defaults: {
    	bodyStyle: 'padding:15px'
    },

    initComponent: function(){  

    	Ext.apply(this, {    		
            items: [{
            	layout:'column',
                border:false,
                labelWidth : 90,
                border:false,
                items: [{
                    columnWidth:0.5,
                    layout: 'form',
                    labelWidth : 90,
                    border:false,
                    items: [{
                            	xtype : 'daterangefield',                
                                fieldLabel: 'Transaction Time',
                                labelSeparator : '', 
                                name: 'transactionTime'                                
			                }
                         ]
    				}]
            }],
            buttons: [{
                          text: 'Search',
                          id: 'float-wallet-search'                          
                      },
                      {
                          text: 'Reset',
                          id: 'float-wallet-reset'                          
                      },' ',' ', ' '
                 ]           
        });
    	
    	this.callParent(arguments);
    }
});
