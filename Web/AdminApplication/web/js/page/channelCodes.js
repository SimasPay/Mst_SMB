/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.channelCodes = function(config){
    var detailsForm = new mFino.widget.ChannelCodeDetails(Ext.apply({
        }, config));

    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.ChannelCodeAddForm(config),
            height : 200,
            width : 350
        },config));

    var channelCodeSearch = new mFino.widget.ChannelCodeSearchForm(Ext.apply({
        }, config));
    var channelCodesGrid = new mFino.widget.ChannelCodesGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
    },config));

    channelCodeSearch.on("channelCodeSearch", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.SourceApplicationSearch === "undefined"){
            values.SourceApplicationSearch =null;
        }
        channelCodesGrid.store.baseParams = values;

        channelCodesGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(channelCodesGrid.store.lastOptions.params, values);
        channelCodesGrid.store.load(channelCodesGrid.store.lastOptions);
    });

    channelCodesGrid.on("defaultSearch", function() {
        channelCodeSearch.searchHandler();
    });

    channelCodesGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-remove'){
                    Ext.MessageBox.confirm(
                        _('Delete Level?'),
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
                    gridAddForm.setTitle(_("Edit Channel Code"));
                    gridAddForm.setMode("edit");
                    gridAddForm.show();
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(grid.store);
                }
            }
        });

    channelCodesGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(channelCodesGrid.store);
    });
    channelCodesGrid.on('render', function(){
        var tb = channelCodesGrid.getTopToolbar();
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
        items: [ detailsForm, channelCodeSearch, channelCodesGrid ]
    });
    return panel;
};