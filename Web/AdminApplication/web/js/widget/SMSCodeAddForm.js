/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSCodeAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.SMSCodeAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSCodeAddForm, Ext.form.FormPanel, {
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
                xtype : 'textfield',
                fieldLabel: _("SMS Code"),
                allowBlank: false,
                blankText : _('SMS Code is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSSMSCode.Entries.SMSCodeText._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Description"),
                allowBlank: false,
                blankText : _('Description is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSSMSCode.Entries.Description._name
            },
            {
                xtype : 'enumdropdown',
                fieldLabel: _("Service Name"),
                allowBlank: false,
                enumId : CmFinoFIX.TagID.ServiceName,
                blankText : _('Service Name is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSSMSCode.Entries.ServiceName._name
            },
            {
                xtype: 'combo',
                fieldLabel :_('Brand'),
                allowBlank : false,
                anchor : '95%',
                triggerAction: "all",
                minChars : 2,
                forceSelection : true,
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSBrand),
                RPCObject : CmFinoFIX.message.JSBrand,
                displayField: CmFinoFIX.message.JSBrand.Entries.BrandName._name,
                valueField : CmFinoFIX.message.JSBrand.Entries.ID._name,
                name: CmFinoFIX.message.JSSMSCode.Entries.BrandID._name
            },
            {
                xtype : 'enumdropdown',
                fieldLabel: _("Status"),
                allowBlank: false,
                enumId : CmFinoFIX.TagID.SMSCodeStatus,
                blankText : _('Status is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSSMSCode.Entries.SMSCodeStatus._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Short Codes"),
                anchor : '95%',
                vtype : 'smsnumbercomma',
                name : CmFinoFIX.message.JSSMSCode.Entries.ShortCodes._name
            }
            ]
        }
        ];

        mFino.widget.SMSCodeAddForm.superclass.initComponent.call(this);
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

Ext.reg("SMSCodeAddForm", mFino.widget.SMSCodeAddForm);