/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionChargeViewForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.TransactionChargeViewForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionChargeViewForm, Ext.FormPanel, {
    initComponent : function () {
		this.sharePartnerGridView = new mFino.widget.SharePartnerGridView({
	        itemId:'sharepartnergridview',
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
                  labelWidth : 175,
                  labelPad : 5,
                  items : [
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Transaction Rule"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSTransactionCharge.Entries.TransactionRuleName._name
                  },
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Charge Type"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSTransactionCharge.Entries.ChargeTypeName._name
                  },
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Charge Definition"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSTransactionCharge.Entries.ChargeDefinitionName._name
                  },
                  {
                  	xtype :"displayfield",
                  	fieldLabel :_('Is Active'),
                  	labelSeparator:':',
      				name :CmFinoFIX.message.JSTransactionCharge.Entries.IsActive._name,
      				renderer : function(value) {
						if (value !== null&&value) {
							return "true";
						}else{
							return "false";
							}
      				}
                  }
                  ]
              }
              ]
          },
          {
              xtype:'tabpanel',
              itemId:'tabelpanelTransactionCharge',
              frame:true,
              activeTab: 0,
              border : false,
              deferredRender:false,
              defaults:{
	              layout:'column',
	              columnWidth: 1,
                  bodyStyle:'padding:10px'
              },
              items:[
			  {
				 title: _('Charge Share'),
				 autoHeight: true,
				 padding: '0 0 0 0',
				 frame: true,
	             layout: 'form',
	             labelWidth : 175,
	             labelPad : 5,				 
				 items:[ 
				 this.sharePartnerGridView
				 ]
			  }
			  ]
          }
          ];

        mFino.widget.TransactionChargeViewForm.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.templateID = record.data[CmFinoFIX.message.JSTransactionCharge.Entries.ID._name];
        this.sharePartnerGridView.setTemplateID(this.templateID);
        this.sharePartnerGridView.reloadGrid();
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();  
    }

});

Ext.reg("transactionchargeviewform", mFino.widget.TransactionChargeViewForm);
