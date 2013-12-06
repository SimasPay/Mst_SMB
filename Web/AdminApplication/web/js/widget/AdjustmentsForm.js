/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AdjustmentsForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.AdjustmentsForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsForm, Ext.FormPanel, {
    initComponent : function () {
    	this.store = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSAdjustments),
        this.labelWidth = 200;
        this.labelPad = 20;
        this.items = [
        {
            layout:'column',
            items : [
            {
                columnWidth: 0.5,
                layout: 'form',
                labelWidth : 100,
                labelPad : 1,
                items : [                
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Source Pocket"),
                    allowBlank: false,
                    addEmpty: false,
                    emptyText : _('<select one..>'),
                    itemId : 'adjustments.form.sourcepocket',
                    RPCObject : CmFinoFIX.message.JSAdjustmentsPocket,
                    displayField: CmFinoFIX.message.JSAdjustmentsPocket.Entries.PocketTemplateDescription._name,
                    valueField : CmFinoFIX.message.JSAdjustmentsPocket.Entries.PocketID._name,
                    name: CmFinoFIX.message.JSAdjustments.Entries.SourcePocketID._name,
                    hiddenName: CmFinoFIX.message.JSAdjustments.Entries.SourcePocketID._name,
                    listeners: {
                    	select: function(field) {
                    		var destPocketField = this.findParentByType('adjustmentsForm').find("itemId", "adjustments.form.destpocket")[0]
                    		if(destPocketField.getValue() == field.getValue()) {
                    			Ext.ux.Toast.msg(_("Error"), _("Source and Destination cannot be same"));
                    			field.reset();
                    		}
                    	}
                    }
                },                
                {
                    xtype : "remotedropdown",
                    anchor : '95%',
                    fieldLabel :_("Destination Pocket"),
                    allowBlank: false,
                    addEmpty: false,
                    emptyText : _('<select one..>'),
                    itemId : 'adjustments.form.destpocket',
                    RPCObject : CmFinoFIX.message.JSAdjustmentsPocket,
                    displayField: CmFinoFIX.message.JSAdjustmentsPocket.Entries.PocketTemplateDescription._name,
                    valueField : CmFinoFIX.message.JSAdjustmentsPocket.Entries.PocketID._name,
                    name: CmFinoFIX.message.JSAdjustments.Entries.DestPocketID._name,
                    hiddenName: CmFinoFIX.message.JSAdjustments.Entries.DestPocketID._name,
                    listeners: {
                    	select: function(field) {
                    		var sourcePocketField = this.findParentByType('adjustmentsForm').find("itemId", "adjustments.form.sourcepocket")[0]
                    		if(sourcePocketField.getValue() == field.getValue()) {
                    			Ext.ux.Toast.msg(_("Error"), _("Source and Destination cannot be same"));
                    			field.reset();
                    		}
                    	}
                    }
                },
                {
                    xtype : 'numberfield',
                    fieldLabel: _("Amount"),
                    itemId : 'adjustments.form.amount',
                    labelSeparator:':',
                    anchor : '95%',
                    allowBlank: false,
                    allowDecimals:true,
                    decimalPrecision : 2,
                    name: CmFinoFIX.message.JSAdjustments.Entries.Amount._name
                }]
            },
            {
                columnWidth: 0.5,
                layout: 'form',
                labelWidth : 100,
                labelPad : 1,
                items : [{
		                    xtype : 'numberfield',
		                    fieldLabel: _("Type"),
		                    itemId : 'adjustments.form.type',
		                    labelSeparator:':',
		                    anchor : '95%',
		                    allowDecimals: false,
		                    name: CmFinoFIX.message.JSAdjustments.Entries.AdjustmentType._name
		                },
		                {
		                    xtype : 'textarea',
		                    fieldLabel: _("Description"),
		                    itemId : 'adjustments.form.description',
		                    labelSeparator:':',
		                    anchor : '95%',
		                    name: CmFinoFIX.message.JSAdjustments.Entries.Description._name
		                }
		                ]            
            }
          ]
        }
        ];
        this. buttons = [{
            text: _('Submit'),
            handler : this.save.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        mFino.widget.AdjustmentsForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);        
    }, 
    
    save : function(){
        if(this.getForm().isValid()){
        	var values = this.getForm().getValues();
        	Ext.applyIf(values, { SctlId : this.sctlRecord.get('ID') });
        	this.fireEvent("search", values);
        }
    },
    
    resetHandler : function(){
        this.getForm().reset();
    },
    
    getSourcePockets : function(field) {
    	var sr_combo = this.find('itemId','adjustments.form.sourcepocket')[0];
    	sr_combo.clearValue();
    	sr_combo.store.reload({
    		params: {
    			SctlId : this.sctlRecord.get('ID')
    			}
    	});
    	return sr_combo;
    },
    
    getDestPockets : function(field) {
    	var dest_combo = this.find('itemId','adjustments.form.destpocket')[0];
    	dest_combo.clearValue();
    	dest_combo.store.reload({
    		params: {
    			SctlId : this.sctlRecord.get('ID')
    			}
    	});
    	return dest_combo;
    },
    
    setEditable : function(isEditable){
        if(isEditable === undefined || isEditable){
            this.items.each(function(item) {
            	//enable the item
                item.enable();
            });
            Ext.each(this.buttons,function(button){
            	button.enable();
        	});
        }else{
            this.items.each(function(item) {
            	//Disable the item
                item.disable();
            });
            Ext.each(this.buttons,function(button){
            	button.disable();
        	});
        }
    },
    
    setRecord : function(sctlRecord){
        this.getForm().reset();
        this.sctlRecord = sctlRecord; // sctlRecord will hold the values of AdjustmentsChargeTransactionsGrid entry
        							  // The sctlId and status of the entry are used to load source, dest pocket combos, hence set here.
    	var sr_combo = this.getSourcePockets();
    	var dest_combo = this.getDestPockets();
    }
});

Ext.reg("adjustmentsForm", mFino.widget.AdjustmentsForm);