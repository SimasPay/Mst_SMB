/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PartnerServiceViewForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.PartnerServiceViewForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PartnerServiceViewForm, Ext.FormPanel, {
    initComponent : function () {
	this.settlementConfigGridView = new mFino.widget.SettlementConfigGridView({
        itemId:'settlementconfiggridview',
        bodyStyle:'padding:5px',
        height: 160,
        frame:true,
        border:true,
        dataUrl:this.initialConfig.dataUrl			
	});	
        this.labelWidth = 200;
        this.labelPad = 20;
        this.items = [
          {
              layout:'column',
              items : [
              {
                  columnWidth: 1,
                  layout: 'form',
                  labelWidth : 175,
                  labelPad : 5,
                  items : [
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Service Provider"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSPartnerServices.Entries.ServiceProviderName._name
                  },                  
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Service Type"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSPartnerServices.Entries.ServiceName._name
                  },   
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Distribution Chain Template"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSPartnerServices.Entries.DistributionChainName._name
                  },
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Parent(Trade Name)"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSPartnerServices.Entries.TradeName._name
                  },
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Service Charge Sharing"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSPartnerServices.Entries.IsServiceChargeShareText._name
                  },                   
                  {
                      xtype: 'displayfield',                   
                      fieldLabel: _('Collector Pocket'),
                      labelSeparator:':',
                      name : CmFinoFIX.message.JSPartnerServices.Entries.CollectorPocketDispText._name
                  },
                  {
                      xtype: 'displayfield',                   
                      fieldLabel: _('Outgoing Funds Pocket'),
                      labelSeparator:':',
                      name : CmFinoFIX.message.JSPartnerServices.Entries.SourcePocketDispText._name
                  },
                  {
                      xtype: 'displayfield',                   
                      fieldLabel: _('Incoming Funds Pocket'),
                      labelSeparator:':',
                      name : CmFinoFIX.message.JSPartnerServices.Entries.DestPocketDispText._name
                  },
                  {
                      xtype: 'displayfield',                   
                      fieldLabel: _('Status'),
                      labelSeparator:':',
                      name : CmFinoFIX.message.JSPartnerServices.Entries.PartnerServiceStatusText._name
                  }
                  ]
              }
              ]
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
				 title: _('Settelement Configuration'),
				 autoHeight: true,
				 padding: '0 0 0 0',
				 itemId : 'sct.form.pricing',
				 frame: true,
				 items:[ 
				      this.settlementConfigGridView
				 ]
			  }
              ]
          }];

        mFino.widget.PartnerServiceViewForm.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        
		this.partnerServiceId = record.data[CmFinoFIX.message.JSPartnerServices.Entries.ID._name];
		this.partnerId = record.data[CmFinoFIX.message.JSPartnerServices.Entries.PartnerID._name];
		this.settlementConfigGridView.setParentData(this.partnerServiceId, this.partnerId);
		this.settlementConfigGridView.reloadGrid();        
        
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();  
    }

});

Ext.reg("partnerserviceviewform", mFino.widget.PartnerServiceViewForm);
