Ext.define('Mfino.store.PerRcTransactions', {
    extend: 'Ext.data.Store',
    id: 'PerRcTransactions',
    model: 'Mfino.model.PerRcTransactions',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'perRcTransactions',
            monitoringPeriod : Mfino.util.Utilities.monitoringPeriod
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});