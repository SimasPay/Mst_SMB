/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.bankAdmin = function(config){

    var bankAdminAddFormWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.BankAdminAddForm(config),
        width:420,
        height:420
    },config));

    var detailsForm = new mFino.widget.BankAdminDetails(Ext.apply({
        height :260
    }, config));

    var searchBox = new mFino.widget.BankAdminSearchForm(Ext.apply({
        height : 260
    }, config));

    var bankAdminDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.BankAdminDetails(config),
        height : 300,
        width: 800,
        mode : "close",
        title : _('Bank Admin Details')
    },config));

    var grid = new mFino.widget.BankAdminGrid(Ext.apply({
        title : _('Bank Admin Search Results'),
        height: 450
    }, config));

    grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                bankAdminDetailsWindow.show();
                bankAdminDetailsWindow.setRecord(record);
                bankAdminDetailsWindow.setStore(grid.store);
            }
        }
    });

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
            {
                anchor : "100%",
                tbar : [
                   '<b class= x-form-tbar>'+ _('Bank Admin Details') + '</b>',
                    '->',
                    {
                        iconCls: 'mfino-button-add',
                        tooltip : _('Add Bank Admin '),
                        itemId : 'bankadmin.details.add',
                        handler:  function(){
                            var record = new grid.store.recordType();
                    bankAdminAddFormWindow.setTitle("Add Bank Admin");
                    bankAdminAddFormWindow.setMode("add");
                    bankAdminAddFormWindow.show();
                    bankAdminAddFormWindow.form.find("itemId","bankRole")[0].enable();
                            bankAdminAddFormWindow.setRecord(record);
                            bankAdminAddFormWindow.setStore(grid.store);
                        }
                    },
                    {
                        iconCls: 'mfino-button-edit',
                        tooltip : _('Edit Bank Admin'),
                        itemId : 'bankadmin.details.edit',
                        handler : function(){
                            if(!(detailsForm.record)){
                                Ext.MessageBox.alert(_("Alert"), _("No Bank Admin selected!"));
                            }else{
                        bankAdminAddFormWindow.setTitle("Edit Bank Admin");
                        bankAdminAddFormWindow.setMode("edit");
                                bankAdminAddFormWindow.show();
                        bankAdminAddFormWindow.form.find("itemId","bankRole")[0].disable();
                                bankAdminAddFormWindow.form.find('itemId','bankadmin.form.username')[0].disable();
                                bankAdminAddFormWindow.setRecord(detailsForm.record);
                                bankAdminAddFormWindow.setStore(detailsForm.store);
                            }
                        }
                    },
                    {
                        iconCls: 'mfino-button-key',
                        itemId : 'user.details.resetpassword',
                        tooltip : _('Reset Password'),
                        handler : function(){
                            if(!(detailsForm.record)){
                                Ext.MessageBox.alert(_("Alert"), _("No User selected!"));
                            }else{
                                Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Reset Password?"),
                                function(btn){
                                    if(btn !== "yes"){
                                        return;
                                    }
                                    var msg = new CmFinoFIX.message.JSResetPassword();
                                    var params = mFino.util.showResponse.getDisplayParam();
                                    Ext.apply(params, {
                                        success :  function(response){
                                        	if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                                                Ext.ux.Toast.msg(_('Success'), _("Password has been reset"));
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

                                    msg.m_pUserID = detailsForm.record.get(CmFinoFIX.message.JSBankAdmin.Entries.UserID._name);
                                    mFino.util.fix.send(msg, params);
                                }, this);
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
        if(values.RoleSearch === "undefined"){
            values.RoleSearch =null;
        }
        if(values.StatusSearch === "undefined"){
            values.StatusSearch =null;
        }
        if(values.RestrictionsSearch === "undefined"){
            values.RestrictionsSearch =null;
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
        layout: "border",
        broder: false,
        width : 1020,
        items: [
            {
                region: 'west',
                width : 250,
                layout : "anchor",
                items:[ searchBox ]
            },
            {
                region: 'center',
                layout : "fit",
                items: [ panelCenter ]
            },
            {
                region: 'south',
                height:440,
                layout : "fit",
                items: [ grid ]
            }]
    });

    return panel;
};
