/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkLOPAddGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSPocket);
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
                dataIndex: CmFinoFIX.message.JSPocket.Entries.PocketTemplDescription._name
            },
            {
                header: _("Denomination"),
                dataIndex: CmFinoFIX.message.JSPocket.Entries.Denomination._name
            },
            {
                header: _('Units'),
                editor: {
                    xtype: 'textfield',
                    itemId : 'units'
                },
                dataIndex: CmFinoFIX.message.JSPocket.Entries.Units._name
            }
        ]
     }
     );
    mFino.widget.BulkLOPAddGrid.superclass.constructor.call(this, localConfig);
};
Ext.extend(mFino.widget.BulkLOPAddGrid, Ext.grid.EditorGridPanel, {
    initComponent : function () {
        mFino.widget.BulkLOPAddGrid.superclass.initComponent.call(this);
    }
});
Ext.reg("bulklopaddgrid", mFino.widget.BulkLOPAddGrid);