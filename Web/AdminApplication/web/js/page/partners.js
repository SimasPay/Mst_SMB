/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.partners = function(config){

    var detailsForm = new mFino.widget.PartnerDetails(Ext.apply({
        }, config));

    var searchBox = new mFino.widget.PartnerSearchForm(Ext.apply({
        height : 200
    }, config));

    var partnerDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.PartnerDetails(config),
        height : 330,
        width: 800,
        mode : "close",
        title : _('Partner Details')
    },config));

    var grid = new mFino.widget.PartnerGrid(Ext.apply({
        //        title : _('LOP Search Results'),
        height: 500
    }, config));

    var distributeWindow = new mFino.widget.DistributeWindow(config);
    var confirmApproveWindow =new mFino.widget.ConfirmApproveWindow(config);
    var confirmRejectWindow =new mFino.widget.ConfirmRejectWindow(config);
    var lopDiscountWindow = new mFino.widget.BulkLOPDiscountWindow(config);

    grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                partnerDetailsWindow.show();
                partnerDetailsWindow.setRecord(record);
                partnerDetailsWindow.setStore(grid.store);
            }
        }
    });

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height: 250,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Partner Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-discount',
                tooltip : _('Change/Add LOP Discount'),
                id : 'discount',
                text : _('Change Discount'),
                itemId: 'lop.discount',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _('No Record selected!'),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else{
                        lopDiscountWindow.show();
                        lopDiscountWindow.setRecord(detailsForm.record);
                    }
                }
            },
            {
                iconCls: 'mfino-button-approve',
                tooltip : _('Approve LOP'),
                id : 'approve',
                text : _('Approve'),
                itemId: 'lop.approve',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _('No Record selected!'),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else{
                        confirmApproveWindow.show();
                        confirmApproveWindow.setRecord(detailsForm.record);
                    }
                }
            },
            {
                iconCls: 'mfino-button-distributelop',
                tooltip : _("Distribute LOP"),
                text : _('Distribute'),
                id :'distribute',
                itemId: 'lop.distribute',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("No Record selected!"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else{
                        distributeWindow.show();
                        distributeWindow.setRecord(detailsForm.record);                              

                    }
                }
            },
            {
                iconCls: 'mfino-button-reject',
                tooltip : _("Reject LOP"),
                text : _('Reject'),
                id : 'reject',
                itemId: 'lop.reject',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("No Record selected!"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });

                    }else{
                        confirmRejectWindow.show();
                        confirmRejectWindow.setRecord(detailsForm.record);
                    }
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

    searchBox.on("search", function(values){
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(values.LOPStatusSearch === "undefined"){
            values.LOPStatusSearch =null;
        }
        if(values.LOPViewSearch === "undefined" || values.LOPViewSearch === ""){
            values.LOPViewSearch = null;
        }
        grid.store.baseParams = values;
        grid.store.baseParams[CmFinoFIX.message.JSLOP.StartDateSearch._name] = values.startDate;
        grid.store.baseParams[CmFinoFIX.message.JSLOP.EndDateSearch._name] = values.endDate;

        grid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(grid.store.lastOptions.params, values);
        grid.store.load(grid.store.lastOptions);
    });
    grid.on("download", function() {
        var values = searchBox.getForm().getValues();
        var idSearch = values[CmFinoFIX.message.JSLOP.IDSearch._name];
        var distributorNameSearch = values[CmFinoFIX.message.JSLOP.DCTNameSearch._name];
        var userNameSearch = values[CmFinoFIX.message.JSLOP.UsernameSearch._name];
        var lopStatusSearch = values[CmFinoFIX.message.JSLOP.LOPStatusSearch._name];
        var lopViewSearch = values[CmFinoFIX.message.JSLOP.LOPViewSearch._name];
        var startDateSearch = values.startDate;
        var endDateSearch = values.endDate;
        var queryString = "dType=lop";
        if(idSearch){
            queryString += "&"+CmFinoFIX.message.JSLOP.IDSearch._name+"="+idSearch;
        }
        if(distributorNameSearch){
            queryString += "&"+CmFinoFIX.message.JSLOP.DCTNameSearch._name+"="+distributorNameSearch;
        }
        if(userNameSearch){
            queryString += "&"+CmFinoFIX.message.JSLOP.UsernameSearch._name+"="+userNameSearch;
        }
        if(lopStatusSearch !== null && !(lopStatusSearch === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSLOP.LOPStatusSearch._name+"="+lopStatusSearch;
        }
        if(lopViewSearch !== null && !(lopViewSearch === "undefined")){
            queryString += "&"+CmFinoFIX.message.JSLOP.LOPViewSearch._name+"="+lopViewSearch;
        }
        if(startDateSearch){
            queryString += "&"+CmFinoFIX.message.JSLOP.StartDateSearch._name+"="+getUTCdate(startDateSearch);
        }
        if(endDateSearch){
            queryString += "&"+CmFinoFIX.message.JSLOP.EndDateSearch._name+"="+getUTCdate(endDateSearch);
        }
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });
    grid.on("defaultSearch", function() {
        searchBox.searchHandler();
    });

    grid.selModel.on("rowselect", function(sm, rowIndex, record){
        var status = record.data[CmFinoFIX.message.JSLOP.Entries.Status._name];
        var approve = Ext.getCmp('approve');
        var reject = Ext.getCmp('reject');
        var distribute = Ext.getCmp('distribute');
        var discount = Ext.getCmp('discount');
        
        if(status === CmFinoFIX.LOPStatus.Distributed || status === CmFinoFIX.LOPStatus.Expired){
            if(approve){
                approve.hide();
            }
            if(reject){
                reject.hide();
            }
            if(distribute){
                distribute.hide();
            }
            if(discount){
                discount.hide();
            }
        }
        else if(status === CmFinoFIX.LOPStatus.Approved){
            if(approve){
                approve.hide();
            }
            if(reject){
                reject.show();
            }
            if(distribute){
                distribute.show();
            }
            if(discount){
                discount.hide();
            }
        }
        else if(status === CmFinoFIX.LOPStatus.Rejected){
            if(approve){
                approve.show();
            }
            if(reject){
                reject.hide();
            }
            if(distribute){
                distribute.hide();
            }
            if(discount){
                discount.hide();
            }
        }
        else if(status === CmFinoFIX.LOPStatus.Pending){
            if(approve){
                approve.show();
            }
            if(reject){
                reject.show();
            }
            if(distribute){
                distribute.hide();
            }
            if(discount){
                discount.show();
            }
        }
        detailsForm.setRecord(record);
        detailsForm.setStore(grid.store);
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
            height : 455,
            layout : "fit",
            items: [ grid ]
        }]
    });
    return panel;
};
