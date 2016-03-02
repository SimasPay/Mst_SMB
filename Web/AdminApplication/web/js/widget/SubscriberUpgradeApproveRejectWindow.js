/**
 *SubscriberUpgradeApproveRejectWindow.js 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberUpgradeApproveRejectWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Subscriber Upgrade"),
        layout:'fit',
        floating: true,
        width:500,
        height:260,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.SubscriberUpgradeApproveRejectWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberUpgradeApproveRejectWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            text: _('Ok'),
            handler: this.ok.createDelegate(this)
        },
        {
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        }
        ];

        this.form = new Ext.form.FormPanel({
            frame : true,
            items : [
               {
            	   fieldLabel: 'Comment'
               },{
                xtype : 'textarea',
                itemId :'comment',
                id:'comment',
                fieldLabel : _('Comments'),
                allowBlank: false,
                hideLabel: true,
                labelSeparator :'',
                anchor : '100%'
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
                    itemId : 'approve',
                    name: 'selectone',
                    anchor : '100%',
                    checked : true,
                    boxLabel: _('Approve'),
                    handler: {
                    	call:function(field){
                    		
	                    	if(field.checked){
	                			
	                    		Ext.getCmp('comment').enable();
	                		}
                    	}
                    }
                },
                {
                    columnWidth: 0.3,
                    xtype : 'radio',
                    itemId : 'reject',
                    anchor : '100%',
                    name: 'selectone',
                    boxLabel: _('Reject'),
                    handler: {
                    	call :  function(field){
                    		
                    		if(field.checked){
                    			
                    			Ext.getCmp('comment').reset();
                    		}
                        }
                    }
                }
               ]
            }]
        });
        this.items = [ this.form ];
        mFino.widget.SubscriberUpgradeApproveRejectWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },
    onChangeType : function(){
//	   	 
    },
    ok : function(){
        if(!this.record){
            Ext.Msg.show({
                title: _('Alert !'),
                minProgressWidth:250,
                msg: _("No subscriber selected!"),
                buttons: Ext.MessageBox.OK,
                multiline: false
            });
            return;
        }
        if(this.form.getForm().isValid()){
            if(this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctStatus._name]==CmFinoFIX.SubscriberUpgradeStatus.Initialized){
         
            	   var amsg = new CmFinoFIX.message.JSSubscriberUpgrade();
            	   amsg.m_pMDNID = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
            	   amsg.m_pUpgradeAcctComments = this.form.items.get('comment').getValue();
            	   amsg.m_paction="update"
            		   
            		   if(this.form.find('itemId','approve')[0].checked)
            		   	{
//            			   alert('Approve::'+this.form.items.get('comment').getValue())
            			   amsg.m_pUpgradeAcctStatus=CmFinoFIX.SubscriberUpgradeStatus.Approve
            		   	}
            		   else if(this.form.find('itemId','reject')[0].checked){
            			   alert('reject ::'+this.form.items.get('comment').getValue())
            			   amsg.m_pUpgradeAcctStatus=CmFinoFIX.SubscriberUpgradeStatus.Reject
            		   }
                  
                   var aparams = mFino.util.showResponse.getDisplayParam();
                   mFino.util.fix.send(amsg, aparams);
                   this.hide();

            }else{
            	Ext.Msg.show({
                    title: _('Alert !'),
                    minProgressWidth:600,
                    msg: _("Approve/Reject Not allowed"),
                    buttons: Ext.MessageBox.OK,
                    multiline: false
                });
                this.hide();
                return;
            }
         
        }     
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    setRecord : function(record){
        this.form.getForm().reset();
        this.record = record;
        this.form.getForm().loadRecord(record);
        this.form.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
    }
});
Ext.reg("SubscriberUpgradeApproveRejectWindow", mFino.widget.SubscriberUpgradeApproveRejectWindow);
