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
                     		xtype : 'textfield',
                     		fieldLabel: _(' Full Name'),
                     		allowBlank: false,
                     		blankText : _('Full Name is required'),
                     		anchor : '100%',
                     		name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name
                     	},
						{
							xtype : "hidden",
							itemId : 'subupgradekyc.form.ProvincialVal',
							name: CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name
						},
                     	{
                     		xtype : 'enumdropdown',
                     		fieldLabel: _(' ID Type'),
                     		labelSeparator : '',
                     		anchor : '100%',
                     		triggerAction : "all",
							forceSelection : true,
							pageSize : 10,
							addEmpty : true,
                     		allowBlank: false,
                     		blankText : _('ID Type is required'),
							emptyText : _('<select one..>'),
							enumId : CmFinoFIX.TagID.IDTypeForKycUpgrade,
                     		name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDType._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' ID Number'),
                     		allowBlank: false,
                     		blankText : _('ID Number is required'),
                     		anchor : '100%',
                     		name: CmFinoFIX.message.JSSubscriberMDN.Entries.IDNumber._name
                     	},
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Birth Place'),
                     		allowBlank: false,
                     		blankText : _('Birth Place is required'),
                     		anchor : '100%',
                     		name: CmFinoFIX.message.JSSubscriberMDN.Entries.BirthPlace._name
                     	},
                     	{
                            anchor : '100%',
                            renderer: "date",
                            xtype : 'datefield',
                            format : 'd-m-Y',
                     		allowBlank: false,
                     		blankText : _('Birth Date is required'),
                            fieldLabel: _(' Date of Birth'),
                            name: CmFinoFIX.message.JSSubscriberMDN.Entries.DateOfBirth._name
                        },
                     	{
                     		xtype : 'textfield',
                     		fieldLabel: _(' Mother Maiden Name'),
                     		allowBlank: false,
                     		blankText : _('Mother Maiden Name is required'),
                     		anchor : '100%',
                     		name: CmFinoFIX.message.JSSubscriberMDN.Entries.MothersMaidenName._name
                     	},
                     	{
                            xtype : 'textfield',
                            fieldLabel: _('Email'),
                            vtype: 'email',
                            anchor : '100%',
                            name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name
                        },
                     	{
                            xtype : 'fileuploadfield',
                            fieldLabel: _('Upload ID Card'),
                            buttonText: _('Browse'),
                            emptyText:_(''),
                            anchor : '100%',
                            name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name
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
							name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPRegionName._name,
							store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvince), 
							displayField: CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
							valueField : CmFinoFIX.message.JSProvince.Entries.DisplayText._name,
							hiddenName : CmFinoFIX.message.JSSubscriberMDN.Entries.KTPRegionName._name,
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
				            name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPCity._name,
				            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSProvinceRegion), 
				            displayField: CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
				            valueField : CmFinoFIX.message.JSProvinceRegion.Entries.DisplayText._name,
				            hiddenName :CmFinoFIX.message.JSSubscriberMDN.Entries.KTPCity._name,
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
				            name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPState._name,
				            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSDistrict), 
				            displayField: CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
				            valueField : CmFinoFIX.message.JSDistrict.Entries.DisplayText._name, 
				            hiddenName : CmFinoFIX.message.JSSubscriberMDN.Entries.KTPState._name,
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
				            name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPSubState._name,
				            store: new FIX.FIXStore("./fix.htm", CmFinoFIX.message.JSVillage), 
				            displayField: CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
				            valueField : CmFinoFIX.message.JSVillage.Entries.DisplayText._name, 
				            hiddenName : CmFinoFIX.message.JSSubscriberMDN.Entries.KTPSubState._name,
						},
						{
						    xtype : 'textfield',
						    fieldLabel: _(' Street Address/Alamat'),
						    allowBlank: false,
				            blankText : _('Alamat is required'),
						    anchor : '100%',
						    name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPPlotNo._name
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
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    onUpgradeKyc : function(formWindow){
        if(this.getForm().isValid()){
     	   this.getForm().submit({
     		   url: 'upgradesubscriberkyc.htm',
     		   waitMsg: _('Processing Upgrade Subscriber ...'),
     		   reset: false,
     		   success : function(fp, action){
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
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
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
    }
});
Ext.reg("SubscriberUpgradeKycLevelWindow", mFino.widget.SubscriberUpgradeKycLevelWindow);