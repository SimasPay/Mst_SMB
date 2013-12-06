/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.notification = function(config){

    var detailsForm = new mFino.widget.NotificationDetails(Ext.apply({
        }, config));

    var searchBox = new mFino.widget.NotificationSearchForm(Ext.apply({
        height : 70
    }, config));

    var notificationEditWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.NotificationEditForm(config),
        mode : 'edit',
        modal:true,
        title : _('Edit Notification Message'),
        layout:'fit',
        floating: true,
        width:360,
        height:375,
        plain:true
    },config));
    var grid = new mFino.widget.NotificationsGrid(Ext.apply({
        height: 480
    }, config));

    var notificationAddWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.NotificationAddForm(config),
        mode : 'add',
        modal:true,
        title : _('Add Notification Message'),
        layout:'fit',
        floating: true,
        width:360,
        height:375,
        plain:true
    },config));

    grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-edit' && record !== null){
                notificationEditWindow.show();
                notificationEditWindow.setRecord(record);
                notificationEditWindow.setStore(grid.store);
            }else if (action === 'mfino-button-add' && record !== null){
                var newrecord = new this.grid.store.recordType();
                newrecord.data[CmFinoFIX.message.JSNotification.Entries.NotificationCode._name] =record.data[CmFinoFIX.message.JSNotification.Entries.NotificationCode._name];
                newrecord.data[CmFinoFIX.message.JSNotification.Entries.NotificationCodeName._name] =record.data[CmFinoFIX.message.JSNotification.Entries.NotificationCodeName._name];
                newrecord.data[CmFinoFIX.message.JSNotification.Entries.MSPID._name] =record.data[CmFinoFIX.message.JSNotification.Entries.MSPID._name];
                
                notificationAddWindow.show();
                notificationAddWindow.setRecord(newrecord);
                notificationAddWindow.setStore(grid.store);
            }
        }
    });

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            height : 150,
            layout : "fit",
            tbar : [
           '<b class= x-form-tbar>' + _('Notification Details') + '</b>'
            ],
            items: [ detailsForm ]
        }
        ]
    });

    searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        if(values.Language === "undefined" )
        {
            values.Language =null;
        }
        if(values.NotificationMethod === "undefined")
        {
            values.NotificationMethod =null;
        }
        if(values.NotificationCodeName === "undefined")
        {
            values.NotificationCodeName =null;
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

    var panel = new Ext.Panel({
        broder: false,
        width : 1020,
        items: [ panelCenter, searchBox, grid]
    });
    return panel;
};
