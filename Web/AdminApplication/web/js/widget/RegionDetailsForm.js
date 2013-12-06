/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RegionDetailsForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    mFino.widget.RegionDetailsForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RegionDetailsForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 100;
        this.labelPad = 20;
        this.autoScroll = true;
        this.items = [
        {
            layout:'column',
            items : [
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 100,
                labelPad : 5,
                items : [
//                {
//                    xtype : 'combo',
//                    fieldLabel: _("Company Name"),
//                    width: 50,
//                    itemId : 'company',
//                    allowBlank: false,
//                    triggerAction: "all",
//                    minChars : 2,
//                    forceSelection : true,
//                    store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSCompany),
//                    RPCObject : CmFinoFIX.message.JSCompany,
//                    displayField: CmFinoFIX.message.JSCompany.Entries.CompanyName._name,
////                    valueField : CmFinoFIX.message.JSRegion.Entries.CompanyName._name,
//                    name: CmFinoFIX.message.JSRegion.Entries.CompanyName._name,
//                    anchor:'95%'
//                }
            {
                xtype : "displayfield",
                anchor : '95%',
                fieldLabel :_('Company Name'),
                name : CmFinoFIX.message.JSRegion.Entries.CompanyName._name,
                value: mFino.auth.getCompanyName()
            }
            ]
            },
            {
                columnWidth:1,
                layout: 'form',
                labelWidth : 100,
                labelPad : 5,
                autoScroll:true,
                items :
                [
                {
                    xtype: 'textfield',
                    labelWidth : 50,
                    fieldLabel: _('Region Code'),
                    itemId : 'regionCode',
                    anchor:'95%',
                    allowBlank: false,
                    name: CmFinoFIX.message.JSRegion.Entries.RegionCode._name
                }
                ]
            },
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 100,
                labelPad : 5,
                autoScroll:true,
                items :
                [
                {
                    xtype : 'textfield',
                    labelWidth : 50,
                    fieldLabel: _('Region Name'),
                    anchor : '95%',
                    itemId : 'regionName',
                    allowBlank: false,
                    name: CmFinoFIX.message.JSRegion.Entries.RegionName._name
                }
                ]
            }
            ]
        }
        ];
        mFino.widget.RegionDetailsForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        this.on("render", function(){
            this.setEditable(this.initialConfig.isEditable);
        });
    },
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);

            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSRegion.Entries.CompanyID._name, mFino.auth.getCompanyId());
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
    },

    setEditable : function(isEditable){
        if(isEditable){
            this.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield'|| item.getXType()=='checkbox'||item.getXType()=='checkboxgroup'||item.getXType()=='enumdropdown') {
                    //enable the item
                    item.enable();
                }
            });
        }else{
            this.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield'|| item.getXType()=='checkbox'||item.getXType()=='checkboxgroup'||item.getXType()=='enumdropdown') {
                    //Disable the item
                    item.disable();
                }
            });
        }
    }
});

Ext.reg("RegionDetailsForm", mFino.widget.RegionDetailsForm);
