Ext.ns("mFino.widget");

mFino.widget.ChannelCodeAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.ChannelCodeAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChannelCodeAddForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [
        {
            layout: 'form',
            autoHeight: true,
            items : [
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _("Channel Code"),
                itemId : 'ChannelCodeAdd.form.channelcode',
                allowBlank: false,
                blankText : _('Channel Code is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSChannelCode.Entries.ChannelCode._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Channel Name"),
                itemId : 'ChannelCodeAdd.form.channelname',
                allowBlank: false,
                blankText : _('Channel Name is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Description"),
                itemId : 'ChannelCodeAdd.form.description',
                allowBlank: false,
                blankText : _('Description is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSChannelCode.Entries.Description._name
            }
            ]
        }
        ];

        mFino.widget.ChannelCodeAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            if(this.store){
                if(this.record.phantom && !(this.record.store)){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
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

Ext.reg("ChannelCodeAddForm", mFino.widget.ChannelCodeAddForm);