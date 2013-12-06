/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.productIndicator = function(config){

    var detailsForm = new mFino.widget.ProductIndicatorDetails(Ext.applyIf({
        height : 110
    }, config));

    var searchBox = new mFino.widget.ProductIndicatorSearchForm(Ext.applyIf({
        height : 70
    }, config));
    
    var grid = new mFino.widget.ProductIndicatorGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
    }, config));

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            tbar : [ '<b class= x-form-tbar>' + _('Product Indicator Details') + '</b>' ],
            items: [  detailsForm ]
        }
        ]
    });

    searchBox.on(
        "ProductIndicatorSearchEvent", function(values){
            detailsForm.getForm().reset();
            if(values.TransactionTypeSearch === "undefined" ) {
                values.TransactionTypeSearch =null;
            }
            grid.store.baseParams = values;
            grid.store.lastOptions = {
                params : {
                    start : 0,
                    limit : CmFinoFIX.PageSize.Default
                }
            };
            Ext.apply(grid.store.lastOptions.params, values);
            grid.store.load(grid.store.lastOptions);
        });

    grid.on("defaultSearch", function() {
        searchBox.searchHandler();
    });

    grid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(grid.store);
    });
    grid.on('render', function(){
        var tb = grid.getTopToolbar();
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
        items: [ panelCenter,searchBox,grid]
    });
    return panel;
};