/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.distribution = function(config){

    var detailsForm = new mFino.widget.DCTDetails(Ext.applyIf({
        height : 200
    }, config));

    var searchBox = new mFino.widget.DCTSearchForm(Ext.applyIf({
        height : 200
    }, config));

    var DCTFormWindow = new mFino.widget.DCTWindow(Ext.applyIf({
        form : new mFino.widget.DCTForm(config),
        title : _('New Distribution Chain Template'),
        resizable: false,
        height:420,
        width:830
    },config));
    
    var grid = new mFino.widget.DCTGrid(Ext.applyIf({
        layout:'fit',
        title: _('Distribution Chain Template Search Results'),
        frame:true,
        loadMask:true,
        height: 500        
    }, config));

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            tbar : [
          '<b class= x-form-tbar>' + _('Administration - DCT Details ') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-dist-add',
                tooltip : _('Add Distribution Chain Template'),
                text : _('New'),
                itemId: 'dct.details.add',
                handler: function(){
                    var record = new grid.store.recordType();
                    DCTFormWindow.setMode("add");
                    DCTFormWindow.setTitle(_("New DC Template"));
                    DCTFormWindow.show();
                    DCTFormWindow.setRecord(record);
                    DCTFormWindow.setStore(grid.store);
                }
            }
            ],
            items: [  detailsForm ]
        }
        ]
    });
    var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){
        var tb = mainItem.getTopToolbar();
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

    searchBox.on(
        "DCTSearchEvent", function(values){
            detailsForm.getForm().reset();
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

    grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-edit'){
                DCTFormWindow.setMode("edit");
                DCTFormWindow.setTitle( _("Edit Distribution Template"));
                DCTFormWindow.setStore(grid.store);
                DCTFormWindow.show();
                DCTFormWindow.setRecord(record);
            }
        }
    });

    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 250,
            layout : "fit",
            items:[ searchBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        },
        {
            region: 'south',
            height:500,
            layout : "fit",
            items: [ grid ]
        }]
    });
    return panel;
};
