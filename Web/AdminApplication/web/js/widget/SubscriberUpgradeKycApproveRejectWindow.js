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
        width: 650,
        height: 550,
        frame : true,
        labelWidth: 5,
        closable:false
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
            bodyStyle: 'padding: 10px 10px 0 10px;',
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
									xtype : "hidden",
									itemId : 'subupgradekyc.displayfield.mdnid',
									name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.ID._name
								},
            					{
            						xtype : 'displayfield',
                					fieldLabel: _('Full Name'),
                					anchor : '90%',
									itemId : 'subupgradekyc.displayfield.fullname',
                					name: CmFinoFIX.message.JSSubscriberUpgradeKyc.FirstName._name
            					},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('ID Type'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.idtype',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.IDType._name
                             	},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Birth Place'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.birthplace',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.BirthPlace._name
                             	},
                             	{
                                    xtype : 'displayfield',
                                    fieldLabel: _('Email'),
                                    vtype: 'email',
                                    anchor : '100%',
									itemId : 'subupgradekyc.displayfield.email',
                                    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Email._name
                                },
            					{
            						xtype : 'displayfield',
                					fieldLabel: _('Nationality'),
                					anchor : '90%',
									itemId : 'subupgradekyc.displayfield.Nationality',
                					name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Nationality._name
            					},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Job'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.Work',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Work._name
                             	},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Other Job'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.OtherWork',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.OtherWork._name
                             	},
                             	{
                                    xtype : 'displayfield',
                                    fieldLabel: _('Gender'),
                                    vtype: 'email',
                                    anchor : '100%',
									itemId : 'subupgradekyc.displayfield.Gender',
                                    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Gender._name
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
									itemId : 'subupgradekyc.displayfield.mothermaidenname',
                     				name: CmFinoFIX.message.JSSubscriberUpgradeKyc.MothersMaidenName._name
                     			},
                     			{
                             		xtype : 'displayfield',
                             		fieldLabel: _('ID Number'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.idnumber',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.IDNumber._name
                             	},
                             	{
                                    anchor : '100%',
                                    renderer: "date",
                                    xtype : 'displayfield',
                                    format : 'd-m-Y',
									itemId : 'subupgradekyc.displayfield.birthdate',
                                    fieldLabel: _('Date of Birth'),
                                    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.DateOfBirth._name
                                },
                             	{
                                    xtype : 'displayfield',
                                    fieldLabel: _('ID Card'),
                                    anchor : '100%',
                                    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.KTPDocumentPath._name,
									itemId : 'subupgradekyc.displayfield.idpath',
                                    style: {
                    					color: '#0000ff',
                    					cursor:'pointer'
                    				},
                                    listeners:{
                                   	 	afterrender: function(component) {
                                   	 		component.getEl().on('click', function() { 
                                   	 			mFino.widget.SubscriberUpgradeKycApproveRejectWindow.prototype.showImage(component.getValue())
                                   	        });  
                                   	    }
                                    }
                                },
            					{
            						xtype : 'displayfield',
                					fieldLabel: _('Marital Status'),
                					anchor : '90%',
									itemId : 'subupgradekyc.displayfield.MaritalStatus',
                					name: CmFinoFIX.message.JSSubscriberUpgradeKyc.MaritalStatus._name
            					},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Source Of Fund'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.SourceOfFund',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.SourceOfFund._name
                             	},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Average Monthly Income'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.Income',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Income._name
                             	},
                             	{
                                    xtype : 'displayfield',
                                    fieldLabel: _('Account Opening Purpose'),
                                    vtype: 'email',
                                    anchor : '100%',
									itemId : 'subupgradekyc.displayfield.GoalOfAcctOpening',
                                    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.GoalOfAcctOpening._name
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
									itemId : 'subupgradekyc.displayfield.ProvinceCom',
                					name: CmFinoFIX.message.JSSubscriberUpgradeKyc.RegionName._name
            					},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('District/Kecamatan'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.DistrictCom',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.State._name
                             	},
                             	{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Street Address/Alamat'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.StreetAddress',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.StreetAddress._name
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
									itemId : 'subupgradekyc.displayfield.CityCom',
                     				name: CmFinoFIX.message.JSSubscriberUpgradeKyc.City._name
                     			},
                     			{
                             		xtype : 'displayfield',
                             		fieldLabel: _('Village/Kelurahan'),
                             		anchor : '100%',
									itemId : 'subupgradekyc.displayfield.VillageCom',
                             		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.SubState._name
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
								maxLength : 255,
								hideLabel: true,
								labelSeparator :'',
								itemId : 'subupgradekyc.displayfield.commentsu',
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
								boxLabel: _('Approve')
							},
							{
								columnWidth: 0.33,
								xtype : 'radio',
								itemId : 'revision',
								anchor : '100%',
								name: 'selectone',
								boxLabel: _('Revision')
							},
							{
								columnWidth: 0.33,
								xtype : 'radio',
								itemId : 'reject',
								anchor : '100%',
								name: 'selectone',
								boxLabel: _('Reject')
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
        	if(this.form.getForm().isValid()){
    			Ext.Msg.confirm(_("Confirm?"), _("Are you sure?"),
			        function(btn){
			            if(btn !== "yes"){
			                return;
			            }
			            
			            var amsg = new CmFinoFIX.message.JSSubscriberUpgradeKyc();
			            var values = this.form.getForm().getValues();
			            amsg.m_pID = values[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];
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
			            Ext.apply(aparams, {
                			success :  function(response){
                				this.scope.fireEvent("refresh", amsg.m_pID );
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
								"<img width='100%' alt=\"image\" src=\""+imagePath+"\" />" +
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
    setDetails : function(response, mdnid, comments){
    	this.form.getForm().reset();
    	this.form.getForm().items.get("subupgradekyc.displayfield.mdnid").setValue(mdnid);
    	if(response.m_ptotal > 0){
    		console.log(response.m_pEntries[0]);
    		var d = response.m_pEntries[0].m_pDateOfBirth.m_Date;
    		var month = '' + (d.getMonth() + 1);
            var day = '' + d.getDate();
            var year = d.getFullYear();

	        if (month.length < 2) month = '0' + month;
	        if (day.length < 2) day = '0' + day;
	
	        var formatedDate = [year, month, day].join('-');
	        if(response.m_pEntries[0].m_pKTPDocumentPath != null){
				var docFullPath = response.m_pEntries[0].m_pKTPDocumentPath;
				docFullPath = docFullPath.replace("\\","/");
				docFullPath = docFullPath.replace("\\","/");
				if(mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path == null || mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path == '' || mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path == undefined){
						mFino.widget.SubscriberUpgradeKycApproveRejectWindow.path = docFullPath.substring(0, docFullPath.lastIndexOf('/')+1);
				}
				var docName = docFullPath.substring(docFullPath.lastIndexOf('/')+1, docFullPath.length);
				this.form.getForm().items.get("subupgradekyc.displayfield.idpath").setValue(docName);
			}
	        this.form.getForm().items.get("subupgradekyc.displayfield.commentsu").setValue(comments);
	        this.form.getForm().items.get("subupgradekyc.displayfield.fullname").setValue(response.m_pEntries[0].m_pFirstName);
	        this.form.getForm().items.get("subupgradekyc.displayfield.idtype").setValue(response.m_pEntries[0].m_pIDTypeText);
	        this.form.getForm().items.get("subupgradekyc.displayfield.idnumber").setValue(response.m_pEntries[0].m_pIDNumber);
	        this.form.getForm().items.get("subupgradekyc.displayfield.birthplace").setValue(response.m_pEntries[0].m_pBirthPlace);
	        this.form.getForm().items.get("subupgradekyc.displayfield.birthdate").setValue(formatedDate);
	        this.form.getForm().items.get("subupgradekyc.displayfield.mothermaidenname").setValue(response.m_pEntries[0].m_pMothersMaidenName);
	        this.form.getForm().items.get("subupgradekyc.displayfield.email").setValue(response.m_pEntries[0].m_pEmail);
	        this.form.getForm().items.get("subupgradekyc.displayfield.ProvinceCom").setValue(response.m_pEntries[0].m_pRegionName);
	        this.form.getForm().items.get("subupgradekyc.displayfield.CityCom").setValue(response.m_pEntries[0].m_pCity);
	        this.form.getForm().items.get("subupgradekyc.displayfield.DistrictCom").setValue(response.m_pEntries[0].m_pState);
	        this.form.getForm().items.get("subupgradekyc.displayfield.VillageCom").setValue(response.m_pEntries[0].m_pSubState);
	        this.form.getForm().items.get("subupgradekyc.displayfield.StreetAddress").setValue(response.m_pEntries[0].m_pStreetAddress);
	        this.form.getForm().items.get("subupgradekyc.displayfield.Nationality").setValue(response.m_pEntries[0].m_pNationality);
	        this.form.getForm().items.get("subupgradekyc.displayfield.Gender").setValue(response.m_pEntries[0].m_pGenderText);
	        this.form.getForm().items.get("subupgradekyc.displayfield.Work").setValue(response.m_pEntries[0].m_pWorkText);
	        this.form.getForm().items.get("subupgradekyc.displayfield.OtherWork").setValue(response.m_pEntries[0].m_pOtherWork);
	        this.form.getForm().items.get("subupgradekyc.displayfield.SourceOfFund").setValue(response.m_pEntries[0].m_pSourceOfFund);
	        this.form.getForm().items.get("subupgradekyc.displayfield.MaritalStatus").setValue(response.m_pEntries[0].m_pMaritalStatusText);
	        this.form.getForm().items.get("subupgradekyc.displayfield.Income").setValue(response.m_pEntries[0].m_pIncomeText);
	        this.form.getForm().items.get("subupgradekyc.displayfield.GoalOfAcctOpening").setValue(response.m_pEntries[0].m_pGoalOfAcctOpening);
        }
    }
});
Ext.reg("SubscriberUpgradeKycApproveRejectWindow", mFino.widget.SubscriberUpgradeKycApproveRejectWindow);