/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeTypeForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.ChargeTypeForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeTypeForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 200;
        this.labelPad = 20;
        this.items = [
        {
            layout:'column',
            items : [
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : [
                {
                    xtype : 'textfield',
                    fieldLabel: _("Name"),
                    labelSeparator:':',
                    itemId:'ChargeType.form.name',
                    anchor : '90%',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSChargeType.Entries.Name._name,
                    listeners: {
                	    change: function(field) {
                			this.findParentByType('chargetypeform').onChangeName(field);
                        }
                    }
                },
                {
                    xtype : 'textfield',
                    fieldLabel: _("Description"),
                    itemId:'ChargeType.form.Desc',
                    labelSeparator:':',
                    anchor : '90%',
                    maxLength : 255,
                    name: CmFinoFIX.message.JSChargeType.Entries.Description._name
                }
                ]
            }
            ]
        }
        ];
        mFino.widget.ChargeTypeForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    onChangeName : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSChargeType.Entries.Name._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSDuplicateNameCheck();
            msg.m_pName = field.getValue();
            msg.m_pTableName = "Charge Type";
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
            if(this.store){
                if(this.record.phantom){
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

Ext.reg("chargetypeform", mFino.widget.ChargeTypeForm);