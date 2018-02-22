/**
 *SubscriberEditMakerForm.js 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberEditMakerForm = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    	id: "subscriberEditMaker",
        defaultType: 'textfield',
        fileUpload:true,
        width: 550,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.SubscriberEditMakerForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberEditMakerForm, Ext.FormPanel, {
    initComponent : function(){
        this.autoScroll = true;
        this.frame = true;
    	this.labelWidth = 220;
        this.labelPad = 5;
        this.defaults = {
            anchor: '90%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : ':'
        };
    	this.items = [{
		    xtype: 'fieldset',
		    title : _('Personal Data'),
		    autoHeight: true,
		    anchor : '100%',
		    columns: 1,
		    items: [
						{
							xtype : "hidden",
							itemId : 'subeditmaker.form.mdnid',
							name: CmFinoFIX.message.JSSubscriberEdit.Entries.MDNID._name
						},
						{
							xtype : "hidden",
							itemId : 'subeditmaker.form.notificationMethod',
							name: CmFinoFIX.message.JSSubscriberEdit.Entries.NotificationMethod._name
						},
						{
							xtype : "hidden",
							itemId : 'subeditmaker.form.mdnRestriction',
							name: CmFinoFIX.message.JSSubscriberEdit.Entries.MDNRestrictions._name
						},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' MDN'),
                     		allowBlank: false,
                     		anchor : '100%',
							itemId : 'subeditmaker.form.mdn',
                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.MDN._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Full Name'),
                     		allowBlank: false,
                     		blankText : _('Full Name is required'),
                     		anchor : '100%',
							itemId : 'subeditmaker.form.fullname',
                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.FirstName._name
                     	},
                     	{
                     		xtype : 'enumdropdown',
                     		fieldLabel: _(' ID Type'),
                     		anchor : '100%',
                     		triggerAction : "all",
							forceSelection : true,
							pageSize : 10,
							addEmpty : true,
                     		allowBlank: false,
                     		blankText : _('ID Type is required'),
							emptyText : _('<select one..>'),
							itemId : 'subeditmaker.form.idtype',
							enumId : CmFinoFIX.TagID.IDTypeForKycUpgrade,
                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.IDType._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' ID Number'),
                     		allowBlank: false,
                     		blankText : _('ID Number is required'),
                     		anchor : '100%',
							itemId : 'subeditmaker.form.idnumber',
                     		name: CmFinoFIX.message.JSSubscriberEdit.Entries.IDNumber._name
                     	},
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Email'),
                            vtype: 'email',
                            anchor : '100%',
							itemId : 'subeditmaker.form.email',
                            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Email._name
                        },
                     	{
                            xtype : 'fileuploadfield',
                            fieldLabel: _('Upload ID Card'),
                            buttonText: _('Browse'),
                            emptyText:_(''),
                            anchor : '100%',
							itemId : 'subeditmaker.form.idpath',
                            name: CmFinoFIX.message.JSSubscriberEdit.Entries.KTPDocumentPath._name
                        },
                     	{
                            xtype : 'displayfield',
                            fieldLabel: _(' '),
                     		labelSeparator : '',
                            anchor : '100%',
                            name: CmFinoFIX.message.JSSubscriberEdit.KTPDocumentPath._name,
							itemId : 'subeditmaker.form.displayfield.idpath',
                            style: {
            					color: '#0000ff',
            					cursor:'pointer'
            				},
                            listeners:{
                           	 	afterrender: function(component) {
                           	 		component.getEl().on('click', function() { 
                           	 			mFino.widget.SubscriberEditMakerForm.prototype.showImage(component.getValue())
                           	        });  
                           	    }
                            }
                        },
                     	{
                        	xtype : 'enumdropdown',
                     		fieldLabel: _(' Language'),
                     		anchor : '100%',
                     		triggerAction : "all",
							forceSelection : true,
							pageSize : 10,
							addEmpty : true,
                     		allowBlank: false,
                     		blankText : _('Language is required'),
							emptyText : _('<select one..>'),
							enumId : CmFinoFIX.TagID.Language,
                            anchor : '100%',
							itemId : 'subeditmaker.form.language',
                            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Language._name
                        },
                        {
                       	 	xtype : 'textfield',
                            fieldLabel: _('Bank Account Number'),
                       	 	itemId : 'subeditmaker.form.bankaccid',
                       	 	anchor : '100%',
                       	 	name: CmFinoFIX.message.JSSubscriberEdit.Entries.AccountNumber._name
                        },
                        {
                       	 	xtype : 'hidden',
                       	 	itemId : 'subeditmaker.form.hidden.status',
                       	 	anchor : '100%',
                       	 	name: CmFinoFIX.message.JSSubscriberEdit.Entries.AccountNumber._name
                        },
                        {
                            xtype : "enumdropdown",
                            anchor : '100%',
                            allowBlank: false,
                            blankText : _('Status is required'),
                            itemId : 'subeditmaker.form.status',
                            emptyText : _('<select one..>'),
                            fieldLabel :status,
                            addEmpty: false,
                            enumId : CmFinoFIX.TagID.SubscriberStatus,
                            name : CmFinoFIX.message.JSSubscriberEdit.Entries.SubscriberStatus._name,
                            value : CmFinoFIX.MDNStatus.Initialized,
                            listeners : {
                                select :  function(field){
                                    var status= field.getValue();
                                    this.findParentByType('SubscriberEditMakerForm').onStatusDropdown(status);
                                }
                            }
                        }
            ]
		},
        {
			xtype:'tabpanel',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            itemId:'tabpanelsubeditmaker',
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
				    items:[subsAddress]
				},
				{
				    title: _('Notification Method'),
				    layout:'column',
				    frame:true,
				    autoHeight: true,
				    items:[subsEditMakerNotificationMethod]
				},
				{
				    title: _('Restriction'),
				    layout:'column',
				    frame:true,
				    autoHeight: true,
				    items:[subsEditMakerRestrictions]
				},
				{
					title: _('Additional Data'),
				    layout:'column',
				    frame:true,
				    autoHeight: true,
					items: [subsEditMakerAdditionalData]
				}
			]
        }]
    	mFino.widget.SubscriberEditMakerForm.superclass.initComponent.call(this);
    	markMandatoryFields(this.form);
    },
    onSubscriberEdit : function(formWindow){
    	var status = this.form.items.get("subeditmaker.form.status").getValue();
    	
    	if(this.getForm().isValid()){
        	
           var notiValue = 0;
           if(this.form.items.get("subeditmaker.SMS").checked){
                notiValue = notiValue + CmFinoFIX.NotificationMethod.SMS;
           }
           if(this.form.items.get("subeditmaker.Email1").checked){
                notiValue = notiValue + CmFinoFIX.NotificationMethod.Email;
           }

           var resValue = 0;
           if(this.form.items.get("subeditmaker.Suspended").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.Suspended;
           }
           if(this.form.items.get("subeditmaker.SecurityLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.SecurityLocked;
           }
           if(this.form.items.get("subeditmaker.AbsoluteLocked").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.AbsoluteLocked;
           }
           if(this.form.items.get("subeditmaker.NoFundMovement").checked){
                resValue = resValue + CmFinoFIX.SubscriberRestrictions.NoFundMovement;
           }
           this.getForm().items.get("subeditmaker.form.notificationMethod").setValue(notiValue);
           this.getForm().items.get("subeditmaker.form.mdnRestriction").setValue(resValue);
           
           this.getForm().items.get("subeditmaker.form.status").enable();
           
     	   this.getForm().submit({
     		   url: 'subscribereditmaker.htm',
     		   waitMsg: _('Processing Subscriber Edit...'),
     		   reset: false,
     		   success : function(fp, action){
     			   formWindow.fireEvent("refresh");
     			   formWindow.hide();
     			   Ext.Msg.show({
                      title: _('Success'),
                      minProgressWidth:250,
                      msg: "Request to edit subscriber datas has been submitted successfully. The data will be updated once approved.",
                      buttons: Ext.MessageBox.OK,
                      multiline: false
     			   });
     		   },
     		   failure : function(fp, action){
     			   Ext.Msg.show({
                      title: _('Error'),
                      minProgressWidth:250,
                      msg: action.result.Error,
                      buttons: Ext.MessageBox.OK,
                      multiline: false
                  });
     		   },
     		   params: {
     		   }
     	   	});
        }     
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    onProvince2 : function(provinceId){
    	var region_combo = this.find('itemId','subeditmaker.form.CityCom')[0];
    	region_combo.reset();
    	region_combo.store.reload({
    		params: {
    			IdProvince : provinceId
    		}
    	});
    },
    onProvinceRegion2 : function(regionId){
    	var district_combo = this.find('itemId','subeditmaker.form.DistrictCom')[0];
    	district_combo.reset();
    	district_combo.store.reload({
    		params: {
    			IdRegion : regionId
    		}
    	});
    },
    onDistrict2 : function(districtId){
    	var village_combo = this.find('itemId','subeditmaker.form.VillageCom')[0];
    	village_combo.reset();
    	village_combo.store.reload({
    		params: {
    			IdDistrict : districtId
    		}
    	});
    },
    setStore : function(store){
        this.store = store;
    },
    setDetails : function(response, mdnid){
        this.form.reset();
    	this.form.items.get("subeditmaker.form.mdnid").setValue(mdnid);
    	this.form.items.get("subeditmaker.form.mdn").disable();
    	
    	if(response.m_ptotal > 0){
    		var existingIndexData = response.m_ptotal - 1;
    		
	        if(response.m_pEntries[existingIndexData].m_pKTPDocumentPath != null){
				var docFullPath = response.m_pEntries[existingIndexData].m_pKTPDocumentPath;
				docFullPath = docFullPath.replace("\\","/");
				docFullPath = docFullPath.replace("\\","/");
				if(mFino.widget.SubscriberEditMakerForm.path == null || mFino.widget.SubscriberEditMakerForm.path == '' || mFino.widget.SubscriberEditMakerForm.path == undefined){
						mFino.widget.SubscriberEditMakerForm.path = docFullPath.substring(0, docFullPath.lastIndexOf('/')+1);
				}
				var docName = docFullPath.substring(docFullPath.lastIndexOf('/')+1, docFullPath.length);
	        	this.form.items.get("subeditmaker.form.idpath").setValue(docName);
	        	this.form.items.get("subeditmaker.form.displayfield.idpath").setValue(docName);
	        	this.form.items.get("subeditmaker.form.displayfield.idpath").show();
			}
	        var resValue = response.m_pEntries[existingIndexData].m_pMDNRestrictions;
	        this.form.items.get("subeditmaker.Suspended").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.Suspended) > 0);
	        this.form.items.get("subeditmaker.SecurityLocked").setValue( ( resValue & CmFinoFIX.SubscriberRestrictions.SecurityLocked) > 0);
	        this.form.items.get("subeditmaker.AbsoluteLocked").setValue((resValue & CmFinoFIX.SubscriberRestrictions.AbsoluteLocked) > 0);
	        this.form.items.get("subeditmaker.NoFundMovement").setValue((resValue & CmFinoFIX.SubscriberRestrictions.NoFundMovement) > 0);

	        var notiValue = response.m_pEntries[existingIndexData].m_pNotificationMethod;
	        this.form.items.get("subeditmaker.SMS").setValue( (notiValue & CmFinoFIX.NotificationMethod.SMS) > 0);
	        this.form.items.get("subeditmaker.Email1").setValue( ( notiValue & CmFinoFIX.NotificationMethod.Email) > 0);
	        
    		this.form.items.get("subeditmaker.form.mdn").setValue(response.m_pEntries[existingIndexData].m_pMDN);
    		this.form.items.get("subeditmaker.form.fullname").setValue(response.m_pEntries[existingIndexData].m_pFirstName);
        	this.form.items.get("subeditmaker.form.idtype").setValue(response.m_pEntries[existingIndexData].m_pIDType);
        	this.form.items.get("subeditmaker.form.idnumber").setValue(response.m_pEntries[existingIndexData].m_pIDNumber);
        	this.form.items.get("subeditmaker.form.email").setValue(response.m_pEntries[existingIndexData].m_pEmail);
        	this.form.items.get("subeditmaker.form.language").setValue(response.m_pEntries[existingIndexData].m_pLanguage);
        	this.form.items.get("subeditmaker.form.status").setValue(response.m_pEntries[existingIndexData].m_pSubscriberStatus);
        	this.form.items.get("subeditmaker.form.hidden.status").setValue(response.m_pEntries[existingIndexData].m_pSubscriberStatus);
        	
        	this.onStatusDropdown(response.m_pEntries[existingIndexData].m_pSubscriberStatus);
        	
        	if(response.m_pEntries[existingIndexData].m_pAccountNumber == '#'){
        		this.form.items.get("subeditmaker.form.bankaccid").disable();
        	}else{
        		this.form.items.get("subeditmaker.form.bankaccid").setValue(response.m_pEntries[existingIndexData].m_pAccountNumber);
        	}
        	
        	this.form.items.get("subeditmaker.form.ProvincialCom").setValue(response.m_pEntries[existingIndexData].m_pRegionName);
        	this.form.items.get("subeditmaker.form.CityCom").setValue(response.m_pEntries[existingIndexData].m_pCity);
        	this.form.items.get("subeditmaker.form.DistrictCom").setValue(response.m_pEntries[existingIndexData].m_pState);
        	this.form.items.get("subeditmaker.form.VillageCom").setValue(response.m_pEntries[existingIndexData].m_pSubState);
        	this.form.items.get("subeditmaker.form.StreetAddress").setValue(response.m_pEntries[existingIndexData].m_pStreetAddress);
        	
        	this.form.items.get("subeditmaker.form.nationality").setValue(response.m_pEntries[existingIndexData].m_pNationality);
    		this.form.items.get("subeditmaker.form.gender").setValue(response.m_pEntries[existingIndexData].m_pGender);
        	this.form.items.get("subeditmaker.form.work").setValue(response.m_pEntries[existingIndexData].m_pWork);
        	this.form.items.get("subeditmaker.form.otherwork").setValue(response.m_pEntries[existingIndexData].m_pOtherWork);
        	this.form.items.get("subeditmaker.form.maritalstatus").setValue(response.m_pEntries[existingIndexData].m_pMaritalStatus);
        	this.form.items.get("subeditmaker.form.sourceoffund").setValue(response.m_pEntries[existingIndexData].m_pSourceOfFund);
        	this.form.items.get("subeditmaker.form.avgincome").setValue(response.m_pEntries[existingIndexData].m_pIncome);
        	this.form.items.get("subeditmaker.form.GoalOfAcctOpening").setValue(response.m_pEntries[existingIndexData].m_pGoalOfAcctOpening);
        	
        }
    },
    onView: function(){
    	this.form.items.each(function(item) {
            if(item.getXType() == 'textfield' || item.getXType() == 'combo' || item.getXType() == 'checkbox' || 
                item.getXType() == 'textarea' || item.getXType() == 'numberfield' || item.getXType() == 'button' || 
                item.getXType() == 'datefield' ||item.getXType() == 'checkboxgroup' ||
                item.getXType() == 'enumdropdown'||item.getXType() == 'remotedropdown') {
                //Disable the item
                item.disable();
            }
        });
    	console.log(this);
    },
    showImage:function(imageName){
    	var imagePath = mFino.widget.SubscriberEditMakerForm.path+imageName
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
    onStatusDropdown : function(status){
    	var items = this.form.items.keys;
    	if(status == CmFinoFIX.MDNStatus.PendingRetirement || status==CmFinoFIX.MDNStatus.Retired){
            for(var i=0;i<items.length;i++){
            	this.find('itemId',items[i])[0].disable();
            }
            
        }else{
        	for(i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].enable();
            }
            
            if((status == CmFinoFIX.MDNStatus.Active) || (status == CmFinoFIX.MDNStatus.Initialized)) {
            	this.find('itemId','subeditmaker.form.status')[0].enable();
            } else {
            	this.find('itemId','subeditmaker.form.status')[0].disable();
            }
        }
        
    	this.find('itemId','subeditmaker.form.mdn')[0].disable();
    	this.find('itemId','subeditmaker.form.mdnid')[0].enable();
    },
    onSuspendClick: function(){
    	var currentStatus = this.form.items.get("subeditmaker.form.hidden.status").getValue();
        if(this.form.items.get("subeditmaker.Suspended").checked) {
            this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
        } else {
        	if (CmFinoFIX.SubscriberStatus.Suspend == currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("subeditmaker.form.status").setValue(currentStatus);
        }
    },
    onSecurityLockClick: function(){
    	var currentStatus = this.form.items.get("subeditmaker.form.hidden.status").getValue();
        if(this.form.items.get("subeditmaker.SecurityLocked").checked) {
            this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("subeditmaker.Suspended").checked) {
                this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive == currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("subeditmaker.form.status").setValue(currentStatus);
        }
    },
    onAbsoluteLockClick: function(){
    	var currentStatus = this.form.items.get("subeditmaker.form.hidden.status").getValue();
        if(this.form.items.get("subeditmaker.AbsoluteLocked").checked) {
            this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("subeditmaker.Suspended").checked) {
                this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive == currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Initialized;
        	}
            this.form.items.get("subeditmaker.form.status").setValue(currentStatus);
        }
    },
     onNoFundMovementClick: function(){
    	var currentStatus = this.form.items.get("subeditmaker.form.hidden.status").getValue();
        if(this.form.items.get("subeditmaker.NoFundMovement").checked) {
            this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.InActive);
            if(this.form.items.get("subeditmaker.Suspended").checked) {
                this.form.items.get("subeditmaker.form.status").setValue(CmFinoFIX.SubscriberStatus.Suspend);
            }
        } else {
        	if (CmFinoFIX.SubscriberStatus.InActive == currentStatus) {
        		currentStatus = CmFinoFIX.SubscriberStatus.Active;
        		if(this.form.items.get("subeditmaker.Suspended").checked) {
                	currentStatus = CmFinoFIX.SubscriberStatus.Suspend;
            	}
        	}
            this.form.items.get("subeditmaker.form.status").setValue(currentStatus);
        }
    },
    onJobSelect: function(){
    	var work = this.find('itemId', 'subeditmaker.form.work')[0];
    	if(CmFinoFIX.JobList.Lainnya == work.getValue()){
    		this.form.items.get("subeditmaker.form.otherwork").getEl().up('.x-form-item').setDisplayed(true);
    	}else{
    		this.form.items.get("subeditmaker.form.otherwork").getEl().up('.x-form-item').setDisplayed(false);
    	}
    },
});

/**
 * Subscriber Address
 */

var subsAddress = {
	title: _(''),
    autoHeight: true,
    width: 430,
    layout: 'form',
    items : [
    	{
		    xtype : "combo",
			anchor : '100%',
			fieldLabel :_(" Province/Propinsi"),
			itemId : 'subeditmaker.form.ProvincialCom',
			triggerAction: "all",
			editable:false,
			allowBlank: false,
			blankText : _('Province/Propinsi is required'),
			emptyText : '<Select one..>',
			name: CmFinoFIX.message.JSSubscriberEdit.Entries.RegionName._name,
			store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvince), 
			displayField: CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
			valueField : CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
			hiddenName : CmFinoFIX.message.JSSubscriberEdit.Entries.RegionName._name,
			listeners: {
			 	select: function(field, record) {
			 		this.findParentByType('SubscriberEditMakerForm').onProvince2(record.id);
			 	}
			}
		},
		{
			xtype : "combo",
	       	editable:false,
	        anchor : '100%',
	        fieldLabel :_(" City/Kota"),
	        itemId : 'subeditmaker.form.CityCom',
            triggerAction: "all",
            allowBlank: false,
            blankText : _('City/Kota is required'),
            emptyText : '<Select one..>',
            mode: 'local',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.City._name,
            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvinceRegion), 
            displayField: CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
            valueField : CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
            hiddenName : CmFinoFIX.message.JSSubscriberEdit.Entries.City._name,
			listeners: {
				 	select: function(field,record) {
				 	this.findParentByType('SubscriberEditMakerForm').onProvinceRegion2(record.id);
			    }
			}
		},
		{
			xtype : "combo",
        	editable:false,
            anchor : '100%',
            fieldLabel :_(" District/Kecamatan"),
            itemId : 'subeditmaker.form.DistrictCom',
            allowBlank: false,
            blankText : _('District/Kecamatan is required'),
            triggerAction: "all",
            emptyText : '<Select one..>',
            mode: 'local',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.State._name,
            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSDistrict), 
            displayField: CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
            valueField : CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
            hiddenName : CmFinoFIX.message.JSSubscriberEdit.Entries.State._name,
			listeners: {
				 	select: function(field,record) {
				 	this.findParentByType('SubscriberEditMakerForm').onDistrict2(record.id);
			    }
			}
		},
		{
			xtype : "combo",
            editable:false,
            anchor : '100%',
            fieldLabel :_(" Village/Kelurahan"),
            itemId : 'subeditmaker.form.VillageCom',
            allowBlank: false,
            blankText : _('Village/Kelurahan is required'),
            triggerAction: "all",
            emptyText : '<Select one..>',
            mode: 'local',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.SubState._name,
            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSVillage), 
            displayField: CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
            valueField : CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
            hiddenName : CmFinoFIX.message.JSSubscriberEdit.Entries.SubState._name,
		},
		{
		    xtype : 'textfield',
		    fieldLabel: _(' Address/Alamat'),
		    allowBlank: false,
            blankText : _('Alamat is required'),
		    anchor : '100%',
            itemId : 'subeditmaker.form.StreetAddress',
		    name: CmFinoFIX.message.JSSubscriberEdit.Entries.StreetAddress._name
		}
    ]
}

/*
 * Notification Method
 **/
var subsEditMakerNotificationMethod = {
    title: _(''),
    autoHeight: true,
    width: 300,
    layout: 'form',
    items : [{
    	layout : 'column',
        autoHeight: true,
        columns: 2,
        style : {
            margin: '5px'
        },
        items: [
            {
        		columnWidth: 0.5,
        		xtype : 'checkbox',
        		itemId : 'subeditmaker.SMS',
        		boxLabel: sms
        	},
        	{
        		columnWidth: 0.5,
        		xtype : 'checkbox',
        		itemId : 'subeditmaker.Email1',
        		boxLabel: email
        	}
        ]
    }]
};

/*
 * Subscriber Restrictions
 **/
var subsEditMakerRestrictions = {
    title: _(''),
    autoHeight: true,
    width: 300,
    layout: 'form',
    items : [{
        layout : 'column',
        autoHeight: true,
        columns: 2,
        style : {
            margin: '5px'
        },
        items: [
                {
                	columnWidth: 0.5,
                	xtype : 'checkbox',
                	itemId : 'subeditmaker.SecurityLocked',
                	boxLabel: securitylocked,
                	listeners: {
                        check: function() {
                            this.findParentByType("SubscriberEditMakerForm").onSecurityLockClick();
                        }
                    }
                },
                {
                	columnWidth: 0.5,
                	xtype : 'checkbox',
                	itemId : 'subeditmaker.AbsoluteLocked',
                	boxLabel: absolutelocked,
                	listeners: {
                        check: function() {
                            this.findParentByType("SubscriberEditMakerForm").onAbsoluteLockClick();
                        }
                    }
                },
                {
                	columnWidth: 0.5,
                	xtype : 'checkbox',
                	itemId : 'subeditmaker.Suspended',
                	boxLabel: suspended,
                	listeners: {
                        check: function() {
                            this.findParentByType("SubscriberEditMakerForm").onSuspendClick();
                        }
                    }
                },
                {
                	columnWidth: 0.5,
                	xtype : 'checkbox',
                	itemId : 'subeditmaker.NoFundMovement',
                	boxLabel: 'NoFundMovement',
                	listeners: {
                        check: function() {
                            this.findParentByType("SubscriberEditMakerForm").onNoFundMovementClick();
                        }
                    }
                }
          ]
    }]
};

/*
 * Subscriber Additional Data
 **/
var subsEditMakerAdditionalData = {
	title: _(''),
	autoHeight: true,
	width: 430,
	layout: 'form',
    items : [
    	{
            xtype : 'textfield',
            fieldLabel: _('Nationality'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.nationality',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Nationality._name,
            value: 'Indonesia'
        },
     	{
            xtype : 'enumdropdown',
            fieldLabel: _('Job'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.work',
			enumId : CmFinoFIX.TagID.JobList,
			emptyText : _('<select one..>'),
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Work._name,
            listeners: {
			 	select: function(field,record) {
				 	this.findParentByType('SubscriberEditMakerForm').onJobSelect();
			    }
			}
        },
     	{
            xtype : 'textfield',
            fieldLabel: _('Other Job'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.otherwork',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.OtherWork._name
        },
     	{
            xtype : 'enumdropdown',
            fieldLabel: _('Gender'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.gender',
			enumId : CmFinoFIX.TagID.Gender,
			emptyText : _('<select one..>'),
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Gender._name
        },
     	{
            xtype : 'enumdropdown',
            fieldLabel: _('Marital Status'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.maritalstatus',
			enumId : CmFinoFIX.TagID.MaritalStatusList,
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.MaritalStatus._name
        },
     	{
            xtype : 'textfield',
            fieldLabel: _('Source of Fund'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.sourceoffund',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.SourceOfFund._name
        },
     	{
            xtype : 'enumdropdown',
            fieldLabel: _('Average Monthly Income'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.avgincome',
			enumId : CmFinoFIX.TagID.AvgIncomeList,
			emptyText : _('<select one..>'),
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.Income._name
        },
     	{
            xtype : 'textfield',
            fieldLabel: _('Account Opening Purpose'),
            emptyText:_(''),
            anchor : '100%',
			itemId : 'subeditmaker.form.GoalOfAcctOpening',
			value : 'Transaksi',
            name: CmFinoFIX.message.JSSubscriberEdit.Entries.GoalOfAcctOpening._name
        }
    ]
};
Ext.reg("SubscriberEditMakerForm", mFino.widget.SubscriberEditMakerForm);
