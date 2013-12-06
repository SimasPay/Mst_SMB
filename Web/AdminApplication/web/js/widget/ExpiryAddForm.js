
Ext.ns("mFino.widget");

mFino.widget.ExpiryAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.ExpiryAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ExpiryAddForm, Ext.FormPanel, {
	initComponent : function () {
        this.labelWidth = 150;
        this.labelPad = 20;
        this.items = [
		{
			xtype: 'enumdropdown',                   
			fieldLabel: _('ExpiryType'),
			itemId : 'ExpiryAddForm.ExpiryType',
			labelSeparator:':',
			emptyText : _('<select one..>'),
			anchor:'95%',
			allowBlank: false,
			editable: false,
			addEmpty:false,
			enumId : CmFinoFIX.TagID.ExpiryType,
			name : CmFinoFIX.message.JSExpirationType.Entries.ExpiryType._name
		},
		{
			xtype: 'enumdropdown',                   
			fieldLabel: _('ExpiryMode'),
			itemId : 'ExpiryAddForm.ExpiryMode',
			labelSeparator:':',
			emptyText : _('<select one..>'),
			anchor:'95%',
			allowBlank: false,
			addEmpty:false,
			enumId : CmFinoFIX.TagID.ExpiryMode,
			name : CmFinoFIX.message.JSExpirationType.Entries.ExpiryMode._name
		},
		{
			xtype:'textfield',
			fieldLabel: _('ExpiryValue'),
			allowBlank:false,
			vtype:'numbercomma',
			labelSeparator: '',
			emptyText: _('Duration in seconds'),
			maxLength : 16,
			name: CmFinoFIX.message.JSExpirationType.Entries.ExpiryValue._name
		},
		{
			xtype : 'textfield',
			fieldLabel:'ExpiryDescription',
			labelSeparator: '',
			allowBlank: false,
			name: CmFinoFIX.message.JSExpirationType.Entries.ExpiryDescription._name
		}
		  
        ];        

        mFino.widget.ExpiryAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
	    	if(item.getXType() == 'remotedropdown') {
	    		item.reload();
	    	}
    	});
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

Ext.reg("ExpiryAddForm", mFino.widget.ExpiryAddForm);