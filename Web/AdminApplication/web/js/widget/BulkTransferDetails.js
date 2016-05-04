/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkTransferDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        autoScroll : true,
        items: [        
         {
            columnWidth: 0.5,
            layout: 'form',
//            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: 'Bulk Transfer Id',
                name: CmFinoFIX.message.JSBulkUpload.Entries.ID._name
            },                     
            {
                xtype : 'displayfield',
                fieldLabel: 'File Name',
                name: CmFinoFIX.message.JSBulkUpload.Entries.FileName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Description',
                name: CmFinoFIX.message.JSBulkUpload.Entries.Description._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Execution Date Time',
                renderer: 'date',
                name: CmFinoFIX.message.JSBulkUpload.Entries.PaymentDate._name
            },            
            {
                xtype : 'displayfield',
                fieldLabel: 'No. of MDNs',
                name: CmFinoFIX.message.JSBulkUpload.Entries.TransactionsCount._name
            },            
            {
                xtype : 'displayfield',
                fieldLabel: 'Total Amount To Be Disbursed',
                name: CmFinoFIX.message.JSBulkUpload.Entries.TotalAmount._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Source Pocket',
                name: CmFinoFIX.message.JSBulkUpload.Entries.SourcePocketDispText._name
            }            
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            items : [
            {
     			xtype : 'displayfield',
     			fieldLabel: 'Name',
     			name: CmFinoFIX.message.JSBulkUpload.Entries.Name._name
 			},                     
            {
                xtype : 'displayfield',
                fieldLabel: 'Reference Id',
                name: CmFinoFIX.message.JSBulkUpload.Entries.ServiceChargeTransactionLogID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Status',
                name: CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadDeliveryStatusText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Failure Reason',
                name: CmFinoFIX.message.JSBulkUpload.Entries.FailureReason._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'No. of Failed Txns',
                name: CmFinoFIX.message.JSBulkUpload.Entries.FailedTransactionsCount._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Amount Disbursed',
                name: CmFinoFIX.message.JSBulkUpload.Entries.SuccessAmount._name
            },            
            {
                xtype : 'displayfield',
                fieldLabel: 'Created By',
                name: CmFinoFIX.message.JSBulkUpload.Entries.CreatedBy._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Created Time',
                renderer: 'date',
                name: CmFinoFIX.message.JSBulkUpload.Entries.CreateTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Last Update Time',
                renderer: 'date',
                name: CmFinoFIX.message.JSBulkUpload.Entries.LastUpdateTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Update By',
                name: CmFinoFIX.message.JSBulkUpload.Entries.UpdatedBy._name
            }
            ]
        }        
      ]
    });

    mFino.widget.BulkTransferDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransferDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 150;
        this.labelPad = 5;
        mFino.widget.BulkTransferDetails.superclass.initComponent.call(this);
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

Ext.reg("BulkTransferDetails", mFino.widget.BulkTransferDetails);