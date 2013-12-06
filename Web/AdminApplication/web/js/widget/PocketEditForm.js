/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketEditForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        autoScroll: true,
        frame:true
    });

    mFino.widget.PocketEditForm.superclass.constructor.call(this, localConfig);   
};

Ext.extend(mFino.widget.PocketEditForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [
        {
            xtype : "displayfield",
            fieldLabel :_("Pocket ID"),
            name: CmFinoFIX.message.JSPocket.Entries.ID._name
        },
        {
            xtype : "combo",
            fieldLabel :_("Pocket Template"),
            itemId : "sub.pocket.template",
            allowBlank : false,
            anchor : '100%',
            triggerAction: "all",
            minChars : 2,
            forceSelection : true,
            pageSize : 20,
            RPCObject : CmFinoFIX.message.JSPocketTemplate,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocketTemplate),
            displayField: CmFinoFIX.message.JSPocketTemplate.Entries.Description._name,
            valueField : CmFinoFIX.message.JSPocketTemplate.Entries.ID._name,
            hiddenName: CmFinoFIX.message.JSPocket.Entries.PocketTemplateID._name,
            name: CmFinoFIX.message.JSPocket.Entries.PocketTemplDescription._name,
            listeners : {
				expand : function(){
					this.findParentByType("pocketeditform").getPocketTemplates();
				}
            }
        },
        {
            xtype: "displayfield",
            fieldLabel: _('MDN'),
            itemId : "sub.pocket.mdn",
            maxLength : 16,
            name: CmFinoFIX.message.JSPocket.Entries.SubsMDN._name
        },
        {
            xtype: "displayfield",
            fieldLabel: _('User ID'),
            maxLength : 255,
            name: CmFinoFIX.message.JSPocket.Entries.SubscriberID._name
        },
        {
            fieldLabel: _('Account Number'),
            xtype : 'textfield',
            width : 160,
            itemId:'sub.pocket.cardpan',
            vtype:'tendigitnumber',            
            disabled : true,
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
            fieldLabel: _('Is Default'),
            xtype : "checkbox",
            itemId : 'IsDefault',
            name: CmFinoFIX.message.JSPocket.Entries.IsDefault._name,
			listeners: {
				check: function(field){
					this.findParentByType('pocketeditform').allowStatusChange(field);
				}
			}
        },
        {
            fieldLabel: _('Pocket Status'),
            xtype:'enumdropdown',
            itemId : 'sub.pocket.status',
            enumId: CmFinoFIX.TagID.PocketStatus,
            name: CmFinoFIX.message.JSPocket.Entries.PocketStatus._name,
            addEmpty: false,
            listeners : {
                select :  function(field){
                    this.findParentByType('pocketeditform').onStatusDropdown(field.getValue());
                }
            }
        }
        ];
        mFino.widget.PocketEditForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    onStatusDropdown : function(status){
    	if (this.find('itemId','IsDefault')[0].checked && (status==CmFinoFIX.PocketStatus.Retired || status==CmFinoFIX.PocketStatus.PendingRetirement) ) {
    		Ext.MessageBox.alert(_("Alert"), _("Default Pocket can't be Retired or Graved"));
    		this.find('itemId','sub.pocket.status')[0].setRawValue(this.record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatusText._name));
    		return;
    	}
    	if(status==CmFinoFIX.PocketStatus.OneTimeActive){
    		Ext.MessageBox.alert(_("Alert"), _("Status cannot be changed to OneTimeActive"));
    		this.find('itemId','sub.pocket.status')[0].setRawValue(this.record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatusText._name));
    		return;
    	}
        if(status==CmFinoFIX.PocketStatus.Retired){
            this.find('itemId','sub.pocket.status')[0].disable();
            this.find('itemId','sub.pocket.template')[0].disable();
            this.find('itemId','sub.pocket.cardpan')[0].disable();
            this.find('itemId','cardAlias')[0].disable();
            this.find('itemId','SelfSuspended')[0].disable();
            this.find('itemId','Suspended')[0].disable();
            this.find('itemId','SecurityLocked')[0].disable();
            this.find('itemId','AbsoluteLocked')[0].disable();
            this.find('itemId','IsDefault')[0].disable();
        }else if(status==CmFinoFIX.PocketStatus.Active){
            var isDefault = this.find('itemId','IsDefault')[0].checked;
    		if(isDefault == true){
    			this.getForm().items.get("sub.pocket.status").disable();
    		}else{
    			this.getForm().items.get("sub.pocket.status").enable();
    		}
            this.find('itemId','sub.pocket.template')[0].enable();
  //          this.find('itemId','sub.pocket.cardpan')[0].enable();
            this.find('itemId','cardAlias')[0].enable();
            this.find('itemId','SelfSuspended')[0].enable();
            this.find('itemId','Suspended')[0].enable();
            this.find('itemId','SecurityLocked')[0].enable();
            this.find('itemId','AbsoluteLocked')[0].enable();
            this.find('itemId','IsDefault')[0].enable();
        }else if(status==CmFinoFIX.PocketStatus.PendingRetirement){
            this.find('itemId','sub.pocket.status')[0].disable();
            this.find('itemId','sub.pocket.template')[0].disable();
            this.find('itemId','sub.pocket.cardpan')[0].disable();
            this.find('itemId','cardAlias')[0].disable();
            this.find('itemId','SelfSuspended')[0].disable();
            this.find('itemId','Suspended')[0].disable();
            this.find('itemId','SecurityLocked')[0].disable();
            this.find('itemId','AbsoluteLocked')[0].disable();
            this.find('itemId','IsDefault')[0].disable();
        }else if(status==CmFinoFIX.PocketStatus.Initialized){
        	//check to see if the 'Initialized' is initial status value or newly selected value. If it is newly selected status we shld not allow to change status to initialiazed. 
        	if(this.record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatus._name) == CmFinoFIX.PocketStatus.Initialized){
        		this.find('itemId','sub.pocket.template')[0].enable();
                this.find('itemId','SelfSuspended')[0].enable();
                this.find('itemId','Suspended')[0].enable();
                this.find('itemId','SecurityLocked')[0].enable();
                this.find('itemId','AbsoluteLocked')[0].enable();
                this.find('itemId','IsDefault')[0].enable();
                this.find('itemId','cardAlias')[0].disable();
        	} else {
        		Ext.MessageBox.alert(_("Alert"), _("Status cannot be changed to Initialized"));
        		this.find('itemId','sub.pocket.status')[0].setRawValue(this.record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatusText._name));
        		return;
        	}        	
        }
    },
    
    allowStatusChange: function(field){
    	if(this.find('itemId','sub.pocket.status')[0].value == CmFinoFIX.PocketStatus.Active && field.checked){
    		this.find('itemId','sub.pocket.status')[0].disable();
    	}
    	else{
    		this.find('itemId','sub.pocket.status')[0].enable();
    	}
    },
    
    disableNotPermittedItems: function(){
        var checkAbleItems = ['sub.pocket.template', 'sub.pocket.cardpan','sub.pocket.status','IsDefault'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
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

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);

        var pocketTemplate = this.find('itemId', 'sub.pocket.template')[0];
        pocketTemplate.setRawValue(record.get(CmFinoFIX.message.JSPocket.Entries.PocketTemplDescription._name));

        var pocketType = record.get(CmFinoFIX.message.JSPocket.Entries.PocketTypeText._name);
        var commodity = record.get(CmFinoFIX.message.JSPocket.Entries.CommodityText._name);
        var partnerCode = record.get(CmFinoFIX.message.JSPocket.Entries.PartnerCode._name);
        
        if(pocketType === "BankAccount" && commodity === "Money"){
            this.getForm().items.get("sub.pocket.cardpan").enable();
            this.getForm().items.get("sub.pocket.cardpan").allowBlank  = false;
        } else if(pocketType === "SVA" && commodity === "Money"){
        	this.getForm().items.get("sub.pocket.cardpan").allowBlank  = true;
            if(partnerCode == mFino.auth.getSmartPartnerCode()) {
                this.getForm().items.get("sub.pocket.cardpan").disable();
            }else {
                this.getForm().items.get("sub.pocket.cardpan").enable();
            }
        } else if(pocketType === "NFC" && commodity === "Money"){
                this.getForm().items.get("sub.pocket.cardpan").disable();
                this.getForm().items.get("sub.pocket.cardpan").allowBlank  = false;
        } 
        else{
        	this.getForm().items.get("sub.pocket.cardpan").allowBlank  = true;
            this.getForm().items.get("sub.pocket.cardpan").disable();
        }
		
     /*   var pocketStatus = record.get(CmFinoFIX.message.JSPocket.Entries.PocketStatus._name);
		if(pocketStatus == CmFinoFIX.PocketStatus.Initialized){
			this.getForm().items.get("sub.pocket.status").disable();
		} */
		
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
		var p_combo = this.find('itemId','sub.pocket.template')[0];
    	p_combo.clearValue();
		p_combo.store.reload({
    		params: {
    			MDNSearch : this.find('itemId','sub.pocket.mdn')[0].getValue()
    		}
    	});
    }
});

Ext.reg("pocketeditform", mFino.widget.PocketEditForm);

