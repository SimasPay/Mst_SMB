/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketIssuerViewForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.PocketIssuerViewForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketIssuerViewForm, Ext.FormPanel, {
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
                    xtype : 'displayfield',
                    fieldLabel: _("Pocket Template Description"),
                    labelSeparator : ':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.Description._name
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
                    xtype: 'displayfield',
                    fieldLabel: _('Pocket Type'),
                    labelSeparator : ':',
                    anchor:'95%',
                    name : CmFinoFIX.message.JSPocketTemplate.Entries.PocketTypeText._name
                },                
                {
                    xtype: 'displayfield',
                    labelSeparator : ':',
                    fieldLabel: _('Account Category'),
                    anchor:'95%',
                    name : CmFinoFIX.message.JSPocketTemplate.Entries.PocketSubTypeText._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Account Number Suffix Length"),
                    labelSeparator : ':',
                    anchor:'95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CardPANSuffixLength._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Billing Type'),
                    labelSeparator : ':',
                    anchor : '95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.BillingTypeText._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Type of Check'),
                    labelSeparator : ':',
                    anchor : '95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.TypeOfCheckText._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Set As Collector Pocket'),
                    itemId : 'collectorPocket',
                    anchor : '95%'
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Set As Suspense Pocket'),
                    itemId : 'isSuspencePocket',
                    anchor : '95%'
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Set As System Pocket'),
                    itemId : 'isSystemPocket',
                    anchor : '95%'
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
                    xtype: 'displayfield',
                    fieldLabel: _('Denomination'),
                    labelSeparator : ':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.Denomination._name,
                    anchor:'95%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Bank Code For Routing'),
                    labelSeparator : ':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.BankCode._name,
                    anchor:'95%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Pocket Code'),
                    labelSeparator : ':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.PocketCode._name,
                    anchor:'95%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Commodity Type'),
                    labelSeparator : ':',
                    anchor:'95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CommodityTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Operator Code For Routing'),
                    labelSeparator : ':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.OperatorCodeForRoutingText._name,
                    anchor:'95%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Pockets Allowed for MDN'),
                    anchor:'95%',
                    labelSeparator:':',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.NumberOfPocketsAllowedForMDN._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Regular Expression'),
                    labelSeparator : ':',
                    anchor : '95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.RegularExpression._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Interest Rate (%PA)'),
                    labelSeparator : ':',
                    anchor : '95%',
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
            defaults:{
                bodyStyle:'padding:10px'
            },
            items:[
            {
                title: _('Pocket Balance Limit'),
                layout:'form',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    layout: 'form',
                    labelWidth : 100,
                    items :[
                    {
                        xtype: 'displayfield',
                        fieldLabel: _('Maximum '),
                        width: 150,
                        renderer : 'money',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaximumStoredValue._name
                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: _('Minimum '),
                        width: 150,
                        renderer : 'money',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MinimumStoredValue._name
                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: _('Low Balance Notification '),
                        width: 150,
                        renderer : 'money',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.LowBalanceNtfcThresholdAmt._name
                    }
                    ]
                }
                ]
            },
            {
                title: _('Spending Limits'),
                layout:'form',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    layout: 'form',
                    labelWidth : 200,
                    items :[
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Transaction'),
                        anchor : '55%',
                        //       renderer : 'money',
                        itemId : 'maximumpertransaction',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerTransaction._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Minimum Per Transaction'),
                        anchor : '55%',
                        //  renderer : 'money',
                        itemId : 'minimumpertransaction',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MinAmountPerTransaction._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Day'),
                        anchor : '55%',
                        //  renderer : 'money',
                        itemId : 'maximumperday',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerDay._name
                    },{
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Week'),
                        anchor : '55%',
                        //  renderer : 'money',
                        itemId : 'maximumperweek',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerWeek._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Month'),
                        anchor : '55%',
                        //    renderer : 'money',
                        itemId : 'maximumpermonth',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerMonth._name
                    }
                    ]
                }
                ]
            },
            {
                title: _('Transaction Frequency Limits'),
                layout:'form',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    layout: 'form',
                    labelWidth : 200,
                    items :[
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Min Time Between Transactions(millisec)'),
                        anchor : '70%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MinTimeBetweenTransactions._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Day'),
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxTransactionsPerDay._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Week'),
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxTransactionsPerWeek._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Maximum Per Month'),
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.MaxTransactionsPerMonth._name
                    }
                    ]
                }
                ]
            }/*,
            
            {
                title: _('Authorization'),
                layout:'form',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    layout: 'form',
                    labelWidth : 200,
                    items :[
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Merchant Dompet'),
                        itemId : 'merchantDompet',
                        anchor : '55%'
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Registered'),
                        itemId : 'registered',
                        anchor : '55%'
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Cash In Dompet'),
                        itemId : 'cashIn',
                        anchor : '55%'
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Cash Out Dompet'),
                        itemId : 'cashOut',
                        anchor : '55%'
                    }
                    ]
                }
                ]
            },
            {
                title: _('Transaction time intervals'),
                layout:'form',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    layout: 'form',
                    labelWidth : 200,
                    items :[
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Web Time Interval(sec)'),
                        itemId : 'webtimeinterval',
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.WebTimeInterval._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Web Service Time Interval(sec)'),
                        itemId : 'webservicetimeinterval',
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.WebServiceTimeInterval._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('UTK Time Interval(sec)'),
                        itemId : 'utktimeinterval',
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.UTKTimeInterval._name
                    },
                    {
                        xtype : 'displayfield',
                        fieldLabel: _('Bank Channel Time Interval(sec)'),
                        itemId : 'bankchanneltimeinterval',
                        anchor : '55%',
                        name: CmFinoFIX.message.JSPocketTemplate.Entries.BankChannelTimeInterval._name
                    }
                    ]
                }
                ]
            }*/
            ]
        }
        ];

        mFino.widget.PocketIssuerViewForm.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        // the limits are set using renderer money. If we mention renderer:money for the displayfield it is not working properly in the following case.
        //case: Just login and goto the adminpage, click on the view details of any pockettemplate and move to the second tab it is showing empty.
        //now click ok and view the same pockettemplate page move to second tab now we can see the values.
        this.form.items.get("maximumpertransaction").setValue(Ext.util.Format.number(record.get(CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerTransaction._name), '0,000'));
        this.form.items.get("minimumpertransaction").setValue(Ext.util.Format.number(record.get(CmFinoFIX.message.JSPocketTemplate.Entries.MinAmountPerTransaction._name), '0,000'));
        this.form.items.get("maximumperday").setValue(Ext.util.Format.number(record.get(CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerDay._name), '0,000'));
        this.form.items.get("maximumperweek").setValue(Ext.util.Format.number(record.get(CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerWeek._name), '0,000'));
        this.form.items.get("maximumpermonth").setValue(Ext.util.Format.number(record.get(CmFinoFIX.message.JSPocketTemplate.Entries.MaxAmountPerMonth._name), '0,000'));
        var allowance = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.Allowance._name);
        this.form.items.get("merchantDompet").setValue((allowance & CmFinoFIX.PocketAllowance.MerchantDompet)>0?"Y":"N");
        this.form.items.get("registered").setValue((allowance & CmFinoFIX.PocketAllowance.Registered)>0?"Y":"N");
        this.form.items.get("cashIn").setValue((allowance & CmFinoFIX.PocketAllowance.CashInDompet)>0?"Y":"N");
        this.form.items.get("cashOut").setValue((allowance & CmFinoFIX.PocketAllowance.CashOutDompet)>0?"Y":"N");
        var isCollectorPocket = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.IsCollectorPocket._name);
        this.form.items.get("collectorPocket").setValue((isCollectorPocket>0)?"Y":"N");
        var isSuspencePocket = record.get(CmFinoFIX.message.JSPocketTemplate.Entries.IsSuspencePocket._name);
        this.form.items.get("isSuspencePocket").setValue((isSuspencePocket>0)?"Y":"N");
        this.getForm().clearInvalid();  
    }

//    setStore : function(store){
//        this.store = store;
//    }
});


Ext.reg("pocketissuerviewform", mFino.widget.PocketIssuerViewForm);
