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
        width: 550,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.SubscriberUpgradeKycLevelWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberUpgradeKycLevelWindow, Ext.FormPanel, {
    initComponent : function(){
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
                            anchor : '100%',
                            renderer: "date",
                            xtype : 'datefield',
                            format : 'd-m-Y',
                     		allowBlank: false,
                     		blankText : _('Birth Date is required'),
                            fieldLabel: _(' Date of Birth'),
							itemId : 'subupgradekyc.form.birthdate',
                            name: CmFinoFIX.message.JSSubscriberUpgradeKyc.Entries.DateOfBirth._name
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
    setStore : function(store){
        this.store = store;
    },
    setDetails : function(response, mdnid, existingEmail, existingfullName){
        this.form.reset();
    	this.form.items.get("subupgradekyc.form.displayfield.idpath").hide();
    	this.form.items.get("subupgradekyc.form.mdnid").setValue(mdnid);
    	this.form.items.get("subupgradekyc.form.fullname").setValue(existingfullName);
    	this.form.items.get("subupgradekyc.form.email").setValue(existingEmail);
    	
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
});
Ext.reg("SubscriberUpgradeKycLevelWindow", mFino.widget.SubscriberUpgradeKycLevelWindow);