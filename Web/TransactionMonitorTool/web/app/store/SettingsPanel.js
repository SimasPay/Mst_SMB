Ext.define('Mfino.store.SettingsPanel', {
    extend: 'Ext.data.ArrayStore',
    model: 'Mfino.model.SettingsPanel',
    data : [["TransactionSummaryPortlet","Transaction Summary"],
    		["PerServiceTransactionsPortlet","Per Service Transaction Details"],
    		["BalancePortlet","Balance(Float Balance)"],
    		["FailedTransactionsPortlet","Last 5 Failed Transactions"],
    		["PerChannelTransactionsPortlet","Per Channel Transactions"]]
});