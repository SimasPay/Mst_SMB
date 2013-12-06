/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.actorChannelMapping = function(config){

    var detailsForm = new mFino.widget.ActorChannelMappingDetails(Ext.apply({
        height : 238
    }, config));

    var searchBox = new mFino.widget.ActorChannelMappingSearchForm(Ext.apply({
        height : 190
    }, config));

    var addForm = new mFino.widget.FormWindowWithValidation(Ext.apply({
        form : new mFino.widget.ActorChannelMappingForm(config),
        height : 315,
        width: 400
    },config));  

    var grid = new mFino.widget.ActorChannelMappingGrid(Ext.apply({
        layout:'fit',
        title : _('Actor Channel Mapping Search Results'),
        frame:true,
        loadMask:true,
        height: 455,
        width: 925
    }, config));


    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            autoScroll : true,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Actor Channel Mapping Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-pocket-add',
                tooltip : _('Add Actor Channel Mapping'),
                text : _('New'),
                itemId: 'actorChannelMapping.edit',
                handler: function(){
                    var record = new grid.store.recordType();                    
                    addForm.setTitle(_("Add Actor Channel Mapping"));
                    addForm.setMode("add");
                    addForm.setEditable(true);
                    addForm.show();
                    addForm.setRecord(record);
                    addForm.setStore(grid.store);
                }
            }
            ],
            items: [ detailsForm ]
        }
        ]
    });

    var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){
        var tb = mainItem.getTopToolbar();
        var itemIDs = [];
        //0, 1 are the header and alignment items
        for(var i = 2; i < tb.items.length; i++){
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

    searchBox.on("search", function(values){
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
            	addForm.setTitle( _("Edit Actor Channel Mapping"));
            	addForm.setMode("edit");
            	addForm.show();
            	addForm.setRecord(record);
            	addForm.setStore(grid.store);
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
            height: 435,
            layout : "fit",
            items: [ grid ]
        }]
    });
    return panel;
};
