/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkLOPDetailsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSBulkLOPDetails);
    }

 localConfig = Ext.apply(localConfig, {
        dataUrl: "fix.htm",
        loadMask : true,
        height : 250,
        frame: true,
        viewConfig: {
            emptyText: Config.grid_no_data
        },
        columns:[
            {
                header: _("PocketTemplateDescription"),
                dataIndex: CmFinoFIX.message.JSBulkLOPDetails.Entries.PocketTemplDescription._name
            },
            {
                header: _("Denomination"),
                dataIndex: CmFinoFIX.message.JSBulkLOPDetails.Entries.Denomination._name
            },
            {
                header: _('Units'),
                dataIndex: CmFinoFIX.message.JSBulkLOPDetails.Entries.Units._name
            }
        ]
     });
    mFino.widget.BulkLOPDetailsGrid.superclass.constructor.call(this, localConfig);
};
Ext.extend(mFino.widget.BulkLOPDetailsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        mFino.widget.BulkLOPDetailsGrid.superclass.initComponent.call(this);
    }
});
Ext.reg("bulklopdetailsgrid", mFino.widget.BulkLOPDetailsGrid);