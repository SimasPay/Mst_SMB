/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AdjustmentDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.AdjustmentDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentDetails, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 140;
        this.labelPad = 10;
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
                    fieldLabel: _("Adjustment ID"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Adjustment Status'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.AdjustmentStatusText._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Source Pocket"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.SourcePocketTemplateDescription._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Destination Pocket'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.DestPocketTemplateDescription._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Amount"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.Amount._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Applied By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.AppliedBy._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("Applied Time"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.AppliedTime._name
                }
                ]
            },
            {
                columnWidth:0.5,
                layout: 'form',
                items : [
	             {
	                 xtype: 'displayfield',
	                 fieldLabel: _('Sctl ID'),
	                 anchor:'95%',
	                 name: CmFinoFIX.message.JSAdjustments.Entries.SctlId._name
	             },
	             {
	                 xtype: 'displayfield',
	                 fieldLabel: _('Approved/Rejected By'),
	                 anchor:'95%',
	                 name: CmFinoFIX.message.JSAdjustments.Entries.ApprovedOrRejectedBy._name
	             },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Approve/Reject Comment'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.ApproveOrRejectComment._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Approve/Reject Time'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.ApproveOrRejectTime._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Created By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.CreatedBy._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Updated By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSAdjustments.Entries.UpdatedBy._name
                },                
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Creation Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSAdjustments.Entries.CreateTime._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Last Update Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSAdjustments.Entries.LastUpdateTime._name
                }
                ]
            }
            ]
        }
        ];

        mFino.widget.AdjustmentDetails.superclass.initComponent.call(this);
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

Ext.reg("adjustmentDetails", mFino.widget.AdjustmentDetails);
