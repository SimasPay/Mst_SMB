/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SettlementConfigGrid = function(config) {
	var localConfig = Ext.apply( {}, config);

	var dateEditor = new Ext.form.DateField({
        editable:false,
        emptyText: _('eg: 10/29/2009')
	});
	
	this.stEditor = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        pageSize : 10,
        lastQuery: '',
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSettlementTemplate),
        RPCObject : CmFinoFIX.message.JSSettlementTemplate,
        displayField: CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementName._name,
        valueField : CmFinoFIX.message.JSSettlementTemplate.Entries.ID._name,
        autoLoad: true
	});
	


	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	};
	
	function formatDate(value){
        return value ? value.dateFormat('m/d/Y') : '';
    }
	
	var isDefaultColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _('Is Default'),
				dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.IsDefault._name,
				width : 55
				});
	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSServiceSettlementConfig);
	}

	this.rowDelete = new Ext.ux.grid.RowActions({
		header : '',
		keepSelection : true,
		actions : [{
			iconCls : 'mfino-button-remove',
			tooltip : _('Delete Row'),
			align : 'center'
		}],
		callbacks:{
			'mfino-button-remove' : function(grid, record, action, rowIndex, colIndex){
				Ext.MessageBox.confirm(_('Delete Settlement configuration?'), _('Do you want to delete Settlement Template [')
						+record.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.SettlementName._name]
						+'] ?',
					function(btn) {
						if (btn === 'yes') {
							if (grid.store) {
								grid.store.remove(record);
							}
						}
					}
				);
			 }
		}		
	});
	
	localConfig = Ext.apply(localConfig, {
		dataUrl : "fix.htm",
		loadMask : true,
		plugins : [ this.rowDelete, isDefaultColumn ],
		tbar : [{
			iconCls : 'mfino-button-add',
			text : _('Add Settlement Template'),
			handler : this.onAdd.createDelegate(this)
		}],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),

		columns : [
		        this.rowDelete,
				{
					header : _("Settlement Template"),
					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.SettlementTemplateID._name,
					width : 140,
		            editor: this.stEditor,
		            renderer: Ext.util.Format.comboRenderer(this.stEditor)

				},	
				isDefaultColumn				
//				{
//					header : _("Start Date"),
//					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.StartDate._name,
//					width : 100,
//					editor : dateEditor,
//					renderer: formatDate
//				},
//				{
//					header : _("End Date"),
//					dataIndex : CmFinoFIX.message.JSServiceSettlementConfig.Entries.EndDate._name,
//					width : 100,
//					editor : dateEditor,
//					renderer: formatDate
//				}
				]
	});

	mFino.widget.SettlementConfigGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SettlementConfigGrid, Ext.grid.EditorGridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.SettlementConfigGrid.superclass.initComponent.call(this);

	},
	
	onAdd : function() {
		var record = new this.store.recordType();
		record.disable = false;
		var size = this.store.getCount();
		record.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.ID._name] = -1;
		if (this.partnerServiceID) {
			record.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.PartnerServicesID._name] = this.partnerServiceID;
		}
		this.store.add(record);
	},

	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},

	setParentData : function(partnerServiceID, partnerId) {
		this.partnerServiceID = partnerServiceID;
		this.partnerId = partnerId;
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('PartnerServicesID', partnerServiceID);
		}
	},
	
	setScheduleStatus : function(scheduleStatus) {
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('SchedulerStatus', scheduleStatus);
		}
	},
	
	setCollectorPocket : function(collectorPocketID) {
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('CollectorPocket', collectorPocketID);
		}
	},
	
	reloadGrid : function() {
		this.loadSettlementTemplate();
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.partnerServiceID) {
			Ext.apply(this.store.lastOptions.params, {
				"PartnerServicesID" : this.partnerServiceID
			});
		}
		this.store.load(this.store.lastOptions);
	},
	
	loadSettlementTemplate : function() {
		if (this.partnerId) {
			this.stEditor.store.baseParams[CmFinoFIX.message.JSSettlementTemplate.PartnerID._name] = this.partnerId;
			this.stEditor.store.reload({
				params: {PartnerID: this.partnerId }
			});
		}
	},
	
	checkSettlementConfigs : function() {
		size = this.store.getCount();
		if((size ==0) && (EMONEY_ENABLED)) {
			Ext.ux.Toast.msg(_("Error"), _("Please add the default Settlement Template"),5);
			return 0; // failure
		}		
		var lst = new Array(size-1);
		var defaultCount = 0;
		var c = 0;
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var stId = rec.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.SettlementTemplateID._name];
//			var startDate = rec.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.StartDate._name];
//			var endDate = rec.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.EndDate._name];
			var isDefault = rec.data[CmFinoFIX.message.JSServiceSettlementConfig.Entries.IsDefault._name];
			
//			if ((stId===null || typeof(stId)==="undefined") 
//					|| (!isDefault && ((startDate===null || typeof(startDate)==="undefined")
//						|| (endDate===null || typeof(endDate)==="undefined")))) {
			if (stId===null || typeof(stId)==="undefined") {
				Ext.ux.Toast.msg(_("Error"), _("Enter the data for Settlement configuration at row: ") + (i+1),5);
				return 0; // failure				
			}
			
			if (isDefault) {
				defaultCount++;
			}
//			
//			if (!isDefault && (endDate <= startDate)) {
//				Ext.ux.Toast.msg(_("Error"), _("End Date must be greater than the Start Date at row: ") + (i+1),5);
//				return 0; // failure				
//			}
//			
//			if (!isDefault) {
//				lst[c] = new Array(2);
//				lst[c][0] = startDate;
//				lst[c][1] = endDate;
//				c++;
//			}
		}
		
		if((defaultCount <= 0) && (EMONEY_ENABLED)){
			Ext.ux.Toast.msg(_("Error"), _("Atleast one default Settlement Template should be defined"),5);
			return 0; // failure				
		} else if((defaultCount > 1) && (EMONEY_ENABLED)) {
			Ext.ux.Toast.msg(_("Error"), _("Only one default Settlement Template should be defined"),5);
			return 0; // failure
		}
		
//		var slst = mFino.util.fix.sort(lst);
//		size = slst.length;
//		
//		for (var i=0; i<size; i++ ){
//			var minLmt = slst[i][0];
//			var maxLmt = slst[i][1];
//			
//			// Checking for the duplicate ranges
//			for (var j=i+1; j<size; j++) {
//				var minLmt_j = slst[j][0];
//				var maxLmt_j = slst[j][1];				
//				if (minLmt_j <= minLmt || maxLmt_j <= minLmt
//						|| minLmt_j <= maxLmt || maxLmt_j <= maxLmt) {
//					Ext.ux.Toast.msg(_("Error"), _("Date ranges are not defined properly."),5);
//					return 0; // failure						
//				}
//			}
//			
//			// checking whether all the pricing ranges defined properly or not.
//			if ((i<size-1) && (slst[i+1][0] - slst[i][1]) != (24*60*60*1000)) {
//				Ext.ux.Toast.msg(_("Error"), _("Date ranges are not defined properly."),5);
//				return 0; // failure	
//			}
//		}		
		return 1; //Success
	}
	
});



Ext.reg("settlementconfiggrid", mFino.widget.SettlementConfigGrid);
