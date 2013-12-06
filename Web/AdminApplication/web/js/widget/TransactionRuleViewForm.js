/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionRuleViewForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true
    });

    mFino.widget.TransactionRuleViewForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionRuleViewForm, Ext.FormPanel, {
    initComponent : function () {
    	this.txnRuleAddnInfoGridView = new mFino.widget.TxnRuleAdditionalInfoGrid({
	        itemId:'txnRuleAddnInfoGridView',
	        bodyStyle:'padding:5px',
	        height: 150,
	        frame:false,
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
                      name: CmFinoFIX.message.JSTransactionRule.Entries.Name._name
                  },
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Service Provider"),
                      labelSeparator:':',                      
                      name: CmFinoFIX.message.JSTransactionRule.Entries.ServiceProviderName._name
                  },                
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Service"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSTransactionRule.Entries.ServiceName._name
                  },
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Transaction Type"),
                      labelSeparator:':',                      
                      name: CmFinoFIX.message.JSTransactionRule.Entries.TransactionName._name
                  }, 
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Channel"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSTransactionRule.Entries.ChannelName._name
                  },
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Charge Mode"),
                      labelSeparator:':',                      
                      name: CmFinoFIX.message.JSTransactionRule.Entries.ChargeModeText._name
                  }, 
                  {
                      xtype : 'displayfield',
                      fieldLabel: _("Source Group"),
                      labelSeparator:':',
                      name: CmFinoFIX.message.JSTransactionRule.Entries.SourceGroupName._name
                  },
                  {
                      xtype : "displayfield",
                      fieldLabel :_("Destination Group"),
                      labelSeparator:':',                      
                      name: CmFinoFIX.message.JSTransactionRule.Entries.DestinationGroupName._name
                  }
                  ]
              }
              ]
          },
          {
              xtype:'tabpanel',
              itemId:'transactionRuleTabPanelView',
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
				 title: _('Additional Info'),
				 autoHeight: true,
				 padding: '0 0 0 0',
				 frame: true,
	             layout: 'form',
	             labelWidth : 175,
	             labelPad : 5,				 
				 items:[ 
				 this.txnRuleAddnInfoGridView
				 ]
			  }
			  ]
          }
          ];

        mFino.widget.TransactionRuleViewForm.superclass.initComponent.call(this);
    },
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.transactionRuleID = record.data[CmFinoFIX.message.JSTransactionRule.Entries.ID._name];
        this.txnRuleAddnInfoGridView.setTransactionRuleID(this.transactionRuleID);
        var serviceID = record.data[CmFinoFIX.message.JSTransactionRule.Entries.ServiceID._name];
        var transactionTypeID = record.data[CmFinoFIX.message.JSTransactionRule.Entries.TransactionTypeID._name];
        this.txnRuleAddnInfoGridView.loadRuleKeys(serviceID, transactionTypeID);
        this.txnRuleAddnInfoGridView.reloadGrid();
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();  
    }

});

Ext.reg("transactionruleviewform", mFino.widget.TransactionRuleViewForm);
