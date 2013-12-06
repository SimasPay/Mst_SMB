/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.CCDetailsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSCardInfo);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View Card Details')
        }]
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
        columns: [
         this.action,
        {
            header: _('ID'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.ID._name
        },
        {
            header: _('User ID'),
            width :70,
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.SubscriberID._name
        },
        {
            header: _('First 6 Digits'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardF6._name
        },
        {
            header: _('Last 4 Digits'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardL4._name
        },
        {
            header: _('Card Issuer Name'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardIssuerName._name
        },
        {
            header: _('Name On Card'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardNameOnCard._name
        },
        {
            header: _('Line1'),
            width:150,
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardLine1._name
        },
        {
            header: _('Line2'),
            width:150,
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardLine2._name
        },
        {
            header: _('City'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardCity._name
        },
        {
            header: _('State'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardState._name
        },
        {
            header: _('ZIP Code'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardZipCode._name
        },
        {
            header: _('CardBilling Line1'),
            width:150,
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardBillingLine1._name
        },
        {
            header: _('CardBilling Line2'),
            width:150,
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardBillingLine2._name
        },
        {
            header: _('CardBilling City'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardBillingCity._name
        },
        {
            header: _('CardBilling State'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardBillingState._name
        },
        {
            header: _('CardBilling ZIP Code'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CardBillingZipCode._name
        },
        {
            header: _('CreatedTime'),
            renderer:'date',
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.CreateTime._name
        },
        {
            header: _('UpdatedBy'),
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.UpdatedBy._name
        },
        {
            header: _('LastUpdateTime'),
            renderer:'date',
            dataIndex: CmFinoFIX.message.JSCardInfo.Entries.LastUpdateTime._name
        } 
        ]
    });

    mFino.widget.CCDetailsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CCDetailsGrid, Ext.grid.GridPanel, {
	initComponent : function () {
	mFino.widget.CCDetailsGrid.superclass.initComponent.call(this);
}
    });

Ext.reg("ccdetailsgrid", mFino.widget.CCDetailsGrid);
