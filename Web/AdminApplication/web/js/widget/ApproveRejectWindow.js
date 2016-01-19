/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ApproveRejectWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Subscriber"),
        layout:'fit',
        floating: true,
        width:400,
        height:260,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.ApproveRejectWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ApproveRejectWindow, Ext.Window, {
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
               	xtype: 'enumdropdown',                   
                fieldLabel: _('Reject Reason'),
                itemId : 'rejectReason',
                id : 'rejectReason',
                labelSeparator:':',
                emptyText : _('<select one..>'),
                anchor:'95%',
                allowBlank: false,
                addEmpty : false,
                editable: false,
                disabled: true,
                enumId : CmFinoFIX.TagID.RejectReason,
                   //name : CmFinoFIX.message.JSPocketTemplate.Entries.Type._name,
                listeners: {
                   	select: function() {
                               this.findParentByType('ApproveRejectWindow').onChangeType();
                           }
                       }
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
                    columnWidth: 0.5,
                    xtype : 'radio',
                    itemId : 'approve',
                    name: 'selectone',
                    anchor : '90%',
                    checked : true,
                    boxLabel: _('Approve'),
                    handler: {
                    	call:function(field){
                    		
	                    	if(field.checked){
	                			
	                    		Ext.getCmp('comment').enable();
	                    		Ext.getCmp('rejectReason').disable();
	                		}
                    	}
                    }
                },
                {
                    columnWidth: 0.5,
                    xtype : 'radio',
                    itemId : 'reject',
                    anchor : '90%',
                    name: 'selectone',
                    boxLabel: _('Reject'),
                    handler: {
                    	call :  function(field){
                    		
                    		if(field.checked){
                    			
                    			Ext.getCmp('comment').reset();
                    			Ext.getCmp('comment').disable();
                    			Ext.getCmp('rejectReason').enable();
                    		}
                        }
                    }
                }]
            }]
        });
        this.items = [ this.form ];
        mFino.widget.ApproveRejectWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },
    onChangeType : function(){
	   	 var value = this.form.items.get("rejectReason").getValue();
	     if(Number(value) === CmFinoFIX.RejectReason.Others){
	    	 
	    	Ext.getCmp('comment').reset();
 			Ext.getCmp('comment').enable();
 			
	     } else {
	    	 
	    	Ext.getCmp('comment').reset();
	 		Ext.getCmp('comment').disable();
	     }
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
            if(this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name]!=CmFinoFIX.UpgradeState.Upgradable
            		&&this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name]!=CmFinoFIX.UpgradeState.Rejected){
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
           
            if(this.form.find('itemId','approve')[0].checked)
            {
                var amsg = new CmFinoFIX.message.JSApproveRejectSubscriber();
                amsg.m_pSubscriberMDNID = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
                amsg.m_pAdminComment = this.form.items.get('comment').getValue();
                //amsg.SetUserStatus(this.record.data[CmFinoFIX.message.JSUsers.Entries.UserStatus._name]);
                amsg.m_pAdminAction = CmFinoFIX.AdminAction.Approve;
                var aparams = mFino.util.showResponse.getDisplayParam();
                aparams.store = this.store;
                aparams.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                mFino.util.fix.send(amsg, aparams);
                this.hide();
            }
            else if(this.form.find('itemId','reject')[0].checked){
            	 if(this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeState._name]===CmFinoFIX.UpgradeState.Rejected){
                     Ext.Msg.show({
                         title: _('Alert !'),
                         minProgressWidth:600,
                         msg: _("Only Approve allowed as Already Rejected "),
                         buttons: Ext.MessageBox.OK,
                         multiline: false
                     });
                 }else{
                var rmsg = new CmFinoFIX.message.JSApproveRejectSubscriber();
                rmsg.m_pSubscriberMDNID = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
                
                var value = this.form.items.get("rejectReason").getValue();
                
       	     	if(Number(value) === CmFinoFIX.RejectReason.Others){
       	     		
       	     		rmsg.m_pAdminComment = this.form.items.get('comment').getValue();
       	     		
       	     	} else {
       	     		
       	     		rmsg.m_pAdminComment = this.form.items.get('rejectReason').getRawValue();
       	     	}
       	     	
                rmsg.m_pAdminAction = CmFinoFIX.AdminAction.Reject;
                var rparams = mFino.util.showResponse.getDisplayParam();
                rparams.store = this.store;
                rparams.store.lastOptions.params[CmFinoFIX.message.JSSubscriberMDN.IDSearch._name] = this.record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name);
                mFino.util.fix.send(rmsg, rparams);
                this.hide();
            }        
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
Ext.reg("ApproveRejectWindow", mFino.widget.ApproveRejectWindow);
