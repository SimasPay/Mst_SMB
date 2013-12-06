/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SharePartnerGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	
	
	var commisionEditor = new Ext.form.TextField({
		allowBlank: false,
		vtype : 'shareValidation'
	});
	
	var partnerEditor = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        pageSize : 10,
        lastQuery: '',
        id: "sharepartner.form.partner",
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPartner),
        RPCObject : CmFinoFIX.message.JSPartner,
        displayField: CmFinoFIX.message.JSPartner.Entries.TradeName._name,
        valueField : CmFinoFIX.message.JSPartner.Entries.ID._name	
	});
	
	var typeEditor = new mFino.widget.EnumDropDown({
		addEmpty : true,
		enumId : CmFinoFIX.TagID.ShareHolderType
	});	

	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	}
	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSharePartner);
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
				Ext.MessageBox.confirm(_('Remove Partner?'), _('Do you want to remove partner at row: ' 
						+ (rowIndex+1) + ' ?'),
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
		plugins : [ this.rowDelete ],
		tbar : [{
			iconCls : 'mfino-button-add',
			text : _('Add Partner'),
			handler : this.onAdd.createDelegate(this)
		}],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),
		
		listeners: {		
			beforeedit : function(e) {
				if(e.field == 'PartnerID'){
					var selectedRow = e.grid.selModel.getSelected();
					if(selectedRow.data.ShareHolderType != CmFinoFIX.ShareHolderType.Partner){
						return false;
					}
				}
				if(e.field == 'ActualSharePercentage') {
					new Ext.ToolTip({
			            closable:true,
			            hideDelay : 5000,
			            dismissDelay : 5000,
			            padding: '0 0 0 0',		            
			            width: 100,		            
			            html: 	'Enter share in %'
			        }).showAt([this.el.getX() + 430, this.el.getY()]);
				}
			},	
			afteredit : function(e) {
				var rowData = e.record.data;
				if(e.field == 'ShareHolderType'){
					if(rowData.ShareHolderType != CmFinoFIX.ShareHolderType.Partner){
						e.grid.store.getAt(e.row).set(CmFinoFIX.message.JSSharePartner.Entries.PartnerID._name,-1);							
					}
				} /*else if(e.field == 'ActualSharePercentage' || e.field == 'MinSharePercentage' || e.field == 'MaxSharePercentage'){
					var expr = rowData[e.field];
					if(expr != "" && expr!= null){
						var msg = new CmFinoFIX.message.JSValidateChargeExpr();
			            msg.m_pCharge = expr;
			            var params = {
		                    success :  function(response){           	
		                		if(response.m_pErrorDescription){
		                			Ext.ux.Toast.msg(_("Error"), "Invalid expression for "+e.field+":"+response.m_pErrorDescription.split(":")[0]);
		                			e.record.set(e.field, "");
		                        }
		                    }
			            };
			            mFino.util.fix.send(msg, params);
					}
				}*/
			}
	    },

		columns : [
		        this.rowDelete,
		        {
					header : _("Share Holder Type"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.ShareHolderType._name,
					width : 125,
		            editor: typeEditor,
		            renderer: Ext.util.Format.comboRenderer(typeEditor)
				},
		        {
					header : _("Partner Name"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.PartnerID._name,
					width : 125,
		            editor: partnerEditor,
		            renderer: Ext.util.Format.comboRenderer(partnerEditor)
				},				
				{
					header : _("Share"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.ActualSharePercentage._name,
					width : 75,
					editor : commisionEditor
				}/*,
				{
					header : _("Min Share"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.MinSharePercentage._name,
					width : 75,
					editor : commisionEditor
				},
				{
					header : _("Max Share"),
					dataIndex : CmFinoFIX.message.JSSharePartner.Entries.MaxSharePercentage._name,
					width : 75,
					editor : commisionEditor
				}*/
				]
	});

	mFino.widget.SharePartnerGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SharePartnerGrid, Ext.grid.EditorGridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.SharePartnerGrid.superclass.initComponent.call(this);

	},
	
	onBeforeEdit: function(e){
		var cell = e.grid.getView().getCell(e.row, e.cell);
	},
	
	onAdd : function() {
		var record = new this.store.recordType();
		var size = this.store.getCount();
		record.data[CmFinoFIX.message.JSSharePartner.Entries.ID._name] = -1;
		if (this.templateID) {
			record.data[CmFinoFIX.message.JSSharePartner.Entries.TransactionChargeID._name] = this.templateID;			
			record.data[CmFinoFIX.message.JSSharePartner.Entries.ShareType._name] = CmFinoFIX.ShareType.TransactionCharge;
		}
		this.store.add(record);
	},

	reset : function() {
		this.store.removeAll();
		this.store.removed = [];
	},
	
	disablePartner: function(){
		var hai = '';
	},

	setTemplateID : function(templateID) {
		this.templateID = templateID;
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('TransactionChargeID', templateID);
		}
	},
					
	reloadGrid : function(transactionruleId) {
		this.loadPartners(transactionruleId);
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.templateID) {
			Ext.apply(this.store.lastOptions.params, {
				"TransactionChargeID" : this.templateID
			});
		}
		this.store.load(this.store.lastOptions);
	},

	loadPartners : function(transactionruleId) {
		var searchPartners = CmFinoFIX.BusinessPartnerType.ServicePartner + "," + CmFinoFIX.BusinessPartnerType.SolutionPartner + 
								"," + CmFinoFIX.BusinessPartnerType.RegulatoryBody;
    	var sr_combo = Ext.getCmp("sharepartner.form.partner");

    	sr_combo.store.baseParams[CmFinoFIX.message.JSPartner.TransactionRuleSearch._name] = transactionruleId;
    	sr_combo.store.baseParams[CmFinoFIX.message.JSPartner.PartnerTypeSearchString._name] = searchPartners;
    	
    	sr_combo.store.reload({
    		params: {
    			TransactionRuleSearch: transactionruleId,
    			PartnerTypeSearchString: searchPartners
    		}
    	});	
	},
	
	validateChargeShareGrid : function() {
		size = this.store.getCount();
		var total = 0;
		if(size == 0) {
			Ext.ux.Toast.msg(_("Error"), _("Please add a partner for Charge share"),5);
			return -1; // failure
		}
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var shid = rec.data[CmFinoFIX.message.JSSharePartner.Entries.ShareHolderType._name];
			var spid = rec.data[CmFinoFIX.message.JSSharePartner.Entries.PartnerID._name];
			var share = rec.data[CmFinoFIX.message.JSSharePartner.Entries.ActualSharePercentage._name];
			/*var minShare = rec.data[CmFinoFIX.message.JSSharePartner.Entries.MinSharePercentage._name];
			var maxShare = rec.data[CmFinoFIX.message.JSSharePartner.Entries.MaxSharePercentage._name];*/
			if ( (shid == CmFinoFIX.ShareHolderType.Partner) && ((spid===null) || (typeof(spid) === "undefined")) ) {
				Ext.ux.Toast.msg(_("Error"), _("Please select the Partner at row: ") + (i+1),5);
				return -1; // failure				
			}
			if (share == "" || share == null /*|| minShare == "" || minShare == null || maxShare == "" || maxShare == null*/) {
				Ext.ux.Toast.msg(_("Error"), _("Please enter share percentage at row: ") + (i+1),5);
				return -1; // failure				
			}
			total = total + Number(share.replace('%',''));
		}
		if(size > 0 && total != 100) {
			Ext.ux.Toast.msg(_("Error"), _("Total share must be equal to 100%"),5);
			return -1; // failure
		}		
		return 0; //success
	},
	
	onlyOneNonPartner : function() {
		size = this.store.getCount();
		var srcCount = 0, dstCount = 0, regCount = 0;
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var shid = rec.data[CmFinoFIX.message.JSSharePartner.Entries.ShareHolderType._name];
			
			if (shid == CmFinoFIX.ShareHolderType.Source) {
				srcCount += 1;				
			}
			if (shid == CmFinoFIX.ShareHolderType.Destination) {
				dstCount+= 1;
			}
			if (shid == CmFinoFIX.ShareHolderType.RegistrationPartner) {
				regCount+= 1;
			}
			if ((srcCount > 1) || (dstCount > 1) || (regCount > 1)) {
				Ext.ux.Toast.msg(_("Error"), _("Only one Source/Destination/RegistrationPartner type allowed"));
				return -1; // failure				
			}
		}
	}
	
	
});

Ext.reg("sharepartnergrid", mFino.widget.SharePartnerGrid);
