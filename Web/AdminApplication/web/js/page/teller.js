/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.teller = function(config){
	  var gridEditForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.ServicePartnerForm(config),
	    height : 550,
	    width: 920    
    },config));
	  
	  var changePin = new mFino.widget.FormWindowLOP(Ext.apply({
	        form : new mFino.widget.ChangePin(config),
	        title : _("ChangePin"),
	        height : 200,
	        width:350,
	        mode:"changepin"
	    },config));
	 
	  
    var detailsForm = new mFino.widget.ServicePartnerDetails(config);
    
    var approveWindow = new mFino.widget.ApproveRejectPartnerWindow(config);
    
    var searchBox = new mFino.widget.ServicePartnerSearchForm(Ext.apply({
        height : 230,
        title : _("BankTeller Search")
    }, config));

    var checkBalanceWindow = new mFino.widget.CheckBalanceSubscriber(Ext.apply({
        layout: "fit",
        height: 175
    }, config));
    
    var cashout = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.CashOut(config),
        title : _("CashOut"),
        height : 150,
        width:250,
        mode:"select"
    },config));
    
    var cashin = new mFino.widget.FormWindowLOP(Ext.apply({
        form : new mFino.widget.CashIn(config),
        title : _("Cashin"),
        height : 220,
        width:400,
        mode:"transfer"
    },config));
    
    var checkSubscriberForm = new mFino.widget.ConvertSubscriberForm(config,_("Enter MDN"));
    var subscriberStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSubscriberMDN);
    subscriberStore.baseParams[CmFinoFIX.message.JSSubscriberMDN.SubscriberSearch._name] = false;
//    subscriberStore.baseParams[CmFinoFIX.message.JSSubscriberMDN.SubscriberSearch._name] = true;
    checkSubscriberForm.setStore(subscriberStore);
//    checkSubscriberForm.setTitle( _("Enter MDN"));
    var listBox = new mFino.widget.ServicePartnerList(Ext.apply({anchor : "100%, -230",title : _("BankTeller Search Result")}, config));

    //this enables serverside form validation
    listBox.store.form = gridEditForm.form;

    var tabPanel = new mFino.widget.ServicePartnerTab(Ext.apply({
        parenttab : "teller."
    }, config));
    
    tabPanel.checkEnabledItems();

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 200,
            anchor : "100%",
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('BankTeller Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-refresh',
                tooltip : _('Refresh BankTeller'),
                handler : function(){
                    if(detailsForm.record)
                    {
                    	listBox.store.lastOptions.params[CmFinoFIX.message.JSPartner.PartnerIDSearch._name] = detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.ID._name);
                    	listBox.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
                        listBox.store.load(listBox.store.lastOptions);
                    }
                    else
                    {
                        Ext.ux.Toast.msg(_('Error'), _("No BankTeller is Selected"));
                    }
                }
            },
            {

                iconCls: 'mfino-button-user-add',
                tooltip : _('Register BankTeller'),
                itemId : 'servicepartner.details.add',
                handler:  function(){
                	var record = new listBox.store.recordType();
                	gridEditForm.form.resetAll();
                	gridEditForm.setRecord(record);
                    gridEditForm.setStore(listBox.store);
                	gridEditForm.setTitle( _("Register BankTeller"));
                    gridEditForm.setMode("add");
                    gridEditForm.show();
                    gridEditForm.setEditable(false);
                    gridEditForm.form.onSpecificPartnerType(CmFinoFIX.BusinessPartnerType.BranchOffice);
                    gridEditForm.form.find('itemId','servicepartner.form.mobileno')[0].enable();             
                }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit BankTeller'),
                itemId : 'servicepartner.details.edit',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No BankTeller selected!"));
                    }else{
                        gridEditForm.setTitle( _("Edit BankTeller"));
                        gridEditForm.setMode("edit");
                        gridEditForm.form.resetAll();
                        gridEditForm.show();
                        var status=detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name);
                        gridEditForm.form.onStatusDropdown(status);
                        gridEditForm.form.onSpecificPartnerType(CmFinoFIX.BusinessPartnerType.BranchOffice);                        
//                        if(status === CmFinoFIX.SubscriberStatus.PendingRetirement||status===CmFinoFIX.SubscriberStatus.Retired){
//                        gridEditForm.form.find('itemId','servicepartner.form.status')[0].disable();
//                        }else{
//                        	gridEditForm.form.find('itemId','servicepartner.form.status')[0].enable();
//                        }
                        gridEditForm.form.find('itemId','servicepartner.form.username')[0].disable();
                        gridEditForm.form.disableNotPermittedItems();
                        gridEditForm.setRecord(detailsForm.record);
                        gridEditForm.setStore(detailsForm.store);
                    }
                }
            },
            {
                iconCls: 'mfino-button-View',
                tooltip : _('View BankTeller Details'),
                itemId : 'servicepartner.details.view',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No BankTeller selected!"));
                    }else{
                        gridEditForm.setTitle( _(" BankTeller Details"));
                        gridEditForm.setMode("close");
                        gridEditForm.form.setReadOnly(true);
                        gridEditForm.show();
                       /* gridEditForm.form.onPartnerType(CmFinoFIX.TagID.BusinessPartnerTypePartner);*/
                        gridEditForm.setRecord(detailsForm.record);
                        gridEditForm.setStore(detailsForm.store);
                    }
                }
            },
            {
                iconCls : "mfino-button-resolve",
                tooltip : _(' Approve/Reject'),
               // text : _(' Approve/Reject subscriber'),
                itemId: 'partner.approve',
                handler : function(){
            	if(!detailsForm.record){
                    Ext.MessageBox.alert(_("Alert"), _("No BankTeller selected!"));
                }else{
                	if(detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name)===CmFinoFIX.MDNStatus.Retired
                			||detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name)===CmFinoFIX.MDNStatus.PendingRetirement){
                		Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("Invalid BankTeller Status"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.UpgradeState._name)===CmFinoFIX.UpgradeState.Upgradable|| 
                    		detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.UpgradeState._name)===CmFinoFIX.UpgradeState.Rejected){
                        approveWindow.show();
                        approveWindow.setRecord(detailsForm.record);
                    }else if (detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.UpgradeState._name)===CmFinoFIX.UpgradeState.Approved ) {
                        Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("BankTeller already Approved"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else{
                        Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("Not Allowed for this BankTeller"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }
                }
                }
            },
            {
                iconCls: 'mfino-button-resetotp',
                tooltip : _('Send new OTP'),
                itemId : 'servicepartner.reset.otp',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No BankTeller selected!"));
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.UpgradeState._name)!=CmFinoFIX.UpgradeState.Approved){
                    	 Ext.MessageBox.alert(_("Alert"), _("Send New OTP not allowed as BankTeller is not approved"));                    	
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name)!=CmFinoFIX.MDNStatus.Initialized){
                    	 Ext.MessageBox.alert(_("Alert"), _("Send New OTP is allowed only for Approved and Initialized BankTellers"));                    	
                    }else{
                    	var mdn=detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.MDN._name);
                    	var name=detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.TradeName._name);
                    
                    Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to send New OTP for "+name+","+mdn+"?"),
                        function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg= new CmFinoFIX.message.JSResetOTP();
                                msg.m_pMDNID = detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.MDNID._name);
                                var params = mFino.util.showResponse.getDisplayParam();
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            },
            {
                iconCls: 'mfino-button-distribute',
                tooltip : _('CashIn Subscriber Account'),
                itemId: 'subscriber.cashin',
                handler : function(){
                	checkSubscriberForm.show();
                }
            },
            {
                iconCls: 'mfino-button-recharge',
                tooltip : _('CashOut'),
                itemId : 'subscriber.cashout',
                handler : function(){
                	cashout.enable();
                	cashout.form.reset();
                	cashout.show();
                	cashout.setFormWindow();
                }
            },
            {
                iconCls: 'mfino-button-key',
                tooltip : _('Reset PIN'),
//                id : 'partner.resetpin',
                itemId : 'partner.resetpin',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No BankTeller selected!"));
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name)!=CmFinoFIX.MDNStatus.Active){
                    	Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("Reset Pin not allowed ,Invalid Status"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else{
                    	var mdn=detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.MDN._name);
                    	var name=detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.TradeName._name);
                       
                        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Reset Pin for "+mdn+","+name+"?"),
                            function(btn){
                                if(btn !== "yes"){
                                    return;
                                }
                                var msg= new CmFinoFIX.message.JSResetPin();
                                msg.m_pSourceMDN = detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.MDN._name);
                                var params = mFino.util.showResponse.getDisplayParam();
                                mFino.util.fix.send(msg, params);
                            }, this);
                    }
                }
            },
            {
                iconCls: 'mfino-button-key',
                tooltip : _('Change PIN'),
                id : 'partner.changepin',
                itemId : 'partner.changepin',
                handler : function(){
                    if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No BankTeller selected!"));
                    }else if(detailsForm.record.get(CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name)!=CmFinoFIX.MDNStatus.Active){
                    	Ext.Msg.show({
                            title: _('Alert !'),
                            minProgressWidth:250,
                            msg: _("Change Pin not allowed ,Invalid Status"),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }else{
                    	changePin.setRecord(detailsForm.record);
                    	changePin.show();
                    }
                }
            }
            ],
            items: [ detailsForm ]
        },
        {
            anchor : "100%, -200",
            layout: "fit",
            items: [ tabPanel ]
        }
        ]
    });

    var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){       
        if(Ext.getCmp('partner.resetpin')){
            Ext.getCmp('partner.resetpin').hide();
        }
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
        listBox.store.baseParams[CmFinoFIX.message.JSPartner.StartDateSearch._name] = values.startDate;
        listBox.store.baseParams[CmFinoFIX.message.JSPartner.EndDateSearch._name] = values.endDate;
        listBox.store.baseParams[CmFinoFIX.message.JSPartner.PartnerTypeSearch._name] = CmFinoFIX.BusinessPartnerType.BranchOffice;
        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(listBox.store.lastOptions.params, values);
        listBox.store.load(listBox.store.lastOptions);
        //#commented# 
		tabPanel.setPartnerRecord(null); //clear the tab panels
    });

    listBox.on("download", function() {
        var queryString;
        var values = searchBox.getForm().getValues();
        
        var partnerIdSearch= values.PartnerIDSearch;
        var partnerCodeSearch = values.PartnerCodeSearch;
        var tradeNameSearch = values.TradeNameSearch;
        var authorizedEmailSearch = values.AuthorizedEmailSearch;
        var startDateSearch = getUTCdate(values.startDate);
        var endDateSearch = getUTCdate(values.endDate);
        var serviceID = values.ServiceIDSearch;
        var status = values.UpgradeStateSearch;
        queryString = "dType=servicePartner";
       
        if(partnerIdSearch){
        	queryString += "&"+CmFinoFIX.message.JSPartner.PartnerIDSearch._name+"="+partnerIdSearch;
        }
        if(partnerCodeSearch){
        	queryString += "&"+CmFinoFIX.message.JSPartner.PartnerCodeSearch._name+"="+partnerCodeSearch;
        }
        if(tradeNameSearch){
        	queryString += "&"+CmFinoFIX.message.JSPartner.TradeNameSearch._name+"="+tradeNameSearch;
        }
        if(authorizedEmailSearch){
        	queryString += "&"+CmFinoFIX.message.JSPartner.AuthorizedEmailSearch._name+"="+authorizedEmailSearch;
        }
        if(startDateSearch){
        	queryString += "&"+CmFinoFIX.message.JSPartner.StartDateSearch._name+"="+startDateSearch;
        }
        if(endDateSearch){
        	queryString += "&"+CmFinoFIX.message.JSPartner.EndDateSearch._name+"="+endDateSearch;
        }
        if(serviceID){
        	queryString += "&"+CmFinoFIX.message.JSPartner.ServiceIDSearch._name+"="+serviceID;
        }
        if(status){
        	queryString += "&"+CmFinoFIX.message.JSPartner.UpgradeStateSearch._name+"="+status;
        }
        queryString += "&"+CmFinoFIX.message.JSPartner.PartnerTypeSearch._name+"="+CmFinoFIX.BusinessPartnerType.BranchOffice;
        
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });
    
    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });
    
    listBox.on("clearSelected", function() {
    	detailsForm.getForm().reset();
        detailsForm.record = null;
        tabPanel.setPartnerRecord(null);        
   });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
    	detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        tabPanel.setPartnerRecord(record);
        if(Ext.getCmp('partner.resetpin')){
        	if((record.data[CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name]==CmFinoFIX.BusinessPartnerType.BranchOffice
        		|| record.data[CmFinoFIX.message.JSPartner.Entries.BusinessPartnerType._name]==CmFinoFIX.BusinessPartnerType.CorporateUser) 
                &&record.data[CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name]==CmFinoFIX.MDNStatus.Active){
                Ext.getCmp('partner.resetpin').show();
            }else{
            	Ext.getCmp('partner.resetpin').hide();
            	}
            }
    });
    
    subscriberStore.on("load", function(){
        var record = subscriberStore.getAt(0);
        if(record==null){
        	 Ext.ux.Toast.msg(_("Error"), "No Subscriber Found ");
        }else if((record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberType._name]===CmFinoFIX.SubscriberType.Subscriber
        		||record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.PartnerType._name]===CmFinoFIX.TagID.BusinessPartnerTypeAgent
        		||record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.PartnerType._name]===CmFinoFIX.BusinessPartnerTypePartner.Merchant)
        		&&(record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name]!=CmFinoFIX.MDNStatus.NotRegistered
        				&&record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name]!=CmFinoFIX.MDNStatus.Retired)){
        checkSubscriberForm.hide();
        subscriberStore.remove(record);
        checkSubscriberForm.form.getForm().reset();
        cashin.enable();
        cashin.form.setSubscriberDetails(record);
      	cashin.show();
        }else{
        	 subscriberStore.remove(record);
//             checkSubscriberForm.form.getForm().reset();
        	 Ext.ux.Toast.msg(_("Error"), "Not a valid Subscriber or Cashin not allowed to Subscriber");
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
