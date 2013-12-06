/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.enumPage = function(config){

    var detailsForm = new mFino.widget.EnumDetails(Ext.applyIf({
        height : 110
    }, config));

    var searchBox = new mFino.widget.EnumSearchForm(Ext.applyIf({
        height : 70
    }, config));

    var enumAddWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.EnumAddForm(config),
        modal:true,
        layout:'fit',
        floating: true,
        width:360,
        height:320,
        plain:true,
        mode : 'add',
        title : _('Add New Enum Language')
    },config));

    var enumEditWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.EnumEditForm(config),
        modal:true,
        layout:'fit',
        floating: true,
        width:360,
        height:320,
        plain:true,
        mode : 'edit',
        title : _('Edit New Enum Language')
    },config));

    var grid = new mFino.widget.EnumGrid(Ext.applyIf({
        layout:'fit',
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
           '<b class= x-form-tbar>' + _('Enum Details') + '</b>'            
            ],
            items: [  detailsForm ]
        }
        ]
    });

    searchBox.on(
        "EnumSearchEvent", function(values){
            detailsForm.getForm().reset();
            if(values.Language === "undefined" )
            {
                values.Language =null;
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

    grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-edit'){
                enumEditWindow.show();
                enumEditWindow.setRecord(detailsForm.record);
                enumEditWindow.setStore(grid.store);
            }else if (action === 'mfino-button-add'){
                var newrecord = new this.grid.store.recordType();
                newrecord.data[CmFinoFIX.message.JSEnumText.Entries.TagName._name] = record.data[CmFinoFIX.message.JSEnumText.Entries.TagName._name];
                newrecord.data[CmFinoFIX.message.JSEnumText.Entries.TagID._name] = record.data[CmFinoFIX.message.JSEnumText.Entries.TagID._name];
                newrecord.data[CmFinoFIX.message.JSEnumText.Entries.EnumValue._name] = record.data[CmFinoFIX.message.JSEnumText.Entries.EnumValue._name];
                newrecord.data[CmFinoFIX.message.JSEnumText.Entries.EnumCode._name] = record.data[CmFinoFIX.message.JSEnumText.Entries.EnumCode._name];
                newrecord.data[CmFinoFIX.message.JSEnumText.Entries.Language._name] = record.data[CmFinoFIX.message.JSEnumText.Entries.Language._name];

                enumAddWindow.show();
                enumAddWindow.setRecord(newrecord);
                enumAddWindow.setStore(grid.store);
            }
        }
    });


    var panel = new Ext.Panel({
        broder: false,
        width : 1020,
        items: [  panelCenter,searchBox,grid]
    });
    return panel;
};
