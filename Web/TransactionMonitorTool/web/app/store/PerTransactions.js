Ext.define('Mfino.store.PerTransactions', {
    extend: 'Ext.data.Store',
    id: 'pertransactionsStore',
    model: 'Mfino.model.PerTransactions',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'perTransactions',
            monitoringPeriod : Mfino.util.Utilities.monitoringPeriod
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});