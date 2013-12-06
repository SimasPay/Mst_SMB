/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.smsPartner = function(config){
    var gridEditForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.SMSPartnerForm(config),
        width: 350,
        height: 300
    },config));

    var detailsForm = new mFino.widget.SMSPartnerDetails(config);

    var searchBox = new mFino.widget.SMSPartnerSearchForm(Ext.apply({
        height : 200
    }, config));
    
    var listBox = new mFino.widget.SMSPartnerList(Ext.apply({
        anchor : "100%, -200"
    }, config));
    //this enables serverside form validation
    listBox.store.form = gridEditForm.form;
    var smscGrid = new mFino.widget.SMSCGrid(config);
    var smscForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.SMSCAddForm(config),
        height : 300,
        width : 350
    },config));
//    var smscEditForm = new mFino.widget.FormWindow(Ext.apply({
//        form : new mFino.widget.SMSCEditForm(config),
//        title : _("Edit SMSC Detail"),
//        mode : "edit",
//        width : 350,
//        height : 300
//    },config));
    var smscDetailsForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.SMSCDetails(config),
        title : _("SMSC Details"),
        mode : "close",
        width:350,
        height:300
    },config));

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 200,
            anchor : "100%",
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('SMS Partner Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-user-add',
                tooltip : _('Add SMS Partner'),
                itemId : 'smspartner.add',
                handler:  function(){
                    var record = new listBox.store.recordType();
                    gridEditForm.setTitle(_("Add SMS Partner"));
                    gridEditForm.setMode("add");
                    gridEditForm.show();
                    gridEditForm.form.find('itemId','sms.partner.username')[0].enable();
                    gridEditForm.form.find('itemId','sms.partner.password')[0].enable();
                    gridEditForm.setRecord(record);
                    gridEditForm.setStore(listBox.store);
                }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit SMS Partner'),
                itemId : 'smspartner.edit',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Partner selected!"));
                    }else{
                        gridEditForm.setTitle( _("Edit SMS Partner"));
                        gridEditForm.setMode("edit");
                        gridEditForm.show();
                        gridEditForm.form.find('itemId','sms.partner.username')[0].disable();
                        gridEditForm.form.find('itemId','sms.partner.password')[0].disable();
                        gridEditForm.setRecord(detailsForm.record);
                        gridEditForm.setStore(detailsForm.store);
                    }
                }
            },
            {
                iconCls: 'mfino-button-key',
                itemId : 'smspartner.resetapitoken',
                tooltip : _('Reset APItoken'),
                handler : function(){
                    if(!(detailsForm.record)){
                        Ext.MessageBox.alert(_("Alert"), _("No Smspartner selected!"));
                    }else{
                        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Reset APItoken?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg = new CmFinoFIX.message.JSResetAPItoken();
                                var params = mFino.util.showResponse.getDisplayParam();
                                Ext.apply(params, {
                                    success :  function(response){
                                    	if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                                            Ext.ux.Toast.msg(_('Success'), _("APItoken has been reset"));
                                        } else{
                                            Ext.Msg.show({
                                                title: _('Error'),
                                                minProgressWidth:250,
                                                msg: response.m_pErrorDescription,
                                                buttons: Ext.MessageBox.OK,
                                                multiline: false
                                            });
                                        }
                                    }
                                });

                                msg.m_pPartnerID = detailsForm.record.get(CmFinoFIX.message.JSSMSPartner.Entries.ID._name);
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            }
            ],
            items: [ detailsForm ]
        },
        {
            anchor : "100%, -200",
            layout: "fit",
            items: [ smscGrid ]
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
        smscGrid.setPartnerRecord(null);
    });

    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        smscGrid.setPartnerRecord(record);
    });
    smscGrid.on("addsmsc",function(){
        if(!detailsForm.record){
            Ext.MessageBox.alert(_("Alert"), _("No Partner selected!"));
        }
        else{
            var record = new smscGrid.store.recordType();
            record.data[CmFinoFIX.message.JSSMSC.Entries.PartnerID._name] = detailsForm.record.data[CmFinoFIX.message.JSSMSPartner.Entries.ID._name];
            smscForm.setTitle(_("Add SMSC"));
            smscForm.setMode("add");
            smscForm.show();
            smscForm.setRecord(record);
            smscForm.setStore(smscGrid.store);
        }
    });
    smscGrid.action.on({
        action: function(grid, record, action, row, col) {
            if(action === 'mfino-button-edit'){
                smscForm.setTitle(_("Edit SMSC"));
                smscForm.setMode("edit");
                smscForm.show();
                smscForm.setRecord(record);
                smscForm.setStore(grid.store);
            } else if (action === 'mfino-button-View') {
                smscDetailsForm.show();
                smscDetailsForm.setRecord(record);
                smscDetailsForm.setStore(grid.store);
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
