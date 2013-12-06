/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.PocketAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketAddForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
		
        this.items = [
        {
            xtype : "combo",
            fieldLabel :_("Pocket Template"),
            itemId : "pockettemplate",
            allowBlank : false,
            editable : false,
            anchor : '100%',
            triggerAction: "all",
            minChars : 2,
            emptyText : _('<select one..>'),
            forceSelection : true,
            pageSize : 20,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocketTemplate),
            RPCObject : CmFinoFIX.message.JSPocketTemplate,
            displayField: CmFinoFIX.message.JSPocketTemplate.Entries.Description._name,
            valueField : CmFinoFIX.message.JSPocketTemplate.Entries.ID._name,
            name: CmFinoFIX.message.JSPocket.Entries.PocketTemplateID._name,
            listeners : {
                select :  function(field,record){
                    var ptype = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.Type._name);
                    var commodity = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.Commodity._name);
                    var bankCode = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.BankCode._name);
                    this.findParentByType('pocketaddform').onStateDropdown(ptype,commodity,bankCode);
                },
				expand : function(){
					this.findParentByType("pocketaddform").getPocketTemplates();
				}
            }
        },
        {
            xtype: "displayfield",
			itemId : "mdn",
            fieldLabel: _('MDN'),
            name: CmFinoFIX.message.JSPocket.Entries.SubsMDN._name
        },
        {
            xtype: "displayfield",
            fieldLabel: _('User ID'),
            name: CmFinoFIX.message.JSPocket.Entries.SubscriberID._name
        },
        {
            xtype : 'textfield',
            fieldLabel: _("Account Number"),
            itemId:'cardpan',
            disabled : true,
            vtype:'tendigitnumber',
            anchor : '95%',
            name: CmFinoFIX.message.JSPocket.Entries.CardPAN._name
        },
        {
            fieldLabel: _('Card Alias'),
            xtype : 'textfield',
            width : 160,
            itemId:'cardAlias',
            vtype:'numberchar',            
            disabled : true,
            name: CmFinoFIX.message.JSPocket.Entries.CardAlias._name
        },
        {
            xtype: 'fieldset',
            title : _('Pocket Restrictions'),
            layout : 'column',
            autoHeight: true,
            anchor : '90%',
            columns: 2,
            items: [
            {
                columnWidth: 0.5,
                xtype : 'checkbox',
                itemId : "SelfSuspended",
                boxLabel: _(' Self Suspended')
            },
            {
                columnWidth: 0.5,
                xtype : 'checkbox',
                itemId : "Suspended",
                boxLabel: _(' Suspended')
            },
            {
                columnWidth: 0.5,
                xtype : 'checkbox',
                itemId : "SecurityLocked",
                boxLabel: _(' Security Locked')
            },
            {
                columnWidth: 0.5,
                xtype : 'checkbox',
                itemId : "AbsoluteLocked",
                boxLabel: _(' Absolute Locked')
            }
            ]
        },
        {
            xtype : "checkbox",
            fieldLabel: _("Is Default"),
            itemId:'IsDefault',
            name: CmFinoFIX.message.JSPocket.Entries.IsDefault._name
        },
        {
            xtype:'displayfield',
            itemId:'sub.pocket.status',
            fieldLabel: _("Pocket Status"),
            value: 'Initialized'
        }
        ];
        mFino.widget.PocketAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            var resValue = 0;
            if(this.form.items.get("SelfSuspended").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SelfSuspended;
            }
            if(this.form.items.get("Suspended").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.Suspended;
            }
            if(this.form.items.get("SecurityLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SecurityLocked;
            }
            if(this.form.items.get("AbsoluteLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.AbsoluteLocked;
            }
            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSPocket.Entries.Restrictions._name, resValue);
            this.record.endEdit();

            if(this.store){
                if(this.record.phantom
                    && this.store.getAt(0)!= this.record){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },
    onStateDropdown : function(ptype, commodity, bankCode){
        if(ptype == CmFinoFIX.PocketType.BankAccount && commodity == CmFinoFIX.Commodity.Money ||
        		ptype == CmFinoFIX.PocketType.NFC && commodity == CmFinoFIX.Commodity.Money){
            this.getForm().items.get("cardpan").enable();
            this.getForm().items.get("cardpan").allowBlank  = false;
//            this.getForm().items.get("IsDefault").disable();
        } else if(ptype == CmFinoFIX.PocketType.SVA && commodity == CmFinoFIX.Commodity.Money){
        	this.getForm().items.get("cardpan").allowBlank  = true;
            if(bankCode == mFino.auth.getSmartPartnerCode()) {
                this.getForm().items.get("cardpan").disable();
                this.getForm().items.get("cardpan").setValue(null);
            }else {
                this.getForm().items.get("cardpan").enable();
            }
//            this.getForm().items.get("IsDefault").disable();
        } else {
        	this.getForm().items.get("cardpan").allowBlank  = true;
            this.getForm().items.get("cardpan").disable();
            this.getForm().items.get("cardpan").setValue(null);
//            this.getForm().items.get("IsDefault").enable();
        }
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);

        var resValue = record.get(CmFinoFIX.message.JSPocket.Entries.Restrictions._name);

        this.form.items.get("SelfSuspended").setValue( (resValue & CmFinoFIX.SubscriberRestrictions.SelfSuspended) > 0);
        this.form.items.get("Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
        this.form.items.get("SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
        this.form.items.get("AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);

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
    },
	
	 getPocketTemplates: function(){
		var p_combo = this.find('itemId','pockettemplate')[0];
    	p_combo.clearValue();
		p_combo.store.reload({
    		params: {
    			MDNSearch : this.find('itemId','mdn')[0].getValue()
    		}
    	});
    }
});

Ext.reg("pocketaddform", mFino.widget.PocketAddForm);

