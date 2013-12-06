/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.TxnRuleAdditionalInfoGrid = function(config) {
	var localConfig = Ext.apply( {}, config);
	
	
	var valueEditor = new Ext.form.TextField({
		allowBlank: false
	});
	
	/*var comparatorEditor = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        pageSize : 10,
        lastQuery: '',        
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSRuleKeyComparision),
        RPCObject : CmFinoFIX.message.JSRuleKeyComparision,
        displayField: CmFinoFIX.message.JSRuleKeyComparision.Entries.TxnRuleKeyComparision._name,
        valueField : CmFinoFIX.message.JSRuleKeyComparision.Entries.TxnRuleKeyComparision._name	
	});*/

	
	this.ruleKeyEditor = new Ext.form.ComboBox({
        anchor : '100%',
        triggerAction: "all",
        forceSelection : true,
        pageSize : 10,
        lastQuery: '',
        store: new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSRuleKey),
        RPCObject : CmFinoFIX.message.JSRuleKey,
        displayField: CmFinoFIX.message.JSRuleKey.Entries.TxnRuleKey._name,
        valueField : CmFinoFIX.message.JSRuleKey.Entries.TxnRuleKey._name,
        listeners: {
        	select : function(field, record) { 
        		var value = field.getValue();
        		for(var i = 0; i < this.store.data.length - 1; i++) {
        			var gridRecord = this.store.getAt(i);
        			if(gridRecord.data && gridRecord.data['TxnRuleKey'] == value) {
        				Ext.ux.Toast.msg(_("Error"), _("The Key is already added. Cannot select the same."),5);
        				field.clearValue();
        				return false;
        			}
        		}
        		/*comparatorEditor.store.reload({
        			params: {
		    			RuleKeyID: record.data.ID
		    		}
        		})*/
        	},
        	scope : this
        }
	});	
	
	Ext.util.Format.comboRenderer = function(combo){
	    return function(value){
	        var record = combo.findRecord(combo.valueField, value);
	        return record ? record.get(combo.displayField) : "";
	    }
	}
	
	if (!localConfig.store) {
		localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTxnRuleAddnInfo);
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
				Ext.MessageBox.confirm(_('Remove Addn Info?'), _('Do you want to remove addn info at row: ' 
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
			text : _('Add Addn Info'),
			handler : this.onAdd.createDelegate(this)
		}],

		sm : new Ext.grid.RowSelectionModel( {
			singleSelect : true
		}),
		
		listeners: {				
			afteredit : function(e) {
				var rowData = e.record.data;
				if(e.field == 'TxnRuleKey') {
					e.record.set('TxnRuleComparator', "");
				}				
			}
	    },

		columns : [
		        this.rowDelete,
		        {
					header : _("Key"),
					dataIndex : CmFinoFIX.message.JSTxnRuleAddnInfo.Entries.TxnRuleKey._name,
					width : 120,
		            editor: this.ruleKeyEditor,
		            renderer: Ext.util.Format.comboRenderer(this.ruleKeyEditor)
				},
				/*{
					header : _("Comparator"),
					dataIndex : CmFinoFIX.message.JSTxnRuleAddnInfo.Entries.TxnRuleComparator._name,
					width : 90,
					editor : comparatorEditor
				},*/
		        {
					header : _("Value"),
					dataIndex : CmFinoFIX.message.JSTxnRuleAddnInfo.Entries.TxnRuleValue._name,
					width : 100,
		            editor: valueEditor
				}
				]
	});

	mFino.widget.TxnRuleAdditionalInfoGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TxnRuleAdditionalInfoGrid, Ext.grid.EditorGridPanel, {
	initComponent : function() {
		this.actions = new Ext.ux.grid.RowActions( {
			header : '',
			keepSelection : true
		});

		mFino.widget.TxnRuleAdditionalInfoGrid.superclass.initComponent.call(this);

	},
	
	onAdd : function() {
		var record = new this.store.recordType();
		var size = this.store.getCount();
		if (this.transactionRuleID) {
			record.data[CmFinoFIX.message.JSTxnRuleAddnInfo.Entries.TransactionRuleID._name] = this.transactionRuleID;			
		}
		this.store.add(record);
	},

	reset : function() {
		this.store.removeAll();
		var ruleKey = this.ruleKeyEditor;
		if(ruleKey) {
			ruleKey.store.removeAll();
		}
		this.store.removed = [];
	},

	setTransactionRuleID : function(transactionRuleID) {
		this.transactionRuleID = transactionRuleID;
		var size = this.store.getCount();
		for ( var i = 0; i < size; i++) {
			var rec = this.store.getAt(i);
			rec.set('TransactionRuleID', transactionRuleID);
		}
	},
					
	reloadGrid : function() {
		this.store.lastOptions = {
			params : {
				start : 0,
				limit : CmFinoFIX.PageSize.Default
			}
		};
		if (this.transactionRuleID) {
			Ext.apply(this.store.lastOptions.params, {
				"TransactionRuleID" : this.transactionRuleID
			});
		}
		this.store.load(this.store.lastOptions);
	},
	
	loadRuleKeys : function(serviceID, transactionTypeID) {		
    	var key_combo = this.ruleKeyEditor;
    	if(key_combo) {
    		key_combo.store.reload({
        		params: {
        			ServiceID: serviceID,
        			TransactionTypeID: transactionTypeID,
        			TxnRuleKeyType: 'Additional'
        		}
        	});	
    	}    	
	},
	
	validateAddnInfoGrid : function() {
		size = this.store.getCount();
		for (var i=0; i < size ; i++) {
			var rec = this.store.getAt(i);
			var ruleKey = rec.data[CmFinoFIX.message.JSTxnRuleAddnInfo.Entries.TxnRuleKey._name];
			var ruleValue = rec.data[CmFinoFIX.message.JSTxnRuleAddnInfo.Entries.TxnRuleValue._name];
			if(ruleKey == undefined || ruleValue == undefined 
					|| ruleKey == "" || ruleValue.trim() == "" ) {
				Ext.ux.Toast.msg(_("Error"), _("Please enter all field values at row: ") + (i+1) + _(" in Addn Info grid"),5);
				return false; // failure				
			}		
		}
		return true; //success
	}	
});

Ext.reg("txnRuleAdditionalInfoGrid", mFino.widget.TxnRuleAdditionalInfoGrid);
