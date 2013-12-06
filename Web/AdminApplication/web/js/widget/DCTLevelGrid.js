/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.DCTLevelGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	var commisionEditor = new Ext.form.NumberField( {
		allowNegative : false,
		allowDecimals : true,
		decimalPrecision : 2,
		maxValue : 100
	});
	var numberEditor = new Ext.form.NumberField( {
		allowNegative : false,
		allowDecimals : true,
		decimalPrecision : 2
	});
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl,
				CmFinoFIX.message.JSDistributionChainLevel);
	}
	
	this.transactionTypeCombo = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        id: "dctlevelgrid.form.transactiontype",
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionType),
        RPCObject : CmFinoFIX.message.JSTransactionType,
        displayField: CmFinoFIX.message.JSTransactionType.Entries.TransactionName._name,
        valueField : CmFinoFIX.message.JSTransactionType.Entries.ID._name
	});

	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	}
	
	// the check column is created using a custom plugin
	var rechargeColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _('Recharge'),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.RechargeAllowed._name,
				width : 60
			});
	var dirDistColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _("Direct Distribute"),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.DirectDistributeAllowed._name,
				width : 90
			});
	var indirDistColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _('Indirect Distribute'),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.IndirectDistributeAllowed._name,
				width : 100
			});
	var dirTrnsColumn = new Ext.grid.CheckColumn(
			{
				header : _('Direct Transfer'),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.DirectTransferAllowed._name,
				width : 90
			});
	var indirTrnsColumn = new Ext.grid.CheckColumn(
			{
				header : _('Indirect Transfer'),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.IndirectTransferAllowed._name,
				width : 100
			});
	var genLOPColumn = new Ext.grid.CheckColumn(
			{
				header : _('Generate LOP'),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.GenerateLOPAllowed._name,
				width : 80
			});
	var distLOPColumn = new Ext.grid.CheckColumn(
			{
				header : _('Distribute LOP'),
				dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPDistributeAllowed._name,
				width : 80
			});

	// this.rowActions = new Ext.ux.grid.RowActions({
	// header:'',
	// keepSelection:true,
	// actions:[{
	// iconCls:'mfino-button-remove',
	// tooltip:'Delete'
	// }],
	// callbacks:{
	// 'mfino-button-remove' : function(grid, record, action, rowIndex,
	// colIndex){
	// if(!record){
	// Ext.MessageBox.alert(_("Alert"), _("No User selected!"));
	// } else {
	// Ext.MessageBox.confirm(
	// 'Delete Level?',
	// 'Do you want to delete <b>' +
	// record.get(CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name)
	// +
	// '</b>',
	// function(btn){
	// if (btn == 'yes') {
	// if(grid.store) {
	// grid.store.remove(record);
	// if(!record.phantom){
	// grid.store.save();
	// }
	//
	// var tempIndex = 1;
	// grid.store.each(function(){
	// this.set(CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name,
	// tempIndex);
	// tempIndex++;
	// });
	// }
	//
	// }
	// });
	// }
	// }
	// }
	// });
	//

	localConfig = Ext
			.apply(
					localConfig,
					{
						dataUrl : "fix.htm",
						loadMask : true,
						clicksToEdit : 1,
						plugins : [ rechargeColumn, dirDistColumn,
								indirDistColumn, dirTrnsColumn,
								indirTrnsColumn, genLOPColumn, distLOPColumn ],
						tbar : [ '->', {
							iconCls : 'mfino-button-add',
							tooltip : _('Add Level'),
							handler : this.onAdd.createDelegate(this)
						}, {
							iconCls : 'mfino-button-remove',
							tooltip : _('Delete Last Level'),
							handler : this.onDeleteLast.createDelegate(this)
						} ],

						sm : new Ext.grid.RowSelectionModel( {
							singleSelect : true
						}),

						columns : [
								{
									header : _("Level"),
									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name,
									width : 50
								},
//								{
//									header : _("Transaction"),
//									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.TransactionID._name,
//									width : 175,
//						            editor: this.transactionTypeCombo,
//						            renderer: Ext.util.Format.comboRenderer(this.transactionTypeCombo)
//								},								
								{
									header : _("Commission(%)"),
									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPCommission._name,
									width : 110,
									editor : commisionEditor
								}
							/*	,
								{
									header : _("Max Commission(%)"),
									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPMaxCommission._name,
									width : 110,
									editor : commisionEditor
								},
								{
									header : _("Min Commission(%)"),
									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPMinCommission._name,
									width : 110,
									editor : commisionEditor
								}
								,
								{
									header : _("Maximum Weekly Purchase Amount"),
									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.MaxWeeklyPurchaseAmount._name,
									width : 110,
									maxLength : 16,
									editor : numberEditor
								},
								{
									header : _("Maximum Limit Per LOP Transaction"),
									dataIndex : CmFinoFIX.message.JSDistributionChainLevel.Entries.MaxAmountPerTransaction._name,
									width : 110,
									maxLength : 16,
									editor : numberEditor
								}, rechargeColumn, dirDistColumn,
								indirDistColumn, dirTrnsColumn,
								indirTrnsColumn, genLOPColumn, distLOPColumn 
*/								
							]
					});

	mFino.widget.DCTLevelGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DCTLevelGrid,Ext.grid.EditorGridPanel,{
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.DCTLevelGrid.superclass.initComponent.call(this);
	},
	onAdd : function() {
		var record = new this.store.recordType();
		var size = this.store.getCount();
		// FIXME: Currently the serializer seems to consider
		// only one record
		// without an ID
		// This is a workaround to add multiple records at the
		// same time.
		record.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.ID._name] = -1;
		record.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name] = size + 1;
		if (this.templateID) {
			record.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionChainTemplateID._name] = this.templateID;
		}
		this.store.add(record);
	},
	onDeleteLast : function() {
		var size = this.store.getCount();
		if(size == 1) {
			Ext.ux.Toast.msg(_('Error'),_("Level cannot be deleted. DCT should have atleast 1 level"));
			return 1;
		}
		if (size > 0) {
			var record = this.store.getAt(size - 1);
			var store = this.store;
			Ext.MessageBox.confirm(_('Delete Level?'),
					_('Do you want to delete ')+ '<b>'+ record.get(CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name)+ '</b>', 
								function(btn) {
									if (btn == 'yes') {
										if (store) {
											store.remove(record);
											if (!record.phantom) {
												store.save();
											}
										}
									}
								});
		} else {
			Ext.ux.Toast.msg(_('Error'),_("No Records to Delete"));
		}
	},
	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},
	setTemplateID : function(templateID) {
		this.templateID = templateID;
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('DistributionChainTemplateID', templateID);
		}
	},
	validateLevels : function(){
		var size = this.store.getCount();
		if(size == 0){
			Ext.ux.Toast.msg(_("Error"),_("DCT creation with 0 levels is not allowed"));
			return 1; // failure
		}
		// Check if total commission is greater than 100%
		var totalCommission = 0; 
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var commission = rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPCommission._name];
			if(commission == null || commission === "") {
				Ext.ux.Toast.msg(_("Error"),_("Commission should not be empty"));
				return 1; // failure
			}
			totalCommission += commission;
		}
		if(totalCommission > 100){
			Ext.ux.Toast.msg(_("Error"),_("Total commission should not be greater than 100%"));
			return 1; // failure
		}
		return 0; //success
	},
	calculatePermissions : function() { 
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			var perm = CmFinoFIX.DistributionPermissions.None;
			/*
			 * Max, min commission is no longer used, hence commented the below code 
			 * Note: CmFinoFIX.message.JSDistributionChainLevel.Entries.Permissions._name is mandatory for every record as it not null value in DB
			 * hence it is simply set to CmFinoFIX.DistributionPermissions.None value
			 * 
			 * var maxlopcommission = rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPMaxCommission._name];
			var minlopcommission = rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPMinCommission._name];
			if(!maxlopcommission){
				maxlopcommission=null;
			}
			if(!minlopcommission){
				minlopcommission=null;
			}
			if (maxlopcommission === null || maxlopcommission.length===0) {
				if (minlopcommission !== null && minlopcommission.length!==0) {
					Ext.ux.Toast.msg(_("Error"),_("Enter Max commission for level "+ rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name]));
					return 1; // failure
				}
			}
			if (minlopcommission === null || minlopcommission.length===0) {
				if (maxlopcommission !== null && maxlopcommission.length!==0) {
					Ext.ux.Toast.msg(_("Error"),_("Enter Min commission for level "+ rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name]));
					return 1; // failure
				}
			}
			if(!((maxlopcommission===null || maxlopcommission.length===0)&&(minlopcommission===null|| minlopcommission.length===0))){
			if (maxlopcommission <= minlopcommission) {
				Ext.ux.Toast.msg(_("Error"),_("Max commission is less than Min commission for level "+ rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DistributionLevel._name]));
				return 1; // failure
				}
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.RechargeAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.Recharge;
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DirectDistributeAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.DirectDistribute;
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.IndirectDistributeAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.IndirectDistribute;
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.DirectTransferAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.DirectTransfer;
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.IndirectTransferAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.IndirectTransfer;
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.GenerateLOPAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.LOP;
			}
			if (rec.data[CmFinoFIX.message.JSDistributionChainLevel.Entries.LOPDistributeAllowed._name]) {
				perm += CmFinoFIX.DistributionPermissions.LOPDistribute;
			}*/
			rec.set(CmFinoFIX.message.JSDistributionChainLevel.Entries.Permissions._name,perm);
		}		
	},
	reloadGrid : function() {
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.templateID) {
			Ext.apply(this.store.lastOptions.params, {
				"DistributionChainTemplateID" : this.templateID
			});
		}
		this.store.load(this.store.lastOptions);
	},
	setServiceId: function(value){
		this.serviceId = value;
		this.transactionTypeCombo.clearValue();
		this.transactionTypeCombo.store.reload({
			params: {ServiceIDSearch : value}
		});
	}
});

Ext.reg("levelgrid", mFino.widget.DCTLevelGrid);
