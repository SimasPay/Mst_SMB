/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DHPartnerServiceForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });
    mFino.widget.DHPartnerServiceForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DHPartnerServiceForm, Ext.FormPanel, {
    initComponent : function () {
		this.settlementConfigGrid = new mFino.widget.SettlementConfigGrid({
	        itemId:'settlementconfiggrid',
	        bodyStyle:'padding:5px',
	        height: 160,
	        frame:true,
	        border:true,
	        dataUrl:this.initialConfig.dataUrl			
		});
        this.labelWidth = 120;
        this.labelPad = 20;
		//this.defaults = {anchor: "80%"};
        this.items = [
          {
              xtype : "combo",
              anchor : '80%',
              fieldLabel :_("Service Provider"),
              itemId : 'ps.form.serviceprovider',
              allowBlank: false,
              triggerAction: "all",
              forceSelection : true,
              lastQuery: '',
              store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSServiceProvider),
              displayField: CmFinoFIX.message.JSServiceProvider.Entries.ServiceProviderName._name,
              valueField : CmFinoFIX.message.JSServiceProvider.Entries.ID._name,
              name: CmFinoFIX.message.JSPartnerServices.Entries.ServiceProviderID._name,
              listeners: {
	              select: function(field) {
        	  		  this.findParentByType('DHPartnerServiceForm').getServices(field.getValue());
	                  this.findParentByType('DHPartnerServiceForm').getPartners();
	              }
	          }              
          },  
          {
              xtype : "combo",
              anchor : '80%',
              fieldLabel :_("Service Type"),
              allowBlank: false,
              itemId : 'ps.form.servicetype',
              triggerAction: "all",
              forceSelection : true,
              lastQuery: '',
              store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSServicesForServiceProvider),
              displayField: CmFinoFIX.message.JSServicesForServiceProvider.Entries.ServiceName._name,
              valueField : CmFinoFIX.message.JSServicesForServiceProvider.Entries.ServiceID._name,
              name: CmFinoFIX.message.JSPartnerServices.Entries.ServiceID._name,
              listeners: {
	              select: function(field) {
	                  this.findParentByType('DHPartnerServiceForm').getPartners();
	                  this.findParentByType('DHPartnerServiceForm').getDcts(field.value);
	              }
	          }              
          },  
          {
          	  xtype : "remotedropdown",
              anchor : '80%',
              itemId : 'ps.form.DCT',
//              allowBlank: false,
			  lastQuery: '',
              fieldLabel :_("Distribution Chain Template"),
              RPCObject : CmFinoFIX.message.JSDistributionChainTemplate,
              displayField: CmFinoFIX.message.JSDistributionChainTemplate.Entries.DistributionChainName._name,
              valueField : CmFinoFIX.message.JSDistributionChainTemplate.Entries.ID._name,
              name: CmFinoFIX.message.JSPartnerServices.Entries.DistributionChainTemplateID._name, 
              listeners: {
                  select: function(field) {
                      this.findParentByType('DHPartnerServiceForm').getPartners();
                  }
              }              
          },
          {
              xtype:'combo',
              fieldLabel: _('Parent(Trade Name)'),
              itemId : 'ps.form.parentId',
              anchor : '80%',
              triggerAction: "all",
              lastQuery: '',
              store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPartnerByDCT),
              displayField: CmFinoFIX.message.JSPartnerByDCT.Entries.TradeName._name,
              valueField : CmFinoFIX.message.JSPartnerByDCT.Entries.ID._name,            
              name: CmFinoFIX.message.JSPartnerServices.Entries.ParentID._name,
              hiddenName:CmFinoFIX.message.JSPartnerServices.Entries.ParentID._name,
 	          minChars:1,
 	          forceSelection:true,
 	          enableKeyEvents:true,
 	          pageSize:10,
 	          resizable:true,
 	          typeAhead:true,
 	          mode: 'local'
          }, 
          {
              xtype: 'enumdropdown',                   
              fieldLabel: _('Service Charge Sharing'),
              allowBlank: false,
              labelSeparator:':',
              itemId : 'ps.form.servicechargesharing',
              anchor:'80%',
              enumId : CmFinoFIX.TagID.IsServiceChargeShare,
              name : CmFinoFIX.message.JSPartnerServices.Entries.IsServiceChargeShare._name,
              listeners: {
                  blur: function(field) {
                      this.findParentByType('DHPartnerServiceForm').checkParent(field);
                  }
              }
          },          
	      {
	          xtype : "combo",
	          anchor : '80%',
	          fieldLabel :_("Collector Pocket"),
	          allowBlank: false,
	          itemId : 'ps.form.collectorpocket',
	          triggerAction: "all",
	          forceSelection : true,
	          lastQuery: '',
	          store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
	          displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
	          valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
	          name: CmFinoFIX.message.JSPartnerServices.Entries.CollectorPocket._name,
              listeners: {
                  focus: function(field) {
                      this.findParentByType('DHPartnerServiceForm').checkSize(field);
                  }
              }
	      },
	      {
	          xtype : "combo",
	          anchor : '80%',
	          fieldLabel :_("Outgoing Funds Pocket"),
	          allowBlank: false,
	          itemId : 'ps.form.sourcepocket',
	          triggerAction: "all",
	          forceSelection : true,
	          lastQuery: '',
	          store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
	          displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
	          valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
	          name: CmFinoFIX.message.JSPartnerServices.Entries.SourcePocket._name
	      },
	      {
	          xtype : "combo",
	          anchor : '80%',
	          fieldLabel :_("Incoming Funds Pocket"),
	          allowBlank: false,
	          itemId : 'ps.form.destpocket',
	          triggerAction: "all",
	          forceSelection : true,
	          lastQuery: '',
	          store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSPocket),
	          displayField: CmFinoFIX.message.JSPocket.Entries.PocketDispText._name,
	          valueField : CmFinoFIX.message.JSPocket.Entries.ID._name,
	          name: CmFinoFIX.message.JSPartnerServices.Entries.DestPocketID._name
	      },	
          {
              xtype : "enumdropdown",
              anchor : '80%',
              fieldLabel :_('Status'),
              itemId: 'ps.form.status',
              allowBlank: false,
              blankText : _('Status is required'),
              enumId: CmFinoFIX.TagID.PartnerServiceStatus,
              name : CmFinoFIX.message.JSPartnerServices.Entries.PartnerServiceStatus._name,
              listeners : {
                  select :  function(status){
                      var s = status.getValue();
                      this.findParentByType('DHPartnerServiceForm').onStatusDropdown(s);
                  }
              }
          },
          {
              xtype:'tabpanel',
              itemId:'tabelpanelPartnerServices',
              frame:true,
              activeTab: 0,
              border : false,
              deferredRender:false,
              defaults:{
                  layout:'form',
                  bodyStyle:'padding:10px'
              },
              items:[
	  		    {
	  		       title: _('Settlement Configuration'),
//	  		       autoHeight: true,
	  		       padding: '0 0 0 0',
	  		       itemId : 'ps.form.settlement',
	  		       items:[ 
	  		             this.settlementConfigGrid
	  		       ]
	  		    }
	  		  ]
          }
        ];

		this.buttons = [
			{
				itemId : "save",
				text: _('Save'),
				handler: this.save.createDelegate(this)
			},
			{
				itemId: "reset",
				text: _('Reset'),
				handler: this.resetPS.createDelegate(this)
			}
		];

        mFino.widget.DHPartnerServiceForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);        
    },
    disableNotPermittedItems: function(){
    	var checkAbleItems = [ 'ps.form.servicetype', 'ps.form.DCT','ps.form.parentId','ps.form.servicechargesharing','ps.form.status'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
    },
    enablePermittedItems: function(){
        var checkAbleItems = [ 'ps.form.servicetype', 'ps.form.DCT','ps.form.parentId','ps.form.servicechargesharing','ps.form.status'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            // The above items are disabled only for Edit operation. They should be available for Add.
            checkItem.enable();
        }
    },
    getServices : function(field) {
    	var st_combo = this.find('itemId','ps.form.servicetype')[0];
    	st_combo.clearValue();
    	st_combo.store.reload({
    		params: {
    			ServiceProviderID : field,
    			PartnerTypeSearch : this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.PartnerTypeSearch._name)
    		}
    	});
    },
    
    checkParent : function(field) {
    	if (field.getValue() == CmFinoFIX.IsServiceChargeShare.Shared_Up_Chain) {
        	var parent = this.find('itemId','ps.form.parentId')[0].getValue();
        	if (parent===null || parent==="" || typeof(parent)==='undefined') {
    			Ext.ux.Toast.msg(_("Error"), _("Please select Parent Partner "),5);
        		this.find('itemId','ps.form.servicechargesharing')[0].clearValue();
        	}
    	}
    },
    
    checkSize : function(field) {
    	var cp_combo = this.find('itemId',field.getItemId())[0];
    	if (cp_combo.store.getCount() === 0) {
    		Ext.ux.Toast.msg(_("Error"), _("Please Add / Activate the Non-Transactionable E-Money pockets."),5);
    	}
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            
            if (this.record.get('ID') > 0) {
//            	this.settlementConfigGrid.setScheduleStatus(CmFinoFIX.SchedulerStatus.Rescheduled);
            	this.saveGridData();
            } else {
            	this.settlementConfigGrid.setScheduleStatus(CmFinoFIX.SchedulerStatus.TobeScheduled);
            	this.store.on("write", this.saveGridData, this);
            }            
            
            if(this.store){
                if(this.record.phantom
                    && this.store.getAt(0)!= this.record){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },
    
    saveGridData : function() {
        this.settlementConfigGrid.setParentData(this.record.get('ID'));
        this.settlementConfigGrid.store.save();    	  
    },    
    
    setRecord : function(record){
		record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name] = this.values.PartnerID;
		record.data[CmFinoFIX.message.JSPartnerServices.SubscriberID._name] = this.values.SubscriberID;
		record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerTypeSearch._name] = this.values.BusinessPartnerType;

        this.getForm().reset();
        this.settlementConfigGrid.reset();
        this.record = record;
        this.partnerServiceId = null;
        this.partnerId = null;
        
    	var spr_combo = this.find('itemId','ps.form.serviceprovider')[0];
    	spr_combo.store.reload({
    		params: {
    		    PartnerIDSearch: this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name),
    		    PartnerTypeSearch: this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.PartnerTypeSearch._name)	
    		}
    	}); 

    	var sr_combo = this.find('itemId','ps.form.servicetype')[0];
    	sr_combo.store.reload({
    		params: {ServiceProviderID: this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.ServiceProviderID._name)}
    	}); 
		
		var dct_combo = this.find('itemId','ps.form.DCT')[0];
    	dct_combo.store.baseParams[CmFinoFIX.message.JSDistributionChainTemplate.ServiceIDSearch._name] = this.record.data[CmFinoFIX.message.JSPartnerServices.Entries.ServiceID._name];
    	dct_combo.store.load();

    	var statusSearchString = CmFinoFIX.PocketStatus.Initialized + "," + CmFinoFIX.PocketStatus.Active;
    	
    	var cp_combo = this.find('itemId','ps.form.collectorpocket')[0];
    	cp_combo.store.baseParams[CmFinoFIX.message.JSPocket.SubscriberIDSearch._name] = 
    						this.record.get(CmFinoFIX.message.JSPartnerServices.SubscriberID._name);
    	cp_combo.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;    	
    	cp_combo.store.baseParams[CmFinoFIX.message.JSPocket.PocketType._name] = CmFinoFIX.PocketType.SVA;
    	cp_combo.store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;    	
    	cp_combo.store.baseParams[CmFinoFIX.message.JSPocket.IsCollectorPocket._name] = 1;
    	cp_combo.store.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = statusSearchString;
    	cp_combo.store.reload({
    		params: {
    			SubscriberIDSearch: this.record.get(CmFinoFIX.message.JSPartnerServices.SubscriberID._name),
    			NoCompanyFilter: true,
    			PocketType: CmFinoFIX.PocketType.SVA,
    			Commodity: CmFinoFIX.Commodity.Money,
    			StatusSearch: statusSearchString,
    			IsCollectorPocket : 1
    		}
    	});   
    	
    	var sp_combo = this.find('itemId','ps.form.sourcepocket')[0];
    	sp_combo.store.baseParams[CmFinoFIX.message.JSPocket.SubscriberIDSearch._name] = this.record.get(CmFinoFIX.message.JSPartnerServices.SubscriberID._name);
    	sp_combo.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;    	
		sp_combo.store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;    	
		sp_combo.store.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = statusSearchString;  
    	sp_combo.store.reload({
    		params: {
    			SubscriberIDSearch: this.record.get(CmFinoFIX.message.JSPartnerServices.SubscriberID._name),
    			NoCompanyFilter: true,
    			StatusSearch: statusSearchString,
    			Commodity: CmFinoFIX.Commodity.Money
    		}
    	}); 
    	
    	var dp_combo = this.find('itemId','ps.form.destpocket')[0];
    	dp_combo.store.baseParams[CmFinoFIX.message.JSPocket.SubscriberIDSearch._name] = 
    						this.record.get(CmFinoFIX.message.JSPartnerServices.SubscriberID._name);
    	dp_combo.store.baseParams[CmFinoFIX.message.JSPocket.NoCompanyFilter._name] = true;    	
    	dp_combo.store.baseParams[CmFinoFIX.message.JSPocket.Commodity._name] = CmFinoFIX.Commodity.Money;    	
    	dp_combo.store.baseParams[CmFinoFIX.message.JSPocket.IsCollectorPocketAllowed._name] = true;
    	dp_combo.store.baseParams[CmFinoFIX.message.JSPocket.StatusSearch._name] = statusSearchString;
    	dp_combo.store.reload({
    		params: {
    			SubscriberIDSearch: this.record.get(CmFinoFIX.message.JSPartnerServices.SubscriberID._name),
    			NoCompanyFilter: true,
    			Commodity: CmFinoFIX.Commodity.Money,
    			StatusSearch: statusSearchString,
    			IsCollectorPocketAllowed : true
    		}
    	});   
    	
    	var p_combo = this.find('itemId','ps.form.parentId')[0];
    	p_combo.store.reload({
    		params: {
				ServiceProviderIDSearch : this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.ServiceProviderID._name),
				ServiceIDSearch : this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.ServiceID._name),
    			DCTIDSearch : this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.DistributionChainTemplateID._name),
    			ForPartnerID : this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name)
    		}
    	});
    	
		this.partnerServiceId = record.data[CmFinoFIX.message.JSPartnerServices.Entries.ID._name];
		this.partnerId = record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name];
		this.settlementConfigGrid.setParentData(this.partnerServiceId, this.partnerId);
    	if (!this.record.phantom) {
    		this.find('itemId','ps.form.servicetype')[0].disable();
    		this.find('itemId','ps.form.serviceprovider')[0].disable();
    		this.find('itemId','ps.form.DCT')[0].disable();
    		this.find('itemId','ps.form.parentId')[0].disable();
    		this.find('itemId','ps.form.servicechargesharing')[0].disable();
    		this.settlementConfigGrid.reloadGrid();
//    		this.find('itemId','ps.form.status')[0].enable();
    	} else {
    		this.settlementConfigGrid.loadSettlementTemplate();
    		this.find('itemId','ps.form.servicetype')[0].enable();
            this.find('itemId','ps.form.status')[0].disable();
            this.find('itemId','ps.form.status')[0].setRawValue('Initialized');
    	}
        
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
		//alert(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.SourcePocketDispText._name));
        cp_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.CollectorPocketDispText._name));
        sp_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.SourcePocketDispText._name));
        dp_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.DestPocketDispText._name));
        sr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.ServiceName._name));
        p_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.TradeName._name));
        spr_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.ServiceProviderName._name));
		dct_combo.setRawValue(this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.ServiceName._name));
    },

    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    },
    
    onChangeST : function(field){
        var resValue = this.record.get(CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementName._name);
        if(field.getValue() !== resValue){
            var msg = new CmFinoFIX.message.JSSettlementTemplateCheck();
            msg.m_pSettlementName = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
	getDcts : function(serviceId){
		var dct_combo = this.find('itemId','ps.form.DCT')[0];
		dct_combo.clearValue();
    	dct_combo.store.baseParams[CmFinoFIX.message.JSDistributionChainTemplate.ServiceIDSearch._name] = serviceId;
    	dct_combo.store.load();
	},
    getPartners : function(){
    	var p_combo = this.find('itemId','ps.form.parentId')[0];
    	p_combo.clearValue();
    	p_combo.store.baseParams[CmFinoFIX.message.JSPartnerByDCT.ServiceProviderIDSearch._name] =
    		this.find('itemId','ps.form.serviceprovider')[0].getValue();
    	p_combo.store.baseParams[CmFinoFIX.message.JSPartnerByDCT.ServiceIDSearch._name] = 
    		this.find('itemId','ps.form.servicetype')[0].getValue();
    	p_combo.store.baseParams[CmFinoFIX.message.JSPartnerByDCT.DCTIDSearch._name] = 
    		this.find('itemId','ps.form.DCT')[0].getValue();
    	p_combo.store.baseParams[CmFinoFIX.message.JSPartnerByDCT.ForPartnerID._name] = 
    		this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name);    	
    	p_combo.store.reload({
    		params: {
    			ServiceProviderIDSearch : this.find('itemId','ps.form.serviceprovider')[0].getValue(),
    			ServiceIDSearch : this.find('itemId','ps.form.servicetype')[0].getValue(),
    			DCTIDSearch : this.find('itemId','ps.form.DCT')[0].getValue(),
    			ForPartnerID : this.record.get(CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name)
    		}
    	});
    },
    onStatusDropdown : function(status) {
    	if (status == CmFinoFIX.PartnerServiceStatus.PendingRetirement || status == CmFinoFIX.PartnerServiceStatus.Retired) {
            var items = ['ps.form.servicetype','ps.form.DCT','ps.form.parentId','ps.form.servicechargesharing', 'ps.form.destpocket',
                         'ps.form.collectorpocket','ps.form.sourcepocket','ps.form.status','tabelpanelPartnerServices','ps.form.serviceprovider'];
            for(var i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].disable();
            }           
    	} else if (status == CmFinoFIX.PartnerServiceStatus.Initialized || status == CmFinoFIX.PartnerServiceStatus.Active) {
            var items = ['ps.form.DCT','ps.form.parentId','ps.form.servicechargesharing',
                         'ps.form.collectorpocket','ps.form.sourcepocket','ps.form.status','tabelpanelPartnerServices','ps.form.destpocket'];
            for(var i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].enable();
            } 
    	}
    },

	resetPS: function(){
		if(this.record){
			this.setRecord(this.record);
		}
	},

    isInitializedOrActive : function (record) {
    	var status = false;
    	if (record) {
        	if (record.data[CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name] == CmFinoFIX.SubscriberStatus.Initialized || 
        			record.data[CmFinoFIX.message.JSPartner.Entries.PartnerStatus._name] == CmFinoFIX.SubscriberStatus.Active) {
        		status = true;
        	}
    	}

    	return status;
    },

	setValues: function(values){
		this.values = values;
	}
});

Ext.reg("DHPartnerServiceForm", mFino.widget.DHPartnerServiceForm);

