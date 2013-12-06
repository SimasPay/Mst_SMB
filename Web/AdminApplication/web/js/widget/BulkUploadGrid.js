/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkUploadGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkUpload);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls:'mfino-button-View',
            tooltip:'View',
            align:'right',
            mode: 'close'
        }
        ]
    });


    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
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
        autoScroll : true,
        plugins:[this.action],
        listeners : {
        },
        columns: [
        this.action,
        {
            header: _('Trans ID'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.ID._name
        },
        {
            header: _('Currency'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.Currency._name
        },
        {
            header: _('Total Amount'),
            renderer : 'money',
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.TotalAmount._name
        },
        {
            header: _("Total Amount Success"),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.SuccessAmount._name
        },
        {
            header: _('No of Trans'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.TransactionsCount._name
        },
        {
            header: _('Successful Trans'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.SuccessfulTransactionsCount._name,
            renderer: function(value, metadata, record)
            {
                if(value!==null)
                {
                    return value;
                }
                else
                {
                    return "--";
                }
            }
        },
        {
            header: _('Hash Total'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.VerificationChecksum._name
        },
        {
            header: _('Uploaded By'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.UploadedBy._name
        },
        
        {
            header: _('Status'),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadDeliveryStatusText._name
        },
        {
            header: _("Batch Description"),
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.Description._name
        },
        {
            header: _('Delivery Date'),
            renderer: "date",
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadDeliveryDate._name
        },
        {
            header: _('Upload Time'),
            renderer:"date" ,
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.CreateTime._name
        },
        {
            header: _('Process Time'),
            renderer: "date",
            dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.ProcessTime._name
        }
        ]
    });

    mFino.widget.BulkUploadGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkUploadGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        //        if(this.store){
        //            this.store.on("load", this.onStoreChange.createDelegate(this));
        //            this.store.on("update", this.onStoreChange.createDelegate(this));
        //        }
        mFino.widget.BulkUploadGrid.superclass.initComponent.call(this);
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("search", values);
    }
//,
//    onStoreChange : function(){
//        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
//            this.getSelectionModel().selectFirstRow();
//        }
//   }
});

Ext.reg("bulkuploadgrid", mFino.widget.BulkUploadGrid);
