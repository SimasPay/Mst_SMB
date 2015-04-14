Ext.define('Mfino.store.SettingsPanel', {
    extend: 'Ext.data.ArrayStore',
    model: 'Mfino.model.SettingsPanel',
    data : [["TransactionSummaryPortlet","Transaction Summary"],
            ["PerTransactionsPortlet","Per Transaction Details"],
    		["PerServiceTransactionsPortlet","Per Service Transaction Details"],
    		//["BalancePortlet","Balance(Float Balance)"],
    		["FailedTransactionsPortlet","Last 5 Failed Transactions"],
    		["PerRcTransactionsPortlet","Per RC Transaction Details"],
    		["PerChannelTransactionsPortlet","Per Channel Transactions"]]
});
