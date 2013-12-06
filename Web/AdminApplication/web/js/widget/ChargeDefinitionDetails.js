/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeDefinitionDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.ChargeDefinitionDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeDefinitionDetails, Ext.FormPanel, {
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
                    fieldLabel: _("Charge Definition ID"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Name'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.Name._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Description'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.Description._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Charge Type'),
                    anchor:'95%',
                    name : CmFinoFIX.message.JSChargeDefinition.Entries.ChargeTypeName._name
                },
                {
                	xtype: 'displayfield',
                    fieldLabel: _("Is Charge From Customer"),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.IsChargeFromCustomer._name,
                    renderer: function(value) {
        				if (value) {
        					return "Yes";
        				} else {
        					return "No";
        				}
        			}
                },                
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Dependant Charge Type"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.DependantChargeTypeName._name
                }                
                ]
            },
            {
                columnWidth:0.5,
                layout: 'form',
                items : [
                {
                 	xtype: 'displayfield',
                    fieldLabel: _("Is Charge Taxable"),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.IsTaxable._name,
                    renderer: function(value) {
         				if (value) {
         					return "Yes";
         				} else {
         					return "No";
         				}
         			}
                },                         
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Created By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.CreatedBy._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Updated By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.UpdatedBy._name
                },                
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Creation Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.CreateTime._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Last Update Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSChargeDefinition.Entries.LastUpdateTime._name
                }                
                ]
            }
            ]
        }
        ];

        mFino.widget.ChargeDefinitionDetails.superclass.initComponent.call(this);
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

Ext.reg("chargedefinitionDetails", mFino.widget.ChargeDefinitionDetails);
