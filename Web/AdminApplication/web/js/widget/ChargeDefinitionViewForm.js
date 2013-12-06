/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeDefinitionViewForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.ChargeDefinitionViewForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeDefinitionViewForm, Ext.FormPanel, {
    initComponent : function () {
		this.pricingGridView = new mFino.widget.ChargePricingGridView({
	        itemId:'pricinggridview',
	        bodyStyle:'padding:5px',
	        height: 135,
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
                  labelWidth : 120,
                  labelPad : 5,
                  items : [
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Name"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSChargeDefinition.Entries.Name._name
                  },
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Description"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSChargeDefinition.Entries.Description._name
                  },
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Charge Type"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSChargeDefinition.Entries.ChargeTypeName._name
                  },
                  {
                      xtype:'displayfield',
                      fieldLabel: _('Is charge From Customer'),
                      itemId:'chargedefinition.form.ischargefromcustomer'
                  },                  
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Dependant Charge Type"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSChargeDefinition.Entries.DependantChargeTypeName._name
                  },
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Funding Partner"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSChargeDefinition.Entries.TradeName._name
                  },
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Funding Pocket"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSChargeDefinition.Entries.PocketDispText._name
                  },
                  
                  {
                      xtype:'displayfield',
                      fieldLabel: _('Is Charge Taxable'),
                      itemId:'chargedefinition.form.istaxable'
                  }                  
                  ]
              }
              ]
          },
          {
              xtype:'tabpanel',
              itemId:'tabelpanelChargeDefinition',
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
				 title: _('Charge Pricings'),
				 autoHeight: true,
				 padding: '0 0 0 0',
				 itemId : 'chargedefinition.form.pricing',
				 frame: true,
				 items:[ 
				    this.pricingGridView 
				 ]
			  }
			  ]
          }
          ];

        mFino.widget.ChargeDefinitionViewForm.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.templateID = record.data[CmFinoFIX.message.JSChargeDefinition.Entries.ID._name];
        this.pricingGridView.setParentTemplateData(this.templateID);
        this.pricingGridView.reloadGrid();
        this.getForm().loadRecord(record);
        var isChargeFromCustomer = record.get(CmFinoFIX.message.JSChargeDefinition.Entries.IsChargeFromCustomer._name);
        this.form.items.get("chargedefinition.form.ischargefromcustomer").setValue((isChargeFromCustomer>0)?"Y":"N");

        var isTaxable = record.get(CmFinoFIX.message.JSChargeDefinition.Entries.IsTaxable._name);
        this.form.items.get("chargedefinition.form.istaxable").setValue((isTaxable>0)?"Y":"N");
        this.getForm().clearInvalid();  
    }

});

Ext.reg("chargedefinitionviewform", mFino.widget.ChargeDefinitionViewForm);
