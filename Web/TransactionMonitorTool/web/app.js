Ext.Loader.setConfig('disableCaching', false);

Ext.application({
    name: 'Mfino',

    controllers: [
        'Dashboard', 'SettingsPanel', 'DetailsWindow', 'FloatWalletDetailsWindow'
    ],

    requires: [
    	'Mfino.util.Utilities'    	
    ],

    autoCreateViewport: true
});