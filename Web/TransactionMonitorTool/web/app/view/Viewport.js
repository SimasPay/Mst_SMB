Ext.define('Mfino.view.Viewport', {

    extend: 'Ext.container.Viewport',
   
    uses: [
        'Mfino.view.dashboard.DashboardPanel'
    ],

    initComponent: function(){

        Ext.apply(this, {
            id: 'app-viewport',
            layout: {
                type: 'border'
                //padding: '0 5 5 5'
            },
            items: [{
                id: 'app-header',
                xtype: 'panel',
                layout: 'fit',
                region: 'north',
                height: 90,
                width: '100%',                
                items: Ext.create('Mfino.view.dashboard.HeaderCombo')
            },{
                xtype: 'container',
                region: 'center',
                layout: 'border',
                items: [{
                    id: 'app-portal',
                    xtype: 'dashboardPanel',
                    region: 'center'
                }]
            }]
        });
        this.callParent(arguments);
    }
});