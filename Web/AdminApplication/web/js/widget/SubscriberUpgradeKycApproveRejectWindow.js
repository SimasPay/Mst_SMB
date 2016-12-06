/**
 *SubscriberUpgradeKycApproveRejectWindow.js 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberUpgradeKycApproveRejectWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Subscriber Upgrade Kyc"),
        floating: true,
        width: 600,
        height: 470,
        frame : true,
        labelWidth: 5,
        plain:true,
        layout: 'fit'
    });
    mFino.widget.SubscriberUpgradeKycApproveRejectWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberUpgradeKycApproveRejectWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
            {
            	text: _('Proceed'),
                handler: this.ok.createDelegate(this)
             }, 
             {
                text: _('Cancel'),
                handler: this.cancel.createDelegate(this)
             }
        ];

        this.form = new Ext.form.FormPanel({
            items : [
            	{
            		xtype: 'fieldset',
            	    title : _('Personal Data'),
            	   	anchor : '100%',
            	   	layout: 'column',
            	   	columns: 2,
            	   	items: [
            	   		{
            	   			columnWidth: 0.5,
            				layout: 'form',
            				labelWidth : 100,
            				items : [
            					{
            						xtype : 'displayfield',
                					fieldLabel: _('Full Name'),
                					anchor : '90%',
                					name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name
            					},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('ID Type'),
                             		anchor : '100%',
                             		name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDType._name
                             	},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Birth Place'),
                             		anchor : '100%',
                             		name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
                             	},
                             	{
                                    xtype : 'displayfield',
                                    fieldLabel: _('Email'),
                                    vtype: 'email',
                                    anchor : '100%',
                                    name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name
                                }
            				]
            	   		},
            	   		{
            	   			columnWidth: 0.5,
            				layout: 'form',
            				labelWidth : 130,
            				items : [
            					{
                     				xtype : 'displayfield',
                     				fieldLabel: _(' Mother Maiden Name'),
                     				anchor : '90%',
                     				name: CmFinoFIX.message.JSSubscriberMDN.Entries.MothersMaidenName._name
                     			},
                     			{
                             		xtype : 'displayfield',
                             		fieldLabel: _('ID Number'),
                             		anchor : '100%',
                             		name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDNumber._name
                             	},
                             	{
                                    anchor : '100%',
                                    renderer: "date",
                                    xtype : 'displayfield',
                                    format : 'd-m-Y',
                                    fieldLabel: _('Date of Birth'),
                                    name: CmFinoFIX.message.JSSubscriberMDN.Entries.DateOfBirth._name
                                },
                             	{
                                    xtype : 'displayfield',
                                    fieldLabel: _('ID Card'),
                                    anchor : '100%',
                                    name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,
                                    style: {
                    					color: '#0000ff',
                    					cursor:'pointer'
                    				},
                                    listeners:{
                                   	 	afterrender: function(component) {
                                   	 		component.getEl().on('click', function() { 
                                   	 			mFino.widget.SubscriberDetails.prototype.showImage(component.getValue())
                                   	        });  
                                   	    }
                                    }
                                }
            				]
            	   		}
            	   	]
            	},
            	{
            		xtype: 'fieldset',
            	    title : _('Address'),
            	   	anchor : '100%',
            	   	layout: 'column',
            	   	columns: 2,
            	   	items: [
            	   		{
            	   			columnWidth: 0.5,
            				layout: 'form',
            				labelWidth : 130,
            				items : [
            					{
            						xtype : 'displayfield',
                					fieldLabel :_("Province/Propinsi"),
                					anchor : '90%',
                					name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPRegionName._name
            					},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('District/Kecamatan'),
                             		anchor : '100%',
                             		name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPState._name
                             	},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Street Address/Alamat'),
                             		anchor : '100%',
                             		name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPPlotNo._name
                             	}
            				]
            	   		},
            	   		{
            	   			columnWidth: 0.5,
            				layout: 'form',
            				labelWidth : 130,
            				items : [
            					{
                     				xtype : 'displayfield',
                     				fieldLabel: _('City/Kota'),
                     				anchor : '90%',
                     				name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPCity._name
                     			},
                     			{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Village/Kelurahan'),
                             		anchor : '100%',
                             		name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPSubState._name
                             	}
            				]
            	   		}
            	   	]
            	},
            	{
            		xtype: 'fieldset',
            		title : _('Response'),
            		layout : 'column',
            		autoHeight: true,
            		anchor : '100%',
            		columns: 1,
            		items: [
							{
								fieldLabel: 'Comment',
								columnWidth: 1,
							},
							{
								columnWidth: 1,
								xtype : 'textarea',
								itemId :'comment',
								id:'commentsu',
								fieldLabel : _('Comments'),
								allowBlank: false,
								hideLabel: true,
								labelSeparator :'',
								name: CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctComments._name,
								anchor : '100%'
							},
							{
								columnWidth: 0.33,
								xtype : 'radio',
								itemId : 'approve',
								name: 'selectone',
								anchor : '100%',
								checked : true,
								boxLabel: _('Approve'),
								handler: {
									call:function(field){
										if(field.checked){
											Ext.getCmp('commentsu').enable();
										}
									}
								}
							},
							{
								columnWidth: 0.33,
								xtype : 'radio',
								itemId : 'revision',
								anchor : '100%',
								name: 'selectone',
								boxLabel: _('Revision'),
								handler: {
									call :  function(field){
										if(field.checked){
											Ext.getCmp('commentsu').reset();
										}
									}
								}
							},
							{
								columnWidth: 0.33,
								xtype : 'radio',
								itemId : 'reject',
								anchor : '100%',
								name: 'selectone',
								boxLabel: _('Reject'),
								handler: {
									call :  function(field){
										if(field.checked){
											Ext.getCmp('commentsu').reset();
										}
									}
								}
							}
            	    ]
               }
            ]
        });
        
        this.items = [ this.form ];
        mFino.widget.SubscriberUpgradeKycApproveRejectWindow.superclass.initComponent.call(this);
    },
    cancel : function(){
        this.hide();
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
        		if(this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctStatus._name] == CmFinoFIX.SubscriberUpgradeKycStatus.Initialized){
        			Ext.Msg.confirm(_("Confirm?"), _("Are you sure?"),
    			        function(btn){
    			            if(btn !== "yes"){
    			                return;
    			            }
    			            
    			            var amsg = new CmFinoFIX.message.JSSubscriberUpgradeKyc();
    			            var values = this.form.getForm().getValues();
    			            amsg.m_pID = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
    			            amsg.m_pUpgradeAcctComments = values[CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctComments._name];
    			            amsg.m_paction="update";
		            	   
    			            if(this.form.find('itemId', 'approve')[0].checked)
    			            {
    			            	amsg.m_pSubscriberUpgradeStatus = CmFinoFIX.SubscriberUpgradeKycStatus.Approve;
    			            }
    			            else if(this.form.find('itemId', 'reject')[0].checked)
    			            {
    			            	amsg.m_pSubscriberUpgradeStatus = CmFinoFIX.SubscriberUpgradeKycStatus.Reject;
    			            }
    			            else if(this.form.find('itemId', 'revision')[0].checked)
    			            {
    			            	amsg.m_pSubscriberUpgradeStatus = CmFinoFIX.SubscriberUpgradeKycStatus.Revision;
    			            }
		                  
    			            var aparams = mFino.util.showResponse.getDisplayParam();
    			            mFino.util.fix.send(amsg, aparams);
    			            this.hide();
    			            
    			        }, this
    			   );
        		} else {
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
        
        if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name) != null){

			var docFullPath = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name)
			docFullPath = docFullPath.replace("\\","/");
			docFullPath = docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path == null || mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path == '' || mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path == undefined){
					mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path = docFullPath.substring(0, docFullPath.lastIndexOf('/')+1);
			}
			
			var docName = docFullPath.substring(docFullPath.lastIndexOf('/')+1, docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name, docName);
		}
    },

    setStore : function(store){
        this.store = store;
    },
    
    showImage:function(imageName){
    	var imagePath = mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path+imageName
		if(imagePath.indexOf('.') != -1){
			var window = new Ext.Window({
				layout:'anchor',
				width:500,
				height:500,
				autoScroll:true,
				bodyStyle:'backgroundColor:white',
				title:imageName,
				items:[{
					anchor : '100%',
					html: "<div display=\"block\" style=''>" + 
						
					"<div style=\"text-align:left;line-height:3px;padding:5px 3px 4px;\">" +
						"<span>" + 
								"<img height=300 width=400 alt=\"image\" src=\""+imagePath+"\" />" +
						"</span>" + 
					"</div>" +
				   "</div>"
				}]
			});
			window.show();
		}else{
			Ext.Msg.alert('Info', 'Document Not Available!');
		}
    }
    
});
Ext.reg("SubscriberUpgradeKycApproveRejectWindow", mFino.widget.SubscriberUpgradeKycApproveRejectWindow);