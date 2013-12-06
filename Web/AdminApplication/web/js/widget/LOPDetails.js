/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.LOPDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 150,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _('Giro Ref ID'),
                anchor : '100%',
                name: CmFinoFIX.message.JSLOP.Entries.GiroRefID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('User Name'),
                anchor : '100%',
                name: CmFinoFIX.message.JSLOP.Entries.Username._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Transaction Reference'),
                anchor : '100%',
                name: CmFinoFIX.message.JSLOP.Entries.TransactionID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Paid Amount'),
                anchor : '100%',
                renderer : "money",
                name: CmFinoFIX.message.JSLOP.Entries.ActualAmountPaid._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Commission'),
                anchor : '100%',
                renderer : "percentage",
                name: CmFinoFIX.message.JSLOP.Entries.Commission._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Approval Time'),
                name : CmFinoFIX.message.JSLOP.Entries.ApprovalTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Distribution Time'),
                anchor : '100%',
                renderer : "date",
                name: CmFinoFIX.message.JSLOP.Entries.DistributeTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Creation Time'),
                anchor : '100%',
                renderer : "date",
                name: CmFinoFIX.message.JSLOP.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Update Time'),
                anchor : '100%',
                renderer : "date",
                name: CmFinoFIX.message.JSLOP.Entries.LastUpdateTime._name
            }
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("LOP ID"),
                name: CmFinoFIX.message.JSLOP.Entries.ID._name,
                anchor : '100%'
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('LOP Status'),
                name : CmFinoFIX.message.JSLOP.Entries.Status._name
            },
             {
                xtype : 'displayfield',
                fieldLabel: _('Comment'),
                anchor : '100%',
                name: CmFinoFIX.message.JSLOP.Entries.Comment._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('Value Amount'),
                renderer : "money",
                name : CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Transfer Date'),
                name : CmFinoFIX.message.JSLOP.Entries.TransferDate._name,
                renderer : function(value){
                    if(value){
                        return value.split(' ')[0];
                    }
                }
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Approved By'),
                name: CmFinoFIX.message.JSLOP.Entries.ApprovedBy._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Distributed By'),
                name : CmFinoFIX.message.JSLOP.Entries.DistributedBy._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Created By'),
                name : CmFinoFIX.message.JSLOP.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Updated By'),
                name: CmFinoFIX.message.JSLOP.Entries.UpdatedBy._name
            }
            ]
        }]
    });

    mFino.widget.LOPDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.LOPDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.LOPDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
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
    }
});

Ext.reg("lopdetails", mFino.widget.LOPDetails);

