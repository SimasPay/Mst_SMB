/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.smsCodes = function(config){
    var detailsForm = new mFino.widget.SMSCodeDetails(Ext.apply({
        }, config));

    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.SMSCodeAddForm(config),
            height : 250,
            width : 350
        },config));
        
    var smsCodeSearch = new mFino.widget.SMSCodeSearchForm(Ext.apply({
        }, config));
    var smsCodesGrid = new mFino.widget.SMSCodesGrid(Ext.applyIf({
        layout:'fit',
        frame:true,
        loadMask:true
    },config));

    smsCodeSearch.on("smsCodeSearch", function(values){
        if(values.SMSCodeStatusSearch === "undefined") {
            values.SMSCodeStatusSearch = null;
        }
        
        detailsForm.getForm().reset();
        detailsForm.record = null;
        smsCodesGrid.store.baseParams = values;
    
        smsCodesGrid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(smsCodesGrid.store.lastOptions.params, values);
        smsCodesGrid.store.load(smsCodesGrid.store.lastOptions);
    });
    
    smsCodesGrid.on("defaultSearch", function() {
        smsCodeSearch.searchHandler();
    });

    smsCodesGrid.action.on({
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
                    gridAddForm.setTitle(_("Edit SMS Code"));
                    gridAddForm.setMode("edit");
                    gridAddForm.show();
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(grid.store);
                }
            }
        });

    smsCodesGrid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(smsCodesGrid.store);
    });
    smsCodesGrid.on('render', function(){
        var tb = smsCodesGrid.getTopToolbar();
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
        items: [ detailsForm, smsCodeSearch, smsCodesGrid ]
    });
    return panel;
};