/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.LOPHistoryGrid = function (config){
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSLOPHistory);
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
            dataIndex: CmFinoFIX.message.JSLOPHistory.Entries.ID._name
        },
        {
            header: _("Old Discount"),
            width:150,
            dataIndex: CmFinoFIX.message.JSLOPHistory.Entries.OldDiscount._name
        },
        {
            header: _("New Discount"),
            width:100,
            dataIndex: CmFinoFIX.message.JSLOPHistory.Entries.NewDiscount._name
        },
        {
            header: _("Changed By"),
            dataIndex: CmFinoFIX.message.JSLOPHistory.Entries.DiscountChangedBy._name,
            width : 250
        },
        {
            header: _("Comments"),
            dataIndex: CmFinoFIX.message.JSLOPHistory.Entries.Comments._name,
            width : 250
        }
        ]
    });
    mFino.widget.LOPHistoryGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.LOPHistoryGrid, Ext.grid.GridPanel, {
});
