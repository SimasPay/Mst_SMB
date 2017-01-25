/**
 *SubscriberAddPocketWindow.js 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberAddPocketWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    	id: "addPocket",
        defaultType: 'textfield',
        fileUpload:true,
        width: 550,
        frame : true,
        selectOnFocus: true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.SubscriberAddPocketWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberAddPocketWindow, Ext.FormPanel, {
    initComponent : function(){
    	this.labelWidth = 220;
        this.labelPad = 5;
        this.store = new FIX.FIXStore(mFino.DATA_URL,CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber);
        this.defaults = {
            anchor: '90%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : ':'
        };
    	this.items = [{
		    xtype: 'fieldset',
		    autoHeight: true,
		    anchor : '100%',
		    columns: 1,
		    items: [
						{
							xtype : "hidden",
							itemId : 'subaddpocket.form.mdnid',
							name: CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.MDNID._name
							
						},
						{
                     		xtype : 'textfield',
                     		fieldLabel: _('MDN'),
                     		allowBlank: false,
                     		blankText : _('MDN is required'),
                     		anchor : '100%',
							itemId : 'subaddpocket.form.mdn',
                     		name: CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.MDN._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Full Name'),
                     		allowBlank: false,
                     		blankText : _('Full Name is required'),
                     		anchor : '100%',
							itemId : 'subaddpocket.form.firstname',
                     		name: CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.FirstName._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _('Pocket Template'),
                     		allowBlank: false,
                     		blankText : _('Pocket Template is required'),
                     		anchor : '100%',
							itemId : 'subaddpocket.form.pockettemplate',
                     		name: CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.PocketTemplateConfigID._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _('CIF No'),
                     		allowBlank: false,
                     		blankText : _('CIF No is required'),
                     		vtype:'name',
                     		anchor : '100%',
							itemId : 'subaddpocket.form.cifno',
                     		name: CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.ApplicationID._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _('Bank Account No'),
                     		allowBlank: false,
                     		blankText : _('Bank Account No is required'),
                     		vtype:'tendigitnumber',
                     		anchor : '100%',
							itemId : 'subaddpocket.form.bankaccno',
                     		name: CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.AccountNumber._name
                     	}
                     	
                     	
            ]
		}]
    	mFino.widget.SubscriberAddPocketWindow.superclass.initComponent.call(this);
    	markMandatoryFields(this.form);
    },
  
 
    setRecord : function(record){
        this.getForm().reset();
		this.record=record;
		this.find('itemId', 'subaddpocket.form.mdnid')[0].setValue(record.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.MDNID._name]);
		this.find('itemId', 'subaddpocket.form.mdn')[0].setValue(record.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.MDN._name]).disable();
	    this.find('itemId', 'subaddpocket.form.firstname')[0].setValue(record.data[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.Entries.FirstName._name]).disable();
		this.find('itemId', 'subaddpocket.form.pockettemplate')[0].setValue("BankAccount-Savings").disable();
    },    
    onAddPocket : function(formWindow){
        if(this.getForm().isValid()){
        		   var amsg= new CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber();
        		   var values = this.form.getValues();
              	  amsg.m_pMDNID =values[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.MDNID._name];
                  amsg.m_pMDN=values[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.MDN._name];
              	  amsg.m_pApplicationID= values[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.ApplicationID._name];
              	  amsg.m_pAccountNumber=values[CmFinoFIX.message.JSAddBankPocketToEmoneySubscriber.AccountNumber._name];
                   //amsg.m_paction = "default";
                   var params = mFino.util.showResponse.getDisplayParam();
                   params.formWindow = formWindow;
                   mFino.util.fix.send(amsg, params);
                   Ext.apply(params, {
         		   success : function(response){
         			   formWindow.hide();
         			   if(response.m_psuccess == true){
         			   Ext.Msg.show({
                          title: _('Success'),
                          minProgressWidth:250,
                          msg: "Request to MBanking Services has been submitted successfully. This feature will be available once approved.",
                          buttons: Ext.MessageBox.OK,
                          multiline: false
         			   });
         			   }else{
        				   Ext.MessageBox.alert(_("Info"), _(response.m_pErrorDescription));   	   
        			   }
         		   }
         	   	});
               	formWindow.hide();
         }     
         else{
             Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
         }
     },
    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
 
});
Ext.reg("SubscriberAddPocketWindow", mFino.widget.SubscriberAddPocketWindow);