/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.transactionRule = function(config){

    var detailsForm = new mFino.widget.TransactionRuleDetails(Ext.apply({
        height : 218
    }, config));

    var searchBox = new mFino.widget.TransactionRuleSearchForm(Ext.apply({
        height : 200
    }, config));

    var gridAddForm = new mFino.widget.FormWindowWithValidation(Ext.apply({
        form : new mFino.widget.TransactionRuleForm(config),
        height : 470,
        width: 400
    },config));

    var gridViewForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.TransactionRuleViewForm(config),
        height: 470,
        width: 400,
        mode:'close'
    },config));

    var grid = new mFino.widget.TransactionRuleGrid(Ext.apply({
        layout:'fit',
        title : _('Transaction Rule Search Results'),
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
            '<b class= x-form-tbar>' + _('Transaction Rule Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-pocket-add',
                tooltip : _('Add Transaction Rule'),
                text : _('New'),
                itemId: 'transactionrule.details.add',
                handler: function(){
                    var record = new grid.store.recordType();
                    gridAddForm.setTitle(_("Add Transaction Rule"));
                    gridAddForm.setMode("add");
                    gridAddForm.show();
                    gridAddForm.setEditable(true);
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(grid.store);
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
        if(values.ServiceTypeNameSearch === "undefined"){
            values.ServiceTypeNameSearch = null;
        }
        if(values.TransactionTypeNameSearch === "undefined"){
            values.TransactionTypeNameSearch = null;
        } 
        if(values.AccessChannelSearch === "undefined"){
            values.AccessChannelSearch = null;
        } 
        if(values.ChargeModeSearch === "undefined"){
            values.ChargeModeSearch = null;
        }         
        grid.store.baseParams = values;
        grid.store.baseParams[CmFinoFIX.message.JSPocketTemplate.StartDateSearch._name] = values.startDate;
        grid.store.baseParams[CmFinoFIX.message.JSPocketTemplate.EndDateSearch._name] = values.endDate;

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
                gridAddForm.setTitle( _("Edit Transaction Rule"));
                gridAddForm.setMode("edit");
                gridAddForm.show();
                gridAddForm.setEditable(true);
                gridAddForm.form.find('itemId','transactionrule.form.chargemode')[0].enable();
                gridAddForm.setRecord(record);
                gridAddForm.setStore(grid.store);
            }
            if(action === 'mfino-button-View'){
                gridViewForm.setTitle( _("View Transaction Rule"));
                gridViewForm.setMode("view");
                gridViewForm.show();
                gridViewForm.form.txnRuleAddnInfoGridView.disable();
                gridViewForm.setRecord(record);
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
            height: 455,
            layout : "fit",
            items: [ grid ]
        }]
    });
    return panel;
};
