/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkUploadDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        title: 'Bulk Upload Details',
        frame : true,
        width: 926,
        items: [        {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: 'File Name',
                name: CmFinoFIX.message.JSBulkUploadFile.Entries.FileName._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'File Type',
                anchor : '75%',
                name: CmFinoFIX.message.JSBulkUploadFile.Entries.RecordTypeText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Status',
                anchor : '75%',
                name: CmFinoFIX.message.JSBulkUploadFile.Entries.UploadStatusText._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Last Update Time',
                renderer: 'date',
                anchor : '75%',
                name: CmFinoFIX.message.JSBulkUploadFile.Entries.LastUpdateTime._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: 'Created Time',
                renderer: 'date',
                anchor : '75%',
                name: CmFinoFIX.message.JSBulkUploadFile.Entries.CreateTime._name
            }
            ]
        }]
    });

    mFino.widget.BulkUploadDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkUploadDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.BulkUploadDetails.superclass.initComponent.call(this);
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

Ext.reg("BulkUploadDetails", mFino.widget.BulkUploadDetails);