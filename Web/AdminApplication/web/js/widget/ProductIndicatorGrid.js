/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.ProductIndicatorGrid = function (config) {
    var localConfig = Ext.apply({}, config);  
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSProductIndicator);
    }   
    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.ProductIndicatorAddForm(config),
        mode : "add",
        width : 325,
        height : 275
    },config));
    localConfig = Ext.apply(localConfig, {
        dataUrl             : "fix.htm",
        loadMask : true,
        height : 500,
        viewConfig: {
            emptyText: Config.grid_no_data
            },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'productIndicator.grid.add',
            text:'New',
            tooltip: _('Add New Product Indicator'),
            handler: function(){
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New Product Indicator"));
                gridAddForm.setMode("add");
                gridAddForm.show();
                gridAddForm.setRecord(record);
                gridAddForm.setStore(localConfig.store);
            }
        }],
        columns: [        
        {
            header: _("Transaction Type"),
            width: 160,
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.TransactionUICategoryText._name
        },
        {
            header: _("Channel"),
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.ChannelSourceApplicationText._name
        },
        {
            header: _("Company Name"),
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.CompanyName._name
        },
        {
            header: _("Requestor ID"),
            width:150,          
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.RequestorID._name
        },
        {
            header: _("Product Description"),
            width:150,
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.ProductDescription._name
        },
        {
            header: _("Channel Text"),
            width:150,
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.ChannelText._name
        },
        {
            header: _("Product Indicator"),
            width:100,
            dataIndex: CmFinoFIX.message.JSProductIndicator.Entries.ProductIndicatorCode._name
        }
        ]        
    });

    mFino.widget.ProductIndicatorGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ProductIndicatorGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }

        mFino.widget.ProductIndicatorGrid.superclass.initComponent.call(this);

    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("ProductIndicatorGrid", mFino.widget.ProductIndicatorGrid);