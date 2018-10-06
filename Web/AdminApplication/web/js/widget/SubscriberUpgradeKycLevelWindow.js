/**
 *SubscriberUpgradeKycLevelWindow.js 
 */
Ext.ns("mFino.widget");

mFino.widget.SubscriberUpgradeKycLevelWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    	id: "upgrageKycLevel",
        defaultType: 'textfield',
        fileUpload:true,
        width: 600,
        height: 600,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.SubscriberUpgradeKycLevelWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberUpgradeKycLevelWindow, Ext.FormPanel, {
    initComponent : function(){
        this.autoScroll = true;
        this.draggable=true;
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
							itemId : 'subupgradekyc.form.mdnid',
							name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.ID._name
						},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Full Name'),
                     		allowBlank: false,
                            maxLength : 255,
                     		blankText : _('Full Name is required'),
                     		anchor : '100%',
							itemId : 'subupgradekyc.form.fullname',
                     		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.FirstName._name
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
							itemId : 'subupgradekyc.form.idtype',
							enumId : CmFinoFIX.TagID.IDTypeForKycUpgrade,
                     		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.IDType._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' ID Number'),
                     		allowBlank: false,
                            maxLength : 30,
                     		blankText : _('ID Number is required'),
                     		anchor : '100%',
							itemId : 'subupgradekyc.form.idnumber',
                     		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.IDNumber._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Birth Place'),
                     		allowBlank: false,
                            maxLength : 255,
                     		blankText : _('Birth Place is required'),
                     		anchor : '100%',
							itemId : 'subupgradekyc.form.birthplace',
                     		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.BirthPlace._name
                     	},
                     	{
                     		xtype : 'datefield',
                          	allowBlank: false,
                          	editable: false,
                       	    fieldLabel: _('Date of Birth'),
                       	    format : 'd-m-Y',
                       	    blankText : _('Birth Date is required'),
                       	    itemId : 'subupgradekyc.form.birthdate',
                       	    anchor : '100%',
                       	    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.DateOfBirth._name,
                            maxValue:new Date().add('d',-1),
                            maxText:'Date of birth should not be future date'
                        },
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Mother Maiden Name'),
                     		allowBlank: false,
                            maxLength : 255,
                     		blankText : _('Mother Maiden Name is required'),
                     		anchor : '100%',
							itemId : 'subupgradekyc.form.mothermaidenname',
                     		name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.MothersMaidenName._name
                     	},
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Email'),
                            vtype: 'email',
                            maxLength : 255,
                            anchor : '100%',
							itemId : 'subupgradekyc.form.email',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.Email._name
                        },
                     	{
                            xtype : 'fileuploadfield',
                            fieldLabel: _('Upload ID Card'),
                            buttonText: _('Browse'),
                     		blankText : _('Scan ID is required'),
                            emptyText:_(''),
                            anchor : '100%',
							itemId : 'subupgradekyc.form.idpath',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.KTPDocumentPath._name
                        },
                     	{
                            xtype : 'displayfield',
                            fieldLabel: _('Uploaded ID Card'),
                            anchor : '100%',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.KTPDocumentPath._name,
							itemId : 'subupgradekyc.form.displayfield.idpath',
                            style: {
            					color: '#0000ff',
            					cursor:'pointer'
            				},
                            listeners:{
                           	 	afterrender: function(component) {
                           	 		component.getEl().on('click', function() { 
                           	 			mFino.widget.SubscriberUpgradeKycLevelWindow.prototype.showImage(component.getValue())
                           	        });  
                           	    }
                            }
                        },
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Nationality'),
                            emptyText:_(''),
                            allowBlank: false,
                            anchor : '100%',
							itemId : 'subupgradekyc.form.nationality',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.Nationality._name,
                            value: 'Indonesia'
                        },
                     	{
                            xtype : 'enumdropdown',
                            fieldLabel: _('Job'),
                            emptyText:_(''),
                            allowBlank: false,
                            anchor : '100%',
							itemId : 'subupgradekyc.form.work',
							enumId : CmFinoFIX.TagID.JobList,
							emptyText : _('<select one..>'),
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.Work._name,
                            listeners: {
							 	select: function(field,record) {
								 	this.findParentByType('SubscriberUpgradeKycLevelWindow').onJobSelect();
							    }
							}
                        },
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Other Job'),
                            emptyText:_(''),
                            allowBlank: false,
                            anchor : '100%',
							itemId : 'subupgradekyc.form.otherwork',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.OtherWork._name
                        },
                     	{
                            xtype : 'enumdropdown',
                            fieldLabel: _('Gender'),
                            emptyText:_(''),
                            allowBlank: false,
                            anchor : '100%',
							itemId : 'subupgradekyc.form.gender',
							enumId : CmFinoFIX.TagID.Gender,
							emptyText : _('<select one..>'),
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.Gender._name
                        },
                     	{
                            xtype : 'enumdropdown',
                            fieldLabel: _('Marital Status '),
                            emptyText : _('<select one..>'),
                            allowBlank: false,
                            anchor : '100%',
							itemId : 'subupgradekyc.form.maritalstatus',
							enumId : CmFinoFIX.TagID.MaritalStatusList,
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.MaritalStatus._name
                        },
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Source of Fund'),
                            emptyText:_(''),
                            anchor : '100%',
                            allowBlank: false,
							itemId : 'subupgradekyc.form.sourceoffund',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.SourceOfFund._name
                        },
                     	{
                            xtype : 'enumdropdown',
                            fieldLabel: _('Average Monthly Income'),
                            emptyText:_(''),
                            anchor : '100%',
                            allowBlank: false,
							itemId : 'subupgradekyc.form.avgincome',
							enumId : CmFinoFIX.TagID.AvgIncomeList,
							emptyText : _('<select one..>'),
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.Income._name
                        },
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Account Opening Purpose'),
                            emptyText:_(''),
                            anchor : '100%',
                            allowBlank: false,
							itemId : 'subupgradekyc.form.GoalOfAcctOpening',
							value : 'Transaksi',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.GoalOfAcctOpening._name
                        }
            ]
		},
        {
            xtype: 'fieldset',
            title : _('Address'),
            autoHeight: true,
            anchor : '100%',
            columns: 1,
            items: [
						{
						    xtype : "combo",
							anchor : '100%',
							fieldLabel :_(" Province/Propinsi"),
							itemId : 'subupgradekyc.form.ProvincialCom',
							triggerAction: "all",
							editable:false,
							allowBlank: false,
							blankText : _('Province/Propinsi is required'),
							emptyText : '<Select one..>',
							name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.RegionName._name,
							store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvince), 
							displayField: CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
							valueField : CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
							hiddenName : CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.RegionName._name,
							listeners: {
							 	select: function(field, record) {
							 		this.findParentByType('SubscriberUpgradeKycLevelWindow').onProvince2(record.id);
							 	}
							}
						},
						{
							xtype : "combo",
					       	editable:false,
					        anchor : '100%',
					        fieldLabel :_(" City/Kota"),
					        itemId : 'subupgradekyc.form.CityCom',
				            triggerAction: "all",
				            allowBlank: false,
				            blankText : _('City/Kota is required'),
				            emptyText : '<Select one..>',
				            mode: 'local',
				            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.City._name,
				            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvinceRegion), 
				            displayField: CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
				            valueField : CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
				            hiddenName : CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.City._name,
							listeners: {
								 	select: function(field,record) {
								 	this.findParentByType('SubscriberUpgradeKycLevelWindow').onProvinceRegion2(record.id);
							    }
							}
						},
						{
							xtype : "combo",
				        	editable:false,
				            anchor : '100%',
				            fieldLabel :_(" District/Kecamatan"),
				            itemId : 'subupgradekyc.form.DistrictCom',
				            allowBlank: false,
				            blankText : _('District/Kecamatan is required'),
				            triggerAction: "all",
				            emptyText : '<Select one..>',
				            mode: 'local',
				            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.State._name,
				            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSDistrict), 
				            displayField: CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
				            valueField : CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
				            hiddenName : CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.State._name,
							listeners: {
								 	select: function(field,record) {
								 	this.findParentByType('SubscriberUpgradeKycLevelWindow').onDistrict2(record.id);
							    }
							}
						},
						{
							xtype : "combo",
				            editable:false,
				            anchor : '100%',
				            fieldLabel :_(" Village/Kelurahan"),
				            itemId : 'subupgradekyc.form.VillageCom',
				            allowBlank: false,
				            blankText : _('Village/Kelurahan is required'),
				            triggerAction: "all",
				            emptyText : '<Select one..>',
				            mode: 'local',
				            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.SubState._name,
				            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSVillage), 
				            displayField: CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
				            valueField : CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
				            hiddenName : CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.SubState._name,
						},
						{
						    xtype : 'textfield',
						    fieldLabel: _(' Street Address/Alamat'),
						    allowBlank: false,
				            blankText : _('Alamat is required'),
						    anchor : '100%',
                            maxLength : 255,
				            itemId : 'subupgradekyc.form.StreetAddress',
						    name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.StreetAddress._name
						}
				]
        },
        {
        	xtype: 'displayfield',
        	anchor: '100%',
        	fieldLabel: _(' Mandatory'),
        	labelSeparator : ''
        }]
    	mFino.widget.SubscriberUpgradeKycLevelWindow.superclass.initComponent.call(this);
    	markMandatoryFields(this.form);
    },
    onUpgradeKyc : function(formWindow){
        if(this.getForm().isValid()){
     	   this.getForm().submit({
     		   url: 'upgradesubscriberkyc.htm',
     		   waitMsg: _('Processing Upgrade Subscriber ...'),
     		   reset: false,
     		   success : function(fp, action){
     			   formWindow.fireEvent("refresh");
     			   formWindow.hide();
     			   Ext.Msg.show({
                      title: _('Success'),
                      minProgressWidth:250,
                      msg: "Request to upgrade from non-KYC to KYC has been submitted successfully. The pocket will be upgraded to KYC once approved.",
                      buttons: Ext.MessageBox.OK,
                      multiline: false
     			   });
     		   },
     		   failure : function(fp, action){
     			   formWindow.hide();
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
     	   	formWindow.hide();
        }     
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    onProvince2 : function(provinceId){
    	var region_combo = this.find('itemId','subupgradekyc.form.CityCom')[0];
    	region_combo.reset();
    	region_combo.store.reload({
    		params: {
    			IdProvince : provinceId
    		}
    	});
    },
    onProvinceRegion2 : function(regionId){
    	var district_combo = this.find('itemId','subupgradekyc.form.DistrictCom')[0];
    	district_combo.reset();
    	district_combo.store.reload({
    		params: {
    			IdRegion : regionId
    		}
    	});
    },
    onDistrict2 : function(districtId){
    	var village_combo = this.find('itemId','subupgradekyc.form.VillageCom')[0];
    	village_combo.reset();
    	village_combo.store.reload({
    		params: {
    			IdDistrict : districtId
    		}
    	});
    },
    onJobSelect: function(){
    	var work = this.find('itemId', 'subupgradekyc.form.work')[0];
    	if(CmFinoFIX.JobList.Lainnya == work.getValue()){
    		this.form.items.get("subupgradekyc.form.otherwork").getEl().up('.x-form-item').setDisplayed(true);
    	}else{
    		this.form.items.get("subupgradekyc.form.otherwork").getEl().up('.x-form-item').setDisplayed(false);
    	}
    },
    setStore : function(store){
        this.store = store;
    },
    setDetails : function(response, mdnid, existingEmail, existingfullName){
        this.form.reset();
    	this.form.items.get("subupgradekyc.form.displayfield.idpath").hide();
    	this.form.items.get("subupgradekyc.form.mdnid").setValue(mdnid);
    	this.form.items.get("subupgradekyc.form.fullname").setValue(existingfullName);
    	this.form.items.get("subupgradekyc.form.email").setValue(existingEmail);
    	this.form.items.get("subupgradekyc.form.otherwork").getEl().up('.x-form-item').setDisplayed(false);
    	
    	if(response.m_ptotal > 0){
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
				if(mFino.widget.SubscriberUpgradeKycLevelWindow.path == null || mFino.widget.SubscriberUpgradeKycLevelWindow.path == '' || mFino.widget.SubscriberUpgradeKycLevelWindow.path == undefined){
						mFino.widget.SubscriberUpgradeKycLevelWindow.path = docFullPath.substring(0, docFullPath.lastIndexOf('/')+1);
				}
				var docName = docFullPath.substring(docFullPath.lastIndexOf('/')+1, docFullPath.length);
	        	this.form.items.get("subupgradekyc.form.idpath").setValue(docName);
	        	this.form.items.get("subupgradekyc.form.displayfield.idpath").setValue(docName);
	        	this.form.items.get("subupgradekyc.form.displayfield.idpath").show();
			}
	        
    		this.form.items.get("subupgradekyc.form.fullname").setValue(response.m_pEntries[0].m_pFirstName);
        	this.form.items.get("subupgradekyc.form.idtype").setValue(response.m_pEntries[0].m_pIDType);
        	this.form.items.get("subupgradekyc.form.idnumber").setValue(response.m_pEntries[0].m_pIDNumber);
        	this.form.items.get("subupgradekyc.form.birthplace").setValue(response.m_pEntries[0].m_pBirthPlace);
        	this.form.items.get("subupgradekyc.form.birthdate").setValue(formatedDate);
        	this.form.items.get("subupgradekyc.form.mothermaidenname").setValue(response.m_pEntries[0].m_pMothersMaidenName);
        	this.form.items.get("subupgradekyc.form.email").setValue(response.m_pEntries[0].m_pEmail);
        	this.form.items.get("subupgradekyc.form.ProvincialCom").setValue(response.m_pEntries[0].m_pRegionName);
        	this.form.items.get("subupgradekyc.form.CityCom").setValue(response.m_pEntries[0].m_pCity);
        	this.form.items.get("subupgradekyc.form.DistrictCom").setValue(response.m_pEntries[0].m_pState);
        	this.form.items.get("subupgradekyc.form.VillageCom").setValue(response.m_pEntries[0].m_pSubState);
        	this.form.items.get("subupgradekyc.form.StreetAddress").setValue(response.m_pEntries[0].m_pStreetAddress);
        	
        	this.form.items.get("subupgradekyc.form.nationality").setValue(response.m_pEntries[0].m_pNationality);
        	this.form.items.get("subupgradekyc.form.gender").setValue(response.m_pEntries[0].m_pGender);
        	if(CmFinoFIX.JobList.Lainnya == response.m_pEntries[0].m_pWork){
        		this.form.items.get("subupgradekyc.form.otherwork").getEl().up('.x-form-item').setDisplayed(true);
            	this.form.items.get("subupgradekyc.form.otherwork").setValue(response.m_pEntries[0].m_pOtherWork);
        	}else{
        		this.form.items.get("subupgradekyc.form.otherwork").getEl().up('.x-form-item').setDisplayed(false);
        	}
        	this.form.items.get("subupgradekyc.form.work").setValue(response.m_pEntries[0].m_pWork);
        	this.form.items.get("subupgradekyc.form.sourceoffund").setValue(response.m_pEntries[0].m_pSourceOfFund);
        	this.form.items.get("subupgradekyc.form.maritalstatus").setValue(response.m_pEntries[0].m_pMaritalStatus);
        	this.form.items.get("subupgradekyc.form.avgincome").setValue(response.m_pEntries[0].m_pIncome);
        	this.form.items.get("subupgradekyc.form.GoalOfAcctOpening").setValue(response.m_pEntries[0].m_pGoalOfAcctOpening);
        	
        }
    },
    showImage:function(imageName){
    	var imagePath = mFino.widget.SubscriberUpgradeKycLevelWindow.path+imageName
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
});
Ext.reg("SubscriberUpgradeKycLevelWindow", mFino.widget.SubscriberUpgradeKycLevelWindow);