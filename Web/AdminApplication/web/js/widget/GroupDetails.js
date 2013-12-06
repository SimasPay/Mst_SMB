/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.GroupDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.GroupDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.GroupDetails, Ext.FormPanel, {
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
                    fieldLabel: _("Charge Type ID"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSGroup.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Name'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSGroup.Entries.GroupName._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Description'),
                    anchor:'95%',
                    name : CmFinoFIX.message.JSGroup.Entries.Description._name
                }
                ]
            },
            {
                columnWidth:0.5,
                layout: 'form',
                items : [
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Created By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSGroup.Entries.CreatedBy._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Updated By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSGroup.Entries.UpdatedBy._name
                },                
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Creation Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSGroup.Entries.CreateTime._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Last Update Time'),
                    anchor : '95%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSGroup.Entries.LastUpdateTime._name
                }                
                ]
            }
            ]
        }
        ];

        mFino.widget.GroupDetails.superclass.initComponent.call(this);
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

Ext.reg("GroupDetails", mFino.widget.GroupDetails);
