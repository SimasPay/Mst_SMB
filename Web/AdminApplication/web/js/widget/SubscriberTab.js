/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberTab = function(config){
    mFino.widget.SubscriberTab.superclass.constructor.call(this, config);
};

Ext.extend(mFino.widget.SubscriberTab, Ext.TabPanel, {
    initComponent : function () {
        var config = this.initialConfig;

        this.pocketAddFormWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.PocketAddForm(config),
            title : _("Add Pocket"),
            mode : "add",
            width : 400,
            height : 350
        },config));
        this.pocketEditFormWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.PocketEditForm(config),
            title : _("Edit Pocket Detail"),
            mode : "edit",
            width : 400,
            height : 400
        },config));
        this.pocketGrid = new mFino.widget.PocketGrid(Ext.apply({
            layout: "fit",            
            height: 470,
            parenttab : ""
        }, config));
        this.pocketGrid.store.form = this.pocketEditFormWindow.form;
        
        
        
        this.pocketDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.PocketDetails({}),
            title : _("Pocket Details"),
            mode : "close",
            width:500,
            height:550
        },config));

//        this.person2PersonGrid = new mFino.widget.Person2PersonGrid(Ext.apply({
//            layout: "fit",
//            height: 470
//        }, config));
        this.transactionsGrid = new mFino.widget.SubscriberTransactionsGrid(Ext.apply({
            layout: "fit",
            height: 470
        }, config));
        
        this.pocketTransactionsviewform = new mFino.widget.BookingDatedBalanceViewGridWindow(Ext.apply({
            grid : new mFino.widget.PocketTransactionsViewGrid(config),
            title :_( 'Pocket Transaction Details')
        },config));
        
        this.pocketGrid.action.on({
            action: this.editOrViewPocket.createDelegate(this)
        });

        this.pocketGrid.on({
            addclick: this.addPocket.createDelegate(this)
        });
        
        this.checkBalanceWindow = new mFino.widget.CheckBalanceSubscriber(Ext.apply({
            layout: "fit",
            height: 175
        }, config));

//        this.person2PersonGrid.on({
//            addclick: this.addPerson2Person.createDelegate(this)
//        },
//        this.person2PersonGrid.action.on({
//            action: this.editOrDeletePeer.createDelegate(this)
//        })
//        );

        this.activeTab = 0;
        this.items = [
        {
            title: _('Pocket'),
            layout : "fit",
            itemId: "sub.pocket.tab",
            items:  this.pocketGrid
        },
        {
            title: _('Transactions'),
            layout : "fit",
            itemId: "sub.transactions.tab",
            items:  this.transactionsGrid
        }
//        ,
//        {
//            title: _('Person To Person'),
//            layout : "fit",
//            itemId: "sub.p2p.tab",
//            items:  this.person2PersonGrid
//        }
        ];

        mFino.widget.SubscriberTab.superclass.initComponent.call(this);       
    },

    setMDNRecord : function(record){
        if(!record){
            delete this.record;
            if(this.pocketGrid.store){
                this.pocketGrid.store.removeAll();
                this.pocketGrid.store.removed = [];
                this.pocketGrid.getBottomToolbar().hide();
            }
//            if(this.person2PersonGrid.store) {
//                this.person2PersonGrid.store.removeAll();
//                this.person2PersonGrid.store.removed = [];
//                this.person2PersonGrid.getBottomToolbar().hide();
//            }
            if(this.transactionsGrid.store) {
                this.transactionsGrid.store.removeAll();
                this.transactionsGrid.store.removed = [];
                this.transactionsGrid.getBottomToolbar().hide();
            }
            return;
        }
        this.transactionsGrid.setRecord(record);
        this.record = record;

        this.pocketGrid.getBottomToolbar().show();
//        this.person2PersonGrid.getBottomToolbar().show();
        this.transactionsGrid.getBottomToolbar().show();

        var mdnID = record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
        
        // Ticket #222: Getting the MDN.
        var mdn = record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
        var subscriberID = record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
        var subscriberMDNID = record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];

        if(mdnID){
            if(this.pocketGrid.store){
                this.pocketGrid.store.baseParams[CmFinoFIX.message.JSPocket.MDNIDSearch._name] = mdnID;
                this.pocketGrid.store.baseParams[CmFinoFIX.message.JSPocket.limit._name]=CmFinoFIX.PageSize.Default;
                this.pocketGrid.store.load();
            }

//            if(this.person2PersonGrid.store) {
//                this.person2PersonGrid.store.baseParams[CmFinoFIX.message.JSPerson2Person.SubscriberIDSearch._name] = subscriberID;
//                this.person2PersonGrid.store.baseParams[CmFinoFIX.message.JSPerson2Person.limit._name]=CmFinoFIX.PageSize.Default;
//                this.person2PersonGrid.store.load();
//            }
//            this.person2PersonGrid.subID = subscriberID;

            if(this.transactionsGrid.store){
                this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestMDNAndID._name] = mdn + "," + subscriberMDNID;
                this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name] = true;
                this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name]=CmFinoFIX.TransferState.Complete;
                this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.limit._name]=CmFinoFIX.PageSize.Default;
                this.transactionsGrid.store.load();
            }
        }
    },

    editOrViewPocket : function(grid, record, action, row, col) {
        record.data[CmFinoFIX.message.JSPocket.Entries.SubscriberID._name]= this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
        if(action === 'mfino-button-edit'){
        	 if(this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
            	 Ext.MessageBox.alert(_("Info"), _("Edit pocket not allowed for Unregistered Subscriber "));
            }else{
            this.pocketEditFormWindow.show();
            var status = record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatus._name);
            //           this.pocketEditFormWindow.form.onStatusDropdown(status);
            this.pocketEditFormWindow.setRecord(record);
            this.pocketEditFormWindow.form.onStatusDropdown(status);
            var subStatus = this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name);
            var approvalStatus = this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name);
			if((subStatus == CmFinoFIX.SubscriberStatus.Initialized) || (approvalStatus == CmFinoFIX.UpgradeStateSearch.Applied)){
				this.pocketEditFormWindow.form.get("sub.pocket.status").disable();
			}
            this.pocketEditFormWindow.setStore(grid.store);
            this.pocketEditFormWindow.form.disableNotPermittedItems();
            }
        } else if (action === 'mfino-button-View') {
            this.pocketDetailsWindow.show();
            this.pocketDetailsWindow.setRecord(record);
            this.pocketDetailsWindow.setStore(grid.store);
        }else if (action === 'mfino-button-history') {
	        this.pocketTransactionsviewform.grid.store.lastOptions = {
	                    params : {
	                        start : 0,
	                        limit : CmFinoFIX.PageSize.Default
	                    }
	          };
	        this.pocketTransactionsviewform.searchform.resetHandler();
	        this.pocketTransactionsviewform.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestnPocketID._name] = record.get(CmFinoFIX.message.JSPocket.Entries.ID._name);
	        this.pocketTransactionsviewform.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name]=CmFinoFIX.TransferState.Complete;
	        this.pocketTransactionsviewform.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name] = true;
	        this.pocketTransactionsviewform.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name] = null;
	        this.pocketTransactionsviewform.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.StartTime._name] = null;
	        this.pocketTransactionsviewform.grid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.EndTime._name] = null; 
	      //this.pocketTransactionsviewform.grid.store.load(this.pocketTransactionsviewform.grid.store.lastOptions);
	        this.pocketTransactionsviewform.setRecord(record);
	        this.pocketTransactionsviewform.setStore(this.pocketTransactionsviewform.grid.store);
	        this.pocketTransactionsviewform.show();
	        this.pocketTransactionsviewform.searchform.fireEvent('search', {});
        }else if (action === 'mfino-button-currency') {
           var message= new CmFinoFIX.message.JSCheckBalanceForSubscriber();
           message.m_pPocketID = record.get(CmFinoFIX.message.JSPocket.Entries.ID._name);
           var params = mFino.util.showResponse.getDisplayParam();
           params.myForm = this.checkBalanceWindow;
           params.record=record;
           mFino.util.fix.send(message, params);
           Ext.apply(params, {
               success :  function(response){
            	   if(response.m_psuccess){
            		   this.myForm.show();
                	   this.myForm.setValues(response, params.record);
            		   }else{
            			   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription)); 	   
            	   }
               }
           });
        }
        
    },

//    editOrDeletePeer: function(grid, record, action, row, col){
//        if(action === 'mfino-button-remove') {
//            if(!record){
//                Ext.MessageBox.alert(_("Alert"), _("No User selected!"));
//            } else {
//                Ext.MessageBox.confirm(
//                    _('Delete User?'),
//                    _('Do you want to delete ') + '<b>' +
//                    record.get(CmFinoFIX.message.JSPerson2Person.Entries.MDN._name) +
//                    '</b>',
//                    function(btn){
//                        if (btn == 'yes') {
//                            if(grid.store) {
//                                grid.store.remove(record);
//                                grid.store.save();
//                            }
//                        }
//                    });
//            }
//        }
//    },
//    
    addPocket : function(){
        var record = new this.pocketGrid.store.recordType();
        if(this.record) {
        	 if(this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name)==CmFinoFIX.SubscriberStatus.NotRegistered){
            	 Ext.MessageBox.alert(_("Info"), _("Pocket addition not allowed for Unregistered Subscriber "));
            }else{
            record.data[CmFinoFIX.message.JSPocket.Entries.MDNID._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
            record.data[CmFinoFIX.message.JSPocket.Entries.SubscriberID._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
            record.data[CmFinoFIX.message.JSPocket.Entries.SubsMDN._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
            var status = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.Status._name];
            if(status == CmFinoFIX.SubscriberStatus.Initialized || status ==CmFinoFIX.SubscriberStatus.Active )
            {
                this.pocketAddFormWindow.show();
                this.pocketAddFormWindow.setRecord(record);
                this.pocketAddFormWindow.setStore(this.pocketGrid.store);
            }
            else
            {
                Ext.MessageBox.alert(_("Error"), _("Cannot Add Pocket as Subscriber is not Initialized/Active"));
            }
            }
        } else {
            Ext.MessageBox.alert(_("No Subscriber!"), _("Please select a subscriber first."));
        }
    },
    
//    addPerson2Person : function(){
//        var record = new this.person2PersonGrid.store.recordType();
//        record.data[CmFinoFIX.message.JSPerson2Person.Entries.ID._name] = this.record.data[CmFinoFIX.message.JSPerson2Person.Entries.ID._name];
//        record.data[CmFinoFIX.message.JSPerson2Person.Entries.MSPID._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MSPID._name];
//        record.data[CmFinoFIX.message.JSPerson2Person.Entries.SubscriberID._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
//
//        this.person2PersonGridEditForm.show();
//        this.person2PersonGridEditForm.setRecord(record);
//        this.person2PersonGridEditForm.setStore(this.person2PersonGrid.store);
//    },

    checkEnabledItems : function(){
        var tabNames = [];
        for(var i = 0; i < this.items.length; i++){
            tabNames.push(this.items.get(i).itemId);
        }

        for(i = 0; i < tabNames.length; i++){
            var tabName = tabNames[i];
            if(!mFino.auth.isEnabledItem(tabName)){
                var tab = this.getComponent(tabName);
                this.remove(tab);
            }
        }       
    }
});

