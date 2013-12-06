Ext.define('Mfino.view.dashboard.SettingsPanel', {
    extend: 'Ext.window.Window',  
    modal : true,
    width: 300,
    resizable: false,
    
    alias: 'widget.settingsPanel', 
    
    initComponent: function() {
    	
    	var settingsGrid = Ext.create('Ext.grid.Panel', {    
    		id: 'settingsGrid',
    		cls: 'settingsGrid',
            store: Ext.create('Mfino.store.SettingsPanel'),
            selModel: Ext.create('Ext.selection.CheckboxModel'),
            stripeRows:true,
			columnLines:true,
			autoScroll: false,
            columns: [{ 
            	header: 'Section Name',
                height: 30,
                dataIndex: 'sectionName', 
                width: 260, 
                menuDisabled: true, 
                sortable: false
               }]
        });
    	
        Ext.apply(this, {        	
        	items: settingsGrid
        });
                
        this.callParent(arguments);
    }
});