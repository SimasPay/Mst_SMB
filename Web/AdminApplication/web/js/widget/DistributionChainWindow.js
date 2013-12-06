/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DistributionChainWindow = function (config){
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMerchant);
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        autoScroll : true,
        columns: [
        {
            header: _("ID"),
            width:50,
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.ID._name
        },
        {
            header: _("User Name"),
            width:150,
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.Username._name
        },
        {
            header: _("Status"),
            width:100,
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.SubscriberStatusText._name
        },
        {
            header: _("Restrictions"),
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.SubscriberRestrictionsText._name,
            width : 250
        }
        ]
    });
    mFino.widget.DistributionChainWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DistributionChainWindow, Ext.grid.GridPanel, {
});
