/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.LOPGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSLOP);
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
    var popup = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
        title:  "LOP History",
        grid : new mFino.widget.LOPHistoryGrid(config),
        height : 466,
        width: 600
    },config));
    this.popup = popup;
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { 
            emptyText: Config.grid_no_data
        },
        tbar:  [
        '<b class= x-form-tbar>' + _('LOP Search Results') + '</b>',
        '->',
        {
            iconCls: 'mfino-button-history',
            text:'LOP History',
            tooltip: _('LOP History'),
            handler: function(){
                this.findParentByType("lopgrid").tBarClicked();
            }
        }],
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
            dataIndex: CmFinoFIX.message.JSLOP.Entries.ID._name
        },
        {
            header: _("Merchant ID"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.SubscriberID._name
        },
        {
            header: _("GiroRef ID"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.GiroRefID._name
        },
        {
            header: _('Transaction Reference'),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.TransactionID._name
        },
        {
            header: _("User Name"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.Username._name
        },
        {
            header: _("MDN"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.MDN._name
        },
        {
            header: _("Transfer Date"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSLOP.Entries.TransferDate._name,
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
            dataIndex: CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name
        },
        {
            header: _("Commission"),
            renderer : "percentage",
            dataIndex: CmFinoFIX.message.JSLOP.Entries.Commission._name
        },
        {
            header: _("Paid amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSLOP.Entries.ActualAmountPaid._name
        },
        {
            header: _("Status"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.Status._name
        },
        {
            header: _("Create Date"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSLOP.Entries.CreateTime._name
        },
        {
            header: _("Approval Date"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSLOP.Entries.ApprovalTime._name
        },
        {
            header: _("Approved/Rejected By"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSLOP.Entries.ApprovedBy._name
        },
        {
            header: _("Distribution Time"),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSLOP.Entries.DistributeTime._name
        },
        {
            header: _("Distributed By"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.DistributedBy._name
        },
        {
            header: _("Last Modified"),
            renderer: "date",
            dataIndex: CmFinoFIX.message.JSLOP.Entries.LastUpdateTime._name
        }
        ]
    });

    mFino.widget.LOPGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.LOPGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.LOPGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    excelView: function(){
        this.fireEvent("download");
    },
    tBarClicked: function()
    {
        var selectedRecord = this.getSelectionModel().getSelected();
        if(selectedRecord)
        {
            this.popup.grid.store.lastOptions = {
                params : {
                    start : 0,
                    limit : CmFinoFIX.PageSize.Default
                }
            };
            this.popup.grid.store.baseParams[CmFinoFIX.message.JSLOPHistory.LOPIDSearch._name] = selectedRecord.data[CmFinoFIX.message.JSLOP.Entries.ID._name];
            this.popup.grid.store.load(this.popup.grid.store.lastOptions);
            this.popup.show();
        }else {
            Ext.Msg.show({
                title: _('Alert !'),
                minProgressWidth:250,
                msg: _('No Record selected!'),
                buttons: Ext.MessageBox.OK,
                multiline: false
            });
        }
    }
});

Ext.reg("lopgrid", mFino.widget.LOPGrid);
