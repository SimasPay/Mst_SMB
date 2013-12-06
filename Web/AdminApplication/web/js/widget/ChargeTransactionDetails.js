/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeTransactionDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
         autoScroll : true,

        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 150,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Reference ID"),
                name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ID._name,
                anchor : '100%'
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Transfer ID'),
                name : CmFinoFIX.message.JSServiceChargeTransactions.Entries.CommodityTransferID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Transaction Type'),
                anchor : '100%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionName._name           
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('SourceMDN'),
                anchor : '100%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.SourceMDN._name           
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('SourcePartnerCode'),
                anchor : '100%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.SourcePartnerCode._name           
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('TransactionAmount'),
                anchor : '100%',
                renderer : "money",
                name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionAmount._name           
            },  
            {
                xtype : 'displayfield',
                fieldLabel: _("Access Channel"),
                name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.AccessMethodText._name,
                anchor : '100%'
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer: "date",
                fieldLabel : _('Transaction Time'),
                name : CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Reversal Reason'),
                name : CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReversalReason._name
            },
	          {
	              xtype : 'displayfield',
	              fieldLabel: _('Amount Rev Status'),
	              anchor : '100%',
	              name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.AmtRevStatusText._name
	          },
	          {
	              xtype : 'displayfield',
	              fieldLabel: _('Charge Rev Status'),
	              anchor : '100%',
	              name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ChrgRevStatusText._name
	          },
	          {
                	xtype : 'displayfield',
                	anchor : '100%',
                	fieldLabel : _("Info1"),
                	name : CmFinoFIX.message.JSServiceChargeTransactions.Entries.Info1._name
              },
              {
                  xtype : 'displayfield',
                  fieldLabel: _("Invoice No."),
                  name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.InvoiceNo._name,
                  anchor : '100%'
              },
              {
                    xtype : 'displayfield',
                    fieldLabel: _("Is Charge Distributed"),
                    name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.IsChargeDistributed._name,
                    anchor : '100%',
    		          renderer: function(value) {
  						if (value) {
  							return "Yes";
  						} else {
  							return "No";
  						}
    		          }
               },
               {
                   xtype : 'displayfield',
                   fieldLabel: _("Charge Mode"),
                   name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ChargeModeText._name,
                   anchor : '100%'
               }
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 160,
            items : [
					 {
					     xtype : 'displayfield',
						 anchor : '100%',
						 fieldLabel : _('Parent SCTL ID'),
						 name : CmFinoFIX.message.JSServiceChargeTransactions.Entries.ParentSCTLID._name
					  },
                      /*{
		                xtype : "displayfield",
		                anchor : '100%',
		                fieldLabel :_('Transaction ID'),
		                name : CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransactionID._name
                      },*/
                      {
                          xtype : 'displayfield',
                          fieldLabel: _('Status'),
                          anchor : '100%',
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.TransferStatusText._name
                      },
			          {
			              xtype : 'displayfield',
			              fieldLabel: _('Additional Info'),
			              anchor : '100%',
			              name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.AdditionalInfo._name
			          },
			          {
			              xtype : 'displayfield',
			              fieldLabel: _('IntegrationRRN'),
			              anchor : '100%',
			              name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.BankRetrievalReferenceNumber._name
			          },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _('Service Name'),
                          anchor : '100%',
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ServiceName._name           
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _('DestinationMDN'),
                          anchor : '100%',
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.DestMDN._name           
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _('DestinationPartnerCode'),
                          anchor : '100%',
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.DestPartnerCode._name           
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _('Biller Code'),
                          anchor : '100%',
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.MFSBillerCode._name           
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _('Charge'),
                          anchor : '100%',
                          renderer : "money",
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.CalculatedCharge._name           
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _("Status Reason"),
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.FailureReason._name,
                          anchor : '100%'
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _("IntegrationType"),
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.IntegrationType._name,
                          anchor : '100%'
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _("Description"),
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.Description._name,
                          anchor : '100%'
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _("ReconcilationID1"),
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReconcilationID1._name,
                          anchor : '100%'
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _("ReconcilationID2"),
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReconcilationID2._name,
                          anchor : '100%'
                      },
                      {
                          xtype : 'displayfield',
                          fieldLabel: _("ReconcilationID3"),
                          name: CmFinoFIX.message.JSServiceChargeTransactions.Entries.ReconcilationID3._name,
                          anchor : '100%'
                      }
                            
            ]
        }]
    });

    mFino.widget.ChargeTransactionDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.ChargeTransactionDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.ChargeTransactionDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
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
    }
});

Ext.reg("chargetransactiondetails", mFino.widget.ChargeTransactionDetails);

