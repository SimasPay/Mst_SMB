/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.merchant = function(config){
    
    var convertSubscriberForm = new mFino.widget.ConvertSubscriberForm(config);
    var convertSubscriberStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSubscriberMDN);
    convertSubscriberForm.setStore(convertSubscriberStore);
    var convertMerchantStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMerchant);
    var mgridEditForm = new mFino.widget.FormWindowForMerchant(Ext.apply({
        form : new mFino.widget.MerchantForm(config),
        height : 572,
        width: 900
    },config));

    var checkBalanceWindow = new mFino.widget.CheckBalance(Ext.apply({
        layout: "fit",
        height: 175
    }, config));

    var detailsForm = new mFino.widget.MerchantDetails(Ext.apply({
        height : 275
    }, config));

    var dctForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.MerchantDCTDetails(config),
        height : 200,
        width : 500,
        mode : "close"
    }, config));

    var Recharge = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.Recharge(config),
        title : _("Recharge"),
        height : 250,
        width:400,
        mode:"recharge"
    },config));
    var Transfer = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.Transfer(config),
        title : _("Transfer"),
        height : 220,
        width:400,
        mode:"transfer"
    },config));
    var BulkTransfer = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.BulkTransfer(config),
        title : _("Payment File"),
        height : 400,
        width:500,
        mode:"bulk"
    },config));
    
    var searchBox = new mFino.widget.MerchantSearchForm(Ext.apply({
        height : 290
    }, config));

    var listBox = new mFino.widget.MerchantList(Ext.apply({
        }, config));

    var treeBox = new mFino.widget.MerchantTree(Ext.apply({
        height: 672
    }, config));

    var treeSorter = new Ext.tree.TreeSorter(treeBox, {
        dir: "asc"
    });

    var tabpanl = new Ext.TabPanel({
        activeTab:0,
        items:[
        {
            title: _('Search Merchant'),
            layout : "anchor",
            items:  [ searchBox, listBox ],
            listeners: {
                activate: function(t) {
                // TODO :: Here set the first.
                }
            }
        },
        {
            title: _('Distribution Chain'),
            layout : "fit",
            iconCls: 'mfino-button-treeview',
            items:  [ treeBox ],
            listeners: {
                activate: function(t) {
                    listBox.setTitle(_("Merchant"));
                    treeBox.loadThisMerchant(listBox.getCurrentlySelectedMerchantId());
                }
            }
        }
        ]
    });

    //this displays the data node on the right hand side
    treeBox.on("nodeClickEvent", function(id, text) {
        // here get the record with this id.
        searchBox.setMerchantUserName(text);
        listBox.store.baseParams = {};
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.IDSearch._name] = id;
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.SelfAndDownlineSearch._name] = false;

        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        listBox.store.load(listBox.store.lastOptions);
    });

    var tabPanel = new mFino.widget.MerchantTab(config);
    tabPanel.checkEnabledItems();

    var firstName = CmFinoFIX.message.JSMerchant.Entries.FirstName._name;
    var lastName = CmFinoFIX.message.JSMerchant.Entries.LastName._name;
    var userName = CmFinoFIX.message.JSMerchant.Entries.Username._name;

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 181,
            autoScroll : true,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Merchant Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-refresh',
                tooltip : _('Refresh Merchant'),
                handler : function(){
                    if(detailsForm.record) {
                        listBox.store.lastOptions.params[CmFinoFIX.message.JSMerchant.IDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSMerchant.Entries.ID._name);
                        listBox.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
                        listBox.store.load(listBox.store.lastOptions);
                    } else {
                        Ext.ux.Toast.msg(_('Error'), _("No Merchant is Selected"));
                    }
                }
            },
            {
                iconCls: 'mfino-button-user-add',
                tooltip : _('Add Merchant'),
                itemId: 'mer.details.add',
                handler:  function(){
                    convertSubscriberForm = new mFino.widget.ConvertSubscriberForm(config);
                    convertSubscriberForm.setStore(convertSubscriberStore);
                    convertSubscriberForm.show();
                    mgridEditForm.form.find('itemId','mer.form.username')[0].enable();
                    mgridEditForm.form.find('itemId','mer.form.groupID')[0].enable();
                    mgridEditForm.form.find('itemId','mer.form.mdn')[0].disable();
                    mgridEditForm.form.find('itemId','mer.form.regionname')[0].disable();
                    mgridEditForm.form.find('itemId','mer.form.partnerType')[0].enable();                
                }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit Merchant'),
                itemId: 'mer.details.edit',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        Ext.getCmp("merchantGroupID").isInvalid_mFino = false; 
                        mgridEditForm.reset("tabpanelmerchant");
                        mgridEditForm.setTitle( _("Edit Merchant ") + detailsForm.record.data[userName]);
                        mgridEditForm.setMode("edit");
                        mgridEditForm.show(); // we should call this method first b'coz it is enabling all textfileds, numberfields etc..
                        mgridEditForm.form.find('itemId','mer.form.mdnRangeTab')[0].enable();
                        mgridEditForm.form.find('itemId','mer.form.username')[0].disable();
                        mgridEditForm.form.find('itemId','mer.form.groupID')[0].disable();
                        mgridEditForm.form.find('itemId','mer.form.mdn')[0].enable();
                        mgridEditForm.form.find('itemId','mer.form.sourceIp')[0].enable();
                        mgridEditForm.form.find('itemId','mer.form.regionname')[0].disable();
                        mgridEditForm.form.find('itemId','mer.form.partnerType')[0].disable();
                        detailsForm.record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.DoGeneratePin._name, false);
                        var status=detailsForm.record.get(CmFinoFIX.message.JSMerchant.Entries.SubscriberStatus._name);
                        mgridEditForm.form.onStatusDropdown(status);
                        mgridEditForm.form.disableNotPermittedItems();
                        if(status == CmFinoFIX.SubscriberStatus.Initialized || status == CmFinoFIX.SubscriberStatus.Active)
                        {
                            var merchantId = detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                            var message= new CmFinoFIX.message.JSMerchantCommission();
                            message.m_pID = merchantId;
                            message.m_pStatus = CmFinoFIX.SubscriberStatus.Active;   // status check is done above
                            var params = mFino.util.showResponse.getDisplayParam();
                            params.myForm = this;
                            mFino.util.fix.send(message, params);
                            mgridEditForm.form.setLOPPermission(false);
                            mgridEditForm.form.find('itemId','mer.form.regionname')[0].disable();
                            Ext.apply(params, {
                                success :  function(response){
                                	if(response.m_pAllowedForLOP){
                                        mgridEditForm.form.setLOPPermission(true);
                                        mgridEditForm.form.find('itemId','mer.form.regionname')[0].enable();
                                    }
                                }
                            });
                            if(detailsForm.record.data['ParentName']){
                                mgridEditForm.form.find('itemId','mer.form.DCT')[0].disable();
                            }else if(detailsForm.record.data['DistributionChainTemplateID']){
                                mgridEditForm.form.find('itemId','mer.form.parentId')[0].disable();
                                if(mFino.auth.isEnabledItem('mer.form.DCT')){
                                    mgridEditForm.form.find('itemId','mer.form.DCT')[0].enable();
                                }
                            }
                        }
                        mgridEditForm.setRecord(detailsForm.record);
                        mgridEditForm.setStore(detailsForm.store);
                    }
                }
            },
            {
                iconCls: 'mfino-button-currency',
                tooltip : _('Check Balance for Merchant'),
                itemId: 'mer.details.checkBalance',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        var message= new CmFinoFIX.message.JSCheckBalance();
                        message.m_pMerchantID = detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                        message.m_pMDN = detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name];
                        var params = mFino.util.showResponse.getDisplayParam();
                        params.myForm = this;
                        params.record=detailsForm.record;
                        mFino.util.fix.send(message, params);
                        Ext.apply(params, {
                            success :  function(response){
                                checkBalanceWindow.setTitle(_("Balance for ") + detailsForm.record.data[firstName] + " " + detailsForm.record.data[lastName] + "(" + detailsForm.record.data[userName] + ")");
                                checkBalanceWindow.buttons[0].enable();
                                checkBalanceWindow.show();
                                if(!mFino.auth.isEnabledItem('mer.balance.emptyButton')){
                                    checkBalanceWindow.buttons[0].disable();
                                }
                                checkBalanceWindow.setRecord(response, params.record);
                            }
                        });
                    }
                }
            },
            {
                iconCls: 'mfino-button-generatelop',
                tooltip : _('Generate LOP for Merchant'),
                itemId: 'mer.details.generatelop',
                handler : function(){
                    // Need to check whether LOP add permission is there or not.
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    } else {
                        var record = detailsForm.record;
                        var merchantId = record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                        var merchantParentId = record.data[CmFinoFIX.message.JSMerchant.Entries.ParentID._name];
                        var status = record.data[CmFinoFIX.message.JSMerchant.Entries.SubscriberStatus._name];
                        if(merchantParentId===null || merchantParentId==="" || merchantId===merchantParentId
                            || status!==CmFinoFIX.SubscriberStatus.Active){
                            Ext.Msg.show({
                                title: _('Error'),
                                minProgressWidth:250,
                                msg:_(record.data[CmFinoFIX.message.JSMerchant.Entries.Username._name] + " is not authorized to generate LOP"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }else {
                            var message= new CmFinoFIX.message.JSMerchantCommission();
                            message.m_pID = merchantId;
                            message.m_pStatus = status;
                            var params = mFino.util.showResponse.getDisplayParam();
                            params.myForm = this;
                            mFino.util.fix.send(message, params);
                            Ext.apply(params, {
                                success :  function(response){
                                	if(response.m_pAllowedForLOP){
                                		var maxWeeklyPurchaseAmount = response.m_pMaxWeeklyPurchaseAmount;
                                		var maxAmountPerTransaction = response.m_pMaxAmountPerTransaction;
                                        var currentWeeklyPurchaseAmount = record.data[CmFinoFIX.message.JSMerchant.Entries.CurrentWeeklyPurchaseAmount._name];
                                        var availableforLOP = maxWeeklyPurchaseAmount - currentWeeklyPurchaseAmount;
                                        var lopCommission = response.m_pCommission;
                                        var mdn = record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name];
                                        var groupId = record.data[CmFinoFIX.message.JSMerchant.Entries.GroupIDDisplayText._name];
                                        var LOPAdd = new mFino.widget.FormWindowLOP(Ext.apply({
                                            form : new mFino.widget.LOPAddForm(config),
                                            height : 450,
                                            width:400,
                                            mode:"lop"
                                        },config));
                                        LOPAdd.buttons[1].enable();
                                        LOPAdd.setTitle(_("Generate LOP for ") + detailsForm.record.data[firstName] + " " + detailsForm.record.data[lastName] + "(" + detailsForm.record.data[userName] + ")" );
                                        LOPAdd.show();
                                        LOPAdd.form.setLimits(mdn,maxWeeklyPurchaseAmount,currentWeeklyPurchaseAmount,availableforLOP,lopCommission,groupId,maxAmountPerTransaction);
                                    } else{
                                        Ext.Msg.show({
                                            title: _('Error'),
                                            minProgressWidth:250,
                                            msg: _(record.data[CmFinoFIX.message.JSMerchant.Entries.Username._name] + " " + response.m_pErrorDescription),
                                            buttons: Ext.MessageBox.OK,
                                            multiline: false
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            },
            {
                iconCls: 'mfino-button-generatebulklop',
                tooltip : _('Generate BulkLOP for Merchant'),
                itemId: 'mer.details.generatebulklop',
                handler : function(){
                    // Need to check whether LOP add permission is there or not.
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    } else {
                        var record = detailsForm.record;
                        var merchantId = record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                        var merchantParentId = record.data[CmFinoFIX.message.JSMerchant.Entries.ParentID._name];
                        var status = record.data[CmFinoFIX.message.JSMerchant.Entries.SubscriberStatus._name];
                        if(merchantParentId===null || merchantParentId==="" || merchantId===merchantParentId
                            || status!==CmFinoFIX.SubscriberStatus.Active){
                            Ext.Msg.show({
                                title: _('Error'),
                                minProgressWidth:250,
                                msg:_(record.data[CmFinoFIX.message.JSMerchant.Entries.Username._name] + " is not authorized to generate LOP"),
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }else {
                            var message= new CmFinoFIX.message.JSMerchantCommission();
                            message.m_pID = merchantId;
                            message.m_pStatus = status;
                            var params = mFino.util.showResponse.getDisplayParam();
                            params.myForm = this;
                            mFino.util.fix.send(message, params);
                            Ext.apply(params, {
                                success :  function(response){
                                	if(response.m_pAllowedForLOP){
                                		var maxWeeklyPurchaseAmount = response.m_pMaxWeeklyPurchaseAmount;
                                		var maxAmountPerTransaction = response.m_pMaxAmountPerTransaction;
                                        var currentWeeklyPurchaseAmount = record.data[CmFinoFIX.message.JSMerchant.Entries.CurrentWeeklyPurchaseAmount._name];
                                        var availableforLOP = maxWeeklyPurchaseAmount - currentWeeklyPurchaseAmount;
                                        var lopCommission = response.m_pCommission;
                                        var mdn = record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name];
                                        var groupId = record.data[CmFinoFIX.message.JSMerchant.Entries.GroupIDDisplayText._name];
                                        var merchantid = record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                                        var LOPAdd = new mFino.widget.FormWindowLOP(Ext.apply({
                                            form : new mFino.widget.BulkLOPAddForm(config),
                                            height : 650,
                                            width:400,
                                            mode:"lop"
                                        },config));
                                        LOPAdd.buttons[1].enable();
                                        LOPAdd.setTitle(_("Generate Bulk LOP for ") + detailsForm.record.data[firstName] + " " + detailsForm.record.data[lastName] + "(" + detailsForm.record.data[userName] + ")" );
                                        LOPAdd.show();
                                        LOPAdd.form.setLimits(mdn,maxWeeklyPurchaseAmount,currentWeeklyPurchaseAmount,availableforLOP,lopCommission,groupId,maxAmountPerTransaction, merchantid);
                                    } else{
                                        Ext.Msg.show({
                                            title: _('Error'),
                                            minProgressWidth:250,
                                            msg: _(record.data[CmFinoFIX.message.JSMerchant.Entries.Username._name] + " " + response.m_pErrorDescription),
                                            buttons: Ext.MessageBox.OK,
                                            multiline: false
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            },
            {
                iconCls: 'mfino-button-distribute',
                tooltip : _('Distribute Airtime Stock for Merchant'),
                itemId: 'mer.details.distribute',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        Transfer.form.setMerchantID(detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name], detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name]);
                        Transfer.buttons[3].enable();
                        Transfer.show();
                    }
                }
            },
            {
                iconCls: 'mfino-button-recharge',
                tooltip : _('Recharge Merchant'),
                itemId: 'mer.details.recharge',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        Recharge.form.setMDN(detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name]);
                        Recharge.buttons[0].enable();
                        Recharge.show();
                    }
                }
            },
            {
                iconCls: 'mfino-button-bulkupload',
                tooltip : _('Bulk Upload for Merchant'),
                itemId: 'mer.details.bulkupload',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        BulkTransfer.setMerchantFields(detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name],detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name]);
                        BulkTransfer.show();
                        BulkTransfer.form.getForm().reset();
                        Ext.get('form-file-file').dom.value =null;
                    }
                }
            },
            {
                iconCls: 'mfino-button-distributionchain',
                tooltip : _('Distribution Chain for Merchant'),
                itemId: 'mer.details.dc',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        var popup = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
                            title:  _("Distribution Chain for ") + (detailsForm.record.data[firstName] + " " + detailsForm.record.data[lastName] + "(" +detailsForm.record.data[userName] + ")"),
                            grid : new mFino.widget.DistributionChainWindow(config),
                            height : 466,
                            width: 600
                        },config));
                        popup.grid.store.lastOptions = {
                            params : {
                                start : 0,
                                limit : CmFinoFIX.PageSize.Default
                            }
                        };
                        popup.grid.store.baseParams[CmFinoFIX.message.JSMerchant.ParentIDSearch._name] = detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                        popup.grid.store.load(popup.grid.store.lastOptions);
                        popup.show();
                    }
                }
            },
            {
                iconCls: 'mfino-button-View',
                tooltip : _('DCT Details for Merchant'),
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        var merchantId = detailsForm.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
                        var message= new CmFinoFIX.message.JSMerchantDCT();
                        message.m_pID = merchantId;
                        var params = mFino.util.showResponse.getDisplayParam();
                        params.myForm = this;
                        mFino.util.fix.send(message, params);
                        Ext.apply(params, {
                            success :  function(response){
                            	var dctName = response.m_pMerchantDistributionChainName;
                            	var level = response.m_pDistributionLevel;
                            	var permissions = response.m_pDistributionPermissionsText;
                                dctForm.form.setValues(dctName,level,permissions);
                                dctForm.setTitle(_("DCT Details for ") + detailsForm.record.data[firstName] + " " + detailsForm.record.data[lastName] + "(" +detailsForm.record.data[userName] + ")");
                                dctForm.show();
                            }
                        });
                    }
                }
            },
            {
                iconCls: 'mfino-button-key',
                tooltip : _('Reset Pin for Merchant'),
                itemId: 'mer.details.resetpin',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Merchant selected!"));
                    }else{
                        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Reset Pin?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg = mFino.util.fix.getMerchantResetPinFromRecord(detailsForm.record);
                                var params = mFino.util.showResponse.getDisplayParam();
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            }
            ],
            items: [ detailsForm ]
        },
        {
            layout : "fit",
            anchor : "100%, -181",
            items: [ tabPanel ]
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
        if(values.merchantstatus === "undefined" ) {
            values.merchantstatus =null;
        }
        if(values.merchantrestrictions === "undefined") {
            values.merchantrestrictions =null;
        }
        detailsForm.getForm().reset();
        detailsForm.record = null;
        if(detailsForm.record) {
            detailsForm.record.data['ID'] = null;
        }
        listBox.store.baseParams = values;
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.StartDateSearch._name] = values.startDate;
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.EndDateSearch._name] = values.endDate;
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.ExactGroupIDSearch._name] = values[CmFinoFIX.message.JSSAPGroupID.Entries.GroupID._name];
        
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.StatusSearch._name] = values.merchantstatus;
        listBox.store.baseParams[CmFinoFIX.message.JSMerchant.RestrictionsSearch._name] = values.merchantrestrictions;

        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(listBox.store.lastOptions.params, values);
        listBox.store.load(listBox.store.lastOptions);
        listBox.setTitle(_("Merchant Search Results"));
        tabPanel.setRecord(null); //clear the tab panels
    });

    convertSubscriberStore.on("load", function(){

        var record = convertSubscriberStore.getAt(0);
        var mrecord = new convertMerchantStore.recordType();

        Ext.apply(mrecord.data, record.data);

        mrecord.data['ID'] = null;
        mrecord.data['NotificationMethod'] =CmFinoFIX.NotificationMethod.SMS;
        if(detailsForm.record) {
            mrecord.data['ParentID'] = detailsForm.record.data['ID'];
            mrecord.data['ParentName']=detailsForm.record.data['Username'];
            mrecord.data['RegionName']=detailsForm.record.data['RegionName'];
            mrecord.data['RegionID']=detailsForm.record.data['RegionID'];
        }

        convertSubscriberForm.hide();
       
        mgridEditForm.setTitle(_("Add Merchant"));
        mgridEditForm.setMode("add");
        mgridEditForm.form.enablePermittedItems();
        mgridEditForm.reset("tabpanelmerchant");
        mgridEditForm.show();
        
        var status=mrecord.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name);
        mgridEditForm.form.onStatusDropdown(status);
        mgridEditForm.setRecord(mrecord);
        mgridEditForm.setStore(listBox.store);

        //  disabling DCT if parentID is available
        if(mrecord.data['ParentName']){
            mgridEditForm.form.find('itemId','mer.form.DCT')[0].disable();            
        }
        // Clearing tab panel contents.
        mgridEditForm.resetAll();
        mgridEditForm.form.find('itemId','mer.form.mdnRangeTab')[0].disable();
        convertSubscriberStore.remove(record);
        var groupIdField= mgridEditForm.form.find('itemId','mer.form.groupID')[0];
        //if merchant login we are not allowing him to enter the parentid and group id. parentid is set to logined username in the backend.
        if(mFino.auth.isMerchant()){
            mrecord.data['ParentID'] = null;
            mrecord.data['ParentName']= null;
            mgridEditForm.form.find('itemId','mer.form.parentId')[0].setValue("");
            groupIdField.setValue("");
            groupIdField.disable();
            mgridEditForm.form.find('itemId','mer.form.parentId')[0].disable();
        }else{
            mFino.util.fix.checkGroupIDParent(groupIdField, mrecord.data['ParentID']);            
        }
        mgridEditForm.form.find('itemId','mer.form.username')[0].enable();
        mgridEditForm.form.find('itemId','mer.form.groupID')[0].enable();
        mgridEditForm.form.find('itemId','mer.form.mdn')[0].disable();
        mgridEditForm.form.find('itemId','mer.form.sourceIp')[0].disable();
    });

    listBox.on("defaultSearch", function() {
        searchBox.defaultSearchHandler();
    });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        tabPanel.setRecord(record);
    });

    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            minSize : 250,
            maxSize : 250,
            width : 250,
            layout : "fit",
            items:[ tabpanl ]
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
