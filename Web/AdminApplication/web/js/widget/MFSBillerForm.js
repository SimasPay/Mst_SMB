/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MFSBillerForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.MFSBillerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSBillerForm, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
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
                    fieldLabel: _("Biller Name"),
                    labelSeparator:':',
                    anchor : '95%',
                    itemId : 'MFSBillerForm.billerName',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerName._name,
                    listeners: {
                	    change: function(field) {
//                			this.findParentByType('mfsbillerform').onChangeSCT(field);
                        }
                    }
                },
                {
                    xtype : 'textfield',
                    fieldLabel: _("Biller Code"),
                    labelSeparator:':',
                    anchor : '95%',
                    itemId : 'MFSBillerForm.billerCode',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerCode._name
                },
                {
                    xtype : 'textfield',
                    fieldLabel: _("Biller Type"),
                    labelSeparator:':',
                    itemId : 'MFSBillerForm.billerType',
                    anchor : '95%',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerType._name
                }
                ]
            }
            ]
        }];
        mFino.widget.MFSBillerForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    onChangeSCT : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSServiceChargeTemplate.Entries.Name._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSServiceChargeTemplateCheck();
            msg.m_pSCTName = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
            if(this.store){
                if(this.record.phantom
                		 && this.store.getAt(0)!= this.record){
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

Ext.reg("mfsbillerform", mFino.widget.MFSBillerForm);