Ext.define('Mfino.store.FailedTransactions', {
    extend: 'Ext.data.Store',
    id: 'failedTransactionsStore',
    model: 'Mfino.model.FailedTransactions',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'failedTransactions',
            monitoringPeriod : Mfino.util.Utilities.monitoringPeriod,
            failedTxns : Mfino.util.Utilities.failedTxns
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});