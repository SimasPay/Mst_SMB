/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CreditCardUserTab = function(config){
    mFino.widget.CreditCardUserTab.superclass.constructor.call(this, config);
};

Ext.extend(mFino.widget.CreditCardUserTab, Ext.TabPanel, {
    initComponent : function () {
        var config = this.initialConfig;

        this.CCDetailsGrid = new mFino.widget.CCDetailsGrid(Ext.apply({
            layout: "fit",            
            height: 375
        }, config));

        this.ccDestinationGrid = new mFino.widget.CCDestinationGrid(Ext.apply({
        	layout: "fit",  
        	height: 375
        }, config));
        
        this.cardDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.CCDetails({}),
            title : _("Card Details"),
            mode : "close",
            width:600,
            height:400
        },config));
        
        this.destinationDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
            form : new mFino.widget.CCDestinationDetails({}),
            title : _("Destination Details"),
            mode : "close",
            width:400,
            height:250
        },config));
        
        
        this.CCDetailsGrid.action.on({
            action: this.view.createDelegate(this)
        });

        
        this.ccDestinationGrid.action.on({
            action: this.viewDestination.createDelegate(this)
        });

        this.activeTab = 0;
        this.items = [
        {
            title: _('CreditCards'),
            height: 550,
//            layout : "fit",
           
//            itemId: "sub.pocket.tab",
            items:  this.CCDetailsGrid
        },
        {
            title: _('Destinations'),
//            layout:"fit",
            height: 550,
           
//            itemId: "sub.transactions.tab",
            items:  this.ccDestinationGrid
        }
        ];

        mFino.widget.CreditCardUserTab.superclass.initComponent.call(this);       
    },

    setUserRecord : function(record){
        if(!record){
            delete this.record;
            if(this.CCDetailsGrid.store){
                this.CCDetailsGrid.store.removeAll();
                this.CCDetailsGrid.store.removed = [];
//                this.CCDetailsGrid.getBottomToolbar().hide();
            }
            
            if(this.ccDestinationGrid.store) {
                this.ccDestinationGrid.store.removeAll();
                this.ccDestinationGrid.store.removed = [];
//                this.ccDestinationGrid.getBottomToolbar().hide();
            }
            return;
        }
//        this.ccDestinationGrid.setRecord(record);
        this.record = record;

//        this.CCDetailsGrid.getBottomToolbar().show();
//        this.transactionsGrid.getBottomToolbar().show();

        var subID = record.data[CmFinoFIX.message.JSUsers.Entries.ID._name];
        
        // Ticket #222: Getting the MDN.
//        var mdn = record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name];
        var createTime = record.data[CmFinoFIX.message.JSUsers.Entries.CreateTime._name];
        var lastUpdateTime = record.data[CmFinoFIX.message.JSUsers.Entries.LastUpdateTime._name];

        if(subID){
            if(this.CCDetailsGrid.store){
                this.CCDetailsGrid.store.baseParams[CmFinoFIX.message.JSCardInfo.SubscriberUserIDSearch._name] = subID;
                this.CCDetailsGrid.store.baseParams[CmFinoFIX.message.JSCardInfo.CreationDateStartTime._name]=createTime;
                this.CCDetailsGrid.store.baseParams[CmFinoFIX.message.JSCardInfo.CreationDateEndTime._name]=lastUpdateTime;
                this.CCDetailsGrid.store.load();
            }

            
            if(this.ccDestinationGrid.store){
                this.ccDestinationGrid.store.baseParams[CmFinoFIX.message.JSCreditCardDestination.SubscriberUserIDSearch._name] = subID;
                this.ccDestinationGrid.store.baseParams[CmFinoFIX.message.JSCreditCardDestination.CreationDateStartTime._name]=createTime;
                this.ccDestinationGrid.store.baseParams[CmFinoFIX.message.JSCreditCardDestination.CreationDateEndTime._name]=lastUpdateTime;
                this.ccDestinationGrid.store.load();
            }
        }
    },

    viewDestination : function(grid, record, action, row, col) {
           	this.destinationDetailsWindow.show();
            this.destinationDetailsWindow.setRecord(record);
            this.destinationDetailsWindow.setStore(grid.store);
    },
   
    view : function(grid, record, action, row, col) {
       	this.cardDetailsWindow.show();
        this.cardDetailsWindow.setRecord(record);
        this.cardDetailsWindow.setStore(grid.store);
    },

    checkEnabledItems : function(){
        var tabNames = [];
        for(var i = 0; i < this.items.length; i++){
            tabNames.push(this.items.get(i).itemId);
        }

//        for(i = 0; i < tabNames.length; i++){
//            var tabName = tabNames[i];
//            if(!mFino.auth.isEnabledItem(tabName)){
//                var tab = this.getComponent(tabName);
//                this.remove(tab);
//            }
//        }       
    }
});

