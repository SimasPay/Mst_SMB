Ext.define('Mfino.store.TransactionSummary', {
    extend: 'Ext.data.Store',
    id: 'transactionSummaryStore',
    model: 'Mfino.model.TransactionSummary',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'transactionSummary',
            monitoringPeriod : Mfino.util.Utilities.monitoringPeriod
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});