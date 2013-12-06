/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.BulkTransferGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);

    var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkUpload);

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls:'mfino-button-View',
            tooltip: _('View Subscriber Transfer Details'),
            align:'right'
        },
        {
            iconCls:'mfino-button-history',
            tooltip: _('View Reverse Transfer Details'),
            align:'right'
        }
        ]
    });

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl: "fix.htm",
        store: store,
        frame: true,
        width: 300,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: store,
            displayInfo: true,
            displayMsg: _('Displaying topics') + ('{0} - {1} of {2}'),
            emptyMsg: _("No Records to display"),
            pageSize: CmFinoFIX.PageSize.Default
            
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
          this.action,
          {
              header: _('ID'),
              width : 80,
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.ID._name
          },
          {
              header:_("File Name"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.FileName._name
          },
          {
              header: _("File Type"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadFileTypeText._name
          },
          {
              header: _("Description"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.Description._name
          },
          {
              header: _("Status"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadDeliveryStatusText._name
          },
          {
              header: _("Failure Reason"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.FailureReason._name
          },
          {
              header: _("No. of Txns"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.TransactionsCount._name
          },
          {
              header: _("Last Update Time"),
              width:150,
              renderer : "date",
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.LastUpdateTime._name
          },
          {
              header: _("Updated By"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.UpdatedBy._name
          },
          {
              header: _("Create Time"),
              width:150,
              renderer : "date",
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.CreateTime._name
          },
          {
              header: _("Created By"),
              dataIndex: CmFinoFIX.message.JSBulkUpload.Entries.CreatedBy._name
          }
          ]
    });

    mFino.widget.BulkTransferGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransferGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.BulkTransferGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});





