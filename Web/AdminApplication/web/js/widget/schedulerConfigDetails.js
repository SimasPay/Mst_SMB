/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.schedulerConfigDetails = function (config)
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
            items : [        {
                layout: 'form',
                labelWidth : 130,
                items : [
                {
                    xtype : 'displayfield',
                    fieldLabel: _("ScheduleConfig Name"),
                    name: CmFinoFIX.message.JSScheduleTemplate.Entries.Name._name
                },
                {
                    xtype : 'displayfield',
                    fieldLabel: _("ScheduleConfig Mode"),
                    name: CmFinoFIX.message.JSScheduleTemplate.Entries.ModeType._name
                },
				{
                    xtype : 'displayfield',
                    fieldLabel: _("ScheduleConfig Cron"),
                    name: CmFinoFIX.message.JSScheduleTemplate.Entries.Cron._name
                },
				{
                    xtype : 'displayfield',
                    fieldLabel: _("ScheduleConfig Description"),
                    name: CmFinoFIX.message.JSScheduleTemplate.Entries.Description._name
                }
                ]
            }
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 160,
            items : [
             
            ]
        }
    ]
    });

    mFino.widget.schedulerConfigDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.schedulerConfigDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.schedulerConfigDetails.superclass.initComponent.call(this);
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

Ext.reg("schedulerConfigDetails", mFino.widget.schedulerConfigDetails);

