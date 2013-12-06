
Ext.ns("mFino.widget");

mFino.widget.ChannelCodeDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        title: 'Channel Code Details',
        frame : true,
        items: [        {
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Channel Code"),
                name: CmFinoFIX.message.JSChannelCode.Entries.ChannelCode._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("Channel Name"),
                name: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Description"),
                name: CmFinoFIX.message.JSChannelCode.Entries.Description._name
            },
            {
                xtype: 'displayfield',
                fieldLabel: _("Source Application"),
                name: CmFinoFIX.message.JSChannelCode.Entries.ChannelSourceApplicationText._name
            }
            ]
        }
        ]
    });

    mFino.widget.ChannelCodeDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.ChannelCodeDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.ChannelCodeDetails.superclass.initComponent.call(this);
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
Ext.reg("ChannelCodeDetails", mFino.widget.ChannelCodeDetails);