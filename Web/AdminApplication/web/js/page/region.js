/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");
mFino.page.region = function(config){

    var detailsForm = new mFino.widget.RegionDetailsDisplay(Ext.apply({
        }, config));

    var regionGrid = new mFino.widget.RegionDetailsGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
    },config));

    var regionSearch = new mFino.widget.RegionSearchForm(Ext.apply({
        }, config));

    regionSearch.on("regionSearch", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.RegionNameSearch === "undefined"){
            values.RegionNameSearch = null;
        }
        regionGrid.store.baseParams = values;
        regionGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(regionGrid.store.lastOptions.params, values);
        regionGrid.store.load(regionGrid.store.lastOptions);
    });
    
    regionGrid.on("defaultSearch", function() {
        regionSearch.searchHandler();
    });

    regionGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(regionGrid.store);
    });
    regionGrid.on('render', function(){
        var tb = regionGrid.getTopToolbar();
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
        items: [ detailsForm,regionSearch,regionGrid]
    });
    return panel;
};