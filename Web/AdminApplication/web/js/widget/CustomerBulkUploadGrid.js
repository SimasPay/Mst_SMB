/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CustomerBulkUploadGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    var uploadFile = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.UploadFile(config),
        title : _("Upload File"),
        height : 200,
        width:500,
        mode:"bulk"
    },config));    
   
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSBulkUploadFile);
    }
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-View',
            tooltip:'View',
            align:'right'
        }
        ]
    });
    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        layout : 'fit',
        loadMask : true,
        viewConfig: {
            emptyText: Config.grid_no_data
        },
        autoScroll : true,
        labelPad : 10,
        labelWidth : 80,
        height:490,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        tbar :  [{
            iconCls: 'mfino-button-add',
            itemId : 'bulkUpload.grid.add',
            text:'Upload New File',
            tooltip: _('Upload New File'),
            handler: function(){
                uploadFile.show();
                uploadFile.form.getForm().reset();
                Ext.get('form-file1-file').dom.value ='';
            }
        }],
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
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
            header: _('BulkUpload ID'),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.ID._name
        },
        {
            header:_("File Name"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.FileName._name
        },
        {
            header: _("File Type"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.RecordTypeText._name
        },
        {
            header: _("Description"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.Description._name
        },
        {
            header: _("Status"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.UploadStatusText._name
        },
        {
            header: _("Total Line Count"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.TotalLineCount._name
        },
        {
            header: _("Successful Transactions Count"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.ErrorLineCount._name,
            renderer: function(value, metadata, record)
            {
                if(record.data[CmFinoFIX.message.JSBulkUploadFile.Entries.UploadFileStatus._name]==CmFinoFIX.UploadFileStatus.Uploaded || record.data[CmFinoFIX.message.JSBulkUploadFile.Entries.ErrorLineCount._name]===null)
                {
                    return "--";
                }
                return record.data[CmFinoFIX.message.JSBulkUploadFile.Entries.TotalLineCount._name] - record.data[CmFinoFIX.message.JSBulkUploadFile.Entries.ErrorLineCount._name];
            }
        },
        {
            header: _("Last Update Time"),
            width:150,
            renderer : "date",
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.LastUpdateTime._name
        },
        {
            header: _("Updated By"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.UpdatedBy._name
        },
        {
            header: _("Create Time"),
            width:150,
            renderer : "date",
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.CreateTime._name
        },
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSBulkUploadFile.Entries.CreatedBy._name
        }
        ]
    });

    mFino.widget.CustomerBulkUploadGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CustomerBulkUploadGrid, Ext.grid.GridPanel, {

    initComponent : function () {        
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.CustomerBulkUploadGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});