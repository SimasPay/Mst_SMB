/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SettlementTemplateAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.SettlementTemplateAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SettlementTemplateAddForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
       
        this.items = [
		{
		    xtype : 'textfield',
		    fieldLabel: _("Settlement Name"),
		    itemId : 'sta.form.settlementname',
            labelSeparator:':',
            anchor : '95%',
            allowBlank: false,
            maxLength : 255,
            name: CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementName._name,
            listeners: {
        	    change: function(field) {
        			this.findParentByType('settlementtemplateaddform').onChangeST(field);
                }
            }            
		},                      
        {
            xtype : "combo",
            anchor : '95%',
            fieldLabel :_("Settlement Pocket"),
            allowBlank: false,
			emptyText : _('<select one..>'),
            itemId : 'sta.form.settlementpocket',
            triggerAction: "all",
            forceSelection : true,
            lastQuery: '',
            store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
            displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
            valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
            name: CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementPocket._name
        },
        /*
        {
            xtype: 'enumdropdown',                   
            fieldLabel: _('Settlement Type'),
            allowBlank: false,
            labelSeparator:':',
            itemId : 'st.form.settlementtype',
            anchor:'95%',
            enumId : CmFinoFIX.TagID.SettlementType,
            name : CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementType._name
        }
        */
        {
			xtype : "remotedropdown",
			fieldLabel :_("Schedule Template"),
			itemId : "st.form.settlementnew",
			allowBlank : false,
			editable : false,
			anchor : '95%',					
			emptyText : _('<select one..>'),					
			pageSize : 10,
			store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSScheduleTemplate),					
			displayField: CmFinoFIX.message.JSScheduleTemplate.Entries.Name._name,
			valueField : CmFinoFIX.message.JSScheduleTemplate.Entries.ID._name,
			name : CmFinoFIX.message.JSSettlementTemplate.Entries.ScheduleTemplateID._name,
			hiddenName: CmFinoFIX.message.JSSettlementTemplate.Entries.ScheduleTemplateID._name
        },
        {
			xtype : "remotedropdown",
			fieldLabel :_("Cutoff Time"),
			itemId : "st.form.cutofftime",
			allowBlank : false,
			editable : false,
			anchor : '95%',					
			emptyText : _('<select one..>'),					
			pageSize : 10,
			params:{start:0, limit:10,ModeSearch:3},
			store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSScheduleTemplate),					
			displayField: CmFinoFIX.message.JSScheduleTemplate.Entries.Name._name,
			valueField : CmFinoFIX.message.JSScheduleTemplate.Entries.ID._name,
			name : CmFinoFIX.message.JSSettlementTemplate.Entries.CutoffTime._name,
			hiddenName: CmFinoFIX.message.JSSettlementTemplate.Entries.CutoffTime._name
        }
        
		/*
		 {
            xtype : "combo",
            anchor : '95%',
            fieldLabel :_("Settlement Type"),
            allowBlank: false,
			emptyText : _('<select one..>'),
            itemId : 'st.form.enhancedsettlementtype',
            triggerAction: "all",
            forceSelection : true,
            lastQuery: '',
            store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSScheduleTemplate),
            displayField: CmFinoFIX.message.JSScheduleTemplate.Entries.Name._name,
            valueField : CmFinoFIX.message.JSScheduleTemplate.Entries.ID._name,
            name: CmFinoFIX.message.JSSettlementTemplate.Entries.ScheduleTemplateID._name
        }
		*/
        ];
        mFino.widget.SettlementTemplateAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);        
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
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
    	var sr_combo = this.find('itemId','sta.form.settlementpocket')[0];
    	var statusSearchString = CmFinoFIX.PocketStatus.Initialized + "," + CmFinoFIX.PocketStatus.Active;
    	sr_combo.store.baseParams[CmFinoFIX.message.JSPocket.SubscriberIDSearch._name] = 
    					this.record.get(CmFinoFIX.message.JSSettlementTemplate.SubscriberID._name);
    	sr_combo.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;    	
    	sr_combo.store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;    	
    	sr_combo.store.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = statusSearchString;
    	sr_combo.store.reload({
    		params: {
    			SubscriberIDSearch: this.record.get(CmFinoFIX.message.JSSettlementTemplate.SubscriberID._name),
//    			PocketType: CmFinoFIX.PocketType.BankAccount,
    			StatusSearch: statusSearchString,
    			NoCompanyFilter: true,
    			Commodity: CmFinoFIX.Commodity.Money
    		}
    	});        
        
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSSettlementTemplate.Entries.PocketDispText._name));
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
    
    onChangeST : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementName._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSSettlementTemplateCheck();
            msg.m_pSettlementName = field.getValue();
            msg.m_pPartnerID = this.record.data.PartnerID;
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    
    enableOrDisable : function(value) {
    	var items = ['sta.form.settlementname','sta.form.settlementpocket','st.form.settlementtype'];
        for(var i=0;i<items.length;i++){
        	if (value) {
        		this.find('itemId',items[i])[0].enable();
        	} else {
        		this.find('itemId',items[i])[0].disable();
        	}
        } 
    }
});

Ext.reg("settlementtemplateaddform", mFino.widget.SettlementTemplateAddForm);

