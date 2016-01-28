/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerTabsp = function(config){
    mFino.widget.ServicePartnerTabsp.superclass.constructor.call(this, config);
};

Ext.extend(mFino.widget.ServicePartnerTabsp, Ext.TabPanel, {
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
            parenttab:config.parenttab
        }, config));
        this.pocketGrid.store.form = this.pocketEditFormWindow.form;
        
        this.serviceAddFormWindow = new mFino.widget.FormWindowForPartnerService(Ext.apply({
            form : new mFino.widget.PartnerServiceForm(config),
            width : 500,
            height : 480
        },config));        
        this.serviceGrid = new mFino.widget.PartnerServiceGrid(Ext.apply({
            layout: "fit",            
            height: 470
        }, config));
        this.serviceGrid.store.form = this.serviceAddFormWindow.form;    
        
        this.serviceDetails = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.PartnerServiceViewForm({}),
            mode : "close",
            width:500,
            height:480
        },config));
        
        this.pocketDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.PocketDetails({}),
            title : _("Pocket Details"),
            mode : "close",
            width:500,
            height:550
        },config));

        this.settlementAddFormWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.SettlementTemplateAddForm(config),
            width : 450,
            height : 210
        },config));
        
        this.settlementGrid = new mFino.widget.SettlementTemplateGrid(Ext.apply({
            layout: "fit",            
            height: 470
        }, config));
        this.settlementGrid.store.form = this.settlementAddFormWindow.form;
        
        this.transactionsGrid = new mFino.widget.SubscriberTransactionsGrid(Ext.apply({
            layout: "fit",
            height: 470
        }, config));

        this.pocketTransactionsviewform = new mFino.widget.BookingDatedBalanceViewGridWindow(Ext.apply({
          grid : new mFino.widget.PocketTransactionsViewGrid(config),
          title :_( 'Pocket Transaction Details')
      },config));
        
        this.checkBalanceWindow = new mFino.widget.CheckBalanceSubscriber(Ext.apply({
            layout: "fit",
            height: 175
        }, config));
        
        this.settlementGrid.action.on({
            action: this.editSettlement.createDelegate(this)
        });

        this.settlementGrid.on({
            addclick: this.addSettlement.createDelegate(this)
        });
        this.serviceGrid.on({
            addclick: this.addService.createDelegate(this)
        });
        
        this.serviceGrid.action.on({
            action: this.editService.createDelegate(this)
        });        
        
        this.pocketGrid.on({
            addclick: this.addPocket.createDelegate(this)
        });
        
        this.pocketGrid.action.on({
            action: this.editOrViewPocket.createDelegate(this)
        });


        this.activeTab = 0;
        this.items = [
        {
            title: _('Pocket'),
            layout : "fit",
            itemId: "sub.pocket.tab",
            items:  this.pocketGrid
        },
        {
            title: _('Settlement Template'),
            layout : "fit",
//            itemId: "sub.settlement.tab",
            items:  this.settlementGrid
        },
        {
            title: _('Service'),
            layout : "fit",
//            itemId: "sub.service.tab",
            items:  this.serviceGrid
        },
        {
            title: _('Transactions'),
            layout : "fit",
            itemId: "sub.transactions.tab",
            items:  this.transactionsGrid
        }  
        ];

        mFino.widget.ServicePartnerTabsp.superclass.initComponent.call(this);       
    },

    setPartnerRecord : function(record){
        if(!record){
            delete this.record;
            if(this.pocketGrid.store){
                this.pocketGrid.store.removeAll();
                this.pocketGrid.store.removed = [];
                this.pocketGrid.getBottomToolbar().hide();
            }
            if(this.settlementGrid.store){
            	this.settlementGrid.store.removeAll();
                this.settlementGrid.store.removed = [];
                this.settlementGrid.getBottomToolbar().hide();
             }
            if(this.serviceGrid.store){
            	this.serviceGrid.store.removeAll();
                this.serviceGrid.store.removed = [];
                this.serviceGrid.getBottomToolbar().hide();
            }
            if(this.transactionsGrid.store){
            	this.transactionsGrid.store.removeAll();
                this.transactionsGrid.store.removed = [];
                this.transactionsGrid.getBottomToolbar().hide();
            }
            return;
        }
        this.record = record;

        this.pocketGrid.getBottomToolbar().show();
        this.settlementGrid.getBottomToolbar().show();
        this.serviceGrid.getBottomToolbar().show();
        this.transactionsGrid.getBottomToolbar().show();

        var partnerId = record.data[CmFinoFIX.message.JSAgent.Entries.ID._name];
        var subscriberId = record.data[CmFinoFIX.message.JSAgent.Entries.SubscriberID._name];
        
        // Ticket #222: Getting the MDN.
        var mdn = record.data[CmFinoFIX.message.JSAgent.Entries.MDN._name];
//        var subscriberID = record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
        var subscriberMDNID = record.data[CmFinoFIX.message.JSAgent.Entries.MDNID._name];
        var tradename =  record.data[CmFinoFIX.message.JSAgent.Entries.TradeName._name];

        if(subscriberId){

            if(this.pocketGrid.store){
                this.pocketGrid.store.baseParams[CmFinoFIX.message.JSPocket.SubscriberIDSearch._name] = subscriberId;
                this.pocketGrid.store.baseParams[CmFinoFIX.message.JSPocket.limit._name]=CmFinoFIX.PageSize.Default;
                this.pocketGrid.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;
                this.pocketGrid.store.load();
            }
        }
        
        if (partnerId) {
        	if (this.settlementGrid.store) {
	        	this.settlementGrid.store.baseParams[CmFinoFIX.message.JSSettlementTemplate.PartnerID._name] = partnerId;
	        	this.settlementGrid.store.baseParams[CmFinoFIX.message.JSSettlementTemplate.limit._name] = CmFinoFIX.PageSize.Default;
	        	this.settlementGrid.store.load();
        	}
        	if (this.serviceGrid.store) {
	        	this.serviceGrid.store.baseParams[CmFinoFIX.message.JSPartnerServices.PartnerID._name] = partnerId;
	        	this.serviceGrid.store.baseParams[CmFinoFIX.message.JSPartnerServices.limit._name] = CmFinoFIX.PageSize.Default;
	        	this.serviceGrid.store.load();
        	} 
        	if(this.transactionsGrid.store){
        		this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestMDNAndID._name] = mdn + "," + subscriberMDNID;
                    this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name] = true;
                    this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name]=CmFinoFIX.TransferState.Complete;
                    this.transactionsGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.limit._name]=CmFinoFIX.PageSize.Default;
                    this.transactionsGrid.store.load();
        	}
        	this.transactionsGrid.setMDNID(subscriberMDNID);
        	 this.transactionsGrid.setRecord(record);
            this.record = record;
        }
    },

    editOrViewPocket : function(grid, record, action, row, col) {
        record.data[CmFinoFIX.message.JSPocket.Entries.SubscriberID._name]= this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
        if(action === 'mfino-button-edit'){
            this.pocketEditFormWindow.show();
            var status = record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatus._name);
            //           this.pocketEditFormWindow.form.onStatusDropdown(status);
            this.pocketEditFormWindow.setRecord(record);
            this.pocketEditFormWindow.form.onStatusDropdown(status);
            this.pocketEditFormWindow.setStore(grid.store);
            this.pocketEditFormWindow.form.disableNotPermittedItems();
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
            } else if (action === 'mfino-button-currency') {
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

    addPocket : function(){
    	
        var record = new this.pocketGrid.store.recordType();
        if(this.record) {
        	if (this.isInitializedOrActive(this.record)) {
	        	record.data[CmFinoFIX.message.JSPocket.Entries.MDNID._name] = this.record.data[CmFinoFIX.message.JSAgent.Entries.MDNID._name];
	            record.data[CmFinoFIX.message.JSPocket.Entries.SubscriberID._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name];
	            record.data[CmFinoFIX.message.JSPocket.Entries.SubsMDN._name] = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
	            
	            this.pocketAddFormWindow.show();
	            this.pocketAddFormWindow.setRecord(record);
	            this.pocketAddFormWindow.setStore(this.pocketGrid.store);
	        } else {
	        	Ext.MessageBox.alert(_("Alert"), _("Adding of Pocket is possible only for Initialized/Active Agents/Partners"));
	        }
        } else {
            Ext.MessageBox.alert(_("No Subscriber!"), _("Please select a Partner first."));
        }
    },
    
    addService : function(){
        var record = new this.serviceGrid.store.recordType();
        if(this.record) {
        	if (this.isInitializedOrActive(this.record)) {
	            record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name] 
	                        = this.record.data[CmFinoFIX.message.JSAgent.Entries.ID._name];
	            
	            record.data[CmFinoFIX.message.JSPartnerServices.SubscriberID._name] 
	                        = this.record.data[CmFinoFIX.message.JSAgent.Entries.SubscriberID._name];
	            
	            record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerTypeSearch._name] 
	                        = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];        	
	            
	            this.serviceAddFormWindow.setTitle(_("Add Partner Service for ") + 
	            		this.record.data[CmFinoFIX.message.JSAgent.Entries.TradeName._name]);    
	            this.serviceAddFormWindow.setMode("add");
	            this.serviceAddFormWindow.form.onStatusDropdown(CmFinoFIX.PartnerServiceStatus.Initialized);
	            this.serviceAddFormWindow.show();
	            this.serviceAddFormWindow.form.find('itemId','ps.form.serviceprovider')[0].enable();
	            this.serviceAddFormWindow.setRecord(record);
	            this.serviceAddFormWindow.setStore(this.serviceGrid.store);
        	} else {
        		Ext.MessageBox.alert(_("Alert"), _("Adding of Service is possible only for Initialized/Active Agents/Partners"));
        	}
        } else {
            Ext.MessageBox.alert(_("No Partner!"), _("Please select a partner first."));
        }
    },  
    
    editService : function(grid, record, action, row, col) {
    	record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name] 
                    = this.record.data[CmFinoFIX.message.JSAgent.Entries.ID._name];
    	
         record.data[CmFinoFIX.message.JSPartnerServices.SubscriberID._name] 
                    = this.record.data[CmFinoFIX.message.JSAgent.Entries.SubscriberID._name];
         
         record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerTypeSearch._name] 
                     = this.record.data[CmFinoFIX.message.JSAgent.Entries.BusinessPartnerType._name];        	

    	if(action === 'mfino-button-edit'){
    		var status = record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerServiceStatus._name];
    		this.serviceAddFormWindow.setTitle(_("Edit Partner Service for ") + 
    				this.record.data[CmFinoFIX.message.JSAgent.Entries.TradeName._name]);
    		this.serviceAddFormWindow.setMode("edit");
    		this.serviceAddFormWindow.show();
            this.serviceAddFormWindow.setRecord(record);
            this.serviceAddFormWindow.setStore(grid.store);
            this.serviceAddFormWindow.form.disableNotPermittedItems();
            this.serviceAddFormWindow.form.onStatusDropdown(status);
            this.serviceAddFormWindow.form.find('itemId','ps.form.serviceprovider')[0].disable();
        } else if (action === 'mfino-button-View') {
        	this.serviceDetails.setTitle(_("Partner Service Details for ") + 
        			this.record.data[CmFinoFIX.message.JSAgent.Entries.TradeName._name]);
        	this.serviceDetails.show();
            this.serviceDetails.setRecord(record);
        }
    },    
    
    addSettlement : function(){
        var record = new this.settlementGrid.store.recordType();
        if(this.record) {
        	if (this.isInitializedOrActive(this.record)) {
                record.data[CmFinoFIX.message.JSSettlementTemplate.Entries.PartnerID._name] 
			                = this.record.data[CmFinoFIX.message.JSAgent.Entries.ID._name];
			    record.data[CmFinoFIX.message.JSSettlementTemplate.SubscriberID._name] 
			                = this.record.data[CmFinoFIX.message.JSAgent.Entries.SubscriberID._name];
			    this.settlementAddFormWindow.setTitle(_("Add Settlement Template for ") + 
			    		this.record.data[CmFinoFIX.message.JSAgent.Entries.TradeName._name]);   
			    this.settlementAddFormWindow.setMode("add");
			    this.settlementAddFormWindow.show();
			    this.settlementAddFormWindow.setRecord(record);
			    this.settlementAddFormWindow.setStore(this.settlementGrid.store);
        	} else {
        		Ext.MessageBox.alert(_("Alert"), _("Adding of Settelement template is possible only for Initialized/Active Agents/Partners"));
        	}

        } else {
            Ext.MessageBox.alert(_("No Partner!"), _("Please select a partner first."));
        }
    },
    
    editSettlement : function(grid, record, action, row, col) {
    	record.data[CmFinoFIX.message.JSSettlementTemplate.Entries.PartnerID._name] 
                    = this.record.data[CmFinoFIX.message.JSAgent.Entries.ID._name];
        record.data[CmFinoFIX.message.JSSettlementTemplate.SubscriberID._name] 
                    = this.record.data[CmFinoFIX.message.JSAgent.Entries.SubscriberID._name];
    	if(action === 'mfino-button-edit'){
    		this.settlementAddFormWindow.setTitle(_("Edit Settlement Template for ") + 
    				this.record.data[CmFinoFIX.message.JSAgent.Entries.TradeName._name]);
    		this.settlementAddFormWindow.setMode("edit");
            this.settlementAddFormWindow.show();
            this.settlementAddFormWindow.setRecord(record);
            this.settlementAddFormWindow.setStore(grid.store);
            this.settlementAddFormWindow.form.enableOrDisable(this.isInitializedOrActive(this.record));
        }
    },

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
    },
    
    isInitializedOrActive : function (record) {
    	var status = false;
    	if (record) {
        	if (record.data[CmFinoFIX.message.JSAgent.Entries.PartnerStatus._name] == CmFinoFIX.SubscriberStatus.Initialized || 
        			record.data[CmFinoFIX.message.JSAgent.Entries.PartnerStatus._name] == CmFinoFIX.SubscriberStatus.Active) {
        		status = true;
        	}
    	}

    	return status;
    }
});

