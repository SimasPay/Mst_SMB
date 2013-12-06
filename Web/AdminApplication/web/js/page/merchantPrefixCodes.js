
Ext.ns("mFino.page");

mFino.page.merchantPrefixCodes = function(config){
    var detailsForm = new mFino.widget.MerchantPrefixCodeDetails(Ext.apply({
        }, config));

    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.MerchantPrefixCodeAddForm(config),
            height : 200,
            width : 350
        },config));

    var merchantPrefixCodeSearch = new mFino.widget.MerchantPrefixCodeSearchForm(Ext.apply({
        }, config));
    var merchantPrefixCodesGrid = new mFino.widget.MerchantPrefixCodesGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
    },config));

    merchantPrefixCodeSearch.on("merchantPrefixCodeSearch", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        merchantPrefixCodesGrid.store.baseParams = values;

        merchantPrefixCodesGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(merchantPrefixCodesGrid.store.lastOptions.params, values);
        merchantPrefixCodesGrid.store.load(merchantPrefixCodesGrid.store.lastOptions);
    });

    merchantPrefixCodesGrid.on("defaultSearch", function() {
        merchantPrefixCodeSearch.searchHandler();
    });

    merchantPrefixCodesGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-remove'){
//                    Ext.MessageBox.confirm(
//                        _('Delete Merchant Code?'),
//                        _('Do you want to delete this code?'),
//                        function(btn){
//                            if (btn == 'yes') {
//                                if(grid.store) {
//                                    grid.store.remove(record);
//                                    if(!record.phantom){
//                                        grid.store.save();
//                                    }
//                                }
//                            }
//                        });
                } else if(action === 'mfino-button-edit'){
                    gridAddForm.setTitle(_("Edit Merchant Code"));
                    gridAddForm.setMode("edit");
                    gridAddForm.show();
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(grid.store);
                }
            }
        });

    merchantPrefixCodesGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(merchantPrefixCodesGrid.store);
    });
    merchantPrefixCodesGrid.on('render', function(){
        var tb = merchantPrefixCodesGrid.getTopToolbar();
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
    var panel = new Ext.Panel({
        broder: false,
        width : 1020,
        items: [ detailsForm, merchantPrefixCodeSearch, merchantPrefixCodesGrid ]
    });
    return panel;
};