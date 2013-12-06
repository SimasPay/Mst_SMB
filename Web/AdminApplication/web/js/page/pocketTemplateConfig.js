/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.pocketTemplateConfig = function(config){

    var detailsForm = new mFino.widget.PocketTemplateConfigIssuerDetails(Ext.apply({
        height : 220
    }, config));

    var searchBox = new mFino.widget.PocketTemplateConfigSearchForm(Ext.apply({
        height : 215
    }, config));

    var gridAddForm = new mFino.widget.FormWindowForPocketTemplateConfig(Ext.apply({
        form : new mFino.widget.PocketTemplateConfigIssuerForm(config),
        height : 350,
		width : 500
    },config));

    var gridViewForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.PocketTemplateConfigIssuerViewForm(config),
        height : 350,
		width : 600,
        mode:'close'
    },config));

    var grid = new mFino.widget.PocketTemplateConfigIssuerGrid(Ext.apply({
        layout:'fit',
        title : _('Pocket Template Config Search Results'),
        frame:true,
        loadMask:true,
        height: 485,
        width: 925
    }, config));


    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            autoScroll : true,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Pocket Template Config Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-pocket-add',
                tooltip : _('Add Pocket Template Config'),
                text : _('New'),
                itemId: 'pockettemplate.details.add',
                handler: function(){
                    var record = new grid.store.recordType();
                   // gridAddForm.reset("tabelpanelPocketTemplate");
                    gridAddForm.setTitle(_("Add Pocket Template Config"));
                    gridAddForm.setMode("add");
                    gridAddForm.show();
                   // gridAddForm.form.enableFields();
                   // gridAddForm.form.find('itemId','regexp')[0].disable();
                   // gridAddForm.form.find('itemId','partnerCode')[0].disable();
                   // gridAddForm.form.find('itemId','bankAccount')[0].disable();
                   // gridAddForm.form.find('itemId','typeofcheck')[0].disable();
                    record.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name,1);
                    record.set(CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupName._name,'ANY');
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(grid.store);
                   // gridAddForm.resetAll('tabelpanelPocketTemplate');
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
            if(values.PocketTypeSearch === "undefined" ){
                values.PocketTypeSearch =null;
            }
            if(values.CommodityTypeSearch === "undefined"){
                values.CommodityTypeSearch =null;
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
               // gridAddForm.reset("tabelpanelPocketTemplate");
                gridAddForm.setTitle( _("Edit Pocket Template Config"));
                gridAddForm.setMode("edit");
                gridAddForm.show();
                //gridAddForm.form.disableFields();
                if(record.data[CmFinoFIX.message.JSPocketTemplateConfig.Entries.SubscriberType._name] == "0"){
					gridAddForm.form.find('itemId','ptc.form.businessType')[0].setValue("1");
                    gridAddForm.form.find('itemId','ptc.form.businessType')[0].disable();
					gridAddForm.form.find('itemId','ptc.form.kyclevel')[0].enable();
                }else{
                    gridAddForm.form.find('itemId','ptc.form.businessType')[0].enable();
					gridAddForm.form.find('itemId','ptc.form.kyclevel')[0].setValue("3");
                    gridAddForm.form.find('itemId','ptc.form.kyclevel')[0].disable();
                } 
               // if(record.data[CmFinoFIX.message.JSPocketTemplate.Entries.Commodity._name] == CmFinoFIX.Commodity.Money){
               //    gridAddForm.form.find('itemId','partnerCode')[0].enable();
               // }else{
               //     gridAddForm.form.find('itemId','partnerCode')[0].disable();
               // } 
                gridAddForm.setRecord(record);
                gridAddForm.setStore(grid.store);
            }
            if(action === 'mfino-button-View'){
               // gridViewForm.reset();
                gridViewForm.setTitle( _("View Pocket Template Config"));
                gridViewForm.setMode("view");
                gridViewForm.show();
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
            height: 460,
            layout : "fit",
            items: [ grid ]
        }]
    });
    return panel;
};
