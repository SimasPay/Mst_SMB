/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.merchantCodes = function(config){
    var detailsForm = new mFino.widget.MerchantCodeDetails(Ext.apply({
        }, config));

    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.MerchantCodeAddForm(config),
            height : 150,
            width : 350
        },config));

    var merchantCodeSearch = new mFino.widget.MerchantCodeSearchForm(Ext.apply({
        }, config));
    var merchantCodesGrid = new mFino.widget.MerchantCodesGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
    },config));

    merchantCodeSearch.on("merchantCodeSearch", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        merchantCodesGrid.store.baseParams = values;

        merchantCodesGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(merchantCodesGrid.store.lastOptions.params, values);
        merchantCodesGrid.store.load(merchantCodesGrid.store.lastOptions);
    });

    merchantCodesGrid.on("defaultSearch", function() {
        merchantCodeSearch.searchHandler();
    });

    merchantCodesGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-remove'){
                    Ext.MessageBox.confirm(
                        _('Delete Merchant Code?'),
                        _('Do you want to delete this code?'),
                        function(btn){
                            if (btn == 'yes') {
                                if(grid.store) {
                                    grid.store.remove(record);
                                    if(!record.phantom){
                                        grid.store.save();
                                    }
                                }
                            }
                        });
                } else if(action === 'mfino-button-edit'){
                    gridAddForm.setTitle(_("Edit Merchant Code"));
                    gridAddForm.setMode("edit");
                    gridAddForm.show();
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(grid.store);
                }
            }
        });

    merchantCodesGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(merchantCodesGrid.store);
    });
    merchantCodesGrid.on('render', function(){
        var tb = merchantCodesGrid.getTopToolbar();
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
        items: [ detailsForm, merchantCodeSearch, merchantCodesGrid ]
    });
    return panel;
};