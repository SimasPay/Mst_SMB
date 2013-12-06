/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketIssuerDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.PocketIssuerDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketIssuerDetails, Ext.FormPanel, {
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
                    fieldLabel: _("Pocket Template ID"),
                    anchor : '100%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Account Number Suffix Length'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CardPANSuffixLength._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Bank Account Card Type'),
                    anchor:'85%',
                    name : CmFinoFIX.message.JSPocketTemplate.Entries.PocketSubTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Partner Code For Routing'),
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.BankCode._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Pocket Code'),
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.PocketCode._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Set As Collector Pocket'),
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.IsCollectorPocket._name,
                    anchor:'85%',
		            renderer: function(value) {
						if (value) {
							return "Y";
						} else {
							return "N";
						}
					}
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Set As Suspense Pocket'),
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.IsSuspencePocket._name,
                    anchor:'85%',
		            renderer: function(value) {
						if (value) {
							return "Y";
						} else {
							return "N";
						}
					}
                }
                ]
            },
            {
                columnWidth: 0.5,
                layout: 'form',
                labelWidth : 180,
                items : [
                {

                    xtype : 'displayfield',
                    fieldLabel: _('Billing Type'),
                    anchor : '85%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.BillingTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Pocket Type'),
                    anchor:'85%',
                    name : CmFinoFIX.message.JSPocketTemplate.Entries.PocketTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Commodity Type'),
                    anchor:'85%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CommodityTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Operator Code For Routing'),
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.OperatorCodeForRoutingText._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Created By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CreatedBy._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Creation Time'),
                    anchor:'100%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.CreateTime._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Updated By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.UpdatedBy._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Last Update Time'),
                    anchor : '100%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSPocketTemplate.Entries.LastUpdateTime._name
                }]
            } ]
        }
        ];

        mFino.widget.PocketIssuerDetails.superclass.initComponent.call(this);
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

Ext.reg("PocketIssuerDetails", mFino.widget.PocketIssuerDetails);
