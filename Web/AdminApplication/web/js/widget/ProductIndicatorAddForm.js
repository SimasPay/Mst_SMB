/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ProductIndicatorAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.ProductIndicatorAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ProductIndicatorAddForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 110;
        this.labelPad = 20;

        this.items = [ 
        {
            layout: 'form',
            autoHeight: true,
            items : [
            {
                xtype : "enumdropdown",
                anchor : '95%',
                itemId : "transactiontype",
                fieldLabel :_('Transaction Type'),
                emptyText : _('<select one..>'),
                enumId : CmFinoFIX.TagID.TransactionUICategory,
                name: CmFinoFIX.message.JSProductIndicator.Entries.TransactionUICategory._name,
                allowBlank: false,
                blankText : _('Transaction Type is required')
            },
            {
                xtype : "combo",
                fieldLabel: _('Channel Name'),
                itemId : "source",
                anchor : '95%',
                triggerAction: "all",
                minChars : 2,
                allowBlank: false,
                forceSelection : true,
                pageSize : 20,
                addEmpty : true,
                emptyText : _('<select one..>'),
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSChannelCode),
                displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                valueField : CmFinoFIX.message.JSChannelCode.Entries.ChannelSourceApplication._name,
                name: CmFinoFIX.message.JSProductIndicator.Entries.ChannelSourceApplication._name,
                blankText : _('Channel is required')
            },
            {
                xtype : "displayfield",
                anchor : '95%',
                fieldLabel :_('Company Name'),
                value: mFino.auth.getCompanyName()
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Requestor ID"),
                itemId : "requestorid",
                name: CmFinoFIX.message.JSProductIndicator.Entries.RequestorID._name,
                anchor : '95%'
            },
            {
                xtype : 'textfield',
                fieldLabel: _('Product Description'),
                itemId : "productdescription",
                name: CmFinoFIX.message.JSProductIndicator.Entries.ProductDescription._name,
                anchor : '95%'
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Channel Text"),
                itemId : "channeltext",
                name: CmFinoFIX.message.JSProductIndicator.Entries.ChannelText._name,
                anchor : '95%'
            },
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _('Product Indicator'),
                itemId : "productcode",
                name: CmFinoFIX.message.JSProductIndicator.Entries.ProductIndicatorCode._name,
                allowBlank: false,
                blankText : _('Product Code is required'),
                anchor : '95%'
            }
            ]
        }
        ];

        mFino.widget.ProductIndicatorAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSProductIndicator.Entries.CompanyID._name, mFino.auth.getCompanyId());
            this.record.endEdit();
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

Ext.reg("ProductIndicatorAddForm", mFino.widget.ProductIndicatorAddForm);