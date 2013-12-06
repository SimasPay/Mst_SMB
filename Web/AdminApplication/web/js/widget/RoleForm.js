/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RoleForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        isEditable: true
    });
    
    mFino.widget.RoleForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RoleForm, Ext.FormPanel, {
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
                    fieldLabel: _("Role Name"),
                    itemId : 'role.form.name',
                    labelSeparator:':',
                    anchor : '95%',
                    allowBlank: false,
                    maxLength : 255,
                    name: CmFinoFIX.message.JSRole.Entries.DisplayText._name,
                    listeners: {
                    	change: function(field) {
                			this.findParentByType('roleForm').onChangeName(field);
                        }
                    }
                },
                {
                    xtype : 'numberfield',
                    allowDecimals : false,
                    fieldLabel: _("Priority Level"),
                    itemId : 'role.form.priorityLevel',
                    labelSeparator:':',
                    anchor : '95%',
                    allowBlank : false,
                    maxLength : 16,
                    minValue : 0,
                    name: CmFinoFIX.message.JSRole.Entries.PriorityLevel._name
                }                									
               ]
            }
            ]
        }
        ];
        mFino.widget.RoleForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    onChangeName : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSRole.Entries.DisplayText._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSDuplicateNameCheck();
            msg.m_pName = field.getValue();
            msg.m_pTableName = "Role";
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
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
    
    validate : function() {
    	if(this.getForm().isValid()) {
    		return true;
    	} else {
    		return false;
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

Ext.reg("roleForm", mFino.widget.RoleForm);