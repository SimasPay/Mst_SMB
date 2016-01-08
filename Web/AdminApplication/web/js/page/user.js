/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.user = function(config){

    var userEditFormWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.UserEditForm(config),
        title : _('Edit User'),
        width:420,
        height:440,
        mode : 'edit'
    },config));

    var userAddFormWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.UserAddForm(config),
        title : _('New User'),
        width:420,
        height:420,
        mode : 'add'
    },config));

    var detailsForm = new mFino.widget.UserDetails(Ext.apply({
        height :260
    }, config));

    var searchBox = new mFino.widget.UserSearchForm(Ext.apply({
        height : 260
    }, config));

    var userDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.UserDetails(config),
        height : 300,
        width: 800,
        mode : "close",
        title : _('User Details')
    },config));

    var grid = new mFino.widget.UserGrid(Ext.apply({
        title : _('User Search Results'),
        height: 450
    }, config));

    grid.action.on({
        action:function(grid, record, action, row, col) {
            if(action === 'mfino-button-View'){
                userDetailsWindow.show();
                userDetailsWindow.setRecord(record);
                userDetailsWindow.setStore(grid.store);
            }
        }
    });

    var panelCenter = new Ext.Panel({        
        layout: 'anchor',
        items : [
            {
                anchor : "100%",
                //   height : 260,
                tbar : [
                   '<b class= x-form-tbar>'+ _('Administration - User Details') + '</b>',
                    '->',
                    {
                        iconCls: 'mfino-button-add',
                        tooltip : _('Add User'),
                        itemId : 'user.details.add',
                        handler:  function(){
                            var record = new grid.store.recordType();
                            userAddFormWindow.show();
                            userAddFormWindow.setRecord(record);
                            userAddFormWindow.setStore(grid.store);
                        }
                    },
                    {
                        iconCls: 'mfino-button-edit',
                        tooltip : _('Edit User'),
                        itemId : 'user.details.edit',
                        handler : function(){
                            if(!(detailsForm.record)){
                                Ext.MessageBox.alert(_("Alert"), _("No User selected!"));
                            }else{
                                userEditFormWindow.show();
                                userEditFormWindow.setRecord(detailsForm.record);
                                userEditFormWindow.setStore(detailsForm.store);
                                if(!mFino.auth.isSystemUser()){
                                userEditFormWindow.form.find("itemId","userEmail")[0].disable();
                                userEditFormWindow.form.find("itemId","userRole")[0].disable();
                            }
                        }
                        }
                    },
                    {
                        iconCls: 'mfino-button-key',
                        id:'user_reset_password',
                        itemId : 'user.details.resetpassword',
                        tooltip : _('Send new Password'),
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

                                    msg.m_pUserID = detailsForm.record.get(CmFinoFIX.message.JSUsers.Entries.ID._name);
                                    mFino.util.fix.send(msg, params);
                                }, this);
                            }
                        }
                    }
                    //            ,
                    //            {
                    //                iconCls: 'mfino-button-remove',
                    //                tooltip : _('Delete User'),
                    //                itemId : 'user.details.retire',
                    //                handler : function(){
                    //                    if(!(detailsForm.record)){
                    //                        Ext.MessageBox.alert(_("Alert"), _("No User selected!"));
                    //                    } else {
                    //                        Ext.MessageBox.confirm(
                    //                            _('Delete User?'),
                    //                            _('Do you want to delete ') + '<b>' +
                    //                            detailsForm.record.get(CmFinoFIX.message.JSUsers.Entries.Username._name) +
                    //                            '</b>',
                    //                            function(btn){
                    //                                if (btn == 'yes') {
                    //                                    detailsForm.form.reset();
                    //                                    detailsForm.store.remove(detailsForm.record);
                    //                                    detailsForm.store.save();
                    //                                //TODO: this is not acurate since the delete could fail on the server side
                    //                                //   this.detailsForm.record.get(CmFinoFIX.message.JSUsers.Entries.Username._name));
                    //                                }
                    //                            });
                    //                    }
                    //                }
                    //            }
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

    //    grid.on("download", function() {
    //        var values = searchBox.getForm().getValues();
    //
    //        var firstNameSearch = values[CmFinoFIX.message.JSUsers.FirstNameSearch._name];
    //        var lastNameSearch = values[CmFinoFIX.message.JSUsers.LastNameSearch._name];
    //        var restrictionsSearch = values[CmFinoFIX.message.JSUsers.RestrictionsSearch._name];
    //        var roleSearch = values[CmFinoFIX.message.JSUsers.RoleSearch._name];
    //        var statusSearch = values[CmFinoFIX.message.JSUsers.StatusSearch._name];
    //        var usernameSearch = values[CmFinoFIX.message.JSUsers.UsernameSearch._name];
    //
    //        //OPTION 1:
    //        var queryString = "dType=user";
    //
    //        if(firstNameSearch){
    //            queryString += "&"+CmFinoFIX.message.JSUsers.FirstNameSearch._name+"="+firstNameSearch;
    //        }
    //        if(lastNameSearch){
    //            queryString += "&"+CmFinoFIX.message.JSUsers.LastNameSearch._name+"="+lastNameSearch;
    //        }
    //        if(usernameSearch){
    //            queryString += "&"+CmFinoFIX.message.JSUsers.UsernameSearch._name+"="+usernameSearch;
    //        }
    //        if(restrictionsSearch){
    //            queryString += "&"+CmFinoFIX.message.JSUsers.RestrictionsSearch._name+"="+restrictionsSearch;
    //        }
    //        if(roleSearch){
    //            queryString += "&"+CmFinoFIX.message.JSUsers.RoleSearch._name+"="+roleSearch;
    //        }
    //        if(statusSearch){
    //            queryString += "&"+CmFinoFIX.message.JSUsers.StatusSearch._name+"="+statusSearch;
    //        }
    //
    //
    //        var URL = "download.htm?" + queryString;
    //
    //        window.open(URL,'mywindow','width=400,height=200');
    //    });

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
