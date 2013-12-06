Ext.ns("mFino.widget");

mFino.widget.ApproveRejectAgentCashInWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Agent CashIn Request"),
        layout:'fit',
        floating: true,
        width:330,
        height:260,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.ApproveRejectAgentCashInWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ApproveRejectAgentCashInWindow, Ext.Window, {
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
                fieldLabel : _('Comments'),
                allowBlank: false,
                hideLabel: true,
                labelSeparator :'',
                anchor : '100%'
               },{
                xtype: 'fieldset',
                title : _('Select One'),
                layout : 'column',
                autoHeight: true,
                anchor : '100%',
                columns: 1,
                items: [
                {
                    columnWidth: 0.5,
                    xtype : 'radio',
                    itemId : 'approve',
                    name: 'selectone',
                    anchor : '90%',
                    checked : true,
                    boxLabel: _('Approve')
                },
                {
                    columnWidth: 0.5,
                    xtype : 'radio',
                    itemId : 'reject',
                    anchor : '90%',
                    name: 'selectone',
                    boxLabel: _('Reject')
                }]
            }]
        });
        this.items = [ this.form ];
        mFino.widget.ApproveRejectAgentCashInWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){
        if(!this.record){
            Ext.Msg.show({
                title: _('Alert !'),
                minProgressWidth:250,
                msg: _("No Transaction is selected"),
                buttons: Ext.MessageBox.OK,
                multiline: false
            });
            return;
        }
        if(this.form.getForm().isValid()){
            var msg = new CmFinoFIX.message.AgentCashIn();
            msg.m_pAdminComment = this.form.items.get('comment').getValue();
			msg.m_paction = "update";
			msg.m_pTransactionID = this.record.data[CmFinoFIX.message.AgentCashIn.Entries.ID._name];
            if(this.form.find('itemId','approve')[0].checked) {
            	msg.m_pAdminAction = CmFinoFIX.AdminAction.Approve;
            } 
            else if(this.form.find('itemId','reject')[0].checked) {
            	msg.m_pAdminAction = CmFinoFIX.AdminAction.Reject;
            }
            var params = mFino.util.showResponse.getDisplayParam();
            params.CCApproveRejectStore = this.record.store;
            mFino.util.fix.send(msg, params);
            this.hide();
        }     
        else{
            Ext.ux.Toast.msg(_("Error"), _("Please provide the comments."),5);
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

