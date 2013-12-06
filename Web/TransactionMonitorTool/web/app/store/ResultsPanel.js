Ext.define('Mfino.store.ResultsPanel', {
    extend: 'Ext.data.Store',    
    model: 'Mfino.model.ResultsPanel',
    pageSize: 20,
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'transactionSearch'
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    }
});