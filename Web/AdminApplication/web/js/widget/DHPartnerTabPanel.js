Ext.ns("mFino.widget");

mFino.widget.DHPartnerTabPanel = function (config)
{
	mFino.widget.DHPartnerTabPanel.superclass.constructor.call(this, config);
}

Ext.extend(mFino.widget.DHPartnerTabPanel, Ext.TabPanel, {

	initComponent : function () {
		var config = this.initialConfig;
		this.activeTab = 0;

		this.partnerRestrictionsGrid = new mFino.widget.PartnerRestrictionsGrid(Ext.apply({
			layout: "fit",            
			height: 470
		}, config));
		
		this.transactionsGrid = new mFino.widget.SubscriberTransactionsGrid(Ext.apply({
            layout: "fit",
            height: 450,
            parenttab: "dhpartnertabpanel"
        }, config));
		
		var txGrid = this.transactionsGrid;
		
		var chargeDistributionview = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
			title : " Charge Distribution Details  ",
			grid : new mFino.widget.ChargeDistributionViewGrid(config),
			height : 466,
			width: 800
		},config));
    
		txGrid.action.on({
			action:function(grid, record, action, row, col) {
				if(action === 'mfino-button-View'){
					chargeDistributionview.grid.store.lastOptions = {
						params : {
							start : 0,
							limit : CmFinoFIX.PageSize.Default
						}
					};
					chargeDistributionview.grid.store.baseParams[CmFinoFIX.message.JSTransactionAmountDistributionLog.ServiceChargeTransactionLogID._name] = record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.ServiceChargeTransactionLogID._name);
					chargeDistributionview.grid.store.load(chargeDistributionview.grid.store.lastOptions);
					chargeDistributionview.setStore(chargeDistributionview.grid.store);
					chargeDistributionview.show();
				}
			}
		});
		
	
		this.partnerServiceForm = new mFino.widget.DHPartnerServiceForm(Ext.apply({
            layout: "form",
            height: 450
        }, config));
		
		var psForm = this.partnerServiceForm;
		
		this.partnerServicesStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPartnerServices);
		var psStore = this.partnerServicesStore;

		this.partnerServicesStore.on("load", function(store, records, opts){
			var record = store.getAt(0);
			this.fireEvent("pstoreload",record);
		});
		
		//This is getting called on each form update, need to find a fix to stop extra ajax calls.
		this.partnerServicesStore.on("pstoreload", function(record){
			if(record){
				psForm.setRecord(record);
				psForm.setStore(psStore);
			}
			else{
				var recordId = Ext.id();
				var defaultData = {};
				record = new psStore.recordType(defaultData, recordId);
				psStore.insert(0,record);
				psForm.setRecord(record);
			}
		});
		
		this.partnerRestrictionsGrid.store.on("save", function(a,b,c) {
			if(a.reader.ResponseMsg.m_psuccess == true){
				Ext.ux.Toast.msg(_("Info"), "Partner Restrictions saved successfully.", 2);
			}
		});
		
		this.partnerStore = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner);
		
		this.partnerStore.on("load", function(store, records, opts){
			var record = store.getAt(0);
			this.fireEvent("mdnStoreLoad",record);
		});

		this.partnerStore.on("mdnStoreLoad", function(record){
			if(txGrid.store) {
				txGrid.store.removeAll();
				txGrid.store.removed = [];
				txGrid.getBottomToolbar().hide();
			}

			txGrid.setRecord(record);
			txGrid.getBottomToolbar().show();

			if(txGrid.store){
				txGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceDestMDNAndID._name] = record.data['MDN'] + "," + record.data['MDNID'];
				txGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.SourceMDN._name] = record.data['MDN'];
				txGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.IsMiniStatementRequest._name] = true;
				txGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.ServiceID._name] = this.partnerServicesStore.baseParams['ServiceID'];
				txGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.TransferState._name]=CmFinoFIX.TransferState.Complete;
				txGrid.store.baseParams[CmFinoFIX.message.JSCommodityTransfer.limit._name]=CmFinoFIX.PageSize.Default;
				txGrid.store.load();
			}
		},this);

		this.items = [
			{
				 title: _('Restrictions'),
				layout : "fit",
				itemId: "partner.restrictions.restrictionstab",
				items:  [this.partnerRestrictionsGrid]
			},
			{
				 title: _('Service'),
				layout : "fit",
				itemId: "partner.restrictions.pstab",
				items:  [this.partnerServiceForm]
			},
			{
				 title: _('Transactions'),
				layout : "fit",
				itemId: "partner.restrictions.txntab",
				items:  [this.transactionsGrid]
			}
		];
		
//		this.partnerRestrictionsGrid.store.on("exception", function(a,b,c) {
//			Ext.ux.Toast.msg(_("Info"), "Exception saving DCT Restrictions.", 2);
//		});
		
		mFino.widget.DHPartnerTabPanel.superclass.initComponent.call(this);     
	},
	
	setValues: function(values){
		this.values = values;
		this.partnerRestrictionsGrid.setValues(values);
		this.partnerServiceForm.setValues(values);	
		//alert('values.PermissionType='+values.PermissionType+', CmFinoFIX.PermissionType.Read_Write='+CmFinoFIX.PermissionType.Read_Write);
		
		if(values.PermissionType != CmFinoFIX.PermissionType.Read_Write){
			this.partnerRestrictionsGrid.setDisabled(true);
			this.partnerServiceForm.setDisabled(true);	
		}
		else{
			this.partnerRestrictionsGrid.setDisabled(false);
			this.partnerServiceForm.setDisabled(false);	
		}

		//this.partnerStore.baseParams['MDNSearch'] = values.MDN;
		this.partnerStore.baseParams['PartnerIDSearch'] = values.PartnerID;
		this.partnerStore.baseParams['IsHierarchyTabPartnerIDSearch'] = true;
		this.partnerStore.load();

		this.partnerServicesStore.baseParams['PartnerID'] = values.PartnerID;
		this.partnerServicesStore.baseParams['ServiceID'] = values.ServiceID;
		this.partnerServicesStore.load();
	},

	onSaveSucess: function(s,b,n){
		Ext.ux.Toast.msg(_("Info"), "Partner Restrictions saved successfully.");
	}
});

Ext.reg("DHPartnerTabPanel", mFino.widget.DHPartnerTabPanel);