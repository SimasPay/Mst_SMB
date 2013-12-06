Ext.ns("mFino.widget");

mFino.widget.ChannelCodeSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('Channel Codes'),
        height:60,
        items:[
        {
            columnWidth:0.2,
            layout:'form',
            labelWidth:80,
            items:[
            {
                xtype:'numberfield',
                allowDecimals:false,
                fieldLabel:'Channel Code',
                anchor:'95%',
                name : CmFinoFIX.message.JSChannelCode.ChannelCodeSearch._name,
                listeners : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.2,
            layout:'form',
            labelWidth:80,
            items:[
            {
                xtype:'textfield',
                fieldLabel:'Channel Name',
                anchor:'95%',
                name : CmFinoFIX.message.JSChannelCode.ChannelNameSearch._name,
                listeners : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.05,
            layout:'form',
            labelWidth:50,
            items:[
            {
                xtype:'displayfield',
                anchor:'90%'
            }
            ]
        },{
            columnWidth:0.2,
            layout:'form',
            items:[
            {
                xtype:'button',
                text:'Search',
                anchor:'60%',
                handler : this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });

    mFino.widget.ChannelCodeSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChannelCodeSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.ChannelCodeSearchForm.superclass.initComponent.call(this);
        this.addEvents("channelCodeSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("channelCodeSearch", values);
    }
});
Ext.reg('ChannelCodeSearchForm',mFino.widget.ChannelCodeSearchForm);