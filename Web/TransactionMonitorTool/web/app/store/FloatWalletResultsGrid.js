Ext.define('Mfino.store.FloatWalletResultsGrid', {
    extend: 'Ext.data.Store',    
    model: 'Mfino.model.FloatWalletResultsGrid',
    pageSize: 15,
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'floatWalletTransactions'
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    }
});