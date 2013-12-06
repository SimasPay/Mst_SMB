/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ResolveWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Resolve Transaction"),
        layout:'fit',
        floating: true,
        width:330,
        height:260,
        plain:true,
        closable: true,
        resizable: false
    });
    mFino.widget.ResolveWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ResolveWindow, Ext.Window, {
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
                xtype : 'textarea',
                itemId :'comment',
                fieldLabel : _('Comments'),
                maxLength : 255,
                labelSeparator :'',
                anchor : '90%'
            },
            {
                xtype: 'fieldset',
                title : _('Select One'),
                layout : 'column',
                autoHeight: true,
                anchor : '90%',
                columns: 1,
                items: [
                {
                    columnWidth: 1,
                    xtype : 'radio',
                    itemId : 'complete',
                    name: 'selectone',
                    anchor : '90%',
                    checked : true,
                    boxLabel: _('  Mark the Transaction as complete ')
                },
                {
                    columnWidth: 1,
                    xtype : 'radio',
                    itemId : 'cancel',
                    anchor : '90%',
                    name: 'selectone',
                    boxLabel: _('  Mark the Transaction as cancel ')
                }]
            }]
        });
        this.items = [ this.form ];
        mFino.widget.ResolveWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },
    
    close : function(){
        this.hide();
    },
    
    ok : function(){
    	 if(!this.record){
             Ext.Msg.show({
                 title: _('Alert !'),
                 minProgressWidth:600,
                 msg: _("No Transaction selected!"),
                 buttons: Ext.MessageBox.OK,
                 multiline: false
             });
    	 }else{
	        var csrComment = this.form.items.get('comment').getValue();
	        var msg = new CmFinoFIX.message.JSPendingCommodityTransferRequest();
	        var params = mFino.util.showResponse.getDisplayParam();
	        msg.m_pTransferID = this.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name);
	        msg.m_pSourceMDN = this.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.SourceMDN._name);
	        msg.m_pMSPID = 1;
	        msg.m_pTransactionID = this.record.get(CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionID._name);
	        if(this.form.find('itemId','complete')[0].checked)
	        {              
	        	 msg.m_pCSRAction = CmFinoFIX.CSRAction.Complete;
	        	 msg.m_pCSRComment = csrComment;
		         mFino.util.fix.send(msg, params);
	        }
	        else if(this.form.find('itemId','cancel')[0].checked){
	        	 msg.m_pCSRAction = CmFinoFIX.CSRAction.Cancel;
	        	 msg.m_pCSRComment = csrComment;
	             mFino.util.fix.send(msg, params);
            }
        }
    	this.record = null;
        this.hide();
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

