/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.bulkUpload = function(config){
    var detailsForm = new mFino.widget.BulkUploadDetails(Ext.apply({
        }, config));

    var uploadGrid = new mFino.widget.CustomerBulkUploadGrid(config);
    var bulkUploadSearchPanel = new mFino.widget.BulkUploadSearchForm(config);
    
    bulkUploadSearchPanel.on("bulkuploadSearch", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.FileTypeSearch === "undefined"){
            values.FileTypeSearch = null;
        }
        if(values.UploadFileStatusSearch === "undefined"){
            values.UploadFileStatusSearch = null;
        }
        if(values.StartDateSearch){
            var date = new Date(Date.parse(values.StartDateSearch)).dateFormat('Ymd-H:i:s:u');
            values.StartDateSearch = getUTCdate(date);
        }
        uploadGrid.store.baseParams = values;

        uploadGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(uploadGrid.store.lastOptions.params, values);
        uploadGrid.store.load(uploadGrid.store.lastOptions);
    });

    uploadGrid.on("defaultSearch", function() {
        bulkUploadSearchPanel.searchHandler();
    });
    
    uploadGrid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                var testviewform = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
                    title : "Details for "+ record.get(CmFinoFIX.message.JSBulkUploadFile.Entries.RecordTypeText._name),
                    grid : new mFino.widget.BulkUploadFileViewGrid(config),
                    height : 466,
                    width: 800
                },config));
                testviewform.grid.store.lastOptions = {
                    params : {
                        start : 0,
                        limit : CmFinoFIX.PageSize.Default
                    }
                };
                testviewform.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadFileEntry.IDSearch._name] = record.get(CmFinoFIX.message.JSBulkUploadFile.Entries.ID._name);
                testviewform.grid.store.load(testviewform.grid.store.lastOptions);
                testviewform.setStore(testviewform.grid.store);
                testviewform.show();
            }
        }
    });

    uploadGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(uploadGrid.store);
    });
    uploadGrid.on('render', function(){
        var tb = uploadGrid.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
        for(i = 0; i < itemIDs.length; i++){
            var itemID = itemIDs[i];
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    });
    var bulkUploadSettings = new Ext.Panel({
        broder: false,
        items: [
        detailsForm,bulkUploadSearchPanel,uploadGrid
        ]
    });
    var panel = new Ext.Panel({
        broder: false,
        width : 1020,
        items: [
        bulkUploadSettings
        ]
    });
    return panel;
};