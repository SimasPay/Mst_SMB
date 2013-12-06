/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionRuleDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.TransactionRuleDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionRuleDetails, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 160;
        this.labelPad = 20;
        this.autoScroll = true;
        this.items = [ {
            layout:'column',
            items : [
            {
                columnWidth:0.5,
                layout: 'form',
                items : [
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Transaction Rule ID"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Name'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.Name._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Service Provider"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ServiceProviderName._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Service'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ServiceName._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Transaction Type"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.TransactionName._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Channel'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ChannelName._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Charge Mode"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.ChargeModeText._name
                }
                ]
            },
            {
                columnWidth:0.5,
                layout: 'form',
                items : [
	             {
	                 xtype: 'displayfield',
	                 fieldLabel: _('Source Group'),
	                 anchor:'95%',
	                 name: CmFinoFIX.message.JSTransactionRule.Entries.SourceGroupName._name
	             },
	             {
	                 xtype: 'displayfield',
	                 fieldLabel: _('Destination Group'),
	                 anchor:'95%',
	                 name: CmFinoFIX.message.JSTransactionRule.Entries.DestinationGroupName._name
	             },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Created By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.CreatedBy._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Updated By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSTransactionRule.Entries.UpdatedBy._name
                },                
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Creation Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSTransactionRule.Entries.CreateTime._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Last Update Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSTransactionRule.Entries.LastUpdateTime._name
                }                
                ]
            }
            ]
        }
        ];

        mFino.widget.TransactionRuleDetails.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
    }
});

Ext.reg("transactionruleDetails", mFino.widget.TransactionRuleDetails);
