/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.biller = function(config){
    var gridEditForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.BillerAddForm(config),
        width: 350,
        height: 280
    },config));

    var detailsForm = new mFino.widget.BillerDetails(config);

    var searchBox = new mFino.widget.BillerSearchForm(Ext.apply({
        height : 200
    }, config));

    var listBox = new mFino.widget.BillerList(Ext.apply({
        anchor : "100%, -200"
    }, config));
    //this enables serverside form validation
    listBox.store.form = gridEditForm.form;
    var denominationGrid = new mFino.widget.DenominationGrid(config);
    var denominationForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.DenominationForm(config),
        height : 150,
        width : 350
    },config));
    var denominationDetailsForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.DenominationDetails(config),
        title : _("Denomination Details"),
        mode : "close",
        width:350,
        height:150
    },config));

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 200,
            anchor : "100%",
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('Biller Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-user-add',
                tooltip : _('Add Biller'),
                itemId : 'biller.add',
                handler:  function(){
                    var record = new listBox.store.recordType();
                    gridEditForm.setTitle(_("Add Biller"));
                    gridEditForm.setMode("add");
                    gridEditForm.show();
                    gridEditForm.form.find('itemId','biller.billerType')[0].enable();
                    gridEditForm.setRecord(record);
                    gridEditForm.setStore(listBox.store);
                }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit Biller'),
                itemId : 'biller.edit',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Biller selected!"));
                    }else{
                        gridEditForm.setTitle( _("Edit Biller"));
                        gridEditForm.setMode("edit");
                        gridEditForm.show();
                        gridEditForm.form.find('itemId','biller.billerType')[0].disable();
                        gridEditForm.setRecord(detailsForm.record);
                        gridEditForm.setStore(detailsForm.store);
                    }
                }
            },
            {
                iconCls: 'mfino-button-remove',
                tooltip : _('Delete Biller'),
                itemId : 'biller.delete',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Biller selected!"));
                    }else{
                        Ext.MessageBox.confirm(_('Delete biller?'),_('Do you want to delete this biller?'),
                            function(btn){
                                if (btn == 'yes') {
                                    if(detailsForm.store) {
                                        detailsForm.store.remove(detailsForm.record);
                                        if(!detailsForm.record.phantom){
                                            detailsForm.store.save();
                                        }
                                    }
                                }
                            });
                    }
                }
            }
            ],
            items: [ detailsForm ]
        },
        {
            anchor : "100%, -200",
            layout: "fit",
            items: [ denominationGrid ]
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

    searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.BillerTypeSearch === "undefined"){
            values.BillerTypeSearch =null;
        }
        listBox.store.baseParams = values;
        listBox.store.baseParams[CmFinoFIX.message.JSSMSPartner.StartDateSearch._name] = values.startDate;
        listBox.store.baseParams[CmFinoFIX.message.JSSMSPartner.EndDateSearch._name] = values.endDate;

        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(listBox.store.lastOptions.params, values);
        listBox.store.load(listBox.store.lastOptions);
        denominationGrid.setBillerRecord(null);
    });

    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        denominationGrid.setBillerRecord(record);
    });
    denominationGrid.on("addDenomination",function(){
        if(!detailsForm.record){
            Ext.MessageBox.alert(_("Alert"), _("No Biller selected!"));
        }
        else{
            var record = new denominationGrid.store.recordType();
            record.data[CmFinoFIX.message.JSDenomination.Entries.BillerID._name] = detailsForm.record.data[CmFinoFIX.message.JSBiller.Entries.ID._name];
            //            record.data[CmFinoFIX.message.JSDenomination.Entries.BankID._name] = detailsForm.record.data[CmFinoFIX.message.JSBiller.Entries.BankCodeForRouting._name];
            denominationForm.setTitle(_("Add Denomination"));
            denominationForm.setMode("add");
            denominationForm.show();
            denominationForm.setRecord(record);
            denominationForm.setStore(denominationGrid.store);
        }
    });
    denominationGrid.action.on({
        action: function(grid, record, action, row, col) {
            if(action === 'mfino-button-edit'){
                denominationForm.setTitle(_("Edit Denomination"));
                denominationForm.setMode("edit");
                denominationForm.show();
                denominationForm.setRecord(record);
                denominationForm.setStore(grid.store);
            } else if (action === 'mfino-button-View') {
                denominationDetailsForm.show();
                denominationDetailsForm.setRecord(record);
                denominationDetailsForm.setStore(grid.store);
            } else if(action === 'mfino-button-remove') {
                Ext.MessageBox.confirm(
                    _('Delete denomination?'),
                    _('Do you want to delete this denomination?'),
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
            width : 266,
            layout : "anchor",
            items:[ searchBox , listBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        }
        ]
    });
    return panel;
};
