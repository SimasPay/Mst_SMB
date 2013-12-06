/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RoleDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        //id : "userdetails",
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            items : [
			{
			    xtype : 'displayfield',
			    fieldLabel: _("Role ID"),
			    name: CmFinoFIX.message.JSRole.Entries.ID._name,
			    anchor : '75%'
			},
            {
                xtype : 'displayfield',
                fieldLabel: _("Rolename"),
                name: CmFinoFIX.message.JSRole.Entries.DisplayText._name,
                anchor : '75%'
            },            
            {
                xtype : 'displayfield',
                fieldLabel: _('Is System Role'),
                anchor : '75%',
                name: CmFinoFIX.message.JSRole.Entries.IsSystemUser._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Priority Level'),
                enumId : CmFinoFIX.TagID.Language,
                name : CmFinoFIX.message.JSRole.Entries.PriorityLevel._name
            }            
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            items : [            
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSRole.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Create on'),
                renderer : 'date',                
                name: CmFinoFIX.message.JSRole.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSRole.Entries.UpdatedBy._name
            },            
            {
                xtype: "displayfield",
                fieldLabel: _('Last Modified on'),
                anchor : '100%',
                renderer : 'date',
                name: CmFinoFIX.message.JSRole.Entries.LastUpdateTime._name
            }
            ]
        }
        ]
    });

    mFino.widget.RoleDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RoleDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.RoleDetails.superclass.initComponent.call(this);
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

Ext.reg("roledetails", mFino.widget.RoleDetails);