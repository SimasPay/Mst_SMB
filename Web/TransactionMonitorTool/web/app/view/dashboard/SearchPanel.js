Ext.define('Mfino.view.dashboard.SearchPanel', {

    extend: 'Ext.form.Panel',
    
    requires: 'Mfino.ux.form.DateRangeField',
    alias: 'widget.searchPanel',    
	flex: 1,
	title: 'Transaction Search',
	labelPad : 10,
    labelWidth : 90,
    bodyStyle:'padding:5px 5px 0',

    initComponent: function(){  

    	Ext.apply(this, {    		
            items: [{
                xtype : 'textfield',                
                fieldLabel: 'Reference ID',
                labelSeparator : '',
                name: 'idSearch'
            },{
                xtype : 'daterangefield',                
                fieldLabel: 'Transaction Time',
                labelSeparator : '', 
                name: 'transactionTime'
            },{
                xtype : 'textfield',                
                fieldLabel: 'Source MDN',
                labelSeparator : '',                
                maxLength : 13,
                name: 'sourceMDN'
            },{
                xtype : 'textfield',                
                fieldLabel: 'Destination MDN',
                labelSeparator : '',                
                maxLength : 13,
                name: 'destMDN'
            },{
                xtype : "textfield",
                fieldLabel: 'Source PartnerCode',
                labelSeparator : '', 
                name: 'sourcePartnerCode'
            },{
                xtype : "textfield",
                fieldLabel: 'Dest PartnerCode',
                labelSeparator : '', 
                name: 'destPartnerCode'
            },{
                xtype : "textfield",
                fieldLabel: 'Biller Code',
                labelSeparator : '',
                name: 'billerCode'
            }],
            buttons: [{
            	text: 'Search',
            	id: 'search-button'            	
            },
            {
            	text: 'Reset',
            	id: 'reset-button'
            }]           
        });
    	
    	this.callParent(arguments);
    }
});
