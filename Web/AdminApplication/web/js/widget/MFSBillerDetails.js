/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MFSBillerDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.MFSBillerDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSBillerDetails, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
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
                    fieldLabel: _("Biller ID"),
                    anchor : '95%',
                    name: CmFinoFIX.message.JSMFSBiller.Entries.ID._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Biller Name'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerName._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Biller Code'),
                    anchor:'85%',
                    name : CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerCode._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Biller Type'),
                    name: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerType._name,
                    anchor:'85%'
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
                    name: CmFinoFIX.message.JSMFSBiller.Entries.CreatedBy._name
                },
                {
                    xtype: 'displayfield',
                    fieldLabel: _('Updated By'),
                    anchor:'95%',
                    name: CmFinoFIX.message.JSMFSBiller.Entries.UpdatedBy._name
                },                
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Creation Time'),
                    anchor : '100%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSMFSBiller.Entries.CreateTime._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _('Last Update Time'),
                    anchor : '100%',
                    renderer: "date",
                    name: CmFinoFIX.message.JSMFSBiller.Entries.LastUpdateTime._name
                }                
                ]
            }
            ]
        }
        ];

        mFino.widget.MFSBillerDetails.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().record = record;
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

Ext.reg("mfsbillerdetails", mFino.widget.MFSBillerDetails);
