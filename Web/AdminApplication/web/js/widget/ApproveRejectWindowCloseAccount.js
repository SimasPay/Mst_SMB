/**
 *ApproveRejectWindowCloseAccount.js 
 */
Ext.ns("mFino.widget");

mFino.widget.ApproveRejectWindowCloseAccount = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Close Account"),
        layout:'fit',
        floating: true,
        width:500,
        height:260,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.ApproveRejectWindowCloseAccount.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ApproveRejectWindowCloseAccount, Ext.Window, {
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
                id:'commentsua',
                fieldLabel : _('Comments'),
                allowBlank: false,
                hideLabel: true,
                labelSeparator :'',
                name: CmFinoFIX.message.JSAgentCloseApproveReject.CloseApproverComments._name,
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
	                			
	                    		Ext.getCmp('commentsua').enable();
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
                    			
                    			Ext.getCmp('commentsua').reset();
                    		}
                        }
                    }
                }
               ]
            }]
        });
        this.items = [ this.form ];
        mFino.widget.ApproveRejectWindowCloseAccount.superclass.initComponent.call(this);
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
                msg: _("No Agent selected!"),
                buttons: Ext.MessageBox.OK,
                multiline: false
            });
            return;
        }
        if(this.form.getForm().isValid()){
       	   var amsg = new CmFinoFIX.message.JSAgentCloseApproveReject();
    	   var values = this.form.getForm().getValues();
    	   amsg.m_pMDNID = this.record.data[CmFinoFIX.message.JSAgent.Entries.MDN._name];
    	   amsg.m_pCloseApproverComments = values[CmFinoFIX.message.JSAgentCloseApproveReject.CloseApproverComments._name];
    	   amsg.m_paction="update"
    		   
		   if(this.form.find('itemId','approve')[0].checked) {
			   
			   amsg.m_pCloseAcctStatus = CmFinoFIX.CloseAcctStatus.Approve;
		   	
		   } else if(this.form.find('itemId','reject')[0].checked){
			   
			   amsg.m_pCloseAcctStatus = CmFinoFIX.CloseAcctStatus.Reject;
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
Ext.reg("ApproveRejectWindowCloseAccount", mFino.widget.ApproveRejectWindowCloseAccount);
