/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketTemplateConfigIssuerDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.PocketTemplateConfigIssuerDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTemplateConfigIssuerDetails, Ext.FormPanel, {
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
                    fieldLabel: _("Pocket Template Config ID"),
                    anchor : '100%',
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Subscriber Type'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.SubscriberTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Business Partner Type'),
                    anchor:'85%',
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.BusinessPartnerTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('KYCLevel'),
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.KYCLevelText._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('IsDefault'),
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsDefault._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Group'),
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupName._name,
                    anchor:'85%'                    	
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
                    fieldLabel: _('Pocket Template ID'),
                    anchor : '85%',
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateID._name
                },
				{

                    xtype : 'displayfield',
                    fieldLabel: _('Pocket Template Description'),
                    anchor : '85%',
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTemplateDescription._name
                },
				{
                    xtype: 'displayfield',
                    fieldLabel: _('Commodity Type'),
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.CommodityTypeText._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Pocket Type'),
                    anchor:'85%',
                    name : CmFinoFIX.message.JSPocketTemplateConfig.Entries.PocketTypeText._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Is Collector Pocket'),
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsCollectorPocket._name,
                    anchor:'85%'
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Is Suspense Pocket'),
                    name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.IsSuspencePocket._name,
                    anchor:'85%'
                }]
            } ]
        }
        ];

        mFino.widget.PocketTemplateConfigIssuerDetails.superclass.initComponent.call(this);
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

Ext.reg("PocketTemplateConfigIssuerDetails", mFino.widget.PocketTemplateConfigIssuerDetails);
