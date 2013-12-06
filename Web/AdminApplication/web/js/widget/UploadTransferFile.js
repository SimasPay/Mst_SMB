/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UploadTransferFile = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id: "uploadTransferFile",
        defaultType: 'textfield',
        fileUpload:true,
        width: 550,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.UploadTransferFile.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UploadTransferFile, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 5;
        this.defaults = {
            anchor: '90%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : ':'
        };
        this.items = [
        {
           	xtype: 'textfield',
           	fieldLabel: _('Account MDN'),
           	itemId : 'transfer.form.mdn',
           	emptyText: _('MDN'),
           	maxLength : 13,
           	name: CmFinoFIX.message.JSBulkUpload.Entries.MDN._name,
            listeners: {
            	blur: function(field) {
            		field.isValid(true);
                	var valmdn=/^[2]{1}[3]{1}[4]{1}[0-9]{10}$/;
                	var valmdn1=/^[2]{1}[3]{1}[4]{1}[0-9]{7}$/;
                	var mdn = field.getValue();
                	if(mdn.length==13)
            		{
                		if(!valmdn.test(mdn))
                		{
                 			//alert("MDN should start from 234");
                			field.markInvalid("MDN start with 234");
                		}else{
                			this.findParentByType('uploadtransferfile').checkMDN(field);
                		}
            		}else if(mdn.length>10){
            			field.markInvalid("MDN starting with 234 should be 13 digits or 10 digits");
            		}
            		else{
            			if(valmdn1.test(mdn))
                		{
                			field.markInvalid("MDN should be 13 digits");
                		}
            		}
                	this.findParentByType('uploadtransferfile').checkMDN(field);
             	}
           }
        },                      
        {
        	xtype: 'textfield',
        	fieldLabel: _('PIN'),
        	itemId : 'transfer.form.pin',
        	emptyText: _('PIN'),
        	inputType: 'password',
        	name: CmFinoFIX.message.JSBulkUpload.Entries.Pin._name,
            listeners: {
            	blur: function(field) {
            		this.findParentByType('uploadtransferfile').validatePin(field);
             	}
           }
        },
	    {
	        xtype : "combo",
	        fieldLabel :_("Source Pocket"),
	        itemId : 'transfer.form.sourcepocket',
	        emptyText: _('Select Source Pcoket'),
	        triggerAction: "all",
	        forceSelection : true,
	        lastQuery: '',
	        store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
	        displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
	        valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
	        hiddenName : CmFinoFIX.message.JSBulkUpload.Entries.SourcePocket._name,
	        name: CmFinoFIX.message.JSBulkUpload.Entries.SourcePocket._name,
            listeners: {
            	focus: function(field) {
            		this.findParentByType('uploadtransferfile').getPockets(field);
             	},
            	select: function(field) {
            		this.findParentByType('uploadtransferfile').getBalance();
             	}
           }	        
	    },
	    {
	        xtype : "displayfield",
	        fieldLabel :_("Current Balance"),
	        allowBlank: true,
	        itemId : 'transfer.form.currentbalance',
	        id : 'transfer.form.currentbalance'
	    },
        {
            xtype: 'textfield',
            fieldLabel: _('Description'),
            itemId : 'transfer.form.description',
            emptyText: _('Enter description about file'),
            name: CmFinoFIX.message.JSBulkUpload.Entries.Description._name
        },
        {
            xtype: 'numberfield',
            allowDecimals:false,
            fieldLabel: _('No. of Transactions'),
            itemId : 'transfer.form.noofrecords',
            emptyText: _('Enter number of records'),
            name: CmFinoFIX.message.JSBulkUpload.Entries.TransactionsCount._name
        },
        {
          	xtype : 'datefield',
          	editable: false,
          	fieldLabel: _('Payment Date'),
          	emptyText: _('Select Payment Date'),
          	itemId : 'transfer.form.paymentdate',
          	name: CmFinoFIX.message.JSBulkUpload.Entries.PaymentDate._name,
            minValue:new Date().clearTime()
            //disabledDates: [new Date()],
            //minText:'Date must be future date'
       	},        
        {
            xtype: 'fileuploadfield',
            id: 'transfer.form.file',
            buttonText: _('Browse'),
            fieldLabel: _('Upload Transfer File'),
            emptyText:_('Browse Transfer.csv'),
            name: CmFinoFIX.message.JSBulkUpload.Entries.FileName._name
        }
        ];

        mFino.widget.UploadTransferFile.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    checkMDN : function(field){
    	var value = field.getValue();
    	var msg = new CmFinoFIX.message.JSCheckMDN();
    	msg.m_pMDN = value;
        var checkForExists=true;
        mFino.util.fix.checkNameInDB(field,msg, checkForExists);
    },
    
    validatePin: function(field) {
    	var value = field.getValue();
    	var mdn = this.find('itemId','transfer.form.mdn')[0].getValue();
    	if (typeof(mdn) === "undefined" || mdn === null || mdn === "") {
			Ext.ux.Toast.msg(_("Error"), _("Please enter your Account MDN"),5);
    		this.find('itemId','transfer.form.mdn')[0].focus();
    		return;
    	}
    	var msg = new CmFinoFIX.message.JSValidatePin();
    	msg.m_pSourceMDN = mdn;
    	msg.m_pPin = value;
        var checkForExists=true;
        mFino.util.fix.checkNameInDB(field,msg, checkForExists);
    },
    
    getPockets: function(field) {
    	field.clearValue();
    	var value = this.find('itemId','transfer.form.mdn')[0].getValue();
    	if (typeof(value) === "undefined" || value === null || value === "") {
			Ext.ux.Toast.msg(_("Error"), _("Please enter your Account MDN"),5);
    		this.find('itemId','transfer.form.mdn')[0].focus();
    	} 
    	else {
    		var statusSearchString = CmFinoFIX.PocketStatus.Initialized + "," + CmFinoFIX.PocketStatus.Active;
    		field.store.baseParams[CmFinoFIX.message.JSPocket.MDNSearch._name] = value;
    		field.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;    	
    		field.store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;
    		field.store.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = statusSearchString; 
    		field.store.reload({
	    		params: {
	    			MDNSearch: value,
	    			NoCompanyFilter: true,
	    			StatusSearch: statusSearchString,
	    			Commodity: CmFinoFIX.Commodity.Money,
	    			IsCollectorPocketAllowed : true
	    		}
	    	});
    		
    	}    		
    },
    
    getBalance: function() {
        var msg= new CmFinoFIX.message.JSGetAvailableBalance();
        msg.m_pSourceMDN = this.form.items.get("transfer.form.mdn").getValue();
        msg.m_pPin = this.form.items.get("transfer.form.pin").getValue();
        msg.m_pSourcePocketID = this.form.items.get("transfer.form.sourcepocket").getValue();

        var params = {
                success : function(response){
                	Ext.getCmp('transfer.form.currentbalance').setValue(response.m_pAvialableBalance);
                },
                failure : function(response){
                }
            };
        mFino.util.fix.send(msg, params);
    }
});

Ext.reg("uploadtransferfile", mFino.widget.UploadTransferFile);

