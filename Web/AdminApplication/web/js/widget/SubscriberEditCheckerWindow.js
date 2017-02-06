/**
 *SubscriberEditCheckerWindow.js 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberEditCheckerWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Approve/Reject Subscriber Edit"),
        floating: true,
        width: 650,
        height: 660,
        frame : true,
        labelWidth: 5,
        plain:true,
        layout: 'fit',
        closable:true
    });
    mFino.widget.SubscriberEditCheckerWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberEditCheckerWindow, Ext.Window, {
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
            bodyStyle: 'padding: 10px 10px 0 10px;',
            items : [
            	{
            		xtype: 'fieldset',
            	    title : _('Personal Data'),
            	   	anchor : '100%',
            	   	layout: 'column',
            	   	columns: 3,
            	   	items: [
            	   		{
            	   			columnWidth: 0.3,
            				layout: 'form',
            				labelWidth : 150,
            				title: _(""),
    						style:{
    							margin: '25px 0px 0px',
    							padding: '0px !important'
   							},
            				items : [
            					{
		                     		xtype : 'displayfield',
		                     		labelSeparator: _(''),
   									value: ":",
		                     		fieldLabel: _(' MDN')
		                     	},
								{
									xtype : 'displayfield',
		                     		labelSeparator: _(''),
   									value: ":",
									fieldLabel: _(' Full Name')
								},
								{
									xtype : 'displayfield',
    								style:{
    									margin: '5px 0px 0px 0px'
   									},
		                     		labelSeparator: _(''),
   									value: ":",
									fieldLabel: _(' ID Type')
								},
								{
									xtype : 'displayfield',
    								style:{
    									margin: '5px 0px 0px 0px'
   									},
		                     		labelSeparator: _(''),
   									value: ":",
									fieldLabel: _(' ID Number')
								},
								{
									xtype : 'displayfield',
    								style:{
    									margin: '5px 0px 0px 0px'
   									},
		                     		labelSeparator: _(''),
   									value: ":",
									fieldLabel: _(' Email')
								},
								{
									xtype : 'displayfield',
    								style:{
    									margin: '5px 0px 0px 0px'
   									},
		                     		labelSeparator: _(''),
   									value: ":",
									fieldLabel: _(' Photo of ID Card')
								},
								{
									xtype : 'displayfield',
    								style:{
    									margin: '5px 0px 0px 0px'
   									},
		                     		labelSeparator: _(''),
   									value: ":",
									fieldLabel: _(' Language')
								},
								{
								 	xtype : 'displayfield',
    								style:{
    									margin: '5px 0px 0px 0px'
   									},
		                     		labelSeparator: _(''),
   									value: ":",
								 	fieldLabel: _('Bank Account Number')
								}
            				]
            	   		},
            	   		{
            	   			columnWidth: 0.35,
            				layout: 'form',
            				labelWidth : 0.1,
            				title: _(""),
            				items : [
             					{
									xtype : "hidden",
									itemId : 'subeditchecker.form.mdnid',
									name: CmFinoFIX.message.JSSubscriberEdit.Entries.MDNID._name
								},
								
								{
									xtype : 'displayfield',
									value: _('Current Value'),
									labelSeparator: '',
									style:{
										'font-weight': 'bold',
										'font-size': '12px'
									},
									anchor : '100%'
								},
		                     	{
		                     		xtype : 'textfield',
		                     		anchor : '100%',
		                     		fieldLabel: _(''),
									labelSeparator : '',
            						readOnly:true,
									itemId : 'subeditchecker.old.form.mdn',
		                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.MDN._name
		                     	},
								{
									xtype : 'textfield',
									anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
            						readOnly:true,
									itemId : 'subeditchecker.old.form.fullname',
									name: CmFinoFIX.message.JSSubscriberEdit.Entries.FirstName._name
								},
								{
									xtype : 'enumdropdown',
									anchor : '100%',
		                     		fieldLabel: _(''),
									labelSeparator : '',
            						disabled:true,
									itemId : 'subeditchecker.old.form.idtype',
									enumId : CmFinoFIX.TagID.IDTypeForKycUpgrade,
									name: CmFinoFIX.message.JSSubscriberEdit.Entries.IDType._name
								},
								{
									xtype : 'textfield',
									anchor : '100%',
		                     		fieldLabel: _(''),
									labelSeparator : '',
            						readOnly:true,
									itemId : 'subeditchecker.old.form.idnumber',
									name: CmFinoFIX.message.JSSubscriberEdit.Entries.IDNumber._name
								},
								{
									xtype : 'textfield',
									anchor : '100%',
		                     		fieldLabel: _(''),
									labelSeparator : '',
            						readOnly:true,
									itemId : 'subeditchecker.old.form.email',
									name: CmFinoFIX.message.JSSubscriberEdit.Entries.Email._name
								},
								{
									xtype : 'displayfield',
									labelSeparator : '',
									anchor : '100%',
		                     		fieldLabel: _(''),
									name: CmFinoFIX.message.JSSubscriberEdit.KTPDocumentPath._name,
									itemId : 'subeditchecker.old.displayfield.idpath',
									style: {
										color: '#0000ff',
										cursor:'pointer'
									},
									listeners:{
							   	 		afterrender: function(component) {
							   	 			component.getEl().on('click', function() { 
							   	 				mFino.widget.SubscriberEditCheckerWindow.prototype.showImage(component.getValue())
							   	 			});  
							   	 		}
									}
								},
								{
									xtype : 'enumdropdown',
									anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
            						disabled:true,
									itemId : 'subeditchecker.old.form.language',
									enumId : CmFinoFIX.TagID.Language,
									name: CmFinoFIX.message.JSSubscriberEdit.Entries.Language._name
								},
								{
								 	xtype : 'textfield',
								 	itemId : 'subeditchecker.old.form.bankaccid',
								 	anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : ' ',
            						readOnly:true,
								 	name: CmFinoFIX.message.JSSubscriberEdit.Entries.AccountNumber._name
								}
            				]
            	   		},
            	   		{
            	   			columnWidth: 0.35,
            				layout: 'form',
            				labelWidth : 0.1,
            				title: _(""),
            				labelWidth: 10,
            				items : [
            				    {
            				        xtype : 'displayfield',
            				        value: _('New Value'),
            				        labelSeparator: '',
            				        style:{
            				        	 'font-weight': 'bold',
            				        	 'font-size': '12px'
            				        },
            				        anchor : '100%'
            				    },
		                     	{
		                     		xtype : 'textfield',
            						readOnly:true,
		                     		anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
									itemId : 'subeditchecker.form.mdn',
		                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.MDN._name
		                     	},
		                     	{
		                     		xtype : 'textfield',
            						readOnly:true,
		                     		anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
									itemId : 'subeditchecker.form.fullname',
		                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.FirstName._name
		                     	},
		                     	{
		                     		xtype : 'enumdropdown',
		                     		anchor : '100%',
            						disabled:true,
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
									itemId : 'subeditchecker.form.idtype',
									enumId : CmFinoFIX.TagID.IDTypeForKycUpgrade,
		                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.IDType._name
		                     	},
		                     	{
		                     		xtype : 'textfield',
            						readOnly:true,
		                     		anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
									itemId : 'subeditchecker.form.idnumber',
		                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.IDNumber._name
		                     	},
		                     	{
		                            xtype : 'textfield',
            						readOnly:true,
		                            anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
									itemId : 'subeditchecker.form.email',
		                            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Email._name
		                        },
		                     	{
		                            xtype : 'displayfield',
		                            fieldLabel: _(''),
		                     		labelSeparator : '',
		                            anchor : '100%',
		                            name: CmFinoFIX.message.JSSubscriberEdit.KTPDocumentPath._name,
									itemId : 'subeditchecker.displayfield.idpath',
		                            style: {
		            					color: '#0000ff',
		            					cursor:'pointer'
		            				},
		                            listeners:{
		                           	 	afterrender: function(component) {
		                           	 		component.getEl().on('click', function() { 
		                           	 			mFino.widget.SubscriberEditCheckerWindow.prototype.showImage(component.getValue())
		                           	        });  
		                           	    }
		                            }
		                        },
		                     	{
		                        	xtype : 'enumdropdown',
            						disabled:true,
		                     		anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '', 
									itemId : 'subeditchecker.form.language',
									enumId : CmFinoFIX.TagID.Language,
		                            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Language._name
		                        },
		                        {
		                       	 	xtype : 'textfield',
            						readOnly:true,
		                       	 	itemId : 'subeditchecker.form.bankaccid',
		                       	 	anchor : '100%',
		                     		fieldLabel: _(''),
                     				labelSeparator : '',
		                       	 	name: CmFinoFIX.message.JSSubscriberEdit.Entries.AccountNumber._name
		                        }
            				]
            	   		}
            	   	]
            	},
            	{
        			xtype:'tabpanel',
                    frame:true,
                    activeTab: 0,
                    border : false,
                    deferredRender:false,
                    itemId:'tabpanelsubeditchecker',
        		    anchor : '100%',
                    defaults:{
                        bodyStyle:'padding:10px'
                    },
                    items: [
        				{
        				    title: _('Address'),
        				    layout:'column',
        				    frame:true,
        				    autoHeight: true,
    						columns: 3,
        				    items:[subsAddressCheckerLable, subsAddressChecker, subsAddressChecker2]
        				},
        				{
        				    title: _('Notification Method'),
        				    layout:'column',
        				    frame:true,
        				    autoHeight: true,
    						columns: 2,
        				    items:[subsNotificationMethodChecker, subsNotificationMethodChecker2]
        				},
        				{
        				    title: _('Restriction'),
        				    layout:'column',
        				    frame:true,
        				    autoHeight: true,
    						columns: 2,
        				    items:[subsRestrictionsChecker, subsRestrictionsChecker2]
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
							id:'subeditchecker.commentsu',
							fieldLabel : _('Comments'),
							allowBlank: true,
							hideLabel: true,
							labelSeparator :'',
							itemId : 'subeditchecker.displayfield.commentsu',
							name: CmFinoFIX.message.JSSubscriberEdit.Entries.UpgradeAcctComments._name,
							anchor : '100%'
						},
						{
							columnWidth: 0.33,
							xtype : 'radio',
							itemId : 'subeditchecker.approve',
							name: 'selectone',
							anchor : '100%',
							checked : true,
							boxLabel: _('Approve')
						},
						{
							columnWidth: 0.33,
							xtype : 'radio',
							itemId : 'subeditchecker.reject',
							anchor : '100%',
							name: 'selectone',
							boxLabel: _('Reject')
						}
            	    ]
               }
            ]
        });
        
        this.items = [ this.form ];
        mFino.widget.SubscriberEditCheckerWindow.superclass.initComponent.call(this);
    },
    cancel : function(){
        this.hide();
    },
    ok : function(){
        	if(this.form.getForm().isValid()){
    			Ext.Msg.confirm(_("Confirm?"), _("Are you sure?"),
			        function(btn){
			            if(btn !== "yes"){
			                return;
			            }
			            
			            var amsg = new CmFinoFIX.message.JSSubscriberEdit();
			            var values = this.form.getForm().getValues();
			            amsg.m_pMDNID = values[CmFinoFIX.message.JSSubscriberEdit.Entries.MDNID._name];
			            amsg.m_pUpgradeAcctComments = values[CmFinoFIX.message.JSSubscriberEdit.Entries.UpgradeAcctComments._name];
			            amsg.m_paction="update";
	            	   
			            if(this.form.find('itemId', 'subeditchecker.approve')[0].checked)
			            {
			            	amsg.m_pSubscriberUpgradeStatus = CmFinoFIX.SubscriberUpgradeKycStatus.Approve;
			            }
			            else if(this.form.find('itemId', 'subeditchecker.reject')[0].checked)
			            {
			            	amsg.m_pSubscriberUpgradeStatus = CmFinoFIX.SubscriberUpgradeKycStatus.Reject;
			            }
	                  
			            var aparams = mFino.util.showResponse.getDisplayParam();
			            mFino.util.fix.send(amsg, aparams);
			            this.hide();
			            Ext.apply(aparams, {
                			success :  function(response){
                				this.scope.fireEvent("refresh", amsg.m_pID );
                				Ext.Msg.show({
                                    title: _('Success'),
                                    minProgressWidth:250,
                                    msg: response.m_pErrorDescription,
                                    buttons: Ext.MessageBox.OK,
                                    multiline: false
                   			   });
                			},
                			scope: this
                		});
			        }, this
			   );
        	}     
        	else{
        		Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        	}
    },
    
    setStore : function(store){
        this.store = store;
    },
    
    showImage:function(imageName){
    	var imagePath = mFino.widget.SubscriberEditCheckerWindow.path+imageName
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
    },
    setDetails : function(response, mdnid){
    	this.form.form.reset();
    	this.form.getForm().items.get("subeditchecker.form.mdnid").setValue(mdnid);
    	
    	if(response.m_ptotal == 2){
	        if(response.m_pEntries[0].m_pKTPDocumentPath != null){
				var docFullPath = response.m_pEntries[0].m_pKTPDocumentPath;
				docFullPath = docFullPath.replace("\\","/");
				docFullPath = docFullPath.replace("\\","/");
				if(mFino.widget.SubscriberEditCheckerWindow.path == null || mFino.widget.SubscriberEditCheckerWindow.path == '' || mFino.widget.SubscriberEditCheckerWindow.path == undefined){
						mFino.widget.SubscriberEditCheckerWindow.path = docFullPath.substring(0, docFullPath.lastIndexOf('/')+1);
				}
				var docName = docFullPath.substring(docFullPath.lastIndexOf('/')+1, docFullPath.length);
				this.form.getForm().items.get("subeditchecker.displayfield.idpath").setValue(docName);
			}else{
				this.form.getForm().items.get("subeditchecker.displayfield.idpath").setValue("-");
			}
    		this.form.getForm().items.get("subeditchecker.form.mdn").setValue(response.m_pEntries[0].m_pMDN);
	        this.form.getForm().items.get("subeditchecker.displayfield.commentsu").setValue(response.m_pEntries[0].m_pUpgradeAcctComments);
	        this.form.getForm().items.get("subeditchecker.form.fullname").setValue(response.m_pEntries[0].m_pFirstName);
	        this.form.getForm().items.get("subeditchecker.form.idtype").setValue(response.m_pEntries[0].m_pIDType);
	        this.form.getForm().items.get("subeditchecker.form.idnumber").setValue(response.m_pEntries[0].m_pIDNumber);
	        this.form.getForm().items.get("subeditchecker.form.email").setValue(response.m_pEntries[0].m_pEmail);
	        this.form.getForm().items.get("subeditchecker.form.ProvincialCom").setValue(response.m_pEntries[0].m_pRegionName);
	        this.form.getForm().items.get("subeditchecker.form.CityCom").setValue(response.m_pEntries[0].m_pCity);
	        this.form.getForm().items.get("subeditchecker.form.DistrictCom").setValue(response.m_pEntries[0].m_pState);
	        this.form.getForm().items.get("subeditchecker.form.VillageCom").setValue(response.m_pEntries[0].m_pSubState);
	        this.form.getForm().items.get("subeditchecker.form.StreetAddress").setValue(response.m_pEntries[0].m_pStreetAddress);
	        this.form.getForm().items.get("subeditchecker.form.language").setValue(response.m_pEntries[0].m_pLanguage);
        	if(response.m_pEntries[0].m_pAccountNumber == '#'){
        		this.form.getForm().items.get("subeditchecker.form.bankaccid").disable();
        	}else{
        		this.form.getForm().items.get("subeditchecker.form.bankaccid").setValue(response.m_pEntries[0].m_pAccountNumber);
        	}
	        var resValue = response.m_pEntries[0].m_pMDNRestrictions;
	        this.form.getForm().items.get("subeditchecker.Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
	        this.form.getForm().items.get("subeditchecker.SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
	        this.form.getForm().items.get("subeditchecker.AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
	        this.form.getForm().items.get("subeditchecker.NoFundMovement").setValue((resValue & CmFinoFIX.SubscriberRestrictions.NoFundMovement) > 0);

	        var notiValue = response.m_pEntries[0].m_pNotificationMethod;
	        this.form.getForm().items.get("subeditchecker.SMS").setValue( (notiValue & CmFinoFIX.NotificationMethod.SMS) > 0);
	        this.form.getForm().items.get("subeditchecker.Email1").setValue( ( notiValue & CmFinoFIX.NotificationMethod.Email) > 0);
	        // fill old value
	        if(response.m_pEntries[1].m_pKTPDocumentPath != null){
				var docFullPath = response.m_pEntries[1].m_pKTPDocumentPath;
				docFullPath = docFullPath.replace("\\","/");
				docFullPath = docFullPath.replace("\\","/");
				if(mFino.widget.SubscriberEditCheckerWindow.path == null || mFino.widget.SubscriberEditCheckerWindow.path == '' || mFino.widget.SubscriberEditCheckerWindow.path == undefined){
						mFino.widget.SubscriberEditCheckerWindow.path = docFullPath.substring(0, docFullPath.lastIndexOf('/')+1);
				}
				var docName = docFullPath.substring(docFullPath.lastIndexOf('/')+1, docFullPath.length);
				this.form.getForm().items.get("subeditchecker.old.displayfield.idpath").setValue(docName);
			} else{
				this.form.getForm().items.get("subeditchecker.old.displayfield.idpath").setValue('-');
			}
    		this.form.getForm().items.get("subeditchecker.old.form.mdn").setValue(response.m_pEntries[1].m_pMDN);
	        this.form.getForm().items.get("subeditchecker.old.form.fullname").setValue(response.m_pEntries[1].m_pFirstName);
	        this.form.getForm().items.get("subeditchecker.old.form.idtype").setValue(response.m_pEntries[1].m_pIDType);
	        this.form.getForm().items.get("subeditchecker.old.form.idnumber").setValue(response.m_pEntries[1].m_pIDNumber);
	        this.form.getForm().items.get("subeditchecker.old.form.email").setValue(response.m_pEntries[1].m_pEmail);
	        this.form.getForm().items.get("subeditchecker.old.form.ProvincialCom").setValue(response.m_pEntries[1].m_pRegionName);
	        this.form.getForm().items.get("subeditchecker.old.form.CityCom").setValue(response.m_pEntries[1].m_pCity);
	        this.form.getForm().items.get("subeditchecker.old.form.DistrictCom").setValue(response.m_pEntries[1].m_pState);
	        this.form.getForm().items.get("subeditchecker.old.form.VillageCom").setValue(response.m_pEntries[1].m_pSubState);
	        this.form.getForm().items.get("subeditchecker.old.form.StreetAddress").setValue(response.m_pEntries[1].m_pStreetAddress);
	        this.form.getForm().items.get("subeditchecker.old.form.language").setValue(response.m_pEntries[1].m_pLanguage);
	        this.form.getForm().items.get("subeditchecker.old.form.bankaccid").setValue(response.m_pEntries[1].m_pAccountNumber);
	        
	        var resValue = response.m_pEntries[1].m_pMDNRestrictions;
	        this.form.getForm().items.get("subeditchecker.old.Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
	        this.form.getForm().items.get("subeditchecker.old.SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
	        this.form.getForm().items.get("subeditchecker.old.AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
	        this.form.getForm().items.get("subeditchecker.old.NoFundMovement").setValue((resValue & CmFinoFIX.SubscriberRestrictions.NoFundMovement) > 0);

	        var notiValue = response.m_pEntries[1].m_pNotificationMethod;
	        this.form.getForm().items.get("subeditchecker.old.SMS").setValue( (notiValue & CmFinoFIX.NotificationMethod.SMS) > 0);
	        this.form.getForm().items.get("subeditchecker.old.Email1").setValue( ( notiValue & CmFinoFIX.NotificationMethod.Email) > 0);
        }
    }
});

/**
 * Subscriber Address
 */
var subsAddressCheckerLable = {
	title: _(''),
    autoHeight: true,
   	layout: 'form',
    columnWidth: 0.2,
    labelWidth : 100,
    style:{
    	margin: '25px 0px 0px'
    },
    items : [
     	{
		    xtype : "displayfield",
			anchor : '100%',
			value: ':',
            labelSeparator : '',
			fieldLabel :_("Province/Propinsi")
		},
		{
			xtype : "displayfield",
	        anchor : '100%',
			value: ':',
            labelSeparator : '',
	        fieldLabel :_("City/Kota")
		},
		{
			xtype : "displayfield",
            anchor : '100%',
			value: ':',
            labelSeparator : '',
			style:{
				margin: '5px 0px 0px 0px'
			},
            fieldLabel :_("District/Kecamatan")
		},
		{
			xtype : "displayfield",
            anchor : '100%',
			value: ':',
            labelSeparator : '',
			style:{
				margin: '5px 0px 0px 0px'
			},
            fieldLabel :_("Village/Kelurahan")
		},
		{
		    xtype : 'displayfield',
		    fieldLabel: _('Address/Alamat'),
			value: ':',
            labelSeparator : '',
			style:{
				margin: '5px 0px 0px 0px'
			},
		    anchor : '100%'
		}
    ]
}
var subsAddressChecker = {
	title: _(''),
    autoHeight: true,
    width: 430,
   	layout: 'form',
    columnWidth: 0.4,
    labelWidth : 10,
    items : [
     	{
		    xtype : 'displayfield',
		    value: _('Current Value'),
			labelSeparator: '',
			style:{
				'font-weight': 'bold',
				'font-size': '12px'
			},
		    anchor : '100%'
		},
		{
		    xtype : 'textfield',
    		readOnly:true,
			anchor : '100%',
			itemId : 'subeditchecker.old.form.ProvincialCom',
			name: CmFinoFIX.message.JSSubscriberEdit.Entries.RegionName._name
		},
		{
			xtype : 'textfield',
    		readOnly:true,
	        anchor : '100%',
	        itemId : 'subeditchecker.old.form.CityCom',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.City._name
		},
		{
			xtype : 'textfield',
    		readOnly:true,
            anchor : '100%',
            itemId : 'subeditchecker.old.form.DistrictCom',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.State._name
		},
		{
			xtype : 'textfield',
    		readOnly:true,
            anchor : '100%',
            itemId : 'subeditchecker.old.form.VillageCom',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.SubState._name
		},
		{
		    xtype : 'textfield',
    		readOnly:true,
		    anchor : '100%',
            itemId : 'subeditchecker.old.form.StreetAddress',
		    name: CmFinoFIX.message.JSSubscriberEdit.Entries.StreetAddress._name
		}
    ]
}

var subsAddressChecker2 = {
	title: _(''),
    autoHeight: true,
    width: 430,
   	layout: 'form',
    columnWidth: 0.4,
    labelWidth : 10,
    items : [
 		{
		    xtype : 'displayfield',
		    value: _('New Value'),
			style:{
				'font-weight': 'bold',
				'font-size': '12px'
			},
			labelSeparator: '',
		    anchor : '100%'
 		},
		{
		    xtype : 'textfield',
			readOnly:true,
			labelWidth : 0,
			anchor : '100%',
			itemId : 'subeditchecker.form.ProvincialCom',
			name: CmFinoFIX.message.JSSubscriberEdit.Entries.RegionName._name,
			style: {
        			margin:'0px 10px !important'
        	}
		},
		{
			xtype : 'textfield',
			readOnly:true,
			labelWidth : 0,
	        anchor : '100%',
	        itemId : 'subeditchecker.form.CityCom',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.City._name
		},
		{
			xtype : 'textfield',
			readOnly:true,
			labelWidth : 0,
            anchor : '100%',
            itemId : 'subeditchecker.form.DistrictCom',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.State._name
		},
		{
			xtype : 'textfield',
			readOnly:true,
			labelWidth : 0,
            anchor : '100%',
            itemId : 'subeditchecker.form.VillageCom',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.SubState._name
		},
		{
		    xtype : 'textfield',
			readOnly:true,
			labelWidth : 0,
		    anchor : '100%',
            itemId : 'subeditchecker.form.StreetAddress',
		    name: CmFinoFIX.message.JSSubscriberEdit.Entries.StreetAddress._name
		}
     ]
}   
/*
 * Notification Method
 **/
var subsNotificationMethodChecker = {
    title: _('Current Value'),
    autoHeight: true,
    width: 300,
    layout : 'column',
    autoHeight: true,
    columns: 2,
    columnWidth: 0.5,
    items: [
    	{
		    xtype : 'checkbox',
    		columnWidth: 1,
    		disabled:true,
		    itemId : 'subeditchecker.old.SMS',
		    boxLabel: sms
		},
		{
		    xtype : 'checkbox',
		    columnWidth: 1,
		    disabled:true,
		    itemId : 'subeditchecker.old.Email1',
		    boxLabel: email
		}
    	
    ]
};
var subsNotificationMethodChecker2 = {
    title: _('New Value'),
    autoHeight: true,
    width: 300,
    layout : 'column',
    autoHeight: true,
    columns: 2,
    columnWidth: 0.5,
    items: [
    	{
		    xtype : 'checkbox',
    		columnWidth: 1,
    		disabled:true,
		    itemId : 'subeditchecker.SMS',
		    boxLabel: sms
		},
		{
		    xtype : 'checkbox',
		    columnWidth: 1,
		    disabled:true,
		    itemId : 'subeditchecker.Email1',
		    boxLabel: email
		}
    	
    ]
};
/*
 * Subscriber Restrictions
 **/
var subsRestrictionsChecker = {
    title: _('Current Value'),
    autoHeight: true,
    width: 300,
    layout: 'column',
    columns: 1,
    columnWidth: 0.5,
    items : [
     	{
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.old.SecurityLocked',
        	boxLabel: securitylocked
        },
        {
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.old.AbsoluteLocked',
        	boxLabel: absolutelocked
        },
        {
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.old.Suspended',
        	boxLabel: suspended
        },
        {
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.old.NoFundMovement',
        	boxLabel: 'NoFundMovement'
        }
    ]
};
var subsRestrictionsChecker2 = {
    title: _('New Value'),
    autoHeight: true,
    width: 300,
    layout: 'column',
    columns: 1,
    columnWidth: 0.5,
    items : [
     	{
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.SecurityLocked',
        	boxLabel: securitylocked
        },
        {
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.AbsoluteLocked',
        	boxLabel: absolutelocked
        },
        {
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.Suspended',
        	boxLabel: suspended
        },
        {
        	xtype : 'checkbox',
			columnWidth: 1,
			disabled:true,
        	itemId : 'subeditchecker.NoFundMovement',
        	boxLabel: 'NoFundMovement'
        }
    ]
};
Ext.reg("SubscriberEditCheckerWindow", mFino.widget.SubscriberEditCheckerWindow);