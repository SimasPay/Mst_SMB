/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.brand = function(config){

    var detailsForm = new mFino.widget.BrandDetailsDisplay(Ext.apply({
        }, config));

    var brandGrid = new mFino.widget.BrandDetailsGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
        },config));

    var brandSearch = new mFino.widget.BrandSearchForm(Ext.apply({
        }, config));

    brandSearch.on("brandSearch", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        brandGrid.store.baseParams = values;

        brandGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(brandGrid.store.lastOptions.params, values);
        brandGrid.store.load(brandGrid.store.lastOptions);
    });
    
    brandGrid.on("defaultSearch", function() {
        brandSearch.searchHandler();
    });
    
    brandGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(brandGrid.store);
    });
    brandGrid.on('render', function(){
        var tb = brandGrid.getTopToolbar();
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
        items: [ detailsForm,brandSearch,brandGrid ]
    });
    return panel;
};