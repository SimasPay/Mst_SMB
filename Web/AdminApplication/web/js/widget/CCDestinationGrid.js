/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.CCDestinationGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
       localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSCreditCardDestination);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View Destination Details')
        }
        ]
    });
    
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        plugins:[this.action],
        autoScroll : true,
        columns: [
         this.action,
        {
            header: _('ID'),
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.ID._name
        },
        {
            header: _('User ID'),
            width :70,
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.SubscriberID._name
        },
        {
            header: _('DestinationMDN'),
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.DestMDN._name
        },
        {
            header: _('Status'),
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.CCMDNStatusText._name
        },
        {
            header: _('Created on'),
            renderer:'date',
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.CreateTime._name
        },
        {
            header: _('UpdatedBy'),
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.UpdatedBy._name
        },
        {
            header: _('LastUpdateTime'),
            renderer:'date',
            dataIndex: CmFinoFIX.message.JSCreditCardDestination.Entries.LastUpdateTime._name
        } 
        ]
    });

    mFino.widget.CCDestinationGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CCDestinationGrid, Ext.grid.GridPanel, {        
    });

Ext.reg("CCDestinationGrid", mFino.widget.CCDestinationGrid);
