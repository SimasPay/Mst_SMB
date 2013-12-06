/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ApproveRejectAdjustmentWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Adjustment"),
        layout:'fit',
        floating: true,
        width:330,
        height:260,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.ApproveRejectAdjustmentWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ApproveRejectAdjustmentWindow, Ext.Window, {
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
        this.addEvents("approveRejectFormSubmit");
        mFino.widget.ApproveRejectAdjustmentWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){        
        if(this.form.getForm().isValid()){                 
            if(this.form.find('itemId','approve')[0].checked) {
            	var msg = new CmFinoFIX.message.JSAdjustments();
            	msg.m_pID = this.record.get(CmFinoFIX.message.JSAdjustments.Entries.ID._name);
            	msg.m_pApproveOrRejectComment = this.form.items.get('comment').getValue();
            	msg.m_pAdjustmentStatus = CmFinoFIX.AdjustmentStatus.Approved;
                msg.m_paction = "update";
                var params = mFino.util.showResponse.getDisplayParam();
                mFino.util.fix.send(msg, params);                
                Ext.apply(params, {
                    success :  function(response){
                    	if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                    		Ext.ux.Toast.msg(_("Success"), _(response.m_pErrorDescription),3);
                    	} else {
                    		Ext.ux.Toast.msg(_('Failure'),_(response.m_pErrorDescription),3);
                    	}
                    	this.scope.fireEvent("approveRejectFormSubmit");
                    },
                    scope : this
                });
                this.hide();                
            } else if(this.form.find('itemId','reject')[0].checked) {
            	var msg = new CmFinoFIX.message.JSAdjustments();
            	msg.m_pID = this.record.get(CmFinoFIX.message.JSAdjustments.Entries.ID._name);
            	msg.m_pApproveOrRejectComment = this.form.items.get('comment').getValue();
            	msg.m_pAdjustmentStatus = CmFinoFIX.AdjustmentStatus.Rejected;
                msg.m_paction = "update";
                var params = mFino.util.showResponse.getDisplayParam();
                mFino.util.fix.send(msg, params);                
                Ext.apply(params, {
                    success :  function(response){
                    	if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                    		Ext.ux.Toast.msg(_("Success"), _(response.m_pErrorDescription),3); 
                    	} else {
                    		Ext.ux.Toast.msg(_('Failure'),_(response.m_pErrorDescription),3);
                    	}
                    	this.scope.fireEvent("approveRejectFormSubmit");
                    },
                    scope : this
                });
                this.hide();
            }
        } else {
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    setRecord : function(record){
        this.form.getForm().reset();
        this.record = record;
        this.form.getForm().clearInvalid();
    }
});

