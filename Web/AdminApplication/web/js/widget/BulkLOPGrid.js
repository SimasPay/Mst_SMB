/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkLOPGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkLOP);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View LOP Details')
        }
        ]
    });
    var sbun = new Ext.Toolbar.Button({
//        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        text: _('Export to Excel'),
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
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
            pageSize: CmFinoFIX.PageSize.Default,
            items: [sbun]
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        this.action,
        {
            header: _("LOP ID"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.ID._name
        },
        {
            header: _("Merchant ID"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.MerchantID._name
        },
        {
            header: _("GiroRef ID"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.GiroRefID._name
        },
        {
            header: _("User Name"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.Username._name
        },
        {
            header: _("MDN"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.MDN._name
        },
        {
            header: _("Transfer Date"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.TransferDate._name,
            renderer : function(value)
            {
                if(value) {
                    return value.split(' ')[0];
                }
            }
        },
        {
            header: _("Value amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.AmountDistributed._name
        },
        {
            header: _("Commission"),
            renderer : "percentage",
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.Commission._name
        },
        {
            header: _("Paid amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.ActualAmountPaid._name
        },
        {
            header: _("Status"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.Status._name
        },
        {
            header: _("Create Date"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.CreateTime._name
        },
        {
            header: _("Approval Date"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.ApprovalTime._name
        },
        {
            header: _("Approved/Rejected By"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.ApprovedBy._name
        },
        {
            header: _("Distribution Time"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.DistributeTime._name
        },
        {
            header: _("Distributed By"),
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.DistributedBy._name
        },
        {
            header: _("Last Modified"),
            renderer: "date",
            dataIndex: CmFinoFIX.message.JSBulkLOP.Entries.LastUpdateTime._name
        }
        ]
    });

    mFino.widget.BulkLOPGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkLOPGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.BulkLOPGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    excelView: function(){
        this.fireEvent("download");
    }
});

Ext.reg("bulklopgrid", mFino.widget.BulkLOPGrid);
