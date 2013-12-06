/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BrandAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.BrandAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BrandAddForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 120;
        this.labelPad = 20;

        this.items = [
        {
            layout: 'form',
            autoHeight: true,
            items : [
            //            {
            //                xtype: 'combo',
            //                fieldLabel :_('Company Name'),
            //                allowBlank : false,
            //                anchor : '95%',
            //                triggerAction: "all",
            //                minChars : 2,
            //                forceSelection : true,
            //                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSCompany),
            //                RPCObject : CmFinoFIX.message.JSCompany,
            //                displayField: CmFinoFIX.message.JSCompany.Entries.CompanyName._name,
            //                valueField : CmFinoFIX.message.JSCompany.Entries.ID._name,
            //                name: CmFinoFIX.message.JSBrand.Entries.CompanyID._name
            //            },
            {
                xtype : "displayfield",
                anchor : '95%',
                fieldLabel :_('Company Name'),
                itemId : 'BrandAdd.form.companyName',
                name : CmFinoFIX.message.JSBrand.Entries.CompanyName._name,
                value: mFino.auth.getCompanyName()
            },
            {
                xtype : "displayfield",
                anchor : '95%',
                fieldLabel :_('International Country Code'),
                itemId : 'BrandAdd.form.icc',
                name : CmFinoFIX.message.JSBrand.Entries.InternationalCountryCode._name,
                value: '234'
            },
            {
                xtype : "numberfield",
                anchor : '95%',
                allowBlank: false,
                blankText : _('Prefix Code is required'),
                fieldLabel :_('Prefix Code'),
                itemId : 'BrandAdd.form.prefix',
                minLength     : 2,
                maxLength     : 3,
                name : CmFinoFIX.message.JSBrand.Entries.PrefixCode._name
            },
            {
                xtype : 'textfield',
                fieldLabel: _("Brand Name"),
                allowBlank: false,
                itemId : 'BrandAdd.form.brandName',
                blankText : _('Brand Name is required'),
                anchor : '95%',
                name : CmFinoFIX.message.JSBrand.Entries.BrandName._name
            }
            ]
        }
        ];

        mFino.widget.BrandAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },   
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSBrand.Entries.CompanyID._name, mFino.auth.getCompanyId());
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

Ext.reg("BrandAddForm", mFino.widget.BrandAddForm);