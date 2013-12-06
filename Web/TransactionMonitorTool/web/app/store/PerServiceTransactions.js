Ext.define('Mfino.store.PerServiceTransactions', {
    extend: 'Ext.data.Store',
    id: 'serviceTransactionsStore',
    model: 'Mfino.model.PerServiceTransactions',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'serviceTransactions',
            monitoringPeriod : Mfino.util.Utilities.monitoringPeriod
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});