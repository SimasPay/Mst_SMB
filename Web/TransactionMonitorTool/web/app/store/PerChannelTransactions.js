Ext.define('Mfino.store.PerChannelTransactions', {
    extend: 'Ext.data.Store',
    id: 'channelTransactionsStore',
    model: 'Mfino.model.PerChannelTransactions',
    proxy: {
        type: 'ajax',
        url: 'getTransactions.htm',
        extraParams: {
            portlet: 'channelTransactions',
            monitoringPeriod : Mfino.util.Utilities.monitoringPeriod
        },
        reader: {
            type: 'json',
            root: 'results'
        } 
    } 
});