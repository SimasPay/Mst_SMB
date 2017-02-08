/**
 *SubscriberAddPocketApproveRejectWindow 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberAddPocketApproveRejectWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    	id: "proceed",
        defaultType: 'textfield',
        fileUpload:true,
        width: 550,
        frame : true,
        selectOnFocus: true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : '',
        closable: false,
        resizable: false
    });
    mFino.widget.SubscriberAddPocketApproveRejectWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberAddPocketApproveRejectWindow, Ext.FormPanel, {
    initComponent : function(){
    	this.labelWidth = 220;
        this.labelPad = 5;
        this.store = new FIX.FIXStore(mFino.DATA_URL,CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber);
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
							name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.MDNID._name
							
						},
						{
                     		xtype : 'textfield',
                     		fieldLabel: _('MDN'),
                     		allowBlank: false,
                     		blankText : _('MDN is required'),
                     		anchor : '100%',
							itemId : 'subaddpocket.form.mdn',
                     		name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.MDN._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Full Name'),
                     		allowBlank: false,
                     		blankText : _('Full Name is required'),
                     		anchor : '100%',
							itemId : 'subaddpocket.form.firstname',
                     		name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.FirstName._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _('Pocket Template'),
                     		allowBlank: false,
                     		blankText : _('Pocket Template is required'),
                     		anchor : '100%',
							itemId : 'subaddpocket.form.pockettemplate',
                     		name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.PocketTemplateConfigID._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _('CIF No'),
                     		allowBlank: false,
                     		blankText : _('CIF No is required'),
                     		vtype:'number',
                     		anchor : '100%',
							itemId : 'subaddpocket.form.cifno',
                     		name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.ApplicationID._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _('Bank Account No'),
                     		allowBlank: false,
                     		blankText : _('Bank Account No is required'),
                     		vtype:'number',
                     		anchor : '100%',
							itemId : 'subaddpocket.form.bankaccno',
                     		name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.AccountNumber._name
                     	},{
                     	   fieldLabel: 'Comment'
                        },{
                            xtype : 'textarea',
                            itemId :'bcomment',
                            id:'bcomment',
                            fieldLabel : _('Comments'),
                            allowBlank: false,
                            hideLabel: true,
                            labelSeparator :'',
                            anchor : '100%',
                            itemId : 'subaddpocket.form.comments',
                     		name: CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.AdminComment._name
                           },
                         	{
                               xtype: 'fieldset',
                               title : _('Select One'),
                               layout : 'column',
                               autoHeight: true,
                               anchor : '100%',
                               columns: 1,
                               items: [
                               {
                                   columnWidth: 0.3,
                                   xtype : 'radio',
                                   itemId : 'bapprove',
                                   name: 'selectone',
                                   anchor : '100%',
                                   boxLabel: _('Approve')
                                 
                               },
                               {
                                   columnWidth: 0.3,
                                   xtype : 'radio',
                                   itemId : 'breject',
                                   anchor : '100%',
                                   name: 'selectone',
                                   boxLabel: _('Reject')
                                 
                               }]
                           }]
                   
		}]
    
    	mFino.widget.SubscriberAddPocketApproveRejectWindow.superclass.initComponent.call(this);
    	markMandatoryFields(this.form);
    },
  
 
    setRecord : function(record){
        this.getForm().reset();
		this.record=record;
		this.find('itemId', 'subaddpocket.form.mdnid')[0].setValue(record.data[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.MDNID._name]);
		this.find('itemId', 'subaddpocket.form.mdn')[0].setValue(record.data[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.MDN._name]).disable();
	    this.find('itemId', 'subaddpocket.form.firstname')[0].setValue(record.data[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.FirstName._name]).disable();
		this.find('itemId', 'subaddpocket.form.pockettemplate')[0].setValue("BankAccount-Savings").disable();
		this.find('itemId', 'subaddpocket.form.cifno')[0].setValue(record.data[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.ApplicationID._name]).disable();
		 this.find('itemId', 'subaddpocket.form.bankaccno')[0].setValue(record.data[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.Entries.AccountNumber._name]).disable();
    },    
    onProceed : function(formWindow){
        if(this.getForm().isValid()){
        	if (this.find('itemId','bapprove')[0].checked || this.find('itemId','breject')[0].checked){
        		   var amsg= new CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber();
        		   var values = this.form.getValues();
              	  amsg.m_pMDNID =values[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.MDNID._name];
                  amsg.m_pMDN=values[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.MDN._name];
                  if(this.find('itemId','bapprove')[0].checked)
                  {
              	  amsg.m_pAdminAction= CmFinoFIX.AdminAction.Approve;
              	  
                  }else if(this.find('itemId','breject')[0].checked)
                  {
                  	  amsg.m_pAdminAction= CmFinoFIX.AdminAction.Reject;
                  }
              	  amsg.m_pAdminComment=values[CmFinoFIX.message.JSApproveRejectAddBankPocketToEmoneySubscriber.AdminComment._name];
                   amsg.m_paction = "update";
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
                          msg: "The request is approved successfully",
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
		else {
			Ext.ux.Toast.msg(_("Error"), _("Please select one of the approval status."),5);				
		} }   
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
Ext.reg("SubscriberAddPocketApproveRejectWindow", mFino.widget.SubscriberAddPocketApproveRejectWindow);