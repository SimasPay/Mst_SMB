
Ext.ns("mFino.widget");

mFino.widget.PurposeAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.PurposeAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PurposeAddForm, Ext.FormPanel, {
	initComponent : function () {
        this.labelWidth = 150;
        this.labelPad = 20;
        var partnerTypeSearchString = CmFinoFIX.BusinessPartnerType.RegulatoryBody + "," + CmFinoFIX.BusinessPartnerType.BranchOffice + "," + 
        CmFinoFIX.BusinessPartnerType.DirectAgent + "," + CmFinoFIX.BusinessPartnerType.SuperAgent + "," + 
        CmFinoFIX.BusinessPartnerType.SubRetailAgent + "," + CmFinoFIX.BusinessPartnerType.Merchant + "," + 
        CmFinoFIX.BusinessPartnerType.Biller + "," + CmFinoFIX.BusinessPartnerType.CorporateUser; 
        this.items = [
        {
        	xtype : "remotedropdown", 
        	fieldLabel: _('Partner Code'), 
        	labelSeparator : '', 	    	   
        	pageSize : 10,
        	params:{start:0, limit:10,PartnerTypeSearchString:partnerTypeSearchString,ServiceIDSearch:2},
        	anchor : '98%',
        	allowBlank: false,
        	addEmpty:false,
        	addDefault:true,
        	itemId :'PurposeAddForm.PartnerCode',
        	emptyText : _('<select  >'), 
        	store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartner), 
        	displayField: CmFinoFIX.message.JSPartner.Entries.PartnerCode._name, 
        	valueField : CmFinoFIX.message.JSPartner.Entries.PartnerCode._name, 
        	hiddenName : CmFinoFIX.message.JSPurpose.Entries.PurposeCode._name, 
        	name: CmFinoFIX.message.JSPurpose.Entries.PurposeCode._name    	   
	    },
		{
			xtype: 'enumdropdown',                   
			fieldLabel: _('Category'),
			itemId : 'PurposeAddForm.Category',
			labelSeparator:':',
			emptyText : _('<select one..>'),
			anchor:'95%',
			allowBlank: false,
			editable: false,
			enumId : CmFinoFIX.TagID.Category,
			name : CmFinoFIX.message.JSPurpose.Entries.Category._name
		}
		  
        ];        

        mFino.widget.PurposeAddForm.superclass.initComponent.call(this);
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
        var mc_combo = this.find('itemId','PurposeAddForm.PartnerCode')[0];
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
        
        mc_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPurpose.Entries.PurposeCode._name));
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

Ext.reg("purposeAddForm", mFino.widget.PurposeAddForm);