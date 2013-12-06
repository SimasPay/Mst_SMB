Ext.define('Mfino.store.FailedTransactions', {
    extend: 'Ext.data.Store',
    id: 'failedTransactionsStore',
    model: 'Mfino.model.FailedTransactions',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'failedTransactions'            
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});