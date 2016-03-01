/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketIssuerForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    mFino.widget.PocketIssuerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketIssuerForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 200;
        this.labelPad = 20;
        this.items = [
        {
            layout:'column',
            items : [
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 175,
                labelPad : 5,
                items : [
                {
                    xtype : 'textfield',
                    fieldLabel: _("Pocket Template Description"),
                    itemId : 'pocketissuer.form.desc',
                    labelSeparator:':',
                    width: 565,
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.Description._name,
                    listeners: {
                        change: function(field) {
                            this.findParentByType('pocketissuerform').onChangePT(field);
                        }
                    }
                }
                ]
            },
            {
                columnWidth: 0.5,
                layout: 'form',
                labelWidth : 175,
                labelPad : 5,
                items :
                [
                {
                    xtype: 'enumdropdown',                   
                    fieldLabel: _('Pocket Type'),
                    itemId : 'pocketType',
                    labelSeparator:':',
                    emptyText : _('<select one..>'),
                    anchor:'95%',
                    allowBlank: false,
                    addEmpty : false,
                    editable: false,
                    enumId : CmFinoFIX.TagID.PocketType,
                    name : CmFinoFIX.message.JSPocketTemplate.Entries.Type._name,
                    listeners: {
                	select: function() {
                            this.findParentByType('pocketissuerform').onChangeType();
                        }
                    }
                },
                {
                    xtype: 'enumdropdown',                   
                    fieldLabel: _('Account Category'),
                    itemId : 'bankAccount',
                    allowBlank: false,
					labelSeparator:':',
                    anchor:'95%',
                    enumId : CmFinoFIX.TagID.BankAccountCardType,
                    name : CmFinoFIX.message.JSPocketTemplate.Entries.BankAccountCardType._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _("Account Number Suffix Length"),
                    itemId : 'pocket.form.suffix',
                    labelSeparator:':',
                    anchor:'95%',
                    maxLength : 2,
                    maxValue: 19,
                    minValue:0,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CardPANSuffixLength._name
                },
                {
//                    xtype : 'enumdropdown',
                	xtype : 'hidden',
                    fieldLabel: _('Billing Type'),
                    anchor : '95%',
                    labelSeparator:':',
                    itemId : 'billingType',
                    enumId : CmFinoFIX.TagID.BillingType,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.BillingType._name
                },
                {
                    xtype : 'enumdropdown',
                    fieldLabel: _('Type of Check'),
                    anchor : '95%',
                    labelSeparator:':',
                    itemId : 'typeofcheck',
                    allowBlank: false,
                    enumId : CmFinoFIX.TagID.TypeOfCheck,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.TypeOfCheck._name,
                    listeners:{
                        select: function(){
                            this.findParentByType("pocketissuerform").enableOrDisableRE();
                        }
                    }
                },
                {
                    xtype:'checkbox',
                    fieldLabel: _('Set As Collector Pocket'),
                    width:150,
                    itemId:'collectorPocket',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.IsCollectorPocket._name
                },
                {
                    xtype:'checkbox',
                    fieldLabel: _('Set As Suspense Pocket'),
                    width:150,
                    itemId:'isSuspencePocket',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.IsSuspencePocket._name
                },
                {
                    xtype:'checkbox',
                    fieldLabel: _('Set As System Pocket'),
                    width:150,
                    itemId:'isSystemPocket',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.IsSystemPocket._name
                }                 
                ]
            },
            {
                columnWidth: 0.5,
                layout: 'form',
                labelWidth : 175,
                labelPad : 5,                
                items :
                [
                {
//                    xtype: 'numberfield',
                	xtype: 'hidden',
                    fieldLabel: _('Denomination'),
                    itemId:'denomination',
                    minValue:1,
                    //                    allowBlank: false,
                    labelSeparator : ':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.Denomination._name,
                    anchor:'95%'
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: _('Partner Code For Routing'),
                    anchor:'95%',
                    labelSeparator:':',
                    disabled : true,
                    allowBlank: false,
                    itemId : 'partnerCode',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.BankCode._name
                },
                {
                    xtype: 'textfield',
                    fieldLabel: _('Pocket Code'),
                    anchor:'95%',
                    labelSeparator:':',
                    itemId : 'pocketCode',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.PocketCode._name
                },
                {
                    xtype: 'enumdropdown',
                    fieldLabel: _('Commodity Type'),
                    anchor:'95%',
                    labelSeparator:':',
                    emptyText : _('<select one..>'),
                    addEmpty : false,
                    itemId : 'commodityType',
                    allowBlank: false,
                    enumId : CmFinoFIX.TagID.Commodity,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.Commodity._name,
                    listeners:{
                        select: function(){
                            this.findParentByType("pocketissuerform").enableOrDisablePartnerCode();
                        }
                    }
                },
                {
//                    xtype: 'enumdropdown',
                	xtype: 'hidden',
                    fieldLabel: _('Operator Code For Routing'),
                    anchor:'95%',
                    labelSeparator:':',
                    itemId : 'operatorCode',
                    enumId : CmFinoFIX.TagID.OperatorCodeForRouting,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.OperatorCode._name
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: _('Pockets Allowed for MDN'),
                    anchor:'95%',
                    labelSeparator:':',
                    allowBlank: false,
                    itemId : 'pocketcount',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.NumberOfPocketsAllowedForMDN._name
                },
                {
                    xtype: 'textfield',
                    fieldLabel: _('Regular Expression'),
                    itemId:'regexp',
                    anchor:'95%',
                    labelSeparator:':',
                    vtype:'RE',
                    disabled : true,
                    allowBlank: false,
                    emptyText:_('Ex:^[0-9]{0,14}$ 14 digit no'),
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.RegularExpression._name
                },
                {
                    xtype: 'textfield',
                    fieldLabel: _('Interest Rate (%PA)'),
                    itemId:'interestRate',
                    anchor:'95%',
                    labelSeparator:':',
                    allowBlank: false,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.InterestRate._name
                }
                ]
            }
            ]
        },
        {
            xtype:'tabpanel',
            itemId:'tabelpanelPocketTemplate',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            defaults:{
                layout:'form',
                bodyStyle:'padding:10px'
            },
            items:[
            {
                title: _('Pocket Balance Limit'),
                frame:true,
                autoHeight: true,
                width : 840,
                items :[
                {
                    xtype: 'numberfield',
                    fieldLabel: _('Maximum '),
                    width: 150,
                    vtype:'number16',
                    itemId : 'pocket.form.pocbalmaxi',
                    maxLength : 16,
                    allowBlank: false,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaximumStoredValue._name
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: _('Minimum '),
                    vtype:'signedNumber16',
                    itemId : 'pocket.form.pocbalmini',
                    maxLength : 16,
                    width: 150,
                    allowBlank: false,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MinimumStoredValue._name
                },
                {
                    xtype:'checkbox',
                    fieldLabel: _('Low Balance Notification Needed'),
                    width:150,
                    itemId:'lowBalNotichecked',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNotificationEnabled._name,
                    listeners: {
                        check: function() {
                            this.findParentByType("pocketissuerform").oncheckclick();
                        }
                    }
                },
                {
                    xtype: 'numberfield',
                    fieldLabel: _('Low Balance Notification '),
                    itemId:'lowBalNoti',
                    maxLength : 16,
                    width: 150,
                    hidden : true,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNtfcThresholdAmt._name
                }
                ]
            },
            {
                title: _('Spending Limits'),
                frame:true,
                autoHeight: true,
                width : 840,
                items :[
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Transaction'),
                    allowBlank: false,
                    anchor : '55%',
                    itemId : 'pocket.form.maximum',
                    vtype:'number16',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerTransaction._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Minimum Per Transaction'),
                    allowBlank: false,
                    anchor : '55%',
                    itemId : 'pocket.form.minimum',
                    vtype:'number16',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MinAmountPerTransaction._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Day'),
                    vtype:'number16',
                    itemId : 'pocket.form.maximumDay',
                    allowBlank: false,
                    anchor : '55%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerDay._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Week'),
                    allowBlank: false,
                    itemId : 'pocket.form.maximumWeek',
                    anchor : '55%',
                    vtype:'number16',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerWeek._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Month'),
                    allowBlank: false,
                    itemId : 'pocket.form.maximumMonth',
                    anchor : '55%',
                    vtype:'number16',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerMonth._name
                }
                ]
            },
            {
                title: _('Transaction Frequency Limits'),
                frame:true,
                autoHeight: true,
                width : 840,
                items :[
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Min Time Between Transactions(millisec)'),
                    allowBlank: false,
                    anchor : '55%',
                    maxValue:2000000000,
                    minValue:0,
                    itemId : 'pocket.form.minTime',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MinTimeBetweenTransactions._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Day'),
                    allowBlank: false,
                    anchor : '55%',
                    itemId : 'pocket.form.maximumDayLimits',
                    maxValue:2000000000,
                    minValue:0,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxTransactionsPerDay._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Week'),
                    allowBlank: false,
                    itemId : 'pocket.form.maximumWeekLimit',
                    anchor : '55%',
                    maxValue:2000000000,
                    minValue:0,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxTransactionsPerWeek._name
                },
                {
                    xtype : 'numberfield',
                    allowDecimals:false,
                    fieldLabel: _('Maximum Per Month'),
                    allowBlank: false,
                    anchor : '55%',
                    itemId : 'pocket.form.maximumMonthLimit',
                    maxValue:2000000000,
                    minValue:0,
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxTransactionsPerMonth._name
                }
                ]
            }
//            ,
//            {
//                title: _('Authorization'),
//                frame:true,
//                autoHeight: true,
//                width : 840,
//                items :[
//                {
//                    xtype : 'checkbox',
//                    fieldLabel: _('Merchant Dompet'),
//                    itemId : 'merchantDompet',
//                    anchor : '55%'
//                },
//                {
//                    xtype : 'checkbox',
//                    fieldLabel: _('Registered'),
//                    itemId : 'registered',
//                    anchor : '55%'
//                },
//                {
//                    xtype : 'checkbox',
//                    fieldLabel: _('Cash In Dompet'),
//                    itemId : 'cashIn',
//                    anchor : '55%'
//                },
//                {
//                    xtype : 'checkbox',
//                    fieldLabel: _('Cash Out Dompet'),
//                    itemId : 'cashOut',
//                    anchor : '55%'
//                }
//                ]
//            },
//            {
//                title: _('Transaction time intervals'),
//                layout:'form',
//                frame:true,
//                autoHeight: true,
//                width : 840,
//                items:[
//                {
//                    xtype: 'panel',
//                    layout: 'form',
//                    labelWidth : 200,
//                    items :[
//                    {
//                        xtype : 'numberfield',
//                        allowDecimals:false,
//                        fieldLabel: _('Web Time Interval(sec)'),
//                        itemId : 'webtimeinterval',
//                        maxValue:20000,
//                        minValue:0,
//                        anchor : '55%',
//                        name: CmFinoFIX.message.JSPocketTemplate.Entries.WebTimeInterval._name
//                    },
//                    {
//                        xtype : 'numberfield',
//                        allowDecimals:false,
//                        fieldLabel: _('Web Service Time Interval(sec)'),
//                        itemId : 'webservicetimeinterval',
//                        maxValue:20000,
//                        minValue:0,
//                        anchor : '55%',
//                        name: CmFinoFIX.message.JSPocketTemplate.Entries.WebServiceTimeInterval._name
//                    },
//                    {
//                        xtype : 'numberfield',
//                        allowDecimals:false,
//                        fieldLabel: _('UTK Time Interval(sec)'),
//                        itemId : 'utktimeinterval',
//                        maxValue:20000,
//                        minValue:0,
//                        anchor : '55%',
//                        name: CmFinoFIX.message.JSPocketTemplate.Entries.UTKTimeInterval._name
//                    },
//                    {
//                        xtype : 'numberfield',
//                        allowDecimals:false,
//                        fieldLabel: _('Bank Channel Time Interval(sec)'),
//                        itemId : 'bankchanneltimeinterval',
//                        maxValue:20000,
//                        minValue:0,
//                        anchor : '55%',
//                        name: CmFinoFIX.message.JSPocketTemplate.Entries.BankChannelTimeInterval._name
//                    }
//                    ]
//                }
//                ]
//            }
            ]
        }];
        mFino.widget.PocketIssuerForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.on("render", function(){
            this.setEditable(this.initialConfig.isEditable);
        });
    },

    oncheckclick: function(){
        if(this.form.items.get("lowBalNotichecked").checked) {
            this.form.items.get("lowBalNoti").setVisible(true);
        } else {
            this.form.items.get("lowBalNoti").reset();
            this.form.items.get("lowBalNoti").setVisible(false);
        }
    },
    disableFields : function(){
        var itemIds = ['pocketType','billingType','bankAccount','operatorCode','commodityType','typeofcheck','denomination'];

        for(var i=0; i<itemIds.length; i++){
            this.form.items.get(itemIds[i]).disable();
        }
    },
    enableFields : function(){
        var itemIds = ['pocketType','billingType','bankAccount','operatorCode','commodityType', 'denomination'];

        for(var i=0; i<itemIds.length; i++){
            this.form.items.get(itemIds[i]).enable();
        }
    },
    enableOrDisableRE: function() {
        var value = this.form.items.get("typeofcheck").getValue();
        if(value==CmFinoFIX.TypeOfCheck.RegularExpressionCheck) {
            this.form.items.get('regexp').enable();
        } else {
            this.form.items.get('regexp').setValue("");
            this.form.items.get('regexp').disable();
        }
    },
    enableOrDisablePartnerCode: function() {
        var value = this.form.items.get("commodityType").getValue();
        if(value==CmFinoFIX.Commodity.Money) {
            this.form.items.get('partnerCode').enable();
        } else {
            this.form.items.get('partnerCode').setValue("");
            this.form.items.get('partnerCode').disable();
        }
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            var allowance =0;
//            if(this.form.items.get("merchantDompet").checked){
//                allowance  = allowance + CmFinoFIX.PocketAllowance.MerchantDompet;
//            }
//            if(this.form.items.get("registered").checked){
//                allowance = allowance + CmFinoFIX.PocketAllowance.Registered;
//            }
//            if(this.form.items.get("cashIn").checked){
//                allowance = allowance + CmFinoFIX.PocketAllowance.CashInDompet;
//            }
//            if(this.form.items.get("cashOut").checked){
//                allowance = allowance + CmFinoFIX.PocketAllowance.CashOutDompet;
//            }

            this.record.beginEdit();
            if (!(this.form.items.get("collectorPocket").checked)) {
            	this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.IsCollectorPocket._name,  false);
            }
            if (!(this.form.items.get("isSuspencePocket").checked)) {
            	this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.IsSuspencePocket._name,  false);
            }
            if(this.form.items.get("lowBalNotichecked").checked) {
                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNotificationEnabled._name,  true);
                if(this.form.items.get("lowBalNoti").getValue())
                {
                    this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNtfcThresholdAmt._name, this.form.items.get("lowBalNoti").getValue() );
                }
                else if(!this.form.items.get("lowBalNoti").getValue()){
                    this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNtfcThresholdAmt._name, 0 );
                }
            } else {
                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNotificationEnabled._name,  false);
                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNtfcThresholdAmt._name,  0);
            }
//            if(this.form.items.get("webtimeinterval").getValue())
//            {
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.WebTimeInterval._name, this.form.items.get("webtimeinterval").getValue() );
//            }
//            else{
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.WebTimeInterval._name, 0 );
//            }
//            if(this.form.items.get("bankchanneltimeinterval").getValue())
//            {
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.BankChannelTimeInterval._name, this.form.items.get("bankchanneltimeinterval").getValue() );
//            }
//            else{
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.BankChannelTimeInterval._name, 0 );
//            }
//            if(this.form.items.get("webservicetimeinterval").getValue())
//            {
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.WebServiceTimeInterval._name, this.form.items.get("webservicetimeinterval").getValue() );
//            }
//            else{
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.WebServiceTimeInterval._name, 0 );
//            }
//            if(this.form.items.get("utktimeinterval").getValue())
//            {
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.UTKTimeInterval._name, this.form.items.get("utktimeinterval").getValue() );
//            }
//            else{
//                this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.UTKTimeInterval._name, 0 );
//            }
            this.record.set(CmFinoFIX.message.JSPocketTemplate.Entries.Allowance._name, allowance);
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
        var resValue = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNotificationEnabled._name);
        if(resValue ===1) {
            this.form.items.get("lowBalNotichecked").setActive(true);
        }
//
        var allowance = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.Allowance._name);
//        this.form.items.get("merchantDompet").setValue((allowance & CmFinoFIX.PocketAllowance.MerchantDompet) >0);
//        this.form.items.get("registered").setValue((allowance & CmFinoFIX.PocketAllowance.Registered) >0);
//        this.form.items.get("cashIn").setValue((allowance & CmFinoFIX.PocketAllowance.CashInDompet) >0);
//        this.form.items.get("cashOut").setValue((allowance & CmFinoFIX.PocketAllowance.CashOutDompet) >0);

        this.getForm().clearInvalid();
    },

    onChangePT : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSPocketTemplate.Entries.Description._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSPocketTemplateCheck();
            msg.m_pPocketTemplateName = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    onChangeType : function(){
    	 var value = this.form.items.get("pocketType").getValue();
      if(Number(value) === CmFinoFIX.PocketType.BankAccount){
    	  this.form.items.get('bankAccount').enable();
    	  this.form.items.get('typeofcheck').enable();
    	  
        }else{
        	 this.form.items.get('bankAccount').disable();
        	 this.form.items.get('typeofcheck').disable();
        	 this.form.items.get('typeofcheck').setValue(CmFinoFIX.TypeOfCheck.None);
        	 this.form.items.get('bankAccount').setValue("");
        	 
        }
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

    setEditable : function(isEditable){
        if(isEditable){
            this.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield'|| item.getXType()=='checkbox'||item.getXType()=='checkboxgroup'||item.getXType()=='enumdropdown') {
                    //enable the item
                    item.enable();
                }
            });
        }else{
            this.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield'|| item.getXType()=='checkbox'||item.getXType()=='checkboxgroup'||item.getXType()=='enumdropdown') {
                    //Disable the item
                    item.disable();
                }
            });
        }
    }
});

Ext.reg("pocketissuerform", mFino.widget.PocketIssuerForm);
