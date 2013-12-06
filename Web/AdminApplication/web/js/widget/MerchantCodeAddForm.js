/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantCodeAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.MerchantCodeAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantCodeAddForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [
        {
            layout: 'form',
            autoHeight: true,
            items : [
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _("Merchant MDN"),
                allowBlank: false,
                vtype: 'smarttelcophoneAddMore',
                blankText : _('Merchant MDN is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantCode.Entries.MDN._name,
                listeners: {
                    change: function(field) {/*
                    	field.isValid(true);
                    	var valmdn=/^[2]{1}[3]{1}[4]{1}[0-9]{10}$/;
                    	var valmdn1=/^[2]{1}[3]{1}[4]{1}[0-9]{7}$/;
                    	var mdn = field.getValue();
                    	if(mdn.length==13)
                		{
                    		if(!valmdn.test(mdn))
                    		{
                    			field.markInvalid("MDN start with 234");
                    		}
                		}else if(mdn.length>10){
                			field.markInvalid("MDN starting with 234 should be 13 digits or 10 digits");
                		}else if(valmdn1.test(mdn)){
                    		
                			field.markInvalid("MDN should be 13 digits");
                		
                 		}
                */}
                }
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Merchant Code"),
                allowBlank: false,
                blankText : _('Merchant Code is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSMerchantCode.Entries.MerchantCode._name
            }
            ]
        }
        ];

        mFino.widget.MerchantCodeAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSMerchantCode.Entries.CompanyID._name, mFino.auth.getCompanyId());
            this.record.endEdit();
            if(this.store){
                if(this.record.phantom && !(this.record.store)){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);

        this.getForm().clearInvalid();
    },
    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});

Ext.reg("MerchantCodeAddForm", mFino.widget.MerchantCodeAddForm);