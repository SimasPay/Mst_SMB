/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.LOPReportGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSLOP);
    }
    var sbun = new Ext.Toolbar.Button({
//        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        text: _('Export to Excel'),
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelViewLOP.createDelegate(this)
    });
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip: _('View LOP Details')
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
            header: _("Paid amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSLOP.Entries.ActualAmountPaid._name
        },
        {
            header: _("Value amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name
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
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSLOP.Entries.CreatedBy._name
        }
        ]
    });

    mFino.widget.LOPReportGrid.superclass.constructor.call(this, localConfig);
//    this.getBottomToolbar().add('->',sbun);
};

Ext.extend(mFino.widget.LOPReportGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        //        if(this.store){
        //            this.store.on("load", this.onStoreChange.createDelegate(this));
        //            this.store.on("update", this.onStoreChange.createDelegate(this));
        //        }
        mFino.widget.LOPReportGrid.superclass.initComponent.call(this);
    }
//,
//    onStoreChange : function(){
//        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
//            this.getSelectionModel().selectFirstRow();
//        }
//    }
,
 excelViewLOP: function(){
        this.fireEvent("downloadlop");
    }
});

Ext.reg("lopreportgrid", mFino.widget.LOPReportGrid);
