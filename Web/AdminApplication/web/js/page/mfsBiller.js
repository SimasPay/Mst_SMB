/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.mfsBiller = function(config){

    var detailsForm = new mFino.widget.MFSBillerDetails(Ext.apply({
    	height : 173
    }, config));

    var searchBox = new mFino.widget.MFSBillerSearchForm(Ext.apply({
        height : 200
    }, config));

    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.MFSBillerForm(config),
        width : 350,
        height : 175
    },config));
    
    var listBox = new mFino.widget.MFSBillerList(Ext.apply({
        anchor : "100%, -200"
    }, config));
    
    listBox.store.form = gridAddForm.form;
    
    var mfsbpGrid = new mFino.widget.MFSBillerPartnerGrid(config);
    var mfsbpForm = new mFino.widget.FormWindowWithValidation(Ext.apply({
        form : new mFino.widget.MFSBillerPartnerForm(config),
        height : 410,
        width : 450
    },config));

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            autoScroll : true,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Biller Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-user-add',
                tooltip : _('Add Biller'),
                itemId: 'mfsb.add',
                handler: function(){
                    var record = new listBox.store.recordType();
                    gridAddForm.setTitle(_("Add Biller"));
                    gridAddForm.setMode("add");
                    gridAddForm.show();
                    gridAddForm.setRecord(record);
                    gridAddForm.setStore(listBox.store);
                }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit Biller'),
                itemId : 'mfsb.edit',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Biller selected!"));
                    }else{
                    	gridAddForm.setTitle( _("Edit Biller"));
                    	gridAddForm.setMode("edit");
                    	gridAddForm.show();
                    	gridAddForm.setRecord(detailsForm.record);
                    	gridAddForm.setStore(detailsForm.store);
                    }
                }
            },
            {
                iconCls: 'mfino-button-remove',
                tooltip : _('Delete Biller'),
                itemId : 'mfsb.delete',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Biller selected!"));
                    }else{
                        Ext.MessageBox.confirm(_('Delete biller?'),_('Do you want to delete this Biller?'),
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
            items: [ mfsbpGrid ]
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
        if(values.BillerNameSearch === "undefined"){
            values.BillerNameSearch = null;
        } 
        if(values.BillerCodeSearch === "undefined"){
            values.BillerCodeSearch = null;
        }
        if(values.BillerTypeSearch === "undefined"){
            values.BillerTypeSearch = null;
        }
        listBox.store.baseParams = values;
        listBox.store.baseParams[CmFinoFIX.message.JSPocketTemplate.StartDateSearch._name] = values.startDate;
        listBox.store.baseParams[CmFinoFIX.message.JSPocketTemplate.EndDateSearch._name] = values.endDate;

        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(listBox.store.lastOptions.params, values);
        listBox.store.load(listBox.store.lastOptions);
        mfsbpGrid.setBillerRecord(null);
    });

    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        mfsbpGrid.setBillerRecord(record);
    });
    
    mfsbpGrid.on("addMFSBPartner",function(){
        if(!detailsForm.record){
            Ext.MessageBox.alert(_("Alert"), _("No Biller selected!"));
        }
        else{
            var record = new mfsbpGrid.store.recordType();
            record.data[CmFinoFIX.message.JSMFSBillerPartner.Entries.MFSBillerId._name] = 
            	detailsForm.record.data[CmFinoFIX.message.JSMFSBiller.Entries.ID._name];
            mfsbpForm.reset("tabpanelTopupDenomination");
            mfsbpForm.setTitle(_("Add Partner"));
            mfsbpForm.setMode("add");
            mfsbpForm.show();
            mfsbpForm.setRecord(record);
            mfsbpForm.setStore(mfsbpGrid.store);
        }
    });
    mfsbpGrid.action.on({
        action: function(grid, record, action, row, col) {
            if(action === 'mfino-button-edit'){
            	mfsbpForm.reset("tabpanelTopupDenomination");
                mfsbpForm.setTitle(_("Edit Partner"));
                mfsbpForm.setMode("edit");
                mfsbpForm.show();
                mfsbpForm.setRecord(record);
                mfsbpForm.setStore(grid.store);
            } else if(action === 'mfino-button-remove') {
                Ext.MessageBox.confirm(
                    _('Remove Partner?'),
                    _('Do you want to remove this Partner?'),
                    function(btn){
                        if (btn == 'yes') {
                            if(grid.store) {
                                grid.store.remove(record);
                                if(!record.phantom){
                                    grid.store.save();
                                }
                            }
                        }
                    }
                );
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
            layout : "anchor",
            items:[ searchBox, listBox ]
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
